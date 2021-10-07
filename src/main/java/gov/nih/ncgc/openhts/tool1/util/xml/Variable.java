/**************************************************************************
 Filename:      Variable.java
 Written by:    Nicholas J Schneble
 NeuroStar Solutions Inc.
 Date started:  10/05/2003
 Last modified: MAY 2007
 **************************************************************************/
package gov.nih.ncgc.openhts.tool1.util.xml;

import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Stores a variable name-value pair read from an INI file.
 */
public class Variable {
  
  public enum Kind {
    TextField, // input a string
    ComboBox   // select an option
  }

  private String options[] = null;

  private Kind kind = Kind.TextField; // Default

  /**
   * This constant is the name of this class.
   */
  private final String CLASS_NAME = "Variable";

  /**
   * This constant is the error message displayed when an invalid label is
   * encountered.
   */
  private final String LABEL_ERROR_MESSAGE = "The label of a variable cannot be null.";
  /**
   * This constant is the error message displayed when invalid text is
   * encountered.
   */
  private final String TEXT_ERROR_MESSAGE = "The text of a variable cannot be null.";
  /**
   * This variable is the label of this variable.
   */
  private String name;
  /**
   * This variable is the text of this variable.
   */
  private String value;
  /**
   * This variable is the transparency setting of the label of this variable.
   */
  private boolean transparencySetting = false;

  /**
   *
   */
  Variable(final String label, final String value, final boolean transparencySetting) {
    super();
    //setViewlet(new VariableViewlet(app,this, null));
    setLabel(label);
    this.value = value;
    setTransparent(transparencySetting);
  }

  /**
   *
   */
  boolean isTransparent() {
    // Statements
    return transparencySetting;
  }

  /**
   * This selectedFunction sets the label of this variable to the specified label.
   */
  void setLabel(final String name) {
    // Statements
    if (name != null) {
      this.name = name;
    }
    else {
      // nop
    }
  }

  /**
   * Assigns a value to the name of this variable
   */
  void setValue(final String value) {
    if (value != null) {
      this.value = value;
    }
    else {
      // nop
    }
  }

  /**
   * This selectedFunction sets the transparency setting of the label of this variable
   * to the specified transparency setting.
   * 
   * @param transparencySetting
   *          The specified transparency setting
   */
  void setTransparent(final boolean transparencySetting) {
    // Statements
    this.transparencySetting = transparencySetting;
    return;
  }

  /**
   * Updates the specified variable based on the contents of the specified
   * display.
   */
  void update(final JPanel panel) {
    final JTextField textField = (JTextField) panel.getComponent(1);
    if (!value.equals(textField.getText())) {
      value = textField.getText();
    }
  }

  void setKind(final Kind kind) {
    this.kind = kind;
  }

  void setOptions(final String[] options) {
    this.options = options;
  }

  Kind getKind() {
    return kind;
  }

  String[] getOptions() {
    return options;
  }

  
  /**
   * @return
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * @return
   */
  public String getValue() {
    return value;
  }
}
// end of file
