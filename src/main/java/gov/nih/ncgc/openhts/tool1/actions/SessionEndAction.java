/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;

public class SessionEndAction extends Tool1Action {

    private Session session;
    public SessionEndAction(final Session session) {
        this.session = session;
        putValue(NAME, "End");
        putValue(MNEMONIC_KEY,KeyEvent.VK_E);
        putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0));
        putValue(ACTION_COMMAND_KEY, getClass().getName());
        putValue(SHORT_DESCRIPTION, "Exit");
        putValue(LONG_DESCRIPTION, "Exit");
        setEnabled(true);
    }

    @Override
		public void actionPerformed(final ActionEvent e) {
      session.statusPanel.log2((String)getValue(Action.NAME));
      session.main.endSession(session);
    }
    private static final long serialVersionUID = 1L;
}

/*
rootPane.registerKeyboardAction(new ActionListener() {
  public void actionPerformed(ActionEvent actionEvent) {
    System.out.println("(exit)");
    try {
      // session.plotter.doExitPlot();
    }
    catch (Throwable ex) {
      session.error("", ex, "exit_canvas_key_error");
    }
  }
}, KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0),
    JComponent.WHEN_IN_FOCUSED_WINDOW);
*/