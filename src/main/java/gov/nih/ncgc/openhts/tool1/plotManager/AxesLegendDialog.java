package gov.nih.ncgc.openhts.tool1.plotManager;

import java.awt.BorderLayout;
import java.util.Enumeration;
import javax.help.CSH;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;
import gov.nih.ncgc.openhts.tool1.dialogManager.DialogManager;

public class AxesLegendDialog extends AbstractDialog {
  private Axes axes;
  private final JTree tree;

  public AxesLegendDialog(final Session session) {
    super(session);
    this.axes = session.plotManager.subject.axes;
    tree = new JTree();
    tree.setToolTipText("tree");
    CSH.setHelpIDString(tree, "legend_axes_tree");
    addWindowListener(DialogManager.getInstance(session));
    addComponentListener(DialogManager.getInstance(session));
    setResizable(true);
    final JPanel topPanel = new JPanel();
    add(topPanel, BorderLayout.CENTER);
    CSH.setHelpIDString(topPanel, "chemspace_legend_axes");
    topPanel.setLayout(new BorderLayout(0, 5));
//    final JCheckBox checkBox = new JCheckBox(
//        "All data sources are in the same chemistry space");
//    checkBox
//        .setToolTipText("if checked, all data sources must have the same representation");
//    CSH.setHelpIDString(checkBox, "chemspace_legend_axes_checkbox");
//    checkBox.setSelected(Axes.pointsetsAreInSameSpace);
//    checkBox.addActionListener(new ActionListener() {
//      public void actionPerformed(final ActionEvent e) {
//        try {
//          if (Axes.pointsetsAreInSameSpace == true) {
//            Axes.pointsetsAreInSameSpace = false;
//            rebuildLegend();
//          }
//          else {
////            if (!Pointset.checkBasis(session)) {
////              session.error(
////                  "All data sources are not in the same chemistry space",
////                  "mapping_error");
////              return;
////            }
//            if (!Pointset.checkAxisColMapping(session)) {
//              session.error(
//                  "All data sources do not have same axis-to-col mapping.",
//                  "mapping_error");
//              return;
//            }
//            Axes.pointsetsAreInSameSpace = true;
//            rebuildLegend();
//          }
//          session.plotter.subject.axes.buildLabelsAndTics(); // axis labels may
//                                                              // have changed
//          session.plotter.canvas.setStale(true);
//          session.plotter.canvas.refresh();
//        }
//        catch (final Throwable ex) {
//          session.error(
//              "While changing same chemistry space constraint: " + ex,
//              "chemspace_legend_axes_checkbox_error");
//        }
//      }
//    });
//    topPanel.add(checkBox, BorderLayout.NORTH);
    rebuildLegend();
    final JScrollPane sp = new JScrollPane(tree);
    CSH.setHelpIDString(sp, "legend_axes");
    topPanel.add(sp, BorderLayout.CENTER);
    setHelpID("help_legend_axes_button");
    add(getButtonPanel(),BorderLayout.SOUTH);
  }

  /**
   * Small routine that will make node out of the first entry in the array, then
   * make nodes out of subsequent entries and make them child nodes of the first
   * one. The process is repeated recursively for entries that are arrays.
   */
  private static DefaultMutableTreeNode processHierarchy(final Object[] hierarchy) {
    final DefaultMutableTreeNode node = new DefaultMutableTreeNode(hierarchy[0]);
    DefaultMutableTreeNode child;
    for (int i = 1; i < hierarchy.length; i++) {
      final Object nodeSpecifier = hierarchy[i];
      if (nodeSpecifier instanceof Object[]) {
        child = processHierarchy((Object[]) nodeSpecifier);
      }
      else {
        child = new DefaultMutableTreeNode(nodeSpecifier); // Ie Leaf
      }
      node.add(child);
    }
    return node;
  }

  public void rebuildLegend() {
return;
// TODO: uncomment below
//    if (null == session.dialogManager.getAxesLegendDialog()) {
//      return; // user not looking at legend right now.
//    }
//    final Object[] hierarchy = new Object[session.plotter.subject.axes.getDim() + 1];
//    hierarchy[0] = "Assignments for axes to column names from data source";
//    for (int j = 0; j < session.plotter.subject.axes.getDim(); j++) {
//      Object[] node;
//      Pointset pointset;
//      if (Axes.dataSetsAreInSameSpace) {
//        if (session.pointsetManager.count() <= 0) {
//          node = new Object[1];
//        }
//        else {
//          node = new Object[2];
//          pointset = session.pointsetManager.getPointset(0);
//          node[1] = pointset.axisToDescriptor[j];
//        }
//        node[0] = Axes.axisNames[j];
//      }
//      else {
//        node = new Object[1 + session.pointsetManager.count()];
//        node[0] = Axes.axisNames[j];
//        for (int i = 0; i < session.pointsetManager.count(); i++) {
//          pointset = session.pointsetManager.getPointset(i);
//          node[i + 1] = pointset.coordLongColNames[pointset.axisToDescriptor[j]]
//              + " from " + pointset.name;
//        }
//      }
//      hierarchy[j + 1] = node;
//    }
//    // JDIALOG: content pane is for user, root pane is for the system
//    final DefaultTreeModel treeModel = new DefaultTreeModel(
//        processHierarchy(hierarchy));
//    tree.setModel(treeModel);
//    expandAll(new TreePath(treeModel.getRoot()), true);
//    tree.invalidate();
//    tree.repaint();
//    tree.validate();
  }

  /**
   *
   */
  private void expandAll(final TreePath parent, final boolean expand) {
    // Traverse children
    final TreeNode node = (TreeNode) parent.getLastPathComponent();
    if (node.getChildCount() >= 0) {
      for (final Enumeration e = node.children(); e.hasMoreElements();) {
        final TreeNode n = (TreeNode) e.nextElement();
        final TreePath path = parent.pathByAddingChild(n);
        expandAll(path, expand);
      }
    }
    // Expansion or collapse must be done bottom-up
    if (expand) {
      tree.expandPath(parent);
    }
    else {
      tree.collapsePath(parent);
    }
  }

  @Override
  protected boolean apply() {
    new RuntimeException("todo: apply");
    return false;

  }
  private static final long serialVersionUID = 1L;
}
