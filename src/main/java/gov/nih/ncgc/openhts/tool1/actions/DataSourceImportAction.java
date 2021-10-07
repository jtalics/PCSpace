package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.FileFilters;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.SplashWindow;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.DataSource;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.DataSourceReader;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class DataSourceImportAction extends Tool1Action {
  private Session session;

  public DataSourceImportAction(final Session session) {
    super("Import data source", null);
    this.session = session;
    // putValue(MNEMONIC_KEY, KeyEvent.VK_I);
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, 0));
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "import data source");
    putValue(LONG_DESCRIPTION, "import data source");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String) getValue(Action.NAME));
    try {
      final JFileChooser fileChooser = new JFileChooser(session.cwd);
      fileChooser.setDialogTitle("Select data source to import");
      fileChooser.setFileFilter(FileFilters.dataSourceXmlFileFilter);
      fileChooser.setAcceptAllFileFilterUsed(false);
      if (fileChooser.showOpenDialog(SplashWindow.getInstance()) == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        DataSource dataSource = new DataSource();
        session.dataSourceManager.registerDataSource(dataSource);
        session.dataSourceManager.soften(dataSource, new BufferedInputStream(
            new FileInputStream(file)), file.getName());
        DataSourceReader dataSourceReader = new DataSourceReader(session,dataSource);
        session.dataSourceManager.processUnrecognizedColumnNames(dataSourceReader);
        session.statusPanel.log2("Successfully imported data source: "
            + dataSource.getName());
      }
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot Import datasource because: \n", ex, "TODO");
    }
    finally {
      // nop
    }
  }

  private static final long serialVersionUID = 1L;
}
