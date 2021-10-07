package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;

public class HeatmapPanRightAction extends Tool1Action {
  private Session session;

  public HeatmapPanRightAction(final Session session) {
    super("Pan right",null);
    this.session = session;
    putValue(MNEMONIC_KEY, null/*KeyEvent.VK_X*/);
    putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,0));
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "pan right");
    putValue(LONG_DESCRIPTION, "pan right");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      session.heatmap.panRight();
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot pan right because: \n",ex,"TODO");
    }
    finally {
      // nop
      
    }
  }

  private static final long serialVersionUID = 1L;
}
