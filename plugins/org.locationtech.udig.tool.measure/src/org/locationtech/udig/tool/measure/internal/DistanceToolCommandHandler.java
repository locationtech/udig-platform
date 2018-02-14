/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2018, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tool.measure.internal;

import org.locationtech.udig.project.ui.tool.IToolHandler;
import org.locationtech.udig.project.ui.tool.Tool;
import org.locationtech.udig.tool.measure.DistanceTool;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IActionBars2;

public class DistanceToolCommandHandler extends AbstractHandler implements IToolHandler {

    private DistanceTool tool;
    private String current;
    private final static String ID = "org.locationtech.udig.tool.edit.clearAction"; //$NON-NLS-1$
    public void setTool( Tool tool ) {
        this.tool = (DistanceTool) tool;
    }

    public void setCurrentCommandId( String currentCommandId ) {
        this.current = currentCommandId;
    }

    public Object execute( ExecutionEvent event ) throws ExecutionException {
        if (ID.equals(current)) {
            tool.reset();
            IActionBars2 actionBars = tool.getContext().getActionBars();
            if (actionBars == null) {
                return null;
            }
            final IStatusLineManager statusBar = actionBars.getStatusLineManager();
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
