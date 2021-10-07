package gov.nih.ncgc.openhts.tool1.dialogManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import gov.nih.ncgc.openhts.tool1.Palette;
import gov.nih.ncgc.openhts.tool1.Session;

public final class ColorChooserDialog extends AbstractDialog {
  private final Session session;
  private Palette.Choice choice;

  public ColorChooserDialog(final Session session) {
    super(session);
    this.session = session;
    addWindowListener(session.dialogManager);
    addComponentListener(session.dialogManager);
    setModal(true);
    setLayout(new BorderLayout());
    final JPanel mainPanel = new JPanel();
    add(mainPanel, BorderLayout.CENTER);
    mainPanel.setLayout(new BorderLayout());
    final JPanel subpanel1 = new JPanel();
    mainPanel.add(subpanel1, BorderLayout.NORTH);
    subpanel1.setToolTipText("TODO");
    subpanel1.setLayout(new BorderLayout());
    subpanel1.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.NORTH);
    subpanel1.add(Box.createRigidArea(new Dimension(7, 0)), BorderLayout.WEST);
    subpanel1.add(Box.createRigidArea(new Dimension(7, 0)), BorderLayout.EAST);
    subpanel1.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.SOUTH);
    final JPanel subpanel2 = new JPanel();
    subpanel2.setLayout(new BorderLayout());
    subpanel2.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.NORTH);
    subpanel2.add(Box.createRigidArea(new Dimension(7, 0)), BorderLayout.WEST);
    subpanel2.add(Box.createRigidArea(new Dimension(7, 0)), BorderLayout.EAST);
    subpanel2.add(Box.createRigidArea(new Dimension(0, 8)), BorderLayout.SOUTH);
    final JPanel colorPanel = new JPanel();
    colorPanel.setLayout(new GridLayout(0, 2, 3, 3));
    for (final Palette.Choice choice : Palette.Choice.values()) {
      final JButton button = new JButton(choice.toString());
      button.setBackground(choice.getColor());
      button.setForeground(choice.getTextColor());
      button.setBorder(BorderFactory.createEmptyBorder(1,1,1,1)); // TODO: indicate selection with border
      button.addActionListener(new ActionListener() {
        @Override
				public void actionPerformed(ActionEvent e) {
          ColorChooserDialog.this.choice = choice;
          doOk();
        }
      });
      colorPanel.add(button);
    }
    subpanel2.add(colorPanel, BorderLayout.CENTER);
    mainPanel.add(subpanel2, BorderLayout.CENTER);
  }

  public Palette.Choice getChoice() {
    return choice;
  }

  /**
   * Called when user clicks OK or Apply button
   */
  @Override
  protected boolean apply() {
    if (choice == null) {
      session.errorNoSupport("Select a color or close window.");
      return false;
    }
    return true;
  }

  public void setChoice(Palette.Choice choice) {
    this.choice = choice;
  }

  private static final long serialVersionUID = 1L;
}
