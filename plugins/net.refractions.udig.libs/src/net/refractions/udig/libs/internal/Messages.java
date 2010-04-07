package net.refractions.udig.libs.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "net.refractions.udig.libs.internal.messages"; //$NON-NLS-1$
    public static String Activator_1;
    public static String Activator_EPSG_DATABASE;
    public static String CHECK;
    public static String COORDINATE_REFERENCE_SYSTSMS;
    public static String COORDINATE_SYSTEMS;
    public static String DATUM_DEFINITIONS;
    public static String DATUMS;
    public static String EPSG_SETUP;
    public static String LOADING;
    public static String MATH_TRANSFORMS;
    public static String OPERATIONS_DEFINITIONS;
    public static String PLEASE_WAIT;
    public static String REGISTER;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
    private Messages() {
    }
}
