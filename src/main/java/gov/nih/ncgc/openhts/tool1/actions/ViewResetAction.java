package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;

public class ViewResetAction extends Tool1Action {
  private Session session;

  public ViewResetAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "View: Reset");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
    putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, 0));
    putValue(Action.ACTION_COMMAND_KEY, "View: Reset");
    putValue(SHORT_DESCRIPTION, "use standard viewpoint");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {
        session.statusPanel.log2("view reset");
        session.setWaitCursor(true);
        session.plotManager.resetView();
      }
      catch (final Throwable ex) {
        session.errorSupport("Cannot reset view because:\n",ex, "reset_view_menu_error");
      }
      finally {
        session.setWaitCursor(false);
      }
  }

  private static final long serialVersionUID = 1L;
}
/*
new ActionListener() {
public void actionPerformed(
    ActionEvent actionEvent) {
  System.out.println("(reset view)");
  try {
    session.plotter.doResetView();
  }
  catch (Throwable ex) {
    session.error("Cannot reset view because:\n", ex,
        "reset_view_canvas_key_error");
  }
  session.plotter.canvas.refresh();
*/