package gov.nih.ncgc.openhts.tool1.demo;

import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;

public final class DemoRobotDialog extends AbstractDialog {
  private final Session session;
  private final JRadioButton[] buttons;
  private int selected = -1;

  public DemoRobotDialog(final Session session, final DemoRobot demoRobot) {
    super(session, "Select demo to run.", true);
    this.session = session;
    setModal(true);
    setLayout(new BorderLayout());
    final JPanel panel = new JPanel();
    add(panel, BorderLayout.CENTER);
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    final ButtonGroup buttonGroup = new ButtonGroup();
    buttons = new JRadioButton[demoRobot.demos.size()];
    for (int i = 0; i < demoRobot.demos.size(); i++) {
      final Demo demo = demoRobot.demos.get(i);
      final JRadioButton button = new JRadioButton(demo.getName());
      buttonGroup.add(button);
      buttons[i] = button;
      panel.add(button);
    }
    add(getButtonPanel(), BorderLayout.SOUTH);
    // setPreferredSize(new Dimension(500, 500));
  }

  @Override
  protected boolean apply() {
    for (int i = 0; i < buttons.length; i++) {
      if (buttons[i].isSelected()) {
        selected = i;
        return true;
      }
    }
    session.errorNoSupport("Select a demo or Cancel.");
    return false;
  }

  public int getSelectedDemoIndex() {
    return selected;
  }

  private static final long serialVersionUID = 1L;
}
