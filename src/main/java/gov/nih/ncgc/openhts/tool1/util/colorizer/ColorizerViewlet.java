package gov.nih.ncgc.openhts.tool1.util.colorizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.pointsetManager.Stats;

public class ColorizerViewlet extends JPanel implements ChangeListener {
  Colorizer colorizer;
  final static int gap = 7;
  private final List<ChangeListener> listeners = new ArrayList<ChangeListener>();
  private JPanel preprocessorPanel;
  private JPanel coloringFunctionPanel;
  private JPanel colorMapPanel;
  JLabel statsLabel = new JLabel();

  public ColorizerViewlet(Colorizer colorizer) {
    this.colorizer = colorizer;
    setLayout(new BorderLayout());
    JTabbedPane tabbedPane = new JTabbedPane();
    add(tabbedPane, BorderLayout.CENTER);
    tabbedPane.addTab("Preprocessing", null, getPreprocessorPanel(),
        "Select processing modes");
    tabbedPane.addTab("Coloring function", null, getColoringFunctionPanel(),
        "Define the function to transform your data to [0,1]");
    tabbedPane.addTab("Color mapping ", null, getColorMapPanel(),
        "Map [0,1] to colors you specify");
    tabbedPane.setSelectedIndex(1);
    tabbedPane.setBorder(BorderFactory.createTitledBorder("Colorize by value"));
    Session.addFocusBorder(this,this);
  }

  private JPanel getColorMapPanel() {
    colorMapPanel = new JPanel(new BorderLayout());
    final JColorChooser colorChooser = new JColorChooser();
    colorChooser.setMinimumSize(new Dimension(10, 10));
    colorMapPanel.add(colorChooser, BorderLayout.NORTH);
    final JPanel previewPanel = new JPanel(new BorderLayout());
    colorMapPanel.add(previewPanel, BorderLayout.CENTER);
    final JLabel lowLabel = new JLabel("lo");
    lowLabel.setBorder(BorderFactory.createEmptyBorder(gap, gap, gap, gap));
    lowLabel.setOpaque(true);
    previewPanel.add(lowLabel, BorderLayout.WEST);
    final JPanel sliderPanel = new JPanel(new BorderLayout());
    previewPanel.add(sliderPanel, BorderLayout.CENTER);
    final JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 40);
    sliderPanel.add(slider, BorderLayout.NORTH);
    final JPanel samplePanel = getSamplePanel();
    sliderPanel.add(samplePanel, BorderLayout.CENTER);
    JLabel highLabel = new JLabel("hi");
    highLabel.setBorder(BorderFactory.createEmptyBorder(gap, gap, gap, gap));
    highLabel.setOpaque(true);
    previewPanel.add(highLabel, BorderLayout.EAST);
    colorChooser.setPreviewPanel(new JPanel()); // bug?
    colorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
      @Override
			public void stateChanged(ChangeEvent e) {
        colorizer.addWaypoint(new Waypoint(slider.getValue(), colorChooser
            .getColor()));
        samplePanel.repaint();
      }
    });
    JButton resetButton = new JButton("Reset");
    previewPanel.add(resetButton, BorderLayout.SOUTH);
    resetButton.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(ActionEvent e) {
        colorizer.clearWaypoints();
        samplePanel.repaint();
      }
    });
    return colorMapPanel;
  }

  private JPanel getSamplePanel() {
    final JPanel samplePanel = new JPanel(new BorderLayout()) {
      @Override
      public Dimension getPreferredSize() {
        return new Dimension(getParent().getWidth(), 20);
      }

      @Override
      public Dimension getMinimumSize() {
        return new Dimension(10, 20);
      }

      @Override
      public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, 20);
      }
    };
    samplePanel.setBackground(new Color(Float.NaN, Float.NaN, Float.NaN));
    samplePanel.add(new ColormapCanvas(colorizer.getWaypoints()),
        BorderLayout.CENTER);
    samplePanel.setBorder(BorderFactory.createMatteBorder(0, gap, gap, gap,
        Color.GRAY));
    return samplePanel;
  }

  private JPanel getPreprocessorPanel() {
    preprocessorPanel = new JPanel(new BorderLayout());
    final JComboBox preprocessorComboBox = new JComboBox();
    preprocessorPanel.add(preprocessorComboBox, BorderLayout.NORTH);
    preprocessorComboBox.setMinimumSize(new Dimension(10, 10));
    for (Function function : colorizer.getPreprocessors()) {
      preprocessorComboBox.addItem(function);
    }
    preprocessorComboBox.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(ActionEvent e) {
        Function function=(Function) preprocessorComboBox.getSelectedItem();
        function.setColumnCount(0);
        colorizer.setSelectedPreprocessorFunction(function);
      }
    });
    preprocessorPanel.add(statsLabel, BorderLayout.CENTER);
    return preprocessorPanel;
  }

  private JPanel getColoringFunctionPanel() {
    coloringFunctionPanel = new JPanel(new BorderLayout());
    JPanel comboBoxPanel = new JPanel();
    coloringFunctionPanel.add(comboBoxPanel, BorderLayout.NORTH);
    final JPanel canvasPanel = new JPanel(new BorderLayout());
    coloringFunctionPanel.add(canvasPanel, BorderLayout.CENTER);
    canvasPanel.setBackground(Color.BLACK);
    final FunctionCanvas functionCanvas = new FunctionCanvas(colorizer);
    canvasPanel.add(functionCanvas, BorderLayout.CENTER);
    Function[] functions = colorizer.getColoringFunctions();
    final JComboBox coloringFunctionComboBox = new JComboBox(functions);
    comboBoxPanel.add(coloringFunctionComboBox);
    coloringFunctionComboBox.setMinimumSize(new Dimension(10, 10));
    coloringFunctionComboBox.setSelectedItem(colorizer.getColoringFunction());
    coloringFunctionComboBox.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(ActionEvent e) {
        Function function=(Function) coloringFunctionComboBox.getSelectedItem();
        function.setColumnCount(0);
        colorizer.setSelectedColoringFunction(function);
      }
    });
    JPanel samplePanel = getSamplePanel();
    coloringFunctionPanel.add(samplePanel, BorderLayout.SOUTH);
    return coloringFunctionPanel;
  }

  @Override
	public void stateChanged(ChangeEvent e) {
    ColorizerChangeEvent ev = (ColorizerChangeEvent) e;
    if (ev.getSource() != colorizer) {
      throw new RuntimeException("interested only in colorizer");
    }
    switch(ev.kind) {
    case PREPROCESSOR_FUNCTION_STATS:
      Stats stats = colorizer.getPreprocessorFunction().getStats();
      statsLabel.setText("Before processing: " + stats.toString());
      statsLabel.setMinimumSize(new Dimension(10, 10));
      preprocessorPanel.revalidate();
      preprocessorPanel.repaint();
      break;
    case COLORING_FUNCTION_STATS:
      coloringFunctionPanel.revalidate();
      coloringFunctionPanel.repaint();
      break;
    case COLOR_MAP:
      colorMapPanel.revalidate();
      colorMapPanel.repaint();
      break;
    case COLORING_FUNCTION:
    case PREPROCESSOR_FUNCTION:
      break;
    }
  }

  private static final long serialVersionUID = 1L;
}
// end of file
