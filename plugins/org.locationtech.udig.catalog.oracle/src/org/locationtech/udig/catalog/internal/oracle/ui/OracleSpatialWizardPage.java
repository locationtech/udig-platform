/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2007, Refractions Research Inc.
 * (C) 2007,      Adrian Custer.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.internal.oracle.ui;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;
import org.geotools.data.oracle.OracleNGDataStoreFactory;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.internal.oracle.OraclePlugin;
import org.locationtech.udig.catalog.internal.oracle.OracleServiceExtension;
import org.locationtech.udig.catalog.oracle.internal.Messages;
import org.locationtech.udig.catalog.ui.preferences.AbstractProprietaryDatastoreWizardPage;
import org.locationtech.udig.catalog.ui.preferences.AbstractProprietaryJarPreferencePage;
import org.locationtech.udig.catalog.ui.wizard.DataBaseConnInfo;

/**
 * Enter Oracle connection parameters.
 *
 * @author David Zwiers, dzwiers, for Refractions Research, Inc.
 * @author Jesse Eichar, jeichar, for Refractions Research, Inc.
 * @author Justin Deoliveira, jdeolive, for Refractions Research, Inc.
 * @author Amr Alam, aalam, for Refractions Research, Inc.
 * @author Richard Gould, rgould, for Refractions Research, Inc.
 * @author Cory Horner, chorner, for Refractions Research, Inc.
 * @author Adrian Custer, acuster.
 * @since 0.3
 */
public class OracleSpatialWizardPage extends AbstractProprietaryDatastoreWizardPage {

    public static final String IMAGE_KEY = ""; //$NON-NLS-1$

    private static final String ORACLE_WIZARD = "ORACLE_WIZARD"; //$NON-NLS-1$

    private static final String ORACLE_RECENT = "ORACLE_RECENT"; //$NON-NLS-1$

    private static final DataBaseConnInfo DEFAULT_ORACLE_CONN_INFO = new DataBaseConnInfo("", //$NON-NLS-1$
            "1521", "", "", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

    public OracleSpatialWizardPage() {

        // Call super with dialog title string
        super(Messages.OracleSpatialWizardPage_wizardTitle);

        // Get any stored settings or create a new one
        settings = OraclePlugin.getDefault().getDialogSettings().getSection(ORACLE_WIZARD);
        if (settings == null) {
            settings = OraclePlugin.getDefault().getDialogSettings().addNewSection(ORACLE_WIZARD);
        }

        // Add the name so the parent can store back to this same section
        settingsArrayName = ORACLE_RECENT;

        // Populate the Settings: default, current, and past list
        defaultDBCI.setParameters(DEFAULT_ORACLE_CONN_INFO);
        currentDBCI.setParameters(defaultDBCI);
        String[] recent = settings.getArray(ORACLE_RECENT);
        if (null != recent) {
            for (String s : recent) {
                DataBaseConnInfo dbs = new DataBaseConnInfo(s);
                if (!storedDBCIList.contains(dbs))
                    storedDBCIList.add(dbs);
            }
        }

        // Populate the Char and CharSeq exclusion lists
        // TODO: when we activate Verification
    }

    @Override
    protected AbstractProprietaryJarPreferencePage getPreferencePage() {
        return new OracleSpatialPreferences();
    }

    public String getId() {
        return "org.locationtech.udig.catalog.ui.oracle"; //$NON-NLS-1$
    }

    @Override
    protected String getRestartMessage() {
        return Messages.OracleSpatialWizardPage_restart;
    }

    @Override
    protected String getDriversMessage() {
        return Messages.OracleSpatialWizardPage_drivers;
    }

    @Override
    protected void doCreateWizardPage(Composite parent) {

        // For Drag 'n Drop as well as for general selections
        // look for a URL as part of the selection
        // TODO: sync with Postgis plugin
        ISelection tmpSelection = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().getSelection();
        IStructuredSelection selection = null;
        if (tmpSelection == null || !(tmpSelection instanceof IStructuredSelection)) {
            selection = new StructuredSelection();
        } else {
            selection = (IStructuredSelection) tmpSelection;
        }

        String selectedText = null;
        for (Iterator<?> itr = selection.iterator(); itr.hasNext();) {
            Object o = itr.next();
            if (o instanceof URL || o instanceof String) {
                selectedText = (String) o;
                // jdbc:postgresql://host:port/database
                // jdbc:oracle:thin:@host:port:instance
                if (selectedText.contains("jdbc:oracle:thin:@")) { //$NON-NLS-1$
                    break;
                }
                selectedText = null;
            }
        }
        if (selectedText != null) {
            int startindex = selectedText.indexOf("@"); //$NON-NLS-1$
            int hostEnd = selectedText.indexOf(":", startindex); //$NON-NLS-1$
            int portEnd = selectedText.indexOf(":", hostEnd); //$NON-NLS-1$
            int databaseEnd = selectedText.indexOf(":", portEnd); //$NON-NLS-1$

            String the_host = selectedText.substring(startindex, hostEnd);
            String the_port = selectedText.substring(hostEnd, portEnd);
            String the_database = selectedText.substring(portEnd, databaseEnd);

            currentDBCI.setHost(the_host);
            if (!the_port.equalsIgnoreCase("")) { //$NON-NLS-1$
                currentDBCI.setPort(the_port);
            }
            currentDBCI.setDb(the_database);
        }
    }

    public boolean canProcess(Object object) {
        return getOracleURL(object) != null;
    }

    protected String getOracleURL(Object data) {
        String url = null;
        if (data instanceof String) {
            String[] strings = ((String) data).split("\n"); //$NON-NLS-1$
            url = strings[0];
            if (!url.toLowerCase().contains("jdbc:oracle")) { //$NON-NLS-1$
                url = null;
            }
        }

        return url;
    }

    @Override
    protected Group createAdvancedControl(Composite arg0) {
        return null;
    }

    @Override
    protected OracleNGDataStoreFactory getDataStoreFactorySpi() {
        return OracleServiceExtension.getFactory();
    }

    @Override
    public Map<String, Serializable> getParams() {
        if (!OracleSpatialPreferences.isInstalled()) {
            return null;
        }

        if (!couldConnect()) {
            return null;
        }

        Map<String, Serializable> params = new HashMap<>();

        params.put(OracleNGDataStoreFactory.DBTYPE.key,
                (Serializable) OracleNGDataStoreFactory.DBTYPE.sample);
        params.put(OracleNGDataStoreFactory.HOST.key, currentDBCI.getHostString());
        try {
            params.put(OracleNGDataStoreFactory.PORT.key,
                    Integer.valueOf(currentDBCI.getPortString()));
        } catch (NumberFormatException e) {
            // use default port
        }
        params.put(OracleNGDataStoreFactory.USER.key, currentDBCI.getUserString());
        params.put(OracleNGDataStoreFactory.PASSWD.key, currentDBCI.getPassString());
        params.put(OracleNGDataStoreFactory.DATABASE.key, currentDBCI.getDbString());
        params.put(OracleNGDataStoreFactory.SCHEMA.key, currentDBCI.getSchemaString());

        return params;
    }

    /**
     * Grab a DataSource using the current connection parameters.
     *
     * @see org.locationtech.udig.catalog.internal.ui.datastore.DataBaseRegistryWizardPage#getConnection()
     * @return DataSource, or null if could not connect
     */
    @Override
    protected DataSource getDataSource() {
        final String hostText = currentDBCI.getHostString();
        final String portText = currentDBCI.getPortString();
        final String userText = currentDBCI.getUserString();
        final String passText = currentDBCI.getPassString();
        final String db = currentDBCI.getDbString();

        // Double check, should never trigger
        if (!couldConnect()) {
            return null;
        }
        if (dataSource == null) {
            runInPage(new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor)
                        throws InvocationTargetException, InterruptedException {
                    if (monitor == null)
                        monitor = new NullProgressMonitor();

                    // may need to run in a background Job
                    // in order to leave this thread free for the user interface?
                    //
                    Connection connection = null;
                    try {
                        monitor.beginTask(Messages.OracleSpatialWizardPage_connectionTask,
                                IProgressMonitor.UNKNOWN);

                        if (dataSource != null) {
                            // close previous connection
                            try {
                                dataSource.close();
                            } catch (SQLException e1) {
                                // it's dead anyhow
                            }
                        }
                        Map<String, Serializable> params = new HashMap<>();
                        params.put(OracleNGDataStoreFactory.HOST.key, hostText);
                        params.put(OracleNGDataStoreFactory.PORT.key,
                                (Integer) OracleNGDataStoreFactory.PORT.parse(portText));
                        params.put(OracleNGDataStoreFactory.DATABASE.key, db);
                        params.put(OracleNGDataStoreFactory.USER.key, userText);
                        params.put(OracleNGDataStoreFactory.PASSWD.key, passText);
                        dataSource = OracleServiceExtension.getFactory().createDataSource(params);

                        // Is this needed/useful?
                        DriverManager.setLoginTimeout(3);
                        monitor.worked(1);
                        monitor.subTask("establish connection"); //$NON-NLS-1$

                        if (monitor.isCanceled()) {
                            dataSource.close();
                            dataSource = null;
                        } else {
                            connection = dataSource.getConnection();
                        }

                        monitor.subTask("connected"); //$NON-NLS-1$
                        monitor.worked(1);
                    } catch (Throwable shame) {
                        if (dataSource != null) {
                            try {
                                dataSource.close();
                            } catch (SQLException e1) {
                                // we are closing already in a state of shame
                            }
                            dataSource = null;
                        }
                        // How to indicate failure? runInPage will show this to user
                        throw new InvocationTargetException(shame, shame.getLocalizedMessage());
                    } finally {
                        if (connection != null) {
                            try {
                                connection.close();
                            } catch (SQLException e) {
                                // ignore since we are closing
                            }
                        }
                    }
                }
            });
        }
        return dataSource;
    }

    /**
     * TODO summary sentence for hasSchema ...
     *
     * @see org.locationtech.udig.catalog.internal.ui.datastore.DataBaseRegistryWizardPage#dbmsUsesSchema()
     * @return
     */
    @Override
    protected boolean dbmsUsesSchema() {
        return true;
    }

    @Override
    protected boolean doIsPageComplete() {
        Map<String, Serializable> p = getParams();
        if (p == null)
            return false;
        boolean r = getDataStoreFactorySpi().canProcess(p);
        return r;
    }

    public List<IResolve> getResources(IProgressMonitor monitor) throws Exception {
        if (!isPageComplete())
            return null;

        OracleServiceExtension creator = new OracleServiceExtension();
        IService service = creator.createService(null, getParams());
        service.getInfo(monitor); // load

        List<IResolve> servers = new ArrayList<>();
        servers.add(service);
        return servers;
    }
}
