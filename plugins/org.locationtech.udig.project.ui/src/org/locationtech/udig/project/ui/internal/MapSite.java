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
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
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

    private IWorkbenchPartSite originalMapSite;

    private MapPart mapPart;

    public MapSite(IWorkbenchPartSite originalMapSite, MapPart mapPart) {
        this.originalMapSite = originalMapSite;
        this.mapPart = mapPart;
    }

    @Override
    public IEditorActionBarContributor getActionBarContributor() {
        if (originalMapSite instanceof IEditorSite) {
            return ((IEditorSite) originalMapSite).getActionBarContributor();
        }
        throw new IllegalStateException(ERROR_DELEGATE_IS_NOT_AN_EDITOR_SITE);
    }

    @Override
    public IActionBars getActionBars() {
        if (originalMapSite instanceof IEditorSite) {
            return new MapEditorActionBars(((IEditorSite) originalMapSite).getActionBars(),
                    mapPart);
        }
        if (originalMapSite instanceof IViewSite) {
            return new MapEditorActionBars(((IViewSite) originalMapSite).getActionBars(), mapPart);
        }
        throw new IllegalStateException(ERROR_DELEGATE_IS_NOT_AN_EDITOR_SITE);
    }

    @Override
    public void registerContextMenu(MenuManager menuManager, ISelectionProvider selectionProvider,
            boolean includeEditorInput) {
        if (originalMapSite instanceof IEditorSite) {
            ((IEditorSite) originalMapSite).registerContextMenu(menuManager, selectionProvider,
                    includeEditorInput);
            return;
        }
        throw new IllegalStateException(ERROR_DELEGATE_IS_NOT_AN_EDITOR_SITE);
    }

    @Override
    public void registerContextMenu(String menuId, MenuManager menuManager,
            ISelectionProvider selectionProvider, boolean includeEditorInput) {
        if (originalMapSite instanceof IEditorSite) {
            ((IEditorSite) originalMapSite).registerContextMenu(menuId, menuManager,
                    selectionProvider, includeEditorInput);
            return;
        }
        throw new IllegalStateException(ERROR_DELEGATE_IS_NOT_AN_EDITOR_SITE);
    }

    @Override
    public Object getAdapter(Class adapter) {
        return originalMapSite.getAdapter(adapter);
    }

    @Override
    public String getId() {
        return originalMapSite.getId();
    }

    @Override
    public IKeyBindingService getKeyBindingService() {
        return originalMapSite.getKeyBindingService();
    }

    @Override
    public IWorkbenchPage getPage() {
        return originalMapSite.getPage();
    }

    @Override
    public IWorkbenchPart getPart() {
        return originalMapSite.getPart();
    }

    @Override
    public String getPluginId() {
        return originalMapSite.getPluginId();
    }

    @Override
    public String getRegisteredName() {
        return originalMapSite.getRegisteredName();
    }

    @Override
    public ISelectionProvider getSelectionProvider() {
        return originalMapSite.getSelectionProvider();
    }

    @Override
    public Shell getShell() {
        return originalMapSite.getShell();
    }

    @Override
    public IWorkbenchWindow getWorkbenchWindow() {
        return originalMapSite.getWorkbenchWindow();
    }

    @Override
    public void registerContextMenu(MenuManager menuManager, ISelectionProvider selectionProvider) {
        originalMapSite.registerContextMenu(menuManager, selectionProvider);
    }

    @Override
    public void registerContextMenu(String menuId, MenuManager menuManager,
            ISelectionProvider selectionProvider) {
        originalMapSite.registerContextMenu(menuId, menuManager, selectionProvider);
    }

    @Override
    public void setSelectionProvider(ISelectionProvider provider) {
        originalMapSite.setSelectionProvider(provider);
    }

    @Override
    public String getSecondaryId() {
        if (originalMapSite instanceof IViewSite) {
            return ((IViewSite) originalMapSite).getSecondaryId();
        }
        throw new IllegalStateException("delegate is not a IViewSite!!!!"); //$NON-NLS-1$
    }

    private static class MapEditorActionBars implements IActionBars {
        private MapPart mapPart;

        private IActionBars actionBars;

        public MapEditorActionBars(IActionBars actionBars, MapPart mapPart) {
            this.actionBars = actionBars;
            this.mapPart = mapPart;
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
            return mapPart.getStatusLineManager();
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
    }

    @Override
    public Object getService(Class api) {
        return originalMapSite.getService(api);
    }

    @Override
    public boolean hasService(Class api) {
        return originalMapSite.hasService(api);
    }
}
