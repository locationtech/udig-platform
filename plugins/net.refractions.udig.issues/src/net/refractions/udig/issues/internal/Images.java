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
package net.refractions.udig.issues.internal;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * Utility class managing ImageDescriptors for the plugin.
 */
public class Images {

    /** Hashtable of ImageDescriptors */
    private ImageRegistry imageCache;
    private URL baseURL;

    /**
     * Creates an image descriptor for later use.
     */
    synchronized ImageDescriptor create( String id ) {
        URL url = null;
        try {
            url = new URL(baseURL, id);
        } catch (MalformedURLException e) {
            return null;
        }
        ImageDescriptor image = ImageDescriptor.createFromURL(url);
        imageCache.put(id, image);
        return image;
    }

    /**
     * Returns the image descriptor for ID, or null if not found.
     * <p>
     * Images are from CatalogUIPlugin.getDefault().getImages()
     * </p>
     * 
     * @param id
     * @return ImageDescriptor, or null if there is no such image.
     */
    public static ImageDescriptor getDescriptor( String id ) {
        Images images = IssuesActivator.getDefault().getImages();
        ImageDescriptor found = images.imageCache.getDescriptor(id);
        if (found != null) {
            return found;
        }
        return images.create(id);
    }
    /**
     * Returns the image associated with the given key, or <code>null</code> if none.
     * 
     * @param id the key
     * @return the image, or <code>null</code> if none
     */
    public static Image get( String id ) {
        Images images = IssuesActivator.getDefault().getImages();
        Image image = images.imageCache.get(id);
        if( image == null ){
        	images.create(id);
        }
		return images.imageCache.get(id);
    }
    /**
     * Clean up Images during plugin shutdown.
     */
    public void cleanUp() {
        imageCache = null; // Display shutdown will clear imageCache
    }
    
    

	public synchronized void ensureReady() {
		if( imageCache == null ){
			IssuesActivator issuesActivator = IssuesActivator.getDefault();
			imageCache = issuesActivator.getImageRegistry();
	        baseURL = issuesActivator.getBundle().getEntry(IssuesActivator.ICONS_PATH);
		}
	}
}