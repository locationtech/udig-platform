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

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Command for setting the applicability of a layer.  
 * 
 * @see ILayer#isApplicable(String)
 * @author Jesse
 * @since 1.0.0
 */
public class SetApplicabilityCommand extends AbstractCommand implements UndoableMapCommand {

    private boolean newValue;
    private String applicabilityId;
    private ILayer layer;
    private boolean oldValue;

    public SetApplicabilityCommand( ILayer layer, String applicabilityId, boolean newValue ) {
        this.layer=layer;
        this.applicabilityId=applicabilityId;
        this.newValue=newValue;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(Messages.SetApplicabilityCommand_name,3); 
        monitor.worked(1);
        this.oldValue=layer.isApplicable(applicabilityId);
        if( oldValue==newValue)
            return;
        ((Layer)layer).setApplicable(applicabilityId, newValue);
        monitor.done();
    }

    public String getName() {
        return Messages.SetApplicabilityCommand_name; 
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(Messages.SetApplicabilityCommand_name,3); 
        monitor.worked(1);
        if( oldValue==newValue)
            return;
        ((Layer)layer).setApplicable(applicabilityId, oldValue);
        monitor.done();
    }

}
