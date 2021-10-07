/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.pointsetManager;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.descriptorManager.BasisTreeNode;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class CheckBoxTreeNodeListener implements MouseListener {
  private JTree tree;
  private PointsetTreeModel pointsetTreeModel;
  private Session session;

  public CheckBoxTreeNodeListener(Session session, JTree tree,
      PointsetTreeModel pointsetTreeModel) {
    this.session = session;
    this.tree = tree;
    this.pointsetTreeModel = pointsetTreeModel;
  }

  @Override
	public void mouseClicked(MouseEvent e) {
    // Get the path thru the tree based on the location of the mouse click
    final TreePath path = tree.getPathForLocation(e.getX(), e.getY());
    if (path == null) {
      return;
    }
    Object obj = path.getLastPathComponent();
    if (obj instanceof PointsetTreeNode) {
      PointsetTreeNode pointsetTreeNode = (PointsetTreeNode) obj;
      JCheckBox checkBox = pointsetTreeNode.getCheckBox();
      int hotspot = checkBox.getPreferredSize().width;
      if (e.getX() <= tree.getPathBounds(path).x + hotspot) {
        checkBox.setSelected(!checkBox.isSelected());
        session.pointsetManager.entityCheckBoxSelected(pointsetTreeNode
            .getPointset(), checkBox.isSelected());
        pointsetTreeModel.nodeChanged(pointsetTreeNode);
      }
    }
    else if (obj instanceof BasisTreeNode) {
      BasisTreeNode basisTreeNode = (BasisTreeNode) obj;
      JCheckBox checkBox = basisTreeNode.getCheckBox();
      int hotspot = basisTreeNode.getCheckBox().getPreferredSize().width;
      if (e.getX() <= tree.getPathBounds(path).x + hotspot) {
        checkBox.setSelected(checkBox.isSelected() ? false : true);
        if (checkBox.isSelected()) {
          session.plotManager.setBasisMode(basisTreeNode.getBasis());
          basisTreeNode.getBasis().setVisible(true);
        }
        else {
          session.plotManager.setBasisMode(null);
          basisTreeNode.getBasis().setVisible(false);
        }
        session.pointsetManager.entityCheckBoxSelected(basisTreeNode
            .getBasis(), checkBox.isSelected());
        pointsetTreeModel.nodeChanged(basisTreeNode);
      }
    }
    else {
      // nop
    }
  }

  @Override
	public void mouseEntered(MouseEvent e) {
    // nop
  }

  @Override
	public void mouseExited(MouseEvent e) {
    // nop
  }

  @Override
	public void mousePressed(MouseEvent e) {
    // nop
  }

  @Override
	public void mouseReleased(MouseEvent e) {
    // nop
  }
}
