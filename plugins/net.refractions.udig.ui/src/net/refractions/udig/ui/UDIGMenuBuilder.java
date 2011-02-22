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
package net.refractions.udig.ui;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import net.refractions.udig.core.internal.ExtensionPointList;
import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.ui.action.NewObjectContribution;
import net.refractions.udig.ui.action.NewObjectDelegate;
import net.refractions.udig.ui.action.NewObjectDelegateComparator;
import net.refractions.udig.ui.internal.Messages;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;

/**
 * This class builds the menus for the uDig application, it is being replaced with
 * org.eclipse.ui.menus.
 * <p>
 * When uDig is run as a plugin, this class would need to be called by something other than the
 * WorkbenchAdvisor in order to setup the menus. Some of these menus might be possible to let
 * Eclipse manage through an extension point.
 * </p>
 * <p>
 * NewContribution should probably be moved to a factory class (e.g. UDIGContributionFactory). This
 * would be similar to the way ContributionItemFactory works for the "Open Perspecive" and "Show
 * View" submenus.
 * </p>
 * To enable this class please include the following in your plugin.xml:<code><pre>
 * &lt;extension point=&quot;net.refractions.udig.ui.menuBuilders&quot;&gt;
 *    &lt;menuBuilder
 *        class=&quot;net.refractions.udig.ui.UDIGMenuBuilder&quot;
 *        id=&quot;net.refractions.udig.ui.uDigMenuBuilder&quot;/&gt;
 * &lt;/extension&gt;
 * </pre></code>
 *
 * @author cole.markham
 * @since 1.0.1
 */
public class UDIGMenuBuilder implements MenuBuilder {
    /**
     * @param menuBar
     * @param window The window that contains this menu
     */
    public void fillMenuBar( IMenuManager menuBar, IWorkbenchWindow window ) {

        IMenuManager fileMenu = menuBar.findMenuUsingPath(IWorkbenchActionConstants.M_FILE);
        if (fileMenu == null) {
            fileMenu = new MenuManager(Messages.UDIGWorkbenchAdvisor_file,
                    IWorkbenchActionConstants.M_FILE);
            if (menuBar.getItems().length > 0) {
                menuBar.insertBefore(menuBar.getItems()[0].getId(), fileMenu);
            } else {
                menuBar.add(fileMenu);
            }
        }

        IMenuManager editMenu = menuBar.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
        if (editMenu == null) {
            editMenu = new MenuManager(Messages.UDIGWorkbenchAdvisor_edit,
                    IWorkbenchActionConstants.M_EDIT);
            menuBar.insertAfter(IWorkbenchActionConstants.M_FILE, editMenu);
        }
        if (menuBar.findUsingPath(IWorkbenchActionConstants.MB_ADDITIONS) == null) {
            menuBar.insertAfter(IWorkbenchActionConstants.M_EDIT, new GroupMarker(
                    IWorkbenchActionConstants.MB_ADDITIONS));
        }
        IMenuManager windowMenu = menuBar.findMenuUsingPath(IWorkbenchActionConstants.M_WINDOW);
        if (windowMenu == null) {
            windowMenu = new MenuManager(Messages.UDIGWorkbenchAdvisor_window,
                    IWorkbenchActionConstants.M_WINDOW);
            menuBar.insertAfter(IWorkbenchActionConstants.MB_ADDITIONS, windowMenu);
        }
        IMenuManager helpMenu = menuBar.findMenuUsingPath(IWorkbenchActionConstants.M_HELP);
        if (helpMenu == null) {
            helpMenu = new MenuManager(Messages.UDIGWorkbenchAdvisor_help,
                    IWorkbenchActionConstants.M_HELP);
            menuBar.insertAfter(IWorkbenchActionConstants.M_WINDOW, helpMenu);
        }

        fillFileMenu(window, fileMenu);
        fillEditMenu(window, editMenu);
        fillWindowMenu(window, windowMenu);
        fillHelpMenu(window, helpMenu);
        UiPlugin.getDefault().getOperationMenuFactory().setWindow(window);
        menuBar.insertBefore(IWorkbenchActionConstants.MB_ADDITIONS, UiPlugin.getDefault()
                .getOperationMenuFactory().getMenu());
        menuBar.insertBefore(IWorkbenchActionConstants.MB_ADDITIONS, createNavigationMenu());
        menuBar.insertBefore(IWorkbenchActionConstants.MB_ADDITIONS, createLayerMenu());
        menuBar.insertBefore(IWorkbenchActionConstants.MB_ADDITIONS, createToolMenu());
        UiPlugin.getDefault().getOperationMenuFactory().contributeActions(menuBar);
    }

    /**
     * @param coolBar
     * @param window The window that contains the CoolBar
     */
    public void fillCoolBar( ICoolBarManager coolBar, IWorkbenchWindow window ) {
        coolBar.add(new ToolBarContributionItem(createFileBar(window),
                IWorkbenchActionConstants.TOOLBAR_FILE));
    }

    private ToolBarManager createFileBar( IWorkbenchWindow window ) {
        ToolBarManager toolbar = new ToolBarManager(SWT.FLAT);
        toolbar.add(new NewObjectContribution(window));

        toolbar.add(ActionFactory.SAVE.create(window));
        toolbar.add(ActionFactory.SAVE_ALL.create(window));

        toolbar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

        return toolbar;
    }

    private void setupFileMenuStructure( IMenuManager fileMenu ) {
        if (fileMenu.findUsingPath(IWorkbenchActionConstants.FILE_START) == null) {
            if (fileMenu.getItems().length > 0) {
                fileMenu.insertBefore(fileMenu.getItems()[0].getId(), new GroupMarker(
                        Constants.FILE_START));
            } else {
                fileMenu.add(new GroupMarker(Constants.FILE_START));
            }
        }

        if (fileMenu.findUsingPath(Constants.OPEN_EXT) == null) {
            fileMenu.insertAfter(Constants.FILE_START, new GroupMarker(Constants.OPEN_EXT));
        }

        if (fileMenu.findUsingPath(Constants.CLOSE_EXT) == null) {
            fileMenu.insertAfter(Constants.OPEN_EXT, new GroupMarker(Constants.CLOSE_EXT));
        }

        if (fileMenu.findUsingPath(Constants.SAVE_EXT) == null) {
            fileMenu.insertAfter(Constants.CLOSE_EXT, new GroupMarker(Constants.SAVE_EXT));
        }

        if (fileMenu.findUsingPath(IWorkbenchActionConstants.MB_ADDITIONS) == null) {
            fileMenu.insertAfter(Constants.SAVE_EXT, new GroupMarker(
                    IWorkbenchActionConstants.MB_ADDITIONS));
        }

        if (fileMenu.findUsingPath(Constants.FILE_END) == null) {
            fileMenu.insertAfter(IWorkbenchActionConstants.MB_ADDITIONS, new GroupMarker(
                    Constants.FILE_END));
        }

        fileMenu.insertAfter(Constants.OPEN_EXT, new Separator());
        fileMenu.insertAfter(Constants.CLOSE_EXT, new Separator());
        fileMenu.insertAfter(Constants.SAVE_EXT, new Separator());
        fileMenu.insertAfter(IWorkbenchActionConstants.MB_ADDITIONS, new Separator());
    }

    private void fillFileMenu( IWorkbenchWindow window, IMenuManager fileMenu ) {
        setupFileMenuStructure(fileMenu);

        IMenuManager newMenu = fileMenu.findMenuUsingPath(ActionFactory.NEW.getId());
        if (newMenu == null) {
            newMenu = new MenuManager(Messages.UDIGWorkbenchAdvisor_new, ActionFactory.NEW.getId());
            fileMenu.insertAfter(Constants.FILE_START, newMenu);
        }

        newMenu.add(new GroupMarker(Constants.NEW_START));

        List<IConfigurationElement> list = ExtensionPointList
                .getExtensionPointList(NewObjectContribution.NEW_ACTION_ID);
        Collections.sort(list, new NewObjectDelegateComparator());
        for( IConfigurationElement element : list ) {
            final NewObjectDelegate item = new NewObjectDelegate(element, window);
            Action newAction = new Action(){
                @Override
                public void runWithEvent( org.eclipse.swt.widgets.Event event ) {
                    item.runAction();
                }
            };
            newAction.setText(item.text);
            newAction.setImageDescriptor(item.icon);
            newMenu.appendToGroup(Constants.NEW_START, newAction);
        }
        newMenu.add(ContributionItemFactory.NEW_WIZARD_SHORTLIST.create(window));

        if (fileMenu.findUsingPath(ActionFactory.CLOSE.getId()) == null) {
            IAction close = ActionFactory.CLOSE.create(window);
            fileMenu.insertAfter(Constants.CLOSE_EXT, close);
        }

        if (fileMenu.findUsingPath(ActionFactory.CLOSE_ALL.getId()) == null) {
            IAction closeAll = ActionFactory.CLOSE_ALL.create(window);
            fileMenu.insertAfter(ActionFactory.CLOSE.getId(), closeAll);
        }

        if (fileMenu.findUsingPath(ActionFactory.SAVE.getId()) == null) {
            IAction save = ActionFactory.SAVE.create(window);
            fileMenu.insertBefore(Constants.SAVE_EXT, save);
        }

        if (fileMenu.findUsingPath(ActionFactory.SAVE_ALL.getId()) == null) {
            IAction saveAll = ActionFactory.SAVE_ALL.create(window);
            fileMenu.insertBefore(Constants.SAVE_EXT, saveAll);
        }

        //fileMenu.insertAfter(Constants.SAVE_EXT, new GroupMarker(Constants.REVERT_EXT));
        fileMenu.insertAfter(Constants.SAVE_EXT, new GroupMarker(Constants.COMMIT_EXT));

        fileMenu.insertBefore(Constants.FILE_END, new GroupMarker(Constants.RENAME_EXT));
        fileMenu.insertAfter(Constants.RENAME_EXT, new Separator());

        if (fileMenu.findUsingPath(ActionFactory.REFRESH.getId()) == null) {
            fileMenu.insertBefore(IWorkbenchActionConstants.MB_ADDITIONS, new GroupMarker(
                    ActionFactory.REFRESH.getId()));
        }

        if (fileMenu.findUsingPath(ActionFactory.IMPORT.getId()) == null) {
            IAction _import = ActionFactory.IMPORT.create(window);
            fileMenu.insertBefore(Constants.FILE_END, _import);
            fileMenu.insertAfter(ActionFactory.IMPORT.getId(), new Separator());
        }

        if (fileMenu.findUsingPath(ActionFactory.EXPORT.getId()) == null) {
            IAction _export = ActionFactory.EXPORT.create(window);
            fileMenu.insertBefore(Constants.FILE_END, _export);
            fileMenu.insertAfter(ActionFactory.EXPORT.getId(), new Separator());
        }

        fileMenu.insertBefore(Constants.FILE_END, new GroupMarker(Constants.CONFIG_EXT));
        fileMenu.insertAfter(Constants.CONFIG_EXT, new Separator());

        if (fileMenu.findUsingPath(ActionFactory.QUIT.getId()) == null) {
            IAction exit = ActionFactory.QUIT.create(window);
            fileMenu.insertAfter(Constants.FILE_END, exit);
        }
    }

    private void fillEditMenu( IWorkbenchWindow window, IMenuManager editMenu ) {
        if (editMenu.findUsingPath(Constants.EDIT_START) == null) {
            if (editMenu.getItems().length > 0) {
                editMenu.insertBefore(editMenu.getItems()[0].getId(), new GroupMarker(
                        Constants.EDIT_START));
            } else {
                editMenu.add(new GroupMarker(Constants.EDIT_START));
            }
        }

        if (editMenu.findUsingPath(Constants.UNDO_EXT) == null) {
            editMenu.insertAfter(Constants.EDIT_START, new GroupMarker(Constants.UNDO_EXT));
        }

        if (editMenu.findUsingPath(Constants.CUT_EXT) == null) {
            editMenu.insertAfter(Constants.UNDO_EXT, new GroupMarker(Constants.CUT_EXT));
        }

        if (editMenu.findUsingPath(Constants.ADD_EXT) == null) {
            editMenu.insertAfter(Constants.CUT_EXT, new GroupMarker(Constants.ADD_EXT));
        }

        if (editMenu.findUsingPath(Constants.EDIT_END) == null) {
            editMenu.insertAfter(Constants.ADD_EXT, new GroupMarker(Constants.EDIT_END));
        }
        if (editMenu.findUsingPath(IWorkbenchActionConstants.MB_ADDITIONS) == null) {
            editMenu.insertAfter(Constants.EDIT_END, new GroupMarker(
                    IWorkbenchActionConstants.MB_ADDITIONS));
        }

        editMenu.appendToGroup(Constants.UNDO_EXT, ActionFactory.UNDO.create(window));
        editMenu.appendToGroup(Constants.UNDO_EXT, ActionFactory.REDO.create(window));
        editMenu.appendToGroup(Constants.CUT_EXT, ActionFactory.CUT.create(window));
        editMenu.appendToGroup(Constants.CUT_EXT, ActionFactory.COPY.create(window));
        editMenu.appendToGroup(Constants.CUT_EXT, ActionFactory.PASTE.create(window));
        editMenu.appendToGroup(Constants.ADD_EXT, ActionFactory.DELETE.create(window));
        // appendToGroup(Constants.ADD_EXT, ActionFactory.SELECT_ALL.create(window));

        editMenu.insertAfter(Constants.UNDO_EXT, new Separator());
        editMenu.insertAfter(Constants.CUT_EXT, new Separator());
        editMenu.insertAfter(Constants.EDIT_END, new Separator());
    }

    private IMenuManager createLayerMenu() {
        MenuManager menu = new MenuManager(Messages.UDIGWorkbenchAdvisor_layerMenu,
                Constants.M_LAYER);
        menu.add(new GroupMarker(Constants.LAYER_ADD_EXT));
        menu.add(new Separator());
        menu.add(new GroupMarker(Constants.LAYER_EDIT_EXT));
        menu.add(new Separator());
        menu.add(new GroupMarker(Constants.LAYER_MAPGRAPHIC_EXT));
        menu.add(new GroupMarker(Constants.LAYER_MAPGRAPHIC_OTHER));

        menu.add(new Separator());
        menu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

        return menu;
    }

    private IMenuManager createNavigationMenu() {
        MenuManager menu = new MenuManager(Messages.UDIGWorkbenchAdvisor_navigationMenu,
                Constants.M_NAVIGATE);
        // menu.add(ActionFactory.BACKWARD_HISTORY.create(window));
        // menu.add(ActionFactory.FORWARD_HISTORY.create(window));
        menu.add(new GroupMarker(Constants.NAV_START));
        menu.add(new Separator());
        menu.add(new GroupMarker(Constants.NAV_ZOOM_EXT));

        menu.add(new Separator());
        menu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

        menu.add(new Separator());
        menu.add(new GroupMarker(Constants.NAV_BOTTOM));

        return menu;
    }

    private IMenuManager createToolMenu() {
        MenuManager menu = new MenuManager(Messages.UDIGWorkbenchAdvisor_tools, Constants.M_TOOL);
        menu.add(new GroupMarker(Constants.TOOL_ACTION));
        menu.add(new Separator());
        menu.add(new GroupMarker(Constants.TOOL_MODAL));
        menu.add(new Separator());
        menu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

        return menu;
    }

    private void fillWindowMenu( IWorkbenchWindow window, IMenuManager windowMenu ) {

        if (windowMenu.findUsingPath(ActionFactory.OPEN_NEW_WINDOW.getId()) == null) {
            IAction openNewWindow = ActionFactory.OPEN_NEW_WINDOW.create(window);
            openNewWindow.setText(Messages.UDIGWorkbenchAdvisor_newWindow_text);
            if (windowMenu.getItems().length > 0) {
                windowMenu.insertBefore(windowMenu.getItems()[0].getId(), openNewWindow);
            } else {
                windowMenu.add(openNewWindow);
            }
        }

        IMenuManager perspectiveMenu = windowMenu
                .findMenuUsingPath(ContributionItemFactory.PERSPECTIVES_SHORTLIST.getId());
        if (perspectiveMenu == null) {
            perspectiveMenu = new MenuManager(Messages.UDIGWorkbenchAdvisor_open_perspective,
                    ContributionItemFactory.PERSPECTIVES_SHORTLIST.getId());
            windowMenu.insertAfter(ActionFactory.OPEN_NEW_WINDOW.getId(), perspectiveMenu);
            IContributionItem perspectiveList = ContributionItemFactory.PERSPECTIVES_SHORTLIST
                    .create(window);
            perspectiveMenu.add(perspectiveList);
        }

        IMenuManager viewMenu = windowMenu
                .findMenuUsingPath(ContributionItemFactory.VIEWS_SHORTLIST.getId());
        if (viewMenu == null) {
            viewMenu = new MenuManager(Messages.UDIGWorkbenchAdvisor_show_view,
                    ContributionItemFactory.VIEWS_SHORTLIST.getId());
            windowMenu
                    .insertAfter(ContributionItemFactory.PERSPECTIVES_SHORTLIST.getId(), viewMenu);
            IContributionItem viewList = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
            viewMenu.add(viewList);
        }

        if (windowMenu.findUsingPath(ContributionItemFactory.OPEN_WINDOWS.getId()) == null) {
            // append this one to the end and we'll work backward from it
            windowMenu.add(ContributionItemFactory.OPEN_WINDOWS.create(window));
        }

        if (windowMenu.findUsingPath(ActionFactory.PREFERENCES.getId()) == null) {
            IAction preferences = ActionFactory.PREFERENCES.create(window);
            preferences.setText(Messages.UDIGWorkbenchAdvisor_preferences_text);
            windowMenu.insertBefore(ContributionItemFactory.OPEN_WINDOWS.getId(), preferences);
        }

        if (windowMenu.findUsingPath(IWorkbenchActionConstants.MB_ADDITIONS) == null) {
            windowMenu.insertBefore(ActionFactory.PREFERENCES.getId(), new GroupMarker(
                    IWorkbenchActionConstants.MB_ADDITIONS));
        }

        if (windowMenu.findUsingPath(ActionFactory.CLOSE_ALL_PERSPECTIVES.getId()) == null) {
            IAction closeAllPerspectives = ActionFactory.CLOSE_ALL_PERSPECTIVES.create(window);
            closeAllPerspectives.setText(Messages.UDIGWorkbenchAdvisor_closeAllPerspectives_text);
            windowMenu.insertBefore(IWorkbenchActionConstants.MB_ADDITIONS, closeAllPerspectives);
        }

        if (windowMenu.findUsingPath(ActionFactory.CLOSE_PERSPECTIVE.getId()) == null) {
            IAction closePerspective = ActionFactory.CLOSE_PERSPECTIVE.create(window);
            closePerspective.setText(Messages.UDIGWorkbenchAdvisor_closePerspective_text);
            windowMenu.insertBefore(ActionFactory.CLOSE_ALL_PERSPECTIVES.getId(), closePerspective);
        }

        if (windowMenu.findUsingPath(ActionFactory.RESET_PERSPECTIVE.getId()) == null) {
            IAction resetPerspective = ActionFactory.RESET_PERSPECTIVE.create(window);
            resetPerspective.setText(Messages.UDIGWorkbenchAdvisor_resetPerspective_text);
            windowMenu.insertBefore(ActionFactory.CLOSE_PERSPECTIVE.getId(), resetPerspective);
        }

        // Add the separators
        windowMenu.insertAfter(ActionFactory.OPEN_NEW_WINDOW.getId(), new Separator());
        windowMenu.insertAfter(ContributionItemFactory.VIEWS_SHORTLIST.getId(), new Separator());
        windowMenu.insertBefore(IWorkbenchActionConstants.MB_ADDITIONS, new Separator());
        windowMenu.insertAfter(IWorkbenchActionConstants.MB_ADDITIONS, new Separator());
    }

    private void fillHelpMenu( IWorkbenchWindow window, IMenuManager helpMenu ) {

        if (helpMenu.findUsingPath(ActionFactory.INTRO.getId()) == null) {
            IAction welcome = ActionFactory.INTRO.create(window);
            welcome.setText(Messages.UDIGWorkbenchAdvisor_welcome_text);
            if (helpMenu.getItems().length > 0) {
                helpMenu.insertBefore(helpMenu.getItems()[0].getId(), welcome);
            } else {
                helpMenu.add(welcome);
            }
        }

        if (helpMenu.findUsingPath(Constants.HELP_START) == null) {
            helpMenu
                    .insertAfter(ActionFactory.INTRO.getId(), new GroupMarker(Constants.HELP_START));
        }

        if (helpMenu.findUsingPath(ActionFactory.HELP_CONTENTS.getId()) == null) {
            IAction helpContents = ActionFactory.HELP_CONTENTS.create(window);
            helpContents.setText(Messages.UDIGWorkbenchAdvisor_helpContents_text);
            helpMenu.insertBefore(Constants.HELP_START, helpContents);
        }

        if (helpMenu.findUsingPath(Constants.HELP_END) == null) {
            helpMenu.insertAfter(Constants.HELP_START, new GroupMarker(Constants.HELP_END));
        }

        // Tips and tricks page would go after HELP_START

        if (helpMenu.findUsingPath(IWorkbenchActionConstants.MB_ADDITIONS) == null) {
            helpMenu.insertAfter(Constants.HELP_END, new GroupMarker(
                    IWorkbenchActionConstants.MB_ADDITIONS));
        }

        // Add the separators
        helpMenu.insertAfter(ActionFactory.INTRO.getId(), new Separator());
        helpMenu.insertBefore(Constants.HELP_START, new Separator());
        helpMenu.insertAfter(Constants.HELP_END, new Separator());
        // helpMenu.insertAfter(, new Separator());

        if (helpMenu.findUsingPath(ActionFactory.ABOUT.getId()) == null) {
            IAction about = ActionFactory.ABOUT.create(window);
            String pattern = Messages.UDIGWorkbenchAdvisor_aboutUDig_text;
            IProduct product = Platform.getProduct();
            String productName;
            if( product == null ){
            	UiPlugin.log("there is no product so default to uDig", null);
            	productName = "uDig";
            }else{
            	productName = product.getName();
            }
			about.setText(MessageFormat.format(pattern, productName));
            // About should always be at the bottom, so just append it to the menu
            helpMenu.add(about);
        }
    }
}
