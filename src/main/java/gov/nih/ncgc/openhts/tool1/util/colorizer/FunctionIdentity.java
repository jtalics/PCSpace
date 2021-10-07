/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.util.colorizer;

import javax.swing.JLabel;
import gov.nih.ncgc.openhts.tool1.util.Viewlet;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class FunctionIdentity extends Function {
  public FunctionIdentity() {
    name = "identity";
    description = "y = x";
    viewlet = new FunctionViewlet(this) {
      @Override
      public Viewlet build() {
        if (statsPerCol != null) {
          topPanel.add(new JLabel(statsPerCol.toString())
             /*new JLabel(""+stats.max)*/);
        }
        return this;
      }

      private static final long serialVersionUID = 1L;
    };
  }

  @Override
  public void doApply(float[] values) {
  }

}
