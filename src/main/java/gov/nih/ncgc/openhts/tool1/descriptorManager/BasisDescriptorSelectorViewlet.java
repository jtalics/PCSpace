/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.descriptorManager;

import gov.nih.ncgc.openhts.tool1.Session;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class BasisDescriptorSelectorViewlet extends DescriptorSelectorViewlet {
  final Basis basis;

  public BasisDescriptorSelectorViewlet(Session session, int selectionMode,
      String title, Basis basis) {
    super(session, selectionMode, title);
    this.basis = basis;
  }

  @Override
  protected void reloadModel() {
    defaultListModel.clear();
    Descriptor[] descriptors = basis.getDescriptors();
    for (int i = 0; i < descriptors.length; i++) {
      defaultListModel.add(i, descriptors[i]);
    }
  }

  private static final long serialVersionUID = 1L;
}
