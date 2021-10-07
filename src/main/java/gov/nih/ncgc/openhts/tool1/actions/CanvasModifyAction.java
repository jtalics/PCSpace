package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.plotManager.CanvasModifyDialog;

public class CanvasModifyAction extends Tool1Action {
  private Session session;

  public CanvasModifyAction(final Session session) {
    //super("",AppLafManager.getIcon(AppLafManager.IconKind.));
    this.session = session;
    putValue(Action.NAME, "Canvas: Modify");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_M);
    putValue(Action.ACTION_COMMAND_KEY, "Canvas: modify");
    putValue(SHORT_DESCRIPTION, "change properties of canvas");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {
         final CanvasModifyDialog canvasModifyDialog = session.dialogManager.getCanvasModifyDialog();
         canvasModifyDialog.showDialog();
         session.plotManager.saved = false;
         session.updateEnablement();

      }
      catch (final Throwable ex) {
        session.errorSupport("Cannot resize canvas because:\n", ex, "modify_canvas_menu_error");
      }
      finally {
        // nop
      }
  }

  private static final long serialVersionUID = 1L;
}
