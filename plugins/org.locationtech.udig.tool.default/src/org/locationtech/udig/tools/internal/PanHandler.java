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
package org.locationtech.udig.tools.internal;

import org.locationtech.udig.project.ui.tool.IToolHandler;
import org.locationtech.udig.project.ui.tool.Tool;

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
    
    private static PanTool TOOL;
    private String id;
    private static final String LEFT = "org.locationtech.udig.tools.panLeftCommand"; //$NON-NLS-1$
    private static final String RIGHT = "org.locationtech.udig.tools.panRightCommand"; //$NON-NLS-1$
    private static final String UP = "org.locationtech.udig.tools.panUpCommand"; //$NON-NLS-1$
    private static final String DOWN = "org.locationtech.udig.tools.panDownCommand"; //$NON-NLS-1$
    private static NavigationUpdateThread PANNER=NavigationUpdateThread.getUpdater();
    
    public void setTool( Tool tool ) {
        TOOL=(PanTool) tool;
    }

    /**
     * @see org.locationtech.udig.project.ui.tool.IToolHandler#setCurrentCommandId(java.lang.String)
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
