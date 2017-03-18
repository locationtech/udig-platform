/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.internal.ui;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import org.locationtech.udig.core.internal.ExtensionPointList;
import org.locationtech.udig.ui.Constants;
import org.locationtech.udig.ui.MenuBuilder;
import org.locationtech.udig.ui.UDIGMenuBuilder;
import org.locationtech.udig.ui.action.NewObjectContribution;
import org.locationtech.udig.ui.action.NewObjectDelegate;
import org.locationtech.udig.ui.action.NewObjectDelegateComparator;
import org.locationtech.udig.ui.internal.Messages;
import org.locationtech.udig.ui.preferences.PreferenceConstants;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * Public base class for configuring the action bars of a workbench window.
 * <p>
 * For UDIG 1.1: An application should leave this alone; and use the *org.eclipse.ui.menu* extension
 * point to slot commands into menus as needed. This ActionBarAdvisor provides the following basic
 * menus that should be applicable to all UDIG based applications:
 * <ul>
 * <li>
 * </ul>
 * If you are wondering about the Navigate, Layer and Data menus please go check out the
 * net.refractions.catalog.ui and net.refractions.project.ui.
 * <p>
 * For UDIG 1.0: An application should declare a subclass of <code>ActionBarAdvisor</code> and
 * override methods to configure a window's action bars to suit the needs of the particular
 * application.
 * </p>
 * <p>
 * The following advisor methods are called at strategic points in the
 * workbench's lifecycle (all occur within the dynamic scope of the call
 * to {@link PlatformUI#createAndRunWorkbench PlatformUI.createAndRunWorkbench}):
 * <ul>
 * <li><code>fillActionBars</code> - called after <code>WorkbenchWindowAdvisor.preWindowOpen</code>
 * to configure a window's action bars</li>
 * </ul>
 * </p>
 * 
 * @see WorkbenchWindowAdvisor#createActionBarAdvisor(IActionBarConfigurer)
 * @author cole.markham
 * @since 1.0.0
 */
public class UDIGActionBarAdvisor extends ActionBarAdvisor {
    /**
     * Strategy Object used to configure menus according to external preference. Considering that is
     * also the job of an ActionBarAdvisor we have some duplication going on here.
     */
    private MenuBuilder menuBuilder;
	/**
     * Default constructor
     * 
     * @param configurer
     */
    public UDIGActionBarAdvisor( IActionBarConfigurer configurer ) {
        super(configurer);
    }

    private MenuBuilder lookupMenuBuilder() {
        Class interfaceClass = MenuBuilder.class;
        String prefConstant = PreferenceConstants.P_MENU_BUILDER;
        String xpid = MenuBuilder.XPID;
        String idField = MenuBuilder.ATTR_ID;
        String classField = MenuBuilder.ATTR_CLASS;

        MenuBuilder mb = (MenuBuilder) UiPlugin.lookupConfigurationObject(interfaceClass,
                UiPlugin.getDefault().getPreferenceStore(), UiPlugin.ID, prefConstant, xpid,
                idField, classField);
        if (mb != null) {
            return mb;
        }
        return new UDIGMenuBuilder();
    }

    /**
     * Get the MenuFactory which will create the menus for this plugin
     * 
     * @return The MenuFactory singleton
     */
    protected MenuBuilder getMenuFactory() {
        if (menuBuilder == null) {
            menuBuilder = lookupMenuBuilder();
        }
        return menuBuilder;
    }

    @Override
    protected void fillCoolBar( ICoolBarManager coolBar ) {
        IWorkbenchWindow window = getActionBarConfigurer().getWindowConfigurer().getWindow();

        MenuBuilder override = getMenuFactory();
        if (override != null && !(override instanceof UDIGMenuBuilder)) {
            // Allows override; deprecated; please write your
            // own ActionBarAdvisor rather
            override.fillCoolBar(coolBar, window);
        } else {
            new UDIGMenuBuilder().fillCoolBar(coolBar, window);
        }
    }

    @Override
    protected void fillMenuBar( IMenuManager menuBar ) {
        menuBar.add(new GroupMarker(IWorkbenchActionConstants.WB_START));

        // Support use of MenuBuilder for RCP applications based on uDig
        // (org.eclipse.ui.menu is preferred!)
        MenuBuilder override = getMenuFactory();
        if (override != null && !(override instanceof UDIGMenuBuilder)) {
            IWorkbenchWindow window = getActionBarConfigurer().getWindowConfigurer().getWindow();
            override.fillMenuBar(menuBar, window);
            return;
        }
        
        MenuManager fileMenu = new MenuManager(Messages.UDIGWorkbenchAdvisor_file,
                IWorkbenchActionConstants.M_FILE);
        fillFileMenu(fileMenu);
        menuBar.add(fileMenu);

        IMenuManager editMenu = new MenuManager(Messages.UDIGWorkbenchAdvisor_edit,
                IWorkbenchActionConstants.M_EDIT);
        editMenu.add(new GroupMarker(IWorkbenchActionConstants.FIND_EXT));
        fillEditMenu(editMenu);
        menuBar.add(editMenu);

        if( true ){
            // TODO: phase these out with org.eclipse.ui.menus        
            IMenuManager navMenu = new MenuManager(Messages.UDIGWorkbenchAdvisor_navigationMenu,
                    Constants.M_NAVIGATE);
            fillNavigateMenu(navMenu);
            menuBar.add(navMenu);
    
            IMenuManager toolMenu = new MenuManager(Messages.UDIGWorkbenchAdvisor_tools,
                    Constants.M_TOOL);
            fillToolMenu(toolMenu);
            menuBar.add(toolMenu);
        }
        
        menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        
        IMenuManager windowMenu = new MenuManager(Messages.UDIGWorkbenchAdvisor_window,
                IWorkbenchActionConstants.M_WINDOW);
        fillWindowMenu(windowMenu);
        menuBar.add(windowMenu);

        IMenuManager helpMenu = new MenuManager(Messages.UDIGWorkbenchAdvisor_help,
                IWorkbenchActionConstants.M_HELP);
        fillHelpMenu(helpMenu);
        menuBar.add(helpMenu);
        
        menuBar.add(new GroupMarker(IWorkbenchActionConstants.WB_END));
        
        if( true ){
            // clue in operations about the window
            IWorkbenchWindow window = getActionBarConfigurer().getWindowConfigurer().getWindow();

            UiPlugin.getDefault().getOperationMenuFactory().setWindow(window);
        }
    }

    /**
     * Set up customary Navigate menu structure as defined by Constants.
     * <p>
     * The uDig navigate menu is a mash up between the traditional functionality
     * such as "showIn" to open views; along side commands to navigate around the
     * current map (complete with back / forward history like a web browser).
     * <pre>
     * navigate
     * navigate/navStart
     * navigate/zoom.ext
     * navigate/additions
     * navigate/bottom
     * navigate/navEnd
     * </pre>
     * 
     * @param menu
     */
    protected void fillNavigateMenu( IMenuManager menu ) {
        // menu.add(ActionFactory.BACKWARD_HISTORY.create(window));
        // menu.add(ActionFactory.FORWARD_HISTORY.create(window));
        menu.add(new GroupMarker(Constants.NAV_START));
        menu.add(new Separator());
        menu.add(new GroupMarker(Constants.NAV_ZOOM_EXT));

        menu.add(new Separator());
        menu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

        menu.add(new Separator());
        menu.add(new GroupMarker(Constants.NAV_BOTTOM));
        menu.add(new GroupMarker(Constants.NAV_END));
        menu.setVisible(true);
    }

    /**
     * Set up Tool menu, used to interact with Map Editor.
     * <pre>
     * tools
     * tools/wbStart
     * tools/zoom.ext
     * tools/additions
     * tools/wbEnd
     * </pre>
     * @param menu
     */
    protected void fillToolMenu( IMenuManager menu) {
        menu.add(new GroupMarker(IWorkbenchActionConstants.WB_START));
        menu.add(new GroupMarker(Constants.TOOL_ACTION));
        menu.add(new Separator());
        menu.add(new GroupMarker(Constants.TOOL_MODAL));
        menu.add(new Separator());
        menu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        menu.add(new GroupMarker(IWorkbenchActionConstants.WB_END));        
    }
    
    /**
     * Set up customary File menu structure as defined by IWorkBenchActionConstants.
     * <p>
     * We are focused on providing the usual "group markers" so that menu paths for action sets,
     * tools, operations or menus will work out okay (for this or *any* RCP application).
     * 
     * <pre>
     * file/fileStart
     * file/new.ext
     * file/new
     * file/project.ext
     * file/close.ext
     * file/close
     * file/save.ext
     * file/save
     * file/additions
     * file/print.ext
     * file/import.ext
     * file/import
     * file/export
     * file/mru
     * file/fileEnd
     * file/quit
     * </pre>
     * 
     * @param window
     * @param fileMenu
     */
    protected void fillFileMenu( IMenuManager fileMenu ) {
        IWorkbenchWindow window = getActionBarConfigurer().getWindowConfigurer().getWindow();

        fileMenu.add(new GroupMarker(Constants.FILE_START));

        IMenuManager newMenu = new MenuManager(Messages.UDIGWorkbenchAdvisor_new, ActionFactory.NEW
                .getId());
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

        fileMenu.add(newMenu);
        fileMenu.add(new GroupMarker(Constants.OPEN_EXT));
        fileMenu.add(new Separator());
        fileMenu.add(new GroupMarker(Constants.PROJECT_EXT));
        fileMenu.add(new Separator());
        fileMenu.add(new GroupMarker(Constants.CLOSE_EXT));
        fileMenu.add(ActionFactory.CLOSE.create(window));
        fileMenu.add(ActionFactory.CLOSE_ALL.create(window));
        fileMenu.add(new Separator());

        fileMenu.add(new GroupMarker(Constants.SAVE_EXT));
        fileMenu.add(ActionFactory.SAVE.create(window));
        fileMenu.add(ActionFactory.SAVE_ALL.create(window));
        fileMenu.add(new Separator());

        fileMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        fileMenu.add(new GroupMarker(ActionFactory.REFRESH.getId()));
        fileMenu.add(new GroupMarker(Constants.RENAME_EXT));
        fileMenu.add(new Separator());

        fileMenu.add(new GroupMarker(IWorkbenchActionConstants.PRINT_EXT));
        fileMenu.add(new Separator());

        fileMenu.add(ActionFactory.IMPORT.create(window));
        fileMenu.add(ActionFactory.EXPORT.create(window));
        fileMenu.add(new Separator());
        fileMenu.add(ActionFactory.PROPERTIES.create(window));
        
        fileMenu.add(new GroupMarker(Constants.CONFIG_EXT));
        fileMenu.add(new Separator());

        fileMenu.add(new GroupMarker(IWorkbenchActionConstants.MRU));

        fileMenu.add(new GroupMarker(IWorkbenchActionConstants.FILE_END));

        IWorkbenchAction quit = ActionFactory.QUIT.create(window);
        IContributionItem item = new ActionContributionItem(quit);
        item.setVisible(!Platform.OS_MACOSX.equals(Platform.getOS()));
        
        fileMenu.add(item);
    }

    /**
     * Define the Edit Menu according to RCP "custom".
     * <p>
     * Most of the "custom" here is recorded as part of IWorkbenchActionsConstants; we are doing the
     * bare minimum here; only positioning the "group markers" in the correct spot so the relative
     * menu path goodness will work for later plugin contributions (using org.eclipse.ui.menu
     * extensions).
     * 
     * <pre>
     * edit/editStart
     * edit/undo.ext
     * edit/cut.ext
     * edit/add.ext
     * edit/additions
     * edit/other
     * edit/commit.ext
     * edit/editEnd
     * </pre>
     * 
     * @param window
     * @param editMenu
     */
    protected void fillEditMenu( IMenuManager editMenu ) {
        IWorkbenchWindow window = getActionBarConfigurer().getWindowConfigurer().getWindow();

        editMenu.add(new GroupMarker(Constants.EDIT_START));

        editMenu.add(new GroupMarker(Constants.UNDO_EXT));
        editMenu.add(ActionFactory.UNDO.create(window));
        editMenu.add(ActionFactory.REDO.create(window));
        editMenu.add(new Separator());

        editMenu.add(new GroupMarker(Constants.CUT_EXT));
        editMenu.add(ActionFactory.CUT.create(window));
        editMenu.add(ActionFactory.COPY.create(window));
        editMenu.add(ActionFactory.PASTE.create(window));
        editMenu.add(new Separator());
        
        editMenu.add(ActionFactory.DELETE.create(window));
        editMenu.add(ActionFactory.SELECT_ALL.create(window));
        editMenu.add(new GroupMarker(Constants.ADD_EXT));
        editMenu.add(new Separator());
        
        editMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        editMenu.add(new GroupMarker(Constants.OTHER));
        editMenu.add(new Separator());
        
        editMenu.add(new GroupMarker(Constants.COMMIT_EXT));
        editMenu.add(new GroupMarker(Constants.EDIT_END));
    }

    private void fillLayerMenu( IMenuManager  menu) {
        menu.add(new GroupMarker(IWorkbenchActionConstants.WB_START));
        menu.add(new GroupMarker(Constants.LAYER_ADD_EXT));
        menu.add(new Separator());
        menu.add(new GroupMarker(Constants.LAYER_MAPGRAPHIC_EXT));
        menu.add(new GroupMarker(Constants.LAYER_MAPGRAPHIC_OTHER));
        menu.add(new Separator());
        menu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        menu.add(new GroupMarker(Constants.LAYER_EDIT_EXT));
        menu.add(new GroupMarker(IWorkbenchActionConstants.WB_END));        
    }
    
    /**
     * Define the Window Menu according to RCP "custom".
     * <p>
     * The window menu is mostly concerned with the care and feeding of application wide
     * customisations and settings; from access to application preferences to opening up views and
     * switching perspectives.
     * <p>
     * window/wbStart window/... window/additions window/wbEnd
     * 
     * @param windowMenu
     */
    protected void fillWindowMenu( IMenuManager windowMenu ) {
        IWorkbenchWindow window = getActionBarConfigurer().getWindowConfigurer().getWindow();

        windowMenu.add(new GroupMarker(IWorkbenchActionConstants.WB_START));

        //IAction openNewWindow = ActionFactory.OPEN_NEW_WINDOW.create(window);
        //openNewWindow.setText(Messages.UDIGWorkbenchAdvisor_newWindow_text);
        //windowMenu.add(openNewWindow);
        
        //windowMenu.add( new Separator());

        IMenuManager perspectiveMenu = new MenuManager(
                Messages.UDIGWorkbenchAdvisor_open_perspective,
                ContributionItemFactory.PERSPECTIVES_SHORTLIST.getId());
        perspectiveMenu.add(ContributionItemFactory.PERSPECTIVES_SHORTLIST.create(window));
        windowMenu.add(perspectiveMenu);

        IMenuManager viewMenu = new MenuManager(Messages.UDIGWorkbenchAdvisor_show_view,
                    ContributionItemFactory.VIEWS_SHORTLIST.getId());
        viewMenu.add(ContributionItemFactory.VIEWS_SHORTLIST.create(window));
        windowMenu.add(viewMenu);
        windowMenu.add( new Separator());
        
        IAction resetPerspective = ActionFactory.RESET_PERSPECTIVE.create(window);
        resetPerspective.setText(Messages.UDIGWorkbenchAdvisor_resetPerspective_text);
        windowMenu.add(resetPerspective);
        
        IAction closePerspective = ActionFactory.CLOSE_PERSPECTIVE.create(window);
        closePerspective.setText(Messages.UDIGWorkbenchAdvisor_closePerspective_text);
        windowMenu.add(closePerspective);
        
        IAction closeAllPerspectives = ActionFactory.CLOSE_ALL_PERSPECTIVES.create(window);
        closeAllPerspectives.setText(Messages.UDIGWorkbenchAdvisor_closeAllPerspectives_text);
        windowMenu.add(closeAllPerspectives);        
        
        windowMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
                
        windowMenu.add( new Separator());
        
        IAction preferences = ActionFactory.PREFERENCES.create(window);
        preferences.setText(Messages.UDIGWorkbenchAdvisor_preferences_text);
        IContributionItem item = new ActionContributionItem(preferences);
        item.setVisible(!Platform.OS_MACOSX.equals(Platform.getOS()));
        
        windowMenu.add(item);
        
        windowMenu.add(ContributionItemFactory.OPEN_WINDOWS.create(window));
        
        windowMenu.add(new GroupMarker(IWorkbenchActionConstants.WB_END));        
    }
    
    protected void fillHelpMenu( IMenuManager helpMenu ) {
        IWorkbenchWindow window = getActionBarConfigurer().getWindowConfigurer().getWindow();

        boolean hasIntro = window
                .getWorkbench().getIntroManager().hasIntro();
        if (hasIntro) {
            if (helpMenu.findUsingPath(ActionFactory.INTRO.getId()) == null) {
                IAction welcome = ActionFactory.INTRO.create(window);
                welcome.setText(Messages.UDIGWorkbenchAdvisor_welcome_text);
                if (helpMenu.getItems().length > 0) {
                    helpMenu.insertBefore(helpMenu.getItems()[0].getId(), welcome);
                } else {
                    helpMenu.add(welcome);
                }
            }
        } else {
            Separator welcome = new Separator(ActionFactory.INTRO.getId());
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

        addAboutItem(helpMenu, window);
    }

    private void addAboutItem( IMenuManager helpMenu, IWorkbenchWindow window ) {
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
	        IContributionItem item = new ActionContributionItem(about);
	        item.setVisible(!Platform.OS_MACOSX.equals(Platform.getOS()));
	        
	        helpMenu.add(item);
        }
    }
}
