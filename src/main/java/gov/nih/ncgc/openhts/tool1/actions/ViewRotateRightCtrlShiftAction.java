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

public class ViewRotateRightCtrlShiftAction extends Tool1Action {
  private Session session;

  public ViewRotateRightCtrlShiftAction(final Session session) {
    super("Rotate right 180", null);
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
    putValue(ACTION_COMMAND_KEY, "Rotate right 180");
    putValue(SHORT_DESCRIPTION, "Rotate right 180");
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
        theta = (float) Math.PI;
      }
      if (session.plotManager.getDim() == 3) {
        session.plotManager.view.rotate(1, 0, theta);
      }
      else if (session.plotManager.getDim() == 2) {
        session.plotManager.view.rotate(theta, 1.0f);
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
    try {
      float theta;
      if (session.plotter.canvas.getOrthogonalMode()) {
        theta = Canvas.NINETY_DEGREES;
      }
      else {
        theta = (float) Math.PI;
      }
      if (session.plotter.subject.axes.getDim() == 3) {
        session.plotter.view.rotate(1, 0, theta);
      }
      else if (session.plotter.subject.axes.getDim() == 2) {
        session.plotter.view.rotate(theta, 1.0f);
      }
      session.plotter.canvas.refresh();
    }
    catch (Throwable ex) {
      session.error("", ex, "view_rotate_canvas_key_error");
    }
  }
}, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK
    | InputEvent.SHIFT_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
*/