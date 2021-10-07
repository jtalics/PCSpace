package gov.nih.ncgc.openhts.tool1;

public class StringAndNumberFloat {
  public final String string;
  public final float numberFloat;
  public StringAndNumberFloat(String string, float numberFloat) {
    this.string=string;
    this.numberFloat=numberFloat;
  }

  public StringAndNumberFloat() {
    this.string=null;
    this.numberFloat=Float.NaN;
  }
}
