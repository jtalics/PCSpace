package gov.nih.ncgc.openhts.tool1.persistence;


/** Purpose is to ...
 * @author talafousj
 *
 */
public abstract class PersistenceService {
	static private PersistenceService theService;
	
	static public void setService(final PersistenceService service) {
		theService = service;
	}
	
	static public PersistenceService getService() {
		return theService;
	}
    
    abstract public void delete(Object o);
	
	abstract public void register(PersistenceRoot root);
	
	abstract public void start();
	
	abstract public void stop();
	
	abstract public boolean isPersistenceThread();
	
	abstract public void invokeLater(Runnable r);
	
	abstract public void invokeAndWait(Runnable r);
}
