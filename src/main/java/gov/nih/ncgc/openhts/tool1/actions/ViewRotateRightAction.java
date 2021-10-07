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

public class ViewRotateRightAction extends Tool1Action {
  private Session session;

  public ViewRotateRightAction(final Session session) {
    super("Rotate right slightly", null);
    this.session = session;
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));
    putValue(Action.ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "Rotate right slightly");
    putValue(LONG_DESCRIPTION, "Rotate right slightly");
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
        theta = (float) Math.PI / 32.0f;
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
}, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
    JComponent.WHEN_IN_FOCUSED_WINDOW);
*/