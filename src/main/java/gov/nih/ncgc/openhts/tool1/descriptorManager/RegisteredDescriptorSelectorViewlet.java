/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.descriptorManager;

import gov.nih.ncgc.openhts.tool1.Session;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class RegisteredDescriptorSelectorViewlet extends
    DescriptorSelectorViewlet {

  public RegisteredDescriptorSelectorViewlet(Session session, int selectionMode, String title) {
    super(session, selectionMode, title);
  }

  @Override
  protected void reloadModel()   {
    defaultListModel.clear();
    Descriptor[] descriptors = session.descriptorManager.getDescriptors();
    for (int i = 0; i < session.descriptorManager.getDescriptors().length; i++) {
      defaultListModel.add(i, descriptors[i]);
    }
  }
}
