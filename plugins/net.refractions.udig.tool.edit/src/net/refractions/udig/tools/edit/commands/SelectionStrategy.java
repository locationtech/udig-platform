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
import org.geotools.feature.Feature;

/**
 * The interface for the strategy objects that are ran when the {@link SelectFeaturesAtPointCommand} encounters
 * a mouse click that intersects with at least one feature.
 *
 * @author jesse
 * @since 1.1.0
 */
public interface SelectionStrategy {

    /**
     * Adds the commands required to perform the action to the commands
     *
     * @param monitor the progress monitor for indicating the progress of the operation
     * @param commands the commands to add commands to for later execution.
     * @param parameters the parameters passed to {@link SelectFeaturesAtPointCommand}
     * @param feature One of the features that the mouse click intersected with.
     * @param firstFeature true if the feature is the first feature that was selected
     */
    void run( IProgressMonitor monitor, UndoableComposite commands,
            SelectionParameter parameters, Feature feature, boolean firstFeature );

}
