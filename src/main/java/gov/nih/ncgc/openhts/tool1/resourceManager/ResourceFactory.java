package gov.nih.ncgc.openhts.tool1.resourceManager;

import gov.nih.ncgc.openhts.tool1.engine.KernelException;

/** Purpose is to ...
 * @author talafousj
 *
 */
public interface ResourceFactory {

    Resource createResource(ResourceDescriptor descriptor) throws KernelException;
}
