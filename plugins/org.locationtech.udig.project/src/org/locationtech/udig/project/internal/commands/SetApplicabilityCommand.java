/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.commands;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.Interaction;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Command for setting the applicability of a layer.  
 * 
 * @see ILayer#getInteraction(String)
 * @author Jesse
 * @since 1.0.0
 */
public class SetApplicabilityCommand extends AbstractCommand implements UndoableMapCommand {

    private boolean newValue;
    private Interaction applicabilityId;
    private ILayer layer;
    private boolean oldValue;

    public SetApplicabilityCommand( ILayer layer, Interaction applicabilityId, boolean newValue ) {
        this.layer=layer;
        this.applicabilityId=applicabilityId;
        this.newValue=newValue;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(Messages.SetApplicabilityCommand_name,3); 
        monitor.worked(1);
        this.oldValue=layer.getInteraction(applicabilityId);
        if( oldValue==newValue)
            return;
        ((Layer)layer).setInteraction(applicabilityId, newValue);
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
        ((Layer)layer).setInteraction(applicabilityId, oldValue);
        monitor.done();
    }

}
