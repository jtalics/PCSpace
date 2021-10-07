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
import gov.nih.ncgc.openhts.tool1.descriptorManager.Descriptor;

public class DescriptorExportAction extends Tool1Action {
  private Session session;

  public DescriptorExportAction(final Session session) {
    super("Export descriptor",null);
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
      Descriptor descriptor = session.descriptorManager.getViewlet().getSelectedDescriptor();
      if (descriptor == null) {
        throw new Exception("no descriptor selected");
        // TODO: hook in the selector dialog instead of returning
      }
      final JFileChooser fileChooser = new JFileChooser(session.cwd);
      fileChooser.setDialogTitle("Select descriptor to export");
      fileChooser.setFileFilter(FileFilters.descriptorXmlFileFilter);
      fileChooser.setAcceptAllFileFilterUsed(false);
      if (fileChooser.showOpenDialog(SplashWindow.getInstance()) == JFileChooser.APPROVE_OPTION) {
        session.descriptorManager.harden(descriptor,fileChooser.getSelectedFile());
        session.statusPanel.log2("Successfully exported descriptor: "+descriptor.getName());
      }
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot Export Descriptor because: \n",ex,"TODO");
    }
    finally {
      // nop
    }
  }

  private static final long serialVersionUID = 1L;
}
