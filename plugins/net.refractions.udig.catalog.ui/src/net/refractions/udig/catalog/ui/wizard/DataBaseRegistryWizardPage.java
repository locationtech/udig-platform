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
package net.refractions.udig.catalog.ui.wizard;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.internal.Messages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Text;

/**
 * David's magic superclass, seems to delegate to createAdvancedControl for anything cool.
 *
 * @author dzwiers
 * @since 0.3
 */
public abstract class DataBaseRegistryWizardPage extends DataStoreWizardPage
	implements ModifyListener, SelectionListener, FocusListener{


    protected Scrollable host = null;
    protected Text port = null;
    protected Text user = null;
    protected Text pass = null;
    protected Scrollable database = null;
    protected CCombo schema = null;
    protected Button advancedKey = null;
    protected Group advanced = null;
    protected abstract boolean isDBCombo();
    protected abstract boolean isHostCombo();
    protected abstract boolean hasSchema();
    String hostStr = null;
    String portStr = null;
    String userStr = null;
    String passStr = null;
    String databaseStr = null;
    String schemaStr = null;
    private boolean fireEvents=true;


    private boolean focusing;
	public DataBaseRegistryWizardPage(String name){
        super(name);
    }
    public DataBaseRegistryWizardPage(){
        super(""); //$NON-NLS-1$
    }

    /**
    * gets the host name
    */
    protected String getHostText() {
    	if( isHostCombo() )
    		return ((CCombo)host).getText();
    	else
    		return ((Text)host).getText();
    }
    /**
    * gets the port number
    */
   protected String getPortText()
   {
       return port.getText();
   }

   /**
    * gets the password
    */
   protected String getPassText() {
       return pass.getText();
   }

   /**
    * gets the username
    */
   protected String getUserText() {
       return user.getText();
   }

   /**
    * Gets the database parameter
    */
   protected String getDBText() {
	   if( isDBCombo() )
		   return ((CCombo) database).getText();
	   else
		   return ((Text) database).getText();
   }

   /**
    * Get the schema text
    * @return Schema
    */
   protected String getSchemaText() {
   	return hasSchema() && schema != null ? schema.getText() : ""; //$NON-NLS-1$
   }

    /**
     * TODO summary sentence for focusGained ...
     *
     * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
     * @param e
     */
    public void focusGained( FocusEvent e ) {
    	//set this flag to prevent further connections upon
    	// another focus
    	if (focusing || getHostText().trim().length()==0 || getUserText().trim().length()==0
                || getPortText().trim().length()==0 )
    		return;


    	focusing = true;
        if (e.widget != null) {
        	// Refresh the DB and SchemaTables if they exist
            if (isDBCombo() && e.widget.equals(database))
            	populateDB();
        	if (hasSchema() && e.widget.equals(schema))
            	populateSchema();

            if (e.widget.equals(user) && user.getText().length() > 0) {
                ((Text)user).setSelection(new Point(0, user.getText().length()));
            }
            if (e.widget.equals(pass) && pass.getText().length() > 0) {
                ((Text)pass).setSelection(new Point(0, pass.getText().length()));
            }
            if (e.widget.equals(port) && port.getText().length() > 0) {
                ((Text)port).setSelection(new Point(0, port.getText().length()));
            }
        }
        getWizard().getContainer().updateButtons();
        focusing = false;
    }
    /**
     * TODO summary sentence for focusLost ...
     *
     * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
     * @param e
     */
    public void focusLost( FocusEvent e ) {
        // do nothing
        getWizard().getContainer().updateButtons();
    }


    /**
     * TODO summary sentence for createControl ...
     *
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @param parent
     */
    public void createControl( Composite arg0 ) {
        Composite composite = new Group(arg0,SWT.NULL);
        composite.setLayout(new GridLayout(5, true));
//        GridData data = new GridData(SWT.FILL, SWT.FILL, false,false);
//        data.widthHint=arg0.getClientArea().width;
//        composite.setLayoutData(data);

        // add host
        Label label = new Label( composite, SWT.NONE );
        label.setText(Messages.DataBaseRegistryWizardPage_label_host_text );
        label.setToolTipText( Messages.DataBaseRegistryWizardPage_label_host_tooltip );
        label.setLayoutData( new GridData(SWT.END, SWT.DEFAULT, false, false ) );

        if(isHostCombo()){
            CCombo host = new CCombo(composite,SWT.DROP_DOWN | SWT.BORDER);
            this.host = host;
            host.setLayoutData( new GridData(SWT.FILL, SWT.DEFAULT, true, false ,2,1) );
            host.addModifyListener(this);
            host.setToolTipText( Messages.DataBaseRegistryWizardPage_label_host_tooltip );
            host.setEditable(true);
            host.setLayoutDeferred(true);
        }else{
            Text host = new Text(composite,SWT.BORDER | SWT.SINGLE);
            this.host = host;
            host.setLayoutData( new GridData(SWT.FILL, SWT.DEFAULT, true, false ,2,1) );
            host.addModifyListener(this);
            host.setToolTipText( Messages.DataBaseRegistryWizardPage_label_host_tooltip );
        }
        host.addFocusListener(this);
//        host.setFocus();
//        host = new Text( composite, SWT.BORDER | SWT.SINGLE );
//        host.setLayoutData( new GridData(GridData.FILL, SWT.DEFAULT, true, false ,2,1) );
//        host.addModifyListener(this);

        // add port
        label = new Label( composite, SWT.NONE );
        label.setText(Messages.DataBaseRegistryWizardPage_label_port_text );
        label.setToolTipText( Messages.DataBaseRegistryWizardPage_label_port_tooltip );
        label.setLayoutData( new GridData(SWT.END, SWT.DEFAULT, false, false ) );

        port = new Text( composite, SWT.BORDER | SWT.SINGLE );
        port.setLayoutData( new GridData(GridData.FILL, SWT.DEFAULT,true,false) );
        port.addModifyListener(this);
        port.setTextLimit(6);
        port.addFocusListener(this);

        // add user
        label = new Label( composite, SWT.NONE );
        label.setText(Messages.DataBaseRegistryWizardPage_label_username_text );
        label.setToolTipText( Messages.DataBaseRegistryWizardPage_label_username_tooltip );
        label.setLayoutData( new GridData(SWT.END, SWT.DEFAULT, false, false ) );

        user = new Text( composite, SWT.BORDER | SWT.SINGLE );
        user.setLayoutData( new GridData(GridData.FILL, SWT.DEFAULT, true, false,2,1) );
        user.addModifyListener(this);
        user.addFocusListener(this);
        //space
        label = new Label( composite, SWT.NONE );
        label.setLayoutData( new GridData(SWT.END, SWT.DEFAULT, false, false ,2,1) );

        // add pass
        label = new Label( composite, SWT.NONE );
        label.setText(Messages.DataBaseRegistryWizardPage_label_password_text );
        label.setToolTipText( Messages.DataBaseRegistryWizardPage_label_password_tooltip );
        label.setLayoutData( new GridData(SWT.END, SWT.DEFAULT, false, false ) );

        pass = new Text( composite, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
        pass.setLayoutData( new GridData(GridData.FILL, SWT.DEFAULT, true, false,2,1) );
        pass.addModifyListener(this);
        pass.addFocusListener(this);

        //space
        label = new Label( composite, SWT.NONE );
        label.setLayoutData( new GridData(SWT.END, SWT.DEFAULT, false, false ,2,1) );

        // add spacer
        label = new Label( composite, SWT.SEPARATOR | SWT.HORIZONTAL );
        label.setLayoutData( new GridData(SWT.FILL, SWT.CENTER, true, false,5,3 ) );

        // database
        label = new Label( composite, SWT.NONE );
        label.setText(Messages.DataBaseRegistryWizardPage_label_database_text );
        label.setToolTipText( Messages.DataBaseRegistryWizardPage_label_database_tooltip );
        label.setLayoutData( new GridData(SWT.END, SWT.DEFAULT, false, false ) );

        if(isDBCombo()){
            CCombo db = new CCombo(composite,SWT.DROP_DOWN | SWT.BORDER);
        	this.database = db;
        	database.setLayoutData( new GridData(SWT.FILL, SWT.DEFAULT, true, false ,2,1) );
        	db.addModifyListener(this);
        	db.addFocusListener(this);
        	db.addSelectionListener(this);
        	db.setToolTipText( Messages.DataBaseRegistryWizardPage_label_database_tooltip );
        	//db.setEditable(true);
        }else{
            Text db = new Text(composite,SWT.BORDER | SWT.SINGLE);
        	this.database = db;
        	db.setLayoutData( new GridData(SWT.FILL, SWT.DEFAULT, true, false ,2,1) );
        	db.addModifyListener(this);
        	db.addFocusListener(this);
        	db.setToolTipText( Messages.DataBaseRegistryWizardPage_label_database_tooltip );
        }

        label = new Label( composite, SWT.NONE );
        label.setLayoutData( new GridData(SWT.END, SWT.DEFAULT, false, false ,2,1) );

        if(hasSchema()){
            // schema
            label = new Label( composite, SWT.NONE );
            label.setText(Messages.DataBaseRegistryWizardPage_label_schema_text );
            label.setToolTipText( Messages.DataBaseRegistryWizardPage_label_schema_tooltip );
            label.setLayoutData( new GridData(SWT.END, SWT.DEFAULT, false, false ) );

            schema = new CCombo(composite,SWT.DROP_DOWN | SWT.BORDER);
            schema.setLayoutData( new GridData(SWT.FILL, SWT.DEFAULT, true, false ,2,1) );
            schema.addModifyListener(this);
            schema.addFocusListener(this);
            schema.setToolTipText( Messages.DataBaseRegistryWizardPage_label_schema_tooltip );
            schema.setEditable(true);

            label = new Label( composite, SWT.NONE );
            label.setLayoutData( new GridData(SWT.END, SWT.DEFAULT, false, false ,2,1) );
        }

        // add spacer
        label = new Label( composite, SWT.SEPARATOR | SWT.HORIZONTAL );
        label.setLayoutData( new GridData(SWT.FILL, SWT.CENTER, true, false,5,3 ) );

        // advanced
        advancedKey = new Button( composite, SWT.CHECK );
        advancedKey.setLayoutData( new GridData(SWT.CENTER, SWT.DEFAULT, false, false ) );
        advancedKey.setSelection(false);
        advancedKey.addSelectionListener(this);
        advancedKey.setText(Messages.DataBaseRegistryWizardPage_label_advanced_text);
        advancedKey.setToolTipText(Messages.DataBaseRegistryWizardPage_label_advanced_tooltip);

        label = new Label( composite, SWT.NONE ); // advanced check row
        label.setLayoutData( new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false ,4,1) );
        label = new Label( composite, SWT.NONE ); // slot one of child row
        label.setLayoutData( new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false ) );

        advanced = createAdvancedControl(composite);
        if(advanced == null){
            advancedKey.setVisible(false);// turn off - we don't want events
            advancedKey.setEnabled(false);// turn off - we don't want events
        }else{
            GridData data = new GridData(SWT.FILL, SWT.FILL, false, false );
            data.horizontalSpan=5;
            advanced.setLayoutData( data );
        	advanced.setVisible(false);
        }

        /*
         * This will never have any effect as far as I can tell.
         * the data is save in the wizard pages automatically.
        if( host.getData() == null && hostStr != null ) {
            if( isHostCombo() ) {
                ((CCombo)host).setText(hostStr);
                ((CCombo)host).add(hostStr);
            } else {
                ((Text)host).setText(hostStr);
            }
            host.setData(hostStr);
            port.setText(portStr);
            user.setText(userStr);
            pass.setText(passStr);
            if( isDBCombo() ) {
                ((CCombo)database).setText(databaseStr);
            } else {
                ((Text)database).setText(databaseStr);
            }
            if( hasSchema())
                schema.setText(schemaStr);
        }
        */
        List<Control> tablist=new LinkedList<Control>();
        tablist.add(host);
        tablist.add(user);
        tablist.add(pass);
        tablist.add(database);
        if( schema!=null )
            tablist.add(schema);
        tablist.add(port);
        tablist.add(advancedKey);
        if( advanced!=null )
            tablist.add(advanced);

        composite.setTabList(tablist.toArray(new Control[tablist.size()]));

        setControl(composite);
        setPageComplete(true);
    }
    protected abstract Group createAdvancedControl( Composite arg0 );

    @Override
    public void setVisible( boolean visible ) {
        super.setVisible(visible);
        if( host!=null ) {
        	host.setFocus();
        }
    }

    /*
     * This is really bad as this function's only returns false
     * and then has side effects that aren't obvious.  It appears that
     * it is setting up the Strings for being used latter.
     * (non-Javadoc)
     * @see net.refractions.udig.catalog.ui.wizard.DataStoreWizardPage#isPageComplete()
     */
    @Override
    public boolean isPageComplete() {
    	storePageData();
        return true;
    }

    /*
     * Moves all the data from the text boxes to
     * strings.
     */
    public void storePageData()
    {
    	if( isHostCombo() ) {
            hostStr = ((CCombo)host).getText();
        } else {
            hostStr = ((Text)host).getText();
        }
        portStr = port.getText();
        userStr = user.getText();
        passStr = pass.getText();
        if( isDBCombo() ) {
            databaseStr = ((CCombo)database).getText();
        } else {
            databaseStr = ((Text)database).getText();
        }
        if( hasSchema() )
            schemaStr = schema.getText();
    }


    /*
     * Silly setter and getter functions
     */
    protected void setFireEvents(boolean fire){
        fireEvents=fire;
    }
    protected boolean isFireEvents(){
        return fireEvents;
    }

    /*
     * Handles special modification to UI
     * (non-Javadoc)
     * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
     */
    public void modifyText( ModifyEvent e ) {
        if (fireEvents) {
            if (e.widget != null) {
                if (e.widget instanceof Text)
                { //not a combo
                    ((Text)e.widget).setForeground(null);
                    //if the user/pass field is modified, clear the databases
                    if (isDBCombo())
                        ((CCombo)database).removeAll();
                }
                if (hasSchema() && e.widget.equals(database) && schema.getItemCount() > 0)
                    schema.removeAll();
            }
            getWizard().getContainer().updateButtons();
        }
    }

    /**
     * TODO summary sentence for widgetSelected ...
     *
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     * @param e
     */
    public void widgetSelected( SelectionEvent e ) {
        if(e.widget!=null && e.widget.equals(advancedKey)){
            advanced.setVisible(advancedKey.getSelection());
        }

        widgetSelectedInternal(e);

        getWizard().getContainer().updateButtons();
    }

    protected void widgetSelectedInternal(SelectionEvent e) {
    	//do nothing
    }

    /*
     * This Queries the Database Server and find out what databases
     * are usable and puts them into the database CCombo
     */
    protected void populateDB(){
    	// dosen't work if DB is not a CCombo
    	if (!isDBCombo() ) return;
    	CCombo db = (CCombo)this.database;

        // will need a connection ...
        Connection con = getConnectionSafe();
        if(con == null)return;

        //save current state
        int selected = db.getSelectionIndex();
        String string = null;
        if (selected > -1) {
        	string = db.getItem(selected);
        }

        db.removeAll();
        db.setText(""); //$NON-NLS-1$

        // actually populate the database table
        try {
            ResultSet rs = getDatabaseResultSet(con);
            while(rs.next()){
            	String dbName = rs.getString(1);
            	if (!excludeDB(dbName)) {
            		((CCombo)database).add(dbName);
            	}

            }
            if (db.getItemCount() > 0)
            	db.select(0);

        } catch (SQLException e) {
            setErrorMessage(e.getLocalizedMessage());
            db.removeAll();
            db.setText(""); //$NON-NLS-1$
            return;
        }

        if (string != null) {
        	String[] items = db.getItems();
        	for (int i = 0; items != null && i < items.length; i++) {
        		if (string.equals(items[i])) {
        			db.select(i);
        			return;
        		}
        	}
        }
    }

    /**
     * This method can be overridden to provide a different method to get a
     * ResultSet of the database names.
     */
    protected ResultSet getDatabaseResultSet(Connection c) throws SQLException
    {
    	return c.getMetaData().getCatalogs();
    }

    /*
     * Setups the schema CCombo box
     */
    protected void populateSchema(){
        if (!hasSchema()) // error
            return;
        // will need a connection ...

        //save some state
        String string = schema.getText();

        schema.removeAll();
        schema.setText(""); //$NON-NLS-1$

        Connection con = getConnectionSafe();
        if(con == null)return;

        try {
        	ResultSet rs = con.getMetaData().getSchemas();
            while(rs.next()){
            	String schemaName = rs.getString(1);
            	if (!excludeSchema(schemaName))
                    schema.add(schemaName);
        	}
            if (schema.getItemCount() > 0)
            	schema.select(0);

        } catch (SQLException e) {
//            setMessage(Messages.DataBaseRegistryWizardPage_schemaMessage);
            schema.removeAll();
            if (string == null) {
                schema.setText(""); //$NON-NLS-1$
            } else { //pretend we didn't break it
                schema.setText(string);
            }
            schema.setFocus();
            return;
        }

        setErrorMessage(null);
        if (string != null) {
        	String[] items = schema.getItems();
        	for (int i = 0; items != null && i < items.length; i++) {
        		if (string.equals(items[i])) {
        			schema.select(i);
        			return;
        		}
        	}
        }
    }

    /*
     * These should be overridden to exclude databases and Schemas
     * that should not go into the CCombo boxes
     */
    protected boolean excludeDB(String db) {
    	return false;
    }

    protected boolean excludeSchema(String schema) {
    	return false;
    }

    // to use for wizard population
    protected abstract Connection getConnection() throws Exception;

    // Same as getConnection but handles Exceptions and returns null
    // if they happen
    protected Connection getConnectionSafe()
    {
        Connection con = null;
        try {
        	con = getConnection();
        }
        catch(Exception e) {
        	CatalogUIPlugin.log(e.getLocalizedMessage(), e);
        	setErrorMessage(e.getLocalizedMessage());
        }

    	if( con != null) // we can reset error messages
    		setErrorMessage(null);

    	// make sure the gui is working
    	enableIfNot(host);
    	enableIfNot(user);
    	enableIfNot(pass);
    	enableIfNot(database);
    	if(hasSchema()) enableIfNot(schema);

        return con;
    }

    static void enableIfNot(Scrollable s)
    {
    	if( !s.isEnabled() )
    		s.setEnabled(true);
    }


    /**
     * TODO summary sentence for widgetDefaultSelected ...
     *
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     * @param e
     */
    public void widgetDefaultSelected( SelectionEvent e ) {
        // do nothing
    }

    public static class DataBaseConnInfo {
        private String host;
        private String port;
        private String user;
        private String pass;
        private String db;
        private String schema;

        public DataBaseConnInfo (String host, String port, String user, String pass, String db, String schema) {
            this.host = host;
            this.port = port;
            this.user = user;
            this.pass = pass;
            this.db = db;
            this.schema=schema;
        }
        /**
         * Is given a tab seperated string of database info
         * @param dbEntry
         */
        public DataBaseConnInfo (String dbEntry) {
            String[] temp = dbEntry.split("\t"); //$NON-NLS-1$

            if( temp.length>0 )
                this.host = temp[0];
            if( temp.length>1 )
                this.port = temp[1];
            if (temp.length > 2)
                this.user = temp[2];
            if (temp.length > 3)
                this.pass = temp[3];
            if (temp.length > 4)
                this.db = temp[4];
            if (temp.length > 5)
                this.schema = temp[5];
        }
        public String getHost() {
            return host==null?"":host; //$NON-NLS-1$
        }
        public String getPort() {
            return port==null?"":port; //$NON-NLS-1$
        }
        public String getUser() {
            return user==null?"":user; //$NON-NLS-1$
        }
        public String getPass() {
            return pass==null?"":pass; //$NON-NLS-1$
        }
        public String getDb() {
            return db==null?"":db; //$NON-NLS-1$
        }
        public String getSchema() {
            return schema==null?"":schema; //$NON-NLS-1$
        }
        @Override
        public String toString() {
            return host + "\t" + port + "\t" + user + "\t" + pass + "\t" + db + "\t" + schema;  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        }
        @Override
        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((db == null) ? 0 : db.hashCode());
            result = PRIME * result + ((host == null) ? 0 : host.hashCode());
            result = PRIME * result + ((pass == null) ? 0 : pass.hashCode());
            result = PRIME * result + ((port == null) ? 0 : port.hashCode());
            result = PRIME * result + ((user == null) ? 0 : user.hashCode());
            return result;
        }
        @Override
        public boolean equals( Object obj ) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            final DataBaseConnInfo other = (DataBaseConnInfo) obj;
            if (db == null) {
                if (other.db != null)
                    return false;
            } else if (!db.equals(other.db))
                return false;
            if (host == null) {
                if (other.host != null)
                    return false;
            } else if (!host.equals(other.host))
                return false;
            if (pass == null) {
                if (other.pass != null)
                    return false;
            } else if (!pass.equals(other.pass))
                return false;
            if (port == null) {
                if (other.port != null)
                    return false;
            } else if (!port.equals(other.port))
                return false;
            if (user == null) {
                if (other.user != null)
                    return false;
            } else if (!user.equals(other.user))
                return false;
            return true;
        }


    }

}
