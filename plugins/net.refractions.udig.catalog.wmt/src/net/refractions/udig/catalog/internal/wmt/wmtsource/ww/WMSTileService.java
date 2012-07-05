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

import java.text.DecimalFormat;

import net.refractions.udig.catalog.internal.wmt.tile.WWTile;
import net.refractions.udig.catalog.internal.wmt.tile.WWTile.WWTileName;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.jdom.Element;

/**
 * Represents &lt;WMSAccessor&gt; inside a &lt;ImageAccessor&gt;
 * 
 * @author to.srwn
 * @since 1.1.0
 */
public class WMSTileService extends TileService {
    private ImageAccessor imageAccessor;
    
    /* the base-url, e.g.: http://wms.jpl.nasa.gov/wms.cgi */
    private String serverGetMapUrl;
    
    private String version;
    private String imageFormat;
    private String layerName;
    private String layerStyle;
    private boolean useTransparency;
    
    private static final DecimalFormat formatter = new DecimalFormat("##0.#################"); //$NON-NLS-1$
    
    
    public WMSTileService(Element child, ImageAccessor imageAccessor) {
        this.imageAccessor = imageAccessor;
        
        serverGetMapUrl = child.getChildText("ServerGetMapUrl"); //$NON-NLS-1$
        version = child.getChildText("Version"); //$NON-NLS-1$
        imageFormat = child.getChildText("ImageFormat"); //$NON-NLS-1$
        layerStyle = child.getChildText("WMSLayerStyle"); //$NON-NLS-1$
        layerName = child.getChildText("WMSLayerName"); //$NON-NLS-1$
        
        String transparency = child.getChildText("UseTransparency"); //$NON-NLS-1$
        if (transparency != null && transparency.trim().toLowerCase().equals("true")) { //$NON-NLS-1$
            useTransparency = true;
        } else {
            useTransparency = false;
        }
    }

    /**
     * Construct the url to request the tile from the WMS.
     * 
     * see: http://worldwindcentral.com/wiki/WMS_Sources#WMS_request_format
     */
    @Override
    public String getTileRequest(WWTileName tileName) {
        StringBuilder request = new StringBuilder(serverGetMapUrl);
        
        
        request.append("?request=GetMap"); //$NON-NLS-1$
        request.append("&layers=" + layerName); //$NON-NLS-1$
        request.append("&srs=EPSG:4326"); //$NON-NLS-1$
        request.append("&width=" + imageAccessor.getTileSize()); //$NON-NLS-1$
        request.append("&height=" + imageAccessor.getTileSize()); //$NON-NLS-1$
        request.append("&format=" + imageFormat); //$NON-NLS-1$
        request.append("&version=" + version); //$NON-NLS-1$
        
        if (layerStyle != null && !layerStyle.isEmpty()) {
            request.append("&styles=" + layerStyle); //$NON-NLS-1$
        } else {
            request.append("&styles="); //$NON-NLS-1$
        }
        
        if (useTransparency && !imageFormat.toLowerCase().contains("jpeg")) { //$NON-NLS-1$
            request.append("&transparent=true"); //$NON-NLS-1$
        }
        
        addBboxString(request,tileName); 
        
        return request.toString();
    }

    private void addBboxString(StringBuilder request, WWTileName tileName) {
        ReferencedEnvelope bounds = WWTile.getExtentFromTileName(tileName);
        
        request.append("&bbox="); //$NON-NLS-1$
        request.append(parseDouble(bounds.getMinX()));
        request.append(',');
        request.append(parseDouble(bounds.getMinY()));
        request.append(',');
        request.append(parseDouble(bounds.getMaxX()));
        request.append(',');
        request.append(parseDouble(bounds.getMaxY()));    
    }
    
    private String parseDouble(double value) {
        return formatter.format(value).replace(',', '.');
    }

}
