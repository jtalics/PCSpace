/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.descriptorManager;

import java.io.File;
import gov.nih.ncgc.openhts.tool1.engine.ExecPump;
import gov.nih.ncgc.openhts.tool1.engine.KernelCommand;
import gov.nih.ncgc.openhts.tool1.engine.KernelException;
import gov.nih.ncgc.openhts.tool1.resourceManager.ResourceProvider;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class DescriptorCommand extends KernelCommand<DescriptorResult> {
  static private int theInstanceId = 0;
  private static final long serialVersionUID = 1L;
  private final int instanceId = theInstanceId++;
  private Descriptor descriptor;
  private File inputFile;
  private File outputFile;

  public DescriptorCommand(final Descriptor descriptor, final File inputFile,
      final File outputFile) {
    super();
    this.descriptor = descriptor;
    this.inputFile = inputFile;
    this.outputFile = outputFile;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("DescriptorCommand[").append(instanceId)
        .append(",");
    sb.append("descriptor=").append(descriptor).append(",");
    sb.append("]");
    return sb.toString();
  }

  @Override
  protected void doExecute(final ResourceProvider provider) throws KernelException {
    fireExecutionBegin(this);
    final StringBuffer stderr=new StringBuffer(),stdout=new StringBuffer();
    try {
      final String osName = System.getProperty("os.name");
      final String[] cmd = new String[7];
      if (osName.startsWith("Windows")) {
        cmd[0] = "cmd.exe";
        cmd[1] = "/C";
        cmd[2] = descriptor.runCommand;
        cmd[3] = descriptor.runCommandXmlFileName;
        cmd[4] = inputFile.getPath();
        cmd[5] = outputFile.getPath();
        cmd[6] = descriptor.getName();
      }
      else {
        throw new RuntimeException("unknown OS");
      }
      final Runtime rt = Runtime.getRuntime();
      System.out.println("DESCRIPTOR: Execing " + cmd[0] + " " + cmd[1] + " " + cmd[2]+ " " + cmd[3]+ " " + cmd[4]+ " " + cmd[5] + " " + cmd[6]);
      final Process proc = rt.exec(cmd);
      // any error message?
      final ExecPump errorPump = new ExecPump(proc.getErrorStream(), stderr);
      // any output?
      final ExecPump outputPump = new ExecPump(proc.getInputStream(), stdout);
      // kick them off
      errorPump.start();
      outputPump.start();
      // any error???
      final int exitVal = proc.waitFor();
      System.out.println("DESCRIPTOR ExitValue: " + exitVal);
    }
    catch (final Throwable t) {
      t.printStackTrace();
    }
    result = new DescriptorResult(stdout,stderr);
  }
}
