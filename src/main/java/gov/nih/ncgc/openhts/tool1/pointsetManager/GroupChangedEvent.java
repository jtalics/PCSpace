package gov.nih.ncgc.openhts.tool1.pointsetManager;

import java.util.EventObject;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class GroupChangedEvent extends EventObject {
  
  public final int eventCode;
  public final Object member;
  
  public GroupChangedEvent(Object source, Object member, int eventCode) {
    super(source);
    this.member = member;
    this.eventCode = eventCode;
  }
  
  public static final int NONE=0;
  public static final int ADDED=1;
  public static final int REMOVED=2;

  public int getEventCode() {
    return eventCode ;
  }
  private static final long serialVersionUID = 1L;  
}
