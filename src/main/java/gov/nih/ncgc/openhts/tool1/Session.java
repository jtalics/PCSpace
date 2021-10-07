package gov.nih.ncgc.openhts.tool1;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.help.HelpBroker;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import com.jidesoft.swing.JidePopupMenu;
import gov.nih.ncgc.openhts.tool1.actions.ActionManager;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.DataSource;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.DataSourceManager;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Basis;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Descriptor;
import gov.nih.ncgc.openhts.tool1.descriptorManager.DescriptorManager;
import gov.nih.ncgc.openhts.tool1.dialogManager.DialogManager;
import gov.nih.ncgc.openhts.tool1.heatmap.Heatmap;
import gov.nih.ncgc.openhts.tool1.plotManager.PlotManager;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetManager;
import gov.nih.ncgc.openhts.tool1.spreadsheet.Spreadsheet;
import gov.nih.ncgc.openhts.tool1.viewers.SelectionReporter;
import gov.nih.ncgc.openhts.tool1.viewers.SelectionViewer;
import gov.nih.ncgc.openhts.tool1.viewers.SelectionViewerListener;

/**
 * Purpose is to represent the USER to the APP.
 * 
 * @author talafousj
 */
public class Session implements ShutdownListener, WindowListener {
  private class NoCloseJarInputStream extends ZipInputStream {
    // http://weblogs.java.net/blog/kohsuke/archive/2005/07/socket_xml_pitf.html
    public NoCloseJarInputStream(InputStream in) throws IOException {
      super(in);
    }

    @Override
    public void close() {
      // nop
    }

    public void CLOSE() throws IOException {
      super.close();
    }
  }

  private static final Logger logger = Logger.getLogger(Session.class
      .getPackage().getName());
  public final StatusPanel statusPanel;
  private PreferencesDialog preferencesDialog;
  public final DialogManager dialogManager;
  public Main main;
  private String name;
  private boolean shutdown = false;
  // Keys for persistence
  public static final int dividerSize = 8;
  private static final String MAIN_FRAME_WIDTH = "gov.nih.ncgc.openhts.tool1.ui.MainFrameWidth";
  private static final String MAIN_FRAME_HEIGHT = "gov.nih.ncgc.openhts.tool1.ui.MainFrameHeight";
  private static final String MAIN_FRAME_LOCX = "gov.nih.ncgc.openhts.tool1.ui.MainFrameLocX";
  private static final String MAIN_FRAME_LOCY = "gov.nih.ncgc.openhts.tool1.ui.MainFrameLocY";
  private static final String MAIN_SPLIT_KEY = "gov.nih.ncgc.openhts.tool1.ui.MainSplit";
  private static final String DATA_MANAGER_SPLIT_KEY = "gov.nih.ncgc.openhts.tool1.ui.DataManagerSplit";
  private static final String RIGHT_SPLIT_KEY = "gov.nih.ncgc.openhts.tool1.ui.RightSplit";
  private static final String RIGHT_VERTICAL_SPLIT_KEY = "gov.nih.ncgc.openhts.tool1.ui.RightVerticalSplit";
  public static final Color focusLostColor = Color.WHITE;
  public static final Color focusGainedColor = Color.GREEN;
  public final float dotsPerMM;
  public Spreadsheet spreadsheet;
  public PlotManager plotManager;
  public final SelectionViewer selectionViewer;
  public final SelectionReporter selectionReporter;
  public boolean saved;
  public final ActionManager actionManager;
  public final MainMenuBar mainMenuBar;
  public DataSourceManager dataSourceManager;
  public DescriptorManager descriptorManager;
  public PointsetManager pointsetManager;
  public boolean explainedDataTableBeep;
  public static final Font[] fonts = GraphicsEnvironment
      .getLocalGraphicsEnvironment().getAllFonts();
  public File cwd; // current working directory
  public final JFrame frame;
  public Heatmap heatmap;

  public Session(final Main resourceProvider) throws Exception {
    this.main = resourceProvider;
    name = "session " + main.sessionCount();
    dotsPerMM = Toolkit.getDefaultToolkit().getScreenResolution() / 25.4f;
    frame = new JFrame();
    // Allow application to continue running with no visible windows.
    // User may redisplay main window via the task-tray icon.
    frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    frame.setTitle(Version.PRODUCT_NAME + ": " + name);
    frame.setIconImage(AppLafManager.getIcon(AppLafManager.IconKind.Session)
        .getImage());
    actionManager = new ActionManager(this);
    mainMenuBar = new MainMenuBar(this);
    frame.setJMenuBar(mainMenuBar);
    frame.addWindowListener(this);
    SplashWindow.updateLoadingText("Loading plotter...");
    plotManager = new PlotManager(this);
    plotManager.initialize();
    SplashWindow.updateLoadingText("Loading heatmap...");
    heatmap = new Heatmap(this);
    SplashWindow.updateLoadingText("Loading data source manager...");
    dataSourceManager = new DataSourceManager(this);
    SplashWindow.updateLoadingText("Loading descriptor manager...");
    descriptorManager = new DescriptorManager(this);
    descriptorManager.addDescriptorManagerListener(dataSourceManager);
    // NO, done thru dirMonitor:
    // dataSourceManager.addDataSourceManagerListener(descriptorManager);
    SplashWindow.updateLoadingText("Loading pointset manager...");
    pointsetManager = new PointsetManager(this);
    pointsetManager.addPointsetManagerListener(plotManager);
    dataSourceManager.addDataSourceManagerListener(pointsetManager);
    descriptorManager.addDescriptorManagerListener(pointsetManager);
    plotManager.addPlotManagerListener(pointsetManager);
    plotManager.addPlotManagerListener(descriptorManager);
    SplashWindow.updateLoadingText("Loading molecule viewers...");
    selectionViewer = new SelectionViewer(this);
    for (SelectionViewerListener selectionViewerListener : getSelectionViewerListeners()) {
      selectionViewer.addSelectionViewerListener(selectionViewerListener);
    }
    plotManager.addPlotManagerListener(selectionViewer);
    selectionReporter = new SelectionReporter(this);
    SplashWindow.updateLoadingText("Loading spreadsheet viewer...");
    spreadsheet = new Spreadsheet(this);
    pointsetManager.addPointsetManagerListener(spreadsheet);
    pointsetManager.addPointsetManagerListener(heatmap);
    spreadsheet.addSpreadsheetListener(pointsetManager);
    spreadsheet.addSpreadsheetListener(selectionViewer);
    spreadsheet.addSpreadsheetListener(heatmap);
    // pointsetManager.addPointsetManagerListener(descriptorManager);
    statusPanel = new StatusPanel(this);
    frame.setContentPane(buildUI());
    actionManager.bindCanvasKeys();
    actionManager.bindHeatmapKeys();
    SplashWindow.updateLoadingText("Loading dialog manager...");
    dialogManager = DialogManager.getInstance(this);
    SplashWindow.updateLoadingText("Starting directory monitors...");
    if (!Main.isDemoApplet) {
      cwd = Main.homeDir;
    }
  }

  private List<SelectionViewerListener> getSelectionViewerListeners() {
    List<SelectionViewerListener> selectionViewerListeners = new ArrayList<SelectionViewerListener>();
    for (Class plugin : Main.plugins) {
      // System.out.println("SelectionViewerListener class: " +
      // plugin.getClass().getName());
      // System.out.println(" classloader: " +
      // plugin.getClass().getClassLoader());
      // Class<?>[] ifaces = plugin.getClass().getInterfaces();
      // System.out.println(" iface: " + ifaces[0].getName());
      // System.out.println(" iface classloader: " +
      // ifaces[0].getClassLoader());
      // System.out.println(" reg iface classloader:
      // "+SelectionViewerListener.class.getClassLoader());
      for (Class<?> iface : plugin.getInterfaces()) {
        if (iface == SelectionViewerListener.class) {
          SelectionViewerListener selectionViewerInstance;
          try {
            selectionViewerInstance = (SelectionViewerListener) plugin
                .newInstance();
            selectionViewerListeners.add(selectionViewerInstance);
          }
          catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
    return selectionViewerListeners;
  }

  private class Synchronizer extends SwingWorker<String, Object> {
    @Override
    public String doInBackground() {
      try {
        if (Main.isDemoApplet) {
          synchronizeExplicitly();
        }
        else {
          synchronizeImplicitly();
        }
        return "success";
      }
      catch (Throwable throwable) {
        Main.showErrorSupportDialog(Session.this,
            "Cannot synchronize because: ", throwable);
        return "failure";
      }
    }

    @Override
    protected void done() {
      try {
        statusPanel.log2("synchronized: " + get());
      }
      catch (Exception ignore) {
        // nop
      }
    }
  }

  void synchronize() {
    (new Synchronizer()).execute();
  }

  private void synchronizeImplicitly() throws Throwable {
    descriptorManager.synchronizeDescriptors();
    descriptorManager.synchronizeBases();
    pointsetManager.synchronize();
    dataSourceManager.synchronize();
  }

  private void synchronizeExplicitly() throws Throwable {
    // Read all hardened entities from the jar file, in the order they were
    // jarred
    List<Basis> bases = new ArrayList<Basis>();
    List<Pointset> pointsets = new ArrayList<Pointset>();
    String resourceName = "/pcspace_root.zip";
    InputStream fis = Main.class.getResourceAsStream(resourceName);
    if (fis == null) {
      throw new Exception("Cannot access resource: " + resourceName);
    }
    NoCloseJarInputStream jarStream = new NoCloseJarInputStream(fis);
    ZipEntry jarEntry;
    while (null != (jarEntry = jarStream.getNextEntry())) {
      String entryName = jarEntry.getName();
      if (entryName.endsWith(FileFilters.dataSourceXmlExt)) {
        System.out.println("Accessing subresource: " + entryName);
        DataSource dataSource = new DataSource();
        dataSource.setLoading(true);
        dataSourceManager.registerDataSource(dataSource);
        dataSourceManager.soften(dataSource,
            new BufferedInputStream(jarStream), entryName);
        dataSource.setLoading(false);
        dataSourceManager.dataSourceChanged(dataSource);
        // dataSourceManager.processUnrecognizedColumnNames(new
        // DataSourceFileReader(this,
        // new InputStreamReader(jarStream)));
        if (!dataSource.getName().equals(name)) {
          // TODO: throw new IOException("File improperly named: " +
          // xmlFile.getPath());
        }
        // StringBuffer sb = new StringBuffer();
        // BufferedInputStream bis = new BufferedInputStream(jar);
        // int i=0;
        // while (-1!=(i=bis.read())) {
        // sb.append((char)i);
        // }
        // System.out.println(sb.toString());
      }
      if (entryName.endsWith(FileFilters.descriptorXmlExt)) {
        System.out.println("Accessing subresource: " + entryName);
        Descriptor descriptor = descriptorManager.softenDescriptor(
            new BufferedInputStream(jarStream), entryName);
        if (!descriptor.getName().equals(name)) {
          // TODO: throw new IOException("File improperly named: " + entryName);
        }
        descriptorManager.registerDescriptor(descriptor);
      }
      if (entryName.endsWith(FileFilters.basisXmlExt)) {
        System.out.println("Accessing subresource: " + entryName);
        Basis basis = descriptorManager.softenBasis(new BufferedInputStream(
            jarStream), entryName);
        if (!basis.getName().equals(name)) {
          // TODO: throw new IOException("File improperly named: " + entryName);
        }
        bases.add(basis);
      }
      if (entryName.endsWith(FileFilters.pointsetTxtExt)) {
        System.out.println("Accessing subresource: " + entryName);
        Pointset pointset = new Pointset(this, null);
        pointsetManager.setVisible(pointset, false);
        pointset.setName("?");
        pointsetManager.registerPointset(null, pointset);
        pointsetManager.setLoading(pointset, true);
        pointsetManager.soften(pointset, new InputStreamReader(jarStream),
            entryName);
        pointsetManager.setLoading(pointset, false);
        pointsetManager.setVisible(pointset, true);
        pointsets.add(pointset);
        if (!pointset.getName().equals(name)) {
          // TODO: throw new IOException("File improperly named: " + entryName);
        }
        pointsetManager.pointsetModified(pointset);
      }
    }
    jarStream.CLOSE();
    for (Basis basis : bases) {
      basis.findMissingDescriptors();
      basis.initialize();
      descriptorManager.registerBasis(basis);
    }
    for (Pointset pointset : pointsets) {
      pointset.findMissingBases();
      // Re-add pointset so that it gets the right parent node,
      // basis may have been changed
      pointsetManager.pointsetTreeModel.removePointset(pointset);
      pointsetManager.pointsetTreeModel.addPointset(pointset);
      pointsetManager.selectPointsets(new Pointset[] { pointset });
    }
  }

  /**
   * Builds the user interface: system tray
   * 
   * @param resourceProvider
   */
  public void showUI() {
    if (shutdown) {
      return;
    }
    if (SystemTray.isSupported()) {
      final Image image = AppLafManager.getIcon(AppLafManager.IconKind.Session)
          .getImage();
      final JidePopupMenu popup = new JidePopupMenu();
      popup.add(new JMenuItem(actionManager.showUiAction));
      popup.add(new JMenuItem(actionManager.hideUiAction));
      popup.add(new JSeparator());
      popup.add(new JMenuItem(actionManager.sessionEndAction));
      if (!Main.isDemoApplet) {
        try {
          final TrayIcon trayIcon = new TrayIcon(image, Version.PRODUCT_NAME,
              null);
          SystemTray.getSystemTray().add(trayIcon);
          trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(final MouseEvent e) {
              if (e.isPopupTrigger()) {
                popup.setLocation(e.getX(), e.getY());
                popup.setInvoker(popup);
                popup.setVisible(true);
              }
            }
          });
          trayIcon.setImageAutoSize(true);
          trayIcon.addActionListener(new ActionListener() {
            @Override
						public void actionPerformed(final ActionEvent e) {
              if (!frame.isVisible()) {
                frame.setVisible(true);
              }
            }
          });
        }
        catch (final AWTException x) {
          errorNoSupport("TrayIcon could not be added.");
        }
      }
    }
    else {
      errorNoSupport("SystemTray is not supported.");
    }
    setDimLoc();
  }

  private void setDimLoc() {
    // Determine the dimensions and location of the session frame
    // Following are defaults if user has not already persisted theirs
    int mainWidth = 1200;
    int mainHeight = 800;
    int mainLocX = 100;
    int mainLocY = 100;
    final String mainWidthProp = GlobalSettings.getInstance().getProperty(
        MAIN_FRAME_WIDTH);
    if (mainWidthProp != null) {
      mainWidth = Integer.parseInt(mainWidthProp);
    }
    final String mainHeightProp = GlobalSettings.getInstance().getProperty(
        MAIN_FRAME_HEIGHT);
    if (mainHeightProp != null) {
      mainHeight = Integer.parseInt(mainHeightProp);
    }
    final String mainLocXProp = GlobalSettings.getInstance().getProperty(
        MAIN_FRAME_LOCX);
    if (mainLocXProp != null) {
      mainLocX = Integer.parseInt(mainLocXProp);
    }
    final String mainLocYProp = GlobalSettings.getInstance().getProperty(
        MAIN_FRAME_LOCY);
    if (mainLocYProp != null) {
      mainLocY = Integer.parseInt(mainLocYProp);
    }
    frame.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(final ComponentEvent event) {
        final Dimension d = frame.getSize();
        GlobalSettings.getInstance().putProperty(MAIN_FRAME_WIDTH,
            Integer.toString(d.width));
        GlobalSettings.getInstance().putProperty(MAIN_FRAME_HEIGHT,
            Integer.toString(d.height));
      }

      @Override
      public void componentMoved(final ComponentEvent event) {
        final Point p = frame.getLocation();
        GlobalSettings.getInstance().putProperty(MAIN_FRAME_LOCX,
            Integer.toString(p.x));
        GlobalSettings.getInstance().putProperty(MAIN_FRAME_LOCY,
            Integer.toString(p.y));
      }
    });
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(final WindowEvent e) {
        super.windowClosing(e);
      }
    });
    final int frameWidth = mainWidth;
    final int frameHeight = mainHeight;
    final int frameLocX = mainLocX;
    final int frameLocY = mainLocY;
    // Catch all exception, even from UI threads
    final ThreadGroup exceptionThreadGroup = new ExceptionGroup();
    // We have all the needed measurements, now use them
    SwingUtilities.invokeLater(new Thread(exceptionThreadGroup, "Init thread") {
      @Override
      public void run() {
        frame.setSize(frameWidth, frameHeight);
        frame.setLocation(frameLocX, frameLocY);
        frame.setVisible(true);
      }
    });
  }

  private void preferencesDialogAction(final ActionEvent e) {
    if (preferencesDialog == null) {
      preferencesDialog = new PreferencesDialog(this);
    }
    preferencesDialog.showPreferencesDialog();
  }

  private void helpAction(final ActionEvent event) {
    if (Main.getHelpEventHandler() != null) {
      Main.getHelpEventHandler().actionPerformed(event);
    }
  }

  private JPanel buildUI() {
    // Configure the main splitpane
    final JSplitPane mainSplitPane = new JSplitPane() {
      @Override
      public void setDividerLocation(int dl) {
        super.setDividerLocation(dl);
        GlobalSettings.getInstance().putProperty(MAIN_SPLIT_KEY,
            Integer.toString(dl));
      }

      private static final long serialVersionUID = 1L;
    };
    mainSplitPane.setDividerSize(dividerSize);
    String dl = GlobalSettings.getInstance().getProperty(MAIN_SPLIT_KEY);
    if (dl != null) {
      mainSplitPane.setDividerLocation(Integer.parseInt(dl));
    }
    else {
      mainSplitPane.setDividerLocation(224);
    }
    mainSplitPane.setOneTouchExpandable(true);
    // 
    final JSplitPane dataManagerSplitPane = new JSplitPane(
        JSplitPane.VERTICAL_SPLIT) {
      @Override
      public void setDividerLocation(int dl) {
        super.setDividerLocation(dl);
        GlobalSettings.getInstance().putProperty(DATA_MANAGER_SPLIT_KEY,
            Integer.toString(dl));
      }

      private static final long serialVersionUID = 1L;
    };
    dataManagerSplitPane.setTopComponent(dataSourceManager.getViewlet());
    dataManagerSplitPane.setBottomComponent(pointsetManager.getViewlet());
    dl = GlobalSettings.getInstance().getProperty(DATA_MANAGER_SPLIT_KEY);
    if (dl != null) {
      dataManagerSplitPane.setDividerLocation(Integer.parseInt(dl));
    }
    else {
      dataManagerSplitPane.setDividerLocation(300);
    }
    dataManagerSplitPane.setOneTouchExpandable(true);
    dataManagerSplitPane.setDividerSize(dividerSize);
    mainSplitPane.setLeftComponent(dataManagerSplitPane);
    final JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab("Plot", plotManager);
    tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1,
        "3D/2D interactive pointset viewer");
    tabbedPane.addTab("Report", selectionReporter);
    tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1,
        "Info about current spreadsheet selections");
    tabbedPane.addComponentListener(plotManager);
    tabbedPane.addTab("Descriptors", descriptorManager.getViewlet());
    tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1,
        "Tools for managing descriptors and bases");
    tabbedPane.addTab("Operators", new JLabel("Clustering, dimred, etc"));
    tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1,
        "Tools for managing pointset operators");
    tabbedPane.addTab("Heatmap", heatmap);
    tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1,
        "Visualize values by colored matrix");
    tabbedPane.setSelectedComponent(heatmap);
    // Build the right splitpane
    final JSplitPane rightSplitPane = new JSplitPane() {
      private static final long serialVersionUID = 1L;

      @Override
      public void setDividerLocation(int dl) {
        super.setDividerLocation(dl);
        GlobalSettings.getInstance().putProperty(RIGHT_SPLIT_KEY,
            Integer.toString(dl));
      }
    };
    mainSplitPane.setRightComponent(rightSplitPane);
    dl = GlobalSettings.getInstance().getProperty(RIGHT_SPLIT_KEY);
    if (dl != null) {
      rightSplitPane.setDividerLocation(Integer.parseInt(dl));
    }
    else {
      rightSplitPane.setDividerLocation(650);
    }
    rightSplitPane.setOneTouchExpandable(true);
    rightSplitPane.setDividerSize(dividerSize);
    rightSplitPane.setLeftComponent(tabbedPane);
    // rightSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
    // new PropertyChangeListener() {
    //
    // public void propertyChange(PropertyChangeEvent evt) {
    // int width = (Integer)evt.getNewValue();
    // System.out.println("Divider location changed to " + width+"tabbed pane
    // size = "+tabbedPane.getSize());
    // // tabbedPane.setPreferredSize(new
    // Dimension(width,rightSplitPane.getHeight()));
    // // tabbedPane.invalidate();
    // // tabbedPane.revalidate();
    // // tabbedPane.repaint();
    //
    // plotter.canvas.externalResize();
    // }
    // });
    rightSplitPane.setRightComponent(getRightMainPanel());
    final JPanel contentPane = new JPanel();
    contentPane.setLayout(new BorderLayout());
    // Set up the status panel
    statusPanel.log1("high level status messages here");
    statusPanel.log2("low level status messages area");
    contentPane.add(statusPanel, BorderLayout.SOUTH);
    contentPane.add(mainSplitPane, BorderLayout.CENTER);
    return contentPane;
  }

  @Override
	public void shutdown() {
    statusPanel.log1("Shutting down session ... thank you for using "
        + Version.PRODUCT_NAME_SHORT);
    dataSourceManager.shutdown();
    pointsetManager.shutdown();
    descriptorManager.shutdown();
    plotManager.shutdown();
  }

  public void showPreferencesDialog() {
    preferencesDialogAction(null);
  }

  private JPanel getRightMainPanel() {
    final JPanel rightMainPanel = new JPanel();
    rightMainPanel.setLayout(new BoxLayout(rightMainPanel, BoxLayout.Y_AXIS));
    final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT) {
      private static final long serialVersionUID = 1L;

      @Override
      public void setDividerLocation(int dl) {
        super.setDividerLocation(dl);
        GlobalSettings.getInstance().putProperty(RIGHT_VERTICAL_SPLIT_KEY,
            Integer.toString(dl));
      }
    };
    rightMainPanel.add(splitPane);
    splitPane.setBorder(new EmptyBorder(0, 0, 0, 0));
    splitPane.setOneTouchExpandable(true);
    splitPane.setDividerSize(dividerSize);
    final String dl = GlobalSettings.getInstance().getProperty(
        RIGHT_VERTICAL_SPLIT_KEY);
    if (dl != null) {
      splitPane.setDividerLocation(Integer.parseInt(dl));
    }
    splitPane.setAlignmentX(Component.CENTER_ALIGNMENT);
    splitPane.setTopComponent(selectionViewer);
    splitPane.setDividerLocation(200);
    splitPane.setDividerSize(dividerSize);
    splitPane.setBottomComponent(spreadsheet);
    return rightMainPanel;
  }

  public void errorHelp(final String string, final String helpId) {
    // TODO put help button in error dialog which hooks up help id
    errorNoSupport(string);
  }

  public void errorSupport(String message, final Throwable throwable,
      final String helpID) {
    message += " " + throwable.getMessage();
    Main.showErrorSupportDialog(this, message, throwable);
    // TODO do something with help id
  }

  public void errorNoSupport(final String message) {
    Main.showErrorSupportDialog(this, message, null);
  }

  public void message(final String string) {
    final JButton okButton = new JButton("OK");
    okButton.setBorder(BorderFactory.createRaisedBevelBorder());
    final Object[] options = { "OK" };
    JOptionPane.showOptionDialog(frame, string, "Message",
        JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, options,
        options[0]);
  }

  public void clear() {
    dataSourceManager.clear();
    descriptorManager.clear();
    pointsetManager.clearPointsets();
  }

  /**
   * Override if you don't want to put new windows dead center
   */
  public Point assignLocation() {
    final int x = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2;
    final int y = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2;
    final Point p = new Point(x, y);
    return p;
  }

  /**
   * returns true if it's ok to continue
   */
  public boolean checkForOverwrite(final File file) throws Throwable {
    if (file.exists()) {
      final boolean ii = confirm("File " + file.getPath()
          + "\nexists.  OK to Overwrite?");
      if (ii) { // yes
        if (!file.canWrite()) {
          errorHelp("Unable to overwrite the file \n" + file.getPath(),
              "overwrite_error");
          return false;
        }
      }
      else {
        return false;
      } // cancel
    }
    return true;
  }

  /**
   * Puts up a confirm panel.
   */
  public boolean confirm(final String message) {
    return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(frame,
        message, "Yes/No Question", JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);
  }

  public void setLocation(final Component dialog, final Point center) {
    dialog.setLocation((int) (center.getX() - dialog.getWidth() / 2),
        (int) (center.getY() - dialog.getHeight() / 2));
  }

  public void saveTextFile(final File file, final String fileContents) {
    throw new RuntimeException("TODO");
    // servantManager.saveTextFile(fileName, fileContents);
  }

  public void setWaitCursor(final boolean waiting) {
    if (waiting) {
      final Cursor c = new Cursor(Cursor.WAIT_CURSOR);
      frame.setCursor(c);
      plotManager.canvas.setCursor(AppLafManager.CursorKind.CURSOR_WAIT);
    }
    else {
      final Cursor c = new Cursor(Cursor.DEFAULT_CURSOR);
      frame.setCursor(c);
      plotManager.canvas.setCursorNonWait();
    }
    try {
      Thread.sleep(100);
    }
    catch (final InterruptedException ex) {
      // ignore
    }
  }

  public HelpBroker getHelpBroker() {
    return Main.getHelpBroker();
  }

  public static boolean isLegalName(final String name2, boolean nullIsLegal) {
    if (name2 == null) {
      if (nullIsLegal) {
        // Usually initializing
        return true;
      }
      return false;
    }
    if (name2.isEmpty()) {
      return false;
    }
    final char[] chars = name2.toCharArray();
    char c = chars[0];
    // boolean lowerAlpha = (c >= 'a') && (c <= 'z');
    // boolean upperAlpha = (c >= 'A') && (c <= 'Z');
    // if (!(lowerAlpha || upperAlpha)) {
    // // Must start with alpha
    // return false;
    // }
    for (int x = 1; x < chars.length; x++) {
      c = chars[x];
      // if ((c >= 'a') && (c <= 'z')) continue; // lowercase
      // if ((c >= 'A') && (c <= 'Z')) continue; // uppercase
      // if ((c >= '0') && (c <= '9')) continue; // numeric
      // if ((c >= ' ')) continue; // space
      if (c == '\\' || c == '/' || c == '*' || c == '?' || c == '<' || c == '>'
          || c == ':' || c == '|' || c == '\"') {
        return false;
      }
    }
    return true;
  }

  public static String legalizeName(final String name2, boolean nullIsLegal) {
    if (name2 == null) {
      if (nullIsLegal) {
        return null;
      }
      throw new RuntimeException("name is null");
    }
    final char[] chars = name2.toCharArray();
    char c;
    for (int i = 1; i < chars.length; i++) {
      c = chars[i];
      if (c == '\\' || c == '/' || c == '*' || c == '?' || c == '<' || c == '>'
          || c == ':' || c == '|' || c == '\"') {
        chars[i] = '~';
      }
    }
    return new String(chars);
  }

  public static void addFocusBorder(final JComponent focusee,
      final JComponent borderee) {
    final Border focusGainedBorder = BorderFactory
        .createLineBorder(Session.focusGainedColor);
    final Border focusLostBorder = BorderFactory
        .createLineBorder(Session.focusLostColor);
    borderee.setBorder(focusLostBorder);
    focusee.addFocusListener(new FocusListener() {
      @Override
			public void focusGained(final FocusEvent e) {
        borderee.setBorder(focusGainedBorder);
        System.out.println("focus gained: " + focusee);
      }

      @Override
			public void focusLost(final FocusEvent e) {
        borderee.setBorder(focusLostBorder);
      }
    });
  }

  // @Implements WindowListener
  @Override
	public void windowActivated(WindowEvent e) {
    plotManager.canvas.resumeInertia();
  }

  // @Implements WindowListener
  @Override
	public void windowClosed(WindowEvent e) {
    // nop
  }

  // @Implements WindowListener
  @Override
	public void windowClosing(WindowEvent e) {
    // nop
  }

  // @Implements WindowListener
  @Override
	public void windowDeactivated(WindowEvent e) {
    plotManager.canvas.pauseInertia();
  }

  // @Implements WindowListener
  @Override
	public void windowDeiconified(WindowEvent e) {
    plotManager.canvas.resumeInertia();
  }

  // @Implements WindowListener
  @Override
	public void windowIconified(WindowEvent e) {
    plotManager.canvas.pauseInertia();
  }

  // @Implements WindowListener
  @Override
	public void windowOpened(WindowEvent e) {
    // nop
  }

  public void updateEnablement() {
    // This is called by the objects when they change state.
    // TODO: needs to be cleaned up. Use a proper listener
    actionManager.updateEnablement();
    mainMenuBar.inertiaMenuItem.setSelected(plotManager.view.getInertia());
    plotManager.canvas.updateActions();
  }

  private static final long serialVersionUID = 1L;
}
// DIRECTORY MONITOR
// DataSourceManager listens because it needs to monitor the directories of a
// DataSource
// DescriptorManager listens because someone (even this app) may modify the
// Directory xml file
// PLOT MANAGER
// PointsetManager listens because it needs to convert the pointsets to proper
// dimensionality
// Axes listen because dimensionality may change and they need to redraw
// DescriptorManager listens because ...
// DATA SOURCE MANAGER
// PointsetManager listens because it needs to update if pointset changed or
// removed
// (DescriptorManager does NOT listen (directly) because the DescriptorManager
// is listening to files
// and DescriptorManager changes files, which in turn cause the DirMonitor to
// fire)
// DESCRIPTOR MANAGER
// DataSourceManager listens because it needs to recalculate descriptors(?)
// PointsetManager listens because it needs update basis display to user
// DescriptorSelectorViewlet listens because it needs to update descriptors
// display
// BasisSelectorViewlet listens because it needs to update basis display
// OPERATOR MANAGER
// TODO, but very similar to Descriptor Manager
// SPREADSHEET (POINTSET TABLE MANAGER)
// Nothing listens
// POINTSET MANAGER
// PointsetModifyDialog listens because it needs to display the pointsets and
// their selection state
// Spreadsheet listens because it needs to update the tables
