/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.geotools.data.Query;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.AbstractContext;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.render.IRenderContext;

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
    @Override
    public boolean hasContent(Point screenLocation);

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
    @Override
    public BufferedImage copyImage(Rectangle rectangle);

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
    @Override
    public BufferedImage getImage(int width, int height);

    /**
     * Returns a bufferedImage that a renderer can renders to.
     * <p>
     * The returned image will be the same size as the display or bigger
     * </p>
     *
     * @return The bufferedImage that the renderer renders to.
     */
    @Override
    public BufferedImage getImage();

    /**
     * Returns the query that selects all the features that need to be rendered for the layer.
     *
     * @return the query that selects all the features that need to be rendered for the layer.
     */
    public Query getQuery(ILayer layer);

    /**
     * Returns the layer in the renderer is responsible for.
     * <p>
     * Should normally be used when only one layer is being rendered.
     * </p>
     */
    Layer getLayerInternal();

    /**
     * Sets the value of the
     * '{@link org.locationtech.udig.project.internal.render.RenderContext#getLayerInternal
     * <em>Layer Internal</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Layer Internal</em>' reference.
     * @see #getLayerInternal()
     */
    void setLayerInternal(Layer value);

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
     * Sets the value of the
     * '{@link org.locationtech.udig.project.internal.render.RenderContext#getGeoResourceInternal
     * <em>Geo Resource Internal</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @param value the new value of the '<em>Geo Resource Internal</em>' attribute.
     * @see #getGeoResourceInternal()
     * @generated
     */
    void setGeoResourceInternal(IGeoResource value);

    /**
     * Determines the zorder of the renderer.
     * <p>
     * In most cases it is recommended that the zorder=layer.getZorder()
     * <p>
     *
     * @model changeable="false" transient="true" notify="true" volatile="true"
     */
    @Override
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
    @Override
    public boolean isVisible();

    /**
     * Initialize context so it matches the argument.
     *
     * @param renderContext
     */
    public void init(RenderContext renderContext);

    /**
     * Clears the image so it is all transparent
     */
    @Override
    public void clearImage();

    /**
     * Query used to obtain features to be drawn (this may be adjusted by the current selection, or
     * if the EditManager is currently drawing a few of the features as EditGeometry).
     * <p>
     * This implementation takes the time to check Layer.getQuery( selected ) along with any filter
     * overrides provided on the style blackboard, and any temporarily excluded features being held
     * by the EditManager.
     * </p>
     *
     * @return The filter that will return all the features that need to be rendered
     * @throws Exception In case something goes wrong, IOException, IllegalTransformException etc...
     */
    @Override
    public Query getFeatureQuery();

    /**
     * Clears the area.
     *
     * @param paintArea
     */
    @Override
    public void clearImage(Rectangle paintArea);

    /**
     * Sets the status of the layer contained by the context.
     *
     * @param error
     */
    @Override
    public void setStatus(int status);

    @Override
    public RenderContext copy();

    /**
     * Sets the size of the image to draw. If set to null the image size will be the same as the
     * MapDisplay size.
     * <p>
     * For the regular renderers this will be the mapDisplay size; however for the tiled renderer
     * this will be tile size
     * </p>
     *
     * @param width
     * @param height
     */
    public void setImageSize(Dimension d);

    /**
     * Sets the bounds in world coordinates that the image represents. If set to null the bounds of
     * the image are assumed to match the bounds of the viewport model.
     *
     * <p>
     * This is used by the tiled renderer so each context can represent a specific tiled area.
     * </p>
     *
     * @param bounds
     */
    public void setImageBounds(ReferencedEnvelope bounds);

}
