/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.commands;

import java.lang.reflect.InvocationTargetException;

import org.locationtech.udig.project.command.UndoableCommand;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * Undoes a command
 * @author Jesse
 * @since 1.1.0
 */
public class CommandUndoer implements IRunnableWithProgress {

    UndoableCommand command;
    
    public CommandUndoer( UndoableCommand command ) {
        super();
        this.command = command;
    }

    public void run( IProgressMonitor monitor ) throws InvocationTargetException,
            InterruptedException {
        try {
            command.rollback(monitor);
        } catch (Exception e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
    }

}
