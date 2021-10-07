/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;

public class HelpKeyBindingsAction extends Tool1Action {
  private Session session;

  public HelpKeyBindingsAction(final Session session) {
    super("Actions", null);
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_H);
    putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_H, 0));
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "Actions");
    putValue(LONG_DESCRIPTION, "Actions that user can do");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      session.dialogManager.getCanvasHelpDialog().showDialog(); 
    }
    catch (final Throwable ex) {
      session.errorSupport("", ex, "help_canvas_key_error");
    }    
  }

  private static final long serialVersionUID = 1L;
}
/*
rootPane.registerKeyboardAction(new ActionListener() {
  public void actionPerformed(ActionEvent actionEvent) {
    try {
      session.dialogManager.getCanvasHelpDialog();
    }
    catch (Throwable ex) {
      session.error("", ex, "help_canvas_key_error");
    }
  }
}, KeyStroke.getKeyStroke(KeyEvent.VK_H, 0),
    JComponent.WHEN_IN_FOCUSED_WINDOW);
*/