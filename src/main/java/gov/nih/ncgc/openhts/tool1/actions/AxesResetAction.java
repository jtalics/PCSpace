package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;

public class AxesResetAction extends Tool1Action {
  private Session session;

  public AxesResetAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Axes: Reset");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
    putValue(Action.ACTION_COMMAND_KEY, "Axes: Reset");
    putValue(SHORT_DESCRIPTION, "use standard axes properties");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      session.setWaitCursor(true);
      session.dialogManager.hideAxesDialogs();
      session.plotManager.resetAxes();
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot reset because:", ex, "reset_axes_menu_error");
    }
    finally {
      session.setWaitCursor(false);
    }
  }

  private static final long serialVersionUID = 1L;
}
