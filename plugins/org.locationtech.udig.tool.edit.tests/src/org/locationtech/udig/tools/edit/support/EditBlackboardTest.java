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
package org.locationtech.udig.tools.edit.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.project.internal.render.impl.ScaleUtils;
import org.locationtech.udig.tool.edit.tests.TestsPlugin;
import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;
import org.locationtech.udig.tools.edit.support.EditBlackboardEvent.EventType;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.gml.GMLFilterDocument;
import org.geotools.gml.GMLFilterFeature;
import org.geotools.gml.GMLFilterGeometry;
import org.geotools.gml.GMLReceiver;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.referencing.operation.transform.IdentityTransform;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.operation.MathTransform;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

public class EditBlackboardTest {
    static {
       new PreferenceUtil(){
            {
                instance=this;
            }
            @Override
            public int getVertexRadius() {
                return 0;
            }
        };
    }
    
    AffineTransform transform=AffineTransform.getTranslateInstance(10,5);
    private MathTransform layerToWorld;

    java.awt.Point SCREEN=new java.awt.Point(500,500);

    @Before
    public void setUp() throws Exception {
        layerToWorld=IdentityTransform.create(2);
    }

    @Test
    public void testSetup() throws Exception {
        EventListener l=new EventListener();
        EditBlackboard map = new EditBlackboard(SCREEN.x,SCREEN.y, transform, layerToWorld);
        map.getListeners().add(l);
        assertPixMapState(map,1,0,0,0);

        map.addPoint(10,5,map.getGeoms().get(0).getShell());
        assertPixMapState(map,1,1,0,0);
        assertEquals(10, map.getGeoms().get(0).getShell().getPoint(0).getX() );
        assertEquals(5, map.getGeoms().get(0).getShell().getPoint(0).getY() );
        assertEquals(EventType.ADD_POINT, l.event.getType());
        EditBlackboardEvent editBlackboardEvent = l.getEditBlackboardEvent();
        assertEquals(null, editBlackboardEvent.getOldValue());
        assertEquals(Point.valueOf(10,5), editBlackboardEvent.getNewValue());
        assertEquals(map.getGeoms().get(0).getShell(), editBlackboardEvent.getSource());
        
        map.addPoint(10,10,map.getGeoms().get(0).getShell());

        assertPixMapState(map,1,2,0,0);
        assertEquals(10, map.getGeoms().get(0).getShell().getPoint(1).getX() );
        assertEquals(10, map.getGeoms().get(0).getShell().getPoint(1).getY() );
        assertEquals(EventType.ADD_POINT, l.event.getType());
        editBlackboardEvent = l.getEditBlackboardEvent();
        assertEquals(null, editBlackboardEvent.getOldValue());
        assertEquals(Point.valueOf(10,10), editBlackboardEvent.getNewValue());
        assertEquals(map.getGeoms().get(0).getShell(), editBlackboardEvent.getSource());

        GeometryFactory factory=new GeometryFactory();
        Geometry geom=factory.createPoint(new Coordinate(10,5));
        map=new EditBlackboard(SCREEN.x,SCREEN.y, transform, layerToWorld);
        map.getListeners().add(l);
        Map<Geometry, EditGeom> mapping = map.setGeometries(geom, null);
        assertNotNull(mapping.get(geom));
        assertEquals(ShapeType.POINT, mapping.get(geom).getShapeType());
        assertPixMapState(map,1,1,0,0);
        assertEquals(20, map.getGeoms().get(0).getShell().getPoint(0).getX() );
        assertEquals(10, map.getGeoms().get(0).getShell().getPoint(0).getY() );
        assertEquals(EventType.SET_GEOMS, l.event.getType());
        editBlackboardEvent = l.getEditBlackboardEvent();
        assertEquals(1, ((List)editBlackboardEvent.getOldValue()).size());
        assertEquals(1, ((List)editBlackboardEvent.getNewValue()).size());
        assertEquals(map, editBlackboardEvent.getSource());
       
        geom=factory.createMultiPoint(new Coordinate[]{new Coordinate(10,5), new Coordinate(20,10)});
        map=new EditBlackboard(SCREEN.x,SCREEN.y, transform, layerToWorld);
        map.getListeners().add(l);
        String string = "featureID"; //$NON-NLS-1$
        mapping=map.setGeometries(geom, string);
        EditGeom next = mapping.values().iterator().next();
        assertEquals(ShapeType.POINT, next.getShapeType());
        assertEquals(string, next.getFeatureIDRef().get());
        assertNotNull(mapping.get(geom.getGeometryN(0)));
        assertNotNull(mapping.get(geom.getGeometryN(1)));
        assertPixMapState(map,2,1,0,0);
        assertEquals(20, map.getGeoms().get(0).getShell().getPoint(0).getX() );
        assertEquals(10, map.getGeoms().get(0).getShell().getPoint(0).getY() );
        assertEquals(30, map.getGeoms().get(1).getShell().getPoint(0).getX() );
        assertEquals(15, map.getGeoms().get(1).getShell().getPoint(0).getY() );
        assertEquals(new Coordinate(10,5), map.getGeoms().get(0).getShell().getCoord(0));
        assertEquals(new Coordinate(20,10), map.getGeoms().get(1).getShell().getCoord(0));
        editBlackboardEvent = l.getEditBlackboardEvent();
        assertEquals(2, ((List) editBlackboardEvent.getNewValue()).size());
        assertEquals(map, editBlackboardEvent.getSource());


        LinearRing ring = createShellRing(factory, 10);
        
        map=new EditBlackboard(SCREEN.x,SCREEN.y, transform, layerToWorld);
        map.getListeners().add(l);
        mapping=map.setGeometries(ring, null);
        assertEquals(ShapeType.LINE, mapping.get(ring).getShapeType());
        assertNotNull(mapping.get(ring));
        assertPixMapState(map,1,5,0,0);
        assertEquals(20, map.getGeoms().get(0).getShell().getPoint(0).getX() );
        assertEquals(10, map.getGeoms().get(0).getShell().getPoint(0).getY() );
        assertEquals(30, map.getGeoms().get(0).getShell().getPoint(1).getX() );
        assertEquals(10, map.getGeoms().get(0).getShell().getPoint(1).getY() );
        assertEquals(30, map.getGeoms().get(0).getShell().getPoint(2).getX() );
        assertEquals(15, map.getGeoms().get(0).getShell().getPoint(2).getY() );
        assertEquals(20, map.getGeoms().get(0).getShell().getPoint(3).getX() );
        assertEquals(15, map.getGeoms().get(0).getShell().getPoint(3).getY() );
        assertEquals(20, map.getGeoms().get(0).getShell().getPoint(4).getX() );
        assertEquals(10, map.getGeoms().get(0).getShell().getPoint(4).getY() );
        editBlackboardEvent = l.getEditBlackboardEvent();
        assertEquals(1, ((List) editBlackboardEvent.getNewValue()).size());
        assertEquals(map, editBlackboardEvent.getSource());
        
        map=new EditBlackboard(SCREEN.x,SCREEN.y, transform, layerToWorld);
        map.getListeners().add(l);
        Polygon polygon = createPolygon(factory, 10);
        mapping=map.setGeometries(polygon, null);
        assertEquals(ShapeType.POLYGON, mapping.get(polygon).getShapeType());
        assertNotNull(mapping.get(polygon));
        assertPixMapState(map,1,5,1,5);
        assertEquals(Point.valueOf(20,10), map.getGeoms().get(0).getShell().getPoint(0) );
        assertEquals(Point.valueOf(25,12), map.getGeoms().get(0).getHoles().get(0).getPoint(0) );
        assertEquals(Point.valueOf(30,10), map.getGeoms().get(0).getShell().getPoint(1) );
        assertEquals(Point.valueOf(28,12), map.getGeoms().get(0).getHoles().get(0).getPoint(1) );
        assertEquals(new Coordinate(15,7), map.getGeoms().get(0).getHoles().get(0).getCoord(0));
        assertEquals(new Coordinate(18,7), map.getGeoms().get(0).getHoles().get(0).getCoord(1));
        editBlackboardEvent = l.getEditBlackboardEvent();
        assertEquals(1, ((List) editBlackboardEvent.getNewValue()).size());
        assertEquals(map, editBlackboardEvent.getSource());

        geom=factory.createMultiPolygon(new Polygon[]{
           createPolygon(factory, 0), createPolygon(factory, 20)      
        });

        map=new EditBlackboard(SCREEN.x,SCREEN.y, transform, layerToWorld);
        map.getListeners().add(l);
        mapping=map.setGeometries(geom, null);
        assertPixMapState(map,2,5,1,5);
        editBlackboardEvent = l.getEditBlackboardEvent();
        assertEquals(2, ((List) editBlackboardEvent.getNewValue()).size());
        assertEquals(map, editBlackboardEvent.getSource());
        
    }

    private Polygon createPolygon(  GeometryFactory factory, int i ) {
        return factory.createPolygon(createShellRing(factory, i), new LinearRing[]{createHoleRing(factory, i)});
    }

    private LinearRing createHoleRing( GeometryFactory factory, int offset) {
        return factory.createLinearRing(new Coordinate[]{
                new Coordinate(offset+5, 7),
                new Coordinate(offset+8, 7),
                new Coordinate(offset+8, 8),
                new Coordinate(offset+5, 8),
                new Coordinate(offset+5, 7)
        });
    }

    private LinearRing createShellRing( GeometryFactory factory, int offset ) {
        return factory.createLinearRing(new Coordinate[]{
                new Coordinate(offset+0,5),
                new Coordinate(offset+10,5),
                new Coordinate(offset+10,10),
                new Coordinate(offset+0,10),
                new Coordinate(offset+0,5)
        });
    }

    /**
     * @param numGeoms number of expected geometries
     * @param numShellPoints expected number of points in shells of each geometry
     * @param numHoles expected number of holes  of each geometry
     * @param numHolesPoints expected number of points in all holes of each geometry
     */
    private void assertPixMapState( EditBlackboard map, int numGeoms, int numShellPoints, int numHoles, int numHolesPoints) {
        assertEquals("numGeoms", numGeoms, map.getGeoms().size()); //$NON-NLS-1$
        for( EditGeom geom : map.getGeoms() ) {
            assertEquals("numShellPoints", numShellPoints, geom.getShell().getNumPoints() ); //$NON-NLS-1$
            assertEquals("numHoles", numHoles, geom.getHoles().size() ); //$NON-NLS-1$
            for( PrimitiveShape hole : geom.getHoles() ) {
                assertEquals("numHolesPoints ", numHolesPoints, hole.getNumPoints() ); //$NON-NLS-1$                 
            }
        }
    }
    
    @Test
    public void testGetCandidate() throws Exception {
        GeometryFactory factory=new GeometryFactory();
        LinearRing ring1=createShellRing(factory, 10);
        LinearRing ring2=createShellRing(factory, 30);
        LinearRing ring3=createShellRing(factory, 32);
        EditBlackboard map = new EditBlackboard(SCREEN.x,SCREEN.y, transform, layerToWorld);
        Map<Geometry, EditGeom> mapping = map.setGeometries(ring1, null);
        mapping.putAll(map.addGeometry(ring2, null));
        mapping.putAll(map.addGeometry(ring3, null));
        assertPixMapState(map,3,5,0,0);
        
        List<ClosestEdge> candidates = map.getCandidates(22,0, true);
        assertEquals(1, candidates.size());
        assertSame( candidates.get(0).getGeom(), mapping.get(ring1));

        candidates = map.getCandidates(32,0, true);
        assertEquals(0, candidates.size());

        candidates = map.getCandidates(42,0, true);
        assertEquals(1, candidates.size());
        assertSame( candidates.get(0).getGeom(), mapping.get(ring2));
        
        candidates = map.getCandidates(51,12, true);
        assertEquals(2, candidates.size());
        assertTrue( candidates.get(0).getGeom()==mapping.get(ring3) || candidates.get(1).getGeom()==mapping.get(ring3));
        assertTrue( candidates.get(0).getGeom()==mapping.get(ring2) || candidates.get(1).getGeom()==mapping.get(ring2));
        
        // now testing geoms with holes
        Polygon polygon=createPolygon(factory, 10);
        mapping=map.setGeometries(ring1, null);
        mapping.putAll(map.addGeometry(polygon, null));

        candidates = map.getCandidates(26,12, true);
        assertEquals(1, candidates.size());
        assertSame( candidates.get(0).getGeom(), mapping.get(polygon));
    }
    
    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.PixelCoordMap.addCoord(int, int, Coordinate)'
     */
    @Test
    public void testAddCoord() {
        EventListener l=new EventListener();
        
        GeometryFactory factory=new GeometryFactory();
        Geometry geom=factory.createPoint(new Coordinate(10,5));
        EditBlackboard map = new EditBlackboard(SCREEN.x,SCREEN.y, transform, layerToWorld);
        map.getListeners().add(l);

        map.setGeometries(geom, null);
        assertPixMapState(map,1,1,0,0);
        
        EditGeom geomShape = map.getGeoms().get(0);
        map.addPoint(10,5,geomShape.getShell());
        assertPixMapState(map,1,2,0,0);
        assertEquals(1, map.getCoords(10,5).size());
        assertEquals(EventType.ADD_POINT, l.getEditBlackboardEvent().getType());
        EditBlackboardEvent editBlackboardEvent = l.getEditBlackboardEvent();
        assertEquals(null, editBlackboardEvent.getOldValue());
        assertEquals(Point.valueOf(10,5), editBlackboardEvent.getNewValue());
        
        LinearRing ring = createShellRing(factory, 10);
        
        map=new EditBlackboard(SCREEN.x,SCREEN.y, transform, layerToWorld);
        map.getListeners().add(l);
        Map<Geometry, EditGeom> mapping = map.setGeometries(ring, null);
        assertPixMapState(map,1,5,0,0);
        
        //add at a particular index.
        map.insertCoord(25,15, 3, mapping.get(ring).getShell());
        assertEquals(Point.valueOf(20,10),mapping.get(ring).getShell().getPoint(0));
        assertEquals(Point.valueOf(30,10),mapping.get(ring).getShell().getPoint(1));
        assertEquals(Point.valueOf(30,15),mapping.get(ring).getShell().getPoint(2));
        assertEquals(Point.valueOf(25,15),mapping.get(ring).getShell().getPoint(3));
        assertEquals(Point.valueOf(20,15),mapping.get(ring).getShell().getPoint(4));
        assertEquals(EventType.ADD_POINT, l.event.getType());
        editBlackboardEvent = l.getEditBlackboardEvent();
        assertEquals(null, editBlackboardEvent.getOldValue());
        assertEquals(Point.valueOf(25,15), editBlackboardEvent.getNewValue());
        assertEquals(mapping.get(ring).getShell(), editBlackboardEvent.getSource());

        //create a geom one point at a time   test ordering of geometries
        map=new EditBlackboard(SCREEN.x,SCREEN.y, transform, layerToWorld);
        map.getListeners().add(l);
        geomShape = map.getGeoms().get(0);
        map.addPoint(0,0, geomShape.getShell());
        map.addPoint(0,100, geomShape.getShell());
        map.addPoint(50,100, geomShape.getShell());
        map.addPoint(100,100, geomShape.getShell());
        map.addPoint(50,150, geomShape.getShell());
        
        assertEquals(Point.valueOf(0,0), geomShape.getShell().getPoint(0));
        assertEquals(Point.valueOf(0,100), geomShape.getShell().getPoint(1));
        assertEquals(Point.valueOf(50,100), geomShape.getShell().getPoint(2));
        assertEquals(Point.valueOf(100,100), geomShape.getShell().getPoint(3));
        assertEquals(Point.valueOf(50,150), geomShape.getShell().getPoint(4));

        //test the coordinates were created correcly
        assertEquals(new Coordinate(-9.5,-4.5), geomShape.getShell().getCoord(0));
        assertEquals(new Coordinate(-9.5,95.5), geomShape.getShell().getCoord(1));
        assertEquals(new Coordinate(40.5,95.5), geomShape.getShell().getCoord(2));
        assertEquals(new Coordinate(90.5,95.5), geomShape.getShell().getCoord(3));
        assertEquals(new Coordinate(40.5,145.5), geomShape.getShell().getCoord(4));
        
        // now making sure the the CoordMap is correctly updated too.
        assertEquals(1, map.getCoords(0,0).size());
        assertEquals(new Coordinate(-9.5,-4.5), map.getCoords(0,0).get(0));
        assertEquals(1, map.getCoords(0,100).size());
        assertEquals(new Coordinate(-9.5,95.5), map.getCoords(0,100).get(0));
        assertEquals(1, map.getCoords(0,100).size());
        assertEquals(new Coordinate(40.5,95.5), map.getCoords(50,100).get(0));
        assertEquals(1, map.getCoords(0,100).size());
        assertEquals(new Coordinate(90.5,95.5), map.getCoords(100,100).get(0));
        assertEquals(1, map.getCoords(50,150).size());
        assertEquals(new Coordinate(40.5,145.5), map.getCoords(50,150).get(0));

        //insert a vertex in a hole
        Polygon polygon=createPolygon(factory, 10);
        map=new EditBlackboard(SCREEN.x,SCREEN.y, transform, layerToWorld);
        map.getListeners().add(l);
        mapping = map.setGeometries(polygon, null);
        assertPixMapState(map,1,5,1,5);
        
        //add at a particular index.
        PrimitiveShape hole = mapping.get(polygon).getHoles().get(0);
        map.insertCoord(26,13, 3, hole);
        assertEquals(Point.valueOf(25,12),hole.getPoint(0));
        assertEquals(Point.valueOf(28,12),hole.getPoint(1));
        assertEquals(Point.valueOf(28,13),hole.getPoint(2));
        assertEquals(Point.valueOf(26,13),hole.getPoint(3));
        assertEquals(Point.valueOf(25,13),hole.getPoint(4));        
        editBlackboardEvent = l.getEditBlackboardEvent();
        assertEquals(null, editBlackboardEvent.getOldValue());
        assertEquals(Point.valueOf(26,13), editBlackboardEvent.getNewValue());
        assertEquals(hole, editBlackboardEvent.getSource());


        
        //add on an edge test ordering of geometries
        LinearRing ring2=createShellRing(factory, 30);
        LinearRing ring3=createShellRing(factory, 32);
        
        ring3.getCoordinateN(2).y=20;
        
        mapping=map.setGeometries(ring2, null);
        mapping.putAll(map.addGeometry(ring3, null));
        
        map.addToNearestEdge(54,12, mapping.get(ring2), true);
        assertEquals(1, map.getCoords(54,12).size());
        assertEquals(new Coordinate(44.5,7.5), mapping.get(ring2).getShell().getCoord(2));
        editBlackboardEvent = l.getEditBlackboardEvent();
        assertEquals(null, editBlackboardEvent.getOldValue());
        assertEquals(Point.valueOf(54,12), editBlackboardEvent.getNewValue());
        assertEquals(mapping.get(ring2).getShell(), editBlackboardEvent.getSource());
        
        
        mapping=map.setGeometries(ring2, null);
        mapping.putAll(map.addGeometry(ring3, null));
        
        List<ClosestEdge> added = map.addToNearestEdge(51,12,true);
        assertEquals(2, added.size());
        assertTrue( added.get(0).getGeom()==mapping.get(ring3) || added.get(1).getGeom()==mapping.get(ring3));
        assertTrue( added.get(0).getGeom()==mapping.get(ring2) || added.get(1).getGeom()==mapping.get(ring2));
        assertEquals(2, map.getCoords(51,12).size());
        assertEquals(new Coordinate(41.5,7.5), mapping.get(ring2).getShell().getCoord(2));
        assertEquals(new Coordinate(41.5,7.5), mapping.get(ring3).getShell().getCoord(2));
        editBlackboardEvent = l.getEditBlackboardEvent();
        assertEquals(null, editBlackboardEvent.getOldValue());
        assertEquals(Point.valueOf(51,12), editBlackboardEvent.getNewValue());
        assertTrue(((Collection) l.event.getSource()).contains(added.get(0).getPart())  );
        assertTrue(((Collection) l.event.getSource()).contains(added.get(1).getPart())  );
        
        // create hole one point at a time
        map=new EditBlackboard(SCREEN.x,SCREEN.y, transform, layerToWorld);
        map.getListeners().add(l);
        geomShape = map.getGeoms().get(0);
        hole = geomShape.newHole();
        try{
            map.addPoint(0,0, null);
            fail();
        }catch (Exception e) {
            //good
        }
     
        map.addPoint(0,0, hole);
        assertEquals(Point.valueOf(0,0), l.getEditBlackboardEvent().getNewValue());
        map.addPoint(0,100, hole);
        assertEquals(Point.valueOf(0,100), l.getEditBlackboardEvent().getNewValue());
        map.addPoint(50,100, hole);
        assertEquals(Point.valueOf(50,100), l.getEditBlackboardEvent().getNewValue());
        map.addPoint(100,100, hole);
        assertEquals(Point.valueOf(100,100), l.getEditBlackboardEvent().getNewValue());
        map.addPoint(50,150, hole);
        assertEquals(null, l.getEditBlackboardEvent().getOldValue());
        assertEquals(Point.valueOf(50,150), l.getEditBlackboardEvent().getNewValue());
        assertEquals(hole, l.getEditBlackboardEvent().getSource());
        
        
        assertEquals(Point.valueOf(0,0), geomShape.getHoles().get(0).getPoint(0));
        assertEquals(Point.valueOf(0,100), geomShape.getHoles().get(0).getPoint(1));
        assertEquals(Point.valueOf(50,100), geomShape.getHoles().get(0).getPoint(2));
        assertEquals(Point.valueOf(100,100), geomShape.getHoles().get(0).getPoint(3));
        assertEquals(Point.valueOf(50,150), geomShape.getHoles().get(0).getPoint(4));

        //test the coordinates were created correcly
        assertEquals(new Coordinate(-9.5,-4.5), geomShape.getHoles().get(0).getCoord(0));
        assertEquals(new Coordinate(-9.5,95.5), geomShape.getHoles().get(0).getCoord(1));
        assertEquals(new Coordinate(40.5,95.5), geomShape.getHoles().get(0).getCoord(2));
        assertEquals(new Coordinate(90.5,95.5), geomShape.getHoles().get(0).getCoord(3));
        assertEquals(new Coordinate(40.5,145.5), geomShape.getHoles().get(0).getCoord(4));

        // now making sure the the CoordMap is correctly updated too.
        assertEquals(1, map.getCoords(0,0).size());
        assertEquals(new Coordinate(-9.5,-4.5), map.getCoords(0,0).get(0));
        assertEquals(1, map.getCoords(0,100).size());
        assertEquals(new Coordinate(-9.5,95.5), map.getCoords(0,100).get(0));
        assertEquals(1, map.getCoords(0,100).size());
        assertEquals(new Coordinate(40.5,95.5), map.getCoords(50,100).get(0));
        assertEquals(1, map.getCoords(0,100).size());
        assertEquals(new Coordinate(90.5,95.5), map.getCoords(100,100).get(0));
        assertEquals(1, map.getCoords(50,150).size());
        assertEquals(new Coordinate(40.5,145.5), map.getCoords(50,150).get(0));
        
        // call add to edge so that it is added to a hole
        polygon=createPolygon(factory, 10);
        LinearRing ring1=createShellRing(factory, 10);
        mapping=map.setGeometries(ring1, null);
        mapping.putAll(map.addGeometry(polygon, null));

        added=map.addToNearestEdge(26,12, true);
        assertEquals(1, added.size());
        assertEquals(6, added.iterator().next().getGeom().getHoles().get(0).getNumCoords());
        
        map=new EditBlackboard(SCREEN.x,SCREEN.y, transform, this.layerToWorld );
        map.getListeners().add(l);
        added=map.addToNearestEdge(26,12, true);
        assertEquals(Point.valueOf(26,12), map.getGeoms().get(0).getShell().getPoint(0));
    }


    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.PixelCoordMap.moveCoords(int, int, int, int)'
     */
    @Test
    public void testMoveCoords() throws Exception, Exception {
        EventListener l=new EventListener();
        GeometryFactory factory=new GeometryFactory();
        LinearRing ring1=createShellRing(factory, 10);
        LinearRing ring2=createShellRing(factory, 10);
        LinearRing ring3=createShellRing(factory, 10);
        LinearRing ring4=createShellRing(factory, 40);
        
        EditBlackboard map = new EditBlackboard(SCREEN.x,SCREEN.y, transform, layerToWorld);
        map.getListeners().add(l);
        Map<Geometry, EditGeom> mapping = map.setGeometries(ring1, null);
        mapping.putAll(map.addGeometry(ring2, null));
        mapping.putAll(map.addGeometry(ring3, null));
        mapping.putAll(map.addGeometry(ring4, null));
        assertPixMapState(map,4,5,0,0);
        
        // move a vertex
        List<Coordinate> modified = map.moveCoords(20, 10, 15, 0);
        assertEquals(6, modified.size());
        assertEquals(6, map.getCoords(15,0).size());
        assertEquals(0,map.getCoords(20,10).size());
        assertEquals(new Coordinate(5, -5), mapping.get(ring1).getShell().getCoord(0));
        assertEquals(new Coordinate(5, -5), mapping.get(ring1).getShell().getCoord(4));
        EditBlackboardEvent editBlackboardEvent = l.getEditBlackboardEvent();
        assertEquals(EventType.MOVE_POINT, l.event.getType());
        assertEquals(Point.valueOf(20,10), editBlackboardEvent.getOldValue());
        assertEquals(Point.valueOf(15,0), editBlackboardEvent.getNewValue());

        assertEquals(new Coordinate(5, -5), mapping.get(ring2).getShell().getCoord(0));
        assertEquals(new Coordinate(5, -5), mapping.get(ring2).getShell().getCoord(4));
        
        assertEquals(new Coordinate(5, -5), mapping.get(ring3).getShell().getCoord(0));
        assertEquals(new Coordinate(5, -5), mapping.get(ring3).getShell().getCoord(4));
        
        assertEquals(3, map.getGeoms(15,0).size());
        assertEquals(0, map.getGeoms(20,10).size());
        
        assertEquals( Point.valueOf(15,0), map.getGeoms().get(0).getShell().getPoint(0));
        
        //put back
        map.moveCoords(15,0, 20,10);

        // move an edge (can't do it)
        modified = map.moveCoords(25,10, 30, 0);
        assertEquals(0, modified.size());

        // move to location that contains coords... don't clobber
        modified=map.moveCoords(20,10, 30, 10);
        assertEquals(6, modified.size());
        assertEquals(9, map.getCoords(30,10).size());
        assertEquals(3, map.getGeoms(30,10).size());
        
        map.moveCoords(50,10, 30, 10);
        assertEquals(4, map.getGeoms(30,10).size());
        
        // test the case where the layer is not in the same projection as the map.
        MathTransform albersToWGS84 = CRS.findMathTransform(CRS.decode("EPSG:3005"), DefaultGeographicCRS.WGS84, true); //$NON-NLS-1$
        map = new EditBlackboard(SCREEN.x,SCREEN.y, AffineTransform.getTranslateInstance(0,0), albersToWGS84);
        
        map.addPoint(0,0, map.getGeoms().get(0).getShell());
        map.addPoint(0,0, map.getGeoms().get(0).getShell());
        
        Coordinate c = new Coordinate();
        JTS.transform(map.getCoords(0,0).get(0), c, albersToWGS84);
        
        assertEquals(0, (int)c.x);
        assertEquals(0, (int)c.y);
        
        map.moveCoords(0,0, 10,5);
        
        JTS.transform(map.getCoords(10,5).get(0), c, albersToWGS84);
        
        assertEquals(10, (int)c.x);
        assertEquals(5, (int)c.y);        
        
        JTS.transform(map.getCoords(10,5).get(1), c, albersToWGS84);
        
        assertEquals(10, (int)c.x);
        assertEquals(5, (int)c.y);
        
    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.PixelCoordMap.deleteCoords(int, int)'
     */
    @Test
    public void testDeleteCoords() {
        
        GeometryFactory factory=new GeometryFactory();
        LinearRing ring1=createShellRing(factory, 10);
        LinearRing ring2=createShellRing(factory, 10);
        LinearRing ring3=createShellRing(factory, 10);
        EditBlackboard map = new EditBlackboard(SCREEN.x,SCREEN.y, transform, layerToWorld);
        EventListener l=new EventListener();
        map.getListeners().add(l);
        Map<Geometry, EditGeom> mapping = map.setGeometries(ring1, null);
        mapping.putAll(map.addGeometry(ring2, null));
        mapping.putAll(map.addGeometry(ring3, null));
        assertPixMapState(map,3,5,0,0);
        
        List<Coordinate> modified = map.removeCoordsAtPoint(20,10);
        assertEquals(6, modified.size());
        assertEquals(3, mapping.get(ring1).getShell().getNumPoints());
        assertEquals(3, mapping.get(ring1).getShell().getNumCoords());        
        EditBlackboardEvent editBlackboardEvent = l.getEditBlackboardEvent();
        assertEquals(EventType.REMOVE_POINT, l.event.getType());
        assertEquals(Point.valueOf(20,10), editBlackboardEvent.getOldValue());
        assertEquals(null, editBlackboardEvent.getNewValue());

        
        assertEquals(3, mapping.get(ring2).getShell().getNumPoints());
        assertEquals(3, mapping.get(ring2).getShell().getNumCoords());

        assertEquals(3, mapping.get(ring3).getShell().getNumPoints());
        assertEquals(3, mapping.get(ring3).getShell().getNumCoords());
        
        assertEquals(0, map.getGeoms(20,10).size());
        
        // delete nothing
        modified=map.removeCoordsAtPoint(0,0);
        assertEquals(0, modified.size());
        
        // make sure this works on holes
        Polygon poly = createPolygon(factory, 10);
        mapping=map.setGeometries(poly, null);
        modified=map.removeCoordsAtPoint(25,12);
        assertEquals(2, modified.size());
        assertEquals(3,mapping.get(poly).getHoles().get(0).getNumCoords());
        assertEquals(new Coordinate(15,7), modified.get(0));
        assertEquals(new Coordinate(15,7), modified.get(1));
    }
    
    @Test
    public void testTransform() throws Exception {
        MathTransform t=CRS.findMathTransform(CRS.decode("EPSG:3005"), DefaultGeographicCRS.WGS84); //$NON-NLS-1$
        AffineTransform translateInstance = AffineTransform.getTranslateInstance(180, 90);
        EditBlackboard map=new EditBlackboard( SCREEN.x, SCREEN.y, translateInstance, t);
        
        int x=-12+180;
        int y=52+90;
        
        map.addPoint(x,y, map.getGeoms().get(0).getShell());
        double [] tmp=new double[]{ x+.5,y+.5 };
        double [] expected=new double[2];
        translateInstance.inverseTransform(tmp, 0,tmp,0,1);
        t.inverse().transform(tmp, 0, expected,0,1);
        assertEquals(new Coordinate(expected[0], expected[1]),map.getCoords(x,y).get(0));
    }
    
    @Test
    public void testAddOverlappingVertex() throws Exception {
        new PreferenceUtil(){
            {
                instance=this;
            }
            @Override
            public int getVertexRadius() {
                return 4;
            }
        };
        EventListener l=new EventListener();
        EditBlackboard map = new EditBlackboard(SCREEN.x,SCREEN.y, transform, layerToWorld);
        map.getListeners().add(l);
        
        PrimitiveShape shell = map.getGeoms().get(0).getShell();
        
        map.addPoint(10,10, shell);
        map.addPoint(9,9, shell);
        map.addPoint(9,10, shell);
        map.addPoint(9,11, shell);
        map.addPoint(10,9, shell);
        map.addPoint(11,10, shell);
        map.addPoint(11,9, shell);
        map.addPoint(11,10, shell);
        map.addPoint(11,11, shell);
        
        assertEquals(9, map.getCoords(10,10).size());
        assertEquals(0, map.getCoords(9,9).size());
        assertEquals(0, map.getCoords(9,10).size());
        assertEquals(0, map.getCoords(9,11).size());
        assertEquals(0, map.getCoords(10,9).size());
        assertEquals(0, map.getCoords(10,11).size());
        assertEquals(0, map.getCoords(11,9).size());
        assertEquals(0, map.getCoords(11,10).size());
        assertEquals(0, map.getCoords(11,11).size());
        new PreferenceUtil(){
            {
                instance=this;
            }
            @Override
            public int getVertexRadius() {
                return 0;
            }
        };     
    }
    
    @Test
    public void testLakeDrawingCase() throws Exception {
        URL url = TestsPlugin.getDefault().getBundle().getResource("data/lake.gml"); //$NON-NLS-1$
        InputStream in = url.openConnection().getInputStream();
        
        InputStreamReader filereader=new InputStreamReader(in);
        
        InputSource input = new InputSource(filereader);
        DefaultFeatureCollection collection = new DefaultFeatureCollection();
        GMLReceiver receiver=new GMLReceiver(collection);
        GMLFilterFeature filterFeature = new GMLFilterFeature(receiver);
        GMLFilterGeometry filterGeometry = new GMLFilterGeometry(filterFeature);
        GMLFilterDocument filterDocument = new GMLFilterDocument(filterGeometry);
        try {
            // parse xml
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(filterDocument);
            reader.parse(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SimpleFeature feature=collection.features().next();
        ReferencedEnvelope bounds = new ReferencedEnvelope(feature.getBounds());
        bounds=new ReferencedEnvelope( bounds.getMinX()-(bounds.getWidth()/8),
                bounds.getMaxX()+(bounds.getWidth()/8),
                bounds.getMinY()-(bounds.getHeight()/4),
                bounds.getMaxY()+(bounds.getHeight()/4), DefaultGeographicCRS.WGS84 );
        EditBlackboard map=new EditBlackboard(SCREEN.x, SCREEN.y, ScaleUtils.worldToScreenTransform(bounds, new Dimension(100,100)), layerToWorld);
        
        map.setGeometries((Geometry) feature.getDefaultGeometry(), null);
        
        Polygon poly=(Polygon) ((MultiPolygon) feature.getDefaultGeometry()).getGeometryN(0);
        
        PrimitiveShape shell = map.getGeoms().get(0).getShell();
        assertEquals(poly.getExteriorRing().getCoordinates().length, shell.getNumCoords());
        for (int i=0; i<shell.getNumCoords(); i++){
            assertEquals("i="+i, poly.getExteriorRing().getCoordinateN(i), shell.getCoord(i)); //$NON-NLS-1$
        }
        assertEquals(shell.getCoord(0), shell.getCoord(shell.getNumCoords()-1));
        assertEquals(shell.getPoint(0), shell.getPoint(shell.getNumPoints()-1));
        
        List<PrimitiveShape> holes = map.getGeoms().get(0).getHoles();
        
        for( int j=0; j<holes.size(); j++ ) {
            PrimitiveShape hole = holes.get(j);
            for (int i=0; i<hole.getNumCoords(); i++){
                assertEquals("hole="+j+"i="+i, poly.getInteriorRingN(j).getCoordinateN(i), hole.getCoord(i)); //$NON-NLS-1$ //$NON-NLS-2$
            }            
            assertEquals(hole.getCoord(0), hole.getCoord(hole.getNumCoords()-1));
            assertEquals(hole.getPoint(0), hole.getPoint(hole.getNumPoints()-1));
        }
    }
    
    @Test
    public void testSetTransform() throws Exception {
        GeometryFactory factory=new GeometryFactory();
        Polygon poly = createPolygon(factory, 10);
        EditBlackboard map = new EditBlackboard(SCREEN.x, SCREEN.y, transform, layerToWorld);
        Map<Geometry, EditGeom> mapping = map.setGeometries(poly, null);
        EventListener l=new EventListener();
        map.getListeners().add(l);
        
        // test equal transform
        map.setToScreenTransform(new AffineTransform(transform) );
        assertTranslation(map, mapping.get(poly), 0,0);
        assertNull( l.event );
        assertEquals(Point.valueOf(30,25), map.toPoint(new Coordinate(20.5,20.5)));
        assertEquals(new Coordinate(20.5,20.5), map.toCoord(Point.valueOf(30,25)));
        
        
        // test translated transform
        map.setToScreenTransform(AffineTransform.getTranslateInstance(0,0));
        assertTranslation(map, mapping.get(poly),-10, -5);
        EditBlackboardEvent event=findEvent(EventType.TRANFORMATION,l);
        assertNotNull(event);
        assertEquals( new AffineTransform(transform), event.getOldValue());
        assertEquals(Point.valueOf(20, 20), map.toPoint(new Coordinate(20.5,20.5)));
        assertEquals(new Coordinate(20.5,20.5), map.toCoord(Point.valueOf(20,20)));
        
        l.allEvents.clear();
        
        map = new EditBlackboard(SCREEN.x, SCREEN.y, AffineTransform.getScaleInstance(.5,.5), layerToWorld);
        map.getListeners().add(l);
        mapping = map.setGeometries(poly, null);
        
        map.setCollapseVertices(false);
        
        map.setToScreenTransform(AffineTransform.getTranslateInstance(10,5));
        assertTranslation(map, mapping.get(poly), 0,0 );
        
        event=findEvent(EventType.TRANFORMATION,l);
        assertNotNull(event);
        assertEquals( AffineTransform.getScaleInstance(.5,.5), event.getOldValue());
        assertEquals(Point.valueOf(30,25), map.toPoint(new Coordinate(20.5,20.5)));
        assertEquals(new Coordinate(20.5,20.5), map.toCoord(Point.valueOf(30,25)));
    }

    EditBlackboardEvent findEvent( EventType type, EventListener l ){
        for( EditBlackboardEvent event : l.allEvents ) {
            if( event.getType()==type )
                return event;
        }
        return null;
    }
    
    private void assertTranslation( EditBlackboard map, EditGeom geom, int diffX, int diffY ) {
        // check coords moved 
        assertEquals(map.getCoords(20+diffX,10+diffY).get(0), geom.getShell().getCoord(0) );
        assertEquals(map.getCoords(30+diffX,10+diffY).get(0), geom.getShell().getCoord(1) );
        assertEquals(map.getCoords(30+diffX,15+diffY).get(0), geom.getShell().getCoord(2) );
        assertEquals(map.getCoords(20+diffX,15+diffY).get(0), geom.getShell().getCoord(3) );
        assertEquals(map.getCoords(20+diffX,10+diffY).get(0), geom.getShell().getCoord(4) );
        // check hole
        assertEquals(map.getCoords(25+diffX,12+diffY).get(0), geom.getHoles().get(0).getCoord(0) );
        assertEquals(map.getCoords(28+diffX,12+diffY).get(0), geom.getHoles().get(0).getCoord(1) );
        assertEquals(map.getCoords(28+diffX,13+diffY).get(0), geom.getHoles().get(0).getCoord(2) );
        assertEquals(map.getCoords(25+diffX,13+diffY).get(0), geom.getHoles().get(0).getCoord(3) );
        assertEquals(map.getCoords(25+diffX,12+diffY).get(0), geom.getHoles().get(0).getCoord(4) );

        // check geoms moved 
        assertSame(map.getGeoms(20+diffX,10+diffY).get(0), geom);
        assertSame(map.getGeoms(30+diffX,10+diffY).get(0), geom );
        assertSame(map.getGeoms(30+diffX,15+diffY).get(0), geom );
        assertSame(map.getGeoms(20+diffX,15+diffY).get(0), geom );
        assertSame(map.getGeoms(20+diffX,10+diffY).get(0), geom );
        // check hole
        assertEquals(map.getGeoms(25+diffX,12+diffY).get(0), geom );
        assertEquals(map.getGeoms(28+diffX,12+diffY).get(0), geom );
        assertEquals(map.getGeoms(28+diffX,13+diffY).get(0), geom );
        assertEquals(map.getGeoms(25+diffX,13+diffY).get(0), geom );
        assertEquals(map.getGeoms(25+diffX,12+diffY).get(0), geom );
        
        // check points in EditGeoms
        assertEquals(Point.valueOf(20+diffX,10+diffY), geom.getShell().getPoint(0));
        assertEquals(Point.valueOf(30+diffX,10+diffY), geom.getShell().getPoint(1));
        assertEquals(Point.valueOf(30+diffX,15+diffY), geom.getShell().getPoint(2));
        assertEquals(Point.valueOf(20+diffX,15+diffY), geom.getShell().getPoint(3));
        assertEquals(Point.valueOf(20+diffX,10+diffY), geom.getShell().getPoint(4));
        // check hole
        assertEquals(Point.valueOf(25+diffX,12+diffY), geom.getHoles().get(0).getPoint(0));
        assertEquals(Point.valueOf(28+diffX,12+diffY), geom.getHoles().get(0).getPoint(1));
        assertEquals(Point.valueOf(28+diffX,13+diffY), geom.getHoles().get(0).getPoint(2));
        assertEquals(Point.valueOf(25+diffX,13+diffY), geom.getHoles().get(0).getPoint(3));
        assertEquals(Point.valueOf(25+diffX,12+diffY), geom.getHoles().get(0).getPoint(4));
    }
    
    @Test
    public void testRemovePoint() throws Exception {
        
    }
    
    @Test
    public void testRemoveGeoms() throws Exception {
        EventListener l=new EventListener();
        EditBlackboard map = new EditBlackboard(SCREEN.x,SCREEN.y, new AffineTransform(), layerToWorld);
        map.getListeners().add(l);

        GeometryFactory factory=new GeometryFactory();
        map.getListeners().add(l);
        map.setGeometries(factory.createPoint(new Coordinate(10,10)), null);
        map.addGeometry(factory.createPoint(new Coordinate(20,10)), null);
        Coordinate coordinate = new Coordinate(30,10);
        map.addGeometry(factory.createPoint(coordinate), null);
        Coordinate coordinate2 = new Coordinate(40,10);
        map.addGeometry(factory.createPoint(coordinate2), null);
        
        List<EditGeom> toRemove=new ArrayList<EditGeom>(map.getGeoms());
        toRemove.remove(0);
        toRemove.remove(0);
        

        assertTrue(map.getCoords(30,10).contains(coordinate));
        assertTrue(map.getCoords(40,10).contains(coordinate2));
        assertEquals(1, map.getGeoms(30,10).size());
        assertEquals(1, map.getGeoms(40,10).size());
        List<EditGeom> removed = map.removeGeometries(toRemove);
        assertTrue(toRemove.containsAll(removed));
        
        // this is specific to this case.  I know that all geometries in toRemove were in the blackboard
        // this test is not true for the more general case
        assertTrue(removed.containsAll(toRemove));
        assertTrue(((List) l.getEditBlackboardEvent().getOldValue()).contains(toRemove.get(0)));
        assertTrue(((List) l.getEditBlackboardEvent().getOldValue()).contains(toRemove.get(1)));
        assertEquals(2, ((List) l.getEditBlackboardEvent().getOldValue()).size());
        assertFalse(map.getCoords(30,10).contains(coordinate));
        assertFalse(map.getCoords(40,10).contains(coordinate2));
        assertEquals(0, map.getGeoms(30,10).size());
        assertEquals(0, map.getGeoms(40,10).size());
    }
    
    // Test the move method that declares which coords to move.
    @Test
    public void testMoveVertexSrcDestToMove() throws Exception {
        EventListener l=new EventListener();
        EditBlackboard map = new EditBlackboard(SCREEN.x,SCREEN.y, new AffineTransform(), layerToWorld);
        EditGeom geom = map.getGeoms().get(0);
        map.addPoint(10,10, geom.getShell());
        map.addPoint(10,10, geom.getShell());
        map.selectionAdd(Point.valueOf(10,10));
        map.addPoint(10,10, geom.getShell());
        
        map.getListeners().add(l);
        map.moveSelection(0, 5, map.getSelection());

        Iterator<Point> iter = map.getSelection().iterator();
        assertEquals(Point.valueOf(10,15), iter.next());
        assertFalse( iter.hasNext() );
        assertEquals(1, map.getCoords(10,10).size());
        assertEquals(2, map.getCoords(10,15).size());
        assertEquals(Point.valueOf(10,15), geom.getShell().getPoint(0));
        assertEquals(Point.valueOf(10,10), geom.getShell().getPoint(1));

        map.clear();
        
        // test moving coord when it is the only coord.
        geom=map.getGeoms().get(0);
        map.addPoint(0,0,geom.getShell());
        map.selectionAdd(Point.valueOf(0,0));
        
        map.moveSelection(0,5, map.getSelection());
        assertEquals(0, map.getCoords(0,0).size());
        assertEquals(1, map.getCoords(0,5).size());
        assertEquals(1, geom.getShell().getNumPoints());
        assertEquals(1, geom.getShell().getNumCoords());
        assertEquals(Point.valueOf(0,5), geom.getShell().getPoint(0));

        map.moveSelection(0,-5, map.getSelection());
        assertEquals(1, map.getCoords(0,0).size());
        assertEquals(0, map.getCoords(0,5).size());
        assertEquals(1, geom.getShell().getNumPoints());
        assertEquals(1, geom.getShell().getNumCoords());
        assertEquals(Point.valueOf(0,0), geom.getShell().getPoint(0));
        
        map.clear();
        
        // test moving coord that is in middle of a shape rather than at the beginning
        geom=map.getGeoms().get(0);
        map.addPoint(0,0,geom.getShell());
        map.addPoint(10,0,geom.getShell());
        map.addPoint(10,10,geom.getShell());
        map.selectionAdd(Point.valueOf(10,10));
        map.addPoint(10,10,geom.getShell());
        map.addPoint(0,10,geom.getShell());
        map.addPoint(0,0,geom.getShell());

        map.moveSelection(0,5, map.getSelection());
        
        assertEquals(1, map.getCoords(10,10).size());
        assertEquals(1, map.getCoords(10,15).size());
        assertEquals(6, geom.getShell().getNumPoints());
        assertEquals(Point.valueOf(10,15), geom.getShell().getPoint(2));
        assertEquals(Point.valueOf(10,10), geom.getShell().getPoint(3));

        map.moveSelection(0,-5, map.getSelection());
        assertEquals(5, geom.getShell().getNumPoints());
        assertEquals(2, map.getCoords(10,10).size());
        assertEquals(0, map.getCoords(10,15).size());
        assertEquals(Point.valueOf(10,10), geom.getShell().getPoint(2));
        assertEquals(Point.valueOf(0,10), geom.getShell().getPoint(3));
        
        map.selectionClear();
        map.selectionAdd(Point.valueOf(10,10));
        
        map.moveSelection(-10, 0, map.getSelection());
        assertEquals(3, map.getCoords(0,10).size());
        assertEquals(0, map.getCoords(10,10).size());
        assertEquals(4, geom.getShell().getNumPoints());
        assertEquals(Point.valueOf(0,10), geom.getShell().getPoint(2));
        assertEquals(Point.valueOf(0,0), geom.getShell().getPoint(3));

        map.moveSelection(5, 0, map.getSelection());
        assertEquals(1, map.getCoords(0,10).size());
        assertEquals(2, map.getCoords(5,10).size());
        assertEquals(5, geom.getShell().getNumPoints());
        assertEquals(Point.valueOf(5,10), geom.getShell().getPoint(2));
        assertEquals(Point.valueOf(0,10), geom.getShell().getPoint(3));
        
                
        // test the case where the layer is not in the same projection as the map.
        MathTransform albersToWGS84 = CRS.findMathTransform(CRS.decode("EPSG:3005"), DefaultGeographicCRS.WGS84, true); //$NON-NLS-1$
        map = new EditBlackboard(SCREEN.x,SCREEN.y, AffineTransform.getTranslateInstance(0,0), albersToWGS84);
        
        map.addPoint(0,0, map.getGeoms().get(0).getShell());
        Coordinate c = new Coordinate();
        JTS.transform(map.getCoords(0,0).get(0), c, albersToWGS84);
        
        assertEquals(0, (int)c.x);
        assertEquals(0, (int)c.y);
        
        map.selectionAdd(Point.valueOf(0,0));
        map.moveSelection(10,5, map.getSelection() );
        
        JTS.transform(map.getCoords(10,5).get(0), c, albersToWGS84);
        
        assertEquals(10, (int)c.x);
        assertEquals(5, (int)c.y);
    }
    
    @Test
    public void testSetEditGeom() throws Exception {
        EditBlackboard bb=new EditBlackboard(SCREEN.x, SCREEN.y, transform, layerToWorld);
        EventListener l=new EventListener();
        bb.getListeners().add(l);
        
        bb.getGeoms().get(0).setChanged(true);
        assertEquals(1, bb.getGeoms().size());
        EditGeom geom=bb.newGeom(null, ShapeType.LINE);
        assertEquals(2, bb.getGeoms().size());
        assertTrue( bb.getGeoms().contains(geom) );
        assertEquals(EventType.ADD_GEOMS, l.event.getType());
        assertEquals(geom, ((List) l.getEditBlackboardEvent().getNewValue()).get(0) );
    }
    
    /*
     * Test method for 'org.locationtech.udig.tools.edit.support.EditUtils.overVertex(Point, PixelCoordMap, int)'
     */
    @Test
    public void testOverVertex() {
        EditBlackboard map=new EditBlackboard(SCREEN.x, SCREEN.y, AffineTransform.getTranslateInstance(0,0),
                layerToWorld);
        
        map.addPoint(10,10, map.getGeoms().get(0).getShell());
        
        Point valueOf = Point.valueOf(10,10);
        
        int snapSize=6;
        for( int x=-snapSize; x<snapSize;x++)
            for( int y=-snapSize; y<snapSize;y++)
                assertEquals("x="+(10-x)+", y="+(10-y),valueOf, map.overVertex(Point.valueOf(10-x,10-y), snapSize)); //$NON-NLS-1$  //$NON-NLS-2$
                
        assertNull(map.overVertex(Point.valueOf(12,11), 1));
        assertNull(map.overVertex(Point.valueOf(10,12), 1));
        assertNull(map.overVertex(Point.valueOf(9,12), 1));
        assertNull(map.overVertex(Point.valueOf(12,9), 1));
        assertNull(map.overVertex(Point.valueOf(12,10), 1));
        assertNull(map.overVertex(Point.valueOf(12,11), 1));
    }

    @Test
    public void testNewGeom() throws Exception {
        TestEditBlackboard bb=new TestEditBlackboard();
        bb.getGeoms().get(0).setChanged(false);
        bb.newGeom("testing", null); //$NON-NLS-1$
        
        assertEquals(1, bb.getGeoms().size());
        assertEquals("testing", bb.getGeoms().get(0).getFeatureIDRef().get()); //$NON-NLS-1$

        bb.clear();
        bb.getGeoms().get(0).setChanged(true);
        bb.newGeom("test2", null); //$NON-NLS-1$
        
        assertEquals(2, bb.getGeoms().size());
        assertEquals("test2", bb.getGeoms().get(1).getFeatureIDRef().get()); //$NON-NLS-1$
        
    }
    
    @Test
    public void testMoveGeom() throws Exception {
        TestEditBlackboard bb=new TestEditBlackboard();
        EditGeom geom = bb.getGeoms().get(0);
        PrimitiveShape shell = geom.getShell();
        bb.addPoint(10,10, shell);
        bb.addPoint(11,10, shell);
        bb.addPoint(12,10, shell);
        bb.addPoint(13,10, shell);
        bb.addPoint(14,10, shell);
        
        bb.moveSelection(1,0,geom.createSelection());
        
        assertEquals(Point.valueOf(11,10), shell.getPoint(0));
        assertEquals(Point.valueOf(12,10), shell.getPoint(1));
        assertEquals(Point.valueOf(13,10), shell.getPoint(2));
        assertEquals(Point.valueOf(14,10), shell.getPoint(3));
        assertEquals(Point.valueOf(15,10), shell.getPoint(4));
    }
    
    class EventListener implements EditBlackboardListener{
        EditBlackboardEvent event;
        List<EditBlackboardEvent> batched;
        // only the events that are raised by changed()
        List<EditBlackboardEvent> allEvents=new ArrayList<EditBlackboardEvent>();
        
        EditBlackboardEvent getEditBlackboardEvent(){
            EditBlackboardEvent result = event;
            return result;
        }

        public void changed( EditBlackboardEvent e ) {
            event=e;
            allEvents.add(e);
        }
        
        public void batchChange( List<EditBlackboardEvent> e ) {
            batched=e;
        }
    }

    
    
}
