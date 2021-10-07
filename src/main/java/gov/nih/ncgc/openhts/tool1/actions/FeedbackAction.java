/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;
import gov.nih.ncgc.openhts.tool1.Session;

public class FeedbackAction extends Tool1Action {
  private Session session;

  public FeedbackAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Feedback");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_F);
    putValue(Action.ACTION_COMMAND_KEY, "Feedback");
    putValue(SHORT_DESCRIPTION, "X");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    JOptionPane.showMessageDialog(session.frame, "Your experience with this tool is very valuable.\nPlease send an e-mail to jtalafous@gmail.com with your feedback.");
  }

  private static final long serialVersionUID = 1L;
}
