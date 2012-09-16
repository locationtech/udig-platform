/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.tools.edit.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
import java.util.List;

import net.refractions.udig.TestViewportPane;
import net.refractions.udig.core.internal.FeatureUtils;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.render.impl.ViewportModelImpl;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.TestHandler;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureIterator;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Test the split SimpleFeature command
 * 
 * @author jones
 * @since 1.1.0
 */
public class DifferenceFeatureCommandTest {

    private SimpleFeature[] features;
    private Map map;
    private TestHandler handler;

    @Before
    public void setUp() throws Exception {
        GeometryFactory fac=new GeometryFactory();
        LineString line = fac.createLineString(new Coordinate[]{
           new Coordinate(0,10),new Coordinate(10,10), new Coordinate(20,10) 
        });
        Polygon poly = fac.createPolygon(fac.createLinearRing(new Coordinate[]{
                new Coordinate(20,20),new Coordinate(40,20), new Coordinate(40,40), 
                new Coordinate(20,40), new Coordinate(20,20) 
             }), new LinearRing[0]);
    
        handler=new TestHandler();
        features=UDIGTestUtil.createTestFeatures("DifferenceFeatureTests", new Geometry[]{line, poly}, new String[]{"line", "poly"});   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        map = MapTests.createNonDynamicMapAndRenderer(MapTests.createGeoResource(features, true), new Dimension(500,500));
        
        Envelope env=map.getBounds(null);
        map.getRenderManagerInternal().setMapDisplay(new TestViewportPane(new Dimension((int)env.getWidth(),(int)env.getHeight())));
        map.setViewportModelInternal(new ViewportModelImpl(){
           @Override
        public AffineTransform worldToScreenTransform() {
            return new AffineTransform();
        } 
        });
        
        handler.setContext(ApplicationGIS.createContext(map));
    }

    /*
     * Test method for 'net.refractions.udig.tools.edit.commands.SplitFeatureCommand.run(IProgressMonitor)'
     */
    @Test
    public void testDifferencePolygonOnce() throws Exception {
        handler.resetEditBlackboard();
        EditBlackboard bb = handler.getEditBlackboard();
        PrimitiveShape shell = bb.newGeom(null, null).getShell();
        handler.setCurrentShape(shell);
        bb.addPoint(25,0, shell);
        bb.addPoint(35,0, shell);
        bb.addPoint(35,60, shell);
        bb.addPoint(25,60, shell);
        
        DifferenceFeatureCommand command=new DifferenceFeatureCommand(handler, EditState.NONE);
        
        command.setMap(map);
        NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
        command.run(nullProgressMonitor);
        
        FeatureSource<SimpleFeatureType, SimpleFeature> resource = map.getEditManager().getSelectedLayer().getResource(FeatureSource.class, nullProgressMonitor);
        assertEquals(3, resource.getCount(Query.ALL));
        FeatureIterator<SimpleFeature> iter = resource.getFeatures().features();
        int found=0;
        while( iter.hasNext() ){
            SimpleFeature feature=iter.next();
            if( feature.getID().equals("new0") ){ //$NON-NLS-1$
                List<Coordinate> coords = Arrays.asList(((Geometry) feature.getDefaultGeometry()).getCoordinates());
                assertEquals(10, coords.size());
                assertTrue(coords.contains( bb.toCoord(Point.valueOf(25,0)) ) );
                assertTrue(coords.contains(new Coordinate(35.5,0.5)) );
                assertTrue(coords.contains(new Coordinate(35.5,20)) );
                assertTrue(coords.contains(new Coordinate(25.5,20)) );
                assertTrue(coords.contains(new Coordinate(25.5,40) ) );
                assertTrue(coords.contains(new Coordinate(35.5,40)) );
                assertTrue(coords.contains(new Coordinate(35.5,60.5)) );
                assertTrue(coords.contains(new Coordinate(25.5,60.5)) );
                found++;
                break;
            }
        }
        
        assertEquals( 1, found );
        assertNull(handler.getCurrentGeom());
    }

    /*
     * Test method for 'net.refractions.udig.tools.edit.commands.SplitFeatureCommand.run(IProgressMonitor)'
     */
    @Test
    public void testDifferenceMultiPolygon() throws Exception {
        handler.resetEditBlackboard();
        GeometryFactory fac=new GeometryFactory();
        Polygon[] polygons = new Polygon[]{ (Polygon) features[1].getDefaultGeometry()};
        MultiPolygon createMultiPolygon = fac.createMultiPolygon(polygons);
        features[1].setDefaultGeometry(createMultiPolygon);
        EditBlackboard bb = handler.getEditBlackboard();
        PrimitiveShape shell = bb.newGeom(null, null).getShell();
        handler.setCurrentShape(shell);
        bb.addPoint(25,0, shell);
        bb.addPoint(35,0, shell);
        bb.addPoint(35,60, shell);
        bb.addPoint(25,60, shell);
        
        DifferenceFeatureCommand command=new DifferenceFeatureCommand(handler, EditState.NONE);
        
        command.setMap(map);
        NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
        command.run(nullProgressMonitor);
        
        FeatureSource<SimpleFeatureType, SimpleFeature> resource = map.getEditManager().getSelectedLayer().getResource(FeatureSource.class, nullProgressMonitor);
        assertEquals(3, resource.getCount(Query.ALL));
        FeatureIterator<SimpleFeature> iter = resource.getFeatures().features();
        int found=0;
        while( iter.hasNext() ){
            SimpleFeature feature=iter.next();
            if( feature.getID().equals("new0") ){ //$NON-NLS-1$
                List<Coordinate> coords = Arrays.asList(((Geometry) feature.getDefaultGeometry()).getCoordinates());
                assertEquals(10, coords.size());
                assertTrue(coords.contains(new Coordinate(25.5,0.5) ) );
                assertTrue(coords.contains(new Coordinate(35.5,0.5)) );
                assertTrue(coords.contains(new Coordinate(35.5,20)) );
                assertTrue(coords.contains(new Coordinate(25.5,20)) );
                assertTrue(coords.contains(new Coordinate(25.5,0.5)) );
                assertTrue(coords.contains(new Coordinate(25.5,40) ) );
                assertTrue(coords.contains(new Coordinate(35.5,40)) );
                assertTrue(coords.contains(new Coordinate(35.5,60.5)) );
                assertTrue(coords.contains(new Coordinate(25.5,60.5)) );
                assertTrue(coords.contains(new Coordinate(25.5,40)) );
                found++;
                break;
            }
        }
        
        assertEquals( 1, found );
    }

    @Test
    public void testDiffOnMultipleGeoms() throws Exception {
        handler.resetEditBlackboard();
        GeometryFactory fac=new GeometryFactory();
        LinearRing ring=fac.createLinearRing(new Coordinate[]{
                new Coordinate(0,50),
                new Coordinate(50,50),
                new Coordinate(50,55),
                new Coordinate(0,55),
                new Coordinate(0,50),
        });
        Polygon polygon = fac.createPolygon(ring, new LinearRing[0]);
        FeatureStore<SimpleFeatureType, SimpleFeature> store = map.getMapLayers().get(0).getResource(FeatureStore.class, new NullProgressMonitor());        
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
		store.modifyFeatures(features[0].getFeatureType().getGeometryDescriptor(), polygon, 
                filterFactory.id(FeatureUtils.stringToId(filterFactory, features[0].getID())));
		
        EditBlackboard bb = handler.getEditBlackboard();
        
        PrimitiveShape shell = bb.newGeom(null, null).getShell();
        handler.setCurrentShape(shell);
        bb.addPoint(25,0, shell);
        bb.addPoint(35,0, shell);
        bb.addPoint(35,60, shell);
        bb.addPoint(25,60, shell);
        
        DifferenceFeatureCommand command=new DifferenceFeatureCommand(handler, EditState.NONE);
        
        command.setMap(map);
        NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
        command.run(nullProgressMonitor);
        
        FeatureSource<SimpleFeatureType, SimpleFeature> resource = map.getEditManager().getSelectedLayer().getResource(FeatureSource.class, nullProgressMonitor);
        assertEquals(3, resource.getCount(Query.ALL));
        FeatureIterator<SimpleFeature> iter = resource.getFeatures().features();
        int found=0;
        while( iter.hasNext() ){
            SimpleFeature feature=iter.next();
            if( feature.getID().equals("new0") ){ //$NON-NLS-1$
                List<Coordinate> coords = Arrays.asList(((Geometry) feature.getDefaultGeometry()).getCoordinates());
                assertEquals(15, coords.size());
                assertTrue(coords.contains(new Coordinate(25.5,0.5) ) );
                assertTrue(coords.contains(new Coordinate(35.5,0.5)) );
                assertTrue(coords.contains(new Coordinate(35.5,20)) );
                assertTrue(coords.contains(new Coordinate(25.5,20)) );
                assertTrue(coords.contains(new Coordinate(25.5,0.5)) );
                
                assertTrue(coords.contains(new Coordinate(25.5,40) ) );
                assertTrue(coords.contains(new Coordinate(35.5,40)) );
                assertTrue(coords.contains(new Coordinate(35.5,50)) );
                assertTrue(coords.contains(new Coordinate(25.5,50)) );
                assertTrue(coords.contains(new Coordinate(25.5,40)) );
                
                assertTrue(coords.contains(new Coordinate(25.5,55) ) );
                assertTrue(coords.contains(new Coordinate(35.5,55)) );
                assertTrue(coords.contains(new Coordinate(35.5,60.5)) );
                assertTrue(coords.contains(new Coordinate(25.5,60.5)) );
                assertTrue(coords.contains(new Coordinate(25.5,55)) );
                found++;
                break;
            }
        }
        
        assertEquals( 1, found );
    }
    
    @Test
    public void testMultiGeometry() throws Exception {
        handler.resetEditBlackboard();
        GeometryFactory fac=new GeometryFactory();
        LinearRing ring1=fac.createLinearRing(new Coordinate[]{
                new Coordinate(0,0),
                new Coordinate(50,0),
                new Coordinate(50,10),
                new Coordinate(0,10),
                new Coordinate(0,0),
        });
        Polygon polygon1 = fac.createPolygon(ring1, new LinearRing[0]);

        LinearRing ring2=fac.createLinearRing(new Coordinate[]{
                new Coordinate(0,50),
                new Coordinate(50,50),
                new Coordinate(50,60),
                new Coordinate(0,60),
                new Coordinate(0,50),
        });
        Polygon polygon2 = fac.createPolygon(ring2, new LinearRing[0]);

        Geometry geom=fac.createMultiPolygon(new Polygon[]{polygon1, polygon2});
        
        FeatureStore<SimpleFeatureType, SimpleFeature> store = map.getMapLayers().get(0).getResource(FeatureStore.class, new NullProgressMonitor());
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
		store.removeFeatures(filterFactory.id(FeatureUtils.stringToId(filterFactory, features[1].getID())));
        store.modifyFeatures(features[0].getFeatureType().getGeometryDescriptor(), geom, 
                filterFactory.id(FeatureUtils.stringToId(filterFactory, features[0].getID())));
        EditBlackboard bb = handler.getEditBlackboard();
        
        PrimitiveShape shell = bb.newGeom(null, null).getShell();
        handler.setCurrentShape(shell);
        bb.addPoint(25,0, shell);
        bb.addPoint(35,0, shell);
        bb.addPoint(35,65, shell);
        bb.addPoint(25,65, shell);
        
        DifferenceFeatureCommand command=new DifferenceFeatureCommand(handler, EditState.NONE);
        
        command.setMap(map);
        NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
        command.run(nullProgressMonitor);
        
        FeatureSource<SimpleFeatureType, SimpleFeature> resource = map.getEditManager().getSelectedLayer().getResource(FeatureSource.class, nullProgressMonitor);
        assertEquals(2, resource.getCount(Query.ALL));
        FeatureIterator<SimpleFeature> iter = resource.getFeatures().features();
        int found=0;
        while( iter.hasNext() ){
            SimpleFeature feature=iter.next();
            if( feature.getID().equals("new0") ){ //$NON-NLS-1$
                List<Coordinate> coords = Arrays.asList(((Geometry) feature.getDefaultGeometry()).getCoordinates());
                assertEquals(10, coords.size());
                assertTrue(coords.contains(new Coordinate(25.5,10) ) );
                assertTrue(coords.contains(new Coordinate(35.5,10)) );
                assertTrue(coords.contains(new Coordinate(35.5,50)) );
                assertTrue(coords.contains(new Coordinate(25.5,50)) );
                assertTrue(coords.contains(new Coordinate(25.5,10)) );
                
                assertTrue(coords.contains(new Coordinate(25.5,60) ) );
                assertTrue(coords.contains(new Coordinate(35.5,60)) );
                assertTrue(coords.contains(new Coordinate(35.5,65.5)) );
                assertTrue(coords.contains(new Coordinate(25.5,65.5)) );
                assertTrue(coords.contains(new Coordinate(25.5,65.5)) );
                
                found++;
                break;
            }
        }
        
        assertEquals( 1, found );
    }
    
}
