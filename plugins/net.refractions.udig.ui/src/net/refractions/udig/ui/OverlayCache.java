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