package gov.nih.ncgc.openhts.tool1.actions;

// TEMPLATE FOR NEW ACTION

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.FileFilters;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.SplashWindow;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.DataSource;

public class DataSourceExportAction extends Tool1Action {
  private Session session;

  public DataSourceExportAction(final Session session) {
    super("Export datasource ",null);
    this.session = session;
    //putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_X, 0));
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "Export datasource");
    putValue(LONG_DESCRIPTION, "Export datasource");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      DataSource dataSource = session.dataSourceManager.getViewlet().getSelectedDataSource();
      if (dataSource == null) {
        throw new Exception("no dataSource selected");
      }
      final JFileChooser fileChooser = new JFileChooser(session.cwd);
      fileChooser.setDialogTitle("Select data source to export");
      fileChooser.setFileFilter(FileFilters.dataSourceXmlFileFilter);
      fileChooser.setAcceptAllFileFilterUsed(false);
      if (fileChooser.showOpenDialog(SplashWindow.getInstance()) == JFileChooser.APPROVE_OPTION) {
        session.dataSourceManager.harden(dataSource,fileChooser.getSelectedFile());
        session.statusPanel.log2("Successfully exported data source: "+dataSource.getName());
      }
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot export because: \n",ex,"TODO");
    }
    finally {
      // nop
    }
  }

  private static final long serialVersionUID = 1L;
}
