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
package net.refractions.udig.tools.edit.activator;

import net.refractions.udig.core.IProvider;
import net.refractions.udig.tools.edit.Activator;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.commands.DrawEndPointsCommand;
import net.refractions.udig.tools.edit.support.PrimitiveShape;

/**
 * Adds a draw command to the viewport model that will draw the end points of the 
 * current shape if the current shape is a LINE
 * 
 * @author jones
 * @since 1.1.0
 */
public class DrawEndPointsActivator implements Activator {

    
    private DrawEndPointsCommand drawEndPoints;

    public void activate( final EditToolHandler handler ) {
        
        drawEndPoints=new DrawEndPointsCommand( handler.getMouseTracker(), new CurrentProvider(handler) );
        handler.getDrawCommands().add(drawEndPoints);
        handler.getContext().sendASyncCommand(drawEndPoints);    }

    public void deactivate( EditToolHandler handler ) {
        drawEndPoints.setValid(false);
        drawEndPoints=null;
    }

    public void handleActivateError( EditToolHandler handler, Throwable error ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

    public void handleDeactivateError( EditToolHandler handler, Throwable error ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }
    
    private static class CurrentProvider implements IProvider<PrimitiveShape>{
        private final EditToolHandler handler;
        
        public CurrentProvider( final EditToolHandler handler ) {
            this.handler = handler;
        }

        public PrimitiveShape get(Object... params) {
            return handler.getCurrentShape();
        }
        
    }

}
