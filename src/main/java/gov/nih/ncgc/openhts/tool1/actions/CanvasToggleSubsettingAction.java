/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;

public class CanvasToggleSubsettingAction extends Tool1Action {
  private Session session;

  public CanvasToggleSubsettingAction(final Session session) {
    super("todo", null);
    this.session = session;
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_A, 0));
    putValue(Action.ACTION_COMMAND_KEY, "todo");
    putValue(SHORT_DESCRIPTION, "Toggle subsetting (A)");
    putValue(LONG_DESCRIPTION, "Toggle subsetting (A)");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    
  }

  private static final long serialVersionUID = 1L;
}

/*
rootPane.registerKeyboardAction(new ActionListener() {
  public void actionPerformed(ActionEvent actionEvent) {
    try {
      session.plotter.canvas.toggleSubsettingMode();
    }
    catch (Throwable ex) {
      session.error("", ex, "toggle_subsetting_canvas_key_error");
    }
  }
}, KeyStroke.getKeyStroke(KeyEvent.VK_A, 0),
    JComponent.WHEN_IN_FOCUSED_WINDOW);
*/