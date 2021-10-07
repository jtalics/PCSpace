package gov.nih.ncgc.openhts.tool1.heatmap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetManagerEvent;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetManagerListener;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Stats;
import gov.nih.ncgc.openhts.tool1.spreadsheet.PointsetTable;
import gov.nih.ncgc.openhts.tool1.spreadsheet.SpreadsheetEvent;
import gov.nih.ncgc.openhts.tool1.spreadsheet.SpreadsheetListener;
import gov.nih.ncgc.openhts.tool1.util.ProgressBar;
import gov.nih.ncgc.openhts.tool1.util.ProgressListener;
import gov.nih.ncgc.openhts.tool1.util.colorizer.Colorizer;
import gov.nih.ncgc.openhts.tool1.util.colorizer.ColorizerChangeEvent;
import gov.nih.ncgc.openhts.tool1.util.colorizer.ColorizerViewlet;

public class Heatmap extends JPanel implements PointsetManagerListener,
    SpreadsheetListener, ChangeListener, ProgressListener {
  Pointset pointset;
  private Colorizer colorizer;
  protected ColorizerViewlet colorizerViewlet;
  private final Session session;
  protected HeatmapCanvas heatmapCanvas;
  protected boolean pixelUnitsColumnWidth = true;
  protected boolean pixelUnitsRowHeight = true;
  protected boolean pixelUnitsLineThickness = true;
  protected Color lineColor = Color.darkGray;
  protected boolean drawLine = true;
  protected int rowHeight = 20, colWidth = 20;
  protected int lineThickness = 1;
  protected double magnification = 1;
  protected double magStep = 0.1;
  protected double panStep = 0.9;
  protected boolean preview = true;
  protected Timer previewTimer;
  protected final List<HeatmapListener> listeners = new ArrayList<HeatmapListener>();
  protected JScrollPane scrollPane;
  protected int previewDelay = 250;
  boolean colorizeByColumn = true;
  private ProgressBar progressBar;

  public Heatmap(Session session) {
    this.session = session;
    setLayout(new BorderLayout());
    heatmapCanvas = new HeatmapCanvas(session, this);
    scrollPane = new JScrollPane(heatmapCanvas);
    add(scrollPane, BorderLayout.CENTER);
    scrollPane.getViewport().addChangeListener(heatmapCanvas);
    // TODO: move colorizer to HeatmapModifyDialog
    colorizer = new Colorizer();
    colorizer.addChangeListener(this);
    colorizerViewlet = new ColorizerViewlet(colorizer);
    colorizer.addChangeListener(colorizerViewlet);
    colorizer.addChangeListener(this);
    colorizer.addProgressListener(this);
    Session.addFocusBorder(heatmapCanvas, this);
    progressBar = new ProgressBar();
    progressBar.setVisible(false);
    add(progressBar, BorderLayout.SOUTH);
  }

  @Override
	public void pointsetManagerChanged(PointsetManagerEvent ev) {
    switch (ev.kind) {
    case MEMBER_LOADED:
      // nop, rebuild after Spreadsheet loads, need its userToTable ordering
      break;
    case MEMBERS_SELECTION:
      if (ev.member instanceof Pointset[]) {
        Pointset[] pointsets = (Pointset[]) ev.member;
        if (pointsets.length > 0) {
          if (pointset != pointsets[0]) { // new selection?
            pointset = pointsets[0];
            heatmapCanvas.initOnPointset(pointsets[0]);
            if (!pointset.isLoading()) {
              initializeColorizer();
              colorize();
              drawHeatmap();
            }
          }
        }
      }
      break;
    case POINTSET_REMOVED:
      heatmapCanvas.initOnPointset(null);
      drawHeatmap(); // blank
      break;
    case POINT_VIZ:
      if (pointset == ev.member) {
        colorize();
        drawHeatmap();
      }
      break;
    case MEMBER_ADDED:
    case MEMBER_VISABILITY:
    case AC_MAP:
    case MANAGER_CHANGED:
    case MEMBER_CHANGED:
      break;
    }
  }

  public void rebuild() {
        try {
          progressBar.setVisible(true);
          revalidate();
          // TODO: repaint();
          heatmapCanvas.initOnPointset(pointset);
          //heatmapCanvas.repaint(); // gives intermediate results
          initializeColorizer();
          //heatmapCanvas.repaint();
          colorize();
          progressBar.setVisible(false);
          revalidate();
          repaint();// does what drawHeatmap() does;

        }
        catch (Throwable t) {
          session.errorSupport("Cannot draw heat map because ", t, "TODO");
        }

  }

  void drawHeatmap() {
    // switch (orientation) {
    // case JSplitPane.VERTICAL_SPLIT:
    // break;
    // case JSplitPane.HORIZONTAL_SPLIT:
    // // heatmapCanvas.setMinimumSize(new Dimension(pointset.getPointCount(),
    // // pointset.getDimensionality()));
    // break;
    // default:
    // break;
    // }
    System.out.println("drawing heat map");
    heatmapCanvas.revalidate();
    heatmapCanvas.repaint();
  }

  @Override
	public void progressChanged(Object subject, String string, int min,
      int value, int max) {
    progressBar.progressChanged(subject, string, min, value, max);
  }

  @Override
	public void stateChanged(ChangeEvent event) {
    // If the colorizer functions change, we need to supply the stats
    // as needed.
    if (event instanceof ColorizerChangeEvent) {
      ColorizerChangeEvent ev = (ColorizerChangeEvent) event;
      if (ev.getSource() != colorizer) {
        throw new RuntimeException("interested in colorizer only");
      }
      boolean b;
      switch (ev.kind) {
      case PREPROCESSOR_FUNCTION:
        initializeColorizer();
        break;
      case COLORING_FUNCTION:
        if (colorizeByColumn) {
          float[][] values = getValuesArray2();
          colorizer.setColumnCount(pointset.getDimensionality());
          Stats[] stats = new Stats[pointset.getDimensionality()];
          for (int col = 0; col < pointset.getDimensionality(); col++) {
            float[] colValues = new float[pointset.getPointCount()];
            for (int row = 0; row < pointset.getPointCount(); row++) {
              colValues[row] = values[row][col];
            }
            stats[col] = new Stats(colValues);
          }
          colorizer.setColoringFunctionStats(stats);
        }
        else {
          float values[];
          Stats stats;
          // do a partial init
          values = getValuesArray();
          colorizer.getPreprocessorFunction().apply(values);
          stats = new Stats(values);
          colorizer.setColoringFunctionStats(stats);
        }
        break;
      case PREPROCESSOR_FUNCTION_STATS:
      case COLORING_FUNCTION_STATS:
      case COLOR_MAP:
        break;
      }
    }
  }

  void initializeColorizer() {
    // Perform a full initialization of colorizer based on the
    // currently selected Pointset
    if (colorizeByColumn) {
      float[][] values = getValuesArray2();
      colorizer.setColumnCount(pointset.getDimensionality());
      colorizer.setPreprocessorFunctionStats(pointset.getStats());
      int columnCount = pointset.getDimensionality();
      for (int col = 0; col < columnCount; col++) {
        float[] colValues = new float[pointset.getPointCount()];
        for (int row = 0; row < pointset.getPointCount(); row++) {
          colValues[row] = values[row][col];
        }
        colorizer.getPreprocessorFunction().apply(colValues);
        for (int row = 0; row < pointset.getPointCount(); row++) {
          values[row][col] = colValues[row];
        }
      }
      Stats[] stats = new Stats[pointset.getDimensionality()];
      for (int col = 0; col < pointset.getDimensionality(); col++) {
        float[] colValues = new float[pointset.getPointCount()];
        for (int row = 0; row < pointset.getPointCount(); row++) {
          colValues[row] = values[row][col];
        }
        stats[col] = new Stats(colValues);
      }
      colorizer.setColoringFunctionStats(stats);System.out.println("C");

    }
    else {
      Stats stats;
      float values[];
      values = getValuesArray();
      stats = new Stats(values);
      colorizer.setPreprocessorFunctionStats(stats);
      colorizer.getPreprocessorFunction().apply(values);
      stats = new Stats(values);
      colorizer.setColoringFunctionStats(stats);
    }
  }

  private float[] getValuesArray() {
    boolean b = pointset.isVisible();
    session.pointsetManager.setVisible(pointset, false);
    // pointset.moveFromSubjectSpace();
    float[] values = Stats.collectIntoArray(pointset
        .getFilteredPointsInUserSpace());
    // pointset.moveToSubjectSpace();
    session.pointsetManager.setVisible(pointset, b);
    return values;
  }

  private float[][] getValuesArray2() {
    boolean b = pointset.isVisible();
    session.pointsetManager.setVisible(pointset, false);
    // pointset.moveFromSubjectSpace();
    float[][] values = pointset.getFilteredPointsInUserSpace();
    // pointset.moveToSubjectSpace();
    session.pointsetManager.setVisible(pointset, b);
    return values;
  }

//  private boolean isPointsetSelected(Pointset pointset) {
//    return (this.pointset == pointset);
//  }

  void colorize() {
    // ASSUME: colorizer was initialized properly
    int rows = pointset.getPointCount();
    int cols = pointset.getDimensionality();
    heatmapCanvas.red = new float[rows][cols];
    heatmapCanvas.green = new float[rows][cols];
    heatmapCanvas.blue = new float[rows][cols];
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        heatmapCanvas.red[row][col] = heatmapCanvas.green[row][col] = heatmapCanvas.blue[row][col] = Float.NaN;
      }
      progressChanged(this, "init colorizer", 0, row, rows);
    }
    float[][] filteredPoints = pointset.getFilteredPointsInUserSpace();
    // if (colorizePerColumnHead) then colorize will do it
    colorizer.colorize(filteredPoints, heatmapCanvas.red, heatmapCanvas.green,
        heatmapCanvas.blue);
  }

  public void zoom() {
    heatmapCanvas.zoom();
  }

  @Override
  public boolean isFocusable() {
    return true;
  }

  public void unzoom() {
    heatmapCanvas.unzoom();
  }

  public void panDown() {
    heatmapCanvas.panDown();
  }

  public void panUp() {
    heatmapCanvas.panUp();
  }

  public void panRight() {
    heatmapCanvas.panRight();
  }

  public void panLeft() {
    heatmapCanvas.panLeft();
  }

  public void cellSelected(int row, int col, MouseEvent ev) {
    PointsetTable pointsetTable = session.spreadsheet
        .getPointsetTable(pointset);
    pointsetTable.setSelectedFor(new int[] { row }, true, ev);
    pointsetTable.scrollToPoint(row, col);
  }

  public void addHeatmapListener(HeatmapListener listener) {
    listeners.add(listener);
  }

  public void removeXListener(HeatmapListener listener) {
    listeners.remove(listener);
  }

  void fireHeatmapChanged() {
    for (HeatmapListener listener : listeners) {
      listener.heatmapChanged(new HeatmapEvent(this, null,
          HeatmapEvent.Kind.MANAGER_CHANGED));
    }
  }

  public JComponent getCanvas() {
    return heatmapCanvas;
  }

  @Override
	public void spreadsheetChanged(SpreadsheetEvent ev) {
    PointsetTable pointsetTable;
    Pointset pointset2;
    switch (ev.kind) {
    case MEMBER_LOADED:
      pointsetTable = (PointsetTable)ev.member;
      pointset2 = pointsetTable.getPointset();
        if (pointset2 == pointset) {
//        if (isPointsetSelected((Pointset) ev.member) && !pointset.isLoading()) {
          rebuild();
        }
      break;
    case MEMBERS_SELECTION:
      break;
    case LEAD_SELECTION:
      break;
    case POINT_SELECTION:
//    System.out.println("selection changed");
    pointsetTable = (PointsetTable)ev.member;
    heatmapCanvas.selectedRows = pointsetTable.getSelectedPoints();
    heatmapCanvas.repaint();
      break;
    case MANAGER_CHANGED:
    case MEMBER_CHANGED:
    case MEMBER_ADDED:
    case MEMBER_REMOVED: 
    case MEMBER_VISABILITY:
      throw new RuntimeException("TODO");
    }
  }

  private static final long serialVersionUID = 1L;
}
// end of file
