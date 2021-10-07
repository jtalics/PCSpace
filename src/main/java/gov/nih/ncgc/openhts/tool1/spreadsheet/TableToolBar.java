/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.spreadsheet;

import javax.help.CSH;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import gov.nih.ncgc.openhts.tool1.Session;

/** Purpose is to ...
 * @author talafousj
 */
public class TableToolBar extends JToolBar {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  TableToolBar(final Session session, final JTabbedPane tabbedPane) {
    setOrientation(SwingConstants.VERTICAL);
    setToolTipText("selection assist toolbar can be dragged");
    CSH.setHelpIDString(this, "toolbar_tables_data");
    setBorder(BorderFactory.createRaisedBevelBorder());
    setFloatable(true);
    final JButton[] buttons = new JButton[15];
    buttons[0] = new JButton(session.actionManager.spreadsheetLockTableAction);
    CSH.setHelpIDString(buttons[0], "lock_this_table_tables_data_button");
    add(buttons[0]);
    buttons[1] = new JButton(session.actionManager.spreadsheetUnlockTableAction);
    CSH.setHelpIDString(buttons[1], "unlock_this_table_tables_data_button");
    add(buttons[1]);
    buttons[2] = new JButton(
        session.actionManager.spreadsheetSelectAllRowsAction);
    add(buttons[2]);
    buttons[3] = new JButton(
        session.actionManager.spreadsheetDeselectAllRowsAction);
    add(buttons[3]);
    buttons[4] = new JButton(
        session.actionManager.spreadsheetToggleSelectionsAction);
    add(buttons[4]);
    add(new JSeparator());
    buttons[5] = new JButton(
        session.actionManager.spreadsheetLockAllTablesAction);
    add(buttons[5]);
    buttons[6] = new JButton(
        session.actionManager.spreadsheetUnlockAllTablesAction);
    add(buttons[6]);
    buttons[7] = new JButton(
        session.actionManager.spreadsheetSelectAllRowsAction);
    add(buttons[7]);
    buttons[8] = new JButton(
        session.actionManager.spreadsheetDeselectAllRowsUnlockedAction);
    add(buttons[8]);
    buttons[9] = new JButton(
        session.actionManager.spreadsheetToggleUnlockedAction);
    add(buttons[9]);
    add(new JSeparator());
    buttons[10] = new JButton(session.actionManager.spreadsheetHideSelectionsAction);
    add(buttons[10]);
    buttons[11] = new JButton(
        session.actionManager.spreadsheetShowSelectionsAction);
    add(buttons[11]);
    buttons[12] = new JButton(
        session.actionManager.spreadsheetExportSelectionsAction);
    add(buttons[12]);
    add(new JSeparator());
    buttons[13] = new JButton(
        session.actionManager.spreadsheetRevealHiddenAction);
    add(buttons[13]);
    buttons[14] = new JButton(session.actionManager.spreadsheetMaskHiddenAction);
    add(buttons[14]);
    for (int i=0; i<15; i++) {
      buttons[i].setText("");
    }
  }
}
