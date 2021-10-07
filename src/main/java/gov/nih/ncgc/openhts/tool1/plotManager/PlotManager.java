package gov.nih.ncgc.openhts.tool1.plotManager;

import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import gov.nih.ncgc.openhts.tool1.Main;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Basis;
import gov.nih.ncgc.openhts.tool1.descriptorManager.BasisSelectorDialog;
import gov.nih.ncgc.openhts.tool1.persistence.Db4oPersistenceService;
import gov.nih.ncgc.openhts.tool1.persistence.PersistenceContext;
import gov.nih.ncgc.openhts.tool1.persistence.PersistenceRoot;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetManagerEvent;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetManagerListener;

public final class PlotManager extends JPanel implements ComponentListener,
    PointsetManagerListener {
  public final Session session;
  private final static boolean debug = false;
  public boolean saved;
  public Subject subject;
  public Canvas canvas;
  public View view;
  private List<Text> textList;
  public Text selectedText;
  // If Basis is null, then we are in NoBasis mode
  public Basis basis = null;
  public Basis prevBasis = null;
  private Integer dimensionality;
  private final List<PlotManagerListener> listeners = new ArrayList<PlotManagerListener>();
  private ObjectsHolder objectsHolder;
  private List<Object> objects;
  private final List<Pointset> pointsets = new LinkedList<Pointset>();
  
  public PlotManager(final Session session) {
    this.session = session;
    session.plotManager = this; // TODO, get rid of this line
    setLayout(new BorderLayout(0, 0));
    if (getObjectsHolder() == null) {
      objects = new ArrayList<Object>();
      dimensionality = new Integer(3);
      view = new View(session);
      view.initialize();
      subject = new Subject(session);
      textList = new ArrayList<Text>();
      subject.initialize();
      storeObjects();
    }
    else {
      objects = getObjectsHolder().getObjects();
      loadObjects();
      dimensionality = (Integer) objects.get(0);
      view = (View) objects.get(1);
      view.setSession(session);
      view.initializeTransient();
      textList = (List<Text>) objects.get(2);
      subject = (Subject) objects.get(3);
      subject.setSession(session);
      subject.initializeTransient();
      for (final Text text : textList) {
        text.setSession(session);
        text.initializeTransient();
      }
      basis = (Basis) objects.get(4);
      session.mainMenuBar.checkItem.setSelected(basis != null);
      prevBasis = (Basis) objects.get(5);
    }
  }

  public void initialize() {
    initializeTransient();
  }

  public void initializeTransient() {
    canvas = new Canvas(session);
    canvas.initialize();
    add(canvas, BorderLayout.CENTER);
    add(canvas.getToolbar(), BorderLayout.WEST);
    canvas.setStale(false);
    canvas.internalResize();
    canvas.requestFocus();
    saved = false;
    canvas.setStale(true);
    canvas.refresh();
    Session.addFocusBorder(canvas, this);
  }

  public boolean clearPlot() {
    if (!subject.isEmpty()) {
      int i = JOptionPane.showConfirmDialog(this,
          "Are you sure you want to clear entire plot?", "Clear plot confirm",
          JOptionPane.YES_NO_OPTION);
      if (i == 0) { // yes
        try {
          // session.setWaitCursor(true);
          // session.spreadsheet.disposeTableFrame();
          for (i = 0; i < session.pointsetManager.pointsetCount(); i++) {
            final Pointset pointset = session.pointsetManager.getPointsetAt(i);
            session.dialogManager.hideDatasetDialogs();
            pointset.clearReferences();
          }
          session.pointsetManager.clearPointsets();
          for (i = 0; i < textList.size(); i++) {
            final Text text = textList.get(i);
            session.dialogManager.hideTextDialogs();
            text.clearReferences();
          }
          textList.clear();
          if (subject.axes != null) {
            session.dialogManager.hideAxesDialogs();
            subject.axes.clearReferences();
            subject.axes = null;
          }
          subject.initialize();
          session.dialogManager.hideViewDialogs();
          view.initialize();
          canvas.blankData = false;
          session.dialogManager.hideCanvasDialogs();
          canvas.setSelectionMode(Canvas.SELECT_SELECTION_MODE);
          canvas.setDragMode(Canvas.DragModeKind.NO_DRAG_MODE);
          view.setInertia(false);
          session.plotManager.refreshCanvas();
          System.runFinalization();
          System.gc();
        }
        finally {
          session.setWaitCursor(false);
        }
      }
      else { // cancel
        return false;
      }
    }
    return true;
  }

  public int getDim() {
    return dimensionality;
  }

  public void setSaved(final boolean status) {
    saved = status;
  }

  private static final long serialVersionUID = 1L;

  // @Implements ComponentListener
  @Override
	public void componentHidden(final ComponentEvent ev) {
    System.out.println("Plotter parent POINT_VIZ.");
    // TODO turn off inertia if it is on
  }

  // @Implements ComponentListener
  @Override
	public void componentShown(final ComponentEvent ev) {
    System.out.println("Plotter parent SHOWN.");
    canvas.refresh();
  }

  // @Implements ComponentListener
  @Override
	public void componentMoved(final ComponentEvent e) {
    System.out.println("Plotter parent MOVED.");
    // nop
  }

  // @Implements ComponentListener
  @Override
	public void componentResized(final ComponentEvent ev) {
//    System.out.println("Plotter parent RESIZED.");
    canvas.externalResize();
  }

  public void setBasisMode(Basis basisMode) {
    prevBasis = basis;
    basis = basisMode;
    session.mainMenuBar.checkItem.setSelected(basis != null);
    firePlotManagerChanged(new PlotManagerEvent(this,basis,PlotManagerEvent.Kind.BASISMODE_CHANGED));
  }

  public Basis getBasisMode() {
    return basis;
  }

  public void toggleBasisMode() {
    if (basis == null) {
      if (prevBasis == null) {
        // Nothing to toggle to, so ask user which Basis to toggle to.
        if (session.descriptorManager.getBases().length <= 0) {
          session
              .message("Unable to switch to Basis mode, no basis registered yet.");
          return;
        }
        final BasisSelectorDialog bsd = session.dialogManager
            .getBasisSelectorDialog();
        bsd.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bsd.showDialog(); // modal block
        if (!bsd.isOk()) {
          return;
        }
        prevBasis = bsd.getSelectedBases()[0];
      }
      basis = prevBasis;
    }
    else {
      prevBasis = basis;
      basis = null;
    }
    firePlotManagerChanged(new PlotManagerEvent(this,basis,PlotManagerEvent.Kind.BASISMODE_CHANGED));
  }

  public void addPlotManagerListener(
      final PlotManagerListener plotManagerListener) {
    // PointsetManager listens because it needs to convert the pointsets to
    // proper dimensionality
    // Axes listen because dimensionality may change and they need to redraw
    // DescriptorManager listens because ...
    listeners.add(plotManagerListener);
  }

  public void removelotterListener(final PlotManagerListener plotManagerListener) {
    listeners.remove(plotManagerListener);
  }

  private void firePlotManagerChanged(PlotManagerEvent ev) {
    for (final PlotManagerListener listener : listeners) {
      listener.plotManagerChanged(ev);
    }
  }
  
//  private void fireBasisModeChange() {
//    System.out.println("PLOT MANAGER: basis mode changed");
//    for (final HeatmapListener listener : listeners) {
//      listener.basisModeChanged(this, basis);
//    }
//    refresh();
//    // canvas.setStale(true);
//    // canvas.refresh();
//  }
//
//  // @Implements HeatmapListener
//  private void fireTextSelectionChanged() {
//    for (final HeatmapListener listener : listeners) {
//      listener.textSelectionChanged(this);
//    }
//    // TODO: outline the selected text on the canvas HERE
//  }

//  // @Implements HeatmapListener
//  private void firePointsetOrderChanged() {
//    for (final HeatmapListener listener : listeners) {
//      listener.pointsetOrderChanged(this);
//    }
//  }

//  // @Implements HeatmapListener
//  private void fireTextPropertiesChanged(Text text) {
//    for (final HeatmapListener listener : listeners) {
//      listener.textPropertiesChanged(this, text);
//    }
//    // TODO: outline the selected text on the canvas HERE
//  }

//  // @Implements HeatmapListener
//  void fireIdChanged(Object source, Map<Pointset, int[]> pointsetToSelectedPoints) {
//    for (final HeatmapListener listener : listeners) {
//      listener.idSelectionChanged(this, selected);
//    }
//  }

  public List<Object> loadObjects() {
    List<Object> objects = null;
    final List<ObjectsHolder> lrh = theRoot.getPersistenceContext().getExtent(
        ObjectsHolder.class);
    if (lrh.size() == 0) {
      return null;
    }
    if (lrh.size() > 1) {
      throw new RuntimeException("multiple tree node ObjectsHolders");
    }
    objectsHolder = lrh.get(0);
    objects = objectsHolder.getObjects();
    System.out.println("PlotManager.load(): objects");
    return objects;
  }

  public ObjectsHolder getObjectsHolder() {
    if (!Main.isPersisting) {
      return null;
    }
    if (objectsHolder != null) {
      return objectsHolder;
    }
    ObjectsHolder rh = null;
    final List<ObjectsHolder> lrh = theRoot.getPersistenceContext().getExtent(
        ObjectsHolder.class);
    if (lrh.size() == 0) {
      return null;
    }
    if (lrh.size() > 1) {
      throw new RuntimeException("multiple tree node ObjectsHolders");
    }
    rh = lrh.get(0);
    return rh;
  }

  public void storeObjects() {
    // Check that we are actually persisting
    if (!Main.isPersisting) {
      return;
    }
    if (objects == null) {
      throw new RuntimeException("objects may not be null");
    }
    objects.clear();
    objects.add(dimensionality);
    objects.add(view);
    objects.add(textList);
    objects.add(subject);
    objects.add(basis);
    objects.add(prevBasis);
    System.out.println("storing PlotterManager.objects");
    if (objectsHolder == null) {
      objectsHolder = new ObjectsHolder(objects);
    }
    else {
      objectsHolder.setObjects(objects);
    }
    theRoot.getPersistenceContext().set(objectsHolder);
  }

  public void clearRoot() {
    if (objectsHolder != null) {
      theRoot.getPersistenceContext().delete(objectsHolder);
    }
    else {
      throw new IllegalStateException("no objectsHolder");
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

  public Text[] getTextList() {
    final Text[] copy = new Text[textList.size()];
    for (int i = 0; i < copy.length; i++) {
      copy[i] = textList.get(i);
    }
    return copy;
  }

  public void addText(final Text text) {
    if (text == null) {
      throw new RuntimeException("text is null");
    }
    // OK to add duplicates if you want to draw them twice
    textList.add(text);
  }

  public Text getText(final int index) {
    return textList.get(index);
  }

  public int textCount() {
    return textList.size();
  }

  public boolean removeText(final Text text) {
    return textList.remove(text);
  }

  public boolean containsText(final Text text) {
    return textList.contains(text);
  }

  public void setDim(final int dimensionality) {
    if (dimensionality != 2 && dimensionality != 3) {
      throw new RuntimeException("bad dim");
    }
    this.dimensionality = dimensionality;
  }

  public void shutdown() {
    session.statusPanel.log1("Persisting plot for next session.");
    storeObjects();
  }

  public static void configureDb4o() {
    Db4oPersistenceService.configureDb4oForClass(ObjectsHolder.class,
        Subject.class, View.class, Text.class, Basis.class, Integer.class);
  }

  boolean selectTextNear(final int x, final int y) {
    // JTAL put in int selected
    final List<Text> candidates = new ArrayList<Text>();
    // System.out.println("CLICK AT X,Y: "+x+","+y);
    // scan the text and find the closest with respect to x,y
    for (Text text : textList) {
      // System.out.println("xLo: "+text.tStart[0]
      // +"xHi: "+(text.tStart[0]+text.width)
      // +"yLo: "+text.tStart[1]
      // +"yHi: "+(text.tStart[1]+text.height));
      if (x >= text.tStart[0] - text.width / 2
          && x <= text.tStart[0] + text.width / 2
          && y >= text.tStart[1] - text.width / 2
          && y <= text.tStart[1] + text.height / 2) {
        candidates.add(text);
        // System.out.println("HIT*******************");
      }
    }
    if (candidates.size() > 0) {
      if (getDim() == 3) {
        float t;
        float minz = Float.POSITIVE_INFINITY;
        // find closest z
        for (Text candidate : candidates) {
          t = candidate.tStart[2];
          if (t < minz) {
            minz = t;
          }
        }
      }
      // Pick first one on list
      selectText(candidates.get(0));
      return true;
    }
    return false;
  }

  void moveSelectedText(final int xOffset, final int yOffset) {
    selectedText.tStart[0] += xOffset;
    selectedText.tStart[1] += yOffset;
    if (selectedText.moving) {
      if (getDim() == 2) {
        // f = start;
        selectedText.start = view.unTransform2D(selectedText.tStart);
      }
      else if (session.plotManager.getDim() == 3) {
        // f = start;
        selectedText.start = view.unTransform3D(selectedText.tStart);
      }
      // Have the axes calculate the distance to
      // the axis or tic that owns this text, if any
      subject.axes.calculateLabelOffsetLengthMM(selectedText);
    }
    else {
      selectedText.start[0] += xOffset;
      selectedText.start[1] += yOffset;
    }
    if (!session.plotManager.view.zoomed) {
      selectedText.unzoomedStart[0] = selectedText.start[0]
          + session.plotManager.subject.cumDelta[0];
      selectedText.unzoomedStart[1] = selectedText.start[1]
          + session.plotManager.subject.cumDelta[1];
      selectedText.unzoomedStart[2] = selectedText.start[2]
          + session.plotManager.subject.cumDelta[2];
    }
    selectedText.calculateSubjectExtents();
    firePlotManagerChanged(new PlotManagerEvent(this,basis,PlotManagerEvent.Kind.TEXT_PROPERTIES_CHANGED));
//    fireTextPropertiesChanged(selectedText);
  }

  public void deleteAxes() {
    view.unzoom();
    canvas.setDragMode(Canvas.DragModeKind.NO_DRAG_MODE);
    saved = false;
    session.dialogManager.hideAxesDialogs();
    subject.axes.clearReferences();
    subject.axes = null;
    subject.calculateExtents();
    refresh();
  }

  public void resetAxes() {
    subject.axes.initialize();
    saved = false;
    refresh();
  }

  public void refreshCanvas() {
    refresh();
  }

  public void resetView() {
    view.reset();
    saved = false;
    refresh();
  }

  public void addTextAndRefresh(Text text) {
    addText(text);
    refresh();
  }

  public void removeTextAndRefresh(Text text) {
    session.plotManager.saved = false;
    refresh();
  }

  private void refresh() {
    canvas.setStale(true);
    canvas.refresh();
    // TODO: need to call the following in a more discriminating manner
    session.updateEnablement();
  }

  public Text getSelectedText() {
    return selectedText;
  }

  public void selectTextAt(int index) {
    selectedText = textList.get(index);
    selectText(selectedText);
  }

  public void selectText(Text text) {
    selectedText = text;
    firePlotManagerChanged(new PlotManagerEvent(this,basis,PlotManagerEvent.Kind.TEXT_SELECTION_CHANGED));
  }

  @Override
	public void progressChanged(Object subject, String string, int min,
      int value, int max) {
    // TODO Auto-generated method stub
  }

  public void cyclePointsetOrder() {
    if (session.pointsetManager.pointsetCount() > 0) {
      pointsets.add(pointsets.remove(0));
      refresh();
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

  public int pointsetCount() {
    return pointsets.size();
  }

  public Pointset getPointsetAt(int i) {
    return pointsets.get(i);
  }

  private void bringToForeground(Pointset pointset) {
    if (pointsets.size() - 1 != pointsets.indexOf(pointset)
        && pointsets.contains(pointset)) {
      // Pointset may not be registered yet
      pointsets.remove(pointset);
      if (pointsets.contains(pointset)) {
        throw new RuntimeException("duplicate pointset = " + pointset);
      }
      final boolean b = pointsets.add(pointset);
      firePlotManagerChanged(new PlotManagerEvent(this,basis,PlotManagerEvent.Kind.POINTSET_ORDER_CHANGED));
      refresh();
    }
  }

  private boolean registerPointset(final Pointset pointset) {
    if (pointset == null) {
      throw new RuntimeException("cannot add null pointset to manager");
    }
    if (pointsets.contains(pointset)) {
      throw new RuntimeException("duplicate pointset = " + pointset);
    }
    final boolean b = pointsets.add(pointset);
    firePlotManagerChanged(new PlotManagerEvent(this,basis,PlotManagerEvent.Kind.POINTSET_ORDER_CHANGED));
    return true;
  }

  private boolean unregisterPointset(final Pointset pointset) {
    if (!pointsets.contains(pointset)) {
      throw new RuntimeException("unregistering an unregistered pointset = "
          + pointset);
    }
    final boolean b = pointsets.remove(pointset);
    firePlotManagerChanged(new PlotManagerEvent(this,basis,PlotManagerEvent.Kind.POINTSET_ORDER_CHANGED));
    return true;
  }

  public void reorderPointsets(List<Pointset> pointsets2) {
    for (Pointset pointset : pointsets2) {
      if (!pointsets.contains(pointset)) {
        throw new RuntimeException("mismatch in pointset list");
      }
    }
    pointsets.clear();
    for (int i = 0; i < pointsets2.size(); i++) {
      pointsets.add(pointsets2.get(i));
    }
    firePlotManagerChanged(new PlotManagerEvent(this,basis,PlotManagerEvent.Kind.POINTSET_ORDER_CHANGED));
  }


  // @Implements PointsetManagerListener
  @Override
	public void pointsetManagerChanged(PointsetManagerEvent ev) {
    if (!(ev.member instanceof Pointset) && !(ev.member instanceof Pointset[])) { 
      return; 
    }

    switch (ev.kind) {
    case MEMBER_LOADED:
      break;
    case MEMBER_ADDED:
      registerPointset((Pointset)ev.member);
      session.plotManager.canvas.setStale(true);
      session.plotManager.canvas.refresh();
      break;
    case MEMBER_CHANGED:
      session.plotManager.canvas.setStale(true);
      session.plotManager.canvas.refresh();
      break;
    case POINTSET_REMOVED:
      unregisterPointset((Pointset)ev.member);
      session.plotManager.canvas.setStale(true);
      session.plotManager.canvas.refresh();
      break;
    case MEMBERS_SELECTION:
      Pointset[] selectedPointsets = (Pointset[])ev.member;
      if (selectedPointsets.length > 0) {
        bringToForeground(selectedPointsets[0]);
      }
      break;
    case MANAGER_CHANGED:
      session.plotManager.canvas.setStale(true);
      session.plotManager.canvas.refresh();
      break;
    case MEMBER_VISABILITY:
      session.plotManager.canvas.setStale(true);
      session.plotManager.canvas.refresh();
      break;
    case POINT_VIZ:
      session.plotManager.canvas.setStale(true);
      session.plotManager.canvas.refresh();
      break;
    case AC_MAP:
      session.plotManager.canvas.setStale(true);
      session.plotManager.canvas.refresh();
      break;
    }
  }

  public void previewChanged(Map<Pointset, int[]> pointsetToSelectedPoints) {
    firePlotManagerChanged(new PlotManagerEvent(this,pointsetToSelectedPoints,PlotManagerEvent.Kind.PREVIEW_CHANGED));
  }
}
