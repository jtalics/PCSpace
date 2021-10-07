/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.persistence;

import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.io.File;

public class PersistenceDelegateFile extends PersistenceDelegate {
  @Override
  protected Expression instantiate(Object oldInstance, Encoder out) {
    File file = (File) oldInstance;
    return new Expression(oldInstance, oldInstance.getClass(), "new",
        new Object[] { file.getPath() });
  }
}