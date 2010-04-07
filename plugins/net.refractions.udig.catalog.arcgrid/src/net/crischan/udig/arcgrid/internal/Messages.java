package net.crischan.udig.arcgrid.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "net.crischan.udig.arcgrid.internal.messages"; //$NON-NLS-1$
    public static String ArcGridGeoResourceImplementation_Connecting;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
    private Messages() {
    }
}
