/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wmt.tile;

import java.util.List;

import org.locationtech.udig.catalog.internal.wmt.wmtsource.WMTSource;
import org.locationtech.udig.catalog.wmsc.server.TiledWebMapServer;
import org.locationtech.udig.catalog.wmsc.server.WMSTileSet;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Envelope;

public class WMTTileSetWrapper extends WMSTileSet {
    private WMTSource wmtSource;
    
    
    public WMTTileSetWrapper(WMTSource wmtSource) {
        this.wmtSource = wmtSource;
    }

    @Override
    public String createQueryString(Envelope tile) {
        return super.createQueryString(tile);
    }

    @Override
    public ReferencedEnvelope getBounds() {
        return super.getBounds();
    }

    @Override
    public List<Envelope> getBoundsListForZoom( Envelope bounds, double zoom ) {
        return super.getBoundsListForZoom(bounds, zoom);
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return super.getCoordinateReferenceSystem();
    }

    @Override
    public String getEPSGCode() {
        return super.getEPSGCode();
    }

    @Override
    public String getFormat() {
        return "image\\" + wmtSource.getFileFormat(); //$NON-NLS-1$
    }

    @Override
    public int getHeight() {
        return wmtSource.getTileHeight();
    }

    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public String getLayers() {
        return ""; //$NON-NLS-1$
    }

    @Override
    public int getNumLevels() {
        return 0;
    }

    @Override
    public double[] getResolutions() {
        return null;
    }

    @Override
    public TiledWebMapServer getServer() {
        return null;
    }

    @Override
    public String getStyles() {
        return ""; //$NON-NLS-1$
    }

    @Override
    public int getWidth() {
        return wmtSource.getTileWidth();
    }
   

}
