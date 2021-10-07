package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;

public class ViewStereoAction extends Tool1Action {
  private Session session;

  public ViewStereoAction(final Session session) {
    super("View: Stereo", null);
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_S);
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, 0));
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "display stereogram pair of images");
    putValue(LONG_DESCRIPTION, "display stereogram pair of images");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String) getValue(Action.NAME));
    try {
      session.setWaitCursor(true);
      session.plotManager.canvas.toggleStereo();
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot do stereo view because:\n", ex,
          "stereo_view_menu_error");
    }
    finally {
      session.setWaitCursor(false);
    }
  }

  private static final long serialVersionUID = 1L;
}
/*
 * rootPane.registerKeyboardAction(new ActionListener() { public void
 * actionPerformed(ActionEvent actionEvent) { try {
 * session.plotter.canvas.toggleStereo(); } catch (Throwable ex) {
 * session.error("", ex, "toggle_stereo_canvas_key_error"); } System.out
 * .println("(stereo: " + session.plotter.view.getStereo() + ")"); } },
 * KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
 */
