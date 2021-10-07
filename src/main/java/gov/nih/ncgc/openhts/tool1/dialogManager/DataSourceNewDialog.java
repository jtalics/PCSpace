package gov.nih.ncgc.openhts.tool1.dialogManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import gov.nih.ncgc.openhts.tool1.DialogUtilities;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dirchooser.DirChooser;
import gov.nih.ncgc.openhts.tool1.dirchooser.SelectionMode;
import gov.nih.ncgc.openhts.tool1.util.mutableList.DefaultListCellEditor;
import gov.nih.ncgc.openhts.tool1.util.mutableList.DefaultMutableListModel;
import gov.nih.ncgc.openhts.tool1.util.mutableList.JListMutable;

public class DataSourceNewDialog extends AbstractDialog {
  public final static int APPROVE_OPTION = 0;
  public final static int CANCEL_OPTION = 1;
  // The file paths that will be consumed by calling code
  // private final Vector<JTextField> pathTextFields = new Vector<JTextField>();
  protected DefaultMutableListModel model;
  private final JTextField nameTextField = new JTextField(null);
  private final JLabel label = new JLabel();
  private final Session session;

  public DataSourceNewDialog(final Session session) {
    super(session);
    this.session = session;
    final JPanel jContentPane = new JPanel();
    jContentPane.setLayout(new BorderLayout());
    jContentPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    jContentPane.add(getButtonPanel(), BorderLayout.SOUTH);
    jContentPane.add(getMainPanel(), BorderLayout.CENTER);
    this.setContentPane(jContentPane);
    getButtonPanel().setHelpId("TODO");
  }

  public String getNameString() {
    return nameTextField.getText();
  }

  /**
   * Provides the primary product of this class.
   * 
   * @return list of paths to selected directories
   */
  public List<String> getPaths() {
    final List<String> paths = new ArrayList<String>();
    for (int i = 0; i < model.getSize(); i++) {
      paths.add(((String) model.getElementAt(i)));
    }
    return paths;
  }

  public boolean isFormValid() {
    if (getNameString().equals("")) {
      return false;
    }
    if (model.getSize() == 0) {
      return false;
    }
    for (int i = 0; i < model.getSize(); i++) {
      if (((String) model.getElementAt(i)).equals("")) {
        return false;
      }
    }
    return true;
  }

  public void setNameString(final String name) {
    nameTextField.setText(name);
  }

  public void setPathList(final List<String> pathList) {
    model.clear();
    for (final String path : pathList) {
      model.addElement(path);
    }
  }

  private JPanel getMainPanel() {
    final JPanel mainPanel = new JPanel(new BorderLayout());
    // NORTH
    final JPanel namePanel = new JPanel(new BorderLayout());
    mainPanel.add(namePanel, BorderLayout.NORTH);
    namePanel.add(new JLabel("Name"), BorderLayout.WEST);
    namePanel.add(nameTextField, BorderLayout.CENTER);
    final ValidationMediator validationMediator = new ValidationMediator();
    nameTextField.addCaretListener(validationMediator);
    // WEST
    mainPanel.add(new JLabel("Path(s)  "), BorderLayout.WEST);
    // CENTER
    model = new DefaultMutableListModel();
    final JListMutable pathList = new JListMutable(model);
    pathList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    pathList.setVisibleRowCount(-1);
    pathList.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(final KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
          final int[] selectedIndices = pathList.getSelectedIndices();
          if (selectedIndices.length <= 0) {
            JOptionPane.showMessageDialog(DataSourceNewDialog.this,
                "No paths selected to delete.");
            return;
          }
          pathList.clearSelection();
          for (int i = selectedIndices.length - 1; i >= 0; i--) {
            System.out.println(model.remove(selectedIndices[i]));
          }
        }
      }
    });
    // pathList.setCellRenderer(new DefaultListCellRenderer() {
    // @Override
    // public Component getListCellRendererComponent(JList list, Object value,
    // int index, boolean isSelected, boolean cellHasFocus) {
    // return (JTextField) value;
    // }
    // });
    pathList.setListCellEditor(new DefaultListCellEditor(new JTextField()));
    final JScrollPane scrollPane = new JScrollPane(pathList);
    scrollPane.setPreferredSize(new Dimension(250, 150));
    mainPanel.add(scrollPane, BorderLayout.CENTER);
    // EAST
    final JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
    buttonPanel.add(Box.createVerticalGlue());
    mainPanel.add(buttonPanel, BorderLayout.EAST);
    //
    final JButton addButton = new JButton();
    buttonPanel.add(addButton);
    addButton.setText("Add Path");
    // addButton.setPreferredSize(new Dimension(80,
    // addButton.getPreferredSize().height));
    buttonPanel.add(Box.createRigidArea(new Dimension(0, 8)));
    addButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
			public void actionPerformed(final java.awt.event.ActionEvent e) {
        model.addElement("??");
      }
    });
    //
    final JButton removeButton = new JButton();
    buttonPanel.add(removeButton);
    removeButton.setText("Remove Path");
    buttonPanel.add(Box.createRigidArea(new Dimension(0, 8)));
    // removeButton.setPreferredSize(new Dimension(80,
    // removeButton.getPreferredSize().height));
    removeButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
			public void actionPerformed(final java.awt.event.ActionEvent e) {
        final Object selectedObject = pathList.getSelectedValue();
        if (selectedObject == null) {
          session.errorNoSupport("Select a path to remove.");
          return;
        }
        model.removeElement(selectedObject);
      }
    });
    //
    final JButton clearButton = new JButton();
    buttonPanel.add(clearButton);
    clearButton.setText("Clear Paths");
    buttonPanel.add(Box.createRigidArea(new Dimension(0, 8)));
    // clearButton.setPreferredSize(new Dimension(80,
    // clearButton.getPreferredSize().height));
    clearButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
			public void actionPerformed(final java.awt.event.ActionEvent e) {
        model.clear();
      }
    });
    //
    final JButton browseButton = new JButton();
    buttonPanel.add(browseButton);
    browseButton.setText("Browse");
    buttonPanel.add(Box.createRigidArea(new Dimension(0, 8)));
    // browseButton.setPreferredSize(new Dimension(80,
    // browseButton.getPreferredSize().height));
    browseButton.addActionListener(new java.awt.event.ActionListener() {
      @Override
			public void actionPerformed(final java.awt.event.ActionEvent e) {
        final DirChooser dirChooser = new DirChooser(DataSourceNewDialog.this,
            SelectionMode.Multiple, "Select data source directory", getPaths());
        DialogUtilities.centerDialog(dirChooser);
        dirChooser.setVisible(true);
        // dirChooser is a modal dialog and blocks here until disposed
        if (dirChooser.isOk()) {
          model.clear();
          final String[] selections = dirChooser.getSelections();
          Arrays.sort(selections, String.CASE_INSENSITIVE_ORDER);
          for (final String selection : selections) {
            model.addElement(selection);
          }
          pathList.ensureIndexIsVisible(model.size() - 1);
          scrollPane.revalidate();
        }
        validationMediator.actionPerformed(e);
      }
    });
    return mainPanel;
  }

  private class ValidationMediator implements ActionListener, CaretListener {
    @Override
		public void actionPerformed(final ActionEvent e) {
      getButtonPanel().getOkButton().setEnabled(isFormValid());
    }

    @Override
		public void caretUpdate(final CaretEvent e) {
      getButtonPanel().getOkButton().setEnabled(isFormValid());
    }
  }

  @Override
  protected boolean apply() {
    final String name = nameTextField.getText();
    if (name == null || name.isEmpty()) {
      session.errorNoSupport("You must supply a name or Cancel.");
      return false;
    }
    if (getPaths().size() <= 0) {
      session.errorNoSupport("You must supply a path or Cancel.");
      return false;
    }
    return true;
  }

  private static final long serialVersionUID = 1L;
}
