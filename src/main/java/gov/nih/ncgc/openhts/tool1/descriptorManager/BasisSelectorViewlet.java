/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.descriptorManager;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import gov.nih.ncgc.openhts.tool1.Session;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class BasisSelectorViewlet extends JScrollPane implements
    DescriptorManagerListener {
  private final Session session;
  private final JList basisList;
  private final DefaultListModel defaultListModel;

  public BasisSelectorViewlet(Session session, int selectionMode) {
    this.session = session;
    setBorder(BorderFactory.createTitledBorder("Available bases"));
    defaultListModel = new DefaultListModel();
    reloadModel();
    basisList = new JList(defaultListModel);
    basisList.setSelectionMode(selectionMode);
    setViewportView(basisList);
    session.descriptorManager.addDescriptorManagerListener(this);
  }

  private void reloadModel() {
    defaultListModel.clear();
    Basis[] bases = session.descriptorManager.getBases();
    for (int i = 0; i < session.descriptorManager.getBases().length; i++) {
      defaultListModel.add(i, bases[i]);
    }
  }

  public Basis[] getSelectedBases() {
    Object[] objects = basisList.getSelectedValues();
    Basis[] bases = new Basis[objects.length];
    for (int i = 0; i < bases.length; i++) {
      bases[i] = (Basis) objects[i];
    }
    return bases;
  }

  public Basis getSelectedBasis() {
    Basis[] bases = getSelectedBases();
    if (bases.length != 1 || bases == null) {
      return null;
    }
    return bases[0];
  }

  @Override
	public void descriptorManagerChanged(DescriptorManagerEvent ev) {
    switch (ev.kind) {
    case CHANGED:
		case MEMBERS_SELECTION:
		case MEMBER_ADDED:
		case MEMBER_CHANGED:
		case MEMBER_LOADED:
		case MEMBER_REMOVED:
		case MEMBER_REPLACED:
		default:
      reloadModel();
    }
  }
}
