package gov.nih.ncgc.openhts.tool1.dataSourceManager.filter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.Tool1Exception;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.DataSource;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.DataSourceReader;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.Preview;
import gov.nih.ncgc.openhts.tool1.descriptorManager.Basis;
import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;
import gov.nih.ncgc.openhts.tool1.pointsetManager.ColumnHead;
import gov.nih.ncgc.openhts.tool1.util.ProgressBar;
import gov.nih.ncgc.openhts.tool1.util.ProgressListener;

public class FilterCreateDialog extends AbstractDialog implements
    ProgressListener {
  private class PCThread extends Thread {
    private MatcherEditor matcherEditor;

    public PCThread(MatcherEditor matcherEditor) {
      this.matcherEditor = matcherEditor;
    }

    private boolean stopRequest = false;

    @Override
    public void run() {
      while (!stopRequest) {
        try {
          CalcInputParser.Input(matcherEditor);
        }
        catch (ParseException e) {
          matcherEditor.print("ERROR (click 0)");
        }
      }
    }

    public void stopRequest() {
      stopRequest = true;
    }
  }

  private PCThread pcThread;
  private final Map<ColumnHead, Preview> columnHeadToPreview = new HashMap<ColumnHead, Preview>();
  private FilterViewlet filterViewlet;
  // private final Basis basis;
  private DataSourceFilter dataSourceFilter;
  private final DataSourceReader dataSourceReader;
  private final ProgressBar progressBar = new ProgressBar();
  MatcherEditor matcherEditor;
  private static final Dimension buttonSize = null;

  public FilterCreateDialog(final Session session, DataSource dataSource,
      final DataSourceReader dataSourceReader, final Basis basis) {
    super(session, "Data source filter", true);
    this.session = session;
    this.dataSourceReader = dataSourceReader;
    final JButton previewButton = new JButton("Preview descriptor values");
    final JPanel northPanel = new JPanel();
    add(northPanel, BorderLayout.NORTH);
    String name = dataSource.getName();
    northPanel.add(new JLabel("Building filter for data source named '" + name
        + "'"));
    northPanel.add(previewButton);
    add(getCenterPanel(), BorderLayout.CENTER);
    previewButton.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(ActionEvent e) {
        dataSourceReader.addProgressListener(FilterCreateDialog.this);
        // session.dataSourceManager.addDataSourceManagerListener(bar);
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        northPanel.remove(previewButton);
        northPanel.add(progressBar);
        northPanel.validate();
        northPanel.repaint();
        final JLabel label;
        SwingWorker<String, Object> worker = new SwingWorker<String, Object>() {
          @Override
          public String doInBackground() {
            try {
              dataSourceReader.readPreview(columnHeadToPreview);
            }
            catch (IOException e) {
              session.errorSupport("Cannot preview because ", e, "TODO");
            }
            catch (Tool1Exception e) {
              session.errorSupport("Cannot preview because ", e, "TODO");
            }
            catch (Throwable e) {
              session.errorSupport("Cannot preview because ", e, "TODO");
            }
            return "unused";
          }

          @Override
          protected void done() {
            // if (get() != "success") {
            // }
            northPanel.remove(progressBar);
            northPanel.add(previewButton);
            northPanel.validate();
            northPanel.repaint();
            matcherEditor.setMatchPanel(matcherEditor
                .getSelectedBasisDescriptor());
          }
        };
        worker.execute();
      }
    });
    add(getButtonPanel(), BorderLayout.SOUTH);
    // setPreferredSize(new Dimension(900, 400));
    /*
     * TokenManager tm = new TokenCollector(); CalcInputParser cp = new
     * CalcInputParser(tm); pcThread = new PCThread(matcherEditor);
     * pcThread.start();
     */
  }

  private JPanel getCenterPanel() {
    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
    matcherEditor = new MatcherEditor(session, columnHeadToPreview);
    matcherEditor.setPreferredSize(new Dimension(310, 500));
    matcherEditor.addListSelectionListener(new ListSelectionListener() {
      @Override
			public void valueChanged(ListSelectionEvent e) {
        //filterViewlet.clearSelection();
      }
    });
    centerPanel.add(matcherEditor, BorderLayout.CENTER);
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
    //buttonPanel.setAlignmentY(0.5f);
    //buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    centerPanel.add(buttonPanel);
    //
    JButton addButton = new JButton("Add >>") {
    @Override
    public Dimension getMaximumSize() {
      return new Dimension(Integer.MAX_VALUE, super.getPreferredSize().height);
    }

    private static final long serialVersionUID = 1L;
  };
    buttonPanel.add(addButton);
    addButton.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
    addButton.setPreferredSize(buttonSize);
    addButton.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(ActionEvent e) {
        Matcher matcher = matcherEditor.getMatcher();
        if (matcher == null) {
          session.errorNoSupport("No matcher defined in editor.");
          return;
        }
        filterViewlet.addMatcher(matcher);
        matcherEditor.clear();
      }
    });
    //
    JButton replaceButton = new JButton("Replace >>"){
      @Override
      public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, super.getPreferredSize().height);
      }

      private static final long serialVersionUID = 1L;
    };
    buttonPanel.add(replaceButton);
    replaceButton.setPreferredSize(buttonSize);
    replaceButton.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
    //replaceButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    replaceButton.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(ActionEvent e) {
        Matcher matcher = matcherEditor.getMatcher();
        if (matcher == null) {
          session.errorNoSupport("No matcher defined in editor");
          return;
        }
        if (filterViewlet.getSelectedMatcher() == null) {
          session.errorNoSupport("No matcher selected in point filter.");
          return;
        }
        try {
          filterViewlet.replaceMatcher(matcher);
          matcherEditor.clear();
        }
        catch (Tool1Exception e2) {
          session.errorNoSupport("Cannot replace matcher because: "
              + e2.getMessage());
        }
      }
    });
    //    
    JButton editButton = new JButton("<< Edit"){
      @Override
      public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, super.getPreferredSize().height);
      }

      private static final long serialVersionUID = 1L;
    };
    buttonPanel.add(editButton);
    editButton.setPreferredSize(buttonSize);
    editButton.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
    //editButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    editButton.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(ActionEvent e) {
        Matcher matcher = filterViewlet.getSelectedMatcher();
        if (matcher == null) {
          session.errorNoSupport("No matcher selected in point filter.");
          return;
        }
        try {
          matcherEditor.setMatchPanel(matcher);
        }
        catch (Exception e2) {
          session.errorNoSupport("Cannot edit matcher because: "
              + e2.getMessage());
        }
      }
    });
    //    
    JButton removeButton = new JButton("<< Remove"){
      @Override
      public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, super.getPreferredSize().height);
      }

      private static final long serialVersionUID = 1L;
    };
    buttonPanel.add(removeButton);
    removeButton.setPreferredSize(buttonSize);
    removeButton.setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
    //removeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    removeButton.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(ActionEvent e) {
        Matcher matcher = filterViewlet.getSelectedMatcher();
        if (matcher == null) {
          session.errorNoSupport("No matcher selected in point filter.");
          return;
        }
        try {
          filterViewlet.removeMatcher(matcher);
          matcherEditor.setMatchPanel(matcher);
        }
        catch (Exception e2) {
          session.errorNoSupport("Cannot remove matcher because: "
              + e2.getMessage());
        }
      }
    });
//
    filterViewlet = new FilterViewlet(session);
    centerPanel.add(filterViewlet);
    filterViewlet.addTreeSelectionListener(new TreeSelectionListener() {
      @Override
			public void valueChanged(TreeSelectionEvent e) {
//        Matcher matcher = filterViewlet.getSelectedMatcher();
//        if (matcher != null) {
//          matcherEditor.clear();
//          matcherEditor.setMatchPanel(matcher);
//        }
      }
    });
    filterViewlet.setPreferredSize(new Dimension(310, 500));
    return centerPanel;
  }

  @Override
  protected boolean apply() {
    if (filterViewlet.getFilter().getChildCount() > 0) {
      // pcThread.stopRequest();
      dataSourceFilter = new DataSourceFilter(session, filterViewlet
          .getFilter());
      return true;
    }
    int answer = JOptionPane.showConfirmDialog(this,
        "Confirm: really use filter with no matches?", "No filter?",
        JOptionPane.YES_NO_OPTION);
    if (answer != JOptionPane.NO_OPTION) {
      dataSourceFilter = null;
      return true;
    }
    return false;
  }

  public DataSourceFilter getDataSourceFilter() {
    return dataSourceFilter;
  }

  @Override
	public void progressChanged(Object subject, String string, int min,
      int value, int max) {
    if (subject != dataSourceReader) {
      return;
    }
    if (min >= max) {
      progressBar.setIndeterminate(true);
    }
    else {
      progressBar.setIndeterminate(false);
      progressBar.setMinimum(min);
      progressBar.setValue(value);
      progressBar.setMaximum(max);
    }
    if (string == null) {
      progressBar.setString("line " + new Integer(value).toString());
    }
    else {
      progressBar.setString(string);
    }
  }

  private static final long serialVersionUID = 1L;
}
