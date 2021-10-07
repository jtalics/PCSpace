package gov.nih.ncgc.openhts.tool1.dataSourceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

  public class DataSourceDir extends File {
    boolean synched;
    // monitor and keep these files in sync
    final List<File> syncFiles = new ArrayList<File>();

    //
    public DataSourceDir(final String dirPath) {
      super(dirPath==null?"default":dirPath);
    }
    private static final long serialVersionUID = 1L;
  }