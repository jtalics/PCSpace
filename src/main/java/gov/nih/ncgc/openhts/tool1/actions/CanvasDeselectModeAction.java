package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.plotManager.Canvas;

public class CanvasDeselectModeAction extends Tool1Action {
  private Session session;

  public CanvasDeselectModeAction(final Session session) {
    super("Deselect mode",AppLafManager.getIcon(AppLafManager.IconKind.ICON_DESELECT_MODE));
    this.session = session;
    putValue(ACTION_COMMAND_KEY, "todo");
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(SHORT_DESCRIPTION, "Deselect mode (F9)");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {
        session.plotManager.canvas.setSelectionMode(Canvas.DESELECT_SELECTION_MODE);
      }
      catch (final Throwable ex) {
        session.errorSupport("" , ex, "deselect_mode_canvas_button_error");
      }
  }

  private static final long serialVersionUID = 1L;
}
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
