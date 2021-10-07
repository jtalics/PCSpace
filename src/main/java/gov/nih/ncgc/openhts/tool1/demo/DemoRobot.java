package gov.nih.ncgc.openhts.tool1.demo;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import gov.nih.ncgc.openhts.tool1.DialogUtilities;
import gov.nih.ncgc.openhts.tool1.Session;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class DemoRobot extends Robot {
  private final Session session;
  private final JDialog dialog;
  private final JEditorPane htmlDisplay = new JEditorPane();
  // Declaring worker here encourages only one worker going at a time
  DemoWorker worker;
  public List<Demo> demos = new ArrayList<Demo>();
  private JButton continueButton;
  private JButton stopButton;
  int TICK = 250;

  public DemoRobot(final Session session) throws AWTException {
    this.session = session;
    demos.add(new Demo0(session, this));
    demos.add(new Demo1(session, this));
    demos.add(new Demo2(session, this));
    demos.add(new Demo3(session, this));
    demos.add(new Demo4(session, this));
    demos.add(new Demo5(session, this));
    final DemoRobotDialog dialog2 = new DemoRobotDialog(session, this);
    dialog2.showDialog(); // modal block
    dialog = new JDialog(session.frame);
    if (dialog2.isOk()) {
      try {
        htmlDisplay.setEditable(false);
        htmlDisplay.setContentType("text/html");
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.add(new JScrollPane(htmlDisplay), BorderLayout.CENTER);
        // TODO: control for TICK
        final JPanel buttonPanel = new JPanel(new GridLayout());
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        continueButton = new JButton("Continue");
        buttonPanel.add(continueButton);
        continueButton.addActionListener(new ActionListener() {
          @Override
					public void actionPerformed(final ActionEvent e) {
            dialog.toBack();
            dialog.setVisible(false); // unblocks
          }
        });
        stopButton = new JButton("Stop");
        buttonPanel.add(stopButton);
        stopButton.addActionListener(new ActionListener() {
          @Override
					public void actionPerformed(final ActionEvent e) {
            worker.cancel(true);
          }
        });
        // Prepare Dialog but do not show it
        dialog.setPreferredSize(new Dimension(320, 200));
        dialog.pack();
        DialogUtilities.centerDialog(dialog);
        dialog.setModal(true);
        demos.get(dialog2.getSelectedDemoIndex()).perform();
      }
      catch (final Exception e) {
        e.printStackTrace();
      }
    }
    this.setAutoDelay(TICK);
  }

  void finished(final String htmlMessage) {
    System.out.println("Done.");
    continueButton.setEnabled(false);
    stopButton.setEnabled(false);
    if (worker.isCancelled()) {
      setHTML("Demo cancelled.");
      dialog.setVisible(true);
      dialog.toFront();
      dialog.requestFocus();
    }
    if (worker.isDone()) {
      setHTML(htmlMessage);
      dialog.setVisible(true);
      dialog.toFront();
      dialog.requestFocus();
    }
  }

  void keyTyped(final int keycode, final int repetitions) {
    for (int i = 0; i < repetitions; i++) {
      keyTyped(keycode);
    }
  }

  void keyTyped(final int keyCode) {
    System.out.println("ROBOT keyTyped: " + KeyEvent.getKeyText(keyCode));
    keyPress(keyCode);
    keyRelease(keyCode);
  }

  void keyTypedWithModifier(final int keyCode, final int modifierCode) {
    System.out.println("ROBOT keyTyped: " + KeyEvent.getKeyText(modifierCode)
        + "-" + KeyEvent.getKeyText(keyCode));
    keyPress(modifierCode);
    keyPress(keyCode);
    keyRelease(keyCode);
    keyRelease(modifierCode);
  }

  boolean showModalDialogAndContinue() {
    // session.toBack();
    dialog.setVisible(true); // modal block
    if (worker.isCancelled()) {
      return false;
    }
    session.frame.toFront();
    session.frame.requestFocus();
    delay(TICK);
    return true;
  }

  void setHTML(final String string) {
    htmlDisplay.setText(string);
  }

  Point pC(final int x, final int y) {
    final Point p = session.frame.getLocationOnScreen();
    return new Point(p.x + session.frame.getWidth() / 2 + x, p.y
        + session.frame.getHeight() / 2 + y);
  }

  void typeString(final String s) {
    final int toUpper = 65 - 97;
    try {
      for (int i = 0; i < s.length(); i++) {
        final char c = s.charAt(i);
        if (Character.isLetter(c)) {
          if (Character.isUpperCase(c)) {
            keyPress(KeyEvent.VK_SHIFT);
            keyPress(c);
            keyRelease(c);
            keyRelease(KeyEvent.VK_SHIFT);
          }
          else {
            keyPress(c + toUpper);
            keyRelease(c + toUpper);
          }
        }
        else if (Character.isDigit(c)) {
          keyPress(c);
          keyRelease(c);
        }
        else {
          keyPress(c);
          keyRelease(c);
        }
        delay(TICK);
      }
    }
    catch (final Throwable t) {
      t.printStackTrace();
    }
  }

  void moveToComponentAndClick(final Component component, final int buttonMask,
      final int nClick) {
    final Point p = component.getLocationOnScreen();
    final int x = p.x + component.getWidth() / 2;
    final int y = p.y + component.getHeight() / 2;
    moveToPointAndClick(new Point(x, y), buttonMask, nClick);
  }

  void moveToPointAndClick(final Point point, final int buttonMask, final int nClick) {
    moveToPoint(point);
    for (int i = 0; i < nClick; i++) {
      mousePress(buttonMask);
      mouseRelease(buttonMask);
    }
  }

  void moveToPoint(final Point point) {
    final int xEnd = point.x;
    final int yEnd = point.y;
    double div;
    final Point start = MouseInfo.getPointerInfo().getLocation();
    if ((xEnd - start.x) < 50.0) {
      div = 15;
    }
    else if ((xEnd - start.x) < 100.0) {
      div = 30;
    }
    else {
      div = 60;
    }
    final double xInc = (xEnd - start.x) / div;
    final double yInc = (yEnd - start.y) / div;
    double x = start.x;
    double y = start.y;
    for (int step = 0; step < div; step++) {
      x += xInc;
      y += yInc;
      mouseMove((int) x, (int) y);
      delay(10);
    }// end while loop
    delay(50);
  }

  public void setTitle(final String title) {
    dialog.setTitle(title);
  }

  public void delayNTimes(final int msec, final int times) {
    for (int i=0; i<times; i++) {
      delay(msec);
    }
  }
}
