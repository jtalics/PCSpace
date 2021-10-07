package gov.nih.ncgc.openhts.tool1.spreadsheet;

import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTabbedPane;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class CustomTabbedPaneMouseHandler extends MouseAdapter {
  private final CustomTabbedPaneUI paneUI;
  private final JTabbedPane tabbedPane;
  private final Session session;
  
  CustomTabbedPaneMouseHandler(final Session session, final JTabbedPane tabbedPane, final CustomTabbedPaneUI paneUI) {
    this.session=session;
    this.paneUI = paneUI;
    this.tabbedPane = tabbedPane;
  }

  @Override
  public void mousePressed(final MouseEvent ev) {
    try {
      // ignore everything except left mouse button
      if ((ev.getModifiers() & InputEvent.BUTTON1_MASK) == 0) {
        // ev.consume();
        return;
      }
      if (this.paneUI.iconRectangle.contains(ev.getPoint())) {
        final int i = tabbedPane.getSelectedIndex();
        final Pointset pointset = session.spreadsheet.getPointsetInSelectedTab();
        if (session.spreadsheet.getPointsetTable(pointset).isUnlocked()) {
          session.spreadsheet.lockThisTable();
        }
        else {
          session.spreadsheet.unlockThisTable();
        }
      }
      tabbedPane.paintImmediately(new Rectangle(tabbedPane.getWidth(),
          tabbedPane.getHeight()));
    }
    catch (final Throwable ex) {
      session.errorSupport("While listening to mouse: ", ex,
          "mouse_tabbed_pane_error");
    }
  }
}