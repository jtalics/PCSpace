package gov.nih.ncgc.openhts.tool1.pointsetManager;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Basis;
import gov.nih.ncgc.openhts.tool1.descriptorManager.BasisTreeNode;
import gov.nih.ncgc.openhts.tool1.util.ProgressBar;

class PointsetTreeCellRenderer extends DefaultTreeCellRenderer {

  private final Session session;
  
  public PointsetTreeCellRenderer(final Session session) {
    this.session = session;
  }
  
  @Override
  public Component getTreeCellRendererComponent(final JTree tree, final Object value,
      final boolean isSelected, final boolean expanded, final boolean leaf, final int row,
      final boolean hasFocus) {
    final JLabel symbolLabel = new JLabel();
    symbolLabel.setOpaque(true);
    final JLabel nameLabel = new JLabel();
    nameLabel.setOpaque(true);
    JPanel panel;
    Component colorMap;
    if (value instanceof PointsetTreeNode) {
      final PointsetTreeNode ptn = (PointsetTreeNode) value;
      final Pointset pointset = (Pointset) ptn.getUserObject();
      panel = ptn.getPanel();
      if (pointset.isLoading()) {
        ProgressBar progressBar=ptn.getProgressBar();
        progressBar.setPreferredSize(new Dimension(100, 15));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
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
        JPanel upperPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        upperPanel.setOpaque(false);
        panel.add(upperPanel);
        JCheckBox checkBox = ptn.getCheckBox();
        //checkBox.setBackground(pointset.getColor());
        checkBox.setBorder(null);
        
        upperPanel.add(checkBox);
        upperPanel.add(nameLabel);
        upperPanel.add(symbolLabel);
        symbolLabel.setText(pointset.getShape().toString());
        symbolLabel.setBackground(pointset.getColor());
        symbolLabel.setFont(tree.getFont());
        //
        nameLabel.setText(pointset.getName()+": "+pointset.getPointCount());
        final String family = nameLabel.getFont().getFamily();
        final int size = nameLabel.getFont().getSize();
        if (false/* use for a different property pointset.isVisible()*/) {
          if (pointset.isStale()) {
            nameLabel.setFont(new Font(family, Font.BOLD | Font.ITALIC, size));
          }
          else {
            nameLabel.setFont(new Font(family, Font.BOLD, size));
          }
        }
        else {
          if (pointset.isStale()) {
            nameLabel.setFont(new Font(family, Font.ITALIC, size));
          }
          else {
            nameLabel.setFont(new Font(family, Font.PLAIN, size));
          }
        }
        ColumnHead colorizingColumnHead = pointset.getColorizingColumnHead();
        if (pointset.isVisible() && colorizingColumnHead != null) {
          colorMap = ptn.getColorMap();
          colorMap.setPreferredSize(new Dimension(100,15));
          panel.add(colorMap);
        }
      }
      panel.validate();
      int renderedHeight = panel.getPreferredSize().height;
      
      if (renderedHeight != ptn.lastRenderedHeight(renderedHeight)) {
        tree.setRowHeight(1);
        tree.setRowHeight(0);
      }
    }
    else if (value instanceof BasisTreeNode) {
      // NOTE: DESCRIPTOR MANAGER USES SIMLAR CODE
      final BasisTreeNode btn = (BasisTreeNode) value;
      panel = btn.getPanel();
      //panel.add(new JLabel(AppLafManager.getIcon(AppLafManager.IconKind.Basis)));
      panel.add(btn.getCheckBox());
      panel.add(nameLabel);
      panel.add(symbolLabel);
      nameLabel.setText(((BasisTreeNode)value).getUserObject().toString());
      nameLabel.setFont(tree.getFont());
      final String family = symbolLabel.getFont().getFamily();
      final int size = symbolLabel.getFont().getSize();
      final Basis basis = (Basis) btn.getUserObject();
      if (false/* save for another property session.plotManager.basis == basis*/) {
        if (basis.isStale()) {
          nameLabel.setFont(new Font(family, Font.BOLD | Font.ITALIC, size));
        }
        else {
          nameLabel.setFont(new Font(family, Font.BOLD, size));
        }
      }
      else {
        if (basis.isStale()) {
          nameLabel.setFont(new Font(family, Font.ITALIC, size));
        }
        else {
          nameLabel.setFont(new Font(family, Font.PLAIN, size));
        }
      }
    }
    else if (tree.getModel().getRoot() == value) {
      panel = new JPanel();
      nameLabel.setText("Pointset management");
      panel.add(nameLabel);
    }
    else {
      panel = new JPanel();
      nameLabel.setText("error: " + value.getClass().getSimpleName());
      panel.add(nameLabel);
    }
    // Set selection colors
    if (isSelected) {
      panel.setBackground(backgroundSelectionColor);
      nameLabel.setForeground(textSelectionColor);
      nameLabel.setBackground(backgroundSelectionColor);
    }
    else {
      panel.setBackground(backgroundNonSelectionColor);
      nameLabel.setForeground(textNonSelectionColor);
      nameLabel.setBackground(backgroundNonSelectionColor);
    }
    panel.validate();
    return panel;
  }

  private static final long serialVersionUID = 1L;
}
