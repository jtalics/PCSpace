package gov.nih.ncgc.openhts.tool1.persistence;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.tree.DefaultMutableTreeNode;
import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;
import gov.nih.ncgc.openhts.tool1.GlobalSettings;
import gov.nih.ncgc.openhts.tool1.Main;
import gov.nih.ncgc.openhts.tool1.dataSourceManager.DataSourceManager;
import gov.nih.ncgc.openhts.tool1.descriptorManager.DescriptorManager;
import gov.nih.ncgc.openhts.tool1.plotManager.PlotManager;
import gov.nih.ncgc.openhts.tool1.pointsetManager.PointsetManager;
import gov.nih.ncgc.openhts.tool1.spreadsheet.Spreadsheet;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class Db4oPersistenceService extends PersistenceService {
private final static boolean debug=false;
  public static final Version VERSION = new Version("1.0 2007-12-31");
  private ObjectContainer objectContainer;
  private final List<PersistenceRoot> roots = new ArrayList<PersistenceRoot>();
  public static final String DB_NAME = "main.db";
  private File db;
  private PersistQueue persistQueue;
  private PersistenceContext defaultContext;

  public Db4oPersistenceService() throws PersistentStoreVersionException {
    Db4o.configure().callConstructors(true);
    Db4o.configure().exceptionsOnNotStorable(true);
    // Db4o.configure().weakReferences(false);
    Db4o.configure().objectClass(DefaultMutableTreeNode.class).storeTransientFields(true);
    configureDb4oForClass(DefaultMutableTreeNode.class);
    DescriptorManager.configureDb4o();
    DataSourceManager.configureDb4o();
    PointsetManager.configureDb4o();
    PlotManager.configureDb4o();
    Spreadsheet.configureDb4o();
    GlobalSettings.configureDb4o();
    db = new File(Main.homeDir, DB_NAME);
    objectContainer = Db4o.openFile(db.getPath());
    checkVersion();
  }

  public static void configureDb4oForClass(final Class<? extends Object>... classes) {
    for (final Class klass : classes ) { 
    Db4o.configure().objectClass(klass)
        .cascadeOnActivate(true);
    Db4o.configure().objectClass(klass)
        .cascadeOnUpdate(true);
    Db4o.configure().objectClass(klass)
        .cascadeOnDelete(true);
    Db4o.configure().objectClass(klass).updateDepth(
        Integer.MAX_VALUE);
    }
  }

  private void checkVersion() throws PersistentStoreVersionException {
    final ObjectSet<Version> os = objectContainer.query(Version.class);
    if (os.size() == 0) {
      objectContainer.set(VERSION);
    }
    else {
      if (os.size() > 1) {
        throw new PersistentStoreVersionException(
            "multiple version records present: " + os);
      }
      final Version v = os.get(0);
      if (!VERSION.equals(v)) {
        throw new PersistentStoreVersionException(
            "Application persistence version " + VERSION
                + " not compatible with existing store version " + v);
      }
    }
  }

  public static void main(final String[] args) throws Exception {
    if (true) {
      benchmark();
      return;
    }
    final Db4oPersistenceService service = new Db4oPersistenceService();
    // List ll;
    // ll = service.objectContainer.query(FolderTreeNodeRootHolder.class);
    // System.out.println(ll.size());
    // FolderTreeNodeRootHolder h = (FolderTreeNodeRootHolder) ll.get(0);
    // List aggregateListNodes =
    // h.getRoot().findAllOfType(FolderTreeNode.Kind.AggregateList);
    // for (Iterator i = aggregateListNodes.iterator(); i.hasNext();) {
    // AggregateListTreeNode tn = (AggregateListTreeNode) i.next();
    // System.out.println("tn " + tn.getId() + " " + tn.getMediaSet().getId() +
    // " " + tn.getName());
    // }
    //        
    // for (Iterator i = aggregateListNodes.iterator(); i.hasNext();) {
    // AggregateListTreeNode tn = (AggregateListTreeNode) i.next();
    // System.out.println("tn " + tn.getId() + " " + tn.getMediaSet().getId() +
    // " " + tn.getName());
    // }
  }

  static long startId = new Date().getTime();

  private static void benchmark() {
    final int dbsize = 200000000; // 200M
    final int maxDepth = 10;
    final int maxBallast = 5000;
    final int maxTopLevelObjects = 1000;
    final int maxMembers = 10;
    final int nTrials = 500;
    final String dbname = "C:\\temp\\a.a";
    final class ArbitraryObject {
      long id;
      long[] ballast = new long[(int) (Math.random() * maxBallast)];
      ArbitraryObject[] aos = new ArbitraryObject[(int) (Math.random() * maxMembers)];

      ArbitraryObject(final long id, int level) {
        this.id = id;
        for (int i = 0; i < aos.length; i++) {
          if (level++ < Math.random() * maxDepth) {
            aos[i] = new ArbitraryObject(startId++, level);
          }
        }
      }
    }
    // Build database, don't forget to delete old one
    if (false) {
      Db4o.configure().objectClass(GlobalSettings.class).updateDepth(
          Integer.MAX_VALUE);
      final ObjectContainer objectContainer = Db4o.openFile(dbname);
      int i = 0;
      final File dbFile = new File(dbname);
      while (i++ < maxTopLevelObjects) {
        if (dbFile.length() > dbsize) {
          System.out.println("Reached maximum desired database size = "
              + dbFile.length());
          break;
        }
        final int level = 0;
        long id;
        final ArbitraryObject ao = new ArbitraryObject(id = startId++, level);
        objectContainer.set(ao);
        objectContainer.commit();
        final long time1 = new Date().getTime();
        final Query query = objectContainer.query();
        query.constrain(ArbitraryObject.class);
        query.descend("id").constrain(id);
        final ObjectSet<Object> aos = query.execute();
        if (aos.size() != 1) {
          throw new RuntimeException("objects.size() = " + aos.size());
        }
        final long time2 = new Date().getTime();
        System.out.println("Top level object id = "
            + ((ArbitraryObject) aos.get(0)).id
            + " inserted into container.  Fetch time = " + (time2 - time1));
      }
      objectContainer.close();
    }
    // Benchmark fetch times
    if (false) {
      final ObjectContainer objectContainer = Db4o.openFile(dbname);
      Db4o.configure().objectClass(GlobalSettings.class).updateDepth(
          Integer.MAX_VALUE);
      final ObjectSet<Object> objects = objectContainer.get(null);
      System.out.println("NumberFloat of objects in db = " + objects.size());
      final int[] fetchTimes = new int[nTrials];
      for (int i = 0; i < nTrials; i++) {
        final Query query = objectContainer.query();
        query.constrain(ArbitraryObject.class);
        final int j = (int) (Math.random() * objects.size());
        final long id = ((ArbitraryObject) objects.get(j)).id;
        query.descend("id").constrain(id);
        final long time1 = new Date().getTime();
        final ObjectSet<Object> testies = query.execute();
        if (testies.size() != 1) {
          throw new RuntimeException("objects.size() = " + testies.size());
        }
        final long time2 = new Date().getTime();
        fetchTimes[i] = (int) (time2 - time1);
        System.out.println("Object " + id + " fetch(ms) = " + fetchTimes[i]);
      }
      float meanFetchTime = 0.0f;
      float sum = 0.0f;
      for (final int fetchTime : fetchTimes) {
        sum += fetchTime;
      }
      meanFetchTime = sum / nTrials;
      sum = 0.0f;
      float diff;
      for (final int fetchTime : fetchTimes) {
        diff = fetchTime - meanFetchTime;
        sum += diff * diff;
      }
      System.out.println("Average fetch time = " + meanFetchTime + " +/- "
          + Math.sqrt(sum / (nTrials - 1)) + "msec");
    }
  }

  @Override
  public void start() {
    persistQueue = new PersistQueue("mainPersistQueue");
    persistQueue.start();
    defaultContext = new Db4oPersistenceContext("default", objectContainer,
        persistQueue);
    for (final PersistenceRoot root : roots) {
      final String name = root.getClass().getSimpleName();
      root.setPersistenceContext(new Db4oPersistenceContext(name,
          objectContainer, persistQueue));
    }
  }

  @Override
  public void stop() {
    persistQueue.stop();
    objectContainer.close();
  }

  @Override
  public void register(final PersistenceRoot root) {
    roots.add(root);
  }

  @Override
  public void delete(final Object o) {
    defaultContext.delete(o);
  }

  @Override
  public void invokeLater(final Runnable r) {
    final Request req = new Request(r.toString()) {
      @Override
      public void doRequest() {
        r.run();
      }
    };
    persistQueue.add(req);
  }

  @Override
  public void invokeAndWait(final Runnable r) {
    if (!isPersistenceThread()) {
      final BlockingRequest req = new BlockingRequest(r.toString()) {
        @Override
        public void doRequest() {
          r.run();
        }
      };
      persistQueue.add(req);
      req.waitForComplete();
      if (req.getRuntimeException() != null) {
        throw req.getRuntimeException();
      }
    }
    else {
      r.run();
    }
  }

  @Override
  public boolean isPersistenceThread() {
    return Thread.currentThread().equals(persistQueue.thread);
  }

  static private class Db4oPersistenceContext implements PersistenceContext {
    private String name;
    private ObjectContainer objectContainer;
    private PersistQueue persistQueue;

    private Db4oPersistenceContext(final String name,
        final ObjectContainer objectContainer, final PersistQueue persistQueue) {
      this.name = name;
      this.objectContainer = objectContainer;
      this.persistQueue = persistQueue;
    }

    @Override
		public <T> T find(final Class<T> claz, final String indexMemberName,
        final Object indexValue) {
      final BlockingRequest req = new BlockingRequest("find " + claz + " "
          + indexMemberName + " " + indexValue) {
        @Override
        public void doRequest() {
          Query query = objectContainer.query();
          query.constrain(claz);
          query.descend(indexMemberName).constrain(indexValue);
          ObjectSet objectSet = query.execute();
          if (objectSet.size() == 0) {
            result = null;
          }
          else {
            if (objectSet.size() > 1) {
              throw new RuntimeException("multiple matches for " + claz + "."
                  + indexMemberName + "=" + indexValue);
            }
            result = objectSet.ext().get(0);
          }
        }
      };
      persistQueue.add(req);
      req.waitForComplete();
      if (req.getRuntimeException() != null) {
        throw req.getRuntimeException();
      }
      return claz.cast(req.getResult());
    }

    @Override
		public <T> List<T> findAll(final Class<T> claz,
        final String indexMemberName, final Object indexValue) {
      final BlockingRequest req = new BlockingRequest("findAll " + claz + " "
          + indexMemberName + " " + indexValue) {
        @Override
        public void doRequest() {
          Query query = objectContainer.query();
          query.constrain(claz);
          query.descend(indexMemberName).constrain(indexValue);
          ObjectSet objectSet = query.execute();
          List<T> l = new ArrayList<T>();
          for (Object o : objectSet) {
            l.add(claz.cast(o));
          }
          result = l;
        }
      };
      persistQueue.add(req);
      req.waitForComplete();
      if (req.getRuntimeException() != null) {
        throw req.getRuntimeException();
      }
      final List<T> l = (List<T>) req.getResult();
      return l;
    }

    @Override
		public <T> List<T> getExtent(final Class<T> claz) {
      final BlockingRequest req = new BlockingRequest(claz, "getExtent " + claz) {
        @Override
        public void doRequest() {
          List ll = objectContainer.query(claz);
          List<T> l = new ArrayList<T>();
          for (Object o : ll) {
            l.add(claz.cast(o));
          }
          result = l;
        }
      };
      persistQueue.add(req);
      req.waitForComplete();
      if (req.getRuntimeException() != null) {
        throw req.getRuntimeException();
      }
      final List<T> l = (List<T>) req.getResult();
      return l;
    }

    @Override
		public void set(final Object o) {
      final SetRequest r = new SetRequest(o, objectContainer);
      persistQueue.add(r);
    }

    @Override
		public void delete(final Object o) {
      final DeleteRequest r = new DeleteRequest(o, objectContainer);
      persistQueue.add(r);
    }
  }

  private class PersistQueue implements Runnable {
    private String name;
    private final BlockingQueue<Request> requests = new LinkedBlockingQueue<Request>();
    private Thread thread;
    private boolean stopped;

    private PersistQueue(final String name) {
      this.name = name;
      this.thread = new Thread(this);
      stopped = true;
    }

    public void add(final Request r) {
      if (debug) {
        System.out.println("PERSISTENCE QUEUE: Add request = " + r);
      }
      requests.offer(r);
    }

    public void start() {
      stopped = false;
      if (debug) {
        System.out.println("PERSISTENCE QUEUE: Started");
      }
      thread.start();
    }

    public void stop() {
      add(new Request("stop") {
        @Override
        public void doRequest() {
          stopped = true;
        }
      });
      if (debug) {
        System.out.println("PERSISTENCE QUEUE: Stop: Remaining requests = " + requests.size());
      }
      for (final Request request : requests) {
        System.out.println("    " + request);
      }
      try {
        thread.join();
      }
      catch (final InterruptedException e) {
        e.printStackTrace();
      }
    }

    @Override
		public void run() {
      final List<Request> l = new ArrayList<Request>();
      while (!stopped) {
        l.clear();
        try {
          // block until queue has some requests
          final Request req = requests.take();
          if (debug) {
            System.out.println("PERSISTENCE QUEUE: Take request =: "+ req);
          }
          l.add(req);
        }
        catch (final InterruptedException e) {
          e.printStackTrace();
        }
        // drain all current requests
        Request req;
        while ((req = requests.poll()) != null) {
          if (debug) {
            System.out.println("PERSISTENCE QUEUE: Poll for request = "+ req);
          }
          l.add(req);
        }
        // Consolidate request list, preserving write-operation dependencies.
        final List<Request> sequence = new ArrayList<Request>();
        final Map<Request, RequestChain> requestChains = new HashMap<Request, RequestChain>();
        for (final Request r : l) {
          if (r instanceof DeleteRequest) {
            final DeleteRequest dr = (DeleteRequest) r;
            final RequestChain ec = requestChains.remove(new SetRequest(
                dr.deleteObject, dr.oc));
            if (ec != null) {
              ec.setChainEnded(true);
              requestChains.put(ec.get(0), ec);
            }
          }
          RequestChain chain = requestChains.get(r);
          if (chain == null || chain.isChainEnded()) {
            chain = new RequestChain();
            chain.add(r);
            requestChains.put(r, chain);
            sequence.add(r);
          }
          else {
            chain.add(r);
            if (!(r instanceof SetRequest)) {
              sequence.remove(r);
              sequence.add(r);
            }
          }
        }
        for (final Request r : sequence) {
          try {
            r.run();
          }
          catch (final Throwable t) {
            // TODO many exceptions here may not have to do with Db4o, and so
            // don't need Db4o reset
            System.out
                .println("Db4oPersistenceService.PersistQueue.run: failed processing "
                    + r);
            t.printStackTrace();
            try {
              System.out
                  .println("Db4oPersistenceService.PersistQueue.run: closing container");
              objectContainer.close();
              System.out
                  .println("Db4oPersistenceService.PersistQueue.run: container closed");
            }
            catch (final Throwable tt) {
              // nop
            }
            try {
              System.out
                  .println("Db4oPersistenceService.PersistQueue.run: reopen container");
              objectContainer = Db4o.openFile(db.getAbsolutePath());
              System.out
                  .println("Db4oPersistenceService.PersistQueue.run: container reopened");
            }
            catch (final Throwable tt) {
              tt.printStackTrace();
            }
          }
          final List<Request> chain = requestChains.get(r);
          if (chain == null) {
            System.out.println("PersistQueue.run: null chain for " + " " + r);
          }
          if (r instanceof BlockingRequest) {
            for (final Request rr : chain) {
              final BlockingRequest er = (BlockingRequest) r;
              ((BlockingRequest) rr).result = er.result;
              ((BlockingRequest) rr).runtimeException = er.runtimeException;
              rr.complete();
            }
          }
          else {
            for (final Request rr : chain) {
              rr.complete();
            }
          }
        }
        objectContainer.commit();
      }
    }
  }

  private abstract static class Request implements Runnable {
    private String description;
    private boolean chainEnded = false;
    protected int chainHash;

    public Request(final String description) {
      this.description = description;
    }

    @Override
		public void run() {
      doRequest();
    }

    abstract public void doRequest();

    public void complete() {
      // nop
    }

    @Override
    public String toString() {
      return "Request[" + description + "," + chainEnded + "," + chainHash
          + "]";
    }

    public void setChainEnded(final boolean chainEnded, final RequestChain chain) {
      this.chainEnded = chainEnded;
      chainHash = chain.hashCode();
    }

    public boolean isChainEnded() {
      return chainEnded;
    }
  }

  static private class SetRequest extends Request {
    private Object setObject;
    private ObjectContainer oc;

    public SetRequest(final Object o, final ObjectContainer oc) {
      super("set " + o.getClass() + " " + o);
      setObject = o;
      this.oc = oc;
    }

    @Override
    public void doRequest() {
      try {
        oc.set(setObject);
      }
      catch (final RuntimeException e) {
        System.out
            .println("Db4oPersistenceService.SetRequest.doRequest: failed "
                + setObject.getClass() + " " + setObject);
        e.printStackTrace();
        throw e;
      }
    }

    @Override
    public int hashCode() {
      return setObject.hashCode() + (isChainEnded() ? chainHash : 0);
    }

    @Override
    public boolean equals(final Object o) {
      if (o instanceof SetRequest) {
        final SetRequest r = (SetRequest) o;
        if (isChainEnded() || r.isChainEnded()) {
          if (isChainEnded() != r.isChainEnded()) {
            return false;
          }
          else {
            return chainHash == r.chainHash;
          }
        }
        else {
          return setObject.equals(r.setObject);
        }
      }
      else {
        return false;
      }
    }
  }

  static private class RequestChain extends ArrayList<Request> {
    private boolean chainEnded;

    public void setChainEnded(final boolean chainEnded) {
      this.chainEnded = chainEnded;
      for (final Request r : this) {
        r.setChainEnded(chainEnded, this);
      }
    }

    public boolean isChainEnded() {
      return chainEnded;
    }
    private static final long serialVersionUID = 1L;
  }

  static private class DeleteRequest extends Request {
    private Object deleteObject;
    private ObjectContainer oc;

    public DeleteRequest(final Object o, final ObjectContainer oc) {
      super("delete " + o.getClass() + " " + o);
      deleteObject = o;
      this.oc = oc;
    }

    @Override
    public void doRequest() {
      oc.delete(deleteObject);
    }
  }

  private abstract static class BlockingRequest extends Request {
    private RuntimeException runtimeException;
    protected Object result = null;
    private boolean complete = false;
    private Object subject;

    public BlockingRequest(final String description) {
      this(null, description);
    }

    public BlockingRequest(final Object subject, final String description) {
      super(description);
      this.subject = subject;
    }

    @Override
    public void run() {
      try {
        doRequest();
      }
      catch (final RuntimeException e) {
        runtimeException = e;
      }
    }

    public RuntimeException getRuntimeException() {
      return runtimeException;
    }

    @Override
    synchronized public void complete() {
      complete = true;
      notifyAll();
    }

    public void waitForComplete() {
      while (!complete) {
        synchronized (this) {
          try {
            wait();
          }
          catch (final InterruptedException e) {
            // nop
          }
        }
      }
    }

    public Object getResult() {
      return result;
    }

    @Override
    public int hashCode() {
      if (subject != null) {
        return subject.hashCode();
      }
      else {
        return super.hashCode();
      }
    }

    @Override
    public boolean equals(final Object o) {
      if (o instanceof BlockingRequest) {
        if (subject != null) {
          return subject.equals(((BlockingRequest) o).subject);
        }
        else {
          return false;
        }
      }
      else {
        return false;
      }
    }
  }

  private static class Version {
    private String versionString;

    Version(final String v) {
      this.versionString = v;
    }

    @Override
    public boolean equals(final Object o) {
      if (o instanceof Version) {
        return versionString.equals(((Version) o).versionString);
      }
      else {
        return false;
      }
    }

    @Override
    public String toString() {
      return versionString;
    }
  }
}
