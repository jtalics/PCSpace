// MANAGERS COLLECT MOST OF THE INTELLIGENCE CONCERNING THE ENTITY THEY MANAGE.  
// THE ENTITY IS AS DUMB AND PERSISTABLE AS POSSIBLE
// MANAGERS ARE MIDDLEWARE BETWEEN THE ENTITY AND USER
package gov.nih.ncgc.openhts.tool1.pointsetManager;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import gov.nih.ncgc.openhts.tool1.FileFilters;
import gov.nih.ncgc.openhts.tool1.Main;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.Tool1Exception;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.DataSource;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.DataSourceManager;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.DataSourceManagerEvent;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.DataSourceManagerListener;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.DataSourceReader;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.filter.DataSourceFilter;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.filter.FilterCreateDialog;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Basis;
import gov.nih.ncgc.openhts.tool1.descriptorManager.BasisSpecifyDialog;
import gov.nih.ncgc.openhts.tool1.descriptorManager.BasisTreeNode;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Descriptor;
import gov.nih.ncgc.openhts.tool1.descriptorManager.DescriptorManager;
import gov.nih.ncgc.openhts.tool1.descriptorManager.DescriptorManagerEvent;
import gov.nih.ncgc.openhts.tool1.descriptorManager.DescriptorManagerListener;
import gov.nih.ncgc.openhts.tool1.persistence.Db4oPersistenceService;
import gov.nih.ncgc.openhts.tool1.persistence.PersistenceContext;
import gov.nih.ncgc.openhts.tool1.persistence.PersistenceRoot;
import gov.nih.ncgc.openhts.tool1.plotManager.Axes;
import gov.nih.ncgc.openhts.tool1.plotManager.PlotManagerEvent;
import gov.nih.ncgc.openhts.tool1.plotManager.PlotManagerListener;
import gov.nih.ncgc.openhts.tool1.spreadsheet.PointsetTable;
import gov.nih.ncgc.openhts.tool1.spreadsheet.SpreadsheetEvent;
import gov.nih.ncgc.openhts.tool1.spreadsheet.SpreadsheetListener;
import gov.nih.ncgc.openhts.tool1.util.ProgressBar;
import gov.nih.ncgc.openhts.tool1.util.ProgressListener;
import gov.nih.ncgc.openhts.tool1.util.colorizer.Waypoint;

public class PointsetManager implements DataSourceManagerListener,
    DescriptorManagerListener, PlotManagerListener, SpreadsheetListener {
  public static final int REPLACE = 0;
  public static final int DELETE = 1;
  final Session session;
  private final List<Pointset> pointsets = new ArrayList<Pointset>();
  private final List<PointsetManagerListener> listeners = new ArrayList<PointsetManagerListener>();
  private final List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();
  public PointsetManagerViewlet viewlet;
  private DefaultMutableTreeNodeHolder rootHolder = null;
  private final Map<DataSource, List<Pointset>> dataSourceToPointsets = new HashMap<DataSource, List<Pointset>>();
  public PointsetTreeModel pointsetTreeModel;
  int nAdaptVisible, maxNPoints;
  File homeDir = new File(Main.homeDir + File.separator + "pointsets");

  public PointsetManager(final Session session) {
    super();
    this.session = session;
    this.session.pointsetManager = this;
    DefaultMutableTreeNode root;
    if (getRootHolder() == null) {
      root = new DefaultMutableTreeNode(null);
      for (final Basis basis : session.descriptorManager.getBases()) {
        root.add(new BasisTreeNode(session, basis));
      }
      pointsetTreeModel = new PointsetTreeModel(session, root);
      storeRoot();
    }
    else {
      root = getRootHolder().getRoot();
      pointsetTreeModel = new PointsetTreeModel(session, root);
      loadRoot();
      for (final Enumeration<DefaultMutableTreeNode> e = (root)
          .depthFirstEnumeration(); e.hasMoreElements();) {
        final DefaultMutableTreeNode dmtn = e.nextElement();
        final Object object = dmtn.getUserObject();
        if (object instanceof Basis) {
          // DescriptorManager handles their init
        }
        else if (object instanceof Pointset) {
          final Pointset pointset = (Pointset) object;
          pointset.setSession(session);
          pointset.initializeTransient();
          pointsets.add(pointset);
          session.updateEnablement();
        }
        else {
          // nop
        }
      }
      session.plotManager.refreshCanvas();
    }
    Collections.sort(pointsets); // precautionary sort
    viewlet = new PointsetManagerViewlet(session);
    addPointsetManagerListener(viewlet);
    // NOTE: only use the viewlet reference to get info, like selections, not
    // modify the viewlet; because the viewlet is listneing to us. The viewlet
    // is "for" the user. The rest of the app deals with the manager.
  }

  public boolean uniqueName(String name) {
    for (final Pointset pointset : pointsets) {
      if (pointset.getName().equals(name)) {
        return false;
      }
    }
    return true;
  }

  // // @Implements
  // private void firePointsetAdded(final Pointset pointset) {
  // // System.out.println("POINTSET MANAGER: firing pointset added " +
  // // pointset);
  // session.updateEnablement();
  // for (final PointsetManagerListener listener : listeners) {
  // listener.pointsetManagerChanged(new DirMonitorEvent(this, pointset,
  // DirMonitorEvent.Kind.MEMBER_ADDED));
  // // pointsetAdded(this, pointset);
  // }
  // }
  // // @Implements
  // private void firePointsetRemoved(final Pointset pointset) {
  // session.updateEnablement();
  // if (pointsets.contains(pointset)) {
  // throw new RuntimeException("unregistered pointset removed");
  // }
  // // System.out.println("POINTSET MANAGER: firing pointset removed" +
  // // pointset);
  // for (final PointsetManagerListener listener : listeners) {
  // listener.pointsetManagerChanged(new DirMonitorEvent(this, pointset,
  // DirMonitorEvent.Kind.MEMBER_REMOVED));
  // // listener.pointsetRemoved(this, pointset);
  // }
  // }
  // @Implements
  private void firePointsetManagerChanged(PointsetManagerEvent ev) {
    session.updateEnablement();
    if (!pointsets.contains(ev.member)) {
      System.out.println("Warning: pointset is unregistered with manager.");
    }
    for (final PointsetManagerListener listener : listeners) {
      listener.pointsetManagerChanged(ev);
    }
  }

  // // @Implements
  // public void fireBasisSelectionChanged(final Basis[] bases) {
  // // System.out.println("POINTSET MANAGER: bases selection changed");
  // for (final PointsetManagerListener listener : listeners) {
  // listener.pointsetManagerChanged(new DirMonitorEvent(this,
  // Basis.class, DirMonitorEvent.Kind.MEMBERS_SELECTION));
  // }
  // }
  // @Implements
  // public void firePointsetSelectionChanged(final Pointset[] pointsets) {
  // System.out.println("POINTSET MANAGER: pointsets selection changed");
  // for (final PointsetManagerListener listener : listeners) {
  // listener.pointsetManagerChanged();
  // // listener.pointsetSelectionChanged(this, pointsets);
  // }
  // }
  // @Implements
  public void addPointsetManagerListener(final PointsetManagerListener listener) {
    // PointsetModifyDialog listens because it needs to display the pointsets
    // and their selection state
    // Spreadsheet listens because it needs to update the tables
    // TODO: what if duplicate listener?
    listeners.add(listener);
  }

  public void removePointsetManagerListener(
      final PointsetManagerListener listener) {
    listeners.remove(listener);
  }

  public void fireProgressChanged(Pointset pointset, String string, int min,
      int value, int max) {
    for (final PointsetManagerListener listener : listeners) {
      listener.progressChanged(pointset, string, min, value, max);
    }
  }

  public DefaultMutableTreeNode loadRoot() {
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
    System.out.println("PointsetManager.load(): root = " + root);
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

  public void storeRoot() {
    if (!Main.isPersisting) {
      return;
    }
    final DefaultMutableTreeNode root = pointsetTreeModel
        .getRoot();
    if (root == null) {
      throw new RuntimeException("root may not be null");
    }
    System.out.println("storing PointsetTreeNode.userObject=" + root);
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

  public int pointsetCount() {
    return pointsets.size();
  }

  public boolean registerPointset(final DataSource dataSource,
      final Pointset pointset) {
    if (pointset == null) {
      throw new RuntimeException("cannot add null pointset to manager");
    }
    if (pointsets.contains(pointset)) {
      throw new RuntimeException("duplicate pointset = " + pointset);
    }
    for (final Pointset p : pointsets) {
      while (0 == pointset.compareTo(p)) {
        final PointsetRenameDialog pointsetRenameDialog = session.dialogManager
            .getPointsetRenameDialog();
        pointsetRenameDialog.setPointset(pointset);
        pointsetRenameDialog.showDialog(); // modal block
        if (!pointsetRenameDialog.isOk()) {
          throw new RuntimeException("duplicate descriptor (same name): "
              + pointset);
        }
      }
    }
    final boolean b = pointsets.add(pointset);
    Collections.sort(pointsets);
    // Associate this pointset with its originating DataSource
    List<Pointset> pointsets2;
    pointsets2 = dataSourceToPointsets.get(dataSource);
    if (pointsets2 == null) {
      pointsets2 = new ArrayList<Pointset>();
      dataSourceToPointsets.put(dataSource, pointsets2);
    }
    pointsets2.add(pointset);
    Collections.sort(pointsets2);
    pointsetTreeModel.addPointset(pointset);
    // TODO: next line is a kludge to make sure tree has this node
    // viewlet.selectPath(pointsetTreeModel.addPointset(pointset));
    firePointsetManagerChanged(new PointsetManagerEvent(this, pointset,
        PointsetManagerEvent.Kind.MEMBER_ADDED));
    checkForSize();
    session.updateEnablement();
    // next line due to no listers design, does a rebuild when unhidden
    // session.dialogManager.getAxesLegendDialog().rebuildLegend();
    // session.dialogManager.hideCanvasDialogs();
    return b;
  }

  public boolean containsPointset(final Pointset pointset) {
    return this.pointsets.contains(pointset);
  }

  public boolean unregisterPointsetAt(final int i) {
    if (i < 0 || i >= pointsets.size()) {
      throw new RuntimeException("bad pointset index");
    }
    return unregisterPointset(pointsets.get(i));
  }

  public boolean unregisterPointset(final Pointset pointset) {
    if (!pointsets.contains(pointset)) {
      throw new RuntimeException("unregistered pointset cannot be removed");
    }
    final boolean b = this.pointsets.remove(pointset);
    if (b) {
      pointsetTreeModel.removePointset(pointset);
      firePointsetManagerChanged(new PointsetManagerEvent(this, pointset,
          PointsetManagerEvent.Kind.POINTSET_REMOVED));
      session.saved = false;
      session.plotManager.canvas.setStale(true);
      session.plotManager.canvas.refresh();
      session.updateEnablement();
      return b;
    }
    throw new RuntimeException("No such pointset: " + pointset);
  }

  public void createPointset(final DataSource dataSource) throws IOException,
      NumberFormatException, Tool1Exception {
    final File dataSourceFile = dataSource.getDataSourceFile();
    if (dataSourceFile == null || !dataSourceFile.canRead()) {
      throw new IOException("Cannot read file: " + dataSourceFile.getPath());
    }
    // Specify the Basis that this Pointset will use
    final BasisSpecifyDialog basisSpecifyDialog = session.dialogManager
        .getBasisSpecifyDialog();
    basisSpecifyDialog.showDialog(); // modal block
    if (!basisSpecifyDialog.isOk()) {
      session.statusPanel.log2("Pointset creation cancelled");
      return;
    }
    final Basis basis = basisSpecifyDialog.getSelectedBasis();
    // Specify the DataSourceFilter that will be applied for building this
    // Pointset
    final FilterCreateDialog filterCreateDialog = new FilterCreateDialog(
        session, dataSource, new DataSourceReader(session, dataSource), basis);
    filterCreateDialog.showDialog(); // modal block
    if (!filterCreateDialog.isOk()) {
      session.statusPanel.log2("Data source filter creation cancelled");
      return;
    }
    final DataSourceFilter filter = filterCreateDialog.getDataSourceFilter();
    // Finally create the pointset
    final Pointset pointset = new Pointset(session, basis);
    pointset.setName(null);
    setVisible(pointset, false);
    setLoading(pointset, true);
    registerPointset(dataSource, pointset);
    SwingWorker<String, Object> worker = new SwingWorker<String, Object>() {
      @Override
      public String doInBackground() {
        try {
          // System.out.println("OPENING DATA SOURCE FILE: "+dataSourceFile+"
          // canRead="+dataSourceFile.canRead());
          // FileInputStream fis = new FileInputStream(dataSourceFile);
          // Following will generate a new DataSource file which will trigger
          // a read and creation of a DataSource
          int objCount;
          if (filter == null) {
            DataSourceReader dataSourceReader = new DataSourceReader(session,
                dataSource);
            // dataSourceReader.processHeader();
            dataSourceReader.addProgressListener(PointsetManager.this);
            dataSourceReader.countObjs(pointset);
            objCount = dataSourceReader.getObjCount();
            pointset.initializeAcMap();
            firePointsetManagerChanged(new PointsetManagerEvent(this, pointset,
                PointsetManagerEvent.Kind.AC_MAP));
            pointset.initializePoints(objCount);
            dataSourceReader = new DataSourceReader(session, dataSource);
            dataSourceReader.addProgressListener(PointsetManager.this);
            dataSourceReader.setObjCount(objCount);
            dataSourceReader.readAll(pointset);
          }
          else {
            final int[] matchedLines;
            DataSourceReader dataSourceReader = new DataSourceReader(session,
                dataSource);
            dataSourceReader.addProgressListener(PointsetManager.this);
            matchedLines = dataSourceReader.countObjsAndMatchLines(filter,
                pointset);
            objCount = dataSourceReader.getObjCount();
            session.statusPanel.log2("Match count = " + matchedLines.length);
            pointset.initializeAcMap();
            firePointsetManagerChanged(new PointsetManagerEvent(this, pointset,
                PointsetManagerEvent.Kind.AC_MAP));
            pointset.initializePoints(matchedLines.length);
            // Only way to stream reset() - create new ds reader!
            dataSourceReader = new DataSourceReader(session, dataSource);
            dataSourceReader.addProgressListener(PointsetManager.this);
            dataSourceReader.setObjCount(objCount);
            dataSourceReader.fetchLines(matchedLines, pointset);
            session.statusPanel.log2(dataSourceFile.getName() + ": cols = "
                + dataSourceReader.getHeaderLineLength() + " datum count = "
                + matchedLines.length);
          }
          PointsetModifyDialog pointsetModifyDialog = session.dialogManager
              .getPointsetModifyDialog();
          pointsetModifyDialog.setModal(true); // must have name to proceed
          pointsetModifyDialog.showDialog();
          if (!pointsetModifyDialog.isOk()) {
            unregisterPointset(pointset);
          }
          else {
            // pointset.moveToSubjectSpace();
            // TODO: put progress bar, setLoading() takes too long
            setLoading(pointset, false);
            setVisible(pointset, true);
          }
        }
        catch (OutOfMemoryError ex) {
          session.errorSupport("While reading data:", ex, "TODO");
          return "failure";
        }
        catch (Throwable ex) {
          session.errorSupport("While reading data: ", ex, "TODO");
          return "failure";
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
    // TODO: use new SwingWorker_DELETEME
    // final SwingWorker_DELETEME worker = new SwingWorker_DELETEME() {
    // @Override
    // public Object construct() {
    // };
    // worker.start();
  }

  public void movePointsetsToSubjectSpace() {
    for (final Pointset pointset : pointsets) {
      pointset.moveToSubjectSpace();
    }
  }

  public void movePointsetsToUserSpace() {
    for (final Pointset pointset : pointsets) {
      pointset.moveToUserSpace();
    }
  }

  public Pointset[] getPointsets() {
    final int size = pointsets.size();
    final Pointset[] pointsetArray = new Pointset[size];
    for (int i = 0; i < size; i++) {
      pointsetArray[i] = pointsets.get(i);
    }
    return pointsetArray;
  }

  public void setSelectedPointsetVisible(final boolean visible) {
    Pointset pointset = viewlet.getSelectedPointset();
    setVisible(pointset, visible);
    firePointsetManagerChanged(new PointsetManagerEvent(this, pointset,
        PointsetManagerEvent.Kind.MEMBER_VISABILITY));
  }

  public void entityCheckBoxSelected(final PointsetManagerEntity entity,
      boolean visible) {
    if (entity instanceof Pointset) {
      setVisible((Pointset) entity, visible);
    }
    else if (entity instanceof Basis) {
      // Basis will take care of itself
    }
    firePointsetManagerChanged(new PointsetManagerEvent(this, entity,
        PointsetManagerEvent.Kind.MEMBER_VISABILITY));
  }

  public PointsetManagerViewlet getViewlet() {
    return viewlet;
  }

  public Pointset getSelectedPointset() {
    return viewlet.getSelectedPointset();
  }

  public PointsetTreeModel getTreeModel() {
    return pointsetTreeModel;
  }

  public void clearPointsets() {
    final List<Pointset> copy = new ArrayList<Pointset>(pointsets);
    for (final Pointset pointset : copy) {
      unregisterPointset(pointset);
    }
  }

  public void convertPointsetsTo3D() {
    // Usually called when the user changes the axes dimensionality.
    for (final Pointset pointset : pointsets) {
      if (pointset.getBasis().getDimensionality() <= 2) {
        throw new RuntimeException("can not convert to 3D");
      }
      // map z to first available column
      // TODO: ask user what the mapping should be
      AxesColumnHeadsMapping currentAcMap = pointset.getCurrentAcMap();
      if (AxesColumnHeadsMapping.UNMAPPED == currentAcMap
          .getColumnHeadIndexForAxis(Axes.Z_AXIS)) {
        for (ColumnHead columnHead : currentAcMap.getColumnHeads()) {
          if (AxesColumnHeadsMapping.UNMAPPED == currentAcMap
              .getAxisForColumnHead(columnHead)) {
            currentAcMap.addMapping(Axes.Z_AXIS, columnHead);
            pointset.moveColumnHeadToSubjectSpace(columnHead);
            break;
          }
        }
      }
    }
  }

  public void convertPointsetsTo2D() {
    // Called when the user (or monitor) changes the axes dimensionality.
    for (final Pointset pointset : pointsets) {
      AxesColumnHeadsMapping currentAcMap = pointset.getCurrentAcMap();
      if (AxesColumnHeadsMapping.UNMAPPED != currentAcMap
          .getColumnHeadIndexForAxis(Axes.Z_AXIS)) {
        pointset.moveColumnHeadToUserSpace(currentAcMap
            .getColumnHeadForAxis(Axes.Z_AXIS));
        currentAcMap.removeMappingFromAxis(Axes.Z_AXIS);
      }
    }
  }

  public void calculateSubjectExtents() {
    for (final Pointset pointset : pointsets) {
      pointset.calculateSubjectExtents();
    }
  }

  public void shutdown() {
    // TODO dirty
    session.statusPanel.log1("Persisting pointsets for next session.");
    storeRoot();
  }

  // @Implements DescriptorManagerListener
  public void basisSelectionChanged(DescriptorManager manager,
      Basis[] selectedBases) {
    this.firePointsetManagerChanged(new PointsetManagerEvent(this,
        selectedBases, PointsetManagerEvent.Kind.MEMBERS_SELECTION));
  }

  public void checkAccuracy() {
    // TODO:
    // managers have no duplicate user objects
    // all manager user objects have exactly one node of the correct type
    // all nodes have the right user object
    // tree connectivity is same as the user objects connectivity
  }

  public Pointset getPointsetAt(int i) {
    return pointsets.get(i);
  }

  /**
   * This sets the number of visible numbers for the sets as a whole. It is the
   * responsibility of the individual data sets to show only nAdaptVisible or
   * less.
   */
  public void setSubsettingVisible() {
    int drawTime, lo = 0, hi = maxNPoints; // brackets acceptable candidates
    int j;
    final int size = pointsetCount();
    // nAdaptVisible ranges from 0 to maxNPoints
    // We want to set nAdaptVisible so that it as close to
    // acceptableTime as possible.
    for (;;) { // maximum iterations
      // predict time it will take to draw based on past average
      drawTime = 0;
      for (j = 0; j < size; j++) {
        final Pointset pointset = getPointsetAt(j);
        if (nAdaptVisible >= pointset.getPointCount()) {
          drawTime += pointset.getAvgDrawTimeAllPoints();
        }
        else {
          drawTime += pointset.getAvgDrawTimeAllPoints() * nAdaptVisible
              / pointset.getPointCount();
        }
      }
      // System.out.println("PREDICTED DRAWTIME: "+drawTime);
      // bracket search for a better nAdaptVisible
      if (drawTime > session.plotManager.canvas.acceptableTime) { // pred too
        // long
        hi = nAdaptVisible; // don't bother looking higher
        // System.out.println("BRACKET HI: "+hi);
        if (nAdaptVisible > lo) { // we have room to go lower
          nAdaptVisible = lo + (nAdaptVisible - lo) / 2;
        }
        else {
          if (nAdaptVisible != lo) {
            throw new RuntimeException();
          }
          break; // can't go any lower
        }
      }
      else if (drawTime < session.plotManager.canvas.acceptableTime) {
        lo = nAdaptVisible; // don't bother looking lower
        // System.out.println("BRACKET LO: "+lo);
        if (nAdaptVisible < hi) { // maybe higher?
          nAdaptVisible += (hi - nAdaptVisible) / 2;
        }
        else {
          if (nAdaptVisible != hi) {
            throw new RuntimeException();
          }
          break; // can't go any higher
        }
      }
      else {
        break; // got it
      }
      if (lo + 1 >= hi) {
        break; // best answer
      }
    }
    // System.out.println("FINAL N ADAPT VISIBLE: " + nAdaptVisible);
    // if (nAdaptVisible > 1.05*prevAdaptVisible // TODO: subject to drift
    // || nAdaptVisible < 0.95*prevAdaptVisible) {
    for (j = 0; j < size; j++) {
      final Pointset pointset = getPointsetAt(j);
      if (!pointset.isAdapting()) {
        SwingWorker<String, Object> worker = new SwingWorker<String, Object>() {
          @Override
          public String doInBackground() {
            pointset.setAdapting(true);
            pointset.adapt();
            pointset.setAdapting(false);
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

  public void checkForSize() {
    // If there are too many data numbers, turn on subsetting
    // There must be at least one canvas refresh first for this to be accurate.
    int totNPoints = 0;
    for (int i = 0; i < pointsetCount(); i++) {
      final Pointset pointset = getPointsetAt(i);
      totNPoints += pointset.getPointCount();
    }
    if (totNPoints > session.plotManager.canvas.turnOnSubsettingCutoff) {
      session.plotManager.canvas.enableSubsetting(true);
      session.plotManager.canvas.fastCompose = true;
    }
    else {
      session.plotManager.canvas.enableSubsetting(false);
      session.plotManager.canvas.fastCompose = false;
    }
  }

  public void harden(Pointset pointset, File file) throws IOException {
    if (!SwingUtilities.isEventDispatchThread()) {
      throw new RuntimeException(
          "exporting on non EventDispatch thread may give erratic visual results");
    }
    // pointset.moveToUserSpace();
    try {
      final CSVWriter csvWriter = new CSVWriter(new FileWriter(file),
          DataSourceManager.csvDelimiter, CSVWriter.NO_QUOTE_CHARACTER);
      String[] line;
      line = new String[2];
      // Row 1: pointset name
      line[0] = "name";
      line[1] = pointset.getName();
      csvWriter.writeNext(line);
      // Row 2: shape of numbers
      line[0] = "shape";
      line[1] = pointset.getShape().name();
      csvWriter.writeNext(line);
      // Row 3: color of numbers
      line = new String[4];
      line[0] = "color";
      line[1] = Integer.toString(pointset.getColor().getRed());
      line[2] = Integer.toString(pointset.getColor().getGreen());
      line[3] = Integer.toString(pointset.getColor().getBlue());
      csvWriter.writeNext(line);
      // Row 4: size of numbers
      line = new String[2];
      line[0] = "size";
      line[1] = Float.toString(pointset.getSize());
      csvWriter.writeNext(line);
      line = new String[2];
      // Row 5: number of numbers, nPoints
      line[0] = "nPoints";
      line[1] = Integer.toString(pointset.getPointCount());
      csvWriter.writeNext(line);
      // Row 5b: basis name
      line[0] = "basis";
      line[1] = pointset.getBasis().getName();
      csvWriter.writeNext(line);
      // Row 6: dimensionality
      line[0] = "dimensionality";
      line[1] = Integer.toString(pointset.getDimensionality());
      csvWriter.writeNext(line);
      // Row 7: axisToCol mapping
      line = new String[4];
      line[0] = "axisToCol";
      line[1] = Integer.toString(pointset.getCurrentAcMap()
          .getColumnHeadIndexForAxis(Axes.X_AXIS));// axisToCol[0]);
      line[2] = Integer.toString(pointset.getCurrentAcMap()
          .getColumnHeadIndexForAxis(Axes.Y_AXIS));// //axisToCol[1]);
      line[3] = Integer.toString(pointset.getCurrentAcMap()
          .getColumnHeadIndexForAxis(Axes.Z_AXIS));// axisToCol[2]);
      csvWriter.writeNext(line);
      // Row 8: colToAxis mapping
      line = new String[pointset.getDimensionality() + 1];
      line[0] = "colToAxis";
      for (int d = 0; d < pointset.getDimensionality(); d++) {
        line[d + 1] = Integer.toString(pointset.getCurrentAcMap()
            .getAxisForColumnHeadIndex(d));// colToAxis[d]);
      }
      csvWriter.writeNext(line);
      // Row 10: descriptor
      line[0] = "columnHead names";
      for (int d = 0; d < pointset.getDimensionality(); d++) {
        line[d + 1] = pointset.getCurrentAcMap().getColumnHeadAt(d).getName();
      }
      csvWriter.writeNext(line);
      // Row 9: columnHead Descriptions
      line = new String[pointset.getDimensionality() + 1];
      line[0] = "columnHead descriptions";
      for (int d = 0; d < pointset.getDimensionality(); d++) {
        line[d + 1] = pointset.getCurrentAcMap().getColumnHeadAt(d)
            .getDescription();
      }
      csvWriter.writeNext(line);
      // Row 11 - end: point values
      line = new String[pointset.getDimensionality() + 2];
      for (int i = 0; i < pointset.getPointCount(); i++) {
        line[0] = new String(pointset.getObjNameAt(i).bytes);
        int d = 0;
        for (; d < pointset.getDimensionality(); d++) {
          line[d + 1] =
          // Float.toString(pointset.getPointsInSubjectSpace()[i][d]);
          pointset.getUserValue(i, d).toString();
        }
        line[d + 1] = new String(pointset.getObjDescriptionAt(i).bytes);
        csvWriter.writeNext(line);
      }
      csvWriter.close();
    }
    finally {
      // pointset.moveToSubjectSpace();
    }
  }

  public Pointset soften(Pointset pointset, InputStreamReader isr,
      String fileName) throws IOException {
    final CSVReader csvReader = new CSVReader(isr,
        DataSourceManager.csvDelimiter);
    String[] line = new String[2];
    // Row 1: name
    line = csvReader.readNext();
    // if (line[0].("name")) {throw new IOException("bad format");}
    pointset.setName(line[1]);
    // Row 2: shape
    line = csvReader.readNext();
    // line[0]="shape") {throw new IOException("bad format");}
    pointset.setPointShape(createShape(line[1]));
    // Row 3: color
    line = csvReader.readNext();
    // line[0]="color") {throw new IOException("bad format");}
    pointset.setColor(new Color(Integer.parseInt(line[1]), Integer
        .parseInt(line[2]), Integer.parseInt(line[3])));
    // line[1] = color.toString();
    // Row 4: size
    line = csvReader.readNext();
    // line[0] = "size") {throw new IOException("bad format");}
    pointset.setPointSize(Float.parseFloat(line[1]));
    // Row 5: nPoints
    line = csvReader.readNext();
    // if (line[0].equals("nPoints")) {throw new IOException("bad format");}
    pointset.setPointCount(Integer.parseInt(line[1]));
    // Row 5b: basis
    line = csvReader.readNext();
    // line[0]="basis") {throw new IOException("bad format");}
    Basis basis = session.descriptorManager.getBasisByName(line[1]);
    if (basis == null) {
      pointset.setMissingBasis(line[1]);
    }
    else {
      pointset.setBasis(basis);
    }
    // Row 6: dimensionality
    line = csvReader.readNext();
    assert (session.plotManager.getDim() == Integer.parseInt(line[1]));
    pointset.setDimensionality(Integer.parseInt(line[1]));
    AxesColumnHeadsMapping acMap = new AxesColumnHeadsMapping(session);
    pointset.setAcMap(acMap);
    acMap.initialize(pointset.getDimensionality());
    pointset.initializeAcMap();
    pointset.initializePoints(pointset.getPointCount());
    // Row 7: axisToCol mapping
    line = new String[pointset.getDimensionality() + 1];
    line = csvReader.readNext();
    pointset.getCurrentAcMap().addMapping(Axes.X_AXIS,
        Integer.parseInt(line[1]));
    pointset.getCurrentAcMap().addMapping(Axes.Y_AXIS,
        Integer.parseInt(line[2]));
    // TODO: what if 2D? Next line will fail
    pointset.getCurrentAcMap().addMapping(Axes.Z_AXIS,
        Integer.parseInt(line[3]));
    // Row 8: colToAxis mapping
    line = csvReader.readNext();
    for (int d = 0; d < pointset.getDimensionality(); d++) {
      // TODO: Since the mapping goes both ways, just check its validity
      // int axis=pointset.getCurrentAcMap().getAxisForColumnHeadIndex(d);
      // int axis2=Integer.parseInt(line[d + 1]);
      // if (axis != Aaxis != axis2) {
      // throw new RuntimeException(
      // "inconsistent axes-columns mapping ("+axis+"!="+axis2+") in file: " +
      // fileName);
      // }
    }
    // Row 10: columnHead names
    line = csvReader.readNext();
    // line[0]="columnNames") {throw new IOException("bad format");}
    for (int d = 0; d < pointset.getDimensionality(); d++) {
      Descriptor descriptor = session.descriptorManager
          .getDescriptorByName(line[d + 1]);
      if (descriptor == null) {
        // throw new IOException("Unregistered descriptor; "+ descriptor);
        ColumnHead columnHead = new ColumnHead(session);
        columnHead.setName(line[d + 1]);
        acMap.setColumnHeadAt(columnHead, d);
      }
      else {
        acMap.setColumnHeadAt(descriptor, d);
      }
    }
    // Row 9: columnHead descriptions
    line = csvReader.readNext();
    // line[0] = "columnDescriptions") {throw new IOException("bad format");}
    for (int d = 0; d < pointset.getDimensionality(); d++) {
      acMap.getColumnHeadAt(d).setDescription(line[d + 1]);
    }
    // Row 11 - end: point values
    for (int i = 0; i < pointset.getPointCount(); i++) {
      line = csvReader.readNext();
      int d = 0;
      for (; d < pointset.getDimensionality(); d++) {
        pointset.setUserValue(line[d + 1], i, d);
      }
      pointset.setObjNameAt(i, new ByteSequence(line[0].getBytes()));
      pointset.setObjDescriptionAt(i, new ByteSequence(line[d + 1].getBytes()));
      if (i % ProgressBar.progressIncr == 0) {
        fireProgressChanged(pointset, "synchronizing", 0, i, pointset
            .getPointCount());
      }
    }
    fireProgressChanged(pointset, "done", 0, pointset.getPointCount(), pointset
        .getPointCount());
    csvReader.close();
    // pointset.moveToSubjectSpace();
    return pointset;
  }

  private static Shape createShape(String name) {
    if (name.equals("PIXEL")) {
      return Shape.PIXEL;
    }
    else if (name.equals("DOT")) {
      return Shape.DOT;
    }
    else if (name.equals("CIRCLE")) {
      return Shape.CIRCLE;
    }
    else if (name.equals("BLOCK")) {
      return Shape.BLOCK;
    }
    else if (name.equals("SQUARE")) {
      return Shape.SQUARE;
    }
    else if (name.equals("XSHAPE")) {
      return Shape.XSHAPE;
    }
    else if (name.equals("PLUS")) {
      return Shape.PLUS;
    }
    else {
      throw new RuntimeException("unrecognized shape string");
    }
  }

  public void synchronize() throws IOException {
    // Make sure all hard bases have a soft mate.
    final File[] files = homeDir
        .listFiles(FileFilters.pointsetTxtFilenameFilter);
    for (final File file : files) {
      String name = FileFilters.entityNameFromFile(file,
          FileFilters.pointsetTxtExt);
      if (null == getPointsetByName(name)) { // already app-persisted by db4o?
        Pointset pointset = new Pointset(session, null);
        pointset.setName("?");
        setVisible(pointset, false);
        setLoading(pointset, true);
        registerPointset(null, pointset);
        soften(pointset, new FileReader(file), name);
        setLoading(pointset, false);
        setVisible(pointset, true);
        // Re-add pointset so that it gets the right parent node,
        // basis may have been changed
        pointsetTreeModel.removePointset(pointset);
        pointsetTreeModel.addPointset(pointset);
        // viewlet.selectPath(pointsetTreeModel.addPointset(pointset));
        if (!pointset.getName().equals(name)) {
          throw new IOException("File improperly named: " + file.getPath());
        }
        firePointsetManagerChanged(new PointsetManagerEvent(this, pointset,
            PointsetManagerEvent.Kind.MANAGER_CHANGED));
        selectPointsets(new Pointset[] { pointset });
      }
    }
  }

  public Pointset getPointsetByName(String name) {
    String[] pointsetNames = getPointsetNames();
    int i = Arrays.binarySearch(pointsetNames, name);
    if (i < 0) {
      return null;
    }
    return pointsets.get(i);
  }

  public String[] getPointsetNames() {
    String[] pointsetNames = new String[pointsets.size()];
    for (int i = 0; i < pointsets.size(); i++) {
      pointsetNames[i] = pointsets.get(i).getName();
    }
    return pointsetNames;
  }

  public void selectPointsets(Pointset[] pointsets) {
    firePointsetManagerChanged(new PointsetManagerEvent(this, pointsets,
        PointsetManagerEvent.Kind.MEMBERS_SELECTION));
  }

  public void selectPointsetAt(int i) {
    selectPointsets(new Pointset[] { pointsets.get(i) });
  }

  public void selectBases(Basis[] bases) {
    firePointsetManagerChanged(new PointsetManagerEvent(this, bases,
        PointsetManagerEvent.Kind.MEMBERS_SELECTION));
  }

  public int getPointsetIndex(Pointset pointset) {
    for (int i = 0; i < pointsets.size(); i++) {
      if (pointsets.get(i) == pointset) {
        return i;
      }
    }
    return -1;
  }

  public void assignAxisToSelectedColumnHeadForPointset(int axis,
      ColumnHead columnHead, Pointset pointset) {
    AxesColumnHeadsMapping currentAcMap = pointset.getCurrentAcMap();
    int oldCol = currentAcMap.getColumnHeadIndexForAxis(axis);
    if (oldCol != AxesColumnHeadsMapping.UNMAPPED) {
      pointset.moveColumnHeadToUserSpace(columnHead);
      currentAcMap.removeMappingFromColumnHead(oldCol);
    }
    currentAcMap.addMapping(axis, columnHead);
    pointset.moveColumnHeadToSubjectSpace(columnHead);
    calculateSubjectExtents();
    firePointsetManagerChanged(new PointsetManagerEvent(this, pointset,
        PointsetManagerEvent.Kind.AC_MAP));
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

  public void idSelectionChanged(Object source, int[][] selected) {
    // nop
  }

  public void idSelectionChanged(Object source, Map<Pointset, int[]> selected) {
    // TODO Auto-generated method stub
  }

  public static void configureDb4o() {
    Db4oPersistenceService.configureDb4oForClass(
        DefaultMutableTreeNodeHolder.class, Pointset.class,
        AxesColumnHeadsMapping.class, BasisTreeNode.class,
        PointsetTreeNode.class, ColumnHead.class);
  }

  // @Implements PointsetManagerListener
  public void pointsetOrderChanged(Object source) {
    Pointset[] pointsets = session.plotManager.getPointsets();
    if (pointsets.length > 0) {
      selectPointsets(new Pointset[] { pointsets[pointsets.length - 1] });
      // firePointsetSelectionChanged(new Pointset[] {
      // pointsets[pointsets.length - 1] });
      // viewlet.selectPointset(pointsets[pointsets.length - 1]);
    }
  }

  // @Implements DataSourceManagerListener
  public void dataSourceAdded(final DataSourceManager manager,
      final DataSource dataSource) {
    // System.out.println("POINTSET MANAGER: DataSource added " + dataSource);
    // Do nothing. The user needs to manually create a Pointset.
  }

  // @Implements DataSourceManagerListener
  public void dataSourceChanged(final DataSourceManager manager,
      final DataSource dataSource) {
    System.out.println("POINTSET MANAGER: DataSource changed " + dataSource);
    new RuntimeException("TODO").printStackTrace();
    // if (dataSourceToPointsets.size() != 0) {
    // for (final Pointset pointset : dataSourceToPointsets.get(dataSource)) {
    // try {
    // throw new RuntimeException("TODO");
    // // refreshPointset(dataSource, pointset);
    // }
    // catch (final Exception e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    // }
  }

  // @Implements DataSourceManagerListener
  public void dataSourceRemoved(final DataSourceManager manager,
      final DataSource dataSource) {
    System.out.println("POINTSET MANAGER: DataSource removed " + dataSource);
    throw new RuntimeException("TODO");
  }

  // 
  @Override
	public void progressChanged(Object subject, String string, int min,
      int value, int max) {
    if (subject instanceof Pointset) {
      fireProgressChanged((Pointset) subject, string, min, value, max);
    }
  }

  public void hiddenChanged(Pointset pointset) {
    pointset.hiddenChanged();
    firePointsetManagerChanged(new PointsetManagerEvent(this, pointset,
        PointsetManagerEvent.Kind.POINT_VIZ));
  }

  public void pointsetModified(Pointset pointset) {
    session.pointsetManager
        .firePointsetManagerChanged(new PointsetManagerEvent(this, pointset,
            PointsetManagerEvent.Kind.MANAGER_CHANGED));
  }

  public void setWaypoints(List<Waypoint> waypoints, Pointset pointset) {
    DefaultMutableTreeNode[] dmtn = pointsetTreeModel.getPathToUserObject(pointset);
    ((PointsetTreeNode) dmtn[dmtn.length - 1]).setWaypoints(waypoints);
  }

  public void filterChanged(Object object, ColumnHead columnHead,
      int limitLowPercentage, int limitHighPercentage, boolean showInsideLimit) {
    if (object instanceof Pointset) {
      ((Pointset) object).filterChanged(columnHead, limitLowPercentage,
          limitHighPercentage, showInsideLimit);
      firePointsetManagerChanged(new PointsetManagerEvent(this, object,
          PointsetManagerEvent.Kind.POINT_VIZ));
    }
  }

  @Override
	public void descriptorManagerChanged(DescriptorManagerEvent ev) {
    if (ev.member instanceof Descriptor) {
      switch (ev.kind) {
      case CHANGED: // most vague change possible
        break;
      case MEMBER_CHANGED: // use more specific kind if available
        break;
      case MEMBER_LOADED:
        // TODO
        break;
      case MEMBER_ADDED:
        break;
      case MEMBER_REMOVED:
        // listen
        break;
      case MEMBER_REPLACED:
        break;
      case MEMBERS_SELECTION: // user (de)selected
        break;
      }
    }
    else if (ev.member instanceof Basis) {
      Basis basis = (Basis) ev.member;
      switch (ev.kind) {
      case CHANGED: // most vague change possible
        break;
      case MEMBER_CHANGED: // use more specific kind if available
        firePointsetManagerChanged(new PointsetManagerEvent(this,
            basis , PointsetManagerEvent.Kind.MANAGER_CHANGED));
        break;
      case MEMBER_LOADED:
        // TODO
        break;
      case MEMBER_ADDED:
        System.out.println("POINTSETMANAGER: basis added");
        pointsetTreeModel.addBasis(basis);
        // TODO: breaks design, viewlet should listen
        firePointsetManagerChanged(new PointsetManagerEvent(this,
            new Basis[] { basis }, PointsetManagerEvent.Kind.MEMBERS_SELECTION));
        break;
      case MEMBER_REMOVED:
        // Remove this basis from each pointset if it uses it
        for (Pointset pointset : pointsets) {
          if (pointset.getBasis() == basis) {
            pointset.clearBasis();
            firePointsetManagerChanged(new PointsetManagerEvent(this, basis,
                PointsetManagerEvent.Kind.MEMBER_ADDED)); // TODO: do we mean MEMBER_REMOVED?
          }
        }
        pointsetTreeModel.removeBasis(basis);
        break;
      case MEMBER_REPLACED:
        pointsetTreeModel.removeBasis(basis);
        break;
      case MEMBERS_SELECTION: // user (de)selected
        break;
      }
    }
  }

  @Override
	public void plotManagerChanged(PlotManagerEvent ev) {
    switch (ev.kind) {
    case CHANGED:
      break;
    case BASISMODE_CHANGED:
      pointsetTreeModel.repaintBasisNode((Basis) ev.member);
      for (Pointset pointset : pointsets) {
        pointset.setBasisMode((Basis) ev.member);
        firePointsetManagerChanged(new PointsetManagerEvent(this, pointset,
            PointsetManagerEvent.Kind.MEMBER_VISABILITY));
      }
      session.plotManager.refreshCanvas();
      break;
    case TEXT_SELECTION_CHANGED:
      // nop
      break;
    case POINTSET_ORDER_CHANGED:
      break;
    case TEXT_PROPERTIES_CHANGED:
      // nop
      break;
    case PREVIEW_CHANGED:
      break;
    case DIMENSIONALITY:
      if ((Integer) ev.member == 2) {
        convertPointsetsTo2D();
      }
      else if ((Integer) ev.member == 3) {
        convertPointsetsTo3D();
      }
      else {
        throw new RuntimeException("bad dim; " + ev.member);
      }
      break;
    }
  }

  @Override
	public void dataSourceManagerChanged(DataSourceManagerEvent ev) {
    switch (ev.kind) {
    case CHANGED:
      break;
    case MEMBER_ADDED:
      break;
    case MEMBER_CHANGED:
      break;
    case MEMBER_REMOVED:
      break;
    }
  }

  public void setLoading(Pointset pointset, boolean loading) {
    if (loading) {
      pointset.setLoading(true);
    }
    else {
      pointset.calcStats();
      //ColumnHead[] columnHeads = pointset.getAcMap().getColumnHeads();
      pointset.getBasis().calcStats();
      pointset.setLoading(false);
      firePointsetManagerChanged(new PointsetManagerEvent(this, pointset,
          PointsetManagerEvent.Kind.MEMBER_LOADED));
    }
  }

  public void setVisible(Pointset pointset, boolean visible) {
    pointset.setVisible(visible);
    // if (!visible) {
    // pointset.calcStats();
    // pointset.getBasis().calcStats();
    // }
    firePointsetManagerChanged(new PointsetManagerEvent(this, pointset,
        PointsetManagerEvent.Kind.MEMBER_VISABILITY));
  }

  public int getPointsetCount() {
    return pointsets.size();
  }

  // public void textSelectionChanged(PlotManager plotManager) {
  // // nop
  // }
  //
  // public void textPropertiesChanged(PlotManager manager, Text text) {
  // // nop
  // }
  //
  // public void basisModeChanged(final PlotManager plotManager,
  // final Basis basisMode) {
  // pointsetTreeModel.repaintBasisNode(basisMode);
  // for (Pointset pointset : pointsets) {
  // pointset.setBasisMode(basisMode);
  // firePointsetManagerChanged(new SpreadsheetEvent(this, pointset,
  // SpreadsheetEvent.Kind.POINTSET_CHANGED));
  // }
  // session.plotManager.refreshCanvas();
  // }
  //
  // public void dimensionalityChanged(final PlotManager plotManager, final int
  // dim) {
  // if (dim == 2) {
  // convertPointsetsTo2D();
  // }
  // else if (dim == 3) {
  // convertPointsetsTo3D();
  // }
  // else {
  // throw new RuntimeException("bad dim; " + dim);
  // }
  // }
  // public void leadSelectionChanged(Pointset pointset, int rowIndex) {
  // pointset.setLeadSelection(rowIndex);
  // }
  //
  // // @Implements SpreadsheetListener
  // public void pointSelectionChanged(Pointset pointset, int[] selectedPoints)
  // {
  // pointset.setSelected(selectedPoints);
  // firePointsetManagerChanged(new PointsetManagerEvent(this, pointset,
  // PointsetManagerEvent.Kind.MEMBER_VISABILITY));
  // }
  //
  // // @Implements SpreadsheetListener
  // public void pointsetSelectionChanged(Pointset pointset) {
  // selectPointsets(new Pointset[] { pointset });
  // // firePointsetSelectionChanged(new Pointset[] { pointset });//
  // // viewlet.selectPointset(pointset);
  // }
  //
  @Override
	public void spreadsheetChanged(SpreadsheetEvent ev) {
    PointsetTable pointsetTable;
    Pointset pointset = null;
    switch (ev.kind) {
    case MEMBERS_SELECTION:
      pointsetTable = (PointsetTable) ev.member;
      if (pointsetTable == null) {
        selectPointsets(new Pointset[] {});
      }
      else {
        pointset = pointsetTable.getPointset();
        selectPointsets(new Pointset[] { pointset });
      }
      break;
    case LEAD_SELECTION:
      pointsetTable = (PointsetTable) ev.member;
      pointset = pointsetTable.getPointset();
      pointset.setLeadSelection(pointsetTable.getLeadSelectionPoint());
      break;
    case POINT_SELECTION:
      pointsetTable = (PointsetTable) ev.member;
      pointset = pointsetTable.getPointset();
      pointset.setSelected(pointsetTable.getSelectedPoints());
      firePointsetManagerChanged(new PointsetManagerEvent(this, pointset,
          PointsetManagerEvent.Kind.MEMBER_VISABILITY));
      break;
    case MEMBER_LOADED:
      // nop
      break;
    case MANAGER_CHANGED:
    case MEMBER_CHANGED:
    case MEMBER_ADDED:
    case MEMBER_REMOVED:
    case MEMBER_VISABILITY:
      throw new RuntimeException("TODO");
    }
  }
}
