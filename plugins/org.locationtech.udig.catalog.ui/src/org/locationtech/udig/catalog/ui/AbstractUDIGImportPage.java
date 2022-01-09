/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.ui.workflow.EndConnectionState;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardPage;

/**
 * Abstract implementation of UDIGImportPage.
 *
 * @author jdeolive
 */
public abstract class AbstractUDIGImportPage extends WorkflowWizardPage
        implements UDIGConnectionPage {

    public AbstractUDIGImportPage(String pageName) {
        super(pageName);
    }

    /**
     * Sets a Message on the wizard page. Since these pages are decorated by a connection page the
     * default implementation Fails
     */
    @Override
    public void setMessage(String newMessage, int newType) {
        super.setMessage(newMessage, newType);

        // Wizard pages are decorated by a connection page, so the default
        // implementation of this method will not do anything
        IWizardPage page = getContainer().getCurrentPage();
        if (page != this && page instanceof WizardPage) {
            ((WizardPage) page).setMessage(newMessage, newType);
        } else {
            CatalogUIPlugin.log("A WizardPage was expected but instead was a " //$NON-NLS-1$
                    + page.getClass().getName(), new Exception());
        }
    }

    /**
     * Sets the error Message on the wizard page. Since these pages are decorated by a connection
     * page the default implementation Fails to display the error.
     */
    @Override
    public void setErrorMessage(String newMessage) {
        super.setErrorMessage(newMessage);

        // Wizard pages are decorated by a connection page, so the default
        // implementation of this method will not do anything
        IWizardPage page = getContainer().getCurrentPage();
        if (page != this && page instanceof WizardPage) {
            ((WizardPage) page).setErrorMessage(newMessage);
        } else {
            CatalogUIPlugin.log("A WizardPage was expected but instead was a " //$NON-NLS-1$
                    + page.getClass().getName(), new Exception());
        }
    }

    @Override
    public final IWizardPage getNextPage() {
        return super.getNextPage();
    }

    /**
     * Called by framework as the page is about to be left.
     * <p>
     * There are two main use cases for this method. The first is to save settings for the next time
     * the wizard is visited. The other is to perform some checks or do some loading that is too
     * expensive to do every time isPageComplete() is called. For example a database wizard page
     * might try to connect to the database in this method rather than isPageComplete() because it
     * is such an expensive method to call.
     * </p>
     * <p>
     * Remember that this method is <em>only</em> called when moving forward.
     * </p>
     * <p>
     * If an expensive method is called make sure to run it in the container:
     *
     * <pre>
     * getContainer().run(false, cancelable, runnable);
     * </pre>
     *
     * Remember to pass in false as the fork parameter so that it blocks until the method has
     * completed executing.
     * </p>
     *
     * @see WorkflowWizardPage#leavingPage()
     * @return true if it is acceptable to leave the page false if the page must not be left
     */
    @Override
    public boolean leavingPage() {
        // default does nothing
        return true;
    }

    /**
     * Returns the IDs of the GeoResource to use as the "selected" resources. If a non-empty
     * collection is returned then the next states in the wizard "should" use these as the items
     * selected by the user.
     * <p>
     * Example: The postgis wizard pages permits the user to select the table of interest. When
     * moving to a new state (for example the Resource Selection State in
     * org.locationtech.udig.project.ui) that state should use that as the selected IGeoResources if
     * it needs a selection of IGeoResources <br>
     * In the ResourceSelectionState example it would use this collection and not need to query the
     * user with a wizard page for that input
     *
     * @return the ids of the GeoResource to use as the "selected" resources.
     */
    @Override
    public Collection<URL> getResourceIDs() {
        return Collections.emptySet();
    }

    /**
     * Default implementation creates a collection of services from the parameters returned
     * {@link UDIGConnectionPage#getParams()}
     */
    @Override
    public Collection<IService> getServices() {
        final Map<String, Serializable> params = getParams();
        final Collection<IService> services = new HashSet<>();
        IRunnableWithProgress runnable = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) {
                Collection<IService> newServices = EndConnectionState.constructServices(monitor,
                        params, new HashSet<URL>());
                services.addAll(newServices);
            }
        };
        try {
            getContainer().run(false, true, runnable);
        } catch (InvocationTargetException e) {
            setErrorMessage("Could not connect:" + e.getCause().getMessage());
            throw (RuntimeException) new RuntimeException().initCause(e);
        } catch (InterruptedException e) {
            setErrorMessage("Canceled");
            throw (RuntimeException) new RuntimeException().initCause(e);
        }
        if (!services.isEmpty()) {
            return services; // found!
        }
        return services;
    }

    /**
     * Gather up connection parameters from the user interface
     *
     * @return connection parameters from the user interface
     */
    @Override
    public Map<String, Serializable> getParams() {
        return null;
    }

    @Override
    public void dispose() {
        super.dispose();
        setControl(null);
    }
}
