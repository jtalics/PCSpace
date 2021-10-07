package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;

public class PointsetHideAction extends Tool1Action {
  private Session session;

  public PointsetHideAction(final Session session) {
    super("Hide",null);
    this.session = session;
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
    putValue(Action.ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "Hide pointset");
    putValue(LONG_DESCRIPTION, "Hide pointset");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      session.pointsetManager.setSelectedPointsetVisible(false);
//      session.plotManager.canvas.setStale(true);
//      session.plotManager.canvas.refresh();
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot hide pointset because:\n", ex,
          "add_data_menu_error");
    }
    finally {
      // nop
    }
  }

  private static final long serialVersionUID = 1L;
}
