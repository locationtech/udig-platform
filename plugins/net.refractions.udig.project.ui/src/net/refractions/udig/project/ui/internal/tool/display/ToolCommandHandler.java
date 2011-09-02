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
package net.refractions.udig.project.ui.internal.tool.display;

import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.MapToolEntry;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.project.ui.tool.IToolManager;
import net.refractions.udig.project.ui.tool.ModalTool;
import net.refractions.udig.project.ui.tool.Tool;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.swt.graphics.Cursor;

/**
 * The handler for tool commands.
 * <p>
 * This handler checks in two places:
 * <ul>
 * <li>It checks the category contributions; in order to hunt down a menu or toolbar contributino to run
 * </li>
 * <li>It checks the category ToolP
 * The handler for tool commands.
 * 
 * @author jeichar
 * @since 0.9.0
 */
public class ToolCommandHandler extends AbstractHandler {

    ModalToolCategory category;

    /**
     * Construct <code>ToolCommandHandler</code>.
     */
    public ToolCommandHandler( ModalToolCategory category ) {
        this.category = category;
    }
    
    /**
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    public Object execute( ExecutionEvent event ) {
        if( category != null && category.getContribution() != null ){
            // working through a toolbar drop down cateogry thing
            if (category.getContribution().isChecked()) {
                category.getContribution().incrementSelection();
            }
            category.getContribution().runCurrentTool();
        }
        else if( category != null && category.getContainer() != null ){
            MapToolEntry first = null;
            MapToolEntry victim = null;
            for( Object child : category.getContainer().getChildren() ){
                if( child instanceof MapToolEntry ){
                    MapToolEntry entry = (MapToolEntry) child;                    
                    String toolId = entry.getId();
                    ToolProxy proxy = entry.getMapToolProxy();
                    
                    if( !proxy.isEnabled() ){
                        continue; // skip disabled tools
                    }
                    
                    if( first == null ){
                        first = entry; // this will be what is selected unless we have a 
                    }
                    if( victim == null ){ // if we have not found a victim to activate yet
                        victim = entry; 
                    }
                    if( proxy.isActive() ){
                        // we have a winner!
                        victim = null; // we need to find the next victim after this winner
                    }
                }
            }
            if( victim == null ){
                victim = first; // we were at the end of the list; cycle back to first
            }
            
            // okay victim is our new "Active" tool; how do we update the map an palette to use it?
            if( victim != null ){
                if( victim.getMapToolProxy() != null ){
                    victim.getMapToolProxy().run();
                }
            }
        }
        else {
            ProjectUIPlugin.trace(ToolCommandHandler.class, "Command Handler unable to find matching PalletteContainer or ModalToolCateogry: "+event, null);
        }
        return null;
    }

}
