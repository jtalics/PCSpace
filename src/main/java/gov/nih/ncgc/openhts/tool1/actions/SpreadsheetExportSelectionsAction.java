package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;

public class SpreadsheetExportSelectionsAction extends Tool1Action {
  private Session session;

  public SpreadsheetExportSelectionsAction(final Session session) {
    super("Export selections to CSV",AppLafManager.getIcon(AppLafManager.IconKind.ICON_EXPORT_SELECTIONS));
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACTION_COMMAND_KEY, "todo");
    putValue(SHORT_DESCRIPTION, "Export selections to CSV file (Ctrl-E)");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      session.spreadsheet.exportSelections();
    }
    catch (final Throwable ex) {
      session.errorSupport("" , ex, "export_selections_tables_data_button_error");
    }
  }

  private static final long serialVersionUID = 1L;
}
