package net.refractions.udig.mapgraphic.graticule;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "net.refractions.udig.mapgraphic.graticule.messages"; //$NON-NLS-1$

    public static String GraticuleGraphic_Illegal_CRS;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
