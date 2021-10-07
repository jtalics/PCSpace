package gov.nih.ncgc.openhts.tool1.demo;

import java.awt.event.KeyEvent;
import gov.nih.ncgc.openhts.tool1.Session;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class Demo1 extends Demo {
  public Demo1(final Session session, final DemoRobot robot) {
    super(session, robot, "First demo - clear session and create a data source");
  }

  @Override
  public void perform() {
    r.worker = new DemoWorker(r) {
      @Override
      public String doInBackground() {
        try {
          r.setTitle(getName());
          r.setHTML("<H1>Step1: Clear session</H1>");
          if (!r.showModalDialogAndContinue()) {
            htmlString = "cancelled demo 1";
            return null;
          }
          r.keyTypedWithModifier(KeyEvent.VK_S,KeyEvent.VK_ALT);
          r.keyTyped(KeyEvent.VK_DOWN);
          r.keyTyped(KeyEvent.VK_DOWN);
          r.keyTyped(KeyEvent.VK_DOWN);
          r.keyTyped(KeyEvent.VK_DOWN);
          r.keyTyped(KeyEvent.VK_DOWN);
          r.keyTyped(KeyEvent.VK_SPACE);
          r.setHTML("<H1>Step2: Specify a new DataSource</H1>");
          if (!r.showModalDialogAndContinue()) {
            htmlString = "cancelled demo 1";
            return null;
          }
          session.dataSourceManager.getViewlet().tree.grabFocus();
          r.keyTyped(KeyEvent.VK_HOME);
          r.keyTyped(KeyEvent.VK_SPACE);
          r.keyTyped(KeyEvent.VK_DOWN);
          r.keyTyped(KeyEvent.VK_DOWN);
          r.keyTyped(KeyEvent.VK_SPACE);
          r.keyTypedWithModifier(KeyEvent.VK_ENTER,KeyEvent.VK_SHIFT);
          // 
          r.keyTyped(KeyEvent.VK_DOWN);
          r.keyTyped(KeyEvent.VK_SPACE);
          //
          r.typeString("demo1DataSource");
          r.keyTyped(KeyEvent.VK_TAB);
          r.keyTyped(KeyEvent.VK_HOME);
          r.keyTyped(KeyEvent.VK_F2);
          r.typeString("\\demo");
          r.keyTyped(KeyEvent.VK_ENTER);
          r.keyTyped(KeyEvent.VK_TAB, 5);
          r.keyTyped(KeyEvent.VK_ENTER);
          htmlString = "done with demo1";
        }
        catch (final Throwable t) {
          t.printStackTrace();
          htmlString = "error with demo1";
        }
        return null;
      }
    };
    r.worker.execute();
  }
}
