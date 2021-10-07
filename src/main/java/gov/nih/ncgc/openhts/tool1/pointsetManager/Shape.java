package gov.nih.ncgc.openhts.tool1.pointsetManager;

import java.awt.Color;
import java.util.BitSet;
import gov.nih.ncgc.openhts.tool1.plotManager.Slate;
import gov.nih.ncgc.openhts.tool1.plotManager.View;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public enum Shape implements ShapeDraw {
  PIXEL {
    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size) {
      for (int i = 0; i < n; i++) {
        slate.setPixel((int) transformed[i][0], (int) transformed[i][1]);
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a) {
      for (int i = 0; i < n; i++) {
        slate.setPixel((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2]);
      }
    }

    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size,
        BitSet selected) {
      for (int i = 0; i < n; i++) {
        slate.setPixel((int) transformed[i][0], (int) transformed[i][1]);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], 3, 3);
        }
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a, BitSet selected) {
      for (int i = 0; i < n; i++) {
        slate.setPixel((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2]);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], 3, 3);
        }
      }
    }

    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size,
        byte[][] rgb) {
      for (int i = 0; i < n; i++) {
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.setPixel((int) transformed[i][0], (int) transformed[i][1]);
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a, byte[][] rgb) {
      for (int i = 0; i < n; i++) {
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.setPixel((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2]);
      }
    }

    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size,
        byte[][] rgb, BitSet selected,Color selectionColor) {
      for (int i = 0; i < n; i++) {
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.setPixel((int) transformed[i][0], (int) transformed[i][1]);
        slate.setColor(selectionColor);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], 3, 3);
        }
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a, byte[][] rgb, BitSet selected,Color selectionColor) {
      for (int i = 0; i < n; i++) {
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.setPixel((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2]);
        slate.setColor(selectionColor);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], 3, 3);
        }
      }
    }

    @Override
    public String toString() {
      return "Pixel";
    }
  },
  // //////////////////////////////////////////////
  DOT {
    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size) {
      for (int i = 0; i < n; i++) {
        slate.fillOval((int) transformed[i][0], (int) transformed[i][1], size,
            size);
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.fillOval((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2], newSize / 2, newSize / 2);
      }
    }

    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size,
        BitSet selected) {
      for (int i = 0; i < n; i++) {
        slate.fillOval((int) transformed[i][0],
            (int) transformed[i][1], size, size);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], size+2, size+2);
        }
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a, BitSet selected) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.fillOval((int) transformed[i][0],
            (int) transformed[i][1], transformed[i][2], newSize / 2,
            newSize / 2);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], newSize+2, newSize+2);
        }
      }
    }

    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size,
        byte[][] rgb) {
      for (int i = 0; i < n; i++) {
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.fillOval((int) transformed[i][0], (int) transformed[i][1], size,
            size);
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a, byte[][] rgb) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.fillOval((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2], newSize / 2, newSize / 2);
      }
    }

    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size,
        byte[][] rgb, BitSet selected,Color selectionColor) {
      for (int i = 0; i < n; i++) {
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.fillOval((int) transformed[i][0],
            (int) transformed[i][1], size, size);
        slate.setColor(selectionColor);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], size+2, size+2);
        }
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a, byte[][] rgb, BitSet selected,Color selectionColor) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.fillOval((int) transformed[i][0],
            (int) transformed[i][1], transformed[i][2], newSize / 2,
            newSize / 2);
        slate.setColor(selectionColor);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], newSize+2, newSize+2);
        }
      }
    }

    @Override
    public String toString() {
      return "Dots";
    }
  },
  // ////////////////////////////////////////////
  CIRCLE {
    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size) {
      for (int i = 0; i < n; i++) {
        slate.drawOval((int) transformed[i][0], (int) transformed[i][1], size,
            size);
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.drawOval((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2], newSize / 2, newSize / 2);
      }
    }

    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size,
        BitSet selected) {
      for (int i = 0; i < n; i++) {
        slate.drawOval((int) transformed[i][0], (int) transformed[i][1], size,
            size);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], size+2, size+2);
        }
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a, BitSet selected) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.drawOval((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2], newSize / 2, newSize / 2);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], newSize+2, newSize+2);
        }
      }
    }

    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size,
        byte[][] rgb) {
      for (int i = 0; i < n; i++) {
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.drawOval((int) transformed[i][0], (int) transformed[i][1], size,
            size);
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a, byte[][] rgb) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.drawOval((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2], newSize / 2, newSize / 2);
      }
    }

    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size,
        byte[][] rgb, BitSet selected,Color selectionColor) {
      for (int i = 0; i < n; i++) {
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.drawOval((int) transformed[i][0], (int) transformed[i][1], size,
            size);
        slate.setColor(selectionColor);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], size+2, size+2);
        }
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a, byte[][] rgb, BitSet selected,Color selectionColor) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.drawOval((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2], newSize / 2, newSize / 2);
        slate.setColor(selectionColor);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], newSize+2, newSize+2);
        }
      }
    }

    @Override
    public String toString() {
      return "Circles";
    }
  },
  // /////////////////////////////////////////////////
  BLOCK {
    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size) {
      for (int i = 0; i < n; i++) {
        slate.fillRect((int) transformed[i][0], (int) transformed[i][1], size,
            size);
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.fillRect((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2], newSize / 2, newSize / 2);
      }
    }

    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size,
        BitSet selected) {
      for (int i = 0; i < n; i++) {
        slate.fillRect((int) transformed[i][0], (int) transformed[i][1], size,
            size);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], size+2, size+2);
        }
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a, BitSet selected) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.fillRect((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2], newSize / 2, newSize / 2);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], newSize+2, newSize+2);
        }
      }
    }

    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size,
        byte[][] rgb) {
      for (int i = 0; i < n; i++) {
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.fillRect((int) transformed[i][0], (int) transformed[i][1], size,
            size);
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a, byte[][] rgb) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.fillRect((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2], newSize / 2, newSize / 2);
      }
    }

    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size,
        byte[][] rgb, BitSet selected,Color selectionColor) {
      for (int i = 0; i < n; i++) {
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.fillRect((int) transformed[i][0], (int) transformed[i][1], size,
            size);
        slate.setColor(selectionColor);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], size+2, size+2);
        }
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a, byte[][] rgb, BitSet selected,Color selectionColor) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.fillRect((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2], newSize / 2, newSize / 2);
        slate.setColor(selectionColor);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], newSize+2, newSize+2);
        }
      }
    }

    @Override
    public String toString() {
      return "Blocks";
    }
  },
  // ////////////////////////////////////////////////
  SQUARE {
    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size) {
      for (int i = 0; i < n; i++) {
        slate.drawRect((int) transformed[i][0], (int) transformed[i][1], size,
            size);
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.drawRect((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2], newSize / 2, newSize / 2);
      }
    }

    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size,
        BitSet selected) {
      for (int i = 0; i < n; i++) {
        slate.drawRect((int) transformed[i][0], (int) transformed[i][1], size,
            size);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], size+2, size+2);
        }
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a, BitSet selected) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.drawRect((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2], newSize / 2, newSize / 2);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], newSize+2, newSize+2);
        }
      }
    }

    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size,
        byte[][] rgb) {
      for (int i = 0; i < n; i++) {
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.drawRect((int) transformed[i][0], (int) transformed[i][1], size,
            size);
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a, byte[][] rgb) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.drawRect((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2], newSize / 2, newSize / 2);
      }
    }

    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size,
        byte[][] rgb, BitSet selected,Color selectionColor) {
      for (int i = 0; i < n; i++) {
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.drawRect((int) transformed[i][0], (int) transformed[i][1], size,
            size);
        slate.setColor(selectionColor);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], size+2, size+2);
        }
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a, byte[][] rgb, BitSet selected,Color selectionColor) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.drawRect((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2], newSize / 2, newSize / 2);
        slate.setColor(selectionColor);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], newSize+2, newSize+2);
        }
      }
    }

    @Override
    public String toString() {
      return "Squares";
    }
  },
  // ///////////////////////////////////////////////////
  XSHAPE {
    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size) {
      for (int i = 0; i < n; i++) {
        slate.drawX((int) transformed[i][0], (int) transformed[i][1], size,
            size);
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.drawX((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2], newSize / 2, newSize / 2);
      }
    }

    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size,
        BitSet selected) {
      for (int i = 0; i < n; i++) {
        slate.drawX((int) transformed[i][0], (int) transformed[i][1], size,
            size);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], size+2, size+2);
        }
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a, BitSet selected) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.drawX((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2], newSize / 2, newSize / 2);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], newSize+2, newSize+2);
        }
      }
    }

    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size,
        byte[][] rgb) {
      for (int i = 0; i < n; i++) {
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.drawX((int) transformed[i][0], (int) transformed[i][1], size,
            size);
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a, byte[][] rgb) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.drawX((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2], newSize / 2, newSize / 2);
      }
    }

    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size,
        byte[][] rgb, BitSet selected, Color selectionColor) {
      for (int i = 0; i < n; i++) {
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.drawX((int) transformed[i][0], (int) transformed[i][1], size,
            size);
        slate.setColor(selectionColor);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], size+2, size+2);
        }
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a, byte[][] rgb, BitSet selected,Color selectionColor) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.drawX((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2], newSize / 2, newSize / 2);
        slate.setColor(selectionColor);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], newSize+2, newSize+2);
        }
      }
    }

    @Override
    public String toString() {
      return "Xs";
    }
  },
  // /////////////////////////////////////////////////////
  PLUS {
    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size) {
      for (int i = 0; i < n; i++) {
        slate.drawPlus((int) transformed[i][0], (int) transformed[i][1], size,
            size);
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.drawPlus((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2], newSize / 2, newSize / 2);
      }
    }

    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size,
        BitSet selected) {
      for (int i = 0; i < n; i++) {
        slate.drawPlus((int) transformed[i][0], (int) transformed[i][1], size,
            size);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], size+2, size+2);
        }
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a, BitSet selected) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.drawPlus((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2], newSize / 2, newSize / 2);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], newSize+2, newSize+2);
        }
      }
    }

    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size,
        byte[][] rgb) {
      for (int i = 0; i < n; i++) {
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.drawPlus((int) transformed[i][0], (int) transformed[i][1], size,
            size);
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a, byte[][] rgb) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.drawPlus((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2], newSize / 2, newSize / 2);
      }
    }

    @Override
		public void draw2D(Slate slate, int n, float[][] transformed, int size,
        byte[][] rgb, BitSet selected, Color selectionColor) {
      for (int i = 0; i < n; i++) {
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.drawPlus((int) transformed[i][0], (int) transformed[i][1], size,
            size);
        slate.setColor(selectionColor);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], size+2, size+2);
        }
      }
    }

    @Override
		public void draw3D(Slate slate, View view, int n, float[][] transformed,
        float a, byte[][] rgb, BitSet selected, Color selectionColor) {
      int newSize;
      for (int i = 0; i < n; i++) {
        newSize = (int) (view.getPerspectiveFactor(transformed[i][2]) / a);
        slate.setColor(new Color(0x000000FF & rgb[i][0],
            0x000000FF & rgb[i][1], 0x000000FF & rgb[i][2]));
        slate.drawPlus((int) transformed[i][0], (int) transformed[i][1],
            transformed[i][2], newSize / 2, newSize / 2);
        slate.setColor(selectionColor);
        if (selected.get(i)) {
          slate
              .drawOval((int) transformed[i][0], (int) transformed[i][1], newSize+2, newSize+2);
        }
      }
    }

    @Override
    public String toString() {
      return "+s";
    }
  }
}

interface ShapeDraw {
  // CONSTANT COLOR - FASTEST
  public abstract void draw2D(Slate slate, int n, float[][] transformed,
      int size, BitSet selected);

  public abstract void draw3D(Slate slate, View view, int n,
      float[][] transformed, float a, BitSet selected);

  public abstract void draw2D(Slate slate, int n, float[][] transformed,
      int size);

  public abstract void draw3D(Slate slate, View view, int n,
      float[][] transformed, float a);

  // VARIABLE COLOR - FASTER
  public abstract void draw2D(Slate slate, int n, float[][] transformed,
      int size, byte[][] rgb);

  public abstract void draw3D(Slate slate, View view, int n,
      float[][] transformed, float a, byte[][] rgb);

  // VARIABLE COLOR WITH SELECTION -FAST
  public abstract void draw2D(Slate slate, int n, float[][] transformed,
      int size, byte[][] rgb, BitSet selected, Color selectionColor);

  public abstract void draw3D(Slate slate, View view, int n,
      float[][] transformed, float a, byte[][] rgb, BitSet selected,Color selectionColor);
}
