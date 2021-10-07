package gov.nih.ncgc.openhts.tool1.plotManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import com.jidesoft.swing.JidePopupMenu;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;
import gov.nih.ncgc.openhts.tool1.spreadsheet.PointsetTable;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public final class Canvas extends JPanel implements MouseListener,
    MouseMotionListener, ActionListener, PropertyChangeListener { // SINGLETON
  public enum DragModeKind {
    NO_DRAG_MODE, SELECTION_DRAG_MODE, ROTATION_XY_DRAG_MODE, ROTATION_Z_DRAG_MODE, TRANSLATION_DRAG_MODE, ZOOM_DRAG_MODE, TEXT_DRAG_MODE
  }

  public static final boolean SELECT_SELECTION_MODE = true;
  public static final boolean DESELECT_SELECTION_MODE = false;
  // TODO: move Swing components OUT
  private SpinnerNumberModel model1, model2;
  private JSpinner subsettingSpinner;
  private JidePopupMenu popupMenu;
  DragModeKind dragMode;
  public static Session session;
  private int xMin, xMax, yMin, yMax;
  public int w, h;
  public Color backColor;
  private final static int n = 3;
  int[] draggedX = new int[n], draggedY = new int[n];
  int movedX, movedY;
  private Slate slate;
  public int ssRadius;
  public long acceptableTime;
  private int startDragX, startDragY, stopDragX, stopDragY;
  private int prevStartDragX, prevStartDragY, prevStopDragX, prevStopDragY;
  private int xMouseStart, yMouseStart, xOffset, yOffset; // for Text drag:
  private boolean showedTextMessage = false;
  public boolean pixelUnitsWidth, pixelUnitsHeight;
  public boolean subsetting;
  public Timer previewTimer, inertiaTimer, subsettingTimer;
  private CanvasToolbar toolbar;
  private boolean selectionMode;
  private AppLafManager.CursorKind nonWaitCursor = AppLafManager.CursorKind.CURSOR_DRAG_NONE,
      prevCursor, currentCursor = AppLafManager.CursorKind.CURSOR_DRAG_NONE;
  private boolean drawingRBB;
  public boolean blankData;
  public boolean autoCompose, fastCompose;
  private int cumXRot, cumYRot;
  private float cumZRot;
  private final static int cumRotSensitivity = 100;
  private final static float xyRotationalSensitivity = 0.002f,
      zRotationalSensitivity = -0.00002f;
  public static final float NINETY_DEGREES = (float) Math.PI / 2.0f;
  private boolean mouse1Down;
  public int turnOnSubsettingCutoff;
  public boolean advancedEnabled = false;
  private final Map<Pointset, int[]> pointsetToSelectedPoints = new HashMap<Pointset, int[]>();
  public boolean preview;

  Canvas(final Session session) {
    // System.out.println("CREATED CANVAS " + hashCode() + this);
    Canvas.session = session;
  }

  void initialize() {
    setBackground(backColor = Color.black);
    setForeground(Color.white);
    setCursor(AppLafManager.CursorKind.CURSOR_DRAG_NONE);
    addMouseListener(this);
    addMouseMotionListener(this);
    // addKeyListener(this);
    toolbar = new CanvasToolbar(session);
    popupMenu = new CanvasPopupMenu(session);
    xMax = w = 600;
    yMax = h = 600;
    slate = new Slate(session);
    slate.initialize();
    acceptableTime = 40; // 0.05 sec to draw (not update) a frame
    previewTimer = new Timer(250, this);
    inertiaTimer = new Timer(1, this);
    subsettingTimer = new Timer(30000, this);
    subsettingTimer.setInitialDelay(0);
    turnOnSubsettingCutoff = 50000;
    blankData = false;
    autoCompose = true;
    fastCompose = false;
    mouse1Down = false;
    pixelUnitsWidth = true;
    pixelUnitsHeight = true;
    dragMode = DragModeKind.NO_DRAG_MODE;
    selectionMode = SELECT_SELECTION_MODE;
    ssRadius = 3;
    backColor = new Color(0, 0, 0);
    updateInertiaTimer();
  }

  void clearReferences() {
    // System.out.println("CLEARING REFERENCES TO CANVAS "+hashCode());
    toolbar.removePropertyChangeListener(this);
    toolbar.removeAll();
    toolbar = null;
    // PropertyChangeListener[] pcl = toolBar.getPropertyChangeListeners();
    // for (int i=0; i<pcl.length; i++) {
    // toolBar.removePropertyChangeListener(pcl[i]);
    // }
    // toolBar = null;
    slate = null;
  }

  @Override
  protected void finalize() {
    // System.out.println("FINALIZED CANVAS "+hashCode());
  }

  @Override
	public void propertyChange(final PropertyChangeEvent ev) {
    if (ev.getSource() == toolbar) {
      // System.out.println("ev.getPropertyName():
      // "+ev.getPropertyName()+"ev.getNewValue(): "+ ev.getNewValue());
      if ("orientation".equals(ev.getPropertyName())
      /* || "ancestor".equals(ev.getPropertyName()) */
      ) {
        // toolbarTimer.start(); // use timer due to weird height/width
        // non-updating bug
      }
    }
  }

  /**
   * If app (not system) changes width or height of canvas, then call this
   * method
   */
  public void internalResize() {
    xMin = 0;
    xMax = w;
    yMin = 0;
    yMax = h;
    Dimension dim=new Dimension(w, h);
    setMinimumSize(dim);
    setPreferredSize(dim);
    setMaximumSize(dim);
    //setSize(new Dimension(w, h));
    session.frame.pack();
  }

  /**
   * called by component listener, like when the user changes the frame size or
   * when a frame.pack() occurs
   */
  void externalResize() {
    // user resized frame manually, canvas forced to resize.
    // System.out.println("toolBar.getParent():
    // "+toolBar.getParent().hashCode()+toolBar.getParent());
    // System.out.println("session.getFrame().getContentPane():
    // "+session.getFrame().getContentPane().hashCode()+toolBar.getParent());
    if (toolbar.getParent() == session.plotManager) { // inside
      // System.out.println("\nTOOLBAR is inside thread =
      // "+Thread.currentThread().hashCode());
      if (toolbar.getOrientation() == SwingConstants.VERTICAL) {
        w = session.plotManager.getWidth() - toolbar.getWidth();
        h = session.plotManager.getHeight();
      }
      else {
        w = session.plotManager.getWidth();
        h = session.plotManager.getHeight() - toolbar.getHeight();
        // System.out.println("session.getFrame().getContentPane().getHeight():
        // "+session.getFrame().getContentPane().getHeight());
        // System.out.println("toolBar.getHeight(): "+toolBar.getHeight());
      }
    }
    else {
      // System.out.println("TOOLBAR is outside thread =
      // "+Thread.currentThread().hashCode());
      w = session.plotManager.getWidth();
      h = session.plotManager.getHeight();
    }
    if (model1 != null) {
      if (pixelUnitsWidth) {
        model1.setValue(new Integer(w));
      }
      else {
        model1.setValue(new Integer((int) (w / session.dotsPerMM)));
      }
    }
    if (model2 != null) {
      if (pixelUnitsHeight) {
        model2.setValue(new Integer(h));
      }
      else {
        model2.setValue(new Integer((int) (h / session.dotsPerMM)));
      }
    }
    // setMinimumSize(new Dimension(w,h));
    setPreferredSize(new Dimension(w, h));
    // setMaximumSize(new Dimension(w,h));
    setSize(new Dimension(w, h));
    xMin = 0;
    xMax = w;
    yMin = 0;
    yMax = h;
    // System.out.println("EXTERNALLY RESIZED CANVAS "+ hashCode() +" TO
    // getWidth(): "+getWidth() +" w: "+ w
    // + " getHeight():" +getHeight() + " h: " + h + " thread =
    // "+Thread.currentThread().hashCode());
    slate.initialize();
    smartCompose();
    slate.stale = true;
    refresh();
  }

  @Override
  public void paint(final Graphics g) {
    // This is called by the system when the window is uncovered.
    // graphics = g;
    // if (!resizeTimer.isRunning()) {
    update(g);
    // }
  }

  public void refresh() {
    // Handles repaint requests from this app, not the System
    // new Exception("loading: "+session.loading).printStackTrace();
    // hideIDWindow();
    repaint();
  }

  @Override
  public void update(final Graphics g) {
    // System.out.println("CANVAS UPDATE");
    if (slate.stale) {
      freshenImage(slate);
      slate.stale = false;
    }
    g.drawImage(slate.image, 0, 0, backColor, null);
    // System.out.println("total memory used:
    // "+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory()));
    if (drawingRBB) {
      g.setColor(Color.yellow);
      g.drawLine(startDragX, startDragY, startDragX, stopDragY);
      g.drawLine(startDragX, stopDragY, stopDragX, stopDragY);
      g.drawLine(stopDragX, stopDragY, stopDragX, startDragY);
      g.drawLine(stopDragX, startDragY, startDragX, startDragY);
    }
  }

  private void freshenImage(final Slate slate) {
    slate.clearPixels(backColor); // background
    slate.image.getGraphics().fillRect(0, w, 0, h);
    switch (session.plotManager.getDim()) {
    case 2:
      draw2D(slate);
      break;
    case 3:
      draw3D(slate);
      break;
    default:
      throw new RuntimeException("bad dim");
    }
  }

  public void draw2D(final Slate slate) {
    if (dragMode == DragModeKind.ROTATION_XY_DRAG_MODE) {
      throw new RuntimeException("bad mode");
    }
    // Here we go... !
    session.plotManager.subject.axes.draw2D(slate, false);
    // size = session.plotManager.textList.size();
    for (final Text text : session.plotManager.getTextList()) {
      text.draw2D(slate, false);
    }
    boolean drawAllPoints = false, drawNoPoints = false;
    if (dragMode != DragModeKind.ROTATION_Z_DRAG_MODE
        && dragMode != DragModeKind.TRANSLATION_DRAG_MODE) {
      drawAllPoints = true;
    }
    else {
      if (blankData) {
        if (!mouse1Down) {
          drawAllPoints = true;
        }
        else {
          drawNoPoints = true;
        } // don't draw
      }
      else if (subsetting) {
        if (!mouse1Down) {
          drawAllPoints = true;
        }
        else if (subsetting) {
          drawAllPoints = false;
        }
      }
      else {
        drawAllPoints = true;
      }
    }
    if (!drawNoPoints) {
      for (final Pointset pointset : session.plotManager.getPointsets()) {
        pointset.draw2D(slate, drawAllPoints);
      }
    }
  }

  public void draw3D(final Slate slate) {
    // System.err.println("DRAWING ON THREAD
    // "+Thread.currentThread().hashCode());
    // System.err.println(Runtime.getRuntime().totalMemory());
    // System.err.flush();
    int size, i;
    // Here we go... !
    session.plotManager.subject.axes.draw3D(slate, false);
    for (final Text text : session.plotManager.getTextList()) {
      text.draw3D(slate, false);
    }
    boolean drawAllPoints = false, drawNoPoints = false;
    if (dragMode != DragModeKind.ROTATION_XY_DRAG_MODE
        && dragMode != DragModeKind.ROTATION_Z_DRAG_MODE
        && dragMode != DragModeKind.TRANSLATION_DRAG_MODE) {
      drawAllPoints = true;
    }
    else {
      if (blankData) {
        if (!mouse1Down) {
          drawAllPoints = true;
        }
        else {
          drawNoPoints = true;
        } // don't draw
      }
      else if (subsetting) {
        if (!mouse1Down) {
          drawAllPoints = true;
        }
        else if (subsetting) {
          drawAllPoints = false;
        }
      }
      else {
        drawAllPoints = true;
      }
    }
    if (!drawNoPoints) {
      size = session.plotManager.pointsetCount();
      for (i = 0; i < size; i++) {
        // System.out.println("DRAWING
        // "+session.plotManager.getPointsetAt(i).getName());
        (session.plotManager.getPointsetAt(i)).draw3D(slate, drawAllPoints);
      }
    }
    if (session.plotManager.view.getStereo()) {
      session.plotManager.view.stereoizeTmat();
      if (session.plotManager.subject.axes != null) {
        session.plotManager.subject.axes.draw3D(slate, false);
      }
      for (final Text text : session.plotManager.getTextList()) {
        text.draw3D(slate, false);
      }
      if (!drawNoPoints) {
        size = session.plotManager.pointsetCount();
        for (i = 0; i < size; i++) {
          (session.plotManager.getPointsetAt(i)).draw3D(slate, drawAllPoints);
        }
      }
      session.plotManager.view.unstereoizeTmat();
    }
    // System.runFinalization();
    // System.gc();
    // System.err.println(Runtime.getRuntime().totalMemory());
  }

  // @Implements MouseMotionListener
  @Override
	public void mouseMoved(final MouseEvent ev) {
    if (ev.isConsumed()) {
      return;
    }
    if (preview) {
      previewTimer.restart();
    }
    movedX = ev.getX();
    movedY = ev.getY();
    ev.consume();
  }

  private void selectPointFromVisiblePointsetsAt(final int x, final int y,
      MouseEvent event) {
    int unlockedPointsetCount = 0, lockedPointsetCount = 0;
    Pointset lastPointsetWithSelections = null;
    PointsetTable pointsetTable;
    // Ask each Pointset to select rows in the numbers under the cursor if data
    // source is not locked.
    for (Pointset pointset : session.plotManager.getPointsets()) {
      if (!pointset.isVisible()) {
        continue;
      }
      pointsetTable = session.spreadsheet.getPointsetTable(pointset);
      final int[] selected = getVisiblePointsNear(pointset, x, y);
      pointsetTable.setSelectedFor(selected, getSelectionMode(), event);
      if (session.spreadsheet.getPointsetTable(pointset).isUnlocked()) {
        if (selected.length > 0) {
          lastPointsetWithSelections = pointset;
        }
        unlockedPointsetCount++;
      }
      else {
        lockedPointsetCount++;
      }
    }
    if (unlockedPointsetCount == 0) {
      if (lockedPointsetCount > 0) {
        session
            .message("All tables containing pointsetToSelectedPoints numbers are locked from de/selection.");
      }
    }
    else {
      if (lastPointsetWithSelections != null) {
        final Pointset pointset = lastPointsetWithSelections;
        SwingUtilities.invokeLater(new Runnable() {
          @Override
					public void run() {
            session.spreadsheet.setSelectedPointsetTab(pointset);
            session.spreadsheet.scrollToLastSelection(pointset);
          }
        });
      }
      else {
        session.statusPanel.log2("No numbers were pointsetToSelectedPoints");
      }
    }
  }

  /**
   * Select the data numbers inside the rubber band box
   */
  int selectPointsInside(final Pointset pointset, final int loX, final int hiX,
      final int loY, final int hiY) {
    final PointsetTable pointsetTable = session.spreadsheet
        .getPointsetTable(pointset);
    if (!pointsetTable.unlocked) {
      return 0;
    }
    int i, last = -1, num = 0, nVisible = pointset.getNVisible();
    List<Integer> inside = new ArrayList<Integer>();
    for (i = 0; i < nVisible; i++) {
      if (pointset.tPoints[i][0] >= loX && pointset.tPoints[i][0] <= hiX
          && pointset.tPoints[i][1] >= loY && pointset.tPoints[i][1] <= hiY) {
        // last = pointsetTable.userToTable[i];
        inside.add(i);
        // table.getTable().getSelectionModel().addRowSelectionInterval(last,
        // last);
        num++;
      }
    }
    int[] insideArray = new int[inside.size()];
    for (i = 0; i < insideArray.length; i++) {
      insideArray[i] = inside.get(i);
    }
    pointsetTable.setSelectedPoints(insideArray, session.plotManager.canvas
        .getSelectionMode() == Canvas.SELECT_SELECTION_MODE);
    return num;
  }

  int[] getVisiblePointsNear(final Pointset pointset, final int x, final int y) {
    // returns the point(s) pointsetToSelectedPoints by the user (with user
    // indexing)
    // NOTE: this methods works even if pointset is not visible
    final PointsetTable table = session.spreadsheet.getPointsetTable(pointset);
    if (table == null) {
      throw new RuntimeException("no table for pointset = " + pointset);
    }
    float f, xLo = x - session.plotManager.canvas.ssRadius, xHi = x
        + session.plotManager.canvas.ssRadius, yLo = y
        - session.plotManager.canvas.ssRadius, yHi = y
        + session.plotManager.canvas.ssRadius;
    int i, j;
    // table.maxSelectedIndex = 0;
    final ArrayList<Integer> v = new ArrayList<Integer>();
    // Scan the visible data and find the closest with respect to x,y
    int nVisible = pointset.getNVisible();
    for (i = 0; i < nVisible; i++) {
      if (pointset.tPoints[i][0] >= xLo && pointset.tPoints[i][0] <= xHi
          && pointset.tPoints[i][1] >= yLo && pointset.tPoints[i][1] <= yHi) {
        v.add(new Integer(i));
      }
    }
    // System.out.println("initial compounds: " + v.size());
    // for (i=0; i<v.size(); i++) {
    // j=((Integer)v.get(i)).intValue();
    // System.out.println(molNames[j]+" ");
    // }
    // Reduce the number closest to center pixel if more than one
    // pointsetToSelectedPoints
    if (v.size() > 1) {
      // Scan for a cutoff
      for (f = 0.0f; f <= session.plotManager.canvas.ssRadius; f += 1.0f) {
        xLo = x - f;
        xHi = x + f;
        yLo = y - f;
        yHi = y + f;
        for (i = 0; i < v.size(); i++) {
          j = (v.get(i)).intValue();
          if (pointset.tPoints[j][0] >= xLo && pointset.tPoints[j][0] <= xHi
              && pointset.tPoints[j][1] >= yLo && pointset.tPoints[j][1] <= yHi) {
            break;
          }
        }
        if (i < v.size()) { // have a cutoff set
          break;
        }
      }
      // Remove everything outside the cutoff
      xLo = x - f;
      xHi = x + f;
      yLo = y - f;
      yHi = y + f;
      for (i = v.size() - 1; i >= 0; i--) {
        j = (v.get(i)).intValue();
        if (pointset.tPoints[j][0] < xLo || pointset.tPoints[j][0] > xHi
            || pointset.tPoints[j][1] < yLo || pointset.tPoints[j][1] > yHi) {
          v.remove(i);
        }
      }
      if (v.size() <= 0) {
        throw new RuntimeException("no data point");
      }
      // if 3D, find closest ones on z-axis
      if (session.plotManager.getDim() == 3) {
        float minz = Float.POSITIVE_INFINITY;
        j = 0;
        // find closest z
        for (i = 0; i < v.size(); i++) {
          j = (v.get(i)).intValue();
          if (pointset.tPoints[j][2] < minz) {
            minz = pointset.tPoints[j][2];
          }
        }
        // remove all but closest datapoint(s)
        for (i = v.size() - 1; i >= 0; i--) {
          j = (v.get(i)).intValue();
          if (pointset.tPoints[j][2] > minz) {
            v.remove(i);
          }
        }
      }
    }
    final int[] retval = new int[v.size()];
    for (int k = 0; k < retval.length; k++) {
      retval[k] = v.get(k);
    }
    return retval;
  }

  private void boxSelection(final int loX, final int hiX, final int loY,
      final int hiY) {
    int i, unlockedNum = 0, lockedNum = 0, num = 0;
    int last_i = -1;
    // Ask each Pointset to select rows in the numbers under the cursor if data
    // source
    // is not locked.
    for (i = 0; i < session.plotManager.pointsetCount(); i++) {
      final Pointset pointset = session.plotManager.getPointsetAt(i);
      num += selectPointsInside(pointset, loX, hiX, loY, hiY);
      if (session.spreadsheet.getPointsetTable(pointset).isUnlocked()) {
        if (num > 0) {
          last_i = i;
        }
        unlockedNum += num;
      }
      else {
        lockedNum += num;
      }
    }
    if (unlockedNum <= 0) {
      if (lockedNum <= 0) {
        Toolkit.getDefaultToolkit().beep(); // you missed!
      }
      else {
        session
            .message("All tables of pointsetToSelectedPoints data are locked."/* ,"locked_data" */);
      }
    }
    else {
      // System.out.println("last_i: "+last_i);
      if (last_i >= 0) {
        final int ii = last_i;
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
          @Override
					public void run() {
            final Pointset pointset = session.plotManager.getPointsetAt(ii);
            final PointsetTable table = session.spreadsheet
                .getPointsetTable(pointset);
            table.scrollToLastSelection();
            session.spreadsheet.setSelectedPointsetTab(pointset);
            // tabbedPane.setSelectedIndex(-1);
            // session.spreadsheet.tabbedPane.setSelectedIndex(ii);
            // System.out.println("LAST_I: "+ii);
            // System.out.println("maxSelectedIndex: "+data.maxSelectedIndex);
          }
        });
      }
    }
  }

  // @Implements MouseListener
  @Override
	public void mousePressed(final MouseEvent ev) {
    try {
      if (ev.isConsumed()) {
        return;
      }
      grabFocus();
      if (0 != (ev.getModifiers() & InputEvent.BUTTON3_MASK)) { // right mouse
        // button
        popupMenu.show(this, ev.getX(), ev.getY());
        ev.isConsumed();
        return;
      }
      mouse1Down = true;
      for (int i = 0; i < n; i++) {
        draggedX[i] = ev.getX();
        draggedY[i] = ev.getY();
      }
      if (dragMode == DragModeKind.SELECTION_DRAG_MODE
          || dragMode == DragModeKind.ZOOM_DRAG_MODE) {
        prevStartDragX = xMin - 1;
        prevStartDragY = yMin - 1;
        prevStopDragX = xMin - 1;
        prevStopDragY = yMin - 1;
        stopDragX = startDragX = ev.getX();
        stopDragY = startDragY = ev.getY();
        drawingRBB = true;
      }
      else if (dragMode == DragModeKind.TEXT_DRAG_MODE) {
        xMouseStart = ev.getX();
        yMouseStart = ev.getY();
        if (session.plotManager.selectTextNear(xMouseStart, yMouseStart)) {
          Text.dragging = true;
        }
        drawingRBB = true;
      }
      ev.consume();
    }
    catch (final Throwable ex) {
      session.errorSupport("While listening to mouse:", ex,
          "mouse_listener_canvas_error");
    }
  }

  // @Implements MouseListener
  @Override
	public void mouseReleased(final MouseEvent ev) {
    if (ev.isConsumed()) {
      return;
    }
    if ((ev.getModifiers() & InputEvent.BUTTON1_MASK) == 0) {
      ev.consume();
      return; // ignore everything except the left button
    }
    mouse1Down = false;
    int loX = 0, hiX = 0, loY = 0, hiY = 0;
    if (dragMode == DragModeKind.SELECTION_DRAG_MODE
        || dragMode == DragModeKind.ZOOM_DRAG_MODE) {
      // set up ("sort") low and high *X*
      if (startDragX > stopDragX) {
        hiX = startDragX;
        loX = stopDragX;
      }
      else if (startDragX < stopDragX) {
        loX = startDragX;
        hiX = stopDragX;
      }
      else { // no size to the rubber band
        if (dragMode == DragModeKind.SELECTION_DRAG_MODE) {
          selectPointFromVisiblePointsetsAt(ev.getX(), ev.getY(), ev);
          // windows and gnome behave differently, do focus manually
          requestFocus();
        }
        else {
          java.awt.Toolkit.getDefaultToolkit().beep();
        }
        resetRBB(); // does a refresh
        return;
      }
      // set up ("sort") low and high *Y*
      if (startDragY > stopDragY) {
        hiY = startDragY;
        loY = stopDragY;
      }
      else if (startDragY < stopDragY) {
        loY = startDragY;
        hiY = stopDragY;
      }
      else { // no size to the rubberband
        if (dragMode == DragModeKind.SELECTION_DRAG_MODE) {
          selectPointFromVisiblePointsetsAt(ev.getX(), ev.getY(), ev);
          requestFocus();
        }
        else {
          java.awt.Toolkit.getDefaultToolkit().beep();
        }
        ev.consume();
        resetRBB(); // does a refresh
        return;
      }
      if (dragMode == DragModeKind.ZOOM_DRAG_MODE) {
        session.setWaitCursor(true);
        session.plotManager.view.zoom(loX, hiX, loY, hiY);
        session.setWaitCursor(false);
        resetRBB(); // does a refresh
      }
      else if (dragMode == DragModeKind.SELECTION_DRAG_MODE) {
        try {
          session.setWaitCursor(true);
          // Pointset.createTableFrame(session, session.assignLocation());
          boxSelection(loX, hiX, loY, hiY);
          session.setWaitCursor(false);
        }
        // catch (SpaceViewerException ex) { /* ignore */}
        finally {
          requestFocus();
          session.setWaitCursor(false);
          resetRBB(); // does a refresh
        }
      }
    }
    else if (dragMode == DragModeKind.TEXT_DRAG_MODE) {
      if (Text.dragging) {
        if (xOffset != 0 || yOffset != 0) {
          setStale(true);
        }
        session.plotManager.moveSelectedText(xOffset, yOffset);
        // text.selected = -1;
        xOffset = 0;
        yOffset = 0;
        Text.dragging = false;
        resetRBB();
      }
    }
    else if ((dragMode == DragModeKind.ROTATION_XY_DRAG_MODE
        || dragMode == DragModeKind.ROTATION_Z_DRAG_MODE || dragMode == DragModeKind.TRANSLATION_DRAG_MODE)
        && (blankData || subsetting)) {
      slate.stale = true;
      refresh(); // need to redraw new view of subject
    }
    else if (dragMode == DragModeKind.NO_DRAG_MODE) {
      try {
        // Pointset.createTableFrame(session, session.assignLocation());
        // Pointset.tabbedPane.setSelectedIndex(3);
        selectPointFromVisiblePointsetsAt(ev.getX(), ev.getY(), ev);
      }
      catch (final Throwable ex) {
        session.errorSupport("During single selection: ", ex,
            "drag_none_canvas_error");
      }
      finally {
        //
      }
      // }
      // else {
      // if (!showedIdMessage) {
      // session
      // .message("To identify a datum, hover mouse over it \n"
      // + "and then left click. To identify all data \n"
      // + "in a screen region, set mouse drag mode to \n"
      // + "\"selection\". (This message will be showed\n"
      // + "only once, replaced by a beep.)"/* ,"first_click_drag_none_canvas"
      // */);
      // showedIdMessage = true;
      // }
      // else {
      // Toolkit.getDefaultToolkit().beep();
      // }
      // }
    }
    ev.consume();
  }

  // @Implements MouseListener
  @Override
	public void mouseDragged(final MouseEvent ev) {
    if (ev.isConsumed()) {
      return;
    }
    // ignore everything except the left button
    if ((ev.getModifiers() & InputEvent.BUTTON1_MASK) == 0) {
      ev.consume();
      return;
    }
    if (dragMode == DragModeKind.NO_DRAG_MODE) {
      return;
    }
    int x, y;
    for (int i = n - 1; i > 0; i--) {
      draggedX[i] = draggedX[i - 1];
      draggedY[i] = draggedY[i - 1];
    }
    draggedX[0] = x = ev.getX();
    draggedY[0] = y = ev.getY();
    // alpha is the angle of the vector perpendicular to
    // motion on the screen which serves as the axis of rotation
    if (drawingRBB) {
      switch (dragMode) {
      case ZOOM_DRAG_MODE:
      case SELECTION_DRAG_MODE:
        stopDragX = x;
        stopDragY = y;
        break;
      case TEXT_DRAG_MODE:
        if (Text.dragging) {
          xOffset = x - xMouseStart;
          yOffset = y - yMouseStart;
          final Text text = session.plotManager.selectedText;
          startDragX = (int) text.tStart[0] - text.width / 2 + xOffset;
          stopDragX = (int) text.tStart[0] + text.width / 2 + xOffset;
          startDragY = (int) text.tStart[1] - text.height / 2 + yOffset;
          stopDragY = (int) text.tStart[1] + text.height / 2 + yOffset;
        }
        break;
				//$CASES-OMITTED$
			default:
        throw new RuntimeException("bad drag mode");
      }
    }
    else {
      session.plotManager.view.dy = draggedY[0] - draggedY[1];
      session.plotManager.view.dx = draggedX[0] - draggedX[1];
      switch (dragMode) {
      case ROTATION_XY_DRAG_MODE:
        session.plotManager.view.setInertiaXYDirection(true);
        if (session.plotManager.getDim() == 3) {
          if (session.plotManager.view.getOrthogonalMode()) {
            cumXRot += session.plotManager.view.dx;
            cumYRot += session.plotManager.view.dy;
            if (cumXRot > cumRotSensitivity) {
              session.plotManager.view.rotate(1, 0, NINETY_DEGREES);
              cumXRot = 0;
            }
            else if (cumXRot < -cumRotSensitivity) {
              session.plotManager.view.rotate(-1, 0, NINETY_DEGREES);
              cumXRot = 0;
            }
            if (cumYRot > cumRotSensitivity) {
              session.plotManager.view.rotate(0, 1, NINETY_DEGREES);
              cumYRot = 0;
            }
            else if (cumYRot < -cumRotSensitivity) {
              session.plotManager.view.rotate(0, -1, NINETY_DEGREES);
              cumYRot = 0;
            }
          }
          else {
            session.plotManager.view.rotate(session.plotManager.view.dx,
                session.plotManager.view.dy, xyRotationalSensitivity);
          }
        }
        else {
          throw new RuntimeException("not in 3D mode");
        }
        break;
      case ROTATION_Z_DRAG_MODE:
        session.plotManager.view.setInertiaXYDirection(false);
        session.plotManager.view.cross = (x - session.plotManager.view.tMat[3][0])
            * -session.plotManager.view.dy
            - (y - session.plotManager.view.tMat[3][1])
            * -session.plotManager.view.dx;
        if (session.plotManager.view.getOrthogonalMode()) {
          cumZRot += session.plotManager.view.cross
              * Canvas.zRotationalSensitivity;
          if (cumZRot > NINETY_DEGREES) {
            session.plotManager.view.rotate(NINETY_DEGREES, 1.0f);
            // about z-axis of view
            cumZRot = 0.0f;
          }
          else if (cumZRot < -NINETY_DEGREES) {
            session.plotManager.view.rotate(-NINETY_DEGREES, 1.0f);
            // about z-axis of view
            cumZRot = 0.0f;
          }
        }
        else {
          session.plotManager.view.rotate(session.plotManager.view.cross,
              zRotationalSensitivity);
          // about z-axis of view
        }
        break;
      case TRANSLATION_DRAG_MODE:
        session.plotManager.view.translate(-session.plotManager.view.dx,
            -session.plotManager.view.dy);
        break;
				//$CASES-OMITTED$
			default:
      }
    }
    refresh();
    ev.consume();
  }

  // @Implements MouseListener
  @Override
	public void mouseClicked(final MouseEvent ev) {
    if (ev.isConsumed()) {
      return;
    }
    // Ignore everything except the left button
    if ((ev.getModifiers() & InputEvent.BUTTON1_MASK) == 0) {
      ev.consume();
      return;
    }
    if (dragMode == DragModeKind.TEXT_DRAG_MODE) {
      xMouseStart = ev.getX();
      yMouseStart = ev.getY();
      if (session.plotManager.selectTextNear(xMouseStart, yMouseStart)) {
        session.dialogManager.getTextModifyDialog().showDialog();
      }
      else {
        if (!showedTextMessage) {
          session
              .message("To modify a text item, left click it when\n"
                  + "cursor is in Text mode.  To move the text item\n"
                  + "mouse drag it on the canvas when the cursor\n"
                  + "is in Text mode.  (This message will be showed\n"
                  + "only once, replaced by a beep.)"/* ,"first_click_drag_none_canvas" */);
          showedTextMessage = true;
        }
        else {
          Toolkit.getDefaultToolkit().beep();
        }
      }
    }
    ev.consume();
  }

  // @Implements MouseListener
  @Override
	public void mouseEntered(final MouseEvent ev) {
    // if (ev.isConsumed()) {
    // return;
    // }
    // grabFocus(); // so it gets keyboard input
    // ev.consume();
  }

  // @Implements MouseListener
  @Override
	public void mouseExited(final MouseEvent ev) {
    if (ev.isConsumed()) {
      return;
    }
    previewTimer.stop();
    ev.consume();
  }

  public void setSelectionMode(final boolean mode) {
    selectionMode = mode;
    updateActions();
  }

  void toggleSelectionMode() {
    setSelectionMode(!selectionMode);
  }

  boolean getSelectionMode() {
    return selectionMode;
  }

  public void setDragMode(final DragModeKind mode) {
    dragMode = mode;
    switch (mode) {
    case SELECTION_DRAG_MODE:
      prepRBB();
      setCursor(AppLafManager.CursorKind.CURSOR_DRAG_SELECTION);
      break;
    case ZOOM_DRAG_MODE:
      prepRBB();
      setCursor(AppLafManager.CursorKind.CURSOR_DRAG_ZOOM);
      break;
    case TEXT_DRAG_MODE:
      setCursor(AppLafManager.CursorKind.CURSOR_DRAG_TEXT);
      break;
    case ROTATION_XY_DRAG_MODE:
      resetRBB();
      if (session.plotManager.getDim() != 3) {
        throw new RuntimeException("bad drag request");
      }
      setCursor(AppLafManager.CursorKind.CURSOR_DRAG_XYROTATE);
      break;
    case ROTATION_Z_DRAG_MODE:
      resetRBB();
      setCursor(AppLafManager.CursorKind.CURSOR_DRAG_ZROTATE);
      break;
    case TRANSLATION_DRAG_MODE:
      resetRBB();
      setCursor(AppLafManager.CursorKind.CURSOR_DRAG_TRANSLATE);
      break;
    case NO_DRAG_MODE:
      resetRBB();
      setCursor(AppLafManager.CursorKind.CURSOR_DRAG_NONE);
      break;
    default:
      throw new RuntimeException("unknown drag mode");
    }
    updateActions();
  }

  DragModeKind getDragMode() {
    return dragMode;
  }

  private void resetRBB() {
    drawingRBB = false;
    startDragX = startDragY = stopDragX = stopDragY = -1;
    refresh();
  }

  private void prepRBB() {
    drawingRBB = true;
    prevStartDragX = prevStartDragY = prevStopDragX = prevStopDragY = -1;
  }

  public void enableSubsetting(final boolean enabled) {
    if (enabled) {
      subsetting = true;
      subsettingTimer.restart();
      if (subsettingSpinner != null) {
        subsettingSpinner.setEnabled(true);
      }
    }
    else {
      subsetting = false;
      subsettingTimer.stop();
      if (subsettingSpinner != null) {
        subsettingSpinner.setEnabled(false);
      }
    }
  }

  public void toggleStereo() throws Exception {
    if (session.plotManager.getDim() != 3) {
      session.plotManager.view.setStereo(false);
    }
    else {
      if (!session.plotManager.view.getStereo()) {
        session.plotManager.view.setStereo(true);
        w *= 2;
      }
      else {
        session.plotManager.view.setStereo(false);
        w /= 2;
      }
      internalResize(); // will call the refresh
      // waitForIdle();
    }
    updateActions();
  }

  void toggleSubsettingMode() {
    if (subsetting) {
      System.out.println("subsetting mode off");
      subsetting = false;
      subsettingTimer.stop();
    }
    else {
      System.out.println("subsetting mode on");
      subsetting = true;
      subsettingTimer.restart();
    }
  }

  @Override
	public void actionPerformed(final ActionEvent ev) {
    if (ev.getSource() == previewTimer) {
      if (!hasFocus()) {
        return;
      }
      // Mouse has hovered in one spot for long enough for us to show the
      // user what's underneath.
      previewTimer.stop();
      if (dragMode == DragModeKind.NO_DRAG_MODE) {
        int totalSelectedCount = 0;
        for (Pointset pointset : session.plotManager.getPointsets()) {
          if (!pointset.isVisible()) {
            pointsetToSelectedPoints.put(pointset, new int[0]);
            continue;
          }
          int[] selections = getVisiblePointsNear(pointset, movedX, movedY);
          pointsetToSelectedPoints.put(pointset, selections);
          totalSelectedCount += selections.length;
        }
        session.statusPanel.log2("hovering over " + totalSelectedCount
            + " numbers");
        if (totalSelectedCount > 0) {
          session.plotManager.previewChanged(pointsetToSelectedPoints);
        }
      }
    }
    else if (ev.getSource() == inertiaTimer) {
      if (!session.plotManager.view.getInertia()
          || session.plotManager.view.dx == 0
          && session.plotManager.view.dy == 0) {
        return;
      }
      inertiaTimer.stop();
      if (session.plotManager.view.isInertiaXYDirection()) {
        if (session.plotManager.view.getOrthogonalMode()) {
          cumXRot += session.plotManager.view.dx;
          cumYRot += session.plotManager.view.dy;
          if (cumXRot > cumRotSensitivity) {
            session.plotManager.view.rotate(1, 0, NINETY_DEGREES);
            cumXRot = 0;
          }
          else if (cumXRot < -cumRotSensitivity) {
            session.plotManager.view.rotate(-1, 0, NINETY_DEGREES);
            cumXRot = 0;
          }
          if (cumYRot > cumRotSensitivity) {
            session.plotManager.view.rotate(0, 1, NINETY_DEGREES);
            cumYRot = 0;
          }
          else if (cumYRot < -cumRotSensitivity) {
            session.plotManager.view.rotate(0, -1, NINETY_DEGREES);
            cumYRot = 0;
          }
        }
        else {
          session.plotManager.view.rotate(session.plotManager.view.dx,
              session.plotManager.view.dy, xyRotationalSensitivity);
        }
      }
      else {
        if (session.plotManager.view.getOrthogonalMode()) {
          cumZRot += session.plotManager.view.cross
              * Canvas.zRotationalSensitivity;
          if (cumZRot > NINETY_DEGREES) {
            session.plotManager.view.rotate(NINETY_DEGREES, 1.0f); // about
            // z-axis
            // of view
            cumZRot = 0.0f;
          }
          else if (cumZRot < -NINETY_DEGREES) {
            session.plotManager.view.rotate(-NINETY_DEGREES, 1.0f); // about
            // z-axis
            // of view
            cumZRot = 0.0f;
          }
        }
        else {
          session.plotManager.view.rotate(session.plotManager.view.cross,
              zRotationalSensitivity); // about
          // z-axis
          // of view
        }
      }
      paintImmediately(new Rectangle(getWidth(), getHeight()));
      inertiaTimer.start();
    }
    else if (ev.getSource() == subsettingTimer) {
      SwingWorker<String, Object> worker = new SwingWorker<String, Object>() {
        @Override
        public String doInBackground() {
          try {
            subsettingTimer.stop();
            session.pointsetManager.setSubsettingVisible();
          }
          catch (Throwable ex) {
            session.errorSupport("While subsetting data:", ex,
                "subsetting_canvas_error");
          }
          finally {
            if (subsetting) {
              subsettingTimer.setInitialDelay(30000);
              subsettingTimer.start();
            }
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
      // a
      // final SwingWorker_DELETEME worker = new SwingWorker_DELETEME() {
      // @Override
      // public Object construct() {
      // return null;
      // }
      // };
      // worker.start();
    }
    session.plotManager.setSaved(false);
  }

  void updateInertiaTimer() {
    if (session.plotManager.view.getInertia()) {
      inertiaTimer.start();
    }
    else {
      inertiaTimer.stop();
    }
  }

  public void setCursor(final AppLafManager.CursorKind cursorKind) {
    if (cursorKind != AppLafManager.CursorKind.CURSOR_WAIT) {
      prevCursor = currentCursor;
    }
    currentCursor = cursorKind;
    super.setCursor(AppLafManager.getCursor(currentCursor));
    if (cursorKind != AppLafManager.CursorKind.CURSOR_WAIT) {
      nonWaitCursor = cursorKind;
    } // in case system wait overrides cursor
  }

  public void setCursorNonWait() {
    // Called when waiting cursor is done
    setCursor(nonWaitCursor);
  }

  void setCursorPrev() {
    setCursor(prevCursor);
  }

  @Override
  public boolean isFocusable() {
    // So KeyListener works
    return true;
  }

  /**
   * don't call this, let the session call it. Inefficient but convenient.
   */
  public void updateActions() {
    popupMenu.setVisible(false);
    // System.out.println("updateButtonStates(): canvas: "+this+" subject:
    // "+subject);
    if (session.plotManager.subject.isEmpty()) {
      session.actionManager.canvasSelectModeAction.setEnabled(false);
      session.actionManager.canvasDeselectModeAction.setEnabled(false);
      session.actionManager.canvasUnzoomAction.setEnabled(false);
      session.actionManager.canvasDragNoneAction.setEnabled(false);
      session.actionManager.canvasDragTranslateAction.setEnabled(false);
      session.actionManager.canvasDragSelectionAction.setEnabled(false);
      session.actionManager.canvasDragXYRotateAction.setEnabled(false);
      session.actionManager.canvasDragZRotateAction.setEnabled(false);
      session.actionManager.canvasZoomDragModeAction.setEnabled(false);
      session.actionManager.canvasDragTextAction.setEnabled(false);
      session.actionManager.canvasCycleAction.setEnabled(false);
      session.actionManager.canvasComposeAction.setEnabled(false);
      session.actionManager.canvasOrthogonalAction.setEnabled(false);
      session.actionManager.canvasNonOrthogonalAction.setEnabled(false);
      return;
    }
    session.actionManager.canvasZoomDragModeAction.setEnabled(true);
    session.actionManager.canvasUnzoomAction.setEnabled(true);
    session.actionManager.canvasComposeAction.setEnabled(true);
    if (session.plotManager.view.getOrthogonalMode()) {
      session.actionManager.canvasOrthogonalAction.setEnabled(false);
      if (session.plotManager.getDim() == 3) {
        session.actionManager.canvasNonOrthogonalAction.setEnabled(true);
      }
      else if (session.plotManager.getDim() == 2) {
        session.actionManager.canvasNonOrthogonalAction.setEnabled(false);
      }
    }
    else {
      session.actionManager.canvasOrthogonalAction.setEnabled(true);
      session.actionManager.canvasNonOrthogonalAction.setEnabled(false);
    }
    if (session.plotManager.view.zoomed) {
      session.actionManager.canvasZoomDragModeAction.setEnabled(true);
    }
    else {
      session.actionManager.canvasUnzoomAction.setEnabled(false);
    }
    if (session.plotManager.pointsetCount() < 2) {
      session.actionManager.canvasCycleAction.setEnabled(false);
    }
    else {
      session.actionManager.canvasCycleAction.setEnabled(true);
    }
    // put select mode buttons in an initial state
    session.actionManager.canvasSelectModeAction.setEnabled(true);
    session.actionManager.canvasDeselectModeAction.setEnabled(true);
    if (selectionMode == SELECT_SELECTION_MODE) {
      session.actionManager.canvasSelectModeAction.setEnabled(false);
    }
    else {
      session.actionManager.canvasDeselectModeAction.setEnabled(false);
    }
    // put all drag buttons and items in an initial state
    session.actionManager.canvasDragNoneAction.setEnabled(true);
    session.actionManager.canvasDragTranslateAction.setEnabled(true);
    session.actionManager.canvasDragSelectionAction.setEnabled(true);
    if (session.plotManager.getDim() == 3) {
      session.actionManager.canvasDragXYRotateAction.setEnabled(true);
    }
    else {
      session.actionManager.canvasDragXYRotateAction.setEnabled(false);
    }
    session.actionManager.canvasDragZRotateAction.setEnabled(true);
    session.actionManager.canvasZoomDragModeAction.setEnabled(true);
    if (session.plotManager.textCount() > 0) {
      session.actionManager.canvasDragTextAction.setEnabled(true);
    }
    else {
      session.actionManager.canvasDragTextAction.setEnabled(false);
    }
    // turn off button with current drag mode
    switch (dragMode) {
    case NO_DRAG_MODE:
      session.actionManager.canvasDragNoneAction.setEnabled(false);
      break;
    case SELECTION_DRAG_MODE:
      session.actionManager.canvasDragSelectionAction.setEnabled(false);
      break;
    case ROTATION_XY_DRAG_MODE:
      session.actionManager.canvasDragXYRotateAction.setEnabled(false);
      break;
    case ROTATION_Z_DRAG_MODE:
      session.actionManager.canvasDragZRotateAction.setEnabled(false);
      break;
    case TRANSLATION_DRAG_MODE:
      session.actionManager.canvasDragTranslateAction.setEnabled(false);
      break;
    case ZOOM_DRAG_MODE:
      session.actionManager.canvasZoomDragModeAction.setEnabled(false);
      break;
    case TEXT_DRAG_MODE:
      session.actionManager.canvasDragTextAction.setEnabled(false);
      break;
    }
  }

  public void setStale(final boolean state) {
    slate.stale = state;
  }

  boolean getStale() {
    return slate.stale;
  }

  int getXMin() {
    return xMin;
  }

  int getYMin() {
    return yMin;
  }

  int getXMax() {
    return xMax;
  }

  int getYMax() {
    return yMax;
  }

  int getW() {
    return w;
  }

  int getH() {
    return h;
  }

  public void smartCompose() {
    if (autoCompose) {
      // System.out.println("COMPOSING");
      if (session.plotManager.view != null) {
        session.plotManager.view.compose();
      }
    }
  }

  public BufferedImage getBufferedImage() {
    final int[] pixels = slate.dataBufferInt.getData();
    final BufferedImage bimg = new BufferedImage(w, h,
        BufferedImage.TYPE_INT_RGB);
    bimg.setRGB(0, 0, w, h, pixels, 0, w);
    return bimg;
  }

  public void resumeInertia() {
    if (session.plotManager.view.getInertia()) {
      inertiaTimer.start();
    }
  }

  public void pauseInertia() {
    if (session.plotManager.view.getInertia()) {
      inertiaTimer.stop();
    }
  }

  public CanvasToolbar getToolbar() {
    return toolbar;
  }

  public boolean isPreview() {
    return preview;
  }

  public void setPreview(boolean preview) {
    this.preview = preview;
  }

  private static final long serialVersionUID = 1L;
} // end of Canvas
