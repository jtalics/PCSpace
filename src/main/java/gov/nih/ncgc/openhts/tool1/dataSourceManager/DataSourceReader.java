package gov.nih.ncgc.openhts.tool1.dataSourceManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import au.com.bytecode.opencsv.CSVReader;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.Tool1Exception;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.filter.DataSourceFilter;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Descriptor;
import gov.nih.ncgc.openhts.tool1.pointsetManager.ByteSequence;
import gov.nih.ncgc.openhts.tool1.pointsetManager.ColumnHead;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;
import gov.nih.ncgc.openhts.tool1.util.ProgressBar;
import gov.nih.ncgc.openhts.tool1.util.ProgressListener;

/**
 * Purpose is to read the CSV text file that holds the user info. Data is
 * transfered from DataSourceManager to PointSetManager thru this CSV text file.
 * TODO: refactor, should just load a datasource, then app accesses the data
 * source
 * 
 * @author talafousj
 */
public class DataSourceReader {
  private final List<ProgressListener> listeners = new ArrayList<ProgressListener>();
  private int headerLineLength;
  int smilesCol = -1, cidCol = -1;
  private final Session session;
  //private ProgressListener progressListener;
  //private int gount = -1;
  private String[] columnNames;
  private int objCount = -1;
  private DataSource dataSource;

  public DataSourceReader(Session session, DataSource dataSource) {
    this.dataSource = dataSource;
    this.session = session;
  }

  private String[] getLegalizedColumnNames(CSVReader reader) throws IOException {
    // CSVReader reader = new CSVReader(this,
    // DataSourceManager.csvDelimiter);
    final String[] columnNames2 = reader.readNext();
    // int j;
    // while(-1!=(j=read())) {
    // System.out.print(((char)j));
    // }
    for (int i = 0; i < columnNames2.length; i++) {
      columnNames2[i] = Session.legalizeName(columnNames2[i],false);
    }
    // reader.close();
    return columnNames2;
  }

  private void processHeader(CSVReader reader) throws IOException {
    // TODO: trap empty file
    columnNames = getLegalizedColumnNames(reader);
    headerLineLength = columnNames.length;
    for (int iCol = 0; iCol < columnNames.length; iCol++) {
      final String colName = columnNames[iCol];
      if (colName.trim().equalsIgnoreCase("CID")) {
        cidCol = iCol;
      }
      if (colName.trim().equalsIgnoreCase("SMILES")) {
        smilesCol = iCol;
      }
//      Descriptor descriptor = session.descriptorManager.getDescriptorByName(colName);
//      switch (descriptor.getColumnHeadKind()) {
//      case NumberFloat:
//        break;
//      case String:
//        break;
//      case NumberInt:
//        throw new RuntimeException("TODO");
//      }
    }
    if (smilesCol < 0) {
      // TODO: not really an IOException
      throw new IOException("no SMILES column found");
    }
  }

  public void readAll(final Pointset pointset) throws NumberFormatException,
      IOException, Tool1Exception {
    // final String[] columnNames = getLegalizedColumnNames();

    FileReader fileReader = new FileReader(dataSource.getDataSourceFile());
    CSVReader reader = new CSVReader(fileReader, DataSourceManager.csvDelimiter);
    processHeader(reader);

    final Map<Descriptor, Integer> descriptorToColumnNameIndex = mapDescriptorToColumnNameIndex(
        session.descriptorManager.getDescriptors(), columnNames);
    
    String[] nextLine;
    int iObj = 0;
    while (null != (nextLine = reader.readNext())) {
      if (iObj>=objCount) {
        throw new RuntimeException("bad objCount");
      }
      if (nextLine.length != headerLineLength) {
        throw new Tool1Exception("Row " + (iObj + 1)
            + " is of different column count (" + nextLine.length
            + ") than header line = " + headerLineLength);
      }
      pointset.setObjNameAt(iObj, new ByteSequence(nextLine[cidCol].getBytes()));
      // if (nextLine[smilesCol]==null) {
      // nextLine[smilesCol]="";
      // }
      pointset.setObjDescriptionAt(iObj, new ByteSequence(
        nextLine[smilesCol].getBytes()));

      // The pointset is in user space right now
      // float[] points = pointset.numbers[iObj];
      for (int i = 0; i < pointset.getBasis().getDimensionality(); i++) {
        Descriptor descriptor = pointset.getBasis().getDescriptor(i);
        final int col = descriptorToColumnNameIndex.get(descriptor).intValue();
        pointset.setUserValue(nextLine[col], iObj, descriptor);
//        try {
//          points[i] = Float.parseFloat(nextLine[col]);
//        }
//        catch (NumberFormatException e) {
//          System.out.println("bad float: " + nextLine[col]);
//          points[i] = Float.NaN;
//        }
      }
      if (iObj++ % ProgressBar.progressIncr == 0) {
        fireProgressChanged(pointset, "reading", 0, iObj, objCount);
      }
    }
    reader.close();
    pointset.destring();
    session.pointsetManager
        .fireProgressChanged(pointset, "done", 0, iObj, iObj);
  }

  public void countObjs(Object subject) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(
        new FileInputStream(dataSource.getDataSourceFile())));
    br.readLine(); // skip header
    int iObj = 0, prog = 0;
    while (null != br.readLine()) {
      if (iObj++ % ProgressBar.progressIncr == 0) {
        fireProgressChanged(subject, "counting " + iObj, 0, prog++
            % ProgressBar.progSubIncr, ProgressBar.progSubIncr);
      }
    }
    br.close();
    objCount = iObj;
    session.statusPanel.log2(dataSource.getDataSourceFile().getName() + ": "
        + objCount + " rows excuding header");
  }

  public int[] countObjsAndMatchLines(DataSourceFilter filter, Pointset pointset)
      throws NumberFormatException, IOException, Tool1Exception {
    FileReader fileReader = new FileReader(dataSource.getDataSourceFile());
    CSVReader reader = new CSVReader(fileReader, DataSourceManager.csvDelimiter);
    processHeader(reader);

    final Map<Descriptor, Integer> descriptorToColumnNameIndex = mapDescriptorToColumnNameIndex(
        session.descriptorManager.getDescriptors(), columnNames);

//    FileReader fileReader = new FileReader(dataSource.getDataSourceFile());
//    CSVReader reader = new CSVReader(fileReader, DataSourceManager.csvDelimiter);
    String[] nextLine = reader.readNext(); // skip header
    List<Integer> matchedLines = new ArrayList<Integer>();
    int iObj = 0, prog = 0, col;
    while (null != (nextLine = reader.readNext())) {
      if (nextLine.length != headerLineLength) {
        throw new Tool1Exception("Row " + (iObj + 1)
            + " is of different column count (" + nextLine.length
            + ") than header line = " + headerLineLength);
      }
      for (int i = 0; i < filter.getColumnHeadCount(); i++) {
        col = descriptorToColumnNameIndex.get(filter.getColumnHeadAt(i)).intValue();
        filter.setDescriptorValueAt(nextLine[col], i);
      }
      if (filter.isMatch()) {
        matchedLines.add(new Integer(iObj));
      }
      if (iObj++ % ProgressBar.progressIncr == 0) {
        fireProgressChanged(pointset, "matching " + iObj, 0, prog++
            % ProgressBar.progSubIncr, ProgressBar.progSubIncr);
      }
    }
    reader.close();
    objCount = iObj;
    fireProgressChanged(this, "done ", 0, iObj, iObj);
    int[] matchedLinesArray = new int[matchedLines.size()];
    int i = 0;
    for (Integer matchedLine : matchedLines) {
      matchedLinesArray[i] = matchedLine.intValue();
      i++;
    }
    return matchedLinesArray;
  }

  public void addProgressListener(ProgressListener listener) {
    listeners.add(listener);
  }

  public void removeProgressListener(ProgressListener listener) {
    listeners.remove(listener);
  }

  private void fireProgressChanged(Object subject, String string, int min,
      int value, int max) {
    for (ProgressListener listener : listeners) {
      listener.progressChanged(subject, string, min, value, max);
    }
  }

  public void fetchLines(int[] matchedLines, final Pointset pointset)
      throws NumberFormatException, IOException, Tool1Exception {
    //session.pointsetManager.setLoading(pointset, true);
    // final String[] columnNames = getLegalizedColumnNames();
    FileReader fileReader = new FileReader(dataSource.getDataSourceFile());
    CSVReader reader = new CSVReader(fileReader, DataSourceManager.csvDelimiter);
    processHeader(reader);
    final Map<Descriptor, Integer> descriptorToColumn = mapDescriptorToColumnNameIndex(
        session.descriptorManager.getDescriptors(), columnNames);
    String[] nextLine;
    // Skip the header we just read
    nextLine = reader.readNext();
    int iObj = 0, matchCount = 0;
    while (matchCount < matchedLines.length
        && null != (nextLine = reader.readNext())) {
      if (matchedLines[matchCount] == iObj) {
        pointset.setObjNameAt(matchCount,new ByteSequence(nextLine[cidCol]
            .getBytes()));
        pointset.setObjDescriptionAt(matchCount,new ByteSequence(
            nextLine[smilesCol].getBytes()));
        for (int i = 0; i < pointset.getBasis().getDimensionality(); i++) {
          Descriptor descriptor = pointset.getBasis().getDescriptor(i);
          int col = descriptorToColumn.get(descriptor);
          pointset.setUserValue(nextLine[col], matchCount, descriptor);
        }
        matchCount++;
      }
      if (iObj % ProgressBar.progressIncr == 0) {
        fireProgressChanged(pointset, "fetching ", 0, iObj, objCount);
      }
      iObj++;
    }
    reader.close();
    pointset.destring();
    //session.pointsetManager.setLoading(pointset, false);
    fireProgressChanged(pointset, "done ", 0, iObj, iObj);
    if (matchCount != matchedLines.length) {
      throw new RuntimeException("not all matched lines were fetched");
    }
  }

  // scan the descriptor columns and accumulate
  // a preview of the values in that column, like a range for floats, or n
  // random strings for a String column
  public void readPreview(Map<ColumnHead, Preview> columnHeadToPreview)
      throws IOException, Tool1Exception {
    // final String[] columnNames = getLegalizedColumnNames();
    FileReader fileReader = new FileReader(dataSource.getDataSourceFile());
    CSVReader reader = new CSVReader(fileReader, DataSourceManager.csvDelimiter);
    processHeader(reader);
    final Map<Descriptor, Integer> descriptorToColumn = mapDescriptorToColumnNameIndex(
        session.descriptorManager.getDescriptors(), columnNames);
    for (Descriptor descriptor : session.descriptorManager.getDescriptors()) {
      switch (descriptor.getColumnHeadKind()) {
      case String:
        columnHeadToPreview.put(descriptor, new PreviewString(descriptor));
        break;
      case NumberFloat:
        columnHeadToPreview.put(descriptor, new PreviewFloat(descriptor));
        break;
      case NumberInt:
        throw new Tool1Exception("Cannot preview descriptor of kind "
            + descriptor.getDescriptorKind());
      }
    }
    String[] nextLine;
    int iObj = 0, prog = 0;
    while (null != (nextLine = reader.readNext())) {
      if (nextLine.length != headerLineLength) {
        throw new Tool1Exception("Row " + (iObj + 1)
            + " is of different column count (" + nextLine.length
            + ") than header line = " + headerLineLength);
      }
      for (Descriptor descriptor : session.descriptorManager.getDescriptors()) {
        Integer columnIndex = descriptorToColumn.get(descriptor);
        columnHeadToPreview.get(descriptor).addValue(nextLine[columnIndex.intValue()]);
      }
      if (iObj++ % ProgressBar.progressIncr == 0) {
        fireProgressChanged(this, "previewing " + iObj, 0, prog++
            % ProgressBar.progSubIncr, ProgressBar.progSubIncr);
      }
    }
    reader.close();
    fireProgressChanged(this, "done ", 0, iObj, iObj);
  }

  private Map<Descriptor, Integer> mapDescriptorToColumnNameIndex(
      final Descriptor[] descriptors, final String[] columnNames2)
      throws Tool1Exception {

    Map<Descriptor, Integer> descriptorToColumn = new HashMap<Descriptor, Integer>();
    
    for (final Descriptor descriptor : descriptors) {
      String candColName = descriptor.getName();
      int i;
      for (i = 0; i < columnNames2.length; i++) {
        if (columnNames2[i].equals(candColName)) {
          descriptorToColumn.put(descriptor, i);
          break;
        }
      }
      if (i >= columnNames2.length) {
        throw new Tool1Exception("cannot find as column header: descriptor="
            + descriptor.getName());
      }
    }
    return descriptorToColumn;
  }

  public int getHeaderLineLength() {
    return headerLineLength;
  }

  public void recognizeDescriptorKind(List<Descriptor> unrecognizedDescriptors)
      throws IOException, Tool1Exception {
    // For all the columns that haven't been recognized into a Descriptor yet,
    // scan the column and try to determine whether user meant column to be
    // Strings or floats.
    if (unrecognizedDescriptors.size() <= 0) {
      return;
    }
    // final String[] columnNames = getLegalizedColumnNames();
    final Map<Descriptor, Integer> descriptorToColumn = mapDescriptorToColumnNameIndex(
        unrecognizedDescriptors.toArray(new Descriptor[unrecognizedDescriptors
            .size()]), columnNames);
    final List<Descriptor> unrecognized = new ArrayList<Descriptor>(
        unrecognizedDescriptors);
    final List<Descriptor> recognized = new ArrayList<Descriptor>();
    FileReader fileReader = new FileReader(dataSource.getDataSourceFile());
    CSVReader reader = new CSVReader(fileReader, DataSourceManager.csvDelimiter);
    String[] nextLine = reader.readNext(); // skip header
    int iObj = 0, prog = 0;
    while (null != (nextLine = reader.readNext())) {
      if (nextLine.length != headerLineLength) {
        throw new Tool1Exception("Row " + (iObj + 1)
            + " is of different column count (" + nextLine.length
            + ") than header line = " + headerLineLength);
      }
      for (Descriptor descriptor : unrecognized) {
        Integer columnIndex = descriptorToColumn.get(descriptor);
        try {
          Float.parseFloat(nextLine[columnIndex]);
        }
        catch (NumberFormatException e) {
          recognized.add(descriptor);
          session.statusPanel.log2("descriptor " + descriptor.getName()
              + ", value = '" + nextLine[columnIndex] + "' near line = " + iObj
              + ": could not be parsed as a float, defaulting kind to "
              + ColumnHead.Kind.String.name());
          descriptor.setColumnHeadKind(ColumnHead.Kind.String);
          System.out.println("Descriptor " + descriptor.getName()
              + " recognized as " + descriptor.getColumnHeadKind());
        }
      }
      if (recognized.size() > 0) {
        for (Descriptor descriptor : recognized) {
          unrecognized.remove(descriptor);
        }
        recognized.clear();
      }
      iObj++;
      if (iObj % ProgressBar.progressIncr == 0) {
        fireProgressChanged(this, "parsing " + iObj, 0, prog++
            % ProgressBar.progSubIncr, ProgressBar.progSubIncr);
      }
    }
    for (Descriptor descriptor : unrecognized) {
      descriptor.setColumnHeadKind(ColumnHead.Kind.NumberFloat);
      descriptor.setKind(Descriptor.Kind.Supplied);
      System.out.println("Descriptor " + descriptor.getName()
          + " recognized as " + descriptor.getColumnHeadKind());
    }
    reader.close();
    fireProgressChanged(this, "done ", 0, iObj, iObj);
  }

  //  
  // @Override
  // public void close() {
  // // The CSV reader closes us, so use CLOSE()
  // //new RuntimeException("close!").printStackTrace();
  // }
  //
  // public void CLOSE() throws IOException {
  // super.close();
  // }
  public String[] getColumnNames() throws IOException {
    if (columnNames == null) {
      FileReader fileReader = new FileReader(dataSource.getDataSourceFile());
      processHeader(new CSVReader(fileReader, DataSourceManager.csvDelimiter));
    }
    return columnNames;
  }

  public void setObjCount(int objCount) {
    this.objCount = objCount;
  }

  public int getObjCount() {
    return objCount;
  }
}
