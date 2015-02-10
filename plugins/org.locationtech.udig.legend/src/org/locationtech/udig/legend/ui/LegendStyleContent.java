/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.legend.ui;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.StyleContent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;

public class LegendStyleContent extends StyleContent {

    public static final String ID = "org.locationtech.udig.legend.legendStyle"; //$NON-NLS-1$
    
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
    
    /**
     * Creates the default legend style
     * @return
     */
    public static LegendStyle createDefault() {
        LegendStyle style = new LegendStyle();
        
        style.verticalMargin = 3; 
        style.horizontalMargin = 2; 
        style.verticalSpacing = 5; 
        style.horizontalSpacing = 3; 
        style.indentSize = 10;
        style.imageHeight = 16;
        style.imageWidth = 16;
               
        style.backgroundColour = Color.WHITE;
                
        return style;
    }
}
