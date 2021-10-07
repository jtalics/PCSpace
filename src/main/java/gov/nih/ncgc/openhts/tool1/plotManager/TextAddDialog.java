package gov.nih.ncgc.openhts.tool1.plotManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.help.CSH;
import javax.swing.Box;
import javax.swing.JPanel;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;
import gov.nih.ncgc.openhts.tool1.dialogManager.DialogManager;

public final class TextAddDialog extends AbstractDialog {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private final Session session;
  public TextModifyDialog modifyDialog;


  public TextAddDialog(final Session session) {
    super(session);
    this.session = session;
    setTitle("Text - Add");
    setModal(false);
    setResizable(false);
    addWindowListener(session.dialogManager);
    addComponentListener(DialogManager.getInstance(session));
    final JPanel topPanel = new JPanel();
    add(topPanel, BorderLayout.CENTER);
    CSH.setHelpIDString(topPanel, "add_text");
    topPanel.setLayout(new BorderLayout());
    topPanel.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.WEST);
    topPanel.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.EAST);
    topPanel.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.NORTH);
    final TextViewlet textViewlet = new TextViewlet(session);
    topPanel.add(textViewlet, BorderLayout.CENTER);
    
    setHelpID("help_modify_text_button");
    add(getButtonPanel(), BorderLayout.SOUTH);
  }

  @Override
  protected boolean apply() {
    try {
      final Text text = new Text(session);
      text.initialize();
      //apply(text, Text.ADD);
      dispose();
    }
    catch (final Throwable ex) {
      session.errorSupport("", ex, "ok_add_text_button_error");
    }
    return true; // apply was successful

  }
}
