package gov.nih.ncgc.openhts.tool1.viewers;

/** Purpose is to ...
 * @author talafousj
 *
 */
public interface SelectionViewerListener {

  public abstract void selectionChanged(String selectionName);
  public abstract String urlChanged(String urlString);
}
