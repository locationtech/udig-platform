/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.internal.commands;

import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableCommand;
import net.refractions.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Sets the scale denominator of the map.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class SetScaleCommand extends AbstractCommand implements UndoableCommand {

    private double oldScale;
    private double newScale;
    /**
     * 
     * @param newScale Scale Denominator
     */
    public SetScaleCommand( double newScale ) {
        this.newScale = newScale;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        getMap().getViewportModelInternal().setScale(oldScale);
    }

    public String getName() {
        return Messages.SetScaleCommand_name;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        this.oldScale=getMap().getViewportModel().getScaleDenominator();
        getMap().getViewportModelInternal().setScale(newScale);
    }

}
