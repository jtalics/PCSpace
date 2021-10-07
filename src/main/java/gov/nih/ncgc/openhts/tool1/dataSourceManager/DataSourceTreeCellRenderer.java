/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.dataSourceManager;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.util.ProgressBar;

class DataSourceTreeCellRenderer extends DefaultTreeCellRenderer {
  private final Session session;

  public DataSourceTreeCellRenderer(final Session session) {
    this.session = session;
  }

  @Override
  public Component getTreeCellRendererComponent(final JTree tree,
      final Object value, final boolean isSelected, final boolean expanded,
      final boolean leaf, final int row, final boolean hasFocus) {
    final JPanel panel = new JPanel();
    final JLabel textLabel1 = new JLabel();
    final JLabel textLabel2 = new JLabel();
    panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    if (value instanceof DataSourceTreeNode) {
      final DataSourceTreeNode dstn = (DataSourceTreeNode) value;
      final DataSource dataSource = (DataSource) dstn.getUserObject();
      if (dataSource.isLoading()) {
        ProgressBar progressBar = dstn.getProgressBar();
        progressBar.setPreferredSize(new Dimension(100, tree.getRowHeight()));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        // set selection colors
        if (isSelected) {
          panel.setBackground(backgroundSelectionColor);
          UIManager.put("ProgressBar.selectionBackground",
              new javax.swing.plaf.ColorUIResource(backgroundSelectionColor));
          UIManager.put("ProgressBar.selectionForeground",
              new javax.swing.plaf.ColorUIResource(textSelectionColor));
        }
        else {
          panel.setBackground(tree.getBackground());
          UIManager.put("ProgressBar.selectionBackground",
              new javax.swing.plaf.ColorUIResource(tree.getBackground()));
          UIManager.put("ProgressBar.selectionForeground",
              new javax.swing.plaf.ColorUIResource(tree.getForeground()));
        }
        panel.add(progressBar);
      }
      else {
        panel.add(new JLabel(AppLafManager
            .getIcon(AppLafManager.IconKind.DataSource)));
        String text = dataSource.toString();
        if (text == null || text.isEmpty()) {
          text = "error: no text";
        }
        textLabel1.setText(text);
        panel.add(textLabel1);
        textLabel1.setFont(tree.getFont());
        textLabel2.setText(" !");
        panel.add(textLabel2);
        final String family = textLabel1.getFont().getFamily();
        final int size = textLabel1.getFont().getSize();
        textLabel1.setFont(new Font(family, Font.PLAIN, size));
        // panel.revalidate();
      }
    }
    else if (value instanceof DefaultMutableTreeNode) {
      textLabel1.setText(((DefaultMutableTreeNode) value).toString());
      textLabel2.setText("");
      panel.add(textLabel1);
      panel.add(textLabel2);
    }
    else {
      return new JLabel("error: " + value.getClass().getSimpleName());
    }
    // set selection colors
    if (isSelected) {
      panel.setBackground(backgroundSelectionColor);
      textLabel1.setForeground(textSelectionColor);
      textLabel1.setBackground(backgroundSelectionColor);
      textLabel2.setForeground(textSelectionColor);
      textLabel2.setBackground(backgroundSelectionColor);
    }
    else {
      panel.setBackground(backgroundNonSelectionColor);
      textLabel1.setForeground(textNonSelectionColor);
      textLabel1.setBackground(backgroundNonSelectionColor);
      textLabel2.setForeground(textNonSelectionColor);
      textLabel2.setBackground(backgroundNonSelectionColor);
    }
    panel.validate();
    return panel;
  }

  private static final long serialVersionUID = 1L;
}
