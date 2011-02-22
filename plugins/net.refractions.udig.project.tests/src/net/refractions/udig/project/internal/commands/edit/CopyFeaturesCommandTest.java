package net.refractions.udig.project.internal.commands.edit;

import java.awt.Dimension;
import java.io.IOException;

import junit.framework.TestCase;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.tests.CatalogTests;
import net.refractions.udig.core.internal.GeometryBuilder;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.MapTests;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.FeatureType;
import org.geotools.filter.Filter;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.FilterFactoryFinder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class CopyFeaturesCommandTest extends TestCase {

    GeometryBuilder builder=GeometryBuilder.create();
    private FeatureType sourceType;
    private FeatureType targetType;
    private IGeoResource sourceResource;
    private Map sourceMap;
    private IGeoResource targetResource;
    private Map targetMap;
    private Feature[] sourceFeatures;

    @SuppressWarnings("deprecation")
    protected void setUp() throws Exception {
        super.setUp();
        sourceType=DataUtilities.createType("source", "*geom:LineString,geom2:Point,nAme:String"); //$NON-NLS-1$ //$NON-NLS-2$
        sourceFeatures = new Feature[1];
        GeometryFactory fac=new GeometryFactory();
        sourceFeatures[0]=sourceType.create(new Object[]{builder.safeCreateGeometry(LineString.class, new Coordinate[]{new Coordinate(10,10), new Coordinate(20,10), new Coordinate(20,20)}),
                fac.createPoint(new Coordinate(10,10)), "sourceName"}); //$NON-NLS-1$
        sourceResource=CatalogTests.createGeoResource(sourceFeatures, true);
        sourceMap=MapTests.createNonDynamicMapAndRenderer(sourceResource, new Dimension(100,100));

        targetType=DataUtilities.createType("target", "*targetGeom:Point,name:String"); //$NON-NLS-1$ //$NON-NLS-2$
        Feature[] targetFeatures = new Feature[1];
        targetFeatures[0]=targetType.create(new Object[]{fac.createPoint(new Coordinate(10,10)), "targetName"}); //$NON-NLS-1$
        targetResource=CatalogTests.createGeoResource(targetFeatures, true);
        targetMap=MapTests.createNonDynamicMapAndRenderer(targetResource, new Dimension(100,100));

    }

    protected void tearDown() throws Exception {
        super.tearDown();
        FeatureStore store = sourceResource.resolve(FeatureStore.class, null);
        store.removeFeatures(Filter.NONE);

        store = targetResource.resolve(FeatureStore.class, null);
        store.removeFeatures(Filter.NONE);
    }

    /*
     * Test method for 'net.refractions.udig.project.ui.DropFilterAction.perform(Object, Object, IProgressMonitor)'
     */
    public void testPerform() throws Exception {
        FilterFactory fac=FilterFactoryFinder.createFilterFactory();
        Filter filter=fac.createFidFilter(sourceFeatures[0].getID());
        Layer layer = targetMap.getLayersInternal().get(0);
        assertEquals(Filter.ALL, layer.getFilter());
        Layer sourceLayer = sourceMap.getLayersInternal().get(0);
        CopyFeaturesCommand action=new CopyFeaturesCommand(sourceLayer, filter, layer);
        action.setMap(targetMap);
        action.run(new NullProgressMonitor());

        FeatureSource featureSource = layer.getResource(FeatureSource.class, new NullProgressMonitor());
        assertEquals( 2, featureSource.getFeatures().size() );
        FeatureCollection features=featureSource.getFeatures(layer.getFilter());

        assertEquals(1,features.size());
        Feature addedFeature=features.features().next();

        assertTrue(addedFeature.getDefaultGeometry().equalsExact((Geometry) sourceFeatures[0].getAttribute("geom2"))); //$NON-NLS-1$
        assertEquals(sourceFeatures[0].getAttribute("nAme"), addedFeature.getAttribute("name")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(targetType, addedFeature.getFeatureType());
    }

    @SuppressWarnings("deprecation")
    public void testLine2Polygon() throws Exception {
        setTarget("targetGeom", "Polygon", builder.safeCreateGeometry(Polygon.class, new Coordinate[]{new Coordinate(10,10)}));

        copyFeatures(Polygon.class, 1);
    }
    @SuppressWarnings("deprecation")
    public void testLine2Point() throws Exception {
        setTarget("targetGeom", "Point", builder.safeCreateGeometry(Point.class, new Coordinate[]{new Coordinate(10,10)}));
        LineString line = builder.safeCreateGeometry(LineString.class, new Coordinate[]{new Coordinate(10,10), new Coordinate(20,10), new Coordinate(20,20)});
        setSource("LineString", line);

        copyFeatures(Point.class, 1);
        ILayer layer = targetMap.getMapLayers().get(0);
        FeatureIterator iter = layer.getResource(FeatureSource.class, null).getFeatures(layer.getFilter()).features();
        Feature feature=iter.next();
        iter.close();
        assertEquals( line.getCentroid().getCoordinate(), feature.getDefaultGeometry().getCoordinate() );
    }

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
        FeatureIterator iter = layer.getResource(FeatureSource.class, null).getFeatures(layer.getFilter()).features();
        Feature feature=iter.next();
        assertEquals(2, feature.getDefaultGeometry().getNumGeometries());
        iter.close();
    }

    @SuppressWarnings("deprecation")
    public void testPoint2Polygon() throws Exception {
        setTarget("name2", "Polygon", builder.safeCreateGeometry(Polygon.class, new Coordinate[]{new Coordinate(10,10)}));

        copyFeatures(Polygon.class, 1);
    }

    private void setTarget(String name, String type, Geometry geomValue) throws Exception {
        targetType=DataUtilities.createType("target2", "*"+name+":"+type); //$NON-NLS-1$ //$NON-NLS-2$
        Feature[] targetFeatures = new Feature[1];
        targetFeatures[0]=targetType.create(new Object[]{geomValue});
        targetResource=MapTests.createGeoResource(targetFeatures, true);
        targetMap=MapTests.createNonDynamicMapAndRenderer(targetResource, new Dimension(100,100));
        FeatureStore store = targetResource.resolve(FeatureStore.class, null);
        store.removeFeatures(Filter.NONE);
    }
    @SuppressWarnings("deprecation")
    public void testPoint2Line() throws Exception {

        setTarget("name2", "LineString", builder.safeCreateGeometry(LineString.class, new Coordinate[]{new Coordinate(10,10)}));

        copyFeatures(LineString.class, 1);
    }
    @SuppressWarnings("deprecation")
    public void testLine2MultiLine() throws Exception {
        setTarget("target2", "MultiLineString", builder.safeCreateGeometry(MultiLineString.class, new Coordinate[]{new Coordinate(10,10)}));

        copyFeatures(MultiLineString.class, 1);
    }
    @SuppressWarnings("deprecation")
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
    public void testPoint2LinearRing() throws Exception {

        setTarget("name2", "com.vividsolutions.jts.geom.LinearRing", builder.safeCreateGeometry(LinearRing.class, new Coordinate[]{new Coordinate(10,10)}));

        copyFeatures(LinearRing.class, 1);
    }
    @SuppressWarnings("deprecation")
    public void testPolygon2LinearRing() throws Exception {
        setTarget("target2", "com.vividsolutions.jts.geom.LinearRing", builder.safeCreateGeometry(LinearRing.class, new Coordinate[]{new Coordinate(10,10)}));
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
        sourceFeatures = new Feature[1];
        sourceFeatures[0]=sourceType.create(new Object[]{
                attribute,
                        "sourceName"}); //$NON-NLS-1$
        sourceResource=CatalogTests.createGeoResource(sourceFeatures, true);
        sourceMap=MapTests.createNonDynamicMapAndRenderer(sourceResource, new Dimension(100,100));
    }

    @SuppressWarnings("deprecation")
    public void testPolygon2Point() throws Exception {
        setTarget("name2", "Point", builder.safeCreateGeometry(Point.class, new Coordinate[]{new Coordinate(10,10)}));

        Polygon poly = builder.safeCreateGeometry(Polygon.class, new Coordinate[]{
                    new Coordinate(0,0), new Coordinate(10,0), new Coordinate(10,10), new Coordinate(0,0),
                });
        setSource("Polygon", poly);

        copyFeatures(Point.class, 1);
        ILayer layer = targetMap.getMapLayers().get(0);
        FeatureIterator iter = layer.getResource(FeatureSource.class, null).getFeatures(layer.getFilter()).features();
        Feature feature=iter.next();
        iter.close();
        assertEquals( poly.getCentroid().getCoordinate(), feature.getDefaultGeometry().getCoordinate() );
    }

    @SuppressWarnings("deprecation")
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
        FeatureIterator iter = layer.getResource(FeatureSource.class, null).getFeatures(layer.getFilter()).features();
        Feature feature=iter.next();
        assertTrue( poly.getCentroid().getCoordinate().equals(feature.getDefaultGeometry().getCoordinate()) ||
                poly2.getCentroid().getCoordinate().equals(feature.getDefaultGeometry().getCoordinate()) );
        feature=iter.next();
        assertTrue( poly.getCentroid().getCoordinate().equals(feature.getDefaultGeometry().getCoordinate()) ||
                poly2.getCentroid().getCoordinate().equals(feature.getDefaultGeometry().getCoordinate()) );
        iter.close();
    }


    @SuppressWarnings("deprecation")
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
        FeatureIterator iter = layer.getResource(FeatureSource.class, null).getFeatures(layer.getFilter()).features();
        Feature feature=iter.next();
        iter.close();
        assertEquals( poly.getCentroid().getCoordinate(), feature.getDefaultGeometry().getCoordinates()[0] );
        assertEquals( poly.getCentroid().getCoordinate(), feature.getDefaultGeometry().getCoordinates()[0] );
    }

    @SuppressWarnings("deprecation")
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
        FeatureIterator iter = layer.getResource(FeatureSource.class, null).getFeatures(layer.getFilter()).features();
        Feature feature=iter.next();
        assertTrue( r1.equals(feature.getDefaultGeometry()) || r2.equals(feature.getDefaultGeometry()) );
        feature=iter.next();
        assertTrue( r1.equals(feature.getDefaultGeometry()) || r2.equals(feature.getDefaultGeometry()) );
        iter.close();
    }
    @SuppressWarnings("deprecation")
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
        FeatureIterator iter = layer.getResource(FeatureSource.class, null).getFeatures(layer.getFilter()).features();
        Feature feature=iter.next();
        assertTrue( r1.equals(feature.getDefaultGeometry()) || r2.equals(feature.getDefaultGeometry()) );
        feature=iter.next();
        assertTrue( r1.equals(feature.getDefaultGeometry()) || r2.equals(feature.getDefaultGeometry()) );
        iter.close();
    }

    @SuppressWarnings("deprecation")
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
        FilterFactory fac=FilterFactoryFinder.createFilterFactory();
        Filter filter=fac.createFidFilter(sourceFeatures[0].getID());
        Layer layer = targetMap.getLayersInternal().get(0);

        Layer sourceLayer = sourceMap.getLayersInternal().get(0);
        CopyFeaturesCommand action=new CopyFeaturesCommand(sourceLayer, filter, layer);

        action.setMap(targetMap);
        action.run(new NullProgressMonitor());
        FeatureSource featureSource = layer.getResource(FeatureSource.class, new NullProgressMonitor());
        assertEquals(expectedFeatures, featureSource.getFeatures(layer.getFilter()).size());
        FeatureIterator iter=featureSource.getFeatures(layer.getFilter()).features();
        Feature feature=iter.next();
        iter.close();
        assertNotNull(feature.getDefaultGeometry());
        assertEquals( type, feature.getDefaultGeometry().getClass());
    }

    public void testPointToGeomDragDrop() throws Exception {
        targetType=DataUtilities.createType("target3", "*geom2:Geometry"); //$NON-NLS-1$ //$NON-NLS-2$
        Feature[] targetFeatures = new Feature[1];
        targetFeatures[0]=targetType.create(new Object[]{null});
        targetResource=MapTests.createGeoResource(targetFeatures, true);
        targetMap=MapTests.createNonDynamicMapAndRenderer(targetResource, new Dimension(100,100));

        FilterFactory fac=FilterFactoryFinder.createFilterFactory();
        Filter filter=fac.createFidFilter(sourceFeatures[0].getID());
        Layer layer = targetMap.getLayersInternal().get(0);

        Layer sourceLayer = sourceMap.getLayersInternal().get(0);
        CopyFeaturesCommand action=new CopyFeaturesCommand(sourceLayer, filter, layer);

        action.setMap(targetMap);
        action.run(new NullProgressMonitor());
        FeatureSource featureSource = layer.getResource(FeatureSource.class, new NullProgressMonitor());
        assertEquals(2, featureSource.getFeatures().size());
        assertEquals(1, featureSource.getFeatures(layer.getFilter()).size());

    }

    /*
     * Test method for 'net.refractions.udig.project.internal.commands.edit.CopyFeaturesCommand.rollback(IProgressMonitor)'
     */
    public void testRollback() throws Exception {
        FilterFactory fac=FilterFactoryFinder.createFilterFactory();
        Filter filter=fac.createFidFilter(sourceFeatures[0].getID());
        Layer layer = targetMap.getLayersInternal().get(0);
        assertEquals(Filter.ALL, layer.getFilter());
        Layer sourceLayer = sourceMap.getLayersInternal().get(0);
        CopyFeaturesCommand action=new CopyFeaturesCommand(sourceLayer, filter, layer);

        action.setMap(targetMap);
        action.run(new NullProgressMonitor());

        FeatureSource featureSource = layer.getResource(FeatureSource.class, new NullProgressMonitor());
        assertEquals( 2, featureSource.getFeatures().size() );
        FeatureCollection features=featureSource.getFeatures(layer.getFilter());

        assertEquals(1,features.size());
        Feature addedFeature=features.features().next();

        assertTrue(addedFeature.getDefaultGeometry().equalsExact((Geometry) sourceFeatures[0].getAttribute("geom2"))); //$NON-NLS-1$
        assertEquals(sourceFeatures[0].getAttribute("nAme"), addedFeature.getAttribute("name")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(targetType, addedFeature.getFeatureType());

        action.rollback(new NullProgressMonitor());

        assertFalse( featureSource.getFeatures(filter).iterator().hasNext() );
        assertEquals(Filter.ALL, layer.getFilter());

    }

}
