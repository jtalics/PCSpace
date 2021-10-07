package gov.nih.ncgc.openhts.tool1.resourceManager;

import gov.nih.ncgc.openhts.tool1.engine.KernelException;

/** Purpose is to ...
 * @author talafousj
 *
 */
public interface Resource {

    Context context();
    
    boolean isLoaded();

    void load() throws KernelException;

    void unload() throws KernelException;
    
    ResourceInfo getInfo() throws KernelException;
}
