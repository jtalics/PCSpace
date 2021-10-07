package gov.nih.ncgc.openhts.tool1.plotManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.help.CSH;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;
import gov.nih.ncgc.openhts.tool1.dialogManager.DialogManager;

public final class TextChooseDialog extends AbstractDialog {
   
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private final JList choiceList;
  
  private final Session session;
  private int mode;
  private int selected;
  
  public TextChooseDialog(final Session session) {
   super(session);
   this.session = session;
   
//   if (action==Text.DELETE && (text.session.plotter.textList == null || text.session.plotter.textList.size()<=0 )) {
//
//     text.session.message("There are no text items to delete.");
//     return;
//   }
//   else if (session.plotter.textList.size() == 1) {  // automatic choice
//     doChoice(session);
//     return;
//   }

//   if (chooseDialog != null) {
//     chooseDialog.dispose();
//   }

   //chooseDialog = new EscapeDialog(session);
   setModal(false);
   setResizable(false);
   addWindowListener(session.dialogManager);
   addComponentListener(DialogManager.getInstance(session));

   final JLabel label = new JLabel();
   CSH.setHelpIDString(label,"delete_text");

   switch(mode) {
     // TODO: cleanup
   case Text.DELETE:
     setTitle("Text - Delete");
     label.setText("Select text item to delete:");
     label.setToolTipText("text available to delete");
     CSH.setHelpIDString(label,"select_delete_text_label");
     break;
   }

   final JPanel panel = new JPanel();
   CSH.setHelpIDString(panel,"delete_text");

   panel.setLayout(new BorderLayout());

   final JPanel subpanel1 = new JPanel(new BorderLayout());
   CSH.setHelpIDString(subpanel1,"delete_text");
   subpanel1.setLayout(new BorderLayout());
   subpanel1.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.NORTH);
   subpanel1.add(Box.createRigidArea(new Dimension(7, 0)), BorderLayout.WEST);
   subpanel1.add(Box.createRigidArea(new Dimension(7, 0)), BorderLayout.EAST);
   subpanel1.add(Box.createRigidArea(new Dimension(0, 2)), BorderLayout.SOUTH);
   subpanel1.add(label,BorderLayout.CENTER);

   panel.add(subpanel1, BorderLayout.NORTH);

   final JPanel subpanel2 = new JPanel();
   CSH.setHelpIDString(subpanel2,"delete_text");
   subpanel2.setLayout(new BorderLayout());
   subpanel2.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.NORTH);
   subpanel2.add(Box.createRigidArea(new Dimension(7, 0)), BorderLayout.WEST);
   subpanel2.add(Box.createRigidArea(new Dimension(7, 0)), BorderLayout.EAST);
   subpanel2.add(Box.createRigidArea(new Dimension(0, 8)), BorderLayout.SOUTH);

   choiceList = new JList(session.plotManager.getTextList());
   choiceList.setToolTipText("click to select a text that you want to delete");
   CSH.setHelpIDString(choiceList,"select_delete_text_list");
   final TextListCellRenderer dcr = new TextListCellRenderer();
   choiceList.setCellRenderer(dcr);
   choiceList.setBorder(BorderFactory.createLoweredBevelBorder());
   choiceList.setBackground(panel.getBackground());
   choiceList.setVisibleRowCount(8);
   choiceList.setSelectedIndex(selected);

   final ListSelectionListener listSelectionListener = new ListSelectionListener() {
     @Override
		public void valueChanged(ListSelectionEvent ev) {
       JList list = (JList)ev.getSource();
       if (!ev.getValueIsAdjusting()) {
         selected = list.getSelectedIndex();
       }
     }
   };
   choiceList.addListSelectionListener(listSelectionListener);

   final JScrollPane scrollPane = new JScrollPane();
   CSH.setHelpIDString(scrollPane,"delete_text");
   scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
   scrollPane.setViewportView(choiceList);
   scrollPane.setPreferredSize(new Dimension(480, 200));
   subpanel2.add(scrollPane, BorderLayout.CENTER);
   panel.add(subpanel2, BorderLayout.CENTER);

   setHelpID("help_delete_text_button");
 }
 

  @Override
  protected boolean apply() {
    try {
      doChoice();
    }
    catch(final Exception ex) {
      session.errorSupport("", ex,"apply_delete_text_button_error");
    }

    updateLists();

    //Pointset.disposeTableFrame();  // this is like an update
    session.plotManager.refreshCanvas();
    return true; // apply was successful
  }

  
 private void updateLists() {

     choiceList.invalidate();
     choiceList.validate();
     choiceList.repaint();
 }


 /**
  *
  */
 private void doChoice()
     throws Exception {
   if (selected == -1) {
     return;
   }
   if (mode == Text.MODIFY) {
     final TextModifyDialog textModifyDialog = session.dialogManager.getTextModifyDialog();
     textModifyDialog.showDialog();
   }
   else if (mode == Text.DELETE) {
     delete();
     if (session.plotManager.textCount() > 0) {
       choiceList.setSelectedIndex(selected);
       choiceList.ensureIndexIsVisible(selected);
       // choiceList.invalidate();
       choiceList.repaint();
       // choiceList.validate();
     }
     else {
       if (session.dialogManager.getTextChooseDialog() != null) {
         session.dialogManager.getTextChooseDialog().dispose();
       }
     }
     session.plotManager.subject.calculateExtents();
     session.plotManager.refreshCanvas();
     // session.updateButtonStates();
   }
 }

 /**
  * @param session
  */
 private void delete() {
   final Text text = session.plotManager.getText(selected);
   text.clearReferences();
   session.plotManager.subject.axes.deleteLabel(text);
   session.plotManager.removeText(text);
   if (selected >= session.plotManager.textCount()) {
     selected = session.plotManager.textCount() - 1;
   }
 }


}
