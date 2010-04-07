/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.command;

/**
 * MapCommand Managers have error handles registered that are notified when a command throws an
 * exception. Error handlers know what to do with the exception.
 * <p>
 * An example is a Transaction Error handler which
 * 
 * @author jeichar
 * @since 0.3
 */
public interface ErrorHandler {
    /**
     * Handles an error that occurs during the execution of a command.
     * 
     * @param command The command which raised the excpetion.
     * @param e the exception raised.
     * @see MapCommand
     * @see Throwable API allow this to throw an exception?
     */
    public void handleError( Command command, Throwable e );

    /**
     * Handles an error that occurs during the rollback of a undoable command.
     * 
     * @param command The command which raised the excpetion.
     * @param e the exception raised.
     * @see UndoableCommand
     * @see Throwable API allow this to throw an exception?
     */
    public void handleRollbackError( UndoableCommand command, Throwable e );
}
