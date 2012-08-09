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
package net.refractions.udig.catalog.ui.internal;

import java.io.IOException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.internal.dialogs.AdaptableForwarder;

/**
 * A command hander to display an IGeoResource properties page.
 * 
 * @author Jody Garnett (LISAsoft)
 * @since 1.3.2
 */
public class IServicePropertiesCommandHandler extends AbstractHandler implements IHandler {

    public Object execute( final ExecutionEvent arg0 ) throws ExecutionException {

        final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow();
        
        IShellProvider shellProvider = new IShellProvider(){
            public Shell getShell() {
                return new Shell(activeWorkbenchWindow.getShell());
            }
        };

        IWorkbenchPart activePart = activeWorkbenchWindow.getActivePage().getActivePart();
        ISelectionProvider selectionProvider =  new ServiceSelectionProvider( activePart.getSite().getSelectionProvider() );
        
        PropertyDialogAction action = new PropertyDialogAction( shellProvider, selectionProvider);
        PreferenceDialog dialog = action.createDialog();
        dialog.open();

        return null;
    }
    class ServiceSelectionProvider implements ISelectionProvider {
        ISelectionProvider provider;
        ServiceSelectionProvider( ISelectionProvider provider ){
            this.provider = provider;
        }
        @Override
        public void addSelectionChangedListener(ISelectionChangedListener listener) {
            provider.addSelectionChangedListener( listener );
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
            return selection;
        }
        private ISelection toServiceSelection(IGeoResource resource) {
            try {
                IService service = resource.service( new NullProgressMonitor() );
                return new StructuredSelection( service );
            } catch (IOException e) {
                return StructuredSelection.EMPTY;
            }
        }
        @Override
        public void removeSelectionChangedListener(ISelectionChangedListener listener) {
            provider.addSelectionChangedListener( listener );
        }
        @Override
        public void setSelection(ISelection selection) {
            provider.setSelection( selection );
        }
    }
    /**
     * Placeholder to convert a selection to an IAdaptable.
     * <p>
     * The design of this class was informed by {@link AdaptableForwarder}.
     * 
     * @author jody
     * @since 1.3.2
     */
    static class NullAdaptor implements IAdaptable {
        private Object element;
        public NullAdaptor( Object element ){
            if( element instanceof NullAdaptor ){
                NullAdaptor nullAdaptor = (NullAdaptor) element;
                element = nullAdaptor.element;
            }
            else {
                this.element = element;
            }
        }
        public Object getAdapter(Class type) {
            if( type.isInstance( element ) ){
                return type.cast( element );
            }
            if (element instanceof IAdaptable){
                IAdaptable adaptable = (IAdaptable) element;
                Object adapter = adaptable.getAdapter( type );
                if( adapter != null ){
                    return type.cast( adapter);
                }
            }
            // last ditch attempt!
            IAdapterManager manager = Platform.getAdapterManager();
            return manager.getAdapter( element, type );
        }
        
    }
}
