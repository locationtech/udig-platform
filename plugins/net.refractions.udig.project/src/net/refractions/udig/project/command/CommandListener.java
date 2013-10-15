/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.command;

/**
 * CommandListeners are notified when commands have been executed.
 * 
 * @author jones
 * @since 1.0.0
 */
public interface CommandListener {
    /**
     * CommandManager will call this function once a command is completed.
     * 
     * @param commandType
     */
    public void commandExecuted( int commandType );
}
