package gov.nih.ncgc.openhts.tool1.dataSourceManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import au.com.bytecode.opencsv.CSVReader;
import gov.nih.ncgc.openhts.tool1.FileFilters;
import gov.nih.ncgc.openhts.tool1.Main;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.Tool1Exception;
import gov.nih.ncgc.openhts.tool1.Version;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Basis;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Descriptor;
import gov.nih.ncgc.openhts.tool1.descriptorManager.DescriptorManagerEvent;
import gov.nih.ncgc.openhts.tool1.descriptorManager.DescriptorManagerListener;
import gov.nih.ncgc.openhts.tool1.dirMonitor.DirMonitor;
import gov.nih.ncgc.openhts.tool1.dirMonitor.DirMonitorEvent;
import gov.nih.ncgc.openhts.tool1.dirMonitor.DirMonitorListener;
import gov.nih.ncgc.openhts.tool1.persistence.Db4oPersistenceService;
import gov.nih.ncgc.openhts.tool1.persistence.PersistenceContext;
import gov.nih.ncgc.openhts.tool1.persistence.PersistenceRoot;
import gov.nih.ncgc.openhts.tool1.util.xml.XmlFilteredViewlet;

/**
 * Purpose is to assemble the data records (molecule + descriptors + activities)
 * into a file that stays current on disk. Since dataSources can become quite
 * large, they are kept in memory for the shortest time possible.
 * 
 * @author talafousj
 */
public class DataSourceManager implements DescriptorManagerListener,
    DirMonitorListener {
  private transient final List<DataSource> dataSources = new ArrayList<DataSource>();
  private DataSourcesHolder dataSourcesHolder = null;
  private Map<DataSource, DirMonitor> dataSourceToDirMonitor;
  private DataSourceToDirMonitorHolder dataSourceToDirMonitorHolder = null;
  public DefaultMutableTreeNode root;
  private DefaultMutableTreeNodeHolder rootHolder = null;
  private final List<DataSourceManagerListener> progressListeners = new ArrayList<DataSourceManagerListener>();
  private final Session session;
  private final DataSourceAssembler dataSourceAssembler;
  private final DataSourceManagerViewlet viewlet;
  private File homeDir = new File(Main.homeDir + File.separator + "datasources");
  public static final char csvDelimiter = '\t';
  private static final boolean debug = false;
  private DataSourceTreeModel dataSourceTreeModel;
  private File dataSourceFile;

  public DataSourceManager(final Session session) throws IOException {
    super();
    this.session = session;
    this.session.dataSourceManager = this;
    this.dataSourceAssembler = new DataSourceAssembler(session);
    if (getDataSourceToDirMonitorHolder() == null) {
      dataSourceToDirMonitor = new HashMap<DataSource, DirMonitor>();
      storeDataSourceToDirMonitor();
    }
    else {
      loadDataSourceToDirMonitor();
      dataSources.addAll(dataSourceToDirMonitor.keySet());
      Collections.sort(dataSources);
    }
    if (getRootHolder() == null) {
      root = new DefaultMutableTreeNode("Data source management");
      final DefaultMutableTreeNode servers = new DefaultMutableTreeNode(
          "Servers");
      root.add(servers);
      final DefaultMutableTreeNode local = new DefaultMutableTreeNode("Local");
      root.add(local);
      dataSourceTreeModel = new DataSourceTreeModel(session, root);
      storeRoot();
    }
    else {
      loadRoot();
      final Enumeration nodes = root.depthFirstEnumeration();
      while (nodes.hasMoreElements()) {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodes
            .nextElement();
      }
    }
    viewlet = new DataSourceManagerViewlet(session, this);
    Collections.sort(dataSources);
  }

  public boolean registerDataSource(final DataSource dataSource) {
    if (dataSources == null) {
      throw new RuntimeException("cannot add null dataSource to manager");
    }
    if (dataSources.contains(dataSource)) {
      throw new RuntimeException("duplicate dataSource: " + dataSource);
    }
    for (final DataSource d : dataSources) {
      if (0 == dataSource.compareTo(d)) {
        throw new RuntimeException("duplicate dataSource (same name): "
            + dataSource);
      }
    }
    DirMonitor dirMonitor = null;
    if (!Main.isDemoApplet) {
      dirMonitor = new DirMonitor();
      dirMonitor.initialize(dataSource.getDataSourceDirs(),
          FileFilters.descriptorXmlFilenameFilter, true);
      dirMonitor.addDirMonitorListener(this);
    }
    boolean b = dataSources.add(dataSource);
    if (b) {
      Collections.sort(dataSources);
      // So that the dirMonitor knows which
      // dataSource the dataMonitor is monitoring.
      dataSourceToDirMonitor.put(dataSource, dirMonitor);
      storeDataSourceToDirMonitor();
      viewlet.selectPath(dataSourceTreeModel.addDataSource(dataSource));
      fireDataSourceManagerChanged(new DataSourceManagerEvent(this, dataSource,
          DataSourceManagerEvent.Kind.MEMBER_ADDED));
      return true;
    }
    return false;
  }

  public DataSource[] getDataSources() {
    final int size = dataSources.size();
    final DataSource[] dataSourceArray = new DataSource[size];
    for (int i = 0; i < size; i++) {
      dataSourceArray[i] = dataSources.get(i);
    }
    return dataSourceArray;
  }

  public boolean removeDataSource(final DataSource dataSource) {
    final DirMonitor dirMonitor = dataSourceToDirMonitor.get(dataSource);
    dirMonitor.removeDirMonitorListener(this);
    if (dataSources.remove(dataSource)) {
      viewlet.dataSourceRemoved(dataSource);
      fireDataSourceManagerChanged(new DataSourceManagerEvent(this, dataSource,
          DataSourceManagerEvent.Kind.MEMBER_REMOVED));
      // fireDataSourceRemoved(dataSource);
      return true;
    }
    return false;
  }

  public DataSource newDataSource(final String name, final List<String> pathList)
      throws IOException, ParserConfigurationException,
      TransformerFactoryConfigurationError, TransformerException,
      Tool1Exception {
    final DataSource dataSource = new DataSource();
    dataSource.initialize(name, pathList);
    final File dataSourceFile = new File(homeDir.getPath() + "\\"
        + dataSource.getName() + "." + FileFilters.dataSourceTxtExt);
    dataSource.setDataSourceFile(dataSourceFile);
    dataSource.setDatumCount(dataSourceAssembler.assemble(dataSource));
    // dataSourceFile now created, let's read it
    DataSourceReader dataSourceReader = new DataSourceReader(session,
        dataSource);
    processUnrecognizedColumnNames(dataSourceReader);
    return dataSource;
  }

  private void fireDataSourceManagerChanged(DataSourceManagerEvent ev) {
    for (final DataSourceManagerListener listener : progressListeners) {
      listener.dataSourceManagerChanged(ev);
    }
  }

  public void fireProgressChanged(DataSource dataSource, String string,
      int min, int value, int max) {
    for (final DataSourceManagerListener listener : progressListeners) {
      listener.progressChanged(dataSource, string, min, value, max);
    }
  }

  public void addDataSourceManagerListener(
  // PointsetManager listens because it needs to update if pointset changed or
      // removed (DescriptorManager does NOT listen (directly) because the
      // DescriptorManager is listening to files
      // and DescriptorManager changes files, which in turn cause the DirMonitor
      // to fire)
      final DataSourceManagerListener listener) {
    progressListeners.add(listener);
  }

  public void removeDataSourceManagerListener(
      final DataSourceManagerListener listener) {
    progressListeners.remove(listener);
  }

  public DefaultMutableTreeNode loadRoot() {
    if (!Main.isPersisting) {
      return null;
    }
    final List<DefaultMutableTreeNodeHolder> lrh = theRoot
        .getPersistenceContext().getExtent(DefaultMutableTreeNodeHolder.class);
    if (lrh.size() == 0) {
      return null;
    }
    if (lrh.size() > 1) {
      throw new RuntimeException("multiple DataSourceTreeRootHolders");
    }
    rootHolder = lrh.get(0);
    root = rootHolder.getRoot();
    if (debug) System.out.println("DataSourceManager.load(): root = " + root);
    return root;
  }

  public Map<DataSource, DirMonitor> loadDataSourceToDirMonitor() {
    if (!Main.isPersisting) {
      return null;
    }
    final List<DataSourceToDirMonitorHolder> list = theRoot
        .getPersistenceContext().getExtent(DataSourceToDirMonitorHolder.class);
    if (list.size() == 0) {
      return null;
    }
    if (list.size() > 1) {
      throw new RuntimeException("multiple DataSourceToDirMonitorHolders");
    }
    dataSourceToDirMonitorHolder = list.get(0);
    dataSourceToDirMonitor = dataSourceToDirMonitorHolder
        .getDataSourceToDirMonitor();
    return dataSourceToDirMonitor;
  }

  public void storeRoot() {
    if (!Main.isPersisting) {
      return;
    }
    if (root == null) {
      throw new RuntimeException("root may not be null");
    }
    if (debug) System.out.println("storing DataSourceTreeNode=" + root);
    if (rootHolder == null) {
      rootHolder = new DefaultMutableTreeNodeHolder(root);
    }
    else {
      rootHolder.setRoot(root);
    }
    theRoot.getPersistenceContext().set(rootHolder);
  }

  public void storeDataSourceToDirMonitor() {
    if (!Main.isPersisting) {
      return;
    }
    if (dataSources == null) {
      throw new RuntimeException("dataSources may not be null");
    }
    if (debug)
      System.out.println("DATA SOURCE MANAGER: Storing DataSources = " + root);
    if (dataSourcesHolder == null) {
      dataSourcesHolder = new DataSourcesHolder(dataSources);
    }
    else {
      dataSourcesHolder.setDataSources(dataSources);
    }
    theRoot.getPersistenceContext().set(dataSourcesHolder);
    // ////////
    if (this.dataSourceToDirMonitor == null) {
      throw new RuntimeException("dataSourceToDirMonitor may not be null");
    }
    if (debug)
      System.out
          .println("DATA SOURCE MANAGER: Storing DataSourceToDirMonitor = "
              + dataSourceToDirMonitor);
    if (dataSourceToDirMonitorHolder == null) {
      dataSourceToDirMonitorHolder = new DataSourceToDirMonitorHolder(
          dataSourceToDirMonitor);
    }
    else {
      dataSourceToDirMonitorHolder
          .setDataSourceToDirHolder(dataSourceToDirMonitor);
    }
    theRoot.getPersistenceContext().set(dataSourceToDirMonitorHolder);
  }

  public DefaultMutableTreeNodeHolder getRootHolder() {
    if (!Main.isPersisting) {
      return null;
    }
    if (rootHolder != null) {
      return rootHolder;
    }
    DefaultMutableTreeNodeHolder rh = null;
    final List<DefaultMutableTreeNodeHolder> lrh = theRoot
        .getPersistenceContext().getExtent(DefaultMutableTreeNodeHolder.class);
    if (lrh.size() == 0) {
      return null;
    }
    if (lrh.size() > 1) {
      throw new RuntimeException("multiple tree node RootHolders");
    }
    rh = lrh.get(0);
    return rh;
  }

  public void lockRootHolder(String string) {
    if (Main.isPersisting) {
      getRootHolder().lock(string);
    }
  }

  public void unlockRootHolder(String string) {
    if (Main.isPersisting) {
      getRootHolder().lock(string);
    }
  }

  public DataSourcesHolder getDataSourceListHolder() {
    if (dataSourcesHolder != null) {
      return dataSourcesHolder;
    }
    DataSourcesHolder rh = null;
    final List<DataSourcesHolder> lrh = theRoot.getPersistenceContext()
        .getExtent(DataSourcesHolder.class);
    if (lrh.size() == 0) {
      return null;
    }
    if (lrh.size() > 1) {
      throw new RuntimeException("multiple tree node RootHolders");
    }
    rh = lrh.get(0);
    return rh;
  }

  public DataSourceToDirMonitorHolder getDataSourceToDirMonitorHolder() {
    if (!Main.isPersisting) {
      return null;
    }
    if (dataSourceToDirMonitorHolder != null) {
      return dataSourceToDirMonitorHolder;
    }
    DataSourceToDirMonitorHolder rh = null;
    final List<DataSourceToDirMonitorHolder> lrh = theRoot
        .getPersistenceContext().getExtent(DataSourceToDirMonitorHolder.class);
    if (lrh.size() == 0) {
      return null;
    }
    if (lrh.size() > 1) {
      throw new RuntimeException("multiple DataSourceToDirMonitorHolder");
    }
    rh = lrh.get(0);
    return rh;
  }

  public void clearRoot() {
    if (rootHolder != null) {
      theRoot.getPersistenceContext().delete(rootHolder);
    }
    else {
      throw new IllegalStateException("no rootHolder");
    }
  }

  public static Root theRoot;

  /**
   * Purpose is to ...
   * 
   * @author talafousj
   */
  public static final class Root implements PersistenceRoot {
    private gov.nih.ncgc.openhts.tool1.persistence.PersistenceContext context;
    private NextIdPersistent nextIdPersistent;

    public Root() {
      if (theRoot == null) {
        theRoot = this;
      }
      else {
        throw new IllegalStateException("already initialized");
      }
    }

    synchronized public long getNextId() {
      ++nextIdPersistent.nextId;
      context.set(theRoot.nextIdPersistent);
      return nextIdPersistent.nextId;
    }

    @Override
		public void setPersistenceContext(final PersistenceContext context) {
      this.context = context;
      final List<NextIdPersistent> l = context
          .getExtent(NextIdPersistent.class);
      if (l.size() > 0) {
        nextIdPersistent = l.get(0);
      }
      else {
        nextIdPersistent = new NextIdPersistent();
      }
    }

    @Override
		public PersistenceContext getPersistenceContext() {
      return context;
    }
  }

  static private class NextIdPersistent {
    private long nextId = 0L;
  }

  public void processUnrecognizedColumnNames(DataSourceReader dataSourceReader)
      throws IOException, ParserConfigurationException,
      TransformerFactoryConfigurationError, TransformerException,
      Tool1Exception {
    final String[] descriptorNames = dataSourceReader.getColumnNames();
    List<Descriptor> unrecognized = new ArrayList<Descriptor>();
    for (String descriptorName : descriptorNames) {
      if (null == session.descriptorManager.getDescriptorByName(descriptorName)) {
        Descriptor descriptor = new Descriptor(session);
        descriptor.initialize(descriptorName, null, null);
        unrecognized.add(descriptor);
      }
    }
    // Determine kind of descriptor by previewing
    dataSourceReader.recognizeDescriptorKind(unrecognized);
    // Do not register the unrecognized descriptor, instead harden it, making it
    // look like the user put it there theirself
    for (Descriptor descriptor : unrecognized) {
      File xmlFile = new File(session.descriptorManager.getHomeDir().getPath()
          + File.separator + descriptor.getName() + "."
          + FileFilters.descriptorXmlExt);
      session.descriptorManager.harden(descriptor, xmlFile);
    }
  }

  public DefaultMutableTreeNode getRoot() {
    return root;
  }

  public void clear() {
    final List<DataSource> copy = new ArrayList<DataSource>(dataSources);
    for (final DataSource dataSource : copy) {
      removeDataSource(dataSource);
    }
  }

  public void shutdown() {
    // TODO dirty
    session.statusPanel.log1("Persisting data sources for next session.");
    storeRoot();
    storeDataSourceToDirMonitor();
  }

  public void harden(DataSource dataSource, File file) throws IOException,
      ParserConfigurationException, TransformerFactoryConfigurationError,
      TransformerException {
    DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
    Document document = docBuilder.newDocument();
    Comment comment = document.createComment("Generated on "
        + new Date().toString() + " by " + Version.PRODUCT_NAME + " v"
        + Version.ToString());
    Element root = document.createElement("dataSource");
    document.appendChild(root);
    root.appendChild(comment);
    //
    Element child = document.createElement("name");
    child.setTextContent(dataSource.getName());
    root.appendChild(child);
    //
    child = document.createElement("dataSourceFile");
    child.setTextContent(dataSource.getDataSourceFile().getPath());
    root.appendChild(child);
    //
    child = document.createElement("datumCount");
    child.setTextContent(Integer.toString(dataSource.getDatumCount()));
    root.appendChild(child);
    //
    Element directories = document.createElement("directories");
    root.appendChild(directories);
    //
    for (DataSourceDir dir : dataSource.getDataSourceDirs()) {
      child = document.createElement("directory");
      child.setTextContent(dir.getPath());
      directories.appendChild(child);
    }
    // Prepare the output file
    Result result = new StreamResult(file);
    // Write the DOM document to the file
    Transformer tf = TransformerFactory.newInstance().newTransformer();
    tf.setOutputProperty(OutputKeys.INDENT, "yes");
    // tf.setOutputProperty("{http://xml. customer .org/xslt}indent-amount",
    // "4");
    tf.transform(new DOMSource(document), result);
    if (debug)
      System.out.println("DATA SOURCE MANAGER: " + "hardened dataSource = "
          + dataSource.getName());
  }

  public DataSource soften(DataSource dataSource, BufferedInputStream bis,
      String xmlFileName) throws IOException, SAXException,
      ParserConfigurationException, TransformerFactoryConfigurationError {
    dataSource.setLoading(true);
    try {
      final Document doc = XmlFilteredViewlet.parseXmlStream(bis);
      String name2 = xmlFileName.substring(0, xmlFileName.lastIndexOf("."
          + FileFilters.dataSourceXmlExt));
      name2 = name2.substring(name2.lastIndexOf('/') + 1);
      NodeList nodes = XmlFilteredViewlet.getXPathMatches(doc,
          "/dataSource/name");
      if (1 != nodes.getLength()) {
        throw new RuntimeException("failed xpath: /dataSource/name");
      }
      final String name = nodes.item(0).getTextContent().trim();
      if (!name.equals(name2)) {
        throw new RuntimeException("name of data source (" + name
            + ") does not match name of xml file: " + name2);
      }
      //
      nodes = XmlFilteredViewlet.getXPathMatches(doc,
          "/dataSource/directories/directory");
      if (0 >= nodes.getLength()) {
        // most likely an error
        new RuntimeException("failed xpath: /dataSource/directories/directory")
            .printStackTrace();
      }
      List<String> pathList = new ArrayList<String>();
      for (int i = 0; i < nodes.getLength(); i++) {
        String dirPath = nodes.item(i).getTextContent().trim();
        pathList.add(dirPath);
      }
      dataSource.initialize(name, pathList);
      //
      nodes = XmlFilteredViewlet.getXPathMatches(doc,
          "/dataSource/dataSourceFile");
      if (0 > nodes.getLength()) {
        throw new RuntimeException("failed xpath: /dataSource/dataSourceFile");
      }
      dataSource.setDataSourceFile(new File(nodes.item(0).getTextContent()
          .trim()));
      //
      nodes = XmlFilteredViewlet.getXPathMatches(doc, "/dataSource/datumCount");
      if (0 > nodes.getLength()) {
        throw new RuntimeException("failed xpath: /dataSource/datumCount");
      }
      dataSource.setDatumCount(Integer.parseInt(nodes.item(0).getTextContent()
          .trim()));
    }
    finally {
      dataSource.setLoading(false);
    }
    return dataSource;
  }

  public DataSourceManagerViewlet getViewlet() {
    return viewlet;
  }

  public void synchronize() throws IOException, SAXException,
      ParserConfigurationException, TransformerFactoryConfigurationError,
      TransformerException, Tool1Exception {
    // Make sure all hard dataSources have been softened.
    final File[] xmlfiles = homeDir
        .listFiles(FileFilters.dataSourceXmlFilenameFilter);
    for (final File xmlFile : xmlfiles) {
      String name = FileFilters.entityNameFromFile(xmlFile,
          FileFilters.dataSourceXmlExt);
      if (null == getDataSourceByName(name)) {
        // has not been registered, or restored from persistence yet
        final DataSource dataSource = new DataSource();
        registerDataSource(dataSource);
        SwingWorker<String, Object> worker = new SwingWorker<String, Object>() {
          @Override
          public String doInBackground() {
            BufferedInputStream bis = null;
            try {
              bis = new BufferedInputStream(new FileInputStream(xmlFile));
              soften(dataSource, bis, xmlFile.getName());
              DataSourceReader dataSourceReader = new DataSourceReader(session,
                  dataSource);
              processUnrecognizedColumnNames(dataSourceReader);
              fireDataSourceManagerChanged(new DataSourceManagerEvent(this,
                  dataSource, DataSourceManagerEvent.Kind.MEMBER_CHANGED));
            }
            catch (OutOfMemoryError ex) {
              session.errorSupport("While reading data:", ex, "todo");
              return null;
            }
            catch (Throwable ex) {
              session.errorSupport("While synchronizing dataSource: ", ex,
                  "todo");
            }
            finally {
              try {
                bis.close();
              }
              catch (IOException e) {
                e.printStackTrace();
              }
              // if (!dataSource2.getName().equals(name)) {
              // throw new IOException("File improperly named: " +
              // xmlFile.getPath());
              // }
            }
            return "success";
          }

          @Override
          protected void done() {
            // if (get() != "success") {
            // }
          }
        };
        worker.execute();
      }
    }
  }

  public String[] getDataSourceNames() {
    final String[] dataSourceNames = new String[dataSources.size()];
    for (int i = 0; i < dataSources.size(); i++) {
      dataSourceNames[i] = dataSources.get(i).getName();
    }
    return dataSourceNames;
  }

  public DataSource getDataSourceByName(String name) {
    String[] dataSourceNames = getDataSourceNames();
    int i = Arrays.binarySearch(dataSourceNames, name);
    if (i < 0) {
      return null;
    }
    return dataSources.get(i);
  }

  public static void configureDb4o() {
    Db4oPersistenceService.configureDb4oForClass(
        DefaultMutableTreeNodeHolder.class, DataSourceDir.class,
        DataSource.class,
        // DataSourcesHolder.class,
        DataSourceTreeNode.class, DataSourceToDirMonitorHolder.class,
        DirMonitor.class);
  }

  public File getHomeDir() {
    return homeDir;
  }

  public DataSourceTreeModel getTreeModel() {
    return dataSourceTreeModel;
  }

  public File getDataSourceFile() {
    return dataSourceFile;
  }

  void readDataSource(final DataSource dataSource) throws IOException {
    // Read data source from file it specifies
    final String s = homeDir.getPath() + "\\" + dataSource.getName() + "."
        + FileFilters.dataSourceTxtExt;
    final CSVReader csvReader = new CSVReader(new FileReader(s),
        DataSourceManager.csvDelimiter);
    final List<String[]> pcsdataLineList = csvReader.readAll();
    System.out.println("DATA SOURCE MANAGER: number of MOL in MD= "
        + (pcsdataLineList.size() - 1));
    if (!FileFilters.molIdColName.equals(pcsdataLineList.get(0)[0].trim())) {
      throw new RuntimeException("First column first line in file must be: "
          + FileFilters.molIdColName);
    }
  }

  private void descriptorAdded(final Descriptor descriptor) {
    System.out.println("DATA SOURCE MANAGER: descriptor added = " + descriptor);
    if (Main.isDemoApplet) {
      return;
    }
    for (final DataSource dataSource : dataSources) {
      boolean changed = false;
      final List<File> molFiles = dataSource.getMolFiles();
      for (final File molFile : molFiles) {
        if ((descriptor.getDescriptorKind() == Descriptor.Kind.Calculated)
            && descriptor.runCommand != null) {
          changed = true;
          descriptor.calculate(molFile);
        }
      }
      if (changed) {
        try {
          dataSourceAssembler.assemble(dataSource);
        }
        catch (IOException e) {
          // TODO
          e.printStackTrace();
        }
        catch (Tool1Exception e) {
          // TODO
          e.printStackTrace();
        }
        try {
          DataSourceReader dataSourceReader = new DataSourceReader(session,
              dataSource);
          processUnrecognizedColumnNames(dataSourceReader);
        }
        catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        catch (ParserConfigurationException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        catch (TransformerFactoryConfigurationError e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        catch (TransformerException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        catch (Tool1Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        viewlet.dataSourceChanged(dataSource);
        fireDataSourceManagerChanged(new DataSourceManagerEvent(this,
            dataSource, DataSourceManagerEvent.Kind.MEMBER_CHANGED));
        // fireDataSourceChanged(dataSource);
      }
    }
  }

  @Override
	public void descriptorManagerChanged(DescriptorManagerEvent ev) {
    if (ev.member instanceof Descriptor) {
      Descriptor descriptor = (Descriptor) ev.member;
      switch (ev.kind) {
      case CHANGED:
        break;
      case MEMBER_CHANGED:
        break;
      case MEMBER_LOADED:
        break;
      case MEMBER_ADDED:
        descriptorAdded(descriptor);
        break;
      case MEMBER_REMOVED:
        // TODO
        break;
      case MEMBER_REPLACED:
        // TODO
        break;
      case MEMBERS_SELECTION:
        break;
      }
    }
    else if (ev.member instanceof Basis) {
      // nop
    }
  }

  @Override
	public void dirMonitorChanged(DirMonitorEvent ev) {
    switch (ev.kind) {
    case MANAGER_CHANGED:
      break;
    case FILE_ADDED:
      break;
    case FILE_CHANGED:
      break;
    case FILE_REMOVED:
      break;
    case DIR_REMOVED:
      break;
    case TERMINATED:
      break;
    }
  }

  public void dataSourceChanged(DataSource dataSource) {
    fireDataSourceManagerChanged(new DataSourceManagerEvent(this, dataSource,
        DataSourceManagerEvent.Kind.MEMBER_ADDED));
  }
}
