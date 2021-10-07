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
import gov.nih.ncgc.openhts.tool1.descriptorManager.Basis;

public class BasisExportAction extends Tool1Action {
  private Session session;

  public BasisExportAction(final Session session) {
    super("Export", null);
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, 0));
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "Export basis");
    putValue(LONG_DESCRIPTION, "Export basis");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String) getValue(Action.NAME));
    try {
      Basis basis = session.descriptorManager.getViewlet().getSelectedBasis();
      if (basis == null) {
        throw new Exception("no basis selected");
      }
      final JFileChooser fileChooser = new JFileChooser(session.cwd);
      fileChooser.setDialogTitle("Specify file name of basis to export");
      fileChooser.setFileFilter(FileFilters.basisXmlFileFilter);
      fileChooser.setAcceptAllFileFilterUsed(false);
      if (fileChooser.showOpenDialog(SplashWindow.getInstance()) == JFileChooser.APPROVE_OPTION) {
        session.descriptorManager.harden(basis);
        session.statusPanel.log2("Successfully exported basis: "
            + basis.getName());
      }
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot Export Basis because: \n", ex, "TODO");
    }
    finally {
      // nop
    }
  }

  private static final long serialVersionUID = 1L;
}
