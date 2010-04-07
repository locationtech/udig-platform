/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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
