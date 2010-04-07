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
package net.refractions.udig.internal.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * The image descriptors for the plugin
 */
public class Images {
            
    static ImageRegistry registry() {
        return UiPlugin.getDefault().getImageRegistry();
    }
    /**
     * Returns the image descriptor for ID, or null if not found.
     * <p>
     * Images are from RegistryUIPlugin.getDefault().getImages()
     * </p>
     * @return ImageDescriptor, or null if there is no such image.
     */
    public static ImageDescriptor getDescriptor( String id ){    
        ImageDescriptor found = registry().getDescriptor( id );
        if( found != null ) {
            return found;
        }
        return UiPlugin.getDefault().create( id );
    }
    /**
     * Returns the image associated with the given key, 
     * or <code>null</code> if none.
     *
     * @param key the key
     * @return the image, or <code>null</code> if none
     */
    public static Image get( String id ){
        Image found = registry().get( id );
        if( found == null ) {
            UiPlugin.getDefault().create( id );
        }
        return registry().get( id );        
    }
       
}