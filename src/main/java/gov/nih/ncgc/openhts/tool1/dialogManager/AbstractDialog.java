package gov.nih.ncgc.openhts.tool1.dialogManager;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.help.CSH;
import javax.help.HelpBroker;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import gov.nih.ncgc.openhts.tool1.DialogUtilities;
import gov.nih.ncgc.openhts.tool1.Main;
import gov.nih.ncgc.openhts.tool1.Session;

/**
 * The purpose of AbstractDialog is to supply common functionality for a common
 * L & F. By default, there are three buttons: OK, Cancel, Help. To extend this
 * class, you must supply doOK() and doCancel(). By default, the Help button
 * brings you to the top of the Help system, if you want to specify a particular
 * page, you must supply setHelpId("myString") and modify your javahelp map file
 * accordingly. If you want to remove the Help button, you may supply
 * setHelpId(null). Advanced users have direct access to the buttons via
 * getButtonPanel().get<X>Button();
 */
public abstract class AbstractDialog extends JDialog {
  private final Window owner;
  private boolean ok;
  protected Session session;

  /**
   * The purpose of a ButtonPanel is to provide at most three buttons.
   */
  public class ButtonPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JButton okButton = null;
    private JButton applyButton = null;
    private JButton cancelButton = null;
    private JButton helpButton = null;

    /**
     * 
     */
    private ButtonPanel() {
      setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
      setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
      add(Box.createHorizontalGlue());
      add(getOkButton());
      AbstractDialog.this.getRootPane().setDefaultButton(okButton);
      add(Box.createRigidArea(new Dimension(8, 0)));
      if (!isModal()) {
        add(getApplyButton());
        add(Box.createRigidArea(new Dimension(8, 0)));
      }
      add(getCancelButton());
      add(Box.createRigidArea(new Dimension(8, 0)));
      add(getHelpButton());
      add(Box.createRigidArea(new Dimension(8, 0)));
    }

    /**
     * Manually set an OK button.
     * 
     * @param okButton
     */
    public void setOkButton(final JButton okButton) {
      if (okButton == null) {
        remove(okButton);
      }
      this.okButton = okButton;
      this.okButton.setAlignmentY(Component.CENTER_ALIGNMENT);
    }

    /**
     * Manually set an OK button.
     * 
     * @param okButton
     */
    public void setApplyButton(final JButton applyButton) {
      if (applyButton == null) {
        remove(applyButton);
      }
      this.applyButton = applyButton;
      this.applyButton.setAlignmentY(Component.CENTER_ALIGNMENT);
    }

    /**
     * Manually set the Cancel button.
     * 
     * @param cancelButton
     */
    public void setCancelButton(final JButton cancelButton) {
      if (cancelButton == null && this.cancelButton != null) {
        remove(this.cancelButton);
      }
      this.cancelButton = cancelButton;
    }

    /**
     * Get the OK button if exists, else make default one.
     * 
     * @return
     */
    public final JButton getOkButton() {
      if (okButton == null) {
        okButton = new JButton();
        okButton.setText("OK");
        okButton.setMnemonic(KeyEvent.VK_O);
        okButton.setPreferredSize(new Dimension(80,
            okButton.getPreferredSize().height));
        okButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        okButton.addActionListener(new java.awt.event.ActionListener() {
          @Override
					public void actionPerformed(final java.awt.event.ActionEvent e) {
            doOk();
          }
        });
      }
      return okButton;
    }

    /**
     * Get the Apply button if exists, else make default one.
     * 
     * @return
     */
    public final JButton getApplyButton() {
      if (applyButton == null) {
        applyButton = new JButton();
        applyButton.setText("Apply");
        applyButton.setMnemonic(KeyEvent.VK_A);
        applyButton.setPreferredSize(new Dimension(80, okButton
            .getPreferredSize().height));
        applyButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        applyButton.addActionListener(new java.awt.event.ActionListener() {
          @Override
					public void actionPerformed(final java.awt.event.ActionEvent e) {
            doApply();
          }
        });
      }
      return applyButton;
    }

    /**
     * Manually set the help button.
     * 
     * @param cancelButton
     */
    public void setHelpButton(final JButton helpButton) {
      if (helpButton == null && this.helpButton != null) {
        remove(this.helpButton);
      }
      this.helpButton = helpButton;
    }

    /**
     * Get the Help button if exists, else make default one.
     * 
     * @return
     */
    public final JButton getHelpButton() {
      if (helpButton == null) {
        helpButton = new JButton();
        helpButton.setText("Help");
        helpButton.setMnemonic(KeyEvent.VK_H);
        helpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        helpButton.setPreferredSize(new Dimension(80, helpButton
            .getPreferredSize().height));
        this.setHelpId("TODO");
      }
      return helpButton;
    }

    /**
     * Get the Cancel button if exists, else make default one.
     * 
     * @return
     */
    public final JButton getCancelButton() {
      if (cancelButton == null) {
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.setPreferredSize(new Dimension(80, cancelButton
            .getPreferredSize().height));
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
          @Override
					public void actionPerformed(final java.awt.event.ActionEvent e) {
            doCancel();
          }
        });
      }
      return cancelButton;
    }

    /**
     * @param helpID
     */
    public void setHelpId(final String helpID) {
      if (helpID == null) {
        setHelpButton(null);
      }
      else {
        CSH.setHelpIDString(helpButton, helpID);
        final HelpBroker helpBroker = Main.getHelpBroker();
        if (helpBroker != null) {
          helpBroker.enableHelpOnButton(helpButton, helpID, null);
        }
      }
    }
  }

  private ButtonPanel buttonPanel;

  // ////////// CONSTRUCTORS
  public AbstractDialog(final Dialog owner) {
    super(owner);
    this.owner = owner;
    initialize();
  }

  public AbstractDialog(final Dialog owner, final boolean modal) {
    super(owner, modal ? Dialog.ModalityType.APPLICATION_MODAL
        : Dialog.ModalityType.MODELESS);
    this.owner = owner;
    initialize();
  }

  public AbstractDialog(final Dialog owner, final String title) {
    super(owner, title);
    this.owner = owner;
    initialize();
  }

  public AbstractDialog(final Dialog owner, final String title,
      final boolean modal) {
    super(owner, title, modal ? Dialog.ModalityType.APPLICATION_MODAL
        : Dialog.ModalityType.MODELESS);
    this.owner = owner;
    initialize();
  }

  public AbstractDialog(final Dialog owner, final String title,
      final boolean modal, final GraphicsConfiguration gc) {
    super(owner, title, modal ? Dialog.ModalityType.APPLICATION_MODAL
        : Dialog.ModalityType.MODELESS, gc);
    this.owner = owner;
    initialize();
  }

  public AbstractDialog(final Dialog owner, final ModalityType modalityType) {
    super(owner, modalityType);
    this.owner = owner;
    initialize();
  }

  public AbstractDialog(final Session session) {
    this(session.frame);
    this.session = session;
  }

  public AbstractDialog(final Session session, final boolean modal) {
    this(session.frame, modal);
    this.session = session;
  }

  public AbstractDialog(final Session session, final String title) {
    this(session.frame, title);
    this.session = session;
  }

  public AbstractDialog(final Session session, final String title,
      final boolean modal) {
    this(session.frame, title, modal);
    this.session = session;
  }

  public AbstractDialog(final Session session, final String title,
      final boolean modal, final GraphicsConfiguration gc) {
    this(session.frame, title, modal, gc);
    this.session = session;
  }

  public AbstractDialog(final Frame owner) {
    super(owner);
    this.owner = owner;
    initialize();
  }

  public AbstractDialog(final Frame owner, final boolean modal) {
    super(owner, modal ? Dialog.ModalityType.APPLICATION_MODAL
        : Dialog.ModalityType.MODELESS);
    this.owner = owner;
    initialize();
  }

  public AbstractDialog(final Frame owner, final String title) {
    super(owner, title);
    this.owner = owner;
    initialize();
  }

  public AbstractDialog(final Frame owner, final String title,
      final boolean modal) {
    super(owner, title, modal ? Dialog.ModalityType.APPLICATION_MODAL
        : Dialog.ModalityType.MODELESS);
    this.owner = owner;
    initialize();
  }

  public AbstractDialog(final Frame owner, final String title,
      final boolean modal, final GraphicsConfiguration gc) {
    super(owner, title, modal ? Dialog.ModalityType.APPLICATION_MODAL
        : Dialog.ModalityType.MODELESS, gc);
    this.owner = owner;
    initialize();
  }

  public AbstractDialog(final Frame owner, final ModalityType modalityType) {
    super(owner, modalityType);
    this.owner = owner;
    initialize();
  }

  public AbstractDialog(final Window owner) {
    super(owner);
    this.owner = owner;
    initialize();
  }

  public AbstractDialog(final Window owner, final boolean modal) {
    super(owner, modal ? Dialog.ModalityType.APPLICATION_MODAL
        : Dialog.ModalityType.MODELESS);
    this.owner = owner;
    initialize();
  }

  public AbstractDialog(final Window owner, final String title) {
    super(owner, title);
    this.owner = owner;
    initialize();
  }

  public AbstractDialog(final Window owner, final String title,
      final boolean modal) {
    super(owner, title, modal ? Dialog.ModalityType.APPLICATION_MODAL
        : Dialog.ModalityType.MODELESS);
    this.owner = owner;
    initialize();
  }

  public AbstractDialog(final Window owner, final String title,
      final boolean modal, final GraphicsConfiguration gc) {
    super(owner, title, modal ? Dialog.ModalityType.APPLICATION_MODAL
        : Dialog.ModalityType.MODELESS, gc);
    this.owner = owner;
    initialize();
  }

  public AbstractDialog(final Window owner, final ModalityType modalityType) {
    super(owner, modalityType);
    this.owner = owner;
    initialize();
  }

  /**
   * 
   */
  protected void initialize() {
    buttonPanel = new ButtonPanel();
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(final WindowEvent we) {
        doCancel();
      }
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.JDialog#createRootPane()
   */
  @Override
  protected JRootPane createRootPane() {
    final JRootPane rootPane = new JRootPane();
    // Set up the ESC key
    final ActionListener actionListener = new ActionListener() {
      @Override
			public void actionPerformed(ActionEvent actionEvent) {
        doCancel();
      }
    };
    final KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
    rootPane.registerKeyboardAction(actionListener, stroke,
        JComponent.WHEN_IN_FOCUSED_WINDOW);
    // The Enter key works by setting the default button
    return rootPane;
  }

  /**
   * Called when OK button is pressed, or Enter key is pressed
   */
  protected void doOk() {
    boolean applied = false;
    try {
      applied = apply();
    }
    catch (Exception ex) {
      // TODO: use session.error()
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
    }
    finally {
      if (!applied) {
        ok = false;
        return;
      }
      ok = true;
      setVisible(false);
    }
  }

  /**
   * Called when OK button is pressed, or Enter key is pressed
   */
  protected void doApply() {
    if (!apply()) {
      ok = false;
      return;
    }
    ok = true;
    // dispose();
  }

  /**
   * Called when user clicks OK or Apply button
   */
  protected boolean apply() {
    new RuntimeException("todo: apply").printStackTrace();
    return false;
  }

  /**
   * Called when Cancel button is clicked, or the ESC key is pressed
   */
  protected void doCancel() {
    ok = false;
    setVisible(false);
    // dispose();
  }

  /**
   * This method initializes buttonPanel
   * 
   * @return javax.swing.JPanel
   */
  protected final ButtonPanel getButtonPanel() {
    if (buttonPanel == null) {
      buttonPanel = new ButtonPanel();
    }
    return buttonPanel;
  }

  /**
   * 
   */
  public void setHelpID(final String helpID) {
    buttonPanel.setHelpId(helpID);
  }

  /**
   * Confirm the delete of one item
   * 
   * @return true if and only if OK is pressed
   * @deprecated use DialogUtilities.confirmDelete
   */
  @Deprecated
  public static boolean confirmDelete(final Window owner, final String itemName) {
    return confirmDelete(owner, new String[] { itemName });
  }

  /**
   * A simple confirm dialog.
   * 
   * @return true iff OK is pressed
   * @deprecated use DialogUtilities.confirmDelete
   */
  @Deprecated
  public static boolean confirmDelete(final Window owner,
      final String[] itemNamesToDelete) {
    String message = "Click OK to delete";
    if (itemNamesToDelete.length <= 10) {
      message += ":\n";
      for (final String itemName : itemNamesToDelete) {
        message += "  " + itemName + "\n";
      }
    }
    else if (itemNamesToDelete.length > 0) {
      message += " " + itemNamesToDelete.length + " items?";
    }
    else {
      message = "No items to delete.";
    }
    final JOptionPane optionPane = new JOptionPane(message,
        JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
    final JDialog confirmDialog = optionPane.createDialog(owner,
        "Confirm delete");
    confirmDialog.pack();
    DialogUtilities.centerDialog(confirmDialog);
    confirmDialog.setVisible(true);
    final int selectedValue = ((Integer) optionPane.getValue()).intValue();
    if (selectedValue == JOptionPane.OK_OPTION) {
      return true;
    }
    return false;
  }

  /**
   * Override this if you need to clean up, don't forget to call super.dispose()
   * to finish the job
   */
  @Override
  public void dispose() {
    setVisible(false);
    super.dispose();
  }

  public void showDialog() {
    pack();
    // TODO: don't re-center
    DialogUtilities.centerDialog(this);
    setVisible(true);
  }

  @Override
  public Window getOwner() {
    return owner;
  }

  public boolean isOk() {
    return ok;
  }
}
// end of file
