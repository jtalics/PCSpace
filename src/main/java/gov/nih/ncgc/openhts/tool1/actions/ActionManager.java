package gov.nih.ncgc.openhts.tool1.actions;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import gov.nih.ncgc.openhts.tool1.Session;

public class ActionManager {
  private final Session session;
  public final List<Tool1Action> bindedList = new ArrayList<Tool1Action>();
  public final SessionNewAction sessionNewAction;
  public final HelpAboutAction helpAboutAction;
  public final AxesAddAction axesAddAction;
  public final AxesDeleteAction axesDeleteAction;
  public final AxesLegendAction axesLegendAction;
  public final AxesModifyAction axesModifyAction;
  public final AxesResetAction axesResetAction;
  public final CanvasComposeAction canvasComposeAction;
  public final CanvasCycleAction canvasCycleAction;
  public final CanvasDeselectModeAction canvasDeselectModeAction;
  public final CanvasDragNoneAction canvasDragNoneAction;
  public final CanvasDragSelectionAction canvasDragSelectionAction;
  public final CanvasDragTextAction canvasDragTextAction;
  public final CanvasDragTranslateAction canvasDragTranslateAction;
  public final CanvasDragXYRotateAction canvasDragXYRotateAction;
  public final CanvasDragZRotateAction canvasDragZRotateAction;
  public final CanvasModifyAction canvasModifyAction;
  public final CanvasNonOrthogonalAction canvasNonOrthogonalAction;
  public final CanvasOrderAction canvasOrderAction;
  public final CanvasOrthogonalAction canvasOrthogonalAction;
  public final CanvasRefreshAction canvasRefreshAction;
  public final CanvasSelectModeAction canvasSelectModeAction;
  public final CanvasToggleSubsettingAction canvasToggleSubsettingAction;
  public final CanvasUnzoomAction canvasUnzoomAction;
  public final CanvasZoomDragModeAction canvasZoomDragModeAction;
  public final PointsetShowAction pointsetShowAction;
  public final PointsetHideAction pointsetHideAction;
  public final PointsetImportAction pointsetImportAction;
  public final PointsetExportAction pointsetExportAction;
  public final PointsetRemoveAction pointsetRemoveAction;
  public final PointsetModifyAction pointsetModifyAction;
  public final PointsetReplaceAction pointsetReplaceAction;
  public final PointsetRenameAction pointsetRenameAction;
  public final PointsetCreateAction pointsetCreateAction;
  public final FeedbackAction feedbackAction;
  public final HelpAction helpAction;
  public final HelpBubbleAction helpBubbleAction;
  public final HelpDemoAction helpDemoAction;
  public final HelpIndexAction helpIndexAction;
  public final HelpKeyBindingsAction helpKeyBindingsAction;
  public final HelpSearchAction helpSearchAction;
  public final HelpTutorialAction helpTutorialAction;
  public final HelpWhatAction helpWhatAction;
  public final HideUiAction hideUiAction;
  public final PlotterSaveImageAction plotterSaveImageAction;
  public final SessionClearAction sessionClearAction;
  public final SessionEndAction sessionEndAction;
  public final SessionOpenAction sessionOpenAction;
  public final SessionSaveAction sessionSaveAction;
  public final SessionSaveAsAction sessionSaveAsAction;
  public final ShowUiAction showUiAction;
  public final SpreadsheetDeselectAllRowsAction spreadsheetDeselectAllRowsAction;
  public final SpreadsheetDeselectAllRowsUnlockedAction spreadsheetDeselectAllRowsUnlockedAction;
  public final SpreadsheetExportSelectionsAction spreadsheetExportSelectionsAction;
  public final SpreadsheetHideSelectionsAction spreadsheetHideSelectionsAction;
  public final SpreadsheetLockAllTablesAction spreadsheetLockAllTablesAction;
  public final SpreadsheetLockTableAction spreadsheetLockTableAction;
  public final SpreadsheetMaskHiddenAction spreadsheetMaskHiddenAction;
  public final SpreadsheetRevealHiddenAction spreadsheetRevealHiddenAction;
  public final SpreadsheetSelectAllRowsAction spreadsheetSelectAllRowsAction;
  public final SpreadsheetSelectAllRowsUnlockedAction spreadsheetSelectAllRowsUnlockedAction;
  public final SpreadsheetShowSelectionsAction spreadsheetShowSelectionsAction;
  public final SpreadsheetToggleSelectionsAction spreadsheetToggleSelectionsAction;
  public final SpreadsheetToggleUnlockedAction spreadsheetToggleUnlockedAction;
  public final SpreadsheetUnlockAllTablesAction spreadsheetUnlockAllTablesAction;
  public final SpreadsheetUnlockTableAction spreadsheetUnlockTableAction;
  public final SpreadsheetAssignColumnXItemAction spreadsheetAssignColumnXItemAction;
  public final SpreadsheetAssignColumnYItemAction spreadsheetAssignColumnYItemAction;
  public final SpreadsheetAssignColumnZItemAction spreadsheetAssignColumnZItemAction;
  public final SpreadsheetSortAscendingItemAction spreadsheetSortAscendingItemAction;
  public final SpreadsheetSortDescendingItemAction spreadsheetSortDescendingItemAction;
  public final SubjectOriginForwardAction subjectOriginForwardAction;
  public final SubjectOriginBackAction subjectOriginBackAction;
  public final TextAddAction textAddAction;
  public final TextDeleteAction textDeleteAction;
  public final TextModifyAction textModifyAction;
  public final ViewCenterActionAction viewCenterAction;
  public final ViewCopBackAction viewCopBackAction;
  public final ViewCopForwardtAction viewCopForwardtAction;
  public final ViewInertiaAction viewInertiaAction;
  public final ViewMagnifyAction viewMagnifyAction;
  public final ViewModifyAction viewModifyAction;
  public final ViewReduceAction viewReduceAction;
  public final ViewResetAction viewResetAction;
  public final ViewRotateDownAction viewRotateDownAction;
  public final ViewRotateDownCtrlAction viewRotateDownCtrlAction;
  public final ViewRotateDownCtrlShiftAction viewRotateDownCtrlShiftAction;
  public final ViewRotateDownShiftAction viewRotateDownShiftAction;
  public final ViewRotateLeftAction viewRotateLeftAction;
  public final ViewRotateLeftCtrlAction viewRotateLeftCtrlAction;
  public final ViewRotateLeftCtrlShiftAction viewRotateLeftCtrlShiftAction;
  public final ViewRotateLeftShiftAction viewRotateLeftShiftAction;
  public final ViewRotateRightAction viewRotateRightAction;
  public final ViewRotateRightCtrlAction viewRotateRightCtrlAction;
  public final ViewRotateRightCtrlShiftAction viewRotateRightCtrlShiftAction;
  public final ViewRotateRightShiftAction viewRotateRightShiftAction;
  public final ViewRotateUpAction viewRotateUpAction;
  public final ViewRotateUpCtrlAction viewRotateUpCtrlAction;
  public final ViewRotateUpCtrlShiftAction viewRotateUpCtrlShiftAction;
  public final ViewRotateUpShiftAction viewRotateUpShiftAction;
  public final ViewStereoAction viewStereoAction;
  public final SelectionReporterCreateReportAction selectionReporterCreateReportAction;
  public final SelectionReporterCreateDataSourceAction selectionReporterCreateDataSourceAction;
  public final DataSourceRemoveAction dataSourceRemoveAction;
  public final DataSourceCreateAction dataSourceCreateAction;
  public final PlotterBasisModeAction plotterBasisModeAction;
  public final DescriptorInvertTreeAction descriptorInvertTreeAction;
  public final DescriptorRemoveAction descriptorRemoveAction;
//  public final DescriptorSynchronizeAction descriptorSynchronizeAction;
  public final BasisRemoveAction basisRemoveAction;
  public final BasisModifyAction basisModifyAction;
//  public final BasisSynchronizeAction basisSynchronizeAction;
  public final BasisSetPlotterModeAction basisSetPlotterModeAction;
  public final BasisRenameAction basisRenameAction;
  public final DataSourceImportAction dataSourceImportAction;
  public final DataSourceExportAction dataSourceExportAction;
  public final DescriptorModifyAction descriptorModifyAction;
  public final DescriptorImportAction descriptorImportAction;
  public final DescriptorExportAction descriptorExportAction;
  public final BasisImportAction basisImportAction;
  public final BasisExportAction basisExportAction;
  public final BasisCreateAction basisCreateAction;
  public final SelectionViewerAnimationAction selectionViewerAnimationAction;
  public final HeatmapZoomAction heatmapZoomAction;
  public final HeatmapUnzoomAction heatmapUnzoomAction;
  public final HeatmapPanUpAction heatmapPanUpAction;
  public final HeatmapPanDownAction heatmapPanDownAction;
  public final HeatmapPanLeftAction heatmapPanLeftAction;
  public final HeatmapPanRightAction heatmapPanRightAction;
  public final HeatmapModifyAction heatmapModifyAction;

  public ActionManager(final Session session) {
    this.session = session;
    bind(helpAboutAction = new HelpAboutAction(session));
    bind(axesAddAction = new AxesAddAction(session));
    bind(axesDeleteAction = new AxesDeleteAction(session));
    bind(axesLegendAction = new AxesLegendAction(session));
    bind(axesModifyAction = new AxesModifyAction(session));
    bind(axesResetAction = new AxesResetAction(session));
    bind(pointsetImportAction = new PointsetImportAction(session));
    bind(pointsetExportAction = new PointsetExportAction(session));
    bind(pointsetShowAction = new PointsetShowAction(session));
    bind(pointsetHideAction = new PointsetHideAction(session));
    bind(pointsetRemoveAction = new PointsetRemoveAction(session));
    bind(pointsetModifyAction = new PointsetModifyAction(session));
    bind(pointsetReplaceAction = new PointsetReplaceAction(session));
    bind(pointsetRenameAction = new PointsetRenameAction(session));
    bind(feedbackAction = new FeedbackAction(session));
    bind(helpAction = new HelpAction(session));
    bind(helpBubbleAction = new HelpBubbleAction(session));
    bind(helpDemoAction = new HelpDemoAction(session));
    bind(helpIndexAction = new HelpIndexAction(session));
    bind(helpKeyBindingsAction = new HelpKeyBindingsAction(session));
    bind(helpSearchAction = new HelpSearchAction(session));
    bind(helpTutorialAction = new HelpTutorialAction(session));
    bind(helpWhatAction = new HelpWhatAction(session));
    bind(hideUiAction = new HideUiAction(session));
    bind(plotterBasisModeAction = new PlotterBasisModeAction(session));
    bind(plotterSaveImageAction = new PlotterSaveImageAction(session));
    bind(sessionClearAction = new SessionClearAction(session));
    bind(sessionEndAction = new SessionEndAction(session));
    bind(sessionNewAction = new SessionNewAction(session));
    bind(sessionOpenAction = new SessionOpenAction(session));
    bind(sessionSaveAction = new SessionSaveAction(session));
    bind(sessionSaveAsAction = new SessionSaveAsAction(session));
    bind(showUiAction = new ShowUiAction(session));
    bind(spreadsheetDeselectAllRowsAction = new SpreadsheetDeselectAllRowsAction(
        session));
    bind(spreadsheetDeselectAllRowsUnlockedAction = new SpreadsheetDeselectAllRowsUnlockedAction(
        session));
    bind(spreadsheetExportSelectionsAction = new SpreadsheetExportSelectionsAction(
        session));
    bind(spreadsheetHideSelectionsAction = new SpreadsheetHideSelectionsAction(
        session));
    bind(spreadsheetLockAllTablesAction = new SpreadsheetLockAllTablesAction(
        session));
    bind(spreadsheetLockTableAction = new SpreadsheetLockTableAction(session));
    bind(spreadsheetMaskHiddenAction = new SpreadsheetMaskHiddenAction(session));
    bind(spreadsheetRevealHiddenAction = new SpreadsheetRevealHiddenAction(
        session));
    bind(spreadsheetSelectAllRowsAction = new SpreadsheetSelectAllRowsAction(
        session));
    bind(spreadsheetSelectAllRowsUnlockedAction = new SpreadsheetSelectAllRowsUnlockedAction(
        session));
    bind(spreadsheetShowSelectionsAction = new SpreadsheetShowSelectionsAction(
        session));
    bind(spreadsheetToggleSelectionsAction = new SpreadsheetToggleSelectionsAction(
        session));
    bind(spreadsheetToggleUnlockedAction = new SpreadsheetToggleUnlockedAction(
        session));
    bind(spreadsheetUnlockAllTablesAction = new SpreadsheetUnlockAllTablesAction(
        session));
    bind(spreadsheetUnlockTableAction = new SpreadsheetUnlockTableAction(
        session));
    bind(subjectOriginForwardAction = new SubjectOriginForwardAction(session));
    bind(subjectOriginBackAction = new SubjectOriginBackAction(session));
    bind(spreadsheetAssignColumnXItemAction = new SpreadsheetAssignColumnXItemAction(
        session));
    bind(spreadsheetAssignColumnYItemAction = new SpreadsheetAssignColumnYItemAction(
        session));
    bind(spreadsheetAssignColumnZItemAction = new SpreadsheetAssignColumnZItemAction(
        session));
    bind(spreadsheetSortAscendingItemAction = new SpreadsheetSortAscendingItemAction(
        session));
    bind(spreadsheetSortDescendingItemAction = new SpreadsheetSortDescendingItemAction(
        session));
    bind(textAddAction = new TextAddAction(session));
    bind(textDeleteAction = new TextDeleteAction(session));
    bind(textModifyAction = new TextModifyAction(session));
    bind(selectionReporterCreateReportAction = new SelectionReporterCreateReportAction(
        session));
    bind(selectionReporterCreateDataSourceAction = new SelectionReporterCreateDataSourceAction(
        session));
    bind(dataSourceCreateAction = new DataSourceCreateAction(session));
    bind(pointsetCreateAction = new PointsetCreateAction(
        session));
    bind(dataSourceImportAction = new DataSourceImportAction(session));
    bind(dataSourceExportAction = new DataSourceExportAction(session));
    bind(dataSourceRemoveAction = new DataSourceRemoveAction(session));
    bind(basisRemoveAction = new BasisRemoveAction(session));
    bind(basisSetPlotterModeAction = new BasisSetPlotterModeAction(session));
    bind(descriptorInvertTreeAction = new DescriptorInvertTreeAction(session));
    bind(descriptorModifyAction = new DescriptorModifyAction(session));
    bind(descriptorImportAction = new DescriptorImportAction(session));
    bind(descriptorExportAction = new DescriptorExportAction(session));
    bind(descriptorRemoveAction = new DescriptorRemoveAction(session));
//    bind(descriptorSynchronizeAction = new DescriptorSynchronizeAction(session));
    bind(basisCreateAction = new BasisCreateAction(session));
    bind(basisModifyAction = new BasisModifyAction(session));
//    bind(basisSynchronizeAction = new BasisSynchronizeAction(session));
    bind(basisImportAction = new BasisImportAction(session));
    bind(basisExportAction = new BasisExportAction(session));
    bind(basisRenameAction = new BasisRenameAction(session));
    bind(selectionViewerAnimationAction = new SelectionViewerAnimationAction(session));

    viewCenterAction = new ViewCenterActionAction(session);
    viewCopBackAction = new ViewCopBackAction(session);
    viewCopForwardtAction = new ViewCopForwardtAction(session);
    viewInertiaAction = new ViewInertiaAction(session);
    viewMagnifyAction = new ViewMagnifyAction(session);
    viewModifyAction = new ViewModifyAction(session);
    viewReduceAction = new ViewReduceAction(session);
    viewResetAction = new ViewResetAction(session);
    viewRotateDownAction = new ViewRotateDownAction(session);
    viewRotateDownCtrlAction = new ViewRotateDownCtrlAction(session);
    viewRotateDownCtrlShiftAction = new ViewRotateDownCtrlShiftAction(session);
    viewRotateDownShiftAction = new ViewRotateDownShiftAction(session);
    viewRotateLeftAction = new ViewRotateLeftAction(session);
    viewRotateLeftCtrlAction = new ViewRotateLeftCtrlAction(session);
    viewRotateLeftCtrlShiftAction = new ViewRotateLeftCtrlShiftAction(session);
    viewRotateLeftShiftAction = new ViewRotateLeftShiftAction(session);
    viewRotateRightAction = new ViewRotateRightAction(session);
    viewRotateRightCtrlAction = new ViewRotateRightCtrlAction(session);
    viewRotateRightCtrlShiftAction = new ViewRotateRightCtrlShiftAction(session);
    viewRotateRightShiftAction = new ViewRotateRightShiftAction(session);
    viewRotateUpAction = new ViewRotateUpAction(session);
    viewRotateUpCtrlAction = new ViewRotateUpCtrlAction(session);
    viewRotateUpCtrlShiftAction = new ViewRotateUpCtrlShiftAction(session);
    viewRotateUpShiftAction = new ViewRotateUpShiftAction(session);
    viewStereoAction = new ViewStereoAction(session);
    canvasComposeAction = new CanvasComposeAction(session);
    canvasCycleAction = new CanvasCycleAction(session);
    canvasDeselectModeAction = new CanvasDeselectModeAction(session);
    canvasDragNoneAction = new CanvasDragNoneAction(session);
    canvasDragSelectionAction = new CanvasDragSelectionAction(session);
    canvasDragTextAction = new CanvasDragTextAction(session);
    canvasDragTranslateAction = new CanvasDragTranslateAction(session);
    canvasDragXYRotateAction = new CanvasDragXYRotateAction(session);
    canvasDragZRotateAction = new CanvasDragZRotateAction(session);
    canvasModifyAction = new CanvasModifyAction(session);
    canvasNonOrthogonalAction = new CanvasNonOrthogonalAction(session);
    canvasOrderAction = new CanvasOrderAction(session);
    canvasOrthogonalAction = new CanvasOrthogonalAction(session);
    canvasRefreshAction = new CanvasRefreshAction(session);
    canvasSelectModeAction = new CanvasSelectModeAction(session);
    canvasToggleSubsettingAction = new CanvasToggleSubsettingAction(session);
    canvasUnzoomAction = new CanvasUnzoomAction(session);
    canvasZoomDragModeAction = new CanvasZoomDragModeAction(session);
    heatmapZoomAction = new HeatmapZoomAction(session);
    heatmapUnzoomAction = new HeatmapUnzoomAction(session);
    heatmapPanUpAction = new HeatmapPanUpAction(session);
    heatmapPanDownAction = new HeatmapPanDownAction(session);
    heatmapPanRightAction = new HeatmapPanRightAction(session);
    heatmapPanLeftAction = new HeatmapPanLeftAction(session);
    heatmapModifyAction = new HeatmapModifyAction(session);
  }

  private void bind(final Tool1Action action) {
    bindedList.add(action);
    final JComponent c = session.frame.getRootPane();
    c.registerKeyboardAction(action, (KeyStroke) action
        .getValue(Action.ACCELERATOR_KEY), JComponent.WHEN_IN_FOCUSED_WINDOW);
    // TODO: why doesn't following work for arrows?
    // c.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put((KeyStroke)action.getValue(Action.ACCELERATOR_KEY),
    // action.getClass().getName());
    // c.getActionMap().put(action.getClass().getName(), action);
  }

  public void bindHeatmapKeys() {
    bindHeatmap(heatmapZoomAction);
    bindHeatmap(heatmapUnzoomAction);
    bindHeatmap(heatmapPanUpAction);
    bindHeatmap(heatmapPanDownAction);
    bindHeatmap(heatmapPanLeftAction);
    bindHeatmap(heatmapPanRightAction);
  }
  
  public void bindCanvasKeys() {
    bindCanvas(viewCenterAction);
    bindCanvas(viewCopBackAction);
    bindCanvas(viewCopForwardtAction);
    bindCanvas(viewInertiaAction);
    bindCanvas(viewMagnifyAction);
    bindCanvas(viewModifyAction);
    bindCanvas(viewReduceAction);
    bindCanvas(viewResetAction);
    bindCanvas(viewRotateDownAction);
    bindCanvas(viewRotateDownCtrlAction);
    bindCanvas(viewRotateDownCtrlShiftAction);
    bindCanvas(viewRotateDownShiftAction);
    bindCanvas(viewRotateLeftAction);
    bindCanvas(viewRotateLeftCtrlAction);
    bindCanvas(viewRotateLeftCtrlShiftAction);
    bindCanvas(viewRotateLeftShiftAction);
    bindCanvas(viewRotateRightAction);
    bindCanvas(viewRotateRightCtrlAction);
    bindCanvas(viewRotateRightCtrlShiftAction);
    bindCanvas(viewRotateRightShiftAction);
    bindCanvas(viewRotateUpAction);
    bindCanvas(viewRotateUpCtrlAction);
    bindCanvas(viewRotateUpCtrlShiftAction);
    bindCanvas(viewRotateUpShiftAction);
    bindCanvas(viewStereoAction);
    bindCanvas(canvasComposeAction);
    bindCanvas(canvasCycleAction);
    bindCanvas(canvasDeselectModeAction);
    bindCanvas(canvasDragNoneAction);
    bindCanvas(canvasDragSelectionAction);
    bindCanvas(canvasDragTextAction);
    bindCanvas(canvasDragTranslateAction);
    bindCanvas(canvasDragXYRotateAction);
    bindCanvas(canvasDragZRotateAction);
    bindCanvas(canvasModifyAction);
    bindCanvas(canvasNonOrthogonalAction);
    bindCanvas(canvasOrderAction);
    bindCanvas(canvasOrthogonalAction);
    bindCanvas(canvasRefreshAction);
    bindCanvas(canvasSelectModeAction);
    bindCanvas(canvasToggleSubsettingAction);
    bindCanvas(canvasUnzoomAction);
    bindCanvas(canvasZoomDragModeAction);
  }

  private void bindCanvas(final Tool1Action action) {
    bindedList.add(action);
    final JComponent c = session.plotManager.canvas;
    // c.registerKeyboardAction(action,
    // (KeyStroke)action.getValue(Action.ACCELERATOR_KEY),
    // JComponent.WHEN_IN_FOCUSED_WINDOW);
    c.getInputMap(JComponent.WHEN_FOCUSED).put(
        (KeyStroke) action.getValue(Action.ACCELERATOR_KEY),
        action.getClass().getName());
    c.getActionMap().put(action.getClass().getName(), action);
  }
  
  private void bindHeatmap(final Tool1Action action) {
    bindedList.add(action);
    final JComponent c = session.heatmap.getCanvas();
    // c.registerKeyboardAction(action,
    // (KeyStroke)action.getValue(Action.ACCELERATOR_KEY),
    // JComponent.WHEN_IN_FOCUSED_WINDOW);
    c.getInputMap(JComponent.WHEN_FOCUSED).put(
        (KeyStroke) action.getValue(Action.ACCELERATOR_KEY),
        action.getClass().getName());
    c.getActionMap().put(action.getClass().getName(), action);
  }
  
  public void updateEnablement() {
    // This method enables or disables the user actions depending on the state
    // of the app entities.
    if (session.pointsetManager != null) {
      if (session.pointsetManager.pointsetCount() > 0) {
        session.actionManager.pointsetModifyAction.setEnabled(true);
        // actionManager. tableDataItem.setEnabled(true);
        session.actionManager.pointsetRemoveAction.setEnabled(true);
        session.actionManager.pointsetReplaceAction.setEnabled(true);
      }
      else {
        session.actionManager.pointsetModifyAction.setEnabled(false);
        session.actionManager.pointsetRemoveAction.setEnabled(false);
        // actionManager.tableDataItem.setEnabled(false);
        session.actionManager.pointsetReplaceAction.setEnabled(false);
      }
      if (session.pointsetManager.pointsetCount() > 1) {
        session.actionManager.canvasOrderAction.setEnabled(true);
      }
      else {
        session.actionManager.canvasOrderAction.setEnabled(false);
      }
    }
    // 
    if (session.plotManager != null) {
      if (session.plotManager.subject.isEmpty()) {
        session.actionManager.sessionClearAction.setEnabled(false);
        session.actionManager.sessionSaveAction.setEnabled(false);
        session.actionManager.sessionSaveAsAction.setEnabled(false);
        plotterSaveImageAction.setEnabled(false);
      }
      else {
        sessionClearAction.setEnabled(true);
        if (!session.saved) {
          sessionSaveAction.setEnabled(true);
        }
        sessionSaveAsAction.setEnabled(true);
        plotterSaveImageAction.setEnabled(true);
      }
      if (session.plotManager.subject.axes != null) {
        axesModifyAction.setEnabled(true);
        axesResetAction.setEnabled(true);
        textAddAction.setEnabled(true);
        axesAddAction.setEnabled(false);
        axesDeleteAction.setEnabled(true);
        pointsetImportAction.setEnabled(true);
        axesLegendAction.setEnabled(true);
      }
      else {
        axesModifyAction.setEnabled(false);
        axesResetAction.setEnabled(false);
        textAddAction.setEnabled(false);
        axesAddAction.setEnabled(true);
        axesDeleteAction.setEnabled(false);
        pointsetImportAction.setEnabled(false);
        axesLegendAction.setEnabled(false);
      }
      if (session.plotManager.textCount() > 0) {
        textModifyAction.setEnabled(true);
        textDeleteAction.setEnabled(true);
      }
      else {
        textModifyAction.setEnabled(false);
        textDeleteAction.setEnabled(false);
      }
      if (session.plotManager.subject.axes != null) {
        axesAddAction.setEnabled(false);
        axesDeleteAction.setEnabled(true);
        if (session.plotManager.getDim() == 3) {
          viewInertiaAction.setEnabled(true);
          viewStereoAction.setEnabled(true);
        }
      }
      else {
        axesAddAction.setEnabled(true);
        axesDeleteAction.setEnabled(false);
        viewInertiaAction.setEnabled(false);
        viewStereoAction.setEnabled(false);
      }
    }
  }


}
