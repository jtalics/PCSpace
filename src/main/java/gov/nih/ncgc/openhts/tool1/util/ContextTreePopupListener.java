/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.util;

import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTree;
import com.jidesoft.swing.JidePopupMenu;
import gov.nih.ncgc.openhts.tool1.Session;

public abstract class ContextTreePopupListener extends MouseAdapter implements
    KeyListener {
  private final Session session;
  private final JTree tree;

  public ContextTreePopupListener(final Session session, final JTree tree) {
    this.session = session;
    this.tree = tree;
  }

//  protected List<TreePath> selectedPaths = new ArrayList<TreePath>();

  @Override
  public void mousePressed(final MouseEvent e) {
//    selectedPaths.clear();
//    final TreePath[] selectionPaths = tree.getSelectionPaths();
//    if (selectionPaths != null) {
//      for (final TreePath treePath : selectionPaths) {
//        selectedPaths.add(treePath);
//      }
//    }
  }

  @Override
  public void mouseReleased(final MouseEvent e) {
    if (
        (e.getModifiers() & InputEvent.BUTTON1_MASK)
        == InputEvent.BUTTON1_MASK || !e.isPopupTrigger()) {
      return;
    }
    if (tree.getSelectionPaths() == null || tree.getSelectionPaths().length == 0) {
      session.message("select at least one node in the tree first");
      session.statusPanel.log2("select at least one node in the tree first");
      return;
    }
    final int x = e.getX();
    final int y = e.getY();
    if (!tree.isPathSelected(tree.getPathForLocation(x, y))) {
      session.message("right-click *on* a selected node");
      session.statusPanel.log2("right-click *on* a selected node");
      return;
    }
    JidePopupMenu contextMenu = createContextMenu();
    if (contextMenu != null) {
      contextMenu.show(e.getComponent(), x, y);
    }
  }

  @Override
	public void keyPressed(final KeyEvent e) {
//    System.out.println("KEY pressed: "+e);

    if (!(e.getKeyCode()==KeyEvent.VK_ENTER && e.isShiftDown())) {
      return;
    }
    if (tree.getSelectionPaths().length == 0) {
//      System.out.println("CONTEXTMENU: NO SELECTIONS");
      session.statusPanel.log2("select at least one node in the tree first");
      return;
    }
    final Rectangle r=tree.getPathBounds(tree.getPathForRow(tree.getLeadSelectionRow()));
    final int x = r.x+r.width;
    final int y = r.y+r.height;
//    if (!tree.isPathSelected(tree.getPathForLocation(x, y))) {
//      session.statusPanel.log2("right-click on a selected node");
//      return;
//    }
//    System.out.println("SHOWING CONTEXT MENU");
    createContextMenu().show(e.getComponent(), x, y);
  }

  @Override
	public void keyReleased(final KeyEvent e) {
    // nop    
  }

  @Override
	public void keyTyped(final KeyEvent e) {
    // nop
    
  }

  public abstract JidePopupMenu createContextMenu();

  private static final long serialVersionUID = 1L;
}
