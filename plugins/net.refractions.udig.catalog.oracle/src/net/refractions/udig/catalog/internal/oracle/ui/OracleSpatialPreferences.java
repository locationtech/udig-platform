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
package net.refractions.udig.catalog.internal.oracle.ui;


import java.net.URL;

import net.refractions.udig.catalog.internal.oracle.OracleServiceExtension;
import net.refractions.udig.catalog.oracle.internal.Messages;
import net.refractions.udig.catalog.ui.preferences.AbstractProprietaryJarPreferencePage;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.geotools.data.oracle.OracleNGDataStoreFactory;

/**
 * Oracle Database wizard page allowing people to install the correct drivers.
 * @author jones
 */
public class OracleSpatialPreferences extends AbstractProprietaryJarPreferencePage implements IWorkbenchPreferencePage {
    /**
     * @param title
     * @param image
     */
    public OracleSpatialPreferences() {
        super(Messages.OraclePreferences_title); 
    }

    public OracleSpatialPreferences( String file ) {
        super(file);
    }

    public OracleSpatialPreferences( String file, ImageDescriptor desc ) {
        super(file, desc);
    }

    protected String getDriverLabel(int index) {
        return Messages.OraclePreferences_driverLabel;
    }

    @Override
    protected String getDefaultJarName(int index) {
        return "oracle-driver.jar"; //$NON-NLS-1$
    }

    @Override
    protected boolean installed() {
        return isInstalled();
    }
    
    public static boolean isInstalled() {
        OracleNGDataStoreFactory factory = OracleServiceExtension.getFactory();
        return factory != null && factory.isAvailable();
    }

    @Override
    protected int getRequiredJarsCount() {
        return 1;
    }

    @Override
    protected URL getLibsURL() {
        return Platform.getBundle("net.refractions.udig.libs.oracle").getEntry("/libs"); //$NON-NLS-1$ //$NON-NLS-2$
    }

}
