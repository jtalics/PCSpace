/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.util.colorizer;

import java.awt.Color;

public class Waypoint implements Comparable<Waypoint> {
  public int place;
  public float red=0.0f, green=0.0f,blue=0.0f;

  public Waypoint(int place, Color color) {
    this.place = place;
    if (color != null) {
    float[] rgb = color.getRGBColorComponents(null);
    red = rgb[0];
    green = rgb[1];
    blue = rgb[2];
    }
  }

  @Override
	public int compareTo(Waypoint waypoint) {
    Waypoint p = waypoint;
    return place - p.place;
  }
}