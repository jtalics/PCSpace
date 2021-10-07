/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.dataSourceManager.filter;

import java.awt.FlowLayout;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.Preview;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.PreviewFloat;
import gov.nih.ncgc.openhts.tool1.pointsetManager.ColumnHead;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class MatcherFloatViewlet extends MatcherViewlet {
  private final SpinnerNumberModel loSpinnerModel, hiSpinnerModel;
  private final JLabel label = new JLabel();
  private JRadioButton between;
  private ColumnHead columnHead;

  public MatcherFloatViewlet(Map<ColumnHead, Preview> columnHeadToPreview) {
    super(columnHeadToPreview);
    JPanel topPanel = new JPanel();
    topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.PAGE_AXIS));
    add(topPanel);
    topPanel.add(Box.createVerticalGlue());
    JSpinner loSpinner = new JSpinner(loSpinnerModel = new SpinnerNumberModel(
        0.0, 0.0, 0.0, 0.0));
    topPanel.add(loSpinner);
    topPanel.add(Box.createVerticalGlue());
    loSpinner.setEditor(new JSpinner.NumberEditor(loSpinner, ""));
    //
    JPanel betweenPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
    topPanel.add(betweenPanel);
    topPanel.add(Box.createVerticalGlue());
    ButtonGroup buttonGroup = new ButtonGroup();
    between = new JRadioButton("between (inclusive)");
    between.setSelected(true);
    JRadioButton notbetween=new JRadioButton("not between (exclusive)");
    buttonGroup.add(between);
    buttonGroup.add(notbetween);
    betweenPanel.add(between);
    betweenPanel.add(notbetween);
    //
    JSpinner hiSpinner = new JSpinner(hiSpinnerModel = new SpinnerNumberModel(
        0.0, 0.0, 0.0, 0.0));
    topPanel.add(hiSpinner);
    hiSpinner.setEditor(new JSpinner.NumberEditor(hiSpinner, ""));
    topPanel.add(Box.createVerticalGlue());
    topPanel.add(label);
    label.setHorizontalAlignment(SwingConstants.CENTER);
    topPanel.add(Box.createVerticalGlue());
  }

  @Override
  public void setPreview(ColumnHead columnHead) {
    this.columnHead = columnHead;
    float min, max, step;
    Preview preview = columnHeadToPreview.get(columnHead);
    if (preview == null) {
      min = Float.NEGATIVE_INFINITY;
      max = Float.POSITIVE_INFINITY;
      label.setText("No range preview available for " + columnHead.getName());
      step = 1.0f;
    }
    else {
      min = ((PreviewFloat) preview).getMin();
      max = ((PreviewFloat) preview).getMax();
      label.setText("Range for " + columnHead.getName() + ": [" + min + ","
          + max + "]");
      step = (max - min) / 10;
    }
    Double minimum = new Double(min), maximum = new Double(max), stepSize = new Double(
        step);
    loSpinnerModel.setMinimum(minimum);
    loSpinnerModel.setMaximum(maximum);
    loSpinnerModel.setValue(minimum);
    loSpinnerModel.setStepSize(stepSize);
    //
    hiSpinnerModel.setMinimum(minimum);
    hiSpinnerModel.setMaximum(maximum);
    hiSpinnerModel.setValue(maximum);
    hiSpinnerModel.setStepSize(stepSize);
  }

  public float getLo() {
    return ((Float) loSpinnerModel.getValue()).floatValue();
  }

  public float getHi() {
    return ((Float) hiSpinnerModel.getValue()).floatValue();
  }

  public boolean getBetween() {
    return between.isSelected();
  }

  @Override
  Matcher getMatcher() {
    float lo = ((Double) loSpinnerModel.getValue()).floatValue();
    float hi = ((Double) hiSpinnerModel.getValue()).floatValue();
    return new MatcherFloat(columnHead, lo, hi, between.isSelected());
  }

  @Override
  void setMatcher(Matcher matcher) {
    MatcherFloat matcherFloat = (MatcherFloat) matcher;
    between.setSelected(matcherFloat.between);
    columnHead = matcher.getColumnHead();
    setPreview(columnHead);
    loSpinnerModel.setValue(new Double(matcherFloat.getLo()));
    hiSpinnerModel.setValue(new Double(matcherFloat.getHi()));
  }

  private static final long serialVersionUID = 1L;
}
