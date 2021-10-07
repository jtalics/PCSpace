package gov.nih.ncgc.openhts.tool1.descriptorManager;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import gov.nih.ncgc.openhts.tool1.GlobalSettings;
import gov.nih.ncgc.openhts.tool1.Session;

/**
 * Descriptors are first-level, bases are second-level
 * 
 * @author talafousj
 */
public class DescriptorTreeModel extends DefaultTreeModel {
  enum Mode {
    DescriptorLeaf, BasisLeaf
  }

  private Mode mode;
  transient private Session session;
  private final String TREE_MODEL_MODE = "gov.nih.ncgc.openhts.tool1.ui.DescriptorTreeModelMode";

  public DescriptorTreeModel(final Session session, final TreeNode arg0) {
    super(arg0);
    this.session = session;
    final String s = GlobalSettings.getInstance().getProperty(TREE_MODEL_MODE);
    if (s != null) {
      if (s.equals(Mode.BasisLeaf.name())) {
        mode = Mode.BasisLeaf;
        GlobalSettings.getInstance().putProperty(TREE_MODEL_MODE,
            Mode.BasisLeaf.name());
      }
      else if (s.equals(Mode.DescriptorLeaf.name())) {
        mode = Mode.DescriptorLeaf;
        GlobalSettings.getInstance().putProperty(TREE_MODEL_MODE,
            Mode.DescriptorLeaf.name());
      }
      else {
        throw new RuntimeException("bad mode: " + s);
      }
    }
    else {
      mode = Mode.DescriptorLeaf;
      GlobalSettings.getInstance().putProperty(TREE_MODEL_MODE,
          Mode.DescriptorLeaf.name());
    }
  }

  @Override
  public void removeNodeFromParent(final MutableTreeNode node) {
    session.descriptorManager.lockRootHolder("lock");
    super.removeNodeFromParent(node);
    session.descriptorManager.unlockRootHolder("unlock");
    // session.descriptorManager.storeRoot();
  }

  @Override
  public void insertNodeInto(final MutableTreeNode newChild,
      final MutableTreeNode parent, final int index) {
    session.descriptorManager
        .lockRootHolder("locked in DescriptorTreeModel.insertNodeInto()");
    super.insertNodeInto(newChild, parent, index);
    session.descriptorManager
        .unlockRootHolder("UNlocked in DescriptorTreeModel.insertNodeInto()");
  }

  public BasisTreeNode[] getBasisNodes() {
    final List<BasisTreeNode> basisTreeNodes = new ArrayList<BasisTreeNode>();
    // TODO: use searchByUserObject()
    final Enumeration<DefaultMutableTreeNode> nodes = ((DefaultMutableTreeNode) root)
        .depthFirstEnumeration();
    while (nodes.hasMoreElements()) {
      final DefaultMutableTreeNode dmtn = nodes.nextElement();
      if (dmtn instanceof BasisTreeNode) {
        basisTreeNodes.add((BasisTreeNode) dmtn);
      }
    }
    return basisTreeNodes.toArray((new BasisTreeNode[basisTreeNodes.size()]));
  }

  public DescriptorTreeNode[] getDescriptorNodes() {
    final List<DescriptorTreeNode> descriptorTreeNodes = new ArrayList<DescriptorTreeNode>();
    // TODO: use searchByUserObject()
    final Enumeration<DefaultMutableTreeNode> nodes = ((DefaultMutableTreeNode) root)
        .depthFirstEnumeration();
    while (nodes.hasMoreElements()) {
      final DefaultMutableTreeNode dmtn = nodes.nextElement();
      if (dmtn instanceof DescriptorTreeNode) {
        descriptorTreeNodes.add((DescriptorTreeNode) dmtn);
      }
    }
    return descriptorTreeNodes
        .toArray((new DescriptorTreeNode[descriptorTreeNodes.size()]));
  }

  public TreeNode[] addBasis(final Basis basis) {
    // TODO: INEFFICIENT LINEAR SEARCHES
    final BasisTreeNode basisTreeNode = new BasisTreeNode(session, basis);
    switch (mode) {
    case BasisLeaf:
      boolean orphan = true;
      for (final Descriptor descriptor : basis.getDescriptors()) {
        for (final DescriptorTreeNode descriptorTreeNode : getDescriptorNodes()) {
          if (descriptor == (Descriptor) descriptorTreeNode.getUserObject()) {
            insertNodeInto(basisTreeNode, descriptorTreeNode,
                descriptorTreeNode.getChildCount());
            session.descriptorManager.storeRoot();
            orphan = false;
            break;
          }
        }
      }
      if (orphan) {
        insertNodeInto(basisTreeNode, (DefaultMutableTreeNode) root
            .getChildAt(0), root.getChildAt(0).getChildCount());
      }
      session.descriptorManager.storeRoot();
      break;
    case DescriptorLeaf:
      // Basis must "claim" its descriptors
      DefaultMutableTreeNode[] path = searchByUserObject((DefaultMutableTreeNode) root,
          basis);
      if (path != null) {
        // already there
        return path;
      }
      insertNodeInto(basisTreeNode, (DefaultMutableTreeNode) root, root
          .getChildCount());
      for (final Descriptor descriptor : basis.getDescriptors()) {
        insertNodeInto(new DescriptorTreeNode(descriptor), basisTreeNode,
            basisTreeNode.getChildCount());
        path = searchByUserObject((DefaultMutableTreeNode) root.getChildAt(0), descriptor);
        if (path != null) {
          removeNodeFromParent(path[path.length - 1]);
        }
      }
      session.descriptorManager.storeRoot();
      break;
    default:
      throw new RuntimeException("treeModel mode not set properly");
    }
    return this.getPathToRoot(basisTreeNode);
  }

  public void removeBasis(final Basis basis) {
    DefaultMutableTreeNode[] dmtns = searchByUserObject((DefaultMutableTreeNode) root,
        basis);
    removeNodeFromParent(dmtns[dmtns.length - 1]);
    for (Descriptor descriptor : basis.getDescriptors()) {
      addDescriptor(descriptor);
    }
    session.descriptorManager.storeRoot();
  }

  public TreeNode[] addDescriptor(final Descriptor descriptor) {
    // TODO: INEFFICIENT LINEAR SEARCHES
    DescriptorTreeNode descriptorTreeNode;
    switch (mode) {
    case BasisLeaf:
      DefaultMutableTreeNode[] path;
      // Insert new DescriptorTreeNode into first-level
      if (null != (path = searchByUserObject((DefaultMutableTreeNode) root, descriptor))) {
        // already there
        return path;
      }
      descriptorTreeNode = new DescriptorTreeNode(descriptor);
      insertNodeInto(descriptorTreeNode, (DefaultMutableTreeNode) root, root
          .getChildCount());
      // Create children BasisTreeNodes, but if the child already exists
      // as an orphan, remove that BasisTreeNode
      for (final Basis basis : session.descriptorManager.getBases()) {
        for (final Descriptor d : basis.getDescriptors()) {
          if (d == descriptor) {
            // basis contains the descriptor
            insertNodeInto(descriptorTreeNode,
                new BasisTreeNode(session, basis), descriptorTreeNode
                    .getChildCount());
            if (null != (path = searchByUserObject((DefaultMutableTreeNode) root
                .getChildAt(0), basis))) {
              removeNodeFromParent(path[path.length - 1]);
              break;
            }
          }
        }
      }
      session.descriptorManager.storeRoot();
      break;
    case DescriptorLeaf:
      // Find the BasisTreeNode that is parent to the new DescriptorTreeNode
      descriptorTreeNode = new DescriptorTreeNode(descriptor);
      for (final BasisTreeNode basisTreeNode : getBasisNodes()) {
        if (basisTreeNode.getUserObject() == descriptor) {
          insertNodeInto(descriptorTreeNode, basisTreeNode, basisTreeNode
              .getChildCount());
          return (getPathToRoot(descriptorTreeNode));
        }
      }
      // Orphan
      insertNodeInto(descriptorTreeNode, (DefaultMutableTreeNode) root
          .getChildAt(0), ((DefaultMutableTreeNode) root.getChildAt(0))
          .getChildCount());
      // break;
      session.descriptorManager.storeRoot();
      break;
    default:
      throw new RuntimeException("treeModel mode not set properly");
    }
    return getPathToRoot(descriptorTreeNode);
  }

  public void removeDescriptor(final Descriptor descriptor) {
    DefaultMutableTreeNode[] dmtns = searchByUserObject((DefaultMutableTreeNode) root,
        descriptor);
    removeNodeFromParent(dmtns[dmtns.length - 1]);
    session.descriptorManager.storeRoot();
  }

  private static final long serialVersionUID = 1L;

  public DescriptorTreeModel.Mode getMode() {
    return mode;
  }

  public void setMode(final Mode mode) {
    if (mode == null) {
      throw new RuntimeException("mode may not be null");
    }
    this.mode = mode;
    nodeChanged(root);
    nodeChanged(root.getChildAt(0));
  }

  public void clear() {
    final DefaultMutableTreeNode orphan = (DefaultMutableTreeNode) root
        .getChildAt(0);
    orphan.removeAllChildren();
    ((DefaultMutableTreeNode) root).removeAllChildren();
    ((DefaultMutableTreeNode) root).add(orphan);
    reload();
  }

  public void refreshBasisTreeNode(final Basis basis) {
    for (final BasisTreeNode basisTreeNode : getBasisNodes()) {
      if (basis == basisTreeNode.getUserObject()) {
        this.nodeChanged(basisTreeNode);
      }
    }
  }

  public void checkAccuracy() {
    // TODO:
    // managers have no duplicate user objects
    // all manager user objects have exactly one node of the correct type
    // all nodes have the right user object
    // tree connectivity is same as the user objects connectivity
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
    else if (userObject2 != null
        && userObject1 == userObject2) {
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

//  DefaultMutableTreeNode[] searchByUserObject(DefaultMutableTreeNode startNode,
//      Object userObject1) {
//    TreeNode[] path = null;
//    Object userObject2 = startNode.getUserObject();
//    if (userObject1.getClass() == userObject2.getClass()
//        && userObject1.equals(userObject2)) {
//      // We found it
//      path = getPathToRoot(startNode);
//    }
//    else {
//      int i = 0;
//      int n = getChildCount(startNode);
//      while ((i < n) && (path == null)) {
//        path = searchByUserObject((DefaultMutableTreeNode) getChild(startNode, i),
//            userObject1);
//        i++;
//      }
//    }
//    if (path != null) {
//      return convertToDmtn(path);
//    }
//    return null;
//  }


  private DefaultMutableTreeNode[] convertToDmtn(TreeNode[] path) {
    DefaultMutableTreeNode[] dmtns = new DefaultMutableTreeNode[path.length];
    for (int i = 0; i < path.length; i++) {
      dmtns[i] = (DefaultMutableTreeNode) path[i];
    }
    return dmtns;
  }
}
