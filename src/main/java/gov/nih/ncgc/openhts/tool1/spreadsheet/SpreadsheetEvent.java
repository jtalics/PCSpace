package gov.nih.ncgc.openhts.tool1.spreadsheet;

import java.util.EventObject;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class SpreadsheetEvent extends EventObject {
  public final Object member;
  public final Kind kind;

  public SpreadsheetEvent(Object source, Object member, Kind kind) {
    super(source);
    this.member = member;
    this.kind = kind;
  }

  public enum Kind {
    MANAGER_CHANGED, // most vague change possible
    MEMBER_CHANGED, // use more specific kind if available
    MEMBER_LOADED,
    MEMBER_ADDED,
    MEMBER_REMOVED, 
    MEMBERS_SELECTION, // user (de)selected
    MEMBER_VISABILITY, // hidden changed
    POINT_SELECTION,
    LEAD_SELECTION
  };

  private static final long serialVersionUID = 1L;
}
