/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.plotManager.Canvas;

public class ViewRotateDownCtrlAction extends Tool1Action {
  private Session session;

  public ViewRotateDownCtrlAction(final Session session) {
    super("Rotate down 90", null);
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK));
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "Rotate down 90");
    putValue(LONG_DESCRIPTION, "Rotate down 90");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      if (session.plotManager.getDim() == 3) {
        session.plotManager.view.rotate(0, 1, Canvas.NINETY_DEGREES);
      }
      else if (session.plotManager.getDim() == 2) {
        session.plotManager.view.rotate(Canvas.NINETY_DEGREES, -1.0f);
      }
      session.plotManager.canvas.refresh();
    }
    catch (final Throwable ex) {
      session.errorSupport("", ex, "view_rotate_canvas_key_error");
    }
    
  }

  private static final long serialVersionUID = 1L;
}
/*
rootPane.registerKeyboardAction(new ActionListener() {
  public void actionPerformed(ActionEvent actionEvent) {
  }
}, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK),
    JComponent.WHEN_IN_FOCUSED_WINDOW);
*/