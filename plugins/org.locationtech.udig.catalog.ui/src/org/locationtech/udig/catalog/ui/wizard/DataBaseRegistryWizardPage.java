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
package org.locationtech.udig.catalog.ui.wizard;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.locationtech.udig.catalog.ui.CatalogUIPlugin;
import org.locationtech.udig.catalog.ui.internal.Messages;
import org.locationtech.udig.ui.PlatformGIS;

// TODO: HTMLify this javadoc
/**
 * David's magic superclass reduced to linear, documented, boring reality so that it may be
 * understandable by mortals; this is an abstract superclass for implementations of the
 * 'IWizardPage' interface required by the 'wizardPage' portion of the
 * <code>org.locationtech.udig.catalog.ui.connectionFactory</code> extension point contract with the
 * uDig catalog system. The connectionFactory extension point needs a DataStore factory to make the
 * connection used by the Catalog and a IWizardPage to obtain from the user the parameters which
 * will be used by the factory. The IWizardPage interface extends the eclipse RCP 'Import...' system
 * by providing a User Interface SWT Group which will be placed in a 'Page' of the import wizard and
 * will obtain the required parameters to connect to some resource, in this case a database (Db)
 * server. Concrete extensions of this class will then use the parameters to connect to the server
 * using approaches specific to each Database Management System (DBMS). WARNING: Modifications to
 * this class must consider the consequences for all extending classes, notably both
 * PostGisWizardPage and AbstractProprietaryDatastoreWizardPage. ************* David's magic spells
 * (now for mortals) *********************** This class performs a number of actions as explained
 * below. These actions have been changed from happening magically in response to users clicking on
 * the widgets to happening after user input or button clicks. Hopefully this logic will be more
 * predicatable, robust, and understandable. Establish default Connection Parameters: * must be
 * added by the extending class during its construction * are used to set the current connection
 * parameters at the same time * TODO: let the user reset to this in the case where the user is lost
 * Get Storage for Connection Parameters: * must be obtained or created by the extending class'
 * constructor. Get and Use Stored Connection Parameters: * are taken from storage and added to the
 * list by the extending class during its construction. * are added to the drop down list during *
 * TODO: limit the parameters added to a few recent Retain Successful Connection Parameters: * are
 * added to the list after each successful connection Store Successful Connection Parameters: * are
 * stored to the storage system after each successful connection Show the Advanced group: * by the
 * widgetSelected(.) handler for the check button widget if this widget is clicked (checked).
 * Activate the lookup and connect buttons: * by the modifyText(.) handler for any of the input
 * widgets, if the current host, port, user, password, and database parameters are all filled out.
 * Lookup the available databases and schema on the server: * by the widgetSelected(.) handler for
 * the "Lookup" button, when it is clicked. Connect to a database server: * by the widgetSelected(.)
 * handler for the "Connect" button, when it is clicked. Activate the finish buttons: * by the
 * widgetSelected(.) handler for the "Connect" button if, after the connection attempt, there is a
 * live connection and the parameter map built from the current connection can be processed by the
 * DataStore factory. Reset the lists of available resources when altering key connection params: *
 * by the modifyText(.) handlers for each of the widgets: host, port, user, and password, when they
 * are modified. Still to be done: (some of these are distant hopes) pre-select text on re-edit: *
 * Note this seems to be the default when using the <tab> key to move through the widgets Hint text
 * completion from the storage list: * Note this requires improvement in the earlier logic.
 * Importantly, we must be able to connect to a resource that is a subset of an earlier resource,
 * i.e. if a previous connection was made to the host "serverAlex," a user must still be able to
 * connect to the host "serverA" by typing 's' 'e' 'r' 'v' 'e' 'r' 'A' '<return>' without our
 * completion logic getting in the way of the user. Validate input: * This is to be done in three
 * parts, character exclusion, URL component validation, and URL validation: - changes with
 * characters in the 'invalidChar*List' lists will not be allowed (and add an error in the RCP
 * Animated Area). - changes with character sequences in the 'invalidString*List' lists will not be
 * allowed (and add an error in the RCP Animated Area). - URLs will be subject to a coarse
 * verification before being used to connect to a database. Password entry warnings: * If CapsLock
 * is set during password entry, an error will be placed in the RCP Animated Area. Connection Status
 * Information: * Will be displayed in the dialog. Verifications of permissions: * the permissions
 * of the user on the server's database will be obtained and reported to the user. Verification of
 * the database: * since we can only use spatial tables, the databases offered to the user should be
 * evaluated to ensure they can hold spatial data. ****************** Class Outline (i.e. the key
 * pieces) ********************
 *
 * <pre>
 *
 *   FIELDS:                      // The fields describe here all use instances
 *                                // of the inner class
 *                                //   DataBaseConnInfo
 *                                // which is essentially a structure of
 *                                //       : host
 *                                //       : port
 *                                //       : user
 *                                //       : ?pass?
 *                                //         NOTE: the password field should
 *                                //               probably be dropped in the
 *                                //               future since we want to treat
 *                                //               it specially for safety.
 *                                //       : database
 *                                //       : schema
 *                                //       : timestamp (for sorting)
 *
 *       * currentDBCI            - The connection parameters gathered by the
 *                                  GUI and used to make the final connection.
 *
 *       * defaultDBCI            - Parameters which generally apply to the DBMS
 *                                - The database name will be used during initial
 *                                  lookup of the available databases and schema.
 *                                - The values will be provided as hints to the
 *                                  user.
 *                            !MUST BE PROVIDED BY THE EXTENDING CONSTRUCTOR!
 *
 *       * storedDBCIList         - A java.util.List of DBCI which may be empty
 *                                  if no successful connections have been made
 *                                  or will hold a list of all the connections
 *                                  made along with their timestamps.
 *
 *
 *   CONSTRUCTORS:                // Merely call the superclass with a String
 *                                // for the page title area
 *
 *
 *   UTILITY METHODS:             // These are internal to the uDig system of
 *                                // Database connection dialogs and therefore
 *                                // only required by our needs.
 *
 *
 *   CONTRACT METHODS:            // Satisfy extension point contracts and
 *                                // interface requirements.
 *
 *       * setVisible             //TODO: document
 *
 *       * isPageComplete         //TODO: document
 *
 *       * createControl          // This creates the GUI as follows:
 *                                //   1. Instantiate the widgets
 *                                //   2. Populate the 'Previous' drop down list
 *                                //   3. Hook up the listeners
 *                                //   4? Connect the tab traversal
 *                                //   5? set page complete
 *
 *       * createAdvancedControl  // Implemented in extending classes to add
 *                                // GUI elements for DBMS specific parameters
 *
 *       * getConnection          // THE WHOLE POINT, here is abstract, will be
 *                                // implemented with DBMS specific logic by the
 *                                // concrete extendors of this class
 *
 *   LOOKUP METHODS               // Two methods to lookup the databases and
 *                                // schemata available
 *
 *       * lookupDB               - Gets a String array of available database
 *                                  names
 *
 *       * lookupSchema           - Gets a String array of available schema
 *                                - names
 *
 *   EVENT HANDLERS:
 *
 *       * focusGain              //Does little
 *
 *       * focusLoss              //Does little
 *
 *       * verifyText             //Not yet implemented
 *
 *       * modifyText             //Handles *all* the modification logic:
 *                                - uses the widget text to set the appropriate
 *                                  current connection parameter
 *                                - resets the available database and schema
 *                                  lists if they are no longer valid
 *                                - activates the lookup and connect buttons if
 *                                  the parameters are complete enough
 *
 *       * widgetSelected         //Handles the button clicked events
 *
 *           -lookupButtonWdg     - connects to the server with the default
 *                                  database/schema and gets the list of
 *                                  available databases and schemata.
 *
 *           -connectButtonWdg    - makes the connection using currentDBCI.
 *
 *           -advButtonWdg        - exposes the advanced section of the GUI.
 *
 *       * widgetDefaultSelected  //Not used
 *
 *
 *   DataBaseConnInfo class       // A bean inner class, essentially a structure
 *                                // to hold the connection parameters and a
 *                                // timestamp for the last successful
 *                                // connection using those parameters.
 *
 *
 * </pre>
 *
 * ****************** Contract with Concrete Extending Classses *************** This class, being
 * abstract, is designed specifically for the needs of the extending classes, notably the
 * PostGISWizardPage and the AbstractProprietaryDatastoreWizardPage classes. See
 *
 * @see org.locationtech.udig.catalog.internal.postgis.ui.PostGisWizardPage for the cannonical
 *      example of how to use (extend) this class in a concrete implementation. This class
 *      implicitly establishes the following contract with its extendors: I. EXTENDING CLASSES MUST:
 *      Implement a constructor which 1. calls this constructor, idealy with an appropriate string
 *      for the title area. 2. gets an IDialogSettings instance from the RCP storage system,
 *      creating a new instance if none is available. 3. populates the past connection list 4.
 *      populates the db and schema exclusion lists 5. TODO: populates the invalid character and
 *      characterSequence lists. Implement the abstract methods, most importantly the
 *      getConnection(.) method that will be used to make a test connection with the host. Override
 *      the createControl(..) method with an initial call to super.createControl(..args..) and then
 *      add logic to handle drag-and-drop (TODO). II. Extending classes may: Implement
 *      createAdvancedControl(..) to return an SWT Group widget which contains all the input widgets
 *      to obtain any DBMS specific advanced parameters desired, and handle all the appropriate
 *      event logic that ensues. Override the getDatabaseResultSet(..) and/or the
 *      getSchemasResultSet(..), if a different technique is necessary to retrieve a list of
 *      Databases or Schemas. ????????????????? DROP THIS SECTION AS REPETITIVE
 *      ????????????????????????? Note also: Extending classes wishing to expand the event handling
 *      logic should be aware of the following design for the UI event handling system: The JDBC
 *      'Connection' to the Db or Schema on the DBMS will be obtained in response to the user
 *      clicking on the "Connect" button - This will be performed by the selectionListener(..)
 *      method tied to the connectButton. The selectionListener will eventually call the
 *      getConnection(..) method assuming the parameters to use are stored in the currConn
 *      DataBaseConnInfo field. The list of databases and schema available to the user on the DBMS
 *      will be obtained in response to the user clicking on the "Lookup" button - The
 *      selectionListener tied to to the button will create a temporary, dummy connection to the
 *      DBMS, possibly using some default database generally available on the specific DBMS if the
 *      user has not yet named any more specific database. The list of available databases and
 *      schema will be reset if the user changes any of the key connection parameters - If the user
 *      modifies the host, port, user, or password parameters, the list of available databases and
 *      schema will be reset to null. Parameters for successful connections will be stored into the
 *      eclipse RCP settings system and retrieved on page construction - Values will be obtained
 *      from the eclipse storage system and placed into a java.util.List field. The UI will offer
 *      the user a drop down list of past successful values and selection by the user will cause
 *      both the values to be stored into the currInfo object and into the text area of the input
 *      widgets. TODO: Input will be verified both character by character during input to the GUI
 *      widgets and as overall URLs prior to connection - ? should this happen as each widget is
 *      about to loose focus? ? There are two strategies to help child classes: either a bunch of
 *      new methods can be created with names like verifyHostText() or we can create inner Listener
 *      classes. TODO: When focus arrives on an entry widget, the contents should be pre-selected so
 *      that direct input will obliterate existing values - TODO: The system must ignore focus
 *      events due to the user moving the mouse around the desktop. Auto-completion of values based
 *      on previous successful connection. TODO: Make sure the user actively must accept the
 *      auto-completed text so that a prior connection to 'localhost' does not prevent a future
 *      connection to 'local', i.e. the user should be able to type 'l', 'o', 'c', 'a', 'l',
 *      '<return>' and not have the completion hint in the way.
 *      ****************************************************************************
 * @author David Zwiers, dzwiers, for Refractions Research, Inc.
 * @author Jody Garnett, jody, for Refractions Research, Inc.
 * @author Jesse Eichar, jeichar, for Refractions Research, Inc.
 * @author Richard Gould, rgould, for Refractions Research, Inc.
 * @author Amr Alam, aalam, for Refractions Research, Inc.
 * @author Justin Deoliveira, jdeolive, for Refractions Research, Inc.
 * @author Cory Horner, chorner, for Refractions Research, Inc.
 * @author Adrian Custer, acuster.
 * @author Harry Bullen, hbullen.
 * @since 0.3
 */
public abstract class DataBaseRegistryWizardPage extends DataStoreWizardPage
        implements FocusListener, ModifyListener, SelectionListener
// TODO: ,VerifyListener
{

    // PARAMETERS for connection
    /**
     * The parameters we will use to make any eventual connection. Parameters are presumed to be
     * valid once stored in this object. These Parameters will be obtained via the GUI from the
     * user.
     */
    protected final DataBaseConnInfo currentDBCI = new DataBaseConnInfo(""); //$NON-NLS-1$

    /**
     * The parameters which work as defaults for the particular DBMS of the concrete extender; will
     * be populated during construction.
     */
    protected final DataBaseConnInfo defaultDBCI = new DataBaseConnInfo(""); //$NON-NLS-1$

    /**
     * The parameters of any previous successful connection, obtained from storage during
     * construction of the concrete extending class, added to following any successful connection
     * and stored prior to exit. Copy new DBCI's into this list with DataBaseConnInfo dbci = new
     * DataBaseConnInfo(""); storedDBCIList.add(dbci.setParameters(dbciToAdd)) rather than adding
     * the DBCI directly in order to preserve the separation of instances.
     */
    protected final java.util.List<DataBaseConnInfo> storedDBCIList = new ArrayList<>();

    /**
     * The RCP structure to hold and store the successful connection parameters. This must be here
     * because we need to store the settings on successful connection.
     */
    protected IDialogSettings settings; // guaranteed non-null after
    // construction

    /**
     * The connection used to actually connect with the database. Don't use this directly instead
     * call getDataSource() and use what it returns.
     */
    protected BasicDataSource dataSource = null;

    /**
     * The name of the section used to store the successful connection parameter settings in the RCP
     * storage system.
     */
    protected String settingsArrayName;

    // WIDGETS
    protected Combo rcntComboWgt = null;

    protected Text hostTextWgt = null;

    protected Text portTextWgt = null;

    protected Text userTextWgt = null;

    protected Text passTextWgt = null;

    protected Combo dbComboWgt = null;

    protected Combo schemaComboWgt = null;

    protected Button lookupBtnWgt = null;

    protected Button connectBtnWgt = null;

    protected Button advancedBtnWgt = null;

    protected Group advancedGrp = null;

    // STATE variables for the Logic
    private Widget wgtLostFocus = null; // To ignore focusGain on widgets that
    // just lost focus
    // EXCLUSION Lists

    /**
     * The names of any databases which should not be offered to the user, generally used for
     * databases that are part of the DBMS itself. The list will be populated during construction.
     */
    protected final java.util.List<String> dbExclusionList = new ArrayList<>();

    /**
     * The names of any schemata which should not be offered to the user, generally used for
     * schemata that are part of the DBMS itself. The list will be populated during construction.
     */
    protected final java.util.List<String> schemaExclusionList = new ArrayList<>();

    // CONSTRUCTORS
    /**
     * The usual (?only one used?) constructor.
     *
     * @param header The string presented to the user in the topmost part of the wizard page.
     */
    public DataBaseRegistryWizardPage(String header) {
        super(header);
    }

    // UTILITY METHODS
    /**
     * Checks if the DBMS uses Schema or not; if true, the schema widget will be shown and the
     * schema parameter passed to the DBMS during connect.
     *
     * @return true if the DBMS uses a Schema.
     */
    protected abstract boolean dbmsUsesSchema();

    /**
     * Checks the database name against those that should not be offered to the user for connection;
     * by default, all database names are acceptable so the method always returns false. The
     * constructor of implementing classes should add appropriate names to the exclusion list.
     *
     * @param d the name of the database to check.
     * @return true if the name should be excluded from those presented to the user. Here it will be
     *         false always, because by default the dbExclusionList is empty.
     */
    protected boolean excludeDbFromUserChoices(String d) {
        if (dbExclusionList.contains(d))
            return true;
        return false;
    }

    /**
     * Checks the schema name against those that should not be offered to the user for connection;
     * by default, all schema names are acceptable so the method always returns false. The
     * constructor of implementing classes should add appropriate names to the exclusion list.
     *
     * @param s the name of the schema to check.
     * @return true if the name should be excluded from those presented to the user. Here it will be
     *         false always, because by default the schemaExclusionList is empty.
     */
    protected boolean excludeSchemaFromUserChoices(String s) {
        if (schemaExclusionList.contains(s))
            return true;
        return false;
    }

    /**
     * Evaluates if the currentDBCI is complete enough that we could attempt a connection to lookup
     * values.
     *
     * @return true if we have all the pieces to connect, false otherwise
     */
    protected boolean couldLookup() {

        if ((currentDBCI.getHostString().length() > 0) && (currentDBCI.getPortString().length() > 0)
                && (currentDBCI.getUserString().length() > 0)
                && (currentDBCI.getPassString().length() > 0)
                && (currentDBCI.getDbString().length() > 0))
            return (true);
        // One or more are not set
        return (false);
    }

    /**
     * Evaluates if the currentDBCI is complete enough that we could attempt a connection
     *
     * @return true if we have all the pieces to connect, false otherwise
     */
    protected boolean couldConnect() {

        if ((currentDBCI.getHostString().length() > 0) && (currentDBCI.getPortString().length() > 0)
                && (currentDBCI.getUserString().length() > 0)
                && (currentDBCI.getPassString().length() > 0)
                && (currentDBCI.getDbString().length() > 0)) {

            if (!dbmsUsesSchema())
                // All are set and we don't use schema
                return (true);
            // Need a schema and have one
            if (currentDBCI.getSchemaString().length() > 0)
                return (true);
            // Need schema but don't have one.
            return (false);
        }
        // One or more are not set
        return (false);
    }

    /**
     * Activates the 'Lookup' button.
     */
    protected void canNowLookup() {
        lookupBtnWgt.setEnabled(true);
    }

    /**
     * Activates the 'Connect' button, and implicitly the 'Lookup' button.
     */
    protected void canNowConnect() {
        canNowLookup();
        connectBtnWgt.setEnabled(true);
    }

    /**
     * This method will run the provided activity using the wizard page progress bar; and wait for
     * the result to complete.
     * <p>
     * If anything goes wrong the message will be shown to the user
     *
     * @param activity
     */
    protected void runInPage(final IRunnableWithProgress activity) {
        // on a technical level I am not sure which one of these we should use
        //
        boolean guess = true;
        try {
            if (guess) {
                // This is what I think PostGIS wizard page does
                getContainer().run(false, true, activity);
            } else {
                // This is what the original wizard pages did
                // note the use of an internal blocking call to run the activity
                // and wait for it to complete?
                //
                getContainer().run(false, true, new IRunnableWithProgress() {
                    @Override
                    public void run(IProgressMonitor monitor)
                            throws InvocationTargetException, InterruptedException {
                        PlatformGIS.runBlockingOperation(activity, monitor);
                    }
                });
            }
        } catch (InvocationTargetException e2) {
            // log the error
            CatalogUIPlugin.log(e2.getLocalizedMessage(), e2);
            // tell the user
            setErrorMessage(e2.getCause().getLocalizedMessage());

            // preferences.performDefaults();
        } catch (InterruptedException e2) {
            // user got tired of waiting ...
        }
    }

    // CONTRACT METHODS
    /**
     * Method required by the IDialogPage implementation contract.
     */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        // TODO: is this needed, e.g. for return after <back< and >forward>
        hostTextWgt.setFocus();
    }

    /**
     * Method provided by the DataStoreWizardPage class. TODO: The method currently always returns
     * false; it is probably better to leave it to the subclass to implement it since the compiler
     * will catch the missing method.
     */
    @Override
    public boolean isPageComplete() {
        // For this abstract class, we always return false.
        return false;
    }

    /**
     * Called by the Wizard to instantiate the page and draw the GUI.
     *
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @param parent
     */
    @Override
    public void createControl(Composite comp) {

        /* ****************************************************************** */
        /* Create the GUI */
        /* ****************************************************************** */
        // GUI FIELDS
        // Constants for layout:
        int WIZARD_PARAM_INDENT = 100; // How far from the left the text fields
        // start.
        int WIZARD_COLUMN_SEPARATION = 20; // The space between the columns
        int WIZARD_CONNECT_NEG_INDENT = -142; // How far from the right the
        // status text starts
        // Repeatedly reused variables.
        FormData fd;
        Label lbl;

        // Defensively build a new Composite even though arg0 already is one
        Composite topComp = new Composite(comp, SWT.NULL);
        FormLayout topLayout = new FormLayout();
        topLayout.marginWidth = 0;
        topLayout.marginHeight = 0;
        topComp.setLayout(topLayout);

        // Stored settings: show only if we have some storage.
        if (!storedDBCIList.isEmpty()) {
            // create the combo
            lbl = new Label(topComp, SWT.NONE);
            lbl.setText(Messages.DataBaseRegistryWizardPage_label_recent_text);
            lbl.setToolTipText(Messages.DataBaseRegistryWizardPage_recent_tooltip);
            fd = new FormData();
            fd.top = new FormAttachment(0, 10);
            fd.left = new FormAttachment(0, 24);
            lbl.setLayoutData(fd);
            rcntComboWgt = new Combo(topComp, SWT.NULL | SWT.READ_ONLY);
            rcntComboWgt.select(0);
            rcntComboWgt.setToolTipText(Messages.DataBaseRegistryWizardPage_recent_tooltip);
            fd = new FormData();
            fd.top = new FormAttachment(0, 10);
            fd.left = new FormAttachment(lbl, 5);
            fd.right = new FormAttachment(100, -40);
            rcntComboWgt.setLayoutData(fd);
        }

        // Composite to hold the parameter widgets
        Composite paramComp = new Composite(topComp, SWT.NULL);
        fd = new FormData();
        if (0 < storedDBCIList.size()) {
            fd.top = new FormAttachment(rcntComboWgt, 10);
        } else {
            fd.top = new FormAttachment(0, 2);
        }
        fd.left = new FormAttachment(topComp, 2);
        fd.bottom = new FormAttachment(100, -2);
        paramComp.setLayoutData(fd);
        paramComp.setLayout(new FormLayout());

        // Host
        lbl = new Label(paramComp, SWT.NULL);
        lbl.setText(Messages.DataBaseRegistryWizardPage_label_host_text);
        lbl.setToolTipText(Messages.DataBaseRegistryWizardPage_host_tooltip);
        fd = new FormData();
        fd.top = new FormAttachment(rcntComboWgt, 15);
        fd.left = new FormAttachment(0, 5);
        lbl.setLayoutData(fd);
        hostTextWgt = new Text(paramComp, SWT.BORDER);
        hostTextWgt.setToolTipText(Messages.DataBaseRegistryWizardPage_host_tooltip);
        fd = new FormData();
        fd.top = new FormAttachment(rcntComboWgt, 15);
        fd.left = new FormAttachment(0, WIZARD_PARAM_INDENT);
        fd.right = new FormAttachment(100, -5);
        hostTextWgt.setLayoutData(fd);

        // Port
        lbl = new Label(paramComp, SWT.NULL);
        lbl.setText(Messages.DataBaseRegistryWizardPage_label_port_text);
        lbl.setToolTipText(Messages.DataBaseRegistryWizardPage_port_tooltip);
        fd = new FormData();
        fd.top = new FormAttachment(hostTextWgt, 5);
        fd.left = new FormAttachment(0, 5);
        lbl.setLayoutData(fd);
        portTextWgt = new Text(paramComp, SWT.BORDER);
        portTextWgt.setToolTipText(Messages.DataBaseRegistryWizardPage_port_tooltip);
        fd = new FormData();
        fd.top = new FormAttachment(hostTextWgt, 5);
        fd.left = new FormAttachment(0, WIZARD_PARAM_INDENT);
        // Could change this to calculate size needed for 5 characters: 65535
        fd.right = new FormAttachment(0, WIZARD_PARAM_INDENT + 60);
        portTextWgt.setLayoutData(fd);

        // User
        lbl = new Label(paramComp, SWT.NULL);
        lbl.setText(Messages.DataBaseRegistryWizardPage_label_username_text);
        lbl.setToolTipText(Messages.DataBaseRegistryWizardPage_username_tooltip);
        fd = new FormData();
        fd.top = new FormAttachment(portTextWgt, 5);
        fd.left = new FormAttachment(0, 5);
        lbl.setLayoutData(fd);
        userTextWgt = new Text(paramComp, SWT.BORDER);
        userTextWgt.setToolTipText(Messages.DataBaseRegistryWizardPage_username_tooltip);
        fd = new FormData();
        fd.top = new FormAttachment(portTextWgt, 5);
        fd.left = new FormAttachment(0, WIZARD_PARAM_INDENT);
        fd.right = new FormAttachment(100, -5);
        userTextWgt.setLayoutData(fd);

        // Password
        lbl = new Label(paramComp, SWT.NULL);
        lbl.setText(Messages.DataBaseRegistryWizardPage_label_password_text);
        lbl.setToolTipText(Messages.DataBaseRegistryWizardPage_password_tooltip);
        fd = new FormData();
        fd.top = new FormAttachment(userTextWgt, 5);
        fd.left = new FormAttachment(0, 5);
        lbl.setLayoutData(fd);
        passTextWgt = new Text(paramComp, SWT.BORDER);
        passTextWgt.setEchoChar(("\u2022").toCharArray()[0]); //$NON-NLS-1$
        passTextWgt.setToolTipText(Messages.DataBaseRegistryWizardPage_password_tooltip);
        fd = new FormData();
        fd.top = new FormAttachment(userTextWgt, 5);
        fd.left = new FormAttachment(0, WIZARD_PARAM_INDENT);
        fd.right = new FormAttachment(100, -5);
        passTextWgt.setLayoutData(fd);

        // Lookup Button
        lookupBtnWgt = new Button(paramComp, SWT.PUSH);
        lookupBtnWgt.setText(Messages.DataBaseRegistryWizardPage_button_lookup_text);
        lookupBtnWgt.setToolTipText(Messages.DataBaseRegistryWizardPage_button_lookup_tooltip);
        fd = new FormData();
        fd.top = new FormAttachment(passTextWgt, 20);
        fd.right = new FormAttachment(100, -5);
        lookupBtnWgt.setLayoutData(fd);

        // Database
        lbl = new Label(paramComp, SWT.NULL);
        lbl.setText(Messages.DataBaseRegistryWizardPage_label_database_text);
        lbl.setToolTipText(Messages.DataBaseRegistryWizardPage_database_tooltip);
        fd = new FormData();
        fd.top = new FormAttachment(passTextWgt, 5);
        fd.left = new FormAttachment(0, 5);
        lbl.setLayoutData(fd);
        dbComboWgt = new Combo(paramComp, SWT.BORDER);
        dbComboWgt.setToolTipText(Messages.DataBaseRegistryWizardPage_database_tooltip);
        fd = new FormData();
        fd.left = new FormAttachment(0, WIZARD_PARAM_INDENT);
        fd.top = new FormAttachment(passTextWgt, 5);
        fd.right = new FormAttachment(lookupBtnWgt, -5);
        dbComboWgt.setLayoutData(fd);

        // Schema
        lbl = new Label(paramComp, SWT.NULL);
        lbl.setText(Messages.DataBaseRegistryWizardPage_label_schema_text);
        lbl.setToolTipText(Messages.DataBaseRegistryWizardPage_schema_tooltip);
        fd = new FormData();
        fd.top = new FormAttachment(dbComboWgt, 5);
        fd.left = new FormAttachment(0, 5);
        lbl.setLayoutData(fd);
        schemaComboWgt = new Combo(paramComp, SWT.BORDER);
        schemaComboWgt.setToolTipText(Messages.DataBaseRegistryWizardPage_schema_tooltip);
        fd = new FormData();
        fd.top = new FormAttachment(dbComboWgt, 5);
        fd.left = new FormAttachment(0, WIZARD_PARAM_INDENT);
        fd.right = new FormAttachment(lookupBtnWgt, -5);
        schemaComboWgt.setLayoutData(fd);
        if (!dbmsUsesSchema()) {
            lbl.setVisible(false);
            schemaComboWgt.setVisible(false);
        }

        // Advanced section
        // First, create the Advanced Settings Group by calling the method
        // in the concrete class
        advancedGrp = createAdvancedControl(paramComp);

        // If the group exists, make the button and create the group
        if (null != advancedGrp) {
            // Add the Check Button
            advancedBtnWgt = new Button(paramComp, SWT.CHECK);
            advancedBtnWgt.setText(Messages.DataBaseRegistryWizardPage_label_advanced_text);
            advancedBtnWgt
                    .setToolTipText(Messages.DataBaseRegistryWizardPage_label_advanced_tooltip);
            fd = new FormData();
            fd.top = new FormAttachment(dbComboWgt, 45);
            fd.left = new FormAttachment(0, 5);
            advancedBtnWgt.setLayoutData(fd);
            advancedBtnWgt.setSelection(false);
            advancedBtnWgt.addSelectionListener(this);

            // Lay out the Group
            FormData d = new FormData();
            d.top = new FormAttachment(dbComboWgt, 45);
            d.left = new FormAttachment(0, 5);
            d.right = new FormAttachment(100, -5);
            advancedGrp.setLayoutData(d);
            //
            // //Hide and turn off the Group
            advancedGrp.setVisible(false);// hide it - show with button click
            advancedGrp.setEnabled(false);// turn off - we don't want events
        }

        // Connection Button:
        connectBtnWgt = new Button(topComp, SWT.PUSH);
        connectBtnWgt.setText(Messages.DataBaseRegistryWizardPage_button_connect_text);
        connectBtnWgt.setToolTipText(Messages.DataBaseRegistryWizardPage_button_connect_tooltip);
        fd = new FormData();
        fd.left = new FormAttachment(paramComp, 10);
        // fd.right = new FormAttachment(100,-130);
        fd.bottom = new FormAttachment(100, 0);
        connectBtnWgt.setLayoutData(fd);

        // Strech the paramGrp to take all the space
        ((FormData) paramComp.getLayoutData()).right = new FormAttachment(100,
                WIZARD_CONNECT_NEG_INDENT - WIZARD_COLUMN_SEPARATION);

        /* ****************************************************************** */
        /* Populate the GUI */
        /* ****************************************************************** */

        // If there are stored valid connections, add them to the Combo Widget
        // TODO: This should be limited to only n recent connections
        for (DataBaseConnInfo dbci : storedDBCIList) {
            rcntComboWgt.add(dbci.toDisplayString());
        }

        // Populate the rest of the widgets with the current values which are
        // the
        // defaults which were set in the constructor of the extending class.
        // Note we hide from the user the default database and schema if these
        // are not to be picked as the databases to work against eventhough we
        // leave them as usable for the lookup connection.
        currentDBCI.treatEmptyStringAsNull(false);
        hostTextWgt.setText(currentDBCI.getHostString());
        portTextWgt.setText(currentDBCI.getPortString());
        userTextWgt.setText(currentDBCI.getUserString());
        passTextWgt.setText(currentDBCI.getPassString());
        String str = null;
        str = currentDBCI.getDbString();
        if (!excludeDbFromUserChoices(str))
            dbComboWgt.setText(str);
        if (dbmsUsesSchema()) {
            str = currentDBCI.getSchemaString();
            if (!excludeSchemaFromUserChoices(str))
                schemaComboWgt.setText(str);
        }

        /* ****************************************************************** */
        /* Configure focus and enablement */
        /* ****************************************************************** */
        if (null != rcntComboWgt)
            rcntComboWgt.setFocus();
        else
            hostTextWgt.setFocus();

        if (couldConnect()) {
            lookupBtnWgt.setEnabled(true);
            connectBtnWgt.setEnabled(true);
        } else {
            lookupBtnWgt.setEnabled(false);
            connectBtnWgt.setEnabled(false);
        }

        /* ****************************************************************** */
        /* Hook up the Event Handlers */
        /* ****************************************************************** */

        // Add the Focus listeners
        hostTextWgt.addFocusListener(this);
        portTextWgt.addFocusListener(this);
        userTextWgt.addFocusListener(this);
        passTextWgt.addFocusListener(this);
        dbComboWgt.addFocusListener(this);
        schemaComboWgt.addFocusListener(this);

        // Add the Modify listeners: Store edits after each modification
        if (null != rcntComboWgt)
            rcntComboWgt.addModifyListener(this);
        hostTextWgt.addModifyListener(this);
        portTextWgt.addModifyListener(this);
        userTextWgt.addModifyListener(this);
        passTextWgt.addModifyListener(this);
        dbComboWgt.addModifyListener(this);
        schemaComboWgt.addModifyListener(this);

        // Selection events: connect to DB for lookup or connection
        lookupBtnWgt.addSelectionListener(this);
        connectBtnWgt.addSelectionListener(this);
        // advancedBtnWgt .addSelectionListener(this); //Done above!

        /* ****************************************************************** */
        /* Hook up Tab Traversal */
        /* ****************************************************************** */
        // This currently doesn't work because we have widgets in different
        // trees (not sure that's the reason it is broken).
        // Focus currently follows the default pattern of the order of addition
        // of the widgets plus the occasional setFocus() methods which can
        // be found with a simple text search.
        // Code left here for any future coder wishing to be brave.
        // List<Control> tablist=new LinkedList<Control>();
        // tablist.add(rcntComboWgt);
        // tablist.add(hostTextWgt);
        // tablist.add(portTextWgt);
        // tablist.add(userTextWgt);
        // tablist.add(passTextWgt);
        // tablist.add(dbComboWgt);
        // if( schemaComboWgt!=null )
        // tablist.add(schemaComboWgt);
        // if( advancedGrp!=null ){
        // tablist.add(advancedBtnWgt);
        // tablist.add(advancedGrp);
        // }
        // tablist.add(connectBtnWgt);
        // topComp.setTabList(tablist.toArray(new Control[tablist.size()]));

        /* ****************************************************************** */
        /* Let her rumble */
        /* ****************************************************************** */
        setControl(topComp);
        setPageComplete(true); // used to highlight the finished button
    }

    /**
     * Creates a GUI Group with widgets to set advanced parameters of the connection or, by default,
     * null if there are no advanced settings. Classes extending this abstract class may override
     * this method and both provide the widgets and handle the events.
     *
     * @param arg0 the parent widget in which the widgets will be added.
     * @return the Group containing the widgets which will be drawn in the 'Advanced' section of the
     *         wizard page or null (the default) if there is none.
     */
    protected Group createAdvancedControl(Composite arg0) {
        return null;

    }

    /**
     * This is the class that will actually obtain the DataSource. It will try and make a connection
     * to ensure this works out, as such it throws an SQLException if it's an odd day of the week or
     * if you squint too hard.
     *
     * @return A working DataSource for the database. It should not be closed. But it might be if
     *         you are unlucky.
     */
    protected abstract DataSource getDataSource() throws Exception;

    // LOOKUP METHODS
    /**
     * This method is called in response to selection of the lookup button and gets the names of the
     * available databases on the DBMS server. Instead of overriding this method it is better to
     * override the getDatabaseResultSet method if a custom way of getting the database names is
     * needed
     *
     * @param con the java.sql.Connection returned by getConnection()
     * @return An array of Strings containing the names of only the databases available on the
     *         server which are suitable for display to the user.
     */
    protected String[] lookupDbNamesForDisplay(DataSource dataSource) {
        java.util.List<String> dbList = new ArrayList<>();
        Connection con = null;
        try {
            con = dataSource.getConnection();
            ResultSet rs = null;
            if (con != null) {
                rs = getDatabaseResultSet(con);
                while (rs.next()) {
                    String dbName = rs.getString(1);
                    if (!excludeDbFromUserChoices(dbName)) {
                        dbList.add(dbName);
                    }
                }
            }
            return dbList.toArray(new String[dbList.size()]);
        } catch (SQLException e) {
            setMessage(Messages.DataBaseRegistryWizardPage_databaseMessage);
            setErrorMessage(e.getLocalizedMessage());
            return null;
        } finally {
            if (con != null) {
                try {
                    con.close(); // return to pool
                } catch (SQLException e) {
                    // closing anyways
                }
            }
        }
    }

    /**
     * This method can is called so that diffrent databases can provied their own method of
     * obtaining the list of databases without overriding the lookupDbNamesForDisplay method.
     *
     * @param c the java.sql.Connection returned by getConnection()
     * @return A ResultSet that contains the Database names, for this connection.
     */
    protected ResultSet getDatabaseResultSet(Connection c) throws SQLException {
        return c.getMetaData().getCatalogs();
    }

    /**
     * This method is called in response to selection of the lookup button and gets the names of the
     * available schemata on the DBMS server. Like lookupDbNameForDisplay it is better to avoid
     * overriding this method if a database specific implementation is needed. Instead override
     * getSchemaResultSet WARNING: This should never be called if !dbmsUsesSchema().
     *
     * @param con the java.sql.Connection object returned by getConnection()
     * @return An array of Strings containing the names of the schemata available on the server.
     */
    protected String[] lookupSchemaNamesForDisplay(DataSource dataSource) {
        if (dataSource == null) {
            return null; // not connected
        }
        Connection con = null;
        java.util.List<String> schemaList = new ArrayList<>();
        try {
            con = dataSource.getConnection();
            ResultSet rs = null;
            if (con != null) {
                rs = getSchemasResultSet(con);
                while (rs.next()) {
                    String schemaName = rs.getString(1);
                    if (!excludeSchemaFromUserChoices(schemaName)) {
                        schemaList.add(schemaName);
                    }
                }
            }
            return schemaList.toArray(new String[schemaList.size()]);
        } catch (SQLException e) {
            setMessage(Messages.DataBaseRegistryWizardPage_schemaMessage);
            setErrorMessage(e.getLocalizedMessage());
            return null;
        } finally {
            if (con != null) {
                try {
                    con.close(); // return to pool
                } catch (SQLException e) {
                    // we are closing anyways - ignore
                }
            }
        }
    }

    /**
     * This method can is called so that different databases can provided their own method of
     * obtaining the list of schemas without overriding the lookupSchemasNamesForDisplay method.
     *
     * @param c the java.sql.Connection returned by getConnection()
     * @return A ResultSet that contains the Schema names, for this connection.
     */
    protected ResultSet getSchemasResultSet(Connection c) throws SQLException {
        return c.getMetaData().getSchemas();
    }

    // EVENT HANDLING METHODS
    /**
     * The focusGained method is called when input focus passes to one of the widgets in the page,
     * following the standard FocusListener contract. If we need to distinguish internal changes of
     * focus from changes of focus involving other programs, such as if the user wanders off to read
     * about the GNU project in her browser and then comes back, we compare the widget which lost
     * focus, saved when handling the focusLost event, to the widget for which this event has been
     * generated. In this case we ignore any focusGain events for widgets which just had the focus;
     * such widgets should still be in the correct state. We no longer trigger connections or
     * validation on focusEvents because of this uncertainty. TODO: Gain of focus on text widgets
     * merely pre-selects the text for deletion if the user inputs new text.
     *
     * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
     * @param e the event object describing the action that triggered this call
     */
    @Override
    public void focusGained(FocusEvent e) {

        // Catch the situation where we are disposed between the event being
        // fired and this handler.
        if (null == e.widget) {
            return;
        }

        // We ignore focusGain events on widgets that have just lost focus
        // because this generally means that the focus wandered to another
        // program and has now come back.
        // This also happens when focus returns to the Text field of a Combo
        // widget after the user has interacted with the dropDown portion of
        // that widget.
        if (e.widget == wgtLostFocus)
            return;

    }

    /**
     * The focusLost method is called when input focus leaves one of the widgets in our page,
     * following the standard FocusListener contract. Note that loss of focus does not necessarily
     * imply the user has to work on another widget but may be due to a temporary move to another
     * part of the operating system---the user's mouse could have slipped onto another window or
     * they could be checking email. We no longer trigger connections or validation on focusEvents
     * because of this uncertainty.
     *
     * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
     * @param e the event (and widget via e.widget) generated for the focus change.
     */
    @Override
    public void focusLost(FocusEvent e) {

        // Catch the situation where we are disposed between the event being
        // fired and this handler.
        if (null == e.widget) {
            return;
        }

        // Saved in order to be able to ignore focusGain events on widgets
        // which just lost focus.
        // That will happen for focus changes to other programs.
        // This will also happen when focus leaves the Text field of a Combo
        // widget as the user opens up the dropDown portion of the Combo.
        wgtLostFocus = e.widget;

    }

    /**
     * The verifyText method is called prior to modification of the text content in any widget with
     * a Text component, notably the Text and Combo widgets, following the standard verifyListener
     * contract. TODO: Activate this to make verification work. Re-design it based on exclusion
     * lists of characters and strings. The method is used to validate user provided input. The user
     * may change the text character-by-character if they are typing, or may change a whole string
     * through select then delete, by pasting, or by selecting an entry in a drop down Combo. The
     * method works by exclusion which will allow sub-classes to become more restrictive according
     * to their particular rules. This class should therefore be permissive, excluding only
     * characters which are obviously wrong. Extending classes can override this method, starting
     * with a call to this method, i.e. super.verifyText(ve), and then continue with their own, more
     * restrictive, assessment.
     *
     * @param ve the VerifyEvent object representing the action which triggered the method call.
     */
    /*
     * public void verifyText( VerifyEvent ve) { // Host name: alphanum plus . - if (ve.widget ==
     * host){ ve.doit = ( ( 0 == ve.text.length() ) || Character.isLetterOrDigit(ve.character) ||
     * '.' == ve.character || '-' == ve.character ); } //Port number: only uses digits and
     * non-characters e.g. bksp or del // Handle "" also if (ve.widget == port){ ve.doit = ( (0 ==
     * ve.text.length() ) || Character.isDigit(ve.character) ); return; } //User name: if (ve.widget
     * == user){ return; } //Password is not examined because can be almost anything //Database
     * name: if (ve.widget == database){ return; } //Schema name: if (ve.widget == schema){ return;
     * } }
     */
    /**
     * The modifyText method is called by the event handling mechanism for any widget with a Text
     * field, notably Text and Combo widgets, according to the standard ModifyListener contract. We
     * use this method to add the modified contents into the currentDBCI. The text of the
     * modification should already have been verified prior to this method being called. We may add
     * the verification of the whole text field either here or in the verify listener. TODO: add a
     * verification step here to establish that the whole text contents of the widget's Text field
     * are valid. Figure out how to respond if they are not. If any of the connection parameters are
     * altered, we clear the lists of available databases and schemata since they are no longer
     * guaranteed to work but we leave the text as is since the user is working on it. As a last
     * step, we also evaluate if we can now connect, and if so activate the lookup and connect
     * buttons.
     */
    @Override
    public void modifyText(ModifyEvent e) {

        // Catch the situation where we are disposed between the event being
        // fired and this handler.
        if (null == e.widget) {
            return;
        }

        // SWITCH ON e.widget
        if (e.widget == rcntComboWgt) {
            // set the connection info
            // NOTE: This presumes the two indices are strictly equal. If we make
            // the combo hold only a subset of the stored list, we will
            // have to use a different index.
            int i = rcntComboWgt.getSelectionIndex();
            currentDBCI.setParameters(storedDBCIList.get(i));
            // Set the text info
            hostTextWgt.setText(currentDBCI.getHostString());
            portTextWgt.setText(currentDBCI.getPortString());
            userTextWgt.setText(currentDBCI.getUserString());
            passTextWgt.setText(currentDBCI.getPassString());
            dbComboWgt.setText(currentDBCI.getDbString());
            if (null != schemaComboWgt)
                schemaComboWgt.setText(currentDBCI.getSchemaString());
            canNowConnect();// Implies we can lookup as well
            connectBtnWgt.setFocus(); // Allows the user to pick and hit enter
            // twice.
        }
        if (e.widget == hostTextWgt) {

            // store the modified value
            currentDBCI.setHost(((Text) e.widget).getText());

            // clear the lists of available databases and schemata
            int i = dbComboWgt.getItemCount();
            dbComboWgt.remove(0, i - 1);
            if (dbmsUsesSchema()) {
                int j = schemaComboWgt.getItemCount();
                schemaComboWgt.remove(0, j - 1);
            }

        }
        if (e.widget == portTextWgt) {

            // store the modified value
            currentDBCI.setPort(((Text) e.widget).getText());

            // clear the lists of available databases and schemata
            int i = dbComboWgt.getItemCount();
            dbComboWgt.remove(0, i - 1);
            if (dbmsUsesSchema()) {
                int j = schemaComboWgt.getItemCount();
                schemaComboWgt.remove(0, j - 1);
            }

        }
        if (e.widget == userTextWgt) {

            // store the modified value
            currentDBCI.setUser(((Text) e.widget).getText());

            // clear the lists of available databases and schemata
            int i = dbComboWgt.getItemCount();
            dbComboWgt.remove(0, i - 1);
            if (dbmsUsesSchema()) {
                int j = schemaComboWgt.getItemCount();
                schemaComboWgt.remove(0, j - 1);
            }

        }
        if (e.widget == passTextWgt) {

            // store the modified value
            currentDBCI.setPass(((Text) e.widget).getText());

            // clear the lists of available databases and schemata
            int i = dbComboWgt.getItemCount();
            dbComboWgt.remove(0, i - 1);
            if (dbmsUsesSchema()) {
                int j = schemaComboWgt.getItemCount();
                schemaComboWgt.remove(0, j - 1);
            }

        }
        if (e.widget == dbComboWgt) {

            // store the modified value
            currentDBCI.setDb(((Combo) e.widget).getText());

        }
        if (dbmsUsesSchema()) {

            // store the modified value
            if (e.widget == schemaComboWgt) {
                currentDBCI.setSchema(((Combo) e.widget).getText());
            }

        }

        // At end, activate lookup and connect buttons if there is enough
        // info to try. Either we could connect, in which case we will
        // activate both the connect and the lookup buttons, or we might
        // be able to lookup only.
        if (couldConnect()) {
            canNowConnect();
        } else if (couldLookup()) {
            canNowLookup();
        }

    }

    /**
     * The method called by the event handling mechanism for any regular (i.e. not 'default)
     * selection event on any widget to which this class was added as a SelectionListener. The only
     * widgets we care about are the button widgets. Text and Combo widgets will receive
     * modifyEvents for any changes to their contents so we handle their entries in the
     * modifyText(..) method.
     *
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     * @see #widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     * @param e the SelectionEvent which includes e.widget, the widget generating the event
     */
    @Override
    public void widgetSelected(SelectionEvent e) {
        // Catch the situation where we are disposed between the event being
        // fired and this handler being called.
        if (null == e.widget) {
            return;
        }

        // SWITCH on widget

        if (e.widget.equals(advancedBtnWgt)) {
            boolean b = advancedBtnWgt.getSelection();
            advancedGrp.setVisible(b);
            advancedGrp.setEnabled(b);
        }

        if (e.widget.equals(lookupBtnWgt)) {

            // Trap a spurious second click, re-enable at end
            // Ofcourse this is a race condition so the trap might not work
            lookupBtnWgt.setEnabled(false);

            // Get a connection for the lookup
            DataSource dataSource = null;

            // This check should always be true, because we should check before
            // calling the method. Note, we catch a 'false' here in the next
            // statement below since 'con' will remain 'null'.
            if (couldConnect()) {
                try {
                    dataSource = getDataSource();
                } catch (Exception ex) {
                    // Log the error
                    CatalogUIPlugin.log(ex.getLocalizedMessage(), ex);
                    // Set the error in the animated area of the Dialog
                    setErrorMessage(ex.getLocalizedMessage());
                }
            }

            // Did we fail? If so, bail out
            if (dataSource == null) {
                // Re-enable the lookup button
                // e.g user now starts the server, wants to connect
                lookupBtnWgt.setEnabled(true);

                return;

            }

            // By here, the connection has succeeded, it is non-null.

            // Reset any previous error messages
            setErrorMessage(null);

            // Get the database names suitable for display to the user
            // Logic is in a separate method to be easy to override by extendors
            String[] arr = lookupDbNamesForDisplay(dataSource);
            // Set the database names in the dbComboWgt
            if (null != arr) {
                dbComboWgt.setItems(arr);
                dbComboWgt.select(0);
            }

            // If needed, get the schema names and set them in the
            // schemaComboWgt
            // Logic is in a separate method to be easy to override by extendors
            if (dbmsUsesSchema()) {
                arr = lookupSchemaNamesForDisplay(dataSource);
                if (null != arr) {
                    schemaComboWgt.setItems(arr);
                    schemaComboWgt.select(0);
                }
            }

            // Set focus on the database list: the user may want to pick a new
            // db
            dbComboWgt.setFocus();

            // Close the connection used for lookup
            if (dataSource != null) {
                // dataSource.close();
            }

            // Re-enable the lookup widget
            // This allows the user can try again, for example, if the server
            // has changed in the meantime.
            lookupBtnWgt.setEnabled(true);

            return;
        }

        if (e.widget.equals(connectBtnWgt)) {

            // Trap a spurious second click; disable & re-enable if we fail
            connectBtnWgt.setEnabled(false);

            // TODO: check first if we are connected; if so return (or
            // disconnect?)
            // Look in java.net for functionality (see error message on failure)

            if (!couldConnect()) {
                // This should never happen since the connect button should not
                // be activated if we don't have the info needed to connect.
                // Leave the button inactive.
                return;
            }

            // Try to connect
            DataSource dataSource = null;
            try {
                dataSource = getDataSource();
            } catch (Exception ex) {
                // Log the error
                CatalogUIPlugin.log(ex.getLocalizedMessage(), ex);
                // Set the error in the animated area of the Dialog
                setErrorMessage(ex.getLocalizedMessage());
            }
            // Did we fail?
            if (dataSource == null) {
                // Failed to connect.
                // Reset the dialog: the user may go launch the Db server.
                connectBtnWgt.setEnabled(true);
                return;
            }
            // By here, the connection is ok

            // Reset any previous error messages
            setErrorMessage(null);

            // Activate Finish Button
            getWizard().getContainer().updateButtons();

            // Append the successful connection to the stored list
            if (!storedDBCIList.contains(currentDBCI)) {
                DataBaseConnInfo dbci = new DataBaseConnInfo(""); //$NON-NLS-1$
                dbci.setParameters(currentDBCI);
                storedDBCIList.add(dbci);
            }

            // Store all connection parameters
            int s = storedDBCIList.size();
            java.util.List<String> rec = new ArrayList<>(s);
            for (DataBaseConnInfo i : storedDBCIList) {
                rec.add(i.toString());
            }
            settings.put(settingsArrayName, rec.toArray(new String[s]));

            // Reset the dialog entries
            // NB this will also reset currentDBCI through the modify listener
            // TODO: move this elsewhere since it's ugly to see the values
            // unset prior to clicking the 'Finish' button
            // not to mention very very very confusing
            // XXX: Comented this section out because of the above "todo"

            /*
             * hostTextWgt.setText(defaultDBCI.getHostString());
             * portTextWgt.setText(defaultDBCI.getPortString());
             * userTextWgt.setText(defaultDBCI.getUserString());
             * passTextWgt.setText(defaultDBCI.getPassString()); // These two lines don't seem
             * necessary but should be and do no harm dbComboWgt.removeAll();
             * schemaComboWgt.removeAll(); if (!excludeDbFromUserChoices(defaultDBCI.getDbString()))
             * dbComboWgt.setText(defaultDBCI.getDbString()); if (null != schemaComboWgt) if
             * (!excludeSchemaFromUserChoices(defaultDBCI.getSchemaString()))
             * schemaComboWgt.setText(defaultDBCI.getSchemaString());
             */

        }

    }

    /**
     * The method called by the event handling mechanism for any 'default' selection event on any
     * widget to which this class was added as a SelectionListener. There is nothing to do here. The
     * widgetDefaultSelected method is called only in the Text and Combo widgets when the <enter>
     * key is typed while focus is on the Text field. Since changes to the text field all generate
     * the modifyEvent as well, we will handle modifications in the modifyText(..) method and can do
     * nothing here.
     *
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     * @see #widgetSelected(org.eclipse.swt.events.SelectionEvent)
     * @param e
     */
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {

        return;

        // Catch the situation where we are disposed between the event being
        // fired and this handler.
        // if (null == e.widget){
        // return;
        // }

    }

    @Override
    public void dispose() {
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (SQLException e) {
                // couldn't close connection, no matter -- we are exiting
            }
        }
        super.dispose();
    }

}
