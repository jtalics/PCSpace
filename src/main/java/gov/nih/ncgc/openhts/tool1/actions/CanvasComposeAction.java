package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;

public class CanvasComposeAction extends Tool1Action {
  private Session session;

  public CanvasComposeAction(final Session session) {
    super("Compose subject",AppLafManager.getIcon(AppLafManager.IconKind.ICON_COMPOSE));
    this.session = session;
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(SHORT_DESCRIPTION, "Compose subject by fitting to canvas (Space)");
    putValue(LONG_DESCRIPTION, "todo");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {
        session.plotManager.view.compose();
        session.plotManager.canvas.refresh();
      }
      catch (final Throwable ex) {
        session.errorSupport("" , ex, "compose_canvas_button_error");
      }
  }

  private static final long serialVersionUID = 1L;
}
/*
rootPane.registerKeyboardAction(new ActionListener() {
  public void actionPerformed(ActionEvent actionEvent) {
    try {
      System.out.println("(fit to canvas)");
      session.plotter.view.compose();
      session.plotter.canvas.refresh();
    }
    catch (Throwable ex) {
      session.error("", ex, "view_compose_canvas_key_error");
    }
  }
}, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0),
    JComponent.WHEN_IN_FOCUSED_WINDOW);
*/