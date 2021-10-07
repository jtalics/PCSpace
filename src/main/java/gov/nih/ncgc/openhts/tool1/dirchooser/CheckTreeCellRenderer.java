package gov.nih.ncgc.openhts.tool1.dirchooser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

public class CheckTreeCellRenderer extends DefaultTreeCellRenderer {

    private final CheckTreeSelectionModelB checkTreeSelectionModel;
    private final DefaultTreeCellRenderer delegate;
//  TODO genericize Tri&Quad
    private final QuadstateCheckBox checkBox;
    private final JPanel panel;
    
    public CheckTreeCellRenderer(final DefaultTreeCellRenderer delegate, final CheckTreeSelectionModelB checkTreeSelectionModel) {
        this.delegate = delegate;
        this.checkTreeSelectionModel = checkTreeSelectionModel;
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        checkBox = new QuadstateCheckBox("");
        checkBox.setOpaque(false);
        setTextSelectionColor(Color.WHITE);
        setTextNonSelectionColor(Color.BLACK);
        setBackgroundNonSelectionColor(Color.WHITE);
        setTextNonSelectionColor(Color.BLACK);
        delegate.setLeafIcon(delegate.getClosedIcon());
        delegate.setOpenIcon(delegate.getClosedIcon());
    }

    @Override
    public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean selected, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
        final Component renderer = delegate.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        if (selected) {
            renderer.setBackground(delegate.getBackgroundSelectionColor());
            renderer.setForeground(delegate.getTextSelectionColor());
        }
        else {
            renderer.setBackground(backgroundSelectionColor);
            renderer.setForeground(textNonSelectionColor);
        }
        
        if (row < 0) {
            return new JLabel("this tree cell is not visible");
        }
        final TreePath path = tree.getPathForRow(row);
        if (path == null) {
            throw new RuntimeException("path is null for row = " + row);
        }
        checkBox.getQuadstateModel().setSelected(checkTreeSelectionModel.isPathSelected(path));
        checkBox.getQuadstateModel().setGrayed(checkTreeSelectionModel.hasSelectedDescendant(path));
        panel.removeAll();
        panel.add(checkBox, BorderLayout.WEST);
        panel.add(renderer, BorderLayout.CENTER);
        return panel;
    }
    
    private static final long serialVersionUID = 1L;
}
