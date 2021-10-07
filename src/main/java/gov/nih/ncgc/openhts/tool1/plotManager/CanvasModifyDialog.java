package gov.nih.ncgc.openhts.tool1.plotManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.help.CSH;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import gov.nih.ncgc.openhts.tool1.Palette;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;
import gov.nih.ncgc.openhts.tool1.dialogManager.ColorChooserDialog;
import gov.nih.ncgc.openhts.tool1.dialogManager.DialogManager;

public final class CanvasModifyDialog extends AbstractDialog {
  private final Session session;
  private final Canvas canvas;
  private SpinnerNumberModel widthSpinnerNumberModel, heightSpinnerNumberModel,
      acceptableTimeNumberSpinnerModel, previewTimeSpinnerNumberModel,
      ssRadiusSpinnerNumberModel;
  private JCheckBox subsettingCheckBox, blankDataCheckBox, autoComposeCheckBox,
      fastComposeCheckBox;
  private JSpinner acceptableTimeSpinner, precisionSpinner;
  private JComboBox pixelUnitsWidthComboBox, pixelHeightComboBox;
  private final Dimension dimension = new Dimension(300, 25);

  // TODO: make the modify width text field respond to a mouse responding in
  // Millimeters too
  public CanvasModifyDialog(final Session session) {
    super(session);
    this.session = session;
    this.canvas = session.plotManager.canvas;
    setTitle("Canvas - Modify");
    setModal(false);
    setResizable(false);
    addWindowListener(DialogManager.getInstance(session));
    addComponentListener(DialogManager.getInstance(session));
    canvas.advancedEnabled = false;
    final Box topBox = Box.createVerticalBox();
    add(topBox, BorderLayout.CENTER);
    final Dimension dimension = new Dimension(200, 25);
    final Dimension dimension2 = new Dimension(100, 25);
    final JPanel canvasPanel = new JPanel();
    CSH.setHelpIDString(canvasPanel, "modify_canvas");
    canvasPanel.setLayout(new GridLayout(3, 1));
    // NEXT PANEL: 8
    final JPanel subpanel8 = new JPanel();
    CSH.setHelpIDString(subpanel8, "modify_canvas");
    subpanel8.setLayout(new FlowLayout(FlowLayout.LEFT));
    final JLabel label8 = new JLabel("Background color:", SwingConstants.RIGHT);
    label8.setToolTipText("color of canvas");
    CSH.setHelpIDString(label8, "modify_canvas");
    label8.setPreferredSize(dimension);
    subpanel8.add(label8);
    Palette.Choice choice = Palette.getClosestChoice(canvas.backColor);
    final JButton colorButton = new JButton(choice.toString());
    colorButton.setToolTipText("click to choose the color of the canvas");
    CSH.setHelpIDString(colorButton, "color_modify_canvas_button");
    colorButton.setBorder(BorderFactory.createRaisedBevelBorder());
    colorButton.setBackground(canvas.backColor);
    colorButton.setForeground(choice.getTextColor());
    colorButton.setPreferredSize(dimension2);
    colorButton.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
        try {
          final ColorChooserDialog colorChooserDialog = session.dialogManager
              .getColorChooserModifyDialog();
          colorChooserDialog.setTitle("canvas colors");
          colorChooserDialog.showDialog(); // modal block
          Palette.Choice choice = colorChooserDialog.getChoice();
          canvas.backColor = choice.getColor();
          if (canvas.backColor != null) { // TODO: IS THIS RIGHT?
            colorButton.setBackground(canvas.backColor);
            colorButton.setForeground(choice.getTextColor());
            colorButton.setText(choice.toString());
          }
        }
        catch (final Throwable ex) {
          session.errorSupport("", ex, "color_modify_canvas_button_error");
        }
      }
    });
    subpanel8.add(colorButton);
    canvasPanel.add(subpanel8);
    // NEXT PANEL: ONE
    final JPanel subpanel1 = new JPanel();
    CSH.setHelpIDString(subpanel1, "modify_canvas");
    subpanel1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
    final JLabel label1 = new JLabel("Width:", SwingConstants.RIGHT);
    label1.setToolTipText("horizontal size of canvas");
    CSH.setHelpIDString(label1, "width_horizontal_canvas_label");
    label1.setPreferredSize(dimension);
    subpanel1.add(label1);
    int tmp;
    if (canvas.pixelUnitsWidth) {
      tmp = canvas.w;
    }
    else {
      tmp = (int) (canvas.w / session.dotsPerMM);
    }
    widthSpinnerNumberModel = new SpinnerNumberModel(tmp, 0, Integer.MAX_VALUE,
        50);
    final JSpinner spinner1 = new JSpinner(widthSpinnerNumberModel);
    CSH.setHelpIDString(spinner1, "width_modify_canvas_spinner");
    spinner1.setBorder(BorderFactory.createLoweredBevelBorder());
    spinner1.setPreferredSize(dimension2);
    subpanel1.add(spinner1);
    pixelUnitsWidthComboBox = new JComboBox();
    pixelUnitsWidthComboBox.setToolTipText("click to choose units for width");
    CSH.setHelpIDString(pixelUnitsWidthComboBox,
        "width_units_modify_canvas_combobox");
    pixelUnitsWidthComboBox.setBorder(BorderFactory.createRaisedBevelBorder());
    pixelUnitsWidthComboBox.setPreferredSize(dimension2);
    pixelUnitsWidthComboBox.addItem("Pixels");
    pixelUnitsWidthComboBox.addItem("Millimeters");
    if (canvas.pixelUnitsWidth) {
      pixelUnitsWidthComboBox.setSelectedIndex(0);
    }
    else {
      pixelUnitsWidthComboBox.setSelectedIndex(1);
    }
    pixelUnitsWidthComboBox.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent ev) {
        try {
          if (pixelUnitsWidthComboBox.getSelectedIndex() == 0) {
            canvas.pixelUnitsWidth = true;
          }
          else {
            canvas.pixelUnitsWidth = false;
          }
        }
        catch (final Throwable ex) {
          session.errorSupport("", ex,
              "width_units_modify_canvas_combobox_error");
        }
      }
    });
    subpanel1.add(pixelUnitsWidthComboBox);
    canvasPanel.add(subpanel1);
    // NEXT PANEL: TWO
    final JPanel subpanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
    CSH.setHelpIDString(subpanel2, "modify_canvas");
    final JLabel label2 = new JLabel("Height:", SwingConstants.RIGHT);
    label2.setPreferredSize(dimension);
    label2.setToolTipText("vertical size of canvas");
    CSH.setHelpIDString(label2, "height_units_modify_canvas_label");
    subpanel2.add(label2);
    if (canvas.pixelUnitsWidth) {
      tmp = canvas.h;
    }
    else {
      tmp = (int) (canvas.h / session.dotsPerMM);
    }
    heightSpinnerNumberModel = new SpinnerNumberModel(tmp, 0,
        Integer.MAX_VALUE, 50);
    final JSpinner spinner2 = new JSpinner(heightSpinnerNumberModel);
    CSH.setHelpIDString(spinner2, "height_units_modify_canvas_spinner");
    spinner2.setBorder(BorderFactory.createLoweredBevelBorder());
    spinner2.setPreferredSize(dimension2);
    subpanel2.add(spinner2);
    pixelHeightComboBox = new JComboBox();
    pixelHeightComboBox.setToolTipText("click to choose units of height");
    CSH.setHelpIDString(pixelHeightComboBox,
        "height_units_modify_canvas_combobox");
    pixelHeightComboBox.setBorder(BorderFactory.createRaisedBevelBorder());
    pixelHeightComboBox.setPreferredSize(dimension2);
    pixelHeightComboBox.addItem("Pixels");
    pixelHeightComboBox.addItem("Millimeters");
    if (canvas.pixelUnitsHeight) {
      pixelHeightComboBox.setSelectedIndex(0);
    }
    else {
      pixelHeightComboBox.setSelectedIndex(1);
    }
    pixelHeightComboBox.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent ev) {
        try {
          if (pixelHeightComboBox.getSelectedIndex() == 0) {
            canvas.pixelUnitsHeight = true;
          }
          else {
            canvas.pixelUnitsHeight = false;
          }
        }
        catch (final Throwable ex) {
          session.errorSupport("", ex,
              "height_units_modify_canvas_combobox_error");
        }
      }
    });
    subpanel2.add(pixelHeightComboBox);
    canvasPanel.add(subpanel2);
    topBox.add(canvasPanel); // 0
    topBox.add(new JSeparator()); // 1
    CSH.setHelpIDString(topBox, "modify_canvas");
    acceptableTimeNumberSpinnerModel = new SpinnerNumberModel(
        canvas.acceptableTime, 0.0, Float.POSITIVE_INFINITY, 20.0);
    previewTimeSpinnerNumberModel = new SpinnerNumberModel(canvas.previewTimer
        .getDelay(), 0.0, Float.POSITIVE_INFINITY, 100.0);
    ssRadiusSpinnerNumberModel = new SpinnerNumberModel(canvas.ssRadius, 0,
        Integer.MAX_VALUE, 1);
    addAdvancedButtonPanel(topBox);
    setHelpID("help_modify_canvas_button");
    add(getButtonPanel(), BorderLayout.SOUTH);
  } // end of modify()

  private void addAdvancedButtonPanel(final Box vbox) {
    final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
    CSH.setHelpIDString(buttonPanel, "advanced_modify_canvas");
    buttonPanel.setPreferredSize(new Dimension(400, 60));
    final JLabel label1 = new JLabel("Advanced features setup",
        SwingConstants.RIGHT);
    label1.setToolTipText("more properties that can be modified");
    CSH.setHelpIDString(label1, "advanced_modify_canvas_label");
    label1.setPreferredSize(new Dimension(305, 30));
    buttonPanel.add(label1);
    final JButton advanced = new JButton("Advanced...");
    advanced.setToolTipText("click to see advanced properties");
    CSH.setHelpIDString(advanced, "advanced_modify_canvas_button");
    advanced.setPreferredSize(new Dimension(100, 30));
    advanced.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent ev) {
        canvas.advancedEnabled = true;
        vbox.remove(buttonPanel);
        removeAdvancedButtonPanel(vbox);
        addAdvancedPanel(vbox);
        pack();
      }
    });
    buttonPanel.add(advanced, BorderLayout.EAST);
    vbox.add(buttonPanel, 2);
  }

  private void removeAdvancedButtonPanel(final Box vbox) {
    final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
    CSH.setHelpIDString(buttonPanel, "advanced_modify_canvas");
    buttonPanel.setPreferredSize(new Dimension(400, 60));
    final JLabel label = new JLabel("Back to normal setup",
        SwingConstants.RIGHT);
    label.setToolTipText("advanced properties can be hidden");
    CSH.setHelpIDString(label, "advanced_modify_canvas_label");
    label.setPreferredSize(new Dimension(305, 30));
    buttonPanel.add(label);
    final JButton back = new JButton("Back...");
    back.setToolTipText("click to hide the advanced properties");
    CSH.setHelpIDString(back, "back_modify_canvas_button");
    back.setPreferredSize(new Dimension(100, 30));
    back.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent ev) {
        try {
          vbox.remove(6);
          vbox.remove(5);
          vbox.remove(4);
          vbox.remove(3);
          vbox.remove(buttonPanel);
          addAdvancedButtonPanel(vbox);
          pack();
        }
        catch (final Throwable ex) {
          session.errorSupport("", ex, "back_modify_canvas_error");
        }
      }
    });
    buttonPanel.add(back, BorderLayout.EAST);
    vbox.add(buttonPanel, 2);
  }

  @Override
  protected boolean apply() {
    // ignore any timers going off (weird bug)
    canvas.w = ((Integer) widthSpinnerNumberModel.getValue()).intValue();
    canvas.h = ((Integer) heightSpinnerNumberModel.getValue()).intValue();
    //
    if (pixelUnitsWidthComboBox.getSelectedIndex() == 0) {
      canvas.w = ((Integer) widthSpinnerNumberModel.getValue()).intValue();
      canvas.pixelUnitsWidth = true;
    }
    else {
      canvas.w = (int) (((Integer) widthSpinnerNumberModel.getValue())
          .floatValue() * session.dotsPerMM);
      canvas.pixelUnitsWidth = false;
    }
    if (pixelHeightComboBox.getSelectedIndex() == 0) {
      canvas.h = ((Integer) heightSpinnerNumberModel.getValue()).intValue();
      canvas.pixelUnitsHeight = true;
    }
    else {
      canvas.h = (int) (((Integer) heightSpinnerNumberModel.getValue())
          .floatValue() * session.dotsPerMM);
      canvas.pixelUnitsHeight = false;
    }
    //
    if (canvas.advancedEnabled) {
      canvas.acceptableTime = ((Double) acceptableTimeNumberSpinnerModel
          .getValue()).intValue();
      canvas.ssRadius = ((Integer) ssRadiusSpinnerNumberModel.getValue())
          .intValue();
      // why doesn't this work
      canvas.previewTimer.setDelay(((Double) previewTimeSpinnerNumberModel
          .getValue()).intValue());
      canvas.previewTimer = new Timer(((Double) previewTimeSpinnerNumberModel
          .getValue()).intValue(), canvas); // setDelay() bug work around
      canvas.previewTimer.restart();
      if (subsettingCheckBox != null) {
        canvas.enableSubsetting(subsettingCheckBox.isSelected());
      }
      if (blankDataCheckBox != null) {
        canvas.blankData = blankDataCheckBox.isSelected();
        canvas.autoCompose = autoComposeCheckBox.isSelected();
        canvas.fastCompose = fastComposeCheckBox.isSelected();
      }
    } // end if
    canvas.setStale(true);
    canvas.internalResize(); // may or may not refresh
    session.frame.setBackground(canvas.backColor);
    return true;
  }

  private void addAdvancedPanel(final Box vbox) {
    vbox.add(new JSeparator(), 3);
    vbox.add(getSubsettingPanel(), 4);
    vbox.add(getComposePanel(), 5);
    vbox.add(getSelectionPanel(), 6);
  }

  private JPanel getSubsettingPanel() {
    final JPanel subsettingPanel = new JPanel();
    CSH.setHelpIDString(subsettingPanel, "advanced_modify_canvas");
    subsettingPanel.setBorder(BorderFactory.createTitledBorder("Subsetting"));
    subsettingPanel.setLayout(new GridLayout(3, 1, 5, 5));
    final JPanel checkBoxPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5,
        0));
    subsettingCheckBox = new JCheckBox(
        "Activate subsetting on mouse rotations/translations");
    subsettingCheckBox
        .setToolTipText("if checked, a smaller amount of data will be drawn");
    subsettingCheckBox.setSelected(canvas.subsetting);
    subsettingCheckBox.setBackground(subsettingPanel.getBackground());
    checkBoxPanel1.add(subsettingCheckBox);
    subsettingPanel.add(checkBoxPanel1);
    final JPanel checkBoxPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5,
        0));
    blankDataCheckBox = new JCheckBox(
        "Blank data on rotation (faster for large data sources)");
    blankDataCheckBox
        .setToolTipText("if checked, no data will be drawn during mouse rotaton and translation");
    blankDataCheckBox.setBackground(checkBoxPanel2.getBackground());
    blankDataCheckBox.setSelected(canvas.blankData);
    checkBoxPanel2.add(blankDataCheckBox);
    subsettingPanel.add(checkBoxPanel2);
    final JPanel subpanel3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    final JLabel label3 = new JLabel("Maximum acceptable drawing time (msec):",
        SwingConstants.RIGHT);
    label3.setToolTipText("the longest tolerable time to draw one frame");
    label3.setPreferredSize(dimension);
    subpanel3.add(label3);
    acceptableTimeSpinner = new JSpinner(acceptableTimeNumberSpinnerModel);
    acceptableTimeSpinner.setBorder(BorderFactory.createLoweredBevelBorder());
    acceptableTimeSpinner.setPreferredSize(new Dimension(100, 25));
    subsettingCheckBox.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
        try {
          canvas.enableSubsetting(subsettingCheckBox.isSelected());
        }
        catch (final Throwable ex) {
          session.errorSupport("", ex, "draw_time_modify_canvas_spinner_error");
        }
      }
    });
    subpanel3.add(acceptableTimeSpinner);
    subpanel3.add(Box.createRigidArea(new Dimension(0, 5)));
    subsettingPanel.add(subpanel3);
    return subsettingPanel;
  }

  private JPanel getComposePanel() {
    final JPanel composePanel = new JPanel();
    composePanel.setBorder(BorderFactory
        .createTitledBorder("Fitting to canvas"));
    composePanel.setLayout(new GridLayout(2, 1, 5, 5));
    composePanel.setBorder(BorderFactory.createTitledBorder("Fit to canvas"));
    composePanel.setLayout(new GridLayout(1, 1, 5, 5));
    // NEXT PANEL: SEVEN
    final JPanel subpanel7 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    subpanel7.add(new JSeparator());
    autoComposeCheckBox = new JCheckBox("Automatic");
    autoComposeCheckBox
        .setToolTipText("if unchecked, you must trigger fit to canvas");
    autoComposeCheckBox.setBackground(subpanel7.getBackground());
    subpanel7.add(autoComposeCheckBox);
    autoComposeCheckBox.setSelected(canvas.autoCompose);
    subpanel7.add(autoComposeCheckBox);
    fastComposeCheckBox = new JCheckBox("Fast");
    fastComposeCheckBox
        .setToolTipText("if checked, approximates the fit to screen");
    fastComposeCheckBox.setBackground(subpanel7.getBackground());
    subpanel7.add(fastComposeCheckBox);
    fastComposeCheckBox.setSelected(canvas.fastCompose);
    subpanel7.add(fastComposeCheckBox);
    composePanel.add(subpanel7);
    return composePanel;
  }

  private JPanel getSelectionPanel() {
    final JPanel selectionPanel = new JPanel();
    selectionPanel.setBorder(BorderFactory
        .createTitledBorder("Mouse selection"));
    selectionPanel.setLayout(new GridLayout(3, 1, 5, 5));
    final JPanel precisionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5,
        0));
    selectionPanel.add(precisionPanel);
    precisionPanel.add(new JSeparator());
    final JLabel precisionLabel = new JLabel("Precision (pixels): ",
        SwingConstants.RIGHT);
    precisionLabel.setPreferredSize(dimension);
    precisionPanel.add(precisionLabel);
    precisionSpinner = new JSpinner(ssRadiusSpinnerNumberModel);
    precisionSpinner.setBorder(BorderFactory.createLoweredBevelBorder());
    precisionSpinner.setPreferredSize(new Dimension(100, 25));
    precisionPanel.add(precisionSpinner);
    precisionPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    //
    final JCheckBox previewCheckBox = new JCheckBox("Preview before selecting");
    selectionPanel.add(previewCheckBox);
    previewCheckBox.setSelected(canvas.preview);
    //
    final JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    selectionPanel.add(timePanel);
    timePanel.add(new JSeparator());
    final JLabel timeLabel = new JLabel(
        "Hover time before selection preview (msec): ", SwingConstants.RIGHT);
    timeLabel.setToolTipText("time before the name of the data pops up");
    timeLabel.setPreferredSize(dimension);
    timePanel.add(timeLabel);
    final JSpinner timeSpinner = new JSpinner(previewTimeSpinnerNumberModel);
    timeSpinner.setEnabled(previewCheckBox.isSelected());
    previewCheckBox.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(ActionEvent e) {
        canvas.setPreview(previewCheckBox.isSelected());
        timeSpinner.setEnabled(previewCheckBox.isSelected());
      }
    });
    timeSpinner.setBorder(BorderFactory.createLoweredBevelBorder());
    timeSpinner.setPreferredSize(new Dimension(100, 25));
    timePanel.add(timeSpinner);
    timePanel.add(Box.createRigidArea(new Dimension(0, 5)));
    return selectionPanel;
  }

  private static final long serialVersionUID = 1L;
}
