/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.arcsde.ui;

import java.net.URL;

import org.locationtech.udig.catalog.arcsde.internal.Messages;
import org.locationtech.udig.catalog.ui.preferences.AbstractProprietaryJarPreferencePage;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.geotools.arcsde.data.ArcSDEDataStoreFactory;

/**
 * @author Jesse
 * @since 1.1.0
 */
public class ArcSDEPreferences extends AbstractProprietaryJarPreferencePage
        implements
            IWorkbenchPreferencePage {

    private static final String[] requiredJars = {"jsde_sdk-9.2+.jar", "jpe_sdk-9.2+.jar",
            "icu4j-3.2+.jar"};

    private static final String[] requiredJarDescs = {Messages.ArcSDEPreferences_jar_Drivers,
            Messages.ArcSDEPreferences_jar_projectionEngine, Messages.ArcSDEPreferences_jar_icu4j};

    /**
     * 
     */
    public ArcSDEPreferences() {
    }

    /**
     * @param title
     */
    public ArcSDEPreferences( String title ) {
        super(title);
    }

    /**
     * @param title
     * @param desc
     */
    public ArcSDEPreferences( String title, ImageDescriptor desc ) {
        super(title, desc);
    }

    @Override
    protected String getDefaultJarName( int jarIndex ) {
        return requiredJars[jarIndex];
    }

    @Override
    protected String getDriverLabel( int jarIndex ) {
        return requiredJarDescs[jarIndex];
    }

    @Override
    protected int getRequiredJarsCount() {
        return requiredJars.length;
    }

    @Override
    protected boolean installed() {
        return isInstalled();
    }

    public static boolean isInstalled() {
        ArcSDEDataStoreFactory factory = new ArcSDEDataStoreFactory();
        return factory.isAvailable();
    }

    @Override
    protected URL getLibsURL() {
        return Platform.getBundle("org.locationtech.udig.catalog.arcsde").getEntry("/lib"); //$NON-NLS-1$ //$NON-NLS-2$;
    }

}
