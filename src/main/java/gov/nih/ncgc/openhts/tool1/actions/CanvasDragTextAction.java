package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.plotManager.Canvas;

public class CanvasDragTextAction extends Tool1Action {
  private Session session;

  public CanvasDragTextAction(final Session session) {
    super("Text drag mode",AppLafManager.getIcon(AppLafManager.IconKind.ICON_DRAG_TEXT));
    this.session = session;
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_T, 0));
    putValue(SHORT_DESCRIPTION, "Text drag mode (t)");
    putValue(LONG_DESCRIPTION, "Text drag mode (t)");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {
        session.plotManager.canvas.setDragMode(Canvas.DragModeKind.TEXT_DRAG_MODE);
      }
      catch (final Throwable ex) {
        session.errorSupport("" , ex, "drag_text_canvas_button_help");
      }
  }

  private static final long serialVersionUID = 1L;
}

/*
rootPane.registerKeyboardAction(new ActionListener() {
  public void actionPerformed(ActionEvent actionEvent) {
    try {
      System.out.println("(text mode)");
      session.plotter.canvas
          .setDragMode(Canvas.DragModeKind.TEXT_DRAG_MODE);
    }
    catch (Throwable ex) {
      session.error("", ex, "drag_mode_canvas_key_error");
    }
  }
}, KeyStroke.getKeyStroke(KeyEvent.VK_T, 0),
    JComponent.WHEN_IN_FOCUSED_WINDOW);
*/