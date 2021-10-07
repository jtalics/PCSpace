/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.AboutDialog;
import gov.nih.ncgc.openhts.tool1.DialogUtilities;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.Version;

public class HelpAboutAction extends Tool1Action {
  private final Session session;
  private AboutDialog aboutDialog;

  public HelpAboutAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "About "+Version.PRODUCT_NAME_SHORT);
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
    putValue(Action.ACTION_COMMAND_KEY, "About");
    putValue(SHORT_DESCRIPTION, "Information about this app");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    if (aboutDialog == null) {
      aboutDialog = new AboutDialog(session.frame);
    }
    DialogUtilities.centerDialog(aboutDialog);
    aboutDialog.showAboutDialog(Version.PRODUCT_NAME +" version " + Version.ToString()
        + "\nCopyright 2006-8 Joseph Talafous"
        +"\nDistributed under the terms of the GNU General Public License v3");
    
  }

  private static final long serialVersionUID = 1L;
}
