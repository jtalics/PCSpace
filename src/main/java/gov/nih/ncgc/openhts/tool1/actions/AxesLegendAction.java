package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.plotManager.AxesLegendDialog;

public class AxesLegendAction extends Tool1Action {
  private Session session;

  public AxesLegendAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Axes: Legend");
    putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_L));
    putValue(Action.ACTION_COMMAND_KEY, "Axes: Legend");
    putValue(SHORT_DESCRIPTION, "display labels assigned to axes");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {
        if (session.plotManager.subject.axes == null) {
          session.errorHelp("there are no axes for displaying legend.","TODO");
        }
        final AxesLegendDialog axesLegendDialog = session.dialogManager.getAxesLegendDialog();
        axesLegendDialog.showDialog();
      }
      catch (final Throwable ex) {
        session.errorSupport("Cannot display legend of axes because:",ex,
            "legend_axes_menu_error");
      }
      finally {
        // nop
      }
  }

  private static final long serialVersionUID = 1L;
}
