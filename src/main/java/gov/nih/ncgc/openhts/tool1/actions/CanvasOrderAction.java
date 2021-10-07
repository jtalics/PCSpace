package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.plotManager.CanvasOrderDialog;

public class CanvasOrderAction extends Tool1Action {
  private Session session;

  public CanvasOrderAction(final Session session) {
    //super("",AppLafManager.getIcon(AppLafManager.IconKind.));
    this.session = session;
    putValue(NAME, "Canvas: Order");
    putValue(MNEMONIC_KEY, KeyEvent.VK_O);
    putValue(ACTION_COMMAND_KEY, "Canvas: Order");
    putValue(SHORT_DESCRIPTION, "change the order in which datasets are drawn");

    setEnabled(true);
  }
//CSH.setHelpIDString(orderCanvasItem, "order_canvas_menu");

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      final CanvasOrderDialog canvasOrderDialog = session.dialogManager.getCanvasOrderDialog();
      canvasOrderDialog.showDialog();
      session.plotManager.saved = false;
      session.plotManager.canvas.refresh();
      session.updateEnablement();
     }
     catch (final Throwable ex) {
       session.errorSupport("" , ex, "cycle_canvas_menu_error");
     }
  }

  private static final long serialVersionUID = 1L;
}
