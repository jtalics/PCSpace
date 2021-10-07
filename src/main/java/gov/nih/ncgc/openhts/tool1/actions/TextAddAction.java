package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.plotManager.Text;
import gov.nih.ncgc.openhts.tool1.plotManager.TextAddDialog;

public class TextAddAction extends Tool1Action {
  private Session session;

  public TextAddAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Text: Add");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(Action.ACTION_COMMAND_KEY, "Text: Add");
    putValue(SHORT_DESCRIPTION, "X");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {
        final Text text = new Text(session);
        text.initialize();
        final TextAddDialog textAddDialog = session.dialogManager.getTextAddDialog();
        textAddDialog.showDialog();
        if (textAddDialog.isOk()) {
          session.plotManager.addTextAndRefresh(text);
        }
      }
      catch (final Throwable ex) {
        session.errorSupport("Cannot add text because:\n", ex, "add_data_menu_error");
      }
      finally {
        // nop
      }
  }

  private static final long serialVersionUID = 1L;
}
