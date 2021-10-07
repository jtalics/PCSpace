package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.JFileChooser;
import gov.nih.ncgc.openhts.tool1.FileFilters;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;

public class PointsetExportAction extends Tool1Action {
  private Session session;

  public PointsetExportAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Export pointset");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
    putValue(Action.ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "read a file with a pointset");
    putValue(LONG_DESCRIPTION, "read a pointset file and draw on canvas");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String) getValue(Action.NAME));
    try {
      Pointset pointset = session.pointsetManager.getSelectedPointset();
      if (pointset == null) {
        session.errorHelp("Select one pointset to export.","TODO");
        return;
        // TODO: hook in the pointset selector dialog instead of returning
      }
      final JFileChooser fileChooser = new JFileChooser(session.cwd);
      // String suggestedName =
      // pointset.getName()+"."+FileFilters.pointsetCsvExt
      fileChooser.setDialogTitle("Select pointset file to export.");
      fileChooser.setFileFilter(FileFilters.pointsetTxtFileFilter);
      fileChooser.setAcceptAllFileFilterUsed(false);
      if (fileChooser.showOpenDialog(session.frame) == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        session.cwd = file.getParentFile();
        session.pointsetManager.harden(pointset, file);
      }
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
