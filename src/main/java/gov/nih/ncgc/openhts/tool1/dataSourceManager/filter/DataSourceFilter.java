/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.dataSourceManager.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.pointsetManager.ColumnHead;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class DataSourceFilter {
  private FilterNode rootFilterNode;
  private String[] values;
  private ColumnHead[] columnHeads;
  private Map<ColumnHead,Integer> columnHeadToIndex = new HashMap<ColumnHead,Integer>();
  
  public DataSourceFilter(Session session, FilterNode filterNode) {
    rootFilterNode = filterNode;
    final List<ColumnHead> columnHeads = new ArrayList<ColumnHead>();
    rootFilterNode.addColumnHeads(columnHeads,columnHeadToIndex);
    this.columnHeads = columnHeads.toArray(new ColumnHead[columnHeads.size()]);
    values = new String[this.columnHeads.length];
  }

  public ColumnHead getColumnHeadAt(int index) {
    return columnHeads[index];
  }

  public FilterNode getRootFilterNode() {
    return rootFilterNode;
  }

  public void setRootFilterNode(FilterNode filterNode) {
    rootFilterNode = filterNode;
  }

  public void setDescriptorValueAt(String value, int index) {
    values[index] = value; 
  }

  public String getDescriptorValueAt(int index) {
    return values[index];
  }
  
  public boolean isMatch() {
    return rootFilterNode.match(this);
  }

  public int getColumnHeadCount() {
    
    return columnHeads.length;
  }

  public String getValueFor(ColumnHead columnHead) {
    return values[columnHeadToIndex.get(columnHead).intValue()];
  }
  
}
