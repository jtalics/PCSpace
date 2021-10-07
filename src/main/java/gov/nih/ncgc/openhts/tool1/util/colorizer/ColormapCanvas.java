package gov.nih.ncgc.openhts.tool1.util.colorizer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.List;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class ColormapCanvas extends Component {

  private final List<Waypoint> waypoints;

  public ColormapCanvas(List<Waypoint> waypoints) {
    if (waypoints == null || waypoints.size() < 2) {
      throw new RuntimeException();
    }
    this.waypoints = waypoints;
  }

  @Override
  public void paint(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    int h = this.getHeight();
    int w = this.getWidth();
    int startCol, stopCol;
    Iterator<Waypoint> iterator = waypoints.iterator();
    if (iterator.hasNext()) {
      Waypoint second = iterator.next();
      startCol = Math.round((second.place / 100.0f) * w);
      // draw one tic
      g2d.setColor(new Color(1f - second.red, 1f - second.green,
          1f - second.blue));
      g2d.drawLine(startCol, 0, startCol, ColorizerViewlet.gap);
      Waypoint first = null;
      while (iterator.hasNext()) {
        first = second;
        second = iterator.next();
        startCol = Math.round((first.place / 100.0f) * w);
        stopCol = Math.round((second.place / 100.0f) * w);
        for (int col = startCol; col < stopCol; col++) {
          float fraction = (float) (col - startCol)
              / (float) (stopCol - startCol);
          float red = fraction * (second.red - first.red)
              + first.red;
          float green = fraction * (second.green - first.green)
              + first.green;
          float blue = fraction * (second.blue - first.blue)
              + first.blue;
          g2d.setColor(new Color(red, green, blue));
          g2d.drawLine(col, 0, col, h);
        }
        // Draw tics
        g2d.setColor(new Color(1f - first.red, 1f - first.green,
            1f - first.blue));
        g2d.drawLine(startCol, 0, startCol, ColorizerViewlet.gap);
        g2d.setColor(new Color(1f - second.red, 1f - second.green,
            1f - second.blue));
        g2d.drawLine(stopCol, 0, stopCol, ColorizerViewlet.gap);
      }
    }
  }

  private static final long serialVersionUID = 1L;
}