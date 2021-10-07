package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetManager;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetSelectorDialog;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class PointsetReplaceAction extends Tool1Action {
  private Session session;

  public PointsetReplaceAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Replace");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
    putValue(Action.ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "substitute one data source with another");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
  try {
      final PointsetSelectorDialog pointsetSelectorDialog = new PointsetSelectorDialog(session,PointsetManager.REPLACE); 
      pointsetSelectorDialog.setModal(true);
      pointsetSelectorDialog.showDialog();
      session.saved = false;
      session.plotManager.canvas.setStale(true);
      session.plotManager.canvas.refresh();
      session.updateEnablement();
  }
    catch (final Throwable ex) {
      session.errorSupport("Cannot replace pointset because:\n", ex, "replace_data_menu_error");
    }
    finally {
      // nop
    }

  }

  private static final long serialVersionUID = 1L;
}
