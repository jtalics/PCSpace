/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.demo;

import java.awt.event.KeyEvent;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.Version;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class Demo3 extends Demo {
  public Demo3(final Session session, final DemoRobot robot) {
    super(session, robot, "Third demo - Session persistence");
  }
    @Override
    public void perform() {
      r.worker = new DemoWorker(r) {
        @Override
        public String doInBackground() {      
          try {
            r.setTitle(getName());
            r.setHTML("<H1>Step 1: End session.</H1>" +
                "<H3>Prerequisites: Demo 2</H3>" +
                "All major items that you create in "+Version.PRODUCT_NAME_SHORT+" will be persisted between " +
                "sessions.  After you click Continue below, the Session will end.  " +
                "Upon restarting "+Version.PRODUCT_NAME_SHORT+", you will see that the app is back to nearly " +
                    "the same state as you left it.");
            if (!r.showModalDialogAndContinue()) {
              htmlString="interrupted";
              return null;
            }
            r.keyPress(KeyEvent.VK_ALT);
            r.keyTyped(KeyEvent.VK_S);
            r.keyTyped(KeyEvent.VK_E);
            r.keyRelease(KeyEvent.VK_ALT);
            return "Done.";
          }
          catch (final Throwable t) {
            t.printStackTrace();
            htmlString="error";
          }
          return null;
        }
        
      };
      r.worker.execute();
    }
    
}
