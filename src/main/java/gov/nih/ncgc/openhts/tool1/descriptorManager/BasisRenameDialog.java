/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.descriptorManager;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;

/** 
 * Ask the user the new name of a Basis
 * @author talafousj
 *
 */
public final class BasisRenameDialog extends AbstractDialog {

  Basis basis;
  private JLabel label = new JLabel("setBasis() never called");
  private JTextField newNameTextField;
  
  public BasisRenameDialog(final Session session)  {
    super(session);
    setTitle("Rename basis");
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
    basis.setName(newNameTextField.getText());

    return true;
  }


  public void setBasis(Basis basis) {
    this.basis = basis;
    label.setText("Current pointset name: "+basis.getName());
  }
  private static final long serialVersionUID = 1L;
}
