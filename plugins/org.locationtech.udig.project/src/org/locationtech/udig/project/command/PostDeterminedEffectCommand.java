/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.command;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This is a special type of command that may or may not affect global state.  Whether is does or not is 
 * not known until after the command has been ran.  If the {@link #execute(IProgressMonitor)} method returns true
 * the command will be put on Undo stack otherwise it won't be because it does not need to be undone.
 * 
 * @author jones
 * @since 1.1.0
 */
public interface PostDeterminedEffectCommand extends UndoableMapCommand {
    /**
     * This method will not be called it should throw a UnsupportedException exception.
     */
    void run( IProgressMonitor monitor ) throws Exception;
    /**
     * This method should return true if the method has changed state and will do something when undone.
     *
     * @param monitor used to indicate the progress of the monitor.
     * @return true if the method has changed global state and command should be put on the undo stack.
     */
    boolean execute(IProgressMonitor monitor) throws Exception;
}
