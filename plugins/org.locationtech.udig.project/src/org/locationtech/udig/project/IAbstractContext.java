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
package org.locationtech.udig.project;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.List;

import org.locationtech.udig.project.render.IRenderManager;
import org.locationtech.udig.project.render.IViewportModel;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;

import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform2D;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
/**
 * Provides access to contextual information.
 * <p>
 * Toolkits are used by extensions to access Map and Project information. A Toolkit should never be
 * instantiated by a developer. The framework provides Toolkits to extensions.
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Provide access to the objects that an extension can use for its operations.</li>
 * <li>Provide convenience methods for extension developers to use.</li>
 * <li>Provide a consistent interface for extensions which will not easily change in future
 * versions</li>
 * </ul>
 * </p>
 * 
 * @author Jesse
 * @since 0.5
 */
public interface IAbstractContext {

    /**
     * The map's viewport model.
     * <p>
     * Convenience for getMap().getViewportModel().
     * </p>
     * <p>
     * Called to obtain current viewport bounds and crs.
     * </p>
     * 
     * @return The Viewportmodel for the map.
     * @see IViewportModel
     */
    IViewportModel getViewportModel();

    /**
     * The map's edit manager.
     * <p>
     * Convenience for getMap().getEditManager().
     * </p>
     * <p>
     * Called to obtain the currently editable feature.
     * </p>
     * 
     * @return The map's edit manager.
     * @see IEditManager
     */
    IEditManager getEditManager();

    /**
     * The map's render manager
     * <p>
     * Convenience for getMap().getRenderManager().
     * </p>
     * <p>
     * Called to refresh the current display.
     * </p>
     * 
     * @return The RenderManager for the map.
     * @see IRenderManager
     */
    IRenderManager getRenderManager();

    /**
     * The map's display object.
     * <p>
     * Convenience for getMap().getRenderManager().getMapDisplay().
     * </p>
     * <p>
     * Called to obtain the height and width of the display.
     * </p>
     * 
     * @return The IMapDisplay for the map.
     * @see IMapDisplay
     */
    IMapDisplay getMapDisplay();

    /**
     * The context's map.
     * <p>
     * The Map data object.
     * </p>
     * 
     * @return The context's map.
     * @see IMap
     */
    IMap getMap();

    /**
     * The map's containing Project.
     * <p>
     * Convenience for getMap().getProject().
     * </p>
     * <p>
     * Contains all the {@linkplain IProjectElements} in the project.
     * </p>
     * 
     * @return The containing Project of the map.
     * @see IProject
     */
    IProject getProject();

    /**
     * Gets up the affine transform that will transform from the world to screen. A convenience
     * method.
     * 
     * @return a transform that maps from real world coordinates to the screen
     */
    AffineTransform worldToScreenTransform();

    /**
     * Returns the pixel on the screen for a given coordinate in world space. A convenience method.
     * 
     * @param coord A coordinate in world space.
     * @return The pixel on the screen that the world coordinate is drawn on.
     */
    Point worldToPixel( Coordinate coord );

    /**
     * Converts a coordinate expressed on the device space back to real world coordinates A
     * convenience method.
     * 
     * @param x horizontal coordinate on device space
     * @param y vertical coordinate on device space
     * @return The correspondent real world coordinate
     */
    Coordinate pixelToWorld( int x, int y );

    /**
     * Creates an Envelope that is close, error to slightly larger, to the Rectangle when it is
     * transformed into world coordinates.
     * 
     * @param rectangle
     * @return
     */
    public ReferencedEnvelope worldBounds( Rectangle rectangle );

    /**
     * Creates a MathTransform that will transform from the screen CRS to the world CRS.
     * 
     * @return
     */
    public MathTransform2D worldToScreenMathTransform();
    /**
     * Returns the size of a pixel in world units. A convenience method.
     * 
     * @return the size of a pixel in world units.
     */
    Coordinate getPixelSize();

    /**
     * Returns a world bounding box the size of a pixel at the location corresponding to the point
     * on the screen. A convenience method.
     * 
     * @return the size of a pixel in world units.
     */
    Envelope getPixelBoundingBox( Point screenLocation );

    /**
     * Returns a world bounding box the scalefactor * (size of a pixel) at the location
     * corresponding to the point on the screen. A convenience method.
     * <p>
     * XXX: Can we make this a ReferencedEnvelope?
     * </p>
     * 
     * @return the ReferencedEnvelope around the Point
     */
    ReferencedEnvelope getBoundingBox( Point screenLocation, int scalefactor );

    /**
     * CoordinateReferenceSystem of the map.
     * 
     * @return getViewportModel().getCRS();
     */
    CoordinateReferenceSystem getCRS();

    /**
     * Transform the provided envelope to a java 2d shape (in screen coordiantes).
     * 
     * @param box
     * @return
     */
    Shape toShape( ReferencedEnvelope envelope );

    /**
     * Returns the currently Selected Layer
     * 
     * @return the currently Selected Layer
     */
    public ILayer getSelectedLayer();

    /**
     * Transform the provided geometry to a java 2d shape (in screen coordiantes).
     * 
     * @param box
     * @return
     */
    Shape toShape( Geometry geometry, CoordinateReferenceSystem crs );
    /**
     * Returns all the features that intersect with the bounding box.
     * 
     * @param source The featuresource to get features from.
     * @param bbox The bounding box that acts as a filter.  Must be in map coordinates.
     * @return all the features that intersect with the bounding box.
     */
    FeatureCollection<SimpleFeatureType, SimpleFeature>  getFeaturesInBbox( ILayer layer, Envelope bbox ) throws IOException;

    /**
     * Returns the list of layers in the current map.
     * 
     * @return The list of layers in the current map.
     */
    List<ILayer> getMapLayers();
    
    /**
     * Makes a deep copy of this object, if necessary.
     * This is an alternative to clone since I hate clone, it is too hard to implement.
     *
     * @return a new copy of this object.  
     */
    public IAbstractContext copy();

    /**
     * Transforms the coordinate from the bounding box (bbox) to the area defined by the rectangle it starts at 0,0.  A simple
     * Affine transform is used and the y-axis is flipped since it is assumed the bbox is a world bbox and the Rectangle
     * is a cartesian like on the screen.
     *
     * @param bbox The from area
     * @param displaySize the to area
     * @param coordinate the coordinate to transform.
     * @return the point that the coordiante transforms to.
     */
    Point tranformCoordinate( Envelope bbox, Dimension displaySize, Coordinate coordinate );

}
