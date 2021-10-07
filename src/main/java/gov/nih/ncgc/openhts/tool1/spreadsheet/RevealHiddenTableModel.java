package gov.nih.ncgc.openhts.tool1.spreadsheet;

import chemaxon.formats.MolFormatException;
import chemaxon.formats.MolImporter;
import chemaxon.struc.Molecule;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;

public class RevealHiddenTableModel extends PointsetTableModel {
  private final Pointset pointset;
  private final Session session;

  RevealHiddenTableModel(final Session session, final Pointset pointset) {
    super(pointset);
    this.session = session;
    this.pointset = pointset;
    colOffset = 3;
  }

  @Override
  public String getColumnName(final int col) {
    switch (col) {
    case 0:
      return PointsetTable.hiddenString;
    case 1:
      return PointsetTable.nameString;
    case 2:
      return PointsetTable.structureString;
    default:
      return pointset.getCurrentAcMap().getColumnHeadAt(col - colOffset).getName();
    }
  }

  @Override
  public String getColumnDescription(final int col) {
    switch (col) {
    case 0:
      return PointsetTable.hiddenString;
    case 1:
      return PointsetTable.nameString;
    case 2:
      return PointsetTable.structureString;
    default:
      String description = pointset.getCurrentAcMap().getColumnHeadAt(col - colOffset).getDescription();
      return description;
    }
  }

  @Override
  public Object getValueAt(final int row, final int column) {
    int userRow = session.spreadsheet.getPointsetTable(pointset).tableToUser[row];
    Object object = null;
    switch (column) {
    case 0:
      object = new Boolean(0 == pointset.hid[userRow]?"no":"yes");
      break;
    case 1:
      object = new String(pointset.getObjNameAt(userRow).bytes);
      break;
    case 2:
      String objDescription = null;
      objDescription = pointset.getObjDescriptionAt(userRow).toString();
      try {
        object = MolImporter.importMol(objDescription);
      }
      catch (final MolFormatException e) {
        //e.printStackTrace();
      }
      if (object == null) {
        return "err: "+objDescription;
      }
      break;
    default:
      object = getDefaultValue(row, column - colOffset);
    }
    return object;
  }

  @Override
  public void setValueAt(Object value, int row, int col) {
    switch (col) {
    case 0:
      // hid
      break;
    case 1:
      // name
      break;
    case 2:
      // jchem takes care of itself
      break;
    default:
      // The value entered by the user is in user space.
// TODO: what if columnhead.kind is a string?
      pointset.setPointValueUser(Float.parseFloat(((String) value)), row,
          col - 3);
    }
    fireTableCellUpdated(row, col);
  }

  @Override
  public Class getColumnClass(final int c) {
    switch (c) {
    case 2:
      return Molecule.class;
    default:
      return String.class;
    }
  }

  // In case of double clicking with the mouse's left button this selectedFunction is
  // called. If returns true the cell editor (a MarvinSketch) opens the
  // cell, else nothing happen.
  @Override
  public boolean isCellEditable(final int row, final int col) {
    return true;
  }

  private static final long serialVersionUID = 1L;
}
