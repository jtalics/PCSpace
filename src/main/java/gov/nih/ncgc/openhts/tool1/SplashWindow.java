package gov.nih.ncgc.openhts.tool1;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class SplashWindow extends JWindow {
  private static final long serialVersionUID = 1L;
  private JLabel loadingText;
  private JLabel image;
  static private SplashWindow instance = null;
  public static Color textColor = Color.black;

  private SplashWindow() {
    // nop
  }

  public static SplashWindow getInstance() {
    synchronized (SplashWindow.class) {
      if (null == instance) {
        instance = new SplashWindow();
      }
    }
    return instance;
  }

  private void initialize() {
    setLayout(null);
    //this.setAlwaysOnTop(true);
    loadingText = new JLabel("Loading...");
    add(loadingText);
    image = new JLabel(AppLafManager.getSplash());
    image.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    add(image);
    final Dimension size = image.getPreferredSize();
    image.setBounds(0, 0, size.width, size.height);
    loadingText.setAlignmentX(Component.LEFT_ALIGNMENT);
    loadingText.setBounds(270, 300, 260, loadingText.getPreferredSize().height);
    loadingText.setForeground(textColor);
    setSize(size.width, size.height);
    //DialogUtilities.centerDialog(this);
    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    this.setLocation(dimension.width/2-size.width/2, dimension.height/2-size.height/2);
    setVisible(true);
  }

  private void disposeSplash() {
    synchronized (SplashWindow.class) {
      if (isShowing()) {
        setVisible(false);
        //dispose();
        //qxinstance = null;
      }
    }
  }

  private void updateText(final String text) {
    loadingText.setText(text);
  }

  static public void showSplash() {
    if (SwingUtilities.isEventDispatchThread()) {
      getInstance().initialize();
    }
    else {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
				public void run() {
          showSplash();
        }
      });
    }
  }

  static public void hideSplash() {
    if (SwingUtilities.isEventDispatchThread()) {
      getInstance().disposeSplash();
    }
    else {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
				public void run() {
          hideSplash();
        }
      });
    }
  }

  static public void updateLoadingText(final String text) {
    if (instance == null) {
      return;
    }
    if (SwingUtilities.isEventDispatchThread()) {
      getInstance().updateText(text);
    }
    else {
        SwingUtilities.invokeLater(new Runnable() {
          @Override
					public void run() {
            updateLoadingText(text);
          }
        });
    }
  }
}
