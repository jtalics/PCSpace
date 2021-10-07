package gov.nih.ncgc.openhts.tool1.descriptorManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
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
import gov.nih.ncgc.openhts.tool1.FileFilters;
import gov.nih.ncgc.openhts.tool1.Main;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.Version;
import gov.nih.ncgc.openhts.tool1.dirMonitor.DirMonitor;
import gov.nih.ncgc.openhts.tool1.dirMonitor.DirMonitorEvent;
import gov.nih.ncgc.openhts.tool1.dirMonitor.DirMonitorListener;
import gov.nih.ncgc.openhts.tool1.persistence.Db4oPersistenceService;
import gov.nih.ncgc.openhts.tool1.persistence.PersistenceContext;
import gov.nih.ncgc.openhts.tool1.persistence.PersistenceRoot;
import gov.nih.ncgc.openhts.tool1.plotManager.PlotManager;
import gov.nih.ncgc.openhts.tool1.plotManager.PlotManagerEvent;
import gov.nih.ncgc.openhts.tool1.plotManager.PlotManagerListener;
import gov.nih.ncgc.openhts.tool1.pointsetManager.AxesColumnHeadsMapping;
import gov.nih.ncgc.openhts.tool1.pointsetManager.ColumnHead;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetManagerEntity;
import gov.nih.ncgc.openhts.tool1.util.xml.XmlFilteredViewlet;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class DescriptorManager extends JPanel implements DirMonitorListener,
    PlotManagerListener {
  private transient Session session;
  private final List<Descriptor> descriptors = new ArrayList<Descriptor>();
  private final List<Basis> bases = new ArrayList<Basis>();
  private final JTree tree = new JTree();
  private File homeDir = new File(Main.homeDir + File.separator + "descriptors");
  private transient final List<DescriptorManagerListener> listeners = new ArrayList<DescriptorManagerListener>();
  private DefaultMutableTreeNodeHolder rootHolder = null;
  private DefaultMutableTreeNode root;
  DescriptorTreeModel treeModel;
  private transient DescriptorManagerViewlet viewlet;
  private static final boolean debug = false;

  public DescriptorManager(final Session session) {
    this.session = session;
    this.session.descriptorManager = this;
    final DefaultMutableTreeNode orphans = new DefaultMutableTreeNode("Orphans");
    if (getRootHolder() == null) {
      root = new DefaultMutableTreeNode("Descriptors");
      root.add(orphans);
      for (final Descriptor descriptor : getDescriptors()) {
        orphans.add(new DescriptorTreeNode(descriptor));
      }
      treeModel = new DescriptorTreeModel(session, root);
      // root.setModel(pointsetTreeModel);
      storeRoot();
    }
    else {
      root = getRootHolder().getRoot();
      treeModel = new DescriptorTreeModel(session, root);
      loadRoot();
      for (Enumeration<DefaultMutableTreeNode> e = (root)
          .depthFirstEnumeration(); e.hasMoreElements();) {
        final DefaultMutableTreeNode dmtn = e.nextElement();
        final Object object = dmtn.getUserObject();
        if (object instanceof Basis) {
          final Basis basis = (Basis) object;
          basis.initializeTransient();
          bases.add(basis);
        }
        else if (object instanceof Descriptor) {
          final Descriptor descriptor = (Descriptor) object;
          descriptor.setSession(session);
          descriptor.initializeTransient();
          descriptors.add(descriptor);
        }
        else {
          // root
        }
      }
    }
    viewlet = new DescriptorManagerViewlet(session);
    Collections.sort(descriptors);
    Collections.sort(bases);
    // Assume that all is upto date, use false
    if (!Main.isDemoApplet) {
      DirMonitor dirMonitor = new DirMonitor();
      dirMonitor.initialize(new File[] { homeDir },
          FileFilters.descriptorXmlFilenameFilter, false);
      dirMonitor.addDirMonitorListener(this);
      dirMonitor.start();
    }
  }

  public void synchronizeDescriptors() throws Exception {
    // Make sure all hard descriptors have a soft mate.
    final File[] xmlfiles = homeDir
        .listFiles(FileFilters.descriptorXmlFilenameFilter);
    for (final File xmlFile : xmlfiles) {
      String name = FileFilters.entityNameFromFile(xmlFile,
          FileFilters.descriptorXmlExt);
      Descriptor descriptor = getDescriptorByName(name);
      if (descriptor == null) {
        descriptor = softenDescriptor(new BufferedInputStream(
            new FileInputStream(xmlFile)), xmlFile.getPath());
        if (!descriptor.getName().equals(name)) {
          throw new IOException("File improperly named: " + xmlFile.getPath());
        }
        registerDescriptor(descriptor);
      }
    }
  }

  // 
  public void synchronizeBases() throws Exception {
    // Make sure all hard bases have a soft mate.
    final File[] xmlfiles = homeDir
        .listFiles(FileFilters.basisXmlFilenameFilter);
    List<Basis> bases = new ArrayList<Basis>();
    for (final File xmlFile : xmlfiles) {
      String xmlFileName = xmlFile.getName();
      String name = xmlFileName.substring(0, xmlFileName.lastIndexOf("."
          + FileFilters.basisXmlExt));
      Basis basis = getBasisByName(name);
      if (basis == null) {
        basis = softenBasis(new BufferedInputStream(
            new FileInputStream(xmlFile)), xmlFile.getName());
        if (!basis.getName().equals(name)) {
          throw new IOException("File improperly named: " + xmlFile.getPath());
        }
        bases.add(basis);
      }
    }
    for (Basis basis : bases) {
      basis.findMissingDescriptors();
      basis.initialize();
      registerBasis(basis);
    }
    for (Basis basis : bases) {
      registerBasis(basis);
    }
  }

  public Descriptor softenDescriptor(final BufferedInputStream bis,
      String xmlFileName) throws Exception {
    final Document doc = XmlFilteredViewlet.parseXmlStream(bis);
    // NAME
    NodeList nodes = XmlFilteredViewlet
        .getXPathMatches(doc, "/descriptor/name");
    if (1 != nodes.getLength()) {
      throw new IOException("failed xpath: /descriptor/name");
    }
    final String name = nodes.item(0).getTextContent().trim();
    if (knownDescriptor(name)) {
      return null;
    }
    // KIND
    nodes = XmlFilteredViewlet.getXPathMatches(doc, "/descriptor/kind");
    if (1 != nodes.getLength()) {
      throw new IOException("failed xpath: /descriptor/kind");
    }
    final String kind = nodes.item(0).getTextContent().trim();
    // TODO: read in Descriptor kind too.
    if (!ColumnHead.legalKind(kind)) {
      throw new IOException("Unknown descriptor kind: " + kind + "\nin file: ");
    }
    // RUN-COMMAND
    nodes = XmlFilteredViewlet.getXPathMatches(doc, "/descriptor/run-command");
    if (1 != nodes.getLength()) {
      throw new IOException("failed xpath: /descriptor/run-command");
    }
    final String runCommand = nodes.item(0).getTextContent().trim();
    // CONFIG-XML-PATH
    nodes = XmlFilteredViewlet.getXPathMatches(doc,
        "/descriptor/config-xml-path");
    if (1 < nodes.getLength()) {
      throw new RuntimeException("failed xpath: /descriptor/config-xml-path");
    }
    final String xmlFilePath = nodes.item(0).getTextContent().trim();
    // TODO: validate elsewhere
    // if (!new File(xmlFilePath).canRead()) {
    // throw new IOException(
    // "problem with xpath value of /descriptor/run-command=" + xmlFilePath);
    // }
    final Descriptor descriptor = new Descriptor(session);
    // TODO: supply descriptor kind
    descriptor.initialize(name, null, ColumnHead.Kind.valueOf(kind));
    descriptor.xmlFile = new File(xmlFileName);
    descriptor.runCommandXmlFileName = xmlFilePath;
    descriptor.runCommand = runCommand;
    if (!descriptor.isValid()) {
      return null;
    }
    if (debug)
      System.out.println("DIRMONITOR: " + "softened descriptor = "
          + descriptor.getName());
    return descriptor;
  }

  public Basis softenBasis(BufferedInputStream bis, final String xmlFileName)
      throws Exception {
    final Document doc = XmlFilteredViewlet.parseXmlStream(bis);
    String name2 = xmlFileName.substring(0, xmlFileName.lastIndexOf("."
        + FileFilters.basisXmlExt));
    name2 = name2.substring(name2.lastIndexOf('/') + 1);
    NodeList nodes = XmlFilteredViewlet.getXPathMatches(doc, "/basis/name");
    if (1 != nodes.getLength()) {
      throw new RuntimeException("failed xpath: /basis/name");
    }
    final String name = nodes.item(0).getTextContent().trim();
    if (!name.equals(name2)) {
      throw new RuntimeException("name of basis (" + name
          + ") does not match name of xml file: " + name2);
    }
    final Basis basis = new Basis(session, name);
    basis.setName(name);
    //
    nodes = XmlFilteredViewlet.getXPathMatches(doc,
        "/basis/descriptors/descriptor-name");
    if (0 > nodes.getLength()) {
      throw new RuntimeException(
          "failed xpath: /basis/descriptors/descriptor-name");
    }
    for (int i = 0; i < nodes.getLength(); i++) {
      int j;
      String descriptorName = nodes.item(i).getTextContent().trim();
      if (0 > (j = Arrays.binarySearch(getDescriptorNames(), descriptorName))) {
        basis.addMissingDescriptorName(descriptorName);
      }
      else {
        basis.addDescriptor(descriptors.get(j));
      }
    }
    // basis.initialize();
    if (debug)
      System.out.println("DESCRIPTOR MANAGER: " + "softened basis = "
          + basis.getName());
    return basis;
  }

  public File harden(final Descriptor descriptor, File xmlFile)
      throws IOException, ParserConfigurationException,
      TransformerFactoryConfigurationError, TransformerException {
    // We need a Document
    DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
    Document document = docBuilder.newDocument();
    Comment comment = document.createComment("Generated on "
        + new Date().toString() + " by " + Version.PRODUCT_NAME + " v"
        + Version.ToString());
    //
    Element root = document.createElement("descriptor");
    document.appendChild(root);
    root.appendChild(comment);
    //
    Element child = document.createElement("name");
    child.setTextContent(descriptor.getName());
    root.appendChild(child);
    // TODO: save descriptor kind too
    child = document.createElement("kind");
    child.setTextContent(descriptor.getColumnHeadKind().name());
    root.appendChild(child);
    //
    child = document.createElement("run-command");
    child.setTextContent(descriptor.runCommand);
    root.appendChild(child);
    //
    child = document.createElement("config-xml-path");
    child.setTextContent(descriptor.runCommandXmlFileName);
    root.appendChild(child);
    // Prepare the output file
    Result result = new StreamResult(xmlFile);
    // Write the DOM document to the file
    Transformer tf = TransformerFactory.newInstance().newTransformer();
    tf.setOutputProperty(OutputKeys.INDENT, "yes");
    // tf.setOutputProperty("{http://xml. customer .org/xslt}indent-amount",
    // "4");
    tf.transform(new DOMSource(document), result);
    if (debug)
      System.out.println("DESCRIPTOR MANAGER: " + "hardened descriptor = "
          + descriptor.getName());
    return xmlFile;
  }

  public File harden(final Basis basis) throws IOException,
      ParserConfigurationException, TransformerFactoryConfigurationError,
      TransformerException {
    // We need a Document
    DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
    Document document = docBuilder.newDocument();
    Comment comment = document.createComment("Generated on "
        + new Date().toString() + " by " + Version.PRODUCT_NAME + " v"
        + Version.ToString());
    Element root = document.createElement("basis");
    document.appendChild(root);
    root.appendChild(comment);
    Element name = document.createElement("name");
    name.setTextContent(basis.getName());
    root.appendChild(name);
    Element descriptors = document.createElement("descriptors");
    root.appendChild(descriptors);
    for (Descriptor descriptor : basis.getDescriptors()) {
      Element descname = document.createElement("descriptor-name");
      descname.setTextContent(descriptor.getName());
      descriptors.appendChild(descname);
    }
    // Prepare the output file
    File xmlFile = new File(homeDir.getPath() + File.separator
        + basis.getName() + "." + FileFilters.basisXmlExt);
    Result result = new StreamResult(xmlFile);
    // Write the DOM document to the file
    Transformer tf = TransformerFactory.newInstance().newTransformer();
    tf.setOutputProperty(OutputKeys.INDENT, "yes");
    // tf.setOutputProperty("{http://xml. customer .org/xslt}indent-amount",
    // "4");
    tf.transform(new DOMSource(document), result);
    if (debug)
      System.out.println("DESCRIPTOR MANAGER: " + "hardened basis = "
          + basis.getName());
    return xmlFile;
  }

  public boolean registerDescriptor(final Descriptor descriptor) {
    // NOTE: if you add a Descriptor, you are responsible for hardening it
    if (descriptor == null) {
      throw new RuntimeException("cannot add null descriptor to manager");
    }
    if (descriptors.contains(descriptor)) {
      throw new RuntimeException("duplicate descriptor: " + descriptor);
    }
    for (final Descriptor d : descriptors) {
      if (0 == descriptor.compareTo(d)) {
        throw new RuntimeException("duplicate descriptor (same name): "
            + descriptor);
      }
    }
    final boolean b = descriptors.add(descriptor);
    if (b) {
      Collections.sort(descriptors);
      treeModel.addDescriptor(descriptor);
      storeRoot();
      fireDescriptorManagerChanged(new DescriptorManagerEvent(this, descriptor,
          DescriptorManagerEvent.Kind.MEMBER_ADDED));
      return true;
    }
    return false;
  }

  public boolean removeDescriptor(final Descriptor descriptor) {
    for (Basis basis : bases) {
      basis.removeDescriptor(descriptor);
    }
    final boolean b = descriptors.remove(descriptor);
    if (b) {
      treeModel.removeDescriptor(descriptor);
      storeRoot();
      fireDescriptorManagerChanged(new DescriptorManagerEvent(this, descriptor,
          DescriptorManagerEvent.Kind.MEMBER_REMOVED));
      return true;
    }
    return false;
  }

  public boolean replaceDescriptor(final Descriptor prevDescriptor,
      final Descriptor newDescriptor) {
    Descriptor descriptor = null;
    int i;
    for (i = 0; i < descriptors.size(); i++) {
      descriptor = descriptors.get(i);
      if (descriptor == prevDescriptor) {
        break;
      }
    }
    if (descriptor == null) {
      return false;
    }
    descriptors.add(i, newDescriptor);
    Collections.sort(descriptors);
    storeRoot();
    fireDescriptorManagerChanged(new DescriptorManagerEvent(this, descriptor,
        DescriptorManagerEvent.Kind.MEMBER_REPLACED));
    return true;
  }

  private boolean knownDescriptor(final String name) {
    return false;
  }

  void fireDescriptorManagerChanged(DescriptorManagerEvent ev) {
    for (final DescriptorManagerListener listener : listeners) {
      listener.descriptorManagerChanged(ev);
    }
  }

  public void addDescriptorManagerListener(
  // DataSourceManager listens because it needs to recalculate descriptors(?)
      // PointsetManager listens because it needs update basis display to user
      // DescriptorSelectorViewlet listens because it needs to update
      // descriptors display
      // BasisSelectorViewlet listens because it needs to update basis display
      final DescriptorManagerListener listener) {
    listeners.add(listener);
  }

  public void removeDescriptorManagerListener(
      final DescriptorManagerListener listener) {
    listeners.remove(listener);
  }

  public Descriptor getDescriptorByName(String name) {
    if (name == null) {
      throw new RuntimeException("cannot get descriptor by null name");
    }
    String[] descriptorNames = getDescriptorNames();
    int i = Arrays.binarySearch(descriptorNames, name);
    if (i < 0) {
      return null;
    }
    return descriptors.get(i);
  }

  public String[] getDescriptorNames() {
    final String[] descriptorIds = new String[descriptors.size()];
    for (int i = 0; i < descriptors.size(); i++) {
      descriptorIds[i] = descriptors.get(i).getName();
    }
    return descriptorIds;
  }

  public Descriptor[] getDescriptors() {
    final Descriptor[] descriptorsArray = new Descriptor[descriptors.size()];
    for (int i = 0; i < descriptors.size(); i++) {
      descriptorsArray[i] = descriptors.get(i);
    }
    return descriptorsArray;
  }

  public boolean registerBasis(final Basis basis) {
    if (basis == null) {
      throw new RuntimeException("cannot add a null basis");
    }
    if (bases.contains(basis)) {
      return false;
      // throw new RuntimeException("TODO: handle duplicate basis: " + basis);
    }
    for (final Basis b : bases) {
      if (basis.getName().equals(b.getName())) {
        throw new RuntimeException("basis with non unique name: " + basis);
      }
    }
    final boolean b = bases.add(basis);
    if (b) {
      Collections.sort(bases);
      viewlet.selectPath(treeModel.addBasis(basis));
      storeRoot();
      fireDescriptorManagerChanged(new DescriptorManagerEvent(this, basis,
          DescriptorManagerEvent.Kind.MEMBER_ADDED));
    }
    return b;
  }

  public boolean removeBasis(final Basis basis) {
    final boolean b = bases.remove(basis);
    if (b) {
      treeModel.removeBasis(basis);
      storeRoot();
      fireDescriptorManagerChanged(new DescriptorManagerEvent(this, basis,
          DescriptorManagerEvent.Kind.MEMBER_REMOVED));
    }
    return b;
  }

  public boolean containsBasis(final Basis basis2) {
    for (final Basis basis : bases) {
      if (basis2.equals(basis)) {
        return false;
      }
    }
    return false;
  }

  public Basis[] getBases() {
    final Basis[] copy = new Basis[bases.size()];
    for (int i = 0; i < bases.size(); i++) {
      copy[i] = bases.get(i);
    }
    return copy;
  }

  public int getBasisCount() {
    return bases.size();
  }

  public Basis getBasisByName(String name) {
    if (name == null) {
      throw new RuntimeException("cannot get basis by null name");
    }
    String[] basisNames = getBasisNames();
    int i = Arrays.binarySearch(basisNames, name);
    if (i < 0) {
      return null;
    }
    return bases.get(i);
  }

  public String[] getBasisNames() {
    String[] basisNames = new String[bases.size()];
    for (int i = 0; i < bases.size(); i++) {
      basisNames[i] = bases.get(i).getName();
    }
    return basisNames;
  }

  public Basis getBasisAt(int index) {
    return bases.get(index);
  }

  public int getBasisIndex(final Basis basis) {
    return bases.indexOf(basis);
  }

  public DefaultTreeModel getTreeModel() {
    return treeModel;
  }

  public void clear() {
    final List<Descriptor> copy = new ArrayList<Descriptor>(descriptors);
    for (final Descriptor descriptor : copy) {
      removeDescriptor(descriptor);
    }
    final List<Basis> copy2 = new ArrayList<Basis>(bases);
    for (final Basis basis : copy2) {
      removeBasis(basis);
    }
  }

  public void invert() {
    switch (treeModel.getMode()) {
    case BasisLeaf:
      treeModel.clear();
      treeModel.setMode(DescriptorTreeModel.Mode.DescriptorLeaf);
      break;
    case DescriptorLeaf:
      treeModel.clear();
      treeModel.setMode(DescriptorTreeModel.Mode.BasisLeaf);
      break;
    }
    for (final Basis basis : bases) {
      treeModel.addBasis(basis);
    }
    for (final Descriptor descriptor : descriptors) {
      treeModel.addDescriptor(descriptor);
    }
  }

  public DescriptorManagerViewlet getViewlet() {
    return viewlet;
  }

  private static final long serialVersionUID = 1L;

  public void dimensionalityChanged(final PlotManager plotManager, final int dim) {
    new RuntimeException("TODO").printStackTrace();
  }

  public void shutdown() {
    // TODO dirty
    session.statusPanel
        .log1("Persisting descriptors and bases for next session.");
    storeRoot();
  }

  public Descriptor[] getSelectedDescriptors() {
    return viewlet.getSelectedDescriptors();
  }

  public Basis[] getSelectedBases() {
    return viewlet.getSelectedBases();
  }

  public Basis getSelectedBasis() {
    // TODO Auto-generated method stub
    return viewlet.getSelectedBasis();
  }

  public File getHomeDir() {
    return homeDir;
  }

  public DefaultMutableTreeNode loadRoot() {
    if (!Main.isPersisting) {
      return null;
    }
    DefaultMutableTreeNode root = null;
    final List<DefaultMutableTreeNodeHolder> lrh = theRoot
        .getPersistenceContext().getExtent(DefaultMutableTreeNodeHolder.class);
    if (lrh.size() == 0) {
      return null;
    }
    if (lrh.size() > 1) {
      throw new RuntimeException("multiple tree node RootHolders");
    }
    rootHolder = lrh.get(0);
    root = rootHolder.getRoot();
    if (debug) System.out.println("DescriptorManager.load(): root = " + root);
    return root;
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
      getRootHolder().unlock(string);
    }
  }

  public void storeRoot() {
    if (!Main.isPersisting) {
      return;
    }
    if (root == null) {
      throw new RuntimeException("root may not be null");
    }
    if (debug) System.out.println("storing DescriptorManager.root");
    if (rootHolder == null) {
      rootHolder = new DefaultMutableTreeNodeHolder(root);
    }
    else {
      rootHolder.setRoot(root);
    }
    theRoot.getPersistenceContext().set(rootHolder);
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

  public static void configureDb4o() {
    Db4oPersistenceService.configureDb4oForClass(
        DefaultMutableTreeNodeHolder.class, Basis.class, BasisTreeNode.class,
        AxesColumnHeadsMapping.class, Descriptor.class,
        DescriptorTreeNode.class, ColumnHead.class);
  }

  public int getDescriptorCount() {
    return descriptors.size();
  }

  public Object getDescriptorAt(int i) {
    return descriptors.get(i);
  }

  public void selectBases(Basis[] basis) {
    fireDescriptorManagerChanged(new DescriptorManagerEvent(this, bases,
        DescriptorManagerEvent.Kind.MEMBERS_SELECTION));
  }

  // // @Implements PointsetManagerListener
  // public void basisSelectionChanged(PointsetManager manager,
  // Basis[] selectedBases) {
  // for (Basis basis : selectedBases) {
  // DefaultMutableTreeNode[] path = treeModel.search(
  // (DefaultMutableTreeNode) treeModel.getRoot(), basis);
  // if (path != null && path.length > 0) {
  // viewlet.selectPath(path);
  // }
  // }
  // }
  @Override
	public void dirMonitorChanged(DirMonitorEvent ev) {
    File file = (File) ev.member;
    Descriptor descriptor;
    switch (ev.kind) {
    case MANAGER_CHANGED:
      break;
    case FILE_ADDED:
      System.out.println("DESCRIPTOR MANAGER: file added: " + file);
      descriptor = null;
      try {
        descriptor = softenDescriptor(new BufferedInputStream(
            new FileInputStream(file)), file.getPath());
      }
      catch (final Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      if (descriptor != null) {
        registerDescriptor(descriptor);
      }
      break;
    case FILE_CHANGED:
      System.out.println("DESCRIPTOR MANAGER file modified: " + file);
      // Find the descriptor formed from this file
      Descriptor modified = null;
      for (Descriptor descriptor2 : descriptors) {
        // TODO: determine what to do if the descriptor is not calculable,
        // like an activity, should open up that file to re read it?
        // descriptor.xml should not be null, but just rubber stamp it right
        // here for right now
        if (descriptor2.xmlFile == null || descriptor2.xmlFile.equals(file)) {
          modified = descriptor2;
          // By definition, there should be no duplicates on the list, so break
          break;
        }
      }
      if (modified == null) {
        throw new RuntimeException("unknown descriptor to modify from file = "
            + file);
      }
      try {
        descriptor = softenDescriptor(new BufferedInputStream(
            new FileInputStream(file)), file.getPath());
        if (descriptor == null) {
          throw new RuntimeException("failed load:" + file);
        }
        replaceDescriptor(modified, descriptor);
      }
      catch (final Exception e) {
        e.printStackTrace();
      }
      // This app has convenience editors that may already be updated
      // but an external editor may have modified the file. So we need
      // to update the descriptor viewlet anyways.
      // if (remove != null) {removeDescriptor(remove);}
      // Descriptor descriptor = null;
      // try {
      // descriptor = loadDescriptorXml(file);
      // if (descriptor == null) {
      // throw new RuntimeException("failed load:"+file);
      // }
      // addDescriptor(descriptor);
      // }
      // catch (final IOException e) {
      // e.printStackTrace();
      // }
      break;
    case FILE_REMOVED:
      System.out.println("DESCRIPTOR MANAGER: file removed: " + file);
      final List<Descriptor> removs = new ArrayList<Descriptor>();
      for (final Descriptor descriptor2 : descriptors) {
        if (descriptor2.xmlFile.equals(file)) {
          removs.add(descriptor2);
        }
      }
      for (final Descriptor remov : removs) {
        removeDescriptor(remov);
      }
      break;
    case DIR_REMOVED:
      System.out
          .println("DESCRIPTOR MANAGER: missing dir " + file /* actually a dir */);
      break;
    case TERMINATED:
      throw new RuntimeException("TODO");
      // break;
    }
  }

  @Override
	public void plotManagerChanged(PlotManagerEvent ev) {
    switch (ev.kind) {
    case CHANGED:
      break;
    case BASISMODE_CHANGED:
      treeModel.refreshBasisTreeNode((Basis) ev.member);
      break;
    case TEXT_SELECTION_CHANGED:
      // nop
      break;
    case POINTSET_ORDER_CHANGED:
      // TODO
      break;
    case TEXT_PROPERTIES_CHANGED:
      // nop
      break;
    case PREVIEW_CHANGED: // should be ID_SELECTION_CHANGED
      // nop
      break;
    case DIMENSIONALITY:
      break;
    }
  }

  public void filterChanged(PointsetManagerEntity entity,
      ColumnHead columnHead, int limitLowPercentage, int limitHighPercentage,
      boolean showBelowLimit) {
    if (entity instanceof Basis) {
      ((Basis) entity).filterChanged(columnHead, limitLowPercentage,
          limitHighPercentage, showBelowLimit);
      fireDescriptorManagerChanged(new DescriptorManagerEvent(this, entity,
          DescriptorManagerEvent.Kind.MEMBER_CHANGED));
    }
  }

  public void basisModified(Basis basis) {
    fireDescriptorManagerChanged(new DescriptorManagerEvent(this,
        basis, DescriptorManagerEvent.Kind.MEMBER_CHANGED));
  }

}
