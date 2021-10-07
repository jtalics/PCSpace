package gov.nih.ncgc.openhts.tool1.plotManager;

/**
 * <p>Title: JHLauncher </p>
 * <p>Description: Java Help Launcher</p>
 * <p>Copyright: Optive Research, Inc (c) 2003</p>
 * <p>Company: Optive Research, Inc </p>
 * @author Dong Yuan
 * @version 1.0
 */

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.help.DefaultHelpModel;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.help.JHelp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class JHLauncher {

  static JFrame frame;

  static String currentID = null;

  private static JHelp jh = null;

  static HelpSet hs = null;

  static protected Image image;

  // The initial width and height of the frame
  public static int WIDTH = 1000;
  public static int HEIGHT = 700;

  /**
   * Default Constructor
   */
  public JHLauncher() {
    // nop

  }

  /**
   *
   * @param int value system exit value
   */
  private static void exit(final int value)
  {
    System.exit(value);
  }

  /**
   * Main selectedFunction
   */
  public static void main(final String [] args) {

    if(args.length == 1)
    {
      currentID = args[0];
    }

    final ClassLoader cl = JHLauncher.class.getClassLoader();
    URL url = null;

    // "help/Help.hs" is hard coded here
    try
    {
      url = HelpSet.findHelpSet(cl, "help/Help.hs");
    }
    catch(final Exception e)
    {
      System.out.println("File Help.hs is not available!");
      exit(1);
    }

    //Initialize help set
    try
    {
      hs = new HelpSet(cl, url);
    }
    catch (final HelpSetException ee) {
      System.out.println("Help set cannot be initialized!");
      exit(1);
    }

    if (jh == null) {
        jh = new JHelp(hs);
        jh.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        if(currentID != null) {
          jh.setCurrentID(currentID);
        }
    } else {
        final DefaultHelpModel m = new DefaultHelpModel(hs);
        jh.setModel(m);
    }

    getIconImage();

    createFrame (hs.getTitle());
    launch();

  }

  /**
   *
   */
  private static void getIconImage()
  {
    /* Replace Java cup icon with our lx icon */
    final ImageIcon icon = getImageIcon("/images/lx.gif");

    if (icon != null) {image = icon.getImage();}
    if (image == null) {
      System.out.println("Icon image unavailable.");
    }

  }

  /**
   *
   */
  static protected void launch() {
    if (frame == null) {
      return;
    }
    frame.setVisible(true);
  }

  /**
   *
   * @param title
   * @return
   */
  static protected void createFrame(final String title) {

      if (frame == null) {
          final WindowListener closer = new WindowAdapter() {
              @Override
              public void windowClosing(WindowEvent e) {
                  exit(0);
              }
              @Override
              public void windowClosed(WindowEvent e) {
                  exit(0);
              }
          };

          frame = new JFrame(title);
          frame.setSize(WIDTH, HEIGHT);
          frame.setForeground(Color.black);
          frame.setBackground(Color.lightGray);

          frame.setIconImage(image);

          frame.addWindowListener(closer);
          frame.getContentPane().add(jh);	// the JH panel

      }

      frame.pack();
    }

  /**
   * Create an Icon from a given resource.
   *
   * This works using getResourceAsStream() because several browsers do not
   * correctly implement getResource().
   */
  public static ImageIcon getImageIcon( final String image) {
      final byte[][] buffer = new byte[1][];
      try {
          final InputStream resource = JHLauncher.class.getResourceAsStream(image);
          if (resource == null) {
              return null;
          }
          final BufferedInputStream in = new BufferedInputStream(resource);
          final ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
          buffer[0] = new byte[1024];
          int n;
          while ((n = in.read(buffer[0])) > 0) {
              out.write(buffer[0], 0, n);
          }
          in.close();
          out.flush();
          buffer[0] = out.toByteArray();
      } catch (final IOException ioe) {
          //System.err.println(ioe.toString());
          return null;
      }
      if (buffer[0] == null) {
          System.out.println( image + " not found.");
          return null;
      }
      if (buffer[0].length == 0) {
          System.out.println("warning: " + image +
                             " is zero-length");
          return null;
      }

      return new ImageIcon(buffer[0]);
  }

}
