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
package net.refractions.udig.project.command;

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
