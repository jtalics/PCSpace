/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.pointsetManager;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;

/** 
 * Purpose is to ask the user the new name of a Pointset
 * @author talafousj
 *
 */
public final class PointsetRenameDialog extends AbstractDialog {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  Pointset pointset;
  private JLabel label = new JLabel("setPointset() never called");
  private JTextField newNameTextField;
  
  public PointsetRenameDialog(final Session session)  {
    super(session);
    setTitle("Rename pointset");
    setModal(true);
    add(label, BorderLayout.NORTH);
    final JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.add(new JLabel("New name: "), BorderLayout.WEST);
    centerPanel.add(newNameTextField = new JTextField());
    add(centerPanel, BorderLayout.CENTER);
    add(getButtonPanel(), BorderLayout.SOUTH);
  }
  
  
  @Override
  public boolean apply() {
    if (!session.pointsetManager.uniqueName(newNameTextField.getText())) {
      throw new RuntimeException("non-unique name: " + newNameTextField.getText());
    }
    pointset.setName(newNameTextField.getText());

    return true;
  }


  public void setPointset(Pointset pointset) {
    this.pointset = pointset;
    label.setText("Current pointset name: "+pointset.getName());
  }
}
