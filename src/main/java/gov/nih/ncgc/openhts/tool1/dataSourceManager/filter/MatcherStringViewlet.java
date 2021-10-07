package gov.nih.ncgc.openhts.tool1.dataSourceManager.filter;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.Preview;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.PreviewString;
import gov.nih.ncgc.openhts.tool1.pointsetManager.ColumnHead;

public class MatcherStringViewlet extends MatcherViewlet {
  private final JList previewList= new JList();
  private final DefaultListModel defaultListModel = new DefaultListModel();
  private final JTextField regexpTextField = new JTextField();
  private final JLabel label = new JLabel();
  private ColumnHead columnHead;

  public MatcherStringViewlet(Map<ColumnHead, Preview> columnHeadToPreview) {
    super(columnHeadToPreview);
    previewList.setModel(defaultListModel);
    previewList.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        Object[] objects=previewList.getSelectedValues();
        StringBuffer sb= new StringBuffer();
        for (Object object : objects) {
          if (sb.length()>0) {
            sb.append('|');
          }
          sb.append(object);
        }
        regexpTextField.setText(sb.toString());
      }      
    });
    final JPanel topPanel = new JPanel();
    add(topPanel);
    topPanel.add(Box.createVerticalGlue());
    topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));
    //
    JPanel regexpPanel = new JPanel(new BorderLayout());
    topPanel.add(regexpPanel);
    topPanel.add(Box.createVerticalGlue());
    regexpPanel.add(new JLabel("Reg exp:"),BorderLayout.WEST);
    regexpPanel.add(regexpTextField,BorderLayout.CENTER);
    //
    JLabel noteLabel = new JLabel("([{\\^-$|]})?*+. are metacharacters");
    noteLabel.setHorizontalAlignment(SwingConstants.CENTER);
    topPanel.add(noteLabel);
    topPanel.add(Box.createVerticalGlue());
    topPanel.add(new JScrollPane(previewList));
    topPanel.add(Box.createVerticalGlue());
    topPanel.add(label);
    label.setHorizontalAlignment(SwingConstants.CENTER);
    topPanel.add(Box.createVerticalGlue());
  }

  @Override
  public void setPreview(ColumnHead columnHead) {
    this.columnHead = columnHead;
    defaultListModel.clear();
    Preview preview = columnHeadToPreview.get(columnHead);
    if (preview == null) {
      label.setText("No range preview available for " + columnHead.getName() );
      return;
    }
    else {
      String min = ((PreviewString) preview).getMin();
      String max = ((PreviewString) preview).getMax();
      label.setText("Range for " + columnHead.getName() + ": [" + min + ","
          + max + "]");
    }
    
    for (String string : ((PreviewString) preview).getSamples()) {
      defaultListModel.addElement(string);
    }
  }

  @Override
  public Matcher getMatcher() {
    return new MatcherString(columnHead, regexpTextField.getText());
  }

  @Override
  void setMatcher(Matcher matcher) {
    MatcherString matcherString = (MatcherString)matcher;
    this.columnHead = matcherString.getColumnHead();
    setPreview(columnHead);
    regexpTextField.setText(matcherString.getPattern());
  }

  private static final long serialVersionUID = 1L;

}
