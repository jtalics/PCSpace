package gov.nih.ncgc.openhts.tool1.plotManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;
import gov.nih.ncgc.openhts.tool1.dialogManager.DialogManager;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetListCellRenderer;
import gov.nih.ncgc.openhts.tool1.util.DNDList;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class CanvasOrderDialog extends AbstractDialog implements
    PlotManagerListener {
  private final DNDList list;
  private final Session session;

  public CanvasOrderDialog(final Session session) {
    super(session);
    this.session = session;
    session.plotManager.addPlotManagerListener(this);
    list = new DNDList(session.plotManager.getPointsets());
    setTitle("order of drawing pointsets");
    setModal(false);
    setResizable(false);
    addWindowListener(DialogManager.getInstance(session));
    addComponentListener(DialogManager.getInstance(session));
    final JPanel panel = new JPanel();
    add(panel, BorderLayout.CENTER);
    panel.setLayout(new BorderLayout());
    panel.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.WEST);
    panel.add(Box.createRigidArea(new Dimension(5, 0)), BorderLayout.EAST);
    panel.add(Box.createRigidArea(new Dimension(0, 5)), BorderLayout.NORTH);
    final JPanel subpanel1 = new JPanel(new BorderLayout(0, 5));
    final JLabel label = new JLabel(
        "Order of drawing pointsets (back to front)", SwingConstants.LEFT);
    subpanel1.add(label, BorderLayout.NORTH);
    label.setToolTipText("data sources available to reorder");
    panel.add(subpanel1, BorderLayout.CENTER);
    list
        .setToolTipText("drag the data source to the desired position on this list");
    final PointsetListCellRenderer dcr = new PointsetListCellRenderer(session);
    list.setCellRenderer(dcr);
    list.setBorder(BorderFactory.createLoweredBevelBorder());
    final JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
    scrollPane.setPreferredSize(new Dimension(400, 200));
    scrollPane.setViewportView(list);
    subpanel1.add(scrollPane);
    add(getButtonPanel(), BorderLayout.SOUTH);
    list.repopulate(session.plotManager.getPointsets());
  }

  @Override
  protected boolean apply() {
    List<Pointset> pointsets = new ArrayList<Pointset>();
    for (Object object : list.getObjects()) {
      pointsets.add((Pointset)object);
    }
    session.plotManager.reorderPointsets(pointsets);
    session.plotManager.refreshCanvas();
    return true;
  }

  private static final long serialVersionUID = 1L;

  @Override
	public void plotManagerChanged(PlotManagerEvent ev) {
    switch(ev.kind) {
    case CHANGED:
      break;
    case BASISMODE_CHANGED:
      // TODO
      break;
    case TEXT_SELECTION_CHANGED:
      // TODO
      break;
    case POINTSET_ORDER_CHANGED:
      list.repopulate(session.plotManager.getPointsets());
      break;
    case TEXT_PROPERTIES_CHANGED:
      // TODO
      break;
    case PREVIEW_CHANGED:
      break;
    case DIMENSIONALITY:
      break;

    }
  }
}
