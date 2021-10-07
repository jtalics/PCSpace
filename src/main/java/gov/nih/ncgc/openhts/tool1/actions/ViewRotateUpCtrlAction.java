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

public class ViewRotateUpCtrlAction extends Tool1Action {
  private Session session;

  public ViewRotateUpCtrlAction(final Session session) {
    super("Rotate up 90", null);
    this.session = session;
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK));
    putValue(Action.ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "Rotate up 90");
    putValue(LONG_DESCRIPTION, "Rotate up 90");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      if (session.plotManager.getDim() == 3) {
        session.plotManager.view.rotate(0, -1, Canvas.NINETY_DEGREES);
      }
      else if (session.plotManager.getDim() == 2) {
        session.plotManager.view.rotate(Canvas.NINETY_DEGREES, 1.0f);
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
      if (session.plotter.subject.axes.getDim() == 3) {
        session.plotter.view.rotate(0, -1, Canvas.NINETY_DEGREES);
      }
      else if (session.plotter.subject.axes.getDim() == 2) {
        session.plotter.view.rotate(Canvas.NINETY_DEGREES, 1.0f);
      }
      session.plotter.canvas.refresh();
    }
    catch (Throwable ex) {
      session.error("", ex, "view_rotate_canvas_key_error");
    }
  }
}, KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK),
    JComponent.WHEN_IN_FOCUSED_WINDOW);
*/