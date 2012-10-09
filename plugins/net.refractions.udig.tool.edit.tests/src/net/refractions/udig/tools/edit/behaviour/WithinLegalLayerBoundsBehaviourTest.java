/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit.behaviour;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import net.refractions.udig.AbstractProjectUITestCase;
import net.refractions.udig.catalog.tests.CatalogTests;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.enablement.WithinLegalLayerBoundsBehaviour;
import net.refractions.udig.tools.edit.support.TestHandler;
import net.refractions.udig.ui.WaitCondition;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class WithinLegalLayerBoundsBehaviourTest extends AbstractProjectUITestCase {

    private TestHandler handler;
    private Map map;
    
    @Before
    public void setUp() throws Exception {
        handler=new TestHandler();
        map=(Map) handler.getContext().getMap();
        GeometryFactory fac=new GeometryFactory();
        Point geom=fac.createPoint(new Coordinate(-564121,-1632497));
        SimpleFeature[] feature = UDIGTestUtil.createTestFeatures("test", new Point[]{geom}, new String[]{"name"}, CRS.decode("EPSG:2065")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Layer layer = map.getLayerFactory().createLayer(CatalogTests.createGeoResource(feature, true));
        map.getLayersInternal().add( layer );
        map.getViewportModelInternal().setCRS(DefaultGeographicCRS.WGS84);
        map.getViewportModelInternal().zoomToBox(layer.getBounds(null, map.getViewportModel().getCRS()));
        
        ApplicationGIS.openMap(map);
        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

            public boolean isTrue() {
                return ApplicationGIS.getActiveMap()!=null;
            }
            
        }, false);
        map.getEditManagerInternal().setSelectedLayer(layer);
    }

    @Test
    public void testLegal() throws Exception {
        handler.setCurrentState(EditState.ILLEGAL);
        WithinLegalLayerBoundsBehaviour behav=new WithinLegalLayerBoundsBehaviour();
        MapMouseEvent e=new MapMouseEvent(map.getRenderManager().getMapDisplay(), 0,0, 0,0,0);
        
        assertNull( behav.isEnabled(handler, e, EventType.MOVED) );
    }
    
    @Test
    public void testIllegal() throws Exception {
        map.getViewportModelInternal().setBounds(-300, -250, -180, -140);
        
        System.out.println(map.getViewportModel().getBounds());
        
        WithinLegalLayerBoundsBehaviour behav=new WithinLegalLayerBoundsBehaviour();
        MapMouseEvent e=new MapMouseEvent(map.getRenderManager().getMapDisplay(), 0,0, 0,0,0);
        behav.isEnabled(handler, e, EventType.MOVED);
        
        assertNotNull( behav.isEnabled(handler, e, EventType.MOVED));
    }

}
