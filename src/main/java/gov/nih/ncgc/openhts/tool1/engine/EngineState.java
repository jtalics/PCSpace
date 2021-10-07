package gov.nih.ncgc.openhts.tool1.engine;


/** Purpose is to ...
 * @author talafousj
 *
 */
public class EngineState {
    public static final EngineState STOPPED = new EngineState("stopped");
    public static final EngineState IDLE = new EngineState("idle");
    public static final EngineState BUSY = new EngineState("busy");
    public static final EngineState PAUSED = new EngineState("paused");
    
    private String name;
    
    private EngineState(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof EngineState) {
            return name.equals(((EngineState) o).name);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "EngineState[" + name + "]";
    }
}
