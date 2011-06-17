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
package net.refractions.udig.catalog.service.database;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.ui.AbstractUDIGImportPage;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.ISharedImages;
import net.refractions.udig.catalog.ui.UDIGConnectionPage;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * The first of a two page wizard for connecting to a database. This page requires the user enter
 * host, port, username and password.
 * 
 * @author jesse
 * @since 1.1.0
 */
public class UserHostPage extends AbstractUDIGImportPage implements UDIGConnectionPage {

    // /////
    // The following constants are those used to store (and retrieve) the settings from and to the
    // Dialog Settings
    // /////
    private static final String PREVIOUS_CONNECTIONS = "previous_settings"; //$NON-NLS-1$
    protected static final String TIMESTAMP = "TIMESTAMP"; //$NON-NLS-1$
    private static final String USERNAME = "USERNAME"; //$NON-NLS-1$
    private static final String HOST = "HOST"; //$NON-NLS-1$
    private static final String PORT = "PORT"; //$NON-NLS-1$
    protected static final String PASSWORD = "PASSWORD"; //$NON-NLS-1$
    private static final String SAVE_PASSWORD = "SAVE_PASSWORD"; //$NON-NLS-1$
    // / End of Dialog settings constants

    private static final String REQUIRED_DECORATION = "REQUIRED_DECORATION"; //$NON-NLS-1$
    private static final String DELETED = "DELETED"; //$NON-NLS-1$

    final DatabaseServiceDialect dialect;
    private final DatabaseWizardLocalization localization;
    
    private Text host;
    private Text port;
    private Text password;
    private Text username;
    
    // this is populates when the Next button is pressed. This allows the next page (PostgisConnectionPage) to get at the databaseNames
    // without having to reconnect to the database.
    private String[] databaseNames;
    private Button savePassword;
    private Combo previousConnections;
    private String defaultPort;
	private ExtraParamsControl extraParams;

    public UserHostPage(DatabaseServiceDialect dialect) {
        super("User and Host page"); //$NON-NLS-1$
        this.dialect = dialect;
        this.defaultPort = dialect.portParam.sample.toString();
        this.localization = dialect.localization;
    }

    public Map<String, Serializable> getParams() {
        return null;
    }

    public void createControl( Composite parent ) {
        Composite top = new Composite(parent, SWT.NONE);
        top.setLayout(new GridLayout(4, false));
        setControl(top);

        createPreviousConnectionsCombo(top);
        host = createLabelAndText(top, localization.host+":", SWT.BORDER); //$NON-NLS-1$
        host.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        port = createLabelAndText(top, localization.port+":", SWT.BORDER); //$NON-NLS-1$
        port.setText(defaultPort);
        port.setLayoutData(new GridData());

        username = createLabelAndText(top, localization.username+":", SWT.BORDER); //$NON-NLS-1$
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 3;
        username.setLayoutData(data);

        password = createLabelAndText(top, localization.password+":", SWT.PASSWORD | SWT.BORDER); //$NON-NLS-1$
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 3;
        password.setLayoutData(data);
        
        this.savePassword = new Button(top, SWT.CHECK);
        this.savePassword.setText(localization.storePassword);
        GridDataFactory.swtDefaults().span(4, 1).applyTo(savePassword);
        
        Button removeConnection = new Button(top, SWT.PUSH);
        GridDataFactory.swtDefaults().span(4, 1).indent(0, 10).applyTo(removeConnection);
        removeConnection.setText(localization.removeConnection);
        removeConnection.addListener(SWT.Selection, new Listener(){

            public void handleEvent( Event event ) {
                removeConnection();
            }
            
        });

        this.extraParams = dialect.createHostPageExtraParamControl();

        if(extraParams != null) {

	        final Button optionalParams = new Button(top, SWT.CHECK);
	        optionalParams.setText(localization.optionalParams);
	        GridDataFactory.swtDefaults().span(4, 1).applyTo(optionalParams);
	        optionalParams.setSelection(false);

	        final Control extraParamsControl = extraParams.createControl(top);
	        extraParamsControl.setVisible(false);
	        data = new GridData(GridData.FILL_BOTH);
	        data.horizontalSpan=4;
	        extraParamsControl.setLayoutData(data);

	        optionalParams.addListener(SWT.Selection, new Listener() {
				
				public void handleEvent(Event event) {
					extraParamsControl.setVisible(optionalParams.getSelection());
				}
			});
	        
        }
    }

    protected void removeConnection() {
        if(host.getText().length()==0 && 
                username.getText().length()==0 &&
                password.getText().length()==0 &&
                port.getText().equals(defaultPort)){
            return;
        }
        
        boolean confirm = MessageDialog.openConfirm(getShell(), localization.removeConnection, localization.confirmRemoveConnection);
        
        if (confirm) {
            this.host.setText(""); //$NON-NLS-1$
            this.port.setText(defaultPort); 
            this.username.setText(""); //$NON-NLS-1$
            this.password.setText(""); //$NON-NLS-1$
            this.savePassword.setSelection(false);
            this.host.setFocus();

            int index = previousConnections.getSelectionIndex();
            String item = previousConnections.getItem(index);
            IDialogSettings settings = (IDialogSettings) previousConnections.getData(item);
            if (settings != null) {
                previousConnections.remove(index);
                previousConnections.setData(item, null);
                settings.put(DELETED, true);
            }
            
            if(previousConnections.getItemCount()<=1){
                previousConnections.setVisible(false);
            }
        }

    }

    private void createPreviousConnectionsCombo( Composite top ) {
        Label label = new Label(top, SWT.NONE);
        label.setText(localization.previousConnections);

        GridData gridData = new GridData(SWT.FILL, SWT.TOP, false, false);
        label.setLayoutData(gridData);

        this.previousConnections = new Combo(top, SWT.READ_ONLY);
        populatePreviousConnections(previousConnections);
        gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
        gridData.horizontalSpan = 3;
        previousConnections.setLayoutData(gridData);

        // two is chosen because there may be only the empty string in the combo. In this case we
        // don't want to show the combo
        if (previousConnections.getItemCount() < 2) {
            previousConnections.setVisible(false);
        }

        previousConnections.addListener(SWT.Selection, new Listener(){

            public void handleEvent( Event event ) {
                String item = previousConnections.getItem(previousConnections.getSelectionIndex());
                IDialogSettings settings = (IDialogSettings) previousConnections.getData(item);
                if (settings != null) {
                    String host = settings.get(HOST);
                    String port = settings.get(PORT);
                    String username = settings.get(USERNAME);
                    String password = settings.get(PASSWORD);
                    boolean savedPassword = settings.getBoolean(SAVE_PASSWORD);
                    populateWidgets(host, port, username, password, savedPassword );
                    UserHostPage.this.password.setFocus();
                }else{
                    populateWidgets("", defaultPort, "", "", false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
                previousConnections.setToolTipText(item);
            }

        });
    }

    private void populateWidgets( String host2, String port2, String username2, String password2, boolean savePassword2 ) {
        host.setText(nullToString(host2));
        port.setText(nullToString(port2));
        username.setText(nullToString(username2));
        password.setText(nullToString(password2));
        savePassword.setSelection(savePassword2);
        
    }

    private String nullToString( String string ) {
        if (string == null) {
            return ""; //$NON-NLS-1$
        }
        return string;
    }

    private void populatePreviousConnections( Combo previousConnections ) {
        IDialogSettings previous = getDialogSettings().getSection(PREVIOUS_CONNECTIONS);
        if (previous != null) {

            IDialogSettings[] sections = previous.getSections();
            Arrays.sort(sections, new Comparator<IDialogSettings>(){

                public int compare( IDialogSettings o1, IDialogSettings o2 ) {
                    
                    long time1 = o1.getLong(TIMESTAMP);
                    long time2 = o2.getLong(TIMESTAMP);
                    if (time1 > time2) {
                        return -1;
                    }
                    return 1;
                }

            });
            List<String> items = new ArrayList<String>(sections.length);
            items.add(""); //$NON-NLS-1$
            for( IDialogSettings connection : sections ) {
                if( !connection.getBoolean(DELETED) ) {
                    StringBuilder name = new StringBuilder();
                    name.append(connection.get(USERNAME));
                    name.append('@');
                    name.append(connection.get(HOST));
                    name.append(':');
                    name.append(connection.get(PORT));
    
                    previousConnections.setData(name.toString(), connection);
                    items.add(name.toString());
                }
            }

            previousConnections.setItems(items.toArray(new String[0]));
        }
    }
    @Override
    protected IDialogSettings getDialogSettings() {
        return dialect.getDialogSetting();
    }

    private Text createLabelAndText( Composite parent, String labelText, int textStyle ) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(labelText);

        final Text text = new Text(parent, textStyle);

        final ControlDecoration decoration = new ControlDecoration(text, SWT.TOP | SWT.LEFT);
        ImageRegistry imageRegistry = CatalogUIPlugin.getDefault().getImageRegistry();
        Image image = imageRegistry.get(REQUIRED_DECORATION);
        if (image == null) {
            image = CatalogUIPlugin.getDefault().getImageDescriptor(ISharedImages.WARNING_OVR).createImage();
            imageRegistry.put(REQUIRED_DECORATION, image);
        }
        decoration.setImage(image);
        decoration.setDescriptionText(localization.requiredField);
        decoration.show();

        text.addListener(SWT.Modify, new Listener(){

            public void handleEvent( Event event ) {
                getContainer().updateButtons();
                boolean valid = isValid(text);
                if (text.getText().trim().equals("") && valid) { //$NON-NLS-1$
                    decoration.show();
                } else {
                    decoration.hide();
                }
            }

        });

        return text;
    }

    /**
     * Performs a validation on the text passed in. Right now only the port has any restrictions
     * 
     * @param text
     * @return true if the value in the text is valid
     */
    private boolean isValid( Text text ) {
        if (text == port) {
            try {
                int i = Integer.parseInt(text.getText());
                if (i > 0) {
                    if (localization.portError.equals(getErrorMessage())) {
                        setErrorMessage(null);
                    }
                    return true;
                }
            } catch (NumberFormatException e) {
                // not good but failure is handled out side this catch
            }
            if (!localization.portError.equals(getErrorMessage())) {
                setErrorMessage(localization.portError);
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean isPageComplete() {
        boolean enteredEntries = port.getText().trim().length() > 0
                && host.getText().trim().length() > 0 && username.getText().trim().length() > 0;
        boolean validEntries = isValid(port) && isValid(host) && isValid(username)
                && isValid(password);
        return enteredEntries && validEntries;
    }

    @Override
    public void shown() {
        populatePreviousConnections(previousConnections);
    }
    
    @Override
    public boolean leavingPage() {

        try {
            if (!canConnect()) {
                return false;
            } else {
                setErrorMessage(null);
                IDialogSettings section = getDialogSettings().getSection(PREVIOUS_CONNECTIONS);
                if (section == null) {
                    section = getDialogSettings().addNewSection(PREVIOUS_CONNECTIONS);
                }

                IDialogSettings[] allSections = section.getSections();
                IDialogSettings match = findMatchForCurrent(allSections);
                if (match != null) {
                    if( savePassword.getSelection() ){
                        String storedPass = match.get(PASSWORD);
                        if( !password.getText().equals(storedPass) ){
                            if( storedPass==null || storedPass.length()==0 ){
                                match.put(PASSWORD, password.getText());
                            }else {
                                boolean choice = MessageDialog.openConfirm(getShell(), localization.password, localization.changePasswordQuery);
                                if( choice ){
                                    match.put(PASSWORD, password.getText());                                    
                                }
                            }
                        }
                        
                    }else{
                        match.put(PASSWORD, ""); //$NON-NLS-1$
                    }
                    
                    match.put(SAVE_PASSWORD, savePassword.getSelection());
                    match.put(TIMESTAMP, System.currentTimeMillis());
                } else {
                    IDialogSettings params = section.addNewSection(allSections.length + 1 + ""); //$NON-NLS-1$
                    params.put(HOST, host.getText());
                    params.put(PORT, port.getText());
                    params.put(USERNAME, username.getText());
                    params.put(TIMESTAMP, System.currentTimeMillis());
                    params.put(SAVE_PASSWORD, savePassword.getSelection());
                    if ( savePassword.getSelection() ){
                        params.put(PASSWORD, password.getText());
                    }
                }

                return true;
            }
        } catch (InterruptedException e) {
            setMessage(localization.databaseConnectionInterrupted);
            return false;
        }
    }

    private boolean canConnect() throws InterruptedException {
        String host2 = nullToString(host.getText());
        String port2 = nullToString(port.getText());
        String username2 = nullToString(username.getText());
        String password2 = nullToString(password.getText());
        try {
            DatabaseConnectionRunnable runnable = dialect.createDatabaseConnectionRunnable(host2, Integer.parseInt(port2),
                    username2, password2);
            getContainer().run(false, true, runnable);
            String connect = runnable.canConnect();
            if (connect != null) {
                setErrorMessage(connect);
            } else {
                setErrorMessage(null);
                databaseNames  = runnable.getDatabaseNames();
            }
            return connect == null;
        } catch (InvocationTargetException e) {
            setErrorMessage(localization.unexpectedError+":" + e); //$NON-NLS-1$
            dialect.log("Error while running database connection runnable", e); //$NON-NLS-1$
            return false;
        }
    }
    /**
     * Searches the settings for a match to the current settings in the wizard
     * 
     * @return the match or null;
     */
    private IDialogSettings findMatchForCurrent( IDialogSettings[] allSections ) {
        for( IDialogSettings dialogSettings : allSections ) {
            boolean sameHost = host.getText().equalsIgnoreCase(dialogSettings.get(HOST));
            boolean samePort = Integer.parseInt(port.getText()) == dialogSettings.getInt(PORT);
            boolean sameUser = username.getText().equals(dialogSettings.get(USERNAME));

            if (sameHost && samePort && sameUser) {
                return dialogSettings;
            }
        }
        return null;
    }

    /**
     * Returns the database names looked up when the next button is pressed.  
     * <p>
     * NOTE:  This can therefore only be called after the next button has been pressed.
     * </p>
     *
     * @return the database names looked up when the next button is pressed.
     */
    public String[] getDatabaseNames() {
        return databaseNames;
    }

    /**
     * Returns the host parameter entered into this page.
     *
     * @return the host parameter entered into this page.
     */
    public String getHost() {
        return host.getText();
    }
    /**
     * Returns the port parameter entered into this page.
     *
     * @return the port parameter entered into this page.
     */
    public Integer getPort() {
        return Integer.parseInt(port.getText());
    }
    /**
     * Returns the username parameter entered into this page.
     *
     * @return the username parameter entered into this page.
     */
    public String getUsername() {
        return username.getText();
    }
    /**
     * Returns the password parameter entered into this page.
     *
     * @return the password parameter entered into this page.
     */
    public String getPassword() {
        return password.getText();
    }

	public Map<String, Serializable> addParams() {
		Map<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(dialect.hostParam.key, getHost());
        params.put(dialect.portParam.key, getPort());
        params.put(dialect.usernameParam.key, getUsername());
        params.put(dialect.passwordParam.key, getPassword());

        if(extraParams!=null) {
        	params.putAll(extraParams.getParams());
        }
        
        return params;
	}
}
