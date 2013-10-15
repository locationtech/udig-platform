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
package net.refractions.udig.project.internal.commands.edit;

import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A command that rollsback current changes.
 * 
 * @author jgarnett
 * @since 0.6.0
 */
public class RollbackCommand extends AbstractEditCommand {

    /*
     * @see net.refractions.udig.project.command.MapCommand#run()
     */
    public void run( IProgressMonitor monitor ) throws Exception {
        map.getEditManagerInternal().rollbackTransaction();
    }

    /*
     * @see net.refractions.udig.project.command.MapCommand#copy()
     */
    public MapCommand copy() {
        return new RollbackCommand();
    }

    /*
     * @see net.refractions.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return Messages.RollbackCommand_name; 
    }

}
