package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;

public class HelpIndexAction extends Tool1Action {
  private Session session;

  public HelpIndexAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Index");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_I);
    putValue(Action.ACTION_COMMAND_KEY, "show index to help system");
    putValue(SHORT_DESCRIPTION, "X");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    session.getHelpBroker().setDisplayed(true);
    session.getHelpBroker().setCurrentID("mapkey.index");
  }

  private static final long serialVersionUID = 1L;
}
