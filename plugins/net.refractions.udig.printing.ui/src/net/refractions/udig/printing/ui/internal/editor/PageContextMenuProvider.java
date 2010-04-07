/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.printing.ui.internal.editor;

import net.refractions.udig.printing.ui.internal.PrintingPlugin;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Provides ...TODO summary sentence
 * <p>
 * TODO Description
 * </p><p>
 * Responsibilities:
 * <ul>
 * <li>
 * <li>
 * </ul>
 * </p><p>
 * Example Use:<pre><code>
 * PageContextMenuProvider x = new PageContextMenuProvider( ... );
 * TODO code example
 * </code></pre>
 * </p>
 * @author Richard Gould
 * @since 0.3
 */
public class PageContextMenuProvider extends ContextMenuProvider {

    private ActionRegistry registry;
    
    /**
     * Construct <code>PageContextMenuProvider</code>.
     *
     * @param viewer
     */
    public PageContextMenuProvider( EditPartViewer viewer, ActionRegistry registry ) {
        super(viewer);
        setActionRegistry(registry);
    }

    /**
     * TODO summary sentence for buildContextMenu ...
     * 
     * @see org.eclipse.gef.ContextMenuProvider#buildContextMenu(org.eclipse.jface.action.IMenuManager)
     * @param manager
     */
    @SuppressWarnings("unchecked")
    public void buildContextMenu( IMenuManager manager ) {
        GEFActionConstants.addStandardActionGroups(manager);
        
    	
    	IAction action = getActionRegistry().getAction(ActionFactory.UNDO.getId());
    	manager.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

    	action = getActionRegistry().getAction(ActionFactory.REDO.getId());
    	manager.appendToGroup(GEFActionConstants.GROUP_UNDO, action);
        
        for( BoxAction boxAction : PrintingPlugin.getBoxExtensionActions(null) ) {
            if( boxAction.isEnabled() )
                manager.appendToGroup(GEFActionConstants.GROUP_EDIT,boxAction);
        }
//    	
//    	DirectEditAction action2 = (DirectEditAction) getActionRegistry().getAction(GEFActionConstants.DIRECT_EDIT);
//        action2.getText();
//    	if (action2.isEnabled())
//    	    manager.appendToGroup(GEFActionConstants.GROUP_UNDO, action2);
//
//        action = getActionRegistry().getAction(EditMapAction.EDIT_MAP);
//    	if (action.isEnabled()) 
//    	    manager.appendToGroup(, action);
//    	
    	action = getActionRegistry().getAction(ActionFactory.DELETE.getId());
    	if (action.isEnabled())
    		manager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
    	

    	// Alignment Actions
/*    	MenuManager submenu = new MenuManager("Alignment Thing");

    	action = getActionRegistry().getAction(GEFActionConstants.ALIGN_LEFT);
    	if (action.isEnabled())
    		submenu.add(action);

    	action = getActionRegistry().getAction(GEFActionConstants.ALIGN_CENTER);
    	if (action.isEnabled())
    		submenu.add(action);

    	action = getActionRegistry().getAction(GEFActionConstants.ALIGN_RIGHT);
    	if (action.isEnabled())
    		submenu.add(action);
    		
    	submenu.add(new Separator());
    	
    	action = getActionRegistry().getAction(GEFActionConstants.ALIGN_TOP);
    	if (action.isEnabled())
    		submenu.add(action);

    	action = getActionRegistry().getAction(GEFActionConstants.ALIGN_MIDDLE);
    	if (action.isEnabled())
    		submenu.add(action);

    	action = getActionRegistry().getAction(GEFActionConstants.ALIGN_BOTTOM);
    	if (action.isEnabled())
    		submenu.add(action);

    	if (!submenu.isEmpty())
    		manager.appendToGroup(GEFActionConstants.GROUP_REST, submenu);
*/
    }

    private ActionRegistry getActionRegistry() {
        return registry;
    }
    private void setActionRegistry( ActionRegistry registry ) {
        this.registry = registry;
    }
}
