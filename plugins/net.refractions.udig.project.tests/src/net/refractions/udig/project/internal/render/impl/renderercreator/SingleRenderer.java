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
package net.refractions.udig.project.internal.render.impl.renderercreator;

import java.awt.Graphics2D;

import net.refractions.udig.project.internal.render.impl.RendererImpl;
import net.refractions.udig.project.render.RenderException;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Simulats a renderer that renders a single class.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class SingleRenderer extends RendererImpl {

    @Override
    public void render( Graphics2D destination, IProgressMonitor monitor ) throws RenderException {
    }

    @Override
    public void render( IProgressMonitor monitor ) throws RenderException {
    }

}
