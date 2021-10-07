package gov.nih.ncgc.openhts.tool1.engine;


/**
 * This class of exception indicates Kernel cannot continue to run... must be 
 * re-initialized.
 * @author talafousj
 *
 */
public class FatalKernelException extends KernelException {

    private static final long serialVersionUID = 2006122601040001001L;

    public FatalKernelException(final int engineErrorCode) {
        super(engineErrorCode);
    }

    public FatalKernelException(final String message, final int engineErrorCode) {
        super(message, engineErrorCode);
    }
    
    public FatalKernelException( final KernelException e ) {
        super(e.getMessage(),e.getEngineCode());
    }
}
