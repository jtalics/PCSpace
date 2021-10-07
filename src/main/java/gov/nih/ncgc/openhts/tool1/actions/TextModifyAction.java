package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.plotManager.TextModifyDialog;

public class TextModifyAction extends Tool1Action {
  private Session session;

  public TextModifyAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Text: Modify");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_M);
    putValue(Action.ACTION_COMMAND_KEY, "Text: Modify");
    putValue(SHORT_DESCRIPTION, "change properties of text");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      final TextModifyDialog textModifyDialog = session.dialogManager.getTextModifyDialog();
      textModifyDialog.setModel(session.plotManager.selectedText);
      textModifyDialog.showDialog();
      session.updateEnablement();
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot modify text because:\n", ex,
          "modify_text_menu_error");
    }
    finally {
      // nop
    }
  }

  private static final long serialVersionUID = 1L;
}
