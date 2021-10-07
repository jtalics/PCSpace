package gov.nih.ncgc.openhts.tool1.descriptorManager;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.StringAndNumberFloat;
import gov.nih.ncgc.openhts.tool1.Tool1Exception;
import gov.nih.ncgc.openhts.tool1.persistence.Persistent;
import gov.nih.ncgc.openhts.tool1.pointsetManager.AxesColumnHeadsMapping;
import gov.nih.ncgc.openhts.tool1.pointsetManager.ColumnHead;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Pointset;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetManagerEntity;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Stats;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class Basis implements DescriptorManagerEntity, PointsetManagerEntity,
    Comparable<Basis>, Persistent {
  private final List<Descriptor> descriptors = new ArrayList<Descriptor>();
  private final List<String> missingDescriptors = new ArrayList<String>();
  private String name;
  private boolean stale;
  private transient Session session;
  private final transient List<ChangeListener> listeners = new ArrayList<ChangeListener>();
  private AxesColumnHeadsMapping acMap;
  private Stats[] colStats;
  private int[] limitLowPercentages;
  private int[] limitHighPercentages;
  private boolean[] showInsideRanges;
  private boolean visible;

  public Basis(Session session, final String name) {
    this.session = session;
    setName(name);
  }

  public void initialize() {
    // Call this AFTER adding the descriptors.
    // TODO: Clean up; Basis is mutable, descriptors can be added/removed, so...
    acMap = new AxesColumnHeadsMapping(session);
    acMap.initialize(descriptors.size());
    for (int i = 0; i < descriptors.size(); i++) {
      acMap.setColumnHeadAt(descriptors.get(i), i);
      // acMap.setColumnDescriptionAt(descriptors.get(i).getDescription(),i);
    }
    // acMap.axisToCol[Axes.Z_AXIS]=3; // TODO: temporary
    colStats = new Stats[acMap.getDimensionality()];
    for (int i = 0; i < colStats.length; i++) {
      colStats[i] = new Stats((float[]) null);
    }
    limitLowPercentages = new int[acMap.getDimensionality()];
    limitHighPercentages = new int[acMap.getDimensionality()];
    showInsideRanges = new boolean[acMap.getDimensionality()];
    for (int i = 0; i < acMap.getDimensionality(); i++) {
      limitLowPercentages[i] = 100;
      limitHighPercentages[i] = 100;
      showInsideRanges[i] = true;
    }
    initializeTransient();
  }

  // @Implements Persistent
  @Override
	public void initializeTransient() {
    // nop
  }

  // @Implements Comparable
  @Override
	public int compareTo(final Basis basis) {
    String name1 = getName();
    String name2 = basis.getName();
    if (name1 == null) {
      if (name2 == null) {
        return 0;
      }
      return Integer.MIN_VALUE;
    }
    if (name2 == null) {
      return Integer.MAX_VALUE;
    }
    return name1.compareTo(name2);
  }

  @Override
  public boolean equals(Object object) {
    Basis basis2 = (Basis) object;
    if (basis2 == null) {
      return false;
    }
    if (name == null) {
      if (basis2.name == null) {
        return true;
      }
      return false;
    }
    if (basis2.name == null) {
      return false;
    }
    if (!this.name.equals(basis2.name)) {
      return false;
    }
    if (this.descriptors.size() != basis2.descriptors.size()) {
      return false;
    }
    for (int i = 0; i < descriptors.size(); i++) {
      if (!this.descriptors.contains(basis2.descriptors.get(i))) {
        return false;
      }
    }
    return true;
  }

  public void addDescriptors(final Descriptor[] descriptors)
      throws Tool1Exception {
    for (final Descriptor descriptor : descriptors) {
      addDescriptor(descriptor);
    }
  }

  public void addDescriptors(final List<Descriptor> descriptors)
      throws Tool1Exception {
    for (final Descriptor descriptor : descriptors) {
      addDescriptor(descriptor);
    }
  }

  public Descriptor getDescriptor(final int i) {
    return descriptors.get(i);
  }

  public Descriptor[] getDescriptors() {
    final Descriptor[] descriptorsArray = new Descriptor[descriptors.size()];
    for (int i = 0; i < descriptors.size(); i++) {
      if (null == descriptors.get(i)) {
        throw new RuntimeException("null descriptor in basis");
      }
      descriptorsArray[i] = descriptors.get(i);
    }
    return descriptorsArray;
  }

  public void addDescriptor(final Descriptor descriptor) throws Tool1Exception {
    if (descriptor == null) {
      throw new RuntimeException("cannot add null descriptor to basis = "
          + this);
    }
    if (descriptors.contains(descriptor)) {
      throw new Tool1Exception("basis " + name
          + " already contains descriptor: " + descriptor);
    }
    ColumnHead.Kind kind = descriptor.getColumnHeadKind();
    switch (kind) {
    case NumberFloat:
      break;
    case String:
    case NumberInt:
      // throw new Tool1Exception("basis " + name
      // + " cannot be composed of descriptor " + descriptor.getName()
      // + "\n with kind = " + kind);
    }
    for (final Descriptor d : descriptors) {
      if (0 == descriptor.compareTo(d)) {
        throw new RuntimeException("duplicate descriptor (same name): "
            + descriptor);
      }
    }
    descriptors.add(descriptor);
    Collections.sort(descriptors);
    fireStateChanged();
  }

  public void removeDescriptor(final Descriptor descriptor) {
    if (descriptors.remove(descriptor)) {
      fireStateChanged();
    }
    // Still sorted of course.
  }

  private void fireStateChanged() {
    for (final ChangeListener listener : listeners) {
      listener.stateChanged(new ChangeEvent(this));
    }
  }

  public void addChangeListener(final ChangeListener listener) {
    listeners.add(listener);
  }

  public boolean removeChangeListener(final ChangeListener listener) {
    final boolean b = listeners.remove(listener);
    if (b) {
      fireStateChanged();
      return true;
    }
    return false;
  }

  public int getDimensionality() {
    return descriptors.size();
  }

  @Override
	public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "[Basis: " + name + "; " + descriptors.size() + "]";
  }

  public void setName(final String name) {
    if (!Session.isLegalName(name, true)) {
      throw new RuntimeException("illegal name: " + name);
    }
    this.name = name;
  }

  public String getToolTipText() {
    final StringBuffer sb = new StringBuffer("<html><boundingBox>Basis: "
        + getName() + "</boundingBox><br>");
    for (final Descriptor descriptor : descriptors) {
      sb.append("&nbsp" + descriptor + "<br>");
    }
    sb.append("  stale = " + stale + "<br>");
    sb.append("</html>");
    return sb.toString();
  }

  public boolean isStale() {
    return stale;
  }

  // @Implements Persistent
  @Override
	public void setSession(Session session) {
    this.session = session;
  }

  public boolean containsDescriptor(Descriptor descriptor) {
    return descriptors.contains(descriptor);
  }

  public Descriptor getDescriptorForAxis(int axis) {
    int i = acMap.getColumnHeadIndexForAxis(axis);// axisToCol[axis];
    return (Descriptor) acMap.getColumnHeadAt(i);
  }

  public boolean isValid() {
    return true;
  }

  @Override
	public AxesColumnHeadsMapping getAcMap() {
    return acMap;
  }

  public void setAcMap(AxesColumnHeadsMapping acMap) {
    this.acMap = acMap;
  }

  public void addMissingDescriptorName(String descriptorName) {
    // Missing descriptor names should be registered when found
    if (null != session.descriptorManager.getDescriptorByName(name)) {
      new RuntimeException("descriptor not missing:  " + descriptorName)
          .printStackTrace();
    }
    missingDescriptors.add(descriptorName);
  }

  public void findMissingDescriptors() throws Tool1Exception {
    List<String> found = new ArrayList<String>();
    for (String descriptorName : missingDescriptors) {
      Descriptor descriptor = session.descriptorManager
          .getDescriptorByName(descriptorName);
      if (descriptor != null) {
        found.add(descriptorName);
        addDescriptor(descriptor);
      }
    }
    for (String descriptorName : found) {
      missingDescriptors.remove(descriptorName);
    }
  }

  public int getDescriptorCount() {
    return descriptors.size();
  }

  public void calcStats() {
    ColumnHead[] columnHeads = acMap.getColumnHeads();
    int n = 0;
    for (int col = 0; col < columnHeads.length; col++) {
      for (Pointset pointset : session.pointsetManager.getPointsets()) {
        if (this == pointset.getBasis()) {
          n += pointset.getPointCount();
        }
      }
      float[] values = new float[n];
      n = 0;
      for (Pointset pointset : session.pointsetManager.getPointsets()) {
        if (this == pointset.getBasis()) {
          for (float value : pointset.getColumnUserValuesFor(columnHeads[col])) {
            values[n++] = value;
          }
        }
      }
      colStats[col].calc(values);
      session.pointsetManager.fireProgressChanged(null, "characterizing", 0,
          col, columnHeads.length);
    }
  }

  public Stats getStats(ColumnHead columnHead) {
    return colStats[acMap.getColumnHeadIndexFor(columnHead)];
  }

  public void filterChanged(ColumnHead columnHead, int limitLowPercentage,
      int limitHighPercentage, boolean showInsideRange) {
    this.limitLowPercentages[acMap.getColumnHeadIndexFor(columnHead)] = limitLowPercentage;
    this.limitHighPercentages[acMap.getColumnHeadIndexFor(columnHead)] = limitHighPercentage;
    this.showInsideRanges[acMap.getColumnHeadIndexFor(columnHead)] = showInsideRange;
  }

  @Override
	public Color getColor() {
    return Color.WHITE;
  }

  @Override
	public int getLowLimitPercentageFor(ColumnHead columnHead) {
    int columnHeadIndex = acMap.getColumnHeadIndexFor(columnHead);
    return limitLowPercentages[columnHeadIndex];
  }

  @Override
	public int getHighLimitPercentageFor(ColumnHead columnHead) {
    int columnHeadIndex = acMap.getColumnHeadIndexFor(columnHead);
    return limitHighPercentages[columnHeadIndex];
  }

  @Override
	public boolean getShowInsideLimitFor(ColumnHead columnHead) {
    int columnHeadIndex = acMap.getColumnHeadIndexFor(columnHead);
    return showInsideRanges[columnHeadIndex];
  }

  @Override
	public boolean isVisible() {
    return session.plotManager.basis == this;
  }

  public void setVisible(boolean visible) {
    // this.visible = visible;
  }

  @Override
	public StringAndNumberFloat getStringForNumberFloat(ColumnHead columnHead,
      float value, boolean lessThan, float closest) {
    StringAndNumberFloat stringAndNumberFloat = new StringAndNumberFloat();
    for (Pointset pointset : session.pointsetManager.getPointsets()) {
      if (this != pointset.getBasis()) {
        continue;
      }
      stringAndNumberFloat = pointset.getStringForNumberFloat(columnHead,
          value, lessThan, closest);
      closest = stringAndNumberFloat.numberFloat;
    }
    return stringAndNumberFloat;
  }
}
