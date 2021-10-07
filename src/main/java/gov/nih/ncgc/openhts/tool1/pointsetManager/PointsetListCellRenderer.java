package gov.nih.ncgc.openhts.tool1.pointsetManager;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import gov.nih.ncgc.openhts.tool1.Palette;
import gov.nih.ncgc.openhts.tool1.Session;

public final class PointsetListCellRenderer extends JLabel implements ListCellRenderer {
  private final Session session;

  public PointsetListCellRenderer(final Session session) {
    this.session = session;
    // Don't paint behind the component
    setOpaque(true);
  }

  @Override
	public Component getListCellRendererComponent(final JList list, final Object value,
      final int index, boolean isSelected, final boolean cellHasFocus) {
    Color theColor = null;
    String theText = null;
    if (value instanceof Pointset) {
      final Pointset item = (Pointset) value;
      theText = item.toString();
      theColor = item.getColor();
    }
    else if (value instanceof String) {
      final String item = (String) value;
      Pointset pointset;
      final int len = session.pointsetManager.pointsetCount();
      for (int i = 0; i < len; i++) {
        if (session.pointsetManager.getPointsetAt(i).toString().indexOf(item) != -1) {
          pointset = session.pointsetManager.getPointsetAt(i);
          theText = pointset.toString();
          theColor = pointset.getColor();
          break;
        }
      }
    }
    if (!isSelected) {
      setBorder(BorderFactory.createLineBorder(list.getBackground(), 2));
    }
    else {
      setBorder(BorderFactory.createLineBorder(Color.blue, 2));
    }
    setText(theText);
    setBackground(theColor);
    setForeground(Palette.getComplement(theColor));
    return this;
  }
  private static final long serialVersionUID = 1L;
}