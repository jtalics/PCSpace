package gov.nih.ncgc.openhts.tool1.persistence;

import com.db4o.ObjectContainer;

/** Purpose is to ...
 * @author talafousj
 *
 */
public interface Db4oEnabled {

    public void objectOnActivate(ObjectContainer oc);

    public boolean objectCanActivate(ObjectContainer oc);

    public boolean objectCanNew(ObjectContainer oc);

    public boolean objectCanUpdate(ObjectContainer oc);

    public void objectOnNew(ObjectContainer oc);

    public void objectOnUpdate(ObjectContainer oc);

}
