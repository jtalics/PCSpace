package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;

public class SessionSaveAction extends Tool1Action {
  private Session session;

  public SessionSaveAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Save Session");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
    putValue(Action.ACTION_COMMAND_KEY, "X");
    putValue(SHORT_DESCRIPTION, "write contents of canvas to file");
    setEnabled(false);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      session.setWaitCursor(true);
      if (session.plotManager.subject.isEmpty()) {
        session.errorHelp("there is nothing to save","TODO");
      }
      //session.plotter.save(false);
      session.plotManager.saved = true;
      session.updateEnablement();
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot save because:", ex, "save_file_menu_error");
    }
    finally {
      session.setWaitCursor(false);
    }
  }

  private static final long serialVersionUID = 1L;
}
