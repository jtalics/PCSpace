/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;

public class HideUiAction extends Tool1Action {

    private Session session;
    public HideUiAction(final Session session) {
        this.session=session;
        putValue(Action.NAME, "Hide UI");
        putValue(Action.MNEMONIC_KEY,KeyEvent.VK_S);
        putValue(Action.ACTION_COMMAND_KEY, "Hide UI");
        putValue(SHORT_DESCRIPTION, "Shrink window to system tray");

        setEnabled(true);
    }

    @Override
		public void actionPerformed(final ActionEvent e) {
      session.statusPanel.log2((String)getValue(Action.NAME));
      if (session.frame.isVisible()) {
        session.frame.setVisible(false);
      }
    }
    private static final long serialVersionUID = 1L;
}