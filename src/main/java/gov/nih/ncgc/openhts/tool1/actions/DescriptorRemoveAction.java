package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Descriptor;

public class DescriptorRemoveAction extends Tool1Action {
  private Session session;

  public DescriptorRemoveAction(final Session session) {
    super("Remove",null);
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_D);
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "remove descriptor");
    putValue(LONG_DESCRIPTION, "remove descriptor");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {
        final Descriptor[] descriptors = session.descriptorManager.getSelectedDescriptors();
        if (descriptors == null) {
          session.errorHelp("Select one or more descriptors.","TODO");
          return;
        }
        for (Descriptor descriptor : descriptors) {
          session.descriptorManager.removeDescriptor(descriptor);
        }
      }
      catch (final Throwable ex) {
        session.errorSupport("Cannot remove descriptor because:", ex, "TODO");
      }
      finally {
        // nop
      }
  }

  private static final long serialVersionUID = 1L;
}
