/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.dataSourceManager.filter;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.Tool1Exception;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class FilterViewlet extends JScrollPane {
  private final FilterNode root = new FilterNode();
  private final JTree tree = new JTree(root);
  private final DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
  Session session;

  public FilterViewlet(Session session) {
    this.session = session;
    initialize();
    setBorder(BorderFactory.createTitledBorder("Point filter"));
  }

  public void initialize() {
    tree.setRootVisible(false);
    tree.setShowsRootHandles(false);
    tree.setAutoscrolls(true);
    model.setRoot(root);
    setViewportView(tree);
  }

  public void addMatcher(Matcher matcher) {
    TreePath treePath = tree.getSelectionPath();
    if (treePath != null) {
      FilterNode filterNode = ((FilterNode) treePath.getLastPathComponent());
      filterNode.add(new FilterNode(matcher));
      tree.expandPath(treePath);
    }
    else {
      root.add(new FilterNode(matcher));
    }
    model.nodeStructureChanged(root);
  }

  public void replaceMatcher(Matcher matcher) throws Tool1Exception {
    if (matcher == null) {
      throw new RuntimeException("null matcher in filterNode userObject");
    }
    TreePath treePath = tree.getSelectionPath();
    if (treePath != null) {
      ((FilterNode) treePath.getLastPathComponent()).setUserObject(matcher);
    }
    else {
      throw new Tool1Exception("No matcher selected in point filter.");
    }
    model.nodeStructureChanged(root);
  }

  public void removeMatcher(Matcher matcher) throws Tool1Exception {
    if (matcher == null) {
      throw new RuntimeException("null matcher in filterNode userObject");
    }
    TreePath treePath = tree.getSelectionPath();
    if (treePath != null) {
      FilterNode filterNode = ((FilterNode) treePath.getLastPathComponent());
      filterNode.setUserObject(null);
      filterNode.removeFromParent();
    }
    else {
      throw new Tool1Exception("No matcher selected in pont filter.");
    }
    model.nodeStructureChanged(root);
  }

  public FilterNode getFilter() {
    return root;
  }

  public void clearSelection() {
    tree.clearSelection();
  }

  public void addTreeSelectionListener(TreeSelectionListener listener) {
    tree.addTreeSelectionListener(listener);
  }

  public void removeTreeSelectionListener(TreeSelectionListener listener) {
    tree.removeTreeSelectionListener(listener);
  }

  public Matcher getSelectedMatcher() {
    TreePath treePath = tree.getSelectionPath();
    if (treePath != null) {
      Matcher matcher = ((FilterNode) treePath.getLastPathComponent())
          .getUserObject();
      if (matcher == null) {
        throw new RuntimeException("null matcher in filterNode userObject");
      }
      return matcher;
    }
    return null;
  }

  private static final long serialVersionUID = 1L;
}
