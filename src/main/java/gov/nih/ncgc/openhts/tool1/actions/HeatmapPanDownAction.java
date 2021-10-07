package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;

public class HeatmapPanDownAction extends Tool1Action {
  private Session session;

  public HeatmapPanDownAction(final Session session) {
    super("Pan down",null);
    this.session = session;
    putValue(MNEMONIC_KEY, null/*KeyEvent.VK_X*/);
    putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0));
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "pan down");
    putValue(LONG_DESCRIPTION, "pan down");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      System.out.println("Pan down!");
      session.heatmap.panDown();
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot pan down because: \n",ex,"TODO");
    }
    finally {
      // nop
      
    }
  }

  private static final long serialVersionUID = 1L;
}
