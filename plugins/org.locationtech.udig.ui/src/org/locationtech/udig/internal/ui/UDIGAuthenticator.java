/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.internal.ui;

import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.core.logging.LoggingSupport;
import org.locationtech.udig.ui.PlatformGIS;
import org.osgi.service.prefs.Preferences;

/**
 * This is an Authenticator used when URL connection negotiation
 * needs to ask for the users credentials.
 * <p>
 * This implementation is written to prompt the user with SWT; and
 * to store the username/password if possible.
 * </p>
 * @since 1.0.0
 */
public class UDIGAuthenticator extends Authenticator {
    private static final String NAME = "NAME"; //$NON-NLS-1$
    private static final String PASSWORD = "PASSWORD"; //$NON-NLS-1$
    private static final String URL_AUTHENTICATION = "URL_AUTHENTICATION"; //$NON-NLS-1$
    private String username;
    private String password;
    private boolean storePassword;

    /**
     * The {@link Set} of nodeKeys that this authenticator has tried the stored username/password
     * pair for. This is to make sure that the user is asked to reenter username/password instead of
     * reusing the old invalid username/password.
     */
    private Set<String> triedStoredForNodeKey = new HashSet<>();

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        final String[] name=new String[1];
        final String[] pass=new String[1];

        // only try the stored username/password once before asking the user
        // for a new username/password.
        if (!isTriedStored()) {
            name[0] = loadName();
            pass[0] = loadPassword();
            setTriedStored(true);
        }

        if (name[0] == null && pass[0] == null) {
            //TODO check if credentials have been previously entered and remembered
            PlatformGIS.syncInDisplayThread(new Runnable(){
                @Override
                public void run() {
                    promptForPassword();
                    name[0]=username;
                    pass[0]=password;
                }
            });
        }
        if (name[0] == null && pass[0] == null) {
            return null;
        }
        if( storePassword )
            store(name[0], pass[0]);
        return new PasswordAuthentication(name[0], pass[0].toCharArray());
    }

    private boolean isTriedStored() {
        try {
            return triedStoredForNodeKey.contains(getNodeKey());
        } catch (UnsupportedEncodingException e) {
            LoggingSupport.log(UiPlugin.getDefault(), e);
            return false;
        }
    }

    private void setTriedStored(boolean mark) {
        try {
            if(mark) {
                triedStoredForNodeKey.add(getNodeKey());
            } else {
                triedStoredForNodeKey.remove(getNodeKey());
            }
        } catch (UnsupportedEncodingException e) {
            LoggingSupport.log(UiPlugin.getDefault(), e);
        }
    }

    private void store(String name, String pass) {
        try {
            Preferences node = UiPlugin.getUserPreferences().node(getNodeKey());
            node.put(NAME, name);
            node.put(PASSWORD, pass);
        } catch (Exception e) {
            LoggingSupport.log(UiPlugin.getDefault(), e);
        }
    }

    private String loadPassword() {
        try {
            Preferences node = UiPlugin.getUserPreferences().node(getNodeKey());
            String pass = node.get(PASSWORD, null);
            if( pass == null )
                return null;

            return pass;
        } catch (Exception e) {
            LoggingSupport.log(UiPlugin.getDefault(), e);
            return null;
        }
    }

    private String getNodeKey() throws UnsupportedEncodingException {
        return URL_AUTHENTICATION + URLEncoder.encode(getRequestingURL().toString(), "UTF-8"); //$NON-NLS-1$
    }

    private String loadName() {
        try {
            Preferences node = UiPlugin.getUserPreferences().node(getNodeKey());
            return node.get(NAME, null);

        } catch (Exception e) {
            LoggingSupport.log(UiPlugin.getDefault(), e);
            return null;
        }
    }

    protected void promptForPassword() {

        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        AuthenticationDialog dialog = new AuthenticationDialog(shell);
        dialog.setBlockOnOpen(true);
        int result = dialog.open();
        if (result == Window.CANCEL) {
            username = null;
            password = null;
            return;
        }
        username = dialog.getUsername();
        if (username == null) {
            username = ""; //$NON-NLS-1$
        }
        password = dialog.getPassword();
        if (password == null) {
            password = ""; //$NON-NLS-1$
        }
        storePassword = dialog.shouldRemember();
    }
}
