/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.dataSourceManager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import chemaxon.formats.MolImporter;
import chemaxon.struc.Molecule;
import gov.nih.ncgc.openhts.tool1.Session;

/**
 * Purpose is to merge the Molecule Descriptor and Activities into a Pcsdata
 * file that can be read by Tool1.
 * 
 * @author talafousj
 */
public class MdActMerge {
  // ACTIVITY FILE COLUMN NAMES
  File sdFile=new File("c:\\tool1_work\\mol_act_1\\mol1335.sdf"),
    activityFile=new File("c:\\tool1_work\\mol_act_1\\act1335.csv"),
    molDescFile=new File("c:\\tool1_work\\mol_act_1\\md1335.md"), 
    pcsdataFile=new File("c:\\tool1_work\\mol_act_1\\mol_md_act1335.pcsdata");
  private Session session;

  public MdActMerge(final Session session) {
    this.session = session;
  }

  public MdActMerge(final File sdFile, final File molDescFile, final File activityFile,
      final File pcsdataFile) {
    this.sdFile = sdFile;
    this.molDescFile = molDescFile;
    this.activityFile = activityFile;
    this.pcsdataFile = pcsdataFile;
  }

  public MdActMerge() {
    // TODO Auto-generated constructor stub
  }

  public void merge() throws IOException {
    // First make sure we know where are files are
    System.out.println("SDF: "+sdFile.getName());
    final MolImporter molImporter = new MolImporter(sdFile, null);
    System.out.println("MD: "+molDescFile.getName());
    final CSVReader mdReader = new CSVReader(new FileReader(molDescFile),'\t');
    System.out.println("ACT: "+activityFile.getName());
    final CSVReader actReader = new CSVReader(new FileReader(activityFile));
    System.out.println("PCSDATA: "+pcsdataFile.getName());
    final CSVWriter pcsdataWriter = new CSVWriter(new FileWriter(pcsdataFile),',',CSVWriter.NO_QUOTE_CHARACTER);

    // Read the molecule names from the sdf file
    final List<String> molNames = new ArrayList<String>();
    final List<String> molSmiless = new ArrayList<String>();
    Molecule mol;
    for (;;) {
      mol = new Molecule();
      if (null == molImporter.readMol(mol)) {
        break;
      }
      molNames.add(mol.getName());
      molSmiless.add(mol.toFormat("smiles"));
    }
    System.out.println("number of MOL in SD= " + molNames.size());
    // Load up Molecular Descriptors and validate
    List<String[]> mdLineList = new ArrayList<String[]>();
    mdLineList = mdReader.readAll();
    System.out.println("number of MOL in MD= " + (mdLineList.size()-1));
    final String molNameColName = mdLineList.get(0)[0].trim();
    if (molNames.size() != mdLineList.size()-1) {
      throw new RuntimeException("number of mol in SD do not match MD");
    }
    final int nMdCol = mdLineList.get(0).length;
    for (final String[] mdLine : mdLineList ){
      if (mdLine.length != nMdCol) {
        throw new RuntimeException("MD file does not have all lines with same number of col");
      }
    }
    System.out.println("number of col in MD = "+nMdCol);
    // Load up and validate activities 
    List<String[]> actLineList = new ArrayList<String[]>();
    actLineList = actReader.readAll();
    System.out.println("number of molecules in ACT = " + (actLineList.size()-1));
    if (molNames.size() != actLineList.size()-1) {
      throw new RuntimeException("number of mol in SD do not match ACT");
    }
    final int nActCol = actLineList.get(0).length;
    System.out.println("number of col in ACT = "+nActCol);
//    if (nActCol != ACT_COL_NAMES.length) {
//      throw new RuntimeException("Do not recognize ACT file header");
//    }
    
    // Make sure MD and ACT have the same MOL name col name
    int actMolNameCol=-1;
    boolean found=false;
    for (final String actColName : actLineList.get(0)) {
      actMolNameCol++;
      if (actColName.trim().equals(molNameColName)) {
        found = true;
        // ignore others with same name, first one wins
        break;
      }      
    }
    if (!found) {
      throw new RuntimeException("MD mol column name not found in ACT: "+molNameColName);
    }
    
    for (final String[] actLine : actLineList ){
      if (actLine.length != nActCol) {
        throw new RuntimeException("ACT file does not have all lines with same number of col");
      }
    }
        
    // Write out header info into pcsdata file
    final String[] pcsdataLine = new String[nMdCol+ nActCol];
    if (nMdCol>0) {
      // column with mol id
      pcsdataLine[0] = mdLineList.get(0)[0];      
    }
    
    for (int i = 1; i < nMdCol; i++) {
      pcsdataLine[i] = "D:"+mdLineList.get(0)[i];
    }
    for (int i = 0; i < nActCol; i++) {
      pcsdataLine[i + nMdCol] 
                  = "A:"+actLineList.get(0)[i];
    }
    pcsdataLine[nMdCol+nActCol-1] = "SMILES";
    pcsdataWriter.writeNext(pcsdataLine);
    
    // Now follow the order of the molecules in the sdfile, and assemble the
    // molecular descriptors and Activities.
    
    for (int iMol = 0; iMol<molNames.size(); iMol++) {
      final String molName = molNames.get(iMol);
      found=false;
      int mdLineNum=-1;
      for (final String[] mdLine : mdLineList) {
        mdLineNum++;
        if (mdLine[0].trim().equals(molName)) {
          found=true;
          break;
        }
      }
      if (!found) {
        throw new RuntimeException("could not find this molName in mdFile: "+molName);
      }

      found = false;
      int actLineNum=-1;
      for (final String[] actLine : actLineList) {
        actLineNum++;
        if (actLine[actMolNameCol].trim().equals(molName)) {
          found=true;
          break;
        }
      }
      if (!found) {
        throw new RuntimeException("could not find this molName in actFile: "+molName);
      }

      for (int i = 0; i < nMdCol; i++) {
        pcsdataLine[i] = mdLineList.get(mdLineNum)[i];
      }
      for (int i = 0; i < nActCol; i++) {
        pcsdataLine[i + nMdCol] = actLineList.get(actLineNum)[i];
      }
      pcsdataLine[nMdCol + nActCol-1] = molSmiless.get(iMol);
      //System.out.println("wrote " + iMol);
      pcsdataWriter.writeNext(pcsdataLine);
    }
    pcsdataWriter.close();
  }
  
  public static void main(final String[] args) throws IOException {
    final MdActMerge mdActMerge = new MdActMerge();
    mdActMerge.merge();
    System.out.println("finished");
  }
}
