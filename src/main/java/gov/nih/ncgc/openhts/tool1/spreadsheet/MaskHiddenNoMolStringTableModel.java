/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.spreadsheet;

import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;

public class MaskHiddenNoMolStringTableModel extends PointsetTableModel {
  private final Pointset pointset;
  private final Session session;

  MaskHiddenNoMolStringTableModel(final Session session, final Pointset pointset) {
    super(pointset);
    this.session = session;
    if (pointset == null){
      throw new RuntimeException("pointset is null");
    }
    this.pointset = pointset;
    colOffset = 1;
  }
  
  @Override
  public String getColumnName(final int col) {
    if (col == 0) {
      return PointsetTable.nameString;
    }
    return pointset.getCurrentAcMap().getColumnHeadAt(col - colOffset).getName();
  }

  @Override
  public String getColumnDescription(final int col) {
    if (col == 0) {
      return PointsetTable.nameString;
    }
    return pointset.getCurrentAcMap().getColumnHeadAt(col - colOffset).getDescription();
  }

  @Override
  public Object getValueAt(final int row, final int column) {
    int userRow = session.spreadsheet.getPointsetTable(pointset).tableToUser[row];
    Object object = null;
    switch(column) {
    case 0:
      object = new String(pointset.getObjNameAt(userRow).bytes);
      break;
    default:
      object = getDefaultValue(userRow,column - colOffset);
    }
    return object;
  }

  private static final long serialVersionUID = 1L;

}