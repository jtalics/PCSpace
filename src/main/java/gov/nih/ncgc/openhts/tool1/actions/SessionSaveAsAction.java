package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;

public class SessionSaveAsAction extends Tool1Action {
  private Session session;

  public SessionSaveAsAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Save Session As...");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(Action.ACTION_COMMAND_KEY, "Save As");
    putValue(SHORT_DESCRIPTION, "save canvas with new file name");

    setEnabled(false);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));

      try {
        session.setWaitCursor(true);
        if (session.plotManager.subject.isEmpty()) {
          throw new Exception("there is nothing to save");
        }
        try {
          //session.plotter.save(true);
        }
        finally {
          session.updateEnablement();
        }
      }
      catch (final Throwable ex) {
        session.errorSupport("Cannot save-as because: ", ex, "save_as_file_menu_error");
      }
      finally {
        session.setWaitCursor(false);
      }
  }

  private static final long serialVersionUID = 1L;
}
