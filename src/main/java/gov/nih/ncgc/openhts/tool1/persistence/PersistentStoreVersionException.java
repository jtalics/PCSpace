package gov.nih.ncgc.openhts.tool1.persistence;

/**
 * Indicates an incompatibility between the PersistenceService implementation
 * in the current runtime and a pre-existing application database.
 * @author talafousj
 *
 */
public class PersistentStoreVersionException extends Exception {
	/**
   * 
   */
  private static final long serialVersionUID = 1L;

  public PersistentStoreVersionException(final String msg) {
		super(msg);
	}
}
