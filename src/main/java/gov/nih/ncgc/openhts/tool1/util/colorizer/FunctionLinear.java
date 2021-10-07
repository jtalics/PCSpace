package gov.nih.ncgc.openhts.tool1.util.colorizer;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class FunctionLinear extends Function {
  public FunctionLinear() {
    name = "linear";
    description = "y = m * x + b";
    viewlet = null;
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
          values[i] = (values[i] - statsPerCol[0].min) / (statsPerCol[0].range);
        }
      }
    }
    else {
      for (int col = 0; col < statsPerCol.length; col++) {
        if (statsPerCol[col].range == 0) {
          values[col] = 0.5f;
        }
        else {
          values[col] = (values[col] - statsPerCol[col].min)
              / (statsPerCol[col].range);
        }
      }
    }
  }
}
