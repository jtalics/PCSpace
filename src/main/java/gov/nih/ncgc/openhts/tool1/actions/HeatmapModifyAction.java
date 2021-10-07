package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.heatmap.HeatmapModifyDialog;

public class HeatmapModifyAction extends Tool1Action {
  private Session session;

  public HeatmapModifyAction(final Session session) {
    //super("",AppLafManager.getIcon(AppLafManager.IconKind.));
    this.session = session;
    putValue(Action.NAME, "Modify");
    putValue(Action.MNEMONIC_KEY, null/*KeyEvent.VK_M*/);
    putValue(Action.ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "modify heatmap");
    putValue(LONG_DESCRIPTION, "modify heatmap");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent ev) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {
         final HeatmapModifyDialog heatmapModifyDialog = session.dialogManager.getHeatmapModifyDialog();
         heatmapModifyDialog.showDialog();
         session.updateEnablement();

      }
      catch (final Throwable ex) {
        session.errorSupport("Cannot modify heatmap because:\n", ex, "modify_canvas_menu_error");
      }
      finally {
        // nop
      }
  }

  private static final long serialVersionUID = 1L;
}
