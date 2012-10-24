/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.project.tests.ui;

import java.awt.Graphics2D;

import net.refractions.udig.project.internal.render.impl.RendererImpl;
import net.refractions.udig.project.render.RenderException;

import org.eclipse.core.runtime.IProgressMonitor;

public class DummyRenderer extends RendererImpl {

    @Override
    public void render( Graphics2D destination, IProgressMonitor monitor ) throws RenderException {
        
    }

    @Override
    public void render( IProgressMonitor monitor ) throws RenderException {
    }

}
