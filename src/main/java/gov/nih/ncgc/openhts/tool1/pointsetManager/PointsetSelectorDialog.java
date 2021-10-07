// BROKEN FILE!

package gov.nih.ncgc.openhts.tool1.pointsetManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.help.CSH;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;
import gov.nih.ncgc.openhts.tool1.dialogManager.DialogManager;

public final class PointsetSelectorDialog extends AbstractDialog {

  private int selectedIndex = 0;
  private final Session session;
  private final int mode;

  public PointsetSelectorDialog(final Session session, final int mode) {
    super(session, false);
    this.session = session;
    this.mode = mode;
    if (session.pointsetManager.pointsetCount() <= 0) {
      switch (mode) {
      case PointsetManager.REPLACE:
        session.message("There are no data sets to replace.");
        break;
      case PointsetManager.DELETE:
        session.message("There are no data sets to delete.");
        break;
      default:
        throw new RuntimeException("unknown mode");
      }
      return;
    }
    setLayout(new BorderLayout());
    setModal(false);
    setResizable(false);
    addWindowListener(DialogManager.getInstance(session));
    addComponentListener(DialogManager.getInstance(session));
    final JLabel label = new JLabel();
    final JPanel panel = new JPanel(new BorderLayout());
    add(panel, BorderLayout.CENTER);
    final JPanel subpanel1 = new JPanel(new BorderLayout());
    subpanel1.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.NORTH);
    subpanel1.add(Box.createRigidArea(new Dimension(7, 0)), BorderLayout.WEST);
    subpanel1.add(Box.createRigidArea(new Dimension(7, 0)), BorderLayout.EAST);
    subpanel1.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.SOUTH);
    subpanel1.add(label, BorderLayout.CENTER);
    panel.add(subpanel1, BorderLayout.NORTH);
    final JPanel subpanel2 = new JPanel(new BorderLayout());
    subpanel2.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.NORTH);
    subpanel2.add(Box.createRigidArea(new Dimension(7, 0)), BorderLayout.WEST);
    subpanel2.add(Box.createRigidArea(new Dimension(7, 0)), BorderLayout.EAST);
    subpanel2.add(Box.createRigidArea(new Dimension(0, 8)), BorderLayout.SOUTH);
    final JList choiceList = new JList(session.pointsetManager.getPointsets());
    final PointsetListCellRenderer dcr = new PointsetListCellRenderer(session);
    choiceList.setCellRenderer(dcr);
    choiceList.setBorder(BorderFactory.createLoweredBevelBorder());
    choiceList.setBackground(getBackground());
    choiceList.setSelectedIndex(selectedIndex);
    choiceList.addListSelectionListener(new ListSelectionListener() {
      @Override
			public void valueChanged(final ListSelectionEvent ev) {
        if (!ev.getValueIsAdjusting()) {
          selectedIndex = choiceList.getSelectedIndex();
        }
      }
    });
    final JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
    scrollPane.setViewportView(choiceList);
    scrollPane.setPreferredSize(new Dimension(450, 200));
    subpanel2.add(scrollPane, BorderLayout.CENTER);
    panel.add(subpanel2, BorderLayout.CENTER);
    switch (mode) {
    case PointsetManager.REPLACE:
      setTitle("Pointset - Replace");
      setHelpID("help_replace_data_button");
      label.setText("Select data source to replace:");
      label.setToolTipText("data sources available to replace");
      CSH.setHelpIDString(label, "select_replace_data_label");
      choiceList
          .setToolTipText("click on the data source which you want to replace");
      CSH.setHelpIDString(choiceList, "select_replace_data_list");
      CSH.setHelpIDString(subpanel1, "replace_data");
      CSH.setHelpIDString(subpanel2, "replace_data");
      CSH.setHelpIDString(scrollPane, "select_replace_data");
      break;
    case PointsetManager.DELETE:
      setTitle("Pointset - Delete");
      setHelpID("help_delete_data_button");
      choiceList
          .setToolTipText("click to select the data source you wish to delete");
      CSH.setHelpIDString(choiceList, "delete_data_list");
      label.setText("Select data source to delete:");
      label.setToolTipText("data sources available to delete");
      CSH.setHelpIDString(label, "delete_data");
      CSH.setHelpIDString(this, "delete_data");
      CSH.setHelpIDString(subpanel1, "delete_data");
      CSH.setHelpIDString(subpanel2, "delete_data");
      CSH.setHelpIDString(scrollPane, "delete_data");
      break;
    default:
      throw new RuntimeException("unknown mode");
    }
    add(getButtonPanel(), BorderLayout.SOUTH);
  }

  public void executeChoice() {
    final Pointset pointset = session.pointsetManager.getSelectedPointset();
    if (pointset == null) {
      return;
    }
    if (mode == PointsetManager.REPLACE) {
//      if (!replace()) {
        // if (choiceList != null) {
        // choiceList.repaint();
        // }
//      }
    }
    else if (mode == PointsetManager.DELETE) {
      delete(session);
    }
    session.updateEnablement();
  }

//  boolean replace() {
//    boolean retval = true;
//    final JFileChooser fileChooser = new JFileChooser(session.cwd);
//    fileChooser.setDialogTitle("Please select data file to open.");
//    fileChooser.setFileFilter(FileFilters.dataSourceFileFilter);
//    fileChooser.setAcceptAllFileFilterUsed(false);
//
//    if (fileChooser.showOpenDialog(session) != JFileChooser.APPROVE_OPTION) {
//      return false;
//    }
//    final File file = fileChooser.getSelectedFile();
//    session.cwd = file;
//    final Pointset pointset = new Pointset(session, null);
//    try {
//      //new XplTextReader(session).readDataFile(pointset, file);
//    }
//    catch (final OutOfMemoryError ex) {
//      session
//          .error(
//              "Out of memory.  Increase the heap size to read larger data sources.",
//              "memory_error");
//      return false;
//      // TODO: what if in applet mode?
//      // try {
//      // session.getAppletContext().showDocument(new URL(getCodeBase()
//      // + "outofmemory.html"));
//      // }
//      // catch (MalformedURLException e) {
//      // e.printStackTrace();
//      // }
//    }
//    catch (final Exception e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
//    delete(session);
//    session.pointsetManager.addPointset(null,pointset);
//    session.pointsetManager.checkForSize();
//    session.plotManager.canvas.setStale(true);
//    session.plotManager.canvas.refresh();
//    session.updateEnablement();
//    retval = true;
//    return retval;
//  }

  Pointset delete(final Session session) {
    final Pointset pointset = session.pointsetManager.getPointsetAt(selectedIndex);
    // if (pointset.modifyDialog != null) {
    // pointset.modifyDialog.dispose();
    // }
    session.pointsetManager.unregisterPointsetAt(selectedIndex);
    // if (selectedIndex >= session.datasetList.size()) {
    // selectedIndex = session.datasetList.size() - 1;
    // }
    // pointset.clearReferences();
    // if (choiceList != null) {
    // choiceList.invalidate();
    // choiceList.repaint();
    // choiceList.validate();
    // }
    // if (session.datasetList.size() <= 0 && chooseDialog != null) {
    // chooseDialog.dispose();
    // }
    session.plotManager.subject.calculateExtents();
    session.plotManager.refreshCanvas();
    return pointset;
  }

  @Override
  protected boolean apply() {
    try {
      executeChoice();
      //session.spreadsheet.disposeTableFrame(); // this is like an update
      session.plotManager.refreshCanvas();
    }
    catch (final Throwable ex) {
      session.errorSupport(" ", ex, "apply_choose_data_error");
    }
    return true; // apply was successful
  }
  private static final long serialVersionUID = 1L;
}
