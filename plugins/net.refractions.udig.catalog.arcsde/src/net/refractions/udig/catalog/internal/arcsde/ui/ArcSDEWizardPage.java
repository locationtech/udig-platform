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
package net.refractions.udig.catalog.internal.arcsde.ui;

import java.io.Serializable;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.arcsde.internal.Messages;
import net.refractions.udig.catalog.internal.arcsde.ArcServiceExtension;
import net.refractions.udig.catalog.internal.arcsde.ArcsdePlugin;
import net.refractions.udig.catalog.ui.UDIGConnectionPage;
import net.refractions.udig.catalog.ui.preferences.AbstractProprietaryDatastoreWizardPage;
import net.refractions.udig.catalog.ui.preferences.AbstractProprietaryJarPreferencePage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFactorySpi.Param;
import org.geotools.data.arcsde.ArcSDEDataStoreFactory;

/**
 * Provides ...TODO summary sentence
 * <p>
 * TODO Description
 * </p>
 * @author dzwiers
 * @since 0.6
 */
public class ArcSDEWizardPage extends AbstractProprietaryDatastoreWizardPage implements UDIGConnectionPage{
    private static final String ARCSDE_WIZARD = "ARCSDE_WIZARD"; //$NON-NLS-1$
    private static final String ARCSDE_RECENT = "ARCSDE_RECENT"; //$NON-NLS-1$
    private IDialogSettings settings;
    private static final int COMBO_HISTORY_LENGTH = 15;
    ArrayList<DataBaseConnInfo> dbData;

    public ArcSDEWizardPage() {
        super(Messages.ArcSDEWizardPage_title);
        settings = ArcsdePlugin.getDefault().getDialogSettings().getSection(ARCSDE_WIZARD);
        if (settings == null) {
            settings = ArcsdePlugin.getDefault().getDialogSettings().addNewSection(ARCSDE_WIZARD);
        }
    }

    /**
     * TODO summary sentence for getDataStoreFactorySpi ...
     *
     * @return x
     */
    protected DataStoreFactorySpi getDataStoreFactorySpi() {
        return factory;
    }

    private static ArcSDEDataStoreFactory factory = new ArcSDEDataStoreFactory();

	public String getId() {
		return "net.refractions.udig.catalog.ui.arcsde"; //$NON-NLS-1$
	}

    /**
     * TODO summary sentence for getParams ...
     *
     * @return x
     */
    public Map<String,Serializable> getParams() {
        Map<String,Serializable> params = new HashMap<String,Serializable>();
        Param[] dbParams = factory.getParametersInfo();
        params.put(dbParams[1].key,"arcsde"); //$NON-NLS-1$
        params.put(dbParams[2].key, getHostText());
        String port1 = getPortText();
        try{
            params.put(dbParams[3].key, Integer.valueOf(port1));
        }catch(NumberFormatException e){
            params.put(dbParams[3].key, Integer.valueOf(5432));
        }

        String db = getDBText();
        params.put(dbParams[4].key,db);

        String user1 = getUserText();
        params.put(dbParams[5].key,user1);
        String pass1 = getPassText();
        params.put(dbParams[6].key,pass1);

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
     *
     * @return
     */
    protected String getPassText() {
        return this.pass.getText();
    }

    /**
     *
     * @return
     */
    protected String getUserText() {
        return this.user.getText();
    }

    /**
     *
     * @return
     */
    protected String getDBText() {
        return ((Text)database).getText();
    }

    /**
     *
     * @return
     */
    protected String getHostText() {
        return ((CCombo)host).getText();
    }

    public List<URL> getURLs() {
    	return null;
    }
    /**
     * TODO summary sentence for isDBCombo ...
     *
     * @return x
     */
    protected boolean isDBCombo() {
        // instance?
        return false;
    }

    protected boolean isHostCombo() {
        return true;
    }

    /**
     * TODO summary sentence for hasSchema ...
     *
     * @return x
     */
    protected boolean hasSchema() {
        return false;
    }

    /**
     * TODO summary sentence for createAdvancedControl ...
     *
     * @return x
     */
    protected Group createAdvancedControl( Composite arg0 ) {
        return null;
    }

    /**
     * TODO summary sentence for getConnection ...
     *
     * @return null
     */
    protected Connection getConnection() {
        return null;
    }
    /**
     * TODO summary sentence for populateDB ...
     *
     *
     */
    protected void populateDB() {
        // do nothing
    }
    /**
     * TODO summary sentence for populateSchema ...
     *
     *
     */
    protected void populateSchema() {
        // do nothing
    }

    @Override
    protected boolean doIsPageComplete() {
		Map<String,Serializable> p = getParams();
    	if(p==null)
    		return false;
    	boolean r = factory.canProcess(p);
    	return r;
	}

    /*
     * @see net.refractions.udig.catalog.ui.UDIGImportPage#getResources(org.eclipse.core.runtime.IProgressMonitor)
     */
    public List<IService> getResources( IProgressMonitor monitor ) throws Exception {
        if( !isPageComplete() )
			return null;

        ArcServiceExtension creator = new ArcServiceExtension();

        IService service = creator.createService( null, getParams() );
        service.getInfo( monitor ); // load

        List<IService> servers= new ArrayList<IService>();
        servers.add( service );

        /*
         * Success! Store the URL in history.
         */
        saveWidgetValues();

        return servers;
	}

    /**
     * Saves the widget values
     */
    private void saveWidgetValues() {
        // Update history
        if (settings != null) {
            String[] recentARCSDEs = settings.getArray(ARCSDE_RECENT);
            if (recentARCSDEs == null) {
                recentARCSDEs = new String[0];
            }
            String dbs = new DataBaseConnInfo(getHostText(), port.getText(),
                    user.getText(), pass.getText(), getDBText(), schema.getText()).toString();
            recentARCSDEs = addToHistory(recentARCSDEs, dbs);
            settings.put(ARCSDE_RECENT, recentARCSDEs);
        }
    }

    /**
     * Adds an entry to a history, while taking care of duplicate history items
     * and excessively long histories.  The assumption is made that all histories
     * should be of length <code>COMBO_HISTORY_LENGTH</code>.
     *
     * @param history the current history
     * @param newEntry the entry to add to the history
     * @return the history with the new entry appended
     * Stolen from org.eclipse.team.internal.ccvs.ui.wizards.ConfigurationWizardMainPage
     */
    private String[] addToHistory(String[] history, String newEntry) {
        ArrayList<String> l = new ArrayList<String>(Arrays.asList(history));
        addToHistory(l, newEntry);
        String[] r = new String[l.size()];
        l.toArray(r);
        return r;
    }

    /**
     * Adds an entry to a history, while taking care of duplicate history items
     * and excessively long histories.  The assumption is made that all histories
     * should be of length <code>COMBO_HISTORY_LENGTH</code>.
     *
     * @param history the current history
     * @param newEntry the entry to add to the history
     * Stolen from org.eclipse.team.internal.ccvs.ui.wizards.ConfigurationWizardMainPage
     */
    private void addToHistory(List<String> history, String newEntry) {
        history.remove(newEntry);
        history.add(0,newEntry);

        // since only one new item was added, we can be over the limit
        // by at most one item
        if (history.size() > COMBO_HISTORY_LENGTH)
            history.remove(COMBO_HISTORY_LENGTH);
    }

    @Override
    protected void doCreateWizardPage( Composite parent ) {
        String[] recentARCSDEs = settings.getArray(ARCSDE_RECENT);
        ArrayList<String> hosts = new ArrayList<String>();
        dbData = new ArrayList<DataBaseConnInfo>();
        if (recentARCSDEs != null) {
            for( String recent : recentARCSDEs ) {
                DataBaseConnInfo dbs = new DataBaseConnInfo(recent);
                dbData.add(dbs);
                hosts.add(dbs.getHost());
            }
        }
        if( hosts.size() > 0 ) {
            ((CCombo)host).setItems(hosts.toArray(new String[0]));
            ((CCombo)host).addModifyListener(new ModifyListener(){
                public void modifyText( ModifyEvent e ) {
                    if(e.widget!=null) {
                        for( DataBaseConnInfo db : dbData ) {
                            if(db.getHost().equalsIgnoreCase(getHostText())) {
                                port.setText(db.getPort());
                                user.setText(db.getUser());
                                pass.setText(db.getPass());
                                ((Text)database).setText(db.getDb());
                                schema.setText(db.getSchema());
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
