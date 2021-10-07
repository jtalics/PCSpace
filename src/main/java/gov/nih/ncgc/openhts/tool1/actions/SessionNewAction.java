package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;

public class SessionNewAction extends Tool1Action {
  private Session session;

  public SessionNewAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "New Session");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
    putValue(Action.ACTION_COMMAND_KEY, "New Sessoin");
    putValue(SHORT_DESCRIPTION, "start new sesson in another window");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
     try {
       session.setWaitCursor(true);
       session.main.createSession().showUI();
     }
     catch(Throwable t) {
       // nop
     }
     finally {
       session.setWaitCursor(false);
     }
   }

  private static final long serialVersionUID = 1L;
}
