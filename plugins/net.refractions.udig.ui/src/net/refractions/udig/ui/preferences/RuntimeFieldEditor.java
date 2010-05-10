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
package net.refractions.udig.ui.preferences;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.ui.ExceptionDetailsDialog;
import net.refractions.udig.ui.internal.Messages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

public final class RuntimeFieldEditor extends FieldEditor {
    private final String WORKSPACE_PATH = "WORKSPACE_PATH"; //$NON-NLS-1$
    private final String LANGUAGE = "LANGUAGE"; //$NON-NLS-1$
    private final String MEMORY = "MEMORY"; //$NON-NLS-1$
    private final String PROXYHOST = "PROXYHOST"; //$NON-NLS-1$
    private final String PROXYPORT = "PROXYPORT"; //$NON-NLS-1$

    private Text wkspaceText;
    private Combo langCombo;
    private Text memoryText;
    private String workspacePath;

    private static String[] langArray = new String[]{"de", "en", "es", "fr", "it", "ko"};
    private IPreferenceStore preferenceStore;
    private Text proxyHostText;
    private Text proxyPortText;

    public RuntimeFieldEditor( String name, String labelText, Composite parent ) {
        super(name, labelText, parent);

        preferenceStore = UiPlugin.getDefault().getPreferenceStore();
    }

    public int getNumberOfControls() {
        return 3;
    }
    @Override
    protected void doStore() {
        if (checkValues()) {
            saveValues();
        }
    }

    @Override
    protected void doLoadDefault() {
        wkspaceText.setText(getWorkspacePath());
        memoryText.setText(String.valueOf(getCurrentHeap()));
    }
    @Override
    protected void doLoad() {
        String workSpacePath = preferenceStore.getString(WORKSPACE_PATH);
        if (workSpacePath == null || workSpacePath.equals("")) {
            workSpacePath = getWorkspacePath();
        }
        wkspaceText.setText(workSpacePath);
        memoryText.setText(String.valueOf(getCurrentHeap()));
        String lang = preferenceStore.getString(LANGUAGE);
        if (lang == null || lang.equals("")) {
            Locale locale = Locale.getDefault();
            lang = locale.getLanguage();
        }
        for( int i = 0; i < langArray.length; i++ ) {
            if (lang.equals(langArray[i])) {
                langCombo.select(i);
            }
        }

        String host = preferenceStore.getString(PROXYHOST);
        if (!host.equals("")) {
            proxyHostText.setText(host);
        }
        String port = preferenceStore.getString(PROXYPORT);
        if (!port.equals("")) {
            proxyPortText.setText(port);
        }
    }

    @Override
    protected void doFillIntoGrid( final Composite parent, int numColumns ) {
        setPreferenceStore(preferenceStore);

        workspacePath = getWorkspacePath();

        // workspace
        Label wkspaceLabel = new Label(parent, SWT.NONE);
        wkspaceLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        wkspaceLabel.setText(Messages.RuntimeFieldEditor_workspace_path);

        wkspaceText = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        wkspaceText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        wkspaceText.setText(workspacePath);

        Button wkspaceButton = new Button(parent, SWT.PUSH);
        wkspaceButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        wkspaceButton.setText("..."); //$NON-NLS-1$
        wkspaceButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                DirectoryDialog fileDialog = new DirectoryDialog(parent.getShell(), SWT.OPEN);
                String path = fileDialog.open();
                if (path == null || path.length() < 1) {
                    wkspaceText.setText(""); //$NON-NLS-1$
                } else {
                    wkspaceText.setText(path);
                }
            }
        });

        // language
        Label langLabel = new Label(parent, SWT.NONE);
        langLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        langLabel.setText(Messages.RuntimeFieldEditor_locale);

        langCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData gD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gD.horizontalSpan = 2;
        langCombo.setLayoutData(gD);
        langCombo.setItems(langArray);

        // memory
        Label memoryLabel = new Label(parent, SWT.NONE);
        memoryLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        memoryLabel.setText(Messages.RuntimeFieldEditor_maxheap);

        memoryText = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        GridData gD2 = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gD2.horizontalSpan = 2;
        memoryText.setLayoutData(gD2);
        long maxHeapMemory = getCurrentHeap();
        memoryText.setText(String.valueOf(maxHeapMemory));

        // proxy
        Group proxyGroup = new Group(parent, SWT.NONE);
        GridData proxyGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        proxyGD.horizontalSpan = 3;
        proxyGroup.setLayoutData(proxyGD);
        proxyGroup.setLayout(new GridLayout(2, false));
        proxyGroup.setText("Proxy");

        Label proxyHostLabel = new Label(proxyGroup, SWT.NONE);
        proxyHostLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        proxyHostLabel.setText("Proxy Server");
        proxyHostText = new Text(proxyGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        proxyHostText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        proxyHostText.setText("");

        Label proxyPortLabel = new Label(proxyGroup, SWT.NONE);
        proxyPortLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        proxyPortLabel.setText("Proxy Port");
        proxyPortText = new Text(proxyGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        proxyPortText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        proxyPortText.setText("");

        // restart
        Button restartButton = new Button(parent, SWT.PUSH);
        restartButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        restartButton.setText(Messages.RuntimeFieldEditor_restart);
        restartButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                saveValues();
                restart();
            }
        });

        // put some defaults in
        // preferenceStore.setValue(WORKSPACE_PATH, wkspaceText.getText());
        // preferenceStore.setValue(LANGUAGE, langCombo.getText());
        // preferenceStore.setValue(MEMORY, memoryText.getText());

    }

    private String getWorkspacePath() {
        URL instanceUrl = Platform.getInstanceLocation().getURL();
        return new File(instanceUrl.getFile()).toString();
    }

    private long getCurrentHeap() {
        try {
            int maxHeapSize = UiPlugin.getMaxHeapSize();
            return maxHeapSize;
        } catch (IOException e) {
            e.printStackTrace();
            return Runtime.getRuntime().maxMemory() / 1024l / 1024l;
        }
    }

    private boolean checkValues() {
        String wksPath = wkspaceText.getText();
        File f = new File(wksPath);
        if (!f.exists()) {
            MessageDialog.openError(wkspaceText.getShell(), Messages.RuntimeFieldEditor_error,
                    Messages.RuntimeFieldEditor_path_not_existing);
            return false;
        }

        String memory = memoryText.getText();
        int mem = 0;
        try {
            mem = Integer.parseInt(memory);
        } catch (Exception e) {
            // checked in the next statement
        }
        if (mem < 64) {
            MessageDialog.openError(wkspaceText.getShell(), Messages.RuntimeFieldEditor_error,
                    Messages.RuntimeFieldEditor_memory_positive);
            return false;
        }

        return true;
    }

    private void saveValues() {
        if (checkValues()) {
            preferenceStore.setValue(WORKSPACE_PATH, wkspaceText.getText());
            preferenceStore.setValue(LANGUAGE, langCombo.getText());
            preferenceStore.setValue(MEMORY, memoryText.getText());
            preferenceStore.setValue(PROXYHOST, proxyHostText.getText());
            preferenceStore.setValue(PROXYPORT, proxyPortText.getText());
        }
    }

    private void restart() {
        try {
            String maxHeadSize = memoryText.getText();
            UiPlugin.setMaxHeapSize(maxHeadSize);

            String host = proxyHostText.getText();
            String port = proxyPortText.getText();
            UiPlugin.setProxy(host, port);

            URL configUrlURL = Platform.getConfigurationLocation().getURL();

            String configFilePath = configUrlURL.getFile() + File.separator + "config.ini"; //$NON-NLS-1$
            File configFile = new File(configFilePath);

            // language and path go in the config.ini file
            Properties properties = new Properties();
            properties.load(new FileInputStream(configFile));

            String path = wkspaceText.getText();
            path = new File(path).toURI().toURL().toExternalForm();
            path = path.replaceAll("%20", " "); //$NON-NLS-1$ //$NON-NLS-2$
            properties.setProperty("osgi.instance.area.default", path); //$NON-NLS-1$
            properties.setProperty("osgi.nl", langCombo.getText()); //$NON-NLS-1$

            Set<Object> keySet = properties.keySet();
            BufferedWriter bW = new BufferedWriter(new FileWriter(configFile));

            for( Object key : keySet ) {
                String keyStr = (String) key;
                if (!keyStr.equals("eof")) { //$NON-NLS-1$
                    bW.write(keyStr);
                    bW.write("="); //$NON-NLS-1$
                    bW.write(properties.getProperty(keyStr));
                    bW.write("\n"); //$NON-NLS-1$
                }
            }
            bW.write("eof=eof"); //$NON-NLS-1$
            bW.close();
        } catch (IOException e) {
            e.printStackTrace();
            String message = "An error occurred while setting preferences.";
            ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, UiPlugin.ID, e);
        }

        PlatformUI.getWorkbench().restart();
    }

    @Override
    protected void adjustForNumColumns( int numColumns ) {
    }
}