/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.spreadsheet;

import java.awt.event.MouseEvent;
import javax.swing.JSeparator;
import javax.swing.table.JTableHeader;
import com.jidesoft.swing.JidePopupMenu;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.pointsetManager.AxesColumnHeadsMapping;
import gov.nih.ncgc.openhts.tool1.pointsetManager.ColumnHead;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class PointsetTableHeaderPopup extends JidePopupMenu {
  private Session session;
  private PointsetTable pointsetTable;

  PointsetTableHeaderPopup(Session session, PointsetTable pointsetTable) {
    this.session = session;
    this.pointsetTable = pointsetTable;
  }

  void rebuild(MouseEvent ev) {
    final JTableHeader header = pointsetTable.getTable().getTableHeader();
    this.pointsetTable.selectedColumn = header.columnAtPoint(ev.getPoint());
    ColumnHead selectedColumnHead = pointsetTable.getSelectedColumnHead();
    session.actionManager.spreadsheetAssignColumnXItemAction.setEnabled(false);
    session.actionManager.spreadsheetAssignColumnYItemAction.setEnabled(false);
    session.actionManager.spreadsheetAssignColumnZItemAction.setEnabled(false);
    removeAll();
    if (selectedColumnHead == null) {
      // nop
    }
    else {
      Pointset pointset = pointsetTable.getPointset();
      AxesColumnHeadsMapping acMap = pointset.getCurrentAcMap();
      String name = selectedColumnHead.getName();
      add(session.actionManager.spreadsheetAssignColumnXItemAction);
      session.actionManager.spreadsheetAssignColumnXItemAction.setName("Plot '"+name+"' on x-axis");
      add(session.actionManager.spreadsheetAssignColumnYItemAction);
      session.actionManager.spreadsheetAssignColumnYItemAction.setName("Plot '"+name+"' on y-axis");
      add(session.actionManager.spreadsheetAssignColumnZItemAction);
      session.actionManager.spreadsheetAssignColumnZItemAction.setName("Plot '"+name+"' on z-axis");
      switch (session.plotManager.getDim()) {
      case 3:
        session.actionManager.spreadsheetAssignColumnZItemAction
            .setEnabled(true);
      case 2:
        session.actionManager.spreadsheetAssignColumnXItemAction
            .setEnabled(true);
        session.actionManager.spreadsheetAssignColumnYItemAction
            .setEnabled(true);
        break;
      default:
        throw new RuntimeException("bad dim");
      }
      add(new JSeparator());
    }
    //
    add(session.actionManager.spreadsheetSortAscendingItemAction);
    add(session.actionManager.spreadsheetSortDescendingItemAction);
    if (this.pointsetTable.getTable().getRowCount() <= 1) {
      session.actionManager.spreadsheetSortAscendingItemAction
          .setEnabled(false); // TODO: make work and set true
      session.actionManager.spreadsheetSortAscendingItemAction
          .setName("Ascending sort of column");
      session.actionManager.spreadsheetSortDescendingItemAction
          .setEnabled(false);
      session.actionManager.spreadsheetSortDescendingItemAction
          .setName("Descending sort of column");
    }
    else {
      final String s = " sort of \""
          + header.getColumnModel()
              .getColumn(this.pointsetTable.selectedColumn).getHeaderValue()
          + "\"";
      session.actionManager.spreadsheetSortAscendingItemAction
          .setEnabled(false); // TODO: disabled until
      session.actionManager.spreadsheetSortAscendingItemAction
          .setName("Ascending" + s);
      session.actionManager.spreadsheetSortDescendingItemAction
          .setEnabled(false); // TODO: disabled until
      session.actionManager.spreadsheetSortDescendingItemAction
          .setName("Descending" + s);
    }
  }
}
