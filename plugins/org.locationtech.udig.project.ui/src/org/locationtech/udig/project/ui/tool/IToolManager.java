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
package org.locationtech.udig.project.ui.tool;

import java.util.List;

import org.locationtech.udig.project.ui.internal.MapPart;
import org.locationtech.udig.project.ui.internal.tool.display.ActionToolCategory;
import org.locationtech.udig.project.ui.internal.tool.display.MenuToolCategory;
import org.locationtech.udig.project.ui.internal.tool.display.ModalToolCategory;
import org.locationtech.udig.project.ui.internal.tool.display.ToolCategory;
import org.locationtech.udig.project.ui.internal.tool.display.ToolProxy;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.SubCoolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;

public interface IToolManager {
    
    final String XPID = "org.locationtech.udig.project.ui.toolManagers"; //$NON-NLS-1$
    /**
     * Points to id field of extension point attribute
     */
    final String ATTR_ID = "id"; //$NON-NLS-1$
    
    /**
     * Points to class field of extension point attribute
     */
    final String ATTR_CLASS = "class"; //$NON-NLS-1$
    
    /**
     * Preference constant that can used to set and look up the default 
     * IToolManager. This can be set in plugin_customization.ini with the key
     * "org.locationtech.udig.project.ui/toolManager".
     */
    final String P_TOOL_MANAGER = "toolManager"; //$NON-NLS-1$

    void setCurrentEditor( MapPart editor );

    /**
     * Adds an Action that executes a tool to the toolbar.
     * 
     * @param action
     */
    void addToolAction( IAction action );

    /**
     * Creates a action that acts as a proxy for the tool in the editor toolbar.
     * <p>
     * The client code must set the name image descriptor etc... of the Action
     * </p>
     * 
     * @param toolID the id of the tool
     * @param categoryID the category the tool is part of
     * @return a proxy action that can be put in other toolbars
     */
    IAction createToolAction( final String toolID, final String categoryID );

    void contributeToMenu( IMenuManager manager );
    
    
    /**
     * Contributes items from current active modal tool to the
     * context menu if the modal tool implements <code>IContextMenuContributionTool</code>
     * interface.
     * 
     * @param manager
     *      a context menu manager from MapEditor.
     */
    void contributeActiveModalTool(IMenuManager manager );

    /**
     * Retrieves the redo action that is used by much of the map components such as the MapEditor
     * and the LayersView. redoes the last undone command sent to the currently active map.
     */
    IAction getREDOAction();

    void setREDOAction( IAction action, IWorkbenchPart part );

    /**
     * Retrieves the undo action that is used by much of the map components such as the MapEditor
     * and the LayersView. Undoes the last command sent to the currently active map.
     * 
     * @param part
     */
    IAction getUNDOAction();

    void setUNDOAction( IAction action, IWorkbenchPart part );

    /**
     * Retrieves the forward navigation action that is used by much of the map components such as
     * the MapEditor and the LayersView. Executes the last undone Nav command on the current map.
     */
    IAction getFORWARD_HISTORYAction();

    void setFORWARDAction( IAction action, IWorkbenchPart part );

    /**
     * Registers keybindings for tools and cut/paste with the workbench part
     *
     * @param part
     */
    void registerActionsWithPart( IWorkbenchPart part );
    /**
     * Unregisters keybindings for tools and cut/paste with the workbench part
     *
     * @param part
     */
    void unregisterActions( IWorkbenchPart  part );
    /**
     * Retrieves the backward navigation action that is used by much of the map components such as
     * the MapEditor and the LayersView. Undoes the last Nav command set to the current map.
     * 
     * @param part
     */
    IAction getBACKWARD_HISTORYAction();

    void setBACKAction( IAction action, IWorkbenchPart part );

    IAction getCUTAction( IWorkbenchPart part );

    void setCUTAction( IAction action, IWorkbenchPart part );

    IAction getCOPYAction( final IWorkbenchPart part );

    void setCOPYAction( IAction action, IWorkbenchPart part );

    IAction getPASTEAction( IWorkbenchPart part );

    void setPASTEAction( IAction action, IWorkbenchPart part );

    IAction getDELETEAction();

    void setDELETEAction( IAction action, IWorkbenchPart part );

    IAction getENTERAction();
    
    IAction getZOOMTOSELECTEDAction();

    /**
     * Adds both action tools and modal tools to the manager
     * @deprecated
     * 
     * @param cbmanager
     * @param bars
     * @see org.locationtech.udig.project.ui.tool.ModalTool
     * @see org.locationtech.udig.project.ui.tool.ActionTool
     * 
     */
    void contributeToCoolBar( SubCoolBarManager cbmanager, IActionBars bars );
    
    
    /**
     * Adds action tools contribution items to the toolbar.
     * <p>
     * The actual toolbar UI elements are created and managed by the framework, IToolManager
     * just adds action tools as contributions to the specified <code>IToolBarManager</code>.
     * 
     * @param toolManager
     * @param bars
     */
    void contributeActionTools( IToolBarManager toolBarManager, IActionBars bars );
    
    /**
     * Adds modal tools contribution items to the toolbar.
     * <p>
     * The actual toolbar UI elements are created and managed by the framework, IToolManager
     * just adds action tools as contributions to the specified <code>IToolBarManager</code>.
     * 
     * @param toolManager
     * @param bars
     */
    void contributeModalTools( IToolBarManager toolBarManager, IActionBars bars );

    /**
     * Contributes the common global actions.
     * 
     * @param bars
     */
    void contributeGlobalActions( IWorkbenchPart part, IActionBars bars );

    /**
     * Returns the tool identified by an id and a category.  This action cannot be modified in any way
     * or it will throw an {@link UnsupportedOperationException}, but it can be ran with either
     * {@link IAction#run()} or {@link IAction#runWithEvent(org.eclipse.swt.widgets.Event)}.
     * 
     * @param toolID the id of the tool to find
     * @param categoryID the id of the category the tool is part of
     * @return the tool identified or null if the tool does not exist.
     * @deprecated since 1.1, use getToolAction()
     */
    IAction getTool( String toolID, String categoryID );
    
    /**
     * Returns the tool identified by an id and a category.  This action cannot be modified in any way
     * or it will throw an {@link UnsupportedOperationException}, but it can be ran with either
     * {@link IAction#run()} or {@link IAction#runWithEvent(org.eclipse.swt.widgets.Event)}.
     * 
     * @param toolID the id of the tool to find
     * @param categoryID the id of the category the tool is part of
     * @return the tool identified or null if the tool does not exist.
     */
    IAction getToolAction(String toolID, String categoryID);

    /**
     * Returns the list of categories containing modal tools.
     * 
     * @return the list of categories containing modal tools.
     */
    List<ModalToolCategory> getModalToolCategories();

    /**
     * Returns the tool category that is currently active.
     * 
     * @return the tool category that is currently active.
     */
    ToolCategory getActiveCategory();

    /**
     * This allows for customized operation menus that are based on the currently selected tool.
     * 
     * @param selection the selection to find operations for.
     */
    MenuManager createOperationsContextMenu(ISelection selection);

    /**
     * Returns current active tool implementation object.
     * 
     * @return
     */
    Tool getActiveTool();
    
    
    /**
     * Finds tool proxy and returns the actual tool implementation
     * object. If the tool has not been loaded yet, it is done immediatly by
     * tool proxy and the implementation is returned.
     * 
     * @param toolID the tool ID from extension registry.
     * @return
     */
    Tool findTool(String toolID);
    
    
    /**
     * Searches for the <code>Cursor</code> object by ID.
     * The <code>cursorID</code> is a custom ID from extension
     * registry or a constant from <code>ModatTool</code> interface for
     * systems cursors.
     * 
     * @param cursorID
     * @return
     */
    Cursor findToolCursor(String cursorID);

    /**
     * @param id Category id
     * @return ModalToolCategory for given categoryId, null if it doesn't exists
     */
    ModalToolCategory findModalCategory(String categoryId);

    /**
     * @param categoryId Category id
     * @return MenuToolCategory for given categoryId, null if it doesn't exists
     */
    MenuToolCategory findMenuCategory(String categoryId);

    /**
     * @param categoryId Category id
     * @return ActionToolCategory for given categoryId, null if it doesn't exists
     */
    ActionToolCategory findActionCategory(String categoryId);

    /**
     * @return List of ActiveToolCategories
     */
    List<ActionToolCategory> getActiveToolCategories();

    /**
     * @return active ToolProxy or null if there isn't any
     */
    ToolProxy getActiveToolProxy();

    /**
     * @param toolProxy ToolProxy to set as active ToolProxy
     */
    void setActiveModalToolProxy(ToolProxy toolProxy);

}
