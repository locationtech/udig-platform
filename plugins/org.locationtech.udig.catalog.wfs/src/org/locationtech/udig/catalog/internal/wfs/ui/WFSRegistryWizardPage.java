/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2013, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wfs.ui;

import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.internal.wfs.WFSServiceImpl;
import org.locationtech.udig.catalog.internal.wfs.WfsPlugin;
import org.locationtech.udig.catalog.ui.UDIGConnectionPage;
import org.locationtech.udig.catalog.ui.wizard.DataStoreWizardPage;
import org.locationtech.udig.catalog.ui.workflow.EndConnectionState;
import org.locationtech.udig.catalog.wfs.internal.Messages;
import org.locationtech.udig.core.RecentHistory;

/**
 * Data page responsible for acquiring WFS services.
 * <p>
 * Responsibilities:
 * <ul>
 * <li>defaults based on selection - for URL, WFSService, and generic IService (from search)
 * <li>remember history in dialog settings
 * <li>complete list here:
 * <a href="http://udig.refractions.net/confluence/display/DEV/UDIGImportPage+Checklist">Import Page
 * Checklist</a>
 * </ul>
 * </p>
 * <p>
 * This page is used in the Import and Add Layer wizards.
 * </p>
 *
 * @since 1.0.0
 */
public class WFSRegistryWizardPage extends DataStoreWizardPage
        implements ModifyListener, SelectionListener, UDIGConnectionPage {

    protected Combo urlCombo = null;

    private Button advancedTag = null;

    private Composite advanced = null;

    protected Button getDefault;

    protected Button postDefault;

    protected Text bufferText;

    protected Text timeoutText;

    protected static final String timeoutDefault = "3"; //$NON-NLS-1$

    protected static final String bufferDefault = "100"; //$NON-NLS-1$

    /**
     * MaxFeatures parameters, 0 means no limit
     */
    protected static final String maxFeaturesDefault = "0"; //$NON-NLS-1$

    private IDialogSettings settings;

    private static final String WFS_WIZARD_ID = "WFSWizard"; //$NON-NLS-1$

    private static final String WFS_RECENTLY_USED_ID = "RecentlyUsed"; //$NON-NLS-1$

    private String url = ""; //$NON-NLS-1$

    WFSConnectionFactory wfsConnFactory = new WFSConnectionFactory();

    public WFSRegistryWizardPage(String name) {
        super(name);
        settings = WfsPlugin.getDefault().getDialogSettings().getSection(WFS_WIZARD_ID);
        if (settings == null) {
            settings = WfsPlugin.getDefault().getDialogSettings().addNewSection(WFS_WIZARD_ID);
        }
    }

    public WFSRegistryWizardPage() {
        this(""); //$NON-NLS-1$
    }

    public String getId() {
        return "org.locationtech.udig.catalog.ui.wfs"; //$NON-NLS-1$
    }

    /**
     * Can be called during createControl
     */
    protected Map<String, Serializable> defaultParams() {
        IStructuredSelection selection = (IStructuredSelection) PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getSelectionService().getSelection();
        Map<String, Serializable> toParams = toParams(selection);
        if (!toParams.isEmpty()) {
            return toParams;
        }

        WFSConnectionFactory connectionFactory = new WFSConnectionFactory();
        Map<String, Serializable> params = connectionFactory
                .createConnectionParameters(getState().getWorkflow().getContext());
        if (params != null)
            return params;

        return Collections.emptyMap();
    }

    /**
     * Retrieve "best" WFS guess of parameters based on provided context
     */
    protected Map<String, Serializable> toParams(IStructuredSelection context) {
        if (context == null) {
            // lets go with the defaults then
            return Collections.emptyMap();
        }
        for (Iterator itr = context.iterator(); itr.hasNext();) {
            Map<String, Serializable> params = wfsConnFactory
                    .createConnectionParameters(itr.next());
            if (!params.isEmpty())
                return params;
        }
        return Collections.emptyMap();
    }

    /**
     * TODO summary sentence for createControl ...
     *
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @param parent
     */
    @Override
    public void createControl(Composite arg0) {
        Composite composite = new Group(arg0, SWT.NULL);
        composite.setLayout(new GridLayout(2, false));

        // add URL
        Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.WFSRegistryWizardPage_label_url_text);
        label.setToolTipText(Messages.WFSRegistryWizardPage_label_url_tooltip);
        label.setLayoutData(new GridData(SWT.END, SWT.DEFAULT, false, false));

        String[] temp = settings.getArray(WFS_RECENTLY_USED_ID);
        if (temp == null) {
            temp = new String[0];
        }
        List<String> recent = Arrays.asList(temp);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.widthHint = 400;

        // For Drag 'n Drop as well as for general selections
        // look for a URL as part of the selection
        Map<String, Serializable> params = defaultParams(); // based on selection
        URL selectedURL;
        try {
            selectedURL = WFSDataStoreFactory.URL.lookUp(params);
        } catch (IOException e) {
            selectedURL = null;
        }

        urlCombo = new Combo(composite, SWT.BORDER);
        urlCombo.setItems(recent.toArray(new String[recent.size()]));
        urlCombo.setVisibleItemCount(15);
        urlCombo.setLayoutData(gridData);
        if (selectedURL != null) {
            urlCombo.setText(selectedURL.toExternalForm());
        } else if (url != null && url.length() != 0) {
            urlCombo.setText(url);
        } else {
            urlCombo.setText("http://"); //$NON-NLS-1$
        }
        urlCombo.addModifyListener(this);

        // add spacer
        label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 3));

        advancedTag = new Button(composite, SWT.CHECK);
        advancedTag.setLayoutData(new GridData(SWT.CENTER, SWT.DEFAULT, false, false));
        advancedTag.setSelection(false);
        advancedTag.addSelectionListener(this);
        advancedTag.setText(Messages.WFSRegistryWizardPage_advanced_text);
        advancedTag.setToolTipText(Messages.WFSRegistryWizardPage_advanced_tooltip);

        label = new Label(composite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.DEFAULT, SWT.DEFAULT, false, false));

        advanced = createAdvancedControl(composite);
        advanced.setLayoutData(new GridData(SWT.CENTER, SWT.DEFAULT, true, true, 2, 1));

        setControl(composite);
        setPageComplete(true);

        urlCombo.addModifyListener(this);

        Display.getCurrent().asyncExec(new Runnable() {
            @Override
            public void run() {

                EndConnectionState currentState = getState();
                Map<IService, Throwable> errors = currentState.getErrors();
                if (errors != null && !errors.isEmpty()) {
                    for (Map.Entry<IService, Throwable> entry : errors.entrySet()) {
                        if (entry.getKey() instanceof WFSServiceImpl) {
                            Throwable value = entry.getValue();
                            if (value instanceof ConnectException) {
                                setErrorMessage(
                                        Messages.WFSRegistryWizardPage_serverConnectionProblem);
                            } else {
                                String message = Messages.WFSRegistryWizardPage_problemConnecting
                                        + value.getLocalizedMessage();
                                setErrorMessage(message);
                            }
                        }
                    }
                }

            }
        });

    }

    private Composite createAdvancedControl(Composite arg0) {
        advanced = new Group(arg0, SWT.BORDER);
        advanced.setLayout(new GridLayout(2, false));

        // get
        Label label = new Label(advanced, SWT.NONE);
        label.setText("GET"); //$NON-NLS-1$
        label.setToolTipText(Messages.WFSRegistryWizardPage_label_get_tooltip);
        label.setLayoutData(new GridData(SWT.CENTER, SWT.DEFAULT, false, false));

        getDefault = new Button(advanced, SWT.CHECK);
        getDefault.setLayoutData(new GridData(SWT.CENTER, SWT.DEFAULT, false, false));
        getDefault.setSelection(false);
        getDefault.addSelectionListener(this);

        // post
        label = new Label(advanced, SWT.NONE);
        label.setText("POST"); //$NON-NLS-1$
        label.setToolTipText(Messages.WFSRegistryWizardPage_label_post_tooltip);
        label.setLayoutData(new GridData(SWT.CENTER, SWT.DEFAULT, false, false));

        postDefault = new Button(advanced, SWT.CHECK);
        postDefault.setLayoutData(new GridData(SWT.CENTER, SWT.DEFAULT, false, false));
        postDefault.setSelection(false);
        postDefault.addSelectionListener(this);

        // add spacer
        label = new Label(advanced, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 3));

        // buffer
        label = new Label(advanced, SWT.NONE);
        label.setText(Messages.WFSRegistryWizardPage_label_buffer_text);
        label.setToolTipText(Messages.WFSRegistryWizardPage_label_buffer_tooltip);
        label.setLayoutData(new GridData(SWT.CENTER, SWT.DEFAULT, false, false));

        bufferText = new Text(advanced, SWT.BORDER | SWT.RIGHT);
        bufferText.setLayoutData(new GridData(GridData.FILL, SWT.DEFAULT, true, false));
        bufferText.setText(bufferDefault);
        bufferText.setTextLimit(5);
        bufferText.addModifyListener(this);

        // timeout
        label = new Label(advanced, SWT.NONE);
        label.setText(Messages.WFSRegistryWizardPage_label_timeout_text);
        label.setToolTipText(Messages.WFSRegistryWizardPage_label_timeout_tooltip);
        label.setLayoutData(new GridData(SWT.CENTER, SWT.DEFAULT, false, false));

        timeoutText = new Text(advanced, SWT.BORDER | SWT.RIGHT);
        timeoutText.setLayoutData(new GridData(GridData.FILL, SWT.DEFAULT, true, false, 1, 1));
        timeoutText.setText(timeoutDefault);
        timeoutText.setTextLimit(5);
        timeoutText.addModifyListener(this);

        advanced.setVisible(false);

        return advanced;
    }

    @Override
    public void setErrorMessage(String newMessage) {
        WizardPage page = (WizardPage) getContainer().getCurrentPage();
        page.setErrorMessage(newMessage);
    }

    @Override
    public void setMessage(String newMessage) {
        WizardPage page = (WizardPage) getContainer().getCurrentPage();
        page.setMessage(newMessage);
    }

    @Override
    public void setMessage(String newMessage, int messageType) {
        WizardPage page = (WizardPage) getContainer().getCurrentPage();
        page.setMessage(newMessage, messageType);
    }

    @Override
    public EndConnectionState getState() {
        return (EndConnectionState) super.getState();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (red != null)
            red.dispose();
    }

    @Override
    public void modifyText(ModifyEvent e) {
        if (e.widget != null && e.widget instanceof Text)
            ((Text) e.widget).setForeground(null);
        if (e.widget == urlCombo) {
            ((Combo) e.widget).setForeground(null);
            setErrorMessage(null);
            url = urlCombo.getText();
        }
        getContainer().updateButtons();
    }

    /**
     * TODO summary sentence for widgetSelected ...
     *
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     * @param e
     */
    @Override
    public void widgetSelected(SelectionEvent e) {
        Button b = (Button) e.widget;
        if (b.equals(getDefault)) {
            // allow get was clicked
            if (getDefault.getSelection() && postDefault.getSelection()) {
                postDefault.setSelection(false);
            }
        } else {
            if (b.equals(postDefault)) {
                if (postDefault.getSelection() && getDefault.getSelection()) {
                    getDefault.setSelection(false);
                }
            } else {
                if (b.equals(advancedTag)) {
                    advanced.setVisible(advancedTag.getSelection());
                }
            }
        }
        getWizard().getContainer().updateButtons();
    }

    /**
     * Double click in list, or return from URL control.
     *
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     * @param e
     */
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        if (getWizard().canFinish()) {
            getWizard().performFinish();
        }
    }

    private Color red;

    @Override
    public boolean isPageComplete() {
        Map<String, Serializable> params = getParams();
        if (params == null)
            return false;
        URL url = (URL) params.get(WFSDataStoreFactory.URL.key);
        String trim = url.getHost().trim();
        if (trim.length() == 0)
            return false;
        return factory.canProcess(params);
    }

    private WFSDataStoreFactory factory = new WFSDataStoreFactory();

    public List<URL> getURLs() {
        return null;
    }

    @Override
    public Map<String, Serializable> getParams() {
        Map<String, Serializable> dsParams = new HashMap<>();
        boolean error = false;
        try {
            URL u = new URL(urlCombo.getText());
            dsParams.put(WFSDataStoreFactory.URL.key, u);
        } catch (Exception e) {
            if (red == null)
                red = new Color(null, 255, 0, 0);
            urlCombo.setForeground(red);
            error = true;
        }

        if (postDefault.getSelection()) {
            dsParams.put(WFSDataStoreFactory.PROTOCOL.key, Boolean.TRUE);
        } else {
            if (getDefault.getSelection()) {
                dsParams.put(WFSDataStoreFactory.PROTOCOL.key, Boolean.FALSE);
            } else {
                dsParams.put(WFSDataStoreFactory.PROTOCOL.key, null);
            }
        }

        String timeout = timeoutText.getText();
        if (!WFSRegistryWizardPage.timeoutDefault.equals(timeout)) {
            // parse the string
            Integer sec = null;
            try {
                sec = Integer.valueOf(timeout);
            } catch (NumberFormatException e) {
                if (red == null)
                    red = new Color(null, 255, 0, 0);
                timeoutText.setForeground(red);
                error = true;
            }
            dsParams.put(WFSDataStoreFactory.TIMEOUT.key, Integer.valueOf(sec.intValue() * 1000));
        }

        String buffer = bufferText.getText();
        if (!WFSRegistryWizardPage.bufferDefault.equals(buffer)) {
            // parse the string
            Integer sec = null;
            try {
                sec = Integer.valueOf(buffer);
            } catch (NumberFormatException e) {
                if (red == null)
                    red = new Color(null, 255, 0, 0);
                bufferText.setForeground(red);
                error = true;
            }
            dsParams.put(WFSDataStoreFactory.BUFFER_SIZE.key, sec);
        }

        return error ? null : dsParams;
    }

    /**
     * @see org.locationtech.udig.catalog.ui.UDIGImportPage#getResources(org.eclipse.core.runtime.
     *      IProgressMonitor)
     */
    public List<IService> getResources(IProgressMonitor monitor) throws Exception {
        if (!isPageComplete())
            return null;

        List<IService> list = CatalogPlugin.getDefault().getServiceFactory()
                .createService(getParams());
        saveWidgetValues();
        return list;
    }

    /**
     * TODO summary sentence for getDataStoreFactorySpi ...
     *
     * @see org.locationtech.udig.catalog.internal.ui.datastore.DataStoreWizard#getDataStoreFactorySpi()
     * @return
     */
    @Override
    protected DataStoreFactorySpi getDataStoreFactorySpi() {
        return factory;
    }

    /**
     * Saves the widget values
     */
    private void saveWidgetValues() {
        if (settings != null) {
            RecentHistory<String> recent = new RecentHistory<>(
                    settings.getArray(WFS_RECENTLY_USED_ID));
            recent.add(urlCombo.getText());
            settings.put(WFS_RECENTLY_USED_ID, recent.toArray(new String[recent.size()]));
        }
    }

}
