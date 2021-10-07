package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;

public class SpreadsheetToggleSelectionsAction extends Tool1Action {
  private Session session;

  public SpreadsheetToggleSelectionsAction(final Session session) {
    super("Toggle selections in this table",AppLafManager.getIcon(AppLafManager.IconKind.ICON_TOGGLE_THIS_TABLE));
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACTION_COMMAND_KEY, "todo");
    putValue(SHORT_DESCRIPTION, "Toggle selections in this table");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      session.spreadsheet.toggleThisTable();
    }
    catch (final Throwable ex) {
      session.errorSupport("" , ex, "toggle_this_table_tables_data_button_error");
    }
  }

  private static final long serialVersionUID = 1L;
}
