package net.refractions.udig.document;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "net.refractions.udig.document.messages"; //$NON-NLS-1$

    public static String docView_attach;

    public static String docView_attachFile;

    public static String docView_attachFiles;

    public static String docView_delete;

    public static String docView_errEmpty;

    public static String docView_errFileExistMulti;

    public static String docView_errFileExistSingle;

    public static String docView_errInvalidURL;

    public static String docView_errURLExist;

    public static String docView_link;

    public static String docView_linkDialogHeader;

    public static String docView_linkDialogTitle;

    public static String docView_linkURL;

    public static String docView_name;

    public static String docView_open;

    public static String docView_openDialogTitle;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
