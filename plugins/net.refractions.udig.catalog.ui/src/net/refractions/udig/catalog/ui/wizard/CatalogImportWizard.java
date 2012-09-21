/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004-2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.catalog.ui.wizard;

import java.util.Collection;
import java.util.Map;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.ui.CatalogView;
import net.refractions.udig.catalog.ui.CatalogTreeViewer;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.workflow.EndConnectionState;
import net.refractions.udig.catalog.ui.workflow.State;
import net.refractions.udig.catalog.ui.workflow.Workflow;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizard;
import net.refractions.udig.catalog.ui.workflow.WorkflowWizardPageProvider;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * WorkflowWizard going through the motions of importing a new IService into the catalog.
 */
public class CatalogImportWizard extends WorkflowWizard {

    /** The provided workflow is used for import */
    public CatalogImportWizard(Workflow workflow,
            Map<Class<? extends State>, WorkflowWizardPageProvider> map) {
        super(workflow, map);
        setWindowTitle("Import");
    }

    /**
     * Collects the resutls from {@link EndConnectionState} and
     * adds them to the local catalog.
     */
    @Override
    protected boolean performFinish(IProgressMonitor monitor) {
        // get the connection state from the pipe
        EndConnectionState connState = getWorkflow().getState(EndConnectionState.class);

        if (connState == null)
            return false;

        // wizard page is responsible for sorting out the services
        // to add to the catalog.
        final Collection<IService> services = connState.getServices();
        if (services == null || services.isEmpty())
            return false;

        // add the services to the catalog
        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
        catalog.add(services.iterator().next());// add the first service to catalog

        // for (IService service : services) {
        // catalog.add(service);
        // }

        // To meet Eclipse UI Guidelines wizards are supposed to show the result
        // of their action - in this case selecting the first service added
        // TODO: this has threading issues
        PlatformGIS.asyncInDisplayThread(new Runnable() {
            public void run() {
                try {
                    CatalogView view = getCatalogView();
                    if (view != null) {
                        CatalogTreeViewer treeviewer = view.getTreeviewer();
                        treeviewer
                                .setSelection(new StructuredSelection(services.iterator().next()));
                    }
                } catch (Exception e) {
                    CatalogUIPlugin.log(e.getLocalizedMessage(), e);
                }
            }
        }, true);

        return true;
    }

    protected boolean isShowCatalogView() {
        return true;
    }

    protected CatalogView getCatalogView() throws PartInitException {
        CatalogView view;
        if (isShowCatalogView()) {
            view = (CatalogView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage().showView(CatalogView.VIEW_ID);
        } else {
            view = (CatalogView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage().findView(CatalogView.VIEW_ID);
        }
        return view;
    }
}