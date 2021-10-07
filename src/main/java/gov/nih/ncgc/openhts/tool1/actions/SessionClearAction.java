package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;

public class SessionClearAction extends Tool1Action {
  private Session session;

  public SessionClearAction(final Session session) {
    super("Clear",null);
    this.session = session;
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
    putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_X, 0));
    putValue(Action.ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "clear");
    putValue(LONG_DESCRIPTION, "clear");
       setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
     try {
       session.setWaitCursor(true);
       session.clear();
       session.saved = false;
     }
     finally {
       session.setWaitCursor(false);
     }
   }

  private static final long serialVersionUID = 1L;
}
