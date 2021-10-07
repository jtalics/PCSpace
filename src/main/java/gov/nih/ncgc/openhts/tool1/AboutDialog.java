package gov.nih.ncgc.openhts.tool1;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;

/**
 * 
 */
/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public final class AboutDialog extends AbstractDialog {
  private static final long serialVersionUID = 1L;
  private JTextArea textArea = null;

  public AboutDialog(final Window owner) {
    super(owner, true);
    setTitle("About");
    final JPanel cp = new JPanel();
    cp.setLayout(new BorderLayout());
    cp.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    textArea = new JTextArea();
    textArea.setOpaque(false);
    //label.setHorizontalAlignment(SwingConstants.CENTER);
    final JPanel aboutPane = new JPanel();
    aboutPane.setLayout(new GridBagLayout());
    aboutPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    aboutPane.add(textArea, new GridBagConstraints());
    cp.add(aboutPane, BorderLayout.CENTER);
    final ButtonPanel buttonPanel = getButtonPanel();
    buttonPanel.setHelpId("TODO");
    buttonPanel.setCancelButton(null);
    cp.add(buttonPanel, BorderLayout.SOUTH);
    setContentPane(cp);
  }

  public void showAboutDialog(final String text) {
    textArea.setText(text);
    pack();
    // Point loc = owner.getLocation();
    // loc.translate(20, 20);
    // setLocation(loc);
    setVisible(true);
  }

  public void updateUI() {
    SwingUtilities.updateComponentTreeUI(this);
    pack();
  }

  private String getAboutLabel(final String text) {
    final StringBuilder sb = new StringBuilder();
    sb.append("<html>");
    sb.append("<p style='font-size:large;color:blue;'>").append(
        Version.PRODUCT_NAME).append("</p>");
    sb.append("<br />");
    sb.append("<p>Version ").append(Version.ToString()).append("</p>");
    sb.append("<p>Copyleft 2007, NIH/NCGC.</p>");
    sb.append("<br />");
    sb.append("<p>Engine Version: ");
    sb.append(" NO ENGINE YET");
    sb.append(".");
    sb.append("</p>");
    sb.append("<br />");
    sb.append("<br>Auto-assembling data into sweet eye candy for you since 2001.");
    sb.append("</html>");
    return sb.toString();
  }

}
// end of file
