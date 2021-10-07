package gov.nih.ncgc.openhts.tool1.spreadsheet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.db4o.Db4o;
import com.jidesoft.swing.JidePopupMenu;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.FileFilters;
import gov.nih.ncgc.openhts.tool1.Main;
import gov.nih.ncgc.openhts.tool1.Palette;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.persistence.PersistenceContext;
import gov.nih.ncgc.openhts.tool1.persistence.PersistenceRoot;
import gov.nih.ncgc.openhts.tool1.pointsetManager.AxesColumnHeadsMapping;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetManagerEvent;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetManagerListener;

/**
 * A spreadsheet is a collection of tables. Better called "PointsetTableManager"
 * 
 * @author talafousj
 */
public class Spreadsheet extends JPanel implements PointsetManagerListener {
  JidePopupMenu tabPopupMenu;
  private final TabbedPaneListener tabbedPaneListener;
  private int nLines;
  public JTabbedPane tabbedPane;
  final Session session;
  static JLabel tabPopupLabel;
  public static final int tabbedPaneWidth = 531;
  final JidePopupMenu tablePopup = new JidePopupMenu();
  private Map<Pointset, PointsetTable> pointsetToPointsetTable;
  private PointsetToPointsetTableHolder pointsetToPointsetTableHolder;
  private final List<SpreadsheetListener> listeners = new ArrayList<SpreadsheetListener>();

  // private final Map<Pointset, int[]> selected = new HashMap<Pointset,
  // int[]>();
  public Spreadsheet(final Session session) {
    this.session = session;
    session.spreadsheet = this;
    this.tabbedPaneListener = new TabbedPaneListener(session);
    tablePopup.add(new JMenuItem(
        session.actionManager.spreadsheetLockTableAction));
    tablePopup.add(new JMenuItem(
        session.actionManager.spreadsheetUnlockTableAction));
    tablePopup.add(new JMenuItem(
        session.actionManager.spreadsheetSelectAllRowsAction));
    tablePopup.add(new JMenuItem(
        session.actionManager.spreadsheetDeselectAllRowsAction));
    tablePopup.add(new JMenuItem(
        session.actionManager.spreadsheetToggleSelectionsAction));
    tablePopup.add(new JMenuItem(
        session.actionManager.spreadsheetLockAllTablesAction));
    tablePopup.add(new JMenuItem(
        session.actionManager.spreadsheetUnlockAllTablesAction));
    tablePopup.add(new JMenuItem(
        session.actionManager.spreadsheetSelectAllRowsUnlockedAction));
    tablePopup.add(new JMenuItem(
        session.actionManager.spreadsheetDeselectAllRowsUnlockedAction));
    tablePopup.add(new JMenuItem(
        session.actionManager.spreadsheetToggleUnlockedAction));
    tablePopup.add(new JMenuItem(
        session.actionManager.spreadsheetExportSelectionsAction));
    tablePopup.add(new JMenuItem(
        session.actionManager.spreadsheetHideSelectionsAction));
    tablePopup.add(new JMenuItem(
        session.actionManager.spreadsheetShowSelectionsAction));
    tablePopup.add(new JMenuItem(
        session.actionManager.spreadsheetRevealHiddenAction));
    tablePopup.add(new JMenuItem(
        session.actionManager.spreadsheetMaskHiddenAction));
    if (getPointsetToPointsetTableHolder() == null) {
      pointsetToPointsetTable = new HashMap<Pointset, PointsetTable>();
      storePointsetToPointsetTable();
    }
    else {
      loadPointsetToPointsetTable();
      for (final Map.Entry<Pointset, PointsetTable> entry : pointsetToPointsetTable
          .entrySet()) {
        // Pointset session was already set by its manager
        entry.getValue().setSessionPointset(session, entry.getKey());
        entry.getValue().initializeTransient();
      }
    }
    build();
  }

  void build() {
    setLayout(new BorderLayout(0, 0));
    tabPopupMenu = new JidePopupMenu();
    tabPopupLabel = new JLabel("untitled");
    tabPopupMenu.add(tabPopupLabel);
    tabbedPane = createTabbedPane();
    add(tabbedPane, BorderLayout.CENTER);
    add(new TableToolBar(session, tabbedPane), BorderLayout.EAST);
    updateEnablement();
  }

  public JTabbedPane createTabbedPane() {
    JTabbedPane tp = new JTabbedPane();
    tp.setTabPlacement(SwingConstants.TOP);
    tp.addMouseListener(tabbedPaneListener);
    tp.setUI(new CustomTabbedPaneUI(session, tp));
    tp.setToolTipText("pointset TODO");
    addPointsetTables();
    tp.addChangeListener(new ChangeListener() {
      // @Implements ChangeListener
      @Override
			public void stateChanged(ChangeEvent evt) {
        PointsetTable pointsetTable = getPointsetTableInSelectedTab();
        // firePointsetSelectionChanged(pointsetTable.getPointset()/*,pointsetTable.getTable().getSelectedRows()*/);
        fireSpreadsheetChanged(new SpreadsheetEvent(this, pointsetTable,
            SpreadsheetEvent.Kind.MEMBERS_SELECTION));
        updateEnablement();
      }
    });
    return tp;
  }

  private void addPointsetTables() {
    for (Pointset pointset : session.pointsetManager.getPointsets()) {
      final PointsetTable pointsetTable = pointsetToPointsetTable.get(pointset);
      if (pointsetTable == null) {
        throw new RuntimeException("no table for pointset = " + pointset);
      }
      addPointsetTable(pointsetTable);
      setSelectedPointsetTableTab(pointsetTable);
    }
  }

  private void addPointsetTable(PointsetTable pointsetTable) {
    final JPanel panel = new JPanel(new BorderLayout(0, 0));
    final JScrollPane scrollPane = pointsetTable.getScrollPane();
    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(pointsetTable.selectionBar, BorderLayout.EAST);
    panel.setBorder(BorderFactory.createLineBorder(pointsetTable.getPointset()
        .getColor(), 5));
    scrollPane.getViewport().addMouseListener(
        pointsetTable.pointsetTableHeaderMouseListener);
    // scrollPane.getViewport().addKeyListener(tableListener); // for the esc
    if (pointsetTable.isUnlocked()) {
      tabbedPane.addTab(pointsetTable.getPointset().getName(), AppLafManager
          .getIcon(AppLafManager.IconKind.ICON_UNLOCKED), panel, pointsetTable
          .getPointset().getName());
    }
    else {
      tabbedPane.addTab(pointsetTable.getPointset().getName(), AppLafManager
          .getIcon(AppLafManager.IconKind.ICON_LOCKED), panel, pointsetTable
          .getPointset().getName());
    }
    int i = tabbedPane.getTabCount() - 1;
    tabbedPane.setToolTipTextAt(i, "right click for info about \""
        + pointsetTable.getPointset().getName() + "\"");
    Color color = pointsetTable.getPointset().getColor();
    if (color != null) {
      tabbedPane.setBackgroundAt(i, color);
      final Palette.Choice choice = Palette.getClosestChoice(color);
      tabbedPane.setForegroundAt(i, choice.getTextColor());
    }
  }

  private void removePointsetTable(PointsetTable pointsetTable) {
    tabbedPane.removeTabAt(getTabIndexForPointsetTable(pointsetTable));
    revalidate();
    repaint();
  }

  private void updatePointsetTable(PointsetTable pointsetTable) {
    int i = this.getTabIndexForPointsetTable(pointsetTable);
    JPanel panel = (JPanel) tabbedPane.getComponentAt(i);
    // JScrollPane scrollPane = (JScrollPane)panel.getComponent(0);
    // JTable table = (JTable) scrollPane.getViewport().getView();
    panel.setBorder(BorderFactory.createLineBorder(pointsetTable.getPointset()
        .getColor(), 4));
    tabbedPane.setToolTipTextAt(i, "right click for info about \""
        + pointsetTable.getPointset().getName() + "\"");
    tabbedPane.setTitleAt(i, pointsetTable.getPointset().getName());
    if (pointsetTable.isUnlocked()) {
      tabbedPane.setIconAt(i, AppLafManager
          .getIcon(AppLafManager.IconKind.ICON_UNLOCKED));
    }
    else {
      tabbedPane.setIconAt(i, AppLafManager
          .getIcon(AppLafManager.IconKind.ICON_UNLOCKED));
    }
    Color color = pointsetTable.getPointset().getColor();
    if (color != null) {
      tabbedPane.setBackgroundAt(i, color);
      final Palette.Choice choice = Palette.getClosestChoice(color);
      tabbedPane.setForegroundAt(i, choice.getTextColor());
    }
    tabbedPane.validate();
  }

  public void exportSelections() throws Throwable {
    // String ss[] = {FileChooser.VIRTUAL, "" }; // TODO
    final JFileChooser fileChooser = new JFileChooser(session.cwd);
    fileChooser.setDialogTitle("Please specify CSV file.");
    // session.getFileChooser().setLabelTitle("CSV file to write:");
    fileChooser.setFileFilter(FileFilters.txtFileFilter);
    if (fileChooser.showSaveDialog(session.frame) != JFileChooser.APPROVE_OPTION) {
      return;
    }
    final File txtFile = fileChooser.getSelectedFile();
    session.cwd = txtFile.getParentFile();
    if (!session.checkForOverwrite(txtFile)) {
      return;
    }
    SwingWorker<String, Object> worker = new SwingWorker<String, Object>() {
      @Override
      public String doInBackground() {
        try {
          session.setWaitCursor(true);
          StringBuffer sb = new StringBuffer("");
          nLines = 0;
          for (int i = 0; i < session.pointsetManager.pointsetCount(); i++) {
            Pointset pointset = session.pointsetManager.getPointsetAt(i);
            if (pointsetToPointsetTable.get(pointset).getTable()
                .getSelectedRowCount() > 0) {
              sb.append(getTxtString(pointset));
            }
          }
          if (nLines == 0) {
            session.message("No rows were selected, wrote empty file.");
          }
          session.saveTextFile(txtFile, sb.toString());
        }
        catch (Throwable ex) {
          session.errorSupport("While exporting data: ", ex, "todo");
        }
        finally {
          session.setWaitCursor(false);
        }
        return "success";
      }

      @Override
      protected void done() {
        // if (get() != "success") {
        // }
      }
    };
    worker.execute();
    // a
    // final SwingWorker_DELETEME worker = new SwingWorker_DELETEME() {
    // @Override
    // public Object construct() {
    // return null;
    // }
    // };
    // worker.start();
  }

  String getTxtString(final Pointset pointset) {
    final PointsetTable table = session.spreadsheet.pointsetToPointsetTable
        .get(pointset);
    final StringBuffer sb = new StringBuffer(pointset.getName() + "\n");
    for (int col = 0; col < table.getTable().getColumnCount() - 1; col++) {
      sb.append(quote(table.getTable().getColumnName(col)) + ",");
    }
    sb.append(quote(table.getTable().getColumnName(
        table.getTable().getColumnCount() - 1))
        + "\n");
    for (int row = 0; row < table.getTable().getRowCount(); row++) {
      if (table.getTable().isRowSelected(row)) {
        nLines++;
        for (int col = 0; col < table.getTable().getColumnCount() - 1; col++) {
          sb.append(quote(table.getTable().getValueAt(row, col).toString())
              + ",");
          // TODO: print out actual value, not just what you see on the screen.
          // TODO: if structure, print out Smiles, not structure
        }
        sb.append(quote(table.getTable().getValueAt(row,
            table.getTable().getColumnCount() - 1).toString())
            + "\n");
      }
    }
    return sb.toString();
  }

  private String quote(final String str) {
    return "\"" + str + "\"";
  }

  public void unlockThisTable() {
    // TODO: rename this selectedFunction to unlockSelectedTable()
    final PointsetTable pointsetTable = getPointsetTableInSelectedTab();
    pointsetTable.setUnlocked(true);
    tabbedPane.setIconAt(getTabIndexForPointsetTable(pointsetTable),
        AppLafManager.getIcon(AppLafManager.IconKind.ICON_UNLOCKED));
    updateEnablement();
  }

  public void lockThisTable() {
    final PointsetTable pointsetTable = getPointsetTableInSelectedTab();
    pointsetTable.setUnlocked(false);
    tabbedPane.setIconAt(getTabIndexForPointsetTable(pointsetTable),
        AppLafManager.getIcon(AppLafManager.IconKind.ICON_LOCKED));
    updateEnablement();
  }

  public void selectAllRowsThisTable() {
    final PointsetTable pointsetTable = getPointsetTableInSelectedTab();
    if (pointsetTable.isUnlocked()) {
      pointsetTable.getTable().addRowSelectionInterval(0,
          pointsetTable.getTable().getRowCount() - 1);
    }
    else {
      Toolkit.getDefaultToolkit().beep();
    }
    updateEnablement();
  }

  public void doDeselectAllRowsThisTable() {
    final PointsetTable pointsetTable = getPointsetTableInSelectedTab();
    if (pointsetTable.isUnlocked()) {
      pointsetTable.getTable().clearSelection();
    }
    else {
      Toolkit.getDefaultToolkit().beep();
    }
    updateEnablement();
  }

  public void toggleThisTable() {
    final PointsetTable pointsetTable = getPointsetTableInSelectedTab();
    if (pointsetTable.isUnlocked()) {
      toggleSelectionState(pointsetTable.getPointset());
    }
    else {
      Toolkit.getDefaultToolkit().beep();
    }
    updateEnablement();
  }

  public void lockAllTables() {
    for (PointsetTable pointsetTable : pointsetToPointsetTable.values()) {
      pointsetTable.setUnlocked(false);
      tabbedPane.setIconAt(getTabIndexForPointsetTable(pointsetTable),
          AppLafManager.getIcon(AppLafManager.IconKind.ICON_LOCKED));
    }
    updateEnablement();
  }

  public void unlockAllTables() {
    for (PointsetTable pointsetTable : pointsetToPointsetTable.values()) {
      pointsetTable.setUnlocked(true);
      tabbedPane.setIconAt(getTabIndexForPointsetTable(pointsetTable),
          AppLafManager.getIcon(AppLafManager.IconKind.ICON_UNLOCKED));
    }
    updateEnablement();
  }

  public void selectAllRowsUnlocked() {
    boolean found = false;
    for (PointsetTable pointsetTable : pointsetToPointsetTable.values()) {
      if (pointsetTable.isUnlocked()) {
        pointsetTable.getTable().addRowSelectionInterval(0,
            pointsetTable.getTable().getRowCount() - 1);
        found = true;
      }
    }
    if (!found) {
      Toolkit.getDefaultToolkit().beep();
    }
    updateEnablement();
  }

  public void deselectAllRowsUnlocked() {
    boolean found = false;
    for (PointsetTable pointsetTable : pointsetToPointsetTable.values()) {
      if (pointsetTable.isUnlocked()) {
        pointsetTable.getTable().clearSelection();
        found = true;
      }
    }
    if (!found) {
      Toolkit.getDefaultToolkit().beep();
    }
    updateEnablement();
  }

  public void toggleUnlocked() {
    boolean found = false;
    for (PointsetTable pointsetTable : pointsetToPointsetTable.values()) {
      if (pointsetTable.isUnlocked()) {
        toggleSelectionState(pointsetTable.getPointset());
        found = true;
      }
    }
    if (!found) {
      Toolkit.getDefaultToolkit().beep();
    }
    updateEnablement();
  }

  void toggleSelectionState(final Pointset pointset) {
    for (PointsetTable pointsetTable : pointsetToPointsetTable.values()) {
      JTable table = pointsetTable.getTable();
      table.getSelectionModel().setValueIsAdjusting(true);
      final int nRows = table.getRowCount();
      for (int j = 0; j < nRows; j++) {
        if (table.isRowSelected(j)) {
          table.removeRowSelectionInterval(j, j);
        }
        else {
          table.addRowSelectionInterval(j, j);
        }
      }
      table.getSelectionModel().setValueIsAdjusting(false);
    }
  }

  public void hideSelected() {
    int total = 0;
    for (PointsetTable pointsetTable : pointsetToPointsetTable.values()) {
      JTable table = pointsetTable.getTable();
      final int[] rows = table.getSelectedRows();
      if (rows.length > 0) {
        Pointset pointset = pointsetTable.getPointset();
        for (int i = 0; i < rows.length; i++) {
          int j = pointsetTable.tableToUser[rows[i]];
          pointset.hid[j] |= Pointset.HID_USER;
          pointset.clearSelectionAt(j);
          pointset.clearSelectedPoint();
        }
        total += rows.length;
        session.pointsetManager.hiddenChanged(pointset);
        // pointsetTable.getPointset().hiddenChanged();
      }
    }
    if (total <= 0) {
      Toolkit.getDefaultToolkit().beep();
      return;
    }
    updateEnablement();
  }

  public void showSelected() {
    int total = 0;
    for (PointsetTable pointsetTable : pointsetToPointsetTable.values()) {
      JTable table = pointsetTable.getTable();
      final int[] rows = table.getSelectedRows();
      if (rows.length > 0) {
        Pointset pointset = pointsetTable.getPointset();
        for (int row = 0; row < rows.length; row++) {
          int i = pointsetTable.tableToUser[row];
          pointset.hid[i] = 0x00;
          pointset.clearSelectionAt(i);
          pointset.clearSelectedPoint();
        }
        total += rows.length;
        session.pointsetManager.hiddenChanged(pointset);
      }
    }
    if (total <= 0) {
      Toolkit.getDefaultToolkit().beep();
      return;
    }
    updateEnablement();
  }

  public void setDisplayHidden(boolean displayHidden) {
    for (PointsetTable pointsetTable : pointsetToPointsetTable.values()) {
      pointsetTable.setDisplayHidden(displayHidden);
      pointsetTable.updateModelChoice();
      pointsetTable.invert();
    }
    updateEnablement();
  }

  /**
   * static void writeReport(final Session session) { String ss[] =
   * {FileChooser.VIRTUAL, "" }; try {
   * session.getFileChooser().setDialogTitle("Please specify report file");
   * session.getFileChooser().setLabelTitle("Report file to write:");
   * session.getFileChooser().setHelpID("TODO");
   * session.getFileChooser().setFileFilter(new FileNameFilter("txt", "Report
   * file (*.txt)")); if(session.getFileChooser().showSaveDialog(ss) !=
   * FileChooser.APPROVE_OPTION) { return; } ss =
   * session.getFileChooser().getSelectedFileName(); if (!ss[1].equals("") &&
   * (ss[1].indexOf(".txt") == -1)) { ss[1] += ".txt"; } if
   * (!session.checkForOverwrite(ss)) { return; } StringBuffer sb = new
   * StringBuffer("Name Coordinates"); nLines = 0; for (int i = 0; i < v.size();
   * i++) { Pointset data = (Pointset) v.get(i);
   * sb.append(data.getReportString()); } if (nLines == 0) { session.message("No
   * rows were selected, wrote empty file."); } session.saveTextFile(ss,
   * sb.toString()); } catch (Throwable ex) { session.error("While writing
   * report:",ex,"TODO"); } }
   */
  public void updateEnablement() {
    // tableHeaderPopup.setVisible(false);
    tabPopupMenu.setVisible(false);
    if (tabbedPane.getTabCount() <= 0) {
      session.actionManager.spreadsheetLockTableAction.setEnabled(false);
      session.actionManager.spreadsheetUnlockTableAction.setEnabled(false);
      session.actionManager.spreadsheetSelectAllRowsAction.setEnabled(false);
      session.actionManager.spreadsheetDeselectAllRowsAction.setEnabled(false);
      session.actionManager.spreadsheetToggleSelectionsAction.setEnabled(false);
      //
      session.actionManager.spreadsheetHideSelectionsAction.setEnabled(false);
      session.actionManager.spreadsheetShowSelectionsAction.setEnabled(false);
      session.actionManager.spreadsheetExportSelectionsAction.setEnabled(false);
      //
      session.actionManager.spreadsheetLockAllTablesAction.setEnabled(false);
      session.actionManager.spreadsheetSelectAllRowsAction.setEnabled(false);
      session.actionManager.spreadsheetDeselectAllRowsAction.setEnabled(false);
      session.actionManager.spreadsheetToggleUnlockedAction.setEnabled(false);
      session.actionManager.spreadsheetUnlockAllTablesAction.setEnabled(true);
      //
      session.actionManager.spreadsheetRevealHiddenAction.setEnabled(false);
      session.actionManager.spreadsheetMaskHiddenAction.setEnabled(true);
      return;
    }
    // First set enablement for *selected* table
    PointsetTable selectedPointsetTable = getPointsetTableInSelectedTab();
    if (selectedPointsetTable == null) {
      session.actionManager.spreadsheetLockTableAction.setEnabled(false);
      session.actionManager.spreadsheetUnlockTableAction.setEnabled(false);
      session.actionManager.spreadsheetSelectAllRowsAction.setEnabled(false);
      session.actionManager.spreadsheetDeselectAllRowsAction.setEnabled(false);
      session.actionManager.spreadsheetToggleSelectionsAction.setEnabled(false);
    }
    else if (selectedPointsetTable.isUnlocked()) {
      session.actionManager.spreadsheetLockTableAction.setEnabled(true);
      session.actionManager.spreadsheetUnlockTableAction.setEnabled(false);
      session.actionManager.spreadsheetSelectAllRowsAction.setEnabled(true);
      session.actionManager.spreadsheetDeselectAllRowsAction.setEnabled(true);
      session.actionManager.spreadsheetToggleSelectionsAction.setEnabled(true);
    }
    else {
      session.actionManager.spreadsheetLockTableAction.setEnabled(false);
      session.actionManager.spreadsheetUnlockTableAction.setEnabled(true);
      session.actionManager.spreadsheetSelectAllRowsAction.setEnabled(false);
      session.actionManager.spreadsheetDeselectAllRowsAction.setEnabled(false);
      session.actionManager.spreadsheetToggleSelectionsAction.setEnabled(false);
    }
    //
    if (selectedPointsetTable == null) {
      session.actionManager.spreadsheetRevealHiddenAction.setEnabled(false);
      session.actionManager.spreadsheetMaskHiddenAction.setEnabled(false);
    }
    else if (selectedPointsetTable.isDisplayHidden()) {
      session.actionManager.spreadsheetRevealHiddenAction.setEnabled(false);
      session.actionManager.spreadsheetMaskHiddenAction.setEnabled(true);
    }
    else {
      session.actionManager.spreadsheetRevealHiddenAction.setEnabled(true);
      session.actionManager.spreadsheetMaskHiddenAction.setEnabled(false);
    }
    // Second, set enablement for buttons about the table *collection*
    int num = 0;
    boolean atLeastOneUnlocked = false, atLeastOneLocked = false;
    for (PointsetTable pointsetTable : pointsetToPointsetTable.values()) {
      if (pointsetTable == null) {
        // nop
      }
      else if (pointsetTable.isUnlocked()) {
        atLeastOneUnlocked = true;
        num += pointsetTable.getTable().getSelectedRowCount();
      }
      else {
        atLeastOneLocked = true;
        num += pointsetTable.getTable().getSelectedRowCount();
      }
    }
    if (num <= 0) {
      session.actionManager.spreadsheetHideSelectionsAction.setEnabled(false);
      session.actionManager.spreadsheetShowSelectionsAction.setEnabled(false);
      session.actionManager.spreadsheetExportSelectionsAction.setEnabled(false);
    }
    else {
      session.actionManager.spreadsheetHideSelectionsAction.setEnabled(true);
      session.actionManager.spreadsheetShowSelectionsAction.setEnabled(true);
      session.actionManager.spreadsheetExportSelectionsAction.setEnabled(true);
    }
    if (atLeastOneUnlocked) {
      session.actionManager.spreadsheetLockAllTablesAction.setEnabled(true);
      session.actionManager.spreadsheetSelectAllRowsAction.setEnabled(true);
      session.actionManager.spreadsheetDeselectAllRowsAction.setEnabled(true);
      session.actionManager.spreadsheetToggleUnlockedAction.setEnabled(true);
    }
    else { // all locked
      session.actionManager.spreadsheetLockAllTablesAction.setEnabled(false);
      session.actionManager.spreadsheetSelectAllRowsUnlockedAction
          .setEnabled(false);
      session.actionManager.spreadsheetDeselectAllRowsUnlockedAction
          .setEnabled(false);
      session.actionManager.spreadsheetToggleUnlockedAction.setEnabled(false);
    }
    if (atLeastOneLocked) {
      session.actionManager.spreadsheetUnlockAllTablesAction.setEnabled(true);
    }
    else { // all unlocked
      session.actionManager.spreadsheetUnlockAllTablesAction.setEnabled(false);
    }
  }

  public void updateActions() {
    tablePopup.setVisible(false);
    // tableHeaderPopup.setVisible(false);
    if (getPointsetTableInSelectedTab().isUnlocked()) {
      session.actionManager.spreadsheetLockTableAction.setEnabled(true);
      session.actionManager.spreadsheetUnlockTableAction.setEnabled(false);
      session.actionManager.spreadsheetSelectAllRowsAction.setEnabled(true);
      session.actionManager.spreadsheetDeselectAllRowsAction.setEnabled(true);
      session.actionManager.spreadsheetToggleSelectionsAction.setEnabled(true);
    }
    else {
      session.actionManager.spreadsheetLockTableAction.setEnabled(false);
      session.actionManager.spreadsheetUnlockTableAction.setEnabled(true);
      session.actionManager.spreadsheetSelectAllRowsAction.setEnabled(false);
      session.actionManager.spreadsheetDeselectAllRowsAction.setEnabled(false);
      session.actionManager.spreadsheetToggleSelectionsAction.setEnabled(false);
    }
    if (getPointsetTableInSelectedTab().isDisplayHidden()) {
      session.actionManager.spreadsheetRevealHiddenAction.setEnabled(false);
      session.actionManager.spreadsheetMaskHiddenAction.setEnabled(true);
    }
    else {
      session.actionManager.spreadsheetRevealHiddenAction.setEnabled(true);
      session.actionManager.spreadsheetMaskHiddenAction.setEnabled(false);
    }
    int num = 0;
    boolean atLeastOneUnlocked = false, atLeastOneLocked = false;
    for (int i = 0; i < session.pointsetManager.pointsetCount(); i++) {
      final Pointset pointset2 = session.pointsetManager.getPointsetAt(i);
      num += session.spreadsheet.pointsetToPointsetTable.get(pointset2)
          .getTable().getSelectedRowCount();
      if (getPointsetTableInSelectedTab().isUnlocked()) {
        atLeastOneUnlocked = true;
      }
      else {
        atLeastOneLocked = true;
      }
    }
    if (num <= 0) {
      session.actionManager.spreadsheetHideSelectionsAction.setEnabled(false);
      session.actionManager.spreadsheetShowSelectionsAction.setEnabled(false);
      session.actionManager.spreadsheetExportSelectionsAction.setEnabled(false);
    }
    else {
      session.actionManager.spreadsheetHideSelectionsAction.setEnabled(true);
      session.actionManager.spreadsheetShowSelectionsAction.setEnabled(true);
      session.actionManager.spreadsheetExportSelectionsAction.setEnabled(true);
    }
    if (atLeastOneUnlocked) {
      session.actionManager.spreadsheetLockAllTablesAction.setEnabled(true);
      session.actionManager.spreadsheetSelectAllRowsAction.setEnabled(true);
      session.actionManager.spreadsheetDeselectAllRowsUnlockedAction
          .setEnabled(true);
      session.actionManager.spreadsheetToggleUnlockedAction.setEnabled(true);
    }
    else { // all locked
      session.actionManager.spreadsheetLockAllTablesAction.setEnabled(false);
      session.actionManager.spreadsheetSelectAllRowsUnlockedAction
          .setEnabled(false);
      session.actionManager.spreadsheetDeselectAllRowsUnlockedAction
          .setEnabled(false);
      session.actionManager.spreadsheetToggleUnlockedAction.setEnabled(false);
    }
    if (atLeastOneLocked) {
      session.actionManager.spreadsheetUnlockAllTablesAction.setEnabled(true);
    }
    else { // all unlocked
      session.actionManager.spreadsheetUnlockAllTablesAction.setEnabled(false);
    }
  }

  public void loadPointsetToPointsetTable() {
    final List<PointsetToPointsetTableHolder> lrh = theRoot
        .getPersistenceContext().getExtent(PointsetToPointsetTableHolder.class);
    if (lrh.size() == 0) {
      throw new RuntimeException("absent pointsetToPointsetTables");
    }
    if (lrh.size() > 1) {
      throw new RuntimeException("multiple pointsetToPointsetTables");
    }
    pointsetToPointsetTableHolder = lrh.get(0);
    pointsetToPointsetTable = pointsetToPointsetTableHolder
        .getPointsetToPointsetTable();
    System.out.println("SPREADSHEET.load pointsetToPointsetTable = "
        + pointsetToPointsetTable);
  }

  public void storePointsetToPointsetTable() {
    if (!Main.isPersisting) {
      return;
    }
    if (pointsetToPointsetTable == null) {
      throw new RuntimeException("pointsetToPointsetTable may not be null");
    }
    System.out.println("storing pointsetToPointsetTable="
        + pointsetToPointsetTable);
    if (pointsetToPointsetTableHolder == null) {
      pointsetToPointsetTableHolder = new PointsetToPointsetTableHolder(
          pointsetToPointsetTable);
    }
    else {
      pointsetToPointsetTableHolder.changeLock.lock();
      pointsetToPointsetTableHolder
          .setPointsetToPointsetTable(pointsetToPointsetTable);
      pointsetToPointsetTableHolder.changeLock.unlock();
    }
    theRoot.getPersistenceContext().set(pointsetToPointsetTableHolder);
  }

  public PointsetToPointsetTableHolder getPointsetToPointsetTableHolder() {
    if (!Main.isPersisting) {
      return null;
    }
    if (pointsetToPointsetTableHolder != null) {
      return pointsetToPointsetTableHolder;
    }
    PointsetToPointsetTableHolder rh = null;
    final List<PointsetToPointsetTableHolder> lrh = theRoot
        .getPersistenceContext().getExtent(PointsetToPointsetTableHolder.class);
    if (lrh.size() == 0) {
      return null;
    }
    if (lrh.size() > 1) {
      throw new RuntimeException("multiple pointsetToPointsetTableHolders");
    }
    rh = lrh.get(0);
    return rh;
  }

  public void clearPointsetToPointsetTable() {
    if (pointsetToPointsetTableHolder != null) {
      theRoot.getPersistenceContext().delete(pointsetToPointsetTableHolder);
    }
    else {
      throw new IllegalStateException("no pointsetToPointsetTableHolder");
    }
  }

  public static Root theRoot;

  /**
   * Purpose is to ...
   * 
   * @author talafousj
   */
  public static final class Root implements PersistenceRoot {
    private gov.nih.ncgc.openhts.tool1.persistence.PersistenceContext context;
    private NextIdPersistent nextIdPersistent;

    public Root() {
      if (theRoot == null) {
        theRoot = this;
      }
      else {
        throw new IllegalStateException("already initialized");
      }
    }

    synchronized public long getNextId() {
      ++nextIdPersistent.nextId;
      context.set(theRoot.nextIdPersistent);
      return nextIdPersistent.nextId;
    }

    @Override
		public void setPersistenceContext(final PersistenceContext context) {
      this.context = context;
      final List<NextIdPersistent> l = context
          .getExtent(NextIdPersistent.class);
      if (l.size() > 0) {
        nextIdPersistent = l.get(0);
      }
      else {
        nextIdPersistent = new NextIdPersistent();
      }
    }

    @Override
		public PersistenceContext getPersistenceContext() {
      return context;
    }
  }

  static private class NextIdPersistent {
    private long nextId = 0L;
  }

  public static void configureDb4o() {
    //
    Db4o.configure().objectClass(PointsetTable.class).cascadeOnActivate(true);
    Db4o.configure().objectClass(PointsetTable.class).cascadeOnUpdate(true);
    Db4o.configure().objectClass(PointsetTable.class).cascadeOnDelete(true);
    Db4o.configure().objectClass(PointsetTable.class).updateDepth(
        Integer.MAX_VALUE);
    //
    Db4o.configure().objectClass(PointsetToPointsetTableHolder.class)
        .cascadeOnActivate(true);
    Db4o.configure().objectClass(PointsetToPointsetTableHolder.class)
        .cascadeOnUpdate(true);
    Db4o.configure().objectClass(PointsetToPointsetTableHolder.class)
        .cascadeOnDelete(true);
    Db4o.configure().objectClass(PointsetToPointsetTableHolder.class)
        .updateDepth(Integer.MAX_VALUE);
    // Pointset is handled by its manager
  }

  public void addSpreadsheetListener(SpreadsheetListener listener) {
    listeners.add(listener);
  }

  public void removeSpreadsheetListener(SpreadsheetListener listener) {
    listeners.remove(listener);
  }

  // @Implements SpreadsheetListener
  void fireSpreadsheetChanged(SpreadsheetEvent ev) {
    System.out.println("pointset selection changed");
    for (SpreadsheetListener listener : listeners) {
      listener.spreadsheetChanged(ev);
    }
  }

  // // @Implements SpreadsheetListener
  // void firePointSelectionChanged(Pointset pointset, int[] selected) {
  // System.out.println("pointset selection changed");
  // for (SpreadsheetListener listener : listeners) {
  // listener.pointSelectionChanged(pointset, selected);
  // }
  // }
  //
  // // @Implements SpreadsheetListener
  // void firePointsetSelectionChanged(Pointset pointset) {
  // for (SpreadsheetListener listener : listeners) {
  // listener.pointsetSelectionChanged(pointset);
  // }
  // }
  //
  // // @Implements SpreadsheetListener
  // public void fireLeadSelectionChanged(Pointset pointset, int pointIndex) {
  // for (SpreadsheetListener listener : listeners) {
  // listener.leadSelectionChanged(pointset, pointIndex);
  // }
  // }
  //
  public void setSelectedPointsetTableTab(PointsetTable pointsetTable) {
    if (pointsetTable != getPointsetTableInSelectedTab()) {
      tabbedPane.setSelectedIndex(getTabIndexForPointsetTable(pointsetTable));
      updateEnablement();
      // firePointsetSelectionChanged(pointsetTable.getPointset());
      fireSpreadsheetChanged(new SpreadsheetEvent(this, pointsetTable,
          SpreadsheetEvent.Kind.MEMBERS_SELECTION));
    }
  }

  public void setSelectedPointsetTab(Pointset pointset) {
    PointsetTable pointsetTable = pointsetToPointsetTable.get(pointset);
    if (pointsetTable != null) { // could be initializing
      setSelectedPointsetTableTab(pointsetTable);
    }
  }

  private int getTabIndexForPointsetTable(PointsetTable pointsetTable) {
    for (int i = 0; i < tabbedPane.getTabCount(); i++) {
      JPanel panel = (JPanel) tabbedPane.getComponentAt(i);
      JScrollPane scrollPane = (JScrollPane) panel.getComponent(0);
      JTable table = (JTable) scrollPane.getViewport().getView();
      if (pointsetTable.getTable() == table) {
        return i;
      }
    }
    return -1;
  }

  public PointsetTable getPointsetTableInSelectedTab() {
    final int i = tabbedPane.getSelectedIndex();
    if (i >= 0) {
      JPanel panel = (JPanel) tabbedPane.getComponentAt(i);
      JScrollPane scrollPane = (JScrollPane) panel.getComponent(0);
      JTable table = (JTable) scrollPane.getViewport().getView();
      for (PointsetTable pointsetTable : pointsetToPointsetTable.values()) {
        if (table == pointsetTable.getTable()) {
          return pointsetTable;
        }
      }
    }
    return null;
  }

  public Pointset getPointsetInSelectedTab() {
    PointsetTable pointsetTable = getPointsetTableInSelectedTab();
    if (pointsetTable == null) {
      return null;
    }
    return pointsetTable.getPointset();
  }

  public PointsetTable getPointsetTable(final Pointset pointset) {
    return pointsetToPointsetTable.get(pointset);
  }

  public void scrollToLastSelection(Pointset pointset) {
    session.spreadsheet.getPointsetTable(pointset).scrollToLastSelection();
  }

  public Map<Pointset, int[]> getSelectedPoints() {
    Map<Pointset, int[]> selected = new HashMap<Pointset, int[]>();
    for (PointsetTable pointsetTable : pointsetToPointsetTable.values()) {
      selected.put(pointsetTable.getPointset(), pointsetTable.getTable()
          .getSelectedRows());
    }
    return selected;
  }

  // // @Implements PointsetManagerListener
  // public void basisSelectionChanged(PointsetManager manager,
  // Basis[] selectedBases) {
  // // nop
  // }
  //
  // // @Implements PointsetManagerListener
  // public void pointsetAdded(final PointsetManager manager,
  // final Pointset pointset) {
  // final PointsetTable pointsetTable = new PointsetTable(session, pointset);
  // pointsetTable.initialize();
  // pointsetToPointsetTable.put(pointset, pointsetTable);
  // storePointsetToPointsetTable();
  // addPointsetTable(pointsetTable);
  // updateEnablement();
  // }
  // // @Implements PointsetManagerListener
  // public void pointsetChanged(final DirMonitorEvent ev) {
  // PointsetTable pointsetTable = pointsetToPointsetTable.get(ev.member);
  // switch(ev.getEventCode()) {
  // case DirMonitorEvent.RELOAD:
  // pointsetTable.initializeDiMap(((Pointset)ev.member).getPointCount());
  // pointsetTable.updateModelChoice();
  // if (tabbedPane != null) {
  // updatePointsetTable(pointsetTable);
  // }
  // pointsetTable.getModel().fireTableDataChanged();
  // break;
  // case DirMonitorEvent.POINT_VIZ:
  // Pointset pointset = pointsetTable.getPointset();
  // fireLeadSelectionChanged(pointset, pointsetTable.getLeadSelectionPoint());
  // pointsetTable.getTable().repaint();
  // break;
  // }
  // // TODO: react to a change in adapting,etc.
  // updateEnablement();
  // }
  // // @Implements PointsetManagerListener
  // public void pointsetRemoved(final PointsetManager manager,
  // final Pointset pointset) {
  // PointsetTable pointsetTable = pointsetToPointsetTable.remove(pointset);
  // removePointsetTable(pointsetTable); // TODO: should just remove the
  // specific
  // // tab
  // updateEnablement();
  // storePointsetToPointsetTable();
  // }
  // public void pointsetSelectionChanged(PointsetManager manager,
  // Pointset[] selectedPointsets) {
  // if (selectedPointsets.length > 0) {
  // setSelectedPointsetTab(selectedPointsets[0]);
  // updateEnablement();
  // }
  // }
  // @Implements PoinsetManagerListener
  @Override
	public void pointsetManagerChanged(PointsetManagerEvent ev) {
    if (!(ev.member instanceof Pointset) && !(ev.member instanceof Pointset[])) {
      return;
    }
    // Pointset pointset = (Pointset)ev.member;
    PointsetTable pointsetTable;
    switch (ev.kind) {
    case MEMBER_LOADED:
      if (ev.member instanceof Pointset){
      pointsetTable = pointsetToPointsetTable.get(ev.member);
      pointsetTable
          .initializeDiMap(pointsetTable.getPointset().getPointCount());
      updatePointsetTable(pointsetTable);
      pointsetTable.getModel().fireTableDataChanged();
      fireSpreadsheetChanged(new SpreadsheetEvent(this, pointsetTable,
          SpreadsheetEvent.Kind.MEMBER_LOADED));
      }
      break;
    case MEMBER_ADDED:
      if (ev.member instanceof Pointset){
      pointsetTable = new PointsetTable(session, (Pointset) ev.member);
      pointsetTable.initialize();
      pointsetToPointsetTable.put((Pointset) ev.member, pointsetTable);
      storePointsetToPointsetTable();
      addPointsetTable(pointsetTable);
      setSelectedPointsetTableTab(pointsetTable);
      updateEnablement();
      }
      break;
    case MEMBER_CHANGED:
      break;
    case POINTSET_REMOVED:
      pointsetTable = pointsetToPointsetTable.remove(ev.member);
      removePointsetTable(pointsetTable);
      // TODO: should just remove the specific tab
      updateEnablement();
      storePointsetToPointsetTable();
      break;
    case MEMBERS_SELECTION:
      Pointset[] selectedPointsets = (Pointset[]) ev.member;
      if (selectedPointsets.length > 0) {
        setSelectedPointsetTab(selectedPointsets[0]);
        updateEnablement();
      }
      break;
    case MANAGER_CHANGED:
      pointsetTable = pointsetToPointsetTable.get(ev.member);
      pointsetTable.initializeDiMap(((Pointset) ev.member).getPointCount());
      pointsetTable.updateModelChoice();
      if (tabbedPane != null) {
        updatePointsetTable(pointsetTable);
      }
      pointsetTable.getModel().fireTableDataChanged();
      break;
    case MEMBER_VISABILITY:
      break;
    case POINT_VIZ:
      // Pointset pointset = pointsetTable.getPointset();
      pointsetTable = pointsetToPointsetTable.get(ev.member);
      // fireLeadSelectionChanged((Pointset)ev.member,
      // pointsetTable.getLeadSelectionPoint());
      fireSpreadsheetChanged(new SpreadsheetEvent(this, pointsetTable,
          SpreadsheetEvent.Kind.LEAD_SELECTION));
      pointsetTable.getTable().repaint();
      break;
    case AC_MAP:
      AxesColumnHeadsMapping acMap = ((Pointset) ev.member).getCurrentAcMap();
      for (Pointset pointset : session.pointsetManager.getPointsets()) {
        if (pointset.getCurrentAcMap() == acMap) {
          pointsetToPointsetTable.get(pointset).getTable().repaint();
          pointsetToPointsetTable.get(pointset).getTable().getTableHeader()
              .repaint();
        }
      }
      break;
    }
  }

  // @Implements ProgressChangeListener
  @Override
	public void progressChanged(Object object, String string, int min, int value,
      int max) {
    // nop
  }

  private static final long serialVersionUID = 1L;
}
