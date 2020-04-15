/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.WKTReader;

/**
 * This is a "quick" test case of the functions
 * behind the difference feature tool.
 * <p>
 * We have made the functions package visible so the test case
 * can see them. The test case can be run as a normal 
 * JUnit test and does not require a plugin environment.
 * 
 * @author Brock Anderson
 * @author Jody Garnett
 * @since 1.1.0
 */
@SuppressWarnings("nls")
public class DifferenceFeatureCommandQuickTest {

	/**
	 * This test case is based on the experience of 
	 * @throws Exception
	 */
    @Test
	public void testDiffOnDonut() throws Exception {
        GeometryFactory geometryFactory = new GeometryFactory();
        WKTReader reader = new WKTReader( geometryFactory );
        
        
        //create feature collection with 2 geometries:
        //1. donut minus hole
        Polygon donut = (Polygon) reader.read("POLYGON ((100 100, 120 100, 120 120, 100 120, 100 100),(112 112, 118 112, 118 118, 112 118, 112 112))");
        
        //2. hole
        Polygon hole = (Polygon) reader.read("POLYGON ((112 112, 118 112, 118 118, 112 118, 112 112))");
        
        //add the two geometries to a FeatureCollection
        SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
        b.setName( "Test" );
        b.add( "location", Polygon.class );
        SimpleFeatureType type = b.buildFeatureType();
        SimpleFeature donutFeature = SimpleFeatureBuilder.build(type, new Object[]{donut}, "fid.1");
        SimpleFeature holeFeature = SimpleFeatureBuilder.build(type, new Object[]{hole}, "fid.2");
        DefaultFeatureCollection fc = new DefaultFeatureCollection();
        fc.add(donutFeature);
        fc.add(holeFeature);
        
        //create iterator for collection
        FeatureIterator<SimpleFeature> iterator = fc.features();
        
        //create List<Geometry> for the simulated "drawn" shape
        Polygon userDrawnPoly = (Polygon) reader.read("POLYGON ((114 90, 130 90, 130 130, 114 130, 114 90))");
        List<Geometry> geoms = new ArrayList<Geometry>();
        geoms.add(userDrawnPoly);
        
        DifferenceFeatureCommand.runDifferenceOp(iterator, geoms);
        assertEquals(1, geoms.size());

        Geometry result = geoms.get(0);
        Coordinate[] coordArray = result.getCoordinates();
        List<Coordinate> coords = Arrays.asList(coordArray);
        assertEquals(9, coords.size());
        assertTrue(coords.contains(new Coordinate(114,120) ) ); //twice
        assertTrue(coords.contains(new Coordinate(114,130) ) );
        assertTrue(coords.contains(new Coordinate(130,130) ) );
        assertTrue(coords.contains(new Coordinate(130,90) ) );
        assertTrue(coords.contains(new Coordinate(114,90) ) );
        assertTrue(coords.contains(new Coordinate(114,100) ) );
        assertTrue(coords.contains(new Coordinate(120,100) ) );
        assertTrue(coords.contains(new Coordinate(120,120) ) );
        

        
    }
    
}
