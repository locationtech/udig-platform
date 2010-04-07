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
package net.refractions.udig.catalog.internal.ui;

import java.net.MalformedURLException;
import java.net.URL;

import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.ISharedImages;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * The image descrptors for the plugin
 */
public class Images implements ISharedImages {

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
     * @param id the key
     * @return ImageDescriptor, or null if there is no such image.
     */
    public static ImageDescriptor getDescriptor( String id ) {
        Images images = (Images) CatalogUIPlugin.getDefault().getImages();
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
    public Image get( String id ) {
        Images images = (Images) CatalogUIPlugin.getDefault().getImages();
        ImageDescriptor found = images.imageCache.getDescriptor(id);
        if (found == null) {
            images.create(id);
        }
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
        create(ImageConstants.ADD_CO);
        // create( ImageConstants.PATH_ETOOL );
        // create( ImageConstants.PATH_OBJECT );
        // create( ImageConstants.PATH_ELOCALTOOL );
        create(ImageConstants.DISCOVERY_WIZ);
        create( ImageConstants.CHOOSE_LAYER_WIZARD);
        create(ImageConstants.GRID_OBJ);
        create(ImageConstants.ADD_CO);
        create(ImageConstants.REFRESH_CO);
        create(ImageConstants.REMOVE_CO);

        // obj
        create(ImageConstants.FEATURE_FILE_OBJ);
        create(ImageConstants.FEATURE_OBJ);
        create(ImageConstants.REPOSITORY_OBJ);
        create(ImageConstants.FOLDER_OBJ);
        create(ImageConstants.MEMORY_OBJ);
        create(ISharedImages.GCE_OBJ);
        create(ISharedImages.DATABASE_OBJ);
        create(ISharedImages.DATASTORE_OBJ);
        create(ISharedImages.SERVER_OBJ);
        create(ISharedImages.WFS_OBJ);
        create(ISharedImages.WMS_OBJ);
    }
    /**
     * Cleanup image cache.
     */
    public void cleanUp() {
        imageCache = null; // Display shutdown will clear imageCache
    }

    /*
     * @see net.refractions.udig.catalog.ui.ISharedImages#getImageDescriptor(java.lang.String)
     */
    public ImageDescriptor getImageDescriptor( String id ) {
        return getDescriptor(id);
    }
}