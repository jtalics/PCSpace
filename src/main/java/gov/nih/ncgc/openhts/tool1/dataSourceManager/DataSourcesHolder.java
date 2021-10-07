package gov.nih.ncgc.openhts.tool1.dataSourceManager;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.db4o.ObjectContainer;

public class DataSourcesHolder {
	transient protected Lock changeLock = new ReentrantLock();
	private List<DataSource> dataSources;
	
	DataSourcesHolder(final List<DataSource> dataSources) {
		this.dataSources = dataSources;
	}
  
	public void setDataSources(final List<DataSource> dataSources) {
		this.dataSources = dataSources;
	}

  public List<DataSource> getDataSources() {
		return dataSources;
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
