/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.render;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.media.jai.TileCache;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.AbstractContext;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.render.IRenderContext;

import org.geotools.data.Query;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * @see IRenderContext
 * 
 * @author Jesse
 * @since 1.0.0
 */
public interface RenderContext extends AbstractContext, Comparable<RenderContext>, IRenderContext {

    /**
     * Test if content is rendered at the provided point.
     * <p>
     * Used to optimize getInfo and selection tools.
     * </p>
     * 
     * @param screenLocation
     * @return true if non transparent pixel is rendered at screenLocation
     */
    public boolean hasContent( Point screenLocation );

    /**
     * Grab a specific rectangle from the raster to provide instant feedback for geoInfo and
     * selection tools.
     * <p>
     * Often this feedback takes place on the display under direction of the tool.
     * </p>
     * 
     * @param rectangle Rectangle indicating area of interest
     * @return Buffered image copied from the raster, or null if unavailable
     */
    public BufferedImage copyImage( Rectangle rectangle );
    
    /**
     * Returns a bufferedImage that a renderer can render to.
     * <p>
     * The method does not guarantee an image that is the same size as the request, only that the
     * returned image will be at least the size requested
     * </p>
     * <p>
     * The user of the image is required to clear the image. The image maybe cached and as a result
     * may be dirty.
     * </p>
     * 
     * @return The bufferedImage that the renderer renders to.
     */
    public BufferedImage getImage( int width, int height );

    /**
     * Returns a bufferedImage that a renderer can renders to.
     * <p>
     * The returned image will be the same size as the display or bigger
     * </p>
     * 
     * @return The bufferedImage that the renderer renders to.
     */
    public BufferedImage getImage();

    /**
     * Returns the query that selects all the features that need to be rendered for the layer.
     * 
     * @return the query that selects all the features that need to be rendered for the layer.
     */
    public Query getQuery( ILayer layer );

    /**
     * Returns the layer in the renderer is responsible for.
     * <p>
     * Should normally be used when only one layer is being rendered.
     * </p>
     */
    Layer getLayerInternal();

    /**
     * Sets the value of the '{@link net.refractions.udig.project.internal.render.RenderContext#getLayerInternal <em>Layer Internal</em>}'
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Layer Internal</em>' reference.
     * @see #getLayerInternal()
     */
    void setLayerInternal( Layer value );

    /**
     * Returns service associated with the first layer in the map.
     * <p>
     * Should normally be used when only one layer is being rendered.
     * </p>
     * 
     * @model many="false"
     */
    IGeoResource getGeoResourceInternal();

    /**
     * Sets the value of the '{@link net.refractions.udig.project.internal.render.RenderContext#getGeoResourceInternal <em>Geo Resource Internal</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Geo Resource Internal</em>' attribute.
     * @see #getGeoResourceInternal()
     * @generated
     */
    void setGeoResourceInternal( IGeoResource value );

    /**
     * Determines the zorder of the renderer.
     * <p>
     * In most cases it is recommended that the zorder=layer.getZorder()
     * <p>
     * 
     * @model changeable="false" transient="true" notify="true" volatile="true"
     */
    public int getZorder();

    /**
     * Returns true if the renderer has visible data.
     * <p>
     * If not layer is not a CompositeRenderer then this is just a call to ILayer.isVisible().
     * Otherwise if one of the layers is visible then it should return true
     * </p>
     * 
     * @return true if the renderer has visible data.
     * @model volatile="true" changeable="false"
     */
    public boolean isVisible();

    /**
     * Initialize context so it matches the argument.
     * 
     * @param renderContext
     */
    public void init( RenderContext renderContext );

    /**
     * Clears the image so it is all transparent
     */
    public void clearImage();

    /**
     * Query used to obtain features to be drawn (this may be adjusted by the current selection,
     * or if the EditManager is currently drawing a few of the features as EditGeometry).
     * <p>
     * This implementation takes the time to check Layer.getQuery( selected ) along with
     * any filter overrides provided on the style blackboard, and any temporarily excluded
     * features being held by the EditManager.
     * 
     * @return The filter that will return all the features that need to be rendered
     * @throws Exception In case something goes wrong, IOException, IllegalTransformException etc...
     */
    public Query getFeatureQuery();

    /**
     * Clears the area.
     * 
     * @param paintArea
     */
    public void clearImage( Rectangle paintArea );

    /**
     * Sets the status of the layer contained by the context.
     * 
     * @param error
     */
    public void setStatus( int status );

    public RenderContext copy();
    
    
    /**
     * Sets the size of the image to draw.  If set to null the image size will be the same as the MapDisplay size.
     * <p>
     * For the regular renderers this will be the mapDisplay size; however for the tiled renderer this
     * will be tile size
     *
     * @param width
     * @param height
     */
    public void setImageSize(Dimension d);
    
    
    /**
     * Sets the bounds in world coordinates that the image represents.  If set to null the bounds of the image
     * are assumed to match the bounds of the viewport model.  
     * 
     * <p>
     * This is used by the tiled renderer so each context can represent a specific tiled area.
     * 
     *
     * @param bounds
     */
    public void setImageBounds(ReferencedEnvelope bounds);
    
}