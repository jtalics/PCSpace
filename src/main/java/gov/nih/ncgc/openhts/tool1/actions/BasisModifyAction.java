package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Basis;
import gov.nih.ncgc.openhts.tool1.descriptorManager.BasisModifyDialog;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class BasisModifyAction extends Tool1Action {
  private Session session;

  public BasisModifyAction(final Session session) {
    super("Modify",null);
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_M);
    putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_X, 0));
    putValue(SHORT_DESCRIPTION, "modify basis");
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(LONG_DESCRIPTION, "change properties of a Basis");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {
        final Basis[] bases = session.descriptorManager.getSelectedBases();
        final BasisModifyDialog modifyDialog = session.dialogManager.getBasisModifyDialog();
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
