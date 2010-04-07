/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004-2007, Refractions Research, Inc.
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
package net.refractions.udig.catalog.ui.preferences;

import net.refractions.udig.catalog.ui.wizard.DataBaseRegistryWizardPage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.PageBook;

/**
 * <p>
 * An abstract skeleton class used by plugins extending the catalog system to 
 * be able to use proprietary Database Management Systems.
 * </p>
 * 
 * <p>
 * This class extends the usual eclipse RCP 'Import...' mechanism to handle 
 * situations where the JDBC driver cannot be distributed with uDig but must 
 * be obtained directly by the user. This class extends the import strategy 
 * of the DataBaseRegistryWizardPage class by adding an extra step in which the 
 * user will pick the jar files with the JDBC driver which should be used.
 * </p>
 * 
 * @author   Jesse Eichar,       jeichar,        for Refractions Research, Inc.
 * @author   Adrian Custer,      acuster.
 * 
 * @since 1.1.0
 */
public abstract class AbstractProprietaryDatastoreWizardPage 
                                            extends DataBaseRegistryWizardPage {
    
    /**
     * The page used to locate and install any JDBC jars needed for the 
     * particular Database Management System (DBMS). These jars may only be 
     * available directly from the vendor of the proprietary system and so 
     * must be installed by the user.
     */
    private AbstractProprietaryJarPreferencePage preferences;
    
    /**
     * Constructor simply pass the call to its parent class.
     * 
     * @param wizardPageTitle The string which will be displayed near the top of
     *                        the Import... dialog.
     */
    public AbstractProprietaryDatastoreWizardPage(String wizardPageTitle) {
        super(wizardPageTitle);
    }
    
    /**
     * The method called by the eclipse RCP mechanism to provide the widgets 
     * through which we will configure the parameters for the DataStore 
     * backed by a database.
     * 
     * For this method, the AbstractProprietaryDatastoreWizardPage first adds 
     * an extra Page through which to get the files with the JDBC driver and 
     * only then calls the parent createControl method to get the usual GUI for 
     * connection parameters. To allow concrete implementations to add their 
     * own extra GUI, the method ends by calling the doCreateWizardPage method 
     * which extending classes can override.
     * 
     * @param parent The composite into which the current GUI elements will be
     *               added.
     */
    public final void createControl(final Composite parent) {
    	PageBook book = new PageBook(parent, SWT.NONE);
    	
        preferences = getPreferencePage();
        preferences.createControl(book);
        super.createControl(book);
        doCreateWizardPage(book);
        setControl(book);
//        
//        // When will this be triggered?
//        preferences.setListener(new Listener() {
//
//            public void handleEvent(Event event) {
//                getControl().dispose();
//                advancedGrp = null;
//                advancedBtnWgt = null;
//                hostTextWgt = null;
//                portTextWgt = null;
//                userTextWgt = null;
//                passTextWgt = null;
//                //?Db?
//                schemaComboWgt = null;
//                createControl(parent);
//                parent.layout();
//                setMessage(getRestartMessage()); 
//            }
//
//        });
        
        if (!preferences.installed()) {
            setMessage(getDriversMessage()); 
            book.showPage(book.getChildren()[0]);
        } else {
            book.showPage(book.getChildren()[1]);
        }
    }
    /**
     * Called by the Wizard's updateButtons method to see if the "Finish" 
     * button can be activated.
     * 
     * @return true if the page is complete.
     */
    @Override
    public final boolean isPageComplete() {
        if (!preferences.installed())
            return false;

        return doIsPageComplete();
    }
    /**
     * Internal method which can be overridden by concrete extending classes.
     *
     * @return true if the page is complete and the user can import the 
     *         datastore by clicking on the "Finish" button.
     */
    protected abstract boolean doIsPageComplete();

    /**
     * Called by createControl() to create wizard page. The internal logic 
     * must NOT call {@link #createControl(Composite)}.
     * 
     * @param parent the Composite into which this page's GUI will be added.
     */
    protected abstract void doCreateWizardPage(Composite parent);
    
    /**
     * Creates the GUI for the extra wizard Page in which the user will be 
     * asked for the names of the JDBC driver JAR files.
     *
     * @return The GUI page in which the user will specifiy the location of the 
     *         JDBC drivers needed to communicate with the specific proprietary
     *         database management system server.
     */
    protected abstract AbstractProprietaryJarPreferencePage getPreferencePage();

    /**
     * Example: "An error will occur because the Oracle drivers were not loaded.  
     * Please restart application"
     *
     * @return restart message
     */
    protected abstract String getRestartMessage();
    
    /**
     * Example: "Install Oracle drivers for client"
     *
     * @return message indicating that drivers need to be loaded.
     */
    protected abstract String getDriversMessage();
}