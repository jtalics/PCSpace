package gov.nih.ncgc.openhts.tool1.util.colorizer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.ChangeListener;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Stats;
import gov.nih.ncgc.openhts.tool1.util.ProgressBar;
import gov.nih.ncgc.openhts.tool1.util.ProgressListener;

/**
 * Colors a collection of float data, not necessarily a 2D matrix.
 * 
 * @author talafousj
 */
public class Colorizer implements ProgressListener {
  private final List<Waypoint> waypoints = new ArrayList<Waypoint>();
  private Function preprocessorFunction;
  private Function coloringFunction;
  private Function[] coloringFunctions;
  private final List<ChangeListener> listeners = new ArrayList<ChangeListener>();
  private Function[] preprocessorFunctions;
  private final List<ProgressListener> progListeners = new ArrayList<ProgressListener>();
  private Waypoint[] waypointsArray;

  public Colorizer() {
    preprocessorFunctions = new Function[3];
    preprocessorFunctions[0] = new FunctionIdentity();
    preprocessorFunctions[1] = new FunctionStdDev();
    preprocessorFunctions[2] = new FunctionStdDevAbs();
    for (Function function : preprocessorFunctions) {
      function.addProgressListener(this);
    }
    preprocessorFunction = preprocessorFunctions[0];
    //
    coloringFunctions = new Function[3];
    coloringFunctions[0] = new FunctionLinear();
    coloringFunctions[1] = new FunctionLog10();
    coloringFunctions[2] = new FunctionLogistic();
    for (Function function : coloringFunctions) {
      function.addProgressListener(this);
    }
    coloringFunction = coloringFunctions[0];
    //
    waypoints.add(new Waypoint(0, new Color(0.25f, 0.0f, 0.25f)));
    waypoints.add(new Waypoint(20, Color.BLUE));
    waypoints.add(new Waypoint(40, Color.GREEN));
    waypoints.add(new Waypoint(60, Color.YELLOW));
    waypoints.add(new Waypoint(80, Color.orange));
    waypoints.add(new Waypoint(100, Color.RED));
    waypointsArray = new Waypoint[waypoints.size()];
    waypoints.toArray(waypointsArray);
    Arrays.sort(waypointsArray);
  }

  public void colorize(final float[][] points, final float[][] red,
      final float[][] green, final float[][] blue) {
    // for (int i=0; i<numbers.length; i++) {
    // System.out.print("Colorizing Row " + i+": ");
    // for (int j=0; j<numbers[i].length; j++) {
    // System.out.print(" "+ numbers[i][j]);
    // }
    // System.out.println();
    // }
    // Make a copy of points that will be modified
    float[][] values = new float[points.length][];
    for (int row = 0; row < points.length; row++) {
      values[row] = new float[points[row].length];
      for (int col = 0; col < points[row].length; col++) {
        values[row][col] = points[row][col];
      }
      if (row % ProgressBar.progressIncr == 0) {
        fireProgressChanged(this, "init preprocessing", 0, row, points.length);
      }
    }
    // float[] columnValues = new float[points.length];
    //
    for (int row = 0; row < values.length; row++) {
      // for (int col = 0; col < values.length; col++) {
      // columnValues[col] = values[row][col];
      // }
      preprocessorFunction.apply(/* columnValues */values[row]);
      // for (int col = 0; col < values.length; col++) {
      // values[row][col] = columnValues[col];
      // }
      if (row % ProgressBar.progressIncr == 0) {
        fireProgressChanged(this, "preprocessing", 0, row, points.length);
      }
    }
    //
    for (int row = 0; row < values.length; row++) {
      // for (int col = 0; col < values.length; col++) {
      // columnValues[col] = values[row][col];
      // }
      coloringFunction.apply(/* columnValues */values[row]);
      // for (int col = 0; col < values.length; col++) {
      // values[row][col] = columnValues[col];
      // }
      if (row % ProgressBar.progressIncr == 0) {
        fireProgressChanged(this, "colorizing", 0, row, points.length);
      }
    }
    mapToColor(values, red, green, blue);
  }

  public void colorize(final float[] values, final byte[][] rgb) {
    // All Functions have been selected and set up with stats
    float[] output = new float[values.length];
    for (int row = 0; row < values.length; row++) {
      output[row] = values[row];
    }
    preprocessorFunction.apply(output);
    coloringFunction.apply(output);
    // TODO: remove validation
    for (int i = 0; i < output.length; i++) {
      if (output[i] > 1.0f || output[i] < 0.0f) {
        throw new RuntimeException("coloringFunction "
            + coloringFunction.getName() + " failed");
      }
    }
    mapToColor(output, rgb);
  }

  private void mapToColor(float[][] values, float[][] red, float[][] green,
      float[][] blue) {
    float fraction;
    int i, place;
    Waypoint first, second;
    int[] placeArray = new int[waypointsArray.length];
    for (i=0; i<waypointsArray.length; i++) {
      placeArray[i]=waypointsArray[i].place;
    }
    for (int row = 0; row < values.length; row++) {
      for (int col = 0; col < values[row].length; col++) {
        if (Float.isNaN(values[row][col])) {
          continue;
        }
        place = (int) (100f * values[row][col]); // 100 percent
        if (place < 0 || place > 100) {
          throw new RuntimeException("Bad place: " + place);
        }
        i = Arrays.binarySearch(placeArray,place);
        if (i<0) { // hit between
          i=-i-1;
          first = waypointsArray[i-1];
          second = waypointsArray[i];
          fraction = ((float)place - (float)first.place) / ((float)second.place - (float)first.place);
          red[row][col] = fraction * (second.red - first.red) + first.red;
          green[row][col] = fraction * (second.green - first.green) + first.green;
          blue[row][col] = fraction * (second.blue - first.blue) + first.blue;
        }
        else { // exact hit
          red[row][col] = waypointsArray[i].red;
          green[row][col] = waypointsArray[i].green;
          blue[row][col] = waypointsArray[i].blue;
        }
        // Iterator<Waypoint> iterator = waypoints.iterator();
        // if (iterator.hasNext()) {
        // Waypoint second = iterator.next();
        // Waypoint first = null;
        // while (iterator.hasNext()) {
        // first = second;
        // second = iterator.next();
        // if (f >= first.place && f <= second.place) {
        // fraction = (f - first.place) / (second.place - first.place);
        // red[row][col] = fraction * (second.red - first.red) + first.red;
        // green[row][col] = fraction * (second.green - first.green)
        // + first.green;
        // blue[row][col] = fraction * (second.green - first.green)
        // + first.green;
        // }
        // }
        // }
      }
      if (row % ProgressBar.progressIncr == 0) {
        fireProgressChanged(this, "mapping", 0, row, values.length);
      }
    }
  }

  private void mapToColor(float[] values, byte[][] rgb) {
    // float fraction, f;
    // // NOTE: Waypoints must be sorted
    // for (int i = 0; i < values.length; i++) {
    // f = 100f * values[i];
    // if (f < 0f || f > 100f) {
    // throw new RuntimeException("Bad: " + f);
    // }
    // Iterator<Waypoint> iterator = waypoints.iterator();
    // if (iterator.hasNext()) {
    // Waypoint second = iterator.next();
    // Waypoint first = null;
    // while (iterator.hasNext()) {
    // first = second;
    // second = iterator.next();
    // if (f >= first.place && f <= second.place) {
    // fraction = (f - first.place) / (second.place - first.place);
    // 
    float fraction;
    int i, place;
    Waypoint first, second;
    int[] placeArray = new int[waypointsArray.length];
    for (i=0; i<waypointsArray.length; i++) {
      placeArray[i]=waypointsArray[i].place;
    }
    for (int row = 0; row < values.length; row++) {
      if (Float.isNaN(values[row])) {
        continue;
      }
      place = (int) (100f * values[row]); // 100 percent
      if (place < 0 || place > 100) {
        throw new RuntimeException("Bad place: " + place);
      }
      i = Arrays.binarySearch(placeArray, place);
      if (i<0) { // hit between
        i=-i-1;
        first = waypointsArray[i-1];
        second = waypointsArray[i];
        fraction = ((float)place - (float)first.place) / ((float)second.place - (float)first.place);
        rgb[row][0] = (byte) (int) (((fraction * (second.red - first.red) + first.red)) * 0xFF);
        rgb[row][1] = (byte) (int) (((fraction * (second.green - first.green) + first.green)) * 0xFF);
        rgb[row][2] = (byte) (int) (((fraction * (second.blue - first.blue) + first.blue)) * 0xFF);
      }
      else { // exact hit
        rgb[row][0] = (byte) (int) (waypointsArray[i].red * 0xFF);
        rgb[row][1] = (byte) (int) (waypointsArray[i].green * 0xFF);
        rgb[row][2] = (byte) (int) (waypointsArray[i].blue * 0xFF);
      }
    }
  }

  public Function getPreprocessorFunction() {
    return preprocessorFunction;
  }

  public Function[] getColoringFunctions() {
    return coloringFunctions;
  }

  void setSelectedPreprocessorFunction(Function preprocessor) {
    if (this.preprocessorFunction != preprocessor) {
      this.preprocessorFunction = preprocessor;
      fireChangedState(new ColorizerChangeEvent(this,
          ColorizerChangeEvent.Kind.PREPROCESSOR_FUNCTION));
    }
  }

  void clearWaypoints() {
    if (waypoints.size() > 0) {
      waypoints.clear();
      fireChangedState(new ColorizerChangeEvent(this,
          ColorizerChangeEvent.Kind.COLOR_MAP));
    }
  }

  void addWaypoint(Waypoint waypoint) {
    waypoints.add(waypoint);
    waypointsArray = new Waypoint[waypoints.size()];
    waypoints.toArray(waypointsArray);
    Arrays.sort(waypointsArray);
    waypoints.clear();
    Waypoint prevWaypoint = null;
    for (Waypoint waypoint2 : waypointsArray) {
      waypoints.add(waypoint2);
      if (prevWaypoint != null && prevWaypoint.place == waypoint2.place) {
        waypoints.remove(prevWaypoint);
        // TODO: Average colors of pair with last pair?
      }
      prevWaypoint = waypoint2;
    }
    fireChangedState(new ColorizerChangeEvent(this,
        ColorizerChangeEvent.Kind.COLOR_MAP));
  }

  void setColoringFunction(Function function) {
    if (this.coloringFunction != function) {
      this.coloringFunction = function;
      fireChangedState(new ColorizerChangeEvent(this,
          ColorizerChangeEvent.Kind.COLORING_FUNCTION));
    }
  }

  Iterator<Waypoint> getWaypointsIterator() {
    return waypoints.iterator();
  }

  public void addChangeListener(ChangeListener listener) {
    listeners.add(listener);
  }

  public void removeChangeListener(ChangeListener listener) {
    listeners.remove(listener);
  }

  public void setPreprocessorFunctionStats(Stats stats) {
    preprocessorFunction.setStats(stats);
    fireChangedState(new ColorizerChangeEvent(this,
        ColorizerChangeEvent.Kind.PREPROCESSOR_FUNCTION_STATS));
  }

  public void setPreprocessorFunctionStats(Stats[] stats) {
    for (int col = 0; col < stats.length; col++) {
      preprocessorFunction.setStatsAt(stats[col], col);
    }
    fireChangedState(new ColorizerChangeEvent(this,
        ColorizerChangeEvent.Kind.PREPROCESSOR_FUNCTION_STATS));
  }

  public void setColoringFunctionStats(Stats stats) {
    coloringFunction.setStats(stats);
    fireChangedState(new ColorizerChangeEvent(this,
        ColorizerChangeEvent.Kind.COLORING_FUNCTION_STATS));
  }

  public void setColoringFunctionStats(Stats[] stats) {
    for (int col = 0; col < stats.length; col++) {
      coloringFunction.setStatsAt(stats[col], col);
    }
    fireChangedState(new ColorizerChangeEvent(this,
        ColorizerChangeEvent.Kind.COLORING_FUNCTION_STATS));
  }

  public Function getColoringFunction() {
    return coloringFunction;
  }

  public void setSelectedColoringFunction(Function coloringFunction) {
    this.coloringFunction = coloringFunction;
    fireChangedState(new ColorizerChangeEvent(this,
        ColorizerChangeEvent.Kind.COLORING_FUNCTION));
  }

  public Function[] getPreprocessors() {
    return preprocessorFunctions;
  }

  public List<Waypoint> getWaypoints() {
    return waypoints;
  }

  public void setColumnCount(int columnCount) {
    preprocessorFunction.setColumnCount(columnCount);
    coloringFunction.setColumnCount(columnCount);
  }

  public void addProgressListener(ProgressListener listener) {
    progListeners.add(listener);
  }

  public void removeProgressListener(ProgressListener listener) {
    progListeners.remove(listener);
  }

  private void fireProgressChanged(Object subject, String string, int min,
      int value, int max) {
    for (ProgressListener listener : progListeners) {
      listener.progressChanged(subject, string, min, value, max);
    }
  }

  private void fireChangedState(ColorizerChangeEvent event) {
    for (ChangeListener listener : listeners) {
      listener.stateChanged(event);
    }
  }

  @Override
	public void progressChanged(Object subject, String string, int min,
      int value, int max) {
    fireProgressChanged(subject, string, min, value, max);
  }

  private static final long serialVersionUID = 1L;
}
// end of file
