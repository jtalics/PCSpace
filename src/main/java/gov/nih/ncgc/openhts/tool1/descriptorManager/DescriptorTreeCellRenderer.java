/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.descriptorManager;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import gov.nih.ncgc.openhts.tool1.AppLafManager;
import gov.nih.ncgc.openhts.tool1.Session;

/** Purpose is to ...
 * @author talafousj
 *
 */
class DescriptorTreeCellRenderer extends DefaultTreeCellRenderer {
  private final Session session;

  public DescriptorTreeCellRenderer(final Session session) {
    this.session = session;
  }

  @Override
  public Component getTreeCellRendererComponent(final JTree tree,
      final Object value, final boolean isSelected, final boolean expanded,
      final boolean leaf, final int row, final boolean hasFocus) {
    final JLabel textLabel1 = new JLabel();
    final JLabel textLabel2 = new JLabel();
    JPanel panel;
    if (value instanceof DescriptorTreeNode) {
      final DescriptorTreeNode ptn = (DescriptorTreeNode) value;
      panel = ptn.getPanel();
      panel.add(new JLabel(AppLafManager.getIcon(AppLafManager.IconKind.Descriptor)));
      panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
      final Descriptor descriptor = (Descriptor) ptn.getUserObject();
      String text;
      // TODO: get rid of next line
      if (descriptor != null) {
        text = descriptor.toString();
        if (text == null || text.isEmpty()) {
          text = "error: no text";
        }
      }
      else {
        text = "error: no descriptor";
      }
      textLabel1.setText(text);
//      textPanel.setMinimumSize(new Dimension(100, 100));
      panel.add(textLabel1);
      // textLabel1.setFont(tree.getFont().deriveFont(Font.ITALIC));
      textLabel1.setFont(tree.getFont());
      textLabel2.setText(" !");
      panel.add(textLabel2);
      final String family = textLabel1.getFont().getFamily();
      final int size = textLabel1.getFont().getSize();
      textLabel1.setFont(new Font(family, Font.PLAIN, size));
      panel.revalidate();
    }
    else if (value instanceof BasisTreeNode) {
      // NOTE: POINTSET MANAGER USES SIMILAR CODE
      final BasisTreeNode btn = (BasisTreeNode) value;
      panel = btn.getPanel();
      panel.add(new JLabel(AppLafManager.getIcon(AppLafManager.IconKind.Basis)));

      textLabel1.setText(((BasisTreeNode)value).getUserObject().toString());
      textLabel1.setFont(tree.getFont());
      panel.add(textLabel1);
      final String family = textLabel1.getFont().getFamily();
      final int size = textLabel1.getFont().getSize();
      final Basis basis = (Basis) btn.getUserObject();
      if (false /* use bold for a different property session.plotManager.basis == basis*/) {
        if (basis.isStale()) {
          textLabel1.setFont(new Font(family, Font.BOLD | Font.ITALIC, size));
        }
        else {
          textLabel1.setFont(new Font(family, Font.BOLD, size));
        }
      }
      else {
        if (basis.isStale()) {
          textLabel1.setFont(new Font(family, Font.ITALIC, size));
        }
        else {
          textLabel1.setFont(new Font(family, Font.PLAIN, size));
        }
      }
    }
    else if (tree.getModel().getRoot() == value) {
      panel = new JPanel();
      switch (session.descriptorManager.treeModel.getMode()) {
      case BasisLeaf:
        textLabel1.setText("DESCRIPTORS / BASES");
        panel.add(textLabel1);
        textLabel2.setText("");
        break;
      case DescriptorLeaf:
        textLabel1.setText("Bases / descriptors");
        panel.add(textLabel1);
        textLabel2.setText("");
        break;
      default:
        textLabel1.setText("error: " + value.getClass().getSimpleName());
        panel.add(textLabel1);
        textLabel2.setText("");
      }
    }
    else if (((DefaultMutableTreeNode) tree.getModel().getRoot()).getChildAt(0) == value) {
      panel = new JPanel();
      switch (session.descriptorManager.treeModel.getMode()) {
      case BasisLeaf:
        textLabel1.setText("FREE BASES");
        panel.add(textLabel1);
        textLabel2.setText("");
        break;
      case DescriptorLeaf:
        textLabel1.setText("Free descriptors");
        panel.add(textLabel1);
        textLabel2.setText("");
        break;
      default:
        textLabel1.setText("error: " + value.getClass().getSimpleName());
        panel.add(textLabel1);
        textLabel2.setText("");
      }
    }
    else {
      panel = new JPanel();
      textLabel1.setText("error: " + value.getClass().getSimpleName());
      panel.add(textLabel1);
      textLabel2.setText("");
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
    panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    panel.validate();
    return panel;
  }

  private static final long serialVersionUID = 1L;
}
