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
package org.locationtech.udig.project.ui.internal.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.internal.ui.ResourceSelectionPage;
import org.locationtech.udig.catalog.ui.workflow.BasicWorkflowWizardPageFactory;
import org.locationtech.udig.catalog.ui.workflow.ResourceSelectionState;
import org.locationtech.udig.catalog.ui.workflow.State;
import org.locationtech.udig.catalog.ui.workflow.Workflow;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizard;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardDialog;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardPageProvider;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.ui.PlatformGIS;
import org.locationtech.udig.ui.ProgressManager;

/**
 * Quickly create a new empty map with a default name.
 * 
 * @author rgould
 * @since 0.9.0
 */
public class AddToNewMap
        implements
            IObjectActionDelegate,
            IWorkbenchWindowActionDelegate,
            IWorkbenchWindowPulldownDelegate {

    IStructuredSelection selection = null;

    /**
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
     *      org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart( IAction action, IWorkbenchPart targetPart ) {
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @SuppressWarnings("unchecked")
    public void run( IAction action ) {
        PlatformGIS.run(new ISafeRunnable(){

            public void handleException(Throwable e) {
                ProjectUIPlugin.log("", e); //$NON-NLS-1$
            }

            public void run() throws Exception {
                List toList = selection.toList();
                Collection<IGeoResource> resources = getResources(toList);
                if (resources != null)
                    ApplicationGIS.addLayersToMap(ApplicationGIS.getActiveProject(),
                            new ArrayList<IGeoResource>(resources));
            }

        });
    }

    /**
     * Collection is processed so that georesources are added to the returned collection and if a
     * service has more than 1 child the user is queried to determine which services should be added
     * to the returned collection. Services with only a single child are added to result
     * automatically.
     * 
     * @param collection the collection of {@link IService}s and {@link IGeoResource}s to process.
     * @return the chosen resources.
     */
    static Collection<IGeoResource> getResources(Collection<Object> collection) {
        Set<IGeoResource> resources=new HashSet<IGeoResource>();
        Set<IService> services=new HashSet<IService>();
        boolean needsUserInput=false;
        for( Object object : collection ) {
            try {
                if( object instanceof IGeoResource ){
                    IGeoResource geoResource = (IGeoResource)object;
                    resources.add(geoResource);
                    services.add(geoResource.service(ProgressManager.instance().get()));
                }
                if( object instanceof IService ){
                    IService service = (IService) object;
                    List< ? extends IGeoResource> members;
                    members = service.resources( ProgressManager.instance().get() );
                    if( members.isEmpty() )
                        continue;
                    services.add(service);
                    if( members.size()==1 )
                        resources.add(members.get(0));
                    else{
                        needsUserInput=true;
                    }   
                }
            } catch (IOException e) {
                ProjectUIPlugin.log("", e); //$NON-NLS-1$
            }
        }
        
        if( needsUserInput ){
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
            Shell shell = activeWorkbenchWindow.getShell();
            shell.setActive();
            Map<Class< ? extends State>, WorkflowWizardPageProvider> pageMapping=new HashMap<Class<? extends State>, WorkflowWizardPageProvider>();
            ResourceSelectionPage resourceSelectionPage = new ResourceSelectionPage(Messages.AddToNewMap_resource_selection_page_title);
            resourceSelectionPage.setCollapseCheckedInput(true);
            pageMapping.put(ResourceSelectionState.class, new BasicWorkflowWizardPageFactory(resourceSelectionPage));
            ResourceSelectionState resourceSelectionState = new ResourceSelectionState();
            resourceSelectionState.setServices(services);
            Workflow workflow=new Workflow(new State[]{resourceSelectionState});
            workflow.setContext(resources);
            WorkflowWizard wizard = new WorkflowWizard(workflow, pageMapping);
            WorkflowWizardDialog dialog = new WorkflowWizardDialog(Display.getCurrent().getActiveShell(), wizard);
            dialog.setBlockOnOpen(true);
            dialog.open();
            if (dialog.getReturnCode()==IDialogConstants.CANCEL_ID )
                return null;
            resources=resourceSelectionState.getResources().keySet();
        }
        return resources;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IAction action, ISelection selection ) {

        if (selection instanceof IStructuredSelection)
            this.selection = (IStructuredSelection) selection;
    }

    /*
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose() {
    }

    /*
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init( IWorkbenchWindow window ) {
       // do nothing
    }

    /*
     * @see org.eclipse.ui.IWorkbenchWindowPulldownDelegate#getMenu(org.eclipse.swt.widgets.Control)
     */
    public Menu getMenu( Control parent ) {
        return null;
    }

}
