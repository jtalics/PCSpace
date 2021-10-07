package gov.nih.ncgc.openhts.tool1.descriptorManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import gov.nih.ncgc.openhts.tool1.FileFilters;
import gov.nih.ncgc.openhts.tool1.Main;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.engine.KernelResult;
import gov.nih.ncgc.openhts.tool1.pointsetManager.ColumnHead;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class Descriptor extends ColumnHead implements DescriptorManagerEntity, Comparable<Descriptor> {
  public enum Kind {
    Calculated("D"), Supplied("A"), CalculatedOrSupplied("E");
    String abbrev;
    
    Kind(final String abbrev) {
      // unused, but keep for future use
      this.abbrev = abbrev;
    }
    
    public String getAbbrev() {
      return abbrev;
    }
  }

   private Kind kind;
   File xmlFile;
  String runCommandXmlFileName;
  // name is the unique identifier
   public String runCommand;
  transient List<KernelResult<DescriptorResult>> results = new ArrayList<KernelResult<DescriptorResult>>();

  public Descriptor(Session session) {
    super(session);
  }
  
  public void initialize(final String name, final Kind kind, final ColumnHead.Kind columnHeadKind) {
    setName(name);
    this.kind = kind;
    super.kind=columnHeadKind;
    runCommandXmlFileName="";
    runCommand="";
  }

  @Override
  public String toString() {
    String s;
    if (xmlFile==null) {
      s="no xmlfile";
    }
    else {
      s = xmlFile.getPath();    
    }
    return "[Descriptor: " + getName() + "  ; "+kind+"]";
  }

  public boolean isValid() {
    // TODO Auto-generated method stub
    // session.error("bad descriptor");
    return true;
  }

  public void calculate(final File molFile) {
    if (Main.engine == null || !Main.engine.isRunning()) {return;}
    if (runCommand == null || runCommand.trim().isEmpty()) {
      throw new RuntimeException("null run command in descriptor = "+this);
    }
    final String descFileName = FileFilters.removeExtension(molFile
        .getPath())+"."+getName() + "."+FileFilters.descvalExt;
    final File descFile = new File(descFileName);
    final KernelResult<DescriptorResult> result = Main.engine
        .getResult(new DescriptorCommand(this, molFile, descFile));
    results.add(result);
    // TODO remove
    System.out.println(result.getWhen() + " STDOUT: "
        + result.getResult().getStdout());
    System.out.println(result.getWhen() + " STDERR: "
        + result.getResult().getStderr());
  }

  // @Implements Comparable
  @Override
	public int compareTo(final Descriptor descriptor) {
    String name1 = getName();
    String name2 = ((ColumnHead) descriptor).getName();
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

  public Kind getDescriptorKind() {
    return kind;
  }

  public String getToolTipText() {
    final StringBuffer sb = new StringBuffer("<html><boundingBox>Descriptor: " 
        + getName() + "</boundingBox><br>");
    sb.append("Kind: " + kind);
    sb.append("</html>");
    return sb.toString();
  }

  public static boolean legalKind(String kindString) {
    for (Kind kind : Kind.values()) {
      if (kind.name().equals(kindString)) {
        return true;
      }
    }
    return false;
  }

  public void setKind(Kind kind) {
    this.kind = kind;
  }

}
