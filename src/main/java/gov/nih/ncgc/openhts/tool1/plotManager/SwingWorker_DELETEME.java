// TODO: DELETE ME
package gov.nih.ncgc.openhts.tool1.plotManager;
import javax.swing.SwingUtilities;

/**
 * This is the 3rd version of SwingWorker_DELETEME (also known as
 * SwingWorker_DELETEME 3), an abstract class that you subclass to
 * perform GUI-related work in a dedicated thread.  For
 * instructions on using this class, see:
 *
 * http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
 *
 * Note that the API changed slightly in the 3rd version:
 * You must now invoke start() on the SwingWorker_DELETEME after
 * creating it.
 */
  abstract class SwingWorker_DELETEME {
    private Object value;  // see getValue(), setValue()
    //private final Thread thread;

    /**
     * Class to maintain reference to current worker thread
     * under separate synchronization control.
     */
    private static class ThreadVar {
        private Thread thread;
        ThreadVar(final Thread t) { thread = t; }
        synchronized Thread get() { return thread; }
        synchronized void clear() { thread = null; }
    }

    private ThreadVar threadVar;

    /**
     * Get the value produced by the worker thread, or null if it
     * hasn't been constructed yet.
     */
    protected synchronized Object getValue() {
        return value;
    }

    /**
     * Set the value produced by worker thread
     */
    private synchronized void setValue(final Object x) {
        value = x;
    }

    /**
     * Compute the value to be returned by the <code>get</code> method.
     */
    public abstract Object construct();

    /**
     * Called on the event dispatching thread (not on the worker thread)
     * after the <code>construct</code> method has returned.
     */
    public void finished() {
      // nop
    }

    /**
     * A new method that interrupts the worker thread.  Call this method
     * to force the worker to stop what it's doing.
     */
    public void interrupt() {
        final Thread t = threadVar.get();
        if (t != null) {
            t.interrupt();
        }
        threadVar.clear();
    }

    /**
     * Return the value created by the <code>construct</code> method.
     * Returns null if either the constructing thread or the current
     * thread was interrupted before a value was produced.
     *
     * @return the value created by the <code>construct</code> method
     */
    public Object get() {
        while (true) {
            final Thread t = threadVar.get();
            if (t == null) {
                return getValue();
            }
            try {
                t.join();
            }
            catch (final InterruptedException e) {
                Thread.currentThread().interrupt(); // propagate
                return null;
            }
        }
    }


    /**
     * Start a thread that will call the <code>construct</code> method
     * and then exit.
     */
    public SwingWorker_DELETEME() {
        final Runnable doFinished = new Runnable() {
           @Override
					public void run() { finished(); }
        };

        final Runnable doConstruct = new Runnable() {
            @Override
						public void run() {
                try {
                    setValue(construct());
                }
                catch(Throwable ex){  //in case some exceptions gets here.
                   new Exception("SwingWorker_DELETEME reports: \n"+ex).printStackTrace();
                }
                finally {
                    threadVar.clear();
                }

                SwingUtilities.invokeLater(doFinished);
            }
        };

        final Thread t = new Thread(doConstruct);
        threadVar = new ThreadVar(t);
    }

    /**
     * Start the worker thread.
     */
    public void start() {
        final Thread t = threadVar.get();
        if (t != null) {
            t.start();
        }
    }
    @Override
    public void finalize() {
      System.out.println("Finalized [" + getClass().getSimpleName() + ";"+hashCode()+"]");
    }

}
