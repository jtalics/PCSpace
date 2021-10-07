package gov.nih.ncgc.openhts.tool1;


import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class DialogUtilities {
    // TODO rename this to WindowUtilities.centerWindow
	public static void centerDialog(final Window dialog) {
	  final Window owner = dialog.getOwner();
    Dimension ownerSize = owner.getSize();
        if (ownerSize.width == 0 || ownerSize.height == 0) {
            ownerSize = Toolkit.getDefaultToolkit().getScreenSize();
        }
		final Dimension dialogSize = dialog.getSize();
		int dx = 0;
		int dy = 0;

		if (dialogSize.width < ownerSize.width) {
			dx = (ownerSize.width - dialogSize.width) / 2;
		}

		if (dialogSize.height < ownerSize.height) {
			dy = (ownerSize.height - dialogSize.height) / 2;
		}
		
		final Point loc = dialog.getOwner().getLocation();
        loc.translate(dx, dy);
		dialog.setLocation(loc);
	}
    
    public static boolean confirmDelete(final Window owner, final String itemName) {
        return confirmDelete(owner, new String[]{itemName});
    }
    
	public static boolean confirmDelete(final Window owner, final String[] itemNamesToDelete) {
	    String message = "Click OK to delete";
	    JOptionPane optionPane;
	    if (itemNamesToDelete.length > 0 && itemNamesToDelete.length <= 10) {
            message += ":\n";
            for (final String itemName : itemNamesToDelete) {
                message += "  "+itemName+"\n"; 
            }
            optionPane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE,  JOptionPane.OK_CANCEL_OPTION);
        }
        else if (itemNamesToDelete.length > 10) {
            message += " "+itemNamesToDelete.length + " items?";
            optionPane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE,  JOptionPane.OK_CANCEL_OPTION);
        }
        else {
            message = "No items to delete.";
            JOptionPane.showMessageDialog(owner, message);
            return false;
        }

        final JDialog confirmDialog = optionPane.createDialog(owner, "Confirm delete");
        confirmDialog.pack();
        DialogUtilities.centerDialog(confirmDialog);
        confirmDialog.setVisible(true);
        final int selectedValue = ((Integer)optionPane.getValue()).intValue();

        if (selectedValue==JOptionPane.OK_OPTION) {
            return true;
        }
        return false;
    }
    
    public static void showInfo(final Window owner, final String title, final String message) {
        final JOptionPane optionPane = new JOptionPane(message, JOptionPane.WARNING_MESSAGE);

        final JDialog dlog = optionPane.createDialog(owner, title);
        dlog.pack();
        DialogUtilities.centerDialog(dlog);
        dlog.setVisible(true);
    }
}
