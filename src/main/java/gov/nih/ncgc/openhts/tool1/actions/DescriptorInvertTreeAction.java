package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;

public class DescriptorInvertTreeAction extends Tool1Action {
  private Session session;

  public DescriptorInvertTreeAction(final Session session) {
    super("Invert tree",null);
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_I);
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "invert tree");
    putValue(LONG_DESCRIPTION, "invert tree");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {
        session.descriptorManager.invert();    
      }
      catch (final Throwable ex) {
        session.errorSupport("Cannot invert tree  because:", ex, "todo");
      }
      finally {
        // nop
      }
  }

  private static final long serialVersionUID = 1L;
}
