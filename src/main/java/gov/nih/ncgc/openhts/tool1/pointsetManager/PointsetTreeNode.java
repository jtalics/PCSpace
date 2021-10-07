package gov.nih.ncgc.openhts.tool1.pointsetManager;

import java.awt.Component;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import com.db4o.ObjectContainer;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.persistence.Persistent;
import gov.nih.ncgc.openhts.tool1.util.ProgressBar;
import gov.nih.ncgc.openhts.tool1.util.colorizer.ColormapCanvas;
import gov.nih.ncgc.openhts.tool1.util.colorizer.Waypoint;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class PointsetTreeNode extends DefaultMutableTreeNode implements
    Persistent, PointsetManagerListener {
  // for db4o, userObject is transient
  private Pointset pointset;
  private transient PointsetTreeModel pointsetTreeModel;
  private ProgressBar progressBar;
  private Session session;
  private JPanel panel;
  private JCheckBox checkBox;
  private List<Waypoint> waypoints;
  private int lastRenderedHeight=-1;

  public PointsetTreeNode(Session session, final Pointset pointset) {
    super.setUserObject(pointset);
    this.session = session;
    this.pointset = pointset;
    // if (pointset != null){
    // // root has no user object
    // //this.pointset.addChangeListener(this);
    // }
    progressBar = new ProgressBar();
    progressBar.setIndeterminate(false);
    progressBar.setStringPainted(true);
    // session.dataSourceManager.addDataSourceManagerListener(progressBar);
    // session.dataSourceManager.addDataSourceManagerListener(this);
    panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    checkBox = new JCheckBox();
    checkBox.setBorder(null);
  }

  // @Implements Persistent
  public void objectOnActivate(final ObjectContainer oc) {
    super.setUserObject(pointset);
  }

  // @Implements Persistent
  public void objectOnDeactivate(final ObjectContainer oc) {
    pointset = (Pointset) userObject;
  }

  public String getToolTipText() {
    return pointset.getToolTipText();
  }

  public void setModel(final PointsetTreeModel pointsetTreeModel) {
    this.pointsetTreeModel = pointsetTreeModel;
  }

  // @Implements Persistent
  @Override
	public void initializeTransient() {
    // nop
  }

  // @Implements Persistent
  @Override
	public void setSession(Session session) {
    // nop
  }

  public ProgressBar getProgressBar() {
    return progressBar;
  }

  public void setProgressBar(ProgressBar progressBar) {
    this.progressBar = progressBar;
  }


  public JCheckBox getCheckBox() {
    checkBox.setSelected(pointset.isVisible());
    return checkBox;
  }

  public JPanel getPanel() {
    panel.removeAll();
    return panel;
  }

  public Pointset getPointset() {
    return pointset;
  }


  public Component getColorMap() {
    return new ColormapCanvas(waypoints);
  }

  public void setWaypoints(List<Waypoint> waypoints) {
    this.waypoints = waypoints;
  }
  
  // @Implements
  @Override
	public void progressChanged(Object subject, String string, int min,
      int value, int max) {
    progressBar.progressChanged(subject, string, min, value, max);
    pointsetTreeModel.nodeChanged(this);
  }
  
  @Override
	public void pointsetManagerChanged(PointsetManagerEvent ev) {
    switch (ev.kind) {
    case MEMBER_LOADED:
      if (ev.member == pointset) {
        pointsetTreeModel.nodeChanged(this);
      }
      break;
    case MEMBER_ADDED:
      break;
    case MEMBER_CHANGED:
      if (ev.member != this.pointset) {
        return;
      }
      // TODO: do we want to use pointsetTreeModel.reload(this) instead?
      pointsetTreeModel.nodeChanged(this);
      break;
    case POINTSET_REMOVED:
      break;
    case MEMBERS_SELECTION:
      break;
    case MANAGER_CHANGED:
      break;
    case MEMBER_VISABILITY:
      break;
    case POINT_VIZ:
      break;
    case AC_MAP:
      break;
    }
  }

  private static final long serialVersionUID = 1L;

  public void setRenderedHeight(int height) {
    // TODO Auto-generated method stub
    
  }

  public int lastRenderedHeight(int renderedHeight) {
    int tmp = lastRenderedHeight;
    lastRenderedHeight = renderedHeight;
    return tmp;
  }

}
