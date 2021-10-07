package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;

public class SpreadsheetRevealHiddenAction extends Tool1Action {
  private Session session;

  public SpreadsheetRevealHiddenAction(final Session session) {
    super("Reveal hidden data in tables", AppLafManager
        .getIcon(AppLafManager.IconKind.ICON_REVEAL_HIDDEN));
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACTION_COMMAND_KEY, "todo");
    putValue(SHORT_DESCRIPTION, "Reveal hidden numbers as tables rows");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      session.spreadsheet.setDisplayHidden(true);
    }
    catch (final Throwable ex) {
      session.errorSupport("", ex, "reveal_hidden_tables_data_button_error");
    }
  }

  private static final long serialVersionUID = 1L;
}
