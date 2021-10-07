package gov.nih.ncgc.openhts.tool1.plotManager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import gov.nih.ncgc.openhts.tool1.Palette;

/**
 *
 */
public final class TextListCellRenderer extends JLabel implements ListCellRenderer {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

  /**
   *
   */
  public TextListCellRenderer() {
    // Don't paint behind the component
    setOpaque(true);
  }

  /**
   *
   */
  @Override
	public Component getListCellRendererComponent(final JList list, final Object value, final int index,
    boolean isSelected, final boolean cellHasFocus) {

    Font theFont = null;
    Color theColor = null;
    String theText = null;

    if (value instanceof Text) {

      final Text item = (Text)value;

      theText = item.toString();
      theFont = item.getFont();
      theColor = item.getColor();
    }

    if (!isSelected) {
      setBorder(BorderFactory.createLineBorder(
        list.getBackground(),2));
    }
    else {
      setBorder(BorderFactory.createLineBorder(
        Color.blue,2));
    }

    setText(theText);
    setFont(theFont);
    setBackground(theColor);
    setForeground(Palette.getClosestChoice(theColor).getColor());

    return this;
  }
}