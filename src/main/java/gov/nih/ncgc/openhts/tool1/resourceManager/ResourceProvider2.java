package gov.nih.ncgc.openhts.tool1.resourceManager;

import gov.nih.ncgc.openhts.tool1.engine.Engine;



/** Purpose is to ...
 * @author talafousj
 *
 */
public interface ResourceProvider2 {

    public Engine getEngine();
    public void stop();
}
