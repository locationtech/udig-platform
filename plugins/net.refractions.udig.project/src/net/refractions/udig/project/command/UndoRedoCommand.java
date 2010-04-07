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
 * This class will only rollback and redo the command it wraps.  
 * In the case of where commands are required to interact with the UI it is often desirable to execute the command and then put it on the 
 * command stack so that it can be undone.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class UndoRedoCommand extends AbstractCommand implements UndoableMapCommand {

    UndoableMapCommand wrapped;
    boolean undone=false;
    
    public UndoRedoCommand( UndoableMapCommand addVertexCommand ) {
        this.wrapped=addVertexCommand;
    }

    public String getName() {
        return null;
    }

    public void run( IProgressMonitor monitor ) throws Exception { 
        if( undone )
            wrapped.run(monitor);
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        undone=true;
        wrapped.rollback(monitor);
    }

}
