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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

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
public class DifferenceFeatureCommandQuickTest extends TestCase {

	/**
	 * This test case is based on the experience of 
	 * @throws Exception
	 */
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
        FeatureCollection<SimpleFeatureType, SimpleFeature> fc = FeatureCollections.newCollection();
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
