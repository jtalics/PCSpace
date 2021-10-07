package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Basis;

public class BasisSetPlotterModeAction extends Tool1Action {
  private Session session;

  public BasisSetPlotterModeAction(final Session session) {
    super("View",null);
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_D);
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "view");
    putValue(LONG_DESCRIPTION, "view");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      final Basis[] bases = session.descriptorManager.getSelectedBases();
      if (bases == null || bases.length > 1) {
        session.errorHelp("Select one basis only.", "TODO");
        return;
      }
      for (Basis basis : bases) {
        session.plotManager.setBasisMode(basis);
      }}
    
      catch (final Throwable ex) {
        session.errorSupport("Cannot toggle basis because:", ex, "todo");
      }
      finally {
        // nop
      }
  }

  private static final long serialVersionUID = 1L;
}
