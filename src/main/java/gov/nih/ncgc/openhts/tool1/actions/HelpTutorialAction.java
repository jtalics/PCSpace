package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;

class HelpTutorialAction extends Tool1Action {
  private Session session;

  public HelpTutorialAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Tutorial");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);
    putValue(Action.ACTION_COMMAND_KEY, "Tutorial");
    putValue(SHORT_DESCRIPTION, "learn the basic use of app");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    session.getHelpBroker().setDisplayed(true);
    session.getHelpBroker().setCurrentID("mapkey.tutorials");
  }

  private static final long serialVersionUID = 1L;
}
