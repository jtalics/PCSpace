/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;

public class SubjectOriginForwardAction extends Tool1Action {
  private Session session;

  public SubjectOriginForwardAction(final Session session) {
    super("move subject origin forward", null);
    this.session = session;
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "move subject origin forward");
    putValue(LONG_DESCRIPTION, "move subject origin forward");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      System.out.println("(move subject origin forward)");
      session.plotManager.subject.moveOriginForward();
      session.plotManager.canvas.refresh();
    }
    catch (final Throwable ex) {
      session.errorSupport("", ex, "move_view_key_error");
    }    
  }

  private static final long serialVersionUID = 1L;
}
/*
rootPane.registerKeyboardAction(new ActionListener() {
  public void actionPerformed(ActionEvent actionEvent) {
  }
}, KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0),
    JComponent.WHEN_IN_FOCUSED_WINDOW);
*/
