package gov.nih.ncgc.openhts.tool1.util.xml;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Represents the file with xml format that is to be editted, in either filtered
 * or unfiltered manner.
 * 
 * @author talafousj
 */
public class XmlFilteredViewlet extends JPanel {
  /**
   * The DOM document that will get filled out by the parser for this file
   */
  private Document document;
  // We need to remember which Variable corresponded to which DOM node
  // because we can't just scan the document using the variable name because
  // there may be the same name at different places in the hierarchy.
  private final Map<Variable, Node> uiToDomNodeMap = new HashMap<Variable, Node>();
  private NodeList displayableElementNodeList;
  private final File xmlFile;
  private final List<ChangeListener> listeners = new ArrayList<ChangeListener>();

  public enum DomNodeKind {
    // User assumes non-terminal node
    Variable,
    // User expects further level nodes
    Tree
  }

  public XmlFilteredViewlet(final File xmlFile) {
    this.xmlFile = xmlFile;
    setLayout(new BorderLayout());
  }

  public XmlFilteredViewlet(final String xmlFileName) {
    this(new File(xmlFileName));
  }

  public void build() {
    // Vector<Object> xmlFile = new Vector<Object>();
    // File name as zeroth entry
    // xmlFile.add(xmlFileName);
    // DOM document as first entry
    // Document document = parseXmlFile(xmlFileName);
    // if (document == null) {
    // throw new IOException("not parseable as XML");
    // }
    // xmlFile.add(document);
    // Build the panel of a list of displayable-elements
    // JPanel panel = generateXmlDisplay(xmlFile, xmlFiles.size(),
    // displayableElementNodeList);
    // public static JPanel generateXmlDisplay(Vector<Object> xmlFile, final int
    // index, NodeList displayableElementNodeList) {
    final JPanel center = new JPanel(new BorderLayout());
    final JPanel south = new JPanel(new GridLayout(2, 1));
    final JPanel subsouth1 = new JPanel(new FlowLayout());
    final JPanel subsouth2 = new JPanel(new FlowLayout());
    add(new JLabel("Viewing file: "+xmlFile.getPath()),BorderLayout.NORTH);
    // Put Variable object(s) in the editor for each displayable node
    if (document == null) {
      center.add(new JLabel(" Error in file '" + xmlFile.getName()+"'"));
    }
    else {
    if (displayableElementNodeList == null) {
      // Put the whole xmlfile up as a tree
      final NodeList matchedNodeList = getXPathMatches(document, "/*");
      if (matchedNodeList.getLength() != 1) {
        throw new RuntimeException("bad xmlfile: " + xmlFile.getPath());
      }
      final Node node = matchedNodeList.item(0);
      center.add(new XmlTreeViewlet(document, node, node.getTextContent(),
          true, "editable").build(), BorderLayout.CENTER);
    }
    else {
      final JPanel subpanelCenter = new JPanel(new BorderLayout());
      subpanelCenter.setBorder(new CompoundBorder(new EmptyBorder(5, 5, 5, 5),
          new EtchedBorder()));
      subpanelCenter.setLayout(new BoxLayout(subpanelCenter,
          BoxLayout.PAGE_AXIS));
      for (int iNode = 0; iNode < displayableElementNodeList.getLength(); iNode++) {
        final Element element = (Element) displayableElementNodeList
            .item(iNode);
        final String nodeName = element.getNodeName();
        // Get the xpath from the node specified in the tag in iniconfig.xml
        final String expression = element.getAttributes().getNamedItem("xpath")
            .getNodeValue();
        if (expression == null) {
          System.out.println("problem with node='" + nodeName + "': no xpath");
        }
        // Get the nodes that match the xpath expression
        final NodeList matchedNodeList = getXPathMatches(document, expression);
        if (matchedNodeList.getLength() < 1) {
          // no params, input error
          System.out.println("no match in file='" + xmlFile.getName()
              + "' for xpath='" + expression + "'");
          continue;
        }
        // 
        boolean editable = false;
        String description = "";
        String displayName = null;
        // Kind = variable means terminal node, kind = tree means non-terminal
        String kind = null; // enum
        Node attNode = element.getAttributes().getNamedItem("editable");
        if (attNode != null) {
          final String s = attNode.getNodeValue();
          if (s.equals("true")) {
            editable = true;
          }
          else if (s.equals("false")) {
            editable = false;
          }
          else {
            System.out.println("  problem with node='" + nodeName
                + "', illegal editable = '" + s + "'");
            continue;
          }
        }
        attNode = element.getAttributes().getNamedItem("description");
        if (attNode != null) {
          description = attNode.getNodeValue();
        }
        attNode = element.getAttributes().getNamedItem("display-name");
        if (attNode != null) {
          displayName = attNode.getNodeValue();
        }
        DomNodeKind domNodeKind = DomNodeKind.Variable; // default
        attNode = element.getAttributes().getNamedItem("kind");
        if (attNode != null) {
          kind = attNode.getNodeValue();
          if (kind.equals("variable")) {
            domNodeKind = DomNodeKind.Variable;
          }
          else if (kind.equals("tree")) {
            domNodeKind = DomNodeKind.Tree;
          }
          else {
            System.out.println("  problem with node='" + element.getNodeName()
                + "', illegal kind = '" + kind + "'");
            continue;
          }
        }
        // OK, now build the UI with the info just gotten
        for (int iMatchedNode = 0; iMatchedNode < matchedNodeList.getLength(); iMatchedNode++) {
          // Display name and value of this DOM node
          final Node matchedNode = matchedNodeList.item(iMatchedNode);
          JPanel subsubpanel = null;
          switch (domNodeKind) {
          case Variable:
            if (displayName == null) {
              displayName = matchedNode.getNodeName();
            }
            description += "(xml tag='" + matchedNode.getNodeName() + "')";
            final String paramValue = matchedNode.getNodeValue();
            if (paramValue != null) {
              // User wants pair only, so present as Variable
              final Variable variable = new Variable(displayName, paramValue,
                  false);
              subsubpanel = new VariableViewlet(variable, editable,
                  description, null).build();
              // Remember which node was used to construct this DOM node so
              // that we can return the users data to it during a save.
              uiToDomNodeMap.put(variable, matchedNode);
            }
            else {
              System.out.println("  no value for node = "
                  + matchedNode.getNodeName() + " (which matched xpath='"
                  + expression + "')");
            }
            break;
          case Tree:
            subsubpanel = new XmlTreeViewlet(document, matchedNode,
                displayName, editable, description).build();
            // System.out.println("request for disabled XmlViewlet");
            break;
          }
          if (subsubpanel != null) {
            subpanelCenter.add(subsubpanel, BorderLayout.CENTER);
          }
          /* Do not display attribute nodes, use XPath to expose them */
        }
      }
      center.add(subpanelCenter, BorderLayout.CENTER);
    }
    } // end else if
    // Now do SOUTH
    // SAVE button
    JButton button = new JButton("Save");
    // button.setFont(GuiFrame.FONT);
    button.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
        save();
      }
    });
    subsouth1.add(button);
    // Load button
    button = new JButton("Reload");
    button.addActionListener(new ActionListener() {
      @Override
			public void actionPerformed(final ActionEvent e) {
          try {
            load();
          }
          catch (Exception e1) {
            e1.printStackTrace();
          }
      }
    });
    subsouth1.add(button);
    south.add(subsouth1);
    add(south, BorderLayout.SOUTH);
    add(center, BorderLayout.CENTER);
    // Put the panel inside a scrollpane
    // xmlFiles.add(xmlFile);
    // xmlFiles.add(scrollPane);
  }

  /**
   * @param document
   * @param expression
   * @return
   */
  public static NodeList getXPathMatches(final Document d,
      final String expression) {
    // Get the elements that match the XPath expression
    final XPath xpath = XPathFactory.newInstance().newXPath();
    NodeList nodeList = null;
    try {
      nodeList = (NodeList) xpath.evaluate(expression, d,
          XPathConstants.NODESET);
    }
    catch (final XPathExpressionException e) {
      System.out.println("bad xpath expression: " + expression);
    }
    return nodeList;
  }

  /**
   * This selectedFunction processes the data from the specified *.XML file.
   * 
   * @throws IOException
   * @throws ParserConfigurationException 
   * @throws SAXException 
   */
  public static Document parseXmlFileName(final String xmlFileName)
      throws IOException, SAXException, ParserConfigurationException {
    File xmlFile = new File(xmlFileName);
    if (!xmlFile.exists()) {
      throw new IOException(xmlFile + " does not exist");
    }
    return parseXmlFile(xmlFile);
  }

  public static Document parseXmlFile(File xmlFile) throws IOException, SAXException, ParserConfigurationException {
    if (!xmlFile.canRead()) {
      throw new IOException("Cannot read file: "+ xmlFile.getPath());
    }
    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(xmlFile));
    return parseXmlStream(bis);
  }
  
  public static Document parseXmlStream(BufferedInputStream bis) throws SAXException, ParserConfigurationException, IOException {
    Document doc = null;
    final DocumentBuilderFactory factory = DocumentBuilderFactory
    .newInstance();
    // factory.setValidating(true);
    // factory.setSchema(null);
    final DocumentBuilder documentBuilder;
      documentBuilder = factory.newDocumentBuilder();
      doc = documentBuilder.parse(bis);
      if (doc == null) {
        throw new IOException("not parseable as XML");
      }
    return doc;
  }

  /**
   * Save the data from the GUI to file. There is the intermediate step of using
   * the DOM document.
   * 
   * @param index
   * @param promptForReinstall
   */
  void save(/* int index */) {
    // Vector<Object> xmlFile = (Vector<Object>) xmlFiles.elementAt(index);
    // JScrollPane scrollPane = (JScrollPane) xmlFiles.elementAt(index + 1);
    // JPanel panel = (JPanel) scrollPane.getViewport().getView();
    // JPanel subPanel = (JPanel) panel.getComponent(0);
    // int nComponents=subPanel.getComponentCount();
    // if (nComponents <= 0) {
    // Iniconfig.xml had no displayable-elements
    // System.out.print("subPanel in " + xmlFile.get(0) + " has no
    // subcomponents");
    // Don't return -- write the DOM out anyways to normalize format
    // }
    // Document document = (Document) xmlFile.elementAt(VINDEX_PARSED);
    if (document == null) {
      // Can be empty but not null
      throw new RuntimeException("document is null");
    }
    /*
     * // Now modify the DOM tree with the info from the UI components Map<JTextField,
     * Node> map = (Map<JTextField, Node>) xmlFile.get(GUI.VINDEX_MAP); for
     * (int i = 0; i < nComponents; ++i) { JPanel subSubPanel = (JPanel)
     * subPanel.getComponent(i); JTextField textfield = (JTextField)
     * subSubPanel.getComponent(1); Node node = map.get(textfield); if
     * (node==null) { throw new RuntimeException("node not mapped to
     * textfield"); } node.setNodeValue(textfield.getText()); }
     */
    try {
      // String xmlFileName = (String)
      // xmlFile.elementAt(Editor.VINDEX_FILE_NAME);
      // document = (Document) xmlFile.elementAt(Editor.VINDEX_PARSED);
      if (document == null) {
        throw new RuntimeException("document is null");
      }
      // Prepare the output file
      final Result result = new StreamResult(xmlFile);
      // Write the DOM document to the file
      final Transformer tf = TransformerFactory.newInstance().newTransformer();
      tf.setOutputProperty(OutputKeys.INDENT, "yes");
      // tf.setOutputProperty("{http://xml. customer .org/xslt}indent-amount",
      // "4");
      tf.transform(new DOMSource(document), result);
    }
    catch (final TransformerFactoryConfigurationError e) {
      e.printStackTrace();
    }
    catch (final TransformerException e) {
      e.printStackTrace();
    }
    // if (promptForReinstall) promptForReinstall((String)
    // xmlFile.elementAt(0));
  }

  public void load() throws IOException, SAXException, ParserConfigurationException {
    document = parseXmlFile(xmlFile);
  }

  public void setDisplayableElementNodeList(
      final NodeList displayableElementNodeList) {
    this.displayableElementNodeList = displayableElementNodeList;
  }

  private static final long serialVersionUID = 1L;
}
// end of file
