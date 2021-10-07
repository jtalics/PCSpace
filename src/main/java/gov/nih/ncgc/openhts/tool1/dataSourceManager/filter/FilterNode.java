/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.dataSourceManager.filter;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import gov.nih.ncgc.openhts.tool1.pointsetManager.ColumnHead;

/**
 * Purpose is to
 * 
 * @author talafousj
 */
public class FilterNode extends DefaultMutableTreeNode {
  public FilterNode() {
    super();
  }

  public FilterNode(Object userObject) {
    super(userObject);
  }

  @Override
  public Matcher getUserObject() {
    return (Matcher)super.getUserObject();
  }


  public boolean match(DataSourceFilter filter) {
    Object obj = getUserObject();
    if (obj != null) {
      if (!(obj instanceof Matcher)) {
        throw new RuntimeException("unexpected type");
      }
      Matcher matcher = (Matcher) obj;
      if (!matcher.match(filter)) {
        return false;
      }
    }

    Enumeration<FilterNode> filterNodes = children();
    while (filterNodes.hasMoreElements()) {
      FilterNode filterNode = filterNodes.nextElement();
      if (!filterNode.match(filter)) return false;
    }
    return true;
  }

  public void addColumnHeads(List<ColumnHead> columnHeads,
      Map<ColumnHead, Integer> columnHeadToIndex) {
    Object obj = getUserObject();
    if (obj != null) {
      if (!(obj instanceof Matcher)) {
        throw new RuntimeException("unexpected type");
      }
      Matcher matcher = (Matcher) obj;
      ColumnHead columnHead = matcher.getColumnHead();
      if (!columnHeads.contains(columnHead)) {
        columnHeadToIndex.put(columnHead, columnHeads.size());
        columnHeads.add(columnHead);
      }
    }
    Enumeration<FilterNode> filterNodes = children();
    while (filterNodes.hasMoreElements()) {
      filterNodes.nextElement().addColumnHeads(columnHeads, columnHeadToIndex);
    }
  }
  
}
