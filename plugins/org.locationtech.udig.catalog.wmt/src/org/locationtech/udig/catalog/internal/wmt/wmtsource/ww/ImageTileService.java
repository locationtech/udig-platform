/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wmt.wmtsource.ww;

import java.text.MessageFormat;

import org.locationtech.udig.catalog.internal.wmt.tile.WWTile.WWTileName;

import org.jdom2.Element;

/**
 * Represents &lt;ImageTileService&gt; inside a &lt;ImageAccessor&gt;
 * 
 * @author to.srwn
 * @since 1.1.0
 */
public class ImageTileService extends TileService {
    /*
     * {0}: ServerUrl (e.g. http://worldwind25.arc.nasa.gov/tile/tile.aspx)
     * {1}: DataSetName (e.g. 105)
     * {2}: Zoom-Level
     * {3}: Column Tile Number (x)
     * {4}: Row Tile Number (y)
     */
    public static final String REQUEST = "{0}?T={1}&L={2}&X={3}&Y={4}"; //$NON-NLS-1$
    
    private String serverUrl;
    private String dataSetName;
    
    public ImageTileService(Element xmlElement) {
        serverUrl = xmlElement.getChildText("ServerUrl"); //$NON-NLS-1$
        dataSetName = xmlElement.getChildText("DataSetName"); //$NON-NLS-1$
    }
    
    @Override
    public String getTileRequest(WWTileName tileName) {
        return MessageFormat.format(REQUEST, new Object[] {
                serverUrl, 
                dataSetName,
                Integer.toString(tileName.getZoomLevel()),
                Integer.toString(tileName.getX()),
                Integer.toString(tileName.getY())
        });
    }
}
