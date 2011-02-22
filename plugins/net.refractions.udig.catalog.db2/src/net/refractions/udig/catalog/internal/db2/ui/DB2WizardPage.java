/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2005, Refractions Research Inc.
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
package net.refractions.udig.catalog.internal.db2.ui;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.db2.DB2Plugin;
import net.refractions.udig.catalog.db2.internal.Messages;
import net.refractions.udig.catalog.internal.db2.DB2ServiceExtension;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.UDIGConnectionPage;
import net.refractions.udig.catalog.ui.preferences.AbstractProprietaryDatastoreWizardPage;
import net.refractions.udig.catalog.ui.preferences.AbstractProprietaryJarPreferencePage;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFactorySpi.Param;
import org.geotools.data.db2.DB2ConnectionFactory;
import org.geotools.data.db2.DB2DataStoreFactory;

/**
 * Specify DB2 database connection parameters.
 * <p>
 * </p>
 *
 * @author Justin Deoliveira,Refractions Research Inc.,jdeolive@refractions.net
 * @since 1.0.1
 */
public class DB2WizardPage extends AbstractProprietaryDatastoreWizardPage implements UDIGConnectionPage {

    private static final int COMBO_HISTORY_LENGTH = 15;
    private static final String DB2_RECENT = "DB2_RECENT"; //$NON-NLS-1$
    private static final String DB2_WIZARD = "DB2_WIZARD"; //$NON-NLS-1$
    private static DB2DataStoreFactory factory = new DB2DataStoreFactory();
    ArrayList<DataBaseConnInfo> dbData;
    boolean dirty = true;

    /** DB2WizardPage IMAGE_KEY field - not sure what it is used for */
    public final String IMAGE_KEY = "DB2PageImage"; //$NON-NLS-1$
    Connection realConnection;
    private IDialogSettings settings;
    private DB2Preferences preferences;

    /**
     * Constructs a DB2 database connection wizard page. Reads any settings that may have been saved
     * from a previous session.
     */
    public DB2WizardPage() {
        super(Messages.DB2WizardPage_title);
        this.settings = DB2Plugin.getDefault().getDialogSettings().getSection(DB2_WIZARD);
        if (this.settings == null) {
            this.settings = DB2Plugin.getDefault().getDialogSettings().addNewSection(DB2_WIZARD);
        }
    }
    /**
     * Adds an entry to a history, while taking care of duplicate history items and excessively long
     * histories. The assumption is made that all histories should be of length
     * <code>COMBO_HISTORY_LENGTH</code>.
     *
     * @param history the current history
     * @param newEntry the entry to add to the history Stolen from
     *        org.eclipse.team.internal.ccvs.ui.wizards.ConfigurationWizardMainPage
     */
    private void addToHistory( List<String> history, String newEntry ) {
        history.remove(newEntry);
        history.add(0, newEntry);

        // since only one new item was added, we can be over the limit
        // by at most one item
        if (history.size() > COMBO_HISTORY_LENGTH)
            history.remove(COMBO_HISTORY_LENGTH);
    }

    /**
     * Adds an entry to a history, while taking care of duplicate history items and excessively long
     * histories. The assumption is made that all histories should be of length
     * <code>COMBO_HISTORY_LENGTH</code>.
     *
     * @param history the current history
     * @param newEntry the entry to add to the history
     * @return the history with the new entry appended Stolen from
     *         org.eclipse.team.internal.ccvs.ui.wizards.ConfigurationWizardMainPage
     */
    private String[] addToHistory( String[] history, String newEntry ) {
        ArrayList<String> l = new ArrayList<String>(Arrays.asList(history));
        addToHistory(l, newEntry);
        String[] r = new String[l.size()];
        l.toArray(r);
        return r;
    }
    /**
     * Checks if all user input fields are non-empty.
     *
     * @return true if all needed fields are non-empty.
     */

    protected boolean areAllFieldsFilled() {
        if (getSchemaText().length() == 0)
            return false;
        return areDbFieldsFilled();
    }
    /**
     * Checks if port, host, userid, password and database name fields all are non-empty.
     *
     * @return true if all needed fields are non-empty.
     */
    protected boolean areDbFieldsFilled() {
        if (!DB2Preferences.isInstalled())
            return false;


        if ((getPortText().length() == 0) || (getHostText().length() == 0) || (getUserText().length() == 0)
                || (getPassText().length() == 0) || (getDBText().length() == 0)) {
            return false;
        }
        return true;
    }

    /**
     * Perform additional processing when the GUI is created. Sets the default value for the port
     * field and disables the schema field.
     *
     * @param arg0
     * @return null
     */
    @Override
    protected Group createAdvancedControl( Composite arg0 ) {
        return null;
    }

    private String emptyAsNull( String value ) {
        if (value.length() == 0)
            return null;
        return value;
    }
    /**
     * Always returns false as we want to keep all schema candidates.
     *
     * @param schemaName
     * @return false
     */
    @Override
    protected boolean excludeSchema( String schemaName ) {
        return false;
    }
    /**
     * Gets a connection to the DB2 database. The port, host, userid, password and database name
     * must have been specified in order for the connection to succeed.
     *
     * @return a database Connection
     * @throws Exception
     */
    @Override
    protected Connection getConnection() {

        final String portText = getPortText();
        final String hostText = getHostText();
        final String userText = getUserText();
        final String passText = getPassText();
        final String db = getDBText();

        if (!areDbFieldsFilled()) {
            return null;
        }

        if (this.dirty || this.realConnection == null) {
            this.dirty = false;

            try {
                getContainer().run(true, true, new IRunnableWithProgress(){

                    public void run( IProgressMonitor monitor ) throws InvocationTargetException,
                            InterruptedException {
                        PlatformGIS.runBlockingOperation(new IRunnableWithProgress(){

                            public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
                                monitor.beginTask(Messages.DB2WizardPage_connectionTask, IProgressMonitor.UNKNOWN);
                                if (DB2WizardPage.this.realConnection != null)
                                    try {
                                        DB2WizardPage.this.realConnection.close();
                                    } catch (SQLException e1) {
                                        // it's dead anyhow
                                    }

                                DB2ConnectionFactory connFac = new DB2ConnectionFactory(hostText, portText,
                                        db);
                                connFac.setLogin(userText, passText);
                                DriverManager.setLoginTimeout(3);
                                try {
                                    DB2WizardPage.this.realConnection = connFac.getConnectionPool()
                                            .getConnection();
                                    if (DB2WizardPage.this.realConnection != null) {
                                        DB2WizardPage.this.database.getDisplay().asyncExec(new Runnable(){
                                            public void run() {
                                                DB2WizardPage.this.database.notifyListeners(SWT.FocusIn,
                                                        new Event());

                                            }
                                        });
                                    }
                                } catch (SQLException e) {
                                    throw new InvocationTargetException(e, e.getLocalizedMessage());
                                }                            }

                        }, monitor);
                    }
                });
            } catch (InvocationTargetException e2) {
                preferences.performDefaults();
                throw new RuntimeException(e2.getLocalizedMessage(), e2);
            } catch (InterruptedException e2) {
                // Don't know why this exception doesn't do anything.
            }
        }

        return this.realConnection;
    }
    /**
     * Returns the DB2DataStoreFactory.
     *
     * @return the DB2DataStoreFactory
     */
    @Override
    protected DataStoreFactorySpi getDataStoreFactorySpi() {
        return factory;
    }
    protected String getDBText() {
        return ((Text) this.database).getText();
    }
    protected String getHostText() {
        return ((CCombo) this.host).getText();
    }
    /**
     * Returns a string with the name of the DB2 plugin
     *
     * @return the DB2 plugin name
     */
    public String getId() {
        return "net.refractions.udig.catalog.ui.db2"; //$NON-NLS-1$
    }
    /**
     * Returns the parameters Empty strings are converted to null to work correctly with
     * factory.canProcess.
     *
     * @return a map of parameter values
     */
    @Override
    public Map<String, Serializable> getParams() {

        if( !areDbFieldsFilled() || getConnection()==null ){
            return null;
        }

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        Param[] dbParams = factory.getParametersInfo();
        params.put(dbParams[0].key, "db2"); //$NON-NLS-1$
        params.put(dbParams[1].key, emptyAsNull(getHostText()));
        String dbport = emptyAsNull(getPortText());
        try {
            params.put(dbParams[2].key, emptyAsNull(dbport));
        } catch (NumberFormatException e) {
            params.put(dbParams[2].key, "50000"); //$NON-NLS-1$
        }
        String db = getDBText();
        params.put(dbParams[3].key, emptyAsNull(db));

        String userName = getUserText();
        params.put(dbParams[4].key, emptyAsNull(userName));
        String password = getPassText();
        params.put(dbParams[5].key, emptyAsNull(password));

        params.put(dbParams[6].key, emptyAsNull(getSchemaText()));

        return params;
    }
    /**
     * This method does nothing.
     * TODO: perhaps return a jdbc url?
     */
    public List<URL> getURLs() {
    	return null;
    }

    protected String getPassText() {
        return this.pass.getText();
    }

    protected String getPortText() {
        return this.port.getText();
    }
    /**
     * Creates the DB2 service so we can do real work. Saves the values of text fields from the GUI
     * so that they can be used the next time this GUI page is displayed.
     *
     * @param monitor
     * @return a List with the DB2 service
     * @throws Exception
     */
    public List<IService> getResources( IProgressMonitor monitor ) throws Exception {
        if (!isPageComplete())
            return null;

        DB2ServiceExtension creator = new DB2ServiceExtension();
        IService service = creator.createService(null, getParams());
        service.getInfo(monitor); // load

        List<IService> servers = new ArrayList<IService>();
        servers.add(service);

        /*
         * Success! Store the connection settings in history.
         */
        saveWidgetValues();

        return servers;
    }

    /**
     * Gets the selected schema value.
     *
     * @return field text contents
     */
    protected String getSchemaText() {
        if( schema==null )
            return ""; //$NON-NLS-1$
        return ((CCombo) schema).getText();
    }
    /**
     * Gets the user value.
     *
     * @return field text contents
     */
    protected String getUserText() {
        return this.user.getText();
    }

    /**
     * DB2 always requires the schema.
     *
     * @return true
     */
    @Override
    protected boolean hasSchema() {
        return true;
    }
    /**
     * DB2 doesn't allow database name selection from a list.
     *
     * @return false
     */
    @Override
    protected boolean isDBCombo() {
        return false;
    }

    /**
     * DB2 allows a host selection list.
     *
     * @return true
     */
    @Override
    protected boolean isHostCombo() {
        return true;
    }

    @Override
    public boolean doIsPageComplete() {
        boolean isComplete = false;
        if (areDbFieldsFilled()) {
            this.schema.setEnabled(true);
        } else {
            if( schema!=null )
            this.schema.setEnabled(false);
        }
        if (areAllFieldsFilled())
            isComplete = factory.canProcess(getParams());
        return isComplete;
    }
    /**
     * Fills the combo-box with the schema values available for the specified database. The DB2
     * catalog table db2gse.st_geometry_columns is used to get a list of all the schema values
     * associated with tables that have spatial columns.
     */
    @Override
    protected void populateSchema() {
        if (!hasSchema()) // error
            return;

        // save some state
        CCombo schemaCombo = this.schema;
        int selected = schemaCombo.getSelectionIndex();
        String string = null;
        if (selected > -1) {
            string = schemaCombo.getItem(selected);
        }

        schemaCombo.removeAll();
        schemaCombo.setText(""); //$NON-NLS-1$

        Connection con = null;
        try {
            con = getConnection();
        } catch (Exception e) {
            CatalogUIPlugin.log(e.getLocalizedMessage(), e);
            setErrorMessage(e.getLocalizedMessage());
        }

        if (con == null)
            return;
        String sqlStmt = "select distinct table_schema from db2gse.st_geometry_columns"; //$NON-NLS-1$

        ResultSet rs = null;
        try {
            Statement stmt = con.createStatement();
            rs = stmt.executeQuery(sqlStmt);
            while( rs.next() ) {
                String schemaName = rs.getString(1).trim();
                if (!excludeSchema(schemaName))
                    schemaCombo.add(schemaName);
            }
            if (schemaCombo.getItemCount() > 0)
                schemaCombo.select(0);

        } catch (SQLException e) {
            setErrorMessage(e.getLocalizedMessage());
            // e.printStackTrace();
            // schema.removeAll();
            // schema.setText(""); //$NON-NLS-1$
            return;
        }

        if (string != null) {
            String[] items = schemaCombo.getItems();
            for( int i = 0; items != null && i < items.length; i++ ) {
                if (string.equals(items[i])) {
                    schemaCombo.select(i);
                    return;
                }
            }
        }
    }
    /**
     * Saves the widget values
     */
    private void saveWidgetValues() {
        // Update history
        if (this.settings != null) {
            String[] recentDB2s = this.settings.getArray(DB2_RECENT);
            if (recentDB2s == null) {
                recentDB2s = new String[0];
            }
            String dbs = new DataBaseConnInfo(getHostText(), getPortText(), getUserText(), getPassText(), getDBText(), getSchemaText())
                    .toString();
            recentDB2s = addToHistory(recentDB2s, dbs);
            this.settings.put(DB2_RECENT, recentDB2s);
        }
    }
    /**
     * Sets the database text field.
     *
     * @param value
     */
    protected void setDBText( String value ) {
        ((Text) DB2WizardPage.this.database).setText(value);
    }
    /**
     * Sets the password text field.
     *
     * @param value
     */
    protected void setPassText( String value ) {
        this.pass.setText(value);
    }
    /**
     * Sets the port text field.
     *
     * @param value
     */
    protected void setPortText( String value ) {
        this.port.setText(value);
    }

    /**
     * Sets the userid text field.
     *
     * @param value
     */
    protected void setUserText( String value ) {
        this.user.setText(value);
    }
    @Override
    protected void doCreateWizardPage( Composite parent ) {

        this.port.setTextLimit(5);
        this.port.setText("50000"); //$NON-NLS-1$
        this.schema.setEnabled(false);

        String[] recentDB2s = this.settings.getArray(DB2_RECENT);
        ArrayList<String> hosts = new ArrayList<String>();
        this.dbData = new ArrayList<DataBaseConnInfo>();
        if (recentDB2s != null) {
            for( String recent : recentDB2s ) {
                DataBaseConnInfo dbs = new DataBaseConnInfo(recent);
                this.dbData.add(dbs);
                hosts.add(dbs.getHost());
            }
        }
        if (hosts.size() > 0) {
            ((CCombo) this.host).setItems(hosts.toArray(new String[0]));
            ((CCombo) this.host).addModifyListener(new ModifyListener(){
                public void modifyText( ModifyEvent e ) {
                    if (e.widget != null) {
                        for( DataBaseConnInfo db : DB2WizardPage.this.dbData ) {
                            if (db.getHost().equalsIgnoreCase(getHostText())) {
                                setPortText(db.getPort());
                                setUserText(db.getUser());
                                setPassText(db.getPass());
                                setPassText(db.getPass());
                                setDBText(db.getDb());
                                 DB2WizardPage.this.schema.setText(db.getSchema());
                                break;
                            }
                        }
                    }
                }
            });
        }
    }
    @Override
    protected String getDriversMessage() {
        return Messages.DB2WizardPage_installDrivers;
    }
    @Override
    protected AbstractProprietaryJarPreferencePage getPreferencePage() {
        return new DB2Preferences();
    }
    @Override
    protected String getRestartMessage() {
        return Messages.DB2WizardPage_warning;
    }

}
