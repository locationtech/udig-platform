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
package org.locationtech.udig.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.Category;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.AbstractContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.services.IServiceLocator;
import org.locationtech.udig.core.internal.ExtensionPointList;
import org.locationtech.udig.core.logging.LoggingSupport;
import org.locationtech.udig.internal.ui.UiPlugin;

/**
 * Add additional operation menu contributions to the screen.
 * <p>
 * This is an experiment it may be too late; since it appears the workbench
 * window is already set up?
 *
 * @author Jody Garnett
 */
public class StartupOperations implements IStartup {

    @Override
    public void earlyStartup() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        //workbench.getDisplay().asyncExec(new Runnable() {
        //    public void run() {
        // IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        // if (window != null) {
        processOperations( workbench );
    }

    /**
     * Process operations for the provided scope.
     *
     * @param workbench
     * @param scope
     */
    protected void processOperations( IWorkbench workbench ){
        IHandlerService handlers = workbench.getService( IHandlerService.class );
        ICommandService commands = workbench.getService( ICommandService.class );
        IMenuService menuService = workbench.getService(IMenuService.class);
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();

        List<IConfigurationElement> list = ExtensionPointList.getExtensionPointList("org.locationtech.udig.ui.operation"); //$NON-NLS-1$
        List<IConfigurationElement> categoryElements = listCategories(list);
        if( categoryElements == null || categoryElements.isEmpty() ) return;

        for( IConfigurationElement element : categoryElements ) {
            final String ID = element.getAttribute("id");
            final String NAME = element.getName();
            final String DESCRIPTION = element.getName();
            List<IConfigurationElement> operationElements = listOperationsForCategory(list, ID );
            try {
                // Do not create operation category anymore; only worked for one window
                // categories.put(ID, new OperationCategory(element)); //$NON-NLS-1$

                // Create a Command Category
                Category category = commands.getCategory(ID);
                if( !category.isDefined()){
                    category.define(NAME, DESCRIPTION);
                }
                // TODO: Create an ActionSet

                // TODO: Create a Definition to Check the ActionSet

                // TODO: Create the MenuGroup
                AbstractContributionFactory categoryAdditions = operationsMenu( menuService, operationElements, "menu:nav?after=layer.ext", ID);
                menuService.addContributionFactory(categoryAdditions);
            } catch (Exception e) {
                LoggingSupport.log(UiPlugin.getDefault(), "Operation category " + ID + ":" + e, e);
            }
        }

        for( IConfigurationElement element : list ) {
            final String NAME = element.getName();
            final String ID = element.getAttribute("id");
            try {
                if (NAME.equals("category")) {//$NON-NLS-1$
                    continue;
                }
                /*
                Command command = commands.getCommand(ID);
                if( !command.isDefined()){
                    final String DESCRIPTION = element.getName();
                    final String CATEGORY = element.getAttribute("categoryId");

                    // Create the Command
                    Category category = commands.getCategory(CATEGORY);
                    command.define(NAME, DESCRIPTION, category );
                }
                IHandler handler = new OpHandler( element );
                handlers.activateHandler(ID, handler);
                */

            }
            catch (Exception e) {
                LoggingSupport.log(UiPlugin.getDefault(), "Operation " + ID + ":" + e, e);
            }
        }
    }
    /**
     * List all IConfigurationElements with name "category".
     * @param list
     * @return IConfigurationElments with name "category"
     */
    List<IConfigurationElement> listCategories( List<IConfigurationElement> list) {
        List<IConfigurationElement> results = new ArrayList<>();
        for( IConfigurationElement element : list ) {
            final String NAME = element.getName();
            final String ID = element.getAttribute("id");
            if (NAME.equals("category")) {//$NON-NLS-1$
                results.add( element );
            }
        }
        return results;
    }
    /**
     * List all IConfigurationElements operations that match the provided categoryId.
     *
     * @param list List of IConfigurationElement, assumed to come from operation extension point.
     * @param categoryId
     * @return List, perhaps empty, of IConfigurationElements
     */
    List<IConfigurationElement> listOperationsForCategory( List<IConfigurationElement> list, String categoryId) {
        List<IConfigurationElement> results = new ArrayList<>();
        for( IConfigurationElement element : list ) {
            final String NAME = element.getName();
            final String ID = element.getAttribute("id");
            if (NAME.equals("category")) {//$NON-NLS-1$
                continue;
            }
            final String CATEGORY = element.getAttribute("categoryId");
            if( CATEGORY != null && CATEGORY.equals(categoryId)){
                results.add( element );
            }
        }
        return results;
    }

    /**
     * This will produce an AbstractConfigurationFactory that adds a CommandContribution for
     * each operation in the indicated category.
     * <p>
     * The following are all related "categoryId":
     * <ul>
     * <li>actionSet - actionSet used to toggle this visibility of this contribution
     * <li>expression - true when actionSet is enabled
     * <li>
     * </ul>
     * @param list
     * @param locationURI
     */
    protected AbstractContributionFactory operationsMenu( IMenuService menuService, final List<IConfigurationElement> list, String locationURI, final String categoryId ){
        return new AbstractContributionFactory(locationURI,null){
            @Override
            public void createContributionItems( IServiceLocator serviceLocator,
                    IContributionRoot additions ) {

            }
        };
    }
}
