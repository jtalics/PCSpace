package gov.nih.ncgc.openhts.tool1.engine;

import java.util.Date;
import java.util.Map;
import java.util.Hashtable;
import gov.nih.ncgc.openhts.tool1.resourceManager.Resource;
import gov.nih.ncgc.openhts.tool1.resourceManager.ResourceDescriptor;
import gov.nih.ncgc.openhts.tool1.resourceManager.ResourceFactory;
import gov.nih.ncgc.openhts.tool1.resourceManager.ResourceInfo;
import gov.nih.ncgc.openhts.tool1.resourceManager.ResourceManager;
import gov.nih.ncgc.openhts.tool1.resourceManager.ResourceProvider;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class Kernel implements ResourceProvider {

	// private static final Logger logger = Logger.getLogger(Kernel.class.getName());
    private EngineResourceManager manager;
    private boolean initialized = false;

    @Override
		public Resource acquireResource(final ResourceDescriptor descriptor) throws KernelException {
    	return manager.acquireResource(descriptor);
    }

    @Override
		public ResourceInfo getResourceInfo(final ResourceDescriptor descriptor) throws KernelException {
        return manager.getResourceInfo(descriptor);
    }
    
    @Override
		public void registerResource(final ResourceDescriptor descriptor, final Resource resource) {
    	manager.registerResource(descriptor, resource);
    }

    @Override
		public void releaseResource(final ResourceDescriptor descriptor, final boolean immediateUnload) throws KernelException {
    	manager.releaseResource(descriptor,immediateUnload);
    }

    static void processReturnCode(final int engineReturnCode) throws FatalKernelException, KernelException {

//        } else if (!engineReturnCode.equals(ERRORCODE.SUCCESS)) {
//            throw new KernelException(engineReturnCode);
//        }
    }

    Kernel() {
        manager = new EngineResourceManager();
        registerDefaultFactories();
    }

    void initialize(final long engineCacheSize, final String licenseFilename) throws FatalKernelException {
        if (!initialized) {
//            int rc = CoreAPI.Init(engineCacheSize, false, false, licenseFilename);
            try {
                Kernel.processReturnCode(-1/*rc*/);
            } catch ( final KernelException x ) {
                // if we get an exception on initialize, it is always fatal.
                throw new FatalKernelException("During initialize: " + x.getMessage(), x.getEngineCode());
            }
            initialized = true;
        }
    }

    void deinitialize() throws KernelException {

        if (initialized) {
            manager.unregisterResources();

            // ERRORCODE rc = CoreAPI.APIDeInit();
            Kernel.processReturnCode(/*rc*/-1);
            initialized = false;
        }
    }

    boolean isInitialized() {
        return initialized;
    }

    KernelResult execute(final KernelCommand command) {
        final KernelResult result = new KernelResult(command);
        final Date startTime = new Date();

        try {
            command.execute(manager);
        } catch (final KernelException x) {
            result.setException(x);
            
        } finally {
            result.setWhen(startTime);
        }
        
        command.fireExecutionComplete(result);
        
        return result;
    }

    
    /**
     * 
     */
    @Override
    protected void finalize() throws Throwable {

        try {
            deinitialize();

        } finally {
            super.finalize();
        }
    }

    /**
     * 
     * 
     */
    private void registerDefaultFactories() {
        //manager.registerFactory(MyResourceFactory.class, new MyResourceFactory());
    }

    private class EngineResourceManager extends ResourceManager {

        private final Map<Class, ResourceFactory> factoryRegistry;
        private final Map<ResourceDescriptor, Resource> resourceRegistry;

        /**
         * 
         * 
         */
        private EngineResourceManager() {
            this.factoryRegistry = new Hashtable<Class, ResourceFactory>();
            this.resourceRegistry = new Hashtable<ResourceDescriptor, Resource>();
        }

        /**
         * 
         */
        @Override
        public ResourceFactory getFactory(final Class descriptorClass) {
            return factoryRegistry.get(descriptorClass);
        }

        /**
         * 
         * @throws KernelException
         */
        synchronized void unregisterResources() throws KernelException {

            for (final Resource resource : resourceRegistry.values()) {
                resource.unload();
            }
            resourceRegistry.clear();
        }

        /**
         * 
         */
        @Override
				synchronized public Resource acquireResource(final ResourceDescriptor descriptor) throws KernelException {
            Resource resource = resourceRegistry.get(descriptor);

            if (resource == null) {
                final ResourceFactory factory = getFactory(descriptor.getClass());

                if (factory != null) {
                    resource = factory.createResource(descriptor);
                }
            }

            if (resource != null && !resource.isLoaded()) {
                try { 
                    resource.load();
                } catch ( final KernelException kex ) {
                    // provide a more descriptive error (what failed to load?)
                    throw new KernelException( "Failed to load " + descriptor.toString(), kex );
                }
                registerResource(descriptor, resource);
            }
            return resource;
        }

        @Override
				public ResourceInfo getResourceInfo(final ResourceDescriptor descriptor) throws KernelException {
            final Resource resource = acquireResource(descriptor);
            return resource.getInfo();
        }
        
        /**
         * 
         */
        @Override
        public synchronized boolean isFactoryRegistered(final Class descriptorClass) {
            return factoryRegistry.containsKey(descriptorClass);
        }

        /**
         * 
         */
        @Override
        public synchronized boolean isResourceRegistered(final ResourceDescriptor descriptor) {
            return resourceRegistry.containsKey(descriptor);
        }

        /**
         * 
         */
        @Override
        public void registerFactory(final Class descriptorClass, final ResourceFactory factory) {
            factoryRegistry.put(descriptorClass, factory);
        }

        /**
         * 
         */
        @Override
        public void unregisterFactory(final Class descriptorClass) {
            factoryRegistry.remove(descriptorClass);
        }

        /**
         * 
         */
        @Override
				synchronized public void registerResource(final ResourceDescriptor descriptor, final Resource resource) {
            resourceRegistry.put(descriptor, resource);
        }

        /**
         * 
         */
        @Override
				synchronized public void releaseResource(final ResourceDescriptor descriptor, final boolean immediateUnload) throws KernelException {
            final Resource res = resourceRegistry.remove(descriptor);
            if (immediateUnload) {
                if ( null != res  ) {
                    res.unload();
                }
            }
        }
    }

    public int getCPUCount() {
        return Runtime.getRuntime().availableProcessors();
    }
}
