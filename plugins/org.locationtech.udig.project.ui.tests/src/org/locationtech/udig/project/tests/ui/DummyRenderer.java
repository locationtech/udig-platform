/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.tests.ui;

import java.awt.Graphics2D;

import org.locationtech.udig.project.internal.render.impl.RendererImpl;
import org.locationtech.udig.project.render.RenderException;

import org.eclipse.core.runtime.IProgressMonitor;

public class DummyRenderer extends RendererImpl {

    @Override
    public void render( Graphics2D destination, IProgressMonitor monitor ) throws RenderException {
        
    }

    @Override
    public void render( IProgressMonitor monitor ) throws RenderException {
    }

}
