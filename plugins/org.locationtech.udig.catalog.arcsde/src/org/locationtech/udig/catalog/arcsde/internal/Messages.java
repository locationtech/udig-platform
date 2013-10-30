/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package net.refractions.udig.catalog.arcsde.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "net.refractions.udig.catalog.arcsde.internal.messages"; //$NON-NLS-1$

    public static String ArcGeoResource_error_layer_bounds;

    public static String ArcSDEPreferences_jar_Drivers;

    public static String ArcSDEPreferences_jar_projectionEngine;

    public static String ArcSDEPreferences_jar_icu4j;

    public static String ArcSDEWizardPage_MissingDrivers;

    public static String ArcSDEWizardPage_restartApp;

    public static String ArcSDEWizardPage_title;

    public static String ArcServiceExtension_notSDEURL;

    public static String ArcServiceExtension_urlNull;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
