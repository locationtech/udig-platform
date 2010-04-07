/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.command.navigation;

import java.text.MessageFormat;

import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.NavCommand;
import net.refractions.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Sets the height of the Viewport bounding box. Because the aspect ratio of the viewport bounding
 * box is tied to the bounding box of the viewport, the width is also set to maintain the correct
 * aspect ratio. To change the display size the PlatformUI class should be used to obtain the
 * eclipse shell or site that should be resized. The viewport model does not modify the size of the
 * display.
 * 
 * @author jeichar
 * @since 0.3
 */
public class SetViewportHeight extends AbstractNavCommand implements NavCommand {

    private double height;

    /**
     * Creates a new instance of SetViewportHeight
     * 
     * @param height The new viewport height
     */
    public SetViewportHeight( double height ) {
        this.height = height;
    }

    /**
     * @see net.refractions.udig.project.internal.command.navigation.AbstractNavCommand#runImpl()
     */
    protected void runImpl( IProgressMonitor monitor ) throws Exception {
        model.setHeight(height);
    }

    /**
     * @see net.refractions.udig.project.internal.command.MapCommand#copy()
     */
    public MapCommand copy() {
        return new SetViewportHeight(height);
    }

    /**
     * @see net.refractions.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return MessageFormat.format(
                Messages.SetViewportHeight_setViewHeight, new Object[]{height}); 
    }

}