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
package net.refractions.udig.project.render;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.IAbstractContext;
import net.refractions.udig.project.ILayer;

import org.geotools.data.Query;

/**
 * Simplifies access of resource data and output image for renderers.
 * A RenderContext has three main Jobs:
 * <ul>
 * <li> Maps between a Renderer and the Layer and GeoResource it must use </li>
 * <li> Provides access to the BufferedImage that the renderer should draw to </li>
 * <li> Acts as a facade to the rest of the model </li>
 * </ul>
 *
 * @author Jesse
 * @since 1.0.0
 */
public interface IRenderContext extends IAbstractContext {

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
     * @see BufferedImage
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
     * @see BufferedImage
     */
    public BufferedImage getImage( int width, int height );

    /**
     * Returns a bufferedImage that a renderer can renders to.
     * <p>
     * The returned image will be the same size as the display or bigger
     * </p>
     *
     * @return The bufferedImage that the renderer renders to.
     * @see BufferedImage
     */
    public BufferedImage getImage();

    /**
     * Returns the layer in the renderer is responsible for.
     * <p>
     * Should normally be used when only one layer is being rendered.
     * </p>
     *
     * @return ILayer
     * @see ILayer
     */
    ILayer getLayer();

    /**
     * Returns service associated with the first layer in the map.
     * <p>
     * Should normally be used when only one layer is being rendered.
     * </p>
     *
     * @return IGeoResource the georesource that will be used to render the layer.
     */
    IGeoResource getGeoResource();

    /**
     * Determines the zorder of the renderer. Convenience method for getLayer.getZorder()
     *
     * @return the zorder of the layer contained in the context.
     * @see ILayer#getZorder()
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
     * @see ILayer#isVisible()
     */
    public boolean isVisible();

    /**
     * Clears the entire image so it is all transparent.
     * <p>
     * Convenience for clearImage(getImage().getWidth(), getImage.getHeight());
     * <p>
     *
     * @see clearImage(Rectangle)
     */
    public void clearImage();

    /**
     * @return The filter that will return all the features that need to be rendered
     */
    public Query getFeatureQuery();

    /**
     * Clears the area of the image indicated by the rectangle.
     *
     * @param paintArea
     * @see clearImage()
     */
    public void clearImage( Rectangle paintArea );

    /**
     * Sets the status of the layer contained by the context.
     *
     * @see ILayer#DONE
     * @see ILayer#ERROR
     * @see ILayer#MISSING
     * @see ILayer#WAIT
     * @see ILayer#WARNING
     * @see ILayer#WORKING
     * @see ILayer#UNCONFIGURED
     * @param status the new status to set on the layer.
     * @uml.property name="status"
     */
    public void setStatus( int status );

    /**
     * Gets the status of the layer contained by the context.
     *
     * @see ILayer#DONE
     * @see ILayer#ERROR
     * @see ILayer#MISSING
     * @see ILayer#WAIT
     * @see ILayer#WARNING
     * @see ILayer#WORKING
     * @see ILayer#UNCONFIGURED
     * @uml.property name="status"
     */
    public int getStatus();
    /**
     * A message to provide the user with additional feed back about the current rendering status.
     * <p>
     * This is used to provide feedback for a Layer's rendering status.
     * </p>
     *
     * @see ILayer#getStatusMessage()
     * @see #setStatus(int)
     * @see #getStatus()
     * @see #getStatusMessage()
     * @return message to provide the user with additional feed back about the current rendering
     *         status.
     * @uml.property name="statusMessage"
     */
    public String getStatusMessage();

    /**
     * Sets the current rendering status message
     *
     * @param message the status message
     * @see ILayer#getStatusMessage()
     * @see ILayer#getStatus()
     * @see #getStatusMessage()
     * @see #getStatus()
     * @uml.property name="statusMessage"
     */
    public void setStatusMessage( String message );

    public IRenderContext copy();

    /**
     * Returns the labeller for the next rendering.
     *
     * @return the labeller that draws the labels on the top of the map.
     */
    public ILabelPainter getLabelPainter();
}
