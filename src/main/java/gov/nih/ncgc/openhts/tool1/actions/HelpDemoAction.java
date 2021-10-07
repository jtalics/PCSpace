package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.AWTException;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.demo.DemoRobot;

public class HelpDemoAction extends Tool1Action {
  private Session session;

  public HelpDemoAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Demos");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);
    putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F12, InputEvent.SHIFT_DOWN_MASK));
    putValue(Action.ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "demos");
    putValue(LONG_DESCRIPTION, "demos");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String) getValue(Action.NAME));
    try {
      new DemoRobot(session);
      session.saved = false;
    }
    catch (final AWTException ex) {
      // TODO Auto-generated catch block
      ex.printStackTrace();
    }
    finally {
      session.setWaitCursor(false);
    }
  }

  private static final long serialVersionUID = 1L;
}
