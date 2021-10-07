package gov.nih.ncgc.openhts.tool1.pointsetManager;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.tree.DefaultMutableTreeNode;
import com.db4o.ObjectContainer;
import gov.nih.ncgc.openhts.tool1.persistence.Db4oEnabled;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class DefaultMutableTreeNodeHolder implements Db4oEnabled {
  private transient Lock changeLock = new ReentrantLock();
  private DefaultMutableTreeNode treeRoot;
  private final static boolean debug = false;

  public DefaultMutableTreeNodeHolder(final DefaultMutableTreeNode root) {
    treeRoot = root;
  }

  public void setRoot(final DefaultMutableTreeNode root) {
    treeRoot = root;
  }

  public DefaultMutableTreeNode getRoot() {
    return treeRoot;
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
    if (debug)System.out.println("LOCK pointsetMnaager on thread = " + Thread.currentThread()+ " " +comment);
    changeLock.lock();
  }

  public void unlock(final String comment) {
    if (debug)System.out.println("UNLOCK on thread = " + Thread.currentThread()+ " " +comment);
    changeLock.unlock();
  }
}
