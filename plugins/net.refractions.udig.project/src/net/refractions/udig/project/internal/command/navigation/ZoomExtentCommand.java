/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.command.navigation;

import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A command for making the viewport zoom out fully so the entire map is displayed.
 * 
 * @author jeichar
 * @since 0.3
 */
public class ZoomExtentCommand extends AbstractNavCommand {

    /**
     * @see net.refractions.udig.project.internal.command.MapCommand#copy()
     */
    public MapCommand copy() {
        return new ZoomExtentCommand();
    }

    /**
     * @see net.refractions.udig.project.internal.command.navigation.AbstractNavCommand#runImpl()
     */
    protected void runImpl( IProgressMonitor monitor ) {
        model.zoomToExtent();
    }

    /**
     * @see net.refractions.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return Messages.ZoomExtentCommand_name; 
    }

}
