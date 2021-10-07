package gov.nih.ncgc.openhts.tool1.util.colorizer;

import gov.nih.ncgc.openhts.tool1.util.Viewlet;

/** Purpose is to ...
 * @author talafousj
 *
 */
public abstract class FunctionViewlet extends Viewlet {

  protected Function function;

  public FunctionViewlet(Function function) {
    super();
    this.function = function;
  }

  public Function getFunction() {
    return function;
  }
}
