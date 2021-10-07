package gov.nih.ncgc.openhts.tool1.actions;

// TEMPLATE FOR NEW ACTION

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.descriptorManager.BasisSpecifyDialog;

public class BasisCreateAction extends Tool1Action {
  private Session session;

  public BasisCreateAction(final Session session) {
    super("Create basis",null);
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    //putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_X, 0));
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "Create basis");
    putValue(LONG_DESCRIPTION, "Create basis");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      // TODO: make a BasisCreateDialog, use BasisSpecifyDialog temporarily
      BasisSpecifyDialog bcd = session.dialogManager.getBasisSpecifyDialog();
      bcd.showDialog();
      if (bcd.isOk()) {
        session.descriptorManager.registerBasis(bcd.getSelectedBasis());
      }
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot Create Basis because: \n",ex,"TODO");
    }
    finally {
      // nop
      
    }
  }

  private static final long serialVersionUID = 1L;
}
