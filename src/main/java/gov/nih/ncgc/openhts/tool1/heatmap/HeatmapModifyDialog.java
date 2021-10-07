package gov.nih.ncgc.openhts.tool1.heatmap;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import gov.nih.ncgc.openhts.tool1.Palette;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;
import gov.nih.ncgc.openhts.tool1.dialogManager.ColorChooserDialog;

public final class HeatmapModifyDialog extends AbstractDialog implements
    HeatmapListener {
  private final Session session;
  private final Heatmap heatmap;
  private SpinnerNumberModel lineThicknessSpinnerNumberModel,
      rowHeightSpinnerNumberModel, columnWidthSpinnerNumberModel,
      previewDelaySpinnerNumberModel, magSpinnerNumberModel,
      magStepSpinnerNumberModel, panStepSpinnerNumberModel;
  private JComboBox lineThicknessUnitsComboBox, rowHeightUnitsComboBox,
      columnWidthUnitsComboBox;
  private final Dimension spinnerDimension = new Dimension(100, 25);
  private boolean advancedEnabled = false;
  private JPanel advancedPanel;
  private JLabel advancedLabel;
  private JButton advancedButton;
  private JCheckBox drawLineCheckBox;

  public HeatmapModifyDialog(final Session session) {
    super(session);
    this.session = session;
    this.heatmap = session.heatmap;
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    add(topPanel, BorderLayout.CENTER);
    setTitle("Heatmap - Modify");
    setModal(false);
    advancedEnabled = false;
    //
    JPanel leftPanel = new JPanel();
    topPanel.add(leftPanel);
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
    leftPanel.add(createBasicPanel());
    leftPanel.add(createAdvancedButtonPanel());
    leftPanel.add(createAdvancedPanel());
    add(getButtonPanel(), BorderLayout.SOUTH);
    //
    JPanel rightPanel = new JPanel(new BorderLayout());
    topPanel.add(rightPanel);
    rightPanel.add(heatmap.colorizerViewlet, BorderLayout.CENTER);
    final JCheckBox checkbox = new JCheckBox("Colorize by column");
    checkbox.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(ActionEvent e) {
        heatmap.colorizeByColumn = checkbox.isSelected();
      }
    });
    checkbox.setSelected(heatmap.colorizeByColumn);
    rightPanel.add(checkbox, BorderLayout.NORTH);
    heatmap.addHeatmapListener(this);
  }

  private JPanel createAdvancedPanel() {
    advancedPanel = new JPanel();
    advancedPanel.setLayout(new BoxLayout(advancedPanel, BoxLayout.PAGE_AXIS));
    advancedPanel.add(createPanZoomPanel());
    advancedPanel.add(createSelectionPanel());
    advancedPanel.setVisible(false);
    return advancedPanel;
  }

  private JPanel createBasicPanel() {
    final JPanel basicPanel = new JPanel();
    basicPanel.setLayout(new BoxLayout(basicPanel, BoxLayout.PAGE_AXIS));
    basicPanel.add(createLayoutPanel());
    return basicPanel;
  }

  private JPanel createLayoutPanel() {
    JPanel layoutPanel = new JPanel();
    layoutPanel.setBorder(BorderFactory.createTitledBorder("Layout"));
    layoutPanel.setLayout(new BoxLayout(layoutPanel, BoxLayout.PAGE_AXIS));
    layoutPanel.add(getColumnWidthPanel());
    layoutPanel.add(getRowHeightPanel());
    layoutPanel.add(getLinePanel());
    return layoutPanel;
  }

  private JPanel getLinePanel() {
    JPanel linePanel = new JPanel();
    // linePanel.setBorder(BorderFactory.createTitledBorder("Lines"));
    linePanel.setLayout(new BoxLayout(linePanel, BoxLayout.PAGE_AXIS));
    JPanel drawLinesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
    linePanel.add(drawLinesPanel);
    drawLineCheckBox = new JCheckBox("Draw lines between cells");
    drawLineCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    drawLineCheckBox.setSelected(heatmap.drawLine);
    drawLinesPanel.add(drawLineCheckBox);
    //
    final JLabel lineColorLabel = new JLabel("Line color:",
        SwingConstants.RIGHT);
    drawLinesPanel.add(lineColorLabel);
    Palette.Choice choice = Palette.getClosestChoice(heatmap.lineColor);
    final JButton colorButton = new JButton(choice.toString());
    drawLinesPanel.add(colorButton);
    colorButton.setBackground(heatmap.lineColor);
    colorButton.setForeground(choice.getTextColor());
    colorButton.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
        try {
          final ColorChooserDialog colorChooserDialog = session.dialogManager
              .getColorChooserModifyDialog();
          colorChooserDialog.setTitle("canvas colors");
          colorChooserDialog.showDialog(); // modal block
          Palette.Choice choice = colorChooserDialog.getChoice();
          heatmap.lineColor = choice.getColor();
          if (heatmap.lineColor != null) { // TODO: IS THIS RIGHT?
            colorButton.setBackground(heatmap.lineColor);
            colorButton.setForeground(choice.getTextColor());
            colorButton.setText(choice.toString());
          }
        }
        catch (final Throwable ex) {
          session.errorSupport("", ex, "todo");
        }
      }
    });
    //              
    final JPanel lineThicknessPanel = new JPanel(new FlowLayout(
        FlowLayout.RIGHT, 5, 5));
    linePanel.add(lineThicknessPanel);
    final JLabel lineThicknessLabel = new JLabel("Gap between cells: ",
        SwingConstants.RIGHT);
    lineThicknessPanel.add(lineThicknessLabel);
    int tmp;
    if (heatmap.pixelUnitsLineThickness) {
      tmp = heatmap.lineThickness;
    }
    else {
      tmp = (int) (heatmap.lineThickness / session.dotsPerMM);
    }
    lineThicknessSpinnerNumberModel = new SpinnerNumberModel(tmp, 0,
        Double.MAX_VALUE, 0.25);
    final JSpinner lineThicknessSpinner = new JSpinner(
        lineThicknessSpinnerNumberModel);
    lineThicknessSpinner.setBorder(BorderFactory.createLoweredBevelBorder());
    lineThicknessSpinner.setPreferredSize(spinnerDimension);
    lineThicknessPanel.add(lineThicknessSpinner);
    lineThicknessPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    lineThicknessUnitsComboBox = new JComboBox();
    lineThicknessUnitsComboBox.setBorder(BorderFactory
        .createRaisedBevelBorder());
    lineThicknessUnitsComboBox.addItem("Pixels");
    lineThicknessUnitsComboBox.addItem("Millimeters");
    if (heatmap.pixelUnitsLineThickness) {
      lineThicknessUnitsComboBox.setSelectedIndex(0);
    }
    else {
      lineThicknessUnitsComboBox.setSelectedIndex(1);
    }
    lineThicknessUnitsComboBox.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent ev) {
        try {
          if (lineThicknessUnitsComboBox.getSelectedIndex() == 0) {
            heatmap.pixelUnitsLineThickness = true;
          }
          else {
            heatmap.pixelUnitsLineThickness = false;
          }
        }
        catch (final Throwable ex) {
          session.errorSupport("", ex, "todo");
        }
      }
    });
    lineThicknessPanel.add(lineThicknessUnitsComboBox);
    //
    colorButton.setEnabled(drawLineCheckBox.isSelected());
    drawLineCheckBox.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(ActionEvent e) {
        colorButton.setEnabled(drawLineCheckBox.isSelected());
      }
    });
    return linePanel;
  }

  private JPanel getRowHeightPanel() {
    //
    final JPanel rowHeightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,
        5, 5));
    final JLabel rowHeightLabel = new JLabel("Row height:",
        SwingConstants.RIGHT);
    // rowHeightLabel.setPreferredSize(dimension);
    rowHeightPanel.add(rowHeightLabel);
    int tmp;
    if (heatmap.pixelUnitsRowHeight) {
      tmp = heatmap.rowHeight;
    }
    else {
      tmp = (int) (heatmap.rowHeight / session.dotsPerMM);
    }
    rowHeightSpinnerNumberModel = new SpinnerNumberModel(tmp, 0,
        Integer.MAX_VALUE, 0.25);
    final JSpinner rowHeightSpinner = new JSpinner(rowHeightSpinnerNumberModel);
    rowHeightSpinner.setBorder(BorderFactory.createLoweredBevelBorder());
    rowHeightSpinner.setPreferredSize(spinnerDimension);
    rowHeightPanel.add(rowHeightSpinner);
    rowHeightUnitsComboBox = new JComboBox();
    rowHeightUnitsComboBox.setBorder(BorderFactory.createRaisedBevelBorder());
    rowHeightUnitsComboBox.addItem("Pixels");
    rowHeightUnitsComboBox.addItem("Millimeters");
    if (heatmap.pixelUnitsRowHeight) {
      rowHeightUnitsComboBox.setSelectedIndex(0);
    }
    else {
      rowHeightUnitsComboBox.setSelectedIndex(1);
    }
    rowHeightUnitsComboBox.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent ev) {
        try {
          if (rowHeightUnitsComboBox.getSelectedIndex() == 0) {
            heatmap.pixelUnitsRowHeight = true;
          }
          else {
            heatmap.pixelUnitsRowHeight = false;
          }
        }
        catch (final Throwable ex) {
          session.errorSupport("", ex, "todo");
        }
      }
    });
    rowHeightPanel.add(rowHeightUnitsComboBox);
    return rowHeightPanel;
  }

  private JPanel getColumnWidthPanel() {
    final JPanel columnWidthPanel = new JPanel();
    columnWidthPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
    final JLabel columnWidthLabel = new JLabel("Column width:",
        SwingConstants.RIGHT);
    columnWidthPanel.add(columnWidthLabel);
    int tmp;
    if (heatmap.pixelUnitsColumnWidth) {
      tmp = heatmap.colWidth;
    }
    else {
      tmp = (int) (heatmap.colWidth / session.dotsPerMM);
    }
    columnWidthSpinnerNumberModel = new SpinnerNumberModel(tmp, 0,
        Double.MAX_VALUE, 0.25);
    final JSpinner columnWidthSpinner = new JSpinner(
        columnWidthSpinnerNumberModel);
    columnWidthSpinner.setBorder(BorderFactory.createLoweredBevelBorder());
    columnWidthSpinner.setPreferredSize(spinnerDimension);
    columnWidthPanel.add(columnWidthSpinner);
    columnWidthUnitsComboBox = new JComboBox();
    columnWidthUnitsComboBox.setBorder(BorderFactory.createRaisedBevelBorder());
    // lineThicknessUnitsComboBox.setPreferredSize(dimension2);
    columnWidthUnitsComboBox.addItem("Pixels");
    columnWidthUnitsComboBox.addItem("Millimeters");
    if (heatmap.pixelUnitsColumnWidth) {
      columnWidthUnitsComboBox.setSelectedIndex(0);
    }
    else {
      columnWidthUnitsComboBox.setSelectedIndex(1);
    }
    columnWidthUnitsComboBox.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent ev) {
        try {
          if (columnWidthUnitsComboBox.getSelectedIndex() == 0) {
            heatmap.pixelUnitsColumnWidth = true;
          }
          else {
            heatmap.pixelUnitsColumnWidth = false;
          }
        }
        catch (final Throwable ex) {
          session.errorSupport("", ex, "todo");
        }
      }
    });
    columnWidthPanel.add(columnWidthUnitsComboBox);
    return columnWidthPanel;
  }

  private JPanel createPanZoomPanel() {
    JPanel panZoomPanel = new JPanel();
    panZoomPanel.setLayout(new BoxLayout(panZoomPanel, BoxLayout.PAGE_AXIS));
    panZoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom/pan"));
    JPanel magPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    final JLabel magLabel = new JLabel("Magnification:", SwingConstants.RIGHT);
    panZoomPanel.add(magPanel);
    magPanel.add(magLabel);
    magSpinnerNumberModel = new SpinnerNumberModel(heatmap.magnification, 0.0,
        Float.POSITIVE_INFINITY, heatmap.magStep);
    JSpinner magSpinner = new JSpinner(magSpinnerNumberModel);
    magPanel.add(magSpinner);
    magSpinner.setPreferredSize(spinnerDimension);
    //
    JPanel magStepPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panZoomPanel.add(magStepPanel);
    final JLabel magStepLabel = new JLabel("Magnification step:",
        SwingConstants.RIGHT);
    magStepPanel.add(magStepLabel);
    magStepSpinnerNumberModel = new SpinnerNumberModel(heatmap.magStep, 0.0,
        Float.POSITIVE_INFINITY, 0.1);
    JSpinner magStepSpinner = new JSpinner(magStepSpinnerNumberModel);
    magStepPanel.add(magStepSpinner);
    magStepSpinner.setPreferredSize(spinnerDimension);
    //
    JPanel panStepPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panZoomPanel.add(panStepPanel);
    final JLabel panStepLabel = new JLabel("Pan step:", SwingConstants.RIGHT);
    panStepPanel.add(panStepLabel);
    panStepSpinnerNumberModel = new SpinnerNumberModel(heatmap.panStep, 0.0,
        Float.POSITIVE_INFINITY, 0.1);
    JSpinner panStepSpinner = new JSpinner(panStepSpinnerNumberModel);
    panStepPanel.add(panStepSpinner);
    panStepSpinner.setPreferredSize(spinnerDimension);
    return panZoomPanel;
  }

  private JPanel createAdvancedButtonPanel() {
    final JPanel advancedButtonPanel = new JPanel(new FlowLayout(
        FlowLayout.RIGHT, 5, 5));
    final String advancedString = "Advanced features setup";
    final String advancedButtonString = "Advanced button string";
    advancedLabel = new JLabel(advancedString, SwingConstants.RIGHT);
    advancedButtonPanel.add(advancedLabel);
    advancedButton = new JButton("Advanced...");
    advancedButtonPanel.add(advancedButton);
    advancedButton.setPreferredSize(new Dimension(100, 25));
    advancedButton.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent ev) {
        if (advancedEnabled) {
          advancedPanel.setVisible(false);
          advancedLabel.setText(advancedString);
          advancedButton.setText(advancedButtonString);
          advancedEnabled = false;
        }
        else {
          advancedPanel.setVisible(true);
          advancedLabel.setText("Back to normal setup");
          advancedButton.setText("Back...");
          advancedEnabled = true;
        }
        pack();
      }
    });
    return advancedButtonPanel;
  }

  private JPanel createSelectionPanel() {
    final JPanel selectionPanel = new JPanel();
    selectionPanel.setBorder(BorderFactory
        .createTitledBorder("Mouse selection"));
    selectionPanel.setLayout(new GridLayout(3, 1, 5, 5));
    //
    final JPanel previewPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5,
        0));
    selectionPanel.add(previewPanel);
    final JCheckBox previewCheckBox = new JCheckBox("Preview before selecting");
    previewCheckBox.setSelected(heatmap.preview);
    previewPanel.add(previewCheckBox);
    //
    final JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
    selectionPanel.add(timePanel);
    timePanel.add(new JSeparator());
    final JLabel previewDelayLabel = new JLabel(
        "Hover time before selection preview (msec): ", SwingConstants.RIGHT);
    timePanel.add(previewDelayLabel);
    previewDelaySpinnerNumberModel = new SpinnerNumberModel();
    previewDelaySpinnerNumberModel.setValue(heatmap.previewDelay);
    final JSpinner previewDelaySpinner = new JSpinner(
        previewDelaySpinnerNumberModel);
    previewDelaySpinner.setEnabled(previewCheckBox.isSelected());
    previewCheckBox.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(ActionEvent e) {
        heatmap.preview = previewCheckBox.isSelected();
        previewDelaySpinner.setEnabled(previewCheckBox.isSelected());
      }
    });
    previewDelaySpinner.setBorder(BorderFactory.createLoweredBevelBorder());
    previewDelaySpinner.setPreferredSize(spinnerDimension);
    timePanel.add(previewDelaySpinner);
    timePanel.add(Box.createRigidArea(new Dimension(0, 5)));
    return selectionPanel;
  }

  @Override
  protected boolean apply() {
    heatmap.drawLine = drawLineCheckBox.isSelected();
    heatmap.lineThickness = ((Double) lineThicknessSpinnerNumberModel
        .getValue()).intValue();
    if (lineThicknessUnitsComboBox.getSelectedIndex() == 0) {
      heatmap.pixelUnitsLineThickness = true;
    }
    else {
      heatmap.lineThickness *= session.dotsPerMM;
      heatmap.pixelUnitsLineThickness = false;
    }
    //
    heatmap.colWidth = ((Double) columnWidthSpinnerNumberModel.getValue())
        .intValue();
    if (columnWidthUnitsComboBox.getSelectedIndex() == 0) {
      heatmap.pixelUnitsColumnWidth = true;
    }
    else {
      heatmap.colWidth *= session.dotsPerMM;
      heatmap.pixelUnitsColumnWidth = false;
    }
    //
    heatmap.rowHeight = ((Double) rowHeightSpinnerNumberModel.getValue())
        .intValue();
    if (rowHeightUnitsComboBox.getSelectedIndex() == 0) {
      heatmap.pixelUnitsLineThickness = true;
    }
    else {
      heatmap.rowHeight *= session.dotsPerMM;
      heatmap.pixelUnitsLineThickness = false;
    }
    //
    if (advancedEnabled) {
      // why doesn't this work
      heatmap.previewTimer.setDelay(((Integer) previewDelaySpinnerNumberModel
          .getValue()).intValue());
      // heatmap.previewTimer = new Timer(((Double)
      // previewDelaySpinnerNumberModel.getValue()).intValue(),
      // canvas); // setDelay() bug work around
      // heatmap.previewTimer.restart();
      heatmap.magnification = ((Double) magSpinnerNumberModel.getValue())
          .floatValue();
      heatmap.magStep = ((Double) magStepSpinnerNumberModel.getValue())
          .floatValue();
      heatmap.panStep = ((Double) panStepSpinnerNumberModel.getValue())
          .floatValue();
    }
    heatmap.heatmapCanvas.applyMag();
    // TODO: init colorizer only if needed
    heatmap.rebuild();
     return true;
  }

  @Override
	public void heatmapChanged(HeatmapEvent ev) {
    switch (ev.kind) {
    case MANAGER_CHANGED:
      magSpinnerNumberModel.setValue(heatmap.magnification);
      break;
    }
  }

  private static final long serialVersionUID = 1L;
}
