/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.wizard.url;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.internal.ServiceFactoryImpl;
import org.locationtech.udig.catalog.ui.AbstractUDIGImportPage;
import org.locationtech.udig.catalog.ui.UDIGConnectionPage;
import org.locationtech.udig.core.RecentHistory;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;

/**
 * @author Amr Alam TODO To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Style - Code Templates
 */
public class URLWizardPage extends AbstractUDIGImportPage implements ModifyListener, UDIGConnectionPage {

    final static String[] types = {};
    /** <code>url</code> field */
    protected Combo url;
    private static final String URL_WIZARD = "URL_WIZARD"; //$NON-NLS-1$
    private static final String URL_RECENT = "URL_RECENT"; //$NON-NLS-1$
    private IDialogSettings settings;

    /**
     * Construct <code>URLWizardPage</code>.
     */
    public URLWizardPage() {
        super(Messages.URLWizardPage_title); 

        settings = ProjectUIPlugin.getDefault().getDialogSettings().getSection(URL_WIZARD);
        if (settings == null) {
            settings = ProjectUIPlugin.getDefault().getDialogSettings().addNewSection(URL_WIZARD);
        }
    }

    public String getId() {
        // TODO Auto-generated method stub
        return null;
    }
    public boolean canProcess( Object object ) {
        URL url = CatalogPlugin.locateURL(object);
        if (url == null) {
            return false;
        }
        return true;
    }

    public Map<String, Serializable> toParams( Object object ) {
        return null;
    }

    public void createControl( Composite parent ) {
        String[] recentURLs = settings.getArray(URL_RECENT);
        if (recentURLs == null) {
            recentURLs = new String[0];
        }

        GridData gridData;
        Composite composite = new Composite(parent, SWT.NULL);

        GridLayout gridLayout = new GridLayout();
        int columns = 1;
        gridLayout.numColumns = columns;
        composite.setLayout(gridLayout);

        gridData = new GridData();

        Label urlLabel = new Label(composite, SWT.NONE);
        urlLabel.setText(Messages.URLWizardPage_label_url_text); 
        urlLabel.setLayoutData(gridData);

        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.widthHint = 400;

        url = new Combo(composite, SWT.BORDER);
        url.setItems(recentURLs);
        url.setVisibleItemCount(15);
        url.setLayoutData(gridData);
        url.setText("http://"); //$NON-NLS-1$
        url.addModifyListener(this);

        setControl(composite);
        setPageComplete(true);
    }

    public boolean isPageComplete() {
        try {
            new URL(url.getText());
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

    public boolean canFlipToNextPage() {
        // return canFlip;
        IWizardPage[] pages = getWizard().getPages();
        return isPageComplete() && !pages[pages.length - 1].equals(this);
    }

    /**
     * Double click in list, or return from url control.
     * 
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     * @param e
     */
    public void widgetDefaultSelected( SelectionEvent e ) {
        e.getClass();// kill warning
        if (getWizard().canFinish()) {
            getWizard().performFinish();
        }
    }

    /**
     * This should be called using the Wizard .. job when next/finish is pressed.
     */
    public List<IService> getResources( IProgressMonitor monitor ) throws Exception {
        URL location = new URL(url.getText());
        ServiceFactoryImpl serviceFactory = new ServiceFactoryImpl();
        List<IService> services = serviceFactory.createService(location);
        /*
         * Success! Store the URL in history.
         */
        saveWidgetValues();
        return services;
    }

    public void modifyText( ModifyEvent e ) {
        try {
            new URL(url.getText());
            setErrorMessage(null);
        } catch (MalformedURLException exception) {
            setErrorMessage(Messages.URLWizardPage_error_invalidURL); 
        }
        getWizard().getContainer().updateButtons();
    }

    /**
     * Saves the widget values
     */
    private void saveWidgetValues() {
        if (settings != null) {
            RecentHistory<String> recent =
                    new RecentHistory<String>( settings.getArray(URL_RECENT) );
            recent.add( url.getText() );
            settings.put(URL_RECENT, recent.toArray(new String[recent.size()]));
        }
    }

    public Map<String, Serializable> getParams() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<URL> getURLs() {
        // TODO Auto-generated method stub
        return null;
    }
}
