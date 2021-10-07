package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;

public class HeatmapPanUpAction extends Tool1Action {
  private Session session;

  public HeatmapPanUpAction(final Session session) {
    super("Pan up",null);
    this.session = session;
    putValue(MNEMONIC_KEY, null/*KeyEvent.VK_X*/);
    putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_UP,0));
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "pan up");
    putValue(LONG_DESCRIPTION, "pan up");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      session.heatmap.panUp();
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot pan up because: \n",ex,"TODO");
    }
    finally {
      // nop
      
    }
  }

  private static final long serialVersionUID = 1L;
}
