package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;

public class SelectionReporterCreateReportAction extends Tool1Action {
  private Session session;

  public SelectionReporterCreateReportAction(final Session session) {
    super("Create report", AppLafManager
        .getIcon(AppLafManager.IconKind.ICON_VIEW_SELECTED_ROWS_ALL_TABLES));
    this.session = session;
    //putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACCELERATOR_KEY, null);
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "Create a report from current spreadsheet selections");
    putValue(LONG_DESCRIPTION, "Create a report from current spreadsheet selections");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      session.selectionReporter.createReport();
    }
    catch (final Throwable ex) {
      session.errorSupport("todo: ", ex,
          "todo");
    }
  }

  private static final long serialVersionUID = 1L;
}
