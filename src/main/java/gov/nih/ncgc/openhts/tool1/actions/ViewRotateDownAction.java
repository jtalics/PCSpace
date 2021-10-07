/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.plotManager.Canvas;

public class ViewRotateDownAction extends Tool1Action {
  private Session session;

  public ViewRotateDownAction(final Session session) {
    super("todo", null);
    this.session = session;
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
    putValue(Action.ACTION_COMMAND_KEY,getClass().getName());
    putValue(SHORT_DESCRIPTION, "Rotate slightly");
    putValue(LONG_DESCRIPTION, "Rotate slightly - pi/32");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      float theta;
      if (session.plotManager.view.getOrthogonalMode()) {
        theta = Canvas.NINETY_DEGREES;
      }
      else {
        theta = (float) Math.PI / 32.0f;
      }
      if (session.plotManager.getDim() == 3) {
        session.plotManager.view.rotate(0, 1, theta);
      }
      else if (session.plotManager.getDim() == 2) {
        session.plotManager.view.rotate(theta, -1.0f);
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
}, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
    JComponent.WHEN_IN_FOCUSED_WINDOW);
*/