package gov.nih.ncgc.openhts.tool1.plotManager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.help.CSH;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;
import gov.nih.ncgc.openhts.tool1.dialogManager.DialogManager;

/** Purpose is to ...
 * @author talafousj
 */
public final class FontChooserDialog extends AbstractDialog {
  private final Session session;
  private JComboBox comboBox;
  private JButton size10, size12, size14, size18, size24;
  private Font selectedFont = Session.fonts[0];
  
  public FontChooserDialog(final Session session) {
    super(session);
    this.session = session;
    //this.fontChooser = new FontChooser(session);
    setTitle("Text - Fonts");
    setModal(true);
    setResizable(false);
    addWindowListener(session.dialogManager);
    addComponentListener(DialogManager.getInstance(session));
    final JPanel topPanel = new JPanel();
    add(topPanel, BorderLayout.CENTER);
    CSH.setHelpIDString(topPanel, "font_chooser");
    topPanel.setLayout(new BorderLayout());
    final JPanel northPanel = new JPanel();
    CSH.setHelpIDString(northPanel, "font_chooser");
    northPanel.setLayout(new BorderLayout(0, 5));
    final JPanel mixPanel = new JPanel();
    mixPanel.setLayout(new BorderLayout());
    CSH.setHelpIDString(mixPanel, "font_chooser");
    final JPanel subpanel1 = new JPanel();
    subpanel1.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
    CSH.setHelpIDString(subpanel1, "font_chooser");
    final JLabel fontstyle = new JLabel("Font style to use:", SwingConstants.LEFT);
    fontstyle.setToolTipText("available fonts");
    CSH.setHelpIDString(fontstyle, "style_font_chooser_label");
    subpanel1.add(fontstyle);
    comboBox = new JComboBox(Session.fonts);
    comboBox.setToolTipText("click to change font style");
    CSH.setHelpIDString(comboBox, "style_font_chooser_combobox");
    comboBox.setBorder(BorderFactory.createRaisedBevelBorder());
    comboBox.setRenderer(new ListCellRenderer(){
      
      @Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = new JLabel();
        Font font = (Font)value;
        label.setText(font.getFontName()+" "+font.getSize()+"pt");
        return label;
      }
      
    });
    int i;
    for (i = 0; i < Session.fonts.length; i++) {
      // System.out.println(font.getName() +"=?"+ fonts[i].getName());
      if (Session.fonts[i].equals(selectedFont)) {
        break;
      }
    }
    if (i >= Session.fonts.length) { // missed
      i = 0;
    }
    comboBox.setSelectedIndex(i);
    comboBox.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
        try {
          final JComboBox combobox = (JComboBox) e.getSource();
          final int index = combobox.getSelectedIndex();
          final String buttontext = ((Font)comboBox.getSelectedItem()).getName();
          size10.setText(buttontext + "-10");
          size10.setFont(Font.decode(buttontext + "-10"));
          size12.setText(buttontext + "-12");
          size12.setFont(Font.decode(buttontext + "-12"));
          size14.setText(buttontext + "-14");
          size14.setFont(Font.decode(buttontext + "-14"));
          size18.setText(buttontext + "-18");
          size18.setFont(Font.decode(buttontext + "-18"));
          size24.setText(buttontext + "-24");
          size24.setFont(Font.decode(buttontext + "-24"));
          repaint();
        }
        catch (final Throwable ex) {
          session.errorSupport("While changing font: ", ex,
              "size_font_chooser_combobox_error");
        }
      }
    });
    subpanel1.add(comboBox);
    mixPanel.add(subpanel1, BorderLayout.NORTH);
    final JPanel subpanel2 = new JPanel();
    subpanel2.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
    CSH.setHelpIDString(subpanel2, "font_chooser");
    final JLabel selectsize = new JLabel("Select desired size of this font:",
        SwingConstants.LEFT);
    selectsize.setToolTipText("size of font in numbers");
    CSH.setHelpIDString(selectsize, "size_font_chooser_label");
    subpanel2.add(selectsize);
    mixPanel.add(subpanel2, BorderLayout.SOUTH);
    northPanel.add(mixPanel, BorderLayout.CENTER);
    northPanel
        .add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.NORTH);
    northPanel.add(Box.createRigidArea(new Dimension(7, 0)), BorderLayout.WEST);
    northPanel.add(Box.createRigidArea(new Dimension(7, 0)), BorderLayout.EAST);
    northPanel
        .add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.SOUTH);
    topPanel.add(northPanel, BorderLayout.NORTH);
    topPanel.add(getCenterPanel(),BorderLayout.CENTER);
    final JPanel buttonPanel = getButtonPanel();
    CSH.setHelpIDString(buttonPanel, "font_chooser");
    topPanel.add(buttonPanel, BorderLayout.SOUTH);
    setHelpID("help_font_chooser_button");
  }

  private Component getCenterPanel() {
    final JPanel centerPanel = new JPanel();
    CSH.setHelpIDString(centerPanel, "font_chooser");
    centerPanel.setLayout(new GridLayout(0, 1, 5, 5));
    final String buttontext = "font";//selectedFont.getName();
    size10 = new JButton(buttontext + "-10");
    size10.setToolTipText("click to set font to size of 10 numbers");
    CSH.setHelpIDString(size10, "size_font_chooser_button");
    size10.setFont(Font.decode(buttontext + "-10"));
    size10.setBorder(BorderFactory.createRaisedBevelBorder());
    size10.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
        try {
          setFontFromButton(size24);
        }
        catch (final Throwable ex) {
          session.errorSupport("While changing font:", ex,
              "size_font_chooser_button_error");
        }
      }
    });
    centerPanel.add(size10);
    size12 = new JButton(buttontext + "-12");
    size12.setToolTipText("click to set font to size of 12 numbers");
    CSH.setHelpIDString(size12, "size_font_chooser_button");
    size12.setFont(Font.decode(buttontext + "-12"));
    size12.setBorder(BorderFactory.createRaisedBevelBorder());
    size12.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
        try {
          setFontFromButton(size24);
        }
        catch (final Throwable ex) {
          session.errorSupport("While changing font:", ex,
              "size_font_chooser_button_error");
        }
      }
    });
    centerPanel.add(size12);
    size14 = new JButton(buttontext + "-14");
    size14.setToolTipText("click to set font to size of 14 numbers");
    CSH.setHelpIDString(size14, "size_font_chooser_button");
    size14.setFont(Font.decode(buttontext + "-14"));
    size14.setBorder(BorderFactory.createRaisedBevelBorder());
    size14.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
        try {
          setFontFromButton(size14);
        }
        catch (final Throwable ex) {
          session.errorSupport("While changing font:", ex,
              "size_font_chooser_button_error");
        }
      }
    });
    centerPanel.add(size14);
    size18 = new JButton(buttontext + "-18");
    size18.setToolTipText("click to set font to size of 18 numbers");
    CSH.setHelpIDString(size18, "size_font_chooser_button");
    size18.setFont(Font.decode(buttontext + "-18"));
    size18.setBorder(BorderFactory.createRaisedBevelBorder());
    size18.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
        try {
          setFontFromButton(size18);
        }
        catch (final Throwable ex) {
          session.errorSupport("While changing font:", ex,
              "size_font_chooser_button_error");
        }
      }
    });
    centerPanel.add(size18);
    size24 = new JButton(buttontext + "-24");
    size24.setToolTipText("click to set font to size of 24 numbers");
    CSH.setHelpIDString(size24, "size_font_chooser_button");
    size24.setFont(Font.decode(buttontext + "-24"));
    size24.setBorder(BorderFactory.createRaisedBevelBorder());
    size24.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
        try {
          setFontFromButton(size24);
        }
        catch (final Throwable ex) {
          session.errorSupport("While changing font:", ex,
              "size_font_chooser_button_error");
        }
      }
    });
    centerPanel.add(size24);
    return centerPanel;
  }

  private void setFontFromButton(final JButton button) {
    selectedFont = Font.decode(button.getText());
    super.isOk();
  }

  @Override
  protected boolean apply() {
    if (selectedFont == null) {
      session.errorNoSupport("Select a font or Cancel.");
      return false;
    }
    return true;
  }

  public Font getSelectedFont() {
    return selectedFont;
  }

  public Font setSelectedFont(Font font) {
    return selectedFont;
  }
  
  private static final long serialVersionUID = 1L;
}
