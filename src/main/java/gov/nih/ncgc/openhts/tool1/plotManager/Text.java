/*
 * Title: session
 * Description: session
 * Copyright: Copyright (c) 2002
 */
package gov.nih.ncgc.openhts.tool1.plotManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.text.DecimalFormat;
import gov.nih.ncgc.openhts.tool1.Palette;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.persistence.Persistent;

/**
 * A text entry.
 * 
 * @author Joseph Talafous
 * @version 1.0
 */
public final class Text extends Drawable implements Persistent {
  public static final String sectionType = "TEXT";
  public String name;
  private String string = "my text string";
  float[] unzoomedStart; // coords for the original unzoomed text in User space
  public float[] start; // coords for current axes in Subject space
  public float[] tStart; // in Screen space
  public float labelOffsetLengthMM; // screen distance from label to axis or tic
  public Color color; // at z=0
  String fontString; // usually the X style format like "-*-*-*"
  public Font font = null;
  transient int width, height, ascent;
  transient BufferedImage image;
  transient DataBufferInt dataBufferInt;
  // BTW: not visible when zoomed
  public boolean moving;
  static boolean dragging;
  // private static final int action;
  public static final int MODIFY = 0;
  public static final int DELETE = 1;
  public static final int ADD = 2;
  private static final DecimalFormat format = new DecimalFormat("0.000");

  public Text(final Session session) {
    super(session);
    // this.type = type;
    name = "";
    start = new float[3];
    unzoomedStart = new float[3];
    tStart = new float[3];
    // System.out.println("CREATED TEXT"+hashCode());
  }

  public void initialize() {
    start[0] = -session.plotManager.subject.cumDelta[0];
    start[1] = -session.plotManager.subject.cumDelta[0];
    start[2] = -session.plotManager.subject.cumDelta[0];
    unzoomedStart[0] = 0.0f;
    unzoomedStart[1] = 0.0f;
    unzoomedStart[2] = 0.0f;
    min[0] = 0.0f;
    min[1] = 0.0f;
    min[2] = 0.0f;
    max[0] = 0.0f;
    max[1] = 0.0f;
    max[2] = 0.0f;
    tMin[0] = 0.0f;
    tMin[1] = 0.0f;
    tMin[2] = 0.0f;
    tMax[0] = 0.0f;
    tMax[1] = 0.0f;
    tMax[2] = 0.0f;
    // angle = 0;
    color = new Color(1.0f, 1.0f, 1.0f);
    visible = true;
    moving = true;
    dragging = false;
    labelOffsetLengthMM = Float.NEGATIVE_INFINITY;
    font = new Font("Monospaced", Font.PLAIN, 10); // jic
    name = "text" + hashCode();
    initializeTransient();
  }

  @Override
	public void initializeTransient() {
    // nop
  }

  @Override
  protected void finalize() {
    // System.out.println("FINALIZED TEXT"+ hashCode());
  }

  public void clearReferences() {
    // nop
  }

  /**
   * Checks whether the given Object is equal to this DeploymentSettings
   */
  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof Text)) {
      return false;
    }
    final Text text = (Text) obj;
    if (session.plotManager.getDim() == 3) {
      return text.string == this.string && text.color.equals(this.color)
          && text.font.equals(this.font) && text.moving == this.moving
          && text.start[0] - this.start[0] < 0.0001f
          && text.start[1] - this.start[1] < 0.0001f;
    }
    else {
      return text.string.equals(this.string) && text.color.equals(this.color)
          && text.font.equals(this.font) && text.moving == this.moving
          && text.start[0] - this.start[0] < 0.0001f
          && text.start[1] - this.start[1] < 0.0001f
          && text.start[2] - this.start[2] < 0.0001f;
    }
  }

  void calculateSubjectExtents() {
    if (!visible || !moving) {
      return;
    }
    min[0] = Float.POSITIVE_INFINITY;
    min[1] = Float.POSITIVE_INFINITY;
    min[2] = Float.POSITIVE_INFINITY;
    max[0] = Float.NEGATIVE_INFINITY;
    max[1] = Float.NEGATIVE_INFINITY;
    max[2] = Float.NEGATIVE_INFINITY;
    switch (session.plotManager.getDim()) {
    case -1:
      break;
    case 2:
      if (start[0] < min[0]) {
        min[0] = start[0];
      }
      if (start[0] > max[0]) {
        max[0] = start[0];
      }
      if (start[1] < min[1]) {
        min[1] = start[1];
      }
      if (start[1] > max[1]) {
        max[1] = start[1];
      }
      break;
    case 3:
      if (start[0] < min[0]) {
        min[0] = start[0];
      }
      if (start[0] > max[0]) {
        max[0] = start[0];
      }
      if (start[1] < min[1]) {
        min[1] = start[1];
      }
      if (start[1] > max[1]) {
        max[1] = start[1];
      }
      if (start[2] < min[2]) {
        min[2] = start[2];
      }
      if (start[2] > max[2]) {
        max[2] = start[2];
      }
      break;
    default:
      throw new RuntimeException("bad dim in extents");
    }
  }

  @Override
  public void calculateViewExtents() {
    if (session.plotManager.canvas.fastCompose) {
      super.calculateViewExtents();
      return;
    }
    if (!visible || !moving) {
      return;
    }
    tMin[0] = Float.POSITIVE_INFINITY;
    tMin[1] = Float.POSITIVE_INFINITY;
    tMin[2] = Float.POSITIVE_INFINITY;
    tMax[0] = Float.NEGATIVE_INFINITY;
    tMax[1] = Float.NEGATIVE_INFINITY;
    tMax[2] = Float.NEGATIVE_INFINITY;
    // negative width and/or height and/or ascent will break this calculation
    // float xLo = tStart[0],
    // xHi = tStart[0] + width,
    // yLo = tStart[1],
    // yHi = tStart[1] + height + ascent;
    float xLo, xHi, yLo, yHi;
    switch (session.plotManager.getDim()) {
    case 2:
      transform2D(true);
      xLo = tStart[0];
      xHi = tStart[0];
      yLo = tStart[1];
      yHi = tStart[1];
      if (xHi > tMax[0]) {
        tMax[0] = xHi;
      }
      if (xLo < tMin[0]) {
        tMin[0] = xLo;
      }
      if (yHi > tMax[1]) {
        tMax[1] = yHi;
      }
      if (yLo < tMin[1]) {
        tMin[1] = yLo;
      }
      break;
    case 3:
      transform3D(true);
      xLo = tStart[0];
      xHi = tStart[0];
      yLo = tStart[1];
      yHi = tStart[1];
      if (xHi > tMax[0]) {
        tMax[0] = xHi;
      }
      if (xLo < tMin[0]) {
        tMin[0] = xLo;
      }
      if (yHi > tMax[1]) {
        tMax[1] = yHi;
      }
      if (yLo < tMin[1]) {
        tMin[1] = yLo;
      }
      if (tStart[2] > tMax[2]) {
        tMax[2] = tStart[2];
      }
      if (tStart[2] < tMin[2]) {
        tMin[2] = tStart[2];
      }
      break;
    default:
      throw new RuntimeException("bad dim in extents");
    }
  }

  // This is called from somewhere else exactly once
  void moveToUserSpace() {
    for (int a = 0; a < session.plotManager.getDim(); a++) {
      if (moving) {
        start[a] += session.plotManager.subject.cumDelta[a];
        min[a] += session.plotManager.subject.cumDelta[a];
        max[a] += session.plotManager.subject.cumDelta[a];
      }
    }
  }

  // This is called from somewhere else exactly once
  void moveToSubjectSpace() {
    // Since the text "owned" by the axes must precede the axes,
    // the dimensionality is not defined yet. So we assume 3D.
    // This is not a problem for Text due to the "Fundamental 2D/3D
    // Approximation" which is "For all Drawables except Pointset, the
    // Z-coordinate is allocated and GUI controls are created.
    // In 2D are they made invisible to the user, but still exist."
    for (int a = 0; a < 3; a++) {
      if (moving) {
        start[a] -= session.plotManager.subject.cumDelta[a];
        min[a] -= session.plotManager.subject.cumDelta[a];
        max[a] -= session.plotManager.subject.cumDelta[a];
      }
    }
  }

  @Override
  public void draw2D(final Slate slate, final boolean drawAllText) {
    if (visible) {
      transform2D(drawAllText);
      slate.setColor(color);
      slate.drawString(dataBufferInt, (int) tStart[0] - width / 2,
          (int) tStart[1] - height / 2, width, height);
    }
  }

  @Override
  public void draw3D(final Slate slate, final boolean drawAllText) {
    if (visible) {
      transform3D(drawAllText);
      slate.setColor(color);
      slate.drawString(dataBufferInt, (int) tStart[0] - width / 2,
          (int) tStart[1] - height / 2, tStart[2], width, height);
    }
  }

  @Override
  public void zoom(final int loX, final int hiX, final int loY, final int hiY) {
    if (!session.plotManager.view.getOrthogonalMode()) {
      visible = false;
    }
    calculateSubjectExtents();
  }

  @Override
  public void unzoom() {
    // Stay in Subject space
    max[0] = min[0] = start[0] = unzoomedStart[0]
        - session.plotManager.subject.cumDelta[0];
    max[1] = min[1] = start[1] = unzoomedStart[1]
        - session.plotManager.subject.cumDelta[1];
    max[2] = min[2] = start[2] = unzoomedStart[2]
        - session.plotManager.subject.cumDelta[2];
    tMin[0] = tMax[0] = tStart[0];
    tMin[1] = tMax[1] = tStart[1];
    tMin[2] = tMax[2] = tStart[2];
    visible = true;
    calculateSubjectExtents();
  }

  @Override
  public void transform2D(final boolean notUsed) {
    if (moving) {
      tStart[0] = (start[0] * session.plotManager.view.tMat[0][0] + start[1]
          * session.plotManager.view.tMat[1][0])
          * session.plotManager.view.focal
          + session.plotManager.view.tMat[3][0];
      tStart[1] = (start[0] * session.plotManager.view.tMat[0][1] + start[1]
          * session.plotManager.view.tMat[1][1])
          * session.plotManager.view.focal
          + session.plotManager.view.tMat[3][1];
    }
    else {
      tStart[0] = start[0];
      tStart[1] = start[1];
    }
    tMin[0] = (min[0] * session.plotManager.view.tMat[0][0] + min[1]
        * session.plotManager.view.tMat[1][0] + min[0]
        * session.plotManager.view.tMat[2][0])
        * session.plotManager.view.focal + session.plotManager.view.tMat[3][0];
    tMin[1] = (min[0] * session.plotManager.view.tMat[0][1] + min[1]
        * session.plotManager.view.tMat[1][1] + min[1]
        * session.plotManager.view.tMat[2][1])
        * session.plotManager.view.focal + session.plotManager.view.tMat[3][1];
  }

  @Override
  public void transform3D(final boolean notUsed) {
    // Assumes session.plotter.view.tMat was orthogonalized
    if (moving) {
      tStart[2] = start[0] * session.plotManager.view.tMat[0][2] + start[1]
          * session.plotManager.view.tMat[1][2] + start[2]
          * session.plotManager.view.tMat[2][2]
          + session.plotManager.view.tMat[3][2];
      final float f = session.plotManager.view.getPerspectiveFactor(tStart[2]);
      tStart[0] = (start[0] * session.plotManager.view.tMat[0][0] + start[1]
          * session.plotManager.view.tMat[1][0] + start[2]
          * session.plotManager.view.tMat[2][0])
          * f + session.plotManager.view.tMat[3][0];
      tStart[1] = (start[0] * session.plotManager.view.tMat[0][1] + start[1]
          * session.plotManager.view.tMat[1][1] + start[2]
          * session.plotManager.view.tMat[2][1])
          * f + session.plotManager.view.tMat[3][1];
    }
    else {
      tStart[0] = start[0];
      tStart[1] = start[1];
      tStart[2] = start[2];
    }
  }

  /**
   *
   */
  @Override
  public void translate(final float[] delta) {
    if (moving) {
      // System.out.println("TRANSLATING TEXT BY: " + delta[0] + "," + delta[1]
      // + "," + delta[2]);
      switch (session.plotManager.getDim()) {
      case 2:
        start[0] -= delta[0];
        start[1] -= delta[1];
        min[0] -= delta[0];
        min[1] -= delta[1];
        max[0] -= delta[0];
        max[1] -= delta[1];
        break;
      case 3:
        start[0] -= delta[0];
        start[1] -= delta[1];
        start[2] -= delta[2];
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
    }
  }

  public void makeImage() {
    final FontMetrics metrics = session.plotManager.getFontMetrics(font);
    height = metrics.getHeight();
    width = metrics.stringWidth(string);
    ascent = metrics.getAscent();
    // draw the image of the string
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    final Graphics g = image.getGraphics();
    g.setFont(font);
    g.drawString(string, 0, ascent);
    dataBufferInt = (DataBufferInt) image.getRaster().getDataBuffer();
  }

  // /**
  // *
  // */
  // @Override
  // public
  // void disposeDialogs() {
  //
  // if (modifyDialog != null) {
  // modifyDialog.dispose();
  // }
  // if (chooseDialog != null) {
  // chooseDialog.dispose();
  // }
  // }
  //
  /**
   * Highlight in table
   */
  void find(final int x, final int y) {
    // TODO: what if text is overlapped
    if (tStart[0] <= x && x <= tStart[0] + width && tStart[1] <= y
        && y <= tStart[1] + height) {
      // modify(session.assignLocation());
    }
  }

  /**
   *
   */
  public Color getColor() {
    return color;
  }

  private String getColorName() {
    final String colorName = Palette.getClosestChoice(color).toString();
    return colorName;
  }

  public String getValue() {
    return string;
  }

  /**
   *
   */
  public Font getFont() {
    return font;
  }

  /**
   *
   */
  public String getFontName() {
    final String fontName = font.getName();
    String strStyle;
    if (font.isBold()) {
      strStyle = font.isItalic() ? "bold italic" : "bold";
    }
    else {
      strStyle = font.isItalic() ? "italic" : "";
    }
    // System.out.println("font in text " + fontName + " " + strStyle + " " +
    // font.getSize());
    return fontName + " " + strStyle + " " + font.getSize();
  }

  /**
   *
   */
  private float[] getCoordinates() {
    return start;
  }

  /**
   *
   */
  @Override
  public String toString() {
    if (session.plotManager.getDim() == 3) {
      return string + "; " + "start = ("
          + format.format(start[0] + session.plotManager.subject.cumDelta[0])
          + ","
          + format.format(start[1] + session.plotManager.subject.cumDelta[1])
          + ","
          + format.format(start[2] + session.plotManager.subject.cumDelta[0])
          + ")" /* + "; angle = " + angle */;
    }
    else {
      return string + "; " + "start = ("
          + format.format(start[0] + session.plotManager.subject.cumDelta[0])
          + ","
          + format.format(start[1] + session.plotManager.subject.cumDelta[0])
          + ")" /* + "; angle = " + angle */;
    }
  }

  // @Override
  // public void generateDemo() {
  // }
  //
  // /**
  // *
  // */
  // @Override
  // public void readSection() throws Exception {
  // //System.out.println("subject.cumDelta[0]: "+subject.cumDelta[0]);
  //
  // final String errString = "bad format in text section";
  //
  // if (session.readLine()) {
  // name = new String(session.line);
  // }
  // else {
  // throw new Exception(session.makeMessage(errString+"("+name+")"));
  // }
  //
  // if (session.readLine()) {
  // string = new String(session.line);
  // }
  // else {
  // throw new Exception(session.makeMessage(errString+"("+name+")"));
  // }
  //
  // if (session.readLine(21)) {
  // session.decode1Int(0); // reserved for future
  // session.decode3Floats(start, 4);
  // min[0] = max[0] = unzoomedStart[0] = start[0];
  // min[1] = max[1] = unzoomedStart[1] = start[1];
  // max[2] = max[2] = unzoomedStart[2] = start[2];
  // //System.out.println("READ SECTION unzoomedStart[0]: " + unzoomedStart[0]);
  // //System.out.println("READ SECTION unzoomedStart[1]: " + unzoomedStart[1]);
  // //System.out.println("READ SECTION unzoomedStart[2]: " + unzoomedStart[2]);
  // labelOffsetLengthMM = session.decode1Float(16);
  // moving = session.decode1Boolean(20);
  // }
  // else {
  // throw new Exception(session.makeMessage(errString+"("+name+")"));
  // }
  //
  // if (session.readLine()) {
  // if (session.line.length != 0) {
  // throw new Exception(session.makeMessage(errString+"("+name+")"));
  // }
  // }
  // else {
  // throw new Exception(session.makeMessage(errString+"("+name+")"));
  // }
  //
  // if (session.readLine()) {
  // if (session.line.length != 18) {
  // throw new Exception(session.makeMessage(errString+"("+name+")"));
  // }
  // color = session.decodeColor(0);
  // }
  // else {
  // throw new Exception(session.makeMessage(errString+"("+name+")"));
  // }
  //
  // if (session.readLine()) {
  // fontString = session.readFontString(0);
  // font = session.decodeFont(fontString);
  // }
  // else {
  // throw new Exception(session.makeMessage(errString+"("+name+")"));
  // }
  //
  // moveToSubjectSpace();
  // calculateSubjectExtents();
  // makeImage();
  // }
  //
  // /**
  // *
  // */
  // @Override
  // public void writeSection() throws Exception {
  //
  // moveToUserSpace();
  // try {
  // session.dos.writeBytes(sectionType+"\n");
  // session.dos.writeBytes(name+"\n");
  // session.dos.writeBytes(string+"\n");
  // session.dos.writeInt(-1); // type: reserved for future
  // session.dos.writeFloat(start[0]);
  // session.dos.writeFloat(start[1]);
  // session.dos.writeFloat(start[2]);
  // session.dos.writeFloat(labelOffsetLengthMM);
  // session.dos.writeBoolean(moving);
  // session.dos.writeBytes("\n");
  // session.writeColor(color);
  // fontString = session.encodeFont(font);
  // session.writeFontString(fontString);
  // }
  // catch (final Throwable ex) {
  // throw new Exception(ex.getMessage());
  // }
  // moveToSubjectSpace();
  // }
  /**
   *
   */
  public void dumpUserSpace() {
    System.out.println("text "
        + (start[0] + session.plotManager.subject.cumDelta[0]) + ", "
        + (start[1] + session.plotManager.subject.cumDelta[1]) + ", "
        + (start[2] + session.plotManager.subject.cumDelta[2]));
  }

  @Override
	public void setSession(final Session session) {
    this.session = session;
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }
} // end of Text class
// end of file
