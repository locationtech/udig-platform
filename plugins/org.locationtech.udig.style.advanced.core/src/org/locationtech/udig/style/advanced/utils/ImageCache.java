/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.utils;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.locationtech.udig.style.advanced.core.AdvancedStylePlugin;

/**
 * A singleton cache for images.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class ImageCache {

    public static final String ADD = "icons/add.gif"; //$NON-NLS-1$
    public static final String ADDGROUP = "icons/addgroup.gif"; //$NON-NLS-1$
    public static final String APPLY = "icons/apply.gif"; //$NON-NLS-1$
    public static final String DELETE = "icons/delete.gif"; //$NON-NLS-1$
    public static final String DELETEALL = "icons/deleteall.gif"; //$NON-NLS-1$
    public static final String DOWN = "icons/down.gif"; //$NON-NLS-1$
    public static final String IMPORT = "icons/import.gif"; //$NON-NLS-1$
    public static final String OPEN = "icons/open.gif"; //$NON-NLS-1$
    public static final String UP = "icons/up.gif"; //$NON-NLS-1$
    public static final String SAVE = "icons/save.gif"; //$NON-NLS-1$
    public static final String SAVEALL = "icons/saveall.gif"; //$NON-NLS-1$
    public static final String ONECLICKEXPORT = "icons/export.gif"; //$NON-NLS-1$

    private static ImageCache imageCache;

    private HashMap<String, Image> imageMap = new HashMap<String, Image>();

    private ImageCache() {
    }

    public static ImageCache getInstance() {
        if (imageCache == null) {
            imageCache = new ImageCache();
        }
        return imageCache;
    }

    /**
     * Get an image for a certain key.
     * 
     * <p><b>The only keys to be used are the static strings in this class!!</b></p>
     * 
     * @param key a file key, as for example {@link ImageCache#DATABASE_VIEW}.
     * @return the image.
     */
    public Image getImage( String key ) {
        Image image = imageMap.get(key);
        if (image == null) {
            image = createImage(key);
            imageMap.put(key, image);
        }
        return image;
    }

    private Image createImage( String key ) {
        ImageDescriptor id = AbstractUIPlugin.imageDescriptorFromPlugin(AdvancedStylePlugin.PLUGIN_ID, key);
        Image image = id.createImage();
        return image;
    }

    /**
     * Disposes the images and clears the internal map.
     */
    public void dispose() {
        Set<Entry<String, Image>> entrySet = imageMap.entrySet();
        for( Entry<String, Image> entry : entrySet ) {
            entry.getValue().dispose();
        }
        imageMap.clear();
    }

}
