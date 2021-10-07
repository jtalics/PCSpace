package gov.nih.ncgc.openhts.tool1.actions;

// TEMPLATE FOR NEW ACTION

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.FileFilters;
import gov.nih.ncgc.openhts.tool1.Main;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.SplashWindow;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Basis;

public class BasisImportAction extends Tool1Action {
  private Session session;

  public BasisImportAction(final Session session) {
    super("Import",null);
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_I);
    putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_X, 0));
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "import basis");
    putValue(LONG_DESCRIPTION, "import basis");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      final JFileChooser fileChooser = new JFileChooser(Main.homeDir.getParentFile());
      fileChooser.setDialogTitle("Select basis to import");
      fileChooser.setFileFilter(FileFilters.basisXmlFileFilter);
      fileChooser.setAcceptAllFileFilterUsed(false);
      if (fileChooser.showOpenDialog(SplashWindow.getInstance()) == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        Basis basis = session.descriptorManager.softenBasis(new BufferedInputStream(new FileInputStream(file)),file.getName());
        session.descriptorManager.registerBasis(basis);
        session.statusPanel.log2("Successfully imported descriptor: "+basis.getName());
      }
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot Import basis because: \n",ex,"TODO");
    }
    finally {
      // nop
    }
  }

  private static final long serialVersionUID = 1L;
}
