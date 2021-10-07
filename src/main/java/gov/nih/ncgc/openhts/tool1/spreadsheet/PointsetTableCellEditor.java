package gov.nih.ncgc.openhts.tool1.spreadsheet;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import chemaxon.marvin.beans.MViewPane;
import chemaxon.struc.Molecule;

/** Purpose is to ...
 * @author talafousj
 *
 */
class PointsetTableCellEditor extends DefaultCellEditor {
  // The current molecule of the MViewPane where MViewPane is the
  // cell editor
  Molecule currentMol = null;
  static JCheckBox checkBox = new JCheckBox();

  public PointsetTableCellEditor(final MViewPane mvp) {
    super(checkBox);
    checkBox.setToolTipText("checkBox");
    //CSH.setHelpIDString(checkBox, "moleditor");
    editorComponent = mvp;
  }

  @Override
  protected void fireEditingStopped() {
    super.fireEditingStopped();
  }

  // Gets the current molecule
  @Override
  public Object getCellEditorValue() {
    currentMol = ((MViewPane) editorComponent).getM(0);
    return currentMol;
  }

  // Gets the current editor
  @Override
  public Component getTableCellEditorComponent(final JTable table, final Object obj,
      final boolean isSelected, final int row, final int column) {
//System.out.println("getTableCellEditorComponent");
    if (obj instanceof Molecule) {
      currentMol = (Molecule) obj;
      ((MViewPane) editorComponent).setM(0, currentMol);
      return editorComponent;
    }
    else {
      return null;
    }
  }
  private static final long serialVersionUID = 1L;
}