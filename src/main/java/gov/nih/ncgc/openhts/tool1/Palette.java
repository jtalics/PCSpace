package gov.nih.ncgc.openhts.tool1;

import java.awt.Color;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class Palette {
  /**
   * Purpose is to ...
   * 
   * @author talafousj
   */
  public interface ChoiceColor {
    public abstract Color getColor();

    public abstract Color getTextColor();
  }

  public enum Choice implements ChoiceColor {
    // Even number are dark
    black {
      @Override
			public Color getColor() {
        return new Color(0x0, 0x0, 0x0);
      }

      @Override
			public Color getTextColor() {
        return Color.WHITE;
      }
    },
    white { // odd number are light
      @Override
			public Color getColor() {
        return new Color(0xFF, 0xFF, 0xFF);
      }

      @Override
			public Color getTextColor() {
        return Color.BLACK;
      }
    },
    darkGray {
      @Override
			public Color getColor() {
        return new Color(0xA9, 0xA9, 0xA9);
      }

      @Override
			public Color getTextColor() {
        return Color.WHITE;
      }
    },
    lightGray {
      @Override
			public Color getColor() {
        return new Color(0xD3, 0xD3, 0xD3);
      }

      @Override
			public Color getTextColor() {
        return Color.BLACK;
      }
    },
    darkPurple {
      @Override
			public Color getColor() {
        return new Color(0x94, 0x0, 0xD3);
      }

      @Override
			public Color getTextColor() {
        return Color.WHITE;
      }
    },
    lightPurple {
      @Override
			public Color getColor() {
        return new Color(0x93, 0x70, 0xDB);
      }

      @Override
			public Color getTextColor() {
        return Color.BLACK;
      }
    },
    darkBlue {
      @Override
			public Color getColor() {
        return new Color(0x0, 0x0, 0x8b);
      }

      @Override
			public Color getTextColor() {
        return Color.WHITE;
      }
    },
    lightBlue {
      @Override
			public Color getColor() {
        return new Color(0xAD, 0xD8, 0xE8);
      }

      @Override
			public Color getTextColor() {
        return Color.BLACK;
      }
    },
    darkCyan {
      @Override
			public Color getColor() {
        return new Color(0x0, 0x8B, 0x8B);
      }

      @Override
			public Color getTextColor() {
        return Color.WHITE;
      }
    },
    lightCyan {
      @Override
			public Color getColor() {
        return new Color(0x0, 0xFF, 0xFF);
      }

      @Override
			public Color getTextColor() {
        return Color.BLACK;
      }
    },
    darkGreen {
      @Override
			public Color getColor() {
        return new Color(0x0, 0x64, 0x0);
      }

      @Override
			public Color getTextColor() {
        return Color.WHITE;
      }
    },
    lightGreen {
      @Override
			public Color getColor() {
        return new Color(0x00, 0xFF, 0x00);
      }

      @Override
			public Color getTextColor() {
        return Color.BLACK;
      }
    },
    darkYellow {
      @Override
			public Color getColor() {
        return new Color(0xFF, 0xD7, 0x00);
      }

      @Override
			public Color getTextColor() {
        return Color.WHITE;
      }
    },
    lightYellow {
      @Override
			public Color getColor() {
        return new Color(0xFF, 0xFF, 0x00);
      }

      @Override
			public Color getTextColor() {
        return Color.BLACK;
      }
    },
    darkOrange {
      @Override
			public Color getColor() {
        return new Color(0xFF, 0x8C, 0x00);
      }

      @Override
			public Color getTextColor() {
        return Color.WHITE;
      }
    },
    lightOrange {
      @Override
			public Color getColor() {
        return new Color(0xFF, 0xA5, 0x0);
      }

      @Override
			public Color getTextColor() {
        return Color.BLACK;
      }
    },
    darkRed {
      @Override
			public Color getColor() {
        return new Color(0x8B, 0x0, 0x0);
      }

      @Override
			public Color getTextColor() {
        return Color.WHITE;
      }
    },
    lightRed {
      @Override
			public Color getColor() {
        return new Color(0xFF, 0x0, 0x0);
      }

      @Override
			public Color getTextColor() {
        return Color.BLACK;
      }
    };
    private static Choice choice;

    public static Choice getChoice() {
      return choice;
    }
  }

  public static Choice getClosestChoice(Color color) {
    Choice closestChoice = null;
    double r = color.getRed(), g = color.getGreen(), b = color.getBlue();
    double rDist, gDist, bDist, dist, closestDist = Double.MAX_VALUE;
    for (Choice choice : Choice.values()) {
      dist = Math.sqrt(r * r + g * g + b * b);
      if (dist < closestDist) {
        closestDist = dist;
        closestChoice = choice;
      }
    }
    return closestChoice;
  }

  public static Color getComplement(final Color c) {
    if (c == null) {
      return c;
    }
    final int r = 255 - c.getRed();
    final int g = 255 - c.getGreen();
    final int b = 255 - c.getBlue();
    return new Color(r, g, b);
  }
}
// end of file
