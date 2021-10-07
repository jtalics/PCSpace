package gov.nih.ncgc.openhts.tool1.engine;

/** Purpose is to ...
 * @author talafousj
 *
 */
public interface EngineListener {
    public void engineStateChanged(EngineState engineState);
    public void engineExceptionThrown( KernelException x, KernelResult result );
}
