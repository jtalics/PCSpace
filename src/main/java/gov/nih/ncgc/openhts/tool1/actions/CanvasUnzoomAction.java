package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;

public class CanvasUnzoomAction extends Tool1Action {
  private Session session;

  public CanvasUnzoomAction(final Session session) {
    super("Unzoom completely",AppLafManager.getIcon(AppLafManager.IconKind.ICON_UNZOOM));
    this.session = session;
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
    putValue(SHORT_DESCRIPTION, "Unzoom completely (F7)");
    putValue(LONG_DESCRIPTION, "Unzoom completely (F7)");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {
        session.plotManager.view.unzoom();
      }
      catch (final Throwable ex) {
        session.errorSupport("" , ex, "unzoom_canvas_button_error");
      }
  }

  private static final long serialVersionUID = 1L;
}

/*
rootPane.registerKeyboardAction(new ActionListener() {
  public void actionPerformed(ActionEvent actionEvent) {
    try {
      session.plotter.view.unzoom();
    }
    catch (Throwable ex) {
      session.error("", ex, "unzoom_canvas_key_error");
    }
  }
}, KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0),
    JComponent.WHEN_IN_FOCUSED_WINDOW);
// iconStrings[ICON_SELECT_MODE] = "Select mode (F9)";
// iconStrings[ICON_DESELECT_MODE] = "Deselect mode (F9)";
// iconStrings[ICON_DRAG_SELECTION] = "Box un/selection (u)";
// iconStrings[ICON_DRAG_NONE] = "ID & Single selection (ESC)";
// iconStrings[ICON_DRAG_TRANSLATE] = "Translate (m)";
// iconStrings[ICON_DRAG_XYROTATE] = "XY rotate (x)";
// iconStrings[ICON_DRAG_ZROTATE] = "Z rotate (z)";
// iconStrings[ICON_DRAG_ZOOM] = "Zoom (F6)";
// iconStrings[ICON_DRAG_TEXT] = "Text drag mode (t)";
// iconStrings[ICON_UNZOOM] = "Unzoom completely (F7)";
// iconStrings[ICON_CYCLE] = "Cycle data drawing order (F8)";
// iconStrings[ICON_COMPOSE] = "Fit to canvas (Space)";
// iconStrings[ICON_ORTHOGONAL] = "Orthogonal viewing (o)";
// iconStrings[ICON_NONORTHOGONAL] = "Non-orthogonal viewing (o)";
*/