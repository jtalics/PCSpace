package gov.nih.ncgc.openhts.tool1.heatmap;

import java.util.EventObject;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class HeatmapEvent extends EventObject {
  public final Object member;
  public final Kind kind;

  public HeatmapEvent(Object source, Object member, Kind kind) {
    super(source);
    this.member = member;
    this.kind = kind;
  }

  public enum Kind {
    MANAGER_CHANGED // most vague change possible
  };

  private static final long serialVersionUID = 1L;
}
