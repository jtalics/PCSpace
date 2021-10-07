package gov.nih.ncgc.openhts.tool1.util.colorizer;

import javax.swing.JLabel;
import gov.nih.ncgc.openhts.tool1.util.ProgressBar;
import gov.nih.ncgc.openhts.tool1.util.Viewlet;

public class FunctionStdDevAbs extends Function {

  public FunctionStdDevAbs() {
    name="Absolute Standard Deviation";
    description="y = | x - mean | / sigma";
    viewlet = new FunctionViewlet(this) {

      @Override
      public Viewlet build() {
        topPanel.add(new JLabel(statsPerCol[0].toString()));
        return this;
      }
    };
  }

  @Override
  protected void doApply(float[] values) {
    if (statsPerCol.length == 1) {
      if (statsPerCol[0].range == 0) {
        for (int i = 0; i < values.length; i++) {
          values[i] = 0.5f;
        }
      }
      else {
        for (int i = 0; i < values.length; i++) {
          values[i] = Math.abs(values[i]-statsPerCol[0].mean)/statsPerCol[0].sigma;
        }
      }
    }
    else {
      for (int col = 0; col<statsPerCol.length; col++) {
        if (statsPerCol[col].range == 0) {
            values[col] = 0.5f;
            if (col % ProgressBar.progressIncr == 0) {
              fireProgressChanged(this, "applying ", 0, col, values.length);
            }
        }
        else {
            values[col] = Math.abs(values[col]-statsPerCol[col].mean)/statsPerCol[col].sigma;
        }        
      }
    }
  }
}
