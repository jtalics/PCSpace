package gov.nih.ncgc.openhts.tool1.plotManager;

import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import com.jidesoft.swing.JidePopupMenu;
import gov.nih.ncgc.openhts.tool1.Session;

public class CanvasPopupMenu extends JidePopupMenu {

  public CanvasPopupMenu(final Session session) {
    add(new JMenuItem(session.actionManager.canvasSelectModeAction));
    add(new JMenuItem(session.actionManager.canvasDeselectModeAction));
    addSeparator();
    add(new JMenuItem(session.actionManager.canvasDragNoneAction));
    add(new JMenuItem(session.actionManager.canvasDragSelectionAction));
    add(new JMenuItem(session.actionManager.canvasDragXYRotateAction));
    add(new JMenuItem(session.actionManager.canvasDragZRotateAction));
    add(new JMenuItem(session.actionManager.canvasDragTranslateAction));
    add(new JMenuItem(session.actionManager.canvasDragTextAction));
    add(new JMenuItem(session.actionManager.canvasZoomDragModeAction));
    addSeparator();
    add(new JMenuItem(session.actionManager.canvasUnzoomAction));
    add(new JMenuItem(session.actionManager.canvasCycleAction));
    add(new JMenuItem(session.actionManager.canvasComposeAction));
    add(new JSeparator());
    add(new JMenuItem(session.actionManager.canvasOrthogonalAction));
    add(new JMenuItem(session.actionManager.canvasNonOrthogonalAction));
  }
  private static final long serialVersionUID = 1L;
}
