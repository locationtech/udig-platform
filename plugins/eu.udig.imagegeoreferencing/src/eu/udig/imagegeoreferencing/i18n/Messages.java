package eu.udig.imagegeoreferencing.i18n;

import org.eclipse.osgi.util.NLS;

/**
 * text variable definitions
 * 
 * @author GDavis, Refractions Research
 * @since 1.1.0
 */
public class Messages extends NLS {
	
	    private static final String BUNDLE_NAME = "eu.udig.imagegeoreferencing.i18n.messages"; //$NON-NLS-1$
	    public static String MarkerDialog_title;
	    public static String MarkerDialog_desc;
	    public static String MarkerDialog_removemarkerscheck;
	    public static String MarkerDialog_selecttype;
	    public static String MarkerDialog_basicwarp;
	    public static String MarkerDialog_extremewarp;
	    public static String MarkerDialog_note;
	    public static String MarkerDialog_noimageloaded;
	    public static String MarkerDialog_noimageselected;
	    public static String SaveDialog_desc;
	    public static String SaveDialog_title;
	    public static String saveFile_title;
	    public static String WarpDialog_desc;
	    public static String WarpDialog_title;
	    public static String DeleteDialog_title;
	    public static String DeleteDialog_errordesc;
	    public static String DeleteDialog_desc;
	    public static String LoadImageError_desc;
	    public static String LoadImageError_title;

	    static {
	        // initialize resource bundle
	        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	    }

	    private Messages() {
	    }
}

