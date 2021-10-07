package gov.nih.ncgc.openhts.tool1.dataSourceManager;

import javax.swing.tree.DefaultMutableTreeNode;
import com.db4o.ObjectContainer;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.persistence.Persistent;
import gov.nih.ncgc.openhts.tool1.util.ProgressBar;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class DataSourceTreeNode extends DefaultMutableTreeNode implements
    Persistent, DataSourceManagerListener {
  private transient DataSourceTreeModel model;
  private transient ProgressBar progressBar;
  private transient Session session;
  // for db4o, userObject is transient
  private DataSource dataSource;

  public DataSourceTreeNode(final Session session, final DataSource dataSource) {
    super.setUserObject(dataSource);
    this.dataSource = dataSource;
    this.session = session;
    progressBar = new ProgressBar();
    progressBar.setIndeterminate(false);
    progressBar.setStringPainted(true);
    // session.dataSourceManager.addDataSourceManagerListener(progressBar);
    session.dataSourceManager.addDataSourceManagerListener(this);
  }

  // @Implements Persistent
  public void objectOnActivate(final ObjectContainer oc) {
    super.setUserObject(dataSource);
  }

  // @Implements Persistent
  public void objectOnDeactivate(final ObjectContainer oc) {
    dataSource = (DataSource) userObject;
  }

  public String getToolTipText() {
    return dataSource.getToolTipText();
  }

  @Override
	public void progressChanged(Object dataSource, String string, int min,
      int value, int max) {
    if (dataSource != this.dataSource) {
      return;
    }
    progressBar.progressChanged(dataSource, string, min, value, max);
    model.nodeChanged(this);
  }

  @Override
	public void initializeTransient() {
    // TODO Auto-generated method stub
  }

  @Override
	public void setSession(Session session) {
    this.session = session;
  }

  public ProgressBar getProgressBar() {
    return progressBar;
  }

  public void setProgressBar(ProgressBar progressBar) {
    this.progressBar = progressBar;
  }

  public void setModel(final DataSourceTreeModel dataSourceTreeModel) {
    model = dataSourceTreeModel;
  }

  @Override
	public void dataSourceManagerChanged(DataSourceManagerEvent ev) {
    switch (ev.kind) {
    case CHANGED:
      break;
    case MEMBER_ADDED:
      // nop
      break;
    case MEMBER_CHANGED:
      model.nodeChanged(this);
      break;
    case MEMBER_REMOVED:
      // nop
      break;
    }
  }

  private static final long serialVersionUID = 1L;
}
