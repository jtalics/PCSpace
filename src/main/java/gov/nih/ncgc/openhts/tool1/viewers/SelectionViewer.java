package gov.nih.ncgc.openhts.tool1.viewers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import chemaxon.formats.MolImporter;
import chemaxon.marvin.beans.MViewPane;
import chemaxon.struc.Molecule;
import com.jidesoft.swing.JidePopupMenu;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.plotManager.PlotManagerEvent;
import gov.nih.ncgc.openhts.tool1.plotManager.PlotManagerListener;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;
import gov.nih.ncgc.openhts.tool1.spreadsheet.PointsetTable;
import gov.nih.ncgc.openhts.tool1.spreadsheet.SpreadsheetEvent;
import gov.nih.ncgc.openhts.tool1.spreadsheet.SpreadsheetListener;

public class SelectionViewer extends JPanel implements SpreadsheetListener,
    PlotManagerListener {
  public static class MoleculeNameAndDescription {
    final String name;
    final String description;

    public MoleculeNameAndDescription(final String name,
        final String description) {
      this.name = name;
      this.description = description;
    }
  }

  private final MViewPane singleViewPane;
  private final MViewPane multipleAnimatedViewPane;
  private final MViewPane multipleNonAnimatedViewPane;
  private MViewPane currentViewPane;
  private final Session session;
  private final JLabel nameLabel;
  private final List<SelectionViewerListener> listeners = new ArrayList<SelectionViewerListener>();
  private boolean animated = false;
  private final static String multiAnimatedParams = "";
  // + "rows=1\n"
  // + "cols=1\n"
  // + "border=0\n"
  // + "visibleRows=3\n"
  // + "layout=:2:1:M:1:0:1:1:c:boundingBox:1:1:L:0:0:1:1:c:n:0:1\n"
  // "param=:M:150:150:L:10b\n"
  // +"";
  private final static int rows = 30, cols = 3;
  private final static String multiNonAnimatedParams = "" + "rows=" + rows
      + "\n" + "cols=" + cols + "\n" + "border=1\n" + "visibleRows=3\n";

  public SelectionViewer(final Session session) {
    super();
    this.session = session;
    setLayout(new BorderLayout());
    singleViewPane = new MViewPane();
    add(currentViewPane = singleViewPane, BorderLayout.CENTER);
    singleViewPane.setPreferredSize(new Dimension(200, 200));
    singleViewPane.setAnimated(false);
    multipleAnimatedViewPane = new MViewPane();
    multipleAnimatedViewPane.setParams(multiAnimatedParams);
    multipleAnimatedViewPane.setAnimated(true);
    multipleAnimatedViewPane.setAnimFPS(2.0);
    multipleNonAnimatedViewPane = new MViewPane();
    multipleNonAnimatedViewPane.setPreferredSize(new Dimension(200, 200));
    multipleNonAnimatedViewPane.setMinimumSize(new Dimension(10, 10));
    multipleNonAnimatedViewPane.setParams(multiNonAnimatedParams);
    multipleNonAnimatedViewPane.setAnimated(false);
    nameLabel = new JLabel("selection viewer");
    add(nameLabel, BorderLayout.SOUTH);
    nameLabel.addMouseListener(new MouseListener() {
      @Override
			public void mouseClicked(MouseEvent e) {
        if (MouseEvent.BUTTON1 == e.getButton() && e.getClickCount() == 2) {
          fireUrlChanged(nameLabel.getText());
        }
      }

      @Override
			public void mouseEntered(MouseEvent e) {
        // nop
      }

      @Override
			public void mouseExited(MouseEvent e) {
        // nop
      }

      @Override
			public void mousePressed(MouseEvent e) {
        final int x = e.getX();
        final int y = e.getY();
        if (MouseEvent.BUTTON3 == e.getButton()) {
          getContextMenu().show(e.getComponent(), x, y);
        }
      }

      @Override
			public void mouseReleased(MouseEvent e) {
        // nop
      }
    });
    nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
    nameLabel.setOpaque(true);
    Session.addFocusBorder(currentViewPane, this);
  }

  private JidePopupMenu getContextMenu() {
    JidePopupMenu popupMenu = new JidePopupMenu();
    popupMenu.add(new JMenuItem(
        session.actionManager.selectionViewerAnimationAction));
    return popupMenu;
  }

  private void showMolecule(final MoleculeNameAndDescription molNameVal,
      final Color color) {
    remove(currentViewPane);
    add(currentViewPane = singleViewPane, BorderLayout.CENTER);
    nameLabel.setText(molNameVal.name);
    nameLabel.setBackground(color);
    // TODO: complementary: nameLabel.setForeground(color);
    Molecule mol = null;
    try {
      mol = MolImporter.importMol(molNameVal.description);
      singleViewPane.setM(0, mol);
    }
    catch (final Exception ex) {
      // session.errorHelp("Cannot show objects because: "+ex, "TODO");
    }
    revalidate();
    repaint();
  }

  private void showMolecules(final String[] molStrings) {
    remove(currentViewPane);
    if (molStrings.length > rows * cols) {
      session.statusPanel.log2("More selections (" + molStrings.length
          + ") than can be viewed (" + rows * cols + ")");
    }
    final Molecule[] mols = new Molecule[molStrings.length <= rows * cols ? molStrings.length
        : rows * cols];
    if (animated) {
      add(currentViewPane = multipleAnimatedViewPane, BorderLayout.CENTER);
      try {
        for (int i = 0; i < mols.length; i++) {
          mols[i] = MolImporter.importMol(molStrings[i]);
        }
        multipleAnimatedViewPane.setAnimated(false);
        multipleAnimatedViewPane.setM(0, mols);
        multipleAnimatedViewPane.setAnimated(true);
      }
      catch (final Throwable ex) {
        // session.errorSupport("Cannot show objects because: ",ex, "TODO");
      }
    }
    else {
      add(currentViewPane = multipleNonAnimatedViewPane, BorderLayout.CENTER);
      int molCount = multipleNonAnimatedViewPane.getMols().length;
      for (int i = 0; i < molCount; i++) {
        multipleNonAnimatedViewPane.setM(i, (Molecule) null);
        // multipleNonAnimatedViewPane.setL(i, null);
      }
      for (int i = 0; i < mols.length; i++) {
        try {
          mols[i] = MolImporter.importMol(molStrings[i]);
          multipleNonAnimatedViewPane.setM(i, mols[i]);
          multipleNonAnimatedViewPane.setL(i, mols[i].getName());
        }
        catch (final Throwable ex) {
          // session.errorSupport("Cannot show objects because: ",ex, "TODO");
        }
      }
    }
    nameLabel.setText("multiple selections");
    nameLabel.setBackground(multipleNonAnimatedViewPane.getBackground());
    revalidate();
    repaint();
  }

  public void setText(final String text) {
    nameLabel.setText(text);
  }

  public void addSelectionViewerListener(SelectionViewerListener listener) {
    listeners.add(listener);
  }

  public void removeSelectionViewerListener(SelectionViewerListener listener) {
    listeners.remove(listener);
  }

  private void fireUrlChanged(String urlString) {
    String errMess;
    for (SelectionViewerListener listener : listeners) {
      if (null != (errMess = listener.urlChanged(nameLabel.getText()))) {
        session.statusPanel.log2(errMess);
      }
    }
  }

  public void toggleAnimation() {
    setAnimated(!isAnimated());
  }

  public void setAnimated(boolean animated) {
    this.animated = animated;
  }

  public boolean isAnimated() {
    return animated;
  }

  private static final long serialVersionUID = 1L;

  @Override
	public void plotManagerChanged(PlotManagerEvent ev) {
    switch (ev.kind) {
    case CHANGED:
      break;
    case BASISMODE_CHANGED:
      break;
    case TEXT_SELECTION_CHANGED:
      // nop
      break;
    case POINTSET_ORDER_CHANGED:
      break;
    case TEXT_PROPERTIES_CHANGED:
      // nop
      break;
    case PREVIEW_CHANGED:
      // don't call this if there are no selections at all
      Map<Pointset, int[]> pointsetToSelectedPoints = (Map<Pointset, int[]>) ev.member;
      int totalSelectionCount = 0;
      for (int[] s : pointsetToSelectedPoints.values()) {
        totalSelectionCount += s.length;
      }
      if (totalSelectionCount > 1) {
        final String[] molStrings = new String[totalSelectionCount];
        totalSelectionCount = 0; // reuse
        for (Pointset pointset : pointsetToSelectedPoints.keySet()) {
          for (int i = 0; i < pointsetToSelectedPoints.get(pointset).length; i++) {
            molStrings[totalSelectionCount] = pointset.getObjDescriptionAt(i)
                .toString();
            totalSelectionCount++;
          }
        }
        showMolecules(molStrings);
        setText("multiple selections");
      }
      else if (totalSelectionCount > 0) {
        // exactly one point
        Pointset pointset = null;
        Iterator<Pointset> iterator = pointsetToSelectedPoints.keySet()
            .iterator();
        int[] selections = null;
        while (iterator.hasNext()) {
          pointset = iterator.next();
          selections = pointsetToSelectedPoints.get(pointset);
          if (selections.length != 0) {
            break;
          }
        }
        final int i = selections[0];
        if (Pointset.stringMetaImageColumnIndex != -1) {
          // obj can be viewed
          session.selectionViewer.showMolecule(
              new SelectionViewer.MoleculeNameAndDescription(pointset
                  .getObjNameAt(i).toString(), pointset.getObjDescriptionAt(i)
                  .toString()), pointset.getColor());
        }
        return;
      }
      break;
    case DIMENSIONALITY:
      break;
    }
  }

  // // @Implements HeatmapListener
  // public void basisModeChanged(PlotManager plotManager, Basis basisMode) {
  // // TODO Auto-generated method stub
  // }
  //
  // // @Implements HeatmapListener
  // public void dimensionalityChanged(PlotManager plotManager, int dim) {
  // // TODO Auto-generated method stub
  // }
  //
  // // @Implements HeatmapListener
  // public void idSelectionChanged(Object source, Map<Pointset, int[]>
  // selected) {
  // // don't call this if there are no selections at all
  // int totalSelectionCount = 0;
  // for (int[] s : selected.values()) {
  // totalSelectionCount += s.length;
  // }
  // if (totalSelectionCount > 1) {
  // final String[] molStrings = new String[totalSelectionCount];
  // totalSelectionCount = 0; // reuse
  // for (Pointset pointset : selected.keySet()) {
  // for (int i = 0; i < selected.get(pointset).length; i++) {
  // molStrings[totalSelectionCount] = pointset.getObjDescriptionAt(i);
  // totalSelectionCount++;
  // }
  // }
  // showMolecules(molStrings);
  // setText("multiple selections");
  // }
  // else if (totalSelectionCount > 0) {
  // // exactly one point
  // Pointset pointset = null;
  // Iterator<Pointset> iterator = selected.keySet().iterator();
  // int[] selections = null;
  // while (iterator.hasNext()) {
  // pointset = iterator.next();
  // selections = selected.get(pointset);
  // if (selections.length != 0) {
  // break;
  // }
  // }
  // final int i = selections[0];
  // if (pointset.getObjDescriptions() != null) {
  // // obj can be viewed
  // session.selectionViewer.showMolecule(
  // new SelectionViewer.MoleculeNameAndDescription(pointset
  // .getObjNameAt(i), pointset.getObjDescriptionAt(i)), pointset
  // .getColor());
  // }
  // return;
  // }
  // }
  //
  // // @Implements SpreadsheetListener
  // public void leadSelectionChanged(Pointset pointset, int rowIndex) {
  // // nop
  // }
  //
  // // @Implements SpreadsheetListener
  // public void pointSelectionChanged(Pointset pointset, int[] selectedPoints)
  // {
  // if (selectedPoints.length == 1) {
  // int i = selectedPoints[0];
  // session.selectionViewer.showMolecule(
  // new SelectionViewer.MoleculeNameAndDescription(pointset.getObjNameAt(
  // i).toString(), pointset.getObjDescriptionAt(i).toString()),
  // pointset.getColor());
  // }
  // else {
  // String[] molStrings = new String[selectedPoints.length];
  // for (int i = 0; i < selectedPoints.length; i++) {
  // molStrings[i] = pointset.getObjDescriptionAt(selectedPoints[i])
  // .toString();
  // }
  // session.selectionViewer.showMolecules(molStrings);
  // }
  // }
  //
  // // @Implements SpreadsheetListener
  // public void pointsetSelectionChanged(Pointset pointset) {
  // // nop
  // }
  //
  @Override
	public void spreadsheetChanged(SpreadsheetEvent ev) {
    PointsetTable pointsetTable;
    Pointset pointset;
    switch (ev.kind) {
    case MEMBERS_SELECTION:
      break;
    case LEAD_SELECTION:
      break;
    case POINT_SELECTION:
      pointsetTable = (PointsetTable) ev.member;
      pointset = pointsetTable.getPointset();
      int[] selectedPoints = pointsetTable.getSelectedPoints();
      if (selectedPoints.length == 1) {
        int i = selectedPoints[0];
        session.selectionViewer.showMolecule(
            new SelectionViewer.MoleculeNameAndDescription(pointset
                .getObjNameAt(i).toString(), pointset.getObjDescriptionAt(i)
                .toString()), pointset.getColor());
      }
      else {
        String[] molStrings = new String[selectedPoints.length];
        for (int i = 0; i < selectedPoints.length; i++) {
          molStrings[i] = pointset.getObjDescriptionAt(selectedPoints[i])
              .toString();
        }
        session.selectionViewer.showMolecules(molStrings);
      }
      break;
    case MEMBER_LOADED:
      // nop
      break;
    case MANAGER_CHANGED:
    case MEMBER_CHANGED:
    case MEMBER_ADDED:
    case MEMBER_REMOVED:
    case MEMBER_VISABILITY:
      throw new RuntimeException("TODO");
    }
  }
}
