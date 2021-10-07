package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;

public class SpreadsheetUnlockTableAction extends Tool1Action {
  private Session session;

  public SpreadsheetUnlockTableAction(final Session session) {
    super("Unlock this table",AppLafManager.getIcon(AppLafManager.IconKind.ICON_UNLOCK_THIS_TABLE));
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACTION_COMMAND_KEY, "todo");
    putValue(SHORT_DESCRIPTION, "Unlock this table");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {
        session.spreadsheet.unlockThisTable();
      }
      catch (final Throwable ex) {
        session.errorSupport("Cannot unlock table because:\n" , ex, "unlock_table_error");
      }
      finally {
        // nop
      }
  }

  private static final long serialVersionUID = 1L;
}
