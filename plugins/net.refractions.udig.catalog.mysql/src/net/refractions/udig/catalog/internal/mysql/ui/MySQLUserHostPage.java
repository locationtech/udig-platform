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
package net.refractions.udig.catalog.internal.mysql.ui;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.internal.mysql.MySQLPlugin;
import net.refractions.udig.catalog.internal.ui.Images;
import net.refractions.udig.catalog.mysql.internal.Messages;
import net.refractions.udig.catalog.ui.AbstractUDIGImportPage;
import net.refractions.udig.catalog.ui.UDIGConnectionPage;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;


/**
 * The first of a two page wizard for connecting to a mysql. This page requires the user enter
 * host, port, username and password.
 *
 * @author jesse
 * @author Harry Bullen, Intelligent Automation
 * @since 1.1.0
 */
public class MySQLUserHostPage extends AbstractUDIGImportPage implements UDIGConnectionPage {

    // /////
    // The following constants are those used to store (and retrieve) the settings from and to the
    // Dialog Settings
    // /////
    private static final String PREVIOUS_CONNECTIONS = "previous_settings";
    protected static final String TIMESTAMP = "TIMESTAMP";
    private static final String USERNAME = "USERNAME";
    private static final String HOST = "HOST";
    private static final String PORT = "PORT";
    protected static final String PASSWORD = "PASSWORD";
    private static final String SAVE_PASSWORD = "SAVE_PASSWORD";
    // / End of Dialog settings constants

    private static final String REQUIRED_DECORATION = "REQUIRED_DECORATION";
    private static final String DEFAULT_PORT = "3306";
    private static final String PORT_ERROR = "The port value must be an integer > 0";

    private Text host;
    private Text port;
    private Text password;
    private Text username;

    // this is populates when the Next button is pressed. This allows the next page (PostgisConnectionPage) to get at the databaseNames
    // without having to reconnect to the database.
    private String[] databaseNames;
    private Button savePassword;

    public MySQLUserHostPage() {
        super(Messages.MySQLWizardPage_title);
    }

    public Map<String, Serializable> getParams() {
        return null;
    }

    public void createControl( Composite parent ) {
        Composite top = new Composite(parent, SWT.NONE);
        top.setLayout(new GridLayout(4, false));
        setControl(top);

        createPreviousConnectionsCombo(top);
        host = createLabelAndText(top, "Host:", SWT.BORDER);
        host.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        port = createLabelAndText(top, "Port:", SWT.BORDER);
        port.setText(DEFAULT_PORT);
        port.setLayoutData(new GridData());

        username = createLabelAndText(top, "User Name:", SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 3;
        username.setLayoutData(data);

        password = createLabelAndText(top, "Password:", SWT.PASSWORD | SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 3;
        password.setLayoutData(data);

        this.savePassword = new Button(top, SWT.CHECK);
        this.savePassword.setText("Store Password");
    }

    private void createPreviousConnectionsCombo( Composite top ) {
        Label label = new Label(top, SWT.NONE);
        label.setText("Previous Connections");

        GridData gridData = new GridData(SWT.FILL, SWT.TOP, false, false);
        label.setLayoutData(gridData);

        final Combo previousConnections = new Combo(top, SWT.READ_ONLY);
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
                    MySQLUserHostPage.this.password.setFocus();
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
            return "";
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
            items.add("");
            for( IDialogSettings connection : sections ) {
                StringBuilder name = new StringBuilder();
                name.append(connection.get(USERNAME));
                name.append('@');
                name.append(connection.get(HOST));
                name.append(':');
                name.append(connection.get(PORT));

                previousConnections.setData(name.toString(), connection);
                items.add(name.toString());
            }

            previousConnections.setItems(items.toArray(new String[0]));
        }
    }
    @Override
    protected IDialogSettings getDialogSettings() {
        return MySQLPlugin.getDefault().getDialogSettings();
    }

    private Text createLabelAndText( Composite parent, String labelText, int textStyle ) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(labelText);

        final Text text = new Text(parent, textStyle);

        final ControlDecoration decoration = new ControlDecoration(text, SWT.TOP | SWT.LEFT);
        ImageRegistry imageRegistry = MySQLPlugin.getDefault().getImageRegistry();
        Image image = imageRegistry.get(REQUIRED_DECORATION);
        if (image == null) {
            image = Images.getDescriptor(Images.WARNING_OVR).createImage();
            imageRegistry.put(REQUIRED_DECORATION, image);
        }
        decoration.setImage(image);
        decoration.setDescriptionText("This is a required field");
        decoration.show();

        text.addListener(SWT.Modify, new Listener(){

            public void handleEvent( Event event ) {
                getContainer().updateButtons();
                boolean valid = isValid(text);
                if (text.getText().trim().equals("") && valid) {
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
                    if (PORT_ERROR.equals(getErrorMessage())) {
                        popErrorMessage();
                    }
                    return true;
                }
            } catch (NumberFormatException e) {
                // not good but failure is handled out side this catch
            }
            if (!PORT_ERROR.equals(getErrorMessage())) {
                setErrorMessage(PORT_ERROR);
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean isPageComplete() {
        boolean enteredEntries = port.getText().trim().length() > 0
                && host.getText().trim().length() > 0 && username.getText().trim().length() > 0
                && password.getText().trim().length() > 0;
        boolean validEntries = isValid(port) && isValid(host) && isValid(username)
                && isValid(password);
        return enteredEntries && validEntries;
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
                                boolean choice = MessageDialog.openConfirm(getShell(), "Modify password", "Do you want to change the saved password?");
                                if( choice ){
                                    match.put(PASSWORD, password.getText());
                                }
                            }
                        }

                    }else{
                        match.put(PASSWORD, "");
                    }

                    match.put(SAVE_PASSWORD, savePassword.getSelection());
                    match.put(TIMESTAMP, System.currentTimeMillis());
                } else {
                    IDialogSettings params = section.addNewSection(allSections.length + 1 + "");
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
            setMessage("Database connection process interrupted");
            return false;
        }
    }

    private boolean canConnect() throws InterruptedException {
        String host2 = nullToString(host.getText());
        String port2 = nullToString(port.getText());
        String username2 = nullToString(username.getText());
        String password2 = nullToString(password.getText());
        try {
            DatabaseConnectionRunnable runnable = new DatabaseConnectionRunnable(host2, port2,
                    username2, password2);
            getContainer().run(false, true, runnable);
            String connect = runnable.canConnect();
            if (connect != null) {
                setErrorMessage(connect);
            } else {
                databaseNames  = runnable.getDatabaseNames();
            }
            return connect == null;
        } catch (InvocationTargetException e) {
            setErrorMessage("Unexpected Error:" + e);
            MySQLPlugin.log("Error while running database connection runnable", e);
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
}
