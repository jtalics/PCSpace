package gov.nih.ncgc.openhts.tool1;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import gov.nih.ncgc.openhts.tool1.persistence.Db4oEnabled;
import gov.nih.ncgc.openhts.tool1.persistence.PersistenceContext;
import gov.nih.ncgc.openhts.tool1.persistence.PersistenceRoot;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class GlobalSettings implements Db4oEnabled  {
  private static final long serialVersionUID = -2455374664843010643L;
  public static final String CACHEMB_KEY = "gov.nih.ncgc.openhts.tool1.engine.CacheSize";
  public static final String CACHEMB_DEFAULT_VALUE = "128";
  public static final long CACHE_SIZE_MINIMUM = 16;
  public static final long CACHE_SIZE_MAXIMUM = 1024;
  public static final String POLLINGINTERVALSECONDS_KEY = "gov.nih.ncgc.openhts.tool1.service.PollingIntervalSeconds";
  public static final String POLLINGINTERVALSECONDS_DEFAULT_VALUE = "5";
  public static final int POLLINGINTERVALSECONDS_MINIMUM = 5;
  public static final int POLLINGINTERVALSECONDS_MAXIMUM = 300;
  private Map<String, String> settingsMap;
  private static GlobalSettings self = null;

  private GlobalSettings() {
    settingsMap = Collections.synchronizedMap(new HashMap<String, String>());
  }


  public static GlobalSettings getInstance() {
    if (self == null) {
      // Was not constructed by the persistence framework, so make a default one
      // new IllegalStateException("not initialized");
      self = new GlobalSettings();
    }
    return self;
  }

  public synchronized String getProperty(final String key) {
    if (!settingsMap.containsKey(key)) {
      if (CACHEMB_KEY.equals(key)) {
        putProperty(CACHEMB_KEY, CACHEMB_DEFAULT_VALUE);
      }
      else if (POLLINGINTERVALSECONDS_KEY.equals(key)) {
        putProperty(POLLINGINTERVALSECONDS_KEY,
            POLLINGINTERVALSECONDS_DEFAULT_VALUE);
      }
    }
    return settingsMap.get(key);
  }

  public synchronized void putProperty(final String key, final String value) {
    final String ev = settingsMap.get(key);
    if (ev == null && value != null || ev != null && !ev.equals(value)) {
      settingsMap.put(key, value);
      if (Main.isPersisting) {theRoot.getPersistenceContext().set(self);}
    }
  }

  @Override
  public String toString() {
    return "GlobalSettings[" + settingsMap + "]";
  }

  public static Root theRoot;

  public static final class Root implements PersistenceRoot {
    private PersistenceContext context;

    public Root() {
      if (theRoot == null) {
        theRoot = this;
      }
      else {
        throw new IllegalStateException("already initialized");
      }
    }

    @Override
		public void setPersistenceContext(final PersistenceContext context) {
      this.context = context;
      final List<GlobalSettings> l = context.getExtent(GlobalSettings.class);
      if (l.size() == 0) {
        self = new GlobalSettings();
        context.set(self);
      }
      else {
        if (l.size() > 1) {
          throw new RuntimeException("multiple GlobalSettings instances");
        }
        else {
          self = l.get(0);
        }
      }
    }

    @Override
		public PersistenceContext getPersistenceContext() {
      return context;
    }
  }

  public static void configureDb4o() {
    Db4o.configure().objectClass(GlobalSettings.class).cascadeOnUpdate(true);
    Db4o.configure().objectClass(GlobalSettings.class).cascadeOnActivate(true);
    Db4o.configure().objectClass(GlobalSettings.class).updateDepth(Integer.MAX_VALUE);
  }

  @Override
	public boolean objectCanActivate(ObjectContainer oc) {
    // TODO Auto-generated method stub
    return true;
  }

  @Override
	public boolean objectCanNew(ObjectContainer oc) {
    // TODO Auto-generated method stub
    return true;
  }

  @Override
	public boolean objectCanUpdate(ObjectContainer oc) {
    // TODO Auto-generated method stub
    return true;
  }

  @Override
	public void objectOnActivate(ObjectContainer oc) {
    // TODO Auto-generated method stub
    
  }

  @Override
	public void objectOnNew(ObjectContainer oc) {
    // TODO Auto-generated method stub
    
  }

  @Override
	public void objectOnUpdate(ObjectContainer oc) {
    // TODO Auto-generated method stub
    
  }
}
