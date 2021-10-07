package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.swing.Action;
import javax.swing.JFileChooser;
import gov.nih.ncgc.openhts.tool1.FileFilters;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;

public class PointsetImportAction extends Tool1Action {
  private Session session;

  public PointsetImportAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Import pointset");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
    putValue(Action.ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "read a CSV file with a pointset");
    putValue(LONG_DESCRIPTION, "read a pointset file");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      final JFileChooser fileChooser = new JFileChooser(session.cwd);
      fileChooser.setDialogTitle("Select pointset file to import.");
      fileChooser.setFileFilter(FileFilters.pointsetTxtFileFilter);
      fileChooser.setAcceptAllFileFilterUsed(false);

      if (fileChooser.showOpenDialog(session.frame) != JFileChooser.APPROVE_OPTION) {
        return;
      }
      Pointset pointset = new Pointset(session,null);
      File file = fileChooser.getSelectedFile();
      session.cwd = file.getParentFile();
      session.pointsetManager.soften(pointset,new InputStreamReader(new FileInputStream(file)), file.getName());
      session.pointsetManager.registerPointset(null, pointset);
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot create pointset because:\n", ex,
          "add_data_menu_error");
    }
    finally {
      // nop
    }
  }

  private static final long serialVersionUID = 1L;
}
