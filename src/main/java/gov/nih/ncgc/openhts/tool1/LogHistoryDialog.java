package gov.nih.ncgc.openhts.tool1;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class LogHistoryDialog extends AbstractDialog {

  public LogHistoryDialog(Session session) {
    super(session);
    setModal(true);
    setTitle("Log history");
    JTextArea textArea= new JTextArea();
    add(new JScrollPane(textArea));
    textArea.setEditable(true);
    for (String string : session.statusPanel.getLog2Deque()) {
      textArea.append(string);
      textArea.append("\n");
    }
    add(getButtonPanel(), BorderLayout.SOUTH);
  }

  @Override
  public boolean apply() {
    return true;
  }
  
}
