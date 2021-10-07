package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;

public class HeatmapZoomAction extends Tool1Action {
  private Session session;

  public HeatmapZoomAction(final Session session) {
    super("Zoom",null);
    this.session = session;
    putValue(MNEMONIC_KEY, null/*KeyEvent.VK_X*/);
    putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.SHIFT_MASK));
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "zoom");
    putValue(LONG_DESCRIPTION, "magnify");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      System.out.println("Zoom!");
      session.heatmap.zoom();
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot zoom heatmap because: \n",ex,"TODO");
    }
    finally {
      // nop
      
    }
  }

  private static final long serialVersionUID = 1L;
}
