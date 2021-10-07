package gov.nih.ncgc.openhts.tool1.descriptorManager;

public class DescriptorResult {
    
  private final StringBuffer stderr;
  private final StringBuffer stdout;

  public DescriptorResult(final StringBuffer stdout, final StringBuffer stderr) {
    this.stdout = stdout;
    this.stderr = stderr;
  }

  public StringBuffer getStderr() {
    return stderr;
  }

  public StringBuffer getStdout() {
    return stdout;
  }
}
