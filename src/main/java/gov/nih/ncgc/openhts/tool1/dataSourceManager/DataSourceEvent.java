/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.dataSourceManager;

import java.util.EventObject;

/** Purpose is to ...
 * @author talafousj
 */
public class DataSourceEvent extends EventObject {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private final DataSource dataSource;

  public DataSourceEvent(final DataSource source) {
    super(source);
    this.dataSource = source;
  }
  
  @Override
  public DataSource getSource() {
    return (DataSource)source;
  }
  
  public DataSource getDataSource() {
    return dataSource;
  }
}
