package gov.nih.ncgc.openhts.tool1.descriptorManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
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
import gov.nih.ncgc.openhts.tool1.pointsetManager.AxesColumnHeadsMapping;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;

public class BasisModifyDialog extends AbstractDialog implements
    DescriptorManagerListener {
  static class BasisListModel extends DefaultListModel {
    public BasisListModel(Basis[] bases) {
      super();
      for (int i = 0; i < bases.length; i++) {
        this.ensureCapacity(bases.length);
        this.addElement(bases[i]);
      }
    }

    @Override
    public void fireContentsChanged(Object object, int index1, int index2) {
      super.fireContentsChanged(object, index1, index2);
    }

    private static final long serialVersionUID = 1L;
  }

  private JList basisList;
  private final Session session;
  private final BasisViewlet basisViewlet;
  private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
  private Basis selectedBasis;
  private BasisListModel basisListModel;

  public BasisModifyDialog(final Session session) {
    super(session, "Basis - Modify", false);
    this.session = session;
    addWindowListener(DialogManager.getInstance(session));
    addComponentListener(DialogManager.getInstance(session));
    //
    final JPanel topPanel = new JPanel();
    add(topPanel, BorderLayout.CENTER);
    topPanel.setLayout(new BorderLayout());
    topPanel.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.WEST);
    topPanel.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.EAST);
    topPanel.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.NORTH);
    //
    final JPanel northPanel = new JPanel(new BorderLayout());
    topPanel.add(northPanel, BorderLayout.NORTH);
    // 
    final JPanel basisSelectorPanel = createBasisSelectorPanel();
    northPanel.add(basisSelectorPanel, BorderLayout.NORTH);
    basisViewlet = new BasisViewlet(session);
    addChangeListener(basisViewlet);
    northPanel.add(basisViewlet, BorderLayout.SOUTH);
    // set selected value
    basisList.setSelectedValue(session.descriptorManager.getSelectedBasis(),
        true);
    // dialog must be visible for ensureIndexIsVisible to work
    basisList.ensureIndexIsVisible(basisList.getSelectedIndex());
//    northPanel.add(listpanel, BorderLayout.CENTER);
//    listpanel.setLayout(new BorderLayout());
//    listpanel.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.NORTH);
//    listpanel.add(Box.createRigidArea(new Dimension(7, 0)), BorderLayout.WEST);
//    listpanel.add(Box.createRigidArea(new Dimension(7, 0)), BorderLayout.EAST);
//    listpanel.add(Box.createRigidArea(new Dimension(0, 8)), BorderLayout.SOUTH);
//    final BasisListCellRenderer dcr = new BasisListCellRenderer(session);
//    basisList = new JList(session.descriptorManager.getBases());
//    basisList.setCellRenderer(dcr);
//    basisList.setBorder(BorderFactory.createLoweredBevelBorder());
//    basisList.setBackground(getBackground());
//    basisList.addListSelectionListener(new ListSelectionListener() {
//      public void valueChanged(final ListSelectionEvent ev) {
//        final JList yetAnotherList = (JList) ev.getSource();
//        if (!ev.getValueIsAdjusting()) {
//          selectedBasis = (Basis) yetAnotherList.getSelectedValue();
//          fireChangedState();
//          // basisDialogsBox.acMapViewlet.setSelectedXIndex(basis.acMap.getColumnIndexForAxis(Axes.X_AXIS));
//          // basisDialogsBox.acMapViewlet.setSelectedYIndex(basis.acMap.getColumnIndexForAxis(Axes.Y_AXIS));
//          // basisDialogsBox.acMapViewlet.setSelectedZIndex(basis.acMap.getColumnIndexForAxis(Axes.Z_AXIS));
//          yetAnotherList.invalidate();
//          yetAnotherList.repaint();
//          yetAnotherList.validate();
//          // TODO: this is like an update, do we still need it?
//          // session.spreadsheet.disposeTableFrame();
//        }
//      }
//    });
//    basisList.setSelectedValue(session.descriptorManager.getSelectedBasis(),
//        true);
//    final JScrollPane scrollPane = new JScrollPane();
//    scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
//    scrollPane.setViewportView(basisList);
//    scrollPane.setPreferredSize(new Dimension(450, 120));
//    listpanel.add(scrollPane, BorderLayout.CENTER);
    add(getButtonPanel(), BorderLayout.SOUTH);
  }

  @Override
  protected boolean apply() {
    String name = basisViewlet.getBasisName();
    if (!Session.isLegalName(name, false)) {
      session.errorHelp("Basis name is not legal.", "TODO");
      return false;
    }
    selectedBasis.setName(name);
    // Validate acMapping
    final int i = basisViewlet.getAcMapViewlet().getSelectedColumnForAxis(
        Axes.X_AXIS);
    final int j = basisViewlet.getAcMapViewlet().getSelectedColumnForAxis(
        Axes.Y_AXIS);
    int k = -1;
    if (session.plotManager.getDim() == 3) {
      k = basisViewlet.getAcMapViewlet().getSelectedColumnForAxis(Axes.Z_AXIS);
    }
    // if (selectedBasis.getAcMap() == selectedBasis.getAcMap()) {
    // selectedBasis.moveToUserSpace();
    // }
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
    Basis basis = (Basis) basisList.getSelectedValue();
    // TODO: is following necessary
    for (Pointset pointset : session.pointsetManager.getPointsets()) {
      // All pointsets should have the same acMap, but just in case, check
      if (pointset.getCurrentAcMap() != basis.getAcMap()) {
        pointset.moveToUserSpace();
      }
    }
    // Handle ac mappings
    AxesColumnHeadsMapping acMap = selectedBasis.getAcMap();
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
    acMap.addMapping(Axes.X_AXIS, basisViewlet.getAcMapViewlet()
        .getSelectedColumnForAxis(Axes.X_AXIS));
    acMap.addMapping(Axes.Y_AXIS, basisViewlet.getAcMapViewlet()
        .getSelectedColumnForAxis(Axes.Y_AXIS));
    if (session.plotManager.getDim() == 3) {
      acMap.addMapping(Axes.Z_AXIS, basisViewlet.getAcMapViewlet()
          .getSelectedColumnForAxis(Axes.Z_AXIS));
    }
    // TODO: is following necessary
    for (Pointset pointset : session.pointsetManager.getPointsets()) {
      if (pointset.getCurrentAcMap() != basis.getAcMap()) {
        pointset.moveToSubjectSpace();
      }
    }
    // session.dialogManager.getAxesLegendDialog().rebuildLegend();
    // SECOND, set other variables
    // session.pointsetManager.calculateSubjectExtents(); // since col<->axis
    // mapping changed
    // session.plotManager.refreshCanvas();
    basisList.revalidate();
    basisList.repaint();
    session.descriptorManager.basisModified(selectedBasis);
    return true; // apply was successful
  }

  public void addChangeListener(ChangeListener listener) {
    listeners.add(listener);
  }

  public void removeChangeListener(ChangeListener listener) {
    listeners.remove(listener);
  }

  private void fireChangedState() {
    // selection in list changed
    for (ChangeListener listener : listeners) {
      listener.stateChanged(new ChangeEvent(selectedBasis));
    }
  }

  public Basis getSelectedBasis() {
    return selectedBasis;
  }

  @Override
	public void descriptorManagerChanged(DescriptorManagerEvent ev) {
    switch (ev.kind) {
    case MEMBER_CHANGED:
      break;
    case CHANGED:
    case MEMBER_ADDED:
    case MEMBER_REMOVED:
    case MEMBER_REPLACED:
    case MEMBER_LOADED:
    case MEMBERS_SELECTION:
      throw new RuntimeException("TODO");
    }
  }

  private JPanel createBasisSelectorPanel() {
    final JPanel basisSelectorPanel = new JPanel();
    basisSelectorPanel.setBorder(BorderFactory
        .createTitledBorder("Select basis to modify"));
    basisSelectorPanel.setLayout(new BorderLayout());
    basisSelectorPanel.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.NORTH);
    basisSelectorPanel.add(Box.createRigidArea(new Dimension(7, 0)), BorderLayout.WEST);
    basisSelectorPanel.add(Box.createRigidArea(new Dimension(7, 0)), BorderLayout.EAST);
    basisSelectorPanel.add(Box.createRigidArea(new Dimension(0, 8)), BorderLayout.SOUTH);
    //
    basisListModel = new BasisListModel(session.descriptorManager.getBases());
    basisList = new JList(basisListModel);
    basisList.setCellRenderer(new BasisListCellRenderer(session));
    basisList.setBorder(BorderFactory.createLoweredBevelBorder());
    basisList.setBackground(getBackground());
    basisList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    basisList.addListSelectionListener(new ListSelectionListener() {
      @Override
			public void valueChanged(final ListSelectionEvent ev) {
        if (!ev.getValueIsAdjusting()) {
          selectedBasis = (Basis) basisList.getSelectedValue();
          if (selectedBasis == null) {
            return;
          }
          // // Make the selected pointset listen to the Colorizer for
          // changes
          // ColumnHead columnHead = selectedPointset.getColorizingColumnHead();
          // if (columnHead == null) {
          // updateStatsForSelectedPointset(colcol);
          // }
          // TODO: not sure this is the right way to get a refesh on the list:
          basisList.invalidate();
          basisList.repaint();
          basisList.validate();
          basisViewlet.setSelectedBasis(selectedBasis);
        }
      }
    });
    final JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
    scrollPane.setViewportView(basisList);
    scrollPane.setPreferredSize(new Dimension(450, 120));
    basisSelectorPanel.add(scrollPane, BorderLayout.CENTER);
    return basisSelectorPanel;
  }

  private static final long serialVersionUID = 1L;
}
