package gov.nih.ncgc.openhts.tool1.dirchooser;

import javax.swing.JToggleButton.ToggleButtonModel;

/**
 * Purpose: Usage:
 * 
 */
public class QuadstateButtonModel extends ToggleButtonModel {

    private QuadstateState state;

    /**
     * @param state
     */
    public QuadstateButtonModel(final QuadstateState state) {
        setState(state);
    }

    @Override
    public void setSelected(final boolean selected) {
        setState(getState().setSelected(selected));
    }

    @Override
    public boolean isSelected() {
        return getState().isSelected();
    }

    /**
     * @param grayed
     */
    public void setGrayed(final boolean grayed) {
        setState(getState().setGrayed(grayed));
    }

    /**
     * @return
     */
    public boolean isGrayed() {
        return getState().isGrayed();
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

    /**
     * @param state
     */
    private void setState(final QuadstateState state) {
        if (state == null) {
            throw new RuntimeException("state is null");
        }
        // Set internal state
        this.state = state;
        super.setSelected(state.isSelected());
        super.setArmed(state.isGrayed());
        super.setPressed(state.isGrayed());
        /*
         * if (state == QuadstateState.INDETERMINATE && isEnabled()) { // force the events to fire
         *  // Send ChangeEvent fireStateChanged();
         *  // Send ItemEvent int indeterminate = 3; fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, this, indeterminate)); }
         */
    }

    /**
     * @return
     */
    public QuadstateState getState() {
        return state;
    }

    public void iterateState() {
        throw new RuntimeException("todo");
    }

    private static final long serialVersionUID = 1L;

}
