/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.browser.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.locationtech.udig.browser.BrowserPlugin;
import org.locationtech.udig.browser.ExternalCatalogueImportPage;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author mleslie
 * @since 1.0.0
 */
public class URLImportPage extends WizardPage 
        implements ModifyListener, ExternalCatalogueImportPage {
    
    private static String URL_WIZARD = "URL_WIZARD"; //$NON-NLS-1$
    private static String URL_RECENT = "URL_RECENT"; //$NON-NLS-1$
    private static int RECENT_COUNT = 10;
    private URL url;
    
    private Combo urlCombo;
    private Label urlLabel;
    private IDialogSettings settings;
    private ImageDescriptor icon;
    private LocationListener listen;
    private String emptyOption = "http://"; //$NON-NLS-1$
    private String viewName;
    
    /**
     * 
     */
    public URLImportPage() {
        super("External Catalog by URL"); //$NON-NLS-1$
        settings = BrowserPlugin.getDefault().getDialogSettings().getSection(URL_WIZARD);
        if (settings == null) {
            settings = BrowserPlugin.getDefault().getDialogSettings().addNewSection(URL_WIZARD);
        }
    }

    public String getID() {
        return "org.locationtech.udig.browser.URL"; //$NON-NLS-1$
    }

    public URL getURL() {
        return this.url;
    }

    public void modifyText( ModifyEvent e ) {
        if(isPageComplete()) {
            setErrorMessage(null);
            getWizard().getContainer().updateButtons();
        } else {
            setErrorMessage("Invalid URL"); //$NON-NLS-1$
        }
    }
    
    public boolean canFlipToNextPage() {
        return false;
    }
    
    public boolean isPageComplete() {
        try {
            String urlString = urlCombo.getText();
            this.url = new URL(urlString);
        } catch (MalformedURLException exception) {
            return false;
        }
        updateSettings();
        return true;
    }
    
    private void updateSettings() {
        List<String> list = new LinkedList<String>();
        String[] recents = urlCombo.getItems();
        String selected = urlCombo.getText();
        if(selected == null || selected.length() == 0) {
            this.settings.put(URL_RECENT, recents);
            return;
        }
        list.add(selected);
        int count = 1;
        for(String string : recents) {
            if(!string.equals(selected) && !string.equals(this.emptyOption)) {
                list.add(string);
                count++;
            }
            if(count > RECENT_COUNT)
                break;
        }
        this.settings.put(URL_RECENT, list.toArray(new String[0]));
    }

    public void createControl(Composite parent) {
        String[] recent = settings.getArray(URL_RECENT);
        if(recent == null) {
            recent = new String[] {this.emptyOption};
        }
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        composite.setLayout(layout);
        urlLabel = new Label(composite, SWT.NONE);
        urlLabel.setText("Enter the URL of the catalog to connect to:"); //$NON-NLS-1$
        urlLabel.setLayoutData(data);
        urlCombo = new Combo(composite, SWT.DROP_DOWN);
        urlCombo.setLayoutData(data);
        urlCombo.addModifyListener(this);
        urlCombo.setItems(recent);
        urlCombo.select(0);
        composite.setTabList(new Control[] {urlCombo});
        composite.setFocus();
        setControl(composite);
    }

    public void setIconDescriptor( ImageDescriptor descIcon ) {
        this.icon = descIcon;
    }

    public ImageDescriptor getIconDescriptor() {
        return this.icon;
    }

    public LocationListener getListener() {
        return this.listen;
    }

    public void setListener(LocationListener listen) {
        this.listen = listen;
    }

    public void setViewName( String viewName ) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return this.viewName;
    }
}
