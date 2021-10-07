package gov.nih.ncgc.openhts.tool1.plotManager;

/**
 * <p>Title: jplotter</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * @author Joseph Talafous
 * @version 1.0
 */
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import gov.nih.ncgc.openhts.tool1.Session;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public final class Slate {
  // TODO: fix convex quad bug
  // Canvas canvas;
  Session session;
  BufferedImage image;
  DataBufferInt dataBufferInt;
  float[] zBuffer;
  boolean[] bBuffer;
  boolean stale;
  int[] saved;
  Color backColor;
  private int foreColor, foreRed, foreGreen, foreBlue;
  int xMin, xMax, yMin, yMax, w, h;

  public Slate(final Session session) {
    System.out.println("CREATED SLATE " + hashCode() + this);
    this.session = session;
  }

  void clearPixels(final Color c) {
    final int k = c.getRGB();
    int i = 0;
    final int n = w * h;
    for (i = 0; i < n; i++) {
      dataBufferInt.setElem(i, k);
      zBuffer[i] = Float.POSITIVE_INFINITY;
    }
  }

  /**
   * OVERLOAD 3D
   */
  void drawLine(int Ax, int Ay, final float Az, final int Bx, final int By,
      final float Bz) {
    // INITIALIZE THE COMPONENTS OF THE ALGORITHM THAT ARE NOT AFFECTED BY THE
    // SLOPE OR DIRECTION OF THE LINE
    int dX = Math.abs(Bx - Ax); // store the change in X and Y of the line
    // endpoints
    int dY = Math.abs(By - Ay);
    // DETERMINE "DIRECTIONS" TO INCREMENT X AND Y (REGARDLESS OF DECISION)
    int Xincr, Yincr;
    if (Ax > Bx) {
      Xincr = -1;
    }
    else {
      Xincr = 1;
    } // which direction in X?
    if (Ay > By) {
      Yincr = -1;
    }
    else {
      Yincr = 1;
    } // which direction in Y?
    // DETERMINE INDEPENDENT VARIABLE (ONE THAT ALWAYS INCREMENTS BY 1 (OR -1) )
    // AND INITIATE APPROPRIATE LINE DRAWING ROUTINE (BASED ON FIRST OCTANT
    // ALWAYS). THE X AND Y'S MAY BE FLIPPED IF Y IS THE INDEPENDENT VARIABLE.
    if (dX >= dY) { // if X is the independent variable
      final int dPr = dY << 1; // amount to increment decision if right is
      // chosen (always)
      final int dPru = dPr - (dX << 1); // amount to increment decision if up is
      // chosen
      int P = dPr - dX; // decision variable start value
      for (; dX >= 0; dX--) { // process each point in the line one at a time
        // (just use dX)
        setPixel(Ax, Ay, Az); // plot the pixel
        if (P > 0) { // is the pixel going right AND up?
          Ax += Xincr; // increment independent variable
          Ay += Yincr; // increment dependent variable
          P += dPru; // increment decision (for up)
        }
        else { // is the pixel just going right?
          Ax += Xincr; // increment independent variable
          P += dPr; // increment decision (for right)
        }
      }
    }
    else { // if Y is the independent variable
      final int dPr = dX << 1; // amount to increment decision if right is
      // chosen (always)
      final int dPru = dPr - (dY << 1); // amount to increment decision if up is
      // chosen
      int P = dPr - dY; // decision variable start value
      for (; dY >= 0; dY--) { // process each point in the line one at a time
        // (just use dY)
        setPixel(Ax, Ay, Az); // plot the pixel
        if (P > 0) { // is the pixel going up AND right?
          Ax += Xincr; // increment dependent variable
          Ay += Yincr; // increment independent variable
          P += dPru; // increment decision (for up)
        }
        else { // is the pixel just going up?
          Ay += Yincr; // increment independent variable
          P += dPr; // increment decision (for right)
        }
      }
    }
  }

  /**
   * OVERLOAD 3D
   */
  public void drawLine(int Ax, int Ay, final int Bx, final int By) {
    // INITIALIZE THE COMPONENTS OF THE ALGORITHM THAT ARE NOT AFFECTED BY THE
    // SLOPE OR DIRECTION OF THE LINE
    int dX = Math.abs(Bx - Ax); // store the change in X and Y of the line
    // endpoints
    int dY = Math.abs(By - Ay);
    // DETERMINE "DIRECTIONS" TO INCREMENT X AND Y (REGARDLESS OF DECISION)
    int Xincr, Yincr;
    if (Ax > Bx) {
      Xincr = -1;
    }
    else {
      Xincr = 1;
    } // which direction in X?
    if (Ay > By) {
      Yincr = -1;
    }
    else {
      Yincr = 1;
    } // which direction in Y?
    // DETERMINE INDEPENDENT VARIABLE (ONE THAT ALWAYS INCREMENTS BY 1 (OR -1) )
    // AND INITIATE APPROPRIATE LINE DRAWING ROUTINE (BASED ON FIRST OCTANT
    // ALWAYS). THE X AND Y'S MAY BE FLIPPED IF Y IS THE INDEPENDENT VARIABLE.
    if (dX >= dY) { // if X is the independent variable
      final int dPr = dY << 1; // amount to increment decision if right is
      // chosen (always)
      final int dPru = dPr - (dX << 1); // amount to increment decision if up is
      // chosen
      int P = dPr - dX; // decision variable start value
      for (; dX >= 0; dX--) { // process each point in the line one at a time
        // (just use dX)
        setPixel(Ax, Ay); // plot the pixel
        if (P > 0) { // is the pixel going right AND up?
          Ax += Xincr; // increment independent variable
          Ay += Yincr; // increment dependent variable
          P += dPru; // increment decision (for up)
        }
        else { // is the pixel just going right?
          Ax += Xincr; // increment independent variable
          P += dPr; // increment decision (for right)
        }
      }
    }
    else { // if Y is the independent variable
      final int dPr = dX << 1; // amount to increment decision if right is
      // chosen (always)
      final int dPru = dPr - (dY << 1); // amount to increment decision if up is
      // chosen
      int P = dPr - dY; // decision variable start value
      for (; dY >= 0; dY--) { // process each point in the line one at a time
        // (just use dY)
        setPixel(Ax, Ay); // plot the pixel
        if (P > 0) { // is the pixel going up AND right?
          Ax += Xincr; // increment dependent variable
          Ay += Yincr; // increment independent variable
          P += dPru; // increment decision (for up)
        }
        else { // is the pixel just going up?
          Ay += Yincr; // increment independent variable
          P += dPr; // increment decision (for right)
        }
      }
    }
  }

  void drawLineInBBuffer(int Ax, int Ay, final int Bx, final int By) {
    // INITIALIZE THE COMPONENTS OF THE ALGORITHM THAT ARE NOT AFFECTED BY THE
    // SLOPE OR DIRECTION OF THE LINE
    int dX = Math.abs(Bx - Ax); // store the change in X and Y of the line
    // endpoints
    int dY = Math.abs(By - Ay);
    // DETERMINE "DIRECTIONS" TO INCREMENT X AND Y (REGARDLESS OF DECISION)
    int Xincr, Yincr;
    if (Ax > Bx) {
      Xincr = -1;
    }
    else {
      Xincr = 1;
    } // which direction in X?
    if (Ay > By) {
      Yincr = -1;
    }
    else {
      Yincr = 1;
    } // which direction in Y?
    // DETERMINE INDEPENDENT VARIABLE (ONE THAT ALWAYS INCREMENTS BY 1 (OR -1) )
    // AND INITIATE APPROPRIATE LINE DRAWING ROUTINE (BASED ON FIRST OCTANT
    // ALWAYS). THE X AND Y'S MAY BE FLIPPED IF Y IS THE INDEPENDENT VARIABLE.
    if (dX >= dY) { // if X is the independent variable
      final int dPr = dY << 1; // amount to increment decision if right is
      // chosen (always)
      final int dPru = dPr - (dX << 1); // amount to increment decision if up is
      // chosen
      int P = dPr - dX; // decision variable start value
      for (; dX >= 0; dX--) { // process each point in the line one at a time
        // (just use dX)
        setPixelbBuffer(Ax, Ay); // TODO - fix z
        // SetPixel(Ax, Ay, Color); // plot the pixel
        if (P > 0) { // is the pixel going right AND up?
          Ax += Xincr; // increment independent variable
          Ay += Yincr; // increment dependent variable
          P += dPru; // increment decision (for up)
        }
        else { // is the pixel just going right?
          Ax += Xincr; // increment independent variable
          P += dPr; // increment decision (for right)
        }
      }
    }
    else { // if Y is the independent variable
      final int dPr = dX << 1; // amount to increment decision if right is
      // chosen (always)
      final int dPru = dPr - (dY << 1); // amount to increment decision if up is
      // chosen
      int P = dPr - dY; // decision variable start value
      for (; dY >= 0; dY--) { // process each point in the line one at a time
        // (just use dY)
        setPixelbBuffer(Ax, Ay);
        // SetPixel(Ax, Ay, Color); // plot the pixel
        if (P > 0) { // is the pixel going up AND right?
          Ax += Xincr; // increment dependent variable
          Ay += Yincr; // increment independent variable
          P += dPru; // increment decision (for up)
        }
        else { // is the pixel just going up?
          Ay += Yincr; // increment independent variable
          P += dPr; // increment decision (for right)
        }
      }
    }
  }

  /**
   * h is xSize, k is ySize
   */
  public void drawOval(final int x, final int y, final float z, final int h,
      final int k) {
    int slope_mn, slope_md;
    int position_mn, position_md; // Variable for slope calculations
    int x_position, y_position; // Variables for position calculation
    int balance;
    slope_md = k * k; // find md for the slope of the ellipse
    // notice it has to be positive
    slope_mn = h * h; // Find mn for the slope of the ellipse
    // notice it has to be positive too
    // These positions for the ellipse's md and mn start it at the top
    position_md = 0; // the minimum value of md for the ellipse
    position_mn = h * h * k; // the maximum value of mn for the ellipse
    x_position = 0; // start at the middle x of the ellipse
    y_position = k; // start at the top y of the ellipse
    balance = 0;
    // while not at the last x, y positions, go
    while (y_position > 0 || x_position < h) {
      // since an ellipse is reflective in 4 quadrants, we can draw 4 numbers
      // at a time.
      setPixel(x + x_position, y + y_position, z);
      setPixel(x - x_position, y + y_position, z);
      setPixel(x + x_position, y - y_position, z);
      setPixel(x - x_position, y - y_position, z);
      if (balance < 0) { // if the balance leans toward x ...
        balance = balance + position_mn; // balance x with y
        position_mn = position_mn - slope_mn; // move position slope mn down
        y_position--; // move y in the correct direction
      }
      if (balance >= 0) {
        position_md = position_md + slope_md; // move position slope md up
        balance = balance - position_md; // Balance y with x
        x_position++; // move x in the correct direction
      }
    }
    setPixel(x + x_position, y, z);
    setPixel(x - x_position, y, z);
  }

  /**
   * OVERLOADED, 2D algorithm by joshua cantrell, h is xSize, k is ySize
   */
  public void drawOval(final int x, final int y, final int h, final int k) {
    int slope_mn, slope_md;
    int position_mn, position_md; // Variable for slope calculations
    int x_position, y_position; // Variables for position calculation
    int balance;
    slope_md = k * k; // find md for the slope of the ellipse
    // notice it has to be positive
    slope_mn = h * h; // Find mn for the slope of the ellipse
    // notice it has to be positive too
    // These positions for the ellipse's md and mn start it at the top
    position_md = 0; // the minimum value of md for the ellipse
    position_mn = h * h * k; // the maximum value of mn for the ellipse
    x_position = 0; // start at the middle x of the ellipse
    y_position = k; // start at the top y of the ellipse
    balance = 0;
    // while not at the last x, y positions, go
    while (y_position > 0 || x_position < h) {
      // since an ellipse is reflective in 4 quadrants, we can draw 4 numbers
      // at a time.
      setPixel(x + x_position, y + y_position);
      setPixel(x - x_position, y + y_position);
      setPixel(x + x_position, y - y_position);
      setPixel(x - x_position, y - y_position);
      if (balance < 0) { // if the balance leans toward x ...
        balance = balance + position_mn; // balance x with y
        position_mn = position_mn - slope_mn; // move position slope mn down
        y_position--; // move y in the correct direction
      }
      if (balance >= 0) {
        position_md = position_md + slope_md; // move position slope md up
        balance = balance - position_md; // Balance y with x
        x_position++; // move x in the correct direction
      }
    }
    setPixel(x + x_position, y);
    setPixel(x - x_position, y);
  }

//  public void drawOval(final int i, final int j, final float f,
//      final int k, final int l, final boolean selected) {
//  }
//
//  public void drawOval(final int i, final int j, final int size,
//      final int size2, final boolean selected) {
//  }
//
  /**
   * OVERLOADED 3D
   */
  public void drawPlus(final int x, final int y, final float z,
      final int xSize, final int ySize) {
    int i, j;
    int xLo = x - xSize;
    if (xLo < xMin) {
      xLo = xMin;
    }
    int yLo = y - ySize;
    if (yLo < yMin) {
      yLo = yMin;
    }
    int xHi = x + xSize;
    if (xHi >= xMax) {
      xHi = xMax - 1;
    }
    int yHi = y + ySize;
    if (yHi >= yMax) {
      yHi = yMax - 1;
    }
    for (i = xLo; i <= xHi; i++) {
      setPixel(i, y, z);
    }
    for (j = yLo; j <= yHi; j++) {
      setPixel(x, j, z);
    }
  }

  /**
   * OVERLOADED 2D
   */
  public void drawPlus(final int x, final int y, final int xSize,
      final int ySize) {
    int i, j;
    int xLo = x - xSize;
    if (xLo < xMin) {
      xLo = xMin;
    }
    int yLo = y - ySize;
    if (yLo < yMin) {
      yLo = yMin;
    }
    int xHi = x + xSize;
    if (xHi >= xMax) {
      xHi = xMax - 1;
    }
    int yHi = y + ySize;
    if (yHi >= yMax) {
      yHi = yMax - 1;
    }
    for (i = xLo; i <= xHi; i++) {
      setPixel(i, y);
    }
    for (j = yLo; j <= yHi; j++) {
      setPixel(x, j);
    }
  }

//  public void drawPlus(final int i, final int j, final float f,
//      final int k, final int l, final boolean selected) {
//  }
//
//  public void drawPlus(final int i, final int j, final int size,
//      final int size2, final boolean selected) {
//  }

  /**
   * OVERLOADED 3D
   */
  public void drawRect(final int x, final int y, final float z,
      final int xSize, final int ySize) {
    int i, j;
    int xLo = x - xSize;
    if (xLo < xMin) {
      xLo = xMin;
    }
    int yLo = y - ySize;
    if (yLo < yMin) {
      yLo = yMin;
    }
    int xHi = x + xSize;
    if (xHi >= xMax) {
      xHi = xMax - 1;
    }
    int yHi = y + ySize;
    if (yHi >= yMax) {
      yHi = yMax - 1;
    }
    for (i = xLo; i <= xHi; i++) {
      setPixel(i, yLo, z);
      setPixel(i, yHi, z);
    }
    for (j = yLo; j <= yHi; j++) {
      setPixel(xLo, j, z);
      setPixel(xHi, j, z);
    }
  }

  /**
   * OVERLOADED 2D
   */
  public void drawRect(final int x, final int y, final int xSize,
      final int ySize) {
    int i, j;
    int xLo = x - xSize;
    if (xLo < xMin) {
      xLo = xMin;
    }
    int yLo = y - ySize;
    if (yLo < yMin) {
      yLo = yMin;
    }
    int xHi = x + xSize;
    if (xHi >= xMax) {
      xHi = xMax - 1;
    }
    int yHi = y + ySize;
    if (yHi >= yMax) {
      yHi = yMax - 1;
    }
    for (i = xLo; i <= xHi; i++) {
      setPixel(i, yLo);
      setPixel(i, yHi);
    }
    for (j = yLo; j <= yHi; j++) {
      setPixel(xLo, j);
      setPixel(xHi, j);
    }
  }

//  public void drawRect(final int i, final int j, final float f,
//      final int k, final int l, final boolean selected) {
//  }
//
//  public void drawRect(final int i, final int j, final int size,
//      final int size2, final boolean selected) {
//  }
//
  /**
   * OVERLOAD 3D
   */
  void drawString(final DataBufferInt d, final int x, final int y,
      final float z, final int ww, final int hh) {
    /*
     * // a little debug loop: int i=0; boolean found=false; for (int y2=0; y2 <
     * hh; y2++) { for (int x2=0; x2 < ww; x2++) { if (d.getElem(i) == -1) { if
     * (!found) {System.out.println("OK String1");} found = true; } i++; } }
     */
    int xx, yy, k, i = 0;
    // scan the text's DataIntBuffer int by int
    for (int y2 = 0; y2 < hh; y2++) {
      for (int x2 = 0; x2 < ww; x2++) {
        // System.out.println("I: " + i + "ELEM: " + dataBufferInt2.getElem(i));
        if (d.getElem(i) == -1) {
          xx = x + x2; // where we are going to put it on the slate
          yy = y + y2;
          if (xx < w && yy < h && xx >= 0 && yy >= 0) {
            k = yy * w + xx;
            setPixel(k, z);
            // System.out.println("OK String2");
          }
        }
        i++;
      }
    }
  }

  /**
   * OVERLOAD 2D
   */
  void drawString(final DataBufferInt d, final int x, final int y,
      final int ww, final int hh) {
    int i = 0;
    int xx, yy;
    for (int y2 = 0; y2 < hh; y2++) {
      for (int x2 = 0; x2 < ww; x2++) {
        // System.out.println("I: " + i + "ELEM: " + dataBufferInt2.getElem(i));
        if (d.getElem(i) == -1) {
          xx = x + x2;
          yy = y + y2;
          if (xx < w && yy < h && xx >= 0 && yy >= 0) {
            setPixel(xx, yy);
          }
        }
        i++;
      }
    }
  }

  /**
   * OVERLOADED, 3D
   */
  public void drawX(final int x, final int y, final float z, final int xSize,
      final int ySize) {
    int i, j;
    int xLo = x - xSize;
    if (xLo < xMin) {
      xLo = xMin;
    }
    int yLo = y - ySize;
    if (yLo < yMin) {
      yLo = yMin;
    }
    int xHi = x + xSize;
    if (xHi >= xMax) {
      xHi = xMax - 1;
    }
    int yHi = y + ySize;
    if (yHi >= yMax) {
      yHi = yMax - 1;
    }
    if (xHi - xLo == yHi - yLo) {
      j = xHi - xLo;
      for (i = 0; i < j; i++) {
        setPixel(xLo + i, yLo + i, z);
        setPixel(xLo + i, yHi - i, z);
      }
    }
    if (xHi - xLo > yHi - yLo) {
      for (i = xLo; i <= xHi; i++) {
        // TODO
      }
    }
    else {
      for (i = xLo; i <= xHi; i++) {
        // TODO
      }
    }
  }

  /**
   * OVERLOADED, 2D
   */
  public void drawX(final int x, final int y, final int xSize, final int ySize) {
    int i, j;
    int xLo = x - xSize;
    if (xLo < xMin) {
      xLo = xMin;
    }
    int yLo = y - ySize;
    if (yLo < yMin) {
      yLo = yMin;
    }
    int xHi = x + xSize;
    if (xHi >= xMax) {
      xHi = xMax - 1;
    }
    int yHi = y + ySize;
    if (yHi >= yMax) {
      yHi = yMax - 1;
    }
    if (xHi - xLo == yHi - yLo) {
      j = xHi - xLo;
      for (i = 0; i < j; i++) {
        setPixel(xLo + i, yLo + i);
        setPixel(xLo + i, yHi - i);
      }
    }
    if (xHi - xLo > yHi - yLo) {
      for (i = xLo; i <= xHi; i++) {
        // TODO
      }
    }
    else {
      for (i = xLo; i <= xHi; i++) {
        // TODO
      }
    }
  }

//  public void drawX(final int i, final int j, final float f,
//      final int k, final int l, final boolean selected) {
//  }
//
//  public void drawX(final int i, final int j, final int size,
//      final int size2, final boolean selected) {
//  }
//
  /**
   * OVERLOADED, 3D. Paints the inside of a convex quadrilateral. Five numbers
   * are given, with the first and last being the same. Should all be in the
   * same plane.
   */
  void fillConvexQuad(final int[] x, final int[] y, final float[] z,
      final int nPoints) {
    // clearPixels(Color.black);
    // x[0]=15+r; y[0]=10+r; z[0]=0.0f+r;
    // x[1]=20+r; y[1]=15+r; z[1]=-5.0f+r;
    // x[2]=15+r; y[2]=20+r; z[2]=0.0f+r;
    // x[3]=10+r; y[3]=15+r; z[3]=5.0f+r;
    // x[4]=15+r; y[4]=10+r; z[4]=0.0f+r;
    // nPoints = 5;
    int i = -1, j = -1, a = -1, b, left, right;
    final int iX = x[3], iY = y[3];
    final float iZ = z[3];
    int xLo = Integer.MAX_VALUE, // bounding box
    xHi = Integer.MIN_VALUE, yLo = Integer.MAX_VALUE, yHi = Integer.MIN_VALUE;
    float k = 0.0f;
    // first calculate the equation of the plane
    final float Ax = x[1] - x[0];
    final float Ay = y[1] - y[0];
    final float Az = z[1] - z[0];
    final float Bx = x[2] - x[0];
    final float By = y[2] - y[0];
    final float Bz = z[2] - z[0];
    final float Nx = Ay * Bz - Az * By;
    final float Ny = Az * Bx - Ax * Bz;
    final float Nz = Ax * By - Ay * Bx;
    if (Nz == 0) { // bad plane - just draw the outline
      for (i = 0; i < nPoints - 1; i++) {
        j = i + 1;
        drawLine(x[i], y[i], z[i], x[j], y[j], z[j]);
      }
      return;
    }
    // calculate extents (bounding box) of the quad polygon
    for (i = 0; i < nPoints - 1; i++) {
      if (x[i] < xLo) {
        xLo = x[i];
      }
      if (x[i] > xHi) {
        xHi = x[i];
      }
      if (y[i] < yLo) {
        yLo = y[i];
      }
      if (y[i] > yHi) {
        yHi = y[i];
      }
    }
    // put bounding box entirely on screen if it is not already
    if (xLo < xMin) { // past west
      if (xHi < xMin) { // totally off screen
        return;
      }
      xLo = xMin; // flags[0]=true;
    }
    if (yLo < yMin) { // past north
      if (yHi < yMin) { // off screen
        return;
      }
      yLo = yMin; // flags[1]=true;
    }
    if (xHi > xMax - 1) { // past east
      if (xLo > xMax - 1) { // off screen
        return;
      }
      xHi = xMax - 1; // flags[2] = true;
    }
    if (yHi > yMax - 1) { // past south
      if (yLo > xMax - 1) { // off screen
        return;
      }
      yHi = yMax - 1; // flags[3] = true;
    }
    // Draw the convex quad in the boolean buffer
    for (i = 0; i < nPoints - 1; i++) {
      j = i + 1;
      drawLineInBBuffer(x[i], y[i], x[j], y[j]);
    }
    // Scan the first row of pixels in the bounding box
    // Change all pixels we drew in the boolean buffer to the currentColor
    b = yLo * w;
    for (i = xLo; i <= xHi; i++) {
      a = b + i;
      if (a < bBuffer.length) { // TODO: get rid of this line, a temporary fix
        if (bBuffer[a]) { // array out of bounds here when goes off screen
          k = iZ - (Nx * (i - iX) + Ny * (yLo - iY)) / Nz; // the z-value of
          // i,yLo
          setPixel(a, k);
          bBuffer[a] = false;
        }
      }
    }
    // Scan the bounding box. When we hit a pixel that we already drew,
    // start filling.
    for (j = yLo + 1; j < yHi; j++) {
      b = j * w;
      // Start from the left side of the bounding box
      for (left = xLo; left <= xHi; left++) {
        a = b + left;
        if (bBuffer[a]) {
          break;
        }
      }
      // Start another scan from the right side of the bounding box
      for (right = xHi; right >= xLo; right--) {
        a = b + right;
        if (bBuffer[a]) {
          break;
        }
      }
      // Now fill in between left and right lines
      for (i = left; i <= right; i++) {
        a = b + i;
        k = iZ - (Nx * (i - iX) + Ny * (j - iY)) / Nz;
        setPixel(a, k);
        bBuffer[a] = false; // initialize for next time
      }
    }
    // Scan the last row of pixels in the bounding box
    // Change all pixels we drew to the currentColor
    b = yHi * w;
    for (i = xLo; i <= xHi; i++) {
      a = b + i;
      // System.out.println("i,dataBufferInt.getElem(k):
      // "+i+","+dataBufferInt.getElem(k));
      if (bBuffer[a]) {
        k = iZ - (Nx * (i - iX) + Ny * (yHi - iY)) / Nz;
        // System.out.println("i,KKK3: "+i+ "," +kkk);
        bBuffer[a] = false;
      }
    }
  }

  /**
   * OVERLOADED, 2D. Paints the inside of a convex quadrilateral. Five numbers
   * are given, with the first and last being the same.
   */
  void fillConvexQuad(final int[] x, final int[] y, final int nPoints) {
    int i = -1, j = -1, a = -1, b, left, right;
    int xLo = Integer.MAX_VALUE, // bounding box
    xHi = Integer.MIN_VALUE, yLo = Integer.MAX_VALUE, yHi = Integer.MIN_VALUE;
    // calculate extents (bounding box) of the quad polygon
    for (i = 0; i < nPoints - 1; i++) {
      if (x[i] < xLo) {
        xLo = x[i];
      }
      if (x[i] > xHi) {
        xHi = x[i];
      }
      if (y[i] < yLo) {
        yLo = y[i];
      }
      if (y[i] > yHi) {
        yHi = y[i];
      }
    }
    // put bounding box entirely on screen if it is not already
    if (xLo < xMin) { // past west
      if (xHi < xMin) { // totally off screen
        return;
      }
      xLo = xMin; // flags[0]=true;
    }
    if (yLo < yMin) { // past north
      if (yHi < yMin) { // off screen
        return;
      }
      yLo = yMin; // flags[1]=true;
    }
    if (xHi > xMax - 1) { // past east
      if (xLo > xMax - 1) { // off screen
        return;
      }
      xHi = xMax - 1; // flags[2] = true;
    }
    if (yHi > yMax - 1) { // past south
      if (yLo > xMax - 1) { // off screen
        return;
      }
      yHi = yMax - 1; // flags[3] = true;
    }
    // Draw the convex quad in the boolean buffer
    for (i = 0; i < nPoints - 1; i++) {
      j = i + 1;
      drawLineInBBuffer(x[i], y[i], x[j], y[j]);
    }
    // Scan the first row of pixels in the bounding box
    // Change all pixels we drew in the boolean buffer to the currentColor
    b = yLo * w;
    for (i = xLo; i <= xHi; i++) {
      a = b + i;
      if (a < bBuffer.length) { // TODO: get rid of this line, a temporary fix
        if (bBuffer[a]) { // array out of bounds here when goes off screen
          dataBufferInt.setElem(a, foreColor);
          bBuffer[a] = false;
        }
      }
    }
    // Scan the bounding box. When we hit a pixel that we already drew,
    // start filling.
    for (j = yLo + 1; j < yHi; j++) {
      b = j * w;
      // Start from the left side of the bounding box
      for (left = xLo; left <= xHi; left++) {
        a = b + left;
        if (bBuffer[a]) {
          break;
        }
      }
      // Start another scan from the right side of the bounding box
      for (right = xHi; right >= xLo; right--) {
        a = b + right;
        if (bBuffer[a]) {
          break;
        }
      }
      // Now fill in between left and right lines
      for (i = left; i <= right; i++) {
        a = b + i;
        dataBufferInt.setElem(a, foreColor);
        bBuffer[a] = false; // initialize for next time
      }
    }
    // Scan the last row of pixels in the bounding box
    // Change all pixels we drew to the currentColor
    b = yHi * w;
    for (i = xLo; i <= xHi; i++) {
      a = b + i;
      if (bBuffer[a]) {
        bBuffer[a] = false;
      }
    }
  }

  /**
   * OVERLOADED 3D
   */
  public void fillOval(final int x, final int y, final float z,
      final int xSize, final int ySize) {
    int i, j;
    final int xy = xSize * ySize;
    int xLo = x - xSize;
    if (xLo < xMin) {
      xLo = xMin;
    }
    int yLo = y - ySize;
    if (yLo < yMin) {
      yLo = yMin;
    }
    int xHi = x + xSize;
    if (xHi >= xMax) {
      xHi = xMax - 1;
    }
    int yHi = y + ySize;
    if (yHi >= yMax) {
      yHi = yMax - 1;
    }
    for (i = x + 1; i < xHi; i++) {
      for (j = y + 1; j < yHi; j++) {
        if ((x - i) * (x - i) + (j - y) * (j - y) < xy) {
          setPixel(i, j, z);
        }
      }
      setPixel(i, y, z);
      for (j = y - 1; j > yLo; j--) {
        if ((x - i) * (x - i) + (y - j) * (y - j) < xy) {
          setPixel(i, j, z);
        }
        // dataBufferInt.setElem(j*width+i,currentColor);
      }
    }
    for (j = y + 1; j < yHi; j++) {
      setPixel(x, j, z);
      // dataBufferInt.setElem(j*width+x,currentColor);
    }
    setPixel(x, y, z);
    // dataBufferInt.setElem(y*width+x,currentColor);
    for (j = y - 1; j > yLo; j--) {
      setPixel(x, j, z);
      // dataBufferInt.setElem(j*width+x,currentColor);
    }
    for (i = x - 1; i > xLo; i--) {
      for (j = y + 1; j < yHi; j++) {
        if ((x - i) * (x - i) + (y - j) * (y - j) < xy) {
          setPixel(i, j, z);
        }
        // dataBufferInt.setElem(j*width+i,currentColor);
      }
      setPixel(i, y, z);
      // dataBufferInt.setElem(y*width+i,currentColor);
      for (j = y - 1; j > yLo; j--) {
        if ((x - i) * (x - i) + (y - j) * (y - j) < xy) {
          setPixel(i, j, z);
        }
      }
    }
  }

  /**
   * OVERLOADED 2D
   */
  public void fillOval(final int x, final int y, final int xSize,
      final int ySize) {
    int i, j;
    final int xy = xSize * ySize;
    int xLo = x - xSize;
    if (xLo < xMin) {
      xLo = xMin;
    }
    int yLo = y - ySize;
    if (yLo < yMin) {
      yLo = yMin;
    }
    int xHi = x + xSize;
    if (xHi >= xMax) {
      xHi = xMax - 1;
    }
    int yHi = y + ySize;
    if (yHi >= yMax) {
      yHi = yMax - 1;
    }
    for (i = x + 1; i < xHi; i++) {
      for (j = y + 1; j < yHi; j++) {
        if ((x - i) * (x - i) + (j - y) * (j - y) < xy) {
          setPixel(i, j);
        }
      }
      setPixel(i, y);
      for (j = y - 1; j > yLo; j--) {
        if ((x - i) * (x - i) + (y - j) * (y - j) < xy) {
          setPixel(i, j);
        }
        // dataBufferInt.setElem(j*width+i,currentColor);
      }
    }
    for (j = y + 1; j < yHi; j++) {
      setPixel(x, j);
      // dataBufferInt.setElem(j*width+x,currentColor);
    }
    setPixel(x, y);
    // dataBufferInt.setElem(y*width+x,currentColor);
    for (j = y - 1; j > yLo; j--) {
      setPixel(x, j);
      // dataBufferInt.setElem(j*width+x,currentColor);
    }
    for (i = x - 1; i > xLo; i--) {
      for (j = y + 1; j < yHi; j++) {
        if ((x - i) * (x - i) + (y - j) * (y - j) < xy) {
          setPixel(i, j);
        }
        // dataBufferInt.setElem(j*width+i,currentColor);
      }
      setPixel(i, y);
      // dataBufferInt.setElem(y*width+i,currentColor);
      for (j = y - 1; j > yLo; j--) {
        if ((x - i) * (x - i) + (y - j) * (y - j) < xy) {
          setPixel(i, j);
        }
      }
    }
  }

//  public void fillOvalSelected(final int i, final int j, final float f,
//      final int k, final int l, final boolean selected) {
//  }
//
//  public void fillOvalSelected(final int i, final int j, final int size,
//      final int size2, final boolean selected) {
//  }
//
  /**
   * 3D, OVERLOADED
   */
  public void fillRect(final int x, final int y, final float z,
      final int xSize, final int ySize) {
    int i, j;
    int xLo = x - xSize;
    if (xLo < xMin) {
      xLo = xMin;
    }
    int yLo = y - ySize;
    if (yLo < yMin) {
      yLo = yMin;
    }
    int xHi = x + xSize;
    if (xHi >= xMax) {
      xHi = xMax - 1;
    }
    int yHi = y + ySize;
    if (yHi >= yMax) {
      yHi = yMax - 1;
    }
    for (i = xLo; i <= xHi; i++) {
      for (j = yLo; j <= yHi; j++) {
        setPixel(i, j, z);
      }
    }
  }

  /**
   * 2D, OVERLOADED
   */
  public void fillRect(final int x, final int y, final int xSize,
      final int ySize) {
    int i, j;
    int xLo = x - xSize;
    if (xLo < xMin) {
      xLo = xMin;
    }
    int yLo = y - ySize;
    if (yLo < yMin) {
      yLo = yMin;
    }
    int xHi = x + xSize;
    if (xHi >= xMax) {
      xHi = xMax - 1;
    }
    int yHi = y + ySize;
    if (yHi >= yMax) {
      yHi = yMax - 1;
    }
    for (i = xLo; i <= xHi; i++) {
      for (j = yLo; j <= yHi; j++) {
        setPixel(i, j);
      }
    }
  }

//  public void fillRect(final int i, final int j, final float f,
//      final int k, final int l, final boolean selected) {
//  }
//
//  public void fillRect(final int i, final int j, final int size,
//      final int size2, final boolean selected) {
//  }
//
  @Override
  protected void finalize() {
    System.out.println("FINALIZED SLATE " + hashCode());
  }

  /**
   *
   */
  Color getColor() {
    return new Color(foreColor);
  }

  public void initialize() { // was initImage()
    yMin = session.plotManager.canvas.getYMin();
    xMin = session.plotManager.canvas.getXMin();
    yMax = session.plotManager.canvas.getYMax();
    xMax = session.plotManager.canvas.getXMax();
    w = session.plotManager.canvas.getW();
    h = session.plotManager.canvas.getH();
    // System.out.println("INITIALIZING SLATE, WIDTH: "+w+" HEIGHT: "+h);
    zBuffer = new float[w * h];
    bBuffer = new boolean[w * h];
    saved = new int[w];
    image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    dataBufferInt = (DataBufferInt) image.getRaster().getDataBuffer();
    stale = false;
  }

  /**
   *
   */
  public void setColor(final Color c) {
    // int r = c.getRed();
    // int g = c.getGreen();
    // int boundingBox = c.getBlue();
    // currentColor=(r << 16) | (g << 8) | boundingBox;
    foreColor = c.getRGB();
    foreRed = (foreColor & 0x00FF0000) >> 16;
    foreGreen = (foreColor & 0x0000FF00) >> 8;
    foreBlue = foreColor & 0x000000FF;
  }

  /**
   *
   */
  int setColorForDepth(final float z) {
    final float brightness = 1.0f - 1.0f / (1.0f + (float) Math
        .exp((session.plotManager.view.lightPosition - z)
            / session.plotManager.view.lightRamp));
    return 0xFF000000 | (int) (foreRed * brightness) << 16
        | (int) (foreGreen * brightness) << 8 | (int) (foreBlue * brightness);
  }

  /**
   * Assumes ON SCREEN
   */
  public void setPixel(final int i, final float z) {
    if (z < zBuffer[i]) {
      dataBufferInt.setElem(i, setColorForDepth(z));
      zBuffer[i] = z;
    }
  }

  /**
   * OVERLOADED 2D, DOES NOT ASSUME ON SCREEN
   */
  void setPixel(final int x, final int y) {
    if (x >= xMax || x < xMin || y >= yMax || y < yMin) {
      return; // off screen
    }
    final int k = y * w + x;
    dataBufferInt.setElem(k, foreColor);
  }

//  public void setPixel(final int x, final int y, final boolean selected) {
//    setPixel(x,y);
//    if (selected) {
//    setPixel(x-1,y-1);
//    setPixel(x-1,y);
//    setPixel(x-1,y+1);
//    setPixel(x,y-1);
//    setPixel(x,y+1);
//    setPixel(x+1,y-1);
//    setPixel(x+1,y);
//    setPixel(x+1,y+1);
//    }
//  }

  /**
   * OVERLOADED 3D, DOES NOT ASSUME ON SCREEN
   */
  public void setPixel(final int x, final int y, final float z) {
    if (x >= xMax || x < xMin || y >= yMax || y < yMin) {
      return; // off screen
    }
    final int k = y * w + x;
    if (zBuffer[k] > z) {
      zBuffer[k] = z;
      dataBufferInt.setElem(k, setColorForDepth(z));
    }
  }

//  public void setPixel(final int x, final int y, final float z, final boolean selected) {
//    setPixel(x,y);
//    if (selected) {
//    setPixel(x-1,y-1);
//    setPixel(x-1,y);
//    setPixel(x-1,y+1);
//    setPixel(x,y-1);
//    setPixel(x,y+1);
//    setPixel(x+1,y-1);
//    setPixel(x+1,y);
//    setPixel(x+1,y+1);
//    }
//  }

  /**
   *
   */
  void setPixelbBuffer(final int x, final int y) {
    if (x >= xMax || x < xMin || y >= yMax || y < yMin) {
      return; // off screen
    }
    bBuffer[y * w + x] = true;
  }
  
  public void drawCrosshairs(int x, int y, int pointSize, Color color) {
    setColor(color);
    drawLine(x,y-pointSize-1,x,y-25);
    drawLine(x,y+pointSize+1,x,y+25);
    drawLine(x-pointSize-1,y,x-25,y);
    drawLine(x+pointSize+1,y,x+25,y);
    drawOval(x,y,25,25);
  }

}
// end of file
