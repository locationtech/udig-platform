/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.internal.render.impl.renderercreator;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.StyleContent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;

/**
 * For testing. A style for SingleRenderer
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class SingleRendererStyleContent extends StyleContent {

    static final String ID = "net.refractions.udig.project.tests.single"; //$NON-NLS-1$

    public SingleRendererStyleContent( ) {
        super(ID);
    }

    @Override
    public Object createDefaultStyle( IGeoResource resource, Color colour, IProgressMonitor monitor ) throws IOException {
        return this;
    }

    @Override
    public Class getStyleClass() {
        return SingleRendererStyleContent.class;
    }

    @Override
    public Object load( IMemento memento ) {
        return null;
    }

    @Override
    public Object load( URL url, IProgressMonitor monitor ) throws IOException {
        return null;
    }

    @Override
    public void save( IMemento memento, Object value ) {
    }

}
