package gov.nih.ncgc.openhts.tool1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import com.jidesoft.swing.JideMenu;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class MainMenuBar extends JMenuBar {
  private final JideMenu sessionMenu = new JideMenu("Session");
  private final JideMenu dataSourceMenu = new JideMenu("DataSource");
  private final JideMenu pointsetMenu = new JideMenu("Pointset");
  private final JideMenu plotterMenu = new JideMenu("Plotter");
  private final JideMenu descriptorMenu = new JideMenu("Descriptor");
  private final JideMenu basisMenu = new JideMenu("Basis");
  private final JideMenu heatmapMenu = new JideMenu("Heatmap");
  private final JideMenu helpMenu = new JideMenu("Help");
  private JideMenu plafMenu = new JideMenu("Plaf");
  private final JideMenu axesMenu = new JideMenu("Axes");
  private final JideMenu textMenu = new JideMenu("Text");
  private final JideMenu canvasMenu = new JideMenu("Canvas");
  private final JideMenu viewMenu = new JideMenu("View");
  private final Session session;
  final JCheckBoxMenuItem inertiaMenuItem;
  public final JCheckBoxMenuItem helpBubbleMenuItem;
  public final JCheckBoxMenuItem checkItem;
  
  public MainMenuBar(final Session session) {
    this.session = session;
    add(sessionMenu);
    //sessionMenu.setPreferredPopupHorizontalAlignment(JideMenu.RIGHT);
    sessionMenu.setMnemonic('S');
    sessionMenu.add(new JSeparator());
    //
    sessionMenu.add(new JMenuItem(session.actionManager.sessionOpenAction));
    sessionMenu.add(new JMenuItem(session.actionManager.sessionSaveAction));
    // CSH.setHelpIDString(saveItem, "save_file_menu");
    sessionMenu.add(new JMenuItem(session.actionManager.sessionSaveAsAction));
    // CSH.setHelpIDString(saveAsItem, "save_as_file_menu");
    sessionMenu.add(new JMenuItem(session.actionManager.sessionNewAction));
    // CSH.setHelpIDString(newItem, "new_file_menu");
    sessionMenu.add(createPlafMenu());
    sessionMenu.add(new JMenuItem(session.actionManager.sessionClearAction));
    sessionMenu.add(new JMenuItem(session.actionManager.sessionEndAction));
    ////////////////
    add(dataSourceMenu);
    dataSourceMenu.setMnemonic('D');
    dataSourceMenu.add(new JMenuItem(session.actionManager.dataSourceCreateAction));
    dataSourceMenu.add(new JMenuItem(session.actionManager.dataSourceImportAction));
    dataSourceMenu.add(new JMenuItem(session.actionManager.dataSourceExportAction));
    dataSourceMenu.add(new JMenuItem(session.actionManager.dataSourceRemoveAction));

    // //////////////////////////
    add(pointsetMenu);
    pointsetMenu.setMnemonic('P');
    pointsetMenu.add(new JSeparator());
    pointsetMenu.add(new JMenuItem(session.actionManager.pointsetCreateAction));
    pointsetMenu.add(new JMenuItem(session.actionManager.pointsetImportAction));
    pointsetMenu.add(new JMenuItem(session.actionManager.pointsetExportAction));
    pointsetMenu.add(new JMenuItem(session.actionManager.pointsetModifyAction));
    // TODO: Do we really need this:?
    pointsetMenu.add(new JMenuItem(session.actionManager.pointsetReplaceAction));
    pointsetMenu.add(new JMenuItem(session.actionManager.pointsetRenameAction));
    pointsetMenu.add(new JMenuItem(session.actionManager.pointsetRemoveAction));

    // /////////////////////////
    add(plotterMenu);
    plotterMenu.setMnemonic('L');
    checkItem = new JCheckBoxMenuItem(session.actionManager.plotterBasisModeAction);
    plotterMenu.add(checkItem);
    plotterMenu.add(axesMenu);
    axesMenu.setMnemonic('A');
    axesMenu.add(session.actionManager.axesAddAction);
    axesMenu.add(session.actionManager.axesModifyAction);
    axesMenu.add(session.actionManager.axesResetAction);
    axesMenu.add(session.actionManager.axesDeleteAction);
    axesMenu.add(session.actionManager.axesLegendAction);

    ///////////////
    plotterMenu.add(canvasMenu);
    canvasMenu.setToolTipText("options about how to draw your items");
    canvasMenu.setMnemonic('C');
    canvasMenu.add(new JMenuItem(session.actionManager.canvasModifyAction));
    canvasMenu.add(new JMenuItem(session.actionManager.canvasOrderAction));
    canvasMenu.add(new JMenuItem(session.actionManager.canvasRefreshAction));
    ////////////////
    plotterMenu.add(viewMenu);
    viewMenu.setToolTipText("options about your point of view");
    viewMenu.setMnemonic('V');
    viewMenu.add(new JMenuItem(session.actionManager.viewModifyAction));
    viewMenu.add(new JMenuItem(session.actionManager.viewResetAction));
    viewMenu.add(inertiaMenuItem = new JCheckBoxMenuItem(session.actionManager.viewInertiaAction));
    viewMenu.add(new JMenuItem(session.actionManager.viewStereoAction));
    viewMenu.add(new JSeparator());
////////////////
    plotterMenu.add(textMenu);
    textMenu.setToolTipText("options about text drawn on canvas");
    textMenu.setMnemonic('T');
    textMenu.add(new JMenuItem(session.actionManager.textAddAction));
    textMenu.add(new JMenuItem(session.actionManager.textModifyAction));
    textMenu.add(new JMenuItem(session.actionManager.textDeleteAction));
    plotterMenu.add(new JMenuItem(session.actionManager.plotterSaveImageAction));
/////////////////////
    add(descriptorMenu);
    descriptorMenu.setMnemonic('E');
//    descriptorMenu.add(new JMenuItem(session.actionManager.descriptorSynchronizeAction));
    descriptorMenu.add(new JMenuItem(session.actionManager.descriptorImportAction));
    descriptorMenu.add(new JMenuItem(session.actionManager.descriptorExportAction));
    descriptorMenu.add(new JMenuItem(session.actionManager.descriptorModifyAction));
    descriptorMenu.add(new JMenuItem(session.actionManager.descriptorRemoveAction));
/////////////////////
    add(basisMenu);
    basisMenu.setMnemonic('B');
    basisMenu.add(new JMenuItem(session.actionManager.basisCreateAction));
    basisMenu.add(new JMenuItem(session.actionManager.basisImportAction));
    basisMenu.add(new JMenuItem(session.actionManager.basisExportAction));
    basisMenu.add(new JMenuItem(session.actionManager.basisModifyAction));
    basisMenu.add(new JMenuItem(session.actionManager.basisRemoveAction));
    basisMenu.add(new JMenuItem(session.actionManager.basisRenameAction));
///////////////////////
    add(heatmapMenu);
    heatmapMenu.setMnemonic('H');
    heatmapMenu.add(new JMenuItem(session.actionManager.heatmapModifyAction));
///////////////
    add(helpMenu);
    helpMenu.setMnemonic('L');
    helpMenu.setToolTipText("options for getting help");
    helpMenu.add(new JMenuItem(session.actionManager.helpAction));
    helpMenu.add(new JMenuItem(session.actionManager.helpKeyBindingsAction));
    helpMenu.add(new JMenuItem(session.actionManager.helpDemoAction));
    helpMenu.add(new JMenuItem(session.actionManager.helpAboutAction));
    helpMenu.add(new JMenuItem(session.actionManager.feedbackAction));
    helpMenu.add(new JMenuItem(session.actionManager.helpIndexAction));
    //
    helpMenu.add(new JMenuItem(session.actionManager.helpSearchAction));
    helpMenu.add(new JMenuItem(session.actionManager.helpTutorialAction));
    helpMenu.add(new JMenuItem(session.actionManager.helpWhatAction));
    helpBubbleMenuItem = new JCheckBoxMenuItem(session.actionManager.helpBubbleAction);
    helpBubbleMenuItem.setSelected(ToolTipManager.sharedInstance().isEnabled());
    helpMenu.add(helpBubbleMenuItem);
  }

  /**
   * Make menu for pluggable look and feel.
   */
  public JideMenu createPlafMenu() {

    plafMenu = new JideMenu("Look & Feel");
    plafMenu.setToolTipText("plafMenu");
    final ButtonGroup radiogroup = new ButtonGroup();
    final UIManager.LookAndFeelInfo[] plafs = UIManager.getInstalledLookAndFeels();
    for (final LookAndFeelInfo element : plafs) {
      final String plafName = element.getName();
      final String plafClassName = element.getClassName();
      final String currentPLAF = UIManager.getLookAndFeel().getName();
      final JMenuItem item = plafMenu.add(new JRadioButtonMenuItem(plafName));
      item.setToolTipText(plafClassName);
      item.addActionListener(
        new ActionListener() {
        @Override
				public void actionPerformed(final ActionEvent e) {
          try {
            UIManager.setLookAndFeel(plafClassName);
            SwingUtilities.updateComponentTreeUI(session.frame);
            GlobalSettings.getInstance().putProperty(Main.LOOK_AND_FEEL,
                plafClassName);
          }
          catch (final Throwable ex) {
            session.errorSupport("While changing look & feel:", ex, "");
          }
        }
      }
      );
      if (currentPLAF.equals(plafName)) {
        item.setSelected(true);
      }
      radiogroup.add(item);
    }

    return plafMenu;
  }


  private static final long serialVersionUID = 1L;
}
