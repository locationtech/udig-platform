/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
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
package net.refractions.udig.catalog.internal.wmsc;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.ISharedImages;
import net.refractions.udig.catalog.wmsc.server.TileSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.wms.xml.WMSSchema;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * 
 * A WMSC GeoResource info.
 * 
 * @author Emily Gouge (Refractions Research, Inc.)
 * @since 1.1.0
 */
public class WMSCGeoResourceInfo extends IGeoResourceInfo {
   
    /** WMSResourceInfo resource field */
    private final WMSCGeoResourceImpl resource;

    /**
     * Creates a new info class about a given geo resource.
     * 
     * @param geoResourceImpl
     * @param monitor
     * @throws IOException
     */
    public WMSCGeoResourceInfo( WMSCGeoResourceImpl geoResourceImpl, IProgressMonitor monitor )
            throws IOException {
        
        resource = geoResourceImpl;
       
        TileSet tile = resource.getTileSet();

        this.name = tile.getLayers();
        this.title = this.name + "(" + tile.getEPSGCode() + ":" + tile.getFormat() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        this.description = tile.getLayers() + " : " + tile.getEPSGCode() + " : " + tile.getFormat();  //$NON-NLS-1$//$NON-NLS-2$
        this.bounds = tile.getBounds();

        List<String> keywordsFromWMSC = new ArrayList<String>();
        keywordsFromWMSC.add("WMS-C"); //$NON-NLS-1$
        keywordsFromWMSC.add(this.name);
        this.keywords = keywordsFromWMSC.toArray(new String[keywordsFromWMSC.size()]);

        super.icon = CatalogUIPlugin.getDefault().getImageDescriptor(ISharedImages.GRID_OBJ);

    }
    
    public CoordinateReferenceSystem getCRS() { // part of Coverage
        if (bounds == null) {
            return null;
        }
        return getBounds().getCoordinateReferenceSystem();
    }
    
    @Override
    public URI getSchema() {
        return WMSSchema.NAMESPACE;
    }
}
