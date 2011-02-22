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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.geotools.data.DataStoreFactorySpi.Param;
import org.geotools.data.oracle.OracleConnectionFactory;
import org.geotools.data.oracle.OracleDataStoreFactory;

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.oracle.OracleServiceExtension;
import net.refractions.udig.catalog.oracle.internal.Messages;
import net.refractions.udig.catalog.ui.UDIGConnectionPage;
import net.refractions.udig.catalog.ui.preferences.AbstractProprietaryDatastoreWizardPage;
import net.refractions.udig.catalog.ui.preferences.AbstractProprietaryJarPreferencePage;
import net.refractions.udig.ui.PlatformGIS;

/**
 * Enter Oracle connection parameters.
 *
 * @author dzwiers
 * @since 0.3
 */
public class OracleSpatialWizardPage extends AbstractProprietaryDatastoreWizardPage implements UDIGConnectionPage {

    public OracleSpatialWizardPage( ) {
        super(Messages.OracleSpatialWizardPage_wizardTitle);
    }

    @Override
    protected AbstractProprietaryJarPreferencePage getPreferencePage() {
        return new OracleSpatialPreferences();
    }

    public String getId() {
        return "net.refractions.udig.catalog.ui.oracle"; //$NON-NLS-1$
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
        // look for a url as part of the selction
        ISelection tmpSelection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
        IStructuredSelection selection=null;
        if (tmpSelection == null || !(tmpSelection instanceof IStructuredSelection)) {
            selection=new StructuredSelection();
        } else {
            selection=(IStructuredSelection) tmpSelection;
        }

        String selectedText = null;
        for (Iterator itr = selection.iterator(); itr.hasNext();) {
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
            ((Text) host).setText(the_host);

            String the_port = selectedText.substring(hostEnd, portEnd);
            if (!the_port.equalsIgnoreCase("")) { //$NON-NLS-1$
                port.setText(the_port);
            }
            String the_database = selectedText.substring(portEnd,
                    databaseEnd);
            ((CCombo) database).add(the_database);
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

    /**
     * TODO summary sentence for createAdvancedControl ...
     *
     */
    protected Group createAdvancedControl(Composite arg0) {
        port.setText("1521"); //$NON-NLS-1$
        return null;
    }

    /*
     * dbtype host port user passwd instance schema namespace
     */

    private static OracleDataStoreFactory factory = new OracleDataStoreFactory();

    protected OracleDataStoreFactory getDataStoreFactorySpi() {
        return factory;
    }

    public Map<String, Serializable> getParams() {
        if (!OracleSpatialPreferences.isInstalled())
            return null;

        if (getHostText() == null || getPortText() == null || getUserText() == null
                || getDBText() == null || getSchemaText() == null)
            return null;

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        Param[] dbParams = factory.getParametersInfo();

        params.put(dbParams[0].key, "oracle"); //$NON-NLS-1$
        params.put(dbParams[1].key, getHostText());

        String port1 = getPortText();
        params.put(dbParams[2].key, port1);

        String user1 = getUserText();
        params.put(dbParams[3].key, user1);

        String pass1 = getPassText();
        params.put(dbParams[4].key, pass1);

        String db = getDBText();
        params.put(dbParams[5].key, db);

        String schema1 = getSchemaText();
        params.put(dbParams[6].key, schema1);

        // not sureabout this line
        // params.put(dbParams[7].key,"MAPINFO"); //$NON-NLS-1$

        return params;
    }

    /**
     *
     * @return
     */
    protected String getPortText() {
        return this.port.getText();
    }

    /**
     * TODO summary sentence for getConnection ...
     *
     * @see net.refractions.udig.catalog.internal.ui.datastore.DataBaseRegistryWizardPage#getConnection()
     * @return
     */
    protected Connection getConnection() {
        try {
            String db1 = ((Text) database).getText();
            db1 = "".equals(db1) ? null : db1; //$NON-NLS-1$
            final String db=db1;
            final String host1=((Text) host).getText();
            final String port1=port.getText();
            final String user1=user.getText();
            final String pass1=pass.getText();
            if (db == null) {
                //don't bother
                return null;
            }
            getContainer().run(true, true, new IRunnableWithProgress(){

                public void run( IProgressMonitor monitor ) throws InvocationTargetException,
                        InterruptedException {
                   PlatformGIS.runBlockingOperation(new IRunnableWithProgress(){

                    public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
                        monitor.beginTask(Messages.OracleSpatialWizardPage_connectionTask, IProgressMonitor.UNKNOWN);
                        if (OracleSpatialWizardPage.this.connection != null)
                            try {
                                OracleSpatialWizardPage.this.connection.close();
                            } catch (SQLException e1) {
                                // it's dead anyhow
                            }



                            OracleConnectionFactory connFac = new OracleConnectionFactory(
                                    host1, port1, db);
                            connFac.setLogin(user1, pass1);
                            DriverManager.setLoginTimeout(3);


                        try {
                            OracleSpatialWizardPage.this.connection = connFac.getConnectionPool()
                                    .getConnection();
                            if (OracleSpatialWizardPage.this.connection != null) {
                                OracleSpatialWizardPage.this.database.getDisplay().asyncExec(new Runnable(){
                                    public void run() {
                                        OracleSpatialWizardPage.this.database.notifyListeners(SWT.FocusIn,
                                                new Event());

                                    }
                                });
                            }
                        } catch (SQLException e) {
                            throw new InvocationTargetException(e, e.getLocalizedMessage());
                        }                    }

                   }, monitor);
                }
            });
        } catch (InvocationTargetException e2) {
            throw new RuntimeException(e2.getLocalizedMessage(), e2);
        } catch (InterruptedException e2) {
            // Don't know why this exception doesn't do anything.
        }

        return connection;
    }

    public void focusGained(FocusEvent e) {
        if (e.widget != null) {
            if (e.widget.equals(schema)) {
                super.focusGained(e);
            }
        }
    }

    /**
     * TODO summary sentence for dispose ...
     *
     * @see org.eclipse.jface.dialogs.IDialogPage#dispose()
     *
     */
    public void dispose() {
        super.dispose();
        if (connection != null)
            try {
                connection.close();
            } catch (SQLException e) {
                // it's dead anyhow
            }
    }

    private Connection connection = null;

    public void modifyText(ModifyEvent e) {
        if (e.widget != null) {
            if (e.widget.equals(host) || e.widget.equals(port)
                    || e.widget.equals(user) || e.widget.equals(pass)) {
                setErrorMessage(null);
            }
        }
        super.modifyText(e);
    }

    /**
     * TODO summary sentence for isDBCombo ...
     *
     * @see net.refractions.udig.catalog.internal.ui.datastore.DataBaseRegistryWizardPage#isDBCombo()
     * @return
     */
    protected boolean isDBCombo() {
        return false;
    }

    protected boolean isHostCombo() {
        return false;
    }

    /**
     * TODO summary sentence for hasSchema ...
     *
     * @see net.refractions.udig.catalog.internal.ui.datastore.DataBaseRegistryWizardPage#hasSchema()
     * @return
     */
    protected boolean hasSchema() {
        return true;
    }

    protected String getHostText() {
        String text = ((Text) host).getText();
        if( text.trim().length()==0 )
            text=null;
        return host == null ? null : text;
    }

    protected String getDBText() {
        String text = ((Text) database).getText();
        if( text.trim().length()==0 )
            text=null;
        return database == null ? null : text;
    }

    protected String getSchemaText() {
        String text = schema.getText();
        if( text.trim().length()==0 )
            text=null;
        return schema == null ? null : text;
    }

    protected String getUserText() {
        String text = user.getText();
        if( text.trim().length()==0 )
            text=null;
        return user == null ? null : text;
    }

    protected String getPassText() {
        String text = pass.getText();
        if( text.trim().length()==0 )
            text=null;
        return pass == null ? null : text;
    }

    @Override
    protected boolean doIsPageComplete() {
        Map<String,Serializable> p = getParams();
        if (p == null)
            return false;
        boolean r = factory.canProcess(p);
        return r;
    }


    public List<IResolve> getResources(IProgressMonitor monitor)
            throws Exception {
        if (!isPageComplete())
            return null;

        OracleServiceExtension creator = new OracleServiceExtension();
        IService service = creator.createService(null, getParams());
        service.getInfo(monitor); // load

        List<IResolve> servers = new ArrayList<IResolve>();
        servers.add(service);
        return servers;
    }
}
