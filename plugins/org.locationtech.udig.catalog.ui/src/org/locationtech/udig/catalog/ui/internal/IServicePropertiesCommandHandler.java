/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.ui.internal;

import java.io.IOException;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.core.SelectionProviderForwarder;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * A command hander to display an IGeoResource properties page.
 * 
 * @author Jody Garnett (LISAsoft)
 * @since 1.3.2
 */
public class IServicePropertiesCommandHandler extends AbstractHandler implements IHandler {

    public Object execute( final ExecutionEvent event ) throws ExecutionException {

        final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow();
        
        IShellProvider shellProvider = new IShellProvider(){
            public Shell getShell() {
            	return HandlerUtil.getActiveShell(event);
            }
        };

        IWorkbenchPart activePart = activeWorkbenchWindow.getActivePage().getActivePart();
        ISelectionProvider provider = activePart.getSite().getSelectionProvider();
        if( provider == null ){
            MessageDialog.openInformation( activeWorkbenchWindow.getShell(), "Service Properties", "Please select a service");
            return null;
        }
        ISelectionProvider selectionProvider = new ServiceSelectionProvider( provider );
        
        PropertyDialogAction action = new PropertyDialogAction( shellProvider, selectionProvider);
        PreferenceDialog dialog = action.createDialog();
        dialog.open();

        return null;
    }
    /**
     * SelectionProviderForwarder to forward the selection as an IService.
     * <p>
     * Additional measures are taken to convert an IGeoReosurce (if selected) to its parent IService.
     * 
     * @author Jody Garnett
     * @since 1.3.2
     */
    static class ServiceSelectionProvider extends SelectionProviderForwarder {
        ServiceSelectionProvider( ISelectionProvider provider ){
            super( provider, IService.class );
        }
        @Override
        public ISelection getSelection() {
            ISelection selection = provider.getSelection();
            if( selection instanceof IStructuredSelection && !selection.isEmpty() ){
                IStructuredSelection sel = (IStructuredSelection) selection;
                Object element = sel.getFirstElement();
                
                if( element instanceof IService){
                    return selection;
                }                
                else if (element instanceof IGeoResource ){
                    IGeoResource resource = (IGeoResource) element;
                    return toServiceSelection(resource);
                }
                else if (element instanceof IResolve ){
                    IResolve resolve = (IResolve) element;
                    if( resolve.canResolve( IService.class)){
                        try {
                            IService service = resolve.resolve( IService.class, new NullProgressMonitor() );
                            return new StructuredSelection( service );
                        } catch (IOException e) {
                            return StructuredSelection.EMPTY;
                        }
                    }
                }
                // check IAdaptable incase ILayer or another selection wants to play
                if( element instanceof IAdaptable ){
                    IAdaptable adaptable = (IAdaptable) element;
                    IService service = (IService) adaptable.getAdapter(IService.class);
                    if( service != null ){
                        return new StructuredSelection( service );
                    }
                    IGeoResource resource = (IGeoResource) adaptable.getAdapter(IGeoResource.class);
                    if( resource != null ){
                        return toServiceSelection(resource);
                    }
                    IResolve resolve = (IResolve) adaptable.getAdapter(IResolve.class);
                    if( resolve != null ){
                        if( resolve.canResolve( IService.class)){
                            try {
                                service = resolve.resolve( IService.class, new NullProgressMonitor() );
                                return new StructuredSelection( service );
                            } catch (IOException e) {
                                return StructuredSelection.EMPTY;
                            }
                        }                        
                    }                    
                }
            }
            return StructuredSelection.EMPTY; // no dice!
        }
        private ISelection toServiceSelection(IGeoResource resource) {
            try {
                IService service = resource.service( new NullProgressMonitor() );
                return new StructuredSelection( service );
            } catch (IOException e) {
                return StructuredSelection.EMPTY;
            }
        }
    }
}
