// TODO: split this dialog into BASIS SELECT AND BASIS CREATE

package gov.nih.ncgc.openhts.tool1.descriptorManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ListSelectionModel;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.Tool1Exception;
import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;

public class BasisSpecifyDialog extends AbstractDialog {
  private Basis selectedBasis;
  final DescriptorSelectorViewlet descriptorSelectorViewlet;
  final BasisSelectorViewlet basisSelectorViewlet;
  final JRadioButton existingBasisButton;
  final JRadioButton newBasisButton;

  public BasisSpecifyDialog(final Session session) {
    super(session, "Select registered basis or create new basis from descriptors", true);
    this.session = session;
    setLayout(new BorderLayout());
    final JPanel northPanel = new JPanel(new BorderLayout());
    northPanel.setBorder(BorderFactory.createEtchedBorder());
    add(northPanel, BorderLayout.NORTH);
    final JPanel newOrExistingBasisPanel = new JPanel();
    northPanel.add(newOrExistingBasisPanel,BorderLayout.NORTH);
    newOrExistingBasisPanel.setLayout(new BoxLayout(newOrExistingBasisPanel, BoxLayout.Y_AXIS));
    basisSelectorViewlet = new BasisSelectorViewlet(session,
        ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    ButtonGroup buttonGroup = new ButtonGroup();
    existingBasisButton = new JRadioButton("Use existing basis");
    descriptorSelectorViewlet = new RegisteredDescriptorSelectorViewlet(session,
        ListSelectionModel.MULTIPLE_INTERVAL_SELECTION, "Registered descriptors" );
    descriptorSelectorViewlet.initialize();
    existingBasisButton.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(ActionEvent e) {
        if (existingBasisButton.isSelected()) {
          //northPanel.remove(newBasisPanel);
          remove(basisSelectorViewlet);
          remove(descriptorSelectorViewlet);
          add(basisSelectorViewlet,BorderLayout.CENTER);
          pack();
          repaint();
        }
      }
    });
    buttonGroup.add(existingBasisButton);
    newOrExistingBasisPanel.add(existingBasisButton);
    newBasisButton = new JRadioButton("Create new basis");
    newBasisButton.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(ActionEvent e) {
        if (newBasisButton.isSelected()) {
          remove(basisSelectorViewlet);
          remove(descriptorSelectorViewlet);
          add(descriptorSelectorViewlet,BorderLayout.CENTER);
          pack();
          repaint();
        }
      }
    });
    newOrExistingBasisPanel.add(newBasisButton);
    buttonGroup.add(newBasisButton);
    add(getButtonPanel(), BorderLayout.SOUTH);
    setPreferredSize(new Dimension(500, 500));
  }

  private Descriptor[] getSelectedDescriptors() {
    return descriptorSelectorViewlet.getSelectedDescriptors();
  }

  @Override
  protected boolean apply() {
    if (existingBasisButton.isSelected()) {
      selectedBasis = basisSelectorViewlet.getSelectedBasis();
      if (null == selectedBasis) {
        JOptionPane.showMessageDialog(this,
            "Select an existing basis or click Cancel.");
        return false;
      }
        return true;
    }

    if (newBasisButton.isSelected()) {
      if (getSelectedDescriptors().length < 2) {
        session.errorHelp(
            "Select at least two descriptors or click Cancel.","TODO");
        return false;
      }
      selectedBasis = new Basis(session, null);
      try {
        selectedBasis.addDescriptors(getSelectedDescriptors());
      }
      catch (Tool1Exception e) {
        session.errorSupport(
            "Cannot create basis because: ",e,"TODO");
        return false;
      }
      selectedBasis.initialize();
      session.descriptorManager.registerBasis(selectedBasis);
      BasisModifyDialog basisModifyDialog=new BasisModifyDialog(session);
      basisModifyDialog.setModal(true);
      basisModifyDialog.showDialog(); // modal block
      if (!basisModifyDialog.isOk()) {
        return false;
      }
      session.descriptorManager.registerBasis(selectedBasis);
      return true;
    }

    JOptionPane.showMessageDialog(this,"Choose an option or Cancel.");
    return false;
    
  }

  public Basis getSelectedBasis() {
    return selectedBasis;
  }

  private static final long serialVersionUID = 1L;
}
