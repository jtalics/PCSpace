package gov.nih.ncgc.openhts.tool1.pointsetManager;

import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Basis;
import gov.nih.ncgc.openhts.tool1.descriptorManager.BasisTreeNode;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class PointsetTreeModel extends DefaultTreeModel {
  private final Session session;

  public PointsetTreeModel(final Session session, final TreeNode arg0) {
    super(arg0);
    this.session = session;
  }

  @Override
  public void removeNodeFromParent(final MutableTreeNode node) {
    session.pointsetManager.lockRootHolder("removeNodeFromParent");
    super.removeNodeFromParent(node);
    session.pointsetManager.unlockRootHolder("removeNodeFromParent");
    // session.pointsetManager.storeRoot();
    // session.plotManager.storeObjects();
  }

  @Override
  public void insertNodeInto(final MutableTreeNode newChild,
      final MutableTreeNode parent, final int index) {
    session.pointsetManager.lockRootHolder("insertNodeInto");
    super.insertNodeInto(newChild, parent, index);
    session.pointsetManager.unlockRootHolder("insertNodeInto");
    // session.pointsetManager.storeRoot();
    // session.plotManager.storeObjects();
  }

  private BasisTreeNode[] getBasisNodes() {
    final List<BasisTreeNode> basisTreeNodes = new ArrayList<BasisTreeNode>();
    for (int i = 0; i < root.getChildCount(); i++) {
      DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) root.getChildAt(i);
      if (dmtn instanceof BasisTreeNode) {
        basisTreeNodes.add((BasisTreeNode) dmtn);
      }
    }
    return basisTreeNodes.toArray(new BasisTreeNode[basisTreeNodes.size()]);
  }

  public DefaultMutableTreeNode[] addBasis(final Basis basis) {
    final BasisTreeNode basisTreeNode = new BasisTreeNode(session, basis);
    insertNodeInto(basisTreeNode, (DefaultMutableTreeNode) root, root
        .getChildCount());
    session.pointsetManager.storeRoot();
    session.plotManager.storeObjects();
    return convertToDmtn(getPathToRoot(basisTreeNode));
  }

  private DefaultMutableTreeNode[] convertToDmtn(TreeNode[] path) {
    DefaultMutableTreeNode[] dmtns = new DefaultMutableTreeNode[path.length];
    for (int i = 0; i < path.length; i++) {
      dmtns[i] = (DefaultMutableTreeNode) path[i];
    }
    return dmtns;
  }

  public void removeBasis(final Basis basis) {
    DefaultMutableTreeNode[] path = searchByUserObject(
        (DefaultMutableTreeNode) root, basis);
    BasisTreeNode basisTreeNode = (BasisTreeNode) path[path.length - 1];
    removeNodeFromParent(basisTreeNode);
    // Basis was removed from all the Pointsets that used it
    List<PointsetTreeNode> children = new ArrayList<PointsetTreeNode>();
    for (int i = basisTreeNode.getChildCount() - 1; i >= 0; i--) {
      PointsetTreeNode pointsetTreeNode = (PointsetTreeNode) basisTreeNode
          .getChildAt(i);
      removeNodeFromParent(pointsetTreeNode);
      children.add(pointsetTreeNode);
    }
    for (PointsetTreeNode child : children) {
      addPointset((Pointset) child.getUserObject());
    }
    session.pointsetManager.storeRoot();
    session.plotManager.storeObjects();
  }

  public TreeNode[] addPointset(final Pointset pointset) {
    // Insert new pointset into the tree model
    final PointsetTreeNode ptn = new PointsetTreeNode(session, pointset);
    session.pointsetManager.addPointsetManagerListener(ptn);
    ptn.setModel(this);
    // Second level nodes contain Basis, determine to which node to add this
    // PointsetTreeNode.
    for (final BasisTreeNode basisTreeNode : getBasisNodes()) {
      // TODO: INEFFICIENT LINEAR SEARCH
      if (basisTreeNode.getUserObject() == pointset.getBasis()) {
        insertNodeInto(ptn, basisTreeNode, basisTreeNode.getChildCount());
        return getPathToRoot(ptn);
      }
    }
    insertNodeInto(ptn, (DefaultMutableTreeNode) root, root.getChildCount());
    session.pointsetManager.storeRoot();
    session.plotManager.storeObjects();
    return getPathToRoot(ptn);
  }

  public void removePointset(final Pointset pointset) {
    DefaultMutableTreeNode[] dmtns = searchByUserObject(
        (DefaultMutableTreeNode) root, pointset);
    removeNodeFromParent(dmtns[dmtns.length - 1]);
    session.pointsetManager.storeRoot();
    session.plotManager.storeObjects();
  }

  // public void removePointset(final Pointset pointset) {
  // // TODO: use searchByUserObject()
  // final Enumeration<DefaultMutableTreeNode> nodes = ((DefaultMutableTreeNode)
  // root)
  // .depthFirstEnumeration();
  // while (nodes.hasMoreElements()) {
  // final DefaultMutableTreeNode dmtn = nodes.nextElement();
  // if (pointset == dmtn.getUserObject()) {
  // removeNodeFromParent(dmtn);
  // session.pointsetManager.storeRoot();
  // session.plotManager.storeObjects();
  // return;
  // }
  // }
  // throw new RuntimeException("node not found");
  // }
  public void repaintBasisNode(final Basis basis) {
    for (final BasisTreeNode basisTreeNode : getBasisNodes()) {
      // TODO: INEFFICIENT LINEAR SEARCH
      if (basisTreeNode.getUserObject() == basis) {
        nodeStructureChanged(basisTreeNode); // TODO: vulgar?
        return;
      }
    }
  }

  DefaultMutableTreeNode[] searchByUserObject(DefaultMutableTreeNode startNode,
      Object userObject1) {
    // returns first occurence via DFS of userObject
    TreeNode[] path = null;
    Object userObject2 = startNode.getUserObject();
    if (userObject1 == null) {
      if (userObject2 == null) {
        path = getPathToRoot(startNode);
      }
    }
    else if (userObject2 != null &&
        userObject1 == userObject2) {
      path = getPathToRoot(startNode);
    }

    if (path == null) {
      int i = 0;
      int n = getChildCount(startNode);
      while ((i < n) && (path == null)) {
        path = searchByUserObject((DefaultMutableTreeNode) getChild(startNode,
            i), userObject1);
        i++;
      }
    }
    if (path == null) {
      return null;
    }
    return convertToDmtn(path);
  }

  public DefaultMutableTreeNode[] getPathToUserObject(Object userObject) {
    return searchByUserObject((DefaultMutableTreeNode) root, userObject);
  }

  @Override
  public DefaultMutableTreeNode getRoot() {
    return (DefaultMutableTreeNode) super.getRoot();
  }

  private static final long serialVersionUID = 1L;
}
