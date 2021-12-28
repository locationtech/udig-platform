/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.tool.display;

import java.util.Collection;

import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.tool.IToolManager;
import org.locationtech.udig.ui.Constants;

/**
 * A category object that contributes to a menu.
 *
 * @author jeichar
 * @since 0.9.0
 * @version 1.3.0
 */
public class MenuToolCategory extends ToolCategory {

    /**
     * Construct <code>MenuToolCategory</code>.
     *
     * @param element
     * @param manager
     */
    public MenuToolCategory(IConfigurationElement element, IToolManager manager) {
        super(element, manager);
    }

    /**
     * Construct <code>MenuToolCategory2</code>.
     *
     * @param manager
     */
    public MenuToolCategory(IToolManager manager) {
        super(manager);
    }

    /**
     * Adds items action in the correct locations in the menu.
     *
     * @param manager
     */
    public void contribute(IMenuManager manager) {
        IWorkbench workbench = PlatformUI.getWorkbench();
        IContextService contextService = workbench.getService(IContextService.class);
        Collection<String> active = contextService.getActiveContextIds();

        MenuManager actionMenu = new MenuManager(name, id);
        final String actionExt = "action.ext"; //$NON-NLS-1$
        actionMenu.add(new GroupMarker(actionExt));
        actionMenu.add(new GroupMarker("modal.ext")); //$NON-NLS-1$
        for (ModalItem item : this) {
            ToolProxy tool = (ToolProxy) item;
            String categoryId = tool.getCategoryId();
            if (contextService.getDefinedContextIds().contains(categoryId)) {
                // we have an context for this tool category
                if (!active.contains(categoryId)) {
                    continue; // skip this category please!
                }
            }
            if (tool.getType() == ToolProxy.ACTION) {
                String menuPath = tool.getMenuPath();
                IAction action = tool.getAction();

                if (menuPath != null) {
                    String root = menuPath.substring(0, menuPath.lastIndexOf("/")); //$NON-NLS-1$
                    String groupName = menuPath.substring(menuPath.lastIndexOf("/") + 1, //$NON-NLS-1$
                            menuPath.length());
                    if (groupName.equals(Constants.M_TOOL)) {
                        groupName = "map"; //$NON-NLS-1$
                    }
                    IMenuManager targetMenu = manager.findMenuUsingPath(root);
                    if (targetMenu != null) {
                        IContributionItem find = targetMenu.find(groupName);
                        if (find != null && find instanceof GroupMarker) {
                            // targetMenu.appendToGroup(groupName, action); //$NON-NLS-1$
                            targetMenu.appendToGroup(groupName, tool.getAction());
                            targetMenu.setVisible(true);
                        } else {
                            targetMenu.add(action);
                            targetMenu.setVisible(true);
                        }
                    } else
                        actionMenu.appendToGroup(actionExt, tool.getAction());
                } else {
                    actionMenu.appendToGroup(actionExt, tool.getAction());
                }
            }
            if (tool.getType() == ToolProxy.MODAL) {
                MenuCurrentToolItem menuItem = new MenuCurrentToolItem(tool);
                tool.addContribution(menuItem);
                actionMenu.appendToGroup("modal.ext", menuItem); //$NON-NLS-1$
            }
        }
        if (actionMenu.getItems().length > 0) {
            // Handle left over tools! Place them in the map menu?
            String menuPath = "map"; // was Constants.M_TOOL //$NON-NLS-1$
            IMenuManager toolManager = manager.findMenuUsingPath(menuPath);
            if (toolManager == null) {
                toolManager = new MenuManager(Messages.MenuToolCategory_menu_manager_title,
                        "tools"); //$NON-NLS-1$
                manager.add(toolManager);
                toolManager.add(new GroupMarker(actionExt));
                toolManager.add(new GroupMarker("modal.ext")); //$NON-NLS-1$
            }
            if (toolManager.find(actionExt) == null) {
                toolManager.add(new GroupMarker(actionExt));
            }
            toolManager.appendToGroup(actionExt, actionMenu);
            toolManager.setVisible(true);
        }
    }

    /**
     * Allows a ToolProxy to be contributed as a MenuItem.
     */
    protected class MenuCurrentToolItem extends CurrentContributionItem {
        /**
         * Tool proxy represented by this menu item.
         */
        ToolProxy tool;

        /** Menu item contributed to menu - by {@link #fill(org.eclipse.swt.widgets.Composite) */
        MenuItem menuItem;

        /**
         * Construct <code>MenuToolCategory.MenuCurrentToolItem</code>.
         */
        public MenuCurrentToolItem(ToolProxy proxy) {
            this.tool = proxy;
        }

        @Override
        public void fill(final Menu parent, int index) {
            if (items.isEmpty()) {
                return;
            }

            menuItem = new MenuItem(parent, SWT.RADIO, index);
            menuItem.setText(tool.getName());
            menuItem.setImage(tool.getImage());
            if (items.contains(((ToolManager) manager).defaultModalToolProxy))
                menuItem.setSelection(true);
            menuItem.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    widgetDefaultSelected(e);

                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    tool.run();
                }

            });

        }

        @Override
        public void setSelection(boolean checked, ModalItem proxy) {
            if (proxy != tool)
                throw new AssertionError("Tool not provided for MenuCurrentToolItem"); //$NON-NLS-1$
            if (!menuItem.isDisposed())
                menuItem.setSelection(checked);
        }

        @Override
        protected boolean isChecked() {
            if (!menuItem.isDisposed())
                return menuItem.getSelection();
            return false;
        }

        @Override
        public boolean isDisposed() {
            return menuItem == null || menuItem.isDisposed();
        }
    }

    @Override
    protected IHandler getHandler() {
        return null;
    }

}
