package gov.nih.ncgc.openhts.tool1.spreadsheet;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import chemaxon.marvin.beans.MViewPane;
import chemaxon.struc.Molecule;

/**
 * The cell renderer class
 */
class PointsetTableCellRenderer extends DefaultTableCellRenderer {
  MViewPane mvp;
  Border deselectedBorder = null;
  Border selectedBorder = null;
  boolean isBordered = true;
  boolean init = true;

  public PointsetTableCellRenderer(final boolean isBordered) {
    mvp = new MViewPane();
    this.isBordered = isBordered;
  }

  /**
   * Gets the renderer component, creates borders
   */
  @Override
  public Component getTableCellRendererComponent(final JTable table, final Object mol,
      final boolean isSelected, final boolean hasFocus, final int row, final int column) {
    if (mol instanceof Molecule) {
      mvp.setM(0, (Molecule) mol);
      if (isBordered) {
        if (isSelected) {
          if (selectedBorder == null) {
            selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                table.getSelectionBackground());
          }
          mvp.setBorder(selectedBorder);
        }
        else {
          if (deselectedBorder == null) {
            deselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
                table.getBackground());
          }
          mvp.setBorder(deselectedBorder);
        }
      }
      return mvp;
    }
    setValue(mol.toString());
    return null;
    // setM(0,(Molecule)null);
    // System.out.println((String)mol);
    // javax.swing.JTextField ta = new javax.swing.JTextField((String) mol);
    // ta.setLineWrap(true);
    // JScrollPane sb = new JScrollPane(ta);
    // return ta;
  }
  private static final long serialVersionUID = 1L;
}