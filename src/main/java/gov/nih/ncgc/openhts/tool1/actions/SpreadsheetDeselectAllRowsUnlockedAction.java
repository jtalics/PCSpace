package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;

public class SpreadsheetDeselectAllRowsUnlockedAction extends Tool1Action {
  private Session session;

  public SpreadsheetDeselectAllRowsUnlockedAction(final Session session) {
    super("Deselect all rows in unlocked tables",AppLafManager.getIcon(AppLafManager.IconKind.ICON_DESELECT_ALL_ROWS_UNLOCKED));
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACTION_COMMAND_KEY, "todo");
    putValue(SHORT_DESCRIPTION, "Deselect all rows in unlocked tables (Ctrl-Shift-D)");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {
        session.spreadsheet.deselectAllRowsUnlocked();
      }
      catch (final Throwable ex) {
        session.errorSupport("" , ex,
            "deselect_all_rows_unlocked_tables_data_button_error");
      }
  }

  private static final long serialVersionUID = 1L;
}
