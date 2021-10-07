package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.plotManager.Canvas;

public class CanvasDragXYRotateAction extends Tool1Action {
  private Session session;

  public CanvasDragXYRotateAction(final Session session) {
    super("XY rotate",AppLafManager.getIcon(AppLafManager.IconKind.ICON_DRAG_XYROTATE));
    this.session = session;
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_X, 0));
    putValue(SHORT_DESCRIPTION, "XY rotate (x)");
    putValue(LONG_DESCRIPTION, "XY rotate (x)");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {
        if (session.plotManager.getDim() == 3) {
          session.plotManager.canvas.setDragMode(Canvas.DragModeKind.ROTATION_XY_DRAG_MODE);
        }
        else {
          throw new RuntimeException("button should be disabled");
        }
      }
      catch (final Throwable ex) {
        session.errorSupport("While listening to tabbed pane:" , ex,
                     "drag_xyrotate_canvas_button_error");
      }
  }

  private static final long serialVersionUID = 1L;
}

/*
rootPane.registerKeyboardAction(new ActionListener() {
  public void actionPerformed(ActionEvent actionEvent) {
    try {
      System.out.println("(XY rotate mode)");
      session.plotter.canvas
          .setDragMode(Canvas.DragModeKind.ROTATION_XY_DRAG_MODE);
    }
    catch (Throwable ex) {
      session.error("", ex, "drag_mode_canvas_key_error");
    }
  }
}, KeyStroke.getKeyStroke(KeyEvent.VK_X, 0),
    JComponent.WHEN_IN_FOCUSED_WINDOW);
*/