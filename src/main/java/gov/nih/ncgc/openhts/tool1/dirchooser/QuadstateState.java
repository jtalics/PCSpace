package gov.nih.ncgc.openhts.tool1.dirchooser;

public enum QuadstateState {

    UnselectedUngrayed {


        @Override
        public QuadstateState setGrayed(final boolean selected) {
            return selected?UnselectedGrayed:UnselectedUngrayed;
        }

        @Override
        public QuadstateState setSelected(final boolean selected) {
            return selected?SelectedUngrayed:UnselectedUngrayed;
        }

        @Override
        public boolean isSelected() {
            return false;

        }

        @Override
        public boolean isGrayed() {
            return false;

        }
    },
    // 
    UnselectedGrayed {


        @Override
        public QuadstateState setGrayed(final boolean selected) {
            return selected?UnselectedGrayed:UnselectedUngrayed;
        }

        @Override
        public QuadstateState setSelected(final boolean selected) {
            return selected?SelectedGrayed:UnselectedGrayed;
        }

        @Override
        public boolean isSelected() {
            return false;
        }

        @Override
        public boolean isGrayed() {
            return true;
        }
    },
    //
    SelectedUngrayed {

        @Override
        public QuadstateState setGrayed(final boolean selected) {
            return selected?SelectedGrayed:SelectedUngrayed;
        }

        @Override
        public QuadstateState setSelected(final boolean selected) {
            return selected?SelectedUngrayed:UnselectedUngrayed;
        }


        @Override
        public boolean isSelected() {
            return true;

        }

        @Override
        public boolean isGrayed() {
            return false;

        }
    },
    //
    SelectedGrayed {

        @Override
        public QuadstateState setGrayed(final boolean selected) {
            return selected?SelectedGrayed:SelectedUngrayed;
        }

        @Override
        public QuadstateState setSelected(final boolean selected) {
            return selected?SelectedGrayed:UnselectedGrayed;
        }

        @Override
        public boolean isSelected() {
            return true;

        }

        @Override
        public boolean isGrayed() {
            return true;

        }
    };

    // Leaves the grayed-ness alone
    public abstract QuadstateState setSelected(boolean selected);

    // Leaves selected-ness alone
    public abstract QuadstateState setGrayed(boolean grayed);

    public abstract boolean isSelected();

    public abstract boolean isGrayed();
}
