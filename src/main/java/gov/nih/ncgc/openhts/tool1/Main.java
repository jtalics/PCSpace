package gov.nih.ncgc.openhts.tool1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.JApplet;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.DataSourceManager;
import gov.nih.ncgc.openhts.tool1.descriptorManager.DescriptorManager;
import gov.nih.ncgc.openhts.tool1.engine.Engine;
import gov.nih.ncgc.openhts.tool1.engine.EngineListener;
import gov.nih.ncgc.openhts.tool1.engine.EngineState;
import gov.nih.ncgc.openhts.tool1.engine.KernelException;
import gov.nih.ncgc.openhts.tool1.engine.KernelResult;
import gov.nih.ncgc.openhts.tool1.persistence.Db4oPersistenceService;
import gov.nih.ncgc.openhts.tool1.persistence.PersistenceService;
import gov.nih.ncgc.openhts.tool1.plotManager.PlotManager;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetManager;
import gov.nih.ncgc.openhts.tool1.resourceManager.ResourceProvider2;
import gov.nih.ncgc.openhts.tool1.spreadsheet.Spreadsheet;
import gov.nih.ncgc.openhts.tool1.util.Mailer;
import gov.nih.ncgc.openhts.tool1.util.MailerException;
import gov.nih.ncgc.openhts.tool1.viewers.SelectionViewerListener;

/**
 * Purpose is to represent the SYSTEM to the APP.
 * 
 * @author talafousj
 */
public class Main extends JApplet implements ResourceProvider2, EngineListener {
  public static Main main;
  private static boolean applicationTerminationRequested = false;
  private static final Logger logger = Logger.getLogger(Main.class.getPackage()
      .getName());
  public static AppLafManager appLafManager = null;
  private static final String SINGLE_INSTANCE_SOCKET_PORT_NUMBER = "gov.nih.ncgc.openhts.tool1.SingleInstanceSocketPortNumber";
  static final String LOOK_AND_FEEL = "gov.nih.ncgc.openhts.tool1.ui.LookAndFeel";
  private static final String PROPERTIES_RESOURCE = Version.PRODUCT_NAME_SHORT
      + ".properties.xml";
  public static Engine engine = null;
  static PersistenceService persistenceService;
  private static SingleRunningInstanceService singleRunningInstanceService;
  public static final Mailer mailer = new Mailer();
  public static boolean isDemoApplet, isPersisting;
  private static final List<Session> sessions = new ArrayList<Session>();
  private static HelpBroker helpBroker = null;
  private static ActionListener helpEventHandler = null;
  public static String ROOT_ENV = Version.PRODUCT_NAME_SHORT.toLowerCase()
      + "_root";
  public static File homeDir;
  public static final long dirMonitorPollingInterval = 10000;
  static Class<?>[] plugins;
  private static String[] pluginClassNames = { "gov.nih.ncgc.openhts.tool1.PluginBrowserControl" };

  private static void loadPlugins() {
    List<Class<?>> pluginList = new ArrayList<Class<?>>();
    pluginList.add(SelectionViewerListener.class);
    List<URL> urls = new ArrayList<URL>();
    String pluginDirName = homeDir.getPath() + File.separator + "plugins"
        + File.separator + "plugin_browser_control.jar";
    try {
      urls.add(new URL("file:///" + pluginDirName));
      URLClassLoader pluginClassLoader = new URLClassLoader(urls
          .toArray(new URL[urls.size()]));
      for (String pluginClassName : pluginClassNames) {
        try {
          Class<?> klass = pluginClassLoader.loadClass(pluginClassName);
          pluginList.add(klass);
          System.out.println("Loaded plugin class: " + klass.getName());
        }
        catch (Throwable e) {
          System.err.println("plugin not found, limited functionality: "
              + pluginClassName);
          continue;
        }
      }
    }
    catch (MalformedURLException e1) {
      e1.printStackTrace();
    }
    plugins = pluginList.toArray(new Class<?>[pluginList.size()]);
  }

  public static void shutdown() {
    SplashWindow.textColor = Color.YELLOW;
    SplashWindow.showSplash();
    notifyShutdownListeners();
    SplashWindow.updateLoadingText("Shutting down engine...");
    if (null != engine) {
      engine.shutdown();
    }
    SplashWindow.updateLoadingText("Shutting down persistence service...");
    if (null != persistenceService) {
      persistenceService.stop();
    }
    SplashWindow.hideSplash();
    if (!isDemoApplet) {
      System.exit(0);
    }
  }

  private static final List<ShutdownListener> shutdownListeners = Collections
      .synchronizedList(new ArrayList<ShutdownListener>());

  static public void addShutdownListener(final ShutdownListener listener) {
    shutdownListeners.add(listener);
  }

  static public void removeShutdownListener(final ShutdownListener listener) {
    shutdownListeners.remove(listener);
  }

  private static void notifyShutdownListeners() {
    final List<ShutdownListener> l = new ArrayList<ShutdownListener>();
    synchronized (shutdownListeners) {
      l.addAll(shutdownListeners);
    }
    for (final ShutdownListener sl : l) {
      sl.shutdown();
    }
  }

  public void startApp() {
    isDemoApplet = false;
    isPersisting = false;
    try {
      if (!setupHomeDir()) {
        return;
      }
      // load the resource file...
      final InputStream propStream = Main.class
          .getResourceAsStream(PROPERTIES_RESOURCE);
      int port;
      if (propStream == null) {
        System.out.println("Your classpath is: "
            + System.getProperty("java.class.path"));
        throw new RuntimeException("Could not find: " + PROPERTIES_RESOURCE);
      }
      final Properties properties = new Properties();
      properties.loadFromXML(propStream);
      propStream.close();
      port = Integer.parseInt(properties
          .getProperty(SINGLE_INSTANCE_SOCKET_PORT_NUMBER));
      // make sure we only have one single running instance...
      singleRunningInstanceService = new SingleRunningInstanceService(port
      /* ,getApplicationEventDispatcher() */);
      if (!singleRunningInstanceService.startOrNotifyServer()) {
        logger.severe("already running!  Exiting...");
        applicationTerminationRequested = true;
        return;
      }
      SplashWindow.updateLoadingText("Loading plugins...");
      loadPlugins();
      SplashWindow.updateLoadingText("Loading persistence service...");
      Thread.sleep(100);
      // Initialize application persistence
      persistenceService = new Db4oPersistenceService();
      PersistenceService.setService(persistenceService);
      persistenceService.register(new GlobalSettings.Root());
      persistenceService.register(new DataSourceManager.Root());
      persistenceService.register(new Spreadsheet.Root());
      persistenceService.register(new PointsetManager.Root());
      persistenceService.register(new DescriptorManager.Root());
      persistenceService.register(new PlotManager.Root());
      // persistenceService.register(new SessionSettings.Root());
      persistenceService.start();
      engine = new Engine(Version.PRODUCT_NAME_SHORT + "_Engine", null, Long
          .parseLong(GlobalSettings.getInstance().getProperty(
              GlobalSettings.CACHEMB_KEY)) * 1048576L);
      engine.addListener(this);
      initialize();
    }
    catch (final Throwable e) {
      logger.log(Level.SEVERE, "Could not initialize application: " + e, e);
      showErrorSupportDialog(null, "Could not initialize application: " + e, e);
      shutdown();
    }
  }

  private boolean setupHomeDir() throws HeadlessException, IOException {
    String homeDirName = System.getenv(ROOT_ENV);
    if (homeDirName == null) {
      if (!askUserForHomeDir()) {
        System.out.println("user cancelled, cannot proceed without " + ROOT_ENV
            + " environment variable set");
        applicationTerminationRequested = true;
        return false;
      }
    }
    else {
      homeDir = new File(homeDirName);
    }
    if (!homeDir.getName().endsWith(ROOT_ENV)) {
      applicationTerminationRequested = true;
      JOptionPane.showMessageDialog(null, "Directory '" + homeDir.getName()
          + "' must end with:  " + ROOT_ENV + "\nEnvironment variable "
          + ROOT_ENV + " may need to be set properly");
      return false;
    }
    if (!homeDir.exists()) {
      applicationTerminationRequested = true;
      JOptionPane.showMessageDialog(null, "Non-existant: "
          + homeDir.getCanonicalPath() + "\nEnvironment variable " + ROOT_ENV
          + " may need to be set properly");
      return false;
    }
    return true;
  }

  private static boolean askUserForHomeDir() {
    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fileChooser.setDialogTitle("Find root directory");
    fileChooser.setFileFilter(FileFilters.homeDirFileFilter);
    fileChooser.setAcceptAllFileFilterUsed(false);
    if (fileChooser.showOpenDialog(SplashWindow.getInstance()) != JFileChooser.APPROVE_OPTION) {
      return false;
    }
    homeDir = fileChooser.getSelectedFile();
    return true;
  }

  public void startApplet() {
    isDemoApplet = true;
    isPersisting = false;
    try {
      initialize();
    }
    catch (final Exception x) {
      logger.log(Level.SEVERE, "Could not initialize application: " + x, x);
      JOptionPane.showMessageDialog(null, "Could not initialize application: "
          + x);
      shutdown();
    }
    catch (final Error x) {
      logger.log(Level.SEVERE, "Could not initialize application: " + x, x);
      x.printStackTrace();
      JOptionPane.showMessageDialog(null, "Could not initialize application: "
          + x);
      shutdown();
    }
  }

  @Override
  public void stop() {
    shutdown();
  }

  @Override
  public void start() {
    isDemoApplet = true;
    isPersisting = false;
    SplashWindow.textColor = Color.GRAY;
    SplashWindow.showSplash();
    try {
      startApplet();
      if (applicationTerminationRequested) {
        shutdown();
      }
      else {
        run();
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      SplashWindow.hideSplash();
    }
  }

  @Override
  public void destroy() {
    // nop
  }

  @Override
  public void init() {
    // nop
  }

  private void initialize() throws Exception {
    // This is for the application, see init() for applet initialization
    try {
      SplashWindow.updateLoadingText("Loading Look and Feel...");
      String lookAndFeel = GlobalSettings.getInstance().getProperty(
          LOOK_AND_FEEL);
      if (lookAndFeel == null || lookAndFeel.isEmpty()) {
        lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        GlobalSettings.getInstance().putProperty(LOOK_AND_FEEL, lookAndFeel);
      }
      javax.swing.UIManager.setLookAndFeel(lookAndFeel);
    }
    catch (final javax.swing.UnsupportedLookAndFeelException x) {
      logger
          .warning("Caught UnsupportedLookAndFeelException while setting look and feel "
              + x);
    }
    catch (final IllegalAccessException x) {
      logger
          .warning("Caught IllegalAccessException while setting look and feel "
              + x);
    }
    catch (final InstantiationException x) {
      logger
          .warning("Caught InstantiationException while setting look and feel "
              + x);
    }
    catch (final ClassNotFoundException x) {
      logger
          .warning("Caught ClassNotFoundException while setting look and feel "
              + x);
    }
    SplashWindow.updateLoadingText("Preparing UI...");
    sessions.add(createSession());
  }

  public Session createSession() throws Exception {
    Session session;
    session = new Session(this);
    final ClassLoader classLoader = Main.class.getClassLoader();
    final URL helpSetURL = HelpSet.findHelpSet(classLoader, "help/app.hs");
    if (helpSetURL == null) {
      new RuntimeException("app.hs cannot be found in: "
          + System.getProperty("java.class.path")).printStackTrace();
    }
    else {
      try {
        final HelpSet hs = new HelpSet(classLoader, helpSetURL);
        helpBroker = hs.createHelpBroker("PCSpaceMainWindow");
        helpEventHandler = new CSH.DisplayHelpFromSource(helpBroker);
        helpBroker.enableHelpKey(session.frame.getRootPane(), "mapkey.top", hs);
      }
      catch (final HelpSetException e) {
        e.printStackTrace();
      }
    }
    return session;
  }

  int sessionCount() {
    return sessions.size();
  }

  public void endSession(final Session session) {
    session.shutdown();
    session.frame.dispose();
    sessions.remove(session);
    if (sessions.size() == 0) {
      // try {
      // Thread.sleep(10000);
      // }
      // catch (InterruptedException e) {
      // // TODO Auto-generated catch block
      // e.printStackTrace();
      // }
      stop();
    }
  }

  public void run() throws Exception {
    if (applicationTerminationRequested) {
      return;
    }
    SplashWindow.updateLoadingText("Displaying UI...");
    Session session = sessions.get(0);
    session.showUI();
    sessions.get(0).synchronize();
    if (applicationTerminationRequested) {
      return;
    }
    SplashWindow.updateLoadingText("Starting calculation engines...");
    if (!isDemoApplet) {
      engine.start();
    }
  }

  @Override
	public Engine getEngine() {
    return engine;
  }

  public static ActionListener getHelpEventHandler() {
    return helpEventHandler;
  }

  public static HelpBroker getHelpBroker() {
    return helpBroker;
  }

  public void centerOnScreen(final JFrame frame) {
    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    if (screenSize.getWidth() > frame.getWidth()
        && screenSize.getHeight() > frame.getHeight()) {
      frame.setLocation((int) (screenSize.getWidth() - frame.getWidth()) / 2,
          (int) (screenSize.getHeight() - frame.getHeight()) / 2);
    }
    else { // preferred height or width is larger than screen so maximize frame
      frame.setBounds(0, 0, (int) screenSize.getWidth(), (int) screenSize
          .getHeight());
    }
  }

  @Override
	public void engineExceptionThrown(final KernelException x,
      final KernelResult result) {
    // TODO Auto-generated method stub
    x.printStackTrace();
  }

  @Override
	public void engineStateChanged(final EngineState engineState) {
    // TODO Auto-generated method stub
  }

  public static void showErrorSupportDialog(Session session, String message,
      Throwable throwable) {
    if (throwable == null) {
      JOptionPane.showMessageDialog(session == null ? null : session.frame,
          message, "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    throwable.printStackTrace();
    int answer = JOptionPane.showConfirmDialog(session == null ? null
        : session.frame, message + throwable
        + "\n\nClick yes to e-mail diagnostic for improving "
        + Version.PRODUCT_NAME_SHORT, "Error", JOptionPane.ERROR_MESSAGE);
    if (answer == JOptionPane.YES_OPTION) {
      try {
        StringBuffer sb = new StringBuffer("[Diagnostic report generated "
            + new Date()
            + "]\n\nFollowing error message was presented to user:\n\n");
        sb.append(message);
        sb.append("\n\nStack trace follows:\n");
        for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
          sb.append('\n').append(stackTraceElement);
        }
        if (session != null) {
          sb.append("\n\nLog history follows:\n");
          for (String line : session.statusPanel.getLog2Deque()) {
            sb.append("\n").append(line);
          }
        }
        sb.append("\n\n[End of report.]\n");
        Main.mailer.send("jtalafous@gmail.com", "jtalafous@gmail.com",
            Version.PRODUCT_NAME + ": " + message, sb.toString());
        if (session != null) {
          session.statusPanel
              .log2("Diagnostic report mailed, thank you for contributing.");
        }
      }
      catch (MailerException e) {
        session.statusPanel
            .log2("Diagnotics report not mailed due to network-related error.");
        e.printStackTrace();
      }
    }
  }

  public static void main(final String[] args) {
    // DecimalFormat nf = new DecimalFormat("32.0");
    // String input = "The quick brown fox jumps over the lazy dog";
    // byte[] buf = input.getBytes();
    // int integer=0;
    // StringBuffer sb = new StringBuffer();
    // for (int i = 0; i < buf.length; i++) {
    // int firstByte = 0xFF & buf[i];
    // integer=0;
    // integer = firstByte & 0xFF;
    // for (int j = 0; j < Integer.numberOfLeadingZeros(integer) - 24; j++) {
    // sb.append("0");
    // }
    // sb.append(Long.toString(integer, 2));
    //
    // }
    // System.out.println(sb);
    // for (int i = 0; i < 256; i++) {
    // StringBuffer sb = new StringBuffer("0.");
    // if (i < 100) {
    // sb.append("0");
    // if (i < 10) {
    // sb.append("0");
    // }
    // }
    // sb.append(i);
    // BigInteger bigInteger = new BigInteger(sb.toString().getBytes());
    // String s = sb.append(bigInteger.toString()).toString();
    // System.out.println(s);
    // // float f=Float.parseFloat(s);
    // // System.out.println(f);
    // }
    // BigInteger bigInteger = new BigInteger(sb.toString(),2);
    // if (bigInteger2.equals(bigInteger)) {
    // System.out.println("treu");
    // }
    //    
    // float f = Float.parseFloat("0."+bigInteger.toString());
    // System.out.println();
    // //float f=bigInteger.floatValue();
    // System.out.println(f);
    // BigInteger bigInteger = new BigInteger("Joseph Talafous", 256);
    // System.out.println(bigInteger.floatValue());
    // return;
    // Please do not defeat this expiration. This forces people to login and
    // get a fixed FREE version.
    System.out.println(UIManager.getLookAndFeelDefaults().keySet());
    System.err.println("THIS VERSION OF (" + Version.PRODUCT_NAME
        + ") EXPIRES ON " + Version.expirationDate);
    try {
      if (new Date().before(Version.expirationDate)) {
        SplashWindow.textColor = Color.GRAY;
        SplashWindow.showSplash();
        // SplashWindow.updateLoadingText("Expires on "+expirationDate);
        main = new Main();
        main.startApp();
        if (!applicationTerminationRequested) {
          main.run();
        }
        else {
          Main.shutdown();
        }
      }
      else {
        String message = Version.PRODUCT_NAME + " v" + Version.ToString()
            + "\n expired on " + Version.expirationDate
            + ".\nPlease go to ncgc.nih.gov for a FREE upgrade.";
        System.err.println(message);
        JOptionPane.showMessageDialog(null, message);
      }
    }
    catch (Exception e) {
      showErrorSupportDialog(null, "FATAL: Cannot run application because: "
          + e, e);
    }
    finally {
      SplashWindow.hideSplash();
    }
  }
}
