/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.command;

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
