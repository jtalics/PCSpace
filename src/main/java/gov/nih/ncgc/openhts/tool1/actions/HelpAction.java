/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.Version;

public class HelpAction extends Tool1Action {
  private final Session session;
  
  public HelpAction(final Session session) {
    super(Version.PRODUCT_NAME_SHORT + " Help (F1)", null);
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_H);
    putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "Help");
    putValue(SHORT_DESCRIPTION, "Main Help");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    session.getHelpBroker().setDisplayed(true);
    session.getHelpBroker().setCurrentID("mapkey.top");
  }

  private static final long serialVersionUID = 1L;
}
