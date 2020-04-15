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
package org.locationtech.udig.project.ui.internal.tool.display;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.locationtech.udig.project.ui.internal.MapToolEntry;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;

/**
 * The handler for tool commands.
 * <p>
 * This handler checks in two places:
 * <ul>
 * <li>It checks the category contributions; in order to hunt down a menu or toolbar contribution to run
 * </li>
 * <li>It checks the category ToolP
 * The handler for tool commands.
 * 
 * @author jeichar
 * @since 0.9.0
 * @version 1.3.0
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
                    if( !category.getId().equals( entry.getCategoryId() )){
                        continue; // tool is not from our category
                    }
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
