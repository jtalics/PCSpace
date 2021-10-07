package gov.nih.ncgc.openhts.tool1.util.mutableList;

import javax.swing.DefaultListModel;

//@author Santhosh Kumar T - santhosh@in.fiorano.com 
public class DefaultMutableListModel extends DefaultListModel implements MutableListModel{ 
    /**
   * 
   */
  private static final long serialVersionUID = 1L;

    @Override
		public boolean isCellEditable(final int index){ 
        return true; 
    } 
 
    @Override
		public void setValueAt(final Object value, final int index){ 
        super.setElementAt(value, index); 
    }
} 

