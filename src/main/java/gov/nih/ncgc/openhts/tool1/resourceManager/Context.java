package gov.nih.ncgc.openhts.tool1.resourceManager;

import java.io.Serializable;

/**
 * Encapsulates concept of "handle" which is used to reference resources within
 * the core apis. Ideally, packages outside of platform should not work directly
 * with handles if handles are to be hidden from higher levels of abstraction.
 * 
 * @author talafousj
 */
public class Context implements Serializable {
  private final int handle = 0;

  public Context() {
    // nop
  }

  int handle() {
    return handle;
  }

  @Override
  public boolean equals(final Object o) {
    if (o instanceof Context) {
      final Context ctx = (Context) o;
      return handle == ctx.handle;
    }
    else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return -1;// handle.hashCode();
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Context@");
    sb.append(handle);
    return sb.toString();
  }

  private static final long serialVersionUID = 1L;
}
