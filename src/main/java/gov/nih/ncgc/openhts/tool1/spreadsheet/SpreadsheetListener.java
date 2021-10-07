package gov.nih.ncgc.openhts.tool1.spreadsheet;

import java.util.EventListener;

/** Purpose is to ...
 * @author talafousj
 */
public interface SpreadsheetListener extends EventListener {
  public void spreadsheetChanged(SpreadsheetEvent ev);

//  public void pointsetSelectionChanged(Pointset pointset);
//  public void pointSelectionChanged(Pointset pointset, int[] selectedPoints);
//  public void leadSelectionChanged(Pointset pointset, int rowIndex);
}

