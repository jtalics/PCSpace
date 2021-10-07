package gov.nih.ncgc.openhts.tool1.spreadsheet;

import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import gov.nih.ncgc.openhts.tool1.Session;

class CustomTabbedPaneUI extends BasicTabbedPaneUI {
  private final JTabbedPane tabbedPane;
  private final Session session;;

  CustomTabbedPaneUI(Session session, final JTabbedPane tabbedPane) {
    this.session=session;
    this.tabbedPane = tabbedPane;
  }

  Rectangle iconRectangle = new Rectangle();

  @Override
  protected void installListeners() {
    super.installListeners();
    tabbedPane.addMouseListener(new CustomTabbedPaneMouseHandler(session,tabbedPane,this));
  }

  @Override
  protected void paintTab(final Graphics g, final int tabPlacement, final Rectangle[] rects,
      final int tabIndex, final Rectangle iconRect, final Rectangle textRect) {
    super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);
    /*
     * if (this.active) { g.drawImage(icons[ICON_ACTIVE].getImage(),
     * iconRect.x, iconRect.y, iconRect.width, iconRect.height, Color.gray,
     * null); } else { g.drawImage(icons[ICON_INACTIVE].getImage(),
     * iconRect.x, iconRect.y, iconRect.width, iconRect.height, Color.gray,
     * null); }
     */
    if (tabbedPane.getSelectedIndex() == tabIndex) {
      // System.out.println("tabIndex: "+tabIndex+" selected:
      // "+tabbedPane.getSelectedIndex()+" iconRect: "+iconRect);
      iconRectangle.x = iconRect.x;
      iconRectangle.y = iconRect.y;
      iconRectangle.width = iconRect.width;
      iconRectangle.height = iconRect.height;
    }
  }
}