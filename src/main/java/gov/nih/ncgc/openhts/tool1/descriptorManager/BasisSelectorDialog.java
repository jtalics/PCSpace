package gov.nih.ncgc.openhts.tool1.descriptorManager;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class BasisSelectorDialog extends AbstractDialog {
  private final JList basisList;
  private final Session session;
  private final DefaultListModel basisListModel;
  
  public BasisSelectorDialog(final Session session) {
    super(session, "Select basis mode", true);
    this.session = session;
    if (session.descriptorManager.getBases().length==0) {
      throw new RuntimeException("no bases to select from");
    }
    setLayout(new BorderLayout());
    add(new JLabel("Select a registered basis:"), BorderLayout.NORTH);
    basisList = new JList();
    add(new JScrollPane(basisList),BorderLayout.CENTER);
    basisListModel = new DefaultListModel();
    basisList.setModel(basisListModel);
    for (final Basis basis : session.descriptorManager.getBases()){
      basisListModel.addElement(basis);
    }
    Session.addFocusBorder(basisList,basisList);
    add(getButtonPanel(), BorderLayout.SOUTH);
  }

  @Override
  protected boolean apply() {
    if (basisList.getSelectedIndices() == null || basisList.getSelectedIndices().length == 0) {
      JOptionPane.showMessageDialog(this,
          "Select a basis or click Cancel.");
      return false;
    }
    return true; // apply was successful
  }

  public Basis[] getSelectedBases() {
    List<Basis> bases = new ArrayList<Basis>();
    final int[] selectedIndices = basisList.getSelectedIndices();
    for (int selectedIndex : selectedIndices) {
      bases.add(session.descriptorManager.getBasisAt(selectedIndex));
    }
    return bases.toArray(new Basis[bases.size()]);
  }
  
  private static final long serialVersionUID = 1L;

  public void setSelectionMode(int mode) {
    switch (mode) {
    case ListSelectionModel.MULTIPLE_INTERVAL_SELECTION:
    case ListSelectionModel.SINGLE_INTERVAL_SELECTION:
    case ListSelectionModel.SINGLE_SELECTION:
      basisList.setSelectionMode(mode);
      break;
    default:
      throw new RuntimeException("bad selection mode");
    }
    // TODO Auto-generated method stub
    
  }
}
