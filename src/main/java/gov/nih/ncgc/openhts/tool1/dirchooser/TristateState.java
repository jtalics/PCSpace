package gov.nih.ncgc.openhts.tool1.dirchooser;
public enum TristateState {
    SELECTED {
      @Override
      public TristateState next() {
        return INDETERMINATE;
      }
    },
    INDETERMINATE {
      @Override
      public TristateState next() {
        return DESELECTED;
      }
    },
    DESELECTED {
      @Override
      public TristateState next() {
        return SELECTED;
      }
    };

    public abstract TristateState next();
  }
    