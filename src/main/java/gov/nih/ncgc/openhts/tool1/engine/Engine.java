package gov.nih.ncgc.openhts.tool1.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import gov.nih.ncgc.openhts.tool1.resourceManager.Resource;
import gov.nih.ncgc.openhts.tool1.resourceManager.ResourceDescriptor;
import gov.nih.ncgc.openhts.tool1.resourceManager.ResourceInfo;
import gov.nih.ncgc.openhts.tool1.resourceManager.ResourceProvider;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class Engine implements ResourceProvider, EngineListener {

    private static final Logger logger = Logger.getLogger(Engine.class.getName());
    private static final int DEFAULT_IO_WORKER_PRIORITY = Thread.MIN_PRIORITY + 1;
    private static final int DEFAULT_CPU_WORKER_PRIORITY = Thread.MIN_PRIORITY;

    private final String name;
    private final int ioThreadPriority;
    private final int cpuThreadPriority;

    private long cacheSize;
    private String license;

    private final AtomicBoolean reinitializeRequest;
    private boolean reinitializeShutdownRequest;
    private CyclicBarrier reinitializeCyclicBarrier;
    private final BlockingDeque<KernelCommand> ioBoundCommandQueue; // has one worker
    private final BlockingDeque<KernelCommand> cpuBoundCommandQueue; // has one worker per CPU
    private EngineThread ioBoundWorkerThread;
    private EngineThread cpuBoundWorkerThread[];
    private int totalThreads;
    private final Kernel kernel;
    private final List<EngineListener> engineListeners = Collections.synchronizedList(new ArrayList<EngineListener>());

    private Object engineStateMonitor;
    private int stoppedThreadCount;
    private int pausedThreadCount;
    private int idleThreadCount;
    private int busyThreadCount;
    private EngineState engineState;

    private static Engine theEngine;

    /**
     * 
     * @param name
     */
    public Engine(final String name, final String license, final long cacheSize) {
        this(name, license, cacheSize, DEFAULT_IO_WORKER_PRIORITY, DEFAULT_CPU_WORKER_PRIORITY);
    }

    /**
     * 
     * @param name
     * @param priority
     * @param commandQueueSize
     * @param resultQueueSize
     */
    public Engine(final String name, final String license, final long cacheSize, final int ioThreadPriority, final int cpuThreadPriority) {
        this.name = name;
        this.license = license;
        this.cacheSize = cacheSize;
        this.ioThreadPriority = ioThreadPriority;
        this.cpuThreadPriority = cpuThreadPriority;

        this.kernel = new Kernel();
        this.reinitializeRequest = new AtomicBoolean(false);

        cpuBoundCommandQueue = new LinkedBlockingDeque<KernelCommand>();
        ioBoundCommandQueue = new LinkedBlockingDeque<KernelCommand>();
        cpuBoundWorkerThread = new EngineThread[kernel.getCPUCount()];
        totalThreads = cpuBoundWorkerThread.length + 1;

        reinitializeCyclicBarrier = new CyclicBarrier(totalThreads,new ReinitializeRunnable());
        reinitializeShutdownRequest = false;
        
        engineState = EngineState.STOPPED;
        engineStateMonitor = new Object();
        stoppedThreadCount = cpuBoundWorkerThread.length + 1;
        pausedThreadCount = 0;
        idleThreadCount = 0;
        busyThreadCount = 0;

        logger.config("License set to " + license);
        logger.config("Kernel cache size set to " + cacheSize);

        theEngine = this;
    }

    public Engine getEngine() {
        return theEngine;
    }

    public String getName() {
        return name;
    }

    public void setLicense(final String license) {
        this.license = license;
    }

    public void setCacheSize(final long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public EngineState getEngineState() {
        return engineState;
    }

    public void start() {
        if (!isRunning()) {
            if (initialize() == false) {
                return;
            }
            addListener(this);
            reinitializeShutdownRequest = false;
            reinitializeCyclicBarrier.reset();
            ioBoundWorkerThread = new EngineThread(ioBoundCommandQueue);
            ioBoundWorkerThread.setPriority(ioThreadPriority);
            ioBoundWorkerThread.setName(name + "-ioBoundWorkerThread");
            ioBoundWorkerThread.start();

            for (int i = 0; i < cpuBoundWorkerThread.length; ++i) {
                cpuBoundWorkerThread[i] = new EngineThread(cpuBoundCommandQueue);
                cpuBoundWorkerThread[i].setPriority(cpuThreadPriority);
                cpuBoundWorkerThread[i].setName(name + "-cpuBoundWorkerThread-" + i);
                cpuBoundWorkerThread[i].start();
            }
            logger.info("Engine started [one ioBoundWorker and " + cpuBoundWorkerThread.length + " cpuBoundWorker(s) started]");
        } else {
            logger.warning("start() was called, but engine thread is already running.");
        }
    }

    protected boolean isWorkerThread() {
        // has this been called from a worker thread?
        boolean isWorkerThread = false;
        if (Thread.currentThread().equals(ioBoundWorkerThread)) {
            isWorkerThread = true;
        } else {
            for (final EngineThread element : cpuBoundWorkerThread) {
                if (Thread.currentThread().equals(element)) {
                    isWorkerThread = true;
                    break;
                }
            }
        }
        return isWorkerThread;
    }

    public boolean requestReinitialize() {
        // start the thread again...
        if (!isRunning()) {
            start();
            return true;
        } else {
            return reinitializeRequest.compareAndSet(false, true);
        }
    }

    /**
     * 
     * @return
     */
    public boolean isRunning() {
        return getEngineState() != EngineState.STOPPED;
    }

    private boolean initialize() {
        try {
            kernel.initialize(cacheSize, license);
        } catch (final FatalKernelException x) {
            fireEngineExceptionThrown(x, null);
            return false;
        }
        logger.info("Kernel initialized, cache=" + cacheSize);
        return true;
    }

    private boolean deinitialize() {
        // deinit... retry 5 times, one second apart...
        KernelException kex;
        int timeout = 5; // seconds
        do {
            kex = null;
            try {
                kernel.deinitialize();
            } catch (final KernelException x) {
                timeout--;
                kex = x;
                if (timeout >= 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (final InterruptedException ix) {
                      // nop

                    }
                }
            }
        } while (kex != null && timeout >= 0);
        if (kex != null) {
            fireEngineExceptionThrown(kex, null);
            return false;
        }
        logger.info("Kernel deinitialized");
        return true;
    }

    public void shutdown() {
        if (isRunning()) {
            removeListener(this);

            // shutdown routine is encapsulated in a thread
            final Thread shutdownEngineThread = new Thread("EngineShutdownThread") {

                @Override
                public void run() {
                    reinitializeShutdownRequest = false;
                    reinitializeCyclicBarrier.reset();
                    ioBoundWorkerThread.interrupt();
                    while (ioBoundWorkerThread.getState() != Thread.State.TERMINATED) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException x) {
                          // nop

                        }
                    }
                    for (EngineThread element : cpuBoundWorkerThread) {
                        element.interrupt();
                        while (element.getState() != Thread.State.TERMINATED) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException x) {
                              // nop

                            }
                        }
                    }
                    logger.info("Engine shutdown completed [all threads exited]");
                    deinitialize();
                }
            };

            // if called by a worker thread, start a separate thread
            // (otherwise, worker will not exit)
            if (isWorkerThread()) {
                // shutdown from a separate thread
                shutdownEngineThread.start();
            } else {
                // shutdown synchronously in the current thread...
                shutdownEngineThread.run();
            }

        } else {
            logger.warning("shutdown() was called, but engine thread is not running");
        }
    }

    // Manage EngineListeners...

    public void addListener(final EngineListener listener) {
        engineListeners.add(listener);
        listener.engineStateChanged(engineState);
    }

    public void removeListener(final EngineListener listener) {
        engineListeners.remove(listener);
    }

    private void updateEngineStateFromThreadState(final EngineThread engineThread, final EngineState priorThreadState, final EngineState newThreadState) {
        EngineState newEngineState = null;
        if (priorThreadState == newThreadState) {
            throw new IllegalArgumentException("priorThreadState(" + priorThreadState + ") == newThreadState (" + newThreadState + ")");
        }
        synchronized (engineStateMonitor) {
            if (EngineState.STOPPED.equals(priorThreadState)) {
                stoppedThreadCount--;
            } else if (EngineState.IDLE.equals(priorThreadState)) {
                idleThreadCount--;
            } else if (EngineState.PAUSED.equals(priorThreadState)) {
                pausedThreadCount--;
            } else if (EngineState.BUSY.equals(priorThreadState)) {
                busyThreadCount--;
            } else {
                throw new IllegalStateException("unknown priorThreadState");
            }
            if (EngineState.STOPPED.equals(newThreadState)) {
                stoppedThreadCount++;
            } else if (EngineState.IDLE.equals(newThreadState)) {
                idleThreadCount++;
            } else if (EngineState.PAUSED.equals(newThreadState)) {
                pausedThreadCount++;
            } else if (EngineState.BUSY.equals(newThreadState)) {
                busyThreadCount++;
            } else {
                throw new IllegalStateException("unknown newThreadState");
            }

            // TODO sanity check... you could remove this once things are sane:
            // logger.info("\nstoppedThreadCount=" + stoppedThreadCount + ",idleThreadCount=" + idleThreadCount + ",pausedThreadCount=" + pausedThreadCount + ",busyThreadCount=" + busyThreadCount + "\n" + priorThreadState.getName() + " => " + newThreadState.getName() + " - " + engineThread.getName() );
            if (stoppedThreadCount < 0 || stoppedThreadCount > totalThreads) {
                throw new IllegalStateException("stoppedThreadCount is invalid (" + stoppedThreadCount + ")");
            } else if (idleThreadCount < 0 || idleThreadCount > totalThreads) {
                throw new IllegalStateException("idleThreadCount is invalid (" + idleThreadCount + ")");
            } else if (pausedThreadCount < 0 || pausedThreadCount > totalThreads) {
                throw new IllegalStateException("pausedThreadCount is invalid (" + pausedThreadCount + ")");
            } else if (busyThreadCount < 0 || busyThreadCount > totalThreads) {
                throw new IllegalStateException("busyThreadCount is invalid (" + busyThreadCount + ")");
            } else if (stoppedThreadCount + idleThreadCount + pausedThreadCount + busyThreadCount != totalThreads) {
                throw new IllegalStateException("(stoppedThreadCount + idleThreadCount + pausedThreadCount + busyThreadCount) should be equal to " + totalThreads + " but instead is "
                        + (stoppedThreadCount + idleThreadCount + pausedThreadCount + busyThreadCount));
            }

            if (stoppedThreadCount == totalThreads) {
                newEngineState = EngineState.STOPPED;
            } else if (pausedThreadCount == totalThreads) {
                newEngineState = EngineState.PAUSED;
            } else if (busyThreadCount > 0) {
                newEngineState = EngineState.BUSY;
            } else {
                newEngineState = EngineState.IDLE;
            }
        }
        if (!engineState.equals(newEngineState)) {
            engineState = newEngineState;
            fireEngineStateChanged();
        }
    }

    private void fireEngineStateChanged() {
        final List<EngineListener> l = new ArrayList<EngineListener>();
        synchronized (engineListeners) {
            l.addAll(engineListeners);
        }
        for (final EngineListener listener : l) {
            listener.engineStateChanged(engineState);
        }
    }

    private void fireEngineExceptionThrown(final KernelException x, final KernelResult result) {
        final List<EngineListener> l = new ArrayList<EngineListener>();
        synchronized (engineListeners) {
            l.addAll(engineListeners);
        }
        for (final EngineListener listener : l) {
            listener.engineExceptionThrown(x, result);
        }
    }

    // This class is an EngineListener to catch engine exceptions AND handle restart requests once engine is stopped.

    @Override
		public void engineStateChanged(final EngineState engineState) {
      // nop
    }

    @Override
		public void engineExceptionThrown(final KernelException x, final KernelResult result) {
        if (x instanceof FatalKernelException) {
            if (null == result) {
                logger.log(Level.SEVERE, x.toString(), x);
            } else {
                logger.log(Level.SEVERE, "Error while executing:\n" + result.getCommand().toString(), result.getException());
            }
            shutdown();
        } else {
            if (null == result) {
              // nop

            } else {
                logger.log(Level.WARNING, "Error while executing:\n" + result.getCommand().toString(), result.getException());
            }
        }
    }

    // Execution control...

    public void enqueue(final KernelCommand command) {
        if (command.isCpuBound()) {
            cpuBoundCommandQueue.offer(command);
        } else {
            ioBoundCommandQueue.offer(command);
        }
        command.fireEnqueued(command);
    }

    public void enqueueFirst(final KernelCommand command) {
        if (command.isCpuBound()) {
            cpuBoundCommandQueue.offerFirst(command);
        } else {
            ioBoundCommandQueue.offerFirst(command);
        }
        command.fireEnqueued(command);
    }

    public KernelResult getResult(final KernelCommand command) {
        return getResult(command, false);
    }

    public KernelResult getResultHighPriority(final KernelCommand command) {
        return getResult(command, true);
    }

    /**
     * Synchronously processes the specified command, and returns the corresponding result.
     * 
     * @param command
     */
    public KernelResult getResult(final KernelCommand command, final boolean highPriority) {
        final KernelResult[] resultHolder = new KernelResult[1];

        command.addListener(new KernelCommandListener() {

            @Override
						public void kernelCommandEnqueued(final KernelCommand command) {
              // nop

            }

            @Override
						public void kernelCommandDequeued(final KernelCommand command) {
              // nop

            }

            @Override
						public void kernelCommandExecutionBegin(final KernelCommand command) {
              // nop

            }

            @Override
						public void kernelCommandExecutionComplete(final KernelResult result) {
                synchronized (resultHolder) {
                    resultHolder[0] = result;
                    resultHolder.notify();
                }
            }
        });

        if (highPriority) {
            enqueueFirst(command);
        } else {
            enqueue(command);
        }

        synchronized (resultHolder) {
            while (resultHolder[0] == null) {
                try {
                    resultHolder.wait();
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return resultHolder[0];
    }

    @Override
		public Resource acquireResource(final ResourceDescriptor descriptor) throws KernelException {
        return kernel.acquireResource(descriptor);
    }

    @Override
		public ResourceInfo getResourceInfo(final ResourceDescriptor descriptor) throws KernelException {
        return kernel.getResourceInfo(descriptor);
    }

    @Override
		public void registerResource(final ResourceDescriptor descriptor, final Resource resource) {
        kernel.registerResource(descriptor, resource);
    }

    @Override
		public void releaseResource(final ResourceDescriptor descriptor, final boolean immediateUnload) throws KernelException {
        kernel.releaseResource(descriptor, immediateUnload);
    }

    protected class EngineThread extends Thread {

        private EngineState threadState;
        private final BlockingDeque<KernelCommand> commandQueue;

        public EngineThread(final BlockingDeque<KernelCommand> commandQueue) {
            this.threadState = EngineState.STOPPED;
            this.commandQueue = commandQueue;
        }

        @Override
        public void run() {

            try {
                logger.info("Starting Engine worker thread " + getName());
                processCommands();
            } catch (final InterruptedException x) {
                // ignore this exception... it likely means the worker threads are exiting...
            } catch (final BrokenBarrierException x) {
                // ignore this exception... it likely means the worker threads are exiting... 
            }
            logger.info("Exiting Engine worker thread " + getName());
        }

        private void setThreadState(final EngineState newState) {
            if (!threadState.equals(newState)) {
                final EngineState oldState = threadState;
                threadState = newState;
                updateEngineStateFromThreadState(this, oldState, newState);
            }
        }

        public EngineState getThreadState() {
            return threadState;
        }

        private void processCommands() throws InterruptedException, BrokenBarrierException {

            try {
                /* Indicate that the engine is processing commands. */
                setThreadState(EngineState.IDLE);

                while (true) {
                    if (reinitializeRequest.get()) {
                        setThreadState(EngineState.PAUSED);
                        reinitializeCyclicBarrier.await();
                        if ( reinitializeShutdownRequest ) {
                            throw new BrokenBarrierException();
                        }
                    }
                    if (EngineState.PAUSED.equals(getEngineState())) {
                        setThreadState(EngineState.IDLE);
                    }

                    /* Take the next command off the command queue. */
                    final KernelCommand command = commandQueue.poll(1000, TimeUnit.MILLISECONDS);
                    if (null != command) {
                        setThreadState(EngineState.BUSY);
                        logger.finer("Executing " + command.toString());
                        command.fireDequeued(command);

                        /*
                         * The kernel executes the command and always returns a result in the prescence of KernelException (runtime exceptions will still get through).
                         */
                        final KernelResult result = kernel.execute(command);

                        if (result.isError()) {
                            if (result.getException() instanceof FatalKernelException) {
                                fireEngineExceptionThrown(result.getException(), result);
                            } else if (result.getException() instanceof KernelException) {
                                fireEngineExceptionThrown(new KernelException(result.getException().getMessage() + "\nWhile executing:\n" + result.getCommand().toString(), result.getException()), result);
                            }
                        }
                    } else {
                        setThreadState(EngineState.IDLE);
                    }
                }

            } finally {
                /* Indicate that the thread as ceased processing commands. */
                setThreadState(EngineState.STOPPED);
            }
        }
    }

    protected class ReinitializeRunnable implements Runnable {
        @Override
				public void run() {
            logger.info("Engine reinitialize request acknowledged");
            reinitializeRequest.set(false);
    
            if (deinitialize() == false) {
                reinitializeShutdownRequest = true;
                return;
            }
            if ( initialize() == false ) {
                reinitializeShutdownRequest = true;
            }
        }
    }
}
