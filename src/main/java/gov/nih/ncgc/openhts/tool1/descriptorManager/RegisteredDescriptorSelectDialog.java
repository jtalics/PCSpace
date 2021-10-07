package gov.nih.ncgc.openhts.tool1.descriptorManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;

public final class RegisteredDescriptorSelectDialog extends AbstractDialog {

  RegisteredDescriptorSelectorViewlet rdsv;
  
  public RegisteredDescriptorSelectDialog(final Session session) {
    super(session,"Select molecular descriptors and activities to load",true);

    final JPanel panel = new JPanel(new BorderLayout());
    add(panel);
    rdsv = new RegisteredDescriptorSelectorViewlet(
        session, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION, "Select a registered descriptor");
    rdsv.initialize();

    panel.add(rdsv,BorderLayout.CENTER);
    add(getButtonPanel(), BorderLayout.SOUTH);
    setPreferredSize(new Dimension(500,500));
  }

  public Descriptor[] getSelectedDescriptors() {
    return rdsv.getSelectedDescriptors();
  }

  @Override
  protected void doApply() {
    if (getSelectedDescriptors().length <= 0) {
      JOptionPane.showMessageDialog(this, "Select a Molecular Descriptor or click Cancel.");
      return;
    }
    super.doOk();
  }
  private static final long serialVersionUID = 1L;
}
