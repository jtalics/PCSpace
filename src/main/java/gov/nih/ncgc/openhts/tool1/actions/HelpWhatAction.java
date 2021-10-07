package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.help.CSH;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;

public class HelpWhatAction extends Tool1Action {
  private Session session;

  public HelpWhatAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "What is this?");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_F2);
    putValue(Action.ACTION_COMMAND_KEY, "X");
    putValue(SHORT_DESCRIPTION, "display specific help about a UI component on the screen");

    setEnabled(false);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    new CSH.DisplayHelpAfterTracking(session.getHelpBroker());
  }

  private static final long serialVersionUID = 1L;
}
