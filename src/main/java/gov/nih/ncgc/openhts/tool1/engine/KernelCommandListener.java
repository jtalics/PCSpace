package gov.nih.ncgc.openhts.tool1.engine;

/** Purpose is to ...
 * @author talafousj
 *
 */
public interface KernelCommandListener {
    void kernelCommandEnqueued(KernelCommand command);
    void kernelCommandDequeued(KernelCommand command);
    void kernelCommandExecutionBegin(KernelCommand command);
    void kernelCommandExecutionComplete(KernelResult result);
}
