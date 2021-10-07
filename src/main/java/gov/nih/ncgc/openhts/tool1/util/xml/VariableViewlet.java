package gov.nih.ncgc.openhts.tool1.util.xml;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import gov.nih.ncgc.openhts.tool1.AppLafManager;

/**
 * Presents the name and value of a variable to the user.
 */
class VariableViewlet extends JPanel {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  Variable variable;
  ActionListener actionListener;
  JComponent component; // JTextField or JComboBox
  boolean editable=true; // by default
  String description="no description"; 

  /**
   * @param variable
   * @param actionListener
   */
  VariableViewlet(final Variable variable, final ActionListener actionListener) {
    super();
    this.variable = variable;
    this.actionListener = actionListener;
  }

  /**
   * @param variable
   * @param editable
   * @param description
   * @param actionListener
   */
  VariableViewlet(final Variable variable, final boolean editable,
      final String description, final ActionListener actionListener) {

    super();
    this.editable = editable;
    this.description = description;
    this.variable = variable;
    this.actionListener = actionListener;
  }

  VariableViewlet build() {
    removeAll();
    setLayout(new BorderLayout());
    final JLabel displayName = new JLabel(variable.getName()+"!!: ");
    displayName.setIcon(AppLafManager.getIcon(AppLafManager.IconKind.ICON_LOCKED));
    //displayName.setFont(GuiFrame.PRIORITY_FONT);
    displayName.setToolTipText(description);
    add(displayName, BorderLayout.WEST);
    switch (variable.getKind()) {
    case ComboBox:
      component = new JComboBox(variable.getOptions());
      for (int i = 0; i < variable.getOptions().length; i++) {
        if (variable.getValue().equals(variable.getOptions()[i])) {
          ((JComboBox)component).setSelectedIndex(i);
        }
      }
      ((JComboBox)component).addActionListener(actionListener);
      ((JComboBox)component).setEditable(editable);
      break;
    case TextField:
      component = new JTextField(variable.getValue(), 25);
      if (actionListener != null) {
        ((JTextField)component).addActionListener(actionListener);
      }
      ((JTextField)component).setEditable(editable);
      break;
    default: 
      throw new RuntimeException("unrecognized kind: " + variable.getKind());
    }
    // component.setFont(GuiFrame.FONT);
    component.setBackground(Color.WHITE);
    add(component, BorderLayout.CENTER);
    setAlignmentX(SwingConstants.LEFT);
    return this;
  }

   JTextField getTextField() {
    return (JTextField)component; // TODO
  }
}
// end of file
