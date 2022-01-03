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

import org.locationtech.udig.project.command.navigation.ZoomExtentCommand;
import org.locationtech.udig.project.ui.tool.AbstractActionTool;
import org.locationtech.udig.project.ui.tool.AbstractTool;

/**
 * This class Provides zoom box and click functionality.
 * 
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class ZoomExtent extends AbstractActionTool{
    
    /**
     * @see AbstractTool#AbstractTool(int)
     */
    public ZoomExtent() {
        super();
    }

    /**
     * @see org.locationtech.udig.project.ui.tool.ActionTool#run()
     */
    public void run() {
    	NavigationUpdateThread.getUpdater().cancel();
    	getContext().sendASyncCommand(new ZoomExtentCommand());
    }

    public void dispose() {
        // do nothing
    }
}
