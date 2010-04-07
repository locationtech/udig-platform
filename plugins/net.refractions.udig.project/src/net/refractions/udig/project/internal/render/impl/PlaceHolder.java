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
package net.refractions.udig.project.internal.render.impl;

import java.awt.Graphics2D;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Messages;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.RenderException;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A renderer that does nothing.  It holds the place of a normal renderer and just shows a warning.  This is used
 * if no renderer could be found for the layer.
 * @author Jesse
 * @since 1.1.0
 */
public class PlaceHolder extends RendererImpl implements Renderer {

    @Override
    public void render( Graphics2D destination, IProgressMonitor monitor ) throws RenderException {
        context.setStatus(ILayer.ERROR);
        context.setStatusMessage(Messages.PlaceHolder_error);
    }

    @Override
    public void render( IProgressMonitor monitor ) throws RenderException {
        context.setStatus(ILayer.ERROR);
        context.setStatusMessage(Messages.PlaceHolder_error);
    }
}
