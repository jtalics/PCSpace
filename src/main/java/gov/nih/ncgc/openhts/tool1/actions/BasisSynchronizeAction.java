
package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;

public class BasisSynchronizeAction extends Tool1Action {
  private Session session;

  public BasisSynchronizeAction(final Session session) {
    super("Synchronize",null);
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_S);
    //putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_X, 0));
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "Synchronize bases");
    putValue(LONG_DESCRIPTION, "Synchronize bases");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      session.descriptorManager.synchronizeBases();
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot Synchronize descriptors because: \n",ex,"TODO");
    }
    finally {
      // nop
    }
  }

  private static final long serialVersionUID = 1L;
}
