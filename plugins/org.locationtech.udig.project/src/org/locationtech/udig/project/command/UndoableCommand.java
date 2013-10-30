/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.command;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A type of commands that can be undone/rolled-back. API use
 * 
 * @author jeichar
 * @since 0.2
 * @see MapCommand
 */
public interface UndoableCommand extends Command {
    /**
     * Rollback the effects of the command
     * 
     * @throws Exception
     */
    public void rollback( IProgressMonitor monitor ) throws Exception;
}
