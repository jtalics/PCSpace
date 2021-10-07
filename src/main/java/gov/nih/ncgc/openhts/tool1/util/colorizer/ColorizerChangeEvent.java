package gov.nih.ncgc.openhts.tool1.util.colorizer;

import javax.swing.event.ChangeEvent;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class ColorizerChangeEvent extends ChangeEvent {
  
  public final Kind kind;

  public ColorizerChangeEvent(Object source, Kind kind) {
    super(source);
    this.kind = kind;
  }

  public enum Kind {
    PREPROCESSOR_FUNCTION,
    PREPROCESSOR_FUNCTION_STATS,
    COLORING_FUNCTION,
    COLORING_FUNCTION_STATS,
    COLOR_MAP;
  }

  private static final long serialVersionUID = 1L;
}
