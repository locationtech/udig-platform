/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
