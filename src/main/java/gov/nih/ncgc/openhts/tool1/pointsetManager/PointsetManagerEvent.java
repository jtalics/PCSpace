package gov.nih.ncgc.openhts.tool1.pointsetManager;

import java.util.EventObject;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class PointsetManagerEvent extends EventObject {
  public final Object member;
  public final Kind kind;

  public PointsetManagerEvent(Object source, Object member, Kind kind) {
    super(source);
    this.member = member;
    this.kind = kind;
  }

  public enum Kind {
    MANAGER_CHANGED, // most vague change possible
    MEMBER_CHANGED, // use more specific kind if available
    MEMBER_LOADED,
    MEMBER_ADDED,
    POINTSET_REMOVED, 
    MEMBERS_SELECTION, // user (de)selected
    MEMBER_VISABILITY, // hidden changed
    POINT_VIZ, 
    AC_MAP
  };

  private static final long serialVersionUID = 1L;
}
