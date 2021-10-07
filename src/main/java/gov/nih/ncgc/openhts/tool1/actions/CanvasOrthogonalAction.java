package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;

public class CanvasOrthogonalAction extends Tool1Action {
  private Session session;

  public CanvasOrthogonalAction(final Session session) {
    super("Orthogonal viewing", AppLafManager
        .getIcon(AppLafManager.IconKind.ICON_ORTHOGONAL));
    this.session = session;
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, 0));
    putValue(SHORT_DESCRIPTION, "Orthogonal viewing (o)");
    putValue(LONG_DESCRIPTION, "Orthogonal viewing (o)");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String) getValue(Action.NAME));
    try {
      session.plotManager.view.setOrthogonalMode(true);
    }
    catch (final Throwable ex) {
      session.errorSupport("", ex, "orthogonal_canvas_button_error");
    }
  }

  private static final long serialVersionUID = 1L;
}
