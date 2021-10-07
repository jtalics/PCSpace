package gov.nih.ncgc.openhts.tool1.dirchooser;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class CheckTreeManager extends MouseAdapter /* implements TreeSelectionListener*/ {

    private CheckTreeSelectionModelB checkTreeSelectionModel;
    private JTree tree;
    int hotspot = new JCheckBox().getPreferredSize().width;
    private static final boolean debug=false;
    
    /**
     * @param tree
     * @param CheckTreeSelectionModel if null, will be provided for you
     */
    public CheckTreeManager(final JTree tree, final CheckTreeModel checkTreeModel, final CheckTreeSelectionModelB checkTreeSelectionModel) {
        this.tree = tree;
        this.checkTreeSelectionModel = checkTreeSelectionModel;
        if (this.checkTreeSelectionModel == null) {
            this.checkTreeSelectionModel = new CheckTreeSelectionModelB(checkTreeModel, TreeSelectionModel.SINGLE_TREE_SELECTION);
            throw new RuntimeException();
        }
        // Add the current TreeCellRenderer to our renderer
        final CheckTreeCellRenderer checkTreeCellRenderer = new CheckTreeCellRenderer(
                new DefaultTreeCellRenderer(), this.checkTreeSelectionModel);
        this.tree.setCellRenderer(checkTreeCellRenderer);
        tree.addMouseListener(this);

        if (debug) {
            System.out.println("CheckTreeMananger INSTALLED TSLS follow " + checkTreeSelectionModel);
            for (final TreeSelectionListener tsl : checkTreeSelectionModel.getTreeSelectionListeners()) {
                System.out.println("tsl: " + tsl);
            }
        }
    }

    @Override
    public void mouseClicked(final MouseEvent me) {
        if (debug) {
          System.out.println("Mouse clicked");
        }
        // Get the path thru the tree based on the location of the mouse click
        final TreePath path = tree.getPathForLocation(me.getX(), me.getY());
        if (path == null) {
            return;
        }
        if (me.getX() > tree.getPathBounds(path).x + hotspot) {
            return;
        }

        // If this path is selected, remove the selection listener temporarily
        // so that we can change the selection manually.
        final boolean selected = checkTreeSelectionModel.isPathSelected(path);
        //checkTreeSelectionModel.removeTreeSelectionListener(this);

        // Toggle the selection for this single path
        if (selected) {
            checkTreeSelectionModel.removeSelectionPath(path);
        }
        else {
            checkTreeSelectionModel.addSelectionPath(path);
        }
        //checkTreeSelectionModel.addTreeSelectionListener(this);
        tree.treeDidChange();
    }

    public CheckTreeSelectionModelB getSelectionModel() {
        return checkTreeSelectionModel;
    }
}