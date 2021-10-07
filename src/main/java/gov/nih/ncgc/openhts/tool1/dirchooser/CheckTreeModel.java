package gov.nih.ncgc.openhts.tool1.dirchooser;

import java.util.List;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


public class CheckTreeModel extends DefaultTreeModel {

    /**
   * 
   */
  private static final long serialVersionUID = 1L;

    public CheckTreeModel(final TreeNode root) {
        super(root);
        // TODO Auto-generated constructor stub
    }

    public CheckTreeModel(final TreeNode root, final boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
        // TODO Auto-generated constructor stub
    }

    /**
     * Returns all paths that are descendants to given path
     * 
     * @param path
     * @param pathList
     * @return
     */
    List<TreePath> getDescendantPaths(final TreePath path, final List<TreePath> pathList) {
        final Object node = path.getLastPathComponent();
        if (isLeaf(node)) {
            return pathList;
        }
        final int num = getChildCount(node);
        for (int i = 0; i < num; i++) {
            final TreePath childPath = path.pathByAddingChild(getChild(node, i));
            pathList.add(childPath);
            getDescendantPaths(childPath, pathList);
        }
        return pathList;
    }

}
