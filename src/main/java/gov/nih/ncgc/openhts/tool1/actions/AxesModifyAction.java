package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.plotManager.AxesModifyDialog;

public class AxesModifyAction extends Tool1Action {
  private Session session;

  public AxesModifyAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Axes: Modify");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_M);
    putValue(Action.ACTION_COMMAND_KEY, "Axes: Modify");
    putValue(SHORT_DESCRIPTION, "change properties of axes");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));

      try {
        session.setWaitCursor(true);
        final AxesModifyDialog axesModifyDialog = session.dialogManager.getAxesModifyDialog();
        axesModifyDialog.setTitle("Axes - Modify");
        axesModifyDialog.showDialog();
        session.plotManager.saved = false;
        session.plotManager.canvas.refresh();
        session.updateEnablement();
      }
      catch (final Throwable ex) {
        session.errorSupport("Cannot modify axes because:", ex, "modify_axes_menu_error");
      }
      finally {
        session.setWaitCursor(false);
      }
  }

  private static final long serialVersionUID = 1L;
}
