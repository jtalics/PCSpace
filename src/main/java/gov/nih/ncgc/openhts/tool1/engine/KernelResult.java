package gov.nih.ncgc.openhts.tool1.engine;

import java.io.Serializable;
import java.util.Date;

/** Purpose is to ...
 * @author talafousj
 *
 * @param <T>
 */
public class KernelResult<T> implements Serializable {
  private static final long serialVersionUID = 1L;
  private final KernelCommand<T> command;
  private KernelException exception;
  private Date when;

  public KernelCommand getCommand() {
    return command;
  }

  public KernelException getException() {
    return exception;
  }

  public Date getWhen() {
    return when;
  }

  public T getResult() {
    return command.getResult();
  }

  public boolean isError() {
    return exception != null;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final KernelResult result = (KernelResult) o;
    return command != null && command.equals(result.command);
  }

  @Override
  public int hashCode() {
    return command == null ? 0 : command.hashCode();
  }

  KernelResult(final KernelCommand command) {
    this.command = command;
  }

  void setException(final KernelException x) {
    this.exception = x;
  }

  void setWhen(final Date when) {
    this.when = when;
  }
}
