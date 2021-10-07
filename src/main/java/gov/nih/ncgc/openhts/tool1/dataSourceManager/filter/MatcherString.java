/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.dataSourceManager.filter;

import java.util.regex.Pattern;
import gov.nih.ncgc.openhts.tool1.pointsetManager.ColumnHead;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class MatcherString extends Matcher{
  
  Pattern pattern;
  java.util.regex.Matcher matcher;
  
  public MatcherString(ColumnHead columnHead, String regexp) {
    this.columnHead = columnHead;
    pattern = Pattern.compile(regexp);
    
  }

  @Override
  public boolean match(DataSourceFilter filter) {
    return pattern.matcher(filter.getValueFor(columnHead)).matches();
  }
  
  @Override
  public String toString() {
    String columnHeadName = "<no columnhead defined>";
    if (columnHead != null) {
      columnHeadName = columnHead.getName();
    }
    String regexp = "<no pattern defined>";
    if (pattern != null) {
      regexp=pattern.pattern();
    }
    
    return "[MatcherString: "+columnHeadName+"; "+regexp+"]";
  }
  
  public String getPattern() {
    return pattern.pattern();
  }
}
