package gov.nih.ncgc.openhts.tool1.plotManager;

import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.persistence.Persistent;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;

/**
 * subject is the "model" in the Model-View pattern, it contains all the Drawables
 */
public final class Subject implements Persistent{

  private transient Session session;

  // The coordinates of the data, axes, and text are kept in
  // Subject space.  To convert them back to User space, add cumDelta.
  // To move them back, subtract cumDelta.
  float min[], max[];
  public float cumDelta[];
  public transient Axes axes;

  public Subject(final Session session) {

    this.session = session;
  }

  /**
   * Resets the state of the object to initial without reconstructing it
   */
  public void initialize() {

    min = new float[3];
    max = new float[3];
    cumDelta = new float[3];
    cumDelta[0] = 0.0f;
    cumDelta[1] = 0.0f;
    cumDelta[2] = 0.0f;
    initializeTransient();
  }

  @Override
	public void initializeTransient() {
    axes = new Axes(session);
    axes.initialize();
  }

  public void moveToUserSpace() {

    session.pointsetManager.movePointsetsToUserSpace();

    for (final Text text : session.plotManager.getTextList()) {
      text.moveToUserSpace();
    }

    if (axes != null) {
      axes.moveToUserSpace();
    }
  }

  // this is only called somewhere else once
  public void moveToSubjectSpace() {

    session.pointsetManager.movePointsetsToSubjectSpace();

    for (final Text text : session.plotManager.getTextList()) {
      text.moveToSubjectSpace();
    }

    if (axes != null) {
      axes.moveToSubjectSpace();
    }
  }

  /**
   * Get the extents of the whole subject.  Entries are responsible for
   * maintaining their own extents, this just compares them.
   */
  public void calculateExtents() {

    int j;

    min[0] = Float.POSITIVE_INFINITY;
    min[1] = Float.POSITIVE_INFINITY;
    min[2] = Float.POSITIVE_INFINITY;
    max[0] = Float.NEGATIVE_INFINITY;
    max[1] = Float.NEGATIVE_INFINITY;
    max[2] = Float.NEGATIVE_INFINITY;

    Pointset pointset;
    for (j=0; j < session.pointsetManager.pointsetCount(); j++) {
      pointset = session.pointsetManager.getPointsetAt(j);

//System.out.println("DATA "+data.name+"min[0]: " + data.min[0] +
//                   " min[1]: " + data.min[1] +
//                   " min[2]: " + data.min[2]);

      if (pointset.min[0] < min[0]) {min[0] = pointset.min[0];}
      if (pointset.min[1] < min[1]) {min[1] = pointset.min[1];}
      if (pointset.min[2] < min[2]) {min[2] = pointset.min[2];}


      if (pointset.max[0] > max[0]) {max[0] = pointset.max[0];}
      if (pointset.max[1] > max[1]) {max[1] = pointset.max[1];}
      if (pointset.max[2] > max[2]) {max[2] = pointset.max[2];}
    }

//System.out.println("SUBJECT EXTENTS after DATA, min[0]: "+min[0]
//                                    +" min[1]: "+min[1]
//                                     +" min[2]: "+min[2]);

//System.out.println("SUBJECT EXTENTS after DATA, max[0]: "+max[0]
//                                     +" max[1]: "+max[1]
//                                     +" max[2]: "+max[2]);

    for (final Text text : session.plotManager.getTextList()) {
      if (!text.visible || !text.moving) {
        continue;
      }
//System.out.println("TEXT ("+ text.string +
//                   ") min[0]: " + text.min[0] +
//                   " min[1]: " + text.min[1] +
//                   " min[2]: " + text.min[2]);
      if (text.min[0] < min[0]) {min[0] = text.min[0];}
      if (text.min[1] < min[1]) {min[1] = text.min[1];}
      if (text.min[2] < min[2]) {min[2] = text.min[2];}

//System.out.println("TEXT max[0]: " + text.max[0] +
//                   " max[1]: " + text.max[1] +
//                   " max[2]: " + text.max[2]);
      if (text.max[0] > max[0]) {max[0] = text.max[0];}
      if (text.max[1] > max[1]) {max[1] = text.max[1];}
      if (text.max[2] > max[2]) {max[2] = text.max[2];}
    }

//System.out.println("SUBJECT EXTENTS after TEXT, min[0]: "+min[0]
//                                     +" min[1]: "+min[1]
//                                     +" min[2]: "+min[2]);

//System.out.println("SUBJECT EXTENTS after TEXT, max[0]: "+max[0]
//                                     +" max[1]: "+max[1]
//                                     +" max[2]: "+max[2]);

    if (axes != null) {

//System.out.println("AXES min[0]: " + axes.min[0] +
//                   " AXES min[1]: " + axes.min[1] +
//                   " AXES min[2]: " + axes.min[2]);
      if (axes.min[0] < min[0]) {min[0] = axes.min[0];}
      if (axes.min[1] < min[1]) {min[1] = axes.min[1];}
      if (axes.min[2] < min[2]) {min[2] = axes.min[2];}

//System.out.println("AXES max[0]: " + axes.max[0] +
//                   " AXES max[1]: " + axes.max[1] +
//                   " AXES max[2]: " + axes.max[2]);
      if (axes.max[0] > max[0]) {max[0] = axes.max[0];}
      if (axes.max[1] > max[1]) {max[1] = axes.max[1];}
      if (axes.max[2] > max[2]) {max[2] = axes.max[2];}
    }

//System.out.println("SUBJECT EXTENTS after AXES, min[0]: "+min[0]
//                                    +" min[1]: "+min[1]
//                                     +" min[2]: "+min[2]);

//System.out.println("SUBJECT EXTENTS after AXES, max[0]: "+max[0]
//                                     +" max[1]: "+max[1]
//                                     +" max[2]: "+max[2]);
  }

  /**
   * rotates about origin
   */
  void moveOrigin() {

    final float[] delta=new float[3];
    float[] origin;
    float[] oldOrigin;

//System.out.println("ORIGIN OF VIEW: "
//+plotter.view.tMat[3][0]+" "
//+plotter.view.tMat[3][1]+" "
//+plotter.view.tMat[3][2]);

    // get the old origin in model space
    oldOrigin=session.plotManager.view.unTransform3D(session.plotManager.view.tMat[3]);

//System.out.println("OLD ORIGIN OF MODEL: "
//+oldOrigin[0]+" "
//+oldOrigin[1]+" "
//+oldOrigin[2]);

    // get the new origin in model space
    origin=session.plotManager.view.unTransform3D(session.plotManager.view.origin);

//System.out.println("NEW ORIGIN OF MODEL: "
//+origin[0]+" "
//+origin[1]+" "
//+origin[2]);

    // calculate the delta origin
    delta[0] = origin[0] - oldOrigin[0];
    delta[1] = origin[1] - oldOrigin[1];
    delta[2] = origin[2] - oldOrigin[2];

//System.out.println("DELTA NEW ORIGIN OF MODEL: "
//+origin[0]+" "
//+origin[1]+" "
//+origin[2]);

    translate(delta);
  }

  /**
   * This moves the subject with respect to its origin by
   * adding the vector delta.
   */
  void translate(final float[] delta) {

    cumDelta[0] += delta[0];
    cumDelta[1] += delta[1];
    cumDelta[2] += delta[2];

//System.out.println("cumDelta is now:" + cumDelta[0] + ", " +cumDelta[1] + ", " + cumDelta[2]);

    for (int j=0; j < session.pointsetManager.pointsetCount(); j++) {
      final Pointset pointset = session.pointsetManager.getPointsetAt(j);
      pointset.translate(delta);
    }
    for (final Text text :  session.plotManager.getTextList()) {
      text.translate(delta);
    }
    if (axes != null) {
      axes.translate(delta);
    }

    session.plotManager.canvas.setStale(true);
    //calculateExtents();
    //plotter.changedState(this);
  }

  /**
   * Put the rotational center at the center of "gravity"
   */
  void moveOriginToCOG() {

//System.out.println("MOVING ORIGIN TO COG");
    calculateExtents();

    if (Float.isInfinite(min[0])
      ||Float.isInfinite(max[0])
      ||Float.isInfinite(min[1])
      ||Float.isInfinite(max[1])
    ) {
      return; // probably no subject at all
    }
//System.out.println("SUBJECT EXTENTS MIN[0]: "+min[0] + " MAX[0]: "+max[0]);
//System.out.println("SUBJECT EXTENTS MIN[1]: "+min[1] + " MAX[1]: "+max[1]);

    final float[] delta={0.0f,0.0f,0.0f};

    delta[0] = (max[0] + min[0]) / 2.0f;
    delta[1] = (max[1] + min[1]) / 2.0f;
    if (session.plotManager.getDim() == 3) {
      if (Float.isInfinite(min[2]) || Float.isInfinite(max[2])) {return;}
      delta[2] = (max[2] + min[2]) / 2.0f;
    }
//System.out.println("Delta is: "+delta[0]+","+delta[1]+","+delta[2]);

    translate(delta);
    calculateExtents();
  }

  public void moveOriginBack() {

    final float[] delta = {0.0f,0.0f,-5.0f};
    translate(delta);
  }

  public void moveOriginForward() {

    final float[] delta={0.0f,0.0f,5.0f};
    translate(delta);
  }

  /**
   * This is called after all the entries have reset already.
   */
  void resetDelta() {

    cumDelta[0] = 0.0f;
    cumDelta[1] = 0.0f;
    cumDelta[2] = 0.0f;
  }

  public boolean isEmpty() {

    if (axes == null && session.pointsetManager.pointsetCount() <= 0 && session.plotManager.textCount() <= 0) {
      return true;
    }
    else {return false;}
  }

  @Override
	public void setSession(final Session session) {
    this.session = session;
  }
}

// end of file
