package gov.nih.ncgc.openhts.tool1.persistence;

import gov.nih.ncgc.openhts.tool1.Session;

/** Purpose is to ...
 * @author talafousj
 *
 */
public interface Persistent {
  
    public void setSession(Session session);
    public void initializeTransient();
/*
    public void objectOnActivate(ObjectContainer oc);

    public boolean objectCanActivate(ObjectContainer oc);

    public boolean objectCanNew(ObjectContainer oc);

    public boolean objectCanUpdate(ObjectContainer oc);

    public void objectOnNew(ObjectContainer oc);

    public void objectOnUpdate(ObjectContainer oc);
*/
}
