package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.plotManager.Canvas;

public class CanvasDragSelectionAction extends Tool1Action {
  private Session session;

  public CanvasDragSelectionAction(final Session session) {
    super("Box un/selection",AppLafManager.getIcon(AppLafManager.IconKind.ICON_DRAG_SELECTION));
    this.session = session;
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_V, 0));
    putValue(SHORT_DESCRIPTION, "Box un/selection (u)");
    putValue(LONG_DESCRIPTION, "Box un/selection (u)");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      session.plotManager.canvas
          .setDragMode(Canvas.DragModeKind.SELECTION_DRAG_MODE);
    }
    catch (final Throwable ex) {
      session.errorSupport("", ex, "drag_selection_canvas_button_error");
    }
  }

  private static final long serialVersionUID = 1L;
}

/*
rootPane.registerKeyboardAction(new ActionListener() {
  public void actionPerformed(ActionEvent actionEvent) {
    try {
      System.out.println("(box un/selection");
      session.plotter.canvas
          .setDragMode(Canvas.DragModeKind.SELECTION_DRAG_MODE);
    }
    catch (Throwable ex) {
      session.error("", ex, "drag_mode_canvas_key_error");
    }
  }
}, KeyStroke.getKeyStroke(KeyEvent.VK_U, 0),
    JComponent.WHEN_IN_FOCUSED_WINDOW);
rootPane.registerKeyboardAction(
    session.actionManager.viewResetAction, KeyStroke.getKeyStroke(KeyEvent.VK_V, 0),
    JComponent.WHEN_IN_FOCUSED_WINDOW);
*/