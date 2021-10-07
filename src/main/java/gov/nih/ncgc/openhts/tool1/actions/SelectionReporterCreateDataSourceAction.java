package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;

public class SelectionReporterCreateDataSourceAction extends Tool1Action {
  private Session session;

  public SelectionReporterCreateDataSourceAction(final Session session) {
    super("Create data source", AppLafManager
        .getIcon(AppLafManager.IconKind.ICON_VIEW_SELECTED_ROWS_ALL_TABLES));
    this.session = session;
    //putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACCELERATOR_KEY, null);
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "Create data source from selections in spreadsheet");
    putValue(LONG_DESCRIPTION, "Create data source from selections in spreadsheet");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      session.selectionReporter.createDataSource();
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot create data source because: ", ex,
          "todo");
    }
  }

  private static final long serialVersionUID = 1L;
}
