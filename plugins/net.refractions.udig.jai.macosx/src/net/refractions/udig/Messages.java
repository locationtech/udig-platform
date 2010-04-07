package net.refractions.udig;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "net.refractions.udig.messages"; //$NON-NLS-1$
    public static String InstallJaiStartup_0;
    public static String InstallJaiStartup_1;
    public static String InstallJaiStartup_2;
    public static String InstallJaiStartup_3;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
    private Messages() {
    }
}
