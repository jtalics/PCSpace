/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.demo;

import javax.swing.SwingWorker;

/** Purpose is to ...
 * @author talafousj
 *
 */
public abstract class DemoWorker extends SwingWorker<String, Object> {
  
  DemoRobot r;
  String htmlString;
  
  public DemoWorker(final DemoRobot robot) {
    r=robot;
  }
  
  @Override
  public void done() {
    r.finished(htmlString);
  }
}
