/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.internal.mysql.ui;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.internal.mysql.MySQLPlugin;
import net.refractions.udig.catalog.mysql.internal.Messages;
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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.mysql.MySQLConnectionFactory;
import org.geotools.data.mysql.MySQLDataStoreFactory;


/**
 * Wizard page to enter MySQL connection parameters.
 * Based heavily on the postgis version of this class.
 *
 * @author dzwiers
 * @author Harry Bullen, Intelligent Automation
 * @since 1.1.0
 */
public class MySQLWizardPage extends DataBaseRegistryWizardPage implements UDIGConnectionPage {

	private static final String MYSQL_WIZARD = "MYSQL_WIZARD"; //$NON-NLS-1$
    private static final String MYSQL_RECENT = "MYSQL_RECENT"; //$NON-NLS-1$
    private IDialogSettings settings;
    private static final int COMBO_HISTORY_LENGTH = 15;
    private static final DataBaseConnInfo NULL = new DataBaseConnInfo("localhost","3306","","","","");    //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

    public final String IMAGE_KEY = "MySQLWizardPageImage"; //$NON-NLS-1$
    MySQLuDigConnectionFactory mycFactory = new MySQLuDigConnectionFactory();

    /** <code>wkb</code> field */
    protected Button wkb = null;
    /** <code>looseBBox</code> field */
    protected Button looseBBox = null;

    private String connectionDB = null;

    public MySQLWizardPage() {
        super(Messages.MySQLWizardPage_title);
        settings = MySQLPlugin.getDefault().getDialogSettings().getSection(MYSQL_WIZARD);
        if (settings == null) {
            settings = MySQLPlugin.getDefault().getDialogSettings().addNewSection(MYSQL_WIZARD);
        }
        try {
            Class.forName("com.mysql.jdbc.Driver"); //$NON-NLS-1$
        } catch (ClassNotFoundException e) {
            // TODO Handle ClassNotFoundException
        	// should be big error
            throw (RuntimeException) new RuntimeException( ).initCause( e );
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
            ((CCombo)host).setText("localhost"); //$NON-NLS-1$
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
        String selectedHost = (String)params.get("host");

        if( selectedHost != null ){
            ((CCombo)host).setText(params.get("host").toString());
            port.setText(params.get("port").toString());
            ((CCombo)database).setText(params.get("database").toString());

            user.setText(params.get("user").toString());
            pass.setText(params.get("passwd").toString());
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

        if( updateButtons )
            getContainer().updateButtons();
        setFireEvents(true);
    }

    private List<DataBaseConnInfo> getSavedConnInfo() {
        String[] recentPostGiss = settings.getArray(MYSQL_RECENT);
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

        /*
        wkb = new Button(advanced, SWT.CHECK);
        wkb.setLayoutData(new GridData(SWT.LEFT, SWT.DEFAULT, false, false));
        wkb.setSelection(false);
        wkb.addSelectionListener(this);
        wkb.setText("Use WKB");
        wkb.setToolTipText("Select when you wish to use Well Known Binary");
        wkb.setSelection(true);

        looseBBox = new Button(advanced, SWT.CHECK);
        looseBBox.setLayoutData(new GridData(SWT.LEFT, SWT.DEFAULT, false, false));
        looseBBox.setSelection(false);
        looseBBox.addSelectionListener(this);
        looseBBox.setText("Use Loose BBox");
        looseBBox.setToolTipText("Select when you wish to use Loose Bounding Boxes. This will improve PostGIS Performance, but may decrease accuracy.");
        looseBBox.setSelection(true);
         */

        port.setText("3306"); //$NON-NLS-1$

        return advanced;
    }

    @Override
    protected DataStoreFactorySpi getDataStoreFactorySpi() {
        return factory;
    }

	public String getId() {
		return "net.refractions.udig.catalog.ui.mysql"; //$NON-NLS-1$
	}

	/** Can be called during createControl */
    protected Map<String,Serializable> defaultParams(){
        IStructuredSelection selection = (IStructuredSelection)PlatformUI
            .getWorkbench() .getActiveWorkbenchWindow().getSelectionService()
            .getSelection();
        return toParams( selection );
    }
    /** Retrieve "best" MySQL guess of parameters based on provided context */
    protected Map<String,Serializable> toParams( IStructuredSelection context){
        if( context==null )
            return Collections.emptyMap();
        for( Iterator<?> itr = context.iterator(); itr.hasNext(); ) {
            Map<String,Serializable> params = mycFactory.createConnectionParameters( itr.next() );
            if( params!=null && !params.isEmpty() ) return params;
        }
        return Collections.emptyMap();
    }

    @Override
    protected boolean excludeDB(String db) {
    	return "mysql".equals(db) || "information_schema".equals(db); //$NON-NLS-1$ //$NON-NLS-2$
    }


    Connection realConnection;

    @Override
    protected Connection getConnection() {
        final int portNum = Integer.parseInt( port.getText() );
        final String hostText = ((CCombo) host).getText();
        final String userText = user.getText();
        final String passText = pass.getText();
        final String olddb = ((CCombo) database).getText();


        if (dirty) {

            dirty = false;

           // with a nice monitor
            try {
                getContainer().run(true, true,
            		new IRunnableWithProgress(){

                            public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
                                monitor.beginTask(Messages.MySQLWizardPage_0, IProgressMonitor.UNKNOWN);
                                if (realConnection != null)
                                    try {
                                        realConnection.close();
                                    } catch (SQLException e1) {
                                        // it's dead anyhow
                                    }

                                    	//FIXME this is not nice
                                    // it should handle the lack of a database in a much better way
                                String db = olddb == null || "".equals(olddb) ? "test" : olddb; //$NON-NLS-1$ //$NON-NLS-2$

                                MySQLConnectionFactory conFac = new MySQLConnectionFactory(hostText,
                                        portNum , db);


                                DriverManager.setLoginTimeout(3);
                                try {
                                    if( monitor.isCanceled() )
                                        return;
                                    realConnection = conFac.getConnection(userText, passText);
                                    if (realConnection != null && !monitor.isCanceled()) {
                                        database.getDisplay().asyncExec(new Runnable(){
                                            public void run() {
                                                database.notifyListeners(SWT.FocusIn, new Event());
                                            }
                                        });
                                        connectionDB = db;
                                    }
                                } catch (SQLException e) {
                                    throw (InvocationTargetException) new InvocationTargetException(e,e.getLocalizedMessage());
                                }
                                if( monitor.isCanceled() )
                                    realConnection=null;
                                dbInitialized=true;
                                monitor.done();
                            }
                });
            }
            catch (InvocationTargetException e2) {
                //throw new RuntimeException(e2.getLocalizedMessage(), e2);
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
    protected void widgetSelectedInternal(SelectionEvent e) {
    	if (isFireEvents() && e.widget != null && e.widget.equals(database)) {
    		dirty = true;
    		populateSchema();
    	}
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
                dirty=true;
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
    protected boolean isDBCombo() {
        return true;
    }

    @Override
    protected boolean isHostCombo() {
        return true;
    }

    @Override
    protected boolean hasSchema() {
        return false;
    }


    @Override
    public boolean isPageComplete() {
        boolean complete = super.isPageComplete() && isPageCompleteInternal() && getConnectionSafe()!=null;
        // this is a little bit crazy still, as it is difficult to determine if we should save the
        // widget values without repeatedly reconnecting
        if (complete && connectionDB != null
                && (connectionDB.equals(getDBText())
                || (excludeDB(connectionDB)))) // saves the values if we have a connection to the
                                                // template but have changed the database
            saveWidgetValues();
        return complete;
    }

    //FIXME currently wont allow stuff to go forward fault goes to factory
    // Actually we have a problem going to the next page.
    private boolean isPageCompleteInternal() {
        return getHostText().trim().length()!=0 && getDBText().trim().length()!=0 &&
            factory.canProcess(getParams());
    }

    private static MySQLDataStoreFactory factory = new MySQLDataStoreFactory();

    /**
     * Returns the parameters
     */
    @Override
    public Map<String, Serializable> getParams() {
        Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("dbtype", "mysql");
        params.put("host", getHostText());
        String dbport = getPortText();
        try {
            params.put("port", dbport);
        } catch (NumberFormatException e) {
            params.put("port", "3306");
        }

        //params.put("schema", "");

        String db = getDBText();
        params.put("database", db);

        String userName = getUserText();
        params.put("user", userName);
        String password = getPassText();
        params.put("passwd", password);

        /* this is somw wkb stuff that isn't in mysql connectionat this point
        if (wkb.getSelection())
            params.put("wkbenabled", Boolean.TRUE);
        if (looseBBox.getSelection())
            params.put("loosebbox", Boolean.TRUE);
         */

        params.put("namespace", ""); //$NON-NLS-1$

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
                    user.getText(), pass.getText(), ((CCombo)database).getText(), "");
            info.remove(dbs);
            info.add(0,dbs);
            List<String> recentMySQLs = new ArrayList<String>(info.size());
            for( DataBaseConnInfo info2 : info ) {
                recentMySQLs.add(info2.toString());
            }
            int size=Math.min(recentMySQLs.size(), COMBO_HISTORY_LENGTH);
            settings.put(MYSQL_RECENT, recentMySQLs.subList(0, size).toArray(new String[size]));
        }
    }

    /*
    @Override
    protected void populateSchema() {
        if (connectionDB != null && !connectionDB.equals(getDBText())) {
            dirty = true;
        }
        super.populateSchema();
    }*/

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
