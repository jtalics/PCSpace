/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;

public class ViewCopBackAction extends Tool1Action {
  private Session session;

  public ViewCopBackAction(final Session session) {
    super("Move center of perspective back", null);
    this.session = session;
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_B, 0));
    putValue(Action.ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "Move center of perspective back");
    putValue(LONG_DESCRIPTION, "Move center of perspective back");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      System.out.println("(Move center of perspective back)");
      session.plotManager.view.moveCop(-5.0f);
      session.plotManager.canvas.refresh();
    }
    catch (final Throwable ex) {
      session.errorSupport("", ex, "view_move_cop_canvas_key_error");
    }
    
  }

  private static final long serialVersionUID = 1L;
}
/*

rootPane.registerKeyboardAction(new ActionListener() {
  public void actionPerformed(ActionEvent actionEvent) {
    try {
      System.out.println("(Move center of perspective back)");
      session.plotter.view.moveCop(-5.0f);
      session.plotter.canvas.refresh();
    }
    catch (Throwable ex) {
      session.error("", ex, "view_move_cop_canvas_key_error");
    }
  }
}, KeyStroke.getKeyStroke(KeyEvent.VK_B, 0),
    JComponent.WHEN_IN_FOCUSED_WINDOW);
*/