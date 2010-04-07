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
public class MultiRendererStyleContent extends StyleContent {

    static final String ID = "net.refractions.udig.project.tests.multi"; //$NON-NLS-1$

    public MultiRendererStyleContent( ) {
        super(ID);
    }

    @Override
    public Object createDefaultStyle( IGeoResource resource, Color colour, IProgressMonitor monitor ) throws IOException {
        return this;
    }

    @Override
    public Class getStyleClass() {
        return MultiRendererStyleContent.class;
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
