/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

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
 * Primarily used so the action bars returned by this is a MapEditorActionBars
 *
 * @author Jesse
 * @since 1.1.0
 * @version 1.3.0
 */
public class MapSite implements IEditorSite, IViewSite {

    private static final String ERROR_DELEGATE_IS_NOT_AN_EDITOR_SITE = "delegate is not a IEditorSite!!!"; //$NON-NLS-1$

    IWorkbenchPartSite delegate;

    private MapPart editor;

    public MapSite(IWorkbenchPartSite original, MapPart editor) {
        delegate = original;
        this.editor = editor;
    }

    @Override
    public IEditorActionBarContributor getActionBarContributor() {
        if (delegate instanceof IEditorSite) {
            return ((IEditorSite) delegate).getActionBarContributor();
        }
        throw new IllegalStateException(ERROR_DELEGATE_IS_NOT_AN_EDITOR_SITE);
    }

    @Override
    public IActionBars getActionBars() {
        if (delegate instanceof IEditorSite) {
            return new MapEditorActionBars((IActionBars2) ((IEditorSite) delegate).getActionBars(),
                    editor);
        }
        if (delegate instanceof IViewSite) {
            return new MapEditorActionBars((IActionBars2) ((IViewSite) delegate).getActionBars(),
                    editor);
        }
        throw new IllegalStateException(ERROR_DELEGATE_IS_NOT_AN_EDITOR_SITE);
    }

    @Override
    public void registerContextMenu(MenuManager menuManager, ISelectionProvider selectionProvider,
            boolean includeEditorInput) {
        if (delegate instanceof IEditorSite) {
            ((IEditorSite) delegate).registerContextMenu(menuManager, selectionProvider,
                    includeEditorInput);
            return;
        }
        throw new IllegalStateException(ERROR_DELEGATE_IS_NOT_AN_EDITOR_SITE);
    }

    @Override
    public void registerContextMenu(String menuId, MenuManager menuManager,
            ISelectionProvider selectionProvider, boolean includeEditorInput) {
        if (delegate instanceof IEditorSite) {
            ((IEditorSite) delegate).registerContextMenu(menuId, menuManager, selectionProvider,
                    includeEditorInput);
            return;
        }
        throw new IllegalStateException(ERROR_DELEGATE_IS_NOT_AN_EDITOR_SITE);
    }

    @Override
    public Object getAdapter(Class adapter) {
        return delegate.getAdapter(adapter);
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public IKeyBindingService getKeyBindingService() {
        return delegate.getKeyBindingService();
    }

    @Override
    public IWorkbenchPage getPage() {
        return delegate.getPage();
    }

    @Override
    public IWorkbenchPart getPart() {
        return delegate.getPart();
    }

    @Override
    public String getPluginId() {
        return delegate.getPluginId();
    }

    @Override
    public String getRegisteredName() {
        return delegate.getRegisteredName();
    }

    @Override
    public ISelectionProvider getSelectionProvider() {
        return delegate.getSelectionProvider();
    }

    @Override
    public Shell getShell() {
        return delegate.getShell();
    }

    @Override
    public IWorkbenchWindow getWorkbenchWindow() {
        return delegate.getWorkbenchWindow();
    }

    @Override
    public void registerContextMenu(MenuManager menuManager, ISelectionProvider selectionProvider) {
        delegate.registerContextMenu(menuManager, selectionProvider);
    }

    @Override
    public void registerContextMenu(String menuId, MenuManager menuManager,
            ISelectionProvider selectionProvider) {
        delegate.registerContextMenu(menuId, menuManager, selectionProvider);
    }

    @Override
    public void setSelectionProvider(ISelectionProvider provider) {
        delegate.setSelectionProvider(provider);
    }

    @Override
    public String getSecondaryId() {
        if (delegate instanceof IViewSite) {
            return ((IViewSite) delegate).getSecondaryId();
        }
        throw new IllegalStateException("delegate is not a IViewSite!!!!"); //$NON-NLS-1$
    }

    private static class MapEditorActionBars implements IActionBars2 {
        MapPart editor;

        private IActionBars2 actionBars;

        public MapEditorActionBars(IActionBars2 actionBars, MapPart editor) {
            this.editor = editor;
            this.actionBars = actionBars;
        }

        @Override
        public void clearGlobalActionHandlers() {
            actionBars.clearGlobalActionHandlers();
        }

        @Override
        public IAction getGlobalActionHandler(String actionId) {
            return actionBars.getGlobalActionHandler(actionId);
        }

        @Override
        public IMenuManager getMenuManager() {
            return actionBars.getMenuManager();
        }

        @Override
        public IServiceLocator getServiceLocator() {
            return actionBars.getServiceLocator();
        }

        @Override
        public IStatusLineManager getStatusLineManager() {
            return editor.getStatusLineManager();
        }

        @Override
        public IToolBarManager getToolBarManager() {
            return actionBars.getToolBarManager();
        }

        @Override
        public void setGlobalActionHandler(String actionId, IAction handler) {
            actionBars.setGlobalActionHandler(actionId, handler);
        }

        @Override
        public void updateActionBars() {
            actionBars.updateActionBars();
        }

        @Override
        public ICoolBarManager getCoolBarManager() {
            return actionBars.getCoolBarManager();
        }
    }

    @Override
    public Object getService(Class api) {
        return delegate.getService(api);
    }

    @Override
    public boolean hasService(Class api) {
        return delegate.hasService(api);
    }
}
