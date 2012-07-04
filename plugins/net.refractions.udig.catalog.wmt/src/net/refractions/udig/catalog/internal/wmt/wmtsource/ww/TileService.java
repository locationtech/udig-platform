/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.internal.wmt.wmtsource.ww;

import net.refractions.udig.catalog.internal.wmt.tile.WWTile.WWTileName;

import org.jdom.Element;

/**
 * Represents a TileService of a &lt;ImageAccessor&gt;:
 * either &lt;ImageTileService&gt; or &lt;WMSAccessor&gt;
 * 
 * see: http://worldwindxml.worldwindcentral.com/zoomit.xml?version=1.4.0.0
 * 
 * @author to.srwn
 * @since 1.1.0
 */
public abstract class TileService {
    /**
     * Returns the url to fetch the tile from a given tile-name.
     *
     * @param tileName
     * @return
     */
    public abstract String getTileRequest(WWTileName tileName);
    
    /**
     * Depending on if <ImageAccessor> contains either <ImageTileService> or 
     * <WMSAccessor>, a TileService-implementation is returned.
     *
     * @param imageAccessorElement
     * @return
     * @throws Exception
     */
    public static TileService createTileService(Element imageAccessorElement, ImageAccessor imageAccessor) throws Exception {
        if (imageAccessorElement.getChild("ImageTileService") != null) { //$NON-NLS-1$
            
            return new ImageTileService(imageAccessorElement.getChild("ImageTileService")); //$NON-NLS-1$
        } else if (imageAccessorElement.getChild("WMSAccessor") != null) { //$NON-NLS-1$
            
            return new WMSTileService(imageAccessorElement.getChild("WMSAccessor"), imageAccessor); //$NON-NLS-1$
        }
        
        throw new Exception("[TileService.createTileService] Unknown TileService"); //$NON-NLS-1$
    }
}
