package gov.nih.ncgc.openhts.tool1.dataSourceManager;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import gov.nih.ncgc.openhts.tool1.Session;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class DataSourceTreeModel extends DefaultTreeModel {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private final Session session;

  /**
   * @param arg0
   */
  public DataSourceTreeModel(final Session session, final TreeNode arg0) {
    super(arg0);
    this.session = session;
  }

  @Override
  public void removeNodeFromParent(final MutableTreeNode node) {
    session.dataSourceManager.lockRootHolder("lock");
    super.removeNodeFromParent(node);
    session.dataSourceManager.unlockRootHolder("unlock");
//    session.dataSourceManager.storeRoot();
  }

  @Override
  public void insertNodeInto(final MutableTreeNode newChild, final MutableTreeNode parent,
      final int index) {
    session.dataSourceManager.lockRootHolder("lock");
    super.insertNodeInto(newChild, parent, index);
    session.dataSourceManager.unlockRootHolder("unlock");
    // System.out.println("inserted new CHild: " + newChild);
//  session.dataSourceManager.storeRoot();
  }
  public TreeNode[] addDataSource(final DataSource dataSource) {
    // Insert new pointset into the tree model
    final DataSourceTreeNode dstn = new DataSourceTreeNode(session,dataSource);
    dstn.setModel(this);
    insertNodeInto(dstn, (DefaultMutableTreeNode) root.getChildAt(1), root.getChildAt(1).getChildCount());
    session.dataSourceManager.storeRoot();
    return getPathToRoot(dstn);
  }
  public DefaultMutableTreeNode[] getDataSourceTreeNode(DataSource dataSource) {
    return search((DefaultMutableTreeNode) root,dataSource);
  }

  DefaultMutableTreeNode[] search(DefaultMutableTreeNode startNode,
      Object object) {
    TreeNode[] path = null;
    Object obj = startNode.getUserObject();
    if (object.equals(obj)) {
      path = getPathToRoot(startNode);
    }
    else {
      int i = 0;
      int n = getChildCount(startNode);
      while ((i < n) && (path == null)) {
        path = search((DefaultMutableTreeNode) getChild(startNode, i), object);
        i++;
      }
    }
    if (path != null) {
      return convertToDmtn(path);
    }
    return null;
  }


  private DefaultMutableTreeNode[] convertToDmtn(TreeNode[] path) {
    DefaultMutableTreeNode[] dmtns = new DefaultMutableTreeNode[path.length];
    for (int i = 0; i < path.length; i++) {
      dmtns[i] = (DefaultMutableTreeNode) path[i];
    }
    return dmtns;
  }

}
