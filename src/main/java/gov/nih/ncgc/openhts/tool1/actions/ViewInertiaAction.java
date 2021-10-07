package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;

public class ViewInertiaAction extends Tool1Action {
  private Session session;

  public ViewInertiaAction(final Session session) {
    super("View: Inertia", null);
    this.session = session;
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_I);
    putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_I, 0));
    putValue(Action.ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "continue rotation after mouse release");
    putValue(LONG_DESCRIPTION, "continue rotation after mouse release");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {
        session.setWaitCursor(true);
        session.plotManager.view.toggleInertia();
      }
      catch (final Throwable ex) {
        session.errorSupport("Cannot do inertia view because:\n", ex,
            "inertia_view_menu_error");
      }
      finally {
        session.setWaitCursor(false);
      }
  }

  private static final long serialVersionUID = 1L;
}
/*
rootPane.registerKeyboardAction(new ActionListener() {
  public void actionPerformed(ActionEvent actionEvent) {
    try {
      session.plotter.canvas.toggleInertia();
      System.out.println("(toggled inertia)");
    }
    catch (Throwable ex) {
      session.error("", ex, "toggle_inertia_canvas_key_error");
    }
  }
}, KeyStroke.getKeyStroke(KeyEvent.VK_I, 0),
    JComponent.WHEN_IN_FOCUSED_WINDOW);
*/