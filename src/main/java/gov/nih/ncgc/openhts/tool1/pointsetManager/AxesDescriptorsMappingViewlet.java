/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.pointsetManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.plotManager.Axes;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class AxesDescriptorsMappingViewlet extends JPanel {
  private JComboBox[] axesComboBoxes = new JComboBox[3];
  private Session session;
  private final static Dimension dimension1 = new Dimension(20, 20);
  private final static Dimension dimension2 = new Dimension(100, 20);

  public AxesDescriptorsMappingViewlet(Session session) {
    this.session = session;
    setLayout(new BorderLayout());
    final Box vbox = Box.createVerticalBox();
    add(vbox, BorderLayout.CENTER);
    vbox.setBorder(BorderFactory
        .createTitledBorder("Axis to column assignment"));
    vbox.setToolTipText("descriptors/columns to use for plotting");
    vbox.add(createAxisColumnMappingPanel(Axes.X_AXIS));
    vbox.add(createAxisColumnMappingPanel(Axes.Y_AXIS));
    vbox.add(createAxisColumnMappingPanel(Axes.Z_AXIS));
  }

  public void setAcMap(AxesColumnHeadsMapping acMap) {
    for (int axis = 0; axis < 3; axis++) {
      axesComboBoxes[axis].removeAllItems();
      if (acMap != null) {
        for (ColumnHead columnHead : acMap.getColumnHeads()) {
          axesComboBoxes[axis].addItem(columnHead);
        }
        axesComboBoxes[axis].setSelectedIndex(acMap
            .getColumnHeadIndexForAxis(axis));
      }
    }
  }

  private JPanel createAxisColumnMappingPanel(int axis) {
    final JPanel panel = new JPanel(new FlowLayout());
    panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
    final JLabel label = new JLabel(Axes.getNameForAxis(axis)+":", SwingConstants.RIGHT);
    label.setPreferredSize(dimension1);
    panel.add(label);
    axesComboBoxes[axis] = new JComboBox();
    axesComboBoxes[axis].setPreferredSize(dimension2);
    panel.add(axesComboBoxes[axis]);
    return panel;
  }

  public void setSelectedColumnForAxis(int axis, int columnIndex) {
    axesComboBoxes[axis].setSelectedIndex(columnIndex);
  }

  public int getSelectedColumnForAxis(int axis) {
    return axesComboBoxes[axis].getSelectedIndex();
  }

  public void setColumnHeads(ColumnHead[] descriptors) {
    for (int axis = 0; axis < 3; axis++) {
      axesComboBoxes[axis].removeAllItems();
      for (ColumnHead descriptor : descriptors) {
        axesComboBoxes[axis].addItem(descriptor);
      }
    }
  }

  private static final long serialVersionUID = 1L;
}
