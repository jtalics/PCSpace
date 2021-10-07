package gov.nih.ncgc.openhts.tool1.util.mutableList;
import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextField;

//@author Santhosh Kumar T - santhosh@in.fiorano.com 
public class DefaultListCellEditor extends DefaultCellEditor implements ListCellEditor{ 
    /**
   * 
   */
  private static final long serialVersionUID = 1L;

    public DefaultListCellEditor(final JCheckBox checkBox){
      
        super(checkBox); 
    } 
 
    public DefaultListCellEditor(final JComboBox comboBox){ 
        super(comboBox); 
    } 
 
    public DefaultListCellEditor(final JTextField textField){ 
        super(textField); 
    } 
 
    @Override
		public Component getListCellEditorComponent(final JList list, final Object value, final boolean isSelected, final int index){ 
        delegate.setValue(value); 
        return editorComponent; 
    } 
}