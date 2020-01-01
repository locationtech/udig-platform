/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.behaviour;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.catalog.tests.CatalogTests;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EventType;
import org.locationtech.udig.tools.edit.enablement.WithinLegalLayerBoundsBehaviour;
import org.locationtech.udig.tools.edit.support.TestHandler;
import org.locationtech.udig.ui.WaitCondition;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;
import org.opengis.feature.simple.SimpleFeature;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

@Ignore("in setUp() : MismatchedReferenceSystemException: The coordinate reference system must be the same for all objects ")
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
