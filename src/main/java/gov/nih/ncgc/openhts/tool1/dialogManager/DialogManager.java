package gov.nih.ncgc.openhts.tool1.dialogManager;

import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JDialog;
import javax.swing.JFrame;
import gov.nih.ncgc.openhts.tool1.LogHistoryDialog;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.descriptorManager.BasisSpecifyDialog;
import gov.nih.ncgc.openhts.tool1.descriptorManager.BasisModifyDialog;
import gov.nih.ncgc.openhts.tool1.descriptorManager.BasisRenameDialog;
import gov.nih.ncgc.openhts.tool1.descriptorManager.BasisSelectorDialog;
import gov.nih.ncgc.openhts.tool1.descriptorManager.RegisteredDescriptorSelectDialog;
import gov.nih.ncgc.openhts.tool1.heatmap.HeatmapModifyDialog;
import gov.nih.ncgc.openhts.tool1.plotManager.AxesLegendDialog;
import gov.nih.ncgc.openhts.tool1.plotManager.AxesModifyDialog;
import gov.nih.ncgc.openhts.tool1.plotManager.CanvasModifyDialog;
import gov.nih.ncgc.openhts.tool1.plotManager.CanvasOrderDialog;
import gov.nih.ncgc.openhts.tool1.plotManager.FontChooserDialog;
import gov.nih.ncgc.openhts.tool1.plotManager.Text;
import gov.nih.ncgc.openhts.tool1.plotManager.TextAddDialog;
import gov.nih.ncgc.openhts.tool1.plotManager.TextChooseDialog;
import gov.nih.ncgc.openhts.tool1.plotManager.TextModifyDialog;
import gov.nih.ncgc.openhts.tool1.plotManager.ViewModifyDialog;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetModifyDialog;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetRenameDialog;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetSelectorDialog;

/**
 * Purpose is to create dialogs, distribute refrences, and remember where the
 * user located the dialog on the screen. Client code is responsible to set the
 * properties of the dialog before making it visible. All dialogs are reused.
 * 
 * @author talafousj
 */
public class DialogManager implements ComponentListener, WindowListener {
  private static DialogManager self;
  private final Session session;
  private ColorChooserDialog colorChooserDialog;
  private AxesLegendDialog axesLegendDialog;
  private HelpKeyBindingsDialog helpKeyBindingsDialog;
  private CanvasModifyDialog canvasModifyDialog;
  private BasisSpecifyDialog basisSpecifyDialog;
  private BasisModifyDialog basisModifyDialog;
  private PointsetModifyDialog pointsetModifyDialog;
  private AxesModifyDialog axesModifyDialog;
  private TextAddDialog textAddDialog;
  private TextModifyDialog textModifyDialog;
  private ViewModifyDialog viewModifyDialog;
  private PointsetSelectorDialog datasetChooseDialog;
  private CanvasOrderDialog canvasOrderDialog;
  private FontChooserDialog fontChooserDialog;
  private TextChooseDialog textChooseDialog;
  private RegisteredDescriptorSelectDialog registeredDescriptorSelectDialog;
  private PointsetRenameDialog pointsetRenameDialog;
  private BasisRenameDialog basisRenameDialog;
  private BasisSelectorDialog basisSelectorDialog;
  public Point textChooseLocation;
  public Point canvasModifyLocation;
  public Point axesModifyLocation;
  public Point textAddLocation;
  public Point textModifyLocation;
  public Point pointsetModifyLocation;
  public Point viewModifyLocation;
  public Point datasetChooseLocation;
  public Point canvasOrderLocation;
  public Point colorChooserModifyLocation;
  public Point fontChooserChooseLocation;
  public Point axesLegendLocation;
  public Point axes;
  public Point canvas;
  public Point canvasHelpLocation;
  private LogHistoryDialog logHistoryDialog;
  private HeatmapModifyDialog heatmapModifyDialog;

  private DialogManager(final Session session) {
    this.session = session;
  }

  public static DialogManager getInstance(final Session session) {
    if (self == null) {
      self = new DialogManager(session);
    }
    return self;
  }

  @Override
	public void componentHidden(final ComponentEvent ev) {
    // nop
  }

  @Override
	public void componentShown(final ComponentEvent ev) {
    if (ev.getSource() == session.plotManager.canvas) {
      session.plotManager.canvas.refresh();
    }
  }

  @Override
	public void componentMoved(final ComponentEvent e) {
    rememberDialogLocation(e);
  }

  @Override
	public void componentResized(final ComponentEvent e) {
    rememberDialogLocation(e);
  }

  @Override
	public void windowActivated(final WindowEvent e) {
    // nop
  }

  @Override
	public void windowClosed(final WindowEvent e) {
    // nop
  }

  @Override
	public void windowClosing(final WindowEvent e) {
    final Object obj = e.getSource();
    if (!(obj instanceof JDialog || obj instanceof JFrame)) {
      throw new RuntimeException("");
    }
    else {
      ((Window) obj).dispose();
    }
  }

  @Override
	public void windowDeactivated(final WindowEvent e) {
    // nop
  }

  @Override
	public void windowDeiconified(final WindowEvent e) {
    // nop
  }

  @Override
	public void windowIconified(final WindowEvent e) {
    // nop
  }

  @Override
	public void windowOpened(final WindowEvent e) {
    // nop
  }

  private void rememberDialogLocation(final ComponentEvent ev) {
    final Object obj = ev.getSource();
    if (!(obj instanceof JDialog || obj instanceof JFrame)) {
      throw new RuntimeException("");
    }
  }

  public BasisSpecifyDialog getBasisSpecifyDialog() {
    if (basisSpecifyDialog == null) {
      basisSpecifyDialog = new BasisSpecifyDialog(session);
    }
    return basisSpecifyDialog;
  }

  public BasisModifyDialog getBasisModifyDialog() {
    if (basisModifyDialog == null) {
      basisModifyDialog = new BasisModifyDialog(session);
    }
    return basisModifyDialog;
  }

  public AxesLegendDialog getAxesLegendDialog() {
    if (axesLegendDialog == null) {
      axesLegendDialog = new AxesLegendDialog(session);
    }
    return axesLegendDialog;
  }

  public AxesModifyDialog getAxesModifyDialog() {
    if (axesModifyDialog == null) {
      axesModifyDialog = new AxesModifyDialog(session);
    }
    return axesModifyDialog;
  }

  public HelpKeyBindingsDialog getCanvasHelpDialog() {
    if (helpKeyBindingsDialog == null) {
      helpKeyBindingsDialog = new HelpKeyBindingsDialog(session);
    }
    return helpKeyBindingsDialog;
  }

  public CanvasModifyDialog getCanvasModifyDialog() {
    if (canvasModifyDialog == null) {
      canvasModifyDialog = new CanvasModifyDialog(session);
    }
    return canvasModifyDialog;
  }

  public CanvasOrderDialog getCanvasOrderDialog() {
    if (canvasOrderDialog == null) {
      canvasOrderDialog = new CanvasOrderDialog(session);
    }
    return canvasOrderDialog;
  }

  public ColorChooserDialog getColorChooserModifyDialog() {
    if (colorChooserDialog == null) {
      colorChooserDialog = new ColorChooserDialog(session);
    }
    return colorChooserDialog;
  }

  public PointsetSelectorDialog getPointsetChooseDialog(final int mode) {
    if (datasetChooseDialog == null) {
      datasetChooseDialog = new PointsetSelectorDialog(session, mode);
    }
    return datasetChooseDialog;
  }

  public PointsetModifyDialog getPointsetModifyDialog() {
    return PointsetModifyDialog.getInstance(session);
  }

  public FontChooserDialog getFontChooserChooseDialog() {
    if (fontChooserDialog == null) {
      fontChooserDialog = new FontChooserDialog(session);
    }
    return fontChooserDialog;
  }

  public ColorChooserDialog colorChooserDialog() {
    if (colorChooserDialog == null) {
      colorChooserDialog = new ColorChooserDialog(session);
    }
    return colorChooserDialog;
  }

  public TextAddDialog getTextAddDialog() {
    if (textAddDialog == null) {
      textAddDialog = new TextAddDialog(session);
    }
    return null;
  }

  public TextChooseDialog getTextChooseDialog() {
    if (textChooseDialog == null) {
      textChooseDialog = new TextChooseDialog(session);
    }
    return textChooseDialog;
  }

  public TextModifyDialog getTextModifyDialog() {
    if (textModifyDialog == null) {
      textModifyDialog = new TextModifyDialog(session);
    }
    return textModifyDialog;
  }

  public ViewModifyDialog getViewModifyDialog() {
    if (viewModifyDialog == null) {
      viewModifyDialog = new ViewModifyDialog(session);
    }
    return viewModifyDialog;
  }

  public DataSourceNewDialog getDataSourceNewDialog() {
    // TODO: figure out how to do non-modal in a pattern
    return null;
  }

  public RegisteredDescriptorSelectDialog getDescriptorsSelectDialog() {
    if (registeredDescriptorSelectDialog == null) {
      registeredDescriptorSelectDialog = new RegisteredDescriptorSelectDialog(session);
    }
    return registeredDescriptorSelectDialog;
  }

  public void hideDatasetDialogs() {
    // TODO Auto-generated method stub
  }

  public void hideViewDialogs() {
    // TODO Auto-generated method stub
  }

  public void hideCanvasDialogs() {
    // TODO Auto-generated method stub
  }

  public void hideAxesDialogs() {
    // TODO Auto-generated method stub
  }

  /**
   *
   */
  void hideAllDialogs() {
    int i;
    if (!session.plotManager.subject.isEmpty()) {
//      session.spreadsheet.disposeTableFrame();
      for (i = 0; i < session.pointsetManager.pointsetCount(); i++) {
        final Pointset pointset = session.pointsetManager.getPointsetAt(i);
        session.dialogManager.hideDatasetDialogs();
      }
      for (i = 0; i < session.plotManager.textCount(); i++) {
        final Text text = session.plotManager.getText(i);
        // text.disposeDialogs();
      }
      if (session.plotManager.subject.axes != null) {
        session.dialogManager.hideAxesDialogs();
      }
      session.dialogManager.hideViewDialogs();
      session.dialogManager.hideCanvasDialogs();
    }
  }

  public void hideTextDialogs() {
    // TODO Auto-generated method stub
  }

  public PointsetRenameDialog getPointsetRenameDialog() {
    if (pointsetRenameDialog == null) {
      pointsetRenameDialog = new PointsetRenameDialog(session);
    }
    return pointsetRenameDialog;
  }

  public BasisSelectorDialog getBasisSelectorDialog() {
    if (basisSelectorDialog == null) {
      basisSelectorDialog = new BasisSelectorDialog(session);
    }
    return basisSelectorDialog;
  }

  public BasisRenameDialog getBasisRenameDialog() {
    if (basisRenameDialog == null) {
      basisRenameDialog = new BasisRenameDialog(session);
    }
    return basisRenameDialog;
  }

  public LogHistoryDialog getLogHistoryDialog() {
    if (logHistoryDialog == null) {
      logHistoryDialog = new LogHistoryDialog(session);
    }
    return logHistoryDialog;
  }

  public HeatmapModifyDialog getHeatmapModifyDialog() {
    if (heatmapModifyDialog == null) {
      heatmapModifyDialog = new HeatmapModifyDialog(session);
    }
    return heatmapModifyDialog;
  }
}
// end of DialogManager.java
