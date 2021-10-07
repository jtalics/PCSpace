package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.Tool1Exception;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.DataSource;

public class PointsetCreateAction extends Tool1Action {
  private Session session;

  public PointsetCreateAction(final Session session) {
    super("Create pointset", null);
    this.session = session;
    putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, 0));
    putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    putValue(ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "Create pointset");
    putValue(LONG_DESCRIPTION, "Generate a pointset from selected data source");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String) getValue(Action.NAME));
    try {
      final DataSource dataSource = session.dataSourceManager.getViewlet()
          .getSelectedDataSource();
      if (dataSource == null) {
        throw new Tool1Exception("no data source selected");
      }
      session.pointsetManager.createPointset(dataSource);
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot make pointset because:\n", ex, "todo");
    }
    finally {
       // nop
    }
  }

  private static final long serialVersionUID = 1L;
}
