package gov.nih.ncgc.openhts.tool1.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;

public class DNDList extends JList implements DropTargetListener,
    DragSourceListener, DragGestureListener {
  /**
   * enables this component to be a dropTarget
   */
  private DropTarget dropTarget = null;
  /**
   * enables this component to be a Drag Source
   */
  private DragSource dragSource = null;
  private DataFlavor dataFlavor;
  private DefaultListModel defaultListModel;
  private int oldIndex;
  
  /**
   * constructor - initializes the DropTarget and DragSource.
   */
  public DNDList() {
    dropTarget = new DropTarget(this, this);
    dragSource = new DragSource();
    dragSource.createDefaultDragGestureRecognizer(this,
        DnDConstants.ACTION_MOVE, this);
    defaultListModel= (DefaultListModel)getModel();
    }

  /**
   * constructor - initializes the DropTarget and DragSource.
   */
  public DNDList(final Object[] objects) {
    final DefaultListModel listModel = new DefaultListModel();
    final int size = objects.length;
    for (int i = 0; i < size; i++) {
      listModel.addElement(objects[i]);
    }
    setModel(listModel);
    dropTarget = new DropTarget(this, this);
    dragSource = new DragSource();
    dragSource.createDefaultDragGestureRecognizer(this,
        DnDConstants.ACTION_MOVE, this);
    defaultListModel= (DefaultListModel)getModel();
  }

  /**
   * is invoked when you are dragging over the DropSite
   */
  // @Implements
  @Override
	public void dragEnter(final DropTargetDragEvent event) {
    event.acceptDrag(DnDConstants.ACTION_MOVE);
  }

  /**
   * is invoked when you are exit the DropSite without dropping
   */
  // @Implements
  @Override
	public void dragExit(final DropTargetEvent event) {
    // nop
  }

  /**
   * is invoked when a drag operation is going on
   */
  // @Implements
  @Override
	public void dragOver(final DropTargetDragEvent event) {
    // nop
  }

  /**
   * a drop has occurred
   */
  // @Implements
  @Override
	public void drop(final DropTargetDropEvent event) {
    try {
      final Transferable transferable = event.getTransferable();

      if (transferable.isDataFlavorSupported(DNDListTransferable.localObjectFlavor)) {
        event.acceptDrop(DnDConstants.ACTION_MOVE);
        final Object object = transferable
            .getTransferData(DNDListTransferable.localObjectFlavor);
        int newIndex = locationToIndex(event.getLocation());
        if (newIndex<=oldIndex) {oldIndex++;}
        addElement(object, newIndex);
        event.getDropTargetContext().dropComplete(true);
      }
      else {
        event.rejectDrop();
      }
    }
    catch (final IOException exception) {
      // exception.printStackTrace();
      // System.err.println( "Exception" , exception.getMessage());
      event.rejectDrop();
    }
    catch (final UnsupportedFlavorException ufException) {
      event.rejectDrop();
    }
  }

  /**
   * is invoked if the use modifies the current drop gesture
   */
  // @Implements
  @Override
	public void dropActionChanged(final DropTargetDragEvent event) {
    // nop
  }

  /**
   * a drag gesture has been initiated
   */
  // @Implements
  @Override
	public void dragGestureRecognized(final DragGestureEvent event) {
    final Object selected = this.getSelectedValue();
    oldIndex = defaultListModel.indexOf(selected);

    if (selected != null) {
      Transferable transferable = new DNDListTransferable(selected);
      dragSource.startDrag(event, DragSource.DefaultMoveDrop, transferable, this);
    }
    else {
      // System.out.println( "nothing was selected");
    }
  }

  /**
   * this message goes to DragSourceListener, informing it that the dragging
   * has ended
   */
  // @Implements
  @Override
	public void dragDropEnd(final DragSourceDropEvent event) {
    if (event.getDropSuccess()) {
      removeElementAt(oldIndex);
    }
  }

  /**
   * this message goes to DragSourceListener, informing it that the dragging
   * has entered the DropSite
   */
  // @Implements
  @Override
	public void dragEnter(final DragSourceDragEvent event) {
    // System.out.println( " dragEnter");
  }

  /**
   * this message goes to DragSourceListener, informing it that the dragging
   * has exited the DropSite
   */
  // @Implements
  @Override
	public void dragExit(final DragSourceEvent event) {
    // System.out.println( "dragExit");
  }

  /**
   * this message goes to DragSourceListener, informing it that the dragging
   * is currently ocurring over the DropSite
   */
  // @Implements
  @Override
	public void dragOver(final DragSourceDragEvent event) {
    // System.out.println( "dragExit");
  }

  /**
   * is invoked when the user changes the dropAction
   */
  // @Implements
  @Override
	public void dropActionChanged(final DragSourceDragEvent event) {
    // System.out.println( "dropActionChanged");
  }

  protected void addElement(final Object object, int newIndex) {
    defaultListModel.insertElementAt(object, newIndex);
  }

  protected void removeElementAt(int index) {
    defaultListModel.removeElementAt(index);
  }
  public void setDataFlavor(final DataFlavor flavor) {
    dataFlavor = flavor;
  }

  public void repopulate(Object[] objects) {
    defaultListModel.clear();
    for (Object object : objects) {
      defaultListModel.addElement(object);
    }
  }
  private static final long serialVersionUID = 1L;

  public Object[] getObjects() {
    List<Object> list = new ArrayList<Object>(); 
    for (int i=0; i<defaultListModel.size(); i++) {
      list.add(defaultListModel.getElementAt(i));
    }
    return list.toArray(new Object[list.size()]);
  }
}