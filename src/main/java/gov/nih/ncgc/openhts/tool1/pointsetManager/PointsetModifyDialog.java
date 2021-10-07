package gov.nih.ncgc.openhts.tool1.pointsetManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;
import gov.nih.ncgc.openhts.tool1.dialogManager.DialogManager;
import gov.nih.ncgc.openhts.tool1.plotManager.Axes;
import gov.nih.ncgc.openhts.tool1.util.colorizer.Colorizer;

public final class PointsetModifyDialog extends AbstractDialog implements
    PointsetManagerListener, ChangeListener {
  static class PointsetListModel extends DefaultListModel {
    public PointsetListModel(Pointset[] pointsets) {
      super();
      for (int i = 0; i < pointsets.length; i++) {
        this.ensureCapacity(pointsets.length);
        this.addElement(pointsets[i]);
      }
    }

    @Override
    public void fireContentsChanged(Object object, int index1, int index2) {
      super.fireContentsChanged(object, index1, index2);
    }

    private static final long serialVersionUID = 1L;
  }

  private static PointsetModifyDialog self;
  private JList pointsetList;
  private final Session session;
  private final PointsetViewlet pointsetViewlet;
  private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
  private Pointset selectedPointset;
  private final Colorizer colorizer;
  private PointsetListModel pointsetListModel;

  private PointsetModifyDialog(final Session session) {
    super(session, "Pointset - Modify", false);
    this.session = session;
    if (0 == session.pointsetManager.getPointsetCount()) {
      throw new RuntimeException(
          "Don't call this dialog if there are no Pointsets");
    }
    setModal(false);
    final JPanel topPanel = new JPanel();
    topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
    add(topPanel, BorderLayout.CENTER);
    add(getButtonPanel(), BorderLayout.SOUTH);
    addWindowListener(DialogManager.getInstance(session));
    addComponentListener(DialogManager.getInstance(session));
    final JPanel mainPanel = new JPanel();
    topPanel.add(mainPanel);
    mainPanel.setLayout(new BorderLayout());
    mainPanel.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.WEST);
    mainPanel.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.EAST);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.NORTH);
    final JPanel northPanel = new JPanel();
    mainPanel.add(northPanel, BorderLayout.NORTH);
    northPanel.setLayout(new BorderLayout());
    pointsetViewlet = new PointsetViewlet(session, colorizer = new Colorizer());
    pointsetViewlet.addChangeListener(this);
    // addChangeListener(pointsetViewlet);
    northPanel.add(pointsetViewlet, BorderLayout.SOUTH);
    northPanel.add(createPointsetSelectorPanel(), BorderLayout.CENTER);
    session.pointsetManager.addPointsetManagerListener(this);
    // Now that all listeners are set up, we simulate an initial selection
    selectedPointset = session.pointsetManager.getSelectedPointset();
    if (selectedPointset == null) {
      session.pointsetManager.selectPointsetAt(0);
      // This event will return to us since we are listening to the
      // PointsetManager
    }
    else {
      pointsetList.setSelectedValue(selectedPointset, true);
    }
  }

  public static PointsetModifyDialog getInstance(Session session) {
    if (self == null) {
      self = new PointsetModifyDialog(session);
    }
    return self;
  }

  private JPanel createPointsetSelectorPanel() {
    final JPanel pointsetSelectorPanel = new JPanel();
    pointsetSelectorPanel.setBorder(BorderFactory
        .createTitledBorder("Select pointset to modify"));
    pointsetSelectorPanel.setLayout(new BorderLayout());
    pointsetSelectorPanel.add(Box.createRigidArea(new Dimension(0, 5)),
        BorderLayout.NORTH);
    pointsetSelectorPanel.add(Box.createRigidArea(new Dimension(7, 0)),
        BorderLayout.WEST);
    pointsetSelectorPanel.add(Box.createRigidArea(new Dimension(7, 0)),
        BorderLayout.EAST);
    pointsetSelectorPanel.add(Box.createRigidArea(new Dimension(0, 8)),
        BorderLayout.SOUTH);
    //
    pointsetListModel = new PointsetListModel(session.pointsetManager
        .getPointsets());
    pointsetList = new JList(pointsetListModel);
    pointsetList.setCellRenderer(new PointsetListCellRenderer(session));
    pointsetList.setBorder(BorderFactory.createLoweredBevelBorder());
    pointsetList.setBackground(getBackground());
    pointsetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    pointsetList.addListSelectionListener(new ListSelectionListener() {
      @Override
			public void valueChanged(final ListSelectionEvent ev) {
        if (!ev.getValueIsAdjusting()) {
          selectedPointset = (Pointset) pointsetList.getSelectedValue();
          if (selectedPointset == null) {
            return;
          }
          // // Make the selected pointset listen to the Colorizer for
          // changes
          // ColumnHead columnHead = selectedPointset.getColorizingColumnHead();
          // if (columnHead == null) {
          // updateStatsForSelectedPointset(colcol);
          // }
          // TODO: not sure this is the right way to get a refesh on the list:
          pointsetList.invalidate();
          pointsetList.repaint();
          pointsetList.validate();
          pointsetViewlet.setSelectedPointset(selectedPointset);
        }
      }
    });
    final JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
    scrollPane.setViewportView(pointsetList);
    scrollPane.setPreferredSize(new Dimension(450, 120));
    pointsetSelectorPanel.add(scrollPane, BorderLayout.CENTER);
    return pointsetSelectorPanel;
  }

  private void updatePointsetList() {
    pointsetListModel.clear();
    for (Pointset pointset : session.pointsetManager.getPointsets()) {
      pointsetListModel.addElement(pointset);
    }
    pointsetListModel.fireContentsChanged(pointsetList, 0, pointsetListModel
        .size() - 1);
  }

  @Override
  protected boolean apply() {
    String name = pointsetViewlet.getPointsetName();
    if (!Session.isLegalName(name, false)) {
      session.errorHelp("Pointset name is not legal.", "TODO");
      return false;
    }
    selectedPointset.setName(name);
    Color color = selectedPointset.getColor();
    if (color == null) {
      session.errorHelp("Define a color for the pointset.", "TODO");
      return false;
    }
    selectedPointset.setColor(color);
    // Validate acMapping
    final int i = pointsetViewlet.getAcMapViewlet().getSelectedColumnForAxis(
        Axes.X_AXIS);
    final int j = pointsetViewlet.getAcMapViewlet().getSelectedColumnForAxis(
        Axes.Y_AXIS);
    int k = -1;
    if (session.plotManager.getDim() == 3) {
      k = pointsetViewlet.getAcMapViewlet().getSelectedColumnForAxis(
          Axes.Z_AXIS);
    }
    if (selectedPointset.getCurrentAcMap() == selectedPointset.getAcMap()) {
      selectedPointset.moveToUserSpace();
    }
    if (i == AxesColumnHeadsMapping.UNMAPPED) {
      session.errorHelp("Assign a descriptor to the X-axis.", "TODO");
      return false;
    }
    if (j == AxesColumnHeadsMapping.UNMAPPED) {
      session.errorHelp("Assign a descriptor to the Y-axis.", "TODO");
      return false;
    }
    if (session.plotManager.getDim() == 3
        && k == AxesColumnHeadsMapping.UNMAPPED) {
      session.errorHelp("Assign a descriptor to the Z-axis.", "TODO");
      return false;
    }
    if (i == j || session.plotManager.getDim() == 3 && (i == k || j == k)) {
      session.errorHelp("Axes may not share columns.", "TODO");
      return false;
    }
    // Handle ac mappings
    AxesColumnHeadsMapping acMap = selectedPointset.getAcMap();
    // Remove all mappings first
    if (AxesColumnHeadsMapping.UNMAPPED != acMap
        .getColumnHeadIndexForAxis(Axes.X_AXIS)) {
      acMap.removeMappingFromAxis(Axes.X_AXIS);
    }
    if (AxesColumnHeadsMapping.UNMAPPED != acMap
        .getColumnHeadIndexForAxis(Axes.Y_AXIS)) {
      acMap.removeMappingFromAxis(Axes.Y_AXIS);
    }
    if (AxesColumnHeadsMapping.UNMAPPED != acMap
        .getColumnHeadIndexForAxis(Axes.Z_AXIS)) {
      acMap.removeMappingFromAxis(Axes.Z_AXIS);
    }
    // Set all mappings now
    acMap.addMapping(Axes.X_AXIS, pointsetViewlet.getAcMapViewlet()
        .getSelectedColumnForAxis(Axes.X_AXIS));
    acMap.addMapping(Axes.Y_AXIS, pointsetViewlet.getAcMapViewlet()
        .getSelectedColumnForAxis(Axes.Y_AXIS));
    if (session.plotManager.getDim() == 3) {
      acMap.addMapping(Axes.Z_AXIS, pointsetViewlet.getAcMapViewlet()
          .getSelectedColumnForAxis(Axes.Z_AXIS));
    }
    if (selectedPointset.getCurrentAcMap() == acMap) {
      selectedPointset.moveToSubjectSpace();
    }
    session.dialogManager.getAxesLegendDialog().rebuildLegend();
    //
    selectedPointset.setPointShape(pointsetViewlet.getShape());
    selectedPointset.setPointSize(pointsetViewlet.getSymbolSize());
    ColumnHead columnHead = pointsetViewlet.getColorizerColumnHead();
    // Colorize numbers if a column head is selected
    if (columnHead != null) {
      session.pointsetManager.setWaypoints(colorizer.getWaypoints(),
          selectedPointset);
      selectedPointset.setColorizingColumnHead(columnHead);
      float[] input = selectedPointset.getColorizingColumnValues();
      colorizer.colorize(input, selectedPointset.getRgb());
    }
    session.pointsetManager.pointsetModified(selectedPointset);
    // Since col<->axis mapping changed
    session.pointsetManager.calculateSubjectExtents();
    // session.plotManager.canvas.smartCompose();
    session.plotManager.refreshCanvas();
    // System.out.println("CurrentAcMap after Apply: "
    // + selectedPointset.getCurrentAcMap());
    return true; // apply was successful
  }

  public Pointset getSelectedPointset() {
    return selectedPointset;
  }

  @Override
	public void progressChanged(Object subject, String string, int min,
      int value, int max) {
  }

  @Override
	public void pointsetManagerChanged(PointsetManagerEvent ev) {
    switch (ev.kind) {
    case MEMBER_ADDED:
      if (ev.member instanceof Pointset) {
        updatePointsetList();
        pointsetList.setSelectedValue(ev.member, true);
      }
      break;
    case MEMBER_CHANGED:
      if (ev.member instanceof Pointset) {
        PointsetListModel model = ((PointsetListModel) pointsetList.getModel());
        int i = session.pointsetManager.getPointsetIndex((Pointset) ev.member);
        model.fireContentsChanged(ev.getSource(), i, i);
      }
      break;
    case MEMBER_LOADED:
      break;
    case POINTSET_REMOVED:
      break;
    case MEMBERS_SELECTION:
      break;
    case MANAGER_CHANGED:
      break;
    case MEMBER_VISABILITY:
      break;
    case POINT_VIZ:
      break;
    case AC_MAP:
      if (ev.member == selectedPointset) {
        pointsetViewlet.getAcMapViewlet().setAcMap(selectedPointset.getAcMap());
      }
      break;
    }
  }

  // @Implements ChangeListener
  @Override
	public void stateChanged(ChangeEvent e) {
    pack();
  }

  private static final long serialVersionUID = 1L;
}
