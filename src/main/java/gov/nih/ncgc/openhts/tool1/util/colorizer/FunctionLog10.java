/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.util.colorizer;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class FunctionLog10 extends Function {
  public FunctionLog10() {
    name = "log 10";
    description = "y = log10(x)";
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
          values[i] = (float) (Math.log10((1 + 9
              * (values[i] - statsPerCol[0].min) / statsPerCol[0].range)));
        }
      }
    }
    else {
      for (int col = 0; col < statsPerCol.length; col++) {
        if (statsPerCol[col].range == 0) {
            values[col] = 0.5f;
        }
        else {
          values[col] = (float) (Math.log10((1 + 9
              * (values[col] - statsPerCol[col].min) / statsPerCol[col].range)));
        }
      }
    }
  }
  // @Override
  // public void apply(float[][] values) {
  // if (values[0].length != statsPerCol.length) {
  //      
  // }
  // // assume non-jagged matrix
  // for (int col = 0; col < values[0].length; col++) {
  // if (statsPerCol[col].range == 0) {
  // for (int row = 0; row < values.length; row++) {
  // values[row][col] = 0.5f;
  // }
  // }
  // else {
  // for (int i = 0; i < values.length; i++) {
  // for (int row = 0; row < values.length; row++) {
  // values[row][col] = (values[row][col] - statsPerCol[col].min) /
  // (statsPerCol[col].range);
  // }
  // }
  // }
  // }
  // }
}
