package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;

public class PlotterBasisModeAction extends Tool1Action {
  private Session session;

  public PlotterBasisModeAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Basis mode");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_B);
    putValue(Action.ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "basis mode toggle");
    putValue(LONG_DESCRIPTION, "basis mode toggle");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String) getValue(Action.NAME));
    try {
      session.setWaitCursor(true);
      session.plotManager.toggleBasisMode();
      // final JCheckBox checkBox = new JCheckBox(
      // "All data sources are in the same chemistry space");
      // checkBox
      // .setToolTipText("if checked, all data sources must have the same
      // representation");
      // CSH.setHelpIDString(checkBox, "chemspace_legend_axes_checkbox");
      // checkBox.setSelected(Axes.pointsetsAreInSameSpace);
      // checkBox.addActionListener(new ActionListener() {
      // public void actionPerformed(final ActionEvent e) {
      // try {
      // if (Axes.pointsetsAreInSameSpace == true) {
      // Axes.pointsetsAreInSameSpace = false;
      // rebuildLegend();
      // }
      // else {
      // // if (!Pointset.checkBasis(session)) {
      // // session.error(
      // // "All data sources are not in the same chemistry space",
      // // "mapping_error");
      // // return;
      // // }
      // if (!Pointset.checkAxisColMapping(session)) {
      // session.error(
      // "All data sources do not have same axis-to-col mapping.",
      // "mapping_error");
      // return;
      // }
      // Axes.pointsetsAreInSameSpace = true;
      // rebuildLegend();
      // }
      // session.plotter.subject.axes.buildLabelsAndTics(); // axis labels may
      // // have changed
      // session.plotter.canvas.setStale(true);
      // session.plotter.canvas.refresh();
      // }
      // catch (final Throwable ex) {
      // session.error(
      // "While changing same chemistry space constraint: " + ex,
      // "chemspace_legend_axes_checkbox_error");
      // }
      // }
      // });
      // topPanel.add(checkBox, BorderLayout.NORTH);
      session.updateEnablement();
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot do basis mode toggle because:", ex, "todo");
    }
    finally {
      session.setWaitCursor(false);
    }
  }

  private static final long serialVersionUID = 1L;
}
