package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.DataSource;

public class DataSourceRemoveAction extends Tool1Action {
  private Session session;

  public DataSourceRemoveAction(final Session session) {
    super("Remove",null);
    this.session = session;
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, 0));
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "remove datasource");
    putValue(LONG_DESCRIPTION, "remove datasource");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {
        final DataSource dataSource = session.dataSourceManager.getViewlet().getSelectedDataSource();
        if (dataSource == null){
          session.errorHelp("You must select exactly one data source to remove.","TODO");
        }
        session.dataSourceManager.removeDataSource(dataSource);
      }
      catch (final Throwable ex) {
        session.errorSupport("Cannot remove data source because:\n",ex, "todo");
      }
      finally {
        // nop
      }
  }

  private static final long serialVersionUID = 1L;
}
