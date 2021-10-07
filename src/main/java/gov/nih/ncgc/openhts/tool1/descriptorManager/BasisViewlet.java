package gov.nih.ncgc.openhts.tool1.descriptorManager;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.plotManager.Axes;
import gov.nih.ncgc.openhts.tool1.pointsetManager.AxesColumnHeadsMapping;
import gov.nih.ncgc.openhts.tool1.pointsetManager.AxesDescriptorsMappingViewlet;

/**
 * Purpose is to provide a view for a Basis.
 * 
 * @author talafousj
 */
public class BasisViewlet extends Box implements ChangeListener {
  private final Session session;
  public AxesDescriptorsMappingViewlet acMapViewlet;
  private Basis basis;
  private final JTextField basisNameTextField = new JTextField();

  public BasisViewlet(final Session session) {
    super(BoxLayout.PAGE_AXIS);
    this.session = session;
    setBackground(Color.LIGHT_GRAY);
    // String[] directoryContents = session.descriptorManager.homeDir.list();
    setBorder(new EmptyBorder(5, 5, 5, 5));
    //
    final JPanel basisNamePanel = new JPanel(new BorderLayout());
    add(basisNamePanel);
    basisNamePanel.add(new JLabel("Name: "),
        BorderLayout.WEST);
    basisNamePanel.add(basisNameTextField, BorderLayout.CENTER);
    basisNameTextField.setText(null);
    //
    final JLabel label = new JLabel("select basis?????????????");
    label.setHorizontalAlignment(SwingConstants.CENTER);
    JScrollPane scrollPane = new JScrollPane(label,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    add(scrollPane);
    scrollPane.setWheelScrollingEnabled(true);
    acMapViewlet = new AxesDescriptorsMappingViewlet(session);
    acMapViewlet.setAcMap(null);
    scrollPane.setViewportView(acMapViewlet);
  }

  @Override
	public void stateChanged(ChangeEvent e) {
    Basis basis = (Basis) e.getSource();
    if (basis != null) {
      basisNameTextField.setText(basis.getName());
      acMapViewlet.setColumnHeads(basis.getAcMap().getColumnHeads());
      acMapViewlet.setColumnHeads(basis.getAcMap().getColumnHeads());
      acMapViewlet.setSelectedColumnForAxis(Axes.X_AXIS, basis.getAcMap()
          .getColumnHeadIndexForAxis(Axes.X_AXIS));
      acMapViewlet.setSelectedColumnForAxis(Axes.Y_AXIS, basis.getAcMap()
          .getColumnHeadIndexForAxis(Axes.Y_AXIS));
      acMapViewlet.setSelectedColumnForAxis(Axes.Z_AXIS, basis.getAcMap()
          .getColumnHeadIndexForAxis(Axes.Z_AXIS));
    }
  }

  public AxesDescriptorsMappingViewlet getAcMapViewlet() {
    return acMapViewlet;
  }

  public String getBasisName() {
    return basisNameTextField.getText();
  }

  private static final long serialVersionUID = 1L;

  public void setSelectedBasis(Basis basis) {
    this.basis = basis;
      if (basis == null) {
        basisNameTextField.setText(basis.getName());
        acMapViewlet.setAcMap(null);
        return;
      }
      basisNameTextField.setText(basis.getName());
//      shapeComboBox.setEnabled(true);
//      sizeSpinner.setEnabled(true);
//      shapeComboBox.setSelectedItem(selectedPointset.getShape());
//      colorButton.setEnabled(true);
//      colorButton.setBackground(selectedPointset.getColor());
//      Color color = selectedPointset.getColor();
//      if (color == null) {
//        colorButton.setText(null);
//        colorButton.setForeground(null);      
//      }
//      else {
//        Palette.Choice closestChoice = Palette.getClosestChoice(color);
//        colorButton.setText(closestChoice.toString());
//        colorButton.setForeground(closestChoice.getTextColor());
//      }
//      sizeSpinner.setValue(new Double(selectedPointset.getSize()));
      AxesColumnHeadsMapping acMap = basis.getAcMap();
      acMapViewlet.setAcMap(acMap);
    }
}
