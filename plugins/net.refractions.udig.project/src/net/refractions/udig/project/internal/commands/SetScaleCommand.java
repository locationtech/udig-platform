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
