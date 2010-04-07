/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
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
