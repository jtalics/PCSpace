/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.dirMonitor;

import java.util.EventListener;

/** Purpose is to ...
 * @author talafousj
 *
 */
public interface DirMonitorListener extends EventListener {
  public void dirMonitorChanged(DirMonitorEvent ev);
//  public void fileAdded(DirMonitor service, File dir);
//  public void fileModified(DirMonitor service, File dir);
//  public void fileRemoved(DirMonitor service, File dir);
//  public void quit(DirMonitor service);
//  public void missingDir(DirMonitor service, File dir);
}
