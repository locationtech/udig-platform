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
package net.refractions.udig.mapgraphic.style;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.project.StyleContent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;

/**
 * Rectangle indicating Location in device coordinates, w & h in 1/72 of an inch.
 * <p>
 * <b>Screen Coordinates</b> are used. This naturally changes to Page
 * Coordinates when printed.
 * </p>
 * <p>
 * The x,y position are considered in a relative fashion.
 * <ul>
 * <li>x positive: measured from the left edge of the screen or paper
 * <li>y positive: measured from the top edge of the screen or paper
 * <li>x negative: measured from the bottom edge of the screen or paper
 * <li>y negative: measured from the right edge of the screen or paper
 * </ul>
 * This allows a map graphic to be specified relative to any edge.
 * </p>
 * <p>
 * The width and height of the Rectangle indicate the requested size in terms
 * of Java's usual one pixel indicates 1/72 of an inch. Appropriate scaling
 * will be needed when page output is requested.
 * </p>
 * @author Richard Gould
 * @since 0.6.0
 */
public class LocationStyleContent extends StyleContent {
    /** Magic key used to record height in screen coordinate in our IMemento */
    private static final String H = "h"; //$NON-NLS-1$
    /** Magic key used to record width in screen coordinate in our IMemento */
    private static final String W = "w"; //$NON-NLS-1$
    /** Magic key used to record y screen coordinate in our IMemento */
    private static final String Y = "y"; //$NON-NLS-1$
    /** Magic key used to record x screen coordinate in our IMemento */
    private static final String X = "x";  //$NON-NLS-1$

    /** extension id */
    public static final String ID = "net.refractions.udig.printing.ui.locationStyle"; //$NON-NLS-1$
    
    /** padding constants */
    public static final int YPAD_BOTTOM = 5;
    public static final int YPAD_TOP = 15;
    public static final int XPAD_LEFT = 15;
    public static final int XPAD_RIGHT = 15;
    
    /**
     * Location style holding a rectangle.
     */
    public LocationStyleContent(){
        super( ID );
    }
    
    public Class<?> getStyleClass() {
        return Rectangle.class;
    }
    
    public Object load( IMemento memento ) {
        int x = memento.getInteger(X);
        int y = memento.getInteger(Y);
        int width = memento.getInteger(W);
        int height = memento.getInteger(H);
        
        return new Rectangle(x, y, width, height);
    }

    public void save( IMemento memento, Object style ) {
        Rectangle rectangle = (Rectangle) style;
        memento.putInteger(X, rectangle.x);
        memento.putInteger(Y, rectangle.y);
        memento.putInteger(W, rectangle.width);
        memento.putInteger(H, rectangle.height);
    }
    
    public Object createDefaultStyle(IGeoResource resource, Color colour,  IProgressMonitor monitor) throws IOException {
        if( !resource.canResolve(MapGraphic.class))
            return null;

        if( resource.canResolve(Rectangle.class) ){
            // lets assume this is the best location for this resource
            Rectangle rectangle = resource.resolve(Rectangle.class, monitor);
            if( rectangle!=null ){
                return rectangle;
            }
        }
        
        return createDefaultStyle();
	}
	
    public Object load( URL url, IProgressMonitor monitor) throws IOException {
        return null;
    }

    public static Rectangle createDefaultStyle() {
        return new Rectangle( XPAD_LEFT, YPAD_TOP, 500, 10 );
    }
    
}