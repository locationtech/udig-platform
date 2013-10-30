/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.internal;

import net.refractions.udig.project.ui.tool.AbstractToolCommandHandler;
import net.refractions.udig.project.ui.tool.ActionTool;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * Handles zoom in, out and show all commands
 * @author Jesse
 * @since 1.1.0
 */
public class ZoomHandler extends AbstractToolCommandHandler {

    public Object execute( ExecutionEvent event ) throws ExecutionException {
            ((ActionTool)getCurrentTool()).run();
        return null;
    }

}
