package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;

public class SpreadsheetSelectAllRowsUnlockedAction extends Tool1Action {
  private Session session;

  public SpreadsheetSelectAllRowsUnlockedAction(final Session session) {
    super(null,AppLafManager.getIcon(AppLafManager.IconKind.ICON_SELECT_ALL_ROWS_UNLOCKED));
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACTION_COMMAND_KEY, "Select all rows in unlocked tables");
    putValue(SHORT_DESCRIPTION, "read a data file and draw on canvas");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
                   try {
                     session.spreadsheet.selectAllRowsUnlocked();
                   }
                   catch (final Throwable ex) {
                     session.errorSupport("" , ex,
                         "select_all_rows_unlocked_tables_data_button_error");
                   }
  }

  private static final long serialVersionUID = 1L;
}
