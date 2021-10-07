package gov.nih.ncgc.openhts.tool1.util;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Provides a throwaway superlightweight easy to use component so
 * the model-developer can slap stuff on the screen quick.  Not intended
 * to be used for the long term.
 * 
 * @author talafousj
 */
public abstract class Viewlet extends JScrollPane {
  protected JPanel topPanel;

  public Viewlet() {
    topPanel = new JPanel(new BorderLayout());
    setViewportView(topPanel);
  }

  public abstract Viewlet build();
}
