package gov.nih.ncgc.openhts.tool1.dataSourceManager.filter;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.Preview;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Descriptor;
import gov.nih.ncgc.openhts.tool1.descriptorManager.RegisteredDescriptorSelectorViewlet;
import gov.nih.ncgc.openhts.tool1.pointsetManager.ColumnHead;

public class MatcherEditor extends JPanel implements
    CalcInputParserConstants {
  // Each button push generates a token
//  Button one = new Button("1");
//  Button two = new Button("2");
//  Button three = new Button("3");
//  Button four = new Button("4");
//  Button five = new Button("5");
//  Button six = new Button("6");
//  Button seven = new Button("7");
//  Button eight = new Button("8");
//  Button nine = new Button("9");
//  Button zero = new Button("0");
//  Button dot = new Button(".");
//  Button equal = new Button("=");
//  Button add = new Button("+");
//  Button sub = new Button("-");
//  Button mul = new Button("*");
//  Button div = new Button("/");
//  Button quit = new Button("QUIT");
//  private Label display = new Label("0 ");
  private final List<ListSelectionListener> listeners = new ArrayList<ListSelectionListener>();

  private final Map<ColumnHead, Preview> columnHeadToPreview;
  private MatcherViewlet matcherViewlet;
  private MatcherFloatViewlet matcherFloatViewlet;
  private MatcherStringViewlet matcherStringViewlet;
  final JPanel matcherPanel = new JPanel(new BorderLayout());
  private RegisteredDescriptorSelectorViewlet rdsv;
  
  public MatcherEditor(Session session,
      final Map<ColumnHead, Preview> columnHeadToPreview) {
    setBorder(BorderFactory.createTitledBorder("Matcher editor"));
    this.columnHeadToPreview = columnHeadToPreview;
    matcherFloatViewlet = new MatcherFloatViewlet(columnHeadToPreview);
    matcherStringViewlet = new MatcherStringViewlet(columnHeadToPreview);
    setLayout(new BorderLayout());
    final JSplitPane leftSplitPane = new JSplitPane();
    leftSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    add(leftSplitPane,BorderLayout.CENTER);
    rdsv = new RegisteredDescriptorSelectorViewlet(
        session, ListSelectionModel.SINGLE_SELECTION, "Descriptor to match");
    rdsv.initialize();
    leftSplitPane.add(rdsv,JSplitPane.TOP);
    leftSplitPane.add(matcherPanel,JSplitPane.BOTTOM);
    matcherPanel.setBorder(BorderFactory.createTitledBorder("Matcher params"));
    rdsv.addListSelectionListener(new ListSelectionListener() {
      @Override
			public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()){
          return;
        }
        setMatchPanel(rdsv.getSelectedDescriptor());
        fireListSelectionListener(e);
      }
    });
    // GridBagLayout gb = new GridBagLayout();
    // setLayout(gb);
    // GridBagConstraints gbc = new GridBagConstraints();
    //
    // display.setFont(new Font("TimesRoman", Font.BOLD, 18));
    // display.setAlignment(Label.RIGHT);
    // gbc.gridwidth = GridBagConstraints.REMAINDER;
    // gbc.fill = GridBagConstraints.BOTH;
    // gbc.weightx = 1.0;
    // gbc.weighty = 0.0;
    // gb.setConstraints(display, gbc);
    // add(display);
    //
    // Panel buttonPanel = new Panel();
    // buttonPanel.setFont(new Font("TimesRoman", Font.BOLD, 14));
    // buttonPanel.setLayout(new GridLayout(4,4));
    // buttonPanel.add(one); buttonPanel.add(two); buttonPanel.add(three);
    // buttonPanel.add(four);
    // buttonPanel.add(five); buttonPanel.add(six); buttonPanel.add(seven);
    // buttonPanel.add(eight);
    // buttonPanel.add(nine); buttonPanel.add(zero); buttonPanel.add(dot);
    // buttonPanel.add(equal);
    // buttonPanel.add(add); buttonPanel.add(sub); buttonPanel.add(mul);
    // buttonPanel.add(div);
    // gbc.weighty = 1.0;
    // gb.setConstraints(buttonPanel, gbc);
    // add(buttonPanel);
    //
    // quit.setFont(new Font("TimesRoman", Font.BOLD, 14));
    // gbc.gridheight = GridBagConstraints.REMAINDER;
    // gbc.weighty = 0.0;
    // gb.setConstraints(quit, gbc);
    // add(quit);
  }
 
  void setMatchPanel(Matcher matcher) {
    matcherPanel.removeAll();
    if (matcher instanceof MatcherString) {
      matcherStringViewlet.setMatcher(matcher);
      matcherPanel.add(matcherViewlet=matcherStringViewlet);
    }
    else if (matcher instanceof MatcherFloat) {
      matcherFloatViewlet.setMatcher(matcher);
      matcherPanel.add(matcherViewlet=matcherFloatViewlet);
    }
    else {
      throw new RuntimeException("unknown matcher type: "+matcher.getClass().getName());
    }
    matcherPanel.revalidate();
    matcherPanel.repaint();
    //TODO: rdsv.setSelectedDescriptor(matcher.columnHead);
  }
  
  void setMatchPanel(Descriptor descriptor) {
   matcherPanel.removeAll();
    if (descriptor == null) {
      return;
    }
    switch (descriptor.getColumnHeadKind()) {
    case NumberFloat:
      matcherFloatViewlet.setPreview(descriptor);
      matcherPanel.add(matcherViewlet=matcherFloatViewlet);
      
      break;
    case String:
      matcherStringViewlet.setPreview(descriptor);
      matcherPanel.add(matcherViewlet=matcherStringViewlet);
      break;
		case NumberInt:
    default:
      matcherPanel.add(new JLabel("Cannot preview descriptor of kind " 
          + descriptor.getDescriptorKind()));
    }
    matcherPanel.validate();
    matcherPanel.repaint();
  }

  public Matcher getMatcher() {
    if (matcherViewlet == null) {
      return null;
    }
    return matcherViewlet.getMatcher();
  }
  
  public void clear() {
    matcherViewlet=null;
    matcherPanel.removeAll();
    matcherPanel.repaint();
    rdsv.clearSelection();
  }

  Descriptor getSelectedBasisDescriptor() {
    return rdsv.getSelectedDescriptor();
  }
  
  public void addListSelectionListener(ListSelectionListener listener) {
  	listeners.add(listener);
  }
  
  public void removeListSelectionListener(ListSelectionListener listener) {
  	listeners.remove(listener);
  }
  
  private void fireListSelectionListener(ListSelectionEvent e) {
  	for (ListSelectionListener listener : listeners) {
  		listener.valueChanged(e);
  	}
  }
  
//  /**
//   * Note how handleEvent creates tokens and sends them to the parser through
//   * the producer-consumer.
//   */
//  @Override
//  public boolean handleEvent(Event evt) {
//    Token t;
//    if (evt.id != Event.ACTION_EVENT) {
//      return false;
//    }
//    if (evt.target == one) {
//      t = new Token();
//      t.kind = DIGIT;
//      t.image = "1";
//      ProducerConsumer.pc.addToken(t);
//      return true;
//    }
//    if (evt.target == two) {
//      t = new Token();
//      t.kind = DIGIT;
//      t.image = "2";
//      ProducerConsumer.pc.addToken(t);
//      return true;
//    }
//    if (evt.target == three) {
//      t = new Token();
//      t.kind = DIGIT;
//      t.image = "3";
//      ProducerConsumer.pc.addToken(t);
//      return true;
//    }
//    if (evt.target == four) {
//      t = new Token();
//      t.kind = DIGIT;
//      t.image = "4";
//      ProducerConsumer.pc.addToken(t);
//      return true;
//    }
//    if (evt.target == five) {
//      t = new Token();
//      t.kind = DIGIT;
//      t.image = "5";
//      ProducerConsumer.pc.addToken(t);
//      return true;
//    }
//    if (evt.target == six) {
//      t = new Token();
//      t.kind = DIGIT;
//      t.image = "6";
//      ProducerConsumer.pc.addToken(t);
//      return true;
//    }
//    if (evt.target == seven) {
//      t = new Token();
//      t.kind = DIGIT;
//      t.image = "7";
//      ProducerConsumer.pc.addToken(t);
//      return true;
//    }
//    if (evt.target == eight) {
//      t = new Token();
//      t.kind = DIGIT;
//      t.image = "8";
//      ProducerConsumer.pc.addToken(t);
//      return true;
//    }
//    if (evt.target == nine) {
//      t = new Token();
//      t.kind = DIGIT;
//      t.image = "9";
//      ProducerConsumer.pc.addToken(t);
//      return true;
//    }
//    if (evt.target == zero) {
//      t = new Token();
//      t.kind = DIGIT;
//      t.image = "0";
//      ProducerConsumer.pc.addToken(t);
//      return true;
//    }
//    if (evt.target == dot) {
//      t = new Token();
//      t.kind = DOT;
//      t.image = ".";
//      ProducerConsumer.pc.addToken(t);
//      return true;
//    }
//    if (evt.target == equal) {
//      t = new Token();
//      t.kind = EQ;
//      t.image = "=";
//      ProducerConsumer.pc.addToken(t);
//      return true;
//    }
//    if (evt.target == add) {
//      t = new Token();
//      t.kind = ADD;
//      t.image = "+";
//      ProducerConsumer.pc.addToken(t);
//      return true;
//    }
//    if (evt.target == sub) {
//      t = new Token();
//      t.kind = SUB;
//      t.image = "-";
//      ProducerConsumer.pc.addToken(t);
//      return true;
//    }
//    if (evt.target == mul) {
//      t = new Token();
//      t.kind = MUL;
//      t.image = "*";
//      ProducerConsumer.pc.addToken(t);
//      return true;
//    }
//    if (evt.target == div) {
//      t = new Token();
//      t.kind = DIV;
//      t.image = "/";
//      ProducerConsumer.pc.addToken(t);
//      return true;
//    }
//    if (evt.target == quit) {
//      System.exit(0);
//    }
//    return false;
//  }
//
  public void print(double value) {
//    display.setText(Double.toString(value) + " ");
  }

  public void print(String image) {
//    display.setText(image + " ");
  }

  private static final long serialVersionUID = 1L;

}
