package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.ToolTipManager;
import gov.nih.ncgc.openhts.tool1.Session;

public class HelpBubbleAction extends Tool1Action {
  private Session session;

  public HelpBubbleAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Bubble help");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_B);
    putValue(Action.ACTION_COMMAND_KEY, "Bubble help");
    putValue(SHORT_DESCRIPTION, "popup hints when the mouse hovers over a ui item");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {

        if (ToolTipManager.sharedInstance().isEnabled()) {
          ToolTipManager.sharedInstance().setEnabled(false);
        }
        else {
          ToolTipManager.sharedInstance().setEnabled(true);
        }
        session.mainMenuBar.helpBubbleMenuItem.setSelected(ToolTipManager.sharedInstance().isEnabled());
      }
      catch (final Throwable ex) {
        session.errorSupport("Cannot do bubble help because:\n", ex, "bubble_help_menu_error");
      }
      finally {
        session.setWaitCursor(false);
      }
  }

  private static final long serialVersionUID = 1L;
}
