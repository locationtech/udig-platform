package i18n.omsbox;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "i18n.omsbox.messages"; //$NON-NLS-1$
    public static String OmsBoxView_Load_Experimental;
    public static String OmsBoxView_Modules;
    public static String OmsBoxView_No_module_selected;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }
    private Messages() {
    }
}
