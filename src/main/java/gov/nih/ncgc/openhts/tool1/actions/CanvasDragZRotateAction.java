package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.plotManager.Canvas;

public class CanvasDragZRotateAction extends Tool1Action {
  private Session session;

  public CanvasDragZRotateAction(final Session session) {
    super("Z rotate",AppLafManager.getIcon(AppLafManager.IconKind.ICON_DRAG_ZROTATE));
    this.session = session;
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0));
    putValue(SHORT_DESCRIPTION, "Z rotate (z)");
    putValue(LONG_DESCRIPTION, "Z rotate (z)");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {
        session.plotManager.canvas.setDragMode(Canvas.DragModeKind.ROTATION_Z_DRAG_MODE);
      }
      catch (final Throwable ex) {
        session.errorSupport("" , ex, "drag_zrotate_canvas_button_error");
      }
  }

  private static final long serialVersionUID = 1L;
}    

/*
rootPane.registerKeyboardAction(new ActionListener() {
  public void actionPerformed(ActionEvent actionEvent) {
    try {
      System.out.println("(Z rotate mode)");
      session.plotter.canvas
          .setDragMode(Canvas.DragModeKind.ROTATION_Z_DRAG_MODE);
    }
    catch (Throwable ex) {
      session.error("", ex, "drag_mode_canvas_key_error");
    }
  }
}, KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0),
    JComponent.WHEN_IN_FOCUSED_WINDOW);
*/