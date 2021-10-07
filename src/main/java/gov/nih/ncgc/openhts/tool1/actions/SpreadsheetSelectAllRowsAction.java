package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;

public class SpreadsheetSelectAllRowsAction extends Tool1Action {
  private Session session;

  public SpreadsheetSelectAllRowsAction(final Session session) {
    super("Select all rows in this table",AppLafManager.getIcon(AppLafManager.IconKind.ICON_SELECT_ALL_ROWS_THIS_TABLE));
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACTION_COMMAND_KEY, "todo");
    putValue(SHORT_DESCRIPTION, "Select all rows in this table");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      session.spreadsheet.selectAllRowsThisTable();
    }
    catch (final Throwable ex) {
      session.errorSupport("" , ex,
          "select_all_rows_this_table_tables_data_button_error");
    }
  }

  private static final long serialVersionUID = 1L;
}
