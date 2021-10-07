package gov.nih.ncgc.openhts.tool1.pointsetManager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.jidesoft.swing.RangeSlider;
import gov.nih.ncgc.openhts.tool1.Session;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class PointFilterViewlet extends JPanel {
  private boolean collapsed;
  private JPanel expansionPanel;
  private JButton button;
  private Session session;
  private PointsetManagerEntity pointsetManagerEntity;
  private DecimalFormat df = new DecimalFormat("0.00");

  PointFilterViewlet(Session session,
      PointsetManagerEntity pointsetManagerEntity) {
    this.session = session;
    this.pointsetManagerEntity = pointsetManagerEntity;
    initialize();
  }

  public void initialize() {
    setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    button = new JButton() {
      @Override
      public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, super.getPreferredSize().height);
      }

      private static final long serialVersionUID = 1L;
    };
    add(button);
    button.setBackground(Color.WHITE);
    button.setAlignmentX(Component.CENTER_ALIGNMENT);
    expansionPanel = new JPanel();
    expansionPanel
        .setLayout(new BoxLayout(expansionPanel, BoxLayout.PAGE_AXIS));
    //expansionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    button.setText(pointsetManagerEntity.toString());
    button.setBackground(pointsetManagerEntity.getColor());
    button.setEnabled(pointsetManagerEntity.isVisible());
    AxesColumnHeadsMapping acMap = pointsetManagerEntity.getAcMap();
    if (acMap != null) {
      for (ColumnHead columnHead : acMap.getColumnHeads()) {
        JPanel viewletPanel = getViewletPanel(columnHead);
        viewletPanel.setBackground(Color.WHITE);
        expansionPanel.add(viewletPanel);
      }
    }
    expansionPanel.setBorder(BorderFactory
        .createLineBorder(pointsetManagerEntity.getColor()));
    button.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(ActionEvent e) {
        setCollapseState(!collapsed);
        setCollapseStateForPeers(true);
      }
    });
    setCollapseState(true);
  }

  private JPanel getViewletPanel(final ColumnHead columnHead) {
    final JPanel viewletPanel = new JPanel(new GridLayout(3, 1, 0, 0));
    viewletPanel.setBackground(Color.WHITE);
    final Stats stats = columnHead.getStats(pointsetManagerEntity);
    final JLabel lowValueLabel = new JLabel("lo", SwingConstants.CENTER) {
      @Override
      public Dimension getPreferredSize() {
        return new Dimension(10,super.getPreferredSize().height);
      }
    };
    final JLabel highValueLabel = new JLabel("hi", SwingConstants.CENTER){
      @Override
      public Dimension getPreferredSize() {
        return new Dimension(10,super.getPreferredSize().height);
      }
    };
    final JPanel titlePanel = new JPanel(
        new FlowLayout(FlowLayout.CENTER, 0, 0));
    titlePanel.setBackground(Color.WHITE);
    viewletPanel.add(titlePanel);
    final JLabel nameLabel = new JLabel();
    titlePanel.add(nameLabel);
    nameLabel.setText(columnHead.getName());
    viewletPanel.add(getSliderPanel(columnHead, lowValueLabel, highValueLabel,
        stats));
    viewletPanel.add(getLegendPanel(lowValueLabel, highValueLabel, stats, columnHead));
    viewletPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    return viewletPanel;
  }

  private Component getLegendPanel(final JLabel lowValueLabel,
      final JLabel highValueLabel, final Stats stats, ColumnHead columnHead) {
    final JLabel minLabel = new JLabel("min", SwingConstants.CENTER)  {
      @Override
      public Dimension getPreferredSize() {
        return new Dimension(20,super.getPreferredSize().height);
      }
    };
    final JLabel maxLabel = new JLabel("max", SwingConstants.CENTER)  {
      @Override
      public Dimension getPreferredSize() {
        return new Dimension(20,super.getPreferredSize().height);
      }
    };
    minLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE,
        Integer.MAX_VALUE));
    maxLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE,
        Integer.MAX_VALUE));
    SpringLayout springLayout = new SpringLayout();
    JPanel legendPanel = new JPanel(springLayout);
    legendPanel.add(minLabel);
    legendPanel.add(lowValueLabel);
    legendPanel.add(highValueLabel);
    legendPanel.add(maxLabel);
    legendPanel.setBackground(Color.WHITE);
    String lo, hi, min, max;
    switch (columnHead.kind) {
    case String:
      lo = pointsetManagerEntity.getStringForNumberFloat(columnHead,stats.min,true, Float.NEGATIVE_INFINITY).string;
      lowValueLabel.setText(lo);
      lowValueLabel.setToolTipText(lo);
      minLabel.setText(lo);    
      minLabel.setToolTipText(lo);
      //
      hi = pointsetManagerEntity.getStringForNumberFloat(columnHead,stats.max,false, Float.POSITIVE_INFINITY).string;
      highValueLabel.setText(hi);
      highValueLabel.setToolTipText(hi);
      maxLabel.setText(hi);
      maxLabel.setToolTipText(hi);
      break;
    case NumberFloat:
      min = df.format(stats.min);
      minLabel.setText(min);    
      minLabel.setToolTipText(min);
      //
      lo = df.format(stats.min);
      lowValueLabel.setText(lo);
      lowValueLabel.setToolTipText(lo);
      //
      hi = df.format(stats.max);
      highValueLabel.setText(hi);
      highValueLabel.setToolTipText(hi);
      //
      max = df.format(stats.max);
      maxLabel.setText(max);
      maxLabel.setToolTipText(max);
      break;
    case NumberInt:
      throw new RuntimeException();
    }
    
    lowValueLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE,
        Integer.MAX_VALUE));
    highValueLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE,
        Integer.MAX_VALUE));
    // Set up springs
    springLayout.putConstraint(SpringLayout.WEST, minLabel, 1,
        SpringLayout.WEST, legendPanel);
    springLayout.putConstraint(SpringLayout.WEST, lowValueLabel, 1,
        SpringLayout.EAST, minLabel);
    springLayout.putConstraint(SpringLayout.WEST, highValueLabel, 1,
        SpringLayout.EAST, lowValueLabel);
    springLayout.putConstraint(SpringLayout.WEST, maxLabel, 1,
        SpringLayout.EAST, highValueLabel);
    springLayout.putConstraint(SpringLayout.EAST, legendPanel, 1,
        SpringLayout.EAST, maxLabel);
    return legendPanel;
  }

  private Component getSliderPanel(final ColumnHead columnHead,
      final JLabel lowValueLabel, final JLabel highValueLabel, final Stats stats) {
    final JPanel sliderPanel = new JPanel();
    sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.X_AXIS));
    sliderPanel.setBackground(Color.WHITE);
    final JCheckBox insideRangeCheckBox = new JCheckBox();
    insideRangeCheckBox.setBorder(null);
    insideRangeCheckBox.setSelected(true);
    insideRangeCheckBox.setBackground(Color.WHITE);
    sliderPanel.add(insideRangeCheckBox);
    final RangeSlider slider = new RangeSlider();
    sliderPanel.add(slider);
    slider.setBackground(Color.WHITE);
    slider.setPaintLabels(false);
    slider.setBorder(null);
    slider.setMinimum(0); // 0%
    slider.setMaximum(100); // 100%
    slider.setLowValue(pointsetManagerEntity
        .getLowLimitPercentageFor(columnHead));
    slider.setHighValue(pointsetManagerEntity
        .getHighLimitPercentageFor(columnHead));
    if (stats.range == 0.0) {
      slider.setEnabled(false);
      insideRangeCheckBox.setEnabled(false);
      insideRangeCheckBox.setSelected(true);
    }
    slider.setToolTipText("todo");
    slider.addChangeListener(new ChangeListener() {
      @Override
			public void stateChanged(ChangeEvent e) {
        RangeSlider source = (RangeSlider) e.getSource();
        float lowValue = slider.getLowValue() / 100f * stats.range + stats.min;
        float hiValue = slider.getHighValue() / 100f * stats.range + stats.min;
        String lo, hi;
        
        if (!source.getValueIsAdjusting()) {
          switch (columnHead.kind) {
          case String:
            lo=pointsetManagerEntity.getStringForNumberFloat(columnHead,lowValue,true,Float.NEGATIVE_INFINITY).string;
            lowValueLabel.setText(lo);
            lowValueLabel.setToolTipText(lo);
            hi=pointsetManagerEntity.getStringForNumberFloat(columnHead,hiValue,false,Float.POSITIVE_INFINITY).string;
            highValueLabel.setText(hi);
            highValueLabel.setToolTipText(hi);
            break;
          case NumberFloat:
            lo=df.format(lowValue);
            lowValueLabel.setText(lo);
            lowValueLabel.setToolTipText(lo);
            hi=df.format(hiValue);
            highValueLabel.setText(hi);
            highValueLabel.setToolTipText(hi);
            break;
          case NumberInt:
            throw new RuntimeException();
          }
          session.pointsetManager.filterChanged(pointsetManagerEntity,
              columnHead, slider.getLowValue(), slider.getHighValue(),
              insideRangeCheckBox.isSelected());
          session.descriptorManager.filterChanged(pointsetManagerEntity,
              columnHead, slider.getLowValue(), slider.getHighValue(),
              insideRangeCheckBox.isSelected());
        }
      }
    });
    slider
        .setPreferredSize(new Dimension(10, slider.getPreferredSize().height));
    insideRangeCheckBox.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(ActionEvent e) {
        session.pointsetManager.filterChanged(pointsetManagerEntity,
            columnHead, slider.getLowValue(), slider.getHighValue(),
            insideRangeCheckBox.isSelected());
        session.descriptorManager.filterChanged(pointsetManagerEntity,
            columnHead, slider.getLowValue(), slider.getHighValue(),
            insideRangeCheckBox.isSelected());
      }
    });
    insideRangeCheckBox.setSelected(pointsetManagerEntity
        .getShowInsideLimitFor(columnHead));
    sliderPanel.add(insideRangeCheckBox);
    return sliderPanel;
  }

  private void setCollapseStateForPeers(boolean collapsed) {
    for (Component component : getParent().getComponents()) {
      if (component instanceof PointFilterViewlet && component != this) {
        PointFilterViewlet pointFilterViewlet = (PointFilterViewlet) component;
        pointFilterViewlet.setCollapseState(collapsed);
      }
    }
  }

  private void setCollapseState(boolean collapsed) {
    this.collapsed = collapsed;
    if (collapsed) {
      remove(expansionPanel);
    }
    else {
      add(expansionPanel);
    }
    revalidate();
  }

  private static final long serialVersionUID = 1L;
}
