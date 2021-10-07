/**
 * 
 */
package gov.nih.ncgc.openhts.tool1.dirMonitor;

import java.io.File;

class ContentsAndLastModified {
  File[] contents;
  long[] lastModified;
  ContentsAndLastModified(final File[] contents, final long[] lastModified) {
    this.contents = contents;
    this.lastModified = lastModified;
  }
}