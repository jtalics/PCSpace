/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.heatmap;

import java.util.EventListener;

/** Purpose is to ...
 * @author talafousj
 */
public interface HeatmapListener extends EventListener {

  public void heatmapChanged(HeatmapEvent ev);
//  public void basisModeChanged(PlotManager plotManager, Basis basisMode);
//  public void dimensionalityChanged(PlotManager plotManager, int dim);
//  public void textSelectionChanged(PlotManager plotManager);
//  public void textPropertiesChanged(PlotManager manager, Text text);
//  public void idSelectionChanged(Object source, Map<Pointset,int[]> selected);
//  public void pointsetOrderChanged(Object source);
}

