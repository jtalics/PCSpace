package gov.nih.ncgc.openhts.tool1;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/** Purpose is to ...
 * @author talafousj
 *
 */
public class AppLafManager {

  public enum IconKind {

    Session,
    DataSource,
    Pointset,
    Descriptor,
    Basis,
    ////////////
    ICON_LOCK_THIS_TABLE,
    ICON_UNLOCK_THIS_TABLE,
    ICON_SELECT_ALL_ROWS_THIS_TABLE,
    ICON_DESELECT_ALL_ROWS_THIS_TABLE,
    ICON_TOGGLE_THIS_TABLE,
    ICON_LOCK_ALL_TABLES,
    ICON_UNLOCK_ALL_TABLES,
    ICON_SELECT_ALL_ROWS_UNLOCKED,
    ICON_DESELECT_ALL_ROWS_UNLOCKED,
    ICON_TOGGLE_UNLOCKED,
    ICON_HIDE_SELECTIONS,
    ICON_SHOW_SELECTIONS,
    ICON_EXPORT_SELECTIONS,
    ICON_REVEAL_HIDDEN,
    ICON_MASK_HIDDEN,
    ICON_UNLOCKED,
    ICON_LOCKED,
    //////////////////
    ICON_SELECT_MODE,
    ICON_DESELECT_MODE,
    ICON_DRAG_SELECTION,
    ICON_DRAG_NONE,
    ICON_DRAG_TRANSLATE,
    ICON_DRAG_XYROTATE,
    ICON_DRAG_ZROTATE,
    ICON_DRAG_ZOOM,
    ICON_DRAG_TEXT,
    ICON_UNZOOM,
    ICON_CYCLE,
    ICON_COMPOSE,
    ICON_ORTHOGONAL,
    ICON_NONORTHOGONAL, 
    //////////////////
    ICON_VIEW_SELECTED_ROWS_ALL_TABLES
    
  }
  
  public enum CursorKind {
    CURSOR_DRAG_SELECTION,
    CURSOR_DRAG_NONE,
    CURSOR_DRAG_TRANSLATE,
    CURSOR_DRAG_XYROTATE,
    CURSOR_DRAG_ZROTATE,
    CURSOR_DRAG_ZOOM,
    CURSOR_DRAG_TEXT,
    CURSOR_WAIT
  }
  
  private static final String DEFAULT_ICON_ROOT = "/images/";
  private static final String iconFileRoot=DEFAULT_ICON_ROOT;
  private static final Map<IconKind,ImageIcon> iconKindToIcon = new HashMap<IconKind,ImageIcon>();
  private static final Map<CursorKind,Cursor> cursorKindToCursor = new HashMap<CursorKind,Cursor>();
  static {
        
    iconKindToIcon.put(IconKind.Session, getImageIcon("tool1_icon.png"));
    iconKindToIcon.put(IconKind.DataSource, getImageIcon("green_square.png"));
    iconKindToIcon.put(IconKind.Pointset, getImageIcon("red_dot.png"));
    iconKindToIcon.put(IconKind.Descriptor, getImageIcon("yellow_triangle.png"));
    iconKindToIcon.put(IconKind.Basis, getImageIcon("blue_diamond.png"));
    /////////////
    iconKindToIcon.put(IconKind.ICON_LOCK_THIS_TABLE, getImageIcon("lock_this_table.gif"));
    iconKindToIcon.put(IconKind.ICON_UNLOCK_THIS_TABLE, getImageIcon("unlock_this_table.gif"));
    iconKindToIcon.put(IconKind.ICON_SELECT_ALL_ROWS_THIS_TABLE, getImageIcon("select_all_rows_this_table.gif"));
    iconKindToIcon.put(IconKind.ICON_DESELECT_ALL_ROWS_THIS_TABLE, getImageIcon("deselect_all_rows_this_table.gif"));
    iconKindToIcon.put(IconKind.ICON_TOGGLE_THIS_TABLE, getImageIcon("toggle_this_table.gif"));
    iconKindToIcon.put(IconKind.ICON_LOCK_ALL_TABLES, getImageIcon("lock_all_tables.gif"));
    iconKindToIcon.put(IconKind.ICON_UNLOCK_ALL_TABLES, getImageIcon("unlock_all_tables.gif"));
    iconKindToIcon.put(IconKind.ICON_SELECT_ALL_ROWS_UNLOCKED, getImageIcon("select_all_rows_unlocked.gif"));
    iconKindToIcon.put(IconKind.ICON_DESELECT_ALL_ROWS_UNLOCKED, getImageIcon("deselect_all_rows_unlocked.gif"));
    iconKindToIcon.put(IconKind.ICON_TOGGLE_UNLOCKED, getImageIcon("toggle_unlocked.gif"));
    iconKindToIcon.put(IconKind.ICON_HIDE_SELECTIONS, getImageIcon("hide_selections.gif"));
    iconKindToIcon.put(IconKind.ICON_SHOW_SELECTIONS, getImageIcon("show_selections.gif"));
    iconKindToIcon.put(IconKind.ICON_EXPORT_SELECTIONS, getImageIcon("export_selections.gif"));
    iconKindToIcon.put(IconKind.ICON_REVEAL_HIDDEN, getImageIcon("reveal_hidden.gif"));
    iconKindToIcon.put(IconKind.ICON_MASK_HIDDEN,getImageIcon("reveal_hidden.gif" ));
    iconKindToIcon.put(IconKind.ICON_UNLOCKED, getImageIcon("unlocked.gif"));
    iconKindToIcon.put(IconKind.ICON_LOCKED, getImageIcon("locked.gif"));
////////////
  iconKindToIcon.put(IconKind.ICON_SELECT_MODE,getImageIcon("select_mode.gif"));
  iconKindToIcon.put(IconKind.ICON_DESELECT_MODE,getImageIcon("deselect_mode.gif"));
  iconKindToIcon.put(IconKind.ICON_DRAG_NONE,getImageIcon("drag_none.gif"));
  iconKindToIcon.put(IconKind.ICON_DRAG_SELECTION,getImageIcon("drag_selection.gif"));
  iconKindToIcon.put(IconKind.ICON_DRAG_TRANSLATE,getImageIcon("drag_translate.gif"));
  iconKindToIcon.put(IconKind.ICON_DRAG_XYROTATE,getImageIcon("drag_xyrotate.gif"));
  iconKindToIcon.put(IconKind.ICON_DRAG_ZROTATE,getImageIcon("drag_zrotate.gif"));
  iconKindToIcon.put(IconKind.ICON_DRAG_ZOOM,getImageIcon("drag_zoom.gif"));
  iconKindToIcon.put(IconKind.ICON_DRAG_TEXT, getImageIcon("drag_text.gif"));
  iconKindToIcon.put(IconKind.ICON_UNZOOM,getImageIcon("unzoom.gif"));
  iconKindToIcon.put(IconKind.ICON_CYCLE,getImageIcon("cycle.gif"));
  iconKindToIcon.put(IconKind.ICON_COMPOSE,getImageIcon("compose.gif"));
  iconKindToIcon.put(IconKind.ICON_ORTHOGONAL,getImageIcon("orthogonal.gif"));
  iconKindToIcon.put(IconKind.ICON_NONORTHOGONAL,getImageIcon("nonorthogonal.gif"));
/////////
  iconKindToIcon.put(IconKind.ICON_VIEW_SELECTED_ROWS_ALL_TABLES, getImageIcon("view_selected_rows_all_tables.png"));
/////////
  cursorKindToCursor.put(CursorKind.CURSOR_DRAG_SELECTION,makeCursor("drag_selection_cursor.gif"));
  cursorKindToCursor.put(CursorKind.CURSOR_DRAG_NONE,makeCursor("drag_none_cursor.gif"));
  cursorKindToCursor.put(CursorKind.CURSOR_DRAG_TRANSLATE,makeCursor("drag_translate_cursor.gif"));
  cursorKindToCursor.put(CursorKind.CURSOR_DRAG_XYROTATE,makeCursor("drag_xyrotate_cursor.gif"));
  cursorKindToCursor.put(CursorKind.CURSOR_DRAG_ZROTATE,makeCursor("drag_zrotate_cursor.gif"));
  cursorKindToCursor.put(CursorKind.CURSOR_DRAG_ZOOM,makeCursor("drag_zoom_cursor.gif"));
  cursorKindToCursor.put(CursorKind.CURSOR_DRAG_TEXT,makeCursor("drag_text_cursor.gif"));
  cursorKindToCursor.put(CursorKind.CURSOR_WAIT,new Cursor(Cursor.WAIT_CURSOR));  
//////////
  
  
  }
  
  private static Cursor makeCursor(final String fileName) {
    return Toolkit.getDefaultToolkit().createCustomCursor(getImageIcon(fileName).getImage(), new Point(0, 0),"custom cursor");    
  }

  /*
   * key=Button.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
   * key=CheckBox.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
   * key=CheckBoxMenuItem.acceleratorFont,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=10]
   * key=CheckBoxMenuItem.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
   * key=ColorChooser.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12]
   * key=ComboBox.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
   * key=DesktopIcon.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
   * key=EditorPane.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12]
   * key=FormattedTextField.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12]
   * key=InternalFrame.titleFont,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
   * key=Label.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
   * key=List.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
   * key=Menu.acceleratorFont,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=10]
   * key=Menu.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
   * key=MenuBar.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
   * key=MenuItem.acceleratorFont,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=10]
   * key=MenuItem.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
   * key=OptionPane.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12]
   * key=Panel.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12]
   * key=PasswordField.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12]
   * key=PopupMenu.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
   * key=ProgressBar.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
   * key=RadioButton.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
   * key=RadioButtonMenuItem.acceleratorFont,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=10]
   * key=RadioButtonMenuItem.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
   * key=ScrollPane.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12]
   * key=Slider.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
   * key=Spinner.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
   * key=TabbedPane.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
   * key=Table.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12]
   * key=TableHeader.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12]
   * key=TextArea.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12]
   * key=TextField.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12]
   * key=TextPane.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12]
   * key=TitledBorder.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
   * key=ToggleButton.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
   * key=ToolBar.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=bold,size=12]
   * key=ToolTip.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12]
   * key=Tree.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12]
   * key=Viewport.font,
   * value=javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12]
   */
    
    private static final Logger theLogger = Logger.getLogger(AppLafManager.class.getName());

    private static final AppLafManager instance = null;


    // bypass the manager for the splash...
    private static final ImageIcon splash = null;
    public static ImageIcon getSplash() {
        if ( null != splash ) {
            return splash;
        }
        final String imagePath = DEFAULT_ICON_ROOT + "Splash.png";
        final URL url = AppLafManager.class.getResource(imagePath);
        if (url == null) {
            throw new RuntimeException("no Icon for Splash.png: " + imagePath);
        }
        return new ImageIcon(url);
    }

    public static ImageIcon getIcon(final IconKind iconKind) {
      return iconKindToIcon.get(iconKind);
    }
    
    // old version
    private ImageIcon getIcon(final String iconName) {
        final String imagePath = iconFileRoot + iconName;

        final URL url = AppLafManager.class.getResource(imagePath);
        if (url == null) {
        	theLogger.severe("no Icon for " + iconName + " " + imagePath);
        }
        return new ImageIcon(url);
    }
    
    /**
     * Create an Icon from a given resource.
     *
     * This works uisng getResourceAsStream() because several browsers do not
     * correctly implement getResource().
     */
    private static ImageIcon getImageIcon(final String fileName) {
      if (fileName==null) {
        throw new RuntimeException("filename is null");
      }
      final String imagePath = iconFileRoot + fileName;
                                        
      final byte[][] buffer=new byte[1][];
      try {
        final InputStream resource=AppLafManager.class.getResourceAsStream(imagePath);
        if (resource==null) {
          throw new RuntimeException("Resource is null: " + imagePath);
        }
        final BufferedInputStream in=new BufferedInputStream(resource);
        final ByteArrayOutputStream out=new ByteArrayOutputStream(1024);
        buffer[0]=new byte[1024];
        int n;
        while ( (n=in.read(buffer[0]))>0) {
          out.write(buffer[0], 0, n);
        }
        in.close();
        out.flush();
        buffer[0]=out.toByteArray();
      }
      catch (final IOException ioe) {
        //System.err.println(ioe.toString());
        throw new RuntimeException("IOException for file: " + imagePath);
      }
      if (buffer[0]==null) {
        throw new RuntimeException(
         imagePath+" not found.");
        //    error(baseClass.getName()+"/"+
//          image+" not found.", null);
//        return null;
      }
      if (buffer[0].length==0) {
        throw new RuntimeException("warning: "+imagePath+ " is zero-length");
//        showError("warning: "+image+
//          " is zero-length");
//        return null;
      }

      return new ImageIcon(buffer[0]);
    }

    public static Cursor getCursor(final CursorKind currentCursor) {
      return cursorKindToCursor.get(currentCursor);
    }
}

