package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;

public class SpreadsheetUnlockAllTablesAction extends Tool1Action {
  private Session session;

  public SpreadsheetUnlockAllTablesAction(final Session session) {
    super("Unlock all tables",AppLafManager.getIcon(AppLafManager.IconKind.ICON_UNLOCK_ALL_TABLES));
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACTION_COMMAND_KEY, "todo");
    putValue(SHORT_DESCRIPTION, "Unlock all tables");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      session.spreadsheet.unlockAllTables();
    }
    catch (final Throwable ex) {
      session.errorSupport("" , ex, "unlock_all_tables_tables_data_button_error");
    }
  }

  private static final long serialVersionUID = 1L;
}
