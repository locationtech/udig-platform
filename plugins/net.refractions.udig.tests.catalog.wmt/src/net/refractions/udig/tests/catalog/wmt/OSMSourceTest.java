package net.refractions.udig.tests.catalog.wmt;

import java.util.Map;

import junit.framework.TestCase;
import net.refractions.udig.catalog.internal.wmt.WMTRenderJob;
import net.refractions.udig.catalog.internal.wmt.WMTScaleZoomLevelMatcher;
import net.refractions.udig.catalog.internal.wmt.ui.properties.WMTLayerProperties;
import net.refractions.udig.catalog.internal.wmt.wmtsource.OSMMapnikSource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.OSMOsmarenderSource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.OSMSource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.WMTSource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.WMTSourceFactory;
import net.refractions.udig.catalog.wmsc.server.Tile;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;

public class OSMSourceTest extends TestCase{
    
    private OSMSource source = (OSMMapnikSource) WMTSourceFactory.createSource(null, WMTSource.getRelatedServiceUrl(OSMMapnikSource.class), null, true);
    private OSMSource sourceOsmarender = (OSMOsmarenderSource) WMTSourceFactory.createSource(null, WMTSource.getRelatedServiceUrl(OSMOsmarenderSource.class), null, true);
       
    private ReferencedEnvelope world = new ReferencedEnvelope(-180, 180, -89.51, 89.51, DefaultGeographicCRS.WGS84);
    private WMTScaleZoomLevelMatcher getMatcher(WMTSource source, double scale) throws Exception {
        return WMTScaleZoomLevelMatcher.createMatcher(
                world, 
                scale, 
                source);
    }
    
    public void testZoomLevelMappingScaleFactor50() throws Exception {
                
        // Tests for scale-factor = 50
        int scaleFactorMiddle = 50;
           
        assertEquals(18, source.getZoomLevelFromMapScale(
                getMatcher(source, 0), scaleFactorMiddle));
        assertEquals(18, source.getZoomLevelFromMapScale(
                getMatcher(source, 2200), scaleFactorMiddle));
        assertEquals(18, source.getZoomLevelFromMapScale(
                getMatcher(source, 2260), scaleFactorMiddle));

        assertEquals(17, source.getZoomLevelFromMapScale(
                getMatcher(source, 4450), scaleFactorMiddle));
        assertEquals(17, source.getZoomLevelFromMapScale(
                getMatcher(source, 4550), scaleFactorMiddle));
        
        assertEquals(16, source.getZoomLevelFromMapScale(
                getMatcher(source, 9000), scaleFactorMiddle));
        
        assertEquals(3, source.getZoomLevelFromMapScale(
                getMatcher(source, 68000000), scaleFactorMiddle));

        assertEquals(2, source.getZoomLevelFromMapScale(
                getMatcher(source, 120000000), scaleFactorMiddle));
        assertEquals(2, source.getZoomLevelFromMapScale(
                getMatcher(source, 150000000), scaleFactorMiddle));
        
        // Osmarender Tests
        assertEquals(17, sourceOsmarender.getZoomLevelFromMapScale(
                getMatcher(sourceOsmarender, 1000), scaleFactorMiddle));
        assertEquals(2, sourceOsmarender.getZoomLevelFromMapScale(
                getMatcher(sourceOsmarender, 150000000), scaleFactorMiddle));
    }
    
    public void testZoomLevelMappingScaleFactor0() throws Exception {    
        // Tests for scale-factor = 0 (always scale up tiles)
        int scaleFactorUp = 0;
        
        assertEquals(18, source.getZoomLevelFromMapScale(
                getMatcher(source, 0), scaleFactorUp));
        assertEquals(18, source.getZoomLevelFromMapScale(
                getMatcher(source, 2200), scaleFactorUp));
        assertEquals(17, source.getZoomLevelFromMapScale(
                getMatcher(source, 2250), scaleFactorUp));

        assertEquals(17, source.getZoomLevelFromMapScale(
                getMatcher(source, 4450), scaleFactorUp));   
        assertEquals(16, source.getZoomLevelFromMapScale(
                getMatcher(source, 4480), scaleFactorUp)); 
        
        assertEquals(16, source.getZoomLevelFromMapScale(
                getMatcher(source, 8000), scaleFactorUp)); 
        
        assertEquals(2, source.getZoomLevelFromMapScale(
                getMatcher(source, 80000000), scaleFactorUp));

        assertEquals(2, source.getZoomLevelFromMapScale(
                getMatcher(source, 100000000), scaleFactorUp));
        assertEquals(2, source.getZoomLevelFromMapScale(
                getMatcher(source, 130000000), scaleFactorUp));
        
        // Osmarender Tests
        assertEquals(17, sourceOsmarender.getZoomLevelFromMapScale(
                getMatcher(sourceOsmarender, 1000), scaleFactorUp));
        assertEquals(2, sourceOsmarender.getZoomLevelFromMapScale(
                getMatcher(sourceOsmarender, 130000000), scaleFactorUp));
    }
    
    public void testZoomLevelMappingScaleFactor100() throws Exception {
        
        
        // Tests for scale-factor = 100 (always scale down tiles)
        int scaleFactorDown = 100;
        
        assertEquals(18, source.getZoomLevelFromMapScale(
                getMatcher(source, 0), scaleFactorDown));
        assertEquals(18, source.getZoomLevelFromMapScale(
                getMatcher(source, 2200), scaleFactorDown));
        assertEquals(18, source.getZoomLevelFromMapScale(
                getMatcher(source, 2300), scaleFactorDown));

        assertEquals(18, source.getZoomLevelFromMapScale(
                getMatcher(source, 4400), scaleFactorDown));
        assertEquals(17, source.getZoomLevelFromMapScale(
                getMatcher(source, 4480), scaleFactorDown));
        
        assertEquals(17, source.getZoomLevelFromMapScale(
                getMatcher(source, 8000), scaleFactorDown));
        
        assertEquals(3, source.getZoomLevelFromMapScale(
                getMatcher(source, 74000000), scaleFactorDown));

        assertEquals(3, source.getZoomLevelFromMapScale(
                getMatcher(source, 120000000), scaleFactorDown));
        assertEquals(2, source.getZoomLevelFromMapScale(
                getMatcher(source, 150000000), scaleFactorDown));  
        
        // Osmarender Tests
        assertEquals(17, sourceOsmarender.getZoomLevelFromMapScale(
                getMatcher(sourceOsmarender, 1000), scaleFactorDown));
        assertEquals(2, sourceOsmarender.getZoomLevelFromMapScale(
                getMatcher(sourceOsmarender, 150000000), scaleFactorDown));
        
    }
    
    public void testCutExtentInTiles() throws Exception {
        WMTLayerProperties layerProp = new WMTLayerProperties(null);
        
        ReferencedEnvelope env = new ReferencedEnvelope(-200, 220, -90, 95, DefaultGeographicCRS.WGS84);
        
        WMTRenderJob renderJob1 = WMTRenderJob.createRenderJob(
                env, 
                150000000, 
                source);
        
        Map<String, Tile> tiles = source.cutExtentIntoTiles(renderJob1, 50, true, layerProp);
        
        assertEquals(16, tiles.size());
    }
    
}
