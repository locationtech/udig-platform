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
package org.locationtech.udig.project.internal.commands.edit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.util.factory.GeoTools;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.tests.CatalogTests;
import org.locationtech.udig.core.internal.FeatureUtils;
import org.locationtech.udig.core.internal.GeometryBuilder;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.tests.support.MapTests;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

public class CopyFeaturesCommandTest {

    GeometryBuilder builder=GeometryBuilder.create();
    private SimpleFeatureType sourceType;
    private SimpleFeatureType targetType;
    private IGeoResource sourceResource;
    private Map sourceMap;
    private IGeoResource targetResource;
    private Map targetMap;
    private SimpleFeature[] sourceFeatures;

    @SuppressWarnings("deprecation")
    @Before
    public void setUp() throws Exception {
        sourceType=DataUtilities.createType("source", "*geom:LineString,geom2:Point,nAme:String"); //$NON-NLS-1$ //$NON-NLS-2$
        sourceFeatures = new SimpleFeature[1];
        GeometryFactory fac=new GeometryFactory();
        Object[] values = new Object[]{builder.safeCreateGeometry(LineString.class, new Coordinate[]{new Coordinate(10,10), new Coordinate(20,10), new Coordinate(20,20)}),
                fac.createPoint(new Coordinate(10,10)), "sourceName"};
		sourceFeatures[0]=SimpleFeatureBuilder.build(sourceType, values,"id2"); //$NON-NLS-1$
        sourceResource=CatalogTests.createGeoResource(sourceFeatures, true);
        sourceMap=MapTests.createNonDynamicMapAndRenderer(sourceResource, new Dimension(100,100));

        targetType=DataUtilities.createType("target", "*targetGeom:Point,name:String"); //$NON-NLS-1$ //$NON-NLS-2$
        SimpleFeature[] targetFeatures = new SimpleFeature[1];
        Object[] values2 = new Object[]{fac.createPoint(new Coordinate(10,10)), "targetName"};
		targetFeatures[0]=SimpleFeatureBuilder.build(targetType, values2, "id3"); //$NON-NLS-1$
        targetResource=CatalogTests.createGeoResource(targetFeatures, true);
        targetMap=MapTests.createNonDynamicMapAndRenderer(targetResource, new Dimension(100,100));

    }

    @After
    public void tearDown() throws Exception {
        FeatureStore<SimpleFeatureType, SimpleFeature> store = sourceResource.resolve(FeatureStore.class, null);
        store.removeFeatures(Filter.INCLUDE);
        
        store = targetResource.resolve(FeatureStore.class, null);
        store.removeFeatures(Filter.INCLUDE);
    }

    /*
     * Test method for 'org.locationtech.udig.project.ui.DropFilterAction.perform(Object, Object, IProgressMonitor)'
     */
    @Ignore
    @Test
    public void testPerform() throws Exception {
        FilterFactory fac=CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        Filter filter=fac.id(FeatureUtils.stringToId(fac, sourceFeatures[0].getID()));
        Layer layer = targetMap.getLayersInternal().get(0);
        assertEquals(Filter.EXCLUDE, layer.getFilter());
        Layer sourceLayer = sourceMap.getLayersInternal().get(0);
        CopyFeaturesCommand action=new CopyFeaturesCommand(sourceLayer, filter, layer);
        action.setMap(targetMap);
        action.run(new NullProgressMonitor());
        
         FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = layer.getResource(FeatureSource.class, new NullProgressMonitor());
        assertEquals( 2, featureSource.getFeatures().size() );
        FeatureCollection<SimpleFeatureType, SimpleFeature>  features=featureSource.getFeatures(layer.getFilter());
        
        assertEquals(1,features.size());
        SimpleFeature addedFeature=features.features().next();
        
        assertTrue(((Geometry)addedFeature.getDefaultGeometry()).equalsExact((Geometry) sourceFeatures[0].getAttribute("geom2"))); //$NON-NLS-1$
        assertEquals(sourceFeatures[0].getAttribute("nAme"), addedFeature.getAttribute("name")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(targetType, addedFeature.getFeatureType());
    }

    @SuppressWarnings("deprecation")
    @Ignore
    @Test
    public void testLine2Polygon() throws Exception {
        setTarget("targetGeom", "Polygon", builder.safeCreateGeometry(Polygon.class, new Coordinate[]{new Coordinate(10,10)}));

        copyFeatures(Polygon.class, 1);
    }
    
    @SuppressWarnings("deprecation")
    @Ignore
    @Test
    public void testLine2Point() throws Exception {
        setTarget("targetGeom", "Point", builder.safeCreateGeometry(Point.class, new Coordinate[]{new Coordinate(10,10)}));
        LineString line = builder.safeCreateGeometry(LineString.class, new Coordinate[]{new Coordinate(10,10), new Coordinate(20,10), new Coordinate(20,20)});
        setSource("LineString", line);
        
        copyFeatures(Point.class, 1);
        ILayer layer = targetMap.getMapLayers().get(0);
        FeatureIterator<SimpleFeature> iter = layer.getResource(FeatureSource.class, null).getFeatures(layer.getFilter()).features();
        SimpleFeature feature=iter.next();
        iter.close();
        assertEquals( line.getCentroid().getCoordinate(), ((Geometry)feature.getDefaultGeometry()).getCoordinate() );
    }
    
    @Ignore
    @Test
    public void testPolygonWithHole2MultiPolygon() throws Exception {
        setTarget("name2","MultiPolygon", builder.safeCreateGeometry(MultiPolygon.class, new Coordinate[]{new Coordinate(10,10)}));

        LinearRing r1 = builder.safeCreateGeometry(LinearRing.class, new Coordinate[]{
            new Coordinate(0,0), new Coordinate(10,0), new Coordinate(10,10), new Coordinate(0,10), new Coordinate(0,0)
        });
        LinearRing r2 = builder.safeCreateGeometry(LinearRing.class, new Coordinate[]{
            new Coordinate(2,2), new Coordinate(8,2), new Coordinate(8,8), new Coordinate(2,2),
        });
        
        GeometryFactory fac=new GeometryFactory();
        Polygon poly = fac.createPolygon(r1, new LinearRing[]{r2});
        setSource("Polygon", poly);
        
        copyFeatures(MultiPolygon.class, 1);
    }
    
    @Ignore
    @Test
    public void testPolygonWithHole2MultiLine() throws Exception {
        setTarget("name2","MultiLineString", builder.safeCreateGeometry(MultiLineString.class, new Coordinate[]{new Coordinate(10,10)}));

        LinearRing r1 = builder.safeCreateGeometry(LinearRing.class, new Coordinate[]{
            new Coordinate(0,0), new Coordinate(10,0), new Coordinate(10,10), new Coordinate(0,10), new Coordinate(0,0)
        });
        LinearRing r2 = builder.safeCreateGeometry(LinearRing.class, new Coordinate[]{
            new Coordinate(2,2), new Coordinate(8,2), new Coordinate(8,8), new Coordinate(2,2),
        });
        
        GeometryFactory fac=new GeometryFactory();
        Polygon poly = fac.createPolygon(r1, new LinearRing[]{r2});
        setSource("Polygon", poly);
        
        copyFeatures(MultiLineString.class, 1);
        
        ILayer layer = targetMap.getMapLayers().get(0);
        FeatureIterator<SimpleFeature> iter = layer.getResource(FeatureSource.class, null).getFeatures(layer.getFilter()).features();
        SimpleFeature feature=iter.next();
        assertEquals(2, ((Geometry)feature.getDefaultGeometry()).getNumGeometries());
        iter.close();
    }

    @SuppressWarnings("deprecation")
    @Ignore
    @Test
    public void testPoint2Polygon() throws Exception {
        setTarget("name2", "Polygon", builder.safeCreateGeometry(Polygon.class, new Coordinate[]{new Coordinate(10,10)}));

        copyFeatures(Polygon.class, 1);
    }

    private void setTarget(String name, String type, Geometry geomValue) throws Exception {
        targetType=DataUtilities.createType("target2", "*"+name+":"+type); //$NON-NLS-1$ //$NON-NLS-2$
        SimpleFeature[] targetFeatures = new SimpleFeature[1];
        Object[] values = new Object[]{geomValue};
		targetFeatures[0]=SimpleFeatureBuilder.build(targetType, values, "id");
        targetResource=CatalogTests.createGeoResource(targetFeatures, true);
        targetMap=MapTests.createNonDynamicMapAndRenderer(targetResource, new Dimension(100,100));
        FeatureStore<SimpleFeatureType, SimpleFeature> store = targetResource.resolve(FeatureStore.class, null);
        store.removeFeatures(Filter.INCLUDE);
    }
    
    @SuppressWarnings("deprecation")
    @Ignore
    @Test
    public void testPoint2Line() throws Exception {

        setTarget("name2", "LineString", builder.safeCreateGeometry(LineString.class, new Coordinate[]{new Coordinate(10,10)}));

        copyFeatures(LineString.class, 1);
    }
    
    @SuppressWarnings("deprecation")
    @Ignore
    @Test
    public void testLine2MultiLine() throws Exception {
        setTarget("target2", "MultiLineString", builder.safeCreateGeometry(MultiLineString.class, new Coordinate[]{new Coordinate(10,10)}));

        copyFeatures(MultiLineString.class, 1);
    }
    
    @SuppressWarnings("deprecation")
    @Ignore
    @Test
    public void testPolygon2LineString() throws Exception {
        setTarget("target2", "LineString", builder.safeCreateGeometry(LineString.class, new Coordinate[]{new Coordinate(10,10)}));
        LinearRing r1 = builder.safeCreateGeometry(LinearRing.class, new Coordinate[]{
            new Coordinate(0,0), new Coordinate(10,0), new Coordinate(10,10), new Coordinate(0,10), new Coordinate(0,0)
        });
        LinearRing r2 = builder.safeCreateGeometry(LinearRing.class, new Coordinate[]{
            new Coordinate(2,2), new Coordinate(8,2), new Coordinate(8,8), new Coordinate(2,2),
        });
        
        GeometryFactory fac=new GeometryFactory();
        Polygon poly = fac.createPolygon(r1, new LinearRing[]{r2});
        setSource("Polygon", poly);
        
        copyFeatures(LineString.class, 2);

    }
    
    @SuppressWarnings("deprecation")
    @Ignore
    @Test
    public void testPoint2LinearRing() throws Exception {

        setTarget("name2", "org.locationtech.jts.geom.LinearRing", builder.safeCreateGeometry(LinearRing.class, new Coordinate[]{new Coordinate(10,10)}));

        copyFeatures(LinearRing.class, 1);
    }
    
    @SuppressWarnings("deprecation")
    @Ignore
    @Test
    public void testPolygon2LinearRing() throws Exception {
        setTarget("target2", "org.locationtech.jts.geom.LinearRing", builder.safeCreateGeometry(LinearRing.class, new Coordinate[]{new Coordinate(10,10)}));
        LinearRing r1 = builder.safeCreateGeometry(LinearRing.class, new Coordinate[]{
            new Coordinate(0,0), new Coordinate(10,0), new Coordinate(10,10), new Coordinate(0,10), new Coordinate(0,0)
        });
        LinearRing r2 = builder.safeCreateGeometry(LinearRing.class, new Coordinate[]{
            new Coordinate(2,2), new Coordinate(8,2), new Coordinate(8,8), new Coordinate(2,2),
        });
        
        GeometryFactory fac=new GeometryFactory();
        Polygon poly = fac.createPolygon(r1, new LinearRing[]{r2});
        setSource("Polygon", poly);
        
        copyFeatures(LinearRing.class, 2);
    }
    
    private void setSource(String type, Geometry attribute) throws Exception {
        sourceType=DataUtilities.createType("source2", "*geom:"+type+",nAme:String"); //$NON-NLS-1$ //$NON-NLS-2$
        sourceFeatures = new SimpleFeature[1];
		Object[] values = new Object[] { attribute, "sourceName" };
        sourceFeatures[0]=SimpleFeatureBuilder.build(sourceType, values, "id"); //$NON-NLS-1$
        sourceResource=CatalogTests.createGeoResource(sourceFeatures, true);
        sourceMap=MapTests.createNonDynamicMapAndRenderer(sourceResource, new Dimension(100,100));
    }

    @SuppressWarnings("deprecation")
    @Ignore
    @Test
    public void testPolygon2Point() throws Exception {
        setTarget("name2", "Point", builder.safeCreateGeometry(Point.class, new Coordinate[]{new Coordinate(10,10)}));

        Polygon poly = builder.safeCreateGeometry(Polygon.class, new Coordinate[]{
                    new Coordinate(0,0), new Coordinate(10,0), new Coordinate(10,10), new Coordinate(0,0),
                });
        setSource("Polygon", poly);
        
        copyFeatures(Point.class, 1);
        ILayer layer = targetMap.getMapLayers().get(0);
        FeatureIterator<SimpleFeature> iter = layer.getResource(FeatureSource.class, null).getFeatures(layer.getFilter()).features();
        SimpleFeature feature=iter.next();
        iter.close();
        assertEquals( poly.getCentroid().getCoordinate(), ((Geometry)feature.getDefaultGeometry()).getCoordinate() );
    }

    @SuppressWarnings("deprecation")
    @Ignore
    @Test
    public void testMultiPolygon2Point() throws Exception {
        setTarget("name2", "Point", builder.safeCreateGeometry(Point.class, new Coordinate[]{new Coordinate(10,10)}));

        Polygon poly = builder.safeCreateGeometry(Polygon.class, new Coordinate[]{
            new Coordinate(0,0), new Coordinate(10,0), new Coordinate(10,10), new Coordinate(0,0),
        });
        Polygon poly2 = builder.safeCreateGeometry(Polygon.class, new Coordinate[]{
            new Coordinate(10,10), new Coordinate(20,10), new Coordinate(20,20), new Coordinate(10,10),
        });
        GeometryFactory fac=new GeometryFactory();
        setSource("MultiPolygon", fac.createMultiPolygon(new Polygon[]{poly,poly2}));
        
        copyFeatures(Point.class, 2);
        ILayer layer = targetMap.getMapLayers().get(0);
        FeatureIterator<SimpleFeature> iter = layer.getResource(FeatureSource.class, null).getFeatures(layer.getFilter()).features();
        SimpleFeature feature=iter.next();
        assertTrue( poly.getCentroid().getCoordinate().equals(((Geometry)feature.getDefaultGeometry()).getCoordinate()) ||
                poly2.getCentroid().getCoordinate().equals(((Geometry)feature.getDefaultGeometry()).getCoordinate()) );
        feature=iter.next();
        assertTrue( poly.getCentroid().getCoordinate().equals(((Geometry)feature.getDefaultGeometry()).getCoordinate()) ||
                poly2.getCentroid().getCoordinate().equals(((Geometry)feature.getDefaultGeometry()).getCoordinate()) );
        iter.close();
    }

    @SuppressWarnings("deprecation")
    @Ignore
    @Test
    public void testMultiPolygon2MultiPoint() throws Exception {
        setTarget("name2", "MultiPoint", builder.safeCreateGeometry(MultiPoint.class, new Coordinate[]{new Coordinate(10,10)}));

        Polygon poly = builder.safeCreateGeometry(Polygon.class, new Coordinate[]{
            new Coordinate(0,0), new Coordinate(10,0), new Coordinate(10,10), new Coordinate(0,0),
        });
        Polygon poly2 = builder.safeCreateGeometry(Polygon.class, new Coordinate[]{
            new Coordinate(10,10), new Coordinate(20,10), new Coordinate(20,20), new Coordinate(10,10),
        });
        GeometryFactory fac=new GeometryFactory();
        setSource("MultiPolygon", fac.createMultiPolygon(new Polygon[]{poly,poly2}));
        
        copyFeatures(MultiPoint.class, 1);
        ILayer layer = targetMap.getMapLayers().get(0);
        FeatureIterator<SimpleFeature> iter = layer.getResource(FeatureSource.class, null).getFeatures(layer.getFilter()).features();
        SimpleFeature feature=iter.next();
        iter.close();
        assertEquals( poly.getCentroid().getCoordinate(), ((Geometry)feature.getDefaultGeometry()).getCoordinates()[0] );
        assertEquals( poly.getCentroid().getCoordinate(), ((Geometry)feature.getDefaultGeometry()).getCoordinates()[0] );
    }
    
    @SuppressWarnings("deprecation")
    @Ignore
    @Test
    public void testMultiPolygon2LineString() throws Exception {
        setTarget("name2", "LineString", builder.safeCreateGeometry(LineString.class, new Coordinate[]{new Coordinate(10,10)}));

        Polygon poly = builder.safeCreateGeometry(Polygon.class, new Coordinate[]{
            new Coordinate(0,0), new Coordinate(10,0), new Coordinate(10,10), new Coordinate(0,0),
        });
        Polygon poly2 = builder.safeCreateGeometry(Polygon.class, new Coordinate[]{
            new Coordinate(10,10), new Coordinate(20,10), new Coordinate(20,20), new Coordinate(10,10),
        });
        GeometryFactory fac=new GeometryFactory();
        setSource("MultiPolygon", fac.createMultiPolygon(new Polygon[]{poly,poly2}));
        
        copyFeatures(LineString.class, 2);
    }
    
    @SuppressWarnings("deprecation")
    @Ignore
    @Test
    public void testMultiPolygonWithHole2LineString() throws Exception {
        setTarget("name2", "LineString", builder.safeCreateGeometry(LineString.class, new Coordinate[]{new Coordinate(10,10)}));

        LinearRing r1 = builder.safeCreateGeometry(LinearRing.class, new Coordinate[]{
            new Coordinate(0,0), new Coordinate(10,0), new Coordinate(10,10), new Coordinate(0,10), new Coordinate(0,0)
        });
        LinearRing r2 = builder.safeCreateGeometry(LinearRing.class, new Coordinate[]{
            new Coordinate(2,2), new Coordinate(8,2), new Coordinate(8,8), new Coordinate(2,2),
        });
        
        GeometryFactory fac=new GeometryFactory();
        Polygon poly = fac.createPolygon(r1, new LinearRing[]{r2});
        setSource("MultiPolygon", fac.createMultiPolygon(new Polygon[]{poly}));
        
        copyFeatures(LineString.class, 2);
        
        ILayer layer = targetMap.getMapLayers().get(0);
        FeatureIterator<SimpleFeature> iter = layer.getResource(FeatureSource.class, null).getFeatures(layer.getFilter()).features();
        SimpleFeature feature=iter.next();
        assertTrue( r1.equals(feature.getDefaultGeometry()) || r2.equals(feature.getDefaultGeometry()) );
        feature=iter.next();
        assertTrue( r1.equals(feature.getDefaultGeometry()) || r2.equals(feature.getDefaultGeometry()) );
        iter.close();
    }
    
    @SuppressWarnings("deprecation")
    @Ignore
    @Test
    public void testMultiPolygonWithHole2Polygon() throws Exception {
        setTarget("name2","Polygon", builder.safeCreateGeometry(Polygon.class, new Coordinate[]{new Coordinate(10,10)}));

        LinearRing r1 = builder.safeCreateGeometry(LinearRing.class, new Coordinate[]{
            new Coordinate(0,0), new Coordinate(10,0), new Coordinate(10,10), new Coordinate(0,10), new Coordinate(0,0)
        });
        LinearRing r2 = builder.safeCreateGeometry(LinearRing.class, new Coordinate[]{
            new Coordinate(2,2), new Coordinate(8,2), new Coordinate(8,8), new Coordinate(2,2),
        });
        
        GeometryFactory fac=new GeometryFactory();
        Polygon poly = fac.createPolygon(r1, new LinearRing[]{r2});
        setSource("MultiPolygon", fac.createMultiPolygon(new Polygon[]{poly, fac.createPolygon(r2, new LinearRing[0])}));
        
        copyFeatures(Polygon.class, 2);
    }
    
    @SuppressWarnings("deprecation")
    @Ignore
    @Test
    public void testPolygonWithHole2LineString() throws Exception {
        setTarget("name2", "LineString", builder.safeCreateGeometry(LineString.class, new Coordinate[]{new Coordinate(10,10)}));

        LinearRing r1 = builder.safeCreateGeometry(LinearRing.class, new Coordinate[]{
            new Coordinate(0,0), new Coordinate(10,0), new Coordinate(10,10), new Coordinate(0,10), new Coordinate(0,0)
        });
        LinearRing r2 = builder.safeCreateGeometry(LinearRing.class, new Coordinate[]{
            new Coordinate(2,2), new Coordinate(8,2), new Coordinate(8,8), new Coordinate(2,2),
        });
        
        GeometryFactory fac=new GeometryFactory();
        Polygon poly = fac.createPolygon(r1, new LinearRing[]{r2});
        setSource("Polygon", poly);
        
        copyFeatures(LineString.class, 2);
        
        ILayer layer = targetMap.getMapLayers().get(0);
        FeatureIterator<SimpleFeature> iter = layer.getResource(FeatureSource.class, null).getFeatures(layer.getFilter()).features();
        SimpleFeature feature=iter.next();
        assertTrue( r1.equals(feature.getDefaultGeometry()) || r2.equals(feature.getDefaultGeometry()) );
        feature=iter.next();
        assertTrue( r1.equals(feature.getDefaultGeometry()) || r2.equals(feature.getDefaultGeometry()) );
        iter.close();
    }   
    
    @SuppressWarnings("deprecation")
    @Ignore
    @Test
    public void testMultiLineString2MultiPolygon() throws Exception {
        setTarget("name2", "MultiPolygon", builder.safeCreateGeometry(MultiPolygon.class, new Coordinate[]{new Coordinate(10,10)}));

        LineString poly = builder.safeCreateGeometry(LineString.class, new Coordinate[]{
            new Coordinate(0,0), new Coordinate(10,0), new Coordinate(10,10), new Coordinate(0,0),
        });
        LineString poly2 = builder.safeCreateGeometry(LineString.class, new Coordinate[]{
            new Coordinate(10,10), new Coordinate(20,10), new Coordinate(20,20), new Coordinate(10,10),
        });
        GeometryFactory fac=new GeometryFactory();
        setSource("MultiLineString", fac.createMultiLineString(new LineString[]{poly,poly2}));
        
        copyFeatures(MultiPolygon.class, 2);
    }

    
    private void copyFeatures(Class<? extends Geometry> type, int expectedFeatures) throws Exception, IOException {
        FilterFactory fac=CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        Filter filter=fac.id(FeatureUtils.stringToId(fac, sourceFeatures[0].getID()));
        Layer layer = targetMap.getLayersInternal().get(0);
        
        Layer sourceLayer = sourceMap.getLayersInternal().get(0);
        CopyFeaturesCommand action=new CopyFeaturesCommand(sourceLayer, filter, layer);

        action.setMap(targetMap);
        action.run(new NullProgressMonitor());
         FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = layer.getResource(FeatureSource.class, new NullProgressMonitor());
        assertEquals(expectedFeatures, featureSource.getFeatures(layer.getFilter()).size());
        FeatureIterator<SimpleFeature> iter=featureSource.getFeatures(layer.getFilter()).features();
        SimpleFeature feature=iter.next();
        iter.close();
        assertNotNull(feature.getDefaultGeometry());
        assertEquals( type, feature.getDefaultGeometry().getClass());
    }
    
    @Ignore
    @Test
    public void testPointToGeomDragDrop() throws Exception {
        targetType=DataUtilities.createType("target3", "*geom2:Geometry"); //$NON-NLS-1$ //$NON-NLS-2$
        SimpleFeature[] targetFeatures = new SimpleFeature[1];
        targetFeatures[0]=SimpleFeatureBuilder.build(targetType, new Object[]{null}, "id");
        targetResource=CatalogTests.createGeoResource(targetFeatures, true);
        targetMap=MapTests.createNonDynamicMapAndRenderer(targetResource, new Dimension(100,100));

        FilterFactory fac=CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        Filter filter=fac.id( FeatureUtils.stringToId(fac, sourceFeatures[0].getID()));
        Layer layer = targetMap.getLayersInternal().get(0);
        
        Layer sourceLayer = sourceMap.getLayersInternal().get(0);
        CopyFeaturesCommand action=new CopyFeaturesCommand(sourceLayer, filter, layer);

        action.setMap(targetMap);
        action.run(new NullProgressMonitor());
         FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = layer.getResource(FeatureSource.class, new NullProgressMonitor());
        assertEquals(2, featureSource.getFeatures().size());
        assertEquals(1, featureSource.getFeatures(layer.getFilter()).size());

    }
    
    /*
     * Test method for 'org.locationtech.udig.project.internal.commands.edit.CopyFeaturesCommand.rollback(IProgressMonitor)'
     */
    @Ignore
    @Test
    public void testRollback() throws Exception {
        FilterFactory fac=CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        Filter filter=fac.id(FeatureUtils.stringToId(fac, sourceFeatures[0].getID()));
        Layer layer = targetMap.getLayersInternal().get(0);
        assertEquals(Filter.EXCLUDE, layer.getFilter());
        Layer sourceLayer = sourceMap.getLayersInternal().get(0);
        CopyFeaturesCommand action=new CopyFeaturesCommand(sourceLayer, filter, layer);

        action.setMap(targetMap);
        action.run(new NullProgressMonitor());
        
         FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = layer.getResource(FeatureSource.class, new NullProgressMonitor());
        assertEquals( 2, featureSource.getFeatures().size() );
        FeatureCollection<SimpleFeatureType, SimpleFeature>  features=featureSource.getFeatures(layer.getFilter());
        
        assertEquals(1,features.size());
        SimpleFeature addedFeature=features.features().next();
        
        assertTrue(((Geometry)addedFeature.getDefaultGeometry()).equalsExact((Geometry) sourceFeatures[0].getAttribute("geom2"))); //$NON-NLS-1$
        assertEquals(sourceFeatures[0].getAttribute("nAme"), addedFeature.getAttribute("name")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(targetType, addedFeature.getFeatureType());
        
        action.rollback(new NullProgressMonitor());
        
        assertFalse( featureSource.getFeatures(filter).features().hasNext() );
        assertEquals(Filter.EXCLUDE, layer.getFilter());
        
    }

}
