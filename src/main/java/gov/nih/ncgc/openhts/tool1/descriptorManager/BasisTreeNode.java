package gov.nih.ncgc.openhts.tool1.descriptorManager;

import java.awt.FlowLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import com.db4o.ObjectContainer;
import gov.nih.ncgc.openhts.tool1.Session;

/** Purpose is to ...
 * @author talafousj
 */
public class BasisTreeNode extends DefaultMutableTreeNode {

  // for db4o, userObject is transient
  private final Basis basis;
  private JPanel panel;
  private JCheckBox checkBox;
  private final Session session;

  public BasisTreeNode(Session session, final Basis basis) {
      super.setUserObject(basis);
      this.session = session;
      this.basis = basis;
//    if (userObject instanceof SearchHistoryEntry) {
//        ((SearchHistoryEntry) userObject).addChangeListener(this);
//    }
      panel  = new JPanel();
      panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
      checkBox = new JCheckBox();
      checkBox.setBorder(null);
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
    super.setUserObject(basis);
  //      if (userObject instanceof DataSource) {
  //          ((DataSource) userObject).addChangeListener(this);
  //      }        
  }
  
  //@Implements Persistent
  public void objectOnDeactivate(final ObjectContainer oc) {
    //basis = basis;
  //      if (userObject instanceof DataSource) {
  //          ((DataSource) userObject).addChangeListener(this);
  //      }        
  }

  public String getToolTipText() {
    return basis.getToolTipText();
  }

  public JPanel getPanel() {
    panel.removeAll();
    return panel;
  }

  public JCheckBox getCheckBox() {
    checkBox.setSelected(session.plotManager.basis == basis);
    return checkBox;
  }

  public Basis getBasis() {
    return basis;
  }

  private static final long serialVersionUID = 1L;
}
