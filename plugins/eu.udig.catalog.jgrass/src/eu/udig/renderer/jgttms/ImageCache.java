/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.renderer.jgttms;

import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * A singleton cache for images.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
@SuppressWarnings("nls")
public class ImageCache {

    private static final int LIMIT = 30;

    private static ImageCache imageCache;

    private LinkedHashMap<String, BufferedImage> imageMap = new LinkedHashMap<String, BufferedImage>();

    private ImageCache() {
    }

    public static ImageCache getInstance() {
        if (imageCache == null) {
            imageCache = new ImageCache();
        }
        return imageCache;
    }

    public void addImage( String path, BufferedImage image ) {
        if (imageMap.size() > LIMIT) {
            Set<String> keySet = imageMap.keySet();
            for( String key : keySet ) {
                imageMap.remove(key);
                break;
            }
        }
        imageMap.put(path, image);
    }

    public BufferedImage getImage( String key ) {
        BufferedImage image = imageMap.get(key);
        return image;
    }

    public void dispose() {
        imageMap.clear();
    }

}
