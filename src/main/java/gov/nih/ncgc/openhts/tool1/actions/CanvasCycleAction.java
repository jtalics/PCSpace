package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;

public class CanvasCycleAction extends Tool1Action {
  private Session session;

  public CanvasCycleAction(final Session session) {
    super("Cycle drawing order",AppLafManager.getIcon(AppLafManager.IconKind.ICON_CYCLE));
    this.session = session;
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0));
    putValue(SHORT_DESCRIPTION, "Cycle data drawing order (F8)");
    putValue(LONG_DESCRIPTION, "todo");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
  try {
      session.plotManager.cyclePointsetOrder();
    }
    catch (final Throwable ex) {
      session.errorSupport("", ex, "cycle_canvas_button_error");
    }
  }

  private static final long serialVersionUID = 1L;
}
/*
rootPane.registerKeyboardAction(new ActionListener() {
  public void actionPerformed(ActionEvent actionEvent) {
    try {
      session.plotter.canvas.cycleDataOrder();
    }
    catch (Throwable ex) {
      session.error("", ex, "order_canvas_canvas_key_error");
    }
  }
}, KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0),
    JComponent.WHEN_IN_FOCUSED_WINDOW);
*/
