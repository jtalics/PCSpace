package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.plotManager.TextChooseDialog;

public class TextDeleteAction extends Tool1Action {
  private Session session;

  public TextDeleteAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Text: Delete");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);
    putValue(Action.ACTION_COMMAND_KEY, "Text: Delete");
    putValue(SHORT_DESCRIPTION, "remove text from canvas");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {
        final TextChooseDialog textChooseDialog = session.dialogManager.getTextChooseDialog();
        textChooseDialog.showDialog();
        if (textChooseDialog.isOk()) {
          // TODO: gte selected text from dialog
          session.plotManager.removeTextAndRefresh(null);
        }
      }
      catch (final Throwable ex) {
        session.errorSupport("Cannot delete text because:\n", ex, "delete_text_menu_error");
      }
      finally {
        // nop
      }
  }

  private static final long serialVersionUID = 1L;
}
