/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.viewers;

import javax.help.CSH;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import gov.nih.ncgc.openhts.tool1.Session;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class SelectionReporterToolBar extends JToolBar {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  SelectionReporterToolBar(final Session session) {
    setOrientation(SwingConstants.HORIZONTAL);
    setToolTipText("Selection reporter toolbar can be moved");
    CSH.setHelpIDString(this, "toolbar_tables_data");
    setBorder(BorderFactory.createRaisedBevelBorder());
    setFloatable(true);
    final JButton[] buttons = new JButton[2];
    //
    buttons[0] = new JButton(
        session.actionManager.selectionReporterCreateReportAction);
    CSH.setHelpIDString(buttons[0], "todo");
    //
    buttons[1] = new JButton(
        session.actionManager.selectionReporterCreateDataSourceAction);
    CSH.setHelpIDString(buttons[1], "todo");
    for (final JButton element : buttons) {
      element.setText("");
      add(element);
    }
  }
}
