package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;

public class DescriptorSynchronizeAction extends Tool1Action {
  private Session session;

  public DescriptorSynchronizeAction(final Session session) {
    super("Synchronize",null);
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_S);
    //putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_X, 0));
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "Synchronize descriptors");
    putValue(LONG_DESCRIPTION, "Synchronize descriptors");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      session.descriptorManager.synchronizeDescriptors();
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
