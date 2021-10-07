/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.demo;

import java.awt.event.KeyEvent;
import gov.nih.ncgc.openhts.tool1.Session;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class Demo2 extends Demo {
  public Demo2(final Session session, final DemoRobot robot) {
    super(session, robot,
        "Second demo - create pointset from demo1 data source");
  }

  @Override
  public void perform() {
    r.worker = new DemoWorker(r) {
      @Override
      public String doInBackground() {
        try {
          r.setTitle(getName());
          r.setHTML("<H1>Step 1: Select data source to "
              + "create pointset for</H1>"
              + "A <i>pointset</i> gets its name from a \"set of coordinate numbers\"");
          if (!r.showModalDialogAndContinue()) {
            htmlString = "cancelled demo 2";
            return null;
          }
          session.dataSourceManager.getViewlet().tree.grabFocus();
          r.keyTyped(KeyEvent.VK_HOME);
          r.keyTyped(KeyEvent.VK_SPACE);
          r.keyTyped(KeyEvent.VK_DOWN);
          r.keyTyped(KeyEvent.VK_DOWN);
          r.keyTyped(KeyEvent.VK_RIGHT);
          r.keyTyped(KeyEvent.VK_RIGHT);
          r.keyTyped(KeyEvent.VK_SPACE);
          r.keyTypedWithModifier(KeyEvent.VK_ENTER,KeyEvent.VK_SHIFT);
          r.keyTyped(KeyEvent.VK_DOWN);
          r.keyTyped(KeyEvent.VK_DOWN);
          r.keyTyped(KeyEvent.VK_DOWN);
          r.keyTyped(KeyEvent.VK_SPACE);
          r.keyTyped(KeyEvent.VK_SPACE);
          r.keyTyped(KeyEvent.VK_SPACE);

          r.setHTML("<H1>Step 2: Create a new basis</H1>"
              + "Each column in the data source forms a descriptor.<br>"
              + "A <i>basis</i> is a collection of descriptors "
              + "which forms an <i>n</i> dimensional basis.");
          if (!r.showModalDialogAndContinue()) {
            htmlString = "cancelled demo 2";
            return null;
          }
          r.keyTyped(KeyEvent.VK_TAB);
          r.keyTypedWithModifier(KeyEvent.VK_A,KeyEvent.VK_CONTROL);
          r.typeString("demo2Basis");
          r.keyTyped(KeyEvent.VK_TAB);
          r.keyTyped(KeyEvent.VK_HOME);
          r.keyTypedWithModifier(KeyEvent.VK_DOWN,KeyEvent.VK_SHIFT);
          r.keyTypedWithModifier(KeyEvent.VK_DOWN,KeyEvent.VK_SHIFT);
          r.keyTypedWithModifier(KeyEvent.VK_DOWN,KeyEvent.VK_SHIFT);
          r.keyTyped(KeyEvent.VK_TAB);
          r.keyTyped(KeyEvent.VK_TAB);
          r.keyTyped(KeyEvent.VK_SPACE);
          r.delayNTimes(r.TICK,2);
          r.typeString("demo2Pointset");
          r.keyTyped(KeyEvent.VK_TAB);
          r.keyTyped(KeyEvent.VK_SPACE);
          r.setHTML("<H1>Step 3: Modify pointset size</H1>"
              + "Change from pixel to dot.<br>");
          if (!r.showModalDialogAndContinue()) {
            htmlString = "cancelled demo 2";
            return null;
          }
          r.keyTypedWithModifier(KeyEvent.VK_P,KeyEvent.VK_ALT);
          r.keyTyped(KeyEvent.VK_M);
          r.delayNTimes(r.TICK,2);
          r.keyTyped(KeyEvent.VK_TAB);
          r.keyTyped(KeyEvent.VK_DOWN);
          r.keyTyped(KeyEvent.VK_TAB);
          r.keyTyped(KeyEvent.VK_TAB);
          r.keyTyped(KeyEvent.VK_TAB);
          r.keyTyped(KeyEvent.VK_TAB);
          r.keyTyped(KeyEvent.VK_TAB);
          r.keyTyped(KeyEvent.VK_TAB);
          r.keyTyped(KeyEvent.VK_SPACE);
          
          htmlString = "<H1>Demo 2 completed</H1>"
              + "Notice that there is a new pointset node created in the tree.";
        }
        catch (final Throwable t) {
          t.printStackTrace();
          htmlString = "error demo2";
        }
        return null;
      }
    };
    r.worker.execute();
  }
}
