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
package net.refractions.udig.tools.edit.commands;

import java.lang.reflect.InvocationTargetException;

import net.refractions.udig.project.command.UndoableCommand;

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
