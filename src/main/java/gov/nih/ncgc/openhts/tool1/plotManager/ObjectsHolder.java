package gov.nih.ncgc.openhts.tool1.plotManager;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.db4o.ObjectContainer;
import gov.nih.ncgc.openhts.tool1.persistence.Db4oEnabled;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class ObjectsHolder implements Db4oEnabled {
  private final static boolean debug = false;
  private transient Lock changeLock = new ReentrantLock();
  private List<Object> objects;

  public ObjectsHolder(final List<Object> objects ) {
    this.objects = objects;
  }

  public void setObjects(final List<Object> objects) {
    this.objects = objects;
  }

  public List<Object> getObjects() {
    return objects;
  }

  @Override
	public boolean objectCanActivate(final ObjectContainer oc) {
    return true;
  }

  @Override
	public void objectOnActivate(final ObjectContainer oc) {
    oc.activate(this, Integer.MAX_VALUE);
    changeLock = new ReentrantLock();
  }

  @Override
	public boolean objectCanNew(final ObjectContainer oc) {
    lock("objectCanNew");
    return true;
  }

  @Override
	public boolean objectCanUpdate(final ObjectContainer oc) {
    lock("objectCanUpdate");
    return true;
  }

  @Override
	public void objectOnNew(final ObjectContainer oc) {
    unlock("objectOnNew");
  }

  @Override
	public void objectOnUpdate(final ObjectContainer oc) {
    unlock("objectOnUpdate");
  }

  public void lock(final String comment) {
    if (debug)System.out.println("LOCK plotManagger on thread = " + Thread.currentThread()+ " " +comment);
    changeLock.lock();
  }

  public void unlock(final String comment) {
    if (debug)System.out.println("UNLOCK plotManager on thread = " + Thread.currentThread()+ " " +comment);
    changeLock.unlock();
  }
}
