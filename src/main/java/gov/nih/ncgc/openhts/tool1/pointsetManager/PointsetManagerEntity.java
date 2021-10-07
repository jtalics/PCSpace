/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.pointsetManager;

import java.awt.Color;
import gov.nih.ncgc.openhts.tool1.StringAndNumberFloat;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public interface PointsetManagerEntity {
  public abstract String getName();

  public abstract Color getColor();

  public abstract boolean isVisible();

  public abstract AxesColumnHeadsMapping getAcMap();

  public abstract int getLowLimitPercentageFor(ColumnHead columnHead);

  public abstract int getHighLimitPercentageFor(ColumnHead columnHead);

  public abstract boolean getShowInsideLimitFor(ColumnHead columnHead);

  public abstract StringAndNumberFloat getStringForNumberFloat(ColumnHead columnHead, float value, boolean lessThan, float closest);

}
