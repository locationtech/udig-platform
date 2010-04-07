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
package net.refractions.udig.project.ui.internal;

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
        Images images = ProjectUIPlugin.getDefault().getImages();
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
        Images images = ProjectUIPlugin.getDefault().getImages();
        return images.imageCache.get(id);
    }
    
    /**
     * Initializes the table of images used in this plugin.
     * <p>
     * The Images from ISharedImages will be registered with
     * CatalogUIPlugin.getDefault().getImageRegistry().
     * 
     * @param url
     * @param shared
     */
    public synchronized void initializeImages( URL url, ImageRegistry shared ) {
        imageCache = shared;
        baseURL = url;

        // objects
        create(ISharedImages.MAP_OBJ);
        create(ISharedImages.PAGE_OBJ);
        create(ISharedImages.PROJECT_OBJ);
        create(ISharedImages.COLLAPSE_ALL);
        create(ISharedImages.LINK);
        create(ISharedImages.ADD_CO);
        create(ISharedImages.D_ADD_CO);
        create(ISharedImages.D_COLLAPSE_ALL);
        create(ISharedImages.D_LINK);
        create(ISharedImages.ACTIVE_LINK);
        create(ISharedImages.LAYER_OBJ);
        create(ISharedImages.MAP_FOLDER_OBJ);
        create(ISharedImages.COLLAPSE_ALL);
        create(ISharedImages.NEW_PROJECT);
        create(ImageConstants.UP_CO);
        create(ImageConstants.DOWN_CO);
        create(ImageConstants.NEW_WIZBAN);
        create(ImageConstants.NEWFOLDER_WIZBAN);
        create(ImageConstants.NEWLAYER_WIZBAN);
        create(ImageConstants.DROP_DOWN_BUTTON);
        create(ImageConstants.NEWMAP_WIZBAN);
        create(ImageConstants.NEWPAGE_WIZBAN);
        create(ImageConstants.NEWPROJECT_WIZBAN);
        create(ImageConstants.NEWTEMPLATE_WIZBAN);

        create(ImageConstants.PRIORITY_CRITICAL);
        create(ImageConstants.PRIORITY_HIGH);
        create(ImageConstants.PRIORITY_LOW);
        create(ImageConstants.PRIORITY_TRIVIAL);
        create(ImageConstants.PRIORITY_WARNING);

        create(ImageConstants.RESOLUTION_RESOLVED);
        create(ImageConstants.RESOLUTION_UNKNOWN);
        create(ImageConstants.RESOLUTION_UNRESOLVED);
        create(ImageConstants.RESOLUTION_VIEWED);

        create(ImageConstants.GOTO_ISSUE);
        create(ImageConstants.DELETE);
        create(ImageConstants.DELETE_GROUP);

        create(ImageConstants.LINKED_DISABLED_CO);
        create(ImageConstants.LINKED_ENABLED_CO);
    }
    
    /**
     * Clean up Images during plugin shutdown.
     */
    public void cleanUp() {
        imageCache = null; // Display shutdown will clear imageCache
    }
}