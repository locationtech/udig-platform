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
import org.opengis.feature.simple.SimpleFeature;

/**
 * The interface for the strategy objects that are ran when the {@link SelectFeaturesAtPointCommand} encounters
 * a mouse click that intersects with at least one feature. 
 * <p>
 * The selection strategy is expected to add commands to the provided UndoableComposite
 * in response to the the selected feature. SelectionParameter contains a summary
 * of what is going on.
 * 
 * @author jesse
 * @since 1.1.0
 */
public interface SelectionStrategy {

    /**
     * Adds the commands required to perform the action to the commands
     *
     * @param monitor the progress monitor for indicating the progress of the operation
     * @param commands Add any commands you generate to this UndoableComposite used for later execution  
     * @param parameters The parameters passed in to control the selection process
     * @param feature One of the features that the mouse click intersected with.
     * @param firstFeature true if the feature is the first feature that was selected
     */
    void run( IProgressMonitor monitor, UndoableComposite commands,
            SelectionParameter parameters, SimpleFeature feature, boolean firstFeature );

}
