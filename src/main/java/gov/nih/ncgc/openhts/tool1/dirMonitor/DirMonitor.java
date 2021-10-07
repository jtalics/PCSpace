package gov.nih.ncgc.openhts.tool1.dirMonitor;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;
import gov.nih.ncgc.openhts.tool1.Main;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class DirMonitor {
  // Do not extend Thread due to db4o incompatibility
  private final transient List<DirMonitorListener> listeners = new ArrayList<DirMonitorListener>();
  private final List<File> directories = new ArrayList<File>();
  private final Map<File, ContentsAndLastModified> dirContentsAndLastModified = new HashMap<File, ContentsAndLastModified>();
  private FilenameFilter filter;
  private static final boolean debug = false;
  private final transient Thread thread = new Thread() {
    @Override
    public void run() {
      while (running) {
        if (debug) {
          System.out.println("DIRMONITOR: Starting monitor");
        }
        for (final File directory : directories) {
          if (debug) {
            System.out.println("DIRMONITOR: examining dir = "
                + directory.getPath());
          }
          // Make sure directory exists
          if (!directory.exists()) {
            directories.remove(directory);
            fireDirMonitorChanged(new DirMonitorEvent(this, directory,
                DirMonitorEvent.Kind.FILE_CHANGED));
            continue;
          }
          // Get the current (new) contents of the directory
          final File[] newContents = directory.listFiles(filter);
          Arrays.sort(newContents);
          if (debug) {
            System.out
                .println("DIRMONITOR: *new* contents of directory follows ");
            for (File file : newContents) {
              System.out.println("  " + file.getName());
            }
          }
          final long[] newLastModified = new long[newContents.length];
          // Make a copy of the new contents for the next iteration
          final File[] contents = new File[newContents.length];
          final long[] lastModified = new long[contents.length];
          for (int i = 0; i < contents.length; i++) {
            contents[i] = newContents[i];
            // Get the last time modified
            newLastModified[i] = lastModified[i] = contents[i].lastModified();
          }
          // Get the old contents and save the current contents
          final File[] oldContents = dirContentsAndLastModified.get(directory).contents;
          if (debug) {
            System.out
                .println("DIRMONITOR: *old* contents of directory follows ");
            for (File file : oldContents) {
              System.out.println("  " + file.getName());
            }
          }
          final long[] oldLastModified = dirContentsAndLastModified
              .get(directory).lastModified;
          // Remember contents for next time.
          dirContentsAndLastModified.put(directory,
              new ContentsAndLastModified(contents, lastModified));
          int j;
          for (int i = 0; i < oldContents.length; i++) {
            final File oldFile = oldContents[i];
            j = search(newContents, oldFile);
            if (0 <= j) {
              // Found the old file in new contents
              if (debug) {
                System.out.println("DIRMONITOR: old file "
                    + oldContents[i].getName() + " found in new contents");
              }
              if (oldLastModified[i] != newLastModified[j]) {
                if (debug) {
                  System.out.println("  old file last mod = "
                      + oldLastModified[i] + " new file last mod = "
                      + newLastModified[j]);
                }
                fireDirMonitorChanged(new DirMonitorEvent(this, newContents[j],
                    DirMonitorEvent.Kind.FILE_CHANGED));
              }
              oldContents[i] = null;
              newContents[j] = null;
            }
          }
          for (int i = 0; i < newContents.length; i++) {
            final File newFile = newContents[i];
            j = search(oldContents, newFile);
            if (0 <= j) {
              if (debug) {
                System.out.println("DIRMONITOR: new file "
                    + newContents[i].getName() + " found in old contents");
              }
              if (oldLastModified[j] != newLastModified[i]) {
                if (debug) {
                  System.out.println("  old file last mod = "
                      + oldLastModified[j] + " new file last mod = "
                      + newLastModified[i]);
                }
                fireDirMonitorChanged(new DirMonitorEvent(this, newContents[j],
                    DirMonitorEvent.Kind.FILE_CHANGED));
              }
              newContents[i] = null;
              oldContents[j] = null;
            }
          }
          try {
            SwingUtilities.invokeAndWait(new Runnable() {
              @Override
							public void run() {
                for (final File file : oldContents) {
                  if (file != null) {
                    fireDirMonitorChanged(new DirMonitorEvent(this, file,
                        DirMonitorEvent.Kind.FILE_REMOVED));
                  }
                }
                for (final File file : newContents) {
                  if (file != null) {
                    fireDirMonitorChanged(new DirMonitorEvent(this, file,
                        DirMonitorEvent.Kind.FILE_ADDED));
                  }
                }
              }
            });
          }
          catch (final InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          catch (final InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        try {
          sleep(Main.dirMonitorPollingInterval);
        }
        catch (final InterruptedException e) {
          // nop
        }
      }
      fireDirMonitorChanged(new DirMonitorEvent(this, null,
          DirMonitorEvent.Kind.TERMINATED));
    }
  };

  public DirMonitor() {
    // needed for db4o
  }

  public void initialize(final File[] dirs, final FilenameFilter filter,
      final boolean newFirstTime) {
    for (File dir : dirs) {
      this.directories.add(dir);
    }
    this.filter = filter;
    for (final File directory : dirs) {
      File[] contents = directory.listFiles(filter);
      if (contents == null) {
        throw new RuntimeException("bad dir: " + directory.getPath());
      }
      long[] lastModified = new long[contents.length];
      for (int i = 0; i < contents.length; i++) {
        lastModified[i] = contents[i].lastModified();
      }
      if (newFirstTime) {
        // Forces contents to appear new first time around
        contents = new File[0];
        lastModified = new long[0];
      }
      dirContentsAndLastModified.put(directory, new ContentsAndLastModified(
          contents, lastModified));
    }
  }

  private Object userObject;

  public void setUserObject(final Object object) {
    this.userObject = object;
  }

  public Object getUserObject() {
    return userObject;
  }

  private boolean running = true;

  private int search(final File[] contents, final File file) {
    // TODO: inefficient
    for (int i = 0; i < contents.length; i++) {
      final File file2 = contents[i];
      if (file2 != null && file2.equals(file)) {
        return i;
      }
    }
    return -1;
  }

  private void fireDirMonitorChanged(DirMonitorEvent ev) {
    for (final DirMonitorListener listener : listeners) {
      listener.dirMonitorChanged(ev);
    }
  }

  public void addDirMonitorListener(final DirMonitorListener listener) {
    // DataSourceManager listens because it needs to monitor the directories of
    // a DataSource
    // DescriptorManager listens because someone (even this app) may modify the
    // Directory xml file
    listeners.add(listener);
  }

  public void removeDirMonitorListener(final DirMonitorListener listener) {
    listeners.remove(listener);
  }

  public void quit() {
    running = false;
  }

  public void start() {
    thread.start();
  }
}
