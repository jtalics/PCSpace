package gov.nih.ncgc.openhts.tool1.dirchooser;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

// JTree has two selection mechanisms: normal (path/row) selection and checkbox selection
public class CheckTreeSelectionModelB extends DefaultTreeSelectionModel {

    private CheckTreeModel checkTreeModel;
    int treeSelectionMode;
    private final static boolean debug = false;
    
    /**
     * @param model
     * @param treeSelectionMode
     */
    CheckTreeSelectionModelB(final CheckTreeModel model, final int treeSelectionMode) {
        this.checkTreeModel = model;
        this.treeSelectionMode = treeSelectionMode;
        setSelectionMode(treeSelectionMode);
    }

    @Override
    public boolean isPathSelected(final TreePath path) {
        // add your logic here
        return super.isPathSelected(path);
    }

    boolean hasSelectedDescendant(final TreePath path) {
        // TODO: not very efficient, but fast enough with lazy loading
        final List<TreePath> descendants = checkTreeModel.getDescendantPaths(path, new ArrayList<TreePath>());
        if (debug) {
          System.out.println("path: " + path + " has descendant count ="+descendants.size());
        }
        for (final TreePath descendant : descendants) {
            if (isPathSelected(descendant)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addSelectionPaths(final TreePath[] paths) {
        if (debug) {
            for (final TreePath path : paths) {
                System.out.println("adding selection path: " + path);
            }
        }
        super.addSelectionPaths(paths);
    }

    @Override
    public void removeSelectionPaths(final TreePath[] paths) {
        if (debug) {
            for (final TreePath path : paths) {
                System.out.println("removing selection path: " + path);
            }
        }
        super.removeSelectionPaths(paths);
    }

    private static final long serialVersionUID = 1L;
}
