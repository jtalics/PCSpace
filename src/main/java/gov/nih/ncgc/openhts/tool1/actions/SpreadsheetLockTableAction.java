package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;

public class SpreadsheetLockTableAction extends Tool1Action {
  private Session session;

  public SpreadsheetLockTableAction(final Session session) {
    super("Lock this table",AppLafManager.getIcon(AppLafManager.IconKind.ICON_LOCK_THIS_TABLE));
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACTION_COMMAND_KEY, "todo");
    putValue(SHORT_DESCRIPTION, "Lock this table");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      session.spreadsheet.lockThisTable();
    }
    catch (final Throwable ex) {
      session.errorSupport("" , ex, "lock_this_table_tables_data_button_error");
    }
  }

  private static final long serialVersionUID = 1L;
}
