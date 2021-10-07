package gov.nih.ncgc.openhts.tool1.plotManager;

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
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import gov.nih.ncgc.openhts.tool1.Palette;
import gov.nih.ncgc.openhts.tool1.Session;

public final class TextViewlet extends JPanel implements ChangeListener {
  private final Session session;
  JTextField stringTextField;
  String string; // main product, the text itself
  JButton colorButton, fontButton;
  SpinnerNumberModel xModel, yModel, zModel;
  SpinnerNumberModel model5;
  JSpinner spinner4a, spinner4b, spinner4c;
  JCheckBox checkBox;
  Text text;

  TextViewlet(final Session session) {
    this.session = session;
    final Box vbox = Box.createVerticalBox();
    final Dimension dimension = new Dimension(120, 25);
    final JPanel subpanel1 = new JPanel();
    CSH.setHelpIDString(subpanel1, "modify_text");
    subpanel1.setLayout(new FlowLayout(FlowLayout.LEFT));
    final JLabel label1 = new JLabel("Text: ", SwingConstants.RIGHT);
    label1.setToolTipText("text available for changing the propeties of");
    CSH.setHelpIDString(label1, "modify_text");
    label1.setPreferredSize(dimension);
    subpanel1.add(label1);
    stringTextField = new JTextField(34);
    stringTextField.setToolTipText("type your text in here");
    CSH.setHelpIDString(stringTextField, "modify_text");
    stringTextField.setPreferredSize(new Dimension(360, 25));
    stringTextField.setBorder(BorderFactory.createLoweredBevelBorder());
    stringTextField.setText(string);
    subpanel1.add(stringTextField);
    vbox.add(subpanel1);
    final JPanel subpanel3 = new JPanel();
    CSH.setHelpIDString(subpanel3, "modify_text");
    subpanel3.setLayout(new FlowLayout(FlowLayout.LEFT));
    final JLabel label2 = new JLabel("Color: ", SwingConstants.RIGHT);
    label2.setToolTipText("the color that your text will be on the canvas");
    CSH.setHelpIDString(label2, "color_modify_text_label");
    label2.setPreferredSize(dimension);
    subpanel3.add(label2);
    colorButton = new JButton(/*Palette.getColorName(text.color)*/);
    colorButton.setToolTipText("click to choose the color of your text");
    CSH.setHelpIDString(colorButton, "color_modify_text_button");
    colorButton.setBorder(BorderFactory.createRaisedBevelBorder());
    //colorButton.setBackground(text.color);
    //colorButton.setForeground(ColorChooserDialog.getComplement(text.color));
    colorButton.setPreferredSize(dimension);
//    final ColorChooser colorChooser = new ColorChooser(session, "text colors");
//    colorButton.addActionListener(new ActionListener() {
//      public void actionPerformed(final ActionEvent e) {
//        try {
//          final ColorChooserDialog colorChooserDialog = session.dialogManager
//              .getColorChooserModifyDialog();
//          colorChooserDialog.setModal(true);
//          colorChooserDialog.showDialog();
//          Palette.Choice choice = colorChooserDialog.getChoice();
//          final Color newColor = choice.getColor();
//          if (newColor != null) {
//            colorButton.setBackground(newColor);
//            colorButton.setText(choice.toString());
//            colorButton.setForeground(choice.getTextColor());
//          }
//        }
//        catch (final Throwable ex) {
//          session.errorSupport("While changing color: ", ex,
//              "color_modify_text_button_error");
//        }
//      }
//    });
    subpanel3.add(colorButton);
    final JLabel label3 = new JLabel("Font: ", SwingConstants.RIGHT);
    label3.setToolTipText("the font of your text");
    CSH.setHelpIDString(label3, "font_modify_text_label");
    label3.setPreferredSize(dimension);
    subpanel3.add(label3);
    final FontChooserDialog fontChooserDialog = session.dialogManager
        .getFontChooserChooseDialog();
    // don't make visible, will return default
    fontButton = new JButton(fontChooserDialog.getFont().getName());
    fontButton.setToolTipText("click to choose the font of your text");
    CSH.setHelpIDString(fontButton, "font_modify_text_button");
    fontButton.setBorder(BorderFactory.createRaisedBevelBorder());
    fontButton.setPreferredSize(dimension);
    fontButton.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
        try {
          final FontChooserDialog fontChooserDialog = session.dialogManager
              .getFontChooserChooseDialog();
          fontChooserDialog.setModal(true);
          fontChooserDialog.showDialog();
          if (fontChooserDialog.isOk()) {
            fontButton.setText(fontChooserDialog.getFont().getName());
          }
        }
        catch (final Throwable ex) {
          session.errorSupport("While changing font:", ex,
              "font_modify_text_button_error");
        }
      }
    });
    subpanel3.add(fontButton);
    vbox.add(subpanel3);
    final JPanel subpanel5 = new JPanel();
    CSH.setHelpIDString(subpanel5, "modify_text");
    subpanel5.setLayout(new FlowLayout(FlowLayout.LEFT));
    final JLabel label4 = new JLabel("Position:", SwingConstants.RIGHT);
    label4.setToolTipText("coordinates of the center of the text string");
    CSH.setHelpIDString(label4, "position_modify_text_label");
    label4.setPreferredSize(dimension);
    subpanel5.add(label4);
    final Dimension d = new Dimension(17, 25);
    final JLabel label4a = new JLabel("X", SwingConstants.RIGHT);
    label4a.setToolTipText("x coordinate of the center of text string");
    CSH.setHelpIDString(label4a, "position_x_modify_text_label");
    label4a.setPreferredSize(d);
    subpanel5.add(label4a);
    xModel = new SpinnerNumberModel(Float.POSITIVE_INFINITY/*text.start[0] 
        + session.plotManager.subject.cumDelta[0]*/, Float.NEGATIVE_INFINITY,
        Float.POSITIVE_INFINITY, 1.0);
    spinner4a = new JSpinner(xModel);
    CSH.setHelpIDString(spinner4a, "position_x_modify_text_spinner");
    spinner4a.setBorder(BorderFactory.createLoweredBevelBorder());
    spinner4a.setOpaque(false);
    spinner4a.setPreferredSize(new Dimension(100, 25));
    subpanel5.add(spinner4a);
    final JLabel label4b = new JLabel("Y", SwingConstants.RIGHT);
    label4b.setToolTipText("y coordinate of the center of text string");
    CSH.setHelpIDString(label4b, "position_y_modify_text_label");
    label4b.setPreferredSize(d);
    subpanel5.add(label4b);
    yModel = new SpinnerNumberModel(Float.POSITIVE_INFINITY/*text.start[1]
        + session.plotManager.subject.cumDelta[1]*/, Float.NEGATIVE_INFINITY,
        Float.POSITIVE_INFINITY, 1.0);
    spinner4b = new JSpinner(yModel);
    CSH.setHelpIDString(spinner4b, "position_y_modify_text_spinner");
    spinner4b.setBorder(BorderFactory.createLoweredBevelBorder());
    spinner4b.setOpaque(false);
    spinner4b.setPreferredSize(new Dimension(100, 25));
    subpanel5.add(spinner4b);
    final JLabel label4c = new JLabel("Z", SwingConstants.RIGHT);
    label4c.setToolTipText("z coordinate of center of text");
    CSH.setHelpIDString(label4c, "position_z_modify_text_label");
    label4c.setPreferredSize(d);
    subpanel5.add(label4c);
    zModel = new SpinnerNumberModel(Float.POSITIVE_INFINITY/*text.start[2]
        + session.plotManager.subject.cumDelta[2]*/, Float.NEGATIVE_INFINITY,
        Float.POSITIVE_INFINITY, 1.0);
    spinner4c = new JSpinner(zModel);
    CSH.setHelpIDString(spinner4c, "position_z_modify_text_spinner");
    spinner4c.setBorder(BorderFactory.createLoweredBevelBorder());
    spinner4c.setPreferredSize(new Dimension(100, 25));
    if (session.plotManager.getDim() == 2) {
      label4c.setVisible(false);
      spinner4c.setVisible(false);
    }
    subpanel5.add(spinner4c);
    vbox.add(subpanel5);
    // NEXT PANEL: SIX
    final JPanel subpanel8 = new JPanel();
    CSH.setHelpIDString(subpanel8, "modify_text");
    subpanel8.setLayout(new FlowLayout(FlowLayout.LEFT));
    final JLabel spaces = new JLabel("");
    CSH.setHelpIDString(subpanel8, "modify_text");
    spaces.setPreferredSize(dimension);
    subpanel8.add(spaces);
    checkBox = new JCheckBox("Move with the axes");
    checkBox
        .setToolTipText("if not checked, text will remain fixed on screen during rotations");
    CSH.setHelpIDString(checkBox, "move_modify_text_checkbox");
    checkBox.setBackground(subpanel8.getBackground());
    //checkBox.setSelected(text.moving);
    checkBox.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
        try {
          final float[] f = new float[3];
          f[0] = ((Double) xModel.getValue()).floatValue();
          f[1] = ((Double) yModel.getValue()).floatValue();
          f[2] = ((Double) zModel.getValue()).floatValue();
          if (checkBox.isSelected()) { // change from screen to subject
            // coordinates
            if (session.plotManager.getDim() == 2) {
              //text.start = session.plotManager.view.unTransform2D(f);
            }
            else if (session.plotManager.getDim() == 3) {
              //text.start = session.plotManager.view.unTransform3D(f);
              spinner4c.setVisible(true);
              label4c.setVisible(true);
            }
//            xModel.setValue(new Double(text.start[0]
//                + session.plotManager.subject.cumDelta[0]));
//            yModel.setValue(new Double(text.start[1]
//                + session.plotManager.subject.cumDelta[1]));
//            zModel.setValue(new Double(text.start[2]
//                + session.plotManager.subject.cumDelta[2]));
          }
          else { // change from subject to screen coordinates
//            text.start[0] = f[0] - session.plotManager.subject.cumDelta[0];
//            text.start[1] = f[1] - session.plotManager.subject.cumDelta[1];
//            text.start[2] = f[2] - session.plotManager.subject.cumDelta[2];
//            if (session.plotManager.getDim() == 2) {
//              text.transform2D(true);
//            }
//            else if (session.plotManager.getDim() == 3) {
//              text.transform3D(true);
//            }
//            text.start[0] = text.tStart[0];
//            text.start[1] = text.tStart[1];
//            text.start[2] = text.tStart[2];
            spinner4c.setVisible(false);
            label4c.setVisible(false);
            xModel.setValue(new Double(text.start[0]));
            yModel.setValue(new Double(text.start[1]));
            zModel.setValue(new Double(text.start[2]));
          }
//          text.moving = checkBox.isSelected();
        }
        catch (final Throwable ex) {
          session.errorSupport("", ex, "move_modify_text_checkbox_error");
        }
      }
    });
    subpanel8.add(checkBox);
    vbox.add(subpanel8);
    add(vbox);
  }

  private static final long serialVersionUID = 1L;

  @Override
	public void stateChanged(ChangeEvent e) {
    Text selectedText = (Text)e.getSource();
    stringTextField.setText(selectedText.getValue());
    colorButton.setBackground(selectedText.color);
    Palette.Choice choice = Palette.getClosestChoice(selectedText.color);
    colorButton
        .setText(choice.toString());
    colorButton.setForeground(choice.getColor());
    fontButton.setText(selectedText.getFontName());
    spinner4a.setValue(new Double(selectedText.start[0]
        + session.plotManager.subject.cumDelta[0]));
    spinner4b.setValue(new Double(selectedText.start[1]
        + session.plotManager.subject.cumDelta[1]));
    spinner4c.setValue(new Double(selectedText.start[2]
        + session.plotManager.subject.cumDelta[2]));
    checkBox.setSelected(selectedText.moving);
  }
}
