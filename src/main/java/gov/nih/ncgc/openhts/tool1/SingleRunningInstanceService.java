package gov.nih.ncgc.openhts.tool1;

import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


/** Purpose is to ...
 * @author talafousj
 *
 */
public class SingleRunningInstanceService extends Thread {
    
    
    public class SingleRunningInstanceServiceSocketListener extends Thread {
        private ServerSocket serverSocket;
        
        public SingleRunningInstanceServiceSocketListener(final ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }
        @Override
        public void run() {
            try {
                Socket sock;
                do {
                    sock = null;
                    sock = serverSocket.accept();
                    if ( null != sock ) {
                        //eventDispatcher.broadcastEvent(new ApplicationReactivateEvent(this));
                        sock.close();
                    }
                } while (null != sock);
            } catch (final Exception e) {
                logger.log(Level.INFO,"socket error",e);
            }
        }
    }
    
    private SingleRunningInstanceServiceSocketListener socketListener;
    private ServerSocket serverSocket;
    private int port;
    private static final Logger logger = Logger.getLogger(SingleRunningInstanceService.class.getPackage().getName());
    
    public SingleRunningInstanceService(final int port) {
        this.port = port;
    }
    
    /**
     * returns true if server was launched successfully...
     */
    public boolean startOrNotifyServer() throws Exception {
        // create mutex socket to prevent user from running application twice...
        boolean socketCreationSuccess = true;
        try {
            serverSocket = new ServerSocket(port, 50, InetAddress.getLocalHost());
        } catch (final BindException e) {
            socketCreationSuccess = false;
        }
        
        if ( socketCreationSuccess ) {
            socketListener = new SingleRunningInstanceServiceSocketListener(serverSocket);
            socketListener.start();
        } else {
            // signal the event should fire...
            final Socket sock = new Socket(InetAddress.getLocalHost(), port);
            sock.close();
        }
        
        return socketCreationSuccess;
    }

    public void close() {
        try {
            serverSocket.close();
        } catch ( final Exception e ) {
            logger.log(Level.SEVERE,"socket close failed");
        }        
        serverSocket = null;
    }
}
