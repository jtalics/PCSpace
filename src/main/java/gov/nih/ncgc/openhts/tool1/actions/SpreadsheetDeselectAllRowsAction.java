package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;

public class SpreadsheetDeselectAllRowsAction extends Tool1Action {
  private Session session;

  public SpreadsheetDeselectAllRowsAction(final Session session) {
    super("Deselect all rows in this table",AppLafManager.getIcon(
        AppLafManager.IconKind.ICON_DESELECT_ALL_ROWS_THIS_TABLE));
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "Deselect all rows in this table");
    // putValue(LONG_DESCRIPTION,
    // "select_all_rows_this_table_tables_data_button");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    // CSH.setHelpIDString(buttons[ICON_DESELECT_ALL_ROWS_THIS_TABLE],
    // "deselect_all_rows_this_table_tables_data_button");
    try {
      session.spreadsheet.doDeselectAllRowsThisTable();
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot deselect all rows in this table because: " , ex,
          "TODO");
    }
  }

  private static final long serialVersionUID = 1L;
}
