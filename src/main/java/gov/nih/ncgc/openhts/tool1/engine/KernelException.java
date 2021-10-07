package gov.nih.ncgc.openhts.tool1.engine;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class KernelException extends Exception {


    /**
   * 
   */
  private static final long serialVersionUID = 1L;
    private int engineCode;

    public KernelException(final int engineCode) {
//        super(engineCode);
//        
        this.engineCode = engineCode;
    }

    public KernelException(final String message, final int engineCode) {
        super(message);
        
//        this.engineCode = engineCode;
    }

    public KernelException( final String descriptiveMessage, final KernelException innerException ) {
        super(descriptiveMessage,innerException);
//        this.engineCode = innerException.engineCode;
    }
    
    public KernelException(final Throwable cause, final int engineCode) {
        //super(engineCode,cause);
        
//        this.engineCode = engineCode;
    }

    public KernelException(final String message, final Throwable cause, final int engineCode) {
        super(message, cause);
        
//        this.engineCode = engineCode;
    }

    public int getEngineCode() {
        return engineCode;
    }
}
