package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetRenameDialog;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class PointsetRenameAction extends Tool1Action {
  private Session session;

  public PointsetRenameAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Rename pointset");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
    putValue(Action.ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "Rename pointset");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String) getValue(Action.NAME));
    try {
      Pointset pointset = session.pointsetManager.getSelectedPointset();
      if (pointset == null) {
        throw new Exception("No pointset is selected.");
      }
      final PointsetRenameDialog pointsetRenameDialog = session.dialogManager
          .getPointsetRenameDialog();
      pointsetRenameDialog.setPointset(pointset);
      pointsetRenameDialog.showDialog(); // modal block
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot Rename Pointset because:\n", ex,
          "replace_data_menu_error");
    }
    finally {
      // nop
    }
  }

  private static final long serialVersionUID = 1L;
}
