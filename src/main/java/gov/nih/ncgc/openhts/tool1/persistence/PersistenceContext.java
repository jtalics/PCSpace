package gov.nih.ncgc.openhts.tool1.persistence;

import java.util.List;

/**
 * Describes means for persistence clients to interact with the
 * PersistenceService.
 * @author talafousj
 *
 */
public interface PersistenceContext {
	
	/**
	 * Returns a list of all persistent objects of the specified class.
	 */
	public <T> List<T> getExtent(Class<T> claz);
	
	/**
	 * Returns the persistent object of the specified class, matching
	 * the specified index value.
	 * @param <T>
	 * @param claz
	 * @param indexMemberName
	 * @param indexValue
	 * @return
	 */
	public <T> T find(Class<T> claz, String indexMemberName, Object indexValue);
	
	/**
	 * Returns all persistent objects of the specified class, matching
	 * the specified index value.
	 * @param <T>
	 * @param claz
	 * @param indexMemberName
	 * @param indexValue
	 * @return
	 */
	public <T> List<T> findAll(Class<T> claz, String indexMemberName, Object indexValue);
	
	/**
	 * Records the specified object in the persistent store.
	 */
	public void set(Object o);
	
	/**
	 * Removes the specified object from the persistent store.
	 */
	public void delete(Object o);
}
