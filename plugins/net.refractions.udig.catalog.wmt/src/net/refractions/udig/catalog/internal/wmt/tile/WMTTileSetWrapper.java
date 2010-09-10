package net.refractions.udig.catalog.internal.wmt.tile;

import java.util.List;

import net.refractions.udig.catalog.internal.wmt.wmtsource.WMTSource;
import net.refractions.udig.catalog.wmsc.server.TiledWebMapServer;
import net.refractions.udig.catalog.wmsc.server.WMSTileSet;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

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
