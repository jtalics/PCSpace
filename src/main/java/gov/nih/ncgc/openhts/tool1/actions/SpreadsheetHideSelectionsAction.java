package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;

public class SpreadsheetHideSelectionsAction extends Tool1Action {
  private Session session;

  public SpreadsheetHideSelectionsAction(final Session session) {
    super("Hide selections on canvas", AppLafManager
        .getIcon(AppLafManager.IconKind.ICON_HIDE_SELECTIONS));
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACTION_COMMAND_KEY, "todo");
    putValue(SHORT_DESCRIPTION, "Hide selections on canvas (Ctrl-H)");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      session.spreadsheet.hideSelected();
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot hide selections: ", ex,
          "hide_selections_tables_data_button_error");
    }
  }

  private static final long serialVersionUID = 1L;
}
