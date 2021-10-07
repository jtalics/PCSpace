package gov.nih.ncgc.openhts.tool1.pointsetManager;

import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.persistence.Persistent;

/**
 * Purpose is to record a mapping between the axes being plotted (X,Y, and/or Z)
 * and the columns in a Pointset with the following restriction: a single column
 * will map to a single axis AND a single axis will map to a single column. It is
 * not necessary that all axes or columns are mapped.
 * 
 * @author talafousj
 */
public class AxesColumnHeadsMapping implements Persistent {
  // Maps coordinate (x, y, z) to column/descriptor
  private int[] axisToColumnHead;
  private int[] columnHeadToAxis;
  private ColumnHead[] columnHeads;
  private transient Session session;
  public static final int UNMAPPED = -1;
  int dimensionality;
  private int stringKindCount;
  private int numberKindCount;

  public AxesColumnHeadsMapping(Session session) {
    this.session = session;
  }

  public void initialize(int dim) {
    this.dimensionality = dim;
    columnHeads = new ColumnHead[dim];
    columnHeadToAxis = new int[dim];
    axisToColumnHead = new int[] { UNMAPPED, UNMAPPED, UNMAPPED };
    for (int i = 0; i < session.plotManager.getDim(); i++) {
      axisToColumnHead[i] = UNMAPPED;
    }
    for (int i = 0; i < dim; i++) {
      columnHeadToAxis[i] = UNMAPPED;
    }
    // for (int i = 0; i < session.plotManager.getDim(); i++) {
    // columnHeadToAxis[i] = i;
    // }
    initializeTransient();
  }

  // @Implements Persistent
  @Override
	public void initializeTransient() {
    // nop
  }

  // @Implements Persistent
  @Override
	public void setSession(Session session) {
    this.session = session;
  }

  public int getColumnHeadIndexForAxis(int axis) {
    return axisToColumnHead[axis];
  }

  public ColumnHead getColumnHeadForAxis(int axis) {
    return columnHeads[axisToColumnHead[axis]];
  }

  public int getAxisForColumnHeadIndex(final int columnHeadIndex) {
    return columnHeadToAxis[columnHeadIndex];
  }

  public int getAxisForColumnHead(final ColumnHead columnHead) {
    return columnHeadToAxis[getColumnHeadIndexFor(columnHead)];
  }

  public void addMapping(final int axis, final ColumnHead columnHead) {
    addMapping(axis,getColumnHeadIndexFor(columnHead));
  }
  
  public void addMapping(final int axis, final int columnHeadIndex) {
    // Discard the previous mapping
    if (axisToColumnHead[axis] != UNMAPPED) {
      throw new RuntimeException("Axis already mapped.");
    }
    if (columnHeadToAxis[columnHeadIndex] != UNMAPPED) {
      throw new RuntimeException("mapping already exists at col index = "
          + columnHeadIndex);
    }
    if (axisToColumnHead[axis] != UNMAPPED) {
      throw new RuntimeException("mapping already exists for axis " + axis);
    }
    columnHeadToAxis[columnHeadIndex] = axis;
    axisToColumnHead[axis] = columnHeadIndex;
  }

  public void removeMappingFromAxis(final int axis) {
    int columnHeadIndex = axisToColumnHead[axis];
    if (columnHeadIndex == UNMAPPED
        || columnHeadToAxis[columnHeadIndex] == UNMAPPED) {
         throw new RuntimeException("already unmapped for axis " + axis);
    }
    else {
      columnHeadToAxis[columnHeadIndex] = UNMAPPED;
    }
    axisToColumnHead[axis] = UNMAPPED;
  }

  public void removeMappingFromColumnHead(final int descriptorIndex) {
    int axis = columnHeadToAxis[descriptorIndex];
    if (axis == UNMAPPED || axisToColumnHead[axis] == UNMAPPED) {
       throw new RuntimeException("already unmapped for col = " +
       descriptorIndex);
    }
    else {
      axisToColumnHead[axis] = UNMAPPED;
    }
    columnHeadToAxis[descriptorIndex] = UNMAPPED;
  }

  @Override
  public String toString() {
    String s = "[" + this.getClass().getSimpleName() + ": ";
    s += "axisToCol = {";
    for (int i : axisToColumnHead) {
      s += i + " ";
    }
    s += "}; colToAxis = {";
    for (int i : columnHeadToAxis) {
      s += i + " ";
    }
    s += "}";
    s += "]";
    return s;
  }

  public ColumnHead getColumnHeadAt(int i) {
    return columnHeads[i];
  }

  public void setColumnHeadAt(ColumnHead columnHead, int i) {
    if (columnHead == null) {
      throw new RuntimeException("columnHead cannot be null");
    }
    columnHeads[i] = columnHead;
    switch(columnHead.kind) {
    case String:
      stringKindCount++;
      break;
    case NumberFloat:
      numberKindCount++;
      break;
    case NumberInt:
      throw new RuntimeException("bad kind");
    }
  }

  public ColumnHead[] getColumnHeads() {
    return columnHeads;
  }

  public int getColumnHeadIndexFor(ColumnHead columnHead) {
    for (int i = 0; i < columnHeads.length; i++) {
      if (columnHeads[i] == columnHead) {
        return i;
      }
    }
    return -1;
  }

  public boolean containsColumnHead(ColumnHead columnHead) {
    for (ColumnHead ch : columnHeads) {
      if (columnHead == ch) {
        return true;
      }
    }
    return false;
  }

  public int getDimensionality() {
    return dimensionality;
  }

  public int getNumberKindCount() {
    return numberKindCount;
  }

  public int getStringKindCount() {
    return stringKindCount;
  }
}
