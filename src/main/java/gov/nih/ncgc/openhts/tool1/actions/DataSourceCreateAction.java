package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.DataSource;
import gov.nih.ncgc.openhts.tool1.dialogManager.DataSourceNewDialog;

public class DataSourceCreateAction extends Tool1Action {
  private final Session session;

  public DataSourceCreateAction(final Session session) {
    super("Create", null);
    this.session = session;
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
    putValue(Action.ACTION_COMMAND_KEY, getClass().getName());
    putValue(SHORT_DESCRIPTION, "define a data source");
    putValue(LONG_DESCRIPTION, "define a data source");

    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
      try {
        session.statusPanel.log2("Accessing file system...");

        final DataSourceNewDialog dataSourceNewDialog = new DataSourceNewDialog(session) {

          @Override
          protected boolean apply() {
            List<String> pathList = new ArrayList<String>();
            for (int i=0; i < model.getSize(); i++) {    
              pathList.add((String)model.getElementAt(i));
            }
            try {
              DataSource dataSource = session.dataSourceManager.newDataSource(getNameString(),pathList);
              session.dataSourceManager.registerDataSource(dataSource);
            }
            catch (Throwable e) {
              session.errorSupport("Cannot create DataSource; ",e,"todo");
            }
            return true;
          }
          private static final long serialVersionUID = 1L;
        };
        dataSourceNewDialog.setTitle("Create a new data source");
        dataSourceNewDialog.setModal(false);
        // TODO: get rid of following
        final List<String> pathList = new ArrayList<String>();
        pathList.add(session.dataSourceManager.getHomeDir().getPath());
        dataSourceNewDialog.setPathList(pathList);
        dataSourceNewDialog.showDialog();
      }
      catch (final Throwable ex) {
        session.errorSupport("Cannot add data because:\n", ex, "add_data_menu_error");
      }
      finally {
        // nop
      }
  }

  private static final long serialVersionUID = 1L;
}
