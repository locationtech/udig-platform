/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wmsc;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.ui.CatalogUIPlugin;
import org.locationtech.udig.catalog.ui.ISharedImages;
import org.locationtech.udig.catalog.wmsc.server.TileSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.ows.wms.xml.WMSSchema;
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
