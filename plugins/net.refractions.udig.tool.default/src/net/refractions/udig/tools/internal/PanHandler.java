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
package net.refractions.udig.tools.internal;

import net.refractions.udig.project.ui.tool.IToolHandler;
import net.refractions.udig.project.ui.tool.Tool;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Handles the pan right,left,up and down commands
 * 
 * @author jeichar
 * @since 0.6.0
 */
public class PanHandler extends AbstractHandler implements IToolHandler {
    
    private static Pan TOOL;
    private String id;
    private static final String LEFT = "net.refractions.udig.tools.panLeftCommand"; //$NON-NLS-1$
    private static final String RIGHT = "net.refractions.udig.tools.panRightCommand"; //$NON-NLS-1$
    private static final String UP = "net.refractions.udig.tools.panUpCommand"; //$NON-NLS-1$
    private static final String DOWN = "net.refractions.udig.tools.panDownCommand"; //$NON-NLS-1$
    private static NavigationUpdateThread PANNER=NavigationUpdateThread.getUpdater();
    
    public void setTool( Tool tool ) {
        TOOL=(Pan) tool;
    }

    /**
     * @see net.refractions.udig.project.ui.tool.IToolHandler#setCurrentCommandId(java.lang.String)
     */
    public void setCurrentCommandId( String currentCommandId ) {
        id=currentCommandId;
    }

    public Object execute( ExecutionEvent event ) throws ExecutionException {
        if( id.equals(LEFT) )
            PANNER.left(TOOL.getContext(), 1000);
        if( id.equals(RIGHT) )
            PANNER.right(TOOL.getContext(), 1000);
        if( id.equals(UP) )
            PANNER.up(TOOL.getContext(), 1000);
        if( id.equals(DOWN) )
            PANNER.down(TOOL.getContext(), 1000);
        return null;
    }
    

}
