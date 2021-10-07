package gov.nih.ncgc.openhts.tool1.dataSourceManager;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.db4o.ObjectContainer;
import gov.nih.ncgc.openhts.tool1.dirMonitor.DirMonitor;

public class DataSourceToDirMonitorHolder {
	transient protected Lock changeLock = new ReentrantLock();
	private Map<DataSource,DirMonitor> dataSourceToDirMonitor;
  int unused;
  
	DataSourceToDirMonitorHolder(final Map<DataSource,DirMonitor> dataSourceToDirMonitor) {
    this.dataSourceToDirMonitor = dataSourceToDirMonitor;
  }
  
	public void setDataSourceToDirHolder(final Map<DataSource,DirMonitor> dataSourceToDirMonitor) {
    this.dataSourceToDirMonitor = dataSourceToDirMonitor;
	}

  public final Map<DataSource,DirMonitor> getDataSourceToDirMonitor() {
    return dataSourceToDirMonitor;
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
