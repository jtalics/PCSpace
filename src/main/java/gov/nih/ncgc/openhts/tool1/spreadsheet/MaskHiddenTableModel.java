package gov.nih.ncgc.openhts.tool1.spreadsheet;

import chemaxon.formats.MolFormatException;
import chemaxon.formats.MolImporter;
import chemaxon.struc.Molecule;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class MaskHiddenTableModel extends PointsetTableModel {
  private final Pointset pointset;
  private final Session session;

  MaskHiddenTableModel(final Session session, final Pointset pointset) {
    super(pointset);
    this.session = session;
    if (pointset == null) {
      throw new RuntimeException("pointset is null");
    }
    this.pointset = pointset;
    colOffset = 2;
  }

  @Override
  public final int getRowCount() {
    if (pointset == null || pointset.isLoading()) {
      return 0;
    }
    return pointset.getNVisible();
  }

  @Override
  public String getColumnName(final int col) {
    if (col == 0) {
      return PointsetTable.nameString;
    }
    else if (col == 1) {
      return PointsetTable.structureString;
    }
    else {
      return pointset.getCurrentAcMap().getColumnHeadAt(col - colOffset)
          .getName();
    }
  }

  @Override
  public String getColumnDescription(final int col) {
    if (col == 0) {
      return PointsetTable.nameString;
    }
    else if (col == 1) {
      return PointsetTable.structureString;
    }
    else {
      return pointset.getCurrentAcMap().getColumnHeadAt(col - colOffset)
          .getDescription();
    }
  }

  @Override
  public Object getValueAt(final int row, final int column) {
    int userRow = session.spreadsheet.getPointsetTable(pointset).tableToUser[row];
    Object object = null;
    switch(column) {
    case 0:
      object = pointset.getObjNameAt(userRow).toString();
      break;
    case 1:
      String objDescription = null;
        objDescription = pointset.getObjDescriptionAt(userRow).toString();
      try {
        object = MolImporter.importMol(objDescription);
      }
      catch (final MolFormatException e) {
        // todo
      }
      if (object == null) {
        object = "err: "+objDescription;
      }
      break;
    default:
      object = getDefaultValue(row, column - colOffset);
    }
    return object;
  }

  @Override
  public Class getColumnClass(final int c) {
    if (c == 1) {
      return Molecule.class;
    }
    return getValueAt(0, c).getClass();
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
