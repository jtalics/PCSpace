/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.util.colorizer;

import java.util.ArrayList;
import java.util.List;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetManagerListener;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Stats;
import gov.nih.ncgc.openhts.tool1.util.ProgressListener;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public abstract class Function {
  protected Stats[] statsPerCol;
  protected String name;
  protected String description;
  protected FunctionViewlet viewlet;
  protected final List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();

  public Function() {
  }

  public void setColumnCount(int columnCount) {
    statsPerCol = new Stats[columnCount];
  }
  
  public final void apply(float[] values) {
    fireProgressChanged(this, name, 0, 0, 0);
    doApply(values);
  }

  protected abstract void doApply(float[] values);
  
  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return "[Function; " + getName() + "; " + getDescription() + "]";
  }

  public void setStats(Stats stats) {
    statsPerCol = new Stats[]{stats};
    if (stats != null && viewlet != null) {
      viewlet.build();
    }
  }

  public void setStatsAt(Stats stats, int col) {
    statsPerCol[col] = stats;
    if (stats != null && viewlet != null) {
      viewlet.build();
    }
  }

  public FunctionViewlet getViewlet() {
    return viewlet;
  }

  public Stats getStats() {
    return statsPerCol[0];
  }
  
  public void removeProgressListener(ProgressListener listener) {
    progressListeners.remove(listener);
  }

  public void addProgressListener(final ProgressListener listener) {
    // TODO: what if duplicate listener?
    progressListeners.add(listener);
  }

  public void removeProgressListener(
      final PointsetManagerListener listener) {
    progressListeners.remove(listener);
  }

  public void fireProgressChanged(Function function, String string, int min,
      int value, int max) {
    for (final ProgressListener progressListener : progressListeners) {
      progressListener.progressChanged(function, string, min, value, max);
    }
  }
}
