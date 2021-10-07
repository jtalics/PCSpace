package gov.nih.ncgc.openhts.tool1.dirchooser;

import java.awt.event.ItemEvent;

import javax.swing.JToggleButton.ToggleButtonModel;

public class TristateButtonModel extends ToggleButtonModel {

    private static final long serialVersionUID = -6219407740209383087L;
    private TristateState state = TristateState.DESELECTED;

    public TristateButtonModel(final TristateState state) {
        setState(state);
    }

    public TristateButtonModel() {
        this(TristateState.DESELECTED);
    }

    public void setIndeterminate() {
        setState(TristateState.INDETERMINATE);
    }

    public boolean isIndeterminate() {
        return state == TristateState.INDETERMINATE;
    }

    // Overrides of superclass methods
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        // Restore state display
        displayState();
    }

    @Override
    public void setSelected(final boolean selected) {
        setState(selected ? TristateState.SELECTED : TristateState.DESELECTED);
    }

    // Empty overrides of superclass methods
    @Override
    public void setArmed(final boolean b) {
      // nop
    }

    @Override
    public void setPressed(final boolean b) {
      // nop
    }

    void iterateState() {
        setState(state.next());
    }

    private void setState(final TristateState state) {
        // Set internal state
        this.state = state;
        displayState();
        if (state == TristateState.INDETERMINATE && isEnabled()) {
            // force the events to fire

            // Send ChangeEvent
            fireStateChanged();

            // Send ItemEvent
            final int indeterminate = 3;
            fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, this, indeterminate));
        }
    }

    private void displayState() {
        super.setSelected(state != TristateState.DESELECTED);
        super.setArmed(state == TristateState.INDETERMINATE);
        super.setPressed(state == TristateState.INDETERMINATE);

    }

    public TristateState getState() {
        return state;
    }
}
