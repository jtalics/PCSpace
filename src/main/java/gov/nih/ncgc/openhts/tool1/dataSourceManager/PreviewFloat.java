package gov.nih.ncgc.openhts.tool1.dataSourceManager;

import gov.nih.ncgc.openhts.tool1.descriptorManager.Descriptor;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class PreviewFloat extends Preview {
  public PreviewFloat(Descriptor descriptor) {
    super(descriptor);
  }

  private float min = Float.POSITIVE_INFINITY;
  private float max = Float.NEGATIVE_INFINITY;

  @Override
  void addValue(String value) {
    float f = Float.parseFloat(value);
    min = f < min ? f : min;
    max = f > max ? f : max;
  }

  public float getMin() {
    return min;
  }

  public float getMax() {
    return max;
  }
}