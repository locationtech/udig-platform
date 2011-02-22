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
package net.refractions.udig.tools.edit.commands;

import net.refractions.udig.project.command.UndoableComposite;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * The interface for the strategy objects that are ran when the {@link SelectFeaturesAtPointCommand} encounters
 * a mouse click that does not intersect with any features.
 *
 * @author jesse
 * @since 1.1.0
 */
public interface DeselectionStrategy {

    /**
     * Creates the commands that will perform the action and adds them to the compositeCommand.
     *
     * @param monitor the progress monitor.
     * @param parameters the parameters that the {@link SelectFeaturesAtPointCommand} was configured with
     * @param compositeCommand the composite to add commands to.
     */
    void run( IProgressMonitor monitor, SelectionParameter parameters, UndoableComposite compositeCommand );

}
