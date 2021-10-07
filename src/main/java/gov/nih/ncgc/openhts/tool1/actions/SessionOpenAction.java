package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;

public class SessionOpenAction extends Tool1Action {
  private Session session;

  public SessionOpenAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Open existing Session");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
    putValue(Action.ACTION_COMMAND_KEY, "Open");
    putValue(SHORT_DESCRIPTION, "load items from file to canvas");

    setEnabled(false);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      //session.plotter.loadPlot(null);
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot open plot because: ", ex, "open_file_menu_error");
    }
    finally {
      session.setWaitCursor(false);
    }
  }

  private static final long serialVersionUID = 1L;
}
