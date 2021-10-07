package gov.nih.ncgc.openhts.tool1.plotManager;

import java.text.DecimalFormat;
import gov.nih.ncgc.openhts.tool1.Session;

/**
 * Construct a Tic which contains one Major and all Minor Tics and a gridline
 * (for 2D).
 */
public class Tic {
  private final Axes axes;
  public Text label;
  int axis; // which dimensional axis the tic is on
  int pos; // which tic on the axis, i-th tic
  float[] ticStart; // coord of beginning of major tic, before transformation
  float[] ticStop; // coord of end of major tic, before transformation
  float[] gridStop1;
  float[] gridStop2;
  float[] tTicStart; // coord of beginning of major tic after transformation
  float[] tTicStop; // coord of end of major tic after transformation
  float[] tGridStop1;
  float[] tGridStop2;
  float[][] ticSubStart; // minor tics
  float[][] ticSubStop;
  float[][] tTicSubStart;
  float[][] tTicSubStop;
  int a1, a2;

  private transient final Session session;
  
  /**
   * Construct a new tic object.
   */
  public Tic(final Session session, final Axes axes, final int axis, final int pos) {
    this.session = session;
    this.axes = axes;
    this.axis = axis;
    this.pos = pos;
  }

  public void initialize() {
    // Major tic
    ticStart = new float[3]; // also used as gridStart
    ticStop = new float[3];
    gridStop1 = new float[3];
    gridStop2 = new float[3];
    tTicStart = new float[3]; // also used as tGridStart
    tTicStop = new float[3];
    tGridStop1 = new float[3];
    tGridStop2 = new float[3];
    // Minor tics
    ticSubStart = new float[this.axes.numSubTics[axis]][3];
    tTicSubStart = new float[this.axes.numSubTics[axis]][3];
    ticSubStop = new float[this.axes.numSubTics[axis]][3];
    tTicSubStop = new float[this.axes.numSubTics[axis]][3];
  }
  
  void buildLabelsAndTics(final float factor) { // FOR TIC
    int i, a, a1, a2;
    final float[] f = new float[3], f1 = new float[3], f2 = new float[3];
    final float[] v = new float[3];
    final float sqrt2 = (float) Math.sqrt(2.0);
    float len;
    // ticLength is in millimeters on the screen space.
    // We need to calculate it for subject space.
    switch (session.plotManager.getDim()) {
    case 2:
      // get the corners for this axis (a1)
      if (axis == Axes.X_AXIS) {
        a1 = 1;
      }
      else if (axis == Axes.Y_AXIS) {
        a1 = 3;
      }
      else {
        throw new RuntimeException("axes abuse");
      }
      // get the corners for the axis opposite to this axis (a1)
      if (axis == Axes.X_AXIS) {
        a2 = 3;
      }
      else if (axis == Axes.Y_AXIS) {
        a2 = 1;
      }
      else {
        throw new RuntimeException("axes abuse");
      }
      // Calculate the unit vector, v of this axis
      v[0] = this.axes.bAxes[a1][0] - this.axes.bAxes[0][0];
      v[1] = this.axes.bAxes[a1][1] - this.axes.bAxes[0][1];
      len = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1]);
      v[0] /= len;
      v[1] /= len;
      // Calculate the start of the major tic
      ticStart[0] = this.axes.bAxes[0][0] + pos * this.axes.ticSpace[0] * v[0];
      ticStart[1] = this.axes.bAxes[0][1] + pos * this.axes.ticSpace[1] * v[1];
      // Calculate the start of the minor tics
      for (i = 0; i < this.axes.numSubTics[axis]; i++) {
        ticSubStart[i][0] = ticStart[0] + (i + 1) * this.axes.ticSpace[0]
            / (this.axes.numSubTics[axis] + 1) * v[0];
        ticSubStart[i][1] = ticStart[1] + (i + 1) * this.axes.ticSpace[1]
            / (this.axes.numSubTics[axis] + 1) * v[1];
      }
      // Calculate the grid line which is parallel to the opposite axis
      f[0] = this.axes.bAxes[a2][0] - this.axes.bAxes[0][0];
      f[1] = this.axes.bAxes[a2][1] - this.axes.bAxes[0][1];
      gridStop1[0] = ticStart[0] + f[0];
      gridStop1[1] = ticStart[1] + f[1];
      len = (float) Math.sqrt(f[0] * f[0] + f[1] * f[1]);
      f[0] /= len;
      f[1] /= len;
      // Calculate the tic mark by using the unit vector of each axis
      // to create a unit vector in the reverse direction
      v[0] = -f[0];
      v[1] = -f[1];
      v[2] = -f[2];
      ticStop[0] = ticStart[0] + this.axes.ticLengthSubject * v[0];
      ticStop[1] = ticStart[1] + this.axes.ticLengthSubject * v[1];
      ticStop[2] = ticStart[2] + this.axes.ticLengthSubject * v[2];
      for (i = 0; i < this.axes.numSubTics[axis]; i++) {
        ticSubStop[i][0] = ticSubStart[i][0] + this.axes.ticLengthSubject / 2.0f * v[0];
        ticSubStop[i][1] = ticSubStart[i][1] + this.axes.ticLengthSubject / 2.0f * v[1];
        ticSubStop[i][2] = ticSubStart[i][2] + this.axes.ticLengthSubject / 2.0f * v[2];
      }
      break;
    case 3:
      if (axis == Axes.X_AXIS) {
        a = 4;
        a1 = 2;
        a2 = 1;
      }
      else if (axis == Axes.Y_AXIS) {
        a = 2;
        a1 = 4;
        a2 = 1;
      }
      else if (axis == Axes.Z_AXIS) {
        a = 1;
        a1 = 4;
        a2 = 2;
      }
      else {
        throw new RuntimeException("bad axis");
      }
      // calculate the generalized vector repesenting the axis
      // calculate the unit vector
      v[0] = this.axes.bAxes[a][0] - this.axes.bAxes[0][0];
      v[1] = this.axes.bAxes[a][1] - this.axes.bAxes[0][1];
      v[2] = this.axes.bAxes[a][2] - this.axes.bAxes[0][2];
      len = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
      v[0] /= len;
      v[1] /= len;
      v[2] /= len;
      // if (axis == Z_AXIS) {
      // System.out.println("SUBTIC UNIT V: "+v[0]+" "+v[1]+" "+v[2]);
      // System.out.println(": "+v[0]+" "+v[1]+" "+v[2]);
      // }
      // Calculate the start of the major tic first by adding
      // pos unit vector's worth along the unit vector
      ticStart[0] = this.axes.bAxes[0][0] + pos * this.axes.ticSpace[axis] * v[0];
      ticStart[1] = this.axes.bAxes[0][1] + pos * this.axes.ticSpace[axis] * v[1];
      ticStart[2] = this.axes.bAxes[0][2] + pos * this.axes.ticSpace[axis] * v[2];
      // Calculate the start of the minor tic similarly
      for (i = 0; i < this.axes.numSubTics[axis]; i++) {
        ticSubStart[i][0] = ticStart[0] + (i + 1) * this.axes.ticSpace[axis]
            / (this.axes.numSubTics[axis] + 1) * v[0];
        ticSubStart[i][1] = ticStart[1] + (i + 1) * this.axes.ticSpace[axis]
            / (this.axes.numSubTics[axis] + 1) * v[1];
        ticSubStart[i][2] = ticStart[2] + (i + 1) * this.axes.ticSpace[axis]
            / (this.axes.numSubTics[axis] + 1) * v[2];
      }
      // TODO: gridlines for 3D are not complete
      // Calculate the grid line from x to axis 1 (y)
      f1[0] = this.axes.bAxes[a1][0] - this.axes.bAxes[0][0];
      f1[1] = this.axes.bAxes[a1][1] - this.axes.bAxes[0][1];
      f1[2] = this.axes.bAxes[a1][2] - this.axes.bAxes[0][2];
      gridStop1[0] = ticStart[0] + f1[0];
      gridStop1[1] = ticStart[1] + f1[1];
      gridStop1[2] = ticStart[2] + f1[2];
      len = (float) Math.sqrt(f1[0] * f1[0] + f1[1] * f1[1] + f1[2] * f1[2]);
      f1[0] /= len;
      f1[1] /= len;
      f1[2] /= len;
      // Calculate the gridline for axis 2 (z)
      f2[0] = this.axes.bAxes[a2][0] - this.axes.bAxes[0][0];
      f2[1] = this.axes.bAxes[a2][1] - this.axes.bAxes[0][1];
      f2[2] = this.axes.bAxes[a2][2] - this.axes.bAxes[0][2];
      gridStop2[0] = ticStart[0] + f2[0];
      gridStop2[1] = ticStart[1] + f2[1];
      gridStop2[2] = ticStart[2] + f2[2];
      len = (float) Math.sqrt(f2[0] * f2[0] + f2[1] * f2[1] + f2[2] * f2[2]);
      f2[0] /= len;
      f2[1] /= len;
      f2[2] /= len;
      // Calculate the tic mark by using the unit vector of each axis
      // to create a unit vector in the reverse direction
      v[0] = -(f1[0] + f2[0]) / sqrt2;
      v[1] = -(f1[1] + f2[1]) / sqrt2;
      v[2] = -(f1[2] + f2[2]) / sqrt2;
      ticStop[0] = ticStart[0] + this.axes.ticLengthSubject * v[0];
      ticStop[1] = ticStart[1] + this.axes.ticLengthSubject * v[1];
      ticStop[2] = ticStart[2] + this.axes.ticLengthSubject * v[2];
      for (int j = 0; j < this.axes.numSubTics[axis]; j++) {
        ticSubStop[j][0] = ticSubStart[j][0] + this.axes.ticLengthSubject / 2.0f * v[0];
        ticSubStop[j][1] = ticSubStart[j][1] + this.axes.ticLengthSubject / 2.0f * v[1];
        ticSubStop[j][2] = ticSubStart[j][2] + this.axes.ticLengthSubject / 2.0f * v[2];
      }
      break;
    default:
      throw new RuntimeException("bad dim");
    }
    rebuildLabel(factor);
  }

  /**
   * Repositions the tic label
   */
  private void rebuildLabel(final float screenToSubjectFactor) { // FOR TIC
    if (label == null || !label.moving) {
      return;
    }
    int a1, a2;
    final float[] v = new float[3], f = new float[3], f1 = new float[3], f2 = new float[3];
    float len;
    final float sqrt2 = (float) Math.sqrt(2.0);
    final DecimalFormat df = new DecimalFormat("0.##");
    // Don't change the color or font here!
    label.setString(df.format(ticStop[axis]
        + session.plotManager.subject.cumDelta[axis]));
    switch (session.plotManager.getDim()) {
    case 2:
      if (session.plotManager.session.plotManager.view.zoomed || label.labelOffsetLengthMM < 0) {
        if (axis == Axes.X_AXIS) {
          a1 = 1;
          a2 = 3;
        }
        else if (axis == Axes.Y_AXIS) {
          a1 = 3;
          a2 = 1;
        }
        else {
          throw new RuntimeException("bad axes");
        }
        // User has not positioned text, so we position it automatically
        f[0] = this.axes.bAxes[a2][0] - this.axes.bAxes[0][0];
        f[1] = this.axes.bAxes[a2][1] - this.axes.bAxes[0][1];
        len = (float) Math.sqrt(f[0] * f[0] + f[1] * f[1]);
        f[0] /= len;
        f[1] /= len;
        // Calculate the offset by using the unit vector of each axis
        // to create a unit vector in the reverse direction
        v[0] = -f[0];
        v[1] = -f[1];
        label.unzoomedStart[0] = label.start[0] = ticStop[0]
            + this.axes.ticLengthSubject * v[0];
        label.unzoomedStart[1] = label.start[1] = ticStop[1]
            + this.axes.ticLengthSubject * v[1];
      }
      else {
        // f = ticStart;
        // Find the unit vector that numbers from anchor to the label
        v[0] = label.start[0] - ticStart[0];
        v[1] = label.start[1] - ticStart[1];
        len = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1]);
        v[0] /= len;
        v[1] /= len;
        // Position the axis label offsetLengthMM away from the anchor
        label.unzoomedStart[0] = label.start[0] = ticStart[0]
            + label.labelOffsetLengthMM * screenToSubjectFactor * v[0];
        label.unzoomedStart[1] = label.start[1] = ticStart[1]
            + label.labelOffsetLengthMM * screenToSubjectFactor * v[1];
      }
      break;
    case 3:
      if (session.plotManager.session.plotManager.view.zoomed || label.labelOffsetLengthMM < 0) {
        if (axis == Axes.X_AXIS) {
          a1 = 2;
          a2 = 1;
        }
        else if (axis == Axes.Y_AXIS) {
          a1 = 4;
          a2 = 1;
        }
        else if (axis == Axes.Z_AXIS) {
          a1 = 4;
          a2 = 2;
        }
        else {
          throw new RuntimeException("bad axes");
        }
        // User has not positioned text, so we position it automatically
        // Create a vector that numbers out "diagonally"
        f1[0] = this.axes.bAxes[a1][0] - this.axes.bAxes[0][0];
        f1[1] = this.axes.bAxes[a1][1] - this.axes.bAxes[0][1];
        f1[2] = this.axes.bAxes[a1][2] - this.axes.bAxes[0][2];
        len = (float) Math
            .sqrt(f1[0] * f1[0] + f1[1] * f1[1] + f1[2] * f1[2]);
        f1[0] /= len;
        f1[1] /= len;
        f1[2] /= len;
        f2[0] = this.axes.bAxes[a2][0] - this.axes.bAxes[0][0];
        f2[1] = this.axes.bAxes[a2][1] - this.axes.bAxes[0][1];
        f2[2] = this.axes.bAxes[a2][2] - this.axes.bAxes[0][2];
        len = (float) Math
            .sqrt(f2[0] * f2[0] + f2[1] * f2[1] + f2[2] * f2[2]);
        f2[0] /= len;
        f2[1] /= len;
        f2[2] /= len;
        // Calculate the offset by using the unit vector of each axis
        // to create a unit vector in the reverse direction
        v[0] = -(f1[0] + f2[0]) / sqrt2;
        v[1] = -(f1[1] + f2[1]) / sqrt2;
        v[2] = -(f1[2] + f2[2]) / sqrt2;
        label.unzoomedStart[0] = label.start[0] = ticStop[0]
            + this.axes.ticLengthSubject * v[0];
        label.unzoomedStart[1] = label.start[1] = ticStop[1]
            + this.axes.ticLengthSubject * v[1];
        label.unzoomedStart[2] = label.start[2] = ticStop[2]
            + this.axes.ticLengthSubject * v[2];
      }
      else {
        // f = ticStart;
        // Find the unit vector that numbers from anchor to the label
        v[0] = label.start[0] - ticStart[0];
        v[1] = label.start[1] - ticStart[1];
        v[2] = label.start[2] - ticStart[2];
        len = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        v[0] /= len;
        v[1] /= len;
        v[2] /= len;
        // Position the axis label offsetLengthMM away from the anchor
        label.unzoomedStart[0] = label.start[0] = ticStart[0]
            + label.labelOffsetLengthMM * screenToSubjectFactor * v[0];
        label.unzoomedStart[1] = label.start[1] = ticStart[1]
            + label.labelOffsetLengthMM * screenToSubjectFactor * v[1];
        label.unzoomedStart[2] = label.start[2] = ticStart[2]
            + label.labelOffsetLengthMM * screenToSubjectFactor * v[2];
      }
      break;
    default:
      throw new RuntimeException("bad dim");
    }
    label.makeImage();
    label.calculateSubjectExtents();
  }

  public void calculateViewExtents() {
    int i;
    // we are using tMin & tMax of this axes
    switch (session.plotManager.getDim()) {
    case 2:
      if (tTicStart[0] < this.axes.tMin[0]) {
        this.axes.tMin[0] = tTicStart[0];
      }
      if (tTicStart[1] < this.axes.tMin[1]) {
        this.axes.tMin[1] = tTicStart[1];
      }
      if (tTicStop[0] < this.axes.tMin[0]) {
        this.axes.tMin[0] = tTicStop[0];
      }
      if (tTicStop[1] < this.axes.tMin[1]) {
        this.axes.tMin[1] = tTicStop[1];
      }
      for (i = 0; i < this.axes.numSubTics[axis]; i++) {
        if (tTicSubStart[i][0] < this.axes.tMin[0]) {
          this.axes.tMin[0] = tTicSubStart[i][0];
        }
        if (tTicSubStart[i][1] < this.axes.tMin[1]) {
          this.axes.tMin[1] = tTicSubStart[i][1];
        }
        if (tTicSubStop[i][0] < this.axes.tMin[0]) {
          this.axes.tMin[0] = tTicSubStop[i][0];
        }
        if (tTicSubStop[i][1] < this.axes.tMin[1]) {
          this.axes.tMin[1] = tTicSubStop[i][1];
        }
      }
      if (tTicStart[0] > this.axes.tMax[0]) {
        this.axes.tMax[0] = tTicStart[0];
      }
      if (tTicStart[1] > this.axes.tMax[1]) {
        this.axes.tMax[1] = tTicStart[1];
      }
      if (tTicStop[0] > this.axes.tMax[0]) {
        this.axes.tMax[0] = tTicStop[0];
      }
      if (tTicStop[1] > this.axes.tMax[1]) {
        this.axes.tMax[1] = tTicStop[1];
      }
      for (i = 0; i < this.axes.numSubTics[axis]; i++) {
        if (tTicSubStart[i][0] > this.axes.tMax[0]) {
          this.axes.tMax[0] = tTicSubStart[i][0];
        }
        if (tTicSubStart[i][1] > this.axes.tMax[1]) {
          this.axes.tMax[1] = tTicSubStart[i][1];
        }
        if (tTicSubStop[i][0] > this.axes.tMax[0]) {
          this.axes.tMax[0] = tTicSubStop[i][0];
        }
        if (tTicSubStop[i][1] > this.axes.tMax[1]) {
          this.axes.tMax[1] = tTicSubStop[i][1];
        }
      }
      break;
    case 3:
      if (tTicStart[0] < this.axes.tMin[0]) {
        this.axes.tMin[0] = tTicStart[0];
      }
      if (tTicStart[1] < this.axes.tMin[1]) {
        this.axes.tMin[1] = tTicStart[1];
      }
      if (tTicStart[2] < this.axes.tMin[2]) {
        this.axes.tMin[2] = tTicStart[2];
      }
      if (tTicStop[0] < this.axes.tMin[0]) {
        this.axes.tMin[0] = tTicStop[0];
      }
      if (tTicStop[1] < this.axes.tMin[1]) {
        this.axes.tMin[1] = tTicStop[1];
      }
      if (tTicStop[2] < this.axes.tMin[2]) {
        this.axes.tMin[2] = tTicStop[2];
      }
      for (i = 0; i < this.axes.numSubTics[axis]; i++) {
        if (tTicSubStart[i][0] < this.axes.tMin[0]) {
          this.axes.tMin[0] = tTicSubStart[i][0];
        }
        if (tTicSubStart[i][1] < this.axes.tMin[1]) {
          this.axes.tMin[1] = tTicSubStart[i][1];
        }
        if (tTicSubStart[i][2] < this.axes.tMin[2]) {
          this.axes.tMin[2] = tTicSubStart[i][2];
        }
        if (tTicSubStop[i][0] < this.axes.tMin[0]) {
          this.axes.tMin[0] = tTicSubStop[i][0];
        }
        if (tTicSubStop[i][1] < this.axes.tMin[1]) {
          this.axes.tMin[1] = tTicSubStop[i][1];
        }
        if (tTicSubStop[i][2] < this.axes.tMin[2]) {
          this.axes.tMin[2] = tTicSubStop[i][2];
        }
      }
      if (tTicStart[0] > this.axes.tMax[0]) {
        this.axes.tMax[0] = tTicStart[0];
      }
      if (tTicStart[1] > this.axes.tMax[1]) {
        this.axes.tMax[1] = tTicStart[1];
      }
      if (tTicStart[2] > this.axes.tMax[2]) {
        this.axes.tMax[2] = tTicStart[2];
      }
      if (tTicStop[0] > this.axes.tMax[0]) {
        this.axes.tMax[0] = tTicStop[0];
      }
      if (tTicStop[1] > this.axes.tMax[1]) {
        this.axes.tMax[1] = tTicStop[1];
      }
      if (tTicStop[2] > this.axes.tMax[2]) {
        this.axes.tMax[2] = tTicStop[2];
      }
      for (i = 0; i < this.axes.numSubTics[axis]; i++) {
        if (tTicSubStart[i][0] > this.axes.tMax[0]) {
          this.axes.tMax[0] = tTicSubStart[i][0];
        }
        if (tTicSubStart[i][1] > this.axes.tMax[1]) {
          this.axes.tMax[1] = tTicSubStart[i][1];
        }
        if (tTicSubStart[i][2] > this.axes.tMax[2]) {
          this.axes.tMax[2] = tTicSubStart[i][2];
        }
        if (tTicSubStop[i][0] > this.axes.tMax[0]) {
          this.axes.tMax[0] = tTicSubStop[i][0];
        }
        if (tTicSubStop[i][1] > this.axes.tMax[1]) {
          this.axes.tMax[1] = tTicSubStop[i][1];
        }
        if (tTicSubStop[i][2] > this.axes.tMax[2]) {
          this.axes.tMax[2] = tTicSubStop[i][2];
        }
      }
      break;
    default:
      throw new RuntimeException("bad dim");
    }
  }

  /**
   *
   */
  void moveToUserSpace() {
    int a, i;
    for (a = 0; a < session.plotManager.getDim(); a++) {
      ticStart[a] += session.plotManager.subject.cumDelta[a];
      ticStop[a] += session.plotManager.subject.cumDelta[a];
      gridStop1[a] += session.plotManager.subject.cumDelta[a];
      gridStop2[a] += session.plotManager.subject.cumDelta[a];
      for (i = 0; i < this.axes.numSubTics[axis]; i++) {
        ticSubStart[i][a] += session.plotManager.subject.cumDelta[a];
        ticSubStop[i][a] += session.plotManager.subject.cumDelta[a];
      }
      this.axes.min[a] += session.plotManager.subject.cumDelta[a];
    }
  }

  /**
   *
   */
  void moveToSubjectSpace() {
    int a, i;
    for (a = 0; a < session.plotManager.getDim(); a++) {
      ticStart[a] -= session.plotManager.subject.cumDelta[a];
      ticStop[a] -= session.plotManager.subject.cumDelta[a];
      gridStop1[a] -= session.plotManager.subject.cumDelta[a];
      gridStop2[a] -= session.plotManager.subject.cumDelta[a];
      for (i = 0; i < this.axes.numSubTics[axis]; i++) {
        ticSubStart[i][a] -= session.plotManager.subject.cumDelta[a];
        ticSubStop[i][a] -= session.plotManager.subject.cumDelta[a];
      }
      this.axes.min[a] -= session.plotManager.subject.cumDelta[a];
      this.axes.max[a] -= session.plotManager.subject.cumDelta[a];
    }
  }

  /**
   *
   */
  public void calculateSubjectExtents() {
    int i;
    // we are using min & max of this axes now
    switch (session.plotManager.getDim()) {
    case 2:
      if (ticStart[0] < this.axes.min[0]) {
        this.axes.min[0] = ticStart[0];
      }
      if (ticStart[1] < this.axes.min[1]) {
        this.axes.min[1] = ticStart[1];
      }
      if (ticStop[0] < this.axes.min[0]) {
        this.axes.min[0] = ticStop[0];
      }
      if (ticStop[1] < this.axes.min[1]) {
        this.axes.min[1] = ticStop[1];
      }
      for (i = 0; i < this.axes.numSubTics[axis]; i++) {
        if (ticSubStart[i][0] < this.axes.min[0]) {
          this.axes.min[0] = ticSubStart[i][0];
        }
        if (ticSubStart[i][1] < this.axes.min[1]) {
          this.axes.min[1] = ticSubStart[i][1];
        }
        if (ticSubStop[i][0] < this.axes.min[0]) {
          this.axes.min[0] = ticSubStop[i][0];
        }
        if (ticSubStop[i][1] < this.axes.min[1]) {
          this.axes.min[1] = ticSubStop[i][1];
        }
      }
      if (ticStart[0] > this.axes.max[0]) {
        this.axes.max[0] = ticStart[0];
      }
      if (ticStart[1] > this.axes.max[1]) {
        this.axes.max[1] = ticStart[1];
      }
      if (ticStop[0] > this.axes.max[0]) {
        this.axes.max[0] = ticStop[0];
      }
      if (ticStop[1] > this.axes.max[1]) {
        this.axes.max[1] = ticStop[1];
      }
      for (i = 0; i < this.axes.numSubTics[axis]; i++) {
        if (ticSubStart[i][0] > this.axes.max[0]) {
          this.axes.max[0] = ticSubStart[i][0];
        }
        if (ticSubStart[i][1] > this.axes.max[1]) {
          this.axes.max[1] = ticSubStart[i][1];
        }
        if (ticSubStop[i][0] > this.axes.max[0]) {
          this.axes.max[0] = ticSubStop[i][0];
        }
        if (ticSubStop[i][1] > this.axes.max[1]) {
          this.axes.max[1] = ticSubStop[i][1];
        }
      }
      break;
    case 3:
      if (ticStart[0] < this.axes.min[0]) {
        this.axes.min[0] = ticStart[0];
      }
      if (ticStart[1] < this.axes.min[1]) {
        this.axes.min[1] = ticStart[1];
      }
      if (ticStart[2] < this.axes.min[2]) {
        this.axes.min[2] = ticStart[2];
      }
      if (ticStop[0] < this.axes.min[0]) {
        this.axes.min[0] = ticStop[0];
      }
      if (ticStop[1] < this.axes.min[1]) {
        this.axes.min[1] = ticStop[1];
      }
      if (ticStop[2] < this.axes.min[2]) {
        this.axes.min[2] = ticStop[2];
      }
      for (i = 0; i < this.axes.numSubTics[axis]; i++) {
        if (ticSubStart[i][0] < this.axes.min[0]) {
          this.axes.min[0] = ticSubStart[i][0];
        }
        if (ticSubStart[i][1] < this.axes.min[1]) {
          this.axes.min[1] = ticSubStart[i][1];
        }
        if (ticSubStart[i][2] < this.axes.min[2]) {
          this.axes.min[2] = ticSubStart[i][2];
        }
        if (ticSubStop[i][0] < this.axes.min[0]) {
          this.axes.min[0] = ticSubStop[i][0];
        }
        if (ticSubStop[i][1] < this.axes.min[1]) {
          this.axes.min[1] = ticSubStop[i][1];
        }
        if (ticSubStop[i][2] < this.axes.min[2]) {
          this.axes.min[2] = ticSubStop[i][2];
        }
      }
      if (ticStart[0] > this.axes.max[0]) {
        this.axes.max[0] = ticStart[0];
      }
      if (ticStart[1] > this.axes.max[1]) {
        this.axes.max[1] = ticStart[1];
      }
      if (ticStart[2] > this.axes.max[2]) {
        this.axes.max[2] = ticStart[2];
      }
      if (ticStop[0] > this.axes.max[0]) {
        this.axes.max[0] = ticStop[0];
      }
      if (ticStop[1] > this.axes.max[1]) {
        this.axes.max[1] = ticStop[1];
      }
      if (ticStop[2] > this.axes.max[2]) {
        this.axes.max[2] = ticStop[2];
      }
      for (i = 0; i < this.axes.numSubTics[axis]; i++) {
        if (ticSubStart[i][0] > this.axes.max[0]) {
          this.axes.max[0] = ticSubStart[i][0];
        }
        if (ticSubStart[i][1] > this.axes.max[1]) {
          this.axes.max[1] = ticSubStart[i][1];
        }
        if (ticSubStart[i][2] > this.axes.max[2]) {
          this.axes.max[2] = ticSubStart[i][2];
        }
        if (ticSubStop[i][0] > this.axes.max[0]) {
          this.axes.max[0] = ticSubStop[i][0];
        }
        if (ticSubStop[i][1] > this.axes.max[1]) {
          this.axes.max[1] = ticSubStop[i][1];
        }
        if (ticSubStop[i][2] > this.axes.max[2]) {
          this.axes.max[2] = ticSubStop[i][2];
        }
      }
      break;
    default:
      throw new RuntimeException("bad dim");
    }
  }

  /**
   * private void drawGridLine2D(Slate slate, int x0, int y0, int x1, int y1) {
   * Color prevColor = slate.getColor(); slate.setColor(Color.gray);
   * drawLine2D(slate,x0,y0,x1,y1); slate.setColor(prevColor); }
   */
  /**
   *
   */
  private void drawLine2D(final Slate slate, final float x1, final float y1,
      final float x2, final float y2) {
    final int nSegments = 1;
    int i;
    float x1a, x1b, y1a, y1b, x2a, x2b, y2a, y2b, m1, m2;
    final float f = session.plotManager.session.plotManager.view.focal;
    final int[] polygonX = new int[5];
    final int[] polygonY = new int[5];
    // calculate the perpendicular 2D vector of displacement of linewidth
    final float alpha = (float) Math.atan2(y2 - y1, x2 - x1);
    final float alphaA = alpha - (float) (Math.PI / 2.0);
    final float alphaB = alpha + (float) (Math.PI / 2.0);
    final float halfWidth = this.axes.lineWidthSubject / 2.0f;
    final float halfWidthXa = halfWidth * (float) Math.cos(alphaA);
    final float halfWidthYa = halfWidth * (float) Math.sin(alphaA);
    final float halfWidthXb = halfWidth * (float) Math.cos(alphaB);
    final float halfWidthYb = halfWidth * (float) Math.sin(alphaB);
    for (i = 0; i < nSegments; i++) {
      m1 = (float) i / (float) nSegments;
      x1a = (x2 - x1) * m1 + x1 + halfWidthXa * f;
      y1a = (y2 - y1) * m1 + y1 + halfWidthYa * f;
      x1b = (x2 - x1) * m1 + x1 + halfWidthXb * f;
      y1b = (y2 - y1) * m1 + y1 + halfWidthYb * f;
      m2 = (float) (i + 1) / (float) nSegments;
      x2a = (x2 - x1) * m2 + x1 + halfWidthXa * f;
      y2a = (y2 - y1) * m2 + y1 + halfWidthYa * f;
      x2b = (x2 - x1) * m2 + x1 + halfWidthXb * f;
      y2b = (y2 - y1) * m2 + y1 + halfWidthYb * f;
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

  /**
   *
   */
  private void drawLine3D(final Slate slate, final float x1, final float y1,
      final float z1, final float x2, final float y2, final float z2) {
    final int nSegments = 1;
    int i;
    float f = 1.0f, x1a, x1b, y1a, y1b, x2a, x2b, y2a, y2b, zz1, zz2, m1, m2;
    final int[] polygonX = new int[5];
    final int[] polygonY = new int[5];
    final float[] polygonZ = new float[5];
    // calculate the perpendicular 2D vector of displacement of linewidth
    final float alpha = (float) Math.atan2(y2 - y1, x2 - x1);
    final float alphaA = alpha - (float) (Math.PI / 2.0);
    final float alphaB = alpha + (float) (Math.PI / 2.0);
    final float halfWidth = this.axes.lineWidthSubject / 2.0f;
    final float halfWidthXa = halfWidth * (float) Math.cos(alphaA);
    final float halfWidthYa = halfWidth * (float) Math.sin(alphaA);
    final float halfWidthXb = halfWidth * (float) Math.cos(alphaB);
    final float halfWidthYb = halfWidth * (float) Math.sin(alphaB);
    for (i = 0; i < nSegments; i++) {
      m1 = (float) i / (float) nSegments;
      zz1 = (z2 - z1) * m1 + z1;
      f = session.plotManager.session.plotManager.view.getPerspectiveFactor(zz1);
      x1a = (x2 - x1) * m1 + x1 + halfWidthXa * f;
      y1a = (y2 - y1) * m1 + y1 + halfWidthYa * f;
      x1b = (x2 - x1) * m1 + x1 + halfWidthXb * f;
      y1b = (y2 - y1) * m1 + y1 + halfWidthYb * f;
      m2 = (float) (i + 1) / (float) nSegments;
      zz2 = (z2 - z1) * m2 + z1;
      f = session.plotManager.session.plotManager.view.getPerspectiveFactor(zz2);
      x2a = (x2 - x1) * m2 + x1 + halfWidthXa * f;
      y2a = (y2 - y1) * m2 + y1 + halfWidthYa * f;
      x2b = (x2 - x1) * m2 + x1 + halfWidthXb * f;
      y2b = (y2 - y1) * m2 + y1 + halfWidthYb * f;
      polygonX[0] = (int) x1a;
      polygonY[0] = (int) y1a;
      polygonZ[0] = z1;
      polygonX[1] = (int) x1b;
      polygonY[1] = (int) y1b;
      polygonZ[1] = z1;
      polygonX[2] = (int) x2b;
      polygonY[2] = (int) y2b;
      polygonZ[2] = z2;
      polygonX[3] = (int) x2a;
      polygonY[3] = (int) y2a;
      polygonZ[3] = z2;
      polygonX[4] = (int) x1a;
      polygonY[4] = (int) y1a;
      polygonZ[4] = z1;
      /*
       * INFINITELY THIN LINE polygonX[0] = (int)x1a; polygonY[0] = (int)y1a;
       * polygonZ[0] = z1; polygonX[1] = (int)x1b; polygonY[1] = (int)y1b;
       * polygonZ[1] = z1; polygonX[2] = (int)x1b; polygonY[2] = (int)y1b;
       * polygonZ[2] = z1; polygonX[3] = (int)x1a; polygonY[3] = (int)y1a;
       * polygonZ[3] = z1; polygonX[4] = (int)x1a; polygonY[4] = (int)y1a;
       * polygonZ[4] = z1;
       */
      slate.fillConvexQuad(polygonX, polygonY, polygonZ, 5);
    }
  }

  public void transform2D() {
    tTicStart[0] = (ticStart[0] * session.plotManager.session.plotManager.view.tMat[0][0] + ticStart[1]
        * session.plotManager.view.tMat[1][0])
        * session.plotManager.session.plotManager.view.focal + session.plotManager.view.tMat[3][0];
    tTicStart[1] = (ticStart[0] * session.plotManager.view.tMat[0][1] + ticStart[1]
        * session.plotManager.view.tMat[1][1])
        * session.plotManager.session.plotManager.view.focal + session.plotManager.view.tMat[3][1];
    tTicStop[0] = (ticStop[0] * session.plotManager.view.tMat[0][0] + ticStop[1]
        * session.plotManager.view.tMat[1][0])
        * session.plotManager.session.plotManager.view.focal + session.plotManager.view.tMat[3][0];
    tTicStop[1] = (ticStop[0] * session.plotManager.view.tMat[0][1] + ticStop[1]
        * session.plotManager.view.tMat[1][1])
        * session.plotManager.session.plotManager.view.focal + session.plotManager.view.tMat[3][1];
    // TODO: don't calc if not displayed
    tGridStop1[0] = (gridStop1[0] * session.plotManager.view.tMat[0][0] + gridStop1[1]
        * session.plotManager.view.tMat[1][0])
        * session.plotManager.session.plotManager.view.focal + session.plotManager.view.tMat[3][0];
    tGridStop1[1] = (gridStop1[0] * session.plotManager.view.tMat[0][1] + gridStop1[1]
        * session.plotManager.view.tMat[1][1])
        * session.plotManager.session.plotManager.view.focal + session.plotManager.view.tMat[3][1];
    for (int j = 0; j < this.axes.numSubTics[axis]; j++) {
      tTicSubStart[j][0] = (ticSubStart[j][0] * session.plotManager.view.tMat[0][0] + ticSubStart[j][1]
          * session.plotManager.view.tMat[1][0])
          * session.plotManager.session.plotManager.view.focal + session.plotManager.view.tMat[3][0];
      tTicSubStart[j][1] = (ticSubStart[j][0] * session.plotManager.view.tMat[0][1] + ticSubStart[j][1]
          * session.plotManager.view.tMat[1][1])
          * session.plotManager.session.plotManager.view.focal + session.plotManager.view.tMat[3][1];
      tTicSubStop[j][0] = (ticSubStop[j][0] * session.plotManager.view.tMat[0][0] + ticSubStop[j][1]
          * session.plotManager.view.tMat[1][0])
          * session.plotManager.session.plotManager.view.focal + session.plotManager.view.tMat[3][0];
      tTicSubStop[j][1] = (ticSubStop[j][0] * session.plotManager.view.tMat[0][1] + ticSubStop[j][1]
          * session.plotManager.view.tMat[1][1])
          * session.plotManager.session.plotManager.view.focal + session.plotManager.view.tMat[3][1];
    }
  }

  /**
   *
   */
  public void transform3D() {
    tTicStart[2] = ticStart[0] * session.plotManager.view.tMat[0][2] + ticStart[1]
        * session.plotManager.view.tMat[1][2] + ticStart[2] * session.plotManager.view.tMat[2][2] + session.plotManager.view.tMat[3][2];
    float f = session.plotManager.session.plotManager.view.getPerspectiveFactor(tTicStart[2]);
    tTicStart[0] = (ticStart[0] * session.plotManager.view.tMat[0][0] + ticStart[1]
        * session.plotManager.view.tMat[1][0] + ticStart[2] * session.plotManager.view.tMat[2][0])
        * f + session.plotManager.view.tMat[3][0];
    tTicStart[1] = (ticStart[0] * session.plotManager.view.tMat[0][1] + ticStart[1]
        * session.plotManager.view.tMat[1][1] + ticStart[2] * session.plotManager.view.tMat[2][1])
        * f + session.plotManager.view.tMat[3][1];
    tTicStop[2] = ticStop[0] * session.plotManager.view.tMat[0][2] + ticStop[1] * session.plotManager.view.tMat[1][2]
        + ticStop[2] * session.plotManager.view.tMat[2][2] + session.plotManager.view.tMat[3][2];
    f = session.plotManager.session.plotManager.view.getPerspectiveFactor(tTicStop[2]);
    tTicStop[0] = (ticStop[0] * session.plotManager.view.tMat[0][0] + ticStop[1]
        * session.plotManager.view.tMat[1][0] + ticStop[2] * session.plotManager.view.tMat[2][0])
        * f + session.plotManager.view.tMat[3][0];
    tTicStop[1] = (ticStop[0] * session.plotManager.view.tMat[0][1] + ticStop[1]
        * session.plotManager.view.tMat[1][1] + ticStop[2] * session.plotManager.view.tMat[2][1])
        * f + session.plotManager.view.tMat[3][1];
    tGridStop1[2] = gridStop1[0] * session.plotManager.view.tMat[0][2] + gridStop1[1]
        * session.plotManager.view.tMat[1][2] + gridStop1[2] * session.plotManager.view.tMat[2][2] + session.plotManager.view.tMat[3][2];
    f = session.plotManager.session.plotManager.view.getPerspectiveFactor(tGridStop1[2]);
    tGridStop1[0] = (gridStop1[0] * session.plotManager.view.tMat[0][0] + gridStop1[1]
        * session.plotManager.view.tMat[1][0] + gridStop1[2] * session.plotManager.view.tMat[2][0])
        * f + session.plotManager.view.tMat[3][0];
    tGridStop1[1] = (gridStop1[0] * session.plotManager.view.tMat[0][1] + gridStop1[1]
        * session.plotManager.view.tMat[1][1] + gridStop1[2] * session.plotManager.view.tMat[2][1])
        * f + session.plotManager.view.tMat[3][1];
    //
    tGridStop2[2] = gridStop2[0] * session.plotManager.view.tMat[0][2] + gridStop2[1]
        * session.plotManager.view.tMat[1][2] + gridStop2[2] * session.plotManager.view.tMat[2][2] + session.plotManager.view.tMat[3][2];
    f = session.plotManager.session.plotManager.view.getPerspectiveFactor(tGridStop2[2]);
    tGridStop2[0] = (gridStop2[0] * session.plotManager.view.tMat[0][0] + gridStop2[1]
        * session.plotManager.view.tMat[1][0] + gridStop2[2] * session.plotManager.view.tMat[2][0])
        * f + session.plotManager.view.tMat[3][0];
    tGridStop2[1] = (gridStop2[0] * session.plotManager.view.tMat[0][1] + gridStop2[1]
        * session.plotManager.view.tMat[1][1] + gridStop2[2] * session.plotManager.view.tMat[2][1])
        * f + session.plotManager.view.tMat[3][1];
    //
    for (int j = 0; j < this.axes.numSubTics[axis]; j++) {
      // sub start
      tTicSubStart[j][2] = ticSubStart[j][0] * session.plotManager.view.tMat[0][2]
          + ticSubStart[j][1] * session.plotManager.view.tMat[1][2] + ticSubStart[j][2]
          * session.plotManager.view.tMat[2][2] + session.plotManager.view.tMat[3][2];
      f = session.plotManager.session.plotManager.view.getPerspectiveFactor(tTicSubStart[j][2]);
      tTicSubStart[j][0] = (ticSubStart[j][0] * session.plotManager.view.tMat[0][0]
          + ticSubStart[j][1] * session.plotManager.view.tMat[1][0] + ticSubStart[j][2]
          * session.plotManager.view.tMat[2][0])
          * f + session.plotManager.view.tMat[3][0];
      tTicSubStart[j][1] = (ticSubStart[j][0] * session.plotManager.view.tMat[0][1]
          + ticSubStart[j][1] * session.plotManager.view.tMat[1][1] + ticSubStart[j][2]
          * session.plotManager.view.tMat[2][1])
          * f + session.plotManager.view.tMat[3][1];
      // sub stop
      tTicSubStop[j][2] = ticSubStop[j][0] * session.plotManager.view.tMat[0][2]
          + ticSubStop[j][1] * session.plotManager.view.tMat[1][2] + ticSubStop[j][2]
          * session.plotManager.view.tMat[2][2] + session.plotManager.view.tMat[3][2];
      f = session.plotManager.session.plotManager.view.getPerspectiveFactor(tTicSubStop[j][2]);
      tTicSubStop[j][0] = (ticSubStop[j][0] * session.plotManager.view.tMat[0][0]
          + ticSubStop[j][1] * session.plotManager.view.tMat[1][0] + ticSubStop[j][2]
          * session.plotManager.view.tMat[2][0])
          * f + session.plotManager.view.tMat[3][0];
      tTicSubStop[j][1] = (ticSubStop[j][0] * session.plotManager.view.tMat[0][1]
          + ticSubStop[j][1] * session.plotManager.view.tMat[1][1] + ticSubStop[j][2]
          * session.plotManager.view.tMat[2][1])
          * f + session.plotManager.view.tMat[3][1];
    }
  }

  /**
   *
   */
  public void translate(final float[] delta) {
    ticStart[0] -= delta[0];
    ticStart[1] -= delta[1];
    ticStart[2] -= delta[2];
    ticStop[0] -= delta[0];
    ticStop[1] -= delta[1];
    ticStop[2] -= delta[2];
    for (int j = 0; j < this.axes.numSubTics[axis]; j++) {
      ticSubStart[j][0] -= delta[0];
      ticSubStart[j][1] -= delta[1];
      ticSubStart[j][2] -= delta[2];
      ticSubStop[j][0] -= delta[0];
      ticSubStop[j][1] -= delta[1];
      ticSubStop[j][2] -= delta[2];
    }
  }

  /**
   *
   */
  public void drawTic2D(final Slate slate) {
    slate.setColor(this.axes.color);
    if (this.axes.drawGrid) {
      slate.drawLine((int) tTicStart[0], (int) tTicStart[1],
          (int) tGridStop1[0], (int) tGridStop1[1]);
    }
    for (int j = 0; j < this.axes.numSubTics[axis]; j++) {
      drawLine2D(slate, tTicSubStart[j][0], tTicSubStart[j][1],
          tTicSubStop[j][0], tTicSubStop[j][1]);
    }
    drawLine2D(slate, tTicStart[0], tTicStart[1], tTicStop[0], tTicStop[1]);
  }

  /**
   *
   */
  public void drawTic3D(final Slate slate) {
    slate.setColor(this.axes.color);
    drawLine3D(slate, tTicStart[0], tTicStart[1], tTicStart[2], tTicStop[0],
        tTicStop[1], tTicStop[2]);
    if (this.axes.drawGrid) {
      slate.drawLine((int) tTicStart[0], (int) tTicStart[1], tTicStart[2],
          (int) tGridStop1[0], (int) tGridStop1[1], tGridStop1[2]);
      slate.drawLine((int) tTicStart[0], (int) tTicStart[1], tTicStart[2],
          (int) tGridStop2[0], (int) tGridStop2[1], tGridStop2[2]);
    }
    for (int j = 0; j < this.axes.numSubTics[axis]; j++) {
      drawLine3D(slate, tTicSubStart[j][0], tTicSubStart[j][1],
          tTicSubStart[j][2], tTicSubStop[j][0], tTicSubStop[j][1],
          tTicSubStop[j][2]);
    }
  }
}