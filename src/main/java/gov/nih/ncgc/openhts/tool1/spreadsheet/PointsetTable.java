package gov.nih.ncgc.openhts.tool1.spreadsheet;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import chemaxon.marvin.beans.MViewPane;
import chemaxon.struc.Molecule;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.persistence.Persistent;
import gov.nih.ncgc.openhts.tool1.plotManager.Settings;
import gov.nih.ncgc.openhts.tool1.pointsetManager.ColumnHead;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class PointsetTable implements Persistent {
  // This class must not extend JTable since it's not persistable
  private transient JTable table;
  private transient Session session;
  transient JPanel selectionBar;
  private transient PointsetTableModel pointsetTableModel;
  public transient PointsetTableHeaderMouseListener pointsetTableHeaderMouseListener;
  private transient MouseListener[] mouseListeners;
  private transient MouseMotionListener[] mouseMotionListeners;
  private transient final static PointsetTableCellRenderer pointsetTableCellRenderer;
  private transient final static PointsetTableCellEditor pointsetTableCellEditor;
  static {
    pointsetTableCellRenderer = new PointsetTableCellRenderer(true);
    final MViewPane mvp = new MViewPane();
    mvp.setEditable(1);
    pointsetTableCellEditor = new PointsetTableCellEditor(mvp);
  }
  private Pointset pointset; // TODO: this could be transient
  public final static String hiddenString = "hidden";
  public final static String nameString = "name";
  public final static String structureString = "structure";
  private final int nameColWidth = 100, hiddenColWidth = 40, molColWidth = 100;
  static final DecimalFormat df = new DecimalFormat("0.00");
  int tabbedPaneWidth;
  // boolean highlighting = false;
  public boolean unlocked;
  // Reindexing: user order to table order
  public int[] userToTable;
  // Reindexing: table order to user order key that is sorted according to
  // selection and visibility
  public int[] tableToUser;
  // Display hidden numbers in table?
  public boolean displayHidden = true;
  // public int maxSelectedIndex;
  int selectedColumn = -1;
  private int leadSelectionRow = -1;
  private JScrollPane scrollPane;
  PointsetTableHeaderPopup pointsetTableHeaderPopup;

  public PointsetTable(final Session session, final Pointset pointset) {
    this.session = session;
    this.pointset = pointset;
  }

  public void initialize() {
    initializeDiMap(pointset.getPointCount());
    unlocked = true;
    initializeTransient();
  }

  void initializeDiMap(int n) {
    userToTable = new int[n];
    tableToUser = new int[n];
    for (int i = 0; i < n; i++) {
      userToTable[i] = i;
      tableToUser[i] = i;
    }
  }

  // @Implements Persistent
  @Override
	public void initializeTransient() {
    table = createTable();
    Session.addFocusBorder(table, table);
    pointsetTableHeaderMouseListener = new PointsetTableHeaderMouseListener(
        session, this);
    tabbedPaneWidth = Spreadsheet.tabbedPaneWidth;
    mouseListeners = table.getListeners(MouseListener.class);
    mouseMotionListeners = table.getListeners(MouseMotionListener.class);
    if (!isUnlocked()) {
      removeMouseListeners();
    }
    table.addMouseListener(pointsetTableHeaderMouseListener);
    table.getSelectionModel().addListSelectionListener(
        new ListSelectionListener() {
          @Override
					public void valueChanged(final ListSelectionEvent ev) {
            if (ev.getValueIsAdjusting()) {
              return;
            }
//            int[] tableSelectedRows = table.getSelectedRows();
//            int[] selectedPoints = new int[tableSelectedRows.length];
//            for (int i = 0; i < tableSelectedRows.length; i++) {
//              int selectedTableRow = tableSelectedRows[i];
//              selectedPoints[i] = tableToUser[tableSelectedRows[i]];
//            }
            selectionBar.repaint();
            session.spreadsheet.fireSpreadsheetChanged(new SpreadsheetEvent(
                this, PointsetTable.this,
                SpreadsheetEvent.Kind.POINT_SELECTION));
            session.spreadsheet.updateEnablement();
          }
        });
    table.setColumnSelectionAllowed(false);
    table.setRowSelectionAllowed(true);
    table.getTableHeader().setReorderingAllowed(false);
    table.getTableHeader().addMouseListener(pointsetTableHeaderMouseListener);
    // table.getTableHeader().addMouseMotionListener(pointsetTableHeaderMouseListener);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    // table.setBackground(color);
    final ListSelectionModel listSelectionModel = table.getSelectionModel();
    listSelectionModel
        .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    table.setDefaultRenderer(Molecule.class, pointsetTableCellRenderer);
    table.setDefaultEditor(Molecule.class, pointsetTableCellEditor);
    updateModelChoice();
    scrollPane = new JScrollPane(table);
    selectionBar = new JPanel() {
      @Override
      public Dimension getPreferredSize() {
        return (new Dimension(6, table.getHeight()));
      }

      @Override
      public void paint(Graphics g) {
        JScrollBar scrollBar = scrollPane.getHorizontalScrollBar();
        int thumbsize = scrollPane.getVerticalScrollBar().getWidth(); // ASSUME
        // button
        // square
        int horizScrollbarHeight = scrollPane.getHorizontalScrollBar()
            .getHeight();
        if (!scrollPane.getHorizontalScrollBar().isVisible()) {
          horizScrollbarHeight = 0;
        }
        int[] selections = table.getSelectedRows();
        int y1, y2, rowCount = table.getRowCount();
        int skipY = table.getTableHeader().getHeight()
        // +table.getBorder().getBorderInsets(table).top
            + thumbsize;
        int maxY = getHeight() - skipY - thumbsize - horizScrollbarHeight;
        g.setColor(scrollBar.getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(table.getSelectionBackground());
        for (int i = 0; i < selections.length; i++) {
          y1 = skipY + maxY * selections[i] / rowCount;
          // y2=skipY+maxY*(selections[i]+1)/rowCount;
          y2 = maxY / rowCount;
          g.fillRect(0, y1, getWidth() - 1, y2 < 1 ? 1 : y2);
        }
      }

      private static final long serialVersionUID = 1L;
    };
    scrollPane.getVerticalScrollBar().setUnitIncrement(
        Settings.verticalScrollIncrement);
    final ExcelAdapter myAd = new ExcelAdapter(table);
    pointsetTableHeaderPopup = new PointsetTableHeaderPopup(session, this);
  }

  void updateModelChoice() {
    PointsetTableColumnHeaderRenderer chr = null;
    TableColumn col = null;
    if (isDisplayHidden()) {
      if (Pointset.stringMetaImageColumnIndex == -1) {
        pointsetTableModel = new RevealHiddenNoMolStringTableModel(session,
            pointset);
        table.setModel(pointsetTableModel);
        // hid, name
        chr = new PointsetTableColumnHeaderRenderer(this);
        // chr.setToolTip(hiddenString);
        col = table.getColumnModel().getColumn(0);
        col.setHeaderRenderer(chr);
        col.setPreferredWidth(hiddenColWidth);
        col.setResizable(true);
        chr = new PointsetTableColumnHeaderRenderer(this);
        // chr.setToolTip(nameString);
        col = table.getColumnModel().getColumn(1);
        col.setHeaderRenderer(chr);
        col.setPreferredWidth(nameColWidth);
        col.setResizable(true);
        for (int i = 0; i < pointset.getDimensionality(); i++) {
          chr = new PointsetTableColumnHeaderRenderer(this);
          // chr.setToolTip(pointset.getColumnDescription(i));
          col = table.getColumnModel().getColumn(i + 2);
          col.setHeaderRenderer(chr);
          int k = (tabbedPaneWidth - nameColWidth - hiddenColWidth)
              / pointset.getDimensionality();
          k = k < hiddenColWidth ? hiddenColWidth : k;
          col.setPreferredWidth(k);
          col.setResizable(true);
        }
      }
      else {
        pointsetTableModel = new RevealHiddenTableModel(session, pointset);
        table.setModel(pointsetTableModel);
        // hid, name, structure
        chr = new PointsetTableColumnHeaderRenderer(this);
        // chr.setToolTip(hiddenString);
        col = table.getColumnModel().getColumn(0);
        col.setHeaderRenderer(chr);
        col.setPreferredWidth(hiddenColWidth);
        col.setResizable(true);
        chr = new PointsetTableColumnHeaderRenderer(this);
        // chr.setToolTip(nameString);
        col = table.getColumnModel().getColumn(1);
        col.setHeaderRenderer(chr);
        col.setPreferredWidth(nameColWidth);
        col.setResizable(true);
        chr = new PointsetTableColumnHeaderRenderer(this);
        // chr.setToolTip(structureString);
        col = table.getColumnModel().getColumn(2);
        col.setHeaderRenderer(chr);
        col.setPreferredWidth(molColWidth);
        col.setResizable(true);
        for (int i = 0; i < pointset.getDimensionality(); i++) {
          chr = new PointsetTableColumnHeaderRenderer(this);
          // chr.setToolTip(pointset.getColumnDescription(i));
          col = table.getColumnModel().getColumn(i + 3);
          col.setHeaderRenderer(chr);
          int k = (tabbedPaneWidth - nameColWidth - hiddenColWidth - molColWidth)
              / pointset.getDimensionality();
          k = k < hiddenColWidth ? hiddenColWidth : k;
          col.setPreferredWidth(k);
          col.setResizable(true);
        }
        table.setRowHeight(100);
      }
    }
    else {
      if (Pointset.stringMetaImageColumnIndex == -1) {
        pointsetTableModel = new MaskHiddenNoMolStringTableModel(session,
            pointset);
        table.setModel(pointsetTableModel);
        // name
        chr = new PointsetTableColumnHeaderRenderer(this);
        // chr.setToolTip(nameString);
        col = table.getColumnModel().getColumn(0);
        col.setHeaderRenderer(chr);
        col.setPreferredWidth(nameColWidth);
        col.setResizable(true);
        for (int i = 0; i < pointset.getDimensionality(); i++) {
          chr = new PointsetTableColumnHeaderRenderer(this);
          // chr.setToolTip(pointset.getColumnDescription(i));
          col = table.getColumnModel().getColumn(i + 1);
          col.setHeaderRenderer(chr);
          int k = (tabbedPaneWidth - nameColWidth)
              / pointset.getDimensionality();
          k = k < hiddenColWidth ? hiddenColWidth : k;
          col.setPreferredWidth(k);
          col.setResizable(true);
        }
      }
      else {
        pointsetTableModel = new MaskHiddenTableModel(session, pointset);
        table.setModel(pointsetTableModel);
        // name, structure
        chr = new PointsetTableColumnHeaderRenderer(this);
        // chr.setToolTip(nameString);
        col = table.getColumnModel().getColumn(0);
        col.setHeaderRenderer(chr);
        col.setPreferredWidth(nameColWidth);
        col.setResizable(true);
        chr = new PointsetTableColumnHeaderRenderer(this);
        // chr.setToolTip(structureString);
        col = table.getColumnModel().getColumn(1);
        col.setHeaderRenderer(chr);
        col.setPreferredWidth(molColWidth);
        col.setResizable(true);
        for (int i = 0; i < pointset.getDimensionality(); i++) {
          chr = new PointsetTableColumnHeaderRenderer(this);
          // chr.setToolTip(pointset.getColumnDescription(i));
          col = table.getColumnModel().getColumn(i + 2);
          col.setHeaderRenderer(chr);
          int k = (tabbedPaneWidth - nameColWidth - molColWidth)
              / pointset.getDimensionality();
          k = k < hiddenColWidth ? hiddenColWidth : k;
          col.setPreferredWidth(k);
          col.setResizable(true);
        }
        table.setRowHeight(molColWidth); // thus makes compound cell square
      }
    }
  }

  private JTable createTable() {
    return new JTable() {
      // Implement table header tool tips.
      @Override
      protected JTableHeader createDefaultTableHeader() {
        return new JTableHeader(columnModel) {
          @Override
          public String getToolTipText(MouseEvent e) {
            String tip = null;
            java.awt.Point p = e.getPoint();
            int index = columnModel.getColumnIndexAtX(p.x);
            int realIndex = columnModel.getColumn(index).getModelIndex();
            String columnName = getModel().getColumnName(realIndex);
            return columnName;
          }
        };
      }

      @Override
      public void changeSelection(int rowIndex, int columnIndex,
          boolean toggle, boolean extend) {
        super.changeSelection(rowIndex, columnIndex, toggle, extend);
        leadSelectionRow = rowIndex;
        session.spreadsheet.fireSpreadsheetChanged(new SpreadsheetEvent(this,
            PointsetTable.this, SpreadsheetEvent.Kind.LEAD_SELECTION));
      }

      // This table shades columns that are currently mapped to axes.
      @Override
      public Component prepareRenderer(TableCellRenderer renderer,
          int rowIndex, int colIndex) {
        // System.out.println("rendering");
        Component c = super.prepareRenderer(renderer, rowIndex, colIndex);
        if (c == null) {
          // Nothing to render!
          return new JLabel("[" + rowIndex + "," + colIndex + "]");
        }
        if (isCellSelected(rowIndex, colIndex)) {
          c.setForeground(getSelectionForeground());
          c.setBackground(getSelectionBackground());
          return c;
        }
        c.setForeground(getForeground());
        if (getModel().isColumnInSubjectSpace(colIndex)) {
          c.setBackground(Color.LIGHT_GRAY);
        }
        else {
          // If not shaded, match the table's background
          c.setBackground(getBackground());
        }
        return c;
      }

      @Override
      public PointsetTableModel getModel() {
        return pointsetTableModel;
      }

      private static final long serialVersionUID = 1L;
    };
  }

  private void removeMouseListeners() {
    int i;
    for (i = 0; i < mouseListeners.length; i++) {
      table.removeMouseListener(mouseListeners[i]);
    }
    for (i = 0; i < mouseMotionListeners.length; i++) {
      table.removeMouseMotionListener(mouseMotionListeners[i]);
    }
  }

  private void addMouseListeners() {
    for (final MouseListener element : mouseListeners) {
      table.addMouseListener(element);
    }
    for (final MouseMotionListener element : mouseMotionListeners) {
      table.addMouseMotionListener(element);
    }
  }

  void beep() {
    if (!session.explainedDataTableBeep) {
      session
          .message("This message will be replaced by a beep,\n"
              + "which will mean you attempted to selects rows in \n"
              + "a locked table(s), or attempted to use the \n"
              + "selections when there were none."/* ,"first_click_locked_table_data" */);
      session.explainedDataTableBeep = true;
    }
    else {
      Toolkit.getDefaultToolkit().beep();
    }
  }

  public void clearReferences() {
    table.removeMouseListener(pointsetTableHeaderMouseListener);
    // table.removeKeyListener(tableListener);
    table.getTableHeader()
        .removeMouseListener(pointsetTableHeaderMouseListener);
    // tablePopupMenu.remove(toggleActiveItem);
    // scrollPane.getViewport().removeKeyListener(tableListener);
    // scrollPane.getViewport().removeMouseListener(pointsetTableHeaderMouseListener);
  }

  private void scrollToRow(final int row, final int col) {
    if (row >= 0) {
      final JViewport viewport = (JViewport) table.getParent();
      final Rectangle rect = table.getCellRect(row, col, true);
      final Point pt = viewport.getViewPosition();
      rect.setLocation(rect.x - pt.x, rect.y - pt.y);
      viewport.scrollRectToVisible(rect);
      // tabbedPane.setSelectedIndex(tabIndex);
    }
  }

  public void scrollToPoint(final int pointIndex, int columnHeadIndex) {
    scrollToRow(userToTable[pointIndex], columnHeadIndex
        + pointsetTableModel.colOffset);
  }

  public boolean isUnlocked() {
    return unlocked;
  }

  public void setUnlocked(final boolean unlocked) {
    this.unlocked = unlocked;
    if (unlocked) {
      addMouseListeners();
    }
    else {
      removeMouseListeners();
    }
  }

  public boolean isDisplayHidden() {
    return displayHidden;
  }

  public void setDisplayHidden(final boolean displayHidden) {
    this.displayHidden = displayHidden;
  }

  public void setSessionPointset(final Session session, final Pointset pointset) {
    // called for persistence activation
    this.session = session;
    this.pointset = pointset;
  }

  public JTable getTable() {
    return table;
  }

  // Not an override..
  public PointsetTableModel getModel() {
    return pointsetTableModel;
  }

  // @Implements Persistent
  @Override
	public void setSession(Session session) {
    this.session = session;
  }

  @Override
  public void finalize() {
    System.out.println("Finalized [" + getClass().getSimpleName() + ";"
        + hashCode() + "]");
  }

  public Pointset getPointset() {
    return pointset;
  }

  public void setSelectedFor(int[] selections, boolean select, MouseEvent ev) {
    boolean ctrlDown = ev.isControlDown();
    boolean shiftDown = ev.isShiftDown();
    DefaultListSelectionModel selectionModel = (DefaultListSelectionModel) table
        .getSelectionModel();
    for (int i = 0, row; i < selections.length; i++) {
      row = userToTable[selections[i]];
      if (ctrlDown) {
        if (shiftDown) {
          // ctrl down, shift down
          if (select) {
            selectionModel.setAnchorSelectionIndex(selectionModel
                .getLeadSelectionIndex());
            selectionModel.setLeadSelectionIndex(row);
          }
          else {
            // nop
          }
        }
        else {
          // // ctrl down, shift up
          if (select) {
            if (table.isRowSelected(row)) {
              selectionModel.removeSelectionInterval(row, row);
            }
            else {
              selectionModel.addSelectionInterval(row, row);
            }
          }
          else {
            // nop
          }
        }
      }
      else {
        if (shiftDown) {
          // ctrl up, shift down
          if (select) {
            selectionModel.setLeadSelectionIndex(row);
          }
          else {
            // nop
          }
        }
        else {
          // ctrl up, shift up
          if (select) {
            selectionModel.clearSelection();
          }
          else {
            table.selectAll();
          }
          if (table.isRowSelected(row)) {
            selectionModel.removeSelectionInterval(row, row);
          }
          else {
            selectionModel.addSelectionInterval(row, row);
          }
        }
      }
    }
  }

  public void scrollToLastSelection() {
    int selectionCount = table.getSelectedRows().length;
    if (selectionCount > 0) {
      scrollToRow(table.getSelectedRows()[selectionCount - 1], 0);
    }
  }

  public void invert() {
    // This "inverts" the userToTable[] back to the old order so that the table
    // appears untouched.
    int i;
    for (i = 0; i < pointset.getPointCount(); i++) {
      tableToUser[userToTable[i]] = i;
    }
    // Modify tableToUser such that first nVisible rows redirect to only visible
    // numbers
    if (!displayHidden) {
      int tmp;
      int iBottom = pointset.getPointCount() - 1;
      // numbers at iBottom or greater will be hidden
      i = 0;
      while (i < iBottom) {
        if (pointset.hid[tableToUser[i]] != 0x00) { // hidden
          tmp = tableToUser[iBottom];
          tableToUser[iBottom] = tableToUser[i];
          tableToUser[i] = tmp;
          iBottom--;
        }
        else {
          i++;
        }
      }
    }
  }

  public int getLeadSelectionPoint() {
    if (leadSelectionRow == -1) {
      return -1;
    }
    return tableToUser[leadSelectionRow];
  }

  public void setSelectedPoints(int[] pointIndices, boolean select) {
    ListSelectionModel listSelectionModel = getTable().getSelectionModel();
    listSelectionModel.setValueIsAdjusting(true);
    int row;
    if (select) {
      for (int i = 0; i < pointIndices.length; i++) {
        row = userToTable[pointIndices[i]];
        listSelectionModel.addSelectionInterval(row, row);
      }
    }
    else {
      for (int i = 0; i < pointIndices.length; i++) {
        row = userToTable[pointIndices[i]];
        listSelectionModel.removeSelectionInterval(row, row);
      }
    }
    listSelectionModel.setValueIsAdjusting(false);
  }

  public int[] getSelectedPoints() {
    int[] selectedRows = table.getSelectedRows();
    int[] selectedPoints = new int[selectedRows.length];
    for (int row = 0; row < selectedRows.length; row++) {
      selectedPoints[row] = tableToUser[selectedRows[row]];
    }
    return selectedPoints;
  }

  public JScrollPane getScrollPane() {
    return scrollPane;
  }

  public ColumnHead getSelectedColumnHead() {
    return pointset.getCurrentAcMap().getColumnHeadAt(
        getColumnHeadIndex(selectedColumn));
  }

  public int getSelectedColumnHeadIndex() {
    return getColumnHeadIndex(selectedColumn);
  }

  public int getColumnHeadIndex(int column) {
    int hiddenColumnCount = isDisplayHidden() ? 0 : 1;
    int columnHeadIndex = column - pointsetTableModel.colOffset
        - hiddenColumnCount;
    if (columnHeadIndex < 0 || columnHeadIndex >= pointset.getDimensionality()) {
      // throw new RuntimeException("column index out of bounds");
      return -1;
    }
    return columnHeadIndex;
  }

  private static final long serialVersionUID = 1L;
}
