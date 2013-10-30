/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.command.navigation;

import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A command for making the viewport zoom out fully so the entire map is displayed.
 * 
 * @author jeichar
 * @since 0.3
 */
public class ZoomExtentCommand extends AbstractNavCommand {

    /**
     * @see org.locationtech.udig.project.internal.command.MapCommand#copy()
     */
    public MapCommand copy() {
        return new ZoomExtentCommand();
    }

    /**
     * @see org.locationtech.udig.project.internal.command.navigation.AbstractNavCommand#runImpl()
     */
    protected void runImpl( IProgressMonitor monitor ) {
        model.zoomToExtent();
    }

    /**
     * @see org.locationtech.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return Messages.ZoomExtentCommand_name; 
    }

}
