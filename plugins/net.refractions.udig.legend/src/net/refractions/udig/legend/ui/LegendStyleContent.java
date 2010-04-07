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
package net.refractions.udig.legend.ui;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.StyleContent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;

public class LegendStyleContent extends StyleContent {

    public static final String ID = "net.refractions.udig.legend.legendStyle"; //$NON-NLS-1$
    
    public LegendStyleContent() {
        super(ID);
    }
    
    @Override
    public Class<?> getStyleClass() {
        return LegendStyle.class;
    }

    @Override
    public void save( IMemento momento, Object value ) {
    }

    @Override
    public Object load( IMemento momento ) {
        return null;
    }

    @Override
    public Object load( URL url, IProgressMonitor monitor ) throws IOException {
        return null;
    }

    @Override
    public Object createDefaultStyle( IGeoResource resource, Color colour, IProgressMonitor monitor ) throws IOException {
        if( !resource.canResolve(LegendGraphic.class) )
            return null;
        return createDefault();
    }
    
    public static LegendStyle createDefault() {
        LegendStyle style = new LegendStyle();
        
        style.verticalMargin = 3; 
        style.horizontalMargin = 2; 
        style.verticalSpacing = 5; 
        style.horizontalSpacing = 3; 
        style.indentSize = 10;
        style.imageHeight = 16;
        style.imageWidth = 16;
               
        style.foregroundColour = Color.BLACK;
        style.backgroundColour = Color.WHITE;
                
        return style;
    }
}
