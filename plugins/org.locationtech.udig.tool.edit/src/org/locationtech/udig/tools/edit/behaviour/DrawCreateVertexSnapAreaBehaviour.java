/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.behaviour;

import org.locationtech.udig.core.IProvider;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.commands.IDrawCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EventBehaviour;
import org.locationtech.udig.tools.edit.EventType;
import org.locationtech.udig.tools.edit.commands.DrawSnapAreaCommand;
import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.SnapBehaviour;

/**
 * Shows the snap area around the cursor if snapping is on.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class DrawCreateVertexSnapAreaBehaviour implements EventBehaviour {

    private IDrawCommand command;
    Provider provider=new Provider();
    private static class Provider implements IProvider<Point>{
        MapMouseEvent e;
        public Point get(Object... params) {
            return Point.valueOf(e.x, e.y);
        }
        
    }
    
    public UndoableMapCommand getCommand( EditToolHandler handler, final MapMouseEvent e,
            EventType eventType ) {
        provider.e=e;
        boolean exiting=EventType.EXITED==eventType;
        boolean snapping = PreferenceUtil.instance().getSnapBehaviour()!=SnapBehaviour.OFF&&PreferenceUtil.instance().getSnapBehaviour()!=SnapBehaviour.GRID;
        if( command==null && snapping && !exiting){
            command=new DrawSnapAreaCommand(provider);
            handler.getContext().sendASyncCommand(command);
            handler.getDrawCommands().add(command);
        }else if( !snapping ||exiting ){
            command.setValid(false);
            handler.getDrawCommands().remove(command);
            command=null;
        }
        handler.repaint();
        return null;
    }

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        boolean snapOn = PreferenceUtil.instance().getSnapBehaviour()!=SnapBehaviour.OFF&&PreferenceUtil.instance().getSnapBehaviour()!=SnapBehaviour.GRID;
        boolean mouseMoving = (eventType==EventType.MOVED || eventType==EventType.DRAGGED || eventType!=EventType.EXITED);
        boolean shouldTurnOff = (command!=null
                &&eventType!=EventType.HOVERED);
        return shouldTurnOff || (snapOn && mouseMoving);
    }

}
