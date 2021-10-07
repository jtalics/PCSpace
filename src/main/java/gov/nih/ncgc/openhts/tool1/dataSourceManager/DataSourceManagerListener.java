/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.dataSourceManager;

import gov.nih.ncgc.openhts.tool1.util.ProgressListener;

/** Purpose is to ...
 * @author talafousj
 */
public interface DataSourceManagerListener extends ProgressListener {

  public void dataSourceManagerChanged(DataSourceManagerEvent ev);
//  public void dataSourceAdded(DataSourceManager manager, DataSource dataSource);
//  public void dataSourceChanged(DataSourceManager manager, DataSource dataSource);
//  public void dataSourceRemoved(DataSourceManager manager, DataSource dataSource);
}

