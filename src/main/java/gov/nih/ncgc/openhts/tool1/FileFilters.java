/**
 * 
 */
package gov.nih.ncgc.openhts.tool1;

import java.io.File;
import java.io.FilenameFilter;
import javax.swing.filechooser.FileFilter;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class FileFilters {
  // TODO: where does next line belong?
  public final static String molIdColName = "CID";
  public static final java.io.FileFilter directoryFileFilter = new java.io.FileFilter() {
    // @Implements
    @Override
		public boolean accept(File file) {
      if (file.isDirectory()) {
        return true;
      }
      return false;
    }
  };
  public static final FileFilter homeDirFileFilter = new FileFilter() {
    @Override
    public boolean accept(File f) {
      if (f.getName().endsWith(Main.ROOT_ENV)) {
        return true;
      }
      return false;
    }

    @Override
    public String getDescription() {
      return Main.ROOT_ENV;
    }
  };
  //
  public static final String dataSourceTxtExt = "datasource.txt";
  public static final FileFilter dataSourceFileFilter = new FileFilter(
  /* , dataSourceExt */) {
    @Override
    public boolean accept(File file) {
      if (file.isDirectory() || file.getName().endsWith("." + dataSourceTxtExt)) {
        return true;
      }
      return false;
    }

    @Override
    public String getDescription() {
      // TODO Auto-generated method stub
      return "dataSource files (*." + dataSourceTxtExt + ")";
    }
  };
  public static final FilenameFilter dataSourceFilenameFilter = new FilenameFilter() {
    @Override
		public boolean accept(File dir, String name) {
      if (name.endsWith("." + dataSourceTxtExt)) {
        return true;
      }
      return false;
    }
  };
  //
  public static final String dataSourceXmlExt = "datasource.xml";
  public static final FileFilter dataSourceXmlFileFilter = new FileFilter(
  /* , dataSourceExt */) {
    @Override
    public boolean accept(File file) {
      if (file.isDirectory() || file.getName().endsWith("." + dataSourceXmlExt)) {
        return true;
      }
      return false;
    }

    @Override
    public String getDescription() {
      // TODO Auto-generated method stub
      return "dataSource XML files (*." + dataSourceXmlExt + ")";
    }
  };
  public static final FilenameFilter dataSourceXmlFilenameFilter = new FilenameFilter() {
    @Override
		public boolean accept(File dir, String name) {
      if (name.endsWith("." + dataSourceXmlExt)) {
        return true;
      }
      return false;
    }
  };
//  //
//  public static final String preassembledTxtExt = "datasource.txt";
//  public static final FileFilter preassembledFileFilter = new FileFilter(
//  /* , dataSourceExt */) {
//    @Override
//    public boolean accept(File file) {
//      if (file.isDirectory() || file.getName().endsWith("." + preassembledCsvExt)) {
//        return true;
//      }
//      return false;
//    }
//
//    @Override
//    public String getDescription() {
//      // TODO Auto-generated method stub
//      return "preassembled files (*." + preassembledCsvExt + ")";
//    }
//  };
//  public static final FilenameFilter preassembledFilenameFilter = new FilenameFilter() {
//    public boolean accept(File dir, String name) {
//      if (name.endsWith("." + preassembledCsvExt)) {
//        return true;
//      }
//      return false;
//    }
//  };
  //
  public static final String smilesFileExt = "smi";
  public static final String sdFileExt = "sdf";
  public static final FileFilter moleculeFileFilter = new FileFilter(
  /* , smilesFileExt, sdFileExt */) {
    @Override
    public boolean accept(File file) {
      if (file.isDirectory() || file.getName().endsWith("." + smilesFileExt)) {
        return true;
      }
      return false;
    }

    @Override
    public String getDescription() {
      // TODO Auto-generated method stub
      return "molecular structure files (*." + smilesFileExt + ", *."
          + sdFileExt + ")";
    }
  };
  public static final FilenameFilter moleculeFilenameFilter = new FilenameFilter() {
    @Override
		public boolean accept(File dir, String name) {
      if (name.endsWith("." + sdFileExt) || name.endsWith("." + smilesFileExt)) {
        return true;
      }
      return false;
    }
  };
  //
  public static final String activitiesExt = "activities";
  public static final FileFilter activityFileFilter = new FileFilter(
  /* , activitiesExt */) {
    @Override
    public boolean accept(File file) {
      if (file.isDirectory() || file.getName().endsWith("." + activitiesExt)) {
        return true;
      }
      return false;
    }

    @Override
    public String getDescription() {
      // TODO Auto-generated method stub
      return "activity files (*." + activitiesExt + ")";
    }
  };
  public static final FilenameFilter activityFilenameFilter = new FilenameFilter() {
    @Override
		public boolean accept(File dir, String name) {
      if (name.endsWith(activitiesExt)) {
        return true;
      }
      return false;
    }
  };
  //
  public static final String descvalExt = "descval";
  public static final FileFilter descvalFileFilter = new FileFilter(
  /* , descvalExt */) {
    @Override
    public boolean accept(File file) {
      if (file.isDirectory() || file.getName().endsWith("." + descvalExt)) {
        return true;
      }
      return false;
    }

    @Override
    public String getDescription() {
      // TODO Auto-generated method stub
      return "descriptor files (*." + descvalExt + ")";
    }
  };
  public static final FilenameFilter descvalFilenameFilter = new FilenameFilter() {
    @Override
		public boolean accept(File dir, String name) {
      if (name.endsWith(descvalExt)) {
        return true;
      }
      return false;
    }
  };
  //
  public static final String descriptorXmlExt = "descriptor.xml";
  public static final FileFilter descriptorXmlFileFilter = new FileFilter(
  /* , descriptorXmlExt */) {
    @Override
    public boolean accept(File file) {
      if (file.isDirectory() || file.getName().endsWith("." + descriptorXmlExt)) {
        return true;
      }
      return false;
    }

    @Override
    public String getDescription() {
      // TODO Auto-generated method stub
      return "descriptor files (*." + descriptorXmlExt + ")";
    }
  };
  public static final FilenameFilter descriptorXmlFilenameFilter = new FilenameFilter() {
    @Override
		public boolean accept(File dir, String name) {
      if (name.endsWith("." + descriptorXmlExt)) {
        return true;
      }
      return false;
    }
  };
  //
  public static final String basisXmlExt = "basis.xml";
  public static final FileFilter basisXmlFileFilter = new FileFilter() {
    @Override
    public boolean accept(File file) {
      if (file.isDirectory() || file.getName().endsWith("." + basisXmlExt)) {
        return true;
      }
      return false;
    }

    @Override
    public String getDescription() {
      return "basis files (*." + basisXmlExt + ")";
    }
  };
  public static final FilenameFilter basisXmlFilenameFilter = new FilenameFilter() {
    @Override
		public boolean accept(File dir, String name) {
      if (name.endsWith("." + basisXmlExt)) {
        return true;
      }
      return false;
    }
  };
  //
  public static final String pointsetTxtExt = "pointset.txt";
  public static final FileFilter pointsetTxtFileFilter = new FileFilter() {
    @Override
    public boolean accept(File file) {
      if (file.isDirectory() || file.getName().endsWith("." + pointsetTxtExt)) {
        return true;
      }
      return false;
    }

    @Override
    public String getDescription() {
      return "Pointset Comma separated values (*." + pointsetTxtExt + ")";
    }
  };
  public static final FilenameFilter pointsetTxtFilenameFilter = new FilenameFilter() {
    @Override
		public boolean accept(File dir, String name) {
      if (name.endsWith("." + pointsetTxtExt)) {
        return true;
      }
      return false;
    }
  };
  //
  public static final String txtExt = "txt";
  public static final FileFilter txtFileFilter = new FileFilter() {
    @Override
    public boolean accept(File file) {
      if (file.isDirectory() || file.getName().endsWith("." + txtExt)) {
        return true;
      }
      return false;
    }

    @Override
    public String getDescription() {
      // TODO Auto-generated method stub
      return "Tab separated values (*." + txtExt + ")";
    }
  };
  public static final FilenameFilter csvFilenameFilter = new FilenameFilter() {
    @Override
		public boolean accept(File dir, String name) {
      if (name.endsWith("." + txtExt)) {
        return true;
      }
      return false;
    }
  };
  //
  public static final String jpgExt = "jpg";
  public static final FileFilter jpgFileFilter = new FileFilter(
  /* , jpgExt */) {
    @Override
    public boolean accept(File file) {
      if (file.isDirectory() || file.getName().endsWith("." + jpgExt)) {
        return true;
      }
      return false;
    }

    @Override
    public String getDescription() {
      // TODO Auto-generated method stub
      return "JPG image file (*." + jpgExt + ")";
    }
  };
  public static final FilenameFilter jpgFilenameFilter = new FilenameFilter() {
    @Override
		public boolean accept(File dir, String name) {
      if (name.endsWith("." + jpgExt)) {
        return true;
      }
      return false;
    }
  };

  public static String removeExtension(final String filename) {
    return filename.substring(0, filename.lastIndexOf("."));
  }

  public static String entityNameFromFile(File file, String fileExt) {
    String fileName = file.getName();
    return fileName.substring(0, fileName.lastIndexOf("." + fileExt));
  }
}
