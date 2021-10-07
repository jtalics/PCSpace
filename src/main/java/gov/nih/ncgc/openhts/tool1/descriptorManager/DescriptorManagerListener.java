/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.descriptorManager;

import java.util.EventListener;

/** Purpose is to ...
 * @author talafousj
 */
public interface DescriptorManagerListener extends EventListener {

  public void descriptorManagerChanged(DescriptorManagerEvent ev);
//  public void descriptorAdded(DescriptorManager manager, Descriptor descriptor);
//  public void descriptorReplaced(DescriptorManager manager, Descriptor descriptor);
//  public void descriptorRemoved(DescriptorManager manager, Descriptor descriptor);
//  public void basisAdded(DescriptorManager manager, Basis basis);
//  public void basisReplaced(DescriptorManager manager, Basis basis);
//  public void basisRemoved(DescriptorManager manager, Basis basis);
//  public void basisSelectionChanged(DescriptorManager manager, Basis[] selectedBases);
}

