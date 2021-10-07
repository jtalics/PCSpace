/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class ExecPump extends Thread {
  final InputStream is;
  final StringBuffer buffer;

  public ExecPump(final InputStream is, final StringBuffer buffer) {
    this.is = is;
    this.buffer = buffer;
  }

  @Override
  public void run() {
    try {
      final InputStreamReader isr = new InputStreamReader(is);
      final BufferedReader br = new BufferedReader(isr);
      String line = null;
      while ((line = br.readLine()) != null) {
        buffer.append(line);
      }
    }
    catch (final IOException ioe) {
      ioe.printStackTrace();
    }
  }
}
