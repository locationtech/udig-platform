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
package org.locationtech.udig.internal.ui.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.expressions.Expression;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.SubContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.menus.AbstractContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.services.IServiceLocator;
import org.locationtech.udig.core.internal.ExtensionPointList;
import org.locationtech.udig.core.logging.LoggingSupport;
import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.ui.internal.Messages;
import org.locationtech.udig.ui.operations.OpAction;

/**
 * Creates an Operation Sub menu containing operations for all
 * operations defined for an object.
 *
 * @author jeichar
 * @since 0.6.0
 */
public class OperationMenuFactory {
    List<IConfigurationElement> extensionPoints;
    private MenuManager contextManager;
    private ArrayList<OpAction> actions = new ArrayList<>();
    private Map<String, OperationCategory> categories = new HashMap<>();
    private MenuManager menuManager;
    private IWorkbenchWindow window;
    /**
     * Create instance
     */
    public OperationMenuFactory() {
        extensionPoints = ExtensionPointList.getExtensionPointList("org.locationtech.udig.ui.operation"); //$NON-NLS-1$
        createActionList(extensionPoints);
    }

    /**
     * Gets a context menu containing all the enabled operations
     *
     * @param selection the current selection.
     * @return a context menu containing all the enabled operations
     */
    public MenuManager getContextMenu(ISelection selection) {
        contextManager = createMenuManager();
        Iterator iter = getCategories().values().iterator();
        while( iter.hasNext()) {
            OperationCategory category = (OperationCategory) iter.next();

            for (OpAction action : category.getActions()) {
                if( selection instanceof IStructuredSelection )
                    action.updateEnablement((IStructuredSelection) selection, true);
                if (action.isEnabled())
                    contextManager.add(action);
            }
            if (iter.hasNext())
                contextManager.add(new Separator());
        }

        if (getActions().size() != 0) {
            contextManager.add(new Separator());
        }
        for( OpAction action : getActions() ) {
            if( selection instanceof IStructuredSelection )
                action.updateEnablement((IStructuredSelection) selection, true);
            if (action.isEnabled()) {
                contextManager.add(action);
            }
        }
        return contextManager;
    }

    /**
     * Creates a menu manager with actions that will enable
     * based on the current workbench selection.
     *
     * @return a menu manager with all the Operation Actions.
     */
    public MenuManager getMenu( ) {
        if (menuManager == null) {
            menuManager = new MenuManager(getMenuText());
            for( OperationCategory category : categories.values() ) {
                if (category.getItems().length > 0) {
                    menuManager.add(category);
                }
            }
            for( OpAction action : actions ) {
                menuManager.add(action);
            }
            menuManager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        }
        return menuManager;
    }

    /**
     */
    public void add( ) {
        menuManager.update();
    }

    private void createActionList( List<IConfigurationElement> list ) {
        for( IConfigurationElement element : list ) {
            try {
                if (element.getName().equals("category")) //$NON-NLS-1$
                    categories.put(element.getAttribute("id"), new OperationCategory(element)); //$NON-NLS-1$
            } catch (Exception e) {
                LoggingSupport.log(UiPlugin.getDefault(), e);
            }
        }
        for( IConfigurationElement element : list ) {
            if (element.getName().equals("category")) //$NON-NLS-1$
                continue;
            OpAction action = new OpAction(element);

            if (window != null)
                window.getSelectionService().addSelectionListener(action);
            OperationCategory category = categories.get(element.getAttribute("categoryId")); //$NON-NLS-1$
            if (category != null) {
                category.add(action);
            } else {
                actions.add(action);
                if (element.getAttribute("categoryId") != null //$NON-NLS-1$
                        && element.getAttribute("categoryId").length() != 0) { //$NON-NLS-1$
                    LoggingSupport.log(UiPlugin.getDefault(),
                            "Action '" + action.getText() + "' references invalid category '" //$NON-NLS-1$ //$NON-NLS-2$
                                    + element.getAttribute("categoryId") + "'."); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }
    }

    /**
     * Adds actions defined by extension points to the menu bar.
     * @param menuBar
     */
    public void contributeActions( IMenuManager menuBar ) {
        for( OperationCategory category : categories.values() ) {
            for( OpAction action : category.getActions() ) {
                addActionToMenu(menuBar, action);
            }
        }
        for( OpAction action : actions ) {
            addActionToMenu(menuBar, action);
        }
    }
    /**
     * TODO summary sentence for addActionToMenu ...
     *
     * @param menuBar
     * @param action
     */
    private void addActionToMenu( IMenuManager menuBar, OpAction action ) {
        if (action.getMenuPath() != null)
            try {
                String[] paths = action.getMenuPath().split("/"); //$NON-NLS-1$
                IMenuManager manager = menuBar.findMenuUsingPath(IWorkbenchActionConstants.MB_ADDITIONS);
                if(manager == null){
                    manager = menuBar;
                }
                String markerID = null;
                for( String path : paths ) {
                    markerID = null;
                    IContributionItem item = manager.findUsingPath(path);
                    if (item == null) {
                        LoggingSupport.log(UiPlugin.getDefault(), action.getMenuPath() + " is not a valid menuPath"); //$NON-NLS-1$
                        break;
                    }
                    if (item instanceof IMenuManager) {
                        manager = (IMenuManager) item;
                    } else if (item.isGroupMarker()) {
                        markerID = item.getId();
                    } else if (item instanceof SubContributionItem) {
                        item = ((SubContributionItem) item).getInnerItem();
                        if (item instanceof IMenuManager) {
                            manager = (IMenuManager) item;
                        } else if (item.isGroupMarker()) {
                            markerID = item.getId();
                        }
                    }
                }
                if (manager != null) {
                    if (markerID != null)
                        manager.appendToGroup(markerID, action);
                    else {
                        manager.add(action);
                    }
                } else {
                    LoggingSupport.log(UiPlugin.getDefault(), action.getMenuPath() + " is not a valid menuPath");
                }
            } catch (Exception e) {
                LoggingSupport.log(UiPlugin.getDefault(), "Error adding operation to menu", e); //$NON-NLS-1$
            }
    }

    /**
     * @return Returns the window.
     */
    public IWorkbenchWindow getWindow() {
        return window;
    }

    /**
     * @param window The window to set.
     */
    public void setWindow( IWorkbenchWindow window ) {
        IWorkbenchWindow oldwindow = this.window;
        this.window = window;
        Collection<OperationCategory> cat = categories.values();
        for( OperationCategory category : cat ) {
            List<OpAction> catActions = category.getActions();
            for( OpAction action : catActions ) {
                if( window!=null)
                    window.getSelectionService().addSelectionListener(action);
                if( oldwindow!=null )
                    oldwindow.getSelectionService().removeSelectionListener(action);
            }
        }
        for( OpAction action : actions ) {
            if( window!=null)
                window.getSelectionService().addSelectionListener(action);
            if( oldwindow!=null )
                oldwindow.getSelectionService().removeSelectionListener(action);
        }
    }

    public List<OpAction> getActions() {
        return Collections.unmodifiableList(actions);
    }

    public Map<String, OperationCategory> getCategories() {
        return Collections.unmodifiableMap(categories);
    }

    private String getMenuText() {
        return Messages.OperationMenuFactory_menu_text;
    }

    public MenuManager createMenuManager() {
        return new MenuManager(getMenuText(), "analysis"); //$NON-NLS-1$
    }

    public OperationCategory findCategory( String categoryId ) {
        return getCategories().get(categoryId);
    }

    public OpAction find( String actionId ) {
        for (OpAction action : getActions()) {
            if (action.getId().equals(actionId)) {
                return action;
            }
        }

        for (OperationCategory category : getCategories().values()) {
            for (OpAction action : category.actions) {
                if (action.getId().equals(actionId)) {
                    return action;
                }
            }
        }

        return null;
    }

    /**
     * The provided men
     * @param menuService
     */
    public void addWorkbenchMenus( IMenuService menuService ) {
        String locationURI;
        locationURI = "menu:org.eclipse.ui.main.menu?after=additions";
        menuService.addContributionFactory( new AbstractContributionFactory(locationURI,null){
            @Override
            public void createContributionItems( IServiceLocator serviceLocator,
                    IContributionRoot additions ) {
                additions.addContributionItem( getMenu(), Expression.TRUE );
            }
        });

        locationURI = "menu:edit?after=additions";
        menuService.addContributionFactory( new AbstractContributionFactory(locationURI,null){
            @Override
            public void createContributionItems( IServiceLocator serviceLocator,
            IContributionRoot additions ) {
                for( OpAction action : getActions() ){
                    IContributionItem item = new ActionContributionItem(action);
                    Expression visibleWhen = Expression.TRUE;

                    additions.addContributionItem(item, visibleWhen);
                }
            }
        });
    }

    /**
     * Create array of contribution items; suitable for use in a dynamic menu.
     *
     * @param categoryId
     * @return ActionItems for provided OperationCategory
     */
    public List<IContributionItem> createContributionItems( String categoryId ){
        List<IContributionItem> items = new ArrayList<>();

        OperationCategory category = findCategory( categoryId );
        if( category == null || category.getActions().isEmpty() ){
            return items;
        }
        List<OpAction> actions = category.getActions();
        for( OpAction action : actions ){
            if( action == null ){
                continue; // TODO: why do we have a null action here?
            }
            IContributionItem item = new ActionContributionItem(action);
            item.setVisible(true);
            items.add( item );
        }
        return items;
    }
}
