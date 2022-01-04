/**
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com
 * (C) 2009 IBM Corporation and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.catalog.jgrass.core;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.catalog.jgrass.JGrassPlugin;
import org.locationtech.udig.catalog.jgrass.messages.Messages;
import org.locationtech.udig.catalog.ui.UDIGConnectionPage;
import org.locationtech.udig.core.RecentHistory;

/**
 * <p>
 * Data page responsible for acquiring a JGrass database
 * </p>
 * <p>
 * <i>Note: based on the WMS plugin</i>
 * </p>
 *
 * @author Andrea Antonello - www.hydrologis.com
 * @since 1.1.0
 */
public class JGrassWizardPage extends WizardPage implements ModifyListener, UDIGConnectionPage {

    private String url = ""; //$NON-NLS-1$

    private static final String JGRASS_WIZARD = "JGRASS_WIZARD"; //$NON-NLS-1$

    private static final String JGRASS_RECENT = "JGRASS_RECENT"; //$NON-NLS-1$

    private IDialogSettings settings = null;

    private static final int COMBO_HISTORY_LENGTH = 15;

    private Combo urlCombo = null;

    private List<IService> jgrassServices;

    /**
     * Construct <code>JGrassWizardPage</code>.
     *
     * @param pageName
     */
    public JGrassWizardPage() {
        super(Messages.getString("jgrasswizard.dbimport")); //$NON-NLS-1$

        settings = JGrassPlugin.getDefault().getDialogSettings().getSection(JGRASS_WIZARD);
        if (settings == null) {
            settings = JGrassPlugin.getDefault().getDialogSettings().addNewSection(JGRASS_WIZARD);
        }
    }

    public String getId() {
        return "eu.hydrologis.udig.catalog.internal.jgrass.ui.JGrass"; //$NON-NLS-1$
    }

    /**
     * Can be called during createControl
     */
    protected Map<String, Serializable> defaultParams() {
        IStructuredSelection selection = (IStructuredSelection) PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getSelectionService().getSelection();
        return toParams(selection);
    }

    /**
     * Retrieve "best" JGrass guess of parameters based on provided context
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Serializable> toParams(IStructuredSelection context) {
        if (context != null) {
            for (Iterator itr = context.iterator(); itr.hasNext();) {
                Map<String, Serializable> params = new JGrassConnectionFactory()
                        .createConnectionParameters(itr.next());
                if (!params.isEmpty())
                    return params;
            }
        }
        return Collections.EMPTY_MAP;
    }

    @Override
    public void createControl(Composite parent) {
        String[] recentJGrass = settings.getArray(JGRASS_RECENT);
        if (recentJGrass == null) {
            recentJGrass = new String[0];
        }

        GridData gridData;
        Composite composite = new Composite(parent, SWT.NULL);

        GridLayout gridLayout = new GridLayout();
        int columns = 2;
        gridLayout.numColumns = columns;
        composite.setLayout(gridLayout);

        gridData = new GridData();

        Label urlLabel = new Label(composite, SWT.NONE);
        urlLabel.setText(Messages.getString("jgrasswizard.selectfolder")); //$NON-NLS-1$
        urlLabel.setLayoutData(gridData);
        // placeholder
        new Label(composite, SWT.NONE);

        // For Drag 'n Drop as well as for general selections
        // look for a URL as part of the selection
        Map<String, Serializable> params = defaultParams(); // based on

        // combo selection
        gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.widthHint = 400;
        urlCombo = new Combo(composite, SWT.BORDER);
        urlCombo.setItems(recentJGrass);
        urlCombo.setVisibleItemCount(15);
        urlCombo.setLayoutData(gridData);

        URL selectedURL = getURL(params);
        if (selectedURL != null) {
            File urlToFile = URLUtils.urlToFile(selectedURL);
            urlCombo.setText(urlToFile.getAbsolutePath());
            url = urlToFile.getAbsolutePath();
            setPageComplete(true);
        } else if (url != null && url.length() != 0) {
            urlCombo.setText(url);
            try {
                getResources(new NullProgressMonitor());
                setPageComplete(true);
            } catch (Exception e1) {
                e1.printStackTrace();
                setPageComplete(false);
            }
        } else {
            url = null;
            urlCombo.setText("insert path here"); //$NON-NLS-1$
            setPageComplete(false);
        }
        urlCombo.addModifyListener(this);

        // browse button
        Button browseButton = new Button(composite, SWT.PUSH);
        browseButton.setText(Messages.getString("jgrasswizard.browse")); //$NON-NLS-1$
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog directoryDialog = new DirectoryDialog(
                        Display.getDefault().getActiveShell());
                directoryDialog.setText(Messages.getString("jgrasswizard.choosefolder")); //$NON-NLS-1$
                String selectedDirectory = directoryDialog.open();
                urlCombo.setText(selectedDirectory);
                url = selectedDirectory;
                try {
                    getResources(new NullProgressMonitor());
                    setPageComplete(true);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    setPageComplete(false);
                }
            }
        });
        gridData = new GridData();
        gridData.widthHint = 150;
        browseButton.setLayoutData(gridData);

        setControl(composite);
    }

    public URL getURL(Map<String, Serializable> params) {
        Object value = params.get(JGrassServiceExtension.KEY);
        if (value == null)
            return null;
        if (value instanceof URL)
            return (URL) value;
        if (value instanceof String) {
            try {
                URL url = new File((String) value).toURI().toURL();
                return url;
            } catch (MalformedURLException erp) {
            }
        }
        return null;
    }

    /**
     * Double click in list, or return from url control.
     *
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     * @param e
     */
    public void widgetDefaultSelected(SelectionEvent e) {
        e.getClass();// kill warning
        if (getWizard().canFinish()) {
            getWizard().performFinish();
        }
    }

    /**
     * This should be called using the Wizard .. job when next/finish is pressed.
     */
    public List<IService> getResources(IProgressMonitor monitor) throws Exception {
        URL location = new File(url).toURI().toURL();

        JGrassServiceExtension creator = new JGrassServiceExtension();

        Map<String, Serializable> params = creator.createParams(location);
        IService service = creator.createService(location, params);
        service.getInfo(monitor); // load it

        jgrassServices = new ArrayList<>();
        jgrassServices.add(service);

        /**
         * Success! Store the URL in history.
         */
        saveWidgetValues();

        return jgrassServices;
    }

    @Override
    public void modifyText(ModifyEvent e) {
        url = ((Combo) e.getSource()).getText();
        try {
            getResources(new NullProgressMonitor());
            setPageComplete(true);
            setErrorMessage(null);
        } catch (Exception e1) {
            setErrorMessage(e1.getLocalizedMessage());
            e1.printStackTrace();
            setPageComplete(false);
        }

        getWizard().getContainer().updateButtons();
    }

    /**
     * Saves the widget values
     */
    private void saveWidgetValues() {
        if (settings != null) {
            RecentHistory<String> recent = new RecentHistory<>(
                    settings.getArray(JGRASS_RECENT));
            recent.add(url);
            settings.put(JGRASS_RECENT, recent.toArray(new String[recent.size()]));
        }
    }

    @Override
    public Map<String, Serializable> getParams() {
        try {
            if (url == null)
                return Collections.emptyMap();

            URL location = new File(url).toURI().toURL();

            JGrassServiceExtension creator = new JGrassServiceExtension();
            return creator.createParams(location);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public Collection<URL> getResourceIDs() {
        try {
            ArrayList<URL> l = new ArrayList<>();
            l.add(new File(url).toURI().toURL());

            return l;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public Collection<IService> getServices() {
        return jgrassServices;
    }

}
