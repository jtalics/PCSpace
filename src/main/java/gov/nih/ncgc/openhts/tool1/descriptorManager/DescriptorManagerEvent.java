/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.descriptorManager;

import java.util.EventObject;

/** Purpose is to ...
 * @author talafousj
 */
public class DescriptorManagerEvent extends EventObject {

  public final Object member;
  public final Kind kind;

  public DescriptorManagerEvent(Object source, Object member, Kind kind) {
    super(source);
    this.member = member;
    this.kind = kind;
  }

  public enum Kind {
    CHANGED, // most vague change possible
    MEMBER_CHANGED, // use more specific kind if available
    MEMBER_ADDED, 
    MEMBER_REMOVED, 
    MEMBER_REPLACED,
    MEMBER_LOADED,
    MEMBERS_SELECTION // user (de)selected
  };

  private static final long serialVersionUID = 1L;
}
