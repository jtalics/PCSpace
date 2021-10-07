/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.heatmap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;

class HeatmapCanvas extends JPanel implements MouseListener,
    MouseMotionListener, ChangeListener, ActionListener, FocusListener {
  private final Session session;
  private int startDragX, startDragY, movedX, movedY;
  private Point viewPosition;
  private int rowHeightMag, colWidthMag, lineThickMag;
  int rowCount = -1;
  int colCount = -1;
  float[][] red;
  float[][] green;
  float[][] blue;
  DecimalFormat df = new DecimalFormat("0.##E0");
  int selectedRows[] = new int[0];
  private boolean loaded = false;
  private boolean scrolling = false;
  private Pointset pointset;
  private final JWindow window;
  private final JLabel windowLabel;
  private Heatmap heatmap;

  public HeatmapCanvas(Session session, Heatmap heatmap) {
    this.session = session;
    this.heatmap = heatmap;
    rowHeightMag = heatmap.rowHeight;
    colWidthMag = heatmap.colWidth;
    lineThickMag = heatmap.lineThickness;
    addMouseListener(this);
    addMouseMotionListener(this);
//    Session.addFocusBorder(this, this);
    heatmap.previewTimer = new Timer(heatmap.previewDelay, this);
    window = new JWindow(session.frame);
    windowLabel = new JLabel();
    windowLabel.setBackground(Color.WHITE);
    windowLabel.setOpaque(true);
  }

  private int getMagW() {
    return (colWidthMag + lineThickMag) * colCount + lineThickMag;
  }

  private int getMagH() {
    int h = (rowHeightMag + lineThickMag) * rowCount + lineThickMag;
    return h;
  }

  @Override
  public void paint(Graphics g) {
    super.paintComponent(g); // clears
    if (red == null || green == null || blue == null) {
      //new RuntimeException("uninitialized rgb").printStackTrace();
      return;
    }
    // System.out.println("heatmap paint");
    int magWidth = getMagW(), magHeight = getMagH();
    if (!loaded) {
      g.setColor(null);
      g.fillRect(0, 0, magWidth, magHeight);
      return;
    }
    Rectangle rect = heatmap.scrollPane.getViewport().getViewRect();
    double dRow = (double) rowCount / (double) magHeight;
    int startRow = (int) (dRow * rect.y);
    int stopRow = (int) (dRow * (rect.y + rect.height));
    double dCol = (double) colCount / (double) magWidth;
    int startCol = (int) (dCol * rect.x);
    int stopCol = (int) (dCol * (rect.x + rect.width));
    if (heatmap.drawLine) {
      // Horizontal lines
      for (int row = startRow; row <= stopRow && row <= rowCount; row++) {
        g.setColor(heatmap.lineColor);
        g.fillRect(0, row * (rowHeightMag + lineThickMag), colCount
            * (colWidthMag + lineThickMag) + lineThickMag, lineThickMag);
      }
      // Vertical lines
      int col;
      for (col = startCol; col <= stopCol && col <= colCount; col++) {
        g.setColor(heatmap.lineColor);
        g.fillRect(col * (colWidthMag + lineThickMag), 0,
            lineThickMag, rowCount * (rowHeightMag + lineThickMag)
                + lineThickMag);
      }
    }
    // Cells
    for (int row = startRow; row <= stopRow && row < rowCount; row++) {
      for (int col = startCol; col <= stopCol && col < colCount; col++) {
        if (Float.isNaN(red[row][col]) || Float.isNaN(green[row][col])
            || Float.isNaN(blue[row][col])) {
          continue;
        }
        g.setColor(new Color(red[row][col], green[row][col], blue[row][col]));
        g.fillRect(col * (colWidthMag + lineThickMag) + lineThickMag, row
            * (rowHeightMag + lineThickMag) + lineThickMag,
            colWidthMag /*- lineThickMag*/, rowHeightMag /*- lineThickMag*/);
      }
    }
    g.setColor(Color.YELLOW);
    int halfThick = lineThickMag / 2;
    for (int i = 0; i < selectedRows.length; i++) {
      int row = selectedRows[i];
      if (row >= startRow && row <= stopRow) {
        g.drawRect(halfThick, row * (rowHeightMag + lineThickMag) + halfThick, colCount
            * (colWidthMag + lineThickMag), rowHeightMag +lineThickMag);
      }
    }
  }

  @Override
  public Dimension getPreferredSize() {
    if (!loaded) {
      return super.getPreferredSize();
    }
    return new Dimension(getMagW(), getMagH());
  }

  @Override
  public boolean isFocusable() {
    return true;
  }

  @Override
	public void mouseClicked(MouseEvent ev) {
    if (!loaded) {
      return;
    }
    int row = getRow(ev.getPoint().y);
    int col = getCol(ev.getPoint().x);
    if (row >= rowCount || col >= colCount) {
      return;
    }
    session.heatmap.cellSelected(row, col, ev);
  }

  private int getRow(int y) {
    int row = (int) ((float) y / (float) getMagH() * rowCount);
    return row;
  }

  private int getCol(int x) {
    return (int) ((float) x / (float) getMagW() * colCount);
  }

  @Override
	public void mouseEntered(MouseEvent e) {
    // nop
  }

  @Override
	public void mouseExited(MouseEvent e) {
    window.setVisible(false);
  }

  @Override
	public void mousePressed(MouseEvent e) {
    window.setVisible(false);
    grabFocus();
    startDragX = e.getX();
    startDragY = e.getY();
    viewPosition = heatmap.scrollPane.getViewport().getViewPosition();
    // System.out.println("Start drag point; " + viewPosition);
  }

  @Override
	public void mouseReleased(MouseEvent e) {
    window.setVisible(false);
    // if (newViewPosition != null) {
    // System.out.println("Start drag newPoint; " + newViewPosition);
    // }
    // newViewPosition = null;
  }

  @Override
	public void mouseDragged(MouseEvent e) {
    window.setVisible(false);
    int deltaX = e.getX() - startDragX;
    int deltaY = e.getY() - startDragY;
    if (e.isShiftDown()) {
    }
    else {
      if (scrolling) {
        return;
      }
      int newX = viewPosition.x - deltaX;
      int maxX = getWidth() - heatmap.scrollPane.getViewport().getWidth();
      if (newX < 0) {
        newX = 0;
      }
      else if (newX > maxX) {
        newX = maxX;
      }
      int newY = viewPosition.y - deltaY;
      int maxY = getHeight() - heatmap.scrollPane.getViewport().getHeight();
      if (newY < 0) {
        newY = 0;
      }
      else if (newY > maxY) {
        newY = maxY;
      }
      Point newViewPosition = new Point(new Point(newX, newY));
      scrolling = true;
      heatmap.scrollPane.getViewport().setViewPosition(newViewPosition);
      scrolling = false;
      // startDragX=e.getX();
      // startDragY=e.getY();
    }
  }

  @Override
	public void mouseMoved(MouseEvent ev) {
    window.setVisible(false);
    if (heatmap.preview) {
      heatmap.previewTimer.restart();
    }
    movedX = ev.getX();
    movedY = ev.getY();
    ev.consume();
  }

  @Override
	public void stateChanged(ChangeEvent e) {
    repaint();
  }

  public void zoom() {
    heatmap.magnification += heatmap.magStep;
    session.statusPanel.log2("magnification = "
        + df.format(heatmap.magnification));
    heatmap.scrollPane.revalidate();
    applyMag();
    revalidate();
    repaint();
    heatmap.fireHeatmapChanged();
  }

  public void unzoom() {
    double tiny=heatmap.magStep;
    while (heatmap.magnification-tiny < tiny) {
      tiny = tiny*tiny;
    }
    heatmap.magnification -= tiny;
    session.statusPanel.log2("magnification = "
        + df.format(heatmap.magnification));
    applyMag();
    revalidate();
    repaint();
    heatmap.fireHeatmapChanged();
  }

  protected void applyMag() {
    lineThickMag = (int) (heatmap.lineThickness * heatmap.magnification);
    if (heatmap.lineThickness > 0 && lineThickMag < 1) {
      lineThickMag = 1;
    }
    colWidthMag = (int) (heatmap.colWidth * heatmap.magnification);
    if (colWidthMag < 1) {
      colWidthMag = 1;
    }
    rowHeightMag = (int) (heatmap.rowHeight * heatmap.magnification);
    if (rowHeightMag < 1) {
      rowHeightMag = 1;
    }
  }

  public void panDown() {
    // int maxX = getWidth() - scrollPane.getViewport().getWidth();
    int maxY = getHeight() - heatmap.scrollPane.getViewport().getHeight();
    int h = (int) (heatmap.scrollPane.getViewport().getHeight() * heatmap.panStep);
    Point p = heatmap.scrollPane.getViewport().getViewPosition();
    int newY = p.y + h;
    if (newY > maxY) {
      newY = maxY;
    }
    Point newP = new Point(new Point(p.x, newY));
    heatmap.scrollPane.getViewport().setViewPosition(newP);
    repaint();
  }

  public void panUp() {
    Point p = heatmap.scrollPane.getViewport().getViewPosition();
    int h = (int) (heatmap.scrollPane.getViewport().getHeight() * heatmap.panStep);
    int newY = p.y - h;
    if (newY < 0) {
      newY = 0;
    }
    Point newP = new Point(new Point(p.x, newY));
    heatmap.scrollPane.getViewport().setViewPosition(newP);
    repaint();
  }

  public void panRight() {
    int maxX = getWidth() - heatmap.scrollPane.getViewport().getWidth();
    int w = (int) (heatmap.scrollPane.getViewport().getWidth() * heatmap.panStep);
    Point p = heatmap.scrollPane.getViewport().getViewPosition();
    int newX = p.x + w;
    if (newX > maxX) {
      newX = maxX;
    }
    Point newP = new Point(new Point(newX, p.y));
    heatmap.scrollPane.getViewport().setViewPosition(newP);
    repaint();
  }

  public void panLeft() {
    Point p = heatmap.scrollPane.getViewport().getViewPosition();
    int w = (int) (heatmap.scrollPane.getViewport().getWidth() * heatmap.panStep);
    int newX = p.x - w;
    if (newX < 0) {
      newX = 0;
    }
    Point newP = new Point(new Point(newX, p.x));
    heatmap.scrollPane.getViewport().setViewPosition(newP);
    repaint();
  }

  void initOnPointset(Pointset pointset) {
    if (pointset == null) {
      red = blue = green = null;
      rowCount = -1;
      colCount = -1;
      loaded = false;
      selectedRows = new int[0];
    }
    else {
      rowCount = pointset.getPointCount();
      colCount = pointset.getDimensionality();
      loaded = true;
      selectedRows = new int[0];
      this.pointset = pointset;
    }
  }

  @Override
	public void actionPerformed(ActionEvent e) {
    heatmap.previewTimer.stop();
    if (!hasFocus()) {
      // return;
    }
    int row = getRow(movedY);
    if (row < 0 || row >= rowCount) {
      return;
    }
    int col = getCol(movedX);
    if (col < 0 || col >= colCount) {
      return;
    }
    String text = "\"" + pointset.getObjNameAt(row) + "\" "
      + pointset.getCurrentAcMap().getColumnHeadAt(col).getName() + " = "
      + pointset.getUserValue(row, col);
    windowLabel.setText(text);
    windowLabel.setBorder(BorderFactory.createLineBorder(pointset.getColor()));
    window.add(windowLabel);
    window.setLocation(getLocationOnScreen().x + movedX + 20,
        getLocationOnScreen().y + movedY + 20);
    window.pack();
    window.setVisible(true);
    session.statusPanel.log2(movedX + "," + movedY);
  }

  @Override
	public void focusGained(FocusEvent e) {
    window.setVisible(false);
  }

  @Override
	public void focusLost(FocusEvent e) {
    window.setVisible(false);
  }
  private static final long serialVersionUID = 1L;

}
