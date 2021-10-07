package gov.nih.ncgc.openhts.tool1.resourceManager;

import gov.nih.ncgc.openhts.tool1.engine.KernelException;

/** Purpose is to ...
 * @author talafousj
 *
 */
public interface ResourceProvider {

    /**
     * Acquires a resource from a resource descriptor.  The resource could be 
     * retrieved from a cache.  The resource is created and opened/loaded if 
     * needed.
     * @param descriptor
     * @return
     * @throws KernelException
     */
    public Resource acquireResource(ResourceDescriptor descriptor)
    		throws KernelException;

    /**
     * Retrieves resource info for a given resource descriptor.
     * @param descriptor
     * @return
     * @throws KernelException
     */    
    public ResourceInfo getResourceInfo(ResourceDescriptor descriptor) throws KernelException;
    
    /**
     * This should be used by the Engine internally ONLY.  It is only used to
     * register a platform FTCOREHANDLE that is returned from another action 
     * (IE not an open or load action).  For example, when indexing is 
     * complete, the PAT FTCOREHANDLE is returned, and this method allows 
     * the resource to be registered with the resource manager.
     * @param descriptor
     * @param resource
     */
    public void registerResource(ResourceDescriptor descriptor, Resource resource);

    /**
     * Used to release a resource that is no longer needed.  If forceUnload is 
     * false, resource may be released at implementation's discression.
     * @param descriptor
     */
    public void releaseResource(ResourceDescriptor descriptor, boolean immediateUnload) throws KernelException;
}
