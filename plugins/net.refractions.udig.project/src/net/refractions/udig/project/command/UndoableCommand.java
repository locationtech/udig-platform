/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
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
