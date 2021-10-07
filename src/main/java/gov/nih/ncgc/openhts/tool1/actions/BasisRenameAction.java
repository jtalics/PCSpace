package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Basis;
import gov.nih.ncgc.openhts.tool1.descriptorManager.BasisRenameDialog;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class BasisRenameAction extends Tool1Action {
  private Session session;

  public BasisRenameAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Rename basis");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
    putValue(Action.ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "Rename basis");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String) getValue(Action.NAME));
    try {
      Basis basis = session.descriptorManager.getSelectedBasis();
      if (basis == null) {
        throw new Exception("No pointset is selected.");
      }
      final BasisRenameDialog basisRenameDialog = session.dialogManager
          .getBasisRenameDialog();
      basisRenameDialog.setBasis(basis);
      basisRenameDialog.showDialog(); // modal block
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot Rename Basis because:\n", ex,
          "replace_data_menu_error");
    }
    finally {
      // nop
    }
  }

  private static final long serialVersionUID = 1L;
}
