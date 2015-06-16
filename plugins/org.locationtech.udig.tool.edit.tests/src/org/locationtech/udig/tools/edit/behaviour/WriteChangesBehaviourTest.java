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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.EditManager;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.tests.support.TestLayer;
import org.locationtech.udig.tools.edit.behaviour.accept.AcceptChangesBehaviour;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;
import org.locationtech.udig.tools.edit.support.ShapeType;
import org.locationtech.udig.tools.edit.support.TestHandler;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.Not;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

@Ignore
public class WriteChangesBehaviourTest {

    private TestHandler handler;
    private Layer layer;
    private SimpleFeature feature;
    private SimpleFeature feature2;

    @Before
    public void setUp() throws Exception {
        handler=new TestHandler(2);
        layer = (Layer) handler.getContext().getMap().getMapLayers().get(0);
        FeatureIterator<SimpleFeature> features = layer.getResource(FeatureSource.class, null).getFeatures().features();
        feature = features.next();
        feature2 = features.next();
        ((Map)handler.getContext().getMap()).getEditManagerInternal().setEditFeature(feature, layer);    
    }
    
    /*
     * Test method for 'org.locationtech.udig.tools.edit.behaviour.SetEditFeatureBehaviour.isValid(EditToolHandler)'
     */
    @Test
    public void testIsValid() throws Exception {
        AcceptChangesBehaviour behaviour=new AcceptChangesBehaviour(Polygon.class, false);
        
        // currentGeom is null
        assertFalse(behaviour.isValid(handler));
        EditGeom editGeom = handler.getEditBlackboard().getGeoms().get(0);
        handler.setCurrentShape(editGeom.getShell());
        
        
        // current Geom has no points
        handler.getEditBlackboard().addPoint(10,10,editGeom.getShell());
        assertTrue(behaviour.isValid(handler));       
    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.behaviour.SetEditFeatureBehaviour.run(EditToolHandler)'
     */
    @Test
    public void testPolygon() throws Exception {
        AcceptChangesBehaviour behaviour;
        FeatureIterator<SimpleFeature> features;
        SimpleFeature next;
        EditBlackboard bb = handler.getEditBlackboard();
        EditGeom editGeom = bb.getGeoms().get(0);

        handler.setCurrentShape(editGeom.getShell());
        
           // test create Polygon
        bb.addPoint(10,10,editGeom.getShell());
        bb.addPoint(40,10,editGeom.getShell());
        bb.addPoint(40,40,editGeom.getShell());
        bb.addPoint(10,10,editGeom.getShell());
        
        PrimitiveShape hole = editGeom.newHole();
        bb.addPoint(20,20,hole);
        bb.addPoint(30,30,hole);
        bb.addPoint(30,20,hole);
        bb.addPoint(20,20,hole);
        
        behaviour=new AcceptChangesBehaviour(Polygon.class, false);        
        UndoableMapCommand command = behaviour.getCommand(handler);
        command.setMap(handler.getContext().getMap());
        command.run(new NullProgressMonitor());

        assertFalse(handler.getCurrentGeom().isChanged());
        LineString shell = ((Polygon)feature.getDefaultGeometry()).getExteriorRing();
        assertEquals(4, shell.getCoordinates().length);
        assertEquals(toCoord(bb,10,10), shell.getCoordinates()[0]);
        assertEquals(toCoord(bb,40,10), shell.getCoordinates()[1]);
        assertEquals(toCoord(bb,40,40), shell.getCoordinates()[2]);
        assertEquals(toCoord(bb,10,10), shell.getCoordinates()[3]);
        Coordinate[] holeCoords = ((Polygon)feature.getDefaultGeometry()).getInteriorRingN(0).getCoordinates();

        assertEquals(4, holeCoords.length);
        assertEquals(toCoord(bb,20,20), holeCoords[0]);
        assertEquals(toCoord(bb,30,30), holeCoords[1]);
        assertEquals(toCoord(bb,30,20), holeCoords[2]);
        assertEquals(toCoord(bb,20,20), holeCoords[3]);
        
        features = layer.getResource(FeatureSource.class, null).getFeatures().features();
        next=features.next();
        shell = ((Polygon)next.getDefaultGeometry()).getExteriorRing();
        assertEquals(4, shell.getCoordinates().length);
        assertEquals( toCoord(bb,10,10), shell.getCoordinates()[0] );
        assertEquals( toCoord(bb,40,10), shell.getCoordinates()[1] );
        assertEquals(Polygon.class, next.getDefaultGeometry().getClass());
        
        holeCoords = ((Polygon)next.getDefaultGeometry()).getInteriorRingN(0).getCoordinates();
        assertEquals(4, holeCoords.length);
        assertEquals(toCoord(bb,20,20), holeCoords[0]);
        assertEquals(toCoord(bb,30,30), holeCoords[1]);
        assertEquals(toCoord(bb,30,20), holeCoords[2]);
        assertEquals(toCoord(bb,20,20), holeCoords[3]);
        SimpleFeatureType type = DataUtilities.createType("MultiPolygon", "*geom:MultiPolygon"); //$NON-NLS-1$ //$NON-NLS-2$
        ((TestLayer)handler.getContext().getMap().getMapLayers().get(0)).setSchema(type);
        ((Map)handler.getContext().getMap()).getEditManagerInternal().setEditFeature(SimpleFeatureBuilder.template(type, "newFeature"), layer);
        
    }
    
    /*
     * Test method for 'org.locationtech.udig.tools.edit.behaviour.SetEditFeatureBehaviour.run(EditToolHandler)'
     */
    @Test
    public void testMultiPolygon() throws Exception {
        AcceptChangesBehaviour behaviour;
        FeatureIterator<SimpleFeature> features;
        SimpleFeature next;
        EditBlackboard bb = handler.getEditBlackboard();
        EditGeom editGeom = bb.getGeoms().get(0);

        SimpleFeatureType type = DataUtilities.createType("MultiPolygon", "*geom:MultiPolygon"); //$NON-NLS-1$ //$NON-NLS-2$
        feature = SimpleFeatureBuilder.template(type, feature.getID());
        ((TestLayer)handler.getContext().getMap().getMapLayers().get(0)).setSchema(type);
       ((Map)handler.getContext().getMap()).getEditManagerInternal().setEditFeature(feature, layer);

        handler.setCurrentShape(bb.getGeoms().get(0).getShell());
        
           // test create MultiPolygon
        bb.addPoint(10,10,editGeom.getShell());
        bb.addPoint(40,10,editGeom.getShell());
        bb.addPoint(40,40,editGeom.getShell());
        bb.addPoint(10,10,editGeom.getShell());
        
        PrimitiveShape hole = editGeom.newHole();
        bb.addPoint(20,20,hole);
        bb.addPoint(30,20,hole);
        bb.addPoint(30,30,hole);
        bb.addPoint(20,20,hole);
        
        behaviour=new AcceptChangesBehaviour(Polygon.class, false);
        UndoableMapCommand command = behaviour.getCommand(handler);
        command.setMap(handler.getContext().getMap());
        command.run(new NullProgressMonitor());
        assertFalse(handler.getCurrentGeom().isChanged());
        LineString shell = ((Polygon)((MultiPolygon)feature.getDefaultGeometry()).getGeometryN(0)).getExteriorRing();
        assertEquals(4, shell.getCoordinates().length);
        assertEquals(toCoord(bb,10,10), shell.getCoordinates()[0]);
        assertEquals(toCoord(bb,40,10), shell.getCoordinates()[1]);
        assertEquals(toCoord(bb,40,40), shell.getCoordinates()[2]);
        assertEquals(toCoord(bb,10,10), shell.getCoordinates()[3]);
        Coordinate[] holeCoords = ((Polygon)((MultiPolygon)feature.getDefaultGeometry()).getGeometryN(0)).getInteriorRingN(0).getCoordinates();

        assertEquals(4, holeCoords.length);
        assertEquals(toCoord(bb,20,20), holeCoords[0]);
        assertEquals(toCoord(bb,30,30), holeCoords[1]);
        assertEquals(toCoord(bb,30,20), holeCoords[2]);
        assertEquals(toCoord(bb,20,20), holeCoords[3]);
        
        features = layer.getResource(FeatureSource.class, null).getFeatures().features();
        next=features.next();
        shell =((Polygon)((MultiPolygon)next.getDefaultGeometry()).getGeometryN(0)).getExteriorRing();
        assertEquals(4, shell.getCoordinates().length);
        assertEquals( toCoord(bb,10,10), shell.getCoordinates()[0] );
        assertEquals( toCoord(bb,40,10), shell.getCoordinates()[1] );
        assertEquals(MultiPolygon.class, next.getDefaultGeometry().getClass());
        
        holeCoords = ((Polygon)((MultiPolygon)next.getDefaultGeometry()).getGeometryN(0)).getInteriorRingN(0).getCoordinates();
        assertEquals(4, holeCoords.length);
        assertEquals(toCoord(bb,20,20), holeCoords[0]);
        assertEquals(toCoord(bb,30,30), holeCoords[1]);
        assertEquals(toCoord(bb,30,20), holeCoords[2]);
        assertEquals(toCoord(bb,20,20), holeCoords[3]);
        
    }

    @Ignore
    @Test
    public void testLines() throws Exception {
        AcceptChangesBehaviour behaviour;
        FeatureIterator<SimpleFeature> features;
        SimpleFeature next;
        
        EditBlackboard bb = handler.getEditBlackboard();
        EditGeom editGeom = bb.getGeoms().get(0);

        handler.setCurrentShape(editGeom.getShell());

        bb.addPoint(10,10,editGeom.getShell());
        bb.addPoint(40,10,editGeom.getShell());
     
        SimpleFeatureType type = DataUtilities.createType("MultiLine", "*geom:MultiLineString"); //$NON-NLS-1$ //$NON-NLS-2$
        feature = SimpleFeatureBuilder.template(type, feature.getID());
        ((TestLayer)handler.getContext().getMap().getMapLayers().get(0)).setSchema(type);
        ((Map)handler.getContext().getMap()).getEditManagerInternal().setEditFeature(feature, layer);
        
        // test create LineString
        behaviour=new AcceptChangesBehaviour(LineString.class, false);
        UndoableMapCommand command = behaviour.getCommand(handler);
        command.setMap(handler.getContext().getMap());
        command.run(new NullProgressMonitor());
        assertFalse(handler.getCurrentGeom().isChanged());
        assertEquals(2, ((GeometryCollection)feature.getDefaultGeometry()).getGeometryN(0).getCoordinates().length);
        assertEquals(toCoord(bb,10,10), ((GeometryCollection)feature.getDefaultGeometry()).getGeometryN(0).getCoordinates()[0]);
        assertEquals(toCoord(bb,40,10), ((GeometryCollection)feature.getDefaultGeometry()).getGeometryN(0).getCoordinates()[1]);
        features = layer.getResource(FeatureSource.class, null).getFeatures().features();
        next=features.next();
        assertEquals(2, ((GeometryCollection)next.getDefaultGeometry()).getGeometryN(0).getCoordinates().length);
        assertEquals( toCoord(bb,10,10), ((GeometryCollection)next.getDefaultGeometry()).getGeometryN(0).getCoordinates()[0] );
        assertEquals( toCoord(bb,40,10), ((GeometryCollection)next.getDefaultGeometry()).getGeometryN(0).getCoordinates()[1] );
        assertEquals(MultiLineString.class, feature.getDefaultGeometry().getClass()); 

        // test create LinearRing
        behaviour=new AcceptChangesBehaviour(LinearRing.class, false);
        handler.getCurrentGeom().setChanged(true);
        command = behaviour.getCommand(handler);
        command.setMap(handler.getContext().getMap());
        command.run(new NullProgressMonitor());
        assertEquals(4, ((GeometryCollection)feature.getDefaultGeometry()).getGeometryN(0).getCoordinates().length);
        assertEquals(toCoord(bb,10,10), ((GeometryCollection)feature.getDefaultGeometry()).getGeometryN(0).getCoordinates()[0]);
        assertEquals(toCoord(bb,40,10), ((GeometryCollection)feature.getDefaultGeometry()).getGeometryN(0).getCoordinates()[1]);
        assertEquals(toCoord(bb,10,10), ((GeometryCollection)feature.getDefaultGeometry()).getGeometryN(0).getCoordinates()[2]);
        assertEquals(toCoord(bb,10,10), ((GeometryCollection)feature.getDefaultGeometry()).getGeometryN(0).getCoordinates()[3]);
        features = layer.getResource(FeatureSource.class, null).getFeatures().features();
        next=features.next();
        assertEquals(4, ((GeometryCollection)next.getDefaultGeometry()).getGeometryN(0).getCoordinates().length);
        assertEquals( toCoord(bb,10,10), ((GeometryCollection)next.getDefaultGeometry()).getGeometryN(0).getCoordinates()[0] );
        assertEquals( toCoord(bb,40,10), ((GeometryCollection)next.getDefaultGeometry()).getGeometryN(0).getCoordinates()[1] );
        assertEquals(MultiLineString.class, feature.getDefaultGeometry().getClass());
    }
    
    @Ignore
    @Test
    public void testMultiLine() throws Exception {
        AcceptChangesBehaviour behaviour;
        FeatureIterator<SimpleFeature> features;
        SimpleFeature next;
        
        EditBlackboard bb = handler.getEditBlackboard();
        EditGeom editGeom = bb.getGeoms().get(0);

        handler.setCurrentShape(editGeom.getShell());

        bb.addPoint(10,10,editGeom.getShell());
        bb.addPoint(40,10,editGeom.getShell());
        
        // test create LineString
        behaviour=new AcceptChangesBehaviour(LineString.class, false);
        UndoableMapCommand command = behaviour.getCommand(handler);
        command.setMap(handler.getContext().getMap());
        command.run(new NullProgressMonitor());
        assertFalse(handler.getCurrentGeom().isChanged());
        assertEquals(toCoord(bb,10,10), ((Geometry) feature.getDefaultGeometry()).getCoordinates()[0]);
        assertEquals(toCoord(bb,40,10), ((Geometry) feature.getDefaultGeometry()).getCoordinates()[1]);
        assertEquals(2, ((Geometry) feature.getDefaultGeometry()).getCoordinates().length);
        features = layer.getResource(FeatureSource.class, null).getFeatures().features();
        next=features.next();
        assertEquals( toCoord(bb,10,10), ((Geometry) next.getDefaultGeometry()).getCoordinates()[0] );
        assertEquals( toCoord(bb,40,10), ((Geometry) next.getDefaultGeometry()).getCoordinates()[1] );
        assertEquals(LineString.class, feature.getDefaultGeometry().getClass());
        

        // test create LinearRing
        behaviour=new AcceptChangesBehaviour(LinearRing.class, false);
        handler.getCurrentGeom().setChanged(true);
        command = behaviour.getCommand(handler);
        command.setMap(handler.getContext().getMap());
        command.run(new NullProgressMonitor());
        assertEquals(4, ((Geometry) feature.getDefaultGeometry()).getCoordinates().length);
        assertEquals(toCoord(bb,10,10), ((Geometry) feature.getDefaultGeometry()).getCoordinates()[0]);
        assertEquals(toCoord(bb,40,10), ((Geometry) feature.getDefaultGeometry()).getCoordinates()[1]);
        assertEquals(toCoord(bb,10,10), ((Geometry) feature.getDefaultGeometry()).getCoordinates()[2]);
        assertEquals(toCoord(bb,10,10), ((Geometry) feature.getDefaultGeometry()).getCoordinates()[3]);
        features = layer.getResource(FeatureSource.class, null).getFeatures().features();
        next=features.next();
        assertEquals(4, ((Geometry) next.getDefaultGeometry()).getCoordinates().length);
        assertEquals( toCoord(bb,10,10), ((Geometry) next.getDefaultGeometry()).getCoordinates()[0] );
        assertEquals( toCoord(bb,40,10), ((Geometry) next.getDefaultGeometry()).getCoordinates()[1] );
        assertEquals(LinearRing.class, feature.getDefaultGeometry().getClass());
    }


    private Coordinate toCoord( EditBlackboard bb, int i, int j ) {
        return bb.toCoord(org.locationtech.udig.tools.edit.support.Point.valueOf(i,j));
    }

    @Test
    public void testPoint() throws Exception {
        AcceptChangesBehaviour behaviour=new AcceptChangesBehaviour(Point.class, false);

        EditBlackboard bb = handler.getEditBlackboard();
        EditGeom editGeom = bb.getGeoms().get(0);

        bb.addPoint(10,10,editGeom.getShell());
        
        try{
            behaviour.getCommand(handler);
            fail();
        }catch (IllegalArgumentException e) {
            // good
        }
        
        handler.setCurrentShape(editGeom.getShell());
        
        // test create point
        UndoableMapCommand command = behaviour.getCommand(handler);
        command.setMap(handler.getContext().getMap());
        command.run(new NullProgressMonitor());
        
        assertEquals(toCoord(bb,10,10), ((Geometry) feature.getDefaultGeometry()).getCoordinates()[0]);
        FeatureIterator<SimpleFeature> features = layer.getResource(FeatureSource.class, null).getFeatures().features();
        SimpleFeature next = features.next();
        assertEquals( toCoord(bb,10,10), ((Geometry) next.getDefaultGeometry()).getCoordinates()[0] );
        assertEquals(Point.class, feature.getDefaultGeometry().getClass());
        
        bb.addPoint(40,10,bb.newGeom(null, null).getShell());
        handler.getCurrentGeom().setChanged(true);

        SimpleFeatureType type = DataUtilities.createType("MultiPoint", "*geom:MultiPoint"); //$NON-NLS-1$ //$NON-NLS-2$
        feature = SimpleFeatureBuilder.template(type, feature.getID());
        ((TestLayer)handler.getContext().getMap().getMapLayers().get(0)).setSchema(type);
        ((Map)handler.getContext().getMap()).getEditManagerInternal().setEditFeature(feature, layer);
        

        // test create Multi Point
        command = behaviour.getCommand(handler);
        command.setMap(handler.getContext().getMap());
        command.run(new NullProgressMonitor());
        assertFalse(handler.getCurrentGeom().isChanged());
        assertEquals(toCoord(bb,10,10), ((Geometry) feature.getDefaultGeometry()).getCoordinates()[0]);
        assertEquals(toCoord(bb,40,10), ((Geometry) feature.getDefaultGeometry()).getCoordinates()[1]);
        assertEquals(2, ((Geometry) feature.getDefaultGeometry()).getCoordinates().length);
        features = layer.getResource(FeatureSource.class, null).getFeatures().features();
        next=features.next();
        assertEquals( toCoord(bb,10,10), ((Geometry) next.getDefaultGeometry()).getCoordinates()[0] );
        assertEquals( toCoord(bb,40,10), ((Geometry) next.getDefaultGeometry()).getCoordinates()[1] );
        assertEquals(MultiPoint.class, feature.getDefaultGeometry().getClass());
    }
   
    @Test
    public void testNoEditFeature() throws Exception {
        ((EditManager) handler.getContext().getMap().getEditManager()).setEditFeature(null, null);
        AcceptChangesBehaviour behaviour=new AcceptChangesBehaviour(Point.class, false);

        NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
        FeatureSource<SimpleFeatureType, SimpleFeature> source = layer.getResource(FeatureSource.class, nullProgressMonitor);
        int count=source.getCount(Query.ALL);
        
        EditBlackboard bb = handler.getEditBlackboard();
        EditGeom editGeom = bb.getGeoms().get(0);

        bb.addPoint(10,10,editGeom.getShell());

        handler.setCurrentShape(editGeom.getShell());
       UndoableMapCommand command = behaviour.getCommand(handler);
       command.setMap((Map) handler.getContext().getMap());
       nullProgressMonitor = new NullProgressMonitor();
       command.run(nullProgressMonitor);
       assertFalse(handler.getCurrentGeom().isChanged());
       assertNotNull(handler.getCurrentGeom().getFeatureIDRef().get());
       
        
        assertEquals( count+1, source.getCount(Query.ALL) );
        assertNotNull(handler.getContext().getEditManager().getEditFeature());
        assertEquals(toCoord(bb,10,10), ((Geometry) handler.getContext().getEditManager().getEditFeature().getDefaultGeometry()).getCoordinates()[0]);
    }
    
    /**
     * Test the case where 2 geoms on the blackboard have the same FeatureID as the
     * edit geom.  A multigeom must be made and set on the feature. 
     *
     * @throws Exception
     */
    @Ignore
    @Test
    public void testMultiPointOnBlackboard() throws Exception {
        EditBlackboard bb = handler.getEditBlackboard();
        EditGeom geom = bb.newGeom(feature.getID(), ShapeType.POINT);
        bb.addPoint(10,10,geom.getShell());
        handler.setCurrentShape(geom.getShell());
        
        EditGeom geom2 = bb.newGeom(feature.getID(), ShapeType.POINT);
        bb.addPoint(20,20, geom2.getShell());
        
        AcceptChangesBehaviour behaviour = new AcceptChangesBehaviour(Point.class, false);
        UndoableMapCommand command = behaviour.getCommand(handler);
        command.setMap((Map) handler.getContext().getMap());
        command.run(new NullProgressMonitor());
        assertEquals( "Should have 2 points", 2, ((Geometry) feature.getDefaultGeometry()).getCoordinates().length); //$NON-NLS-1$
        assertEquals( "Should have 2 geoms", 2, ((Geometry) feature.getDefaultGeometry()).getNumGeometries()); //$NON-NLS-1$
    }

    /**
     * Case where 2 features have been imported into the blackboard and
     * both have been changed.  When Write is called both features should have
     * their geometries set.
     *
     * @throws Exception
     */
    @Test
    public void testTwoChangedFeaturesOnBlackboard() throws Exception {
        EditBlackboard bb = handler.getEditBlackboard();
        EditGeom geom1 = bb.newGeom(feature.getID(), null);
        bb.addPoint(10,10, geom1.getShell());
        bb.addPoint(20,10, geom1.getShell());
        bb.addPoint(20,20, geom1.getShell());
        bb.addPoint(10,10, geom1.getShell());
        handler.setCurrentShape(geom1.getShell());
        
        EditGeom geom2 = bb.newGeom(feature2.getID(), null);
        
        bb.addPoint(100,100, geom2.getShell());
        bb.addPoint(200,100, geom2.getShell());
        bb.addPoint(200,200, geom2.getShell());
        bb.addPoint(100,100, geom2.getShell());
        
        AcceptChangesBehaviour behaviour=new AcceptChangesBehaviour(LineString.class, false);
        UndoableMapCommand command = behaviour.getCommand(handler);
        command.setMap((Map) handler.getContext().getMap());
        command.run(new NullProgressMonitor());
        
        layer.getMapInternal().getEditManagerInternal().commitTransaction();
        
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        FeatureSource<SimpleFeatureType, SimpleFeature> resource = layer.getResource(FeatureSource.class, new NullProgressMonitor());
		Id id = filterFactory.id(FeatureUtils.stringToId(filterFactory, feature.getID()));
		feature=resource.getFeatures(id).features().next();
		Id id2 = filterFactory.id(FeatureUtils.stringToId(filterFactory, feature2.getID()));
        feature2=resource.getFeatures(id2).features().next();
        
        assertTrue(feature.getDefaultGeometry() instanceof LineString);
        assertTrue(feature2.getDefaultGeometry() instanceof Polygon);

        assertEquals(toCoord(bb,10,10), ((Geometry)feature.getDefaultGeometry()).getCoordinates()[0]);
        assertEquals(toCoord(bb,20,10), ((Geometry)feature.getDefaultGeometry()).getCoordinates()[1]);
        assertEquals(toCoord(bb,20,20), ((Geometry)feature.getDefaultGeometry()).getCoordinates()[2]);
        assertEquals(toCoord(bb,10,10), ((Geometry)feature.getDefaultGeometry()).getCoordinates()[3]);
        
        assertEquals(toCoord(bb,100,100), ((Geometry)feature2.getDefaultGeometry()).getCoordinates()[0]);
        assertEquals(toCoord(bb,200,100), ((Geometry)feature2.getDefaultGeometry()).getCoordinates()[1]);
        assertEquals(toCoord(bb,200,200), ((Geometry)feature2.getDefaultGeometry()).getCoordinates()[2]);
        assertEquals(toCoord(bb,100,100), ((Geometry)feature2.getDefaultGeometry()).getCoordinates()[3]);
    }
    

    /**
     * Case where a features have been created on the blackboard by a non-standard tool.
     * It needs to be created (Split tool needs this for example).  It is not the editfeature
     *
     * @throws Exception
     */
    @Test
    public void testCreateFeature() throws Exception {
        EditBlackboard bb = handler.getEditBlackboard();
        
        EditGeom geom1 = bb.newGeom("newOne", ShapeType.LINE); //$NON-NLS-1$
        bb.addPoint(10,10, geom1.getShell());
        bb.addPoint(20,10, geom1.getShell());
        bb.addPoint(20,20, geom1.getShell());
        bb.addPoint(10,10, geom1.getShell());
        
        
        EditGeom geom2 = bb.newGeom(feature.getID(), null);
        bb.addPoint(100,100, geom2.getShell());
        bb.addPoint(200,100, geom2.getShell());
        bb.addPoint(200,200, geom2.getShell());
        bb.addPoint(100,100, geom2.getShell());
        handler.setCurrentShape(geom2.getShell());
        
        AcceptChangesBehaviour behaviour=new AcceptChangesBehaviour(LineString.class, false);
        UndoableMapCommand command = behaviour.getCommand(handler);
        command.setMap((Map) handler.getContext().getMap());
        command.run(new NullProgressMonitor());
        
        layer.getMapInternal().getEditManagerInternal().commitTransaction();
        
        FilterFactory fac = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        Set<String> fids = new HashSet<String>();
        fids.add(feature.getID());
        fids.add(feature2.getID());
		Id id = fac.id(FeatureUtils.stringToId(fac,fids));
        
		FeatureSource<SimpleFeatureType, SimpleFeature> resource = layer.getResource(FeatureSource.class, new NullProgressMonitor());
		Not not = fac.not(id);
		SimpleFeature newFeature = resource.getFeatures(not).features().next();
        
        assertTrue(newFeature.getDefaultGeometry() instanceof LineString);

        assertEquals(toCoord(bb,10,10), ((Geometry) newFeature.getDefaultGeometry()).getCoordinates()[0]);
        assertEquals(toCoord(bb,20,10), ((Geometry) newFeature.getDefaultGeometry()).getCoordinates()[1]);
        assertEquals(toCoord(bb,20,20), ((Geometry) newFeature.getDefaultGeometry()).getCoordinates()[2]);
        assertEquals(toCoord(bb,10,10), ((Geometry) newFeature.getDefaultGeometry()).getCoordinates()[3]);
    }
    
}
