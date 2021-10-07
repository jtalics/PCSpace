package gov.nih.ncgc.openhts.tool1.dirchooser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;

/**
 * Purpose: chooses directories modally like JFileChooser selects files Usage:
 * 
 * 
 */
public class DirChooser extends AbstractDialog {

    private static final long serialVersionUID = 1L;
    private final DirChooserPanel dirChooserPanel;

    public DirChooser(final Window owner, final SelectionMode selectionMode, final String title, final List<String> initialPaths) {
        super(owner);
        setTitle(title);
        setLayout(new BorderLayout());
        setModal(true);
        add(dirChooserPanel = new DirChooserPanel(owner, selectionMode, initialPaths));

        final JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));
        add(southPanel, BorderLayout.SOUTH);
        final JCheckBox checkBox = new JCheckBox("Automatically include subfolders");
        checkBox.setSelected(checkBox.isSelected());
        checkBox.addActionListener(new ActionListener() {

            @Override
						public void actionPerformed(final ActionEvent e) {
                dirChooserPanel.setRecurseSelection(checkBox.isSelected());
            }
        });
        southPanel.add(checkBox);
        southPanel.add(getButtonPanel());

        pack();
    }

    @Override
    protected boolean apply() {
        if (dirChooserPanel.getSelectionCount() <= 0) {
            JOptionPane.showMessageDialog(dirChooserPanel, "Select a directory or cancel.");
            return false;
        }
        return true;
    }

    // This will be called after AbstractDialog closes.
    public String[] getSelections() {
        final File[] files = dirChooserPanel.getSelections();
        final String[] paths = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            paths[i] = files[i].getPath();
        }
        return paths;
    }

    public static void main(final String[] av) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (final UnsupportedLookAndFeelException e) {
            // handle exception
        } catch (final ClassNotFoundException e) {
            // handle exception
        } catch (final InstantiationException e) {
            // handle exception
        } catch (final IllegalAccessException e) {
            // handle exception
        }
        final JFrame frame = new JFrame("TestDirChooser");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setForeground(Color.black);
        frame.setBackground(Color.lightGray);
        final DirChooser dirChooser = new DirChooser(frame, SelectionMode.Multiple, "title", null);
        dirChooser.setTitle("Select directories");
        dirChooser.setVisible(true);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
