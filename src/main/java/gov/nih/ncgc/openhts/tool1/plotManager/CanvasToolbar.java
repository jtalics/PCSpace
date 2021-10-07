/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.plotManager;

import javax.help.CSH;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import gov.nih.ncgc.openhts.tool1.Session;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class CanvasToolbar extends JToolBar {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public CanvasToolbar(final Session session) {
    JButton[] buttons;
      this.setOrientation(SwingConstants.VERTICAL);
      setToolTipText("canvas toolbar can be moved");
      CSH.setHelpIDString(this, "toolbar_canvas");
      setBorder(BorderFactory.createRaisedBevelBorder());
      setFloatable(true);
      addPropertyChangeListener(session.plotManager.canvas);
      buttons = new JButton[14];
      buttons[0] = new JButton(session.actionManager.canvasSelectModeAction);
      CSH.setHelpIDString(buttons[0], "select_mode_canvas_button");
      buttons[1] = new JButton(session.actionManager.canvasDeselectModeAction);
      CSH.setHelpIDString(buttons[1], "deselect_mode_canvas_button");
      buttons[2] = new JButton(session.actionManager.canvasDragSelectionAction);
      CSH.setHelpIDString(buttons[2], "drag_selection_canvas_button");
      buttons[3] = new JButton(session.actionManager.canvasDragTranslateAction);
      CSH.setHelpIDString(buttons[3], "drag_translate_canvas_button");
      buttons[4] = new JButton(session.actionManager.canvasDragXYRotateAction);
      CSH.setHelpIDString(buttons[4], "drag_xyrotate_canvas_button");
      buttons[5] = new JButton(session.actionManager.canvasDragZRotateAction);
      CSH.setHelpIDString(buttons[5], "drag_zrotate_canvas_button");
      buttons[6] = new JButton(session.actionManager.canvasZoomDragModeAction);
      CSH.setHelpIDString(buttons[6], "drag_zoom_canvas_button");
      buttons[7] = new JButton(session.actionManager.canvasDragTextAction);
      CSH.setHelpIDString(buttons[7], "drag_text_canvas_button");
      buttons[8] = new JButton(session.actionManager.canvasDragNoneAction);
      CSH.setHelpIDString(buttons[8], "drag_none_canvas_button");
      buttons[9] = new JButton(session.actionManager.canvasUnzoomAction);
      CSH.setHelpIDString(buttons[9], "unzoom_canvas_button");
      buttons[10] = new JButton(session.actionManager.canvasCycleAction);
      CSH.setHelpIDString(buttons[10], "cycle_canvas_button");
      buttons[11] = new JButton(session.actionManager.canvasComposeAction);
      CSH.setHelpIDString(buttons[11], "compose_canvas_button");
      buttons[12] = new JButton(session.actionManager.canvasOrthogonalAction);
      CSH.setHelpIDString(buttons[12], "orthogonal_canvas_button");
      buttons[13] = new JButton(session.actionManager.canvasNonOrthogonalAction);
      CSH.setHelpIDString(buttons[13], "nonorthogonal_canvas_button");
      for (int i = 0; i < 14; i++) {
        buttons[i].setText("");
      }
      add(buttons[0]);
      add(buttons[1]);
      add(new JSeparator());
      add(buttons[2]);
      add(buttons[3]);
      add(buttons[4]);
      add(buttons[5]);
      add(buttons[6]);
      add(buttons[7]);
      add(buttons[8]);
      add(new JSeparator());
      add(buttons[9]);
      add(buttons[10]);
      add(buttons[11]);
      add(new JSeparator());
      add(buttons[12]);
      add(buttons[13]);
  }
  
}
