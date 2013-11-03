/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.commands;

import org.locationtech.udig.project.command.UndoableComposite;

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
