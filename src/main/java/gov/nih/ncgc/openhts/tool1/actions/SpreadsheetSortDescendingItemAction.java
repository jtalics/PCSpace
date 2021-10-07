/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.spreadsheet.PointsetTable;

public class SpreadsheetSortDescendingItemAction extends Tool1Action {
  private final Session session;

  public SpreadsheetSortDescendingItemAction(final Session session) {
    super("Descending sort of column", null);
    this.session = session;
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(Action.ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "Descending sort of column");
    putValue(LONG_DESCRIPTION, "Descending sort of column");
    setEnabled(true);
    // CSH.setHelpIDString(assignColumnXItem, "column_x_tables_data_menu");
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      final PointsetTable table = session.spreadsheet.getPointsetTableInSelectedTab();
//      if (table.isDisplayHidden()) {
//        if (table.columnSelected == 1) {
//          // descendingTableSortString();
//        }
//        else if (table.columnSelected > 1) {
//          // descendingTableSortFloat();
//        }
//      }
//      else {
//        if (table.columnSelected == 0) {
//          // descendingTableSortString();
//        }
//        else if (table.columnSelected > 0) {
//          // descendingTableSortFloat();
//        }
//      }
//      table.columnSelected = -1;
    }
    catch (final Throwable ex) {
      session.errorSupport(" ", ex,
          "descending_tables_data_menu_error");
    }
  }

  private static final long serialVersionUID = 1L;

}
