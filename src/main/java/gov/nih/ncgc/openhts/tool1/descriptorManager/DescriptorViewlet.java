package gov.nih.ncgc.openhts.tool1.descriptorManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import gov.nih.ncgc.openhts.tool1.Session;
import gov.nih.ncgc.openhts.tool1.util.xml.XmlFilteredViewlet;

/**
 * Purpose is to provide a view for a descriptor.
 * 
 * @author talafousj
 */
public class DescriptorViewlet extends JPanel {
  private final Session session;
  private final JScrollPane scrollPane;
  private Descriptor descriptor;

  public DescriptorViewlet(final Session session) {
    this.session = session;
    setLayout(new BorderLayout());
    setBackground(Color.LIGHT_GRAY);
    // String[] directoryContents = session.descriptorManager.homeDir.list();
    setBorder(new EmptyBorder(5, 5, 5, 5));
    // Get all the interesting XML files as specified by the file
    // "iniconfig.xml". First, parse iniconfig.xml into the DOM document
    final JLabel label = new JLabel("select descriptor");
    label.setHorizontalAlignment(SwingConstants.CENTER);
    scrollPane = new JScrollPane(label,
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setWheelScrollingEnabled(true);
    add(scrollPane, BorderLayout.CENTER);
  }

  public void changeDescriptor(final Descriptor descriptor) {
    if (descriptor == null) {
      throw new RuntimeException("null descriptor");
    }
    if (descriptor.xmlFile != null) {
      if (!descriptor.xmlFile.canRead()) {
        throw new RuntimeException("File cannot be read: " + descriptor.xmlFile);
      }
      XmlFilteredViewlet xmlFilteredViewlet;
      this.descriptor = descriptor;
      xmlFilteredViewlet = new XmlFilteredViewlet(
          descriptor.runCommandXmlFileName);
      try {
        xmlFilteredViewlet.load();
        xmlFilteredViewlet.build();
        scrollPane.setViewportView(xmlFilteredViewlet);
      }
      catch (IOException e) {
        scrollPane.setViewportView(new JLabel("Cannot load descriptor because: "+e));
        // TODO: add how to fixit tip
      }
      catch (SAXException e) {
        scrollPane.setViewportView(new JLabel("Cannot load descriptor because: "+e));
        // TODO: add how to fixit tip
      }
      catch (ParserConfigurationException e) {
        scrollPane.setViewportView(new JLabel("Cannot load descriptor because: "+e));
        // TODO: add how to fixit tip
      }
    }
    else {
      final JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.add(new JLabel("Descriptor name: " + descriptor.getName()));
      panel.add(new JLabel("Descriptor kind: " + descriptor.getDescriptorKind()));
      panel.add(new JLabel("Descriptor xmlFile: " + descriptor.xmlFile));
      panel.add(new JLabel("Descriptor run command xmlFile: "
          + descriptor.runCommandXmlFileName));
      scrollPane.setViewportView(panel);
    }
  }

  private static final long serialVersionUID = 1L;
}
