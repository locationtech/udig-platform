/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2006, Refractions Research Inc.
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
package net.refractions.udig.validation.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.validation.ValidateOverlaps;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.DataUtilities;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Id;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

public class IntegrityValidationTest {

    /**
     * Test method for 'net.refractions.udig.validation.ValidateOverlaps.op(Display,
     * Object, IProgressMonitor)'
     */
    @Ignore
    @Test
    public void testOverlapsOp() throws Exception {
        //create features suitable for the test
        GeometryFactory factory = new GeometryFactory();
        LineString[] line = new LineString[4];
        // first test: 2 overlapping lines, overlap test fails?
        line[0] = factory.createLineString(new Coordinate[]{new Coordinate(10, 10),
                new Coordinate(20, 20),});
        line[1] = factory.createLineString(new Coordinate[]{new Coordinate(15, 15),
                new Coordinate(25, 25),});
        assertTrue(line[0].overlaps(line[1])); // just checking :)
        // second test: does this validation test for self-overlaps? (it shouldn't)
        line[2] = factory.createLineString(new Coordinate[]{new Coordinate(50, 50),
                new Coordinate(60, 50), new Coordinate(55, 50),});
        // third test: an intersecting line; is valid?  
        line[3] = factory.createLineString(new Coordinate[]{new Coordinate(10, 20),
                new Coordinate(20, 10),});

        String[] attrValues = new String[4];
        attrValues[0] = "value0"; //$NON-NLS-1$
        attrValues[1] = "value1"; //$NON-NLS-1$
        attrValues[2] = "value2"; //$NON-NLS-1$
        attrValues[3] = "value3"; //$NON-NLS-1$

        SimpleFeatureType ft = DataUtilities.createType("myLineType", "*geom:LineString,name:String"); //$NON-NLS-1$ //$NON-NLS-2$
        ft = DataUtilities.createSubType(ft, null, DefaultEngineeringCRS.CARTESIAN_2D);
        SimpleFeature[] features = new SimpleFeature[4];
        // add lines
        features[0] = SimpleFeatureBuilder.build(ft,new Object[]{line[0], attrValues[0]}, Integer.toString(0));
        features[1] = SimpleFeatureBuilder.build(ft,new Object[]{line[1], attrValues[1]}, Integer.toString(1));
        features[2] = SimpleFeatureBuilder.build(ft,new Object[]{line[2], attrValues[2]}, Integer.toString(2));
        features[3] = SimpleFeatureBuilder.build(ft,new Object[]{line[3], attrValues[3]}, Integer.toString(3));

        IGeoResource resource = MapTests.createGeoResource(features, true);
        Map map = MapTests.createNonDynamicMapAndRenderer(resource, new Dimension(500, 512));
        ValidateOverlaps validator = new ValidateOverlaps();
        validator.op(Display.getDefault(), map.getLayersInternal().get(0),
                new NullProgressMonitor());
        assertEquals(1, validator.genericResults.failedFeatures.size()); //only line[0] and line[1] should fail (counts as 1)
        map.sendCommandSync(new AbstractCommand(){

            public void run( IProgressMonitor monitor ) throws Exception {
            }

            public String getName() {
                return null;
            }

        });//send a sync command so async doesn't give us a false junit failure

        Id filter = (Id) map.getLayersInternal().get(0).getFilter();
        String[] fids = filter.getIDs().toArray(new String[0]);
        //System.out.println(fids[0].length()+" features in FID");
        assertEquals(1, fids[0].length()); //only 1 feature failed?
        assertEquals(features[0].getID(), fids[0]); //it was feature 0 that failed?
    }

}
