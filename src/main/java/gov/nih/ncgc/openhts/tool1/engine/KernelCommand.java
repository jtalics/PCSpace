package gov.nih.ncgc.openhts.tool1.engine;

import java.io.Serializable;
import java.rmi.dgc.VMID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import gov.nih.ncgc.openhts.tool1.resourceManager.ResourceProvider;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 * @param <T>
 */
public abstract class KernelCommand<T> implements Serializable {
  protected T result = null;
  private final VMID id;
  private boolean executed = false;
  private final List<KernelCommandListener> listeners = Collections
      .synchronizedList(new ArrayList<KernelCommandListener>());
  private final boolean cpuBound=true;

  public T getResult() {
    return result;
  }

  public boolean isExecuted() {
    return executed;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final KernelCommand cmd = (KernelCommand) o;
    return id.equals(cmd.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  protected KernelCommand() {
    id = new VMID();
  }

  void execute(final ResourceProvider provider) throws KernelException {
    try {
      doExecute(provider);
    }
    finally {
      executed = true;
    }
  }

  public void addListener(final KernelCommandListener listener) {
    listeners.add(listener);
  }

  public void removeListener(final KernelCommandListener listener) {
    listeners.remove(listener);
  }

  private List<KernelCommandListener> getListeners() {
    final List<KernelCommandListener> ll = new ArrayList<KernelCommandListener>(
        listeners.size());
    synchronized (listeners) {
      ll.addAll(listeners);
    }
    return ll;
  }

  // fired by Engine
  void fireEnqueued(final KernelCommand command) {
    for (final KernelCommandListener l : getListeners()) {
      l.kernelCommandEnqueued(command);
    }
  }

  // fired by Engine
  void fireDequeued(final KernelCommand command) {
    for (final KernelCommandListener l : getListeners()) {
      l.kernelCommandDequeued(command);
    }
  }

  // fired by KernelCommand.doExecute()
  protected void fireExecutionBegin(final KernelCommand command) {
    for (final KernelCommandListener l : getListeners()) {
      l.kernelCommandExecutionBegin(command);
    }
  }

  // fired by Kernel.execute() after the result is composed
  protected void fireExecutionComplete(final KernelResult result) {
    for (final KernelCommandListener l : getListeners()) {
      l.kernelCommandExecutionComplete(result);
    }
  }

  protected abstract void doExecute(ResourceProvider provider)
      throws KernelException;

  public boolean isCpuBound() {
    // if false, then io bound
    return cpuBound;
  }
}
