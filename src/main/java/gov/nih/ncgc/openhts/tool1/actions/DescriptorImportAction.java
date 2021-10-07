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
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.SplashWindow;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Descriptor;

public class DescriptorImportAction extends Tool1Action {
  private Session session;

  public DescriptorImportAction(final Session session) {
    super("Import",null);
    this.session = session;
    //putValue(MNEMONIC_KEY, KeyEvent.VK_I);
    putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_X, 0));
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "import datasource");
    putValue(LONG_DESCRIPTION, "import datasource");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      final JFileChooser fileChooser = new JFileChooser(session.cwd);
      fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
      fileChooser.setDialogTitle("Select descriptor to import.");
      fileChooser.setFileFilter(FileFilters.descriptorXmlFileFilter);
      fileChooser.setAcceptAllFileFilterUsed(false);
      if (fileChooser.showOpenDialog(SplashWindow.getInstance()) == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        Descriptor descriptor = session.descriptorManager.softenDescriptor(new BufferedInputStream(new FileInputStream(file)),file.getPath());
        session.descriptorManager.registerDescriptor(descriptor);
        session.statusPanel.log2("Successfully imported descriptor: "+descriptor.getName());        
      }
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot Import datasource because: \n",ex,"TODO");
    }
    finally {
      // nop
    }
  }

  private static final long serialVersionUID = 1L;
}
