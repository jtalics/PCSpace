package gov.nih.ncgc.openhts.tool1.demo;

import java.util.concurrent.ExecutionException;
import gov.nih.ncgc.openhts.tool1.Session;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class Demo5 extends Demo {
  public Demo5(final Session session, final DemoRobot robot) {
    super(session, robot, "Fourth demo - basic mouse");
  }

  @Override
  public void perform() throws InterruptedException, ExecutionException {
//    r.worker = new SwingWorker_DELETEME<String, Object>() {
//      @Override
//      public String doInBackground() {
//        try {
//          r.setTitle(getName());
//          r
//              .setHTML("<H1>Key bindings</H1>"
//                  + "All user actions can be triggered through a menu, key, or mouse press.  The H key "
//                  + "shows all actions and their bindings.");
//          if (!r.showModalDialog(500)) {
//            return "interrupted";
//          }
//          session.dataSourceManager.viewlet.tree.grabFocus();
//          r.keyTyped(KeyEvent.VK_H);
//          r.delay(5000);
//          r.keyTyped(KeyEvent.VK_ESCAPE);
//          r.setHTML("<H1>Canvas manipulation with mouse</H1>");
//          if (!r.showModalDialog(500)) {
//            return "interrupted";
//          }
//          //
//          session.plotManager.canvas.grabFocus();
//          r
//              .setHTML("<H1>Compose</H1>Hit space to autmatically move the subject onto the canvas, "
//                  + "like composing a photo with your camera");
//          r.keyTyped(KeyEvent.VK_SPACE);
//          r.keyTyped(KeyEvent.VK_MINUS);
//          r.keyTyped(KeyEvent.VK_MINUS);
//          r.keyTyped(KeyEvent.VK_MINUS);
//          r.keyTyped(KeyEvent.VK_MINUS);
//          r.keyTyped(KeyEvent.VK_SPACE);
//          r.delay(2000);
//          r.keyTyped(KeyEvent.VK_EQUALS);
//          r.keyTyped(KeyEvent.VK_EQUALS);
//          r.keyTyped(KeyEvent.VK_EQUALS);
//          r.keyTyped(KeyEvent.VK_EQUALS);
//          r.delay(2000);
//          r.keyTyped(KeyEvent.VK_SPACE);
//          //
//          session.plotManager.canvas.grabFocus();
//          r
//              .setHTML("<H1>Rotation with compose.</H1>" +
//                  "Use arrows.  Shift and Control amplify.");
//          r.keyTyped(KeyEvent.VK_SPACE);
//          r.keyTyped(KeyEvent.VK_LEFT);
//          r.keyTyped(KeyEvent.VK_UP);
//          r.keyTyped(KeyEvent.VK_UP);
//          r.keyTyped(KeyEvent.VK_LEFT);
//          r.keyTyped(KeyEvent.VK_SPACE);
//          r.delay(2000);
//          r.keyTyped(KeyEvent.VK_LEFT);
//          r.keyTyped(KeyEvent.VK_LEFT);
//          r.keyTyped(KeyEvent.VK_LEFT);
//          r.keyTyped(KeyEvent.VK_LEFT);
//          r.delay(2000);
//          r.keyTyped(KeyEvent.VK_SPACE);
//          return "done";
//        }
//        catch (final Throwable t) {
//          t.printStackTrace();
//          return "error";
//        }
//      }
//    };
//    r.worker.execute();
//    return r.worker.get();
  }
}
