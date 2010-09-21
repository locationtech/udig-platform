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
