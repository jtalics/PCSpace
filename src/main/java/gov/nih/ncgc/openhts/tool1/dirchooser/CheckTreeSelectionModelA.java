package gov.nih.ncgc.openhts.tool1.dirchooser;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Stack;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class CheckTreeSelectionModelA extends DefaultTreeSelectionModel {

    private static final long serialVersionUID = 1L;
    private TreeModel model;
    int treeSelectionMode;

    CheckTreeSelectionModelA(final TreeModel model, final int treeSelectionMode) {
        this.model = model;
        this.treeSelectionMode = treeSelectionMode;
        setSelectionMode(treeSelectionMode);
    }

    // Tests whether there is ANY unselected node in the subtree of given path
    boolean isPartiallySelected(final TreePath path) {
        // Was this path selected by user?
        if (isPathSelected(path, true)) {
            return false;
        }
        // Get all paths that are currently selected
        final TreePath[] selectionPaths = getSelectionPaths();
        if (selectionPaths == null || selectionPaths.length == 0) {
            return false;
        }
        for (final TreePath element : selectionPaths) {
            if (isDescendant(element, path)) {
                return true;
            }
        }
        return false;
    }

    // Tells whether given path is selected.
    // If dig is true, then a path is assumed to be selected if
    // one of its ancestor is selected.
    boolean isPathSelected(final TreePath path, boolean dig) {
        // NOTE: this rendering: view not being necessarily 1:1 with model
        if (!dig) { // dig: roots are down imagery
            // Only interested in this path
            return super.isPathSelected(path);
        }
        // Go toward root and see if there is any node selected along the way
        TreePath curpath = path;
        while (curpath != null && !super.isPathSelected(curpath)) {
            curpath = curpath.getParentPath();
        }
        if (curpath != null) {
            return true; // A parent along the path is selected
            // Descendants will *appear* to be selected
        }
        if (treeSelectionMode == TreeSelectionModel.SINGLE_TREE_SELECTION) {
            // *Appear* to select all ancestors to a selected path
            for (final Enumeration e = ((DefaultMutableTreeNode) path.getLastPathComponent()).children(); e.hasMoreElements();) {
                final TreePath childPath = path.pathByAddingChild(e.nextElement());
                if (super.isPathSelected(childPath)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    // Is path1 descendant of path2
    private boolean isDescendant(final TreePath path1, final TreePath path2) {
        final Object obj1[] = path1.getPath();
        final Object obj2[] = path2.getPath();
        for (int i = 0; i < obj2.length; i++) {
            if (obj1[i] != obj2[i]) {
                return false;
            }
        }
        return true;
    }

    /*
     * @Override public void setSelectionPaths(TreePath[] paths) { throw new UnsupportedOperationException("not implemented"); }
     */

    @Override
    public void setSelectionPath(final TreePath path) {
        if (treeSelectionMode != TreeSelectionModel.SINGLE_TREE_SELECTION) {
            throw new RuntimeException("unexpected treeSelectionMode");
        }
        // We don't need to clear the paths since this is SINGLE_TREE_SELECTION
        super.setSelectionPath(path);
        // Select all paths inside this node

    }

    @Override
    public void addSelectionPaths(final TreePath[] paths) {
        for (final TreePath path : paths) {
            final TreePath[] selectionPaths = getSelectionPaths();
            if (selectionPaths == null || selectionPaths.length == 0) {
                break;
            }
            final ArrayList<TreePath> toBeRemoved = new ArrayList<TreePath>();
            for (final TreePath element0 : selectionPaths) {
                if (isDescendant(element0, path)) {
                    toBeRemoved.add(element0);
                }
            }
            super.removeSelectionPaths(toBeRemoved.toArray(new TreePath[0]));
        }

        // Process siblings
        for (TreePath path : paths) {
            // If all siblings are selected then:
            // unselect them and select parent recursively
            // Otherwise: just select that path.
            TreePath allSibsPath = null;
            while (areAllSiblingsSelected(path)) {
                allSibsPath = path;
                if (path.getParentPath() == null) {
                    break;
                }
                path = path.getParentPath();
            }
            // 
            if (allSibsPath != null) {
                if (allSibsPath.getParentPath() != null) {
                    addSelectionPath(allSibsPath.getParentPath());
                }
                else {
                    if (!isSelectionEmpty()) {
                        removeSelectionPaths(getSelectionPaths());
//System.out.println("removed all selection paths");
                    }
//System.out.println("Add Selection: " + path);
                    super.addSelectionPaths(new TreePath[] { allSibsPath });
                }
            }
            else {
//System.out.println("Add Selection: " + path);
                super.addSelectionPaths(new TreePath[] { path });
            }
        }
    }

    // Tells whether ALL siblings of given path are selected.
    private boolean areAllSiblingsSelected(final TreePath path) {
        final TreePath parent = path.getParentPath();
        if (parent == null) {
            return true;
        }
        final Object node = path.getLastPathComponent();
        final Object parentNode = parent.getLastPathComponent();

        final int childCount = model.getChildCount(parentNode);
        for (int i = 0; i < childCount; i++) {
            final Object childNode = model.getChild(parentNode, i);
            if (childNode == node) {
                continue;
            }
            if (!isPathSelected(parent.pathByAddingChild(childNode))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void removeSelectionPaths(final TreePath[] paths) {
for (final TreePath path : paths) {
            //System.out.println("Processing: "+path);
            if (path.getPathCount() == 1) {
                // Given path is root
                super.removeSelectionPaths(new TreePath[] { path });
//System.out.println("Removed root: "+path);
            }
            else {
                // Given path is not root.
                // If any ancestor node of given path is selected then:
                // unselect ancestor and select all ancestor's descendants 
                // (except given path and given path's descendants).
                // Otherwise: just unselect the given path
                
                // Stack holds all unselected ancestors of this path
                final Stack<TreePath> stackOfPaths = new Stack<TreePath>();
                TreePath parent = path.getParentPath();
                while (parent != null && !isPathSelected(parent)) {
                    stackOfPaths.push(parent);
//System.out.println("Pushed " + parent);
                    parent = parent.getParentPath();
                }
                if (parent != null) {
                    // Last parent exists and was selected, stack it too
                    stackOfPaths.push(parent);
//System.out.println("Pushed " + parent);
                }
                else {
                    // Arrived at root and found no selections.
                    // Unselect given path.
                    super.removeSelectionPaths(new TreePath[] { path });
//System.out.println("Removed " + parent);
//System.out.println("returning");
                    return;
                }

                // For every entry on the stack, select its children
                while (!stackOfPaths.isEmpty()) {
                    final TreePath popped = stackOfPaths.pop();
                    final TreePath peekPath = stackOfPaths.isEmpty() ? path : stackOfPaths.peek();
                    final Object poppedNode = popped.getLastPathComponent();
                    final Object peekNode = peekPath.getLastPathComponent();
                    final int childCount = model.getChildCount(poppedNode);
                    for (int j = 0; j < childCount; j++) {
                        final Object childNode = model.getChild(poppedNode, j);
                        if (childNode != peekNode) {
                            final TreePath childPath = popped.pathByAddingChild(childNode);
                            super.addSelectionPaths(new TreePath[] { childPath });
//System.out.println("Added " + childPath);
                        }
                    }
                }
                super.removeSelectionPaths(new TreePath[] { parent });
//System.out.println("Removed " + parent);
            }
        }
//System.out.println("Exited removeSelectionPaths()");
    }
}
