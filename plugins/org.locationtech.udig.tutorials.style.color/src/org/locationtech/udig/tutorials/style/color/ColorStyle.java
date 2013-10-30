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
package org.locationtech.udig.tutorials.style.color;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.StyleContent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;

public class ColorStyle extends StyleContent {
    public static final String ID =
        "org.locationtech.udig.tutorials.style.color.colorStyle";
    public ColorStyle() {
        super(ID);
    }
    public Object createDefaultStyle( IGeoResource resource, Color color, IProgressMonitor monitor )
            throws IOException {
        if( resource.canResolve( Color.class )){
            return resource.resolve( Color.class, monitor );
        }
        return null;
    }    
    public Class<Color> getStyleClass() {
        return Color.class;
    }
    public Object load( IMemento memento ) {
        Integer R = memento.getInteger("r");
        Integer G = memento.getInteger("g");
        Integer B = memento.getInteger("b");
        Integer A = memento.getInteger("a");
        int r = R == null ? 0 : (int) R;
        int g = G == null ? 0 : (int) G;
        int b = B == null ? 0 : (int) B;
        int a = A == null ? 255 : (int) A;
        
        return new Color( r, g, b, a );
    }
    
    public Object load( URL url, IProgressMonitor monitor ) throws IOException {
        return null;
    }
    public void save( IMemento memento, Object value ) {
        Color color = (Color) value;
        if (color != null){
            memento.putInteger("r", color.getRed());
            memento.putInteger("g", color.getGreen());
            memento.putInteger("b", color.getBlue());
            memento.putInteger("a", color.getAlpha());
        }
    }
}
