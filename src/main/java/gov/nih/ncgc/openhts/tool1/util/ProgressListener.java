/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.util;

import java.util.EventListener;

/** Purpose is to ...
 * @author talafousj
 */
public interface ProgressListener extends EventListener {

  public void progressChanged(Object subject, String string, int min, int value, int max);
}

