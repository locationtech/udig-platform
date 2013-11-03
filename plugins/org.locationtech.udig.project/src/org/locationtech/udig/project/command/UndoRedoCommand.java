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
