package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetModifyDialog;

public class PointsetModifyAction extends Tool1Action {
  private Session session;

  public PointsetModifyAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Modify");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_M);
    putValue(Action.ACTION_COMMAND_KEY, "Modify");
    putValue(SHORT_DESCRIPTION, "change properties of the data");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {
        final PointsetModifyDialog modifyDialog = session.dialogManager.getPointsetModifyDialog();
        modifyDialog.showDialog();
        session.plotManager.canvas.refresh();
        session.updateEnablement();
      }
      catch (final Throwable ex) {
        session.errorSupport("Cannot modify data because:\n", ex, "modify_data_menu_error");
      }
      finally {
        // nop
      }
  }

  private static final long serialVersionUID = 1L;
}
