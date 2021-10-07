/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;

public class ViewMagnifyAction extends Tool1Action {
  private Session session;

  public ViewMagnifyAction(final Session session) {
    super("magnify", null);
    this.session = session;
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0));
    putValue(Action.ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "magnify");
    putValue(LONG_DESCRIPTION, "magnify");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      System.out.println("(magnify)");
      session.plotManager.view.magnify(1.1f);
      session.plotManager.canvas.refresh();
    }
    catch (final Throwable ex) {
      session.errorSupport("", ex, "view_magnify_canvas_key_error");
    }    
  }

  private static final long serialVersionUID = 1L;
}
/*
rootPane.registerKeyboardAction(new ActionListener() {
  public void actionPerformed(ActionEvent actionEvent) {
    try {
      System.out.println("(magnify)");
      session.plotter.view.magnify(1.1f);
      session.plotter.canvas.refresh();
    }
    catch (Throwable ex) {
      session.error("", ex, "view_magnify_canvas_key_error");
    }
  }
}, KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, 0),
    JComponent.WHEN_IN_FOCUSED_WINDOW);
*/
