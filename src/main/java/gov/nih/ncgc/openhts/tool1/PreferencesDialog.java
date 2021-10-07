package gov.nih.ncgc.openhts.tool1;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import gov.nih.ncgc.openhts.tool1.dialogManager.AbstractDialog;


/** Purpose is to ...
 * @author talafousj
 *
 */
public final class PreferencesDialog extends AbstractDialog {

    private static final long serialVersionUID = 1L;
    private SpringLayout valuesLayout = null;
    private JPanel valuesContentPane = null;
    private JPanel actionContentPane = null;
    private JPanel mainContentPane = null;
    private JTextField licenseTextField = null;
    private JTextField cacheSizeTextField = null;
    private JTextField pollingIntervalTextField = null;
    private JComboBox rewindSecondsComboBox = null;
    private JPanel secondaryActionContentPane = null;
    private JButton restoreDefaultsButton = null;

    public PreferencesDialog(final Session owner) {
        super(owner);
        this.setTitle("Preferences");
        this.setContentPane(getMainContentPane());
        getButtonPanel().setHelpId("mapkey.top");
    }

    private JPanel getMainContentPane() {
        if (null == mainContentPane) {
            mainContentPane = new JPanel();
            final BorderLayout layout = new BorderLayout();
            mainContentPane.setLayout(layout);
            layout.setHgap(5);
            layout.setVgap(5);
            mainContentPane.add(getValuesContentPane(), BorderLayout.CENTER);
            mainContentPane.add(getActionContentPane(), BorderLayout.PAGE_END);
        }
        return mainContentPane;
    }

    private JPanel getActionContentPane() {
        if (null == actionContentPane) {
            actionContentPane = new JPanel();
            actionContentPane.setLayout(new BoxLayout(actionContentPane, BoxLayout.Y_AXIS));
            actionContentPane.add(getSecondaryActionContentPane());
            actionContentPane.add(Box.createVerticalStrut(8));
            actionContentPane.add(new JSeparator(SwingConstants.HORIZONTAL));
            actionContentPane.add(getButtonPanel());
            actionContentPane.add(Box.createVerticalStrut(8));
        }
        return actionContentPane;
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getValuesContentPane() {
        if (valuesContentPane == null) {
            valuesContentPane = new JPanel();
            valuesLayout = new SpringLayout();
            valuesContentPane.setLayout(valuesLayout);

            final JLabel licenseLabel = new JLabel("License:", SwingConstants.TRAILING);
            licenseLabel.setLabelFor(getLicenseTextField());
            final JLabel cacheSizeLabel = new JLabel("Cache Size (MB):", SwingConstants.TRAILING);
            cacheSizeLabel.setLabelFor(getCacheSizeTextField());
            final JLabel validCacheSizeRangeLabel = new JLabel("(" + GlobalSettings.CACHE_SIZE_MINIMUM + "-" + GlobalSettings.CACHE_SIZE_MAXIMUM + ")");
            final JLabel pollingIntervalLabel = new JLabel("File System Polling Interval (Seconds):", SwingConstants.TRAILING);
            pollingIntervalLabel.setLabelFor(getPollingIntervalTextField());
            final JLabel validPollingIntervalRangeLabel = new JLabel("(" + GlobalSettings.POLLINGINTERVALSECONDS_MINIMUM + "-" + GlobalSettings.POLLINGINTERVALSECONDS_MAXIMUM + ")");
            final JLabel rewindSecondsLabel = new JLabel("Hit Playback Rewind Seconds:", SwingConstants.TRAILING);
            rewindSecondsLabel.setLabelFor(getRewindSecondsComboBox());

            valuesContentPane.add(licenseLabel);
            valuesContentPane.add(cacheSizeLabel);
            valuesContentPane.add(getCacheSizeTextField());
            valuesContentPane.add(validCacheSizeRangeLabel);
            valuesContentPane.add(pollingIntervalLabel);
            valuesContentPane.add(getPollingIntervalTextField());
            valuesContentPane.add(validPollingIntervalRangeLabel);
            valuesContentPane.add(rewindSecondsLabel);
            valuesContentPane.add(getRewindSecondsComboBox());
            valuesContentPane.add(new JLabel());
            makeCompactGrid(valuesContentPane, 4, 3, // rows, cols
                    6, 6, // initX, initY
                    6, 6); // xPad, yPad
        }
        return valuesContentPane;
    }

    private JTextField getLicenseTextField() {
        if (licenseTextField == null) {
            licenseTextField = new JTextField();
            licenseTextField.setPreferredSize(new Dimension(400, 20));
            licenseTextField.setEnabled(false);
        }
        return licenseTextField;
    }

    private JTextField getCacheSizeTextField() {
        if (cacheSizeTextField == null) {
            cacheSizeTextField = new JTextField();
            cacheSizeTextField.setPreferredSize(new Dimension(400, 20));
        }
        return cacheSizeTextField;
    }

    private JTextField getPollingIntervalTextField() {
        if (pollingIntervalTextField == null) {
            pollingIntervalTextField = new JTextField();
            pollingIntervalTextField.setPreferredSize(new Dimension(400, 20));
        }
        return pollingIntervalTextField;
    }

    private JComboBox getRewindSecondsComboBox() {
        if (rewindSecondsComboBox == null) {
            rewindSecondsComboBox = new JComboBox();
            rewindSecondsComboBox.addItem(Float.valueOf(0f));
            rewindSecondsComboBox.addItem(Float.valueOf(0.5f));
            rewindSecondsComboBox.addItem(Float.valueOf(1f));
            rewindSecondsComboBox.addItem(Float.valueOf(2f));
        }
        return rewindSecondsComboBox;
    }
    
    public void showPreferencesDialog() {
        pack();

        final Point loc = getParent().getLocation();
        loc.translate(20, 20);
        setLocation(loc);

        getCacheSizeTextField().setText(GlobalSettings.getInstance().getProperty(GlobalSettings.CACHEMB_KEY));
        getPollingIntervalTextField().setText(GlobalSettings.getInstance().getProperty(GlobalSettings.POLLINGINTERVALSECONDS_KEY));

        setModal(true);
        setVisible(true);
    }
    private JPanel getSecondaryActionContentPane() {
        if (secondaryActionContentPane == null) {
            secondaryActionContentPane = new JPanel();
            secondaryActionContentPane.setLayout(new BoxLayout(secondaryActionContentPane,BoxLayout.X_AXIS));
            secondaryActionContentPane.add(Box.createHorizontalGlue());
            secondaryActionContentPane.add(getRestoreDefaultsButton());
            secondaryActionContentPane.add(Box.createHorizontalStrut(8));
        }
        return secondaryActionContentPane;
    }
    
    private JButton getRestoreDefaultsButton() {
        if (restoreDefaultsButton == null) {
            restoreDefaultsButton = new JButton();
            restoreDefaultsButton.addActionListener(new ActionListener() {
                @Override
								public void actionPerformed(final ActionEvent arg0) {
                    
                    getCacheSizeTextField().setText(GlobalSettings.CACHEMB_DEFAULT_VALUE);
                    getPollingIntervalTextField().setText(GlobalSettings.POLLINGINTERVALSECONDS_DEFAULT_VALUE);
                }
            });
            restoreDefaultsButton.setText("Restore Defaults");
            restoreDefaultsButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
            restoreDefaultsButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        }
        return restoreDefaultsButton;
    }
    /* Used by makeCompactGrid. */
    private static SpringLayout.Constraints getConstraintsForCell(final int row, final int col, final Container parent, final int cols) {
        final SpringLayout layout = (SpringLayout) parent.getLayout();
        final Component c = parent.getComponent(row * cols + col);
        return layout.getConstraints(c);
    }

    /**
     * Aligns the first <code>rows</code> * <code>cols</code> components of <code>parent</code> in a grid. Each component in a column is as wide as the maximum preferred width of the components in that column; height is similarly determined for each row. The parent is made just big enough to fit them all.
     * 
     * @param rows
     *            number of rows
     * @param cols
     *            number of columns
     * @param initialX
     *            x location to start the grid at
     * @param initialY
     *            y location to start the grid at
     * @param xPad
     *            x padding between cells
     * @param yPad
     *            y padding between cells
     */
    public static void makeCompactGrid(final Container parent, final int rows, final int cols, final int initialX, final int initialY, final int xPad, final int yPad) {
        SpringLayout layout;
        try {
            layout = (SpringLayout) parent.getLayout();
        } catch (final ClassCastException exc) {
            System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
            return;
        }

        // Align all cells in each column and make them the same width.
        Spring x = Spring.constant(initialX);
        for (int c = 0; c < cols; c++) {
            Spring width = Spring.constant(0);
            for (int r = 0; r < rows; r++) {
                width = Spring.max(width, getConstraintsForCell(r, c, parent, cols).getWidth());
            }
            for (int r = 0; r < rows; r++) {
                final SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, cols);
                constraints.setX(x);
                constraints.setWidth(width);
            }
            x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
        }

        // Align all cells in each row and make them the same height.
        Spring y = Spring.constant(initialY);
        for (int r = 0; r < rows; r++) {
            Spring height = Spring.constant(0);
            for (int c = 0; c < cols; c++) {
                height = Spring.max(height, getConstraintsForCell(r, c, parent, cols).getHeight());
            }
            for (int c = 0; c < cols; c++) {
                final SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, cols);
                constraints.setY(y);
                constraints.setHeight(height);
            }
            y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
        }

        // Set the parent's size.
        final SpringLayout.Constraints pCons = layout.getConstraints(parent);
        pCons.setConstraint(SpringLayout.SOUTH, y);
        pCons.setConstraint(SpringLayout.EAST, x);
    }

    @Override
    protected void doCancel() {
        dispose();
    }

    private boolean validValues() {
        try {
            final long cacheSize = Long.parseLong(getCacheSizeTextField().getText());
            if (cacheSize < GlobalSettings.CACHE_SIZE_MINIMUM || cacheSize > GlobalSettings.CACHE_SIZE_MAXIMUM) {
                JOptionPane.showMessageDialog(this, "'Cache Size' value must be between " + GlobalSettings.CACHE_SIZE_MINIMUM + " and " + GlobalSettings.CACHE_SIZE_MAXIMUM + ".");
                getCacheSizeTextField().requestFocus();
                return false;
            }
        } catch (final NumberFormatException x) {
            JOptionPane.showMessageDialog(this, "'Cache Size' contains invalid characters.");
            getCacheSizeTextField().requestFocus();
            return false;
        }
        
        try {
            final long pollingInterval = Integer.parseInt(getPollingIntervalTextField().getText());
            if (pollingInterval < GlobalSettings.POLLINGINTERVALSECONDS_MINIMUM || pollingInterval > GlobalSettings.POLLINGINTERVALSECONDS_MAXIMUM) {
                JOptionPane.showMessageDialog(this, "'Polling Interval' value must be between " + GlobalSettings.POLLINGINTERVALSECONDS_MINIMUM + " and " + GlobalSettings.POLLINGINTERVALSECONDS_MAXIMUM + ".");
                getPollingIntervalTextField().requestFocus();
                return false;
            }
        } catch (final NumberFormatException x) {
            JOptionPane.showMessageDialog(this, "'Polling Interval' contains invalid characters.");
            getPollingIntervalTextField().requestFocus();
            return false;
        }
        
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     */
    @Override
    protected void doApply() {
        if ( ! validValues() ) {
            return;
        }
        GlobalSettings.getInstance().putProperty(GlobalSettings.CACHEMB_KEY, getCacheSizeTextField().getText());
        GlobalSettings.getInstance().putProperty(GlobalSettings.POLLINGINTERVALSECONDS_KEY, getPollingIntervalTextField().getText());
        dispose();
    }
}
