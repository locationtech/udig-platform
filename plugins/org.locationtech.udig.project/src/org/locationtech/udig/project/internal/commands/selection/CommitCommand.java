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
package org.locationtech.udig.project.internal.commands.selection;

import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * MapCommand that commits the current Transaction.
 * 
 * @author jgarnett
 * @since 0.6.0
 */
public class CommitCommand extends AbstractCommand {

    /*
     * @see org.locationtech.udig.project.command.MapCommand#run()
     */
    public void run( IProgressMonitor monitor ) throws Exception {
        getMap().getEditManagerInternal().commitTransaction();
    }

    /*
     * @see org.locationtech.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return Messages.CommitCommand_commitCommandName; 
    }

}
