package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;

public class CanvasRefreshAction extends Tool1Action {
  private Session session;

  public CanvasRefreshAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Canvas: Refresh");
    putValue(MNEMONIC_KEY, KeyEvent.VK_F5);
    putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_W, 0));
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "redraw canvas");
    putValue(LONG_DESCRIPTION, "redraw canvas");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {
        session.setWaitCursor(true);
        session.plotManager.refreshCanvas();
      }
      catch (final Throwable ex) {
        session.errorSupport("Cannot refresh because:\n", ex, "refresh_canvas_menu_error");
      }
      finally {
        session.setWaitCursor(false);
      }
  }

  private static final long serialVersionUID = 1L;
}
/*
rootPane.registerKeyboardAction(
    new ActionListener() {
  public void actionPerformed(ActionEvent actionEvent) {
    try {
      System.out.println("(new window)");
      // JPlotter.this.init();
      session.plotter.canvas.refresh();
    }
    catch (Throwable ex) {
      session.error("", ex, "refresh_canvas_key_error");
    }
  }
}, KeyStroke.getKeyStroke(KeyEvent.VK_W, 0),
    JComponent.WHEN_IN_FOCUSED_WINDOW);


rootPane.registerKeyboardAction(new ActionListener() {
  public void actionPerformed(ActionEvent actionEvent) {
    try {
      session.plotter.canvas.refresh();
    }
    catch (Throwable ex) {
      session.error("", ex, "refresh_canvas_key_error");
    }
  }
}, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),
    JComponent.WHEN_IN_FOCUSED_WINDOW);

rootPane.registerKeyboardAction(new ActionListener() {
  public void actionPerformed(ActionEvent actionEvent) {
    try {
      System.out.println("(refresh)");
      session.plotter.canvas.setStale(true);
      session.plotter.canvas.refresh();
    }
    catch (Throwable ex) {
      session.error("", ex, "refresh_canvas_key_error");
    }
  }
}, KeyStroke.getKeyStroke(KeyEvent.VK_R, 0),
    JComponent.WHEN_IN_FOCUSED_WINDOW);
*/