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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;

public final class ViewModifyDialog extends AbstractDialog {
  private final Session session;
  private JCheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5;
  private boolean advancedEnabled = false;
  private SpinnerNumberModel screenXModel, screenYModel, centerXModel,
      centerYModel, centerZModel, model3a, model3b, model4a, model4b, model5a,
      model5b, model8;

  public ViewModifyDialog(final Session session) {
    super(session);
    this.session = session;
    setTitle("View - Modify");
    setModal(false);
    addWindowListener(session.dialogManager);
    addComponentListener(session.dialogManager);
    final JPanel topPanel = new JPanel();
    add(topPanel);
    CSH.setHelpIDString(topPanel, "modify_view");
    advancedEnabled = false;
    topPanel.setLayout(new BorderLayout());
    topPanel.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.WEST);
    topPanel.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.EAST);
    topPanel.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.NORTH);
    final Box vbox = Box.createVerticalBox();
    // NEXT PANEL: EIGHT
    final JPanel subpanel8 = new JPanel();
    CSH.setHelpIDString(subpanel8, "modify_view");
    subpanel8.setLayout(new FlowLayout(FlowLayout.LEFT));
    final JLabel label8 = new JLabel(
        "Minimum distance (mm) between plot and window edges:",
        SwingConstants.RIGHT);
    label8.setToolTipText("distance used in Fit to Canvas");
    CSH.setHelpIDString(label8, "distance_modify_view_label");
    label8.setPreferredSize(new Dimension(440, 25));
    subpanel8.add(label8);
    model8 = new SpinnerNumberModel(session.plotManager.view.margin, 0,
        Float.POSITIVE_INFINITY, 0.25);
    final JSpinner spinner8 = new JSpinner(model8);
    CSH.setHelpIDString(spinner8, "distance_modify_view_spinner_label");
    spinner8.setPreferredSize(new Dimension(100, 25));
    spinner8.setBorder(BorderFactory.createLoweredBevelBorder());
    subpanel8.add(spinner8);
    final JLabel spaces = new JLabel(" ");
    CSH.setHelpIDString(spaces, "modify_view");
    spaces.setPreferredSize(new Dimension(60, 25));
    subpanel8.add(spaces);
    vbox.add(subpanel8);
    addAdvancedButtonPanel(vbox);
    setHelpID("help_modify_view_button");
    topPanel.add(vbox, BorderLayout.CENTER);
    topPanel.add(getButtonPanel(), BorderLayout.SOUTH);
  }

  /**
   *
   */
  private void addAdvancedButtonPanel(final Box vbox) {
    final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
    CSH.setHelpIDString(buttonPanel, "advanced_modify_view");
    final JLabel label = new JLabel("Advanced features setup",
        SwingConstants.RIGHT);
    label.setPreferredSize(new Dimension(440, 30));
    label.setToolTipText("further properties you may want to change");
    CSH.setHelpIDString(label, "advanced_modify_view_label");
    buttonPanel.add(label);
    final JButton advanced = new JButton("Advanced...");
    advanced.setPreferredSize(new Dimension(100, 30));
    advanced.setToolTipText("click to see more properties you can change");
    CSH.setHelpIDString(advanced, "advanced_modify_view_button");
    advanced.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent ev) {
        try {
          advancedEnabled = true;
          vbox.remove(buttonPanel);
          removeAdvancedButtonPanel(vbox);
          addAdvancedPanel(vbox);
          pack();
          setVisible(true);
        }
        catch (final Throwable ex) {
          session.errorSupport("", ex, "advanced_modify_view_button_error");
        }
      }
    });
    buttonPanel.add(advanced, BorderLayout.EAST);
    vbox.add(new JSeparator(), 1);
    vbox.add(buttonPanel, 2);
    CSH.setHelpIDString(vbox, "modify_view");
  }

  /**
   *
   */
  private void removeAdvancedButtonPanel(final Box vbox) {
    final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
    final JLabel label = new JLabel("Back to normal setup",
        SwingConstants.RIGHT);
    label.setPreferredSize(new Dimension(440, 30));
    label.setToolTipText("see fewer properties");
    CSH.setHelpIDString(label, "back_modify_view_label");
    buttonPanel.add(label);
    final JButton back = new JButton("Back...");
    back.setToolTipText("click to see fewer view properties");
    CSH.setHelpIDString(back, "back_modify_view_button");
    back.setPreferredSize(new Dimension(100, 30));
    back.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent ev) {
        try {
          vbox.remove(12);
          vbox.remove(11);
          vbox.remove(10);
          vbox.remove(9);
          vbox.remove(8);
          vbox.remove(7);
          vbox.remove(6);
          vbox.remove(5);
          vbox.remove(4);
          vbox.remove(3);
          vbox.remove(buttonPanel);
          vbox.remove(1);
          addAdvancedButtonPanel(vbox);
          pack();
          setVisible(true);
        }
        catch (final Throwable ex) {
          session.errorSupport("", ex, "back_modify_view_button_error");
        }
      }
    });
    buttonPanel.add(back, BorderLayout.EAST);
    vbox.add(buttonPanel, 2);
  }

  @Override
  protected boolean apply() {
    session.plotManager.view.margin = ((Double) model8.getNumber()).floatValue();
    // System.out.println("margin " + margin);
    if (advancedEnabled) {
      session.plotManager.view.tMat[3][0] = ((Double) screenXModel.getNumber()).floatValue();
      session.plotManager.view.tMat[3][1] = ((Double) screenYModel.getNumber()).floatValue();
      if (session.plotManager.view.AUTOCOMPOSE_ROTATIONAL_CENTERING_MODE == checkBox2
          .isSelected()) {
        // we have not switched
        if (session.plotManager.view.AUTOCOMPOSE_ROTATIONAL_CENTERING_MODE == false) { // old
                                                                                    // mode
          session.plotManager.subject.moveToUserSpace();
        }
      }
      else { // user toggled
        session.plotManager.subject.moveToUserSpace();
      }
      // Following will be ignored in calculation if autocomposing
      session.plotManager.subject.cumDelta[0] = ((Double) centerXModel.getNumber())
          .floatValue();
      session.plotManager.subject.cumDelta[1] = ((Double) centerYModel.getNumber())
          .floatValue();
      session.plotManager.subject.cumDelta[2] = ((Double) centerZModel.getNumber())
          .floatValue();
      if (session.plotManager.view.AUTOCOMPOSE_ROTATIONAL_CENTERING_MODE == checkBox2
          .isSelected()) {
        // we have not switched
        if (session.plotManager.view.AUTOCOMPOSE_ROTATIONAL_CENTERING_MODE == false) {
          session.plotManager.subject.moveToSubjectSpace();
        }
      }
      else { // user toggled
        if (session.plotManager.view.AUTOCOMPOSE_ROTATIONAL_CENTERING_MODE == true) {
          session.plotManager.subject.moveToSubjectSpace();
        }
      }
      session.plotManager.view.AUTOCOMPOSE_SCREEN_CENTERING_MODE = checkBox1
          .isSelected();
      session.plotManager.view.AUTOCOMPOSE_ROTATIONAL_CENTERING_MODE = checkBox2
          .isSelected();
      session.plotManager.view.AUTOCOMPOSE_PERSPECTIVE_MODE = checkBox3
          .isSelected();
      session.plotManager.view.AUTOCOMPOSE_LIGHTING_MODE = checkBox4.isSelected();
      session.plotManager.view.AUTOCOMPOSE_STEREO_MODE = checkBox5.isSelected();
      session.plotManager.view.cop = ((Double) model3a.getNumber()).floatValue();
      session.plotManager.view.focal = ((Double) model3b.getNumber()).floatValue();
      session.plotManager.view.lightRamp = ((Double) model4a.getNumber())
          .floatValue();
      session.plotManager.view.lightPosition = ((Double) model4b.getNumber())
          .floatValue();
      session.plotManager.view.stereoAngle = ((Double) model5a.getNumber())
          .floatValue();
      session.plotManager.view.stereoSeparation = ((Double) model5b.getNumber())
          .intValue();
    }
    //session.plotManager.canvas.smartCompose();
    session.plotManager.refreshCanvas();
    return true; // apply was successful
  }

  /**
   *
   */
  private void addAdvancedPanel(final Box vbox) {
    final Dimension dimension = new Dimension(120, 25);
    final Dimension dimension2 = new Dimension(80, 25);
    final Dimension dimension3 = new Dimension(60, 25);
    final Dimension dimension4 = new Dimension(60, 25);
    // NEXT PANEL: FIVE
    final JPanel subpanel5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    CSH.setHelpIDString(subpanel5, "advanced_modify_view");
    final JLabel label5a = new JLabel("Stereo: ", SwingConstants.RIGHT);
    label5a.setToolTipText("perceive 3D by overlapping two images");
    CSH.setHelpIDString(label5a, "stereo_modify_view_label");
    label5a.setPreferredSize(dimension);
    subpanel5.add(label5a);
    checkBox5 = new JCheckBox("auto ");
    checkBox5
        .setToolTipText("if checked, calculates stereo propetrties during Fit to Screen");
    CSH.setHelpIDString(checkBox5, "stereo_auto_modify_view_checkbox");
    checkBox5.setBackground(subpanel5.getBackground());
    checkBox5.setPreferredSize(dimension4);
    subpanel5.add(checkBox5);
    checkBox5.setSelected(session.plotManager.view.AUTOCOMPOSE_STEREO_MODE);
    final JLabel label5b = new JLabel("Angle:", SwingConstants.RIGHT);
    label5b.setToolTipText("small angle that your eyes form");
    CSH.setHelpIDString(label5b, "stereo_angle_modify_view_label");
    label5b.setPreferredSize(dimension3);
    subpanel5.add(label5b);
    model5a = new SpinnerNumberModel(session.plotManager.view.stereoAngle,
        Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 1.0);
    final JSpinner spinner5a = new JSpinner(model5a);
    CSH.setHelpIDString(spinner5a, "stereo_angle_modify_view_spinner");
    spinner5a.setBorder(BorderFactory.createRaisedBevelBorder());
    spinner5a.setPreferredSize(new Dimension(100, 25));
    subpanel5.add(spinner5a);
    final JLabel label5c = new JLabel("Separation:", SwingConstants.RIGHT);
    label5c.setToolTipText("distance between images on screen in pixels");
    CSH.setHelpIDString(label5c, "setero_separation_modify_view_label");
    label5c.setPreferredSize(dimension2);
    subpanel5.add(label5c);
    model5b = new SpinnerNumberModel(session.plotManager.view.stereoSeparation, 0,
        Integer.MAX_VALUE, 1.0);
    final JSpinner spinner5b = new JSpinner(model5b);
    CSH.setHelpIDString(spinner5b, "stereo_separation_modify_view_spinner");
    spinner5b.setBorder(BorderFactory.createRaisedBevelBorder());
    spinner5b.setPreferredSize(new Dimension(100, 25));
    subpanel5.add(spinner5b);
    if (checkBox5.isSelected()) {
      spinner5a.setEnabled(false);
      spinner5b.setEnabled(false);
    }
    else {
      spinner5a.setEnabled(true);
      spinner5b.setEnabled(true);
    }
    checkBox5.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
        try {
          if (checkBox5.isSelected()) {
            spinner5a.setEnabled(false);
            spinner5b.setEnabled(false);
          }
          else {
            spinner5a.setEnabled(true);
            spinner5b.setEnabled(true);
          }
        }
        catch (final Throwable ex) {
          session.errorSupport("", ex, "stereo_auto_modify_view_checkbox_error");
        }
      }
    });
    // NEXT PANEL: ONE
    final JPanel subpanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    CSH.setHelpIDString(subpanel1, "screen_modify_view");
    final JLabel label1 = new JLabel("Canvas centering: ", SwingConstants.RIGHT);
    label1.setToolTipText("position of rotational center on canvas ");
    CSH.setHelpIDString(label1, "screen_modify_view_label");
    label1.setPreferredSize(dimension);
    subpanel1.add(label1);
    checkBox1 = new JCheckBox("auto  ");
    checkBox1
        .setToolTipText("if checked, calculates screen centering during Fit to Screen");
    CSH.setHelpIDString(checkBox1, "screen_auto_modify_view");
    checkBox1.setBackground(subpanel1.getBackground());
    checkBox1.setPreferredSize(dimension4);
    subpanel1.add(checkBox1);
    checkBox1
        .setSelected(session.plotManager.view.AUTOCOMPOSE_SCREEN_CENTERING_MODE);
    final JLabel label2 = new JLabel("  X:", SwingConstants.RIGHT);
    label2.setToolTipText("x coordinate of screen center in pixels");
    CSH.setHelpIDString(label2, "screen_x_modify_view_label");
    label2.setPreferredSize(dimension3);
    subpanel1.add(label2);
    screenXModel = new SpinnerNumberModel(session.plotManager.view.tMat[3][0],
        Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 10.0);
    final JSpinner spinner1a = new JSpinner(screenXModel);
    CSH.setHelpIDString(spinner1a, "screen_x_modify_view_spinner");
    spinner1a.setBorder(BorderFactory.createRaisedBevelBorder());
    spinner1a.setPreferredSize(new Dimension(100, 25));
    subpanel1.add(spinner1a);
    final JLabel label3 = new JLabel("  Y:", SwingConstants.RIGHT);
    label3.setPreferredSize(dimension2);
    label3.setToolTipText("y coordinate of screen center in pixels");
    CSH.setHelpIDString(label3, "screen_y_modify_view_label");
    subpanel1.add(label3);
    screenYModel = new SpinnerNumberModel(session.plotManager.view.tMat[3][1],
        Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 10.0);
    final JSpinner spinner1b = new JSpinner(screenYModel);
    CSH.setHelpIDString(spinner1b, "screen_y_modify_view_spinner");
    spinner1b.setBorder(BorderFactory.createRaisedBevelBorder());
    spinner1b.setPreferredSize(new Dimension(100, 25));
    subpanel1.add(spinner1b);
    if (checkBox1.isSelected()) {
      spinner1a.setEnabled(false);
      spinner1b.setEnabled(false);
    }
    else {
      spinner1a.setEnabled(true);
      spinner1b.setEnabled(true);
    }
    checkBox1.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
        try {
          if (checkBox1.isSelected()) {
            spinner1a.setEnabled(false);
            spinner1b.setEnabled(false);
          }
          else {
            spinner1a.setEnabled(true);
            spinner1b.setEnabled(true);
          }
        }
        catch (final Throwable ex) {
          session.errorSupport("", ex, "screen_auto_modify_view_checkbox_error");
        }
      }
    });
    // NEXT PANEL: TWO
    final JPanel subpanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    CSH.setHelpIDString(spinner1b, "rotationsl_modify_view");
    final JLabel label4 = new JLabel("Rotational centering: ",
        SwingConstants.RIGHT);
    label4.setToolTipText("rotational origin inside subject");
    CSH.setHelpIDString(label4, "rotational_modify_view");
    label4.setPreferredSize(dimension);
    subpanel2.add(label4);
    checkBox2 = new JCheckBox("auto  ");
    checkBox2
        .setToolTipText("if checked, calculates rotational centering during Fit to Screen");
    CSH.setHelpIDString(checkBox2, "rotational_auto_modify_view_checkbox");
    checkBox2.setBackground(subpanel2.getBackground());
    checkBox2.setPreferredSize(dimension4);
    subpanel2.add(checkBox2);
    checkBox2
        .setSelected(session.plotManager.view.AUTOCOMPOSE_ROTATIONAL_CENTERING_MODE);
    final JLabel label5 = new JLabel("  X:", SwingConstants.RIGHT);
    label5.setToolTipText("x coordinate of rotational center");
    CSH.setHelpIDString(label5, "rotational_x_modify_view_label");
    label5.setPreferredSize(dimension3);
    subpanel2.add(label5);
    centerXModel = new SpinnerNumberModel(session.plotManager.subject.cumDelta[0],
        Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 1.0);
    final JSpinner spinner2a = new JSpinner(centerXModel);
    CSH.setHelpIDString(spinner2a, "rotational_x_modify_view_spinner");
    spinner2a.setBorder(BorderFactory.createRaisedBevelBorder());
    spinner2a.setPreferredSize(new Dimension(100, 25));
    subpanel2.add(spinner2a);
    final JLabel label6 = new JLabel("  Y:", SwingConstants.RIGHT);
    label6.setToolTipText("y coordinate of rotational center");
    CSH.setHelpIDString(label6, "rotational_y_modify_view_label");
    label6.setPreferredSize(dimension2);
    subpanel2.add(label6);
    centerYModel = new SpinnerNumberModel(session.plotManager.subject.cumDelta[1],
        Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 1.0);
    final JSpinner spinner2b = new JSpinner(centerYModel);
    spinner2b.setBorder(BorderFactory.createRaisedBevelBorder());
    CSH.setHelpIDString(spinner2b, "rotational_y_modify_view_spinner");
    spinner2b.setPreferredSize(new Dimension(100, 25));
    subpanel2.add(spinner2b);
    final JLabel label7 = new JLabel("  Z:", SwingConstants.RIGHT);
    label7.setToolTipText("z coordinate of rotational center");
    CSH.setHelpIDString(label7, "rotational_z_modify_view_label");
    label7.setPreferredSize(new Dimension(30, 25));
    subpanel2.add(label7);
    centerZModel = new SpinnerNumberModel(session.plotManager.subject.cumDelta[2],
        Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 1.0);
    final JSpinner spinner2c = new JSpinner(centerZModel);
    CSH.setHelpIDString(spinner2c, "rotational_z_modify_view_spinner");
    spinner2c.setBorder(BorderFactory.createRaisedBevelBorder());
    spinner2c.setPreferredSize(new Dimension(100, 25));
    subpanel2.add(spinner2c);
    if (checkBox2.isSelected()) {
      spinner2a.setEnabled(false);
      spinner2b.setEnabled(false);
      spinner2c.setEnabled(false);
    }
    else {
      spinner2a.setEnabled(true);
      spinner2b.setEnabled(true);
      spinner2c.setEnabled(true);
    }
    checkBox2.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
        try {
          if (checkBox2.isSelected()) {
            spinner2a.setEnabled(false);
            spinner2b.setEnabled(false);
            spinner2c.setEnabled(false);
          }
          else {
            spinner2a.setEnabled(true);
            spinner2b.setEnabled(true);
            spinner2c.setEnabled(true);
          }
        }
        catch (final Throwable ex) {
          session.errorSupport("", ex, "rotational_auto_modify_view_checkbox_error");
        }
      }
    });
    // NEXT PANEL: THREE
    final JPanel subpanel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    CSH.setHelpIDString(subpanel3, "perspective_modify_view");
    final JLabel label3a = new JLabel("Perspective:", SwingConstants.RIGHT);
    label3a.setToolTipText("objects farther away appear smaller");
    CSH.setHelpIDString(label3a, "perspective_modify_view_label");
    label3a.setPreferredSize(dimension);
    subpanel3.add(label3a);
    checkBox3 = new JCheckBox("auto ");
    checkBox3
        .setToolTipText("if checked, calculates perspective during Fit to Screen");
    CSH.setHelpIDString(checkBox3, "perspective_auto_modify_view_checkbox");
    checkBox3.setBackground(subpanel3.getBackground());
    checkBox3.setPreferredSize(dimension4);
    subpanel3.add(checkBox3);
    checkBox3.setSelected(session.plotManager.view.AUTOCOMPOSE_PERSPECTIVE_MODE);
    final JLabel label3b = new JLabel("Eye:", SwingConstants.RIGHT);
    label3b.setToolTipText("distance of eye away from plot");
    CSH.setHelpIDString(label3b, "perspective_eye_modify_view_label");
    label3b.setPreferredSize(dimension3);
    subpanel3.add(label3b);
    model3a = new SpinnerNumberModel(session.plotManager.view.cop,
        Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 1.0);
    final JSpinner spinner3a = new JSpinner(model3a);
    CSH.setHelpIDString(spinner3a, "perspective_eye_modify_view_spinner");
    spinner3a.setBorder(BorderFactory.createRaisedBevelBorder());
    spinner3a.setPreferredSize(new Dimension(100, 25));
    subpanel3.add(spinner3a);
    final JLabel label3c = new JLabel("Focal length:", SwingConstants.RIGHT);
    label3c.setToolTipText("similar to magnification");
    CSH.setHelpIDString(label3c, "perspective_focal_modify_view_label");
    label3c.setPreferredSize(dimension2);
    subpanel3.add(label3c);
    model3b = new SpinnerNumberModel(session.plotManager.view.focal,
        Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 1.0);
    final JSpinner spinner3b = new JSpinner(model3b);
    CSH.setHelpIDString(spinner3b, "perspective_focal_modify_view_spinner");
    spinner3b.setBorder(BorderFactory.createRaisedBevelBorder());
    spinner3b.setPreferredSize(new Dimension(100, 25));
    subpanel3.add(spinner3b);
    if (checkBox3.isSelected()) {
      spinner3a.setEnabled(false);
      spinner3b.setEnabled(false);
    }
    else {
      spinner3a.setEnabled(true);
      spinner3b.setEnabled(true);
    }
    checkBox3.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
        try {
          if (checkBox3.isSelected()) {
            spinner3a.setEnabled(false);
            spinner3b.setEnabled(false);
          }
          else {
            spinner3a.setEnabled(true);
            spinner3b.setEnabled(true);
          }
        }
        catch (final Throwable ex) {
          session.errorSupport("", ex, "perspective_auto_modify_view_checkbox_error");
        }
      }
    });
    // NEXT PANEL: FOUR
    final JPanel subpanel4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    CSH.setHelpIDString(subpanel4, "lighting_modify_view");
    final JLabel label4a = new JLabel("Lighting: ", SwingConstants.RIGHT);
    label4a.setToolTipText("objects farther away appear darker");
    CSH.setHelpIDString(label4a, "lighting_modify_view_label");
    label4a.setPreferredSize(dimension);
    subpanel4.add(label4a);
    checkBox4 = new JCheckBox("auto");
    checkBox4
        .setToolTipText("if checked, calculates lighting during Fit to Screen");
    CSH.setHelpIDString(checkBox4, "lighting_auto_modify_view_checkbox");
    checkBox4.setBackground(subpanel4.getBackground());
    checkBox4.setPreferredSize(dimension4);
    subpanel4.add(checkBox4);
    checkBox4.setSelected(session.plotManager.view.AUTOCOMPOSE_LIGHTING_MODE);
    final JLabel label4b = new JLabel("Ramp:", SwingConstants.RIGHT);
    label4b.setToolTipText("how fast the light fades with distance from eye");
    CSH.setHelpIDString(label4b, "lighting_ramp_modify_view_label");
    label4b.setPreferredSize(dimension3);
    subpanel4.add(label4b);
    model4a = new SpinnerNumberModel(session.plotManager.view.lightRamp,
        Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 1.0);
    final JSpinner spinner4a = new JSpinner(model4a);
    CSH.setHelpIDString(spinner4a, "lighting_ramp_modify_view_spinner");
    spinner4a.setBorder(BorderFactory.createRaisedBevelBorder());
    spinner4a.setPreferredSize(new Dimension(100, 25));
    subpanel4.add(spinner4a);
    final JLabel label4c = new JLabel("Position:", SwingConstants.RIGHT);
    label4c.setToolTipText("where ramp is placed");
    CSH.setHelpIDString(label4c, "lighting_position_modify_view_label");
    label4c.setPreferredSize(dimension2);
    subpanel4.add(label4c);
    model4b = new SpinnerNumberModel(session.plotManager.view.lightPosition,
        Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 1.0);
    final JSpinner spinner4b = new JSpinner(model4b);
    CSH.setHelpIDString(spinner4b, "lighting_position_modify_view_spinner");
    spinner4b.setBorder(BorderFactory.createRaisedBevelBorder());
    spinner4b.setPreferredSize(new Dimension(100, 25));
    subpanel4.add(spinner4b);
    if (checkBox4.isSelected()) {
      spinner4a.setEnabled(false);
      spinner4b.setEnabled(false);
    }
    else {
      spinner4a.setEnabled(true);
      spinner4b.setEnabled(true);
    }
    checkBox4.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
        try {
          if (checkBox4.isSelected()) {
            spinner4a.setEnabled(false);
            spinner4b.setEnabled(false);
          }
          else {
            spinner4a.setEnabled(true);
            spinner4b.setEnabled(true);
          }
        }
        catch (final Throwable ex) {
          session.errorSupport("", ex, "lighting_auto_modify_view_checkbox_error");
        }
      }
    });
    vbox.add(new JSeparator(), 3);
    vbox.add(subpanel5, 4);
    vbox.add(new JSeparator(), 5);
    vbox.add(subpanel4, 6);
    vbox.add(new JSeparator(), 7);
    vbox.add(subpanel3, 8);
    vbox.add(new JSeparator(), 9);
    vbox.add(subpanel1, 10);
    vbox.add(new JSeparator(), 11);
    vbox.add(subpanel2, 12);
  }
  private static final long serialVersionUID = 1L;
}
