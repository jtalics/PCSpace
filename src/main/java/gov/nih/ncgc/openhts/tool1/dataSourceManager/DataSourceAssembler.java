package gov.nih.ncgc.openhts.tool1.dataSourceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import chemaxon.formats.MolFormatException;
import chemaxon.formats.MolImporter;
import chemaxon.struc.Molecule;
import gov.nih.ncgc.openhts.tool1.FileFilters;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.Tool1Exception;

/**
 * Assembles the various user files into one dataSourceFile for internal use
 * 
 * @author talafousj
 */
public class DataSourceAssembler {
  private static final boolean debug = false;

  @Override
  public void finalize() {
    if (debug)
      System.out.println("Finalized [" + getClass().getSimpleName() + ";"
          + hashCode() + "]");
  }

  List<File> Files = new ArrayList<File>();
  private final List<String> moleculeNames = new ArrayList<String>();
  private final List<String> descriptorNames = new ArrayList<String>();
  private final Map<String,String> moleculeNameToSmiles = new HashMap<String,String>();
  private final Map<String,String[]> moleculeNameToDescriptorValues = new HashMap<String,String[]>();
  private final Map<String, List<File>> moleculeNameToFiles = new HashMap<String, List<File>>();
  private final Map<String, List<File>> descriptorNameToFiles = new HashMap<String, List<File>>();
  private final Session session;
  private int smileCol;

  DataSourceAssembler(final Session session) {
    this.session = session;
  }

  // Assemble a data source from the originating data files on disk
  // from scratch.
  int assemble(final DataSource dataSource) throws IOException, Tool1Exception {
    // Read the files' headers in each recognized file type in each directory 
    // in the dataSource to determine the size of the output matrix
    clear();
    File[] molFiles = null, descFiles = null, preassembledFiles = null;
    for (final DataSourceDir dataSourceDir : dataSource.getDataSourceDirs()) {
      molFiles = dataSourceDir.listFiles(FileFilters.moleculeFilenameFilter);
      determineRemovedFiles(dataSourceDir.syncFiles, molFiles);
      for (final File molFile : molFiles) {
        if (!dataSourceDir.syncFiles.contains(molFile)) {
          // Mol-file has no header
          dataSourceDir.syncFiles.add(molFile);
        }
      }
      descFiles = dataSourceDir.listFiles(FileFilters.descvalFilenameFilter);
      for (final File descFile : descFiles) {
        if (!dataSourceDir.syncFiles.contains(descFile)) {
          readHeaderDescFile(descFile);
          dataSourceDir.syncFiles.add(descFile);
        }
      }
      preassembledFiles = dataSourceDir
          .listFiles(FileFilters.dataSourceFilenameFilter);
      for (final File preassembledFile : preassembledFiles) {
        if (!dataSourceDir.syncFiles.contains(preassembledFile)) {
          readHeaderPreassembledFile(preassembledFile);
          dataSourceDir.syncFiles.add(preassembledFile);
        }
      }
    }
    if (debug)
      System.out.println("DS ASSEMBLER: Total number of descriptors: "
          + descriptorNames.size());
    // Now, load all the recognized types of files in each data source dir.
    for (final DataSourceDir dataSourceDir : dataSource.getDataSourceDirs()) {
      for (final File molFile : molFiles) {
        loadMolFile(molFile);
      }
      for (final File descFile : descFiles) {
        loadDescFile(descFile);
      }
      for (final File preassembledFile : preassembledFiles) {
        loadPreassembledFile(preassembledFile);
      }
    }
    // The data source is now completely loaded
    writeDataSource(dataSource);
    return (moleculeNames.size());
  }

  private void clear() {
    moleculeNames.clear();
    descriptorNames.clear();
    moleculeNameToSmiles.clear();
    moleculeNameToDescriptorValues.clear();
    moleculeNameToFiles.clear();
    descriptorNameToFiles.clear();
  }

  private void determineRemovedFiles(final List<File> syncedFiles,
      final File[] existingFiles) {
    // Determine if any files have been deleted from under us.
    for (final File syncedFile : syncedFiles) {
      int i;
      for (i = 0; i < existingFiles.length; i++) {
        // TODO: inefficient linear search
        if (syncedFile.equals(existingFiles[i])) {
          break;
        }
      }
      if (i >= existingFiles.length) {
        // file missing, need a complete rebuild
        syncedFiles.clear();
        return;
      }
    }
  }

  private void loadMolFile(File molFile) throws MolFormatException, IOException {
    if (molFile.getName().endsWith("." + FileFilters.smilesFileExt)) {
      loadSMIFile(molFile);
    }
    else if (molFile.getName().endsWith("." + FileFilters.sdFileExt)) {
      loadSDFile(molFile);
    }
    else {
      throw new IOException("unrecognized file type as judged from extension");
    }
    Collections.sort(moleculeNames);
  }

  private void loadSMIFile(final File molFile) throws IOException {
    if (debug)
      System.out
          .println("DS ASSEMBLER: Reading MOL file: " + molFile.getPath());
    // Read the molecule names from the sdf file
    BufferedReader reader = new BufferedReader(new FileReader(molFile));
    int nMols = 0;
    String line;
    String[] splitLine;
    for (;;) {
      if (null == (line = reader.readLine())) {
        break;
      }
      nMols++;
      splitLine = line.split(" ", 2);
      final String molSmiles = splitLine[0];
      final String molName = line.split(" ", 2)[1]; // TODO catch unique mol
      // name?
      addMoleculeName(molName, molSmiles, molFile);
    }
    if (debug)
      System.out.println("DS ASSEMBLER: NumberFloat of mol read: "
          + moleculeNames.size());
  }

  private void addMoleculeName(String molName, String molSmiles, File molFile) {
    if (moleculeNames.contains(molName)) {
      if (debug) {
        System.out
            .println("DS ASSEMBLER: duplicate molecule named: " + molName);
      }
      if (!molSmiles.equals(moleculeNameToSmiles.get(molName))) {
        new RuntimeException("WARNING: same molId, different SMILES: "
            + molName);
        // Use first SMILES
      }
    }
    else {
      moleculeNames.add(molName);
      moleculeNameToSmiles.put(molName,molSmiles);
    }
    // Record the file name originating this molecule
    List<File> files;
    if (moleculeNameToFiles.containsKey(molName)) {
      files = moleculeNameToFiles.get(molName);
    }
    else {
      files = new ArrayList<File>();
      moleculeNameToFiles.put(molName, files);
    }
    files.add(molFile);
  }

  private void loadSDFile(final File molFile) throws MolFormatException,
      IOException {
    // This is very slow due to using JChem
    if (debug)
      System.out.println("DS ASSEMBLER: Reading SD file: " + molFile.getPath());
    // Read the molecule names from the sdf file
    final MolImporter molImporter = new MolImporter(molFile, null);
    Molecule mol;
    int nMols = 0;
    for (;;) {
      mol = new Molecule();
      if (null == molImporter.readMol(mol)) {
        break;
      }
      nMols++;
      final String molName = mol.getName().trim(); // TODO unique mol name?
      addMoleculeName(molName, "CCCC", molFile);
    }
    if (debug)
      System.out.println("DS ASSEMBLER: NumberFloat of mol read: " + nMols);
  }

  private void readHeaderDescFile(final File descFile) throws IOException {
    // Get the header and determine the descriptors
    if (debug) {
      System.out.println("DS ASSEMBLER: Reading descriptor-value file: "
          + descFile.getPath());
    }
    // Load up Molecular Descriptors and validate
    final CSVReader mdReader = new CSVReader(new FileReader(descFile),
        DataSourceManager.csvDelimiter);
    String[] mdLine = mdReader.readNext();
    if (!FileFilters.molIdColName.equals(mdLine[0].trim())) {
      throw new RuntimeException("First column in descriptor file must be: "
          + FileFilters.molIdColName + ", found " + mdLine[0].trim());
    }
    // Process the column names of the descriptor file
    // Make sure MD and ACT have the same MOL name col name
    final String[] header = mdLine;
    List<File> files;
    for (int iCol = 1; iCol < header.length; iCol++) {
      final String colName = header[iCol].trim();
      if (colName.equalsIgnoreCase("SMILES")) {
        throw new RuntimeException("Column name may not be SMILES in a descriptor file");
      }
      if (descriptorNameToFiles.containsKey(colName)) {
        files = descriptorNameToFiles.get(colName);
      }
      else {
        files = new ArrayList<File>();
        descriptorNameToFiles.put(colName, files);
        descriptorNames.add(colName);
      }
      files.add(descFile);
    }
    mdReader.close();
  }

  private void readHeaderPreassembledFile(final File preassembledFile)
      throws IOException {
    // Get the header and determine the descriptors
    if (debug) {
      System.out.println("DS ASSEMBLER: Reading preassembled file: "
          + preassembledFile.getPath());
    }
    // Load up Molecular Descriptors and validate
    final CSVReader mdReader = new CSVReader(new FileReader(preassembledFile),
        DataSourceManager.csvDelimiter);
    String[] mdLine = mdReader.readNext();
    if (!FileFilters.molIdColName.equals(mdLine[0].trim())) {
      throw new RuntimeException("First column in preassembled file must be: "
          + FileFilters.molIdColName + ", found " + mdLine[0].trim());
    }
    // Process the column names of the descriptor file
    // Make sure MD and ACT have the same MOL name col name
    final String[] header = mdLine;
    List<File> files;
    for (int iCol = 1; iCol < header.length; iCol++) {
      if (header[iCol].trim().equalsIgnoreCase("SMILES")) {
        smileCol = iCol;
        continue;
      }
      // TODO: depending on the final format, some column names (e.g. SMILES)
      // should be excluded.
      final String colName = header[iCol].trim();
      if (descriptorNameToFiles.containsKey(colName)) {
        files = descriptorNameToFiles.get(colName);
      }
      else {
        files = new ArrayList<File>();
        descriptorNameToFiles.put(colName, files);
        descriptorNames.add(colName);
      }
      files.add(preassembledFile);
    }
    mdReader.close();
  }

  private void loadDescFile(final File descFile) throws IOException,
      Tool1Exception {
    // Assumes the header file was already read and descriptorNames absorbed
    String[] mdLine;
    final CSVReader mdReader = new CSVReader(new FileReader(descFile),
        DataSourceManager.csvDelimiter);
    mdLine = mdReader.readNext(); // skip header
    final String[] header = mdLine;
    int iLine = 1;
    boolean ignored = false;
    while (null != (mdLine = mdReader.readNext())) {
      if (mdLine.length != header.length) {
        throw new Tool1Exception("DESC file " + descFile.getPath()
            + "\ndoes not have all lines with same number of col at line: "
            + (iLine + 1) + " header.length=" + header.length
            + " mdLine.length=" + mdLine.length);
      }
      if (moleculeNames.contains(mdLine[0])) {
        // Read in the descriptor values for this line
        // Name of molecule column is assumed to be in first column
        String[] values=new String[header.length-2];
        int j=0;
        for (int i = 1; i < header.length; i++) {
          values[j++] = mdLine[i];
        }
        moleculeNameToDescriptorValues.put(mdLine[0],values);
      }
      else {
        ignored = true;
        // We are not interested in these descriptor values because 
        // we never heard of its molecule
      }
      iLine++;
    }
    if (ignored) {
      session.statusPanel
          .log2("Note: Some molecules were ignored in desc file: "
              + descFile.getName());
    }
    if (debug) {
      System.out
          .println("DS ASSEMBLER: number of molecules in descriptor-value file = "
              + (iLine - 1));
    }
    mdReader.close();
  }

  // TODO: some columns are NOT descriptors and are the molName and SMILES.
  private void loadPreassembledFile(final File preassembledFile) throws IOException,
      Tool1Exception {
    // Assumes the header file was already read and descriptorNames absorbed
    String[] mdLine;
    final CSVReader mdReader = new CSVReader(new FileReader(preassembledFile),
        DataSourceManager.csvDelimiter);
    mdLine = mdReader.readNext(); // skip header
    final String[] header = mdLine;
    int iLine = 1;
    while (null != (mdLine = mdReader.readNext())) {
      if (mdLine.length != header.length) {
        throw new Tool1Exception("PRE file " + preassembledFile.getPath()
            + "\ndoes not have all lines with same number of col at line: "
            + (iLine + 1) + " header.length=" + header.length
            + " mdLine.length=" + mdLine.length);
      }
      if (!moleculeNames.contains(mdLine[0])) {
        addMoleculeName(mdLine[0], mdLine[smileCol], preassembledFile);
      }
      // Read in the descriptor values for this line
      // Name of molecule column is assumed to be in first column
      String[] values=new String[header.length-2];
      int j=0;
      for (int i = 1; i < header.length; i++) {
        if (i==smileCol) {continue;}
        values[j++] = mdLine[i];
      }
      moleculeNameToDescriptorValues.put(mdLine[0],values);
      iLine++;
    }
    if (debug) {
      System.out
          .println("DS ASSEMBLER: number of molecules in preassembled file = "
              + (iLine - 1));
    }
    mdReader.close();
  }

  void writeDataSource(final DataSource dataSource) throws IOException {
    // ASSUME: all mol, desc, and pre files have been read into memory
    // Write data source to file it specifies
    if (debug) {
      System.out.println("DS ASSEMBLER: writing out "
          + dataSource.getDataSourceFile());
    }
    final CSVWriter dataSourceFileWriter = new CSVWriter(new FileWriter(
        dataSource.getDataSourceFile()), DataSourceManager.csvDelimiter,
        CSVWriter.NO_QUOTE_CHARACTER);
    // Write out header info into data source file
    final String[] line = new String[descriptorNames.size() + 2];
    line[0] = FileFilters.molIdColName;
    line[1] = "SMILES";
    for (int i = 2; i < descriptorNames.size()+2; i++) {
      line[i] = descriptorNames.get(i-2);
    }
    dataSourceFileWriter.writeNext(line);
    for (String moleculeName : moleculeNames) {
      line[0] = moleculeName;
      line[1] = moleculeNameToSmiles.get(moleculeName);
      String[] descriptorValues = moleculeNameToDescriptorValues.get(moleculeName);
      for (int iDesc = 0; iDesc < descriptorValues.length; iDesc++) {
        line[2 + iDesc] = "" + descriptorValues[iDesc];
      }
      dataSourceFileWriter.writeNext(line);
    }
    if (debug) {
      System.out.println("DS ASSEMBLER: wrote " + moleculeNames.size()
          + " mol to " + dataSource.getDataSourceFile());
    }
    dataSourceFileWriter.close();
  }
}
