package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;

public class CanvasNonOrthogonalAction extends Tool1Action {
  private Session session;

  public CanvasNonOrthogonalAction(final Session session) {
    super("Non-orthogonal viewing", AppLafManager
        .getIcon(AppLafManager.IconKind.ICON_NONORTHOGONAL));
    this.session = session;
    putValue(ACTION_COMMAND_KEY,getClass().getName());
    // putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(SHORT_DESCRIPTION, "Non-orthogonal viewing (o)");
    putValue(LONG_DESCRIPTION, "Non-orthogonal viewing (o)");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String) getValue(Action.NAME));
    try {
      session.plotManager.view.setOrthogonalMode(false);
    }
    catch (final Throwable ex) {
      session.errorSupport("", ex, "nonorthogonal_canvas_button_error");
    }
  }

  private static final long serialVersionUID = 1L;
}
