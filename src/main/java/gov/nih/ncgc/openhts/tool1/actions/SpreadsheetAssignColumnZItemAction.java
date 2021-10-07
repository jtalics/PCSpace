/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.plotManager.Axes;
import gov.nih.ncgc.openhts.tool1.pointsetManager.ColumnHead;
import gov.nih.ncgc.openhts.tool1.spreadsheet.PointsetTable;

public class SpreadsheetAssignColumnZItemAction extends Tool1Action {
  private final Session session;

  public SpreadsheetAssignColumnZItemAction(final Session session) {
    super("Assign this column to Z-axis", null);
    this.session = session;
    putValue(Action.MNEMONIC_KEY, null/* KeyEvent.VK_X */);
    putValue(Action.ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "Assign this column to Z-axis");
    putValue(LONG_DESCRIPTION, "Assign this column to Z-axis");
    setEnabled(true);
    // CSH.setHelpIDString(assignColumnXItem, "column_x_tables_data_menu");
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String) getValue(Action.NAME));
    try {
      final PointsetTable pointsetTable = session.spreadsheet
          .getPointsetTableInSelectedTab();
      ColumnHead columnHead = pointsetTable.getSelectedColumnHead();
      session.pointsetManager.assignAxisToSelectedColumnHeadForPointset(
          Axes.Z_AXIS, columnHead, pointsetTable.getPointset());
    }
    catch (final Throwable ex) {
      session.errorSupport(" ", ex, "todo");
    }
  }

  private static final long serialVersionUID = 1L;
}
