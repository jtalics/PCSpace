package gov.nih.ncgc.openhts.tool1.plotManager;


import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.persistence.Persistent;
import gov.nih.ncgc.openhts.tool1.plotManager.Canvas.DragModeKind;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;

/** Purpose is to ...
 * @author talafousj
 */
public final class View implements Persistent {
  public transient Session session;
  //static final String sectionType = "VIEW";
  String name;
  public float[][] tMat; // transformation matrix (rot + trans)
  float[][] prevTmat;
  public float[] min;
  public float[] max;
  float[] origin;
  public float lightPosition; // z-distance that light is before model origin
                              // (ABSOLUTE VALUE)
  public float lightRamp; // how fast light ramps down with distance
  public boolean stereo = false;
  public int stereoSeparation; // pixels of separation of stereo pairs
  public float stereoAngle; // angle of perception difference
  public float cop; // Center of Thales Projection Perspective (z-coord) in 3D
  public float focal; // distance from cop to imagePlane in 3D, scaling in 2D
  public boolean AUTOCOMPOSE_ROTATIONAL_CENTERING_MODE = true;
  public boolean AUTOCOMPOSE_SCREEN_CENTERING_MODE = true;
  public boolean AUTOCOMPOSE_PERSPECTIVE_MODE = true;
  public boolean AUTOCOMPOSE_LIGHTING_MODE = true;
  public boolean AUTOCOMPOSE_STEREO_MODE = true;
  public float margin;
  public float startingFocal = 5000.0f;
  public boolean zoomed;
  private boolean inertia, inertiaXYDirection;
  int dy, dx;
  float cross;
  private boolean orthogonalMode;

  public View(final Session session) {
    this.session = session;
    name = "view "+hashCode();
    min = new float[3];
    max = new float[3];
    origin = new float[3];
    tMat = new float[4][3];
    prevTmat = new float[4][3];
    //System.out.println("CREATED VIEW " + hashCode());
  }

  public void initialize() {
    min[0] = 0.0f;
    min[1] = 0.0f;
    min[2] = 0.0f;
    max[0] = 0.0f;
    max[1] = 0.0f;
    max[2] = 0.0f;
    origin[0] = 0.0f;
    origin[1] = 0.0f;
    origin[2] = 0.0f;
    cop = -100.0f;
    focal = startingFocal;
    lightPosition = Float.POSITIVE_INFINITY;
    lightRamp = 1.0f;
    stereo = false;
    stereoSeparation = 500; // TODO: change to Canvas.getW()
    stereoAngle = 0.1f;
    margin = 10; // in millimeters
    reset();
    tMat[3][0] = 0.0f;
    tMat[3][1] = 0.0f;
    tMat[3][2] = 0.0f;
    prevTmat[3][0] = 0.0f;
    prevTmat[3][1] = 0.0f;
    prevTmat[3][2] = 0.0f;
    if (zoomed) {
      unzoom();
      zoomed = false;
    }
//    if (Main.isDemoApplet) { 
//      inertia=inertiaXYDirection=true;
//      dx=2;
//      dy=0;
//      cross=20.0f;
//    }
      inertia=false;
    orthogonalMode = false;
    initializeTransient();
  }
  
  @Override
	public void initializeTransient() {
    // nop
  }
  
  void clearReferences() {
    // System.out.println("CLEARING REFERENCES IN VIEW " + hashCode());
  }

  @Override
  protected void finalize() {
    // System.out.println("FINALIZED VIEW " + hashCode());
  }

  public void reset() {
    tMat[0][0] = 1.0f;
    tMat[0][1] = 0.0f;
    tMat[0][2] = 0.0f;
    tMat[1][0] = 0.0f;
    tMat[1][1] = -1.0f;
    tMat[1][2] = 0.0f;
    tMat[2][0] = 0.0f;
    tMat[2][1] = 0.0f;
    tMat[2][2] = -1.0f;
    prevTmat[0][0] = 1.0f;
    prevTmat[0][1] = 0.0f;
    prevTmat[0][2] = 0.0f;
    prevTmat[1][0] = 0.0f;
    prevTmat[1][1] = -1.0f;
    prevTmat[1][2] = 0.0f;
    prevTmat[2][0] = 0.0f;
    prevTmat[2][1] = 0.0f;
    prevTmat[2][2] = -1.0f;
  }

  /**
   * overloaded method
   */
  public void rotate(final float dx, final float dy, final float delta) {
    float cosA = (float) Math.cos(dx * delta);
    float sinA = (float) Math.sin(dx * delta);
    float tmpFloat = tMat[0][0];
    tMat[0][0] = (cosA * tmpFloat - sinA * tMat[0][2]);
    tMat[0][2] = (sinA * tmpFloat + cosA * tMat[0][2]);
    tmpFloat = tMat[1][0];
    tMat[1][0] = (cosA * tmpFloat - sinA * tMat[1][2]);
    tMat[1][2] = (sinA * tmpFloat + cosA * tMat[1][2]);
    tmpFloat = tMat[2][0];
    tMat[2][0] = (cosA * tmpFloat - sinA * tMat[2][2]);
    tMat[2][2] = (sinA * tmpFloat + cosA * tMat[2][2]);
    cosA = (float) Math.cos(dy * delta);
    sinA = (float) Math.sin(dy * delta);
    tmpFloat = tMat[0][1];
    tMat[0][1] = (cosA * tmpFloat - sinA * tMat[0][2]);
    tMat[0][2] = (sinA * tmpFloat + cosA * tMat[0][2]);
    tmpFloat = tMat[1][1];
    tMat[1][1] = (cosA * tmpFloat - sinA * tMat[1][2]);
    tMat[1][2] = (sinA * tmpFloat + cosA * tMat[1][2]);
    tmpFloat = tMat[2][1];
    tMat[2][1] = (cosA * tmpFloat - sinA * tMat[2][2]);
    tMat[2][2] = (sinA * tmpFloat + cosA * tMat[2][2]);
    //orthogonalizeTMat();
    session.plotManager.canvas.setStale(true);
  }

  /**
   * overloaded method
   */
  public void rotate(final float a, final float delta) {
    final float cosA = (float) Math.cos(a * delta);
    final float sinA = (float) Math.sin(a * delta);
    float tmpFloat = tMat[0][0];
    tMat[0][0] = (cosA * tmpFloat - sinA * tMat[0][1]);
    tMat[0][1] = (sinA * tmpFloat + cosA * tMat[0][1]);
    tmpFloat = tMat[1][0];
    tMat[1][0] = (cosA * tmpFloat - sinA * tMat[1][1]);
    tMat[1][1] = (sinA * tmpFloat + cosA * tMat[1][1]);
    tmpFloat = tMat[2][0];
    tMat[2][0] = (cosA * tmpFloat - sinA * tMat[2][1]);
    tMat[2][1] = (sinA * tmpFloat + cosA * tMat[2][1]);
    //orthogonalizeTMat();
    session.plotManager.canvas.setStale(true);
  }

  /**
   * Don't confuse this with the subject translate()
   */
  void translate(final float dx, final float dy) {
    tMat[3][0] -= dx;
    tMat[3][1] -= dy;
    session.plotManager.canvas.setStale(true);
  }

  /**
   * not used, but works!
   */
  private void euler(final float phi, final float psi, final float theta) {
    final float[][] eMat = new float[3][3]; // euler mat from Goldstein p. 109
    final float[][] newTMat = new float[4][3];
    eMat[0][0] = (float) (Math.cos(psi) * Math.cos(phi) - Math.sin(psi)
        * Math.cos(theta) * Math.sin(phi));
    eMat[0][1] = (float) (Math.cos(psi) * Math.sin(phi) + Math.sin(psi)
        * Math.cos(theta) * Math.cos(phi));
    eMat[0][2] = (float) (Math.sin(psi) * Math.sin(theta));
    eMat[1][0] = (float) (-Math.sin(psi) * Math.cos(phi) - Math.cos(psi)
        * Math.cos(theta) * Math.sin(phi));
    eMat[1][1] = (float) (-Math.sin(psi) * Math.sin(phi) + Math.cos(psi)
        * Math.cos(theta) * Math.cos(phi));
    eMat[1][2] = (float) (Math.cos(psi) * Math.sin(theta));
    eMat[2][0] = (float) (Math.sin(theta) * Math.sin(phi));
    eMat[2][1] = (float) (-Math.sin(theta) * Math.cos(phi));
    eMat[2][2] = (float) Math.cos(theta);
    newTMat[0][0] = eMat[0][0] * tMat[0][0] + eMat[0][1] * tMat[1][0]
        + eMat[0][2] * tMat[2][0];
    newTMat[0][1] = eMat[0][0] * tMat[0][1] + eMat[0][1] * tMat[1][1]
        + eMat[0][2] * tMat[2][1];
    newTMat[0][2] = eMat[0][0] * tMat[0][2] + eMat[0][1] * tMat[1][2]
        + eMat[0][2] * tMat[2][2];
    newTMat[1][0] = eMat[1][0] * tMat[0][0] + eMat[1][1] * tMat[1][0]
        + eMat[1][2] * tMat[2][0];
    newTMat[1][1] = eMat[1][0] * tMat[1][0] + eMat[1][1] * tMat[1][1]
        + eMat[1][2] * tMat[2][1];
    newTMat[1][2] = eMat[1][0] * tMat[2][0] + eMat[1][1] * tMat[1][2]
        + eMat[1][2] * tMat[2][2];
    newTMat[2][0] = eMat[2][0] * tMat[0][0] + eMat[2][1] * tMat[1][0]
        + eMat[2][2] * tMat[2][0];
    newTMat[2][1] = eMat[2][0] * tMat[1][0] + eMat[2][1] * tMat[1][1]
        + eMat[2][2] * tMat[2][1];
    newTMat[2][2] = eMat[2][0] * tMat[2][0] + eMat[2][1] * tMat[1][2]
        + eMat[2][2] * tMat[2][2];
    newTMat[3][0] = tMat[3][0];
    newTMat[3][1] = tMat[3][1];
    newTMat[3][2] = tMat[3][2];
    tMat = newTMat;
  }

  /**
   * makes the transformation matrix orthonormal
   */
  private void orthogonalizeTMat() {
    final float[][] qMat = new float[4][3];
    final float lenX0 = (float) Math.sqrt((tMat[0][0] * tMat[0][0]
        + tMat[1][0] * tMat[1][0] + tMat[2][0] * tMat[2][0]));
    qMat[0][0] = tMat[0][0] / lenX0;
    qMat[1][0] = tMat[1][0] / lenX0;
    qMat[2][0] = tMat[2][0] / lenX0;
    // calculate projection
    final float proj = tMat[0][1] * qMat[0][0] + tMat[1][1] * qMat[1][0] + tMat[2][1]
        * qMat[2][0];
    // make q1
    qMat[0][1] = tMat[0][1] - proj * qMat[0][0];
    qMat[1][1] = tMat[1][1] - proj * qMat[1][0];
    qMat[2][1] = tMat[2][1] - proj * qMat[2][0];
    // normalize q1
    final float lenQ1 = (float) Math.sqrt((qMat[0][1] * qMat[0][1]
        + qMat[1][1] * qMat[1][1] + qMat[2][1] * qMat[2][1]));
    qMat[0][1] = qMat[0][1] / lenQ1;
    qMat[1][1] = qMat[1][1] / lenQ1;
    qMat[2][1] = qMat[2][1] / lenQ1;
    // calculate projections
    final float proj0 = tMat[0][2] * qMat[0][0] + tMat[1][2] * qMat[1][0]
        + tMat[2][2] * qMat[2][0];
    final float proj1 = tMat[0][2] * qMat[0][1] + tMat[1][2] * qMat[1][1]
        + tMat[2][2] * qMat[2][1];
    // make q2
    qMat[0][2] = tMat[0][2] - proj0 * qMat[0][0] - proj1 * qMat[0][1];
    qMat[1][2] = tMat[1][2] - proj0 * qMat[1][0] - proj1 * qMat[1][1];
    qMat[2][2] = tMat[2][2] - proj0 * qMat[2][0] - proj1 * qMat[2][1];
    // Normalize q2
    final float lenQ2 = (float) Math.sqrt((qMat[0][2] * qMat[0][2]
        + qMat[1][2] * qMat[1][2] + qMat[2][2] * qMat[2][2]));
    qMat[0][2] = qMat[0][2] / lenQ2;
    qMat[1][2] = qMat[1][2] / lenQ2;
    qMat[2][2] = qMat[2][2] / lenQ2;
    qMat[3][0] = tMat[3][0];
    qMat[3][1] = tMat[3][1];
    qMat[3][2] = tMat[3][2];
    tMat = qMat;
  }

  /**
   * Center the model origin on the canvas. That is, move the origin of the
   * model (which it rotates about) to the center of the canvas (where it is
   * tPoints to).
   */
  public void centerOrigin() {
    // tMat[3] is the "translation-after-rotation" vector
    if (stereo) {
      tMat[3][0] = session.plotManager.canvas.w / 4;
    }
    else {
      tMat[3][0] = session.plotManager.canvas.w / 2;
    }
    tMat[3][1] = session.plotManager.canvas.h / 2;
    tMat[3][2] = 0.0f;
    session.plotManager.canvas.setStale(true);
  }

  public void compose() {
    //session.setWaitCursor(true);
    this.orthogonalizeTMat();
    switch (session.plotManager.getDim()) {
    case 2:
      if (AUTOCOMPOSE_ROTATIONAL_CENTERING_MODE) {
        session.plotManager.subject.moveOriginToCOG(); // so it rotates at center
                                                    // of "gravity"
      }
      if (AUTOCOMPOSE_SCREEN_CENTERING_MODE) {
        centerOrigin(); // on canvas
      }
      if (AUTOCOMPOSE_PERSPECTIVE_MODE) {
        setPerspective2D();
      }
      break;
    case 3:
      if (AUTOCOMPOSE_ROTATIONAL_CENTERING_MODE) {
        session.plotManager.subject.moveOriginToCOG();
      }
      if (AUTOCOMPOSE_SCREEN_CENTERING_MODE) {
        centerOrigin();
      }
      if (AUTOCOMPOSE_PERSPECTIVE_MODE) {
        setPerspective3D();
      }
      if (AUTOCOMPOSE_LIGHTING_MODE) {
        setLighting();
      }
      if (AUTOCOMPOSE_LIGHTING_MODE) {
        stereoAngle = 0.1f;
        stereoSeparation = session.plotManager.canvas.w / 2;
      }
      break;
    default:
    }
    //session.setWaitCursor(false);
  }

  private void setPerspective2D() {
    float f0, f1;
    // System.out.print("SETFOCAL2D ");
    // System.out.println("model
    // xmax-xmin="+(session.model.max[0]-session.model.min[0]));
    // System.out.println("model
    // ymax-ymin="+(session.model.max[1]-session.model.min[1]));
    session.plotManager.subject.calculateExtents();
    if (Float.isInfinite(session.plotManager.subject.min[0])
        || Float.isInfinite(session.plotManager.subject.max[0])
        || Float.isInfinite(session.plotManager.subject.min[1])
        || Float.isInfinite(session.plotManager.subject.max[1])) {
      return; // probably no subject at all
    }
    // System.out.println("SUBJECT initial MINX: "+session.subject.min[0]+"
    // MINY: "
    // +session.subject.min[1]+" MAXX: "+session.subject.max[0]
    // +" MAXY: "+session.subject.max[1]);
    if (focal < 1.0E-12 /* arbitrary */) {
      focal = startingFocal;
    }
    float prevFocal;
    int iter = 0;
    do {
      prevFocal = focal;
      calculateExtents();
      // System.out.println("VIEW MINX: "+min[0]+" MINY: "+min[1]+" MAXX: "
      // +max[0]+" MAXY: "+max[1]);
      // Now modify focal to fit inside the canvas snugly
      f0 = (max[0] - min[0]) / session.plotManager.canvas.w;
      f1 = (max[1] - min[1]) / session.plotManager.canvas.h;
      if (f1 == 0.0f) {
        if (f0 == 0.0f) {
          focal = 1.0f;
        } // arbitrary, but doesn't matter
        else {
          focal /= f0;
        }
      }
      else if (f0 == 0.0f) {
        if (f1 == 0.0f) {
          focal = 1.0f;
        } // arbitrary, but doesn't matter
        else {
          focal /= f1;
        }
      }
      else {
        focal /= Math.max(f0, f1);
      }
      if (session.plotManager.subject.axes != null) {
        session.plotManager.subject.axes.buildLabelsAndTics(); // focal changed
        if (AUTOCOMPOSE_ROTATIONAL_CENTERING_MODE) {
          session.plotManager.subject.moveOriginToCOG(); // since focal changes
                                                      // ticlength
        }
      }
    } while (Math.abs((focal - prevFocal) / prevFocal) > 0.01 && iter++ < 15);
  }

  private void setLighting() {
    // Recall that rotation occurs about the origin. We want the
    // farthest distance away from the origin to use in our light model.
    float f = Math.abs(session.plotManager.subject.min[0]);
    if (f < Math.abs(session.plotManager.subject.min[1])) {
      f = Math.abs(session.plotManager.subject.min[1]);
    }
    if (f < Math.abs(session.plotManager.subject.min[2])) {
      f = Math.abs(session.plotManager.subject.min[2]);
    }
    if (f < Math.abs(session.plotManager.subject.max[0])) {
      f = Math.abs(session.plotManager.subject.max[0]);
    }
    if (f < Math.abs(session.plotManager.subject.max[1])) {
      f = Math.abs(session.plotManager.subject.max[1]);
    }
    if (f < Math.abs(session.plotManager.subject.max[2])) {
      f = Math.abs(session.plotManager.subject.max[2]);
    }
    if (f != 0) {
      lightRamp = f; // along z-axis
      lightPosition = f;
    }
    else { // for a single point
      lightRamp = 1.0f;
      lightPosition = 1.0f;
    }
    // session.changedState(this);
    session.plotManager.canvas.setStale(true);
  }

  private void setPerspective3D() {
    float maxDiff, f0, f1;
    // System.out.print("SETFOCAL3D ");
    // The view transformation will rotate the subject about its
    // subject origin. We want the cop to be behind any possible rotation.
    session.plotManager.subject.calculateExtents();
//     System.out.println("subject: min[0]: "+subject.min[0]+"max[0]: "+subject.max[0]);
//     System.out.println("subject: min[1]: "+subject.min[1]+"max[1]: "+subject.max[1]);
//     System.out.println("subject: min[2]: "+subject.min[2]+"max[2]: "+subject.max[2]);
    if (Float.isInfinite(session.plotManager.subject.min[0])
        || Float.isInfinite(session.plotManager.subject.max[0])
        || Float.isInfinite(session.plotManager.subject.min[1])
        || Float.isInfinite(session.plotManager.subject.max[1])
        || Float.isInfinite(session.plotManager.subject.min[2])
        || Float.isInfinite(session.plotManager.subject.max[2])) {
      return; // probably no subject at all
    }
    maxDiff = Math.max( // this is an approximation to the largest dimension of
                        // the subject
        Math.max(session.plotManager.subject.max[0]
            - session.plotManager.subject.min[0], session.plotManager.subject.max[1]
            - session.plotManager.subject.min[1]), session.plotManager.subject.max[2]
            - session.plotManager.subject.min[2]);
    if (maxDiff == 0.0f) { // single point
      cop = -1.0f;
      focal = 0.5f;
      return;
    }
    final float midX = (session.plotManager.subject.max[0] + session.plotManager.subject.min[0]) / 2.0f;
    final float midY = (session.plotManager.subject.max[1] + session.plotManager.subject.min[1]) / 2.0f;
    final float midZ = (session.plotManager.subject.max[2] + session.plotManager.subject.min[2]) / 2.0f;
    cop = -10
        * ((float) Math.sqrt(midX * midX + midY * midY + midZ * midZ) + maxDiff);
    // System.out.println("COP: "+cop);
    if (focal == startingFocal || focal < 1.0E-12 /* arbitrary */) {
      focal = Math.abs(cop) / 2.0f; // put halfway
    }
    float prevFocal;
    int iter = 0;
    do {
      prevFocal = focal;
      // Now modify focal to fit the view inside the canvas snugly
      calculateExtents();
      // System.out.println("view: min[0]: "+min[0]+"max[0]: "+max[0]);
      // System.out.println("view: min[1]: "+min[1]+"max[1]: "+max[1]);
      // System.out.println("view: min[2]: "+min[2]+"max[2]: "+max[2]);
      f0 = (max[0] - min[0]) / session.plotManager.canvas.w;
      f1 = (max[1] - min[1]) / session.plotManager.canvas.h;
      // use the smallest non-zero focal
      if (f1 == 0.0f) {
        if (f0 == 0.0f) {
          focal = -cop / 2; // arbitrary, but doesn't matter
          // System.out.println("focal return1: "+ focal);
          return;
        }
        focal /= f0;
          // System.out.println("focal return3: "+ focal);
      }
      else if (f0 == 0.0f) {
        if (f1 == 0.0f) {
          focal = -cop / 2; // arbitrary, but doesn't matter
          // System.out.println("focal return2: "+ focal);
          return;
        }
        focal /= f1;
      }
      else {
        focal /= Math.max(f0, f1);
         //System.out.println("focal return4: "+ focal);
      }
      if (session.plotManager.subject.axes != null) {
        session.plotManager.subject.axes.buildLabelsAndTics(); // since focal
                                                            // changed
        if (AUTOCOMPOSE_ROTATIONAL_CENTERING_MODE) {
          session.plotManager.subject.moveOriginToCOG(); // since focal changes
                                                      // ticlength
        }
      }
      // System.out.println("FOCAL: " + focal);
      // System.out.print("$");
      // System.out.println("subject: min[0]: "+subject.min[0]+"max[0]:
      // "+subject.max[0]);
      // System.out.println("subject: min[1]: "+subject.min[1]+"max[1]:
      // "+subject.max[1]);
      // System.out.println("subject: min[2]: "+subject.min[2]+"max[2]:
      // "+subject.max[2]);
    } while (Math.abs((focal - prevFocal) / prevFocal) > 0.01 && iter++ < 15);
  }

  void stereoizeTmat() {
    prevTmat[0][0] = tMat[0][0];
    prevTmat[0][1] = tMat[0][1];
    prevTmat[0][2] = tMat[0][2];
    prevTmat[1][0] = tMat[1][0];
    prevTmat[1][1] = tMat[1][1];
    prevTmat[1][2] = tMat[1][2];
    prevTmat[2][0] = tMat[2][0];
    prevTmat[2][1] = tMat[2][1];
    prevTmat[2][2] = tMat[2][2];
    prevTmat[3][0] = tMat[3][0];
    prevTmat[3][1] = tMat[3][1];
    prevTmat[3][2] = tMat[3][2];
    rotate(stereoAngle, 0.0f, 1.0f);
    tMat[3][0] += stereoSeparation;
    session.plotManager.canvas.setStale(true);
  }

  void unstereoizeTmat() {
    float[][] tmp;
    tmp = tMat; // swap
    tMat = prevTmat;
    prevTmat = tmp;
    session.plotManager.canvas.setStale(true);
  }

  private void calculateExtents() {
    min[0] = Float.POSITIVE_INFINITY;
    min[1] = Float.POSITIVE_INFINITY;
    min[2] = Float.POSITIVE_INFINITY;
    max[0] = Float.NEGATIVE_INFINITY;
    max[1] = Float.NEGATIVE_INFINITY;
    max[2] = Float.NEGATIVE_INFINITY;
    for (final Pointset pointset : session.pointsetManager.getPointsets()) {
      if (pointset.getNVisible() == 0) {
        continue;
      }
      pointset.calculateViewExtents();
      if (pointset.tMin[0] < min[0]) {
        min[0] = pointset.tMin[0];
      }
      if (pointset.tMin[1] < min[1]) {
        min[1] = pointset.tMin[1];
      }
      if (pointset.tMin[2] < min[2]) {
        min[2] = pointset.tMin[2];
      }
      if (pointset.tMax[0] > max[0]) {
        max[0] = pointset.tMax[0];
      }
      if (pointset.tMax[1] > max[1]) {
        max[1] = pointset.tMax[1];
      }
      if (pointset.tMax[2] > max[2]) {
        max[2] = pointset.tMax[2];
      }
    }
    //System.out.println("VIEW AFTER DATA: " + min[2] + " " +max[2]);
    for (final Text text : session.plotManager.getTextList()) {
      if (!text.visible) {
        continue;
      }
      text.calculateViewExtents();
      if (text.tMin[0] < min[0]) {
        min[0] = text.tMin[0];
      }
      if (text.tMin[1] < min[1]) {
        min[1] = text.tMin[1];
      }
      if (text.tMin[2] < min[2]) {
        min[2] = text.tMin[2];
      }
      if (text.tMax[0] > max[0]) {
        max[0] = text.tMax[0];
      }
      if (text.tMax[1] > max[1]) {
        max[1] = text.tMax[1];
      }
      if (text.tMax[2] > max[2]) {
        max[2] = text.tMax[2];
      }
    }
    //System.out.println("VIEW TEXT MINX: "+min[0]+" MINY: "+min[1]+" MAXX: " +max[0]+" MAXY: "+max[1]);
    if (session.plotManager.subject.axes != null) {
      session.plotManager.subject.axes.calculateViewExtents();
      if (session.plotManager.subject.axes.tMin[0] < min[0]) {
        min[0] = session.plotManager.subject.axes.tMin[0];
      }
      if (session.plotManager.subject.axes.tMin[1] < min[1]) {
        min[1] = session.plotManager.subject.axes.tMin[1];
      }
      if (session.plotManager.subject.axes.tMin[2] < min[2]) {
        min[2] = session.plotManager.subject.axes.tMin[2];
      }
      if (session.plotManager.subject.axes.tMax[0] > max[0]) {
        max[0] = session.plotManager.subject.axes.tMax[0];
      }
      if (session.plotManager.subject.axes.tMax[1] > max[1]) {
        max[1] = session.plotManager.subject.axes.tMax[1];
      }
      if (session.plotManager.subject.axes.tMax[2] > max[2]) {
        max[2] = session.plotManager.subject.axes.tMax[2];
      }
    }
// System.out.println("VIEW AXES X: min: " + min[0] + " max: " + max[0]);
// System.out.println("VIEW AXES Y: min: " + min[1] + " max: " + max[1]);
// System.out.println("VIEW AXES Z: min: " + min[2] + " max: " + max[2]);
    if (stereo) {
      max[0] += stereoSeparation;
    }
    min[0] -= margin * session.dotsPerMM;
    max[0] += margin * session.dotsPerMM;
    min[1] -= margin * session.dotsPerMM;
    max[1] += margin * session.dotsPerMM;
//System.out.println("VIEW AXES MINX: "+min[0]+" MINY: "+min[1]+" MAXX: " +max[0]+" MAXY: "+max[1]);
//System.out.println("VIEW MINX: "+min[0]+" MINY: "+min[1]+" MAXX: "+max[0]+" MAXY: "+max[1]);
  }

  public float[] unTransform2D(final float[] t) {
    final float[] unT = new float[3];
    final float f = focal;
    // recall: the inverse of an orthonormal matrix is its transpose
    unT[0] = (t[0] - tMat[3][0]) * tMat[0][0] / f + (t[1] - tMat[3][1])
        * tMat[0][1] / f;
    unT[1] = (t[0] - tMat[3][0]) * tMat[1][0] / f + (t[1] - tMat[3][1])
        * tMat[1][1] / f;
    return unT;
  }

  public float[] unTransform3D(final float[] t) {
    final float[] unT = new float[3];
    final float f = getPerspectiveFactor(t[2]);
    // recall: the inverse of an orthonormal matrix is its transpose
    unT[0] = (t[0] - tMat[3][0]) * tMat[0][0] / f + (t[1] - tMat[3][1])
        * tMat[0][1] / f + (t[2] - tMat[3][2]) * tMat[0][2];
    unT[1] = (t[0] - tMat[3][0]) * tMat[1][0] / f + (t[1] - tMat[3][1])
        * tMat[1][1] / f + (t[2] - tMat[3][2]) * tMat[1][2];
    unT[2] = (t[0] - tMat[3][0]) * tMat[2][0] / f + (t[1] - tMat[3][1])
        * tMat[2][1] / f + (t[2] - tMat[3][2]) * tMat[2][2];
    return unT;
  }

  public void moveCop(final float f) {
    cop += f;
    // System.out.println("COP NOW AT: "+cop);
    session.plotManager.canvas.setStale(true);
  }

  void moveImagePlane(final float f) {
    focal += f;
    // if (imagePlane < cop) imagePlane = cop;
    // focal = imagePlane-cop;
    // else if (imagePlane > cop) focal = cop;
    // System.out.println("(focal length is now: "+focal+")");
    session.plotManager.canvas.setStale(true);
  }

  public float getPerspectiveFactor(final float z) {
    final float f = z - cop;
    // System.out.println("f2: "+f);
    // System.out.println("focal: "+focal);
    session.plotManager.canvas.setStale(true);
    /* if (f > 0.0) */return focal / f;
    // else return Float.NaN; // clipped
    // return focal;
  }

  boolean getStereo() {
    return stereo;
  }

  void setStereo(final boolean b) {
    if (b && !stereo) {
      session.plotManager.canvas.setStale(true);
    }
    stereo = b;
  }

  /**
   * 1.0 keeps the image the same, >1.0 magnifies, <1.0 reduces
   */
  public void magnify(final float f) {
    focal *= f;
    if (session.plotManager.subject.axes != null) {
      switch (session.plotManager.getDim()) {
      case 2:
        session.plotManager.subject.axes.buildLabelsAndTics();
        break;
      case 3:
        session.plotManager.subject.axes.buildLabelsAndTics();
        break;
      }
    }
    session.plotManager.canvas.setStale(true);
  }

  public void zoom(final int loX, final int hiX, final int loY, final int hiY) {
    zoomed = true;
    if (session.plotManager.subject != null) {
      // order is important because text is hidden before axes redrawn
      for (int i = 0; i < session.pointsetManager.pointsetCount(); i++) { // order is
                                                              // important
        // System.out.println("DATA BEFORE ZOOM:");
        // ((Pointset)Pointset.v.get(i)).dumpUserSpace();
        (session.pointsetManager.getPointsetAt(i)).zoom(loX, hiX, loY, hiY);
        // System.out.println("DATA AFTER ZOOM:");
        // ((Pointset)Pointset.v.get(i)).dumpUserSpace();
      }
      // go backwards because the zoomed text is being removed
      for (int i = session.plotManager.textCount() - 1; i >= 0; i--) {
        // System.out.println("TEXT BEFORE ZOOM:");
        // ((Text)Text.list.get(i)).dumpUserSpace();
        (session.plotManager.getText(i)).zoom(loX, hiX, loY, hiY);
        // System.out.println("TEXT AFTER ZOOM:");
        // ((Text)Text.list.get(i)).dumpUserSpace();
      }
      if (session.plotManager.subject.axes != null) {
        // System.out.println("AXES BEFORE ZOOM:");
        // Subject.axes.dumpUserSpace();
        session.plotManager.subject.axes.zoom(loX, hiX, loY, hiY);
        // System.out.println("AXES AFTER ZOOM:");
        // Subject.axes.dumpUserSpace();
      }
    }
    session.plotManager.canvas.smartCompose();
    session.plotManager.refreshCanvas();
  }

  public void unzoom() {
    int i;
    zoomed = false;
    if (session.plotManager.subject != null) {
      final int len = session.pointsetManager.pointsetCount();
      for (i = len - 1; i >= 0; i--) { // order is important
        // System.out.println("DATA BEFORE UNZOOM:");
        // ((Pointset)Pointset.v.get(i)).dumpUserSpace();
        (session.pointsetManager.getPointsetAt(i)).unzoom();
        // System.out.println("DATA AFTER UNZOOM:");
        // ((Pointset)Pointset.v.get(i)).dumpUserSpace();
      }
      // go backwards because the zoomed text is being removed
      for (i = session.plotManager.textCount() - 1; i >= 0; i--) {
        // System.out.println("TEXT BEFORE UNZOOM:");
        // ((Text)Text.list.get(i)).dumpUserSpace();
        (session.plotManager.getText(i)).unzoom();
        // System.out.println("TEXT AFTER UNZOOM:");
        // ((Text)Text.list.get(i)).dumpUserSpace();
      }
      if (session.plotManager.subject.axes != null) {
        session.plotManager.subject.axes.unzoom();
      }
      session.plotManager.subject.calculateExtents();
      session.plotManager.canvas.smartCompose();
      session.plotManager.refreshCanvas();
      // session.updateButtonStates();
    }
  }

  void dump() {
    System.out.println("\n\t" + tMat[0][0] + "\t" + tMat[0][1] + "\t"
        + tMat[0][2]);
    System.out.println("\t" + tMat[1][0] + "\t" + tMat[1][1] + "\t"
        + tMat[1][2]);
    System.out.println("\t" + tMat[2][0] + "\t" + tMat[2][1] + "\t"
        + tMat[2][2]);
    System.out.println("\t" + tMat[3][0] + "\t" + tMat[3][1] + "\t"
        + tMat[3][2]);
  }

  @Override
	public void setSession(final Session session) {
    this.session = session;  
  }

  public void setInertia(final boolean inertia) {
    this.inertia = inertia;
    session.plotManager.canvas.updateInertiaTimer();
  }

  public void toggleInertia() throws Exception {
    setInertia(!inertia);
  }

  public boolean getInertia() {
    return inertia;
  }

  public boolean isInertiaXYDirection() {
    return inertiaXYDirection;
  }

  public void setInertiaXYDirection(boolean inertiaXYDirection) {
    this.inertiaXYDirection = inertiaXYDirection;
  }
  
  public void setOrthogonalMode(final boolean b) {
    orthogonalMode = b;
    if (orthogonalMode) {
      if (session.plotManager.view.zoomed) {
        session.plotManager.view.unzoom();
        session.plotManager.canvas.setDragMode(DragModeKind.NO_DRAG_MODE);
      }
      session.plotManager.view.reset();
      session.plotManager.refreshCanvas();
    }
    session.plotManager.canvas.updateActions();
  }

  void toggleOrthogonalMode() {
    setOrthogonalMode(!orthogonalMode);
  }

  public boolean getOrthogonalMode() {
    return orthogonalMode;
  }
}
// end of file
