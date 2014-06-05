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
package org.locationtech.udig.mapgraphic.style;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.project.StyleContent;

/**
 * A style class that contains positioning (x and y) information
 * only.  If a width and height are also required see LocationStyleContent.
 * 
 * @author Emily
 *
 */
public class PositionStyleContent extends StyleContent {

    /** Magic key used to record y screen coordinate in our IMemento */
    private static final String Y = "y"; //$NON-NLS-1$
    /** Magic key used to record x screen coordinate in our IMemento */
    private static final String X = "x";  //$NON-NLS-1$

    /** extension id */
    public static final String ID = "org.locationtech.udig.printing.ui.positionStyle"; //$NON-NLS-1$
    
    /** padding constants */
    public static final int YPAD_BOTTOM = 5;
    public static final int YPAD_TOP = 15;
    public static final int XPAD_LEFT = 15;
    public static final int XPAD_RIGHT = 15;
    
    /**
     * Location style holding a rectangle.
     */
    public PositionStyleContent(){
        super( ID );
    }
    
    public Class<?> getStyleClass() {
        return Point.class;
    }
    
    public Object load( IMemento memento ) {
        int x = memento.getInteger(X);
        int y = memento.getInteger(Y);
        
        return new Point(x, y);
    }

    public void save( IMemento memento, Object style ) {
    	Point point = (Point)style;
        memento.putInteger(X, point.x);
        memento.putInteger(Y, point.y);
    }
    
    public Object createDefaultStyle(IGeoResource resource, Color colour,  IProgressMonitor monitor) throws IOException {
        if( !resource.canResolve(MapGraphic.class))
            return null;

        if( resource.canResolve(Point.class) ){
            // lets assume this is the best location for this resource
            Point rectangle = resource.resolve(Point.class, monitor);
            if( rectangle!=null ){
                return rectangle;
            }
        }
        
        return createDefaultStyle();
	}
	
    public Object load( URL url, IProgressMonitor monitor) throws IOException {
        return null;
    }

    public static Point createDefaultStyle() {
        return new Point( XPAD_LEFT, YPAD_TOP );
    }
    
}
