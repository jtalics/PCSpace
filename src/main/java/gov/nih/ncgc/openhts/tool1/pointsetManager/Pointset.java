package gov.nih.ncgc.openhts.tool1.pointsetManager;

import java.awt.Color;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.swing.event.ChangeListener;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.StringAndNumberFloat;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Basis;
import gov.nih.ncgc.openhts.tool1.persistence.Persistent;
import gov.nih.ncgc.openhts.tool1.plotManager.Axes;
import gov.nih.ncgc.openhts.tool1.plotManager.Drawable;
import gov.nih.ncgc.openhts.tool1.plotManager.Slate;
import gov.nih.ncgc.openhts.tool1.spreadsheet.PointsetTable;

/**
 * Purpose is to ... For performance reasons, there is a concept of "User Space"
 * and "Subject Space". Given the form of the graphics math, the translation of
 * subject from the user's coordinate frame to the canvas can be saved by adding
 * the translation vector once and saving it. While this saves CPU (do not need
 * to do all those additions for each view transformation), and save memory (do
 * not need to have twice the memory, one for each space), this does require a
 * more involved management, which goes as follows: All user entities are saved
 * on disk in User Space. When they are read into memory, all entities but
 * Pointsets are moved to Subject Space. Pointsets cannot be saved purely in
 * Subject Space because the View transformation is only 2D or 3D and Pointsets
 * are nD. Therefore, we must move only the columns to Subject space in Pointset
 * that are being viewed and move them back to User Space when they are NOT
 * being viewed. Pointsets are created using a Basis to name and describe th *
 * columns of the numbers, but afterward, a pointset may or may not have a
 * Basis. The Plotter has two modes: Basis or No Basis. In Basis mode, a Basis
 * is selected and only Pointsets having that Basis are visible. In Basis mode,
 * the Axes are labelled with the descriptors that are currently mapped, as
 * defined by the Basis, and the mappings defined by the Pointset are ignored,
 * the Basis takes control. In NoBasis mode, the Axes are labelled simply with
 * X,Y, and/or Z. In NoBasis mode, the Basis for the Pointset,if one exists, is
 * simply disregarded and each Pointset decides which column to map to each
 * axis, as specified by the user. The PointsetManager is responsible for moving
 * the appropriate columns between user and subject space when the Plotter
 * changes basis mode. If the Plotter is in Basis mode, the Plotter applies the
 * column-descriptor mapping found in the Basis mode to Pointset. If it is in No
 * Basis mode, the Plotter applies the mapping found in the Pointset to the
 * Pointset.
 * 
 * @author talafousj
 */
public final class Pointset extends Drawable implements PointsetManagerEntity,
    Comparable<Pointset>, Persistent {
  /**
   * Sorts the numbers based on hid[] as a key.
   * 
   * @author talafousj
   */
  public class PointSorter {
    private void sort() {
      // This moves the elements so that the visible ones are first.
      quickSort(0, hid.length - 1);
      insertionSort(0, hid.length - 1);
    }

    private void quickSort(final int l, final int r) {
      final int M = 4;
      int i, j, v;
      if (r - l > M) {
        i = (r + l) / 2;
        if (hid[l] > hid[i]) {
          swap(l, i); // Tri-Median Method
        }
        if (hid[l] > hid[r]) {
          swap(l, r);
        }
        if (hid[i] > hid[r]) {
          swap(i, r);
        }
        j = r - 1;
        swap(i, j);
        i = l;
        v = hid[j];
        for (;;) {
          while (hid[++i] < v) {
            // nop
          }
          while (hid[--j] > v) {
            // nop
          }
          if (j < i) {
            break;
          }
          swap(i, j);
        }
        swap(i, r - 1);
        quickSort(l, j);
        quickSort(i + 1, r);
      }
    }

    byte tmpByte;
    float[] tmpFloatArray;
    ByteSequence[] tmpByteSequence1, tmpByteSequence2;
    int tmpInt;
    boolean tmpBit;

    private void swap(final int i, final int j) {
      final PointsetTable table = session.spreadsheet
          .getPointsetTable(Pointset.this);
      tmpByte = hid[i];
      hid[i] = hid[j];
      hid[j] = tmpByte;
      //
      tmpFloatArray = numbers[i];
      numbers[i] = numbers[j];
      numbers[j] = tmpFloatArray;
      //
      // tmpByteSequence1 = objNames[i];
      // objNames[i] = objNames[j];
      // objNames[j] = tmpByteSequence1;
      tmpByteSequence1 = strings[i];
      strings[i] = strings[j];
      strings[j] = tmpByteSequence1;
      //
      tmpInt = table.userToTable[i];
      table.userToTable[i] = table.userToTable[j];
      table.userToTable[j] = tmpInt;
      //
      tmpBit = selected.get(i);
      selected.set(i, selected.get(j));
      selected.set(j, tmpBit);
      //
      // if (objDescriptions != null) {
      // tmpByteSequence2 = objDescriptions[i];
      // objDescriptions[i] = objDescriptions[j];
      // objDescriptions[j] = tmpByteSequence2;
      // }
    }

    private void insertionSort(final int lo, final int hi) {
      final PointsetTable table = session.spreadsheet
          .getPointsetTable(Pointset.this);
      int i, j;
      // int tmpInt;
      // byte tmpByte;
      // float[] tmpFloatArray;
      // ByteSequence tmpByteSequence1;
      for (i = lo + 1; i <= hi; i++) {
        tmpInt = table.userToTable[i];
        tmpByte = hid[i];
        tmpFloatArray = numbers[i];
        // tmpByteSequence1 = objNames[i];
        // if (objDescriptions != null) {
        // tmpByteSequence2 = objDescriptions[i];
        // }
        tmpByteSequence1 = strings[i];
        tmpBit = selected.get(i);
        j = i;
        while (j > lo && hid[j - 1] > tmpByte) {
          table.userToTable[j] = table.userToTable[j - 1];
          hid[j] = hid[j - 1];
          numbers[j] = numbers[j - 1];
          strings[j] = strings[j - 1];
          // objNames[j] = objNames[j - 1];
          selected.set(j, selected.get(j - 1));
          // if (objDescriptions != null) {
          // objDescriptions[j] = objDescriptions[j - 1];
          // }
          j--;
        }
        table.userToTable[j] = tmpInt;
        hid[j] = tmpByte;
        numbers[j] = tmpFloatArray;
        // objNames[j] = tmpByteSequence1;
        strings[j] = tmpByteSequence1;
        // if (objDescriptions != null) {
        // objDescriptions[j] = tmpByteSequence2;
        // }
        selected.set(j, tmpBit);
      }
    }

    /**
     * This sorts the table and generates a table reindexing. It does not move
     * the elements. Into ascending order.
     */
    private void ascendingTableSortString() {
      final PointsetTable pointsetTable = session.spreadsheet
          .getPointsetTable(Pointset.this);
      pointsetTable.getTable().getParent().getParent().setCursor(
          new Cursor(Cursor.WAIT_CURSOR));
      ascendingTableQuickSortString(0, pointCount - 1);
      ascendingTableInsertionSortString(0, pointCount - 1);
      pointsetTable.invert();
      // dump();
      pointsetTable.getTable().getParent().getParent().setCursor(
          new Cursor(Cursor.DEFAULT_CURSOR));
    }

    private void ascendingTableQuickSortString(final int l, final int r) {
      /*
       * int M = 4, i, j, v; if ((r-l)>M) { i = (r+l)/2; if
       * (molNames[userToTable[l]].compareToIgnoreCase(
       * molNames[userToTable[i]]) > 0) tableSwap(l,i); if
       * (molNames[userToTable[l]].compareToIgnoreCase(
       * molNames[userToTable[r]]) > 0) tableSwap(l,r); if
       * (molNames[userToTable[i]].compareToIgnoreCase(
       * molNames[userToTable[r]]) > 0) tableSwap(i,r); j = r-1; tableSwap(i,j);
       * i = l; v = j; for(;;) {
       * while(molNames[userToTable[++i]].compareToIgnoreCase(
       * molNames[userToTable[v]]) < 0);
       * while(molNames[userToTable[--j]].compareToIgnoreCase(
       * molNames[userToTable[v]]) > 0); if (j<i) break; tableSwap(i,j); }
       * tableSwap(i,r-1); ascendingTableQuickSortString(l,j);
       * ascendingTableQuickSortString(i+1,r); }
       */
    }

    private void tableSwap(final int i, final int j) {
      final PointsetTable table = session.spreadsheet
          .getPointsetTable(Pointset.this);
      final int tmp = table.userToTable[i];
      table.userToTable[i] = table.userToTable[j];
      table.userToTable[j] = tmp;
    }

    private void ascendingTableInsertionSortString(final int lo, final int hi) {
      /*
       * int i, j, tmp; for (i=lo+1; i<=hi; i++) { tmp = i; j=i; while ((j>lo) &&
       * (molNames[userToTable[j-1]].compareToIgnoreCase(
       * molNames[userToTable[tmp]]) > 0)) { j--; } j = tmp; }
       */
    }

    /**
     * This sorts the table and generates a table reindexing. It does not move
     * the elements. Into descending order.
     */
    private void descendingTableSortString() {
      final PointsetTable pointsetTable = session.spreadsheet
          .getPointsetTable(Pointset.this);
      // session.setWaitCursor(true);
      pointsetTable.getTable().getParent().getParent().setCursor(
          new Cursor(Cursor.WAIT_CURSOR));
      // descendingTableQuickSortString(0, pointCount-1);
      descendingTableInsertionSortString(0, pointCount - 1);
      // dump();
      pointsetTable.invert();
      // dump();
      session.setWaitCursor(false);
      pointsetTable.getTable().getParent().getParent().setCursor(
          new Cursor(Cursor.DEFAULT_CURSOR));
      // session.setWaitCursor(false);
    }

    private void descendingTableQuickSortString(final int l, final int r) {
      /*
       * int M = 4, i, j, v; if ((r-l)>M) { i = (r+l)/2; if
       * (molNames[userToTable[l]].compareToIgnoreCase(
       * molNames[userToTable[i]]) < 0) tableSwap(l,i); if
       * (molNames[userToTable[l]].compareToIgnoreCase(
       * molNames[userToTable[r]]) < 0) tableSwap(l,r); if
       * (molNames[userToTable[i]].compareToIgnoreCase(
       * molNames[userToTable[r]]) < 0) tableSwap(i,r); j = r-1; tableSwap(i,j);
       * i = l; v = j; for(;;) {
       * while(molNames[userToTable[++i]].compareToIgnoreCase(
       * molNames[userToTable[v]]) > 0);
       * while(molNames[userToTable[--j]].compareToIgnoreCase(
       * molNames[userToTable[v]]) < 0); if (j<i) break; tableSwap(i,j); }
       * tableSwap(i,r-1); descendingTableQuickSortString(l,j);
       * descendingTableQuickSortString(i+1,r); }
       */
    }

    private void descendingTableInsertionSortString(final int lo, final int hi) {
      /*
       * int i, j, tmp; for (i=lo+1; i<=hi; i++) { tmp = i; j=i; while ((j>lo) &&
       * (molNames[userToTable[j-1]].compareToIgnoreCase(
       * molNames[userToTable[tmp]]) < 0)) { j--; } j = tmp; }
       */
    }
  } // End of PointSorter

  private Shape symbolShape;
  private float symbolSize;
  private Color color;
  // First nVisible elements are visible during rot, with zoom & user hidden
  private int nVisible;
  // NumberFloat of visible numbers during partial blanking
  private int nVisible2;
  private int pointCount; // total number of data numbers, hidden & visible
  private BitSet selected;
  private int leadSelection = -1;
  // Coordinate numbers (always sorted according to visibility)
  private float[][] numbers; // float descriptor values
  // private ByteSequence[] objNames;
  // private ByteSequence[] objDescriptions;
  private ByteSequence[][] strings; // string descriptor values
  private byte[][] rgb;
  public byte hid[];
  private boolean adapting; // thread is running
  private int nDrawsAllPoints;
  private int drawTimeAllPoints[];
  private int avgDrawTimeAllPoints;
  transient public float[][] tPoints; // Points after transformation
  transient private boolean loading;
  transient public int nBytesRead; // TODO: make this long instead of int
  transient public int fileLength; // TODO: make this long
  transient List<ChangeListener> listeners = new ArrayList<ChangeListener>();
  private Basis basis;
  private String missingBasis;
  private boolean stale; // prereqs have changed
  private boolean dirty; // we need saved
  private int dimensionality;
  private AxesColumnHeadsMapping currentAcMap, acMap;
  // TODO: merge these to columnHeadToColumn?
  // private int[] columnHeadToNumbersColumn;
  private int[] columnHeadToStringsColumn;
  private ColumnHead colorizingColumnHead;
  private PointSorter pointSorter;
  private Stats[] colStats;
  private int[] limitLowPercentages;
  private int[] limitHighPercentages;
  // TODO: eliminate showBelowLimits, negate limitPercentages to indicate
  // instead
  private boolean[] showInsideLimits;
  public static final byte HID_ADAPT = 0x01, HID_ZOOM = 0x02,
      HID_ADAPT_ZOOM = 0x04, HID_USER = 0x08, HID_LIMIT = 0x10;
  private int stringMetaColumnCount = 2;
  public final static int stringMetaPrimaryKeyColumnIndex = 0;
  public final static int stringMetaImageColumnIndex = 1;

  public Pointset(final Session session, final Basis basis) {
    super(session);
    this.basis = basis;
    color = null;// Palette.lightColors[Palette.nextLightColorIndex()]; //
    // default
    symbolShape = Shape.DOT; // default shape
    symbolSize = 2.0f; // default
  }

  public void initializeAcMap() {
    currentAcMap = acMap;
    if (basis != null) {
      if (basis.getDimensionality() < 2) {
        throw new RuntimeException(
            "basis must consist of at least two descriptors");
      }
      this.dimensionality = basis.getDimensionality();
      acMap = new AxesColumnHeadsMapping(session);
      acMap.initialize(dimensionality);
      ColumnHead[] columnHeads = basis.getAcMap().getColumnHeads();
      for (int i = 0; i < columnHeads.length; i++) {
        // TODO: do we want to set x:0,y:1,z:2, or just let them stay -1
        acMap.setColumnHeadAt(columnHeads[i], i);
      }
      if (session.plotManager.basis == null
          || session.plotManager.basis != basis) {
        currentAcMap = acMap;
      }
      else {
        currentAcMap = basis.getAcMap();
      }
    }
    if (acMap == null) {
      // basis was null, so you need to init acMap yourself with value for dim
      throw new RuntimeException("acMap is not defined");
    }
  }

  public void initializePoints(int pointCount) {
    // assumes acmap init'd
    this.pointCount = pointCount;
    int numberKindCount = acMap.getNumberKindCount();
    int stringKindCount = acMap.getStringKindCount();
    if (numberKindCount + stringKindCount != dimensionality) {
      throw new RuntimeException("");
    }
    numbers = new float[pointCount][dimensionality];
    // columnHeadToNumbersColumn = new int[dimensionality];
    //
    strings = new ByteSequence[pointCount][stringKindCount
        + stringMetaColumnCount];
    columnHeadToStringsColumn = new int[dimensionality];
    //
    numberKindCount = stringKindCount = 0;
    for (int i = 0; i < acMap.dimensionality; i++) {
      ColumnHead columnHead = acMap.getColumnHeadAt(i);
      switch (columnHead.kind) {
      case NumberFloat:
        // columnHeadToNumbersColumn[i] = numberKindCount++;
        columnHeadToStringsColumn[i] = -1;
        break;
      case String:
        columnHeadToStringsColumn[i] = stringMetaColumnCount
            + stringKindCount++;
        // columnHeadToNumbersColumn[i] = -1;
        break;
      case NumberInt:
        throw new RuntimeException("bad kind");
      }
    }
    //
    limitLowPercentages = new int[dimensionality];
    limitHighPercentages = new int[dimensionality];
    showInsideLimits = new boolean[dimensionality];
    for (int i = 0; i < dimensionality; i++) {
      limitLowPercentages[i] = 0;
      limitHighPercentages[i] = 100;
      showInsideLimits[i] = true;
    }
    colStats = new Stats[dimensionality];
    for (int i = 0; i < colStats.length; i++) {
      colStats[i] = new Stats((float[]) null);
    }
    //
    selected = new BitSet(pointCount);
    // objNames = new ByteSequence[pointCount];
    // objNames = new ByteSequence[pointCount];
    // objDescriptions = new ByteSequence[pointCount];
    nVisible2 = nVisible = pointCount;
    visible = false; // don't show while loading
    hid = new byte[pointCount]; // visible by default
    drawTimeAllPoints = new int[20];
    pointSorter = new PointSorter();
    initializeTransient();
  }

  @Override
	public void initializeTransient() {
    tPoints = new float[pointCount][dimensionality];
    session.pointsetManager.nAdaptVisible = session.pointsetManager.maxNPoints = Math
        .max(session.pointsetManager.maxNPoints, pointCount);
  }

  @Override
  public void calculateViewExtents() {
    if (session.plotManager.canvas.fastCompose) {
      super.calculateViewExtents();
      return;
    }
    int i;
    tMin[0] = Float.POSITIVE_INFINITY;
    tMin[1] = Float.POSITIVE_INFINITY;
    tMin[2] = Float.POSITIVE_INFINITY;
    tMax[0] = Float.NEGATIVE_INFINITY;
    tMax[1] = Float.NEGATIVE_INFINITY;
    tMax[2] = Float.NEGATIVE_INFINITY;
    switch (session.plotManager.getDim()) {
    case 2:
      transform2D(false); // assumes TMat has been orthogonalized
      for (i = 0; i < nVisible; i++) {
        if (tPoints[i][0] > tMax[0]) {
          tMax[0] = tPoints[i][0];
        }
        if (tPoints[i][1] > tMax[1]) {
          tMax[1] = tPoints[i][1];
        }
        if (tPoints[i][0] < tMin[0]) {
          tMin[0] = tPoints[i][0];
        }
        if (tPoints[i][1] < tMin[1]) {
          tMin[1] = tPoints[i][1];
        }
      }
      break;
    case 3:
      transform3D(false); // assumes TMat has been orthogonalized
      for (i = 0; i < nVisible; i++) {
        if (tPoints[i][0] > tMax[0]) {
          tMax[0] = tPoints[i][0];
        }
        if (tPoints[i][1] > tMax[1]) {
          tMax[1] = tPoints[i][1];
        }
        if (tPoints[i][2] > tMax[2]) {
          tMax[2] = tPoints[i][2];
        }
        if (tPoints[i][0] < tMin[0]) {
          tMin[0] = tPoints[i][0];
        }
        if (tPoints[i][1] < tMin[1]) {
          tMin[1] = tPoints[i][1];
        }
        if (tPoints[i][2] < tMin[2]) {
          tMin[2] = tPoints[i][2];
        }
      }
      break;
    default:
      throw new RuntimeException("bad dim 2");
    }
    // System.out.println("tmax " + tMax[0] + " tmin " + tMin[0]);
  }

  @Override
  public void zoom(final int loX, final int hiX, final int loY, final int hiY) {
    // Set all numbers that transform into the x,y region
    // and are visible to "deselected"
    // Called when the selection state is changed
    int i;
    for (i = 0; i < nVisible; i++) {
      if (tPoints[i][0] >= loX && tPoints[i][0] <= hiX && tPoints[i][1] >= loY
          && tPoints[i][1] <= hiY) {
        // inside RBB: nop
      }
      else {
        if ((hid[i] & HID_ZOOM) == HID_ZOOM) {
          // TODO: remove for production
          throw new RuntimeException("bad hid state");
        }
        hid[i] |= HID_ZOOM;
      }
    }
    hiddenChanged();
    calculateSubjectExtents();
  }

  @Override
  public void unzoom() {
    // Set all numbers that transform into the x,y region and are visible to
    // "deselected". Called when the escape key typed
    for (int i = 0; i < pointCount; i++) {
      hid[i] &= ~HID_ZOOM;
    }
    hiddenChanged();
    calculateSubjectExtents();
  }

  public String[] identify(final int x, final int y) {
    float f = 5.0f; // view units
    float xLo = x - f, xHi = x + f, yLo = y - f, yHi = y + f;
    int i, n;
    boolean b;
    final List<Integer> v = new ArrayList<Integer>();
    // scan the visible data and find the closest with respect to x,y
    for (i = 0; i < nVisible; i++) {
      if (tPoints[i][0] >= xLo && tPoints[i][0] <= xHi && tPoints[i][1] >= yLo
          && tPoints[i][1] <= yHi) {
        v.add(new Integer(i));
      }
    }
    if (v.size() > 1) {
      f = 0.0f;
      l: do {
        b = false;
        xLo = x - f;
        xHi = x + f;
        yLo = y - f;
        yHi = y + f;
        for (i = 0; i < v.size(); i++) {
          if (tPoints[i][0] >= xLo && tPoints[i][0] <= xHi
              && tPoints[i][1] >= yLo && tPoints[i][1] <= yHi) {
            b = true;
          }
        }
        if (b) {
          for (i = v.size() - 1; i <= 0; i--) {
            if (tPoints[i][0] < xLo && tPoints[i][0] > xHi
                && tPoints[i][1] < yLo && tPoints[i][1] > yHi) {
              v.remove(i);
              break l;
            }
          }
        }
      } while (f++ <= 5.0f);
    }
    final int sz = v.size();
    final String[] s = new String[sz];
    n = 0;
    for (n = 0; n < sz; n++) {
      i = v.get(n).intValue();
      s[n] = new String(strings[i][stringMetaPrimaryKeyColumnIndex].bytes);
      // table.setRowSelectionInterval(i,i);
    }
    return s;
  }

  void hiddenChanged() {
    // hidden[] has been modified and we need to sort
    // the numbers based on hidden[] (the key). This moves the
    // elements of the table so the visible ones are first.
    pointSorter.sort();
    // The previous sort() correspondingly changed userToTable[] mapping
    // Now update the reverse mapping tableToUser[]
    session.spreadsheet.getPointsetTable(this).invert();
    int i = 0;
    nVisible = nVisible2 = pointCount;
    while (i < pointCount) {
      if (hid[i] == 0x00) {
        // nop
      }
      else if ((hid[i] & HID_ADAPT) == HID_ADAPT) {
        nVisible2--;
      }
      else if ((hid[i] & HID_USER) == HID_USER
          || (hid[i] & HID_ZOOM) == HID_ZOOM
          || (hid[i] & HID_LIMIT) == HID_LIMIT) {
        nVisible--;
        nVisible2--;
      }
      i++;
    }
    // System.out.println("nHidAdapt: "+nHidAdapt+" nHidUser: "+nHidUser+"
    // hidZoom: "+"hidAdaptZoom: "+nHidAdaptZoom);
    // System.out.println("NVISIBLE: "+nVisible+" nVisible2: "+nVisible2);
    // System.out.println("Points visible = "+nVisible);
    switch (session.plotManager.getDim()) {
    case 2:
      transform2D(true);
      break;
    case 3:
      transform3D(true);
      break;
    default:
      throw new RuntimeException("dim");
    }
    calculateSubjectExtents();
    if (nVisible < nVisible2) {
      throw new RuntimeException("bad visibility: nVisible = " + nVisible
          + ", nVisible2: " + nVisible2);
    }
    // dump2();
  }

  private void dump2() {
    // System.out.println("i,userToTable,tableToUser,compoundName,hid");
    final PointsetTable table = session.spreadsheet.getPointsetTable(this);
    for (int i = 0; i < pointCount; i++) {
      System.out.println(i + "," + table.userToTable[i] + ","
          + table.tableToUser[i] + ","
          + new String(strings[i][stringMetaPrimaryKeyColumnIndex].bytes) + ","
          + hid[i]);
    }
  }

  void dumpUserSpace() {
    for (int i = 0; i < pointCount; i++) {
      System.out.println("data "
          + (numbers[i][0] + session.plotManager.subject.cumDelta[0]) + ", "
          + (numbers[i][1] + session.plotManager.subject.cumDelta[1]) + ", "
          + (numbers[i][2] + session.plotManager.subject.cumDelta[2]));
    }
  }

  private void dumpSubjectSpace() {
    System.out.println("i: sx,sy,sz for" + name);
    for (int i = 0; i < pointCount; i++) {
      System.out.println(i + ": " + numbers[i][0] + "," + numbers[i][1] + ","
          + numbers[i][2]);
    }
  }

  private void dumpScreenSpace() {
    System.out.println("i: tx,ty,tz for" + name);
    for (int i = 0; i < pointCount; i++) {
      System.out.println(i + ": " + tPoints[i][0] + "," + tPoints[i][1] + ","
          + tPoints[i][2]);
    }
  }

  void adapt() {
    int i, n = 0;
    final int nHidden = nVisible - nVisible2; //
    final Random r = new Random();
    if (session.pointsetManager.nAdaptVisible > nVisible2) {
      // We want to make some more numbers visible
      // System.out.println("MAKING MORE POINTS VISIBLE");
      if (session.pointsetManager.nAdaptVisible < nVisible) { // don't show ALL
        // hidden numbers
        n = session.pointsetManager.nAdaptVisible - nVisible2; // number to
        // "turn on"
        // we need to show n random numbers between nVisible and nVisible2
        for (i = nVisible2; i < nVisible; i++) {
          if (r.nextInt(nHidden) < n) { // flip the coin
            hid[i] = 0x00;
          }
        }
      }
      else { // show all hidden numbers - no randomness required
        // Since the numbers are sorted on hid, we can just change
        // the ones starting at nVisible
        for (i = nVisible2; i < nVisible; i++) {
          hid[i] = 0x00;
        }
      }
      hiddenChanged();
    }
    else if (session.pointsetManager.nAdaptVisible < nVisible2) {
      // We want to hide some numbers
      // System.out.println("MAKING LESS POINTS VISIBLE");
      if (session.pointsetManager.nAdaptVisible <= 0) {
        // no randomness required
        for (i = 0; i < nVisible2; i++) {
          hid[i] |= HID_ADAPT;
        }
      }
      else { // randomness required
        n = nVisible2 - session.pointsetManager.nAdaptVisible;
        // number to hide
        // System.out.println("NUMBER TO HIDE: "+n);
        // we need n random numbers between 0 and nVisible
        for (i = 0; i < nVisible2; i++) {
          if (r.nextInt(nVisible2) < n) { // flip the coin
            hid[i] |= HID_ADAPT;
          }
        }
      }
      hiddenChanged();
    }
    else {
      // don't need to do anything
    }
  }

  @Override
  public void draw2D(final Slate slate, final boolean drawAllPoints) {
    if (!visible) {
      return;
    }
    Date dateInitial = null, dateFinal = null;
    int u = (int) (symbolSize / 2 * session.dotsPerMM);
    final int n = drawAllPoints ? nVisible : nVisible2;
    transform2D(drawAllPoints);
    if (u < 1) {
      u = 1;
    }
    // System.out.println("Drawing N = " + n + " with color = " + color);
    slate.setColor(color);
    if (drawAllPoints) {
      dateInitial = new Date();
    }
    symbolShape.draw2D(slate, n, tPoints, u);
    if (drawAllPoints) {
      int i;
      dateFinal = new Date();
      for (i = 19; i > 0; i--) {
        drawTimeAllPoints[i] = drawTimeAllPoints[i - 1];
      }
      drawTimeAllPoints[0] += dateFinal.getTime() - dateInitial.getTime();
      nDrawsAllPoints++;
      int tot = 0;
      for (i = 0; i < 20; i++) {
        tot += drawTimeAllPoints[i];
      }
      avgDrawTimeAllPoints = tot / nDrawsAllPoints;
    }
    if (leadSelection >= 0 && leadSelection < n) {
      slate.drawCrosshairs((int) tPoints[leadSelection][0],
          (int) tPoints[leadSelection][1], (int) symbolSize, color);
    }
  }

  @Override
  public void draw3D(final Slate slate, final boolean drawAllPoints) {
    if (!visible) {
      return;
    }
    // dumpSubjectSpace();
    Date dateInitial = null, dateFinal = null;
    final int n = drawAllPoints ? nVisible : nVisible2;
    transform3D(drawAllPoints);
    slate.setColor(color); // TODO: figure out alpha
    if (drawAllPoints) {
      dateInitial = new Date();
    }
    float a = session.plotManager.view
        .getPerspectiveFactor((session.plotManager.view.max[2] + session.plotManager.view.min[2]) / (2.0f));
    a /= symbolSize * session.dotsPerMM;
    if (colorizingColumnHead == null) {
      if (selected.size() > 0) {
        symbolShape.draw3D(slate, session.plotManager.view, n, tPoints, a,
            selected);
      }
      else {
        symbolShape.draw3D(slate, session.plotManager.view, n, tPoints, a);
      }
    }
    else {
      if (selected.size() > 0) {
        symbolShape.draw3D(slate, session.plotManager.view, n, tPoints, a, rgb,
            selected, color);
      }
      else {
        symbolShape.draw3D(slate, session.plotManager.view, n, tPoints, a, rgb);
      }
    }
    if (drawAllPoints) {
      int i;
      dateFinal = new Date();
      for (i = 19; i > 0; i--) {
        drawTimeAllPoints[i] = drawTimeAllPoints[i - 1];
      }
      drawTimeAllPoints[0] += dateFinal.getTime() - dateInitial.getTime();
      nDrawsAllPoints++;
      int tot = 0;
      for (i = 0; i < 20; i++) {
        tot += drawTimeAllPoints[i];
      }
      avgDrawTimeAllPoints = tot / nDrawsAllPoints;
      // System.out.println("avgDrawTimeAllPoints: "+avgDrawTimeAllPoints);
    }
    if (leadSelection >= 0 && leadSelection < n) {
      slate.drawCrosshairs((int) tPoints[leadSelection][0],
          (int) tPoints[leadSelection][1], (int) symbolSize, color);
    }
  }

  @Override
  public String toString() {
    final String s = "[Pointset: " + name + "; shape = "
        + symbolShape.toString() + "; " + pointCount + "]";
    return s;
  }

  @Override
	public String getName() {
    return name;
  }

  public int getNVisible() {
    return nVisible;
  }

  @Override
	public Color getColor() {
    return color;
  }

  public void setPointCount(final int points) {
    pointCount = points;
  }

  public int getPointCount() {
    return pointCount;
  }

  public String getToolTipText() {
    final StringBuffer sb = new StringBuffer("<HTML><boundingBox>Pointset: "
        + getName() + "</boundingBox><br>");
    sb.append("  point count = " + pointCount + "<br>");
    sb.append("  shape = " + symbolShape.toString() + "<br>");
    if (basis != null) {
      sb.append("  basis = " + basis.getName() + "<br>");
    }
    else {
      sb.append("  no basis<br>");
    }
    sb.append("  visible = " + visible + "<br>");
    sb.append("  extent min = {" + tMin[0] + "," + tMin[1] + "," + tMin[2]
        + "}" + "<br>");
    sb.append("  extent max = {" + tMax[0] + "," + tMax[1] + "," + tMax[2]
        + "}" + "<br>");
    return sb.toString();
  }

  public void setName(final String name) {
    if (!Session.isLegalName(name, true)) {
      throw new RuntimeException("illegal name: " + name);
    }
    this.name = name;
  }

  @Override
	public void setSession(final Session session) {
    this.session = session;
  }

  public boolean isStale() {
    return stale;
  }

  public void setStale(final boolean stale) {
    this.stale = stale;
  }

  // @Implements Comparable
  @Override
	public int compareTo(final Pointset pointset) {
    String name1 = getName();
    String name2 = pointset.getName();
    if (name1 == null) {
      if (name2 == null) {
        return 0;
      }
      return Integer.MIN_VALUE;
    }
    if (name2 == null) {
      return Integer.MAX_VALUE;
    }
    return name1.compareTo(name2);
  }

  void calculateSubjectExtents() {
    min[0] = Float.POSITIVE_INFINITY;
    min[1] = Float.POSITIVE_INFINITY;
    min[2] = Float.POSITIVE_INFINITY;
    max[0] = Float.NEGATIVE_INFINITY;
    max[1] = Float.NEGATIVE_INFINITY;
    max[2] = Float.NEGATIVE_INFINITY;
    int columnHeadIndex, col;
    ColumnHead columnHead;
    float f;
    for (int axis = 0; axis < session.plotManager.getDim(); axis++) {
      columnHead = currentAcMap.getColumnHeadForAxis(axis);
      columnHeadIndex = currentAcMap.getColumnHeadIndexFor(columnHead);
      switch (columnHead.kind) {
      case NumberFloat:
        for (int i = 0; i < nVisible; i++) {
          if (numbers[i][columnHeadIndex] > max[0]) {
            max[0] = numbers[i][columnHeadIndex];
          }
          if (numbers[i][columnHeadIndex] < min[0]) {
            min[0] = numbers[i][columnHeadIndex];
          }
        }
        break;
      case String:
        col = columnHeadToStringsColumn[columnHeadIndex];
        for (int i = 0; i < nVisible; i++) {
          f = strings[i][col].getUserFloat();
          if (f > max[0]) {
            max[0] = f;
          }
          if (f < min[0]) {
            min[0] = f;
          }
        }
        break;
      case NumberInt:
        throw new RuntimeException("bad kind");
      }
    }
  }

  @Override
  public void transform2D(boolean transformAllPoints) {
    final int n = transformAllPoints ? nVisible : nVisible2;
    int xCol = currentAcMap.getColumnHeadIndexForAxis(Axes.X_AXIS);
    int yCol = currentAcMap.getColumnHeadIndexForAxis(Axes.Y_AXIS);
    for (int i = 0; i < n; i++) {
      tPoints[i][0] = (numbers[i][xCol] * session.plotManager.view.tMat[0][0] + numbers[i][yCol]
          * session.plotManager.view.tMat[1][0])
          * session.plotManager.view.focal
          + session.plotManager.view.tMat[3][0];
      tPoints[i][1] = (numbers[i][xCol] * session.plotManager.view.tMat[0][1] + numbers[i][yCol]
          * session.plotManager.view.tMat[1][1])
          * session.plotManager.view.focal
          + session.plotManager.view.tMat[3][1];
    }
  }

  @Override
  public void transform3D(boolean transformAllPoints) {
    // dumpScreenSpace();
    // dumpSubjectSpace();
    float f;
    final int n = transformAllPoints ? nVisible : nVisible2;
    int xCol = currentAcMap.getColumnHeadIndexForAxis(Axes.X_AXIS);
    int yCol = currentAcMap.getColumnHeadIndexForAxis(Axes.Y_AXIS);
    int zCol = currentAcMap.getColumnHeadIndexForAxis(Axes.Z_AXIS);
    for (int i = 0; i < n; i++) {
      tPoints[i][2] = numbers[i][xCol] * session.plotManager.view.tMat[0][2]
          + numbers[i][yCol] * session.plotManager.view.tMat[1][2]
          + numbers[i][zCol] * session.plotManager.view.tMat[2][2]
          + session.plotManager.view.tMat[3][2];
      f = session.plotManager.view.getPerspectiveFactor(tPoints[i][2]);
      tPoints[i][0] = (numbers[i][xCol] * session.plotManager.view.tMat[0][0]
          + numbers[i][yCol] * session.plotManager.view.tMat[1][0] + numbers[i][zCol]
          * session.plotManager.view.tMat[2][0])
          * f + session.plotManager.view.tMat[3][0];
      tPoints[i][1] = (numbers[i][xCol] * session.plotManager.view.tMat[0][1]
          + numbers[i][yCol] * session.plotManager.view.tMat[1][1] + numbers[i][zCol]
          * session.plotManager.view.tMat[2][1])
          * f + session.plotManager.view.tMat[3][1];
    }
  }

  @Override
  public void translate(final float[] delta) {
    // Do not call this yourself, it is called by Subject
    int xCol = currentAcMap.getColumnHeadIndexForAxis(Axes.X_AXIS);
    int yCol = currentAcMap.getColumnHeadIndexForAxis(Axes.Y_AXIS);
    int zCol = currentAcMap.getColumnHeadIndexForAxis(Axes.Z_AXIS);
    switch (session.plotManager.getDim()) {
    case 2:
      for (int i = 0; i < pointCount; i++) {
        numbers[i][xCol] -= delta[0];
        numbers[i][yCol] -= delta[1];
      }
      min[0] -= delta[0];
      min[1] -= delta[1];
      max[0] -= delta[0];
      max[1] -= delta[1];
      break;
    case 3:
      for (int i = 0; i < pointCount; i++) {
        numbers[i][xCol] -= delta[0];
        numbers[i][yCol] -= delta[1];
        numbers[i][zCol] -= delta[2];
      }
      min[0] -= delta[0];
      min[1] -= delta[1];
      min[2] -= delta[2];
      max[0] -= delta[0];
      max[1] -= delta[1];
      max[2] -= delta[2];
      break;
    default:
      throw new RuntimeException("bad dim 3");
    }
  }

  public void moveToUserSpace() {
    // Move ANY columns that are mapped to axes out of subject space.
    // Do not remove ac mappings.
    for (int axis = 0; axis < session.plotManager.getDim(); axis++) {
      int columnHeadIndex = currentAcMap.getColumnHeadIndexForAxis(axis);
      if (columnHeadIndex != AxesColumnHeadsMapping.UNMAPPED) {
        moveColumnHeadIndexToUserSpace(columnHeadIndex);
      }
    }
  }

  public void moveColumnHeadToUserSpace(final ColumnHead columnHead) {
    int columnHeadIndex = currentAcMap.getColumnHeadIndexFor(columnHead);
    moveColumnHeadIndexToUserSpace(columnHeadIndex);
  }

  public void moveColumnHeadIndexToUserSpace(final int columnHeadIndex) {
    int col;
    int axis = currentAcMap.getAxisForColumnHeadIndex(columnHeadIndex);
    if (axis != AxesColumnHeadsMapping.UNMAPPED) {
      for (int i = 0; i < pointCount; i++) {
        numbers[i][columnHeadIndex] += session.plotManager.subject.cumDelta[axis];
      }
      min[axis] += session.plotManager.subject.cumDelta[axis];
      max[axis] += session.plotManager.subject.cumDelta[axis];
    }
  }

  public void moveToSubjectSpace() {
    // Move all mapped columns into subject space based on current ac mapping
    for (int axis = 0; axis < session.plotManager.getDim(); axis++) {
      int columnHeadIndex = currentAcMap.getColumnHeadIndexForAxis(axis);
      if (columnHeadIndex != AxesColumnHeadsMapping.UNMAPPED) {
        moveColumnHeadIndexToSubjectSpace(columnHeadIndex);
      }
    }
  }

  public void moveColumnHeadToSubjectSpace(final ColumnHead columnHead) {
    int columnHeadIndex = currentAcMap.getColumnHeadIndexFor(columnHead);
    moveColumnHeadIndexToSubjectSpace(columnHeadIndex);
  }

  public void moveColumnHeadIndexToSubjectSpace(final int columnHeadIndex) {
    int axis = currentAcMap.getAxisForColumnHeadIndex(columnHeadIndex);
    if (axis != AxesColumnHeadsMapping.UNMAPPED) {
      for (int i = 0; i < pointCount; i++) {
        numbers[i][columnHeadIndex] -= session.plotManager.subject.cumDelta[axis];
      }
      min[axis] -= session.plotManager.subject.cumDelta[axis];
      max[axis] -= session.plotManager.subject.cumDelta[axis];
    }
  }

  public void clearReferences() {
    // System.out.println("CLEARING REFERENCES TO DATA " + hashCode());
    // tabToolTipTimer.removeActionListener(this);
    // tabbedPane.removeMouseListener(tabbedPaneListener);
    final PointsetTable table = session.spreadsheet.getPointsetTable(this);
    table.clearReferences();
  }

  @Override
  public void finalize() {
    System.out.println("Finalized [" + getClass().getSimpleName() + ";"
        + hashCode() + "]");
  }

  public Shape getShape() {
    return symbolShape;
  }

  public void setPointShape(Shape shape) {
    this.symbolShape = shape;
  }

  public int getDimensionality() {
    return dimensionality;
  }

  public Basis getBasis() {
    return basis;
  }

  public void clearBasis() {
    // Calling this will disconnect from its descriptors, so we lose
    // functionality like synchronization but we keep labels for columns.
    this.basis = null;
  }

  public void setBasis(Basis basis) {
    this.basis = basis;
  }

  public void setBasisMode(Basis basisMode) {
    moveToUserSpace();
    if (basisMode == null) {
      // no-basis mode, use our own map
      currentAcMap = acMap;
    }
    else if (basisMode == basis) {
      // basis mode with our basis, use
      currentAcMap = basisMode.getAcMap();
      setVisible(true);
    }
    else {
      // not our basis, use our own, but don't draw
      currentAcMap = acMap;
      setVisible(false);
    }
    moveToSubjectSpace();
  }

  public boolean isAdapting() {
    return adapting;
  }

  public void setAdapting(boolean adapting) {
    this.adapting = adapting;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public float getSize() {
    return symbolSize;
  }

  public void setDimensionality(int dimensionality) {
    this.dimensionality = dimensionality;
  }

  @Override
	public AxesColumnHeadsMapping getAcMap() {
    return acMap;
  }

  public void setAcMap(AxesColumnHeadsMapping acMap) {
    this.acMap = acMap;
  }

  public float[][] getPointsInSubjectSpace() {
    return numbers;
  }

  public float[] getPointInUserSpace(int pointIndex) {
    float[] pointInUserSpace = new float[dimensionality];
    for (int col = 0; col < dimensionality; col++) {
      pointInUserSpace[col] = numbers[pointIndex][col];
    }
    // Move the mapped axes only
    for (int axis = 0; axis < session.plotManager.getDim(); axis++) {
      numbers[pointIndex][currentAcMap.getColumnHeadIndexForAxis(axis)] += session.plotManager.subject.cumDelta[axis];
    }
    return pointInUserSpace;
  }

  public float[][] getFilteredPointsInUserSpace() {
    boolean b = isVisible();
    setVisible(false);
    int[] tableToUser = session.spreadsheet.getPointsetTable(this).tableToUser;
    moveToUserSpace();
    float[][] pointsInUserSpace = new float[pointCount][dimensionality];
    //int stringsCol;
    float lowLimit, highLimit, value;
    // inverted loops here for performance
    for (int col = 0; col < dimensionality; col++) {
      lowLimit = colStats[col].range * (limitLowPercentages[col] / 100f)
          + colStats[col].min;
      highLimit = colStats[col].range * (limitHighPercentages[col] / 100f)
          + colStats[col].min;
      for (int row = 0; row < pointCount; row++) {
        int userRow = tableToUser[row];
        value = numbers[userRow][col];
        pointsInUserSpace[row][col] = Float.NaN;
        if (showInsideLimits[col]) {
          if (value >= lowLimit && value <= highLimit) {
            pointsInUserSpace[row][col] = value;
          }
        }
        else {
          if (value <= lowLimit || value >= highLimit) {
            pointsInUserSpace[row][col] = value;
          }
        }
      }
      session.pointsetManager.fireProgressChanged(this, "filtering", 0, col,
          dimensionality);
    }
    moveToSubjectSpace();
    setVisible(b);
    return pointsInUserSpace;
  }

  public void setPoints(float[][] points) {
    this.numbers = points;
  }

  public ByteSequence[] getObjNames() {
    ByteSequence[] objNames = new ByteSequence[pointCount];
    for (int i = 0; i < pointCount; i++) {
      objNames[i] = strings[i][stringMetaPrimaryKeyColumnIndex];
    }
    return objNames;
  }

  public ByteSequence getObjDescriptionAt(final int i) {
    return strings[i][stringMetaImageColumnIndex];
  }

  public void setObjDescriptionAt(final int i, ByteSequence objDescription) {
    strings[i][stringMetaImageColumnIndex] = objDescription;
  }

  public void setObjNameAt(final int i, ByteSequence objName) {
    strings[i][stringMetaPrimaryKeyColumnIndex] = objName;
  }

  public ByteSequence getObjNameAt(final int i) {
    return strings[i][stringMetaPrimaryKeyColumnIndex];
  }

  public void setObjNames(ByteSequence[] objNames) {
    for (int i = 0; i < pointCount; i++) {
      strings[i][stringMetaPrimaryKeyColumnIndex] = objNames[i];
    }
  }

  public AxesColumnHeadsMapping getCurrentAcMap() {
    return currentAcMap;
  }

  public void setPointValueUser(float f, int i, int col) {
    // f is in User space, need to move to Subject space, if necessary
    int axis = currentAcMap.getAxisForColumnHeadIndex(col);// colToAxis[col];
    if (axis != AxesColumnHeadsMapping.UNMAPPED) {
      // Column is in Subject space, move f to Subject space first
      numbers[i][col] = f - session.plotManager.subject.cumDelta[axis];
    }
    else {
      // Column is in User space
      numbers[i][col] = f;
    }
    dirty = true;
  }

  public float getNumberUser(int row, int columnHeadIndex) {
    int axis = currentAcMap.getAxisForColumnHeadIndex(columnHeadIndex);
    if (axis != AxesColumnHeadsMapping.UNMAPPED) {
      // Column is in Subject space. We need to add the cumDelta
      // for the axis this column maps to.
      return numbers[row][columnHeadIndex]
          + session.plotManager.subject.cumDelta[axis];
    }
    else {
      return numbers[row][columnHeadIndex];
    }
  }

  public Object getPointValueUser(int row, int columnHeadIndex) {
    int axis = currentAcMap.getAxisForColumnHeadIndex(columnHeadIndex);
    Object object = null;
    switch (currentAcMap.getColumnHeadAt(columnHeadIndex).getKind()) {
    case NumberFloat:
      // col = columnHeadToNumbersColumn[columnHeadIndex];
      if (axis != AxesColumnHeadsMapping.UNMAPPED) {
        // Column is in Subject space. We need to add the cumDelta
        // for the axis this column maps to.
        object = new Float(numbers[row][columnHeadIndex]
            + session.plotManager.subject.cumDelta[axis]);
      }
      else {
        object = new Float(numbers[row][columnHeadIndex]);
      }
      break;
    case String:
      int col = columnHeadToStringsColumn[columnHeadIndex];
      float f;
      if (axis != AxesColumnHeadsMapping.UNMAPPED) {
        // Column is in Subject space. We need to add the cumDelta
        // for the axis this column maps to.
        f = numbers[row][columnHeadIndex]
            + session.plotManager.subject.cumDelta[axis];
      }
      else {
        f = numbers[row][columnHeadIndex];
      }
      object = strings[row][col] + " (" + Float.toString(f) + ")";
      break;
    case NumberInt:
      throw new RuntimeException();
    }
    return object;
  }

  public boolean isColumnHeadInSubjectSpace(int columnHeadIndex) {
    boolean b = currentAcMap.getAxisForColumnHeadIndex(columnHeadIndex)/* colToAxis[column] */!= AxesColumnHeadsMapping.UNMAPPED;
    return b;
  }

  public ColumnHead getColorizingColumnHead() {
    return colorizingColumnHead;
  }

  public void setColorizingColumnHead(ColumnHead colorizingColumnHead) {
    if (rgb == null) {
      rgb = new byte[pointCount][3];
    }
    this.colorizingColumnHead = colorizingColumnHead;
  }

  public byte[][] getRgb() {
    return rgb;
  }

  public void findMissingBases() {
    basis = session.descriptorManager.getBasisByName(missingBasis);
    if (basis != null) {
      missingBasis = null;
    }
  }

  public void setMissingBasis(String basisName) {
    missingBasis = basisName;
  }

  public void setSelected(int[] selectedPoints) {
    if (selected == null) {
      return; // initializing
    }
    selected.clear();
    // if (selectedPoints == null || selectedPoints.length == 0) {
    // return;
    // }
    for (int i = 0; i < selectedPoints.length; i++) {
      selected.set(selectedPoints[i]);
    }
  }

  public void setLeadSelection(int rowIndex) {
    leadSelection = rowIndex;
  }

  public void setPointSize(float symbolSize) {
    this.symbolSize = symbolSize;
  }

  public float[] getColorizingColumnValues() {
    return getColumnUserValuesFor(colorizingColumnHead);
  }

  public float[] getColumnUserValuesFor(ColumnHead columnHead) {
    int columnHeadIndex = currentAcMap.getColumnHeadIndexFor(columnHead);
    return getColumnUserValuesAt(columnHeadIndex);
  }

  public float[] getColumnUserValuesAt(int columnHeadIndex) {
    boolean b = isVisible();
    setVisible(false);
    float[] values = new float[pointCount];
    moveColumnHeadIndexToUserSpace(columnHeadIndex);
    for (int i = 0; i < pointCount; i++) {
      values[i] = numbers[i][columnHeadIndex];
    }
    moveColumnHeadIndexToSubjectSpace(columnHeadIndex);
    setVisible(b);
    return values;
  }

  @Override
	public int getLowLimitPercentageFor(ColumnHead columnHead) {
    int columnHeadIndex = acMap.getColumnHeadIndexFor(columnHead);
    return limitLowPercentages[columnHeadIndex];
  }

  @Override
	public int getHighLimitPercentageFor(ColumnHead columnHead) {
    int columnHeadIndex = acMap.getColumnHeadIndexFor(columnHead);
    return limitHighPercentages[columnHeadIndex];
  }

  @Override
	public boolean getShowInsideLimitFor(ColumnHead columnHead) {
    int columnHeadIndex = acMap.getColumnHeadIndexFor(columnHead);
    return showInsideLimits[columnHeadIndex];
  }

  public void filterChanged(ColumnHead columnHead, int limitLowPercentage,
      int limitHighPercentage, boolean showInsideLimit) {
    int columnHeadIndex = acMap.getColumnHeadIndexFor(columnHead);
    this.limitLowPercentages[columnHeadIndex] = limitLowPercentage;
    this.limitHighPercentages[columnHeadIndex] = limitHighPercentage;
    this.showInsideLimits[columnHeadIndex] = showInsideLimit;
    // Recalculating all columns necessary because
    // hidden mask may be caused by more than one filter
    for (int i = 0; i < pointCount; i++) {
      hid[i] &= ~HID_LIMIT;
    }
    for (int col = 0; col < dimensionality; col++) {
      float lowLimit = colStats[col].range * (limitLowPercentages[col] / 100f)
          + colStats[col].min;
      float highLimit = colStats[col].range
          * (limitHighPercentages[col] / 100f) + colStats[col].min;
      float[] values = getColumnUserValuesAt(col);
      if (showInsideLimits[col]) {
        for (int i = 0; i < pointCount; i++) {
          if (values[i] < lowLimit || values[i] > highLimit) {
            hid[i] |= HID_LIMIT;
          }
        }
      }
      else {
        for (int i = 0; i < pointCount; i++) {
          if (values[i] > lowLimit && values[i] < highLimit) {
            hid[i] |= HID_LIMIT;
          }
        }
      }
    }
    hiddenChanged(); // sorts
  }

  public int getAvgDrawTimeAllPoints() {
    return avgDrawTimeAllPoints;
  }

  public void clearSelectionAt(int i) {
    selected.clear(i);
  }

  public void clearSelectedPoint() {
    leadSelection = -1;
  }

  public boolean isLoading() {
    return loading;
  }

  void setLoading(boolean loading) {
    this.loading = loading;
  }

  void calcStats() {
    ColumnHead[] columnHeads = acMap.getColumnHeads();
    for (int col = 0; col < columnHeads.length; col++) {
      colStats[col].calc(getColumnUserValuesFor(columnHeads[col]));
      session.pointsetManager.fireProgressChanged(this, "characterizing", 0,
          col, columnHeads.length);
    }
  }

  public Stats getStats(ColumnHead columnHead) {
    return colStats[acMap.getColumnHeadIndexFor(columnHead)];
  }

  public Stats[] getStats() {
    return colStats;
  }

  public Object getUserValue(int thingIndex, int columnHeadIndex) {
    ColumnHead columnHead = currentAcMap.getColumnHeadAt(columnHeadIndex);
    switch (columnHead.kind) {
    case NumberFloat:
      float value = numbers[thingIndex][columnHeadIndex];
      int axis = currentAcMap.getAxisForColumnHead(columnHead);
      if (AxesColumnHeadsMapping.UNMAPPED != axis) {
        value += session.plotManager.subject.cumDelta[axis];
      }
      return new Float(value);
    case String:
      return strings[thingIndex][columnHeadToStringsColumn[columnHeadIndex]]
          .toString();
    case NumberInt:
      throw new RuntimeException("bad type");
    }
    return null;
  }

  public void setUserValue(String value, int thingIndex, ColumnHead columnHead) {
    int columnHeadIndex = currentAcMap.getColumnHeadIndexFor(columnHead);
    int col;
    switch (columnHead.kind) {
    case NumberFloat:
      // col = columnHeadToNumbersColumn[columnHeadIndex];
      numbers[thingIndex][columnHeadIndex] = Float.parseFloat(value);
      break;
    case String:
      col = columnHeadToStringsColumn[columnHeadIndex];
      strings[thingIndex][col] = new ByteSequence(value.getBytes());
      break;
    case NumberInt:
      throw new RuntimeException("bad type");
    }
    setUserValue(value, thingIndex, columnHeadIndex);
  }

  public void setUserValue(String value, int thingIndex, int columnHeadIndex) {
    int col;
    switch (currentAcMap.getColumnHeadAt(columnHeadIndex).kind) {
    case NumberFloat:
      // col = columnHeadToNumbersColumn[columnHeadIndex];
      numbers[thingIndex][columnHeadIndex] = Float.parseFloat(value);
      break;
    case String:
      col = columnHeadToStringsColumn[columnHeadIndex];
      strings[thingIndex][col] = new ByteSequence(value.getBytes());
      break;
    case NumberInt:
      throw new RuntimeException("bad type");
    }
  }

  public void destring() {
    // Convert string to a float. Could be through a hash, sort, table look up,
    // etc. Value could be used to colorize, or allow to be on a coord axis
    // Convert string to an int value based on its sorted position.
    for (int col = 0; col < dimensionality; col++) {
      switch (currentAcMap.getColumnHeadAt(col).kind) {
      case NumberFloat:
        break;
      case String:
        int stringsCol = columnHeadToStringsColumn[col];
        ByteSequence[] column = new ByteSequence[pointCount];
        for (int row = 0; row < pointCount; row++) {
          // Remember unsorted order
          strings[row][stringsCol].setUserInt(row);
          column[row] = strings[row][stringsCol];
        }
        Arrays.sort(column);
        int rank = 0;
        for (int row = 0; row < pointCount; row++) {
          // Record sorted order
          ByteSequence byteSequence = column[row];
          if (row > 0
              && !byteSequence.toString().equals(column[row - 1].toString())) {
            rank++;
          }
          int unsortedPosition = byteSequence.getUserInt();
          numbers[unsortedPosition][col] = rank;
        }
        break;
      case NumberInt:
        break;
      }
      session.pointsetManager.fireProgressChanged(this, "de-stringing", 0, col,
          dimensionality);
    }
  }

  @Override
  public StringAndNumberFloat getStringForNumberFloat(ColumnHead columnHead,
      float value, boolean lessThan, float closest) {
    if (currentAcMap == null) {
      // we are in the process of initializing
      return new StringAndNumberFloat();
    }
    int columnHeadIndex = currentAcMap.getColumnHeadIndexFor(columnHead);
    // Returns string in "strings" that is equal to value in "numbers".
    // Or if one does not exist, returns closest one, either less than or
    // greater than.
    int stringsIndex = columnHeadToStringsColumn[columnHeadIndex];
    float candidate;
    int closestRow = -1;
      // NOTE: for single pointset, init as follows
      // if (lessThan ) {
      // closest = Float.NEGATIVE_INFINITY;
      // }
      // else {
      // closest = Float.POSITIVE_INFINITY;
      // }
    for (int row = 0; row < pointCount; row++) {
      candidate = getNumberUser(row, columnHeadIndex);
      if (candidate == value) {
        // closest=0.0f;
        return new StringAndNumberFloat(strings[row][stringsIndex].toString(),
            0.0f);
      }
      else if (candidate < value) {
        if (lessThan) {
          if (value > closest) {
            closestRow = row;
            closest = candidate;
          }
        }
      }
      else {
        if (!lessThan) {
          if (value < closest) {
            closestRow = row;
            closest = candidate;
          }
        }
      }
    }
    return new StringAndNumberFloat(strings[closestRow][stringsIndex]
        .toString(), closest);
  }
} // end of Pointset class
