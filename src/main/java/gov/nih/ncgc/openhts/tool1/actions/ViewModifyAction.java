package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.plotManager.ViewModifyDialog;

public class ViewModifyAction extends Tool1Action {
  private Session session;

  public ViewModifyAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "View: Modify");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_M);
    putValue(Action.ACTION_COMMAND_KEY, "View: Modify");
    putValue(SHORT_DESCRIPTION, "change view properties");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      session.setWaitCursor(true);
      final ViewModifyDialog viewModifyDialog = session.dialogManager
          .getViewModifyDialog();
      viewModifyDialog.showDialog();
      session.saved = false;
      session.plotManager.canvas.refresh();
      session.updateEnablement();
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot modify view because:\n", ex,
          "modify_view_menu_error");
    }
    finally {
      session.setWaitCursor(false);
    }
  }

  private static final long serialVersionUID = 1L;
}
