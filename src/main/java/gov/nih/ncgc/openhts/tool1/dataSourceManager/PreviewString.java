package gov.nih.ncgc.openhts.tool1.dataSourceManager;

import java.util.ArrayList;
import java.util.List;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Descriptor;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class PreviewString extends Preview {
  
  String min, max;
  List<String> samples = new ArrayList<String>();

  public PreviewString(Descriptor descriptor) {
    super(descriptor);
  }

  @Override
  void addValue(String value) {
    if (min == null || 0 >= value.compareTo(min)) {
      min=value;
    }
    if (max==null || 0<=value.compareTo(max)) {
      max=value;
    }
    // TODO: devise a better sampling (without knowing N)
    String s = value;
    if (samples.size() < 10 && !samples.contains(s)) {
      samples.add(s);
    }
  }

  public String[] getSamples() {
    return samples.toArray(new String[samples.size()]);
  }

  public String getMin() {
    return min;
  }

  public String getMax() {
    return max;
  }
}