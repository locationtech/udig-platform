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

import net.refractions.udig.catalog.internal.wmt.WMTPlugin;
import net.refractions.udig.catalog.internal.wmt.tile.WWTile.WWTileName.WWZoomLevel;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.jdom.Element;

/**
 * Represents a &lt;QuadTileSet&gt; of a &lt;LayerSet&gt;
 * see: http://worldwindxml.worldwindcentral.com/zoomit.xml?version=1.4.0.0
 * 
 * @author to.srwn
 * @since 1.1.0
 */
public class QuadTileSet {
    private String name;
    private String id;
    private ReferencedEnvelope bbox;
    
    private ImageAccessor accessor;
    
    
    public QuadTileSet(Element xmlElement, String id) throws Exception {
        this.name = xmlElement.getChildText("Name"); //$NON-NLS-1$
        this.id = LayerSet.constructId(id, name);
        
        parseBbox(xmlElement.getChild("BoundingBox")); //$NON-NLS-1$
        parseImageAccessor(xmlElement.getChild("ImageAccessor")); //$NON-NLS-1$
    }
    
    private void parseBbox(Element bboxElement) throws Exception {
        try {
            double maxY = getNumericValueFromChild(bboxElement.getChild("North")); //$NON-NLS-1$
            double minY = getNumericValueFromChild(bboxElement.getChild("South")); //$NON-NLS-1$
            double minX = getNumericValueFromChild(bboxElement.getChild("West")); //$NON-NLS-1$
            double maxX = getNumericValueFromChild(bboxElement.getChild("East")); //$NON-NLS-1$
            
            ReferencedEnvelope bounds = new ReferencedEnvelope(minX, maxX, minY, maxY,
                    DefaultGeographicCRS.WGS84);
            
            this.bbox = bounds;
        } catch (Exception exc) {
            WMTPlugin.log("[QuadTileSet.parseBbox] Getting the BBox failed", exc); //$NON-NLS-1$
            
            throw exc;
        }
    }
    
    private double getNumericValueFromChild(Element bboxChild) throws Exception {
        return Double.parseDouble(
                bboxChild.getChildText("Value") //$NON-NLS-1$
                );
    }
    
    private void parseImageAccessor(Element accessorElement) throws Exception {
        this.accessor = new ImageAccessor(accessorElement, this);
    }
    
    public ReferencedEnvelope getBounds() {
        return bbox;
    }
    
    public double[] getScaleList() {
        return accessor.getScaleList();
    }
    
    public WWZoomLevel getZoomLevel(int index) {
        return accessor.getZoomLevel(index);
    }

    public String getFileFormat() {
        return accessor.getFileFormat();
    }

    public String getName() {
        return name;
    }

    public int getTileSize() {
        return accessor.getTileSize();
    }

    public String getId() {
        return id;
    }
    /**
     * Used to clean up.
     */
    public void dispose() {
        // TODO Auto-generated method stub
    }
}
