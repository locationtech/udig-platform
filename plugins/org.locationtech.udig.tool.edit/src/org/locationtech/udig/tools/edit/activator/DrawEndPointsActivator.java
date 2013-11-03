/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.activator;

import org.locationtech.udig.core.IProvider;
import org.locationtech.udig.tools.edit.Activator;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.commands.DrawEndPointsCommand;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;

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
