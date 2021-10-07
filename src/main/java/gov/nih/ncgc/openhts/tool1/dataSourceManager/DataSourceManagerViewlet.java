package gov.nih.ncgc.openhts.tool1.dataSourceManager;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import com.jidesoft.swing.JidePopupMenu;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.util.ContextTreePopupListener;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class DataSourceManagerViewlet extends JPanel {
  public final JTree tree = new JTree() {
    @Override
    public String getToolTipText(MouseEvent evt) {
      if (getRowForLocation(evt.getX(), evt.getY()) == -1) {
        return null;
      }
      TreePath curPath = getPathForLocation(evt.getX(), evt.getY());
      Object object = curPath.getLastPathComponent();
      if (object instanceof DataSourceTreeNode) {
        return ((DataSourceTreeNode) object).getToolTipText();
      }
      return null;
    }

    private static final long serialVersionUID = 1L;
  };
  Session session;
  private final DefaultMutableTreeNode servers;
  private final DefaultMutableTreeNode local;
//  private final DefaultMutableTreeNode dynamic;
  private final DataSourceTreeModel dataSourceTreeModel;
  private final DefaultMutableTreeNode root;
  private DataSourceManager dataSourceManager;
  // Used by Robot:
  public JidePopupMenu popup;

  public DataSourceManagerViewlet(final Session session,
      final DataSourceManager dataSourceManager) {
    this.session = session;
    this.dataSourceManager = dataSourceManager;
    setLayout(new BorderLayout());
    // dataSourceManager.addDataSourceManagerListener(this);
    root = dataSourceManager.getRoot();
    servers = (DefaultMutableTreeNode) root.getChildAt(0);
    local = (DefaultMutableTreeNode) root.getChildAt(1);
//    dynamic = (DefaultMutableTreeNode) root.getChildAt(2);
    dataSourceTreeModel = session.dataSourceManager.getTreeModel();
    add(tree);
    ToolTipManager.sharedInstance().registerComponent(tree);
    final ContextTreePopupListener ctpl = new ContextTreePopupListener(session,
        tree) {
      @Override
      public JidePopupMenu createContextMenu() {
        popup = new JidePopupMenu();
        popup.add(new JLabel("Data source management"));
        popup.add(new JSeparator());
        final int nSelectedPaths = tree.getSelectionPaths().length;
        if (nSelectedPaths == 1) {
          TreePath treePath = tree.getSelectionPaths()[0];
          Object obj = treePath.getLastPathComponent();
          if (obj instanceof DataSourceTreeNode) {
            popup.add(new JSeparator());
            popup.add(new JMenuItem(
                session.actionManager.dataSourceRemoveAction));
            popup.add(new JMenuItem(
                session.actionManager.dataSourceExportAction));
            popup
                .add(new JMenuItem(session.actionManager.pointsetCreateAction));
          }
          else if (obj instanceof DefaultMutableTreeNode) {
            switch (treePath.getPathCount()) {
            case 1: // root
              return null;
            case 2: // type (e.g. local)
              popup.add(new JMenuItem(
                  session.actionManager.dataSourceCreateAction));
              popup.add(new JSeparator());
              popup.add(new JMenuItem(
                  session.actionManager.dataSourceImportAction));
              break;
            }
          }
        }
        else if (nSelectedPaths > 1) {
          popup.add(new JLabel("multiple selections -- no actions apply"));
        }
        return popup;
      }
    };
    tree.addMouseListener(ctpl);
    tree.addKeyListener(ctpl);
    tree.setRootVisible(true);
    tree.setAutoscrolls(true);
    tree.setShowsRootHandles(true);
    tree.setCellRenderer(new DataSourceTreeCellRenderer(session));
    tree.setModel(dataSourceTreeModel);
    Session.addFocusBorder(tree, tree);
  }

  public void addDataSource(final DataSource dataSource) {
    final DataSourceTreeNode dstn = new DataSourceTreeNode(session, dataSource);
    dataSourceTreeModel.insertNodeInto(dstn, local, local.getChildCount());
    dataSourceManager.storeRoot();
    tree.scrollPathToVisible(new TreePath(dstn.getPath()));
  }

  public void dataSourceChanged(final DataSource dataSource) {
    dataSourceTreeModel.reload();
  }

  public void dataSourceRemoved(final DataSource dataSource) {
    final Enumeration<DefaultMutableTreeNode> nodes = ((DefaultMutableTreeNode) dataSourceTreeModel
        .getRoot()).depthFirstEnumeration();
    while (nodes.hasMoreElements()) {
      final DefaultMutableTreeNode node = nodes.nextElement();
      final Object userObject = node.getUserObject();
      if (userObject == null) {
        throw new RuntimeException("null userObject");
      }
      if (userObject == dataSource) {
        dataSourceTreeModel.removeNodeFromParent(node);
        dataSourceManager.storeRoot();
        // Asssume no duplicates
        return;
      }
      else {
        // root or level one
      }
    }
    new RuntimeException("Could not find node for dataSource = " + dataSource);
  }

  public DataSource getSelectedDataSource() {
    final TreePath[] selectedPaths = tree.getSelectionPaths();
    if (selectedPaths == null || selectedPaths.length != 1) {
      return null;
    }
    return (DataSource) ((DataSourceTreeNode) selectedPaths[0]
        .getLastPathComponent()).getUserObject();
  }

  // Used by robot
  public Point getPointForLocalNode() {
    final Rectangle r = tree.getRowBounds(tree.getRowForPath(new TreePath(
        new Object[] { root, local })));
    final Point p = tree.getLocationOnScreen();
    return new Point(p.x + r.x + r.width / 2, p.y + r.y + r.height / 2);
  }

  public void selectDataSource(DataSource dataSource) {
    selectPath(dataSourceTreeModel.getDataSourceTreeNode(dataSource));
  }
  
  private static final long serialVersionUID = 1L;

    public void selectPath(TreeNode[] path) {
      TreePath treePath = new TreePath(path);
      tree.expandPath(treePath);
      tree.scrollPathToVisible(treePath);
      tree.setSelectionPath(treePath);
    }
}
