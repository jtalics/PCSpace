package gov.nih.ncgc.openhts.tool1.pointsetManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import com.jidesoft.swing.JidePopupMenu;
import com.jidesoft.swing.JideScrollPane;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Basis;
import gov.nih.ncgc.openhts.tool1.descriptorManager.BasisTreeNode;
import gov.nih.ncgc.openhts.tool1.util.ContextTreePopupListener;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
class PointsetManagerViewlet extends JTabbedPane implements
    PointsetManagerListener, TreeSelectionListener {
  final JTree tree = new JTree() {
    @Override
    public String getToolTipText(MouseEvent evt) {
      if (getRowForLocation(evt.getX(), evt.getY()) == -1) {
        return null;
      }
      TreePath curPath = getPathForLocation(evt.getX(), evt.getY());
      Object node = curPath.getLastPathComponent();
      if (node instanceof BasisTreeNode) {
        return ((BasisTreeNode) node).getToolTipText();
      }
      else if (node instanceof PointsetTreeNode) {
        return ((PointsetTreeNode) node).getToolTipText();
      }
      else {
        return null;
      }
    }

    private static final long serialVersionUID = 1L;
  };
  private final Session session;
  private PointsetTreeModel pointsetTreeModel;
  private JPanel filtersPanel;
  private HashMap<Object, PointFilterViewlet> objectToFilterViewlet;

  public PointsetManagerViewlet(final Session session) {
    this.session = session;
    this.addTab("Pointsets", getTreePanel());
    this.addTab("Filters", getPointFilterViewlet());
  }

  private JideScrollPane getPointFilterViewlet() {
    filtersPanel = new JPanel();
    filtersPanel.setLayout(new BoxLayout(filtersPanel, BoxLayout.PAGE_AXIS));
    filtersPanel.setBackground(Color.WHITE);
    JideScrollPane scrollPane = new JideScrollPane(filtersPanel);
    scrollPane
        .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    return scrollPane;
  }

  private JPanel getTreePanel() {
    JPanel treePanel = new JPanel(new BorderLayout());
    pointsetTreeModel = session.pointsetManager.getTreeModel();
    // root = (DefaultMutableTreeNode) pointsetTreeModel.getRoot();
    tree.setModel(pointsetTreeModel);
    treePanel.add(new JideScrollPane(tree));
    ToolTipManager.sharedInstance().registerComponent(tree);
    tree.addTreeSelectionListener(this);
    tree.addMouseListener(new CheckBoxTreeNodeListener(session, tree,
        pointsetTreeModel));
    tree.addMouseListener(new ContextTreePopupListener(session, tree) {
      @Override
      public JidePopupMenu createContextMenu() {
        final int nSelectedPaths = tree.getSelectionPaths().length;
        if (nSelectedPaths == 1) {
          return createPopup(tree.getSelectionPaths()[0]);
        }
        else if (nSelectedPaths > 1) {
          return createPopup(tree.getSelectionPaths());
        }
        throw new IllegalStateException("nSelectedPaths = " + nSelectedPaths);
      }

      private JidePopupMenu createPopup(TreePath treePath) {
        final JidePopupMenu popup = new JidePopupMenu();
        Object obj = treePath.getLastPathComponent();
        if (obj instanceof BasisTreeNode) {
          final Basis basis = (Basis) ((BasisTreeNode) obj).getUserObject();
          popup.add(new JLabel("Basis"));
          popup.add(new JSeparator());
          popup.add(new JMenuItem(
              session.actionManager.basisSetPlotterModeAction));
          popup.add(new JMenuItem(session.actionManager.basisRemoveAction));
          popup.add(new JMenuItem(session.actionManager.basisExportAction));
          popup.add(new JMenuItem(session.actionManager.basisModifyAction));
        }
        else if (obj instanceof PointsetTreeNode) {
          popup.add(new JLabel("Pointset"));
          popup.add(new JSeparator());
          popup.add(new JMenuItem(session.actionManager.pointsetShowAction));
          popup.add(new JMenuItem(session.actionManager.pointsetHideAction));
          popup.add(new JSeparator());
          popup.add(new JMenuItem(session.actionManager.pointsetModifyAction));
          popup.add(new JMenuItem(session.actionManager.pointsetReplaceAction));
          popup.add(new JMenuItem(session.actionManager.pointsetRenameAction));
          popup.add(new JSeparator());
          popup.add(new JMenuItem(session.actionManager.pointsetRemoveAction));
          popup.add(new JSeparator());
          popup.add(new JMenuItem(session.actionManager.pointsetExportAction));
        }
        else if (obj instanceof DefaultMutableTreeNode) {
          popup.add(new JLabel("Pointsets"));
          popup.add(new JSeparator());
          popup.add(new JMenuItem(session.actionManager.pointsetImportAction));
          JMenuItem refreshMenuItem = new JMenuItem("Refresh tree");
          refreshMenuItem.addActionListener(new ActionListener() {
            @Override
						public void actionPerformed(ActionEvent e) {
              if (tree.getRowHeight() <= 0) {
                tree.setRowHeight(1);
              }
              tree.setRowHeight(0);
              pointsetTreeModel.reload();
            }
          });
          popup.add(refreshMenuItem);
        }
        else {
          new RuntimeException("unexpected state, tree too deep")
              .printStackTrace();
        }
        return popup;
      }

      private JidePopupMenu createPopup(TreePath[] selectionPaths) {
        final JidePopupMenu popup = new JidePopupMenu();
        popup.add(new JLabel("multiple selections -- no actions apply"));
        return popup;
      }
    });
    tree.setRootVisible(true);
    tree.setModel(pointsetTreeModel);
    tree.setCellRenderer(new PointsetTreeCellRenderer(session));
    tree.setAutoscrolls(true);
    tree.setShowsRootHandles(true);
    tree.setRowHeight(0);
    Session.addFocusBorder(tree, tree);
    return treePanel;
  }

  public void pathContentsChanged(final PointsetManager manager,
      final List<String> paths) {
    // nop
  }

  public Pointset[] getSelectedPointsets() {
    TreePath[] treePaths = tree.getSelectionPaths();
    if (treePaths == null) {
      return null;
    }
    for (TreePath treePath : treePaths) {
      if (!(treePath.getLastPathComponent() instanceof PointsetTreeNode)) {
        session.errorNoSupport("Only pointsets should be selected.");
        return null;
      }
    }
    Pointset[] pointsets = new Pointset[treePaths.length];
    for (int i = 0; i < treePaths.length; i++) {
      PointsetTreeNode pointsetTreeNode = (PointsetTreeNode) treePaths[i]
          .getLastPathComponent();
      pointsets[i] = (Pointset) pointsetTreeNode.getUserObject();
    }
    return pointsets;
  }

  public Pointset getSelectedPointset() {
    Pointset[] pointsets = getSelectedPointsets();
    if (pointsets == null || pointsets.length != 1) {
      session.errorNoSupport("Exactly 1 pointsets should be selected.");
      return null;
    }
    return pointsets[0];
  }

  public Basis[] getSelectedBases() {
    TreePath[] treePaths = tree.getSelectionPaths();
    if (treePaths == null) {
      return null;
    }
    for (TreePath treePath : treePaths) {
      if (!(treePath.getLastPathComponent() instanceof BasisTreeNode)) {
        session.errorNoSupport("Only bases should be selected.");
        return null;
      }
    }
    Basis[] bases = new Basis[treePaths.length];
    for (int i = 0; i < treePaths.length; i++) {
      BasisTreeNode node = (BasisTreeNode) treePaths[i].getLastPathComponent();
      bases[i] = (Basis) node.getUserObject();
    }
    return bases;
  }

  public Basis getSelectedBasis() {
    Basis[] bases = getSelectedBases();
    if (bases == null || bases.length != 1) {
      session.errorNoSupport("Exactly 1 basis should be selected.");
      return null;
    }
    return bases[0];
  }

  public void selectPath(TreeNode[] path) {
    TreePath treePath = new TreePath(path);
    tree.expandPath(treePath);
    tree.scrollPathToVisible(treePath);
    tree.setSelectionPath(treePath);
  }

  // @Implements TreeSelectionListener
  @Override
	public void valueChanged(TreeSelectionEvent e) {
    TreePath[] treePaths = tree.getSelectionPaths();
    if (treePaths == null) {
      return;
    }
    List<Basis> bases = new ArrayList<Basis>();
    List<Pointset> pointsets = new ArrayList<Pointset>();
    for (TreePath treePath : treePaths) {
      Object obj = treePath.getLastPathComponent();
      if (obj instanceof BasisTreeNode) {
        bases.add((Basis) ((BasisTreeNode) treePath.getLastPathComponent())
            .getUserObject());
      }
      else if (obj instanceof PointsetTreeNode) {
        pointsets.add((Pointset) ((PointsetTreeNode) treePath
            .getLastPathComponent()).getUserObject());
      }
      else {
        // could be root
      }
    }
    // fire even if no selections
    session.pointsetManager.selectBases(bases.toArray(new Basis[bases.size()]));
    session.pointsetManager.selectPointsets(pointsets
        .toArray(new Pointset[pointsets.size()]));
  }

  public void selectPointset(Pointset pointset) {
    selectPath(pointsetTreeModel.getPathToUserObject(pointset));
  }

  public void selectBasis(Basis basis) {
    selectPath(pointsetTreeModel.getPathToUserObject(basis));
  }

  void rebuildFiltersPanel() {
    objectToFilterViewlet = new HashMap<Object, PointFilterViewlet>();
    Enumeration<DefaultMutableTreeNode> nodes = ((DefaultMutableTreeNode) tree
        .getModel().getRoot()).depthFirstEnumeration();
    filtersPanel.removeAll();
    PointFilterViewlet pointFilterViewlet;
    List<DefaultMutableTreeNode> reversedOrderList = new ArrayList<DefaultMutableTreeNode>();
    while (nodes.hasMoreElements()) {
      reversedOrderList.add(nodes.nextElement());
    }
    Collections.reverse(reversedOrderList);
    for (DefaultMutableTreeNode dmtn : reversedOrderList) {
      PointsetManagerEntity pointsetManagerEntity = (PointsetManagerEntity) dmtn
          .getUserObject();
      if (pointsetManagerEntity == null) {
        continue; // root
      }
      pointFilterViewlet = objectToFilterViewlet.get(pointsetManagerEntity);
      if (null == pointFilterViewlet) {
        pointFilterViewlet = new PointFilterViewlet(session,
            pointsetManagerEntity);
        // session.pointsetManager.addPointsetManagerListener(pointFilterViewlet);
        pointFilterViewlet.setBackground(Color.YELLOW);
      }
      filtersPanel.add(pointFilterViewlet);
    }
    validate();
    repaint();
  }

  // @Implements PointsetManagerListener
  @Override
	public void progressChanged(Object subject, String string, int min,
      int value, int max) {
    // nop
  }

  // @Implements PointsetManagerListener
  @Override
	public void pointsetManagerChanged(PointsetManagerEvent ev) {
    if (!(ev.member instanceof Pointset) && !(ev.member instanceof Pointset[])
        && !(ev.member instanceof Basis) && !(ev.member instanceof Basis[])) {
      return;
    }
    DefaultMutableTreeNode[] path;
    switch (ev.kind) {
    case MANAGER_CHANGED: // don't use this unless you are really lazy
      pointsetTreeModel.reload();
      revalidate();
      repaint();
      rebuildFiltersPanel();
      break;
    case MEMBER_CHANGED:
      path = pointsetTreeModel.searchByUserObject(pointsetTreeModel
          .getRoot(), ev.member);
      if (path != null) {
        pointsetTreeModel.reload(path[path.length-1]);
      }
      break;
    case MEMBER_LOADED:
      // node is listening and will take care of itself
      break;
    case MEMBER_ADDED:
      if (ev.member instanceof Pointset) {
        selectPointset((Pointset) ev.member);
      }
      else if (ev.member instanceof Basis) {
        selectBasis((Basis) ev.member);        
      }
      rebuildFiltersPanel();
      break;
    case POINTSET_REMOVED:
      if (ev.member instanceof Pointset) {
        rebuildFiltersPanel();
      }
      break;
    case MEMBERS_SELECTION:
      if (ev.member instanceof Pointset[]) {
        Pointset[] selectedPointsets = (Pointset[]) ev.member;
        for (Pointset pointset : selectedPointsets) {
          path = pointsetTreeModel.searchByUserObject(pointsetTreeModel
              .getRoot(), pointset);
          if (path != null) {
            selectPath(path);
          }
        }
      }
      else if (ev.member instanceof Basis[]) {
        Basis[] selectedBases = (Basis[]) ev.member;
        for (Basis basis : selectedBases) {
          path = pointsetTreeModel.searchByUserObject(pointsetTreeModel
              .getRoot(), basis);
          if (path != null) {
            selectPath(path);
          }
        }
      }
      break;
    case MEMBER_VISABILITY:
      rebuildFiltersPanel();
      break;
    case POINT_VIZ:
      break;
    case AC_MAP:
      break;
    }
  }

  private static final long serialVersionUID = 1L;
}
