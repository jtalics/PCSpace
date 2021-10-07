package gov.nih.ncgc.openhts.tool1.plotManager;

import java.awt.Color;
import java.awt.Font;
import gov.nih.ncgc.openhts.tool1.Palette;
import gov.nih.ncgc.openhts.tool1.Session;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public final class Axes extends Drawable implements PlotManagerListener {
  public static final int X_AXIS = 0, Y_AXIS = 1, Z_AXIS = 2;
  public static final String[] axisNames = { "X", "Y", "Z" };
  private Text[] labels;
  private Color labelColor;
  private Font labelFont;
  //
  public String style;
  public static final String styleBox = "Box", styleMin = "Min", styleMax = "Max";
  static final String styleNone = "None";
  //
  public float[] start; // start of range on an axis (Subject space)
  public float[] stop; // stop of range on axis (Subject space)
  // original unzoomed start for unzoomed (User space)
  public float[] unzoomedStart;
  // original unzoomed stop for unzoomed (User space)
  public float[] unzoomedStop;
  public float[] ticSpace; // distance between major tics
  public float[] unzoomedTicSpace;
  public int[] numSubTics; // number of minor tics between major ones
  public float ticLengthMM, ticLengthSubject, lineWidthMM, lineWidthSubject;
  public Tic[][] tics;
  public boolean drawGrid = false;
  public float[][] bAxes; // before transformation (Subject space)
  public float[][] tAxes; // after transformation (Screen space)
  public Color color;
  public float[] tOrigin;
  public static boolean drawTics; // false when in non-orthogonal zoom
  private final float dotsPerMM = java.awt.Toolkit.getDefaultToolkit()
      .getScreenResolution() / 25.4f;

  public Axes(final Session session) {
    super(session);
    //System.out.println("CREATED AXES " + hashCode() + this);
    name = "";
    start = new float[3];
    stop = new float[3];
    unzoomedStart = new float[3];
    unzoomedStop = new float[3];
    ticSpace = new float[3];
    unzoomedTicSpace = new float[3];
    numSubTics = new int[3];
    bAxes = new float[8][3];
    tAxes = new float[8][3];
    tOrigin = new float[3];
    labels = new Text[3];
    tics = new Tic[3][];
    name = "axes" + hashCode();
    drawTics = true;
    // System.out.println("CREATED AXES " + hashCode());
  }

  public void initialize() {
    session.plotManager.addPlotManagerListener(this);
    start[0] = -session.plotManager.subject.cumDelta[0];
    start[1] = -session.plotManager.subject.cumDelta[1];
    start[2] = -session.plotManager.subject.cumDelta[2];
    stop[0] = 10.0f - session.plotManager.subject.cumDelta[0];
    stop[1] = 10.0f - session.plotManager.subject.cumDelta[1];
    stop[2] = 10.0f - session.plotManager.subject.cumDelta[2];
    unzoomedStart[0] = 0.0f;
    unzoomedStart[1] = 0.0f;
    unzoomedStart[2] = 0.0f;
    unzoomedStop[0] = 10.0f;
    unzoomedStop[1] = 10.0f;
    unzoomedStop[2] = 10.0f;
    ticSpace[0] = 2.0f;
    ticSpace[1] = 2.0f;
    ticSpace[2] = 2.0f;
    unzoomedTicSpace[0] = 2.0f;
    unzoomedTicSpace[1] = 2.0f;
    unzoomedTicSpace[2] = 2.0f;
    numSubTics[0] = 4;
    numSubTics[1] = 4;
    numSubTics[2] = 4;
    ticLengthMM = 4.0f;
    lineWidthMM = 0.5f;
    tOrigin[0] = 0.0f;
    tOrigin[1] = 0.0f;
    tOrigin[2] = 0.0f;
    drawTics = true;
    style = styleBox;
    labelFont = new Font("Monospaced", Font.PLAIN, 12); // default
    labelColor = Palette.Choice.lightYellow.getColor();
    clearTics();
    for (int axis = 0; axis < session.plotManager.getDim(); axis++) {
      if (labels[axis] != null) {
        session.plotManager.removeText(labels[axis]);
      }
      final Text text = new Text(session);
      text.initialize();
      labels[axis] = text;
      labels[axis].color = labelColor;
      labels[axis].font = labelFont;
      // labels[axis].setSingletons();
      // System.out.println("labels[axis]: "+labels[axis]);
      session.plotManager.addText(labels[axis]);
    }
    color = Palette.Choice.white.getColor();
    buildAxes();
    createTics();
    buildLabelsAndTics();
  }

  public void clearReferences() {
    int i;
    clearTics();
    for (int axis = 0; axis < session.plotManager.getDim(); axis++) {
      session.plotManager.removeText(labels[axis]);
      for (i = 0; i < tics[axis].length; i++) {
        session.plotManager.removeText(tics[axis][i].label);
      }
    }
  }

  public Color getLabelColor() {
    // tics need this
    return labelColor;
  }

  public Font getLabelFont() {
    // tics need this
    return labelFont;
  }

  /**
   * Given the ranges for the axes, build the axes by specifying the coordinates
   * of the corners in either 2D or 3D.
   */
  public void buildAxes() {
    switch (session.plotManager.getDim()) {
    case 2:
      bAxes[0][0] = start[0]; // (0,0)
      bAxes[0][1] = start[1];
      bAxes[1][0] = stop[0]; // (x,0)
      bAxes[1][1] = start[1];
      bAxes[2][0] = stop[0]; // (x,y)
      bAxes[2][1] = stop[1];
      bAxes[3][0] = start[0]; // (0,y)
      bAxes[3][1] = stop[1];
      break;
    case 3:
      bAxes[0][0] = start[0]; // (0,0,0)
      bAxes[0][1] = start[1];
      bAxes[0][2] = start[2];
      bAxes[1][0] = start[0]; // (0,0,z)
      bAxes[1][1] = start[1];
      bAxes[1][2] = stop[2];
      bAxes[2][0] = start[0]; // (0,y,0)
      bAxes[2][1] = stop[1];
      bAxes[2][2] = start[2];
      bAxes[3][0] = start[0]; // (0,y,z)
      bAxes[3][1] = stop[1];
      bAxes[3][2] = stop[2];
      bAxes[4][0] = stop[0]; // (x,0,0)
      bAxes[4][1] = start[1];
      bAxes[4][2] = start[2];
      bAxes[5][0] = stop[0]; // (x,0,z)
      bAxes[5][1] = start[1];
      bAxes[5][2] = stop[2];
      bAxes[6][0] = stop[0]; // (x,y,0)
      bAxes[6][1] = stop[1];
      bAxes[6][2] = start[2];
      bAxes[7][0] = stop[0]; // (x,y,z)
      bAxes[7][1] = stop[1];
      bAxes[7][2] = stop[2];
      break;
    default:
      throw new RuntimeException("bad dim 455");
    }
  }

  /**
   * Based on the current axes and their corresponding tic spacings, construct
   * tics and add them to the text list.
   */
  public void createTics() {
    int i;
    float j;
    for (int axis = 0; axis < session.plotManager.getDim(); axis++) {
      j = Math.abs((stop[axis] - start[axis]) / ticSpace[axis]);
      i = Math.round(j);
      tics[axis] = new Tic[i];
      for (i = 0; i < tics[axis].length; i++) {
        final Tic tic = new Tic(session, this, axis, i);
        tic.initialize();
        tics[axis][i] = tic;
        final Text text = new Text(session);
        text.initialize();
        tics[axis][i].label = text;
        // System.out.println("tics[axis][i].label: "+tics[axis][i].label);
        session.plotManager.addText(tics[axis][i].label);
      }
    }
  }

  /**
   * Removes Tic label from text list, too.
   */
  public void clearTics() {
    int i;
    if (tics != null/* && session.plotManager.textList != null */) {
      for (int axis = 0; axis < session.plotManager.getDim(); axis++) {
        if (tics[axis] != null) {
          for (i = 0; i < tics[axis].length; i++) {
            session.plotManager.removeText(tics[axis][i].label);
          }
        }
        tics[axis] = null;
      }
    }
  }

  /**
   * assumes that the model extents for all data have been calculated already
   */
  @Override
  public void zoom(final int loX, final int hiX, final int loY, final int hiY) {
    // System.out.println("ZOOM!");
    // Pairs of loX, hiX, loY, hiY are the corners of the rbb in screen space
    // which may be in any orientation, so we don't know which point is which
    // yet. Determine this.
    switch (session.plotManager.getDim()) {
    case 2: {
      final float[] unT0 = { loX, loY }; // 0,0
      final float[] unT1 = { hiX, loY }; // x,0
      final float[] unT2 = { hiX, hiY }; // x,y
      final float[] unT3 = { loX, hiY }; // 0,y
      // Find the coordinates in model space
      final float[][] t = new float[4][2];
      t[0] = session.plotManager.view.unTransform2D(unT0);
      t[1] = session.plotManager.view.unTransform2D(unT1);
      t[2] = session.plotManager.view.unTransform2D(unT2);
      t[3] = session.plotManager.view.unTransform2D(unT3);
      // build the untransformed axes
      if (session.plotManager.view.getOrthogonalMode()) {
        // Find a cutoff between lo and hi X
        int i, nLoX_indexes = 0, nHiX_indexes = 0;
        float cutoff, lowest = Float.POSITIVE_INFINITY, highest = Float.NEGATIVE_INFINITY;
        final int[] loX_indexes = new int[4], hiX_indexes = new int[4];
        int loY_index, hiY_index;
        for (i = 0; i < 4; i++) { // find a cutoff
          if (t[i][0] > highest) {
            highest = t[i][0];
          }
          if (t[i][0] < lowest) {
            lowest = t[i][0];
          }
        }
        cutoff = (highest + lowest) / 2.0f; // between hi and lo x
        for (i = 0; i < 4; i++) {
          if (t[i][0] < cutoff) {
            loX_indexes[nLoX_indexes] = i;
            nLoX_indexes++;
          }
          else if (t[i][0] > cutoff) {
            hiX_indexes[nHiX_indexes] = i;
            nHiX_indexes++;
          }
          else {
            throw new RuntimeException("bad cutoff");
          }
        }
        // Do the assignments now
        if (t[loX_indexes[0]][1] < t[loX_indexes[1]][1]) {
          loY_index = loX_indexes[0];
          hiY_index = loX_indexes[1];
        }
        else if (t[loX_indexes[0]][1] > t[loX_indexes[1]][1]) {
          loY_index = loX_indexes[1];
          hiY_index = loX_indexes[0];
        }
        else {
          throw new RuntimeException("bad cutoff");
        }
        bAxes[0][0] = t[loY_index][0]; // (0,0)
        bAxes[0][1] = t[loY_index][1];
        bAxes[3][0] = t[hiY_index][0]; // (0,y)
        bAxes[3][1] = t[hiY_index][1];
        if (t[hiX_indexes[0]][1] < t[hiX_indexes[1]][1]) {
          loY_index = hiX_indexes[0];
          hiY_index = hiX_indexes[1];
        }
        else if (t[hiX_indexes[0]][1] > t[hiX_indexes[1]][1]) {
          loY_index = hiX_indexes[1];
          hiY_index = hiX_indexes[0];
        }
        else {
          throw new RuntimeException("bad cutoff");
        }
        bAxes[1][0] = t[loY_index][0]; // (x,0)
        bAxes[1][1] = t[loY_index][1];
        bAxes[2][0] = t[hiY_index][0]; // (x,y)
        bAxes[2][1] = t[hiY_index][1];
      }
      else {
        bAxes[0][0] = t[0][0]; // (0,0)
        bAxes[0][1] = t[0][1];
        bAxes[1][0] = t[1][0]; // (x,0)
        bAxes[1][1] = t[1][1];
        bAxes[2][0] = t[2][0]; // (x,y)
        bAxes[2][1] = t[2][1];
        bAxes[3][0] = t[3][0]; // (0,y)
        bAxes[3][1] = t[3][1];
      }
    }
      break;
    case 3: {
      final float[][] unT = new float[8][3];
      // construct a 3D rubber band box in screen space
      unT[0][0] = loX;
      unT[0][1] = loY;
      unT[0][2] = tMin[2]; // 0,0,0
      unT[1][0] = loX;
      unT[1][1] = loY;
      unT[1][2] = tMax[2]; // 0,0,z
      unT[2][0] = loX;
      unT[2][1] = hiY;
      unT[2][2] = tMin[2]; // 0,y,0
      unT[3][0] = loX;
      unT[3][1] = hiY;
      unT[3][2] = tMax[2]; // 0,y,z
      unT[4][0] = hiX;
      unT[4][1] = loY;
      unT[4][2] = tMin[2]; // x,0,0
      unT[5][0] = hiX;
      unT[5][1] = loY;
      unT[5][2] = tMax[2]; // x,0,z
      unT[6][0] = hiX;
      unT[6][1] = hiY;
      unT[6][2] = tMin[2]; // x,y,0
      unT[7][0] = hiX;
      unT[7][1] = hiY;
      unT[7][2] = tMax[2]; // x,y,z
      // System.out.println("0,0,0: "+unT0[0]+","+unT0[1]+","+unT0[2]);
      // System.out.println("0,0,z: "+unT1[0]+","+unT1[1]+","+unT1[2]);
      // System.out.println("0,y,0: "+unT2[0]+","+unT2[1]+","+unT2[2]);
      // System.out.println("0,y,z: "+unT3[0]+","+unT3[1]+","+unT3[2]);
      // System.out.println("x,0,0: "+unT4[0]+","+unT4[1]+","+unT4[2]);
      // System.out.println("x,0,z: "+unT5[0]+","+unT5[1]+","+unT5[2]);
      // System.out.println("x,y,0: "+unT6[0]+","+unT6[1]+","+unT6[2]);
      // System.out.println("x,y,z: "+unT7[0]+","+unT7[1]+","+unT7[2]);
      final float[][] t = new float[8][3];
      // Transform the rbb screen coordinates back to subject space
      t[0] = session.plotManager.view.unTransform3D(unT[0]);
      t[1] = session.plotManager.view.unTransform3D(unT[1]);
      t[2] = session.plotManager.view.unTransform3D(unT[2]);
      t[3] = session.plotManager.view.unTransform3D(unT[3]);
      t[4] = session.plotManager.view.unTransform3D(unT[4]);
      t[5] = session.plotManager.view.unTransform3D(unT[5]);
      t[6] = session.plotManager.view.unTransform3D(unT[6]);
      t[7] = session.plotManager.view.unTransform3D(unT[7]);
      // System.out.println("\nt[0]: "+t[0][0]+","+t[0][1]+","+t[0][2]);
      // System.out.println("t[1]: "+t[1][0]+","+t[1][1]+","+t[1][2]);
      // System.out.println("t[2]: "+t[2][0]+","+t[2][1]+","+t[2][2]);
      // System.out.println("t[3]: "+t[3][0]+","+t[3][1]+","+t[3][2]);
      // System.out.println("t[4]: "+t[4][0]+","+t[4][1]+","+t[4][2]);
      // System.out.println("t[5]: "+t[5][0]+","+t[5][1]+","+t[5][2]);
      // System.out.println("t[6]: "+t[6][0]+","+t[6][1]+","+t[6][2]);
      // System.out.println("t[7]: "+t[7][0]+","+t[7][1]+","+t[7][2]);
      // ATP, we have eight numbers that form a box, but we don't know how they
      // map to the axes, so determine that.
      if (session.plotManager.view.getOrthogonalMode()) {
        int i, nLoX_indexes = 0, nHiX_indexes = 0, nLoY_loX_indexes = 0, nHiY_loX_indexes = 0, nLoY_hiX_indexes = 0, nHiY_hiX_indexes = 0;
        float cutoff, lowest = Float.POSITIVE_INFINITY, highest = Float.NEGATIVE_INFINITY;
        final int[] loX_indexes = new int[4], hiX_indexes = new int[4];
        final int[] loY_loX_indexes = new int[2], hiY_loX_indexes = new int[2];
        final int[] loY_hiX_indexes = new int[2], hiY_hiX_indexes = new int[2];
        int loZ_index, hiZ_index;
        for (i = 0; i < 8; i++) { // find a cutoff
          if (t[i][0] > highest) {
            highest = t[i][0];
          }
          if (t[i][0] < lowest) {
            lowest = t[i][0];
          }
        }
        cutoff = (highest + lowest) / 2.0f; // between hi and lo x
        // System.out.println("LOWEST: "+lowest+" CUTOFF: "+cutoff+" HIGHEST:
        // "+highest);
        for (i = 0; i < 8; i++) {
          if (t[i][0] < cutoff) {
            loX_indexes[nLoX_indexes] = i;
            nLoX_indexes++;
          }
          else if (t[i][0] > cutoff) {
            hiX_indexes[nHiX_indexes] = i;
            nHiX_indexes++;
          }
          else {
            throw new RuntimeException("bad cutoff");
          }
        }
        // System.out.println("loX_indexes: "+" "+loX_indexes[0]+"
        // "+loX_indexes[1]+" "+loX_indexes[2]+" "+loX_indexes[3]);
        // System.out.println("hiX_indexes: "+" "+hiX_indexes[0]+"
        // "+hiX_indexes[1]+" "+hiX_indexes[2]+" "+hiX_indexes[3]);
        // get low y and high y for the low x indices
        lowest = Float.POSITIVE_INFINITY;
        highest = Float.NEGATIVE_INFINITY;
        for (i = 0; i < 4; i++) {
          if (t[loX_indexes[i]][1] > highest) {
            highest = t[loX_indexes[i]][1];
          }
          if (t[loX_indexes[i]][1] < lowest) {
            lowest = t[loX_indexes[i]][1];
          }
        }
        cutoff = (highest + lowest) / 2.0f; // between hi and low y
        for (i = 0; i < 4; i++) {
          if (t[loX_indexes[i]][1] > cutoff) {
            hiY_loX_indexes[nHiY_loX_indexes] = loX_indexes[i];
            nHiY_loX_indexes++;
          }
          else if (t[loX_indexes[i]][1] < cutoff) {
            loY_loX_indexes[nLoY_loX_indexes] = loX_indexes[i];
            nLoY_loX_indexes++;
          }
          else {
            throw new RuntimeException("bad cutoff");
          }
        }
        // System.out.println("loY_loX_indexes: "+loY_loX_indexes[0]+"
        // "+loY_loX_indexes[1]);
        // System.out.println("hiY_loX_indexes: "+hiY_loX_indexes[0]+"
        // "+hiY_loX_indexes[1]);
        // Do the high x indices
        lowest = Float.POSITIVE_INFINITY;
        highest = Float.NEGATIVE_INFINITY;
        for (i = 0; i < 4; i++) {
          if (t[hiX_indexes[i]][1] > highest) {
            highest = t[hiX_indexes[i]][1];
          }
          if (t[hiX_indexes[i]][1] < lowest) {
            lowest = t[hiX_indexes[i]][1];
          }
        }
        cutoff = (highest + lowest) / 2.0f; // between hi and low y
        for (i = 0; i < 4; i++) {
          if (t[hiX_indexes[i]][1] > cutoff) {
            hiY_hiX_indexes[nHiY_hiX_indexes] = hiX_indexes[i];
            nHiY_hiX_indexes++;
          }
          else if (t[hiX_indexes[i]][1] < cutoff) {
            loY_hiX_indexes[nLoY_hiX_indexes] = hiX_indexes[i];
            nLoY_hiX_indexes++;
          }
          else {
            throw new RuntimeException("bad cutoff");
          }
        }
        // System.out.println("loY_hiX_indexes: "+loY_hiX_indexes[0]+"
        // "+loY_hiX_indexes[1]);
        // System.out.println("hiY_hiX_indexes: "+hiY_hiX_indexes[0]+"
        // "+hiY_hiX_indexes[1]);
        // Do the assignments now
        if (t[loY_loX_indexes[0]][2] < t[loY_loX_indexes[1]][2]) {
          loZ_index = loY_loX_indexes[0];
          hiZ_index = loY_loX_indexes[1];
        }
        else if (t[loY_loX_indexes[0]][2] > t[loY_loX_indexes[1]][2]) {
          loZ_index = loY_loX_indexes[1];
          hiZ_index = loY_loX_indexes[0];
        }
        else {
          throw new RuntimeException("bad cutoff");
        }
        bAxes[0][0] = t[loZ_index][0]; // (0,0,0)
        bAxes[0][1] = t[loZ_index][1];
        bAxes[0][2] = t[loZ_index][2];
        bAxes[1][0] = t[hiZ_index][0]; // (0,0,z)
        bAxes[1][1] = t[hiZ_index][1];
        bAxes[1][2] = t[hiZ_index][2];
        // System.out.println("0,0,0: " + loZ_index);
        // System.out.println("0,0,z: " + hiZ_index);
        if (t[hiY_loX_indexes[0]][2] < t[hiY_loX_indexes[1]][2]) {
          loZ_index = hiY_loX_indexes[0];
          hiZ_index = hiY_loX_indexes[1];
        }
        else if (t[hiY_loX_indexes[0]][2] > t[hiY_loX_indexes[1]][2]) {
          loZ_index = hiY_loX_indexes[1];
          hiZ_index = hiY_loX_indexes[0];
        }
        else {
          throw new RuntimeException("bad cutoff");
        }
        bAxes[2][0] = t[loZ_index][0]; // (0,y,0)
        bAxes[2][1] = t[loZ_index][1];
        bAxes[2][2] = t[loZ_index][2];
        bAxes[3][0] = t[hiZ_index][0]; // (0,y,z)
        bAxes[3][1] = t[hiZ_index][1];
        bAxes[3][2] = t[hiZ_index][2];
        // System.out.println("0,y,0: "+loZ_index);
        // System.out.println("0,y,z: "+hiZ_index);
        if (t[loY_hiX_indexes[0]][2] < t[loY_hiX_indexes[1]][2]) {
          loZ_index = loY_hiX_indexes[0];
          hiZ_index = loY_hiX_indexes[1];
        }
        else if (t[loY_hiX_indexes[0]][2] > t[loY_hiX_indexes[1]][2]) {
          loZ_index = loY_hiX_indexes[1];
          hiZ_index = loY_hiX_indexes[0];
        }
        else {
          throw new RuntimeException("bad cutoff");
        }
        bAxes[4][0] = t[loZ_index][0]; // (x,0,0)
        bAxes[4][1] = t[loZ_index][1];
        bAxes[4][2] = t[loZ_index][2];
        bAxes[5][0] = t[hiZ_index][0]; // (x,0,z)
        bAxes[5][1] = t[hiZ_index][1];
        bAxes[5][2] = t[hiZ_index][2];
        // System.out.println("x,0,0: "+loZ_index);
        // System.out.println("x,0,0: "+hiZ_index);
        if (t[hiY_hiX_indexes[0]][2] < t[hiY_hiX_indexes[1]][2]) {
          loZ_index = hiY_hiX_indexes[0];
          hiZ_index = hiY_hiX_indexes[1];
        }
        else if (t[hiY_hiX_indexes[0]][2] > t[hiY_hiX_indexes[1]][2]) {
          loZ_index = hiY_hiX_indexes[1];
          hiZ_index = hiY_hiX_indexes[0];
        }
        else {
          throw new RuntimeException("bad cutoff");
        }
        bAxes[6][0] = t[loZ_index][0]; // (x,y,0)
        bAxes[6][1] = t[loZ_index][1];
        bAxes[6][2] = t[loZ_index][2];
        bAxes[7][0] = t[hiZ_index][0]; // (x,y,z)
        bAxes[7][1] = t[hiZ_index][1];
        bAxes[7][2] = t[hiZ_index][2];
        // System.out.println("x,y,z: "+loZ_index);
        // System.out.println("x,y,z: "+hiZ_index);
      }
      else {
        bAxes[0][0] = t[0][0]; // (0,0,0)
        bAxes[0][1] = t[0][1];
        bAxes[0][2] = t[0][2];
        bAxes[1][0] = t[1][0]; // (0,0,z)
        bAxes[1][1] = t[1][1];
        bAxes[1][2] = t[1][2];
        bAxes[2][0] = t[2][0]; // (0,y,0)
        bAxes[2][1] = t[2][1];
        bAxes[2][2] = t[2][2];
        bAxes[3][0] = t[3][0]; // (0,y,z)
        bAxes[3][1] = t[3][1];
        bAxes[3][2] = t[3][2];
        bAxes[4][0] = t[4][0]; // (x,0,0)
        bAxes[4][1] = t[4][1];
        bAxes[4][2] = t[4][2];
        bAxes[5][0] = t[5][0]; // (x,0,z)
        bAxes[5][1] = t[5][1];
        bAxes[5][2] = t[5][2];
        bAxes[6][0] = t[6][0]; // (x,y,0)
        bAxes[6][1] = t[6][1];
        bAxes[6][2] = t[6][2];
        bAxes[7][0] = t[7][0]; // (x,y,z)
        bAxes[7][1] = t[7][1];
        bAxes[7][2] = t[7][2];
      }
    } // case 3
      break;
    default:
      throw new RuntimeException("bad dim");
    }
    drawTics = !session.plotManager.view.zoomed
        || session.plotManager.view.getOrthogonalMode()
        && session.plotManager.view.zoomed;
    buildLabelsAndTics(); // calculateSubjectExtents() is called in rebuild()
  }

  /**
   * Called after the view parameters are changed. The subject coordinates of
   * the axes and tics and labels are modified such that the "screen"
   * measurements (line width, tic length, etc.) remain constant AFTER
   * transformation from screen space.
   */
  public void buildLabelsAndTics() { // FOR AXES
    final float[] v = new float[3];
    float d, screenToSubjectFactor = 0.0f;
    switch (session.plotManager.getDim()) {
    case 2:
      if (drawTics) {
        screenToSubjectFactor = dotsPerMM / session.plotManager.view.focal;
        ticLengthSubject = ticLengthMM * screenToSubjectFactor;
        lineWidthSubject = lineWidthMM * screenToSubjectFactor;
        // construct the tics on both axes
        v[0] = bAxes[1][0] - bAxes[0][0];
        v[1] = bAxes[1][1] - bAxes[0][1];
        d = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1]);
        if (session.plotManager.view.zoomed) {
          ticSpace[0] = d / tics[X_AXIS].length;
        }
        ticLengthSubject = d < ticLengthSubject ? d : ticLengthSubject;
        lineWidthSubject = d < lineWidthSubject ? d : lineWidthSubject;
        v[0] = bAxes[3][0] - bAxes[0][0];
        v[1] = bAxes[3][1] - bAxes[0][1];
        d = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1]);
        if (session.plotManager.view.zoomed) {
          ticSpace[1] = d / tics[Y_AXIS].length;
        }
        ticLengthSubject = d < ticLengthSubject ? d : ticLengthSubject;
        lineWidthSubject = d < lineWidthSubject ? d : lineWidthSubject;
      }
      break;
    case 3:
      // We must adjust the line width and tic length SUCH THAT they will
      // appear the correct length ON THE SCREEN after transformation at z=0.0f.
      screenToSubjectFactor = -session.plotManager.view.cop * dotsPerMM
          / session.plotManager.view.focal;
      lineWidthSubject = lineWidthMM * screenToSubjectFactor;
      if (drawTics) {
        ticLengthSubject = ticLengthMM * screenToSubjectFactor;
        // Construct the tics on the axes.
        // First calculate the new tic spacing
        v[0] = bAxes[4][0] - bAxes[0][0];
        v[1] = bAxes[4][1] - bAxes[0][1];
        v[2] = bAxes[4][2] - bAxes[0][2];
        d = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        if (session.plotManager.view.zoomed) {
          ticSpace[0] = d / tics[X_AXIS].length;
        }
        // Note: at very small focal length values, the tic marks and lineThickness
        // will overwhelm the plot and lead to pathological behavior.
        ticLengthSubject = d < ticLengthSubject ? d : ticLengthSubject;
        lineWidthSubject = d < lineWidthSubject ? d : lineWidthSubject;
        v[0] = bAxes[2][0] - bAxes[0][0];
        v[1] = bAxes[2][1] - bAxes[0][1];
        v[2] = bAxes[2][2] - bAxes[0][2];
        d = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        if (session.plotManager.view.zoomed) {
          ticSpace[1] = d / tics[Y_AXIS].length;
        }
        ticLengthSubject = d < ticLengthSubject ? d : ticLengthSubject;
        lineWidthSubject = d < lineWidthSubject ? d : lineWidthSubject;
        v[0] = bAxes[1][0] - bAxes[0][0];
        v[1] = bAxes[1][1] - bAxes[0][1];
        v[2] = bAxes[1][2] - bAxes[0][2];
        d = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        // System.out.println("AXIS UNIT V: "+v[0]/d+" "+v[1]/d+" "+v[2]/d);
        if (session.plotManager.view.zoomed) {
          ticSpace[2] = d / tics[Z_AXIS].length;
        }
        ticLengthSubject = d < ticLengthSubject ? d : ticLengthSubject;
        lineWidthSubject = d < lineWidthSubject ? d : lineWidthSubject;
      }
      break;
    default:
      throw new RuntimeException("Dimensionality is invalid");
    }
    // The labels for axes and tics need to be rebuilt on a case by case basis
    // since the user may have positioned the label.
    int i;
    for (int axis = 0; axis < session.plotManager.getDim(); axis++) {
      rebuildLabelForAxis(axis, screenToSubjectFactor);
      for (i = 0; i < tics[axis].length; i++) {
        tics[axis][i].buildLabelsAndTics(screenToSubjectFactor);
      }
    }
    calculateSubjectExtents();
  }

  /**
   * Draw 2d axes of a specific style
   */
  @Override
  public void draw2D(final Slate slate, final boolean unused) {
    if (!visible) {
      return;
    }
    if (style.equals(styleBox)) {
      drawFullBox2D(slate);
    }
    else if (style.equals(styleMin)) {
      drawFromMin2D(slate);
    }
    else if (style.equals(styleMax)) {
      drawFromMax2D(slate);
    }
    else {
      throw new RuntimeException(style + " style unrecognized");
    }
  }

  /**
   * Draw 3d axes of a specific style
   */
  @Override
  public void draw3D(final Slate slate, final boolean unused) {
//System.out.println("DRAW AXES");
    if (!visible) {
      return;
    }
    if (style.equals(styleBox)) {
      drawFullBox3D(slate);
    }
    else if (style.equals(styleMin)) {
      drawFromMin3D(slate);
    }
    else if (style.equals(styleMax)) {
      drawFromMax3D(slate);
    }
    else {
      throw new RuntimeException(style + " style unrecognized");
    }
  }

  public void deleteLabel(final Text text) {
    int i;
    for (int axis = 0; axis < session.plotManager.getDim(); axis++) {
      if (labels[axis] == text) {
        labels[axis] = null;
        return;
      }
      for (i = 0; i < tics[axis].length; i++) {
        if (tics[axis][i].label == text) {
          tics[axis][i].label = null;
          return;
        }
      }
    }
  }

  void calculateLabelOffsetLengthMM(final Text text) {
    int i, a;
    final float[] v = new float[3];
    float[] f;
    for (int axis = 0; axis < session.plotManager.getDim(); axis++) {
      if (labels[axis] == text) {
        switch (session.plotManager.getDim()) {
        case 2:
          if (axis == X_AXIS) {
            a = 1;
          }
          else if (axis == Y_AXIS) {
            a = 3;
          }
          else {
            throw new RuntimeException("axes abuse");
          }
          f = calculateAnchorPoint(a, labels[axis].start[0],
              labels[axis].start[1], labels[axis].start[2]);
          // Find the vector that numbers from anchor to the label
          v[0] = labels[axis].start[0] - f[0];
          v[1] = labels[axis].start[1] - f[1];
          text.labelOffsetLengthMM = (float) Math.sqrt(v[0] * v[0] + v[1]
              * v[1])
              / (dotsPerMM / session.plotManager.view.focal);
          break;
        case 3:
          if (axis == X_AXIS) {
            a = 4;
          }
          else if (axis == Y_AXIS) {
            a = 2;
          }
          else if (axis == Z_AXIS) {
            a = 1;
          }
          else {
            throw new RuntimeException("axes abuse");
          }
          f = calculateAnchorPoint(a, labels[axis].start[0],
              labels[axis].start[1], labels[axis].start[2]);
          // Find the vector that numbers from anchor to the label
          v[0] = labels[axis].start[0] - f[0];
          v[1] = labels[axis].start[1] - f[1];
          v[2] = labels[axis].start[2] - f[2];
          text.labelOffsetLengthMM = (float) Math.sqrt(v[0] * v[0] + v[1]
              * v[1] + v[2] * v[2])
              / (-session.plotManager.view.cop * dotsPerMM / session.plotManager.view.focal);
          break;
        default:
          throw new RuntimeException("bad dim");
        }
        return;
      }
      for (i = 0; i < tics[axis].length; i++) {
        if (tics[axis][i].label == text) {
          switch (session.plotManager.getDim()) {
          case 2:
            // f = tics[axis][i].ticStart;
            // Find the vector that numbers from anchor to the label
            v[0] = tics[axis][i].label.start[0] - tics[axis][i].ticStart[0];
            v[1] = tics[axis][i].label.start[1] - tics[axis][i].ticStart[1];
            text.labelOffsetLengthMM = (float) Math.sqrt(v[0] * v[0] + v[1]
                * v[1])
                / (dotsPerMM / session.plotManager.view.focal);
            break;
          case 3:
            f = tics[axis][i].ticStart;
            // Find the vector that numbers from anchor to the label
            v[0] = tics[axis][i].label.start[0] - tics[axis][i].ticStart[0];
            v[1] = tics[axis][i].label.start[1] - tics[axis][i].ticStart[1];
            v[2] = tics[axis][i].label.start[2] - tics[axis][i].ticStart[2];
            text.labelOffsetLengthMM = (float) Math.sqrt(v[0] * v[0] + v[1]
                * v[1] + v[2] * v[2])
                / (-session.plotManager.view.cop * dotsPerMM / session.plotManager.view.focal);
            break;
          default:
            throw new RuntimeException("bad dim");
          }
          return;
        }
      }
    }
  }

  private void rebuildLabelForAxis(final int axis,
      final float screenToSubjectFactor) {
    if (labels[axis] == null || !labels[axis].moving) {
      return;
    } // user may have deleted or frozen this label
    float len;
    final float[] v = new float[3];
    float[] f = new float[3];
    final float[] f1 = new float[3], f2 = new float[3];
    int a, a1, a2;
    // Let's figure out where we are going to put the axis labels
    switch (session.plotManager.getDim()) {
    case 2:
      if (axis == X_AXIS) {
        a1 = 1;
        a2 = 3;
      }
      else if (axis == Y_AXIS) {
        a1 = 3;
        a2 = 1;
      }
      else {
        throw new RuntimeException("bad axes");
      }
      if (session.plotManager.view.zoomed
          || labels[axis].labelOffsetLengthMM < 0.0f) {
        // Calculate the axis label placement by using the axes
        // to create a unit vector in the reverse direction
        f[0] = bAxes[a2][0] - bAxes[0][0];
        f[1] = bAxes[a2][1] - bAxes[0][1];
        len = (float) Math.sqrt(f[0] * f[0] + f[1] * f[1]);
        f[0] /= len;
        f[1] /= len;
        v[0] = -3.0f * f[0];
        v[1] = -3.0f * f[1];
        v[2] = -3.0f * f[2];
        // Position the label at the end of the axis offset a little bit
        labels[axis].start[0] = bAxes[a1][0] + ticLengthSubject * v[0];
        labels[axis].start[1] = bAxes[a1][1] + ticLengthSubject * v[1];
      }
      else {
        labels[axis].start[0] = labels[axis].unzoomedStart[0]
            - session.plotManager.subject.cumDelta[0];
        labels[axis].start[1] = labels[axis].unzoomedStart[1]
            - session.plotManager.subject.cumDelta[1];
        labels[axis].start[2] = labels[axis].unzoomedStart[2]
            - session.plotManager.subject.cumDelta[2];
        f = calculateAnchorPoint(a1, labels[axis].start[0],
            labels[axis].start[1], labels[axis].start[2]);
        // Find the unit vector that numbers from anchor to the label
        v[0] = labels[axis].start[0] - f[0];
        v[1] = labels[axis].start[1] - f[1];
        len = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1]);
        v[0] /= len;
        v[1] /= len;
        // Position the axis label offsetLengthMM away from the anchor
        labels[axis].start[0] = f[0] + labels[axis].labelOffsetLengthMM
            * screenToSubjectFactor * v[0];
        labels[axis].start[1] = f[1] + labels[axis].labelOffsetLengthMM
            * screenToSubjectFactor * v[1];
        labels[axis].unzoomedStart[0] = labels[axis].start[0]
            + session.plotManager.subject.cumDelta[0];
        labels[axis].unzoomedStart[1] = labels[axis].start[1]
            + session.plotManager.subject.cumDelta[1];
        labels[axis].unzoomedStart[2] = labels[axis].start[2]
            + session.plotManager.subject.cumDelta[2];
      }
      // if (!view.zoomed) {
      // labels[axis].unzoomedStart[0] = labels[axis].start[0];
      // labels[axis].unzoomedStart[1] = labels[axis].start[1];
      // }
      labels[axis].setString(pickAxisLabel(axis));
      break;
    case 3:
      final float sqrt2 = (float) Math.sqrt(2.0);
      if (axis == X_AXIS) {
        a = 4;
        a1 = 2;
        a2 = 1;
      }
      else if (axis == Y_AXIS) {
        a = 2;
        a1 = 4;
        a2 = 1;
      }
      else if (axis == Z_AXIS) {
        a = 1;
        a1 = 4;
        a2 = 2;
      }
      else {
        throw new RuntimeException("bad axes");
      }
      if (session.plotManager.view.zoomed
          || labels[axis].labelOffsetLengthMM < 0.0f) {
        // User has not positioned text, so we position it automatically
        // Create a vector that numbers out "diagonally"
        f1[0] = bAxes[a1][0] - bAxes[0][0];
        f1[1] = bAxes[a1][1] - bAxes[0][1];
        f1[2] = bAxes[a1][2] - bAxes[0][2];
        len = (float) Math.sqrt(f1[0] * f1[0] + f1[1] * f1[1] + f1[2] * f1[2]);
        f1[0] /= len;
        f1[1] /= len;
        f1[2] /= len;
        f2[0] = bAxes[a2][0] - bAxes[0][0];
        f2[1] = bAxes[a2][1] - bAxes[0][1];
        f2[2] = bAxes[a2][2] - bAxes[0][2];
        len = (float) Math.sqrt(f2[0] * f2[0] + f2[1] * f2[1] + f2[2] * f2[2]);
        f2[0] /= len;
        f2[1] /= len;
        f2[2] /= len;
        // Calculate the offset by using the unit vector of each axis
        // to create a unit vector in the reverse direction
        v[0] = -3.0f * (f1[0] + f2[0]) / sqrt2;
        v[1] = -3.0f * (f1[1] + f2[1]) / sqrt2;
        v[2] = -3.0f * (f1[2] + f2[2]) / sqrt2;
        labels[axis].start[0] = bAxes[a][0] + ticLengthSubject * v[0];
        labels[axis].start[1] = bAxes[a][1] + ticLengthSubject * v[1];
        labels[axis].start[2] = bAxes[a][2] + ticLengthSubject * v[2];
        /*
         * MIDDLE OF AXIS labels[axis].start[0] = (bAxes[a1][0] +
         * bAxes[0][0])/2.0f + ticLengthSubject * v[0]; labels[axis].start[1] =
         * (bAxes[a1][1] + bAxes[0][1])/2.0f + ticLengthSubject * v[1];
         * labels[axis].start[2] = (bAxes[a1][2] + bAxes[0][2])/2.0f +
         * ticLengthSubject * v[2];
         */
      }
      else {
        labels[axis].start[0] = labels[axis].unzoomedStart[0]
            - session.plotManager.subject.cumDelta[0];
        labels[axis].start[1] = labels[axis].unzoomedStart[1]
            - session.plotManager.subject.cumDelta[1];
        labels[axis].start[2] = labels[axis].unzoomedStart[2]
            - session.plotManager.subject.cumDelta[2];
        // System.out.println("BEFORE REBUILD LABEL start[0]: " +
        // labels[axis].start[0]);
        // System.out.println("BEFORE REBUILD LABEL start[1]: " +
        // labels[axis].start[1]);
        // System.out.println("BEFORE REBUILD LABEL start[2]: " +
        // labels[axis].start[2]);
        f = calculateAnchorPoint(a, labels[axis].start[0],
            labels[axis].start[1], labels[axis].start[2]);
        // System.out.println("ANCHOR: f[0]: " + f[0]);
        // System.out.println("ANCHOR: f[1]: " + f[1]);
        // System.out.println("ANCHOR: f[2]: " + f[2]);
        // Find the unit vector that numbers from anchor to the label
        v[0] = labels[axis].start[0] - f[0];
        v[1] = labels[axis].start[1] - f[1];
        v[2] = labels[axis].start[2] - f[2];
        len = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        v[0] /= len;
        v[1] /= len;
        v[2] /= len;
        // Position the axis label offsetLengthMM away from the anchor
        labels[axis].start[0] = f[0] + labels[axis].labelOffsetLengthMM
            * screenToSubjectFactor * v[0];
        labels[axis].start[1] = f[1] + labels[axis].labelOffsetLengthMM
            * screenToSubjectFactor * v[1];
        labels[axis].start[2] = f[2] + labels[axis].labelOffsetLengthMM
            * screenToSubjectFactor * v[2];
        labels[axis].unzoomedStart[0] = labels[axis].start[0]
            + session.plotManager.subject.cumDelta[0];
        labels[axis].unzoomedStart[1] = labels[axis].start[1]
            + session.plotManager.subject.cumDelta[1];
        labels[axis].unzoomedStart[2] = labels[axis].start[2]
            + session.plotManager.subject.cumDelta[2];
        // System.out.println("subject.cumDelta[0]: "+subject.cumDelta[0]);
        // System.out.println("subject.cumDelta[1]: "+subject.cumDelta[1]);
        // System.out.println("subject.cumDelta[2]: "+subject.cumDelta[2]);
        // System.out.println("AFTER REBUILD LABEL unzoomedStart[0]: " +
        // labels[axis].unzoomedStart[0]);
        // System.out.println("AFTER REBUILD LABEL unzoomedStart[1]: " +
        // labels[axis].unzoomedStart[1]);
        // System.out.println("AFTER REBUILD LABEL unzoomedStart[2]: " +
        // labels[axis].unzoomedStart[2]);
      }
      labels[axis].setString(pickAxisLabel(axis));
      break;
    default:
      throw new RuntimeException("bad dim");
    }
    labels[axis].makeImage();
    labels[axis].calculateSubjectExtents();
  }

  float[] calculateAnchorPoint(final int a, final float x, final float y,
      final float z) {
    float len;
    final float[] f = new float[3], f1 = new float[3], f2 = new float[3];
    switch (session.plotManager.getDim()) {
    case 2:
      // Make a vector of the axis
      f1[0] = bAxes[a][0] - bAxes[0][0];
      f1[1] = bAxes[a][1] - bAxes[0][1];
      // Make a vector of the current placement of the text
      f2[0] = x - bAxes[0][0];
      f2[1] = y - bAxes[0][1];
      // Find the projection, f, of f2 onto f1, which is f1 * (f2.f1)/(f1.f1)
      len = (f2[0] * f1[0] + f2[1] * f1[1]) / (f1[0] * f1[0] + f1[1] * f1[1]);
      f[0] = len * f1[0];
      f[1] = len * f1[1];
      // Convert f to coordinates which is the "anchor" point on the axis
      f[0] += bAxes[0][0];
      f[1] += bAxes[0][1];
      break;
    case 3:
      // Make a vector of the axis
      f1[0] = bAxes[a][0] - bAxes[0][0];
      f1[1] = bAxes[a][1] - bAxes[0][1];
      f1[2] = bAxes[a][2] - bAxes[0][2];
      // Make a vector of the current placement of the text
      f2[0] = x - bAxes[0][0];
      f2[1] = y - bAxes[0][1];
      f2[2] = z - bAxes[0][2];
      // Find the projection, f, of f2 onto f1, which is f1 * (f2.f1)/(f1.f1)
      len = (f2[0] * f1[0] + f2[1] * f1[1] + f2[2] * f1[2])
          / (f1[0] * f1[0] + f1[1] * f1[1] + f1[2] * f1[2]);
      f[0] = len * f1[0];
      f[1] = len * f1[1];
      f[2] = len * f1[2];
      // Convert f to coordinates which is the "anchor" point on the axis
      f[0] += bAxes[0][0];
      f[1] += bAxes[0][1];
      f[2] += bAxes[0][2];
      break;
    default:
      throw new RuntimeException("bad dim");
    }
    return f;
  }

  private String pickAxisLabel(final int axis) {
    if (session.plotManager.basis == null) {
      return axisNames[axis]+"-axis";
    }

    return session.plotManager.basis.getDescriptorForAxis(axis).getName();
  }

  @Override
  public void unzoom() {
    // Stay in Subject space
    switch (session.plotManager.getDim()) {
    case 2:
      start[0] = unzoomedStart[0] - session.plotManager.subject.cumDelta[0];
      start[1] = unzoomedStart[1] - session.plotManager.subject.cumDelta[1];
      stop[0] = unzoomedStop[0] - session.plotManager.subject.cumDelta[0];
      stop[1] = unzoomedStop[1] - session.plotManager.subject.cumDelta[1];
      ticSpace[0] = unzoomedTicSpace[0];
      ticSpace[1] = unzoomedTicSpace[1];
      // nTics[0] = Math.round((stop[0] - start[0]) / ticSpace[0]);
      // nTics[1] = Math.round((stop[1] - start[1]) / ticSpace[1]);
      break;
    case 3:
      start[0] = unzoomedStart[0] - session.plotManager.subject.cumDelta[0];
      start[1] = unzoomedStart[1] - session.plotManager.subject.cumDelta[1];
      start[2] = unzoomedStart[2] - session.plotManager.subject.cumDelta[2];
      stop[0] = unzoomedStop[0] - session.plotManager.subject.cumDelta[0];
      stop[1] = unzoomedStop[1] - session.plotManager.subject.cumDelta[1];
      stop[2] = unzoomedStop[2] - session.plotManager.subject.cumDelta[2];
      ticSpace[0] = unzoomedTicSpace[0];
      ticSpace[1] = unzoomedTicSpace[1];
      ticSpace[2] = unzoomedTicSpace[2];
      break;
    default:
      throw new RuntimeException("bad dim");
    }
    drawTics = true;
    buildAxes();
    // (tics are already created, just hidden)
    buildLabelsAndTics();
  }

  private void drawFullBox2D(final Slate slate) {
    transform2D(false); // does tics too
    slate.setColor(color);
    // draw tics first so gridline are behind axes
    drawAxis2D(slate, 0, 1);
    drawAxis2D(slate, 1, 2);
    drawAxis2D(slate, 2, 3);
    drawAxis2D(slate, 3, 0);
    if (drawTics) {
      for (int axis = 0; axis < session.plotManager.getDim(); axis++) {
        for (int i = 0; i < tics[axis].length; i++) {
          tics[axis][i].drawTic2D(slate);
        }
      }
    }
  }

  private void drawFromMin2D(final Slate slate) {
    transform2D(false); // does tics too
    slate.setColor(color);
    // draw tics first so gridlines are behind axes
    drawAxis2D(slate, 0, 1);
    drawAxis2D(slate, 3, 0);
    if (drawTics) {
      for (int axis = 0; axis < session.plotManager.getDim(); axis++) {
        for (int i = 0; i < tics[axis].length; i++) {
          tics[axis][i].drawTic2D(slate);
        }
      }
    }
  }

  private void drawFromMax2D(final Slate slate) {
    transform2D(false); // does tics too
    slate.setColor(color);
    /* TODO: put tic marks on max */
    drawAxis2D(slate, 1, 2);
    drawAxis2D(slate, 2, 3);
  }

  private void drawFullBox3D(final Slate slate) {
    transform3D(false); // does tics too
    slate.setColor(color);
    drawAxis3D(slate, 0, 1);
    drawAxis3D(slate, 1, 3);
    drawAxis3D(slate, 3, 2);
    drawAxis3D(slate, 2, 0);
    drawAxis3D(slate, 0, 4);
    drawAxis3D(slate, 1, 5);
    drawAxis3D(slate, 2, 6);
    drawAxis3D(slate, 3, 7);
    drawAxis3D(slate, 4, 5);
    drawAxis3D(slate, 5, 7);
    drawAxis3D(slate, 7, 6);
    drawAxis3D(slate, 6, 4);
    if (drawTics) {
      for (int axis = 0; axis < session.plotManager.getDim(); axis++) {
        for (int i = 0; i < tics[axis].length; i++) {
          tics[axis][i].drawTic3D(slate);
        }
      }
    }
  }

  private void drawFromMin3D(final Slate slate) {
    transform3D(false); // does tics too
    slate.setColor(color);
    drawAxis3D(slate, 0, 1);
    drawAxis3D(slate, 2, 0);
    drawAxis3D(slate, 0, 4);
    if (drawTics) {
      for (int axis = 0; axis < session.plotManager.getDim(); axis++) {
        for (int i = 0; i < tics[axis].length; i++) {
          tics[axis][i].drawTic3D(slate);
        }
      }
    }
  }

  private void drawFromMax3D(final Slate slate) {
    transform3D(false); // does tics too
    slate.setColor(color);
    drawAxis3D(slate, 3, 7);
    drawAxis3D(slate, 5, 7);
    drawAxis3D(slate, 7, 6);
  }

  private void drawAxis2D(final Slate slate, final int beg, final int end) {
    final int nSegments = 1;
    int i;
    float x1a;
    float x1b, y1a;
    float y1b, x2a;
    float x2b, y2a;
    float y2b;
    float m1, m2;
    final float f = session.plotManager.view.focal;
    final int[] polygonX = new int[5];
    final int[] polygonY = new int[5];
    // calculate the perpendicular 2D vector of displacement of linewidth
    final float alpha = (float) Math.atan2(tAxes[end][1] - tAxes[beg][1],
        tAxes[end][0] - tAxes[beg][0]);
    final float alphaA = alpha - (float) (Math.PI / 2.0);
    final float alphaB = alpha + (float) (Math.PI / 2.0);
    // LineWidth is in mm on the screen
    // How many model units make up a mm on the screen?
    // One mm is (dotsPerMM) pixels in view space.
    // There are (focal) pixels per model unit.
    final float halfWidth = lineWidthSubject / 2.0f;
    final float halfWidthXa = halfWidth * (float) Math.cos(alphaA);
    final float halfWidthYa = halfWidth * (float) Math.sin(alphaA);
    final float halfWidthXb = halfWidth * (float) Math.cos(alphaB);
    final float halfWidthYb = halfWidth * (float) Math.sin(alphaB);
    for (i = 0; i < nSegments; i++) {
      m1 = (float) i / (float) nSegments;
      x1a = (tAxes[end][0] - tAxes[beg][0]) * m1 + tAxes[beg][0] + halfWidthXa
          * f;
      y1a = (tAxes[end][1] - tAxes[beg][1]) * m1 + tAxes[beg][1] + halfWidthYa
          * f;
      x1b = (tAxes[end][0] - tAxes[beg][0]) * m1 + tAxes[beg][0] + halfWidthXb
          * f;
      y1b = (tAxes[end][1] - tAxes[beg][1]) * m1 + tAxes[beg][1] + halfWidthYb
          * f;
      m2 = (float) (i + 1) / (float) nSegments;
      x2a = (tAxes[end][0] - tAxes[beg][0]) * m2 + tAxes[beg][0] + halfWidthXa
          * f;
      y2a = (tAxes[end][1] - tAxes[beg][1]) * m2 + tAxes[beg][1] + halfWidthYa
          * f;
      x2b = (tAxes[end][0] - tAxes[beg][0]) * m2 + tAxes[beg][0] + halfWidthXb
          * f;
      y2b = (tAxes[end][1] - tAxes[beg][1]) * m2 + tAxes[beg][1] + halfWidthYb
          * f;
      polygonX[0] = (int) x1a;
      polygonY[0] = (int) y1a;
      polygonX[1] = (int) x1b;
      polygonY[1] = (int) y1b;
      polygonX[2] = (int) x2b;
      polygonY[2] = (int) y2b;
      polygonX[3] = (int) x2a;
      polygonY[3] = (int) y2a;
      polygonX[4] = (int) x1a;
      polygonY[4] = (int) y1a;
      slate.fillConvexQuad(polygonX, polygonY, 5);
    }
  }

  private void drawAxis3D(final Slate slate, final int beg, final int end) {
    final int nSegments = 1;
    int i;
    float f = 1.0f, x1a;
    float x1b, y1a;
    float y1b, x2a;
    float x2b, y2a;
    float y2b, zz1, zz2, m1, m2;
    final int[] polygonX = new int[5];
    final int[] polygonY = new int[5];
    final float[] polygonZ = new float[5];
    // calculate the perpendicular 2D vector of displacement of linewidth
    final float alpha = (float) Math.atan2(tAxes[end][1] - tAxes[beg][1],
        tAxes[end][0] - tAxes[beg][0]);
    final float alphaA = alpha - (float) (Math.PI / 2.0);
    final float alphaB = alpha + (float) (Math.PI / 2.0);
    final float halfWidth = lineWidthSubject / 2.0f; // in model units
    final float halfWidthXa = halfWidth * (float) Math.cos(alphaA);
    final float halfWidthYa = halfWidth * (float) Math.sin(alphaA);
    final float halfWidthXb = halfWidth * (float) Math.cos(alphaB);
    final float halfWidthYb = halfWidth * (float) Math.sin(alphaB);
    for (i = 0; i < nSegments; i++) {
      m1 = (float) i / (float) nSegments;
      zz1 = (tAxes[end][2] - tAxes[beg][2]) * m1 + tAxes[beg][2];
      f = session.plotManager.view.getPerspectiveFactor(zz1);
      x1a = (tAxes[end][0] - tAxes[beg][0]) * m1 + tAxes[beg][0] + halfWidthXa
          * f;
      y1a = (tAxes[end][1] - tAxes[beg][1]) * m1 + tAxes[beg][1] + halfWidthYa
          * f;
      x1b = (tAxes[end][0] - tAxes[beg][0]) * m1 + tAxes[beg][0] + halfWidthXb
          * f;
      y1b = (tAxes[end][1] - tAxes[beg][1]) * m1 + tAxes[beg][1] + halfWidthYb
          * f;
      m2 = (float) (i + 1) / (float) nSegments;
      zz2 = (tAxes[end][2] - tAxes[beg][2]) * m2 + tAxes[beg][2];
      f = session.plotManager.view.getPerspectiveFactor(zz2);
      x2a = (tAxes[end][0] - tAxes[beg][0]) * m2 + tAxes[beg][0] + halfWidthXa
          * f;
      y2a = (tAxes[end][1] - tAxes[beg][1]) * m2 + tAxes[beg][1] + halfWidthYa
          * f;
      x2b = (tAxes[end][0] - tAxes[beg][0]) * m2 + tAxes[beg][0] + halfWidthXb
          * f;
      y2b = (tAxes[end][1] - tAxes[beg][1]) * m2 + tAxes[beg][1] + halfWidthYb
          * f;
      polygonX[0] = (int) x1a;
      polygonY[0] = (int) y1a;
      polygonZ[0] = zz1;
      polygonX[1] = (int) x1b;
      polygonY[1] = (int) y1b;
      polygonZ[1] = zz1;
      polygonX[2] = (int) x2b;
      polygonY[2] = (int) y2b;
      polygonZ[2] = zz2;
      polygonX[3] = (int) x2a;
      polygonY[3] = (int) y2a;
      polygonZ[3] = zz2;
      polygonX[4] = (int) x1a;
      polygonY[4] = (int) y1a;
      polygonZ[4] = zz1;
      /*
       * INFINITELY THIN LINE polygonX[0] = (int)x1a; polygonY[0] = (int)y1a;
       * polygonZ[0] = zz1; polygonX[1] = (int)x1b; polygonY[1] = (int)y1b;
       * polygonZ[1] = zz1; polygonX[2] = (int)x1b; polygonY[2] = (int)y1b;
       * polygonZ[2] = zz1; polygonX[3] = (int)x1a; polygonY[3] = (int)y1a;
       * polygonZ[3] = zz1; polygonX[4] = (int)x1a; polygonY[4] = (int)y1a;
       * polygonZ[4] = zz1;
       */
      slate.fillConvexQuad(polygonX, polygonY, polygonZ, 5);
    }
  }

  @Override
  public void transform2D(final boolean unused) {
    // Axes
    for (int i = 0; i < 4; i++) {
      tAxes[i][0] = (bAxes[i][0] * session.plotManager.view.tMat[0][0] + bAxes[i][1]
          * session.plotManager.view.tMat[1][0])
          * session.plotManager.session.plotManager.view.focal
          + session.plotManager.view.tMat[3][0];
      tAxes[i][1] = (bAxes[i][0] * session.plotManager.view.tMat[0][1] + bAxes[i][1]
          * session.plotManager.view.tMat[1][1])
          * session.plotManager.session.plotManager.view.focal
          + session.plotManager.view.tMat[3][1];
    }
    for (int axis = 0; axis < session.plotManager.getDim(); axis++) {
      for (int i = 0; i < tics[axis].length; i++) {
        tics[axis][i].transform2D();
      }
    }
  }

  @Override
  public void transform3D(final boolean unused) {
    float f;
    // Axes
    for (int i = 0; i < 8; i++) {
      tAxes[i][2] = bAxes[i][0] * session.plotManager.view.tMat[0][2]
          + bAxes[i][1] * session.plotManager.view.tMat[1][2] + bAxes[i][2]
          * session.plotManager.view.tMat[2][2]
          + session.plotManager.view.tMat[3][2];
      f = session.plotManager.view.getPerspectiveFactor(tAxes[i][2]);
      tAxes[i][0] = (bAxes[i][0] * session.plotManager.view.tMat[0][0]
          + bAxes[i][1] * session.plotManager.view.tMat[1][0] + bAxes[i][2]
          * session.plotManager.view.tMat[2][0])
          * f + session.plotManager.view.tMat[3][0];
      tAxes[i][1] = (bAxes[i][0] * session.plotManager.view.tMat[0][1]
          + bAxes[i][1] * session.plotManager.view.tMat[1][1] + bAxes[i][2]
          * session.plotManager.view.tMat[2][1])
          * f + session.plotManager.view.tMat[3][1];
    }
    for (int axis = 0; axis < session.plotManager.getDim(); axis++) {
      for (int i = 0; i < tics[axis].length; i++) {
        tics[axis][i].transform3D();
      }
    }
  }

  @Override
  public void translate(final float[] delta) {
//  Do not call this yourself, it's for Subject to call    
    int i;
    switch (session.plotManager.getDim()) {
    case 2:
      start[0] -= delta[0]; // need these?
      start[1] -= delta[1];
      stop[0] -= delta[0]; // need these?
      stop[1] -= delta[1];
      bAxes[0][0] -= delta[0];
      bAxes[0][1] -= delta[1];
      bAxes[1][0] -= delta[0];
      bAxes[1][1] -= delta[1];
      bAxes[2][0] -= delta[0];
      bAxes[2][1] -= delta[1];
      bAxes[3][0] -= delta[0];
      bAxes[3][1] -= delta[1];
      min[0] -= delta[0];
      min[1] -= delta[1];
      max[0] -= delta[0];
      max[1] -= delta[1];
      break;
    case 3:
      start[0] -= delta[0];
      start[1] -= delta[1];
      start[2] -= delta[2];
      stop[0] -= delta[0];
      stop[1] -= delta[1];
      stop[2] -= delta[2];
      bAxes[0][0] -= delta[0];
      bAxes[0][1] -= delta[1];
      bAxes[0][2] -= delta[2];
      bAxes[1][0] -= delta[0];
      bAxes[1][1] -= delta[1];
      bAxes[1][2] -= delta[2];
      bAxes[2][0] -= delta[0];
      bAxes[2][1] -= delta[1];
      bAxes[2][2] -= delta[2];
      bAxes[3][0] -= delta[0];
      bAxes[3][1] -= delta[1];
      bAxes[3][2] -= delta[2];
      bAxes[4][0] -= delta[0];
      bAxes[4][1] -= delta[1];
      bAxes[4][2] -= delta[2];
      bAxes[5][0] -= delta[0];
      bAxes[5][1] -= delta[1];
      bAxes[5][2] -= delta[2];
      bAxes[6][0] -= delta[0];
      bAxes[6][1] -= delta[1];
      bAxes[6][2] -= delta[2];
      bAxes[7][0] -= delta[0];
      bAxes[7][1] -= delta[1];
      bAxes[7][2] -= delta[2];
      min[0] -= delta[0];
      min[1] -= delta[1];
      min[2] -= delta[2];
      max[0] -= delta[0];
      max[1] -= delta[1];
      max[2] -= delta[2];
      break;
    default:
      throw new RuntimeException("bad dim");
    }
    // translate tics
    for (int axis = 0; axis < session.plotManager.getDim(); axis++) {
      for (i = 0; i < tics[axis].length; i++) {
        tics[axis][i].translate(delta);
      }
    }
  }

  void moveToUserSpace() {
    // This is called from somewhere else exactly once
    int i, j;
    for (int axis = 0; axis < 3 /* dimensionality */; axis++) {
      start[axis] += session.plotManager.subject.cumDelta[axis];
      stop[axis] += session.plotManager.subject.cumDelta[axis];
      for (i = 0; i < 8; i++) {
        bAxes[i][axis] += session.plotManager.subject.cumDelta[axis];
      }
      min[axis] += session.plotManager.subject.cumDelta[axis];
      max[axis] += session.plotManager.subject.cumDelta[axis];
      if (tics[axis] != null) {
        for (j = 0; j < tics[axis].length; j++) {
          tics[axis][j].ticStart[axis] += session.plotManager.subject.cumDelta[axis];
        }
      }
    }
  }

  void moveToSubjectSpace() {
    // This is called from somewhere else exactly once
    int i, j;
    for (int axis = 0; axis < 3 /* dimensionality */; axis++) {
      start[axis] -= session.plotManager.subject.cumDelta[axis];
      stop[axis] -= session.plotManager.subject.cumDelta[axis];
      for (i = 0; i < 8; i++) {
        bAxes[i][axis] -= session.plotManager.subject.cumDelta[axis];
      }
      min[axis] -= session.plotManager.subject.cumDelta[axis];
      max[axis] -= session.plotManager.subject.cumDelta[axis];
      if (tics[axis] != null) {
        for (j = 0; j < tics[axis].length; j++) {
          tics[axis][j].ticStart[axis] -= session.plotManager.subject.cumDelta[axis];
        }
      }
    }
  }

  private void calculateSubjectExtents() {
    int i;
    min[0] = Float.POSITIVE_INFINITY;
    min[1] = Float.POSITIVE_INFINITY;
    min[2] = Float.POSITIVE_INFINITY;
    max[0] = Float.NEGATIVE_INFINITY;
    max[1] = Float.NEGATIVE_INFINITY;
    max[2] = Float.NEGATIVE_INFINITY;
    switch (session.plotManager.getDim()) {
    case 2:
      for (i = 0; i < 4; i++) {
        if (bAxes[i][0] > max[0]) {
          max[0] = bAxes[i][0];
        }
        if (bAxes[i][1] > max[1]) {
          max[1] = bAxes[i][1];
        }
        if (bAxes[i][0] < min[0]) {
          min[0] = bAxes[i][0];
        }
        if (bAxes[i][1] < min[1]) {
          min[1] = bAxes[i][1];
        }
      }
      break;
    case 3:
      for (i = 0; i < 8; i++) {
        if (bAxes[i][0] > max[0]) {
          max[0] = bAxes[i][0];
        }
        if (bAxes[i][1] > max[1]) {
          max[1] = bAxes[i][1];
        }
        if (bAxes[i][2] > max[2]) {
          max[2] = bAxes[i][2];
        }
        if (bAxes[i][0] < min[0]) {
          min[0] = bAxes[i][0];
        }
        if (bAxes[i][1] < min[1]) {
          min[1] = bAxes[i][1];
        }
        if (bAxes[i][2] < min[2]) {
          min[2] = bAxes[i][2];
        }
      }
      break;
    default:
      throw new RuntimeException("extents");
    }
    if (drawTics) {
      for (int axis = 0; axis < session.plotManager.getDim(); axis++) {
        for (i = 0; i < tics[axis].length; i++) {
          tics[axis][i].calculateSubjectExtents(); // modifies min, max for
          // Axes
        }
      }
    }
  }

  @Override
  public void calculateViewExtents() {
    // Determine the min and max for the view of the axes, including all tics.
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
      transform2D(false);
      for (i = 0; i < 4; i++) {
        if (tAxes[i][0] > tMax[0]) {
          tMax[0] = tAxes[i][0];
        }
        if (tAxes[i][1] > tMax[1]) {
          tMax[1] = tAxes[i][1];
        }
        if (tAxes[i][0] < tMin[0]) {
          tMin[0] = tAxes[i][0];
        }
        if (tAxes[i][1] < tMin[1]) {
          tMin[1] = tAxes[i][1];
        }
      }
      break;
    case 3:
      transform3D(false);
      for (i = 0; i < 8; i++) {
        if (tAxes[i][0] > tMax[0]) {
          tMax[0] = tAxes[i][0];
        }
        if (tAxes[i][1] > tMax[1]) {
          tMax[1] = tAxes[i][1];
        }
        if (tAxes[i][2] > tMax[2]) {
          tMax[2] = tAxes[i][2];
        }
        if (tAxes[i][0] < tMin[0]) {
          tMin[0] = tAxes[i][0];
        }
        if (tAxes[i][1] < tMin[1]) {
          tMin[1] = tAxes[i][1];
        }
        if (tAxes[i][2] < tMin[2]) {
          tMin[2] = tAxes[i][2];
        }
      }
      break;
    default:
      throw new RuntimeException("bad dim in extents");
    }
    if (drawTics) {
      for (int axis = 0; axis < session.plotManager.getDim(); axis++) {
        for (i = 0; i < tics[axis].length; i++) {
          tics[axis][i].calculateViewExtents(); // modifies tMin, tMax for this
          // Axes
        }
      }
    }
  }

  void dumpBAxes() {
    System.out.println("bAxes[0][0]: " + bAxes[0][0] + "bAxes[0][1]: "
        + bAxes[0][0] + "bAxes[0][2]: " + bAxes[0][2]);
    System.out.println("bAxes[1][0]: " + bAxes[1][0] + "bAxes[1][1]: "
        + bAxes[1][0] + "bAxes[1][2]: " + bAxes[1][2]);
    System.out.println("bAxes[2][0]: " + bAxes[2][0] + "bAxes[2][1]: "
        + bAxes[2][0] + "bAxes[2][2]: " + bAxes[2][2]);
    System.out.println("bAxes[3][0]: " + bAxes[3][0] + "bAxes[3][1]: "
        + bAxes[3][0] + "bAxes[3][2]: " + bAxes[3][2]);
    System.out.println("bAxes[4][0]: " + bAxes[4][0] + "bAxes[4][1]: "
        + bAxes[4][0] + "bAxes[4][2]: " + bAxes[4][2]);
    System.out.println("bAxes[5][0]: " + bAxes[5][0] + "bAxes[5][1]: "
        + bAxes[5][0] + "bAxes[5][2]: " + bAxes[5][2]);
    System.out.println("bAxes[6][0]: " + bAxes[6][0] + "bAxes[6][1]: "
        + bAxes[6][0] + "bAxes[6][2]: " + bAxes[6][2]);
    System.out.println("bAxes[7][0]: " + bAxes[7][0] + "bAxes[7][1]: "
        + bAxes[7][0] + "bAxes[7][2]: " + bAxes[7][2]);
  }

  void dumpUserSpace() {
    System.out.println("axes "
        + (start[0] + session.plotManager.subject.cumDelta[0]) + ", "
        + (start[1] + session.plotManager.subject.cumDelta[1]) + ", "
        + (start[2] + session.plotManager.subject.cumDelta[2]));
  }

  public void convertTo2D() {
    // this can be called even if in 2d
    if (session.plotManager.view != null) {
      session.plotManager.view.reset(); // otherwise
    }
    // get weird
    // effects
    session.plotManager.removeText(labels[Z_AXIS]);
    if (tics[Z_AXIS] != null) {
      for (int i = 0; i < tics[Z_AXIS].length; i++) {
        session.plotManager.removeText(tics[Z_AXIS][i].label);
      }
    }
    if (session.plotManager.canvas != null
        && session.plotManager.canvas.getDragMode() == Canvas.DragModeKind.ROTATION_XY_DRAG_MODE) {
      session.plotManager.canvas
          .setDragMode(Canvas.DragModeKind.ROTATION_Z_DRAG_MODE);
    }
    // if (session.plotter.subject != null) {
    // session.plotter.subject.moveToUserSpace();
    // session.plotter.subject.resetDelta();
    session.pointsetManager.convertPointsetsTo2D();
    // }
    session.plotManager.setDim(2);
    session.dialogManager.getAxesLegendDialog().rebuildLegend();
  }

  public void convertTo3D() {
    if (session.plotManager.view != null) {
      // Not mathematically required, but for UI consistency
      session.plotManager.view.reset();
    }
    if (tics[Z_AXIS] == null) {
      final Text text = new Text(session);
      text.initialize();
      labels[Z_AXIS] = text;
      labels[Z_AXIS].color = labelColor;
      labels[Z_AXIS].font = labelFont;
      // labels[Z_AXIS].setSingletons();
      session.plotManager.addText(labels[Z_AXIS]);
      // stop[Z_AXIS] = 10.0f; start[Z_AXIS] = 0.0f;
      ticSpace[Z_AXIS] = (stop[Z_AXIS] - start[Z_AXIS]) / 5;
      tics[Z_AXIS] = new Tic[Math.round((stop[Z_AXIS] - start[Z_AXIS])
          / ticSpace[Z_AXIS])];
      for (int i = 0; i < tics[Z_AXIS].length; i++) {
        final Tic tic = new Tic(session, this, Z_AXIS, i);
        tic.initialize();
        tics[Z_AXIS][i] = tic;
        tics[Z_AXIS][i].label = new Text(session);
        session.plotManager.addText(tics[Z_AXIS][i].label);
      }
    }
    // if (session.plotter.subject != null) {
    // session.plotter.subject.moveToUserSpace();
    // session.plotter.subject.resetDelta();
    session.pointsetManager.convertPointsetsTo3D();
    // }
    session.plotManager.setDim(3);
    session.dialogManager.getAxesLegendDialog().rebuildLegend();
  }

  public void setSession(Session session) {
    this.session = session;
  }

  @Override
  public void finalize() {
    System.out.println("Finalized [" + getClass().getSimpleName() + ";"
        + hashCode() + "]");
  }

  public Text getLabelForAxis(int axis) {     
    return labels[axis];
  }

  public void setLabelForAxis(int axis, Text label) {
    this.labels[axis] = label;
  }

  public void setLabelColor(Color labelColor) {
    this.labelColor = labelColor;
  }

  public void setLabelFont(Font labelFont) {
    this.labelFont = labelFont;
  }

  @Override
	public void plotManagerChanged(PlotManagerEvent ev) {
    switch(ev.kind) {
    case CHANGED:
      break;
    case BASISMODE_CHANGED:
      buildLabelsAndTics();
      break;
    case TEXT_SELECTION_CHANGED:
      // nop
      break;
    case POINTSET_ORDER_CHANGED:
      // nop
      break;
    case TEXT_PROPERTIES_CHANGED:
      // nop
      break;
    case PREVIEW_CHANGED:
      // nop
      break;
    case DIMENSIONALITY:
      new RuntimeException("TODO").printStackTrace();
      break;
    }
  }
  
  public static String getNameForAxis(int axis) {
    String axisName;
    switch (axis) {
    case Axes.X_AXIS:
      return axisNames[Axes.X_AXIS];
    case Axes.Y_AXIS:
      return axisNames[Axes.Y_AXIS];
    case Axes.Z_AXIS:
      return axisNames[Axes.Z_AXIS];
    default:
      throw new RuntimeException("bad axis index");
    }
  }

}
