package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;
import gov.nih.ncgc.openhts.tool1.Session;

public class AxesDeleteAction extends Tool1Action {
  private final Session session;

  public AxesDeleteAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Axes; Delete");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);
    putValue(Action.ACTION_COMMAND_KEY, "Axes: Delete");
    putValue(SHORT_DESCRIPTION, "discard axes");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String) getValue(Action.NAME));
    try {
      if (session.plotManager.subject.axes == null) {
        throw new Exception("there are no axes to delete.");
      }
      final int i = JOptionPane.showConfirmDialog(session.frame, "Delete axes?",
          "Confirm delete axes", JOptionPane.YES_NO_OPTION);
      if (i == 0) { // yes
        session.plotManager.deleteAxes();
      }
    }
    catch (final Throwable ex) {
      session
          .errorSupport("Cannot delete axes because:", ex, "delete_axes_menu_error");
    }
    finally {
      // nop
    }
  }

  private static final long serialVersionUID = 1L;
}
