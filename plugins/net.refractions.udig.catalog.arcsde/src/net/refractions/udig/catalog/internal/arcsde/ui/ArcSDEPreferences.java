/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.internal.arcsde.ui;

import java.net.URL;

import net.refractions.udig.catalog.arcsde.internal.Messages;
import net.refractions.udig.catalog.ui.preferences.AbstractProprietaryJarPreferencePage;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.geotools.arcsde.ArcSDEDataStoreFactory;

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
        return Platform.getBundle("net.refractions.udig.catalog.arcsde").getEntry("/lib"); //$NON-NLS-1$ //$NON-NLS-2$;
    }

}
