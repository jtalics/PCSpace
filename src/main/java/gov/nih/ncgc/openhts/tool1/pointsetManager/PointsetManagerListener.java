/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.pointsetManager;

import gov.nih.ncgc.openhts.tool1.util.ProgressListener;

/** Purpose is to ...
 * @author talafousj
 */
public interface PointsetManagerListener extends ProgressListener {

  public void pointsetManagerChanged(PointsetManagerEvent event);
}

