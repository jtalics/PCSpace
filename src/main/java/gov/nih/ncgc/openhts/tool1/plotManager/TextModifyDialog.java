package gov.nih.ncgc.openhts.tool1.plotManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.help.CSH;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;
import gov.nih.ncgc.openhts.tool1.dialogManager.DialogManager;

/** Purpose is to ...
 * @author talafousj
 *
 */
public final class TextModifyDialog extends AbstractDialog implements PlotManagerListener {
  private static final long serialVersionUID = 1L;
  private final Session session;
  private JList textList;
  private Text selectedText;
  private int mode;
  private final TextViewlet textViewlet;
  public SpinnerNumberModel xModel, yModel, zModel;
  private List<ChangeListener> listeners = new ArrayList<ChangeListener>();

  public TextModifyDialog(final Session session) {
    super(session);
    this.session = session;
    setTitle("Text - Modify");
    setModal(false);
    setResizable(false);
    addWindowListener(DialogManager.getInstance(session));
    addComponentListener(DialogManager.getInstance(session));
    final JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.WEST);
    panel.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.EAST);
    panel.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.NORTH);
    final Box vbox = Box.createVerticalBox();
    final Box vbox1 = Box.createVerticalBox();
    final Dimension dimension = new Dimension(120, 25);
    final JPanel textpanel = new JPanel();
    textpanel.setLayout(new BorderLayout());
    textpanel.add(Box.createRigidArea(new Dimension(7, 0)), BorderLayout.WEST);
    textpanel.add(Box.createRigidArea(new Dimension(7, 0)), BorderLayout.EAST);
    textpanel.add(Box.createRigidArea(new Dimension(0, 8)), BorderLayout.SOUTH);
    final JPanel subpanel1 = new JPanel();
    subpanel1.setLayout(new BorderLayout());
    subpanel1.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.NORTH);
    subpanel1.add(Box.createRigidArea(new Dimension(7, 0)), BorderLayout.WEST);
    subpanel1.add(Box.createRigidArea(new Dimension(7, 0)), BorderLayout.EAST);
    subpanel1.add(Box.createRigidArea(new Dimension(0, 2)), BorderLayout.SOUTH);
    final JLabel label = new JLabel("Select text to modify:");
    CSH.setHelpIDString(label, "select_modify_text_label");
    label.setToolTipText("text available to modify");
    subpanel1.add(label, BorderLayout.CENTER);
    textpanel.add(subpanel1, BorderLayout.NORTH);
    textpanel.add(getListPanel(), BorderLayout.CENTER);
    vbox1.add(textpanel, BorderLayout.CENTER);
    vbox.add(vbox1);
    vbox.add(new JSeparator());
    CSH.setHelpIDString(vbox, "modify_text");
    textViewlet = new TextViewlet(session);
    addChangeListener(textViewlet);
    xModel = textViewlet.xModel;
    yModel = textViewlet.yModel;
    zModel = textViewlet.zModel;
    setTitle("Text - Modify");
    vbox.add(textViewlet);
    panel.add(vbox, BorderLayout.CENTER);
    setHelpID("help_modify_text_button");
    add(panel, BorderLayout.CENTER);
    // Now that all listeners are set up, we simulate an initial selection
    session.plotManager.addPlotManagerListener(this);
    selectedText = session.plotManager.getSelectedText();
    if (selectedText == null) {
      session.plotManager.selectTextAt(0);
      // This event will return to us since we are listening to the
      // PlotManager
    }
    else {
      textList.setSelectedValue(selectedText, true);
    }
    add(getButtonPanel(),BorderLayout.SOUTH);
  }

  private JPanel getListPanel() {
    textList = new JList(session.plotManager.getTextList());
    textList
        .setToolTipText("click to select text to change the properties of");
    CSH.setHelpIDString(textList, "select_modify_text_list");
    final TextListCellRenderer dcr = new TextListCellRenderer();
    textList.setCellRenderer(dcr);
    textList.setBorder(BorderFactory.createLoweredBevelBorder());
    textList.setVisibleRowCount(6);
    textList.setSelectedValue(selectedText,true);
    textList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    final JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
    scrollPane.setViewportView(textList);
    scrollPane.setPreferredSize(new Dimension(300, 120));
    textList.addListSelectionListener(new ListSelectionListener() {
      @Override
			public void valueChanged(ListSelectionEvent ev) {
        try {
          JList list = (JList) ev.getSource();
          if (!ev.getValueIsAdjusting()) {
            selectedText = (Text)list.getSelectedValue();
            // Pointset.disposeTableFrame(); // this is like an update
            // TODO: not sure this is the right way to get a refesh on the list:
            textList.invalidate();
            textList.repaint();
            textList.validate();
            fireStateChanged(selectedText); // alert viewlet
          }
        }
        catch (Throwable ex) {
          session.errorSupport("While listgening to list changing value:", ex,
              "selection_text_modify_list_error");
        }
      }
    });
    textList.setSelectedValue(selectedText,true);
    JPanel panel = new JPanel();
    panel.add(scrollPane);
    fireStateChanged(selectedText);

    return panel;
  }

  @Override
  protected boolean apply() {
    try {
      final int index = textList.getSelectedIndex();
      final Text text = session.plotManager.getText(index);
      // System.out.println("OPERATING ON TEXT "+text.hashCode());
      text.setString(textViewlet.stringTextField.getText());
      text.start[0] = textViewlet.xModel.getNumber().floatValue()
          - session.plotManager.subject.cumDelta[0];
      text.start[1] = textViewlet.yModel.getNumber().floatValue()
          - session.plotManager.subject.cumDelta[1];
      if (session.plotManager.getDim() == 3) {
        text.start[2] = textViewlet.zModel.getNumber().floatValue()
            - session.plotManager.subject.cumDelta[2];
      }
      text.color = textViewlet.colorButton.getBackground();
      text.moving = textViewlet.checkBox.isSelected();
      final String s = textViewlet.fontButton.getText();
      // JTAL text.font = new Font(s);
      // when choose() called
      text.makeImage();
      // session.plotManager.canvas.smartCompose();
      if (mode == Text.ADD && !session.plotManager.containsText(text)) {
        session.plotManager.addText(text);
      }
      session.plotManager.refreshCanvas();
    }
    catch (final Throwable ex) {
      session.errorSupport("", ex, "apply_modify_text_button_error");
    }
    return true; // apply was successful
  }


  public void addChangeListener(ChangeListener listener) {
    listeners.add(listener);
  }

  public void removeChangeListener(ChangeListener listener) {
    listeners.remove(listener);
  }

  private void fireStateChanged(Text text) {
    for (ChangeListener listener : listeners) {
      listener.stateChanged(new ChangeEvent(text));
    }
  }
  
  public void setModel(final Text text) {
    // TODO Auto-generated method stub
  }

//  public void basisModeChanged(PlotManager plotManager, Basis basisMode) {
//    // TODO Auto-generated method stub
//    
//  }
//
//  public void dimensionalityChanged(PlotManager plotManager, int dim) {
//    // TODO Auto-generated method stub
//    
//  }

//  public void textSelectionChanged(PlotManager plotManager) {
//    Text text = session.plotManager.getSelectedText();
//    if (null != text) {
//      textList.setSelectedValue(text, true);
//    }
//    else {
//      textList.clearSelection();
//    }
//  }

//  public void textPropertiesChanged(PlotManager manager, Text text) {
//    if (text == selectedText) {
//      fireStateChanged(selectedText);
//    }
//  }

//  public void idSelectionChanged(Object source, Map<Pointset,int[]> selected) {
//    // nop
//    
//  }

//  public void pointsetOrderChanged(Object source) {
//    // TODO Auto-generated method stub
//    
//  }

  @Override
	public void plotManagerChanged(PlotManagerEvent ev) {
    Text text;
    switch(ev.kind) {
    case CHANGED:
      break;
    case BASISMODE_CHANGED:
      // TODO
      break;
    case TEXT_SELECTION_CHANGED:
      text = session.plotManager.getSelectedText();
      if (null != text) {
        textList.setSelectedValue(text, true);
      }
      else {
        textList.clearSelection();
      }
      break;
    case POINTSET_ORDER_CHANGED:
      // TODO
      break;
    case TEXT_PROPERTIES_CHANGED:
      text = (Text)ev.member; 
      if (text == selectedText) {
        fireStateChanged(selectedText);
      }
      break;
    case PREVIEW_CHANGED:
      // nop
      break;
    case DIMENSIONALITY:
      // TODO
      break;

    }
  }

}
