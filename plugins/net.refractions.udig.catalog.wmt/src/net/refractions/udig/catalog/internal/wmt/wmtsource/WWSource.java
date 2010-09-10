package net.refractions.udig.catalog.internal.wmt.wmtsource;

import net.refractions.udig.catalog.internal.wmt.tile.WWTile;
import net.refractions.udig.catalog.internal.wmt.tile.WWTile.WWTileFactory;
import net.refractions.udig.catalog.internal.wmt.tile.WWTile.WWTileName.WWZoomLevel;
import net.refractions.udig.catalog.internal.wmt.wmtsource.ww.QuadTileSet;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class WWSource extends WMTSource{
    
    private QuadTileSet quadTileSet;
    
    public static final ReferencedEnvelope WORLD_BOUNDS =
        new ReferencedEnvelope(-180, 180, -90, 90, DefaultGeographicCRS.WGS84);
    
    public WWSource(QuadTileSet quadTileSet) {
        this.quadTileSet = quadTileSet;
    }
    
    public ReferencedEnvelope getBounds() {
        return quadTileSet.getBounds();
    }
    
    public CoordinateReferenceSystem getProjectedTileCrs() {
        return DefaultGeographicCRS.WGS84;
    }

    @Override
    public double[] getScaleList() {
        return quadTileSet.getScaleList();
    }

    @Override
    public WWTileFactory getTileFactory() {
        return new WWTile.WWTileFactory();
    }
           
    @Override
    public String getFileFormat() {
        return quadTileSet.getFileFormat();
    }

    @Override
    public String getName() {
        return quadTileSet.getName();
    }

    @Override
    public int getTileHeight() {
        return quadTileSet.getTileSize();
    }

    @Override
    public int getTileWidth() {
        return getTileHeight();
    }
    
    public String getId() {
        return quadTileSet.getId();
    }

    public WWZoomLevel getZoomLevel(int index) {
        return quadTileSet.getZoomLevel(index);
    }
}
