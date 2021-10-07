/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;

public class ShowUiAction extends Tool1Action {

    private Session session;
    public ShowUiAction(final Session session) {
      this.session = session;
      putValue(Action.NAME, "Show UI");
        putValue(Action.MNEMONIC_KEY,KeyEvent.VK_S);
        putValue(Action.ACTION_COMMAND_KEY, "Show UI");
        putValue(SHORT_DESCRIPTION, "Expand system tray icon to full window");

        setEnabled(true);
    }

    @Override
		public void actionPerformed(final ActionEvent e) {
      session.statusPanel.log2((String)getValue(Action.NAME));
      if (!session.frame.isVisible()) {
        session.frame.setVisible(true);
      }
    }
    private static final long serialVersionUID = 1L;
}