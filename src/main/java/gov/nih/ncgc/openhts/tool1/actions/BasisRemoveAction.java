package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Basis;

public class BasisRemoveAction extends Tool1Action {
  private Session session;

  public BasisRemoveAction(final Session session) {
    super("Remove",null);
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_D);
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "remove basis");
    putValue(LONG_DESCRIPTION, "remove basis");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      final Basis[] bases = session.descriptorManager.getSelectedBases();
      if (bases == null) {
        session.errorHelp("Select at least one basis.","TODO");
        return;
      }
      for (Basis basis : bases) {
        session.descriptorManager.removeBasis(basis);
      }}
    
      catch (final Throwable ex) {
        session.errorSupport("Cannot remove basis because:", ex, "todo");
      }
      finally {
        // nop
      }
  }

  private static final long serialVersionUID = 1L;
}
