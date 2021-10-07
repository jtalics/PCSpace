// TODO: Only display descriptors of specified kinds so that caller does not need to validate
package gov.nih.ncgc.openhts.tool1.descriptorManager;

import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionListener;
import gov.nih.ncgc.openhts.tool1.Session;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public abstract class DescriptorSelectorViewlet extends JScrollPane implements
    DescriptorManagerListener {
  protected final Session session;
  private final int selectionMode;
  private JList descriptorList;
  protected DefaultListModel defaultListModel;
  private final String title;

  public DescriptorSelectorViewlet(Session session, int selectionMode, String title) {
    this.session = session;
    this.selectionMode = selectionMode;
    this.title=title;
  }

  public void initialize() {
    setBorder(BorderFactory.createTitledBorder(title));
    defaultListModel = new DefaultListModel();
    reloadModel();
    descriptorList = new JList(defaultListModel);
    descriptorList.setSelectionMode(selectionMode);
    setViewportView(descriptorList);
    session.descriptorManager.addDescriptorManagerListener(this);
  }
  
  public Descriptor[] getSelectedDescriptors() {
    Object[] objects = descriptorList.getSelectedValues();
    Descriptor[] descriptors = new Descriptor[objects.length];
    for (int i = 0; i < descriptors.length; i++) {
      descriptors[i] = (Descriptor) objects[i];
    }
    return descriptors;
  }
  
  public void setSelectedDescriptor(Descriptor descriptor) {
    descriptorList.setSelectedValue(descriptor, true);
  }

  public Descriptor getSelectedDescriptor() {
    Descriptor[] descriptors = getSelectedDescriptors();
    if (descriptors.length > 1) {
      throw new RuntimeException("only one descriptor should be selected");
    }
    if (descriptors == null || descriptors.length==0) {return null;}
    return descriptors[0];
  }

  public void clearSelection() {
    descriptorList.clearSelection();
  }

  protected abstract void reloadModel();

//  public void basisAdded(DescriptorManager manager, Basis basis) {
//    reloadModel();
//  }
//
//  public void basisRemoved(DescriptorManager manager, Basis basis) {
//    reloadModel();
//  }
//
//  public void basisReplaced(DescriptorManager manager, Basis basis) {
//    reloadModel();
//  }
//
//  public void basisSelectionChanged(DescriptorManager manager,
//      Basis[] selectedBases) {
//    // nop
//  }
//
//  public void descriptorAdded(DescriptorManager manager, Descriptor descriptor) {
//    reloadModel();
//  }
//
//  public void descriptorRemoved(DescriptorManager manager, Descriptor descriptor) {
//    reloadModel();
//  }
//
//  public void descriptorReplaced(DescriptorManager manager,
//      Descriptor descriptor) {
//    reloadModel();
//  }

  private final List<ListSelectionListener> listeners = new ArrayList<ListSelectionListener>();

  public void addListSelectionListener(ListSelectionListener listener) {
    descriptorList.addListSelectionListener(listener);
  }

  public void removeListSelectionListener(ListSelectionListener listener) {
    descriptorList.removeListSelectionListener(listener);
  }

  @Override
	public void descriptorManagerChanged(DescriptorManagerEvent ev) {
    switch(ev.kind) {
   case CHANGED: // most vague change possible
     if (ev.member instanceof Descriptor) {
       reloadModel();
     }
     else if (ev.member instanceof Basis){
       reloadModel();
     }
     break;
   case MEMBER_LOADED:
     break;
   case MEMBER_ADDED:
     if (ev.member instanceof Descriptor) {
       reloadModel();
     }
     else if (ev.member instanceof Basis){
       reloadModel();
     }
     break;
   case MEMBER_CHANGED: // use more specific kind if available
     if (ev.member instanceof Descriptor) {
       reloadModel();
     }
     else if (ev.member instanceof Basis){
       reloadModel();
     }
     break;
   case MEMBER_REMOVED: 
     if (ev.member instanceof Descriptor) {
       reloadModel();
     }
     else if (ev.member instanceof Basis){
       reloadModel();
     }
     break;
   case MEMBER_REPLACED:
     if (ev.member instanceof Descriptor) {
       reloadModel();
     }
     else if (ev.member instanceof Basis){
       reloadModel();
     }
     break;
   case MEMBERS_SELECTION: // user (de)selected
     if (ev.member instanceof Descriptor) {
       reloadModel();
     }
     else if (ev.member instanceof Basis){
       reloadModel();
     }
     break;
   }    
  }
}
