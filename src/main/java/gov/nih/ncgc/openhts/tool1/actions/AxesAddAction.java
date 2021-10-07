/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.plotManager.Axes;
import gov.nih.ncgc.openhts.tool1.plotManager.AxesModifyDialog;

public class AxesAddAction extends Tool1Action {
  private final Session session;

  public AxesAddAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Axes: Add");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
    putValue(Action.ACTION_COMMAND_KEY, "Axes: Add");
    putValue(SHORT_DESCRIPTION, "create new axes and draw on canvas");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      if (session.plotManager.subject.axes != null) {
        throw new Exception("Axes already exist, delete first.");
      }
      if (session.plotManager.view.zoomed) {
        throw new Exception("Cannot add axes in zoomed mode.");
      }
      final Axes axes = new Axes(session /* ,true */);
      axes.initialize();
      
      final AxesModifyDialog axesModifyDialog = session.dialogManager.getAxesModifyDialog();
      axesModifyDialog.showDialog();
      
      session.plotManager.saved = false;
      session.updateEnablement();

    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot add axes because:\n", ex, "add_axes_menu");
    }
    finally {
      session.setWaitCursor(false);
    }
  }

  private static final long serialVersionUID = 1L;
}
