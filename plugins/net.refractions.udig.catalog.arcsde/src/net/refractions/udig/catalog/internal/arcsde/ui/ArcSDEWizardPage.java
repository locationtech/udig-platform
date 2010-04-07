/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004-2007, Refractions Research Inc.
 *    (C) 2007,      Adrian Custer.
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
package net.refractions.udig.catalog.internal.arcsde.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.arcsde.internal.Messages;
import net.refractions.udig.catalog.internal.arcsde.ArcServiceExtension;
import net.refractions.udig.catalog.internal.arcsde.ArcsdePlugin;
import net.refractions.udig.catalog.ui.preferences.AbstractProprietaryDatastoreWizardPage;
import net.refractions.udig.catalog.ui.preferences.AbstractProprietaryJarPreferencePage;
import net.refractions.udig.catalog.ui.wizard.DataBaseConnInfo;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.geotools.arcsde.ArcSDEDataStoreFactory;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataAccessFactory.Param;

/**
 * The concrete implementation of the wizard 'Page' used to import data using a database in the
 * ArcSDE format from ESRI.
 * 
 * @author David Zwiers, dzwiers, for Refractions Research, Inc.
 * @author Richard Gould, rgould, for Refractions Research, Inc.
 * @author Jody Garnett, jody, for Refractions Research, Inc.
 * @author Justin Deoliveira, jdeolive, for Refractions Research, Inc.
 * @author Jesse Eichar, jeichar, for Refractions Research, Inc.
 * @author Amr Alam, aalam, for Refractions Research, Inc.
 * @author Cory Horner, chorner, for Refractions Research, Inc.
 * @author Adrian Custer, acuster.
 * 
 * @since 0.6
 */
public class ArcSDEWizardPage extends AbstractProprietaryDatastoreWizardPage {

    // TITLE IMAGE
    public static final String IMAGE_KEY = ""; //$NON-NLS-1$

    // STORED SETTINGS
    private static final String ARCSDE_WIZARD = "ARCSDE_WIZARD"; //$NON-NLS-1$

    private static final String ARCSDE_RECENT = "ARCSDE_RECENT"; //$NON-NLS-1$

    // CONNECTION
    private static final DataBaseConnInfo DEFAULT_ARCSDE_CONN_INFO = new DataBaseConnInfo(
            "", "", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

    private static ArcSDEDataStoreFactory factory = new ArcSDEDataStoreFactory();

    public ArcSDEWizardPage() {

        // Call super with dialog title string
        super(Messages.ArcSDEWizardPage_title);

        // Get any stored settings or create a new one
        settings = ArcsdePlugin.getDefault().getDialogSettings().getSection(ARCSDE_WIZARD);
        if (settings == null) {
            settings = ArcsdePlugin.getDefault().getDialogSettings().addNewSection(ARCSDE_WIZARD);
        }

        // Add the name so the parent can store back to this same section
        settingsArrayName = ARCSDE_RECENT;

        // Populate the Settings: default, current, and past list
        defaultDBCI.setParameters(DEFAULT_ARCSDE_CONN_INFO);
        currentDBCI.setParameters(defaultDBCI);
        String[] recent = settings.getArray(ARCSDE_RECENT);
        if (null != recent) {
            for (String s : recent) {
                DataBaseConnInfo dbs = new DataBaseConnInfo(s);
                if (!storedDBCIList.contains(dbs))
                    storedDBCIList.add(dbs);
            }
        }

        // Populate the db and schema exclusion lists
        //        dbExclusionList.add("");                                                //$NON-NLS-1$
        //        schemaExclusionList.add("");                                            //$NON-NLS-1$

        // Populate the Char and CharSeq exclusion lists
        // TODO: when we activate Verification
    }

    // UTILITY METHODS
    protected DataStoreFactorySpi getDataStoreFactorySpi() {
        return factory;
    }

    public String getId() {
        return "net.refractions.udig.catalog.ui.arcsde"; //$NON-NLS-1$
    }

    public Map<String, Serializable> getParams() {
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        Param[] dbParams = factory.getParametersInfo();
        params.put(dbParams[1].key, "arcsde"); //$NON-NLS-1$
        params.put(dbParams[2].key, currentDBCI.getHostString());
        String port1 = currentDBCI.getPortString();
        try {
            params.put(dbParams[3].key, Integer.valueOf(port1));
        } catch (NumberFormatException e) {
            params.put(dbParams[3].key, Integer.valueOf(5432));
        }

        String db = currentDBCI.getDbString();
        params.put(dbParams[4].key, db);

        String user1 = currentDBCI.getUserString();
        params.put(dbParams[5].key, user1);
        String pass1 = currentDBCI.getPassString();
        params.put(dbParams[6].key, pass1);

        return params;
    }

    protected boolean dbmsUsesSchema() {
        return false;
    }

    protected DataSource getDataSource() {
        return null;
    }

    @Override
    protected boolean doIsPageComplete() {
        Map<String, Serializable> p = getParams();
        if (p == null)
            return false;
        boolean r = factory.canProcess(p);
        return r;
    }

    /*
     * @seenet.refractions.udig.catalog.ui.UDIGImportPage#getResources(org.eclipse.core.runtime.
     * IProgressMonitor)
     */
    public List<IService> getResources(IProgressMonitor monitor) throws Exception {
        if (!isPageComplete())
            return null;

        ArcServiceExtension creator = new ArcServiceExtension();

        IService service = creator.createService(null, getParams());
        service.getInfo(monitor); // load

        List<IService> servers = new ArrayList<IService>();
        servers.add(service);

        /*
         * Success! Store the URL in history.
         */
        // saveWidgetValues();
        return servers;
    }

    @Override
    protected void doCreateWizardPage(Composite parent) {
        // All settings now added in parent
    }

    @Override
    protected String getDriversMessage() {
        return Messages.ArcSDEWizardPage_MissingDrivers;
    }

    @Override
    protected AbstractProprietaryJarPreferencePage getPreferencePage() {
        return new ArcSDEPreferences();
    }

    @Override
    protected String getRestartMessage() {
        return Messages.ArcSDEWizardPage_restartApp;
    }
}
