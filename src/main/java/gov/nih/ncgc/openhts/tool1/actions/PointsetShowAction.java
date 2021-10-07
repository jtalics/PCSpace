package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;

public class PointsetShowAction extends Tool1Action {
  private Session session;

  public PointsetShowAction(final Session session) {
    super("Show",null);
    this.session = session;
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
    putValue(Action.ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "Show pointset on canvas");
    putValue(LONG_DESCRIPTION, "Show pointset on canvas");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      session.pointsetManager.setSelectedPointsetVisible(true);
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot show because:\n", ex,
          "add_data_menu_error");
    }
    finally {
      // nop
    }
  }

  private static final long serialVersionUID = 1L;
}
