/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.dataSourceManager.filter;

import java.awt.BorderLayout;
import java.util.Map;
import javax.swing.JPanel;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.Preview;
import gov.nih.ncgc.openhts.tool1.pointsetManager.ColumnHead;

/** Purpose is to ...
 * @author talafousj
 *
 */
public abstract class MatcherViewlet extends JPanel {
  
  protected Map<ColumnHead, Preview> columnHeadToPreview;

  public MatcherViewlet(Map<ColumnHead, Preview> columnHeadToPreview) {
    this.columnHeadToPreview = columnHeadToPreview;
    setLayout(new BorderLayout());
  }
  abstract void setPreview(ColumnHead columnHead);
  abstract Matcher getMatcher();
  abstract void setMatcher(Matcher matcher);
}
