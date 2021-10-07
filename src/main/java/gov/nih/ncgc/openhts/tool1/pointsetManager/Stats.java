/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.pointsetManager;

public class Stats {
  int n;
  public float min, mean, max;
  int minIndex=-1, maxIndex=-1;
  public float range;
  public float sigma;

  public Stats(float[] values) {
    if (values != null) {
      n=values.length;
      calc(values);
    }
  }
  
  public static float[] collectIntoArray(float[][] valuess) {
    if (valuess == null) {
      return null;
    }

    int n=0;
    for (int row=0; row < valuess.length; row++) {
      n+=valuess[row].length;
    }
      
    float[] values = new float[n];

    n=0;
    for (int row=0; row < valuess.length; row++) {
      for (int col=0; col < valuess[row].length; col++) {      
        values[n]=valuess[row][col];
        n++;
      }
    }
    return values;
  }

  public float getMax() {
    return max;
  }

  public float getMin() {
    return min;
  }

  private void calcMinMax(float[] data) {
    min = Float.POSITIVE_INFINITY;
    max = Float.NEGATIVE_INFINITY;
    for (int i = 0; i < data.length; i++) {
//      min = data[i] < min ? data[i] : min;
      if (data[i] < min ) {
        min=data[i];
        minIndex=i;
      }
//      max = data[i] > max ? data[i] : max;
      if (data[i] > max) {
        max = data[i];
        maxIndex=i;
      }
    }
    range = max - min;
  }

  private void calcMean(float[] data) {
    float sum = 0.0f;
    for (int i = 0; i < data.length; i++) {
      sum += data[i];
    }
    mean = sum / data.length;
  }

  private void calcSigma(float[] data) {
    float s = 0.0f;
    float diff;
    for (int i = 0; i < data.length; i++) {
      diff = data[i] - mean;
      s += diff * diff;
    }
    sigma = s / data.length;
  }
  
  @Override
  public String toString() {
    return "[Stats; min="+min+"; mean="+mean+"; max="+max+"; sigma="+sigma+"]";
  }

  public void calc(float[] values) {
    calcMinMax(values);
    calcMean(values);
    calcSigma(values);        
  }

  public int getMinIndex() {
    // NOTE: there may be more than one index with min value, we return the last one here
    return minIndex;
  }

  public int getMaxIndex() {
    // NOTE: there may be more than one index with max value, we return the last one here
    return maxIndex;
  }
}