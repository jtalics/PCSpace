package gov.nih.ncgc.openhts.tool1;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/** Purpose is to ...
 * @author talafousj
 *
 */
public class Version {
    public static final String PRODUCT_NAME = "NCGC Tool1 (v"+ToString()+")";
    public static final String PRODUCT_NAME_SHORT = "Tool1";
    public static final int MAJOR=0;
    public static final int MINOR=0;
    public static final int MICRO=0;
    public static final int BUILDNUM=77;
    
    public static final String BUILDTYPE = "" ;

    public static final Date expirationDate = new GregorianCalendar(2023, Calendar.JULY, 15)
    .getTime();

    public static String ToString()
    {
        return 
            Integer.toString( MAJOR ) + "." + 
            Integer.toString( MINOR ) + "." + 
            Integer.toString( MICRO ) + "." +
            Integer.toString( BUILDNUM ) + " " +
            BUILDTYPE;
    }
}
