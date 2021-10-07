/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.dataSourceManager;

import java.util.EventObject;

/** Purpose is to ...
 * @author talafousj
 */
public class DataSourceManagerEvent extends EventObject {
  public final Object member;
  public final Kind kind;

  public DataSourceManagerEvent(Object source, Object member, Kind kind) {
    super(source);
    this.member = member;
    this.kind = kind;
  }

  public enum Kind {
    CHANGED, // most vague change possible
    MEMBER_ADDED, 
    MEMBER_CHANGED, // use more specific kind if available
    MEMBER_REMOVED
  };

  private static final long serialVersionUID = 1L;
}
