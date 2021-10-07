package gov.nih.ncgc.openhts.tool1.actions;

// TEMPLATE FOR NEW ACTION

import java.awt.event.ActionEvent;
import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;

public class SelectionViewerAnimationAction extends Tool1Action {
  private Session session;

  public SelectionViewerAnimationAction(final Session session) {
    super("Animation/Simultaneous",null);
    this.session = session;
    //putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    //putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_X, 0));
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "toggle animation");
    putValue(LONG_DESCRIPTION, "toggle animation");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      session.selectionViewer.toggleAnimation();
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot toggle anumation because: \n",ex,"TODO");
    }
    finally {
      // nop
      
    }
  }

  private static final long serialVersionUID = 1L;
}
