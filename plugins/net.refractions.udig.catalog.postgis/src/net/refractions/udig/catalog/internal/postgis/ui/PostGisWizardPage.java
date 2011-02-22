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
package net.refractions.udig.catalog.internal.postgis.ui;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.internal.postgis.PostgisPlugin;
import net.refractions.udig.catalog.postgis.internal.Messages;
import net.refractions.udig.catalog.ui.UDIGConnectionPage;
import net.refractions.udig.catalog.ui.wizard.DataBaseRegistryWizardPage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.postgis.PostgisConnectionFactory;
import org.geotools.data.postgis.PostgisDataStoreFactory;

/**
 * Enter Postgis connection parameters.
 *
 * @author dzwiers
 * @since 0.3
 */
public class PostGisWizardPage extends DataBaseRegistryWizardPage implements UDIGConnectionPage {

	private static final String POSTGIS_WIZARD = "POSTGIS_WIZARD"; //$NON-NLS-1$
    private static final String POSTGIS_RECENT = "POSTGIS_RECENT"; //$NON-NLS-1$
    private IDialogSettings settings;
    private static final int COMBO_HISTORY_LENGTH = 15;
    private static final DataBaseConnInfo NULL = new DataBaseConnInfo("","5432","","","","public");    //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

    public final String IMAGE_KEY = "PostGisWizardPageImage"; //$NON-NLS-1$
    PostgisUdigConnectionFactory pgcFactory = new PostgisUdigConnectionFactory();

    /** <code>wkb</code> field */
    protected Button wkb = null;
    /** <code>looseBBox</code> field */
    protected Button looseBBox = null;

    private String connectionDB = null;
    Connection realConnection;

    public PostGisWizardPage() {
        super(Messages.PostGisWizardPage_title);
        settings = PostgisPlugin.getDefault().getDialogSettings().getSection(POSTGIS_WIZARD);
        if (settings == null) {
            settings = PostgisPlugin.getDefault().getDialogSettings().addNewSection(POSTGIS_WIZARD);
        }
    }

    @Override
    public void createControl( Composite arg0 ) {
        super.createControl(arg0);
        final List<DataBaseConnInfo> dbData = getSavedConnInfo();

        List<String> hosts = new ArrayList<String>();
        for( DataBaseConnInfo info : dbData ) {
            hosts.add(info.getHost()+"/"+info.getDb()); //$NON-NLS-1$
        }

        if( hosts.size() > 0 ) {
            ((CCombo)host).setItems(hosts.toArray(new String[0]));
            ((CCombo)host).addSelectionListener(new SelectionListener(){

                public void widgetDefaultSelected( SelectionEvent e ) {
                    widgetSelected(e);
                }

                public void widgetSelected( SelectionEvent e ) {
                    CCombo combo = (CCombo)host;
                    String item = combo.getText();
                    for (DataBaseConnInfo db : dbData) {
                        if (item.equals(db.getHost() + "/" + db.getDb())) { //$NON-NLS-1$
                            setConnectionInfo(db, true);
                            break;
                        }
                    }
                }

            });
        }

        //For Drag 'n Drop as well as for general selections
        // look for a url as part of the selection
        Map<String,Serializable> params = defaultParams(); // based on selection
        String selectedHost = (String)params.get(PostgisDataStoreFactory.HOST.key);

        if( selectedHost != null ){
            ((CCombo)host).setText(params.get(PostgisDataStoreFactory.HOST.key).toString());
            port.setText(params.get(PostgisDataStoreFactory.PORT.key).toString());
            ((CCombo)database).setText(params.get(PostgisDataStoreFactory.DATABASE.key).toString());
            ((CCombo)schema).setText(params.get(PostgisDataStoreFactory.SCHEMA.key).toString());
            user.setText(params.get(PostgisDataStoreFactory.USER.key).toString());
            pass.setText(params.get(PostgisDataStoreFactory.PASSWD.key).toString());
        }
    }

    private void setConnectionInfo( DataBaseConnInfo db, boolean updateButtons ) {
        setFireEvents(false);
        if( db!=NULL)
            ((CCombo)host).setText(db.getHost());
        port.setText(db.getPort());
        user.setText(db.getUser());
        pass.setText(db.getPass());
        ((CCombo)database).setText(db.getDb());
        schema.setText(db.getSchema());
        if( updateButtons )
            getContainer().updateButtons();
        setFireEvents(true);
    }

    private List<DataBaseConnInfo> getSavedConnInfo() {
        String[] recentPostGiss = settings.getArray(POSTGIS_RECENT);
        final List<DataBaseConnInfo>dbData = new LinkedList<DataBaseConnInfo>();
        if (recentPostGiss != null) {
            for( String recent : recentPostGiss ) {
                DataBaseConnInfo dbs = new DataBaseConnInfo(recent);
                if(!dbData.contains(dbs) )
                    dbData.add(dbs);
            }
        }
        return dbData;
    }

   	@Override
    protected Group createAdvancedControl( Composite arg0 ) {
        advanced = new Group(arg0, SWT.SHADOW_NONE);
        advanced.setLayout(new GridLayout(1, false));

        wkb = new Button(advanced, SWT.CHECK);
        wkb.setLayoutData(new GridData(SWT.LEFT, SWT.DEFAULT, false, false));
        wkb.setSelection(false);
        wkb.addSelectionListener(this);
        wkb.setText(Messages.PostGisWizardPage_button_wkb_text);
        wkb.setToolTipText(Messages.PostGisWizardPage_button_wkb_tooltip);
        wkb.setSelection(true);

        looseBBox = new Button(advanced, SWT.CHECK);
        looseBBox.setLayoutData(new GridData(SWT.LEFT, SWT.DEFAULT, false, false));
        looseBBox.setSelection(false);
        looseBBox.addSelectionListener(this);
        looseBBox.setText(Messages.PostGisWizardPage_button_looseBBox_text);
        looseBBox.setToolTipText(Messages.PostGisWizardPage_button_looseBBox_tooltip);
        looseBBox.setSelection(true);

        port.setText("5432"); //$NON-NLS-1$

        return advanced;
    }

    @Override
    protected DataStoreFactorySpi getDataStoreFactorySpi() {
        return factory;
    }



	public String getId() {
		return "net.refractions.udig.catalog.ui.postgis"; //$NON-NLS-1$
	}

	/** Can be called during createControl */
    protected Map<String,Serializable> defaultParams(){
        IStructuredSelection selection = (IStructuredSelection)PlatformUI
            .getWorkbench() .getActiveWorkbenchWindow().getSelectionService()
            .getSelection();
        return toParams( selection );
    }
    /** Retrieve "best" PostGIS guess of parameters based on provided context */
    protected Map<String,Serializable> toParams( IStructuredSelection context){
        if( context==null )
            return Collections.emptyMap();
        for( Iterator<?> itr = context.iterator(); itr.hasNext(); ) {
            Map<String,Serializable> params = pgcFactory.createConnectionParameters( itr.next() );
            if( params!=null && !params.isEmpty() ) return params;
        }
        return Collections.emptyMap();
    }

    @Override
    protected boolean excludeDB(String db) {
    	return "template0".equals(db) || "template1".equals(db); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    protected boolean excludeSchema(String schema) {
    	return "information_schema".equals(schema) || //$NON-NLS-1$
    		"pg_catalog".equals(schema); //$NON-NLS-1$
    }

    @Override
    public boolean leavingPage() {
        boolean complete = isPageCompleteInternal() && getConnectionSafe()!=null;
        // this is a little bit crazy still, as it is difficult to determine if we should save the
        // widget values without repeatedly reconnecting
        if (complete && connectionDB != null
                && (connectionDB.equals(getDBText())
                || (excludeDB(connectionDB)))) // saves the values if we have a connection to the
                                                // template but have changed the database
            saveWidgetValues();

        return complete;
    }

    @Override
    protected Connection getConnection() {
        final String portText = port.getText();
        final String hostText = ((CCombo) host).getText();
        final String userText = user.getText();
        final String passText = pass.getText();
        final String olddb = ((CCombo) database).getText();

        if (dirty) {
            dirty = false;

            try {
                getContainer().run(true, true,
            		new IRunnableWithProgress(){

                            public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
                                monitor.beginTask(Messages.PostGisWizardPage_0, 10);
                                if (realConnection != null)
                                    try {
                                        realConnection.close();
                                    } catch (SQLException e1) {
                                        // it's dead anyhow
                                    }

                                monitor.worked(1);

                                // if olddb isn't usefull then use "template1"
                                String db = olddb == null || "".equals(olddb) ? "template1" : olddb; //$NON-NLS-1$ //$NON-NLS-2$
                                PostgisConnectionFactory conFac = new PostgisConnectionFactory(hostText,
                                        portText, db);
                                conFac.setLogin(userText, passText);
                                DriverManager.setLoginTimeout(3);
                                monitor.worked(1);
                                try {
                                    if( monitor.isCanceled() )
                                        return;
                                    realConnection = conFac.getConnection();
                                    monitor.worked(5);
                                    if (realConnection != null && !monitor.isCanceled()) {
//                                        database.getDisplay().asyncExec(new Runnable(){
//                                            public void run() {
//                                                database.notifyListeners(SWT.FocusIn, new Event());
//                                            }
//                                        });
                                        connectionDB = db;
                                    }
                                } catch (SQLException e) {
                                    if( db.equals("template1") && e.getMessage().contains("pg_hba.conf")) {
                                        // its ok it means the lookup failed.  Don't show error message
                                    } else {
                                        PostgisPlugin.log("getting connection", e);
                                        throw (InvocationTargetException) new InvocationTargetException(e,e.getLocalizedMessage());
                                    }
                                }
                                if( monitor.isCanceled() )
                                    realConnection=null;
                                dbInitialized = false;
                                monitor.done();
                            }
                });
            }
            catch (InvocationTargetException e2) {
            	e2.printStackTrace();
                throw new RuntimeException(e2.getLocalizedMessage(), e2);
            }
            catch (InterruptedException e2) {

            }
        }

        return realConnection;
    }

    private boolean dirty=true;
    private boolean dbInitialized=false;

    @Override
    protected void populateDB()
    {
        CCombo dbCombo = (CCombo)database;
        boolean userInputInDB = (dbCombo).getText().length()>0;
        if(dbInitialized || userInputInDB) return;
    	super.populateDB();
    	// if there is something in the database list now
    	if( (dbCombo).getItemCount() > 0)
    		dbInitialized = true;
    }

    @Override
    public void modifyText( ModifyEvent e ) {
        if (e.widget == null) {
            return;
        }
        dirty = true;

        if (e.widget.equals(host)) {
            hostModified(e);
        }
        if (e.widget.equals(database)) {
            databaseModified(e);
        }

        if (isFireEvents()) {
            if (e.widget.equals(host) || e.widget.equals(port) || e.widget.equals(user)
                    || e.widget.equals(pass)) {
                setErrorMessage(null);
            }
            if (e.widget.equals(database)) {
                getWizard().getContainer().updateButtons();
            }
        }
    }

    private void hostModified( ModifyEvent e ) {

        if (isFireEvents() && e.widget != null) {
            boolean match = false;
            CCombo combo = (CCombo) host;
            String text = combo.getText();
            for( DataBaseConnInfo db : getSavedConnInfo() ) {
                if (db.getHost() != null && db.getHost().toLowerCase().startsWith(text.toLowerCase())) {
                    match = true;
                    setConnectionInfo(db, false); //fireEvents --> false
                    combo.setSelection(new Point(text.length(), combo.getText().length()));
                    break;
                }
            }
            if (!match) {
                setConnectionInfo(NULL, false); //fireEvents --> false
            }
        }
    }

    private void databaseModified(ModifyEvent e) {
        if (isFireEvents() && e.widget != null) {
            CCombo combo = (CCombo) database;
            String text = combo.getText();
            if (text != null && text.length() > 0) {
                String[] items = combo.getItems();
                for (String item : items) {
                    if (item != null && item.toLowerCase().startsWith(text.toLowerCase())) {
                        //match
                        setFireEvents(false);
                        combo.select(combo.indexOf(item));
                        combo.setSelection(new Point(text.length(), combo.getText().length()));
                        setFireEvents(true);
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected boolean isDBCombo()   { return true; }

    @Override
    protected boolean isHostCombo() { return true; }

    @Override
    protected boolean hasSchema()   { return true; }

    @Override
    public boolean isPageComplete() {
        return true;
    }

    private boolean isPageCompleteInternal() {
        return getHostText().trim().length()!=0 && getDBText().trim().length()!=0 &&
            factory.canProcess(getParams());
    }

    private static PostgisDataStoreFactory factory = new PostgisDataStoreFactory();

    /**
     * Returns the parameters
     */
    @Override
    public Map<String, Serializable> getParams() {
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(PostgisDataStoreFactory.DBTYPE.key, "postgis"); //$NON-NLS-1$
        params.put(PostgisDataStoreFactory.HOST.key, getHostText());
        String dbport = getPortText();
        try {
            params.put(PostgisDataStoreFactory.PORT.key, new Integer(dbport));
        } catch (NumberFormatException e) {
            params.put(PostgisDataStoreFactory.PORT.key, new Integer(5432));
        }

        params.put(PostgisDataStoreFactory.SCHEMA.key, getSchemaText());

        String db = getDBText();
        params.put(PostgisDataStoreFactory.DATABASE.key, db);

        String userName = getUserText();
        params.put(PostgisDataStoreFactory.USER.key, userName);
        String password = getPassText();
        params.put(PostgisDataStoreFactory.PASSWD.key, password);

        if (wkb.getSelection())
            params.put(PostgisDataStoreFactory.WKBENABLED.key, Boolean.TRUE);
        if (looseBBox.getSelection())
            params.put(PostgisDataStoreFactory.LOOSEBBOX.key, Boolean.TRUE);

        params.put(PostgisDataStoreFactory.NAMESPACE.key, ""); //$NON-NLS-1$

        return params;
    }

    /**
     * Saves the widget values
     */
    private void saveWidgetValues() {
        // Update history
        if (settings != null) {
            List<DataBaseConnInfo> info = getSavedConnInfo();
            DataBaseConnInfo dbs = new DataBaseConnInfo(((CCombo)host).getText(), port.getText(),
                    user.getText(), pass.getText(), ((CCombo)database).getText(), schema.getText());
            info.remove(dbs);
            info.add(0,dbs);
            List<String> recentPOSTGISs = new ArrayList<String>(info.size());
            for( DataBaseConnInfo info2 : info ) {
                recentPOSTGISs.add(info2.toString());
            }
            int size=Math.min(recentPOSTGISs.size(), COMBO_HISTORY_LENGTH);
            settings.put(POSTGIS_RECENT, recentPOSTGISs.subList(0, size).toArray(new String[size]));
        }
    }

    /**
     * Populates the database drop-down list. Implementation is identical to
     * base class implementation in all regards except one: we can't use getCatalogs()
     * because the JDBC driver for PostgreSQL only returns one catalog (see
     * http://archives.postgresql.org/pgsql-jdbc/2005-11/msg00224.php for discussion on this change).
     */
    protected ResultSet getDatabaseResultSet(Connection c) throws SQLException
    {
    Statement statement = c.createStatement();
    return statement.executeQuery(
    		"SELECT datname from pg_database ORDER BY datname"); //$NON-NLS-1$
    // Ideally we should be closing the statement but we cannot
    // and the connection should be closed soon anyways.
    }

    @Override
    protected void populateSchema() {
        try {
            getConnection();
        }catch (Exception e) {
            // I don't want to put a stupid error message on the dialog when trying to load the schema
            return;
        }
        if (connectionDB != null && !connectionDB.equals(getDBText())) {
            dirty = true;
        }
        super.populateSchema();
    }

    @Override
    public void dispose() {
        if (realConnection != null) {
            try {
                if (!realConnection.isClosed()) {
                    realConnection.close();
                }
            } catch (SQLException e) {
                //couldn't close connection, no matter -- we are exiting
            }
        }
        super.dispose();
    }
}
