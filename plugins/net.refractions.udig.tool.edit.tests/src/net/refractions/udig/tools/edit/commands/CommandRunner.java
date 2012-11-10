/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit.commands;

import java.lang.reflect.InvocationTargetException;

import net.refractions.udig.project.command.Command;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * Executes a Command
 * @author Jesse
 * @since 1.1.0
 */
public class CommandRunner implements IRunnableWithProgress {
    Command command;
    
    public CommandRunner( Command command ) {
        super();
        this.command = command;
    }

    public void run( IProgressMonitor monitor ) throws InvocationTargetException,
            InterruptedException {
        try {
            command.run(monitor);
        } catch (Exception e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
    }

}
