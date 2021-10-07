package gov.nih.ncgc.openhts.tool1.spreadsheet;

import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;

/**
 *
 */
class TabbedPaneListener extends MouseAdapter {
  /**
   * 
   */
  private final Session session;

  /**
   * @param spreadsheet
   */
  TabbedPaneListener(final Session session) {
    this.session = session;
  }

  /**
   *
   */
  @Override
  public void mousePressed(final MouseEvent ev) {
    try {
      if (ev.isConsumed()) { // taken care of already
        return;
      }
      // ignore everything except right mouse button
      if ((ev.getModifiers() & InputEvent.BUTTON3_MASK) == 0) {
        ev.consume();
        return;
      }
      // Set the text in the popups.
      if (ev.getSource() == session.spreadsheet.tabbedPane) {
        int i = session.spreadsheet.tabbedPane.getSelectedIndex();
        if (i<0) {
          session.statusPanel.log2("no tab is selected");
          return;
        }
        final Pointset pointset = session.pointsetManager.getPointsetAt(i);
        final PointsetTable table = session.spreadsheet.getPointsetTable(pointset);
        if (table.isDisplayHidden()) {
          // System.out.println("this: "+this.hashCode()+" this.tabIndex:
          // "+this.tabIndex);
          Spreadsheet.tabPopupLabel.setText(table.getTable().getSelectedRowCount()
              + " of " + pointset.getPointCount() + " rows selected from \""
              + pointset.getName() + "\"");
        }
        else {
          Spreadsheet.tabPopupLabel.setText(table.getTable().getSelectedRowCount()
              + " of " + pointset.getNVisible() + " rows selected from \""
              + session.spreadsheet.getName() + "\"");
        }
        if (table.unlocked) {
          session.actionManager.spreadsheetUnlockTableAction.setEnabled(false);
          session.actionManager.spreadsheetLockTableAction.setEnabled(true);
        }
        else {
          session.actionManager.spreadsheetLockTableAction.setEnabled(true);
          session.actionManager.spreadsheetUnlockTableAction.setEnabled(false);
        }
        for (i = 0; i < session.pointsetManager.getPointsetCount(); i++) {
          if (session.spreadsheet.getPointsetTable(pointset).isUnlocked() == true) {
            break;
          }
        }
        if (i >= session.spreadsheet.tabbedPane.getTabCount()) { // none active
          session.actionManager.spreadsheetLockAllTablesAction.setEnabled(false);
        }
        else { // some or all active
          session.actionManager.spreadsheetUnlockAllTablesAction.setEnabled(true);
        }
        for (i = 0; i < session.pointsetManager.getPointsetCount(); i++) {
          if (session.spreadsheet.getPointsetTable(pointset).isUnlocked() == false) {
            break;
          }
        }
        if (i >= session.spreadsheet.tabbedPane.getTabCount()) { // none inactive
          session.actionManager.spreadsheetUnlockAllTablesAction.setEnabled(false);
        }
        else { // some or all active
          session.actionManager.spreadsheetLockAllTablesAction.setEnabled(true);
        }
        // readjust since pane moves when selected and we want
        // popup to be on it.
        final Rectangle r = session.spreadsheet.tabbedPane.getBoundsAt(session.spreadsheet.tabbedPane.getSelectedIndex());
        session.spreadsheet.tabPopupMenu.show(session.spreadsheet.tabbedPane, r.x + r.width / 2, r.y + r.height / 2);
      }
    }
    catch (final Throwable ex) {
      session.spreadsheet.session.errorSupport("While listening to tabbed pane: ", ex,
          "mouse_tabbed_pane_error");
    }
  }
}