package gov.nih.ncgc.openhts.tool1.persistence;

/**
 * Describes a well-known root of a persistent graph.
 * Implementations may or may not choose to be persistent themselves.
 * Some implementations may exist only at runtime as a means
 * to access a persistent graph.

 * @author talafousj
 *
 */
public interface PersistenceRoot {
	
	/**
	 * Implementations must be called at this method
	 * prior to receiving any other method calls.
	 */
	public void setPersistenceContext(PersistenceContext context);
	
	/**
	 * Returns the PersistenceContext assigned to this root.
	 * The returned context is used to control persistence for
	 * all (and only those) stored objects managed by this root.
	 * @return
	 */
	public PersistenceContext getPersistenceContext();
}
