package gov.nih.ncgc.openhts.tool1.spreadsheet;

import javax.swing.table.DefaultTableModel;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public abstract class PointsetTableModel extends DefaultTableModel {
  private final Pointset pointset;
  protected int colOffset;
  protected int dim;

  public PointsetTableModel(Pointset pointset) {
    this.pointset = pointset;
  }

  @Override
  public int getRowCount() {
    if (pointset == null || pointset.isLoading()) {
      return 0;
    }
    return pointset.getPointCount();
  }

  @Override
  public final int getColumnCount() {
    return pointset.getDimensionality() + colOffset;
  }

  public boolean isColumnInSubjectSpace(int column) {
    int pointsetCol = column - colOffset;
    if (pointsetCol >= 0) {
      return pointset.isColumnHeadInSubjectSpace(pointsetCol);
    }
    return false;
  }
  
  protected Object getDefaultValue(int r, int c) {
    Object object = pointset.getPointValueUser(r, c);
    if (object instanceof Float) {
      object = PointsetTable.df.format(object);
    }
    return object;
  }
  
  public abstract String getColumnDescription(int realIndex);

  private static final long serialVersionUID = 1L;
}
