/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.dataSourceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import gov.nih.ncgc.openhts.tool1.FileFilters;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.persistence.Persistent;

/**
 * A DataSource is made up of one or more directory/folder. It contains
 * formatted text files of various types that must be visited and processed so
 * that the data in them is in sync with the DataSource.
 * 
 * @author talafousj
 */
public class DataSource implements DataSourceManagerEntity, DataSourceManagerListener, Comparable <DataSource>,
    Persistent {
  private String name;
  private File dataSourceFile;
  private int datumCount;
  private final List<DataSourceDir> dataSourceDirs = new ArrayList<DataSourceDir>();
  private boolean loading;

  public DataSource() {
    // nop
  }

  public void initialize(final String name, final List<String> pathList) {
    setName(name);
    if (pathList != null) {
      addDataSourceDirPaths(pathList);
    }
    initializeTransient();
  }

  // @Implements Persistent
  @Override
	public void initializeTransient() {
    // TODO Auto-generated method stub
  }

  public void addDataSourceDirPaths(final List<String> dataSourceDirPaths) {
    for (final String dataSourceDirPath : dataSourceDirPaths) {
      addDataSourceDirPath(dataSourceDirPath);
    }
  }

  public void addDataSourceDirPath(final String dataSourceDirPath) {
    final DataSourceDir dataSourceDir = new DataSourceDir(dataSourceDirPath);
    dataSourceDirs.add(dataSourceDir);
  }

  public DataSourceDir getDataSourceDirAt(final int i) {
    return dataSourceDirs.get(i);
  }

  @Override
  public String toString() {
    return "[Datasource; " + name + "; " + dataSourceDirs.size() + "; "
        + datumCount + "]";
  }

  public List<File> getMolFiles() {
    final List<File> molFiles = new ArrayList<File>();
    for (final File dataSourceDir : dataSourceDirs) {
      for (final File file : dataSourceDir
          .listFiles(FileFilters.moleculeFilenameFilter)) {
        molFiles.add(file);
      }
    }
    return molFiles;
  }

  public String getToolTipText() {
    final StringBuffer sb = new StringBuffer();
    sb.append("<HTML>");
    sb.append(name + "<hr/>");
    for (final File dataSourceDir : dataSourceDirs) {
      sb.append("<br>" + dataSourceDir.getPath() + "</br>");
    }
    sb.append("</HTML>");
    return sb.toString();
  }

  public String getName() {
    return name;
  }

  public File getDataSourceFile() {
    return dataSourceFile;
  }

  public void setName(final String name) {
    if (!Session.isLegalName(name, false)) {
      throw new RuntimeException("illegal name: " + name);
    }
    this.name = name;
  }

  public void setDataSourceFile(final File dataSourceFile) {
    this.dataSourceFile = dataSourceFile;
  }

  @Override
	public int compareTo(final DataSource dataSource) {
    String name1 = getName();
    String name2 = dataSource.getName();
    if (name1 == null) {
      if (name2 == null) {
        return 0;
      }
      return Integer.MIN_VALUE;
    }
    if (name2 == null) {
      return Integer.MAX_VALUE;
    }
    return name1.compareTo(name2);
  }

  // @Implements Persistent
  @Override
	public void setSession(Session session) {
    // TODO Auto-generated method stub
  }

  @Override
  public void finalize() {
    System.out.println("Finalized [" + getClass().getSimpleName() + ";"
        + hashCode() + "]");
  }

  public int getDatumCount() {
    return datumCount;
  }

  public void setDatumCount(int count) {
    datumCount = count;
  }

  public DataSourceDir[] getDataSourceDirs() {
    return dataSourceDirs.toArray(new DataSourceDir[dataSourceDirs.size()]);
  }

  public void addDataSourceDir(DataSourceDir[] dataSourceDirs) {
    for (DataSourceDir dir : dataSourceDirs){
      this.dataSourceDirs.add(dir);
    }
  }

  @Override
	public void progressChanged(Object subject, String string, int min, int value, int max) {
    // TODO Auto-generated method stub
    
  }

  public boolean isLoading() {
    return loading;
  }

  public void setLoading(boolean loading) {
    this.loading = loading;
  }
  @Override
	public void dataSourceManagerChanged(DataSourceManagerEvent ev) {
    // TODO
    switch(ev.kind) {
    case CHANGED:
      break;
    case MEMBER_ADDED:
      break;
    case MEMBER_CHANGED:
      break;
    case MEMBER_REMOVED:
      break;
    }
  }

}
