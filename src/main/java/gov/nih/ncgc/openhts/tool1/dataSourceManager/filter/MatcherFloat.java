/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.dataSourceManager.filter;

import gov.nih.ncgc.openhts.tool1.pointsetManager.ColumnHead;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class MatcherFloat extends Matcher {

  float lo,hi;
  boolean between;
  
  public MatcherFloat(ColumnHead columnHead, float lo, float hi, boolean between) {
    this.columnHead = columnHead;
    this.lo=lo;
    this.hi=hi;
    this.between = between;
  }

  @Override
  public boolean match(DataSourceFilter filter) {
    float f=Float.parseFloat(filter.getValueFor(columnHead));
    if (between) {
      if (f<lo) {return false;}
      if (f>hi) {return false;}
      return true;
    }
    if (f>lo) {return false;}
    if (f<hi) {return false;}
    return true;
  }

  public boolean isBetween() {
    return between;
  }

  public void setBetween(boolean between) {
    this.between = between;
  }

  public float getHi() {
    return hi;
  }

  public void setHi(float hi) {
    this.hi = hi;
  }

  public float getLo() {
    return lo;
  }

  public void setLo(float lo) {
    this.lo = lo;
  }
  
  @Override
  public String toString() {
    return "[MatcherFloat: "+columnHead.getName()+"; "+lo+"; "+hi+"; "+between+"]";
  }
}
