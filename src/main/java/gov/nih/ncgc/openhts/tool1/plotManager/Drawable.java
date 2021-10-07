package gov.nih.ncgc.openhts.tool1.plotManager;

import gov.nih.ncgc.openhts.tool1.Session;

/** Purpose is to ...
 * @author talafousj
 */
public abstract class Drawable {

  protected transient Session session;
  public float[] min={Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY};
  public float[] max={Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY};
  protected float[] tMin={Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY};
  protected float[] tMax={Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY};
  protected float[][] boundingBox,tBoundingBox; // subject bounding Box and tPoints Bounding Box
  protected String name;  // internal name used in xpl file loading monitoring
  protected boolean visible = true;

  public Drawable(final Session session) {

    this.session = session;
    boundingBox = new float[8][3];
    tBoundingBox = new float[8][3];
  }

  public abstract void draw2D(Slate slate, boolean notAlwaysUsed);

  public abstract void draw3D(Slate slate, boolean notAlwaysUsed);

  public abstract void zoom(int loX, int hiX, int loY, int hiY);

  public abstract void unzoom();

  public abstract void transform2D(boolean drawEverythingForMeasuringTime);

  public abstract void transform3D(boolean drawEverythingForMeasuringTime);

  public void calculateViewExtents() {

    final int n = session.plotManager.getDim();

    boundingBox[0][0] = min[0];
    boundingBox[0][1] = min[1];
    boundingBox[0][2] = min[2];

    boundingBox[1][0] = max[0];
    boundingBox[1][1] = min[1];
    boundingBox[1][2] = min[2];

    boundingBox[2][0] = max[0];
    boundingBox[2][1] = max[1];
    boundingBox[2][2] = min[2];

    boundingBox[3][0] = min[0];
    boundingBox[3][1] = max[1];
    boundingBox[3][2] = min[2];

    boundingBox[4][0] = min[0];
    boundingBox[4][1] = min[1];
    boundingBox[4][2] = max[2];

    boundingBox[5][0] = max[0];
    boundingBox[5][1] = min[1];
    boundingBox[5][2] = max[2];

    boundingBox[6][0] = max[0];
    boundingBox[6][1] = max[0];
    boundingBox[6][2] = max[2];

    boundingBox[7][0] = min[0];
    boundingBox[7][1] = max[1];
    boundingBox[7][2] = max[2];

    tMin[0] = Float.POSITIVE_INFINITY;
    tMin[1] = Float.POSITIVE_INFINITY;
    tMin[2] = Float.POSITIVE_INFINITY;
    tMax[0] = Float.NEGATIVE_INFINITY;
    tMax[1] = Float.NEGATIVE_INFINITY;
    tMax[2] = Float.NEGATIVE_INFINITY;

    // Transform the bounding box of the entry.
    switch(n) {
    case 2:
      for (int i=0; i<4; i++) {
        tBoundingBox[i][0] =
          (boundingBox[i][0] * session.plotManager.view.tMat[0][0]
          + boundingBox[i][1] * session.plotManager.view.tMat[1][0]) * session.plotManager.session.plotManager.view.focal
          + session.plotManager.view.tMat[3][0];

        tBoundingBox[i][1] =
          (boundingBox[i][0] * session.plotManager.view.tMat[0][1]
          + boundingBox[i][1] * session.plotManager.view.tMat[1][1]) * session.plotManager.session.plotManager.view.focal
          + session.plotManager.view.tMat[3][1];

        if (tBoundingBox[i][0] < tMin[0]) {tMin[0] = tBoundingBox[i][0];}
        if (tBoundingBox[i][1] < tMin[1]) {tMin[1] = tBoundingBox[i][1];}
        if (tBoundingBox[i][0] > tMax[0]) {tMax[0] = tBoundingBox[i][0];}
        if (tBoundingBox[i][1] > tMax[1]) {tMax[1] = tBoundingBox[i][1];}
      }
      break;

    case 3:
      for (int i=0; i<8; i++) {
        tBoundingBox[i][2] =
          boundingBox[i][0] * session.plotManager.view.tMat[0][2]
          + boundingBox[i][1] * session.plotManager.view.tMat[1][2]
          + boundingBox[i][2] * session.plotManager.view.tMat[2][2]
          + session.plotManager.view.tMat[3][2];

        final float f = session.plotManager.view.getPerspectiveFactor(tBoundingBox[i][2]);

        tBoundingBox[i][0] =
          (boundingBox[i][0] * session.plotManager.view.tMat[0][0]
          + boundingBox[i][1] * session.plotManager.view.tMat[1][0]
          + boundingBox[i][2] * session.plotManager.view.tMat[2][0]) * f
          + session.plotManager.view.tMat[3][0];

        tBoundingBox[i][1] =
          (boundingBox[i][0] * session.plotManager.view.tMat[0][1]
          + boundingBox[i][1] * session.plotManager.view.tMat[1][1]
          + boundingBox[i][2] * session.plotManager.view.tMat[2][1]) * f
          + session.plotManager.view.tMat[3][1];

        if (tBoundingBox[i][0] < tMin[0]) {tMin[0] = tBoundingBox[i][0];}
        if (tBoundingBox[i][1] < tMin[1]) {tMin[1] = tBoundingBox[i][1];}
        if (tBoundingBox[i][2] < tMin[2]) {tMin[2] = tBoundingBox[i][2];}

        if (tBoundingBox[i][0] > tMax[0]) {tMax[0] = tBoundingBox[i][0];}
        if (tBoundingBox[i][1] > tMax[1]) {tMax[1] = tBoundingBox[i][1];}
        if (tBoundingBox[i][2] > tMax[2]) {tMax[2] = tBoundingBox[i][2];}
      }
    }
  }

  public abstract void translate(float[] delta);

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(final boolean visible) {
    // NOTE: if you call this, it WILL NOT fire a ManagerEvent
    // Call setVisible() in the Manager instead
    this.visible = visible;
  }

}

// end of file
