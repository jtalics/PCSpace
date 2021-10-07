package gov.nih.ncgc.openhts.tool1.descriptorManager;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import gov.nih.ncgc.openhts.tool1.Session;

public class BasisListCellRenderer extends JLabel implements ListCellRenderer {
  private final Session session;

  public BasisListCellRenderer(final Session session) {
    this.session = session;
    // Don't paint behind the component
    setOpaque(true);
  }

  @Override
	public Component getListCellRendererComponent(final JList list,
      final Object value, final int index, boolean isSelected,
      final boolean cellHasFocus) {
//    Color theColor = null;
    String theText = null;
    if (value instanceof Basis) {
      Basis basis = (Basis) value;
      theText = basis.toString();
    }
    else if (value instanceof String) {
      throw new RuntimeException("DELETEME");
    }
    if (!isSelected) {
      setBorder(BorderFactory.createLineBorder(list.getBackground(), 2));
    }
    else {
      setBorder(BorderFactory.createLineBorder(Color.blue, 2));
    }
    setText(theText);
//    setBackground(theColor);
//    setForeground(ColorChooserModifyDialog.getComplement(theColor));
    return this;
  }

  private static final long serialVersionUID = 1L;
}
