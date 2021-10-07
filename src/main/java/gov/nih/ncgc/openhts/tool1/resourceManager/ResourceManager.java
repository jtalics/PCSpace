package gov.nih.ncgc.openhts.tool1.resourceManager;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public abstract class ResourceManager implements ResourceProvider {
  public abstract ResourceFactory getFactory(Class descriptorClass);

  public abstract boolean isFactoryRegistered(Class descriptorClass);

  public abstract boolean isResourceRegistered(ResourceDescriptor descriptor);

  public abstract void registerFactory(Class descriptorClass,
      ResourceFactory factory);

  public abstract void unregisterFactory(Class descriptorClass);
}
