/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.plotManager;

import java.util.EventListener;

/** Purpose is to ...
 * @author talafousj
 */
public interface PlotManagerListener extends EventListener {

  public void plotManagerChanged(PlotManagerEvent ev);
//  public void basisModeChanged(PlotManager plotManager, Basis basisMode);
//  public void dimensionalityChanged(PlotManager plotManager, int dim);
//  public void textSelectionChanged(PlotManager plotManager);
//  public void textPropertiesChanged(PlotManager manager, Text text);
//  public void idSelectionChanged(Object source, Map<Pointset,int[]> selected);
//  public void pointsetOrderChanged(Object source);
}

