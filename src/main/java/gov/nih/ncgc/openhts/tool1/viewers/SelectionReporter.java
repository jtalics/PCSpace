package gov.nih.ncgc.openhts.tool1.viewers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import chemaxon.formats.MolImporter;
import chemaxon.marvin.beans.MViewPane;
import chemaxon.struc.Molecule;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.DataSource;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;

public class SelectionReporter extends JPanel {
  public static class MoleculeNameValue {
    final String name;
    final String value;

    public MoleculeNameValue(final String name, final String value) {
      this.name = name;
      this.value = value;
    }
  }

  private final MViewPane mViewPane;
  private String viewPaneParams;
  private final Session session;
  private final int columnCount = 3;
  private final int borderWidth = 1;
  private final int visibleRows = 3;

  public SelectionReporter(final Session session) {
    super();
    this.session = session;
    mViewPane = new MViewPane();
    mViewPane.setPreferredSize(new Dimension(200, 200));
    setLayout(new BorderLayout());
    add(mViewPane, BorderLayout.CENTER);
    add(new SelectionReporterToolBar(session), BorderLayout.SOUTH);
  }

  public void showMolecules(final List<MoleculeNameValue> molNameVals,
      final Color dataSetColor) {
    Molecule mol = null;
    int i = 0;
    for (final MoleculeNameValue molNameVal : molNameVals) {
      try {
        mol = MolImporter.importMol(molNameVal.value);
        mViewPane.setL(i, molNameVal.name);
        mViewPane.setM(i++, mol);
        mViewPane.setC(i, true);
      }
      catch (final Exception ex) {
        // ignore it
      }
    }
    mViewPane.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5,
        dataSetColor));
  }

  public void createReport() {
    viewPaneParams = "rows=30\n" + "cols=" + columnCount + "\n" + "border="
        + borderWidth + "\n" + "visibleRows=" + visibleRows + "\n"
        + "layout=:2:1:M:1:0:1:1:c:b:1:1:L:0:0:1:1:c:n:0:1\n"
        + "param=:M:150:150:L:10b\n";
    mViewPane.setParams(viewPaneParams);
    // Get the selected numbers from the spreadsheet and construct what the
    // viewer needs
    Map<Pointset, int[]> selected = session.spreadsheet.getSelectedPoints();
    final List<MoleculeNameValue> molNameValsList = new ArrayList<MoleculeNameValue>();
    for (Pointset pointset : selected.keySet()) {
      for (final int i : selected.get(pointset)) {
        molNameValsList.add(new MoleculeNameValue(new String(
        pointset.getObjNameAt(i).bytes), new String(
        pointset.getObjDescriptionAt(i).bytes)));
      }
    }
    // TODO: pass color of pointset thru
    showMolecules(molNameValsList, null);
  }

  public void clearMolecules() {
    // mViewPane.setM(0, (Molecule)null);
  }

  public void createDataSource() {
    DataSource dataSource = new DataSource();
    dataSource.setName("my name");
    session.dataSourceManager.registerDataSource(dataSource);
  }

  private static final long serialVersionUID = 1L;

}
