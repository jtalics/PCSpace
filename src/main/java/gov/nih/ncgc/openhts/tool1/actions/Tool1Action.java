package gov.nih.ncgc.openhts.tool1.actions;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

public abstract class Tool1Action extends AbstractAction {
  
  public Tool1Action() {
    super();
  }
  
  public Tool1Action(String string, Icon icon) {
    super(string, icon);
  }

  public void setName(final String name) {
    putValue(Action.NAME, name);
  }

}
