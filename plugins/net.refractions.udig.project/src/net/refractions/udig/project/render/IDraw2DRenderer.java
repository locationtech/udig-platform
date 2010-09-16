/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.render;

import java.awt.Graphics2D;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.GC;

/**
 * Responsible for rendering a layer using a Draw2D GC  - this class is
 * assigned a context with a specific GeoResource to use as a source of data.
 * </p>
 * This interface extends IRenderer as we still require an AWT Graphics2D rendering
 * pipeline to use when printing.
 * <ul> 
 * @author Jody Garnett
 * @since 1.2
 */
public interface IDraw2DRenderer extends IRenderer {
    /**
     * 
     * @param gc
     * @param monitor
     * @throws RenderException
     */
    public void render( GC gc, IProgressMonitor monitor ) throws RenderException;

    /**
     * We are still required to implement a Graphic2D rendering
     * method (for printing).
     * <p>
     * If needed you can make use of AWTSWTImageUtils to reuse your GC based code.
     * 
     * @param destination The objects that the Renderer will use for rendering
     * @throws RenderException
     */
    public void render( Graphics2D g, IProgressMonitor monitor ) throws RenderException;

    /**
     * Used to draw
     */
    public void render( IProgressMonitor monitor ) throws RenderException;

}