package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetManager;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetSelectorDialog;

public class PointsetRemoveAction extends Tool1Action {
  private final Session session;

  public PointsetRemoveAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Remove");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);
    putValue(Action.ACTION_COMMAND_KEY, "Remove");
    putValue(SHORT_DESCRIPTION, "remove a data source from memory");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String) getValue(Action.NAME));
    try {
      final Pointset pointset = session.pointsetManager.getSelectedPointset();
      if (pointset == null) {
        final PointsetSelectorDialog pointsetSelectorDialog = session.dialogManager
            .getPointsetChooseDialog(PointsetManager.DELETE);
        pointsetSelectorDialog.showDialog();
        return;
      }
      session.pointsetManager.unregisterPointset(pointset);
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot remove because:\n", ex,
          "delete_data_menu_error");
    }
    finally {
      // nop
    }
  }

  private static final long serialVersionUID = 1L;
}
