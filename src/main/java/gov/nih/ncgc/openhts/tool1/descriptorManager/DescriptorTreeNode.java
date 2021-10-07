/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.descriptorManager;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import com.db4o.ObjectContainer;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetTreeModel;

/** Purpose is to ...
 * @author talafousj
 */
public class DescriptorTreeNode extends DefaultMutableTreeNode {

  // for db4o, userObject is transient
  private Descriptor descriptor;
  private transient PointsetTreeModel model;
  private JPanel panel = new JPanel();
  
  public DescriptorTreeNode(final Descriptor descriptor) {
      super.setUserObject(descriptor);
      this.descriptor = descriptor;
//    if (userObject instanceof SearchHistoryEntry) {
//        ((SearchHistoryEntry) userObject).addChangeListener(this);
//    }
  }

//  @Override
//  public Object getUserObject() {
//      return this.dataSource;
//  }
//
//  @Override
//  public void setUserObject(Object object) {
//      super.setUserObject(object);
//      this.dataSource = (DataSource)object;
//  }
//
  
  //@Implements Persistent
  public void objectOnActivate(final ObjectContainer oc) {
    super.setUserObject(descriptor);
  //      if (userObject instanceof DataSource) {
  //          ((DataSource) userObject).addChangeListener(this);
  //      }        
  }
  
  //@Implements Persistent
  public void objectOnDeactivate(final ObjectContainer oc) {
    //descriptor = descriptor;
  //      if (userObject instanceof DataSource) {
  //          ((DataSource) userObject).addChangeListener(this);
  //      }        
  }

  public String getToolTipText() {
    return descriptor.getToolTipText();
  }

  public void setModel(final PointsetTreeModel pointsetTreeModel) {
    this.model=pointsetTreeModel;
  }

  public JPanel getPanel() {
    panel.removeAll();
    return panel;
  }
  
  private static final long serialVersionUID = 1L;
}
