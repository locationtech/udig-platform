/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.internal.commands;

import net.refractions.udig.project.command.Command;
import net.refractions.udig.project.command.ErrorHandler;
import net.refractions.udig.project.command.UndoableCommand;
import net.refractions.udig.project.internal.ProjectPlugin;

/**
 * A basic ErrorHandler. It simple logs the exception API is this public?
 * 
 * @author jeichar
 * @since 0.2
 * @see ErrorHandler
 */
public class DefaultErrorHandler implements ErrorHandler {

    /**
     * @see net.refractions.udig.project.internal.command.ErrorHandler#handleError(net.refractions.udig.project.internal.command.MapCommand,
     *      Throwable)
     */
    public void handleError( Command command, Throwable e ) {
        ProjectPlugin.log(command.getName()+" failed to run", e); //$NON-NLS-1$
    }

    /**
     * @see net.refractions.udig.project.internal.command.ErrorHandler#handleRollbackError(net.refractions.udig.project.internal.command.UndoableCommand,
     *      Throwable)
     */
    public void handleRollbackError( UndoableCommand command, Throwable e ) {
        e.printStackTrace();
    }

}
