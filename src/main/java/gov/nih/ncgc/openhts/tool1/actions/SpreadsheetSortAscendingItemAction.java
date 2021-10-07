/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.spreadsheet.PointsetTable;

public class SpreadsheetSortAscendingItemAction extends Tool1Action {
  private final Session session;
  private PointsetTable pointsetTable;

  public SpreadsheetSortAscendingItemAction(final Session session) {
    super("Ascending sort of column", null);
    this.session = session;
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(Action.ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "Ascending sort of column");
    putValue(LONG_DESCRIPTION, "Ascending sort of column");
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
//          // ascendingTableSortString();
//        }
//        else if (table.columnSelected > 1) {
//          // ascendingTableSortFloat();
//        }
//      }
//      else {
//        if (table.columnSelected == 0) {
//          // ascendingTableSortString();
//        }
//        else if (table.columnSelected > 0) {
//          // ascendingTableSortFloat();
//        }
//      }
//      table.columnSelected = -1;
    }
    catch (final Throwable ex) {
      session.errorSupport(" ", ex,
          "ascending_tables_data_menu_error");
    }
  }

  private static final long serialVersionUID = 1L;

}
