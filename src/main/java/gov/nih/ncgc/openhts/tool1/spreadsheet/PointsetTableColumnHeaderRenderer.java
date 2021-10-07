package gov.nih.ncgc.openhts.tool1.spreadsheet;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import gov.nih.ncgc.openhts.tool1.plotManager.Axes;
import gov.nih.ncgc.openhts.tool1.pointsetManager.AxesColumnHeadsMapping;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
class PointsetTableColumnHeaderRenderer extends DefaultTableCellRenderer {
  /** label to display also an icon */
  private JLabel label = null;
  private final PointsetTable pointsetTable;

  public PointsetTableColumnHeaderRenderer(PointsetTable pointsetTable) {
    super();
    this.pointsetTable = pointsetTable;
    label = new JLabel();
    label.setHorizontalAlignment(SwingConstants.CENTER);
    label.setOpaque(true);
    label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    // label.setPreferredSize(new
    // Dimension(label.getWidth(),label.getHeight()));
  }

  @Override
  public Component getTableCellRendererComponent(final JTable table,
      final Object value, final boolean isSelected, final boolean hasFocus,
      final int row, final int column) {
    String text = "";
    if (value != null) {
      text = value.toString();
    }
    int columnHeadIndex = pointsetTable.getColumnHeadIndex(column);
    if (-1 != columnHeadIndex) {
      label.setBorder(BorderFactory.createRaisedBevelBorder());
      int axis =pointsetTable.getPointset()
      .getCurrentAcMap().getAxisForColumnHeadIndex(columnHeadIndex);
      if (axis != AxesColumnHeadsMapping.UNMAPPED) {
        switch (axis) {
        case Axes.X_AXIS:
          text = "x:"+text;
          break;
        case Axes.Y_AXIS:
          text = "y:"+text;
          break;
        case Axes.Z_AXIS:
          text = "z:"+text;
          break;
        default:
          throw new RuntimeException("bad axis");
        }
      }
    }
    label.setText(text);
    return label;
  }

  @Override
  protected void setValue(final Object value) {
    if (value != null) {
      label.setText("" + value);
    }
    else {
      label.setText("");
    }
  }

  public void setToolTip(final String toolTip) {
    if (toolTip != null) {
      label.setToolTipText(toolTip);
    }
  }

  @Override
  public void setIcon(final Icon icon) {
    if (label != null) {
      label.setIcon(icon);
    }
  }

  private static final long serialVersionUID = 1L;
}
