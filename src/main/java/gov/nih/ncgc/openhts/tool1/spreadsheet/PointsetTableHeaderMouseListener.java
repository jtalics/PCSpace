package gov.nih.ncgc.openhts.tool1.spreadsheet;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.table.JTableHeader;
import gov.nih.ncgc.openhts.tool1.Session;

class PointsetTableHeaderMouseListener implements MouseListener, MouseMotionListener {
  private final PointsetTable pointsetTable;
  private final Session session;
  private int currentColumnIndex = -1;

  PointsetTableHeaderMouseListener(final Session session,
      final PointsetTable pointsetTable) {
    this.session = session;
    this.pointsetTable = pointsetTable;
  }

  @Override
	public void mouseClicked(final MouseEvent ev) {
    // NOTE: see ListSelectionListening in PointsetTable
  }

  @Override
	public void mousePressed(final MouseEvent ev) {
    try {
      final JTableHeader header = pointsetTable.getTable().getTableHeader();
      // Ignore everything except right mouse button
      if ((ev.getModifiers() & InputEvent.BUTTON3_MASK) == 0) {
        final int i = session.spreadsheet.tabbedPane.getSelectedIndex();
        if (!pointsetTable.isUnlocked()) {
          session.statusPanel.log2("table is locked");
        }
        return;
      }
      // Set the text in the popups.
      if (ev.getSource() == header) {
        pointsetTable.pointsetTableHeaderPopup.rebuild(ev);
        pointsetTable.pointsetTableHeaderPopup.show(header, ev.getX(), ev
            .getY());
      }
      else if (ev.getSource() == pointsetTable.getTable()) {
        session.spreadsheet.updateEnablement();
        session.spreadsheet.tablePopup.show(this.pointsetTable.getTable(), ev
            .getX(), ev.getY());
      }
      else {
        new RuntimeException("unhandled mouse event: " + ev.getSource())
            .printStackTrace();
      }
    }
    catch (final Throwable ex) {
      session.errorSupport("While listening to mouse: ",ex,"");
    }
  }

  @Override
	public void mouseEntered(final MouseEvent ev) {
    // nop
  }

  @Override
	public void mouseReleased(final MouseEvent ev) {
    // nop
  }

  @Override
	public void mouseExited(final MouseEvent ev) {
    // not used here
  }

  @Override
	public void mouseDragged(MouseEvent ev) {
    // nop
  }

  @Override
	public void mouseMoved(MouseEvent ev) {
    // NOTE: TOOL TIP now done at JTable constructor
    //    // esp. useful if column is not expanded and can't see whole name
//    JTableHeader header = (JTableHeader)ev.getSource();
//    JTable table = pointsetTable.getTable();
//    TableColumnModel colModel = table.getColumnModel();
//    int vColIndex = colModel.getColumnIndexAtX(ev.getX());
//
//    // Return if not at any column header
//    if (vColIndex < 0) {
//      return;
//    }
//
//    if (vColIndex != currentColumnIndex) {
//        header.setToolTipText(table.getModel().getColumnName(vColIndex));
//        currentColumnIndex = vColIndex;
//    }  
  }
}
