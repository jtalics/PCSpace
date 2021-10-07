package gov.nih.ncgc.openhts.tool1.dialogManager;

import java.awt.BorderLayout;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import gov.nih.ncgc.openhts.tool1.Session;

public final class HelpKeyBindingsDialog extends AbstractDialog {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private final Session session;

  public HelpKeyBindingsDialog(final Session session) {
    super(session);
    this.session = session;
     setTitle("Canvas help");
     setTitle("Canvas - Hotkeys");
     addWindowListener(DialogManager.getInstance(session));
     addComponentListener(DialogManager.getInstance(session));
     final JPanel topPanel = new JPanel(new BorderLayout(0, 0));
     add(topPanel, BorderLayout.CENTER);
//     panel.setToolTipText("hotkeys help");
//     CSH.setHelpIDString(panel, "hotkeys_canvas");
//     JTextArea textArea = new JTextArea("   --- Canvas hot keys ---\n"
//         + " -: reduce\n" + " =: magnify\n" + " F1: online help system\n"
//         + " F2: what is this?\n" + " F3: move subject origin back\n"
//         + " F4: move subject origin forward\n" + " F5: refresh\n"
//         + " F6: zoom mode\n" + " F7: unzoom completely\n"
//         + " F8: cycle data drawing order\n" + " F9: toggle un/select mode\n"
//         + " ESC: id/single selection cursor mode\n"
//         + " SPACE: fit to canvas\n"
//         + " a: toggle mouse rot/trans subsetting \n"
//         + " boundingBox: move center of perspective back\n"
//         + " c: center subject origin\n"
//         + " f: move center of perspective forward\n"
//         + " h: this help message\n" + " i: toggle inertia\n"
//         + " m: translate\n" + " o: toggle orthogonal mode\n" + " q: quit\n"
//         + " r: refresh canvas\n" + " s: toggle stereo\n"
//         + " t: text mode cursor mode\n" + " u: box un/selection\n"
//         + " v: reset view\n"
//         // +" w: new window\n"
//         + " x: XY rotate cursor mode\n" + " z: Z rotate cursor mode\n");

//     textArea.setToolTipText("hotkeys help");
//     CSH.setHelpIDString(textArea, "hotkeys_canvas");
//     textArea.setEditable(false);
//     panel.add(textArea, BorderLayout.CENTER);

//     setHelpID("help_hotkeys_canvas_button");
//     add(panel);
   
   // requestFocus();
    final JTable table = new JTable(/*
        new Object[][]{
          {"hi","hi"}
            //session.actionManager.bindedList.toArray()
            
        },new String[]{"hi","hi"}
        */
        
        new DefaultTableModel() {
      /**
           * 
           */
          private static final long serialVersionUID = 1L;
      @Override
      public Object getValueAt(int row, int col) {
        AbstractAction action = session.actionManager.bindedList.get(row);
        String desc;
        switch(col){
        case 0:
          KeyStroke keyStroke = (KeyStroke)action.getValue(Action.ACCELERATOR_KEY);
          if (keyStroke == null) {
            return "";
          }
          return keyStroke;
        case 1:
          desc = (String)action.getValue(Action.SHORT_DESCRIPTION);
          if (desc == null) {
            return "";
          }
          return desc;
        case 2:
          desc = (String)action.getValue(Action.LONG_DESCRIPTION);
          if (desc == null){
            return "";
          }
          return desc;
        default:
          return "nothing here";
        }
      }
      @Override
      public int getRowCount() {
        int rowCount = session.actionManager.bindedList.size();
        if (rowCount <=0) {
          throw new RuntimeException("zero rows");
        }
        return rowCount;
      }

      @Override
      public int getColumnCount() {
        return 3;
      }
      @Override
      public String getColumnName(int col) {
        switch(col) {
        case 0:
          return "keystroke";
        case 1:
          return "short description";
        case 2:
          return "long description";
        default:
          return "?";
        }
      }
        }
    );    
    //table.setColumnModel(new DefaultTableColumnModel() {});
    topPanel.add(new JScrollPane(table),BorderLayout.CENTER);
    add(getButtonPanel(),BorderLayout.SOUTH);
  }

}
