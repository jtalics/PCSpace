/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;

public class ViewCenterActionAction extends Tool1Action {
  private Session session;

  public ViewCenterActionAction(final Session session) {
    super("center subject origin", null);
    this.session = session;
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_C, 0));
    putValue(Action.ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "center subject origin");
    putValue(SHORT_DESCRIPTION, "center subject origin");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      System.out.println("(center subject origin)");
      session.plotManager.view.centerOrigin();
      session.plotManager.canvas.refresh();
    }
    catch (final Throwable ex) {
      session.errorSupport("", ex, "view_center_origin_canvas_key_error");
    } 
  }

  private static final long serialVersionUID = 1L;
}

/*
rootPane.registerKeyboardAction(new ActionListener() {
  public void actionPerformed(ActionEvent actionEvent) {
    try {
      System.out.println("(center subject origin)");
      session.plotter.view.centerOrigin();
      session.plotter.canvas.refresh();
    }
    catch (Throwable ex) {
      session.error("", ex, "view_center_origin_canvas_key_error");
    }
  }
}, KeyStroke.getKeyStroke(KeyEvent.VK_C, 0),
    JComponent.WHEN_IN_FOCUSED_WINDOW);
*/