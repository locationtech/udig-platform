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
package net.refractions.udig.project.ui.internal;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IActionBars2;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.services.IServiceLocator;

/**
 * Primarily used so the action bars returned by this is 
 * a MapEditorActionBars
 * 
 * @author Jesse
 * @since 1.1.0
 * @version 1.3.0
 */
public class MapEditorSite implements IEditorSite, IViewSite {

    IWorkbenchPartSite delegate;
    private MapPart editor;
    
    public MapEditorSite(IWorkbenchPartSite original, MapPart editor) {
        delegate=original;
        this.editor=editor;
    }
    
    public IEditorActionBarContributor getActionBarContributor() {
        if( delegate instanceof IEditorSite ){
            return ((IEditorSite)delegate).getActionBarContributor();
        }
        throw new IllegalStateException("delegate is not a IEditorSite!!!"); //$NON-NLS-1$
    }

    public IActionBars getActionBars() {
        if( delegate instanceof IEditorSite ){
            return new MapEditorActionBars((IActionBars2) ((IEditorSite)delegate).getActionBars(), editor);
       }
        if( delegate instanceof IViewSite ){
            return new MapEditorActionBars((IActionBars2) ((IViewSite)delegate).getActionBars(), editor);
       }
       throw new IllegalStateException("delegate is not a IEditorSite!!!!"); //$NON-NLS-1$
    }

    public void registerContextMenu( MenuManager menuManager, ISelectionProvider selectionProvider, boolean includeEditorInput ) {
        if( delegate instanceof IEditorSite ){
             ((IEditorSite)delegate).registerContextMenu(menuManager, selectionProvider, includeEditorInput);
             return;
        }
        throw new IllegalStateException("delegate is not a IEditorSite!!!!"); //$NON-NLS-1$
    }

    public void registerContextMenu( String menuId, MenuManager menuManager, ISelectionProvider selectionProvider, boolean includeEditorInput ) {
        if( delegate instanceof IEditorSite ){
            ((IEditorSite)delegate).registerContextMenu(menuId, menuManager, selectionProvider, includeEditorInput);
            return;
       }
       throw new IllegalStateException("delegate is not a IEditorSite!!!!"); //$NON-NLS-1$
    }

    public Object getAdapter( Class adapter ) {
        return delegate.getAdapter(adapter);
    }

    public String getId() {
        return delegate.getId();
    }

    public IKeyBindingService getKeyBindingService() {
        return delegate.getKeyBindingService();
    }

    public IWorkbenchPage getPage() {
        return delegate.getPage();
    }

    public IWorkbenchPart getPart() {
        return delegate.getPart();
    }

    public String getPluginId() {
        return delegate.getPluginId();
    }

    public String getRegisteredName() {
        return delegate.getRegisteredName();
    }

    public ISelectionProvider getSelectionProvider() {
        return delegate.getSelectionProvider();
    }

//    public Object getService( Class api ) {
//        return delegate.getService(api);
//    }

    public Shell getShell() {
        return delegate.getShell();
    }

    public IWorkbenchWindow getWorkbenchWindow() {
        return delegate.getWorkbenchWindow();
    }

//    public boolean hasService( Class api ) {
//        return delegate.hasService(api);
//    }

    public void registerContextMenu( MenuManager menuManager, ISelectionProvider selectionProvider ) {
        delegate.registerContextMenu(menuManager, selectionProvider);
    }

    public void registerContextMenu( String menuId, MenuManager menuManager, ISelectionProvider selectionProvider ) {
        delegate.registerContextMenu(menuId, menuManager, selectionProvider);
    }

    public void setSelectionProvider( ISelectionProvider provider ) {
        delegate.setSelectionProvider(provider);
    }

    public String getSecondaryId() {
        if( delegate instanceof IViewSite ){
            return ((IViewSite)delegate).getSecondaryId();
       }
       throw new IllegalStateException("delegate is not a IViewSite!!!!"); //$NON-NLS-1$
    }

    private static class MapEditorActionBars implements IActionBars2 {
    	MapPart editor;
        private IActionBars2 actionBars;

        public MapEditorActionBars( IActionBars2 actionBars, MapPart editor ) {
            this.editor=editor;
            this.actionBars=actionBars;
        }

        public void clearGlobalActionHandlers() {
            actionBars.clearGlobalActionHandlers();
        }

        public IAction getGlobalActionHandler( String actionId ) {
            return actionBars.getGlobalActionHandler(actionId);
        }

        public IMenuManager getMenuManager() {
            return actionBars.getMenuManager();
        }

        public IServiceLocator getServiceLocator() {
            return actionBars.getServiceLocator();
        }

        public IStatusLineManager getStatusLineManager() {
            return editor.getStatusLineManager();
        }

        public IToolBarManager getToolBarManager() {
            return actionBars.getToolBarManager();
        }

        public void setGlobalActionHandler( String actionId, IAction handler ) {
            actionBars.setGlobalActionHandler(actionId, handler);
        }

        public void updateActionBars() {
            actionBars.updateActionBars();
        }

        public ICoolBarManager getCoolBarManager() {
            return actionBars.getCoolBarManager();
        }
    }

    public Object getService( Class api ) {
        return delegate.getService(api);
    }

    public boolean hasService( Class api ) {
        return delegate.hasService(api);
    }
}
