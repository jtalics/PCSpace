package gov.nih.ncgc.openhts.tool1.plotManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import gov.nih.ncgc.openhts.tool1.Palette;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;
import gov.nih.ncgc.openhts.tool1.dialogManager.ColorChooserDialog;

public class AxesModifyDialog extends AbstractDialog {
  private JCheckBox gridlinesCheckBox;
  private JComboBox comboBox1, comboBox2;
  private JLabel zlabel, zlabel2;
  private JSpinner spinner4c, spinner5c, spinner6c, spinner7c;
  private SpinnerNumberModel model4a, model4b, model4c, model5a, model5b,
      model5c, model6a, model6b, model6c, model7a, model7b, model7c, model12,
      model13;
  private JButton axisColorButton, labelColorButton;
  // private final JButton ticColorButton;
  private final Session session;

  public AxesModifyDialog(final Session session) {
    super(session);
    this.session = session;
    setTitle("Axes - Modify");
    setModal(false);
    setResizable(false);
    addWindowListener(session.dialogManager);
    addComponentListener(session.dialogManager);
    final JPanel topPanel = new JPanel();
    add(topPanel, BorderLayout.CENTER);
    CSH.setHelpIDString(topPanel, "modify_axes");
    topPanel.setLayout(new BorderLayout());
    topPanel.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.WEST);
    topPanel.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.EAST);
    topPanel.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.NORTH);
    final Box vbox = Box.createVerticalBox();
    CSH.setHelpIDString(vbox, "axes_modify_axes");
    // START AXES SECTION
    final Box axisPanel = Box.createVerticalBox();
    axisPanel.setBorder(BorderFactory.createTitledBorder("Axes"));
    axisPanel
        .setToolTipText("change properties of axes in this section of dialog");
    CSH.setHelpIDString(axisPanel, "axes_modify_axes");
    final Dimension dimension = new Dimension(120, 25);
    final Dimension d = new Dimension(120, 25);
    // NEXT PANEL: ONE
    final JPanel firstPanel = new JPanel();
    CSH.setHelpIDString(firstPanel, "axes_modify_axes");
    firstPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    final JPanel subpanel1 = new JPanel();
    CSH.setHelpIDString(subpanel1, "axes_modify_axes");
    subpanel1.setLayout(new FlowLayout(FlowLayout.LEFT));
    final JLabel label1 = new JLabel("Dimensionality:", SwingConstants.RIGHT);
    label1.setToolTipText("dimensionality of axes (2D or 3D)");
    CSH.setHelpIDString(label1, "dimensionality_axes_modify_axes_label");
    label1.setPreferredSize(dimension);
    firstPanel.add(label1);
    comboBox1 = new JComboBox();
    comboBox1.setToolTipText("click to set dimensionality of axes to 2D or 3D");
    CSH.setHelpIDString(comboBox1, "dimensionality_axes_modify_axes_combobox");
    comboBox1.setPreferredSize(d);
    comboBox1.setBorder(BorderFactory.createRaisedBevelBorder());
    comboBox1.addItem("2D");
    comboBox1.addItem("3D"); // note: dim set below after dialog made
    if (session.plotManager.getDim() == 2) {
      comboBox1.setSelectedIndex(0);
    }
    else if (session.plotManager.getDim() == 3) {
      comboBox1.setSelectedIndex(1);
    }
    else {
      throw new RuntimeException("bad dim");
    }
    comboBox1.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent actionEvent) {
        try {
          changeDimensionality(); // so that dialog can dis/enable components
        }
        catch (final Throwable ex) {
          session
              .errorHelp(
                  "Cannot convert to 3D since at least one data \nsource contains only 2 columns in its table.",
                  "dimensionality_axes_modify_combobox_error");
          comboBox1.setSelectedIndex(0);
        }
      }
    });
    firstPanel.add(comboBox1);
    final JLabel label2 = new JLabel("Style:", SwingConstants.RIGHT);
    label2.setToolTipText("style of axes");
    CSH.setHelpIDString(label2, "style_axes_modify_axes_label");
    label2.setPreferredSize(d);
    firstPanel.add(label2);
    final String styleLabels[] = { "Full Box", "Along Min Only" /*
                                                                 * , IGNORE FOR
                                                                 * 1.0 RELEASE
                                                                 * "Along Max
                                                                 * Only"
                                                                 */};
    comboBox2 = new JComboBox(styleLabels);
    comboBox2.setToolTipText("click to choose style of axes");
    CSH.setHelpIDString(comboBox2, "style_axes_modify_axes_combobox");
    comboBox2.setPreferredSize(d);
    comboBox2.setBorder(BorderFactory.createRaisedBevelBorder());
    firstPanel.add(comboBox2);
    axisPanel.add(firstPanel);
    final int x = 100, y = 25;
    final JPanel secondPanel = new JPanel();
    CSH.setHelpIDString(secondPanel, "axes_modify_axes");
    secondPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    // NEXT PANEL: TWELVE
    final JLabel label12a = new JLabel("Line width (mm):", SwingConstants.RIGHT);
    label12a
        .setToolTipText("width of the axes as drawn on the canvas in millileters");
    CSH.setHelpIDString(label12a, "line_width_axes_modify_axes");
    label12a.setPreferredSize(dimension);
    secondPanel.add(label12a);
    model12 = new SpinnerNumberModel(
        session.plotManager.subject.axes.lineWidthMM, Float.NEGATIVE_INFINITY,
        Float.POSITIVE_INFINITY, 0.25);
    final JSpinner spinner12 = new JSpinner(model12);
    CSH.setHelpIDString(spinner12, "line_width_axes_modify_axes_spinner");
    spinner12.setPreferredSize(d);
    spinner12.setBorder(BorderFactory.createLoweredBevelBorder());
    secondPanel.add(spinner12);
    final JPanel subpanel11 = new JPanel();
    CSH.setHelpIDString(subpanel11, "axes_modify_axes");
    subpanel11.setLayout(new FlowLayout(FlowLayout.LEFT));
    final JLabel label11 = new JLabel("Color:", SwingConstants.RIGHT);
    label11.setToolTipText("color of axes and tic marks");
    CSH.setHelpIDString(label11, "color_axes_modify_axes_label");
    label11.setPreferredSize(d);
    secondPanel.add(label11);
    Palette.Choice choice = Palette.getClosestChoice(session.plotManager.subject.axes.color);
    axisColorButton = new JButton(choice.toString());
    axisColorButton
        .setToolTipText("click to choose color of axes and tic marks");
    CSH.setHelpIDString(axisColorButton, "color_axes_modify_axes_button");
    axisColorButton.setPreferredSize(d);
    axisColorButton.setBorder(BorderFactory.createRaisedBevelBorder());
    axisColorButton.setBackground(session.plotManager.subject.axes.color);
    axisColorButton.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
        try {
          final ColorChooserDialog colorChooserDialog = session.dialogManager
              .getColorChooserModifyDialog();
          colorChooserDialog.setChoice(Palette.getClosestChoice(session.plotManager.subject.axes.color));
          colorChooserDialog.setModal(true);
          colorChooserDialog.showDialog();
          if (colorChooserDialog.isOk()) {
            final Palette.Choice choice = colorChooserDialog.getChoice();
            axisColorButton.setBackground(choice.getColor());
            axisColorButton.setText(choice.toString());
            axisColorButton.setForeground(choice.getColor());
            axisColorButton.invalidate();
            axisColorButton.repaint();
            axisColorButton.validate();
          }
        }
        catch (final Throwable ex) {
          session.errorSupport("While changing color: ", ex,
              "color_axes_modify_axes_button_error");
        }
      }
    });
    secondPanel.add(axisColorButton);
    axisPanel.add(secondPanel);
    final JPanel axisLabel = new JPanel();
    axisLabel.setLayout(new FlowLayout(FlowLayout.LEFT));
    final JPanel spacePanel = new JPanel();
    CSH.setHelpIDString(spacePanel, "axes_modify_axes");
    spacePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    spacePanel.setPreferredSize(dimension);
    final JLabel spaces = new JLabel("");
    CSH.setHelpIDString(spaces, "axes_modify_axes");
    spacePanel.add(spaces);
    axisLabel.add(spacePanel);
    final JPanel xPanel = new JPanel();
    CSH.setHelpIDString(xPanel, "axes_modify_axes");
    xPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    xPanel.setPreferredSize(d);
    final JLabel xlabel = new JLabel(Axes.axisNames[0]);
    xlabel.setToolTipText("specify range of x axis");
    CSH.setHelpIDString(xlabel, "range_x_axes_modify_axes_label");
    xPanel.add(xlabel);
    axisLabel.add(xPanel);
    final JPanel yPanel = new JPanel();
    CSH.setHelpIDString(yPanel, "axes_modify_axes");
    yPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    yPanel.setPreferredSize(d);
    final JLabel ylabel = new JLabel(Axes.axisNames[1]);
    ylabel.setToolTipText("specify range of y axis");
    CSH.setHelpIDString(ylabel, "range_y_axes_modify_axes_label");
    yPanel.add(ylabel);
    axisLabel.add(yPanel);
    final JPanel zPanel = new JPanel();
    CSH.setHelpIDString(zPanel, "axes_modify_axes");
    zPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    zPanel.setPreferredSize(d);
    zlabel = new JLabel(Axes.axisNames[2]);
    zlabel.setToolTipText("specify range of z axis");
    CSH.setHelpIDString(zlabel, "range_z_axes_modify_axes_label");
    if (session.plotManager.getDim() == 2) {
      zlabel.setVisible(false);
    }
    zPanel.add(zlabel);
    axisLabel.add(zPanel);
    axisPanel.add(axisLabel);
    final JPanel subpanel4 = new JPanel();
    CSH.setHelpIDString(subpanel4, "axes_modify_axes");
    subpanel4.setLayout(new FlowLayout(FlowLayout.LEFT));
    final JLabel label4 = new JLabel("Start:", SwingConstants.RIGHT);
    label4
        .setToolTipText("low end of the range specifying where the axis begins");
    CSH.setHelpIDString(label4, "start_axes_modify_axes_label");
    label4.setPreferredSize(dimension);
    subpanel4.add(label4);
    model4a = new SpinnerNumberModel(session.plotManager.subject.axes.start[0]
        + session.plotManager.subject.cumDelta[0], Float.NEGATIVE_INFINITY,
        Float.POSITIVE_INFINITY, 1.0);
    final JSpinner spinner4a = new JSpinner(model4a);
    CSH.setHelpIDString(spinner4a, "start_x_axes_modify_axes_spinner");
    spinner4a.setBorder(BorderFactory.createLoweredBevelBorder());
    spinner4a.setPreferredSize(d);
    subpanel4.add(spinner4a);
    model4b = new SpinnerNumberModel(session.plotManager.subject.axes.start[1]
        + session.plotManager.subject.cumDelta[1], Float.NEGATIVE_INFINITY,
        Float.POSITIVE_INFINITY, 1.0);
    final JSpinner spinner4b = new JSpinner(model4b);
    CSH.setHelpIDString(spinner4b, "start_y_axes_modify_axes_spinner");
    spinner4b.setBorder(BorderFactory.createLoweredBevelBorder());
    spinner4b.setPreferredSize(d);
    subpanel4.add(spinner4b);
    model4c = new SpinnerNumberModel(session.plotManager.subject.axes.start[2]
        + session.plotManager.subject.cumDelta[2], Float.NEGATIVE_INFINITY,
        Float.POSITIVE_INFINITY, 1.0);
    spinner4c = new JSpinner(model4c);
    CSH.setHelpIDString(spinner4c, "start_z_axes_modify_axes_spinner");
    spinner4c.setPreferredSize(d);
    spinner4c.setBorder(BorderFactory.createLoweredBevelBorder());
    if (session.plotManager.getDim() == 2) {
      spinner4c.setVisible(false);
    }
    subpanel4.add(spinner4c);
    axisPanel.add(subpanel4);
    // NEXT PANEL: FIVE
    final JPanel subpanel5 = new JPanel();
    CSH.setHelpIDString(subpanel5, "axes_modify_axes");
    subpanel5.setLayout(new FlowLayout(FlowLayout.LEFT));
    final JLabel label5 = new JLabel("Stop:", SwingConstants.RIGHT);
    label5
        .setToolTipText("low end of the range specifying where the axis begins");
    CSH.setHelpIDString(label5, "stop_axes_modify_axes_label");
    label5.setPreferredSize(dimension);
    subpanel5.add(label5);
    model5a = new SpinnerNumberModel(session.plotManager.subject.axes.stop[0]
        + session.plotManager.subject.cumDelta[0], Float.NEGATIVE_INFINITY,
        Float.POSITIVE_INFINITY, 1.0);
    final JSpinner spinner5a = new JSpinner(model5a);
    CSH.setHelpIDString(spinner5a, "stop_x_axes_modify_axes_spinner");
    spinner5a.setPreferredSize(d);
    spinner5a.setBorder(BorderFactory.createLoweredBevelBorder());
    subpanel5.add(spinner5a);
    model5b = new SpinnerNumberModel(session.plotManager.subject.axes.stop[1]
        + session.plotManager.subject.cumDelta[1], Float.NEGATIVE_INFINITY,
        Float.POSITIVE_INFINITY, 1.0);
    final JSpinner spinner5b = new JSpinner(model5b);
    CSH.setHelpIDString(spinner5b, "stop_y_axes_modify_axes_spinner");
    spinner5b.setPreferredSize(d);
    spinner5b.setBorder(BorderFactory.createLoweredBevelBorder());
    subpanel5.add(spinner5b);
    model5c = new SpinnerNumberModel(session.plotManager.subject.axes.stop[2]
        + session.plotManager.subject.cumDelta[2], Float.NEGATIVE_INFINITY,
        Float.POSITIVE_INFINITY, 1.0);
    spinner5c = new JSpinner(model5c);
    CSH.setHelpIDString(spinner5c, "stop_z_axes_modify_axes_spinner");
    spinner5c.setPreferredSize(d);
    spinner5c.setBorder(BorderFactory.createLoweredBevelBorder());
    if (session.plotManager.getDim() == 2) {
      spinner5c.setVisible(false);
    }
    subpanel5.add(spinner5c);
    axisPanel.add(subpanel5);
    // START LABELS SECTION
    final Box labelPanel = Box.createVerticalBox();
    labelPanel.setBorder(BorderFactory.createTitledBorder("Labels"));
    labelPanel
        .setToolTipText("change properties of labels of axes and tics in this section of dialog");
    CSH.setHelpIDString(labelPanel, "labels_modify_axes");
    final JPanel labelButtonPanel = new JPanel();
    CSH.setHelpIDString(labelButtonPanel, "labels_modify_axes");
    labelButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    final JLabel colorlabel = new JLabel("Color:", SwingConstants.RIGHT);
    colorlabel.setToolTipText("the color of labels");
    CSH.setHelpIDString(colorlabel, "color_labels_modify_axes_label");
    colorlabel.setPreferredSize(dimension);
    labelButtonPanel.add(colorlabel);
    labelColorButton = new JButton(Palette
        .getClosestChoice(session.plotManager.subject.axes.getLabelColor()).toString());
    labelColorButton.setToolTipText("click to choose color of labels");
    CSH.setHelpIDString(labelColorButton, "color_labels_modify_axes_button");
    labelColorButton.setPreferredSize(d);
    labelColorButton.setBorder(BorderFactory.createRaisedBevelBorder());
    labelColorButton.setBackground(session.plotManager.subject.axes.getLabelColor());
    // final ColorChooser labelColorChooser = new ColorChooser(session,
    // "Label colors");
    labelColorButton.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
        try {
          final ColorChooserDialog colorChooserDialog = session.dialogManager
              .getColorChooserModifyDialog();
          
          colorChooserDialog.setTitle("Label colors");
          colorChooserDialog.setChoice(Palette.getClosestChoice(session.plotManager.subject.axes.getLabelColor()));
          colorChooserDialog.setModal(true);
          colorChooserDialog.showDialog(); // 
          if (colorChooserDialog.isOk()) {
            final int i;
            final Palette.Choice choice = colorChooserDialog.getChoice();
            labelColorButton.setBackground(choice.getColor());
            labelColorButton.setText(choice.toString());
            labelColorButton.setForeground(choice.getTextColor());
          }
        }
        catch (final Throwable ex) {
          session.errorSupport("While changing color:", ex,
              "color_labels_modify_axes_button_error");
        }
      }
    });
    labelButtonPanel.add(labelColorButton);
    final JLabel labelFontLabel = new JLabel("Font:", SwingConstants.RIGHT);
    labelFontLabel.setToolTipText("the font of the axes and tic labels");
    CSH.setHelpIDString(labelFontLabel, "font_labels_modify_axes_label");
    labelFontLabel.setPreferredSize(d);
    labelButtonPanel.add(labelFontLabel);
    // final FontChooser labelFontChooser = new FontChooser(session,
    // axes.labelFont);
    final JButton labelFontButton = new JButton(session.plotManager.subject.axes.getLabelFont().getName());
    labelFontButton
        .setToolTipText("click to change the font of the axes and tic labels");
    CSH.setHelpIDString(labelFontButton, "font_labels_modify_axes_button");
    labelFontButton.setBorder(BorderFactory.createRaisedBevelBorder());
    labelFontButton.setPreferredSize(d);
    labelFontButton.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
        try {
          final FontChooserDialog fontChooserDialog = session.dialogManager
              .getFontChooserChooseDialog();
          fontChooserDialog.showDialog();
          if (fontChooserDialog.isOk()) {
            session.plotManager.subject.axes.setLabelFont(fontChooserDialog
                .getFont());
            labelFontButton.setText(fontChooserDialog.getSelectedFont().getName());
          }
        }
        catch (final Throwable ex) {
          session.errorSupport("While changing font:", ex,
              "font_labels_modify_axes_button_error");
        }
      }
    });
    labelButtonPanel.add(labelFontButton);
    labelPanel.add(labelButtonPanel);
    // TIC MARK SECTION
    final Box ticPanel = Box.createVerticalBox();
    ticPanel.setBorder(BorderFactory.createTitledBorder("Tic Marks"));
    ticPanel.setToolTipText("change properties of tic marks in this section");
    CSH.setHelpIDString(ticPanel, "tic_marks_modify_axes");
    final JPanel subpanel13 = new JPanel();
    CSH.setHelpIDString(subpanel13, "tic_marks_modify_axes");
    subpanel13.setLayout(new FlowLayout(FlowLayout.LEFT));
    final JLabel label13a = new JLabel("Length (mm):", SwingConstants.RIGHT);
    label13a
        .setToolTipText("length of major tic marks as measured on the screen in millimeters");
    CSH.setHelpIDString(label13a, "length_tic_marks_modify_axes_label");
    label13a.setPreferredSize(dimension);
    subpanel13.add(label13a);
    model13 = new SpinnerNumberModel(
        session.plotManager.subject.axes.ticLengthMM, Float.NEGATIVE_INFINITY,
        Float.POSITIVE_INFINITY, 1.0);
    final JSpinner spinner13 = new JSpinner(model13);
    CSH.setHelpIDString(spinner13, "length_tic_marks_modify_axes_spinner");
    spinner13.setPreferredSize(d);
    spinner13.setBorder(BorderFactory.createLoweredBevelBorder());
    subpanel13.add(spinner13);
    ticPanel.add(subpanel13);
    final JPanel axisLabel2 = new JPanel();
    CSH.setHelpIDString(axisLabel2, "tic_marks_modify_axes");
    axisLabel2.setPreferredSize(dimension);
    axisLabel2.setLayout(new FlowLayout(FlowLayout.LEFT));
    final JPanel spacePanel2 = new JPanel();
    CSH.setHelpIDString(spacePanel2, "tic_marks_modify_axes");
    spacePanel2.setLayout(new FlowLayout(FlowLayout.RIGHT));
    spacePanel2.setPreferredSize(dimension);
    final JLabel spaces2 = new JLabel("");
    CSH.setHelpIDString(spaces2, "tic_marks_modify_axes");
    spacePanel2.add(spaces2);
    axisLabel2.add(spacePanel2);
    final JPanel xPanel2 = new JPanel();
    CSH.setHelpIDString(xPanel2, "tic_marks_modify_axes");
    xPanel2.setLayout(new FlowLayout(FlowLayout.CENTER));
    xPanel2.setPreferredSize(d);
    final JLabel xlabel2 = new JLabel(Axes.axisNames[0]);
    xlabel2.setToolTipText("tic marks on x axis");
    CSH.setHelpIDString(xlabel2, "tic_marks_modify_axes_label");
    xPanel2.add(xlabel2);
    axisLabel2.add(xPanel2);
    final JPanel yPanel2 = new JPanel();
    CSH.setHelpIDString(yPanel2, "tic_marks_modify_axes");
    yPanel2.setLayout(new FlowLayout(FlowLayout.CENTER));
    yPanel2.setPreferredSize(d);
    final JLabel ylabel2 = new JLabel(Axes.axisNames[1]);
    ylabel2.setToolTipText("tic marks on y axis");
    CSH.setHelpIDString(ylabel2, "tic_marks_modify_axes_label");
    yPanel2.add(ylabel2);
    axisLabel2.add(yPanel2);
    final JPanel zPanel2 = new JPanel();
    CSH.setHelpIDString(zPanel2, "tic_marks_modify_axes");
    zPanel2.setLayout(new FlowLayout(FlowLayout.CENTER));
    zPanel2.setPreferredSize(d);
    zlabel2 = new JLabel(Axes.axisNames[2]);
    zlabel2.setToolTipText("tic marks on z axis");
    CSH.setHelpIDString(zlabel2, "tic_marks_modify_axes_label");
    if (session.plotManager.getDim() == 2) {
      zlabel2.setVisible(false);
    }
    zPanel2.add(zlabel2);
    axisLabel2.add(zPanel2);
    ticPanel.add(axisLabel2);
    // NEXT PANEL: SIX
    final JPanel subpanel6 = new JPanel();
    CSH.setHelpIDString(subpanel6, "tic_marks_modify_axes");
    subpanel6.setLayout(new FlowLayout(FlowLayout.LEFT));
    final JPanel labelPanel7 = new JPanel();
    CSH.setHelpIDString(labelPanel7, "tic_marks_modify_axes");
    labelPanel7.setLayout(new FlowLayout(FlowLayout.RIGHT));
    labelPanel7.setPreferredSize(dimension);
    final JLabel label6 = new JLabel("Major tic spacing:");
    labelPanel7.setToolTipText("distance between large tics");
    CSH.setHelpIDString(labelPanel7,
        "major_spacing_tic_marks_modify_axes_label");
    labelPanel7.add(label6);
    subpanel6.add(labelPanel7);
    model6a = new SpinnerNumberModel(
        session.plotManager.subject.axes.ticSpace[0], Float.NEGATIVE_INFINITY,
        Float.POSITIVE_INFINITY, 1.0);
    final JSpinner spinner6a = new JSpinner(model6a);
    CSH.setHelpIDString(spinner6a,
        "major_spacing_x_tic_marks_modify_axes_spinner");
    spinner6a.setBorder(BorderFactory.createLoweredBevelBorder());
    spinner6a.setPreferredSize(d);
    subpanel6.add(spinner6a);
    model6b = new SpinnerNumberModel(
        session.plotManager.subject.axes.ticSpace[1], Float.NEGATIVE_INFINITY,
        Float.POSITIVE_INFINITY, 1.0);
    final JSpinner spinner6b = new JSpinner(model6b);
    CSH.setHelpIDString(spinner6b,
        "major_spacing_y_tic_marks_modify_axes_spinner");
    spinner6b.setBorder(BorderFactory.createLoweredBevelBorder());
    spinner6b.setPreferredSize(d);
    subpanel6.add(spinner6b);
    model6c = new SpinnerNumberModel(
        session.plotManager.subject.axes.ticSpace[2], Float.NEGATIVE_INFINITY,
        Float.POSITIVE_INFINITY, 1.0);
    spinner6c = new JSpinner(model6c);
    CSH.setHelpIDString(spinner6c,
        "major_spacing_z_tic_marks_modify_axes_spinner");
    spinner6c.setBorder(BorderFactory.createLoweredBevelBorder());
    spinner6c.setPreferredSize(d);
    if (session.plotManager.getDim() == 2) {
      spinner6c.setVisible(false);
    }
    subpanel6.add(spinner6c);
    ticPanel.add(subpanel6);
    // NEXT PANEL: SEVEN
    final JPanel subpanel7 = new JPanel();
    CSH.setHelpIDString(subpanel7, "tic_marks_modify_axes");
    subpanel7.setLayout(new FlowLayout(FlowLayout.LEFT));
    final JPanel labelPanel8 = new JPanel();
    CSH.setHelpIDString(labelPanel8, "tic_marks_modify_axes");
    labelPanel8.setLayout(new FlowLayout(FlowLayout.RIGHT));
    labelPanel8.setPreferredSize(dimension);
    final JLabel label7 = new JLabel("Minor tic spacing:");
    label7.setToolTipText("distance between small tic marks");
    CSH.setHelpIDString(label7, "minor_spacing_tic_marks_modify_axes_label");
    labelPanel8.add(label7);
    subpanel7.add(labelPanel8);
    model7a = new SpinnerNumberModel(
        session.plotManager.subject.axes.ticSpace[0]
            / (session.plotManager.subject.axes.numSubTics[0] + 1), 0,
        Float.MAX_VALUE, 0.1);
    final JSpinner spinner7a = new JSpinner(model7a);
    CSH.setHelpIDString(spinner7a,
        "minor_spacing_x_tic_marks_modify_axes_spinner");
    spinner7a.setBorder(BorderFactory.createLoweredBevelBorder());
    spinner7a.setPreferredSize(d);
    subpanel7.add(spinner7a);
    model7b = new SpinnerNumberModel(
        session.plotManager.subject.axes.ticSpace[1]
            / (session.plotManager.subject.axes.numSubTics[1] + 1), 0,
        Float.MAX_VALUE, 0.1);
    final JSpinner spinner7b = new JSpinner(model7b);
    CSH.setHelpIDString(spinner7b,
        "minor_spacing_y_tic_marks_modify_axes_spinner");
    spinner7b.setBorder(BorderFactory.createLoweredBevelBorder());
    spinner7b.setPreferredSize(d);
    subpanel7.add(spinner7b);
    model7c = new SpinnerNumberModel(
        session.plotManager.subject.axes.ticSpace[2]
            / (session.plotManager.subject.axes.numSubTics[2] + 1), 0,
        Float.MAX_VALUE, 0.1);
    spinner7c = new JSpinner(model7c);
    CSH.setHelpIDString(spinner7c,
        "minor_spacing_z_tic_marks_modify_axes_spinner");
    spinner7c.setPreferredSize(d);
    spinner7c.setBorder(BorderFactory.createLoweredBevelBorder());
    if (session.plotManager.getDim() == 2) {
      spinner7c.setVisible(false);
    }
    subpanel7.add(spinner7c);
    ticPanel.add(subpanel7);
    vbox.add(axisPanel);
    vbox.add(labelPanel);
    vbox.add(ticPanel);
    final JPanel gridPanel = new JPanel();
    CSH.setHelpIDString(gridPanel, "grid_modify_axes");
    gridPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    final JPanel subpanel3 = new JPanel();
    CSH.setHelpIDString(subpanel3, "grid_modify_axes");
    subpanel3.setLayout(new FlowLayout(FlowLayout.RIGHT));
    subpanel3.setPreferredSize(dimension);
    gridlinesCheckBox = new JCheckBox("Draw grid lines");
    gridlinesCheckBox
        .setToolTipText("click to choose whether grid lines will be drawn");
    CSH.setHelpIDString(gridlinesCheckBox, "grid_modify_axes_checkbox");
    gridlinesCheckBox.setSelected(session.plotManager.subject.axes.drawGrid);
    gridlinesCheckBox.setBackground(subpanel3.getBackground());
    subpanel3.add(gridlinesCheckBox);
    gridPanel.add(subpanel3);
    vbox.add(new JSeparator());
    vbox.add(gridPanel);
    topPanel.add(vbox, BorderLayout.CENTER);
    try {
      changeDimensionality();
    }
    catch (final Exception ex) {
      throw new RuntimeException("bad dim in file?");
    }
    setHelpID("help_modify_axes_button");
    add(getButtonPanel(), BorderLayout.SOUTH);
  }

  @Override
  protected boolean apply() {
    session.plotManager.subject.axes.drawGrid = gridlinesCheckBox.isSelected();
    if (comboBox1.getSelectedIndex() == 0) { // dimensionality changed to 2
      if (session.plotManager.getDim() == 3) { // dimensionality went down
        session.plotManager.subject.axes.convertTo2D();
      }
      // axes.setDim(2);
      session.plotManager.view.setInertia(false);
      session.plotManager.view.setOrthogonalMode(true);
    }
    else if (comboBox1.getSelectedIndex() == 1) { // dimensionality changed to 3
      if (session.plotManager.getDim() == 2) { // dimensionality went up
        session.plotManager.subject.axes.convertTo3D();
      }
      // axes.setDim(3);
    }
    int i = comboBox2.getSelectedIndex();
    if (i == 0) {
      session.plotManager.subject.axes.style = Axes.styleBox;
    }
    else if (i == 1) {
      session.plotManager.subject.axes.style = Axes.styleMin;
    }
    else if (i == 2) {
      session.plotManager.subject.axes.style = Axes.styleMax;
    }
    session.plotManager.subject.axes.unzoomedStart[0] = ((Double) model4a
        .getValue()).floatValue();
    session.plotManager.subject.axes.start[0] = session.plotManager.subject.axes.unzoomedStart[0]
        - session.plotManager.subject.cumDelta[0];
    session.plotManager.subject.axes.unzoomedStart[1] = ((Double) model4b
        .getValue()).floatValue();
    session.plotManager.subject.axes.start[1] = session.plotManager.subject.axes.unzoomedStart[1]
        - session.plotManager.subject.cumDelta[1];
    if (session.plotManager.getDim() == 3) { // TODO: do we need all these dim
      // checks given the "Fundamental"
      // principle?
      session.plotManager.subject.axes.unzoomedStart[2] = ((Double) model4c
          .getValue()).floatValue();
      session.plotManager.subject.axes.start[2] = session.plotManager.subject.axes.unzoomedStart[2]
          - session.plotManager.subject.cumDelta[2];
    }
    session.plotManager.subject.axes.unzoomedStop[0] = ((Double) model5a
        .getValue()).floatValue();
    session.plotManager.subject.axes.stop[0] = session.plotManager.subject.axes.unzoomedStop[0]
        - session.plotManager.subject.cumDelta[0];
    session.plotManager.subject.axes.unzoomedStop[1] = ((Double) model5b
        .getValue()).floatValue();
    session.plotManager.subject.axes.stop[1] = session.plotManager.subject.axes.unzoomedStop[1]
        - session.plotManager.subject.cumDelta[1];
    if (session.plotManager.getDim() == 3) {
      session.plotManager.subject.axes.unzoomedStop[2] = ((Double) model5c
          .getValue()).floatValue();
      session.plotManager.subject.axes.stop[2] = session.plotManager.subject.axes.unzoomedStop[2]
          - session.plotManager.subject.cumDelta[2];
    }
    float f = ((Double) model6a.getValue()).floatValue();
    if (f == 0) {
      throw new RuntimeException(
          "Major tic spacing for x axis must be non-zero.");
    }
    session.plotManager.subject.axes.unzoomedTicSpace[0] = session.plotManager.subject.axes.ticSpace[0] = f;
    f = ((Double) model6b.getValue()).floatValue();
    if (f == 0) {
      throw new RuntimeException(
          "Major tic spacing for y axis must be non-zero.");
    }
    session.plotManager.subject.axes.unzoomedTicSpace[1] = session.plotManager.subject.axes.ticSpace[1] = f;
    if (session.plotManager.getDim() == 3) {
      f = ((Double) model6c.getValue()).floatValue();
      if (f == 0) {
        throw new RuntimeException(
            "Major tic spacing for z axis must be non-zero.");
      }
      session.plotManager.subject.axes.unzoomedTicSpace[2] = session.plotManager.subject.axes.ticSpace[2] = f;
    }
    f = model7a.getNumber().floatValue();
    if (f > session.plotManager.subject.axes.unzoomedTicSpace[0]) {
      f = session.plotManager.subject.axes.unzoomedTicSpace[0];
    }
    if (f == 0) {
      throw new RuntimeException(
          "Minor tic spacing for x axis must be non-zero.");
    }
    session.plotManager.subject.axes.numSubTics[0] = (int) (session.plotManager.subject.axes.unzoomedTicSpace[0] / f) - 1;
    f = model7b.getNumber().floatValue();
    if (f == 0) {
      throw new RuntimeException(
          "Minor tic spacing for y axis must be non-zero.");
    }
    if (f > session.plotManager.subject.axes.unzoomedTicSpace[1]) {
      f = session.plotManager.subject.axes.unzoomedTicSpace[1];
    }
    session.plotManager.subject.axes.numSubTics[1] = (int) (session.plotManager.subject.axes.unzoomedTicSpace[1] / f) - 1;
    if (session.plotManager.getDim() == 3) {
      f = model7c.getNumber().floatValue();
      if (f == 0) {
        try {
          throw new Exception("Minor tic spacing for z axis must be non-zero.");
        }
        catch (final Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      if (f > session.plotManager.subject.axes.unzoomedTicSpace[2]) {
        f = session.plotManager.subject.axes.unzoomedTicSpace[2];
      }
      session.plotManager.subject.axes.numSubTics[2] = (int) (session.plotManager.subject.axes.unzoomedTicSpace[2] / f) - 1;
    }
    session.plotManager.subject.axes.lineWidthMM = ((Double) model12.getValue())
        .floatValue();
    session.plotManager.subject.axes.ticLengthMM = ((Double) model13.getValue())
        .floatValue();
    session.plotManager.subject.axes.color = axisColorButton.getBackground();
    session.plotManager.subject.axes.setLabelColor(labelColorButton
        .getBackground());
    session.plotManager.subject.axes.buildAxes();
    session.plotManager.subject.axes.clearTics();
    session.plotManager.subject.axes.createTics(); // TODO: this loses user
    // changes and inefficient
    // if not needed
    session.plotManager.subject.axes.buildLabelsAndTics();
    // Set the label color and font for tic label and axis
    for (int axis = 0; axis < session.plotManager.getDim(); axis++) {
      session.plotManager.subject.axes.getLabelForAxis(axis).color = session.plotManager.subject.axes.getLabelColor();
      session.plotManager.subject.axes.getLabelForAxis(axis).font = session.plotManager.subject.axes.getLabelFont();
      for (i = 0; i < session.plotManager.subject.axes.tics[axis].length; i++) {
        session.plotManager.subject.axes.tics[axis][i].label.color = session.plotManager.subject.axes.getLabelColor();
        session.plotManager.subject.axes.tics[axis][i].label.font = session.plotManager.subject.axes.getLabelFont();
      }
    }
    session.plotManager.canvas.smartCompose();
    session.plotManager.refreshCanvas();
    return true; // apply was successful
  }

  public void changeDimensionality() throws Exception {
    final String s = (String) comboBox1.getSelectedItem();
    if (s.equals("2D")) {
      zlabel.setVisible(false);
      zlabel2.setVisible(false);
      spinner4c.setVisible(false);
      spinner5c.setVisible(false);
      spinner6c.setVisible(false);
      spinner7c.setVisible(false);
    }
    else if (s.equals("3D")) {
      // gridlinesCheckBox.setEnabled(false);
      zlabel.setVisible(true);
      zlabel2.setVisible(true);
      spinner4c.setVisible(true);
      spinner5c.setVisible(true);
      spinner6c.setVisible(true);
      spinner7c.setVisible(true);
    }
    pack();
  }

  private static final long serialVersionUID = 1L;
}
