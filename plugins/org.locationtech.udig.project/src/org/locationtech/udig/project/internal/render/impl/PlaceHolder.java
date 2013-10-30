/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render.impl;

import java.awt.Graphics2D;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.render.RenderException;

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
