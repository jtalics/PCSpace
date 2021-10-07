package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;

public class HelpSearchAction extends Tool1Action {
  private Session session;

  public HelpSearchAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Search");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
    putValue(Action.ACTION_COMMAND_KEY, "Search");
    putValue(SHORT_DESCRIPTION, "query the help system");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    session.getHelpBroker().setDisplayed(true);
    session.getHelpBroker().setCurrentID("mapkey.search");
  }

  private static final long serialVersionUID = 1L;
}
