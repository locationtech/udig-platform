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
package net.refractions.udig.tools.edit.behaviour;

import net.refractions.udig.core.IProvider;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.commands.IDrawCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventBehaviour;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.commands.DrawSnapAreaCommand;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.SnapBehaviour;

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
