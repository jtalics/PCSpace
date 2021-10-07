/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.util;

import javax.swing.JProgressBar;

public class ProgressBar extends JProgressBar {
  public static final int progressIncr = 1000;
  public static final int progSubIncr = 100;

  public ProgressBar() {
    paintString = true;
  }

  public void progressChanged(Object subject, String string, int min,
      int value, int max) {
    if (min >= max) {
      setIndeterminate(true);
    }
    else {
      setIndeterminate(false);
      setMinimum(min);
      setValue(value);
      setMaximum(max);
    }
    if (string == null) {
      setString("line " + new Integer(value).toString());
    }
    else {
      setString(string);
    }
  }

  private static final long serialVersionUID = 1L;
}
