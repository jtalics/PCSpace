
package gov.nih.ncgc.openhts.tool1.plotManager;

import javax.help.CSH;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

public class EscapeDialog extends JDialog {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  public EscapeDialog() {
    this((Frame)null, false);
  }
  public EscapeDialog(final Frame owner) {
    this(owner, false);
  }
  public EscapeDialog(final Frame owner, final boolean modal) {
    this(owner, null, modal);
  }
  public EscapeDialog(final Frame owner, final String title) {
    this(owner, title, false);
  }
  public EscapeDialog(final Frame owner, final String title, final boolean modal) {
    super(owner, title, modal);
  }
  public EscapeDialog(final Dialog owner) {
    this(owner, false);
  }
  public EscapeDialog(final Dialog owner, final boolean modal) {
    this(owner, null, modal);
  }
  public EscapeDialog(final Dialog owner, final String title) {
    this(owner, title, false);
  }
  public EscapeDialog(final Dialog owner, final String title, final boolean modal) {
    super(owner, title, modal);
  }
  @Override
  protected JRootPane createRootPane() {
    final ActionListener actionListener = new ActionListener() {
      @Override
			public void actionPerformed(ActionEvent actionEvent) {
        setVisible(false);
      }
    };
    final JRootPane rootPane = new JRootPane();
    CSH.setHelpIDString(rootPane,"top");
    final KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    rootPane.registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

//    rootPane.registerKeyboardAction(
//      new CSH.DisplayHelpAfterTracking(JPlotter.getHelpBroker()),
//      KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0),
//      JComponent.WHEN_IN_FOCUSED_WINDOW);
    return rootPane;
  }
}

