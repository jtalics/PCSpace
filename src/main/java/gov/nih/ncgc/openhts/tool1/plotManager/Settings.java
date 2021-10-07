package gov.nih.ncgc.openhts.tool1.plotManager;

/**
 * <p>Title: plotter 2D</p>
 * <p>Description: A 2d plotter</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * @author Joseph Talafous
 * @version 1.0
 */

import java.awt.Font;
import java.awt.Color;
import java.awt.Point;

public class Settings {

  public Settings() {
    //infoPanelColor = new Color(0,220,0);
    //font1 = new Font( "Dialog", Font.PLAIN, 10 );
  }
  public static String lineSeparator=System.getProperty(
  "line.separator");

public static Color wordColor=Color.black;

//public static final Color qHighlight = new Color(255,0,0);
//public static final Color remotehost = Color.orange;
//public static final Color localhost = Color.yellow;
//public static final Color message = Color.red;
//public static final Color help = Color.green;
///public static final Color color4 = new Color(227,199,81);
public static Color colorFileChooserGetMode=new Color(250, 210, 0);
public static Color colorFileChooserPutMode=new Color(250, 110, 100);

//public static final Color color1 = new Color(152,184,227);
public static Color color1=new Color(182, 214, 255);

public static Color fileChooserColor=new Color(212, 208, 200);

public static Color errorColor=new Color(212, 208, 200);

public static Color infoPanelColor=new Color(212, 208, 200);
public static Color warningColor=new Color(212, 208, 200);
public static Color confirmColor=new Color(212, 208, 200);

//public static final Font fontlist1 = new Font( "dialog", Font.PLAIN, 12 );
public static Font font1=new Font("Dialog", Font.BOLD, 12);
public static Font font2=new Font("Dialog", Font.BOLD, 11);
public static Font font3=new Font("Dialog", Font.BOLD, 12);

public static Font fontlist1=new Font("Monospaced", Font.BOLD, 11);

public static Font logFont=new Font("Courier", Font.PLAIN, 11);

//public static final Font logFont = new Font( "Dialog", Font.BOLD, 11 );
//public static final int maxDimensions = 512;
//public static final int maxMetrics = 512;
//public static final int maxProcesses = 64;
public static String tryAgain
  ="Please answer the question or press cancel to leave your answer unchanged.";
public static String errorMarker="eRrOr";

//public static final Point messageDialogPoint = new Point(100,100);
//public static final Point deleteDialogPoint = new Point(0,200);
public static Point defaultPoint=new Point(200, 100);
public static Point dialogPoint=new Point(0, 200);
public static Point logDialogPoint=new Point(500, 300);

//public static final Point streamDialogPoint = new Point(100,100);
public static final Point helpLocation=new Point(300, 300);

//public static final Point modifierLocation = new Point(150,0);
//public static final Point fileChooserLocation = new Point(500,0);
public static Point streamDisplayLocation=new Point(200, 200);

//public static String tasksPackage="";
//public static String questionsPackage="";
public static final int lowPort=1800;
public static final int highPort=65000;
public static final String securityMessage
  =
  "Applet need to be granted this permission in your Java policy file:"+
  lineSeparator;

//public static final String taskState = "unexpected task state";
//public static final String commandState = "unexpected command state";
//public static final String questionState = "unexpected question state";
//public static final String answerState = "unexpected answer state";
//public static final String noAnswer = "you did not answer the question.";
//public static final String qHelpButton = "Q Help";
public static final String badPassword="Invalid password";
public static final String badModeStateMessage=
  "bad mode state in file chooser";

//public static final int rmiRegistryPortNumber = 18702;

// following are for tracing/debugging various jqa subsystems
// These are priorities - the larger the number the lower priority
public static final int uSys=1; // user subsystem logging

//public static final int qSys = 9; // questioning subsystem logging
public static final int wSys=9; // windowing subsystem logging
public static final int eSys=3; // windowing subsystem logging

//public static final int fSys = 4; // file saving, etc. subsytem
public static final int priority=5; // only see < this number.

public static final int verticalScrollIncrement=25;
public static final String noPortAvailable=" no port available";
public static final String boundInRegistry=" bound in registry.";


  //public static final Font font1 = new Font( "Dialog", Font.PLAIN, 10 );

  //public static Color infoPanelColor = new Color(0,220,0);
}
