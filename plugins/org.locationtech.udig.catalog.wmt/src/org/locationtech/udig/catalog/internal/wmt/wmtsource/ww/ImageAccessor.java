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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.catalog.internal.wmt.tile.WWTile.WWTileName;
import org.locationtech.udig.catalog.internal.wmt.tile.WWTile.WWTileName.WWZoomLevel;
import org.locationtech.udig.project.internal.render.impl.ScaleUtils;

import org.eclipse.swt.widgets.Display;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.jdom2.Element;

/**
 * Represents a &lt;ImageAccessor&gt; of a &lt;QuadTileSet&gt;
 * see: http://worldwindxml.worldwindcentral.com/zoomit.xml?version=1.4.0.0
 * 
 * @author to.srwn
 * @since 1.1.0
 */
public class ImageAccessor {
    /* Width/Height of one tile at level 0 */
    private double levelZeroTileSizeDegrees;
    
    private int numberLevels;
    private int tileSize;
    private String tileFormat;
    
    private List<WWZoomLevel> zoomLevels;   
    private double[] scales;
    
    private QuadTileSet quadTileSet;
    private TileService tileService;
    
    public ImageAccessor(Element xmlElement, QuadTileSet quadTileSet) throws Exception {
        this.quadTileSet = quadTileSet;
        
        levelZeroTileSizeDegrees = Double.parseDouble(
                xmlElement.getChildText("LevelZeroTileSizeDegrees")); //$NON-NLS-1$
        
        numberLevels = Integer.parseInt(xmlElement.getChildText("NumberLevels")); //$NON-NLS-1$
        tileSize = Integer.parseInt(xmlElement.getChildText("TextureSizePixels")); //$NON-NLS-1$
        tileFormat = xmlElement.getChildText("ImageFileExtension"); //$NON-NLS-1$
        
        tileService = TileService.createTileService(xmlElement, this);
        
        setZoomLevels();
    }
    
    private void setZoomLevels() {
        ReferencedEnvelope bounds = quadTileSet.getBounds();
        
        zoomLevels = new ArrayList<WWZoomLevel>(numberLevels);
        scales = new double[numberLevels];
        
        double tileSizeDegrees = levelZeroTileSizeDegrees;
        for (int i = 0; i < numberLevels; i++) {
            WWZoomLevel zoomLevel = new WWZoomLevel(i,
                    getBoundOfFirstTile(tileSizeDegrees, bounds),
                    getScale(tileSizeDegrees),
                    this
                    );
            
            zoomLevel.setZoomLevel(i);
            zoomLevels.add(zoomLevel);
            scales[i] = zoomLevel.getScale();
            
            // as we are having QuadTiles, the size is divided with 2 for the next zoom-level
            tileSizeDegrees /= 2.0;
        }
    }
    
    private ReferencedEnvelope getBoundOfFirstTile(double tileSizeDegrees, ReferencedEnvelope bounds) {        
        return new ReferencedEnvelope(
                bounds.getMinX(),
                bounds.getMinX() + tileSizeDegrees,
                bounds.getMinY(),
                bounds.getMinY() + tileSizeDegrees,
                DefaultGeographicCRS.WGS84
        );
    }
    
    private double getScale(double tileSizeDegrees) {
        // We are building a envelope at (0/0) with the size of the first tile
        // to calculate the scale
        double halfWidth = tileSizeDegrees / 2.0;
        double halfHeight = tileSizeDegrees / 2.0;
        
        ReferencedEnvelope boundsAtEquator = new ReferencedEnvelope(-halfWidth, halfWidth, 
                -halfHeight, halfHeight, DefaultGeographicCRS.WGS84);
                            
        int dpi = 96;
        try{
            dpi = Display.getDefault().getDPI().x;
        }catch(Exception exc){}
        
        return ScaleUtils.calculateScaleDenominator(boundsAtEquator, 
                    new Dimension(tileSize, tileSize),
                    dpi);
    }
    
    public QuadTileSet getQuadTileSet() {
        return quadTileSet;
    }
    
    public double[] getScaleList() {
        return scales;
    }
    
    public WWZoomLevel getZoomLevel(int index) {
        return zoomLevels.get(index);
    }

    public String getTileUrl(WWTileName tileName) {
        return tileService.getTileRequest(tileName);
    }

    public String getFileFormat() {
        return tileFormat;
    }

    public int getTileSize() {
        return tileSize;
    }
}
