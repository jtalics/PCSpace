package gov.nih.ncgc.openhts.tool1.pointsetManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import gov.nih.ncgc.openhts.tool1.Palette;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dialogManager.ColorChooserDialog;
import gov.nih.ncgc.openhts.tool1.util.colorizer.Colorizer;
import gov.nih.ncgc.openhts.tool1.util.colorizer.ColorizerChangeEvent;
import gov.nih.ncgc.openhts.tool1.util.colorizer.ColorizerViewlet;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class PointsetViewlet extends JPanel implements ChangeListener {
  private final Session session;
  JSpinner sizeSpinner;
  private SpinnerNumberModel sizeModel;
  private JComboBox shapeComboBox;
  private JButton colorButton;
  private AxesDescriptorsMappingViewlet acMapViewlet;
  private final static Dimension dimension1 = new Dimension(70, 25);
  private final static Dimension dimension2 = new Dimension(100, 25);
  private JComboBox coloringColumnHeadComboBox;
  private Pointset selectedPointset;
  private JTextField nameTextField;
  private final Colorizer colorizer;
  private ColorizerViewlet colorizerViewlet;
  private final List<ChangeListener> listeners = new ArrayList<ChangeListener>();

  PointsetViewlet(final Session session, Colorizer colorizer) {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    this.session = session;
    this.colorizer = colorizer;
    initialize();
  }

  void initialize() {
    add(getNamePanel());
    JPanel panel = new JPanel();
    add(panel);
    panel.setLayout(new GridLayout(1, 2, 5, 5));
    final Box symbolPanel = Box.createVerticalBox();
    symbolPanel.setBorder(BorderFactory.createTitledBorder("Point symbol"));
    symbolPanel.add(getSymbolShapePanel());
    symbolPanel.add(getSymbolSizePanel());
    symbolPanel.add(getSymbolColorPanel());
    panel.add(symbolPanel);
    acMapViewlet = new AxesDescriptorsMappingViewlet(session);
    acMapViewlet.setAcMap(null);
    panel.add(acMapViewlet);
    colorizerViewlet = new ColorizerViewlet(colorizer);
    colorizer.addChangeListener(colorizerViewlet);
    colorizer.addChangeListener(this);
    add(colorizerViewlet);
  }

  private Component getNamePanel() {
    SpringLayout springLayout = new SpringLayout();
    final JPanel namePanel = new JPanel(springLayout);
    // First add all components
    JLabel nameLabel = new JLabel("Pointset name: ");
    namePanel.add(nameLabel);
    nameTextField = new JTextField();
    namePanel.add(nameTextField, BorderLayout.CENTER);
    JLabel colorLabel = new JLabel("Pointset color: ");
    namePanel.add(colorLabel);
    colorButton = new JButton();
    namePanel.add(colorButton);
    colorButton.setToolTipText("click to change the color of the symbol");
    colorButton.setBorder(BorderFactory.createRaisedBevelBorder());
    colorButton.setPreferredSize(dimension2);
    colorButton.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
        try {
          final ColorChooserDialog colorChooserDialog = session.dialogManager
              .getColorChooserModifyDialog();
          colorChooserDialog.setModal(true);
          colorChooserDialog.showDialog();
          setButtonFace(colorChooserDialog.getChoice());
          selectedPointset.setColor(colorChooserDialog.getChoice().getColor());
        }
        catch (final Throwable ex) {
          session.errorSupport(" ", ex, "color_modify_data_button_error");
        }
      }
    });
    // Set up springs originating from component in container
    springLayout.putConstraint(SpringLayout.WEST, nameLabel, 5,
        SpringLayout.WEST, namePanel);
    springLayout.putConstraint(SpringLayout.WEST, nameTextField, 5,
        SpringLayout.EAST, nameLabel);
    springLayout.putConstraint(SpringLayout.WEST, colorLabel, 5,
        SpringLayout.EAST, nameTextField);
    springLayout.putConstraint(SpringLayout.WEST, colorButton, 5,
        SpringLayout.EAST, colorLabel);
    springLayout.putConstraint(SpringLayout.NORTH, nameLabel, 5,
        SpringLayout.NORTH, namePanel);
    springLayout.putConstraint(SpringLayout.NORTH, nameTextField, 5,
        SpringLayout.NORTH, namePanel);
    springLayout.putConstraint(SpringLayout.NORTH, colorLabel, 5,
        SpringLayout.NORTH, namePanel);
    springLayout.putConstraint(SpringLayout.NORTH, colorButton, 5,
        SpringLayout.NORTH, namePanel);
    // Set up springs originating from container
    springLayout.putConstraint(SpringLayout.EAST, namePanel, 5,
        SpringLayout.EAST, colorButton);
    springLayout.putConstraint(SpringLayout.SOUTH, namePanel, 5,
        SpringLayout.SOUTH, colorButton);
    return namePanel;
  }

  private Component getSymbolSizePanel() {
    final JPanel sizePanel = new JPanel();
    sizePanel.setLayout(new BoxLayout(sizePanel, BoxLayout.LINE_AXIS));
    final JLabel sizeLabel = new JLabel("Size (mm):", SwingConstants.RIGHT);
    sizeLabel
        .setToolTipText("the size in millimeters of the symbol as drawn on the screen");
    sizeLabel.setPreferredSize(dimension1);
    sizePanel.add(sizeLabel);
    sizeModel = new SpinnerNumberModel(0, 0.0, Double.POSITIVE_INFINITY, 0.25);
    sizeSpinner = new JSpinner(sizeModel);
    // sizeSpinner.setBorder(BorderFactory.createLoweredBevelBorder());
    sizeSpinner.setPreferredSize(new Dimension(100, 25));
    sizePanel.add(sizeSpinner);
    return sizePanel;
  }

  private void setButtonFace(Palette.Choice choice) {
    if (choice == null) {
      colorButton.setEnabled(false);
    }
    else {
      colorButton.setEnabled(true);
    }
    colorButton.setBackground(choice.getColor());
    colorButton.setText(choice.toString());
    colorButton.setForeground(choice.getTextColor());
  }

  private Component getSymbolColorPanel() {
    final JPanel colorPanel = new JPanel();
    colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.LINE_AXIS));
    final JLabel colorLabel = new JLabel("Point color:", SwingConstants.RIGHT);
    colorPanel.add(colorLabel);
    colorLabel.setPreferredSize(dimension1);
    coloringColumnHeadComboBox = new JComboBox();
    colorPanel.add(coloringColumnHeadComboBox);
    coloringColumnHeadComboBox.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(ActionEvent e) {
        int index = coloringColumnHeadComboBox.getSelectedIndex();
        if (index == 0) {
          selectedPointset.setColorizingColumnHead(null);
          remove(colorizerViewlet);
        }
        else if (index > 0) {
          ColumnHead columnHead = (ColumnHead) coloringColumnHeadComboBox
              .getSelectedItem();
          colorizer.setPreprocessorFunctionStats(selectedPointset
              .getStats(columnHead));
          float[] values = selectedPointset.getColumnUserValuesFor(columnHead);
          colorizer.getPreprocessorFunction().apply(values);
          colorizer.setColoringFunctionStats(new Stats(values));
          // NOTE: we don't actually colorize, but let the Apply button do it.
          add(colorizerViewlet);
          revalidate();
        }
        fireStateChanged(new ChangeEvent(this)); // need repacked
      }
    });
    return colorPanel;
  }

  private Component getSymbolShapePanel() {
    final JPanel shapePanel = new JPanel();
    shapePanel.setLayout(new BoxLayout(shapePanel, BoxLayout.LINE_AXIS));
    final JLabel label2 = new JLabel("Shape:", SwingConstants.RIGHT);
    label2.setPreferredSize(dimension1);
    shapePanel.add(label2);
    shapeComboBox = new JComboBox();
    shapeComboBox
        .setToolTipText("click to change the appearance of the symbol");
    // shapeComboBox.setBorder(BorderFactory.createRaisedBevelBorder());
    shapeComboBox.addItem(Shape.PIXEL);
    shapeComboBox.addItem(Shape.DOT);
    shapeComboBox.addItem(Shape.CIRCLE);
    shapeComboBox.addItem(Shape.BLOCK);
    shapeComboBox.addItem(Shape.SQUARE);
    shapeComboBox.addItem(Shape.XSHAPE);
    shapeComboBox.addItem(Shape.PLUS);
    shapeComboBox.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
        try {
          if (shapeComboBox.getSelectedIndex() == 0) {
            sizeSpinner.setEnabled(false);
          }
          else {
            sizeSpinner.setEnabled(true);
          }
        }
        catch (final Throwable ex) {
          session.errorSupport(" ", ex, "TODO");
        }
      }
    });
    shapeComboBox.setPreferredSize(dimension2);
    shapePanel.add(shapeComboBox);
    return shapePanel;
  }

  void setSelectedPointset(Pointset pointset) {
    if (pointset == null) {
      shapeComboBox.setEnabled(false);
      colorButton.setEnabled(false);
      sizeSpinner.setEnabled(false);
      return;
    }
    selectedPointset = pointset;
    nameTextField.setText(selectedPointset.getName());
    shapeComboBox.setEnabled(true);
    sizeSpinner.setEnabled(true);
    shapeComboBox.setSelectedItem(selectedPointset.getShape());
    colorButton.setEnabled(true);
    colorButton.setBackground(selectedPointset.getColor());
    Color color = selectedPointset.getColor();
    if (color == null) {
      colorButton.setText(null);
      colorButton.setForeground(null);      
    }
    else {
      Palette.Choice closestChoice = Palette.getClosestChoice(color);
      colorButton.setText(closestChoice.toString());
      colorButton.setForeground(closestChoice.getTextColor());
    }
    sizeSpinner.setValue(new Double(selectedPointset.getSize()));
    AxesColumnHeadsMapping acMap = selectedPointset.getAcMap();
    acMapViewlet.setAcMap(acMap);
    //
    coloringColumnHeadComboBox.removeAllItems();
    if (acMap != null) {
      coloringColumnHeadComboBox.addItem("Use pointset color");
      for (ColumnHead columnHead : acMap.getColumnHeads()) {
        coloringColumnHeadComboBox.addItem(columnHead);
      }
      ColumnHead columnHead = selectedPointset.getColorizingColumnHead();
      if (columnHead == null) {
        coloringColumnHeadComboBox.setSelectedIndex(0);
      }
      else {
        coloringColumnHeadComboBox.setSelectedItem(columnHead);
      }
    }
  }

//acMapViewlet.setColumnHeads(acMap.getColumnHeads());
//acMapViewlet.setSelectedXIndex(acMap
//    .getColumnHeadIndexForAxis(Axes.X_AXIS));
//acMapViewlet.setSelectedYIndex(acMap
//    .getColumnHeadIndexForAxis(Axes.Y_AXIS));
//acMapViewlet.setSelectedZIndex(acMap
//    .getColumnHeadIndexForAxis(Axes.Z_AXIS));
  // // @Implements ChangeListener
  // public void stateChanged(ChangeEvent e) {
  // // listening to PointsetModifyDialog
  // int selectedIndex = ((JList) e.getSource()).getSelectedIndex();
  // if (selectedIndex >= 0) {
  // }
  // else {
  // shapeComboBox.setEnabled(false);
  // colorButton.setEnabled(false);
  // sizeSpinner.setEnabled(false);
  // }
  // }
  public String getPointsetName() {
    return nameTextField.getText();
  }

  public AxesDescriptorsMappingViewlet getAcMapViewlet() {
    return acMapViewlet;
  }

  public float getSymbolSize() {
    return ((Double) sizeModel.getValue()).floatValue();
  }

  public Shape getShape() {
    return (Shape) shapeComboBox.getSelectedItem();
  }

  public ColumnHead getColorizerColumnHead() {
    if (0 == coloringColumnHeadComboBox.getSelectedIndex()) {
      return null;
    }
    return (ColumnHead) coloringColumnHeadComboBox.getSelectedItem();
  }

  // @Implements ChangeListener
  @Override
	public void stateChanged(ChangeEvent e) {
    // Colorizer needs stats
    if (e instanceof ColorizerChangeEvent) {
      ColorizerChangeEvent ev = (ColorizerChangeEvent) e;
      if (ev.getSource() != colorizer) {
        throw new RuntimeException("interested only in colorizer");
      }
      // ColorizerViewlet (user) changed functions
      float values[];
      ColumnHead columnHead;
      switch (ev.kind) {
      case PREPROCESSOR_FUNCTION:
        columnHead = (ColumnHead) coloringColumnHeadComboBox.getSelectedItem();
        colorizer.setPreprocessorFunctionStats(selectedPointset
            .getStats(columnHead));
        values = selectedPointset.getColumnUserValuesFor(columnHead);
        colorizer.getPreprocessorFunction().apply(values);
        colorizer.setColoringFunctionStats(new Stats(values));
        break;
      case COLORING_FUNCTION:
        columnHead = (ColumnHead) coloringColumnHeadComboBox.getSelectedItem();
        values = selectedPointset.getColumnUserValuesFor(columnHead);
        colorizer.getPreprocessorFunction().apply(values);
        colorizer.setColoringFunctionStats(new Stats(values));
        break;
      case PREPROCESSOR_FUNCTION_STATS:
      case COLORING_FUNCTION_STATS:
      case COLOR_MAP:
        break;
      }
    }
  }

  public void addChangeListener(ChangeListener listener) {
    listeners.add(listener);
  }

  public void removeChangeListener(ChangeListener listener) {
    listeners.remove(listener);
  }

  private void fireStateChanged(ChangeEvent ev) {
    for (ChangeListener listener : listeners) {
      listener.stateChanged(ev);
    }
  }

  private static final long serialVersionUID = 1L;
}
