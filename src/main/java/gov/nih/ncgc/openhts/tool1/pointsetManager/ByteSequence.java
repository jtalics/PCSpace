package gov.nih.ncgc.openhts.tool1.pointsetManager;

public class ByteSequence implements Comparable<ByteSequence> {
  // Db4o cannot handle irregular jagged multidimensional array
  public byte[] bytes;
  public byte[] userBytes; // unspecified use

  public ByteSequence(final byte[] bytes) {
    this.bytes = bytes;
  }

  @Override
  public String toString() {
    return new String(bytes);
  }

  public float getUserFloat() {
    int i = (0xFF & userBytes[0]) << 24 | (0xFF & userBytes[1]) << 16
        | (0xFF & userBytes[2]) << 8 | 0xFF & userBytes[3];
    return Float.intBitsToFloat(i);
  }

  public int getUserInt() {
    int i = (0xFF & userBytes[0]) << 24 | (0xFF & userBytes[1]) << 16
        | (0xFF & userBytes[2]) << 8 | 0xFF & userBytes[3];
    return i;
  }

  public void setUserFloat(float f) {
    userBytes = new byte[4];
    int i = Float.floatToRawIntBits(f);
    userBytes[3] = (byte) (i & 0xFF);
    userBytes[2] = (byte) ((i >> 8) & 0xFF);
    userBytes[1] = (byte) ((i >> 16) & 0xFF);
    userBytes[0] = (byte) ((i >> 24) & 0xFF);
  }

  public void setUserInt(int i) {
    userBytes = new byte[4];
    userBytes[3] = (byte) (i & 0xFF);
    userBytes[2] = (byte) ((i >> 8) & 0xFF);
    userBytes[1] = (byte) ((i >> 16) & 0xFF);
    userBytes[0] = (byte) ((i >> 24) & 0xFF);
  }

  @Override
	public int compareTo(ByteSequence byteSequence) {
    return toString().compareTo((byteSequence.toString()));
  }
}
