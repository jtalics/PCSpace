/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.dataSourceManager;

import gov.nih.ncgc.openhts.tool1.descriptorManager.Descriptor;

public abstract class Preview {
  private Descriptor descriptor;

  public Preview(Descriptor descriptor) {
    this.descriptor = descriptor;
  }

  abstract void addValue(String value);

  public Descriptor getDescriptor() {
    return descriptor;
  }
}