/**
 * 
 */
package org.locationtech.udig.catalog.internal.db2.ui;

import java.net.URL;

import org.locationtech.udig.catalog.db2.internal.Messages;
import org.locationtech.udig.catalog.internal.db2.DB2ServiceExtension;
import org.locationtech.udig.catalog.ui.preferences.AbstractProprietaryJarPreferencePage;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for DB2; allows people to install the correct drivers.
 * 
 * @author jones
 * @since 1.0.1
 * @author David Adler, Adtech Geospatial,dadler@adtechgeospatial.com
 * @since 1.5.x
 */
public class DB2Preferences extends AbstractProprietaryJarPreferencePage
        implements
            IWorkbenchPreferencePage {
    private static final String JDBC_DRIVER = "db2jcc.jar"; //$NON-NLS-1$
    private static final String LICENSE = "db2jcc_license_cu.jar"; //$NON-NLS-1$

    /**
     * @param title
     * @param image
     */
    public DB2Preferences() {
        super(Messages.DB2Preferences_title);
    }

    public DB2Preferences( String file ) {
        super(file);
    }

    public DB2Preferences( String file, ImageDescriptor desc ) {
        super(file, desc);
    }
   
    public static boolean isInstalled() {
        return DB2ServiceExtension.getFactory().isAvailable();
    }

    @Override
    protected String getDefaultJarName( int jarIndex ) {
        if (jarIndex == 0) {
            return JDBC_DRIVER;
        } else
            return LICENSE;
    }

    @Override
    protected String getDriverLabel( int jarIndex ) {
        if (jarIndex == 0) {
            return Messages.DB2Preferences_driverLabel;
        } else
            return Messages.DB2Preferences_licenceLabel;
    }

    @Override
    protected int getRequiredJarsCount() {
        return 2;
    }

    @Override
    protected boolean installed() {
        return isInstalled();
    }

    @Override
    protected URL getLibsURL() {
        return Platform.getBundle("org.locationtech.udig.libs.db2").getEntry("/libs"); //$NON-NLS-1$ //$NON-NLS-2$
    }

}
