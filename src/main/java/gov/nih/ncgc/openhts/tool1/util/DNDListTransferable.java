package gov.nih.ncgc.openhts.tool1.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

/**
 * Purpose is to ...
 * 
 * @author talafousj
 */
public class DNDListTransferable implements Transferable {
  public final static DataFlavor localObjectFlavor = new DataFlavor(
      DataFlavor.javaJVMLocalObjectMimeType + "; class=java.lang.Object", "Remote Object");
  public final static DataFlavor remoteObjectFlavor = new DataFlavor(
      Object.class, "Remote Object");
  public final static DataFlavor[] dataFlavors = new DataFlavor[] {
      localObjectFlavor, remoteObjectFlavor };
  private final Object transferObject;

  public DNDListTransferable(Object transferObject) {
    this.transferObject = transferObject;
  }

  // @Implements Transferable
  @Override
	public Object getTransferData(DataFlavor flavor)
      throws UnsupportedFlavorException {
    if (flavor.equals(localObjectFlavor) || flavor.equals(remoteObjectFlavor)) {
      return transferObject;
    }
    throw new UnsupportedFlavorException(flavor);
  }

  // @Implements Transferable
  @Override
	public DataFlavor[] getTransferDataFlavors() {
    // TODO Auto-generated method stub
    return null;
  }

  // @Implements Transferable
  @Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
    if (flavor.equals(localObjectFlavor)) {
      return true;
    }
    if (flavor.equals(remoteObjectFlavor)) {
      return true;
    }
    return false;
  }
}
