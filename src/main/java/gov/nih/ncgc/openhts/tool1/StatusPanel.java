
package gov.nih.ncgc.openhts.tool1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class StatusPanel extends JPanel {
  private static final long serialVersionUID = 2006122601020018001L;
  private JLabel primaryStatusLabel = null;
  private JPanel primaryStatusPanel = null;
  private JPanel secondaryStatusPanel = null;
  private JLabel secondaryStatusLabel = null;
  private final Deque<String> log2Deque = new ArrayDeque<String>();
  private final Session session;

  public StatusPanel(Session session) {
    this.session = session;
    initialize();
  }

  public void log1(final String statusMsg) {
    primaryStatusLabel.setText(statusMsg);
  }

  public void log2(final String statusMsg) {
    log2Deque.offer(statusMsg);
    if (log2Deque.size() > 500) {
      log2Deque.pollFirst();
    }
    secondaryStatusLabel.setText(statusMsg);
    long max = Runtime.getRuntime().maxMemory();
    long total = Runtime.getRuntime().totalMemory();
    long used = total - Runtime.getRuntime().freeMemory();
    max /= 1000000;
    total /= 1000000;
    used /= 1000000;
    log1(used + "M of " + total + "M (" + max + "M max)");
  }

  private void initialize() {
    final GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
    gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints3.gridx = 1;
    gridBagConstraints3.gridy = 1;
    gridBagConstraints3.weightx = 0.25;
    final GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
    gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints2.gridx = 0;
    gridBagConstraints2.gridy = 1;
    gridBagConstraints2.weightx = 0.75;
    final GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 0.75;
    gridBagConstraints.gridwidth = 2;
    setLayout(new GridBagLayout());
    this.add(new JSeparator(), gridBagConstraints);
    this.add(getPrimaryStatusPanel(), gridBagConstraints2);
    this.add(getSecondaryStatusPanel(), gridBagConstraints3);
  }

  /**
   * This method initializes primaryStatusPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getPrimaryStatusPanel() {
    final GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.weightx = 1.0;
    primaryStatusLabel = new JLabel();
    primaryStatusLabel.setPreferredSize(new Dimension(300, 16));
    primaryStatusLabel.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
    primaryStatusPanel = new JPanel();
    primaryStatusPanel.setLayout(new GridBagLayout());
    primaryStatusPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    primaryStatusPanel.add(primaryStatusLabel, gridBagConstraints);
    return primaryStatusPanel;
  }

  /**
   * This method initializes secondaryStatusPanel
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getSecondaryStatusPanel() {
    secondaryStatusLabel = new JLabel();
    secondaryStatusLabel.setPreferredSize(new Dimension(200, 16));
    secondaryStatusPanel = new JPanel();
    secondaryStatusLabel.addMouseListener(new MouseListener() {

      @Override
			public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          session.dialogManager.getLogHistoryDialog().showDialog();
        }
      }

      @Override
			public void mouseEntered(MouseEvent e) {
        // TODO: bring up a window showing whole message, like a tooltip
      }

      @Override
			public void mouseExited(MouseEvent e) {
        // TODO: kill tool tip window
      }

      @Override
			public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        
      }

      @Override
			public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        
      }
    });
    secondaryStatusPanel.setBorder(BorderFactory.createEtchedBorder());
    secondaryStatusPanel.setLayout(new BorderLayout());
    secondaryStatusPanel.add(secondaryStatusLabel);
    return secondaryStatusPanel;
  }

  public Deque<String> getLog2Deque() {
    return log2Deque;
  }
}
