package gov.nih.ncgc.openhts.tool1.util.colorizer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Stats;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
class FunctionCanvas extends Component {
  private final Colorizer colorizer;

  FunctionCanvas(Colorizer colorizer) {
    this.colorizer = colorizer;
  }

  @Override
  public void paint(Graphics g) {
    Function coloringFunction = colorizer.getColoringFunction();
    Stats stats = coloringFunction.getStats();
    if (stats == null) {return;}
    float min = stats.getMin(), max = stats.getMax();
    Graphics2D g2d = (Graphics2D) g;
    int fontHeight = 10;
    // Height of this component as set by LayoutManager
    int canvasHeight = getHeight();
    int canvasWidth = getWidth();
    int xAxisRows = canvasHeight - 2 * ColorizerViewlet.gap;
    int yAxisCols = canvasWidth - 2 * ColorizerViewlet.gap;
    float frac = (max - min) / xAxisRows;
    // Draw min max
    g2d.setColor(Color.WHITE);
    g2d.drawString(Float.toString(max), ColorizerViewlet.gap + 1, fontHeight);
    g2d.drawString(Float.toString(min), ColorizerViewlet.gap + 1, canvasHeight
        - fontHeight);
    // Draw axes
    g2d.drawLine(ColorizerViewlet.gap, ColorizerViewlet.gap,
        ColorizerViewlet.gap, canvasHeight - ColorizerViewlet.gap);
    g2d
        .drawLine(ColorizerViewlet.gap, canvasHeight - ColorizerViewlet.gap,
            canvasWidth - ColorizerViewlet.gap, canvasHeight
                - ColorizerViewlet.gap);
    // Set color for plot
    g2d.setColor(Color.RED);
    int row, col;
    // Calc horiz y-pixel for each pixel on the vertical x-axis
    // NOTE: X is vertical, Y is horizontal (sorry)
    float[] x = new float[xAxisRows];
    for (int xRow = 0; xRow < xAxisRows; xRow++) {
      x[xRow] = xRow * frac + min;
    }
    coloringFunction.apply(x);
    for (int xRow = 0; xRow < xAxisRows; xRow++) {
      row = ColorizerViewlet.gap + xAxisRows - xRow;
      col = (int) (ColorizerViewlet.gap + x[xRow] * yAxisCols);
      g2d.drawLine(col, row, col, row);
    }
  }

  private static final long serialVersionUID = 1L;
}
