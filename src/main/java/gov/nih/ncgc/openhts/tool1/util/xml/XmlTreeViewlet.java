package gov.nih.ncgc.openhts.tool1.util.xml;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.jidesoft.swing.JidePopupMenu;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.util.ContextTreePopupListener;

/**
 * Shows the unfiltered xml tree for possible editing.
 * 
 * @author talafousj
 */
public class XmlTreeViewlet extends JPanel {
  private static final long serialVersionUID = 1L;
  private final Document document;
  private final Node domNode;
  private final boolean editable;
  private final String description;
  private final String displayName;

  XmlTreeViewlet(final Document document, final Node domNode,
      final String displayName, final boolean editable, final String description) {
    this.document = document;
    this.domNode = domNode;
    this.editable = editable;
    this.description = description;
    this.displayName = displayName;
  }

  XmlTreeViewlet build() {
    removeAll();
    final DefaultMutableTreeNode tn = new DefaultMutableTreeNode(domNode);
    // Configure the JTree
    final JTree tree = new JTree(new DefaultTreeModel(processNode(tn)));
    final DefaultTreeCellRenderer defaultTreeCellRenderer = new XmlTreeCellRenderer();
    tree.setCellRenderer(defaultTreeCellRenderer);
    tree.setCellEditor(new XmlTreeCellEditor(tree, defaultTreeCellRenderer));
    // xmlTree.setName("xml tree for" + paramName);
    tree.setSelectionRow(0);
    for (int row = 0; row < tree.getRowCount(); row++) {
      tree.collapseRow(row);
    }
    tree.setSelectionRow(-1);
    setLayout(new BorderLayout());
    add(tree, BorderLayout.CENTER);
    // add(new JLabel(""), BorderLayout.CENTER);
    // final JLabel displayLabel = new JLabel(displayName);
    // displayLabel.setIcon(AppLafManager.getIcon(AppLafManager.IconKind.ICON_LOCKED));
    // displayLabel.setFont(GuiFrame.PRIORITY_FONT);
    // displayLabel.setToolTipText(description);
    // add(displayLabel, BorderLayout.WEST);
    setBorder(BorderFactory.createEtchedBorder());
    tree.setEditable(editable);
    // if (editable) {
    // //xmlTree.setsetBackground(Color.GRAY);
    // addEditButtons(tree);
    // }
    // else {
    // tree.setBackground(Color.LIGHT_GRAY);
    // }
    tree.setRootVisible(true);
    tree.setAutoscrolls(true);
    tree.setShowsRootHandles(true);
    tree.addMouseListener(new ContextTreePopupListener(null, tree) {
      final class EditAction extends AbstractAction {
        EditAction() {
          super("Edit node", null);
        }

        @Override
				public void actionPerformed(final ActionEvent arg0) {
          tree.startEditingAtPath(tree.getSelectionPaths()[0]);
        }

        private static final long serialVersionUID = 1L;
      }

      @Override
      public JidePopupMenu createContextMenu() {
        final JidePopupMenu popup = new JidePopupMenu();
        final int nSelectedPaths = tree.getSelectionPaths().length;
        if (nSelectedPaths == 0) {
          popup.add(new JLabel("nothing selected -- no actions apply"));
        }
        else if (nSelectedPaths == 1) {
          // Only one item selected
          if (tree.getSelectionPaths()[0].getPathCount() > 0) {
            popup.add(new JMenuItem(new EditAction()));
            popup.add(new JSeparator());
          }
        }
        else if (nSelectedPaths > 1) {
          popup.add(new JLabel("multiple selections -- no actions apply"));
        }
        return popup;
      }
    });
    Session.addFocusBorder(tree, tree);
    return this;
  }

  private void addEditButtons(final JTree xmlTree) {
    xmlTree.setBackground(Color.WHITE);
    final JButton addNodeButton = new JButton("Create subnode");
    addNodeButton.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent arg0) {
        if (!xmlTree.isEditable()) {
          Toolkit.getDefaultToolkit().beep();
          return;
        }
        final DefaultMutableTreeNode selectedTreeNode = (DefaultMutableTreeNode) xmlTree
            .getLastSelectedPathComponent();
        if (selectedTreeNode == null
            || ((Node) selectedTreeNode.getUserObject()).getNodeType() != Node.ELEMENT_NODE) {
          JOptionPane.showMessageDialog(null, "Illegal to place subnode here");
          return;
        }
        final String newTagName = JOptionPane
            .showInputDialog("New XML tag name");
        Node newDomNode;
        try {
          newDomNode = document.createElement(newTagName);
        }
        catch (final DOMException e) {
          JOptionPane.showMessageDialog(null, "Invalid XML tag name");
          return;
        }
        newDomNode.setNodeValue("new node");
        ((Node) selectedTreeNode.getUserObject()).appendChild(newDomNode);
        final DefaultMutableTreeNode newTreeNode = new DefaultMutableTreeNode(
            newDomNode);
        final DefaultTreeModel defaultTreeModel = (DefaultTreeModel) xmlTree
            .getModel();
        defaultTreeModel.insertNodeInto(newTreeNode, selectedTreeNode, selectedTreeNode
            .getChildCount());
        final TreeNode[] nodes = defaultTreeModel.getPathToRoot(newTreeNode);
        final TreePath path = new TreePath(nodes);
        xmlTree.scrollPathToVisible(path);
        xmlTree.setSelectionPath(path);
        xmlTree.startEditingAtPath(path);
      }
    });
    final JButton editNodeButton = new JButton("Edit Node Value");
    editNodeButton.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent arg0) {
        if (!xmlTree.isEditable()) {
          Toolkit.getDefaultToolkit().beep();
          return;
        }
        final TreePath selectionPath = xmlTree.getSelectionPath();
        if (selectionPath != null) {
          final DefaultMutableTreeNode selTreeNode = (DefaultMutableTreeNode) xmlTree
              .getLastSelectedPathComponent();
          if (((Node) selTreeNode.getUserObject()).getNodeValue() == null) {
            JOptionPane.showMessageDialog(null,
                "This xml/dom node may not be edited since it has no values.");
            return;
          }
          xmlTree.startEditingAtPath(selectionPath);
        }
      }
    });
    final JButton deleteNodeButton = new JButton("Delete Node");
    deleteNodeButton.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent arg0) {
        if (!xmlTree.isEditable()) {
          Toolkit.getDefaultToolkit().beep();
          return;
        }
        final DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) xmlTree
            .getLastSelectedPathComponent();
        if (selNode == null || selNode.isRoot()) {
          JOptionPane.showMessageDialog(null, "Illegal to delete this node.");
          return;
        }
        final DefaultTreeModel defaultTreeModel = (DefaultTreeModel) xmlTree
            .getModel();
        defaultTreeModel.removeNodeFromParent(selNode);
      }
    });
    final JPanel buttonPanel = new JPanel();
    buttonPanel.add(addNodeButton);
    buttonPanel.add(editNodeButton);
    buttonPanel.add(deleteNodeButton);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  private class XmlTreeCellRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(final JTree tree,
        final Object obj, final boolean selected, final boolean expanded,
        final boolean leaf, final int row, final boolean hasFocus) {
      // System.out.println("obj type = " + obj.getClass().getSimpleName());
      final DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) obj;
      // System.out.println("Rendering cell with domNode =
      // "+dmtn.getUserObject()+" of type = "
      // +dmtn.getUserObject().getClass().getSimpleName());
      final Node domNode = (Node) dmtn.getUserObject();
      String retval = domNode.getNodeName();
      final String value = domNode.getNodeValue();
      if (value != null) {
        retval += " = " + value;
      }
      final JLabel label = new JLabel(retval);
      label.setOpaque(true);
      if (selected) {
        label.setForeground(textSelectionColor);
        label.setBackground(backgroundSelectionColor);
      }
      else {
        label.setForeground(textNonSelectionColor);
        label.setBackground(backgroundNonSelectionColor);
      }
      return label;
    }

    private static final long serialVersionUID = 1L;
  }

  private class XmlTreeCellEditor extends DefaultTreeCellEditor {
    public XmlTreeCellEditor(final JTree arg0,
        final DefaultTreeCellRenderer arg1) {
      super(arg0, arg1);
    }

    @Override
    public Component getTreeCellEditorComponent(final JTree tree,
        final Object obj, final boolean isSelected, final boolean expanded,
        final boolean leaf, final int row) {
      final DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) obj;
      final Node domNode = (Node) dmtn.getUserObject();
      final String value = domNode.getNodeValue();
      // We manage our own events here
      final VariableViewlet variableViewlet = new VariableViewlet(new Variable(
          domNode.getNodeName(), value, false), null);
      variableViewlet.build();
      final JTextField textField = variableViewlet.getTextField();
      textField.addActionListener(new ActionListener() {
        @Override
				public void actionPerformed(final ActionEvent arg0) {
          // Get the text from the textfield and put it into the DOM node.
          domNode.setNodeValue(textField.getText());
          System.out.println("set DOM node to: " + domNode.getNodeValue());
          tree.getModel().valueForPathChanged(tree.getEditingPath(), domNode);
          cancelCellEditing();
        }
      });
      return variableViewlet;
    }

    private static final long serialVersionUID = 1L;
  }

  /**
   * @param treeNode
   *          A prepared tree node complete with user object
   * @return
   */
  private DefaultMutableTreeNode processNode(
      final DefaultMutableTreeNode treeNode) {
    /*
     * String text = element.getNodeValue(); if ((text != null) &&
     * (!text.equals(""))) { currentNode.add(new DefaultMutableTreeNode(new
     * String(text))); }
     */
    processAttributes(treeNode);
    // Get the children DOM nodes from the DOM node in our treeNode and make a
    // new treeNode for each child DOM node and put the child DOM node in it and
    // then attach it to the treeNode
    final NodeList nodeList = ((Element) treeNode.getUserObject())
        .getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      final Node childNode = nodeList.item(i);
      if (childNode.getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }
      final DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(
          childNode);
      treeNode.add(childTreeNode);
      processNode(childTreeNode);
    }
    return treeNode;
  }

  private void processAttributes(final DefaultMutableTreeNode currentNode) {
    final NamedNodeMap atts = ((Element) currentNode.getUserObject())
        .getAttributes();
    for (int i = 0; i < atts.getLength(); i++) {
      final Attr attDomNode = (Attr) atts.item(i);
      final DefaultMutableTreeNode attTreeNode = new DefaultMutableTreeNode(
          attDomNode);
      currentNode.add(attTreeNode);
    }
  }
}
// end of file
