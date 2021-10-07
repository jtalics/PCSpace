package gov.nih.ncgc.openhts.tool1.plotManager;

/**
 * Title:        plotter 2D
 * Description:  A 2d plotter
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author       Dong Yuan
 * @version      1.0
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class FontActionListener implements ActionListener
{
  JButton button;

  FontActionListener(final JButton button)
  {
    this.button = button;
  }

  @Override
	public void actionPerformed(final ActionEvent e)
  {
    final JButton selected = (JButton)e.getSource();

    final String text = selected.getText();

    button.setText(text);
    //fontButton.setName(name);

  }
}// end of file
