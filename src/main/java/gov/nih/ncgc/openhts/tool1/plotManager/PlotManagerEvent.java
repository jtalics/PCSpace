package gov.nih.ncgc.openhts.tool1.plotManager;

import java.util.EventObject;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class PlotManagerEvent extends EventObject {
  public final Object member;
  public final Kind kind;

  public PlotManagerEvent(Object source, Object member, Kind kind) {
    super(source);
    this.member = member;
    this.kind = kind;
  }

  public enum Kind {
    CHANGED, // most vague change possible
    BASISMODE_CHANGED,
    TEXT_SELECTION_CHANGED, // use more specific kind if available
    POINTSET_ORDER_CHANGED,
    TEXT_PROPERTIES_CHANGED,
    PREVIEW_CHANGED,
    DIMENSIONALITY
  };

  private static final long serialVersionUID = 1L;
}
