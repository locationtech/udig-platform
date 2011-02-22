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
package net.refractions.udig.tool.info.internal;

import net.refractions.udig.project.ui.tool.IToolHandler;
import net.refractions.udig.project.ui.tool.Tool;
import net.refractions.udig.tool.info.DistanceTool;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IStatusLineManager;

public class DistanceToolCommandHandler extends AbstractHandler implements IToolHandler {

    private DistanceTool tool;
    private String current;
    private final static String ID = "net.refractions.udig.tool.edit.clearAction"; //$NON-NLS-1$
    public void setTool( Tool tool ) {
        this.tool = (DistanceTool) tool;
    }

    public void setCurrentCommandId( String currentCommandId ) {
        this.current = currentCommandId;
    }

    public Object execute( ExecutionEvent event ) throws ExecutionException {
        if (ID.equals(current)) {
            tool.reset();
            final IStatusLineManager statusBar = tool.getContext().getActionBars()
                    .getStatusLineManager();
            if (statusBar != null) {
                tool.getContext().updateUI(new Runnable(){
                    public void run() {
                        statusBar.setErrorMessage(null);
                        statusBar.setMessage(null);
                    }
                });

            }
        }
        return null;
    }

}
