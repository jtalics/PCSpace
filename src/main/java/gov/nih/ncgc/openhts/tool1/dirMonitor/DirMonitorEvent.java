package gov.nih.ncgc.openhts.tool1.dirMonitor;

import java.util.EventObject;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class DirMonitorEvent extends EventObject {
  public final Object member;
  public final Kind kind;

  public DirMonitorEvent(Object source, Object member, Kind kind) {
    super(source);
    this.member = member;
    this.kind = kind;
  }

  public enum Kind {
    MANAGER_CHANGED, // most vague change possible
    FILE_ADDED, 
    FILE_CHANGED, 
    FILE_REMOVED,
    DIR_REMOVED,
    TERMINATED

  };

  private static final long serialVersionUID = 1L;
}
