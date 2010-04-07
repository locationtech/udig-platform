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
package net.refractions.udig.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * The OverlayCache is works specifically with DecoratorOverlayIcon allowing
 * decorators while not wasting resources.
 * <p>
 * Note this class is limited to working with DecoratorOveralayIcon since it
 * has a working equals and hashcode implemenation.
 * </p>
 */
public class OverlayCache {
    
    private ImageCache imageCache = new ImageCache(); /*from OverlayIcon to Image*/

    /**
     * Returns and caches an image corresponding to the specified icon.
     * @param icon the icon
     * @return the image
     */
    Image getImageFor(DecoratorOverlayIcon icon) {
        return imageCache.getImage(icon);
    }

    /**
     * Disposes of all images in the cache.
     */
    public void disposeAll() {
        imageCache.dispose();
    }

    /**
     * Apply the descriptors for the receiver to the supplied image.
     * <p>
     *      * <p>
     * Example use:<pre><code>
     * applyDescriptors( base,
     *                   new ImageDescriptor[][]={
     *                       null,     // TOP_LEFT     unsure if we will use this
     *                       modified, // TOP_RIGHT    indicate content modified (w/ *) 
     *                       status,   // BOTTOM_LEFT  called the auxiliary overlay warn, error, success
     *                       cached,   // BOTTOM_RIGHT not yet defined, recommened cache status...  
     *                       null,     // UNDERLAY     not sure if we will use this
     *                   }
     * );
     * </code></pre>
     * </p>
     * </p>
     * @param source
     * @param descriptors
     * @return Image
     */
    public Image applyDescriptors(Image source, ImageDescriptor[] descriptors) {
        DecoratorOverlayIcon icon = new DecoratorOverlayIcon(source, descriptors );
        return getImageFor(icon);
    }
    public Image applyStatus(Image source, ImageDescriptor status ) {
        DecoratorOverlayIcon icon = new DecoratorOverlayIcon(source, 
                new ImageDescriptor[] { null, null, status, null, null} );
        return getImageFor(icon);
    }
}