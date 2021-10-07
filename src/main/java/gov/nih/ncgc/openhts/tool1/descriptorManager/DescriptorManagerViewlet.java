package gov.nih.ncgc.openhts.tool1.descriptorManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import com.jidesoft.swing.JidePopupMenu;
import gov.nih.ncgc.openhts.tool1.GlobalSettings;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.util.ContextTreePopupListener;

/**
 * Purpose is to present the DataManager to user.
 * 
 * @author talafousj
 */
public class DescriptorManagerViewlet extends JSplitPane implements TreeSelectionListener {
  private final Session session;
  // private final JComboBox descriptorComboBox;
  private final DescriptorViewlet descriptorViewlet;
  private final JTextField homeDirTextField = new JTextField();
  private JTree tree;
  private DefaultMutableTreeNode root;
  private DefaultTreeModel treeModel;
  private final JPanel defaultPanel = new JPanel();
  private BasisViewlet basisViewlet;
  private static final String DESCRIPTOR_MANAGER_SPLIT_KEY = "gov.nih.ncgc.openhts.tool1.ui.DataManagerSplit";
  private final JPanel displayPanel = new JPanel(new BorderLayout());

  public DescriptorManagerViewlet(final Session session) {
    tree = new JTree() {
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
        else if (node instanceof DescriptorTreeNode) {
          return ((DescriptorTreeNode) node).getToolTipText();
        }
        else {
          return null;
        }
      }
    };
    this.session = session;
    setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    setOneTouchExpandable(true);
    final String dl = GlobalSettings.getInstance().getProperty(
        DESCRIPTOR_MANAGER_SPLIT_KEY);
    if (dl != null) {
      setDividerLocation(Integer.parseInt(dl));
    }
    else {
      setDividerLocation(300);
    }
    setDividerLocation(150);
    setDividerSize(Session.dividerSize);
    setLeftComponent(new JScrollPane(tree));
    treeModel = session.descriptorManager.getTreeModel();
    root = (DefaultMutableTreeNode) treeModel.getRoot();
    tree.setModel(treeModel);
    ToolTipManager.sharedInstance().registerComponent(tree);
    tree.addTreeSelectionListener(this);
    tree.addMouseListener(new ContextTreePopupListener(session, tree) {
      @Override
      public JidePopupMenu createContextMenu() {
        final JidePopupMenu popup = new JidePopupMenu();
        final int nSelectedPaths = tree.getSelectionPaths().length;
        if (nSelectedPaths == 1) {
          // Only one item selected
          final int depth = tree.getSelectionPaths()[0].getPathCount();
          switch (depth) {
          case 1:
            popup.add(new JMenuItem(session.actionManager.descriptorInvertTreeAction));
            break;
          case 2: // Basis popup
            popup.add(new JLabel("Basis"));
            popup.add(new JSeparator());
            popup.add(new JMenuItem(session.actionManager.basisRemoveAction));
            break;
          case 3: // Descriptor popup
            popup.add(new JLabel("Descriptor"));
            popup.add(new JSeparator());
            // popup.add(new
            // JMenuItem(session.actionManager.pointsetShowAction));
            // popup.add(new
            // JMenuItem(session.actionManager.pointsetHideAction));
            // popup.add(new JSeparator());
            // popup
            // .add(new JMenuItem(session.actionManager.pointsetModifyAction));
            // popup
            // .add(new JMenuItem(session.actionManager.pointsetReplaceAction));
            popup
               .add(new JMenuItem(session.actionManager.descriptorRemoveAction));
            break;
          default:
            new RuntimeException("unexpected state, tree too deep")
                .printStackTrace();
          }
        }
        else if (nSelectedPaths > 1) {
          popup.add(new JLabel("multiple selections -- no actions apply"));
        }
        return popup;
      }
    });
    tree.setRootVisible(true);
    tree.setModel(treeModel);
    tree.setCellRenderer(new DescriptorTreeCellRenderer(session));
    tree.setAutoscrolls(true);
    tree.setShowsRootHandles(true);
    Session.addFocusBorder(tree, tree);
    this.setRightComponent(displayPanel);
    tree.addTreeSelectionListener(new TreeSelectionListener() {
      @Override
			public void valueChanged(final TreeSelectionEvent e) {
        final TreePath[] selectedPaths = tree.getSelectionPaths();
        if (selectedPaths == null) {
          // User desected
        }
        else if (selectedPaths.length == 1) {
          final TreePath selectedPath = selectedPaths[0];
          final Object object = selectedPath.getLastPathComponent();
          if (object instanceof BasisTreeNode) {
            basisViewlet.stateChanged(new ChangeEvent(((BasisTreeNode) object).getUserObject()));
            displayPanel.removeAll();
            displayPanel.add(basisViewlet, BorderLayout.CENTER);
          }
          else if (object instanceof DescriptorTreeNode) {
            descriptorViewlet
                .changeDescriptor((Descriptor) ((DescriptorTreeNode) object)
                    .getUserObject());
            displayPanel.removeAll();
            displayPanel.add(descriptorViewlet, BorderLayout.CENTER);
          }
          else {
            displayPanel.removeAll();
            displayPanel.add(defaultPanel, BorderLayout.CENTER);
          }
          displayPanel.invalidate();
          displayPanel.revalidate();
          displayPanel.repaint();
        } // endif
      }
    });
    final JLabel label = new JLabel("Select a Descriptor or Basis from tree at left");
    label.setHorizontalAlignment(SwingConstants.CENTER);
    defaultPanel.add(label);
    defaultPanel.setBackground(Color.LIGHT_GRAY);
    descriptorViewlet = new DescriptorViewlet(session);
    basisViewlet = new BasisViewlet(session);
    displayPanel.add(defaultPanel, BorderLayout.CENTER);
    final JPanel southPanel = new JPanel(new BorderLayout());
    southPanel.add(new JLabel("home dir: "), BorderLayout.WEST);
    southPanel.add(homeDirTextField, BorderLayout.CENTER);
    displayPanel.add(southPanel, BorderLayout.SOUTH);
  }

  @Override
  public void setDividerLocation(final int dl) {
    super.setDividerLocation(dl);
    GlobalSettings.getInstance().putProperty(DESCRIPTOR_MANAGER_SPLIT_KEY,
        Integer.toString(dl));
  }

  private static final long serialVersionUID = 1L;

  public Descriptor[] getSelectedDescriptors() {
    TreePath[] treePaths = tree.getSelectionPaths();
    if (treePaths == null) {
      return null;
    }
    for (TreePath treePath : treePaths) {
      if (!(treePath.getLastPathComponent() instanceof DescriptorTreeNode)) {
        session.errorNoSupport("Only descriptors should be selected.");
        return null;
      }
    }
    Descriptor[] descriptors = new Descriptor[treePaths.length];
    for (int i = 0; i < treePaths.length; i++) {
      DescriptorTreeNode descriptorTreeNode = (DescriptorTreeNode)treePaths[i].getLastPathComponent();
      descriptors[i] = (Descriptor)descriptorTreeNode.getUserObject() ; 
    }
    return descriptors;
  }

  public Descriptor getSelectedDescriptor() {
    Descriptor[] descriptors = getSelectedDescriptors();
    if (descriptors == null || descriptors.length != 1) {
      session.errorNoSupport("Exactly 1 descriptor should be selected.");
      return null;
    }
    return descriptors[0];
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
      bases[i] = (Basis)node.getUserObject();
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
    if (path == null) {
      return;
    }
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
    for (TreePath treePath : treePaths){
      Object obj = treePath.getLastPathComponent();
      if (obj instanceof BasisTreeNode) {
        bases.add((Basis)((BasisTreeNode)treePath.getLastPathComponent()).getUserObject());
      }
    }
    if (bases.size() > 0) {
      session.descriptorManager.selectBases(bases.toArray(new Basis[bases.size()]));
    }
  }
}
