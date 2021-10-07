package gov.nih.ncgc.openhts.tool1.spreadsheet;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.db4o.ObjectContainer;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class PointsetToPointsetTableHolder {
	protected transient Lock changeLock = new ReentrantLock();
	private Map<Pointset,PointsetTable> pointsetToPointsetTable;
  
  //  
	PointsetToPointsetTableHolder(final Map<Pointset,PointsetTable> pointsetToPointsetTable) {
		this.pointsetToPointsetTable = pointsetToPointsetTable;
 }
  
	public void setPointsetToPointsetTable(final Map<Pointset,PointsetTable> pointsetToPointsetTable) {
		this.pointsetToPointsetTable = pointsetToPointsetTable;
	}

  public Map<Pointset,PointsetTable> getPointsetToPointsetTable() {
		return pointsetToPointsetTable;
	}
	
	public boolean objectCanActivate(final ObjectContainer oc) {
		return true;
	}
	public void objectOnActivate(final ObjectContainer oc) {
		oc.activate(this, Integer.MAX_VALUE);
        changeLock = new ReentrantLock();
	}
	public boolean objectCanNew(final ObjectContainer oc) {
		changeLock.lock();
		return true;
	}
	public boolean objectCanUpdate(final ObjectContainer oc) {
		changeLock.lock();
		return true;
		
	}
	public void objectOnNew(final ObjectContainer oc) {
		changeLock.unlock();
	}
	public void objectOnUpdate(final ObjectContainer oc) {
		changeLock.unlock();
	}
}
