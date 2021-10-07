package gov.nih.ncgc.openhts.tool1.util.colorizer;

public class FunctionLogistic extends Function {
  public FunctionLogistic() {
    name = "logistic";
    description = "y = 1 / (1 + sigma * exp(mean-x))";
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
          values[i] = (float) (1.0 / (1.0 + statsPerCol[0].sigma*Math.exp(-(values[i]-statsPerCol[0].mean))));
        }
      }
    }
    else {
      for (int col = 0; col<statsPerCol.length; col++) {
        if (statsPerCol[col].range == 0) {
            values[col] = 0.5f;
        }
        else {
          values[col] = (float) (1.0 / (1.0 + statsPerCol[col].sigma*Math.exp(-(values[col]-statsPerCol[col].mean))));
        }        
      }
    }
  }

}
