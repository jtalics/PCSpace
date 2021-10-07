/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.dataSourceManager.filter;

import gov.nih.ncgc.openhts.tool1.pointsetManager.ColumnHead;

/** Purpose is to ...
 * @author talafousj
 *
 */
public abstract class Matcher {
  protected ColumnHead columnHead;

  public ColumnHead getColumnHead() {
    return columnHead;
  }

  public void setColumnHead(ColumnHead columnHead) {
    this.columnHead = columnHead;
  }
  
  public abstract boolean match(DataSourceFilter filter);

}
