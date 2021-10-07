/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.persistence;

import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.DataSourceDir;

public class PersistenceDelegateDataSourceDir extends PersistenceDelegate {
  @Override
  protected Expression instantiate(Object oldInstance, Encoder out) {
    DataSourceDir file = (DataSourceDir) oldInstance;
    return new Expression(oldInstance, oldInstance.getClass(), "new",
        new Object[] { file.getPath() });
  }
}
