/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.internal.mysql.ui;

import static org.geotools.jdbc.JDBCDataStoreFactory.DATABASE;
import static org.geotools.jdbc.JDBCDataStoreFactory.DBTYPE;
import static org.geotools.jdbc.JDBCDataStoreFactory.HOST;
import static org.geotools.jdbc.JDBCDataStoreFactory.PASSWD;
import static org.geotools.jdbc.JDBCDataStoreFactory.PORT;
import static org.geotools.jdbc.JDBCDataStoreFactory.USER;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import net.refractions.udig.catalog.MySQLServiceExtension;
import net.refractions.udig.catalog.internal.mysql.MySQLPlugin;
import net.refractions.udig.catalog.mysql.internal.Messages;
import net.refractions.udig.catalog.ui.UDIGConnectionPage;
import net.refractions.udig.catalog.ui.wizard.DataBaseConnInfo;
import net.refractions.udig.catalog.ui.wizard.DataBaseRegistryWizardPage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.mysql.MySQLDataStoreFactory;

/**
 * Enter MySQL connection parameters. Based heavily on the postgis version of this class.
 * 
 * @author dzwiers
 * @author Harry Bullen, Intelligent Automation
 * @since 1.1.0
 */
public class MySQLWizardPage extends DataBaseRegistryWizardPage implements UDIGConnectionPage {

    private static final String MYSQL_WIZARD = "MYSQL_WIZARD"; //$NON-NLS-1$
    private static final String MYSQL_RECENT = "MYSQL_RECENT"; //$NON-NLS-1$
    private static final DataBaseConnInfo DEFAULT_MYSQL_CONN_INFO = new DataBaseConnInfo(
            "localhost", "3306", "", "", "test", ""); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    private static MySQLDataStoreFactory factory = new MySQLDataStoreFactory();

    public final String IMAGE_KEY = "MySQLWizardPageImage"; //$NON-NLS-1$
    MySQLuDigConnectionFactory mycFactory = new MySQLuDigConnectionFactory();

    public MySQLWizardPage() {
        // super class
        super(Messages.MySQLWizardPage_title);

        // load mysql Drivers
        try {
            Class.forName("com.mysql.jdbc.Driver"); //$NON-NLS-1$
        } catch (ClassNotFoundException e) {
            // TODO Handle ClassNotFoundException
            // should be big error
            throw (RuntimeException) new RuntimeException().initCause(e);
        }

        // get stored settings
        settings = MySQLPlugin.getDefault().getDialogSettings().getSection(MYSQL_WIZARD);
        if (settings == null) {
            settings = MySQLPlugin.getDefault().getDialogSettings().addNewSection(MYSQL_WIZARD);
        }

        // Add the name so the parent can store back to this same section
        settingsArrayName = MYSQL_RECENT;

        String[] recent = settings.getArray(MYSQL_RECENT);
        if (null != recent) {
            for( String s : recent ) {
                DataBaseConnInfo dbs = new DataBaseConnInfo(s);
                if (!storedDBCIList.contains(dbs))
                    storedDBCIList.add(dbs);
            }
        }

        // Populate the Settings:
        defaultDBCI.setParameters(DEFAULT_MYSQL_CONN_INFO);
        currentDBCI.setParameters(defaultDBCI);

        // populate exclusion lists
        dbExclusionList.add("mysql"); //$NON-NLS-1$
        dbExclusionList.add("information_schema"); //$NON-NLS-1$
    }

    // UTILITY METHODS
    /**
     * Called during createControl to handle Drag-n-drop of a selected object. TODO: move to
     * createControl? TODO: flesh out, what can selection be?
     */
    protected Map<String, Serializable> getParamsFromWorkbenchSelection() {
        IStructuredSelection selection = (IStructuredSelection) PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getSelectionService().getSelection();
        if (null == selection)
            return Collections.emptyMap();
        for( Iterator< ? > itr = selection.iterator(); itr.hasNext(); ) {
            Map<String, Serializable> params = mycFactory.createConnectionParameters(itr.next());
            if (params != null && !params.isEmpty())
                return params;
        }
        // TODO: Why is this here? Is this to handle the existence of a selection
        // but one for which the for loop is going to create garbage? If so,
        // let's not make the garbage.
        return Collections.emptyMap();

    }

    @Override
    public void createControl( Composite arg0 ) {
        super.createControl(arg0);

        // Handle Drag-n-drop by looking at the Map of parameters
        Map<String, Serializable> params = getParamsFromWorkbenchSelection(); // based on
        // selection
        String selectedHost = (String) params.get(MySQLDataStoreFactory.HOST.key);
        if (selectedHost != null) {
            // TODO: make sure this triggers a modifyEvent which then puts the value in
            // currentDBCI
            hostTextWgt.setText(params.get(HOST.key).toString());
            portTextWgt.setText(params.get(PORT.key).toString());
            userTextWgt.setText(params.get(USER.key).toString());
            passTextWgt.setText(params.get(PASSWD.key).toString());
            dbComboWgt.setText(params.get(DATABASE.key).toString());
        }

    }

    @Override
    protected DataStoreFactorySpi getDataStoreFactorySpi() {
        return factory;
    }

    /**
     * Returns the id of the wizard
     */
    public String getId() {
        return "net.refractions.udig.catalog.ui.mysql"; //$NON-NLS-1$
    }

    /** Can be called during createControl */
    protected Map<String, Serializable> defaultParams() {
        IStructuredSelection selection = (IStructuredSelection) PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getSelectionService().getSelection();
        return toParams(selection);
    }

    /** Retrieve "best" MySQL guess of parameters based on provided context */
    protected Map<String, Serializable> toParams( IStructuredSelection context ) {
        if (context == null)
            return Collections.emptyMap();
        for( Iterator< ? > itr = context.iterator(); itr.hasNext(); ) {
            Map<String, Serializable> params = mycFactory.createConnectionParameters(itr.next());
            if (params != null && !params.isEmpty())
                return params;
        }
        return Collections.emptyMap();
    }

    /**
     * The key method of the whole exercise, it will get a java.sql.Connection object with which we
     * can move on to work with data. Note this will be called both by the selection handler for
     * both the lookup and the connect buttons so don't make assumptions about the connection.
     * 
     * @return The java.sql.Connection we will use to get and store data
     */
    @Override
    protected DataSource getDataSource() {
        runInPage(new IRunnableWithProgress(){
            public void run( IProgressMonitor monitor ) throws InvocationTargetException,
                    InterruptedException {
                monitor.beginTask(Messages.MySQLWizardPage_0, IProgressMonitor.UNKNOWN);

                if (dataSource != null) {
                    // close previous connection
                    try {
                        dataSource.close();
                    } catch (SQLException e1) {
                        // it's dead anyhow
                    }
                }
                Connection connection = null;
                try {
                    Map<String, Serializable> params = new HashMap<String, Serializable>();
                    params.put(DBTYPE.key, "mysql");
                    params.put(HOST.key, currentDBCI.getHostString());
                    try {
                        params.put(PORT.key, (Integer) PORT.parse(currentDBCI.getPortString()));
                    } catch (Throwable e) {
                        // ignore - use default port
                    }
                    params.put(DATABASE.key, currentDBCI.getDbString());
                    dataSource = MySQLServiceExtension.getFactory().createDataSource(params);
                    dataSource.setUsername(currentDBCI.getUserString());
                    dataSource.setPassword(currentDBCI.getPassString());

                    // MySQLConnectionFactory conFac = new
                    // MySQLConnectionFactory(currentDBCI.getHostString(),
                    // Integer.parseInt(currentDBCI.getPortString()), currentDBCI.getDbString());

                    DriverManager.setLoginTimeout(3);
                    if (monitor.isCanceled()){
                        dataSource = null;
                        return;
                    }
                    connection = dataSource.getConnection();
                    monitor.done();
                } catch (IOException e) {
                    throw (InvocationTargetException) new InvocationTargetException(e);
                } catch (SQLException e) {
                    throw (InvocationTargetException) new InvocationTargetException(e);
                } finally {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            // we are already on the way out
                        }
                    }
                }
            }
        });
        return dataSource;
    }
    /**
     * Does the Database Management System (DBMS) use schemata for connections.
     * 
     * @return true because PostgreSQL uses schemata
     */
    @Override
    protected boolean dbmsUsesSchema() {
        return false;
    }

    /**
     * Will be called automatically by the RCP 'Import...' mechanism when we call updateButtons() in
     * the widgetSelected handler section for the "Connect" button.
     */
    @Override
    public boolean isPageComplete() {
        return (dataSource != null) && factory.canProcess(getParams());

    }

    /**
     * Returns the connection parameters as a DBMS specific map of key:value, with the keys coming
     * from the Geotools DataStore Factory class.
     * 
     * @return the map of factoryKey:Value pairs used for the connection
     */
    @Override
    public Map<String, Serializable> getParams() {
        Map<String, Serializable> params = new HashMap<String, Serializable>();

        params.put(MySQLDataStoreFactory.DBTYPE.key, "mysql"); //$NON-NLS-1$

        currentDBCI.treatEmptyStringAsNull(true);
        params.put(MySQLDataStoreFactory.HOST.key, currentDBCI.getHostString());
        params.put(MySQLDataStoreFactory.PORT.key, new Integer(currentDBCI.getPortString()));
        params.put(MySQLDataStoreFactory.USER.key, currentDBCI.getUserString());
        params.put(MySQLDataStoreFactory.PASSWD.key, currentDBCI.getPassString());
        params.put(MySQLDataStoreFactory.DATABASE.key, currentDBCI.getDbString());

        currentDBCI.treatEmptyStringAsNull(false);

        /*
         * if (wkbBtnWgt.getSelection()) params.put(PostgisDataStoreFactory.WKBENABLED.key,
         * Boolean.TRUE); if (looseBBoxBtnWgt.getSelection())
         * params.put(PostgisDataStoreFactory.LOOSEBBOX.key, Boolean.TRUE);
         */
        params.put("namespace", ""); //$NON-NLS-1$

        return params;
    }

}
