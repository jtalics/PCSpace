package gov.nih.ncgc.openhts.tool1.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.Action;
import javax.swing.JFileChooser;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import gov.nih.ncgc.openhts.tool1.FileFilters;
import gov.nih.ncgc.openhts.tool1.Session;

public class PlotterSaveImageAction extends Tool1Action {
  private Session session;

  public PlotterSaveImageAction(final Session session) {
    this.session = session;
    putValue(Action.NAME, "Save Image");
    putValue(Action.MNEMONIC_KEY, KeyEvent.VK_I);
    putValue(Action.ACTION_COMMAND_KEY, "Save Image");
    putValue(SHORT_DESCRIPTION, "plotter save image");
    putValue(LONG_DESCRIPTION, "plotter save image");
    setEnabled(true);
  }

  @Override
	public void actionPerformed(final ActionEvent e) {
    session.statusPanel.log2((String)getValue(Action.NAME));
    try {
      session.setWaitCursor(true);
      final JFileChooser fileChooser = new JFileChooser(session.cwd);
      fileChooser.setDialogTitle("Save image as");
      // getFileChooser().setLabelTitle("Save image in:");
      // getFileChooser().setHelpID("save_image_file");
      // fileChooser.setHomeMode(FileChooser.LOCAL);
      fileChooser.setFileFilter(FileFilters.jpgFileFilter);
      fileChooser.setAcceptAllFileFilterUsed(false);
      if (fileChooser.showSaveDialog(session.frame) != JFileChooser.APPROVE_OPTION) {
        return;
      }
      final File file = fileChooser.getSelectedFile();
      session.cwd = file.getParentFile();
      if (!session.checkForOverwrite(file)) {
        return;
      }
      final BufferedImage bimg = session.plotManager.canvas.getBufferedImage();
      /*
       * BufferedImage image = new BufferedImage(canvas.w, canvas.h,
       * BufferedImage.TYPE_INT_ARGB); canvas.printAll(image.getGraphics());
       */
      FileOutputStream fos = null;
      fos = new FileOutputStream(file);
      final JPEGImageEncoder jpeg = JPEGCodec.createJPEGEncoder(fos);
      jpeg.encode(bimg);
      fos.close();
      // JpegEncoder jpg = new JpegEncoder(image, 80, fos);
      // jpg.Compress();
      session.updateEnablement();
    }
    catch (final Throwable ex) {
      session.errorSupport("Cannot save image because:", ex,
          "save_image_file_menu_error");
    }
    finally {
      session.setWaitCursor(false);
    }
  }

  private static final long serialVersionUID = 1L;
}
