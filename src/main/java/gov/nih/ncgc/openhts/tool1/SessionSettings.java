package gov.nih.ncgc.openhts.tool1;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.db4o.ObjectContainer;
import gov.nih.ncgc.openhts.tool1.persistence.Db4oEnabled;
import gov.nih.ncgc.openhts.tool1.persistence.PersistenceContext;
import gov.nih.ncgc.openhts.tool1.persistence.PersistenceRoot;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class SessionSettings implements Db4oEnabled {
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

  private SessionSettings() {
    settingsMap = Collections.synchronizedMap(new HashMap<String, String>());
  }

  private static SessionSettings sessionSettingsInstance = null;

  public static SessionSettings getInstance() {
    if (sessionSettingsInstance == null) {
      throw new IllegalStateException("not initialized");
    }
    return sessionSettingsInstance;
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
      theRoot.getPersistenceContext().set(sessionSettingsInstance);
    }
  }

  @Override
  public String toString() {
    return "SessionSettings[" + settingsMap + "]";
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
      final List<SessionSettings> l = context.getExtent(SessionSettings.class);
      if (l.size() == 0) {
        sessionSettingsInstance = new SessionSettings();
        context.set(sessionSettingsInstance);
      }
      else {
        if (l.size() > 1) {
          throw new RuntimeException("multiple GlobalSettings instances");
        }
        else {
          sessionSettingsInstance = l.get(0);
        }
      }
    }

    @Override
		public PersistenceContext getPersistenceContext() {
      return context;
    }
  }

  @Override
	public boolean objectCanActivate(ObjectContainer oc) {
    return true;
  }

  @Override
	public boolean objectCanNew(ObjectContainer oc) {
    return true;
  }

  @Override
	public boolean objectCanUpdate(ObjectContainer oc) {
    return true;
  }

  @Override
	public void objectOnActivate(ObjectContainer oc) {
    // nop
  }

  @Override
	public void objectOnNew(ObjectContainer oc) {
    // nop
  }

  @Override
	public void objectOnUpdate(ObjectContainer oc) {
    // nop
  }
}
