/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.render;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.SortedSet;

import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.render.impl.ViewportModelImpl;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

/**
 * Models the Viewport on the map.
 * 
 * @author Jesse
 * @since 0.5
 */
public interface IViewportModel {

    /**
     * A Default setting to use for the viewport CRS.
     */
    public static final CoordinateReferenceSystem DEFAULT_CRS = ViewportModelImpl.DEFAULT_CRS;

    /**
     * Returns the local coordinate system. The local coordinate system is the CRS that all the
     * layer data will be transformed into. Once the layer data is transformed into the local CRS
     * then it is transformed for display onto the screen
     * 
     * @return the local coordinate system
     * @see CoordinateReferenceSystem
     */
    public CoordinateReferenceSystem getCRS();

    /**
     * Returns the bounding box of the Viewport in world coordinates.
     * <p>
     * Note: Since Envelope is not a UDIG element changes to the bounds envelope object will not
     * raise events. Therefore the bounds should only be modified via the ViewportModel interface
     * </p>
     * The bounds are in the same CRS as returned by {@linkplain #getCRS()}
     * 
     * @return the bounding box of the Viewport in world coordinates.
     * @see Envelope
     */
    public ReferencedEnvelope getBounds();

    /**
     * Returns the center of the viewport in world coordinates. The bounds are in the same CRS as
     * returned by {@linkplain #getCRS()}
     * 
     * @return the center of the viewport in world coordinates
     * @see Coordinate
     */
    public Coordinate getCenter();

    /**
     * Returns the Viewport's height in world coordinates. The bounds are in the same CRS as
     * returned by {@linkplain #getCRS()}
     * 
     * @return the Viewport's height in world coordinates.
     */
    public double getHeight();

    /**
     * Returns the Viewport's width in world coordinates. The bounds are in the same CRS as returned
     * by {@linkplain #getCRS()}
     * 
     * @return the Viewport's width in world coordinates.
     */
    public double getWidth();

    /**
     * Returns the aspect ratio of the viewport. (width/height)
     * 
     * @return The aspect ratio of the viewport.
     */
    public double getAspectRatio();

    /**
     * Gets the Map that contains the current ViewportModel
     * 
     * @return the Map that contains the current ViewportModel
     */
    public IMap getMap();

    /**
     * Gets up the affine transform that will transform from the world to screen. A convenience
     * method.
     * 
     * @return a transform that maps from real world coordinates to the screen
     * @see AffineTransform
     */
    public AffineTransform worldToScreenTransform();

    /**
     * Returns the pixel on the screen for a given coordinate in world space.
     * 
     * @param coord A coordinate in world space.
     * @return The pixel on the screen that the world coordinate is drawn on.
     * @see Point
     * @see Coordinate
     */
    public Point worldToPixel( Coordinate coord );
    /**
     * Gets up the affine transform that will transform from the world to the display of size
     * destination. A convenience method. This method is independent of the CRS.
     * 
     * @return a transform that maps from real world coordinates to the screen
     */
    public AffineTransform worldToScreenTransform( Envelope mapExtent, Dimension destination );
    /**
     * Converts a coordinate expressed on the device space back to real world coordinates
     * 
     * @param x horizontal coordinate on device space
     * @param y vertical coordinate on device space
     * @return The correspondent real world coordinate
     * @see Coordinate
     */
    public Coordinate pixelToWorld( int x, int y );

    /**
     * Returns the size of a pixel in world units.
     * 
     * @return the size of a pixel in world units.
     * @see Coordinate
     */
    public Coordinate getPixelSize();
	 /**
	  * Find the scale denominator of the map.
	  *   Method:
	  *    1. find the diagonal distance (meters)
	  *    2. find the diagonal distance (pixels)
	  *    3. find the diagonal distance (meters) -- use DPI
	  *    4. calculate scale (#1/#2)
	  * 
	  *   NOTE: return the scale denominator not the actual scale (1/scale = denominator)
	  * 
	  * TODO:  (SLD spec page 28):
	  * Since it is common to integrate the output of multiple servers into a single displayed result in the 
	  * web-mapping environment, it is important that different map servers have consistent behaviour with respect to 
	  * processing scales, so that all of the independent servers will select or deselect rules at the same scales.
	  * To insure consistent behaviour, scales relative to coordinate spaces must be handled consistently between map 
	  * servers. For geographic coordinate systems, which use angular units, the angular coverage of a map should be 
	  * converted to linear units for computation of scale by using the circumference of the Earth at the equator and 
	  * by assuming perfectly square linear units. For linear coordinate systems, the size of the coordinate space 
	  * should be used directly without compensating for distortions in it with respect to the shape of the real Earth.
	  * 
	  * NOTE: we are actually doing a a much more exact calculation, and accounting for non-square pixels
	  * 
	  * @return the scale denominator of the map on the current display.  Return -1 if something is wrong.  For example the display has not
	  * yet been created.
	  */
	public double getScaleDenominator();

    /**
     * The default preferred scale denominator.  if getDefaultPreferredScaleDenominators == getPreferredScaleDenominators then
     * defaults are being used and should only be treated as hints
     */
    public SortedSet<Double> getDefaultPreferredScaleDenominators();
    
	/**
     * List of preferred scale denominators for the map.
     * <p>
     * This set is used to provide good options for a user to change the scale
     * </p>
     * <p>
     * The values will always be present but if the object returned by {@link #getDefaultPreferredScaleDenominators()} and this method
     * are the same <em>instance</em> then they are simply defaults and can be ignored if desired.  However if they are not the same
     * then assume that the values are only hints and can be ignored
     * </p>
     * 
     * @see getScaleDEnominator for a definition of scale denominator
     * @return List of preferred scale denominator values for the map
	 */
	public SortedSet<Double> getPreferredScaleDenominators();
	
    /**
     * Adds a IViewportModelListener to this map.  A given listener will only be added once.
     *
     * @param listener Listener to be added
     * @see org.locationtech.udig.project.ViewportModelEvent.ViewportModelEventType
     */
    public void addViewportModelListener( IViewportModelListener listener );
    
    /**
     * Removes a IViewportModelListener from this map.
     *
     * @param listener Listener to be removed
     */
    public void removeViewportModelListener( IViewportModelListener listener);
    
    /**
     * 
     *
     * @return Returns true if the current bound changes are a part of a series of changes.
     */
    public boolean isBoundsChanging();
}
