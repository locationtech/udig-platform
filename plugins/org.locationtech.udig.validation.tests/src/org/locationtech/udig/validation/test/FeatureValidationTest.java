/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2006, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.validation.test;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.tests.support.MapTests;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;
import org.locationtech.udig.validation.ValidateGeometry;
import org.locationtech.udig.validation.ValidateLineMustBeASinglePart;
import org.locationtech.udig.validation.ValidateLineNoSelfIntersect;
import org.locationtech.udig.validation.ValidateLineNoSelfOverlapping;
import org.locationtech.udig.validation.ValidateNullZero;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
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
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

public class FeatureValidationTest {

    /*
     * Test method for 'org.locationtech.udig.validation.ValidateGeometry.op(Display, Object,
     * IProgressMonitor)'
     */
    @Ignore
    @Test
    public void testGeometryOp() throws Exception {
        SimpleFeature[] features = UDIGTestUtil.createDefaultTestFeatures("someType", 5); //$NON-NLS-1$
        Geometry geometry = (Geometry) features[0].getDefaultGeometry();
		geometry.getCoordinates()[0].x = 2;
        //features[0].setDefaultGeometry(null);
        IGeoResource resource = MapTests.createGeoResource(features, true);
        Map map = MapTests.createNonDynamicMapAndRenderer(resource, new Dimension(500,512));
        ValidateGeometry isValidGeometry = new ValidateGeometry();
        isValidGeometry.op(Display.getDefault(), map.getLayersInternal().get(0), new NullProgressMonitor());
        assertEquals(1,isValidGeometry.results.failedFeatures.size());
        map.sendCommandSync(new AbstractCommand(){

            public void run( IProgressMonitor monitor ) throws Exception {
            }

            public String getName() {
                return null;
            }
            
        });//send a sync command so async doesn't give us a false junit failure
        
        //System.out.println("\n"+map.getLayersInternal().get(0).getFilter().getClass());
        Id filter = (Id) map.getLayersInternal().get(0).getFilter();
        String[] fids = filter.getIDs().toArray(new String[0]);
        assertEquals(1,fids[0].length());
        assertEquals(features[0].getID(),fids[0]);
    }

    /*
     * Test method for 'org.locationtech.udig.validation.ValidateLineMustBeASinglePart.op(Display,
     * Object, IProgressMonitor)'
     */
    @Ignore
    @Test
    public void testLineMustBeASinglePartOp() throws Exception {
        //create features suitable for the test
        GeometryFactory factory=new GeometryFactory();
        // test with 2 lines: 2 points, 3 points
        // - only the 2 point line should pass
        LineString[] line = new LineString[3];
        line[0] = factory.createLineString(new Coordinate[]{new Coordinate(15, 15),
                new Coordinate(20, 20),});
        line[1] = factory.createLineString(new Coordinate[]{new Coordinate(10, 15),
                new Coordinate(20, 25), new Coordinate(30, 35),});
        
        String[] attrValues = new String[3];
        attrValues[0] = "value0"; attrValues[1] = "value1"; attrValues[2] = "value2"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        SimpleFeatureType ft=DataUtilities.createType("myLineType", "*geom:LineString,name:String"); //$NON-NLS-1$ //$NON-NLS-2$
        ft=DataUtilities.createSubType(ft, null, DefaultEngineeringCRS.CARTESIAN_2D);
        SimpleFeature[] features=new SimpleFeature[2];
        // add lines
        features[0]=SimpleFeatureBuilder.build(ft,new Object[]{line[0], attrValues[0]}, Integer.toString(0));    
        features[1]=SimpleFeatureBuilder.build(ft,new Object[]{line[1], attrValues[1]}, Integer.toString(1));    
        
        IGeoResource resource = MapTests.createGeoResource(features, true);
        Map map = MapTests.createNonDynamicMapAndRenderer(resource, new Dimension(500,512));
        ValidateLineMustBeASinglePart isValidLine = new ValidateLineMustBeASinglePart();
        isValidLine.op(Display.getDefault(), map.getLayersInternal().get(0), new NullProgressMonitor());
        //System.out.println(isValidLine.genericResults.failedFeatures.size()+" failed feature");
        assertEquals(1,isValidLine.results.failedFeatures.size());
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
        assertEquals(1,fids[0].length()); //only 1 feature failed?
        assertEquals(features[1].getID(),fids[0]); //feature 1 failed?
    }

    /*
     * Test method for 'org.locationtech.udig.validation.ValidateLineNoSelfIntersect.op(Display,
     * Object, IProgressMonitor)'
     */
    @Ignore
    @Test
    public void testLineNoSelfIntersectOp() throws Exception {
        //create features suitable for the test
        GeometryFactory factory=new GeometryFactory();
        // test with 2 lines: a non-self-intersecting line, a self-intersecting line
        LineString[] line = new LineString[3];
        line[0] = factory.createLineString(new Coordinate[]{new Coordinate(10, 10),
                new Coordinate(10, 20),});
        line[1] = factory.createLineString(new Coordinate[]{new Coordinate(20, 10),
                new Coordinate(20, 20), new Coordinate(30, 15), new Coordinate(15, 15),});
        
        String[] attrValues = new String[2];
        attrValues[0] = "value0"; attrValues[1] = "value1"; //$NON-NLS-1$ //$NON-NLS-2$

        SimpleFeatureType ft=DataUtilities.createType("myLineType", "*geom:LineString,name:String"); //$NON-NLS-1$ //$NON-NLS-2$
        ft=DataUtilities.createSubType(ft, null, DefaultEngineeringCRS.CARTESIAN_2D);
        SimpleFeature[] features=new SimpleFeature[2];
        // add lines
        features[0]=SimpleFeatureBuilder.build(ft,new Object[]{line[0], attrValues[0]}, Integer.toString(0));    
        features[1]=SimpleFeatureBuilder.build(ft,new Object[]{line[1], attrValues[1]}, Integer.toString(1));    
        
        IGeoResource resource = MapTests.createGeoResource(features, true);
        Map map = MapTests.createNonDynamicMapAndRenderer(resource, new Dimension(500,512));
        ValidateLineNoSelfIntersect isValidLine = new ValidateLineNoSelfIntersect();
        isValidLine.op(Display.getDefault(), map.getLayersInternal().get(0), new NullProgressMonitor());
        //System.out.println(isValidLine.genericResults.failedFeatures.size()+" failed feature");
        assertEquals(1,isValidLine.results.failedFeatures.size());
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
        assertEquals(1,fids[0].length()); //only 1 feature failed?
        assertEquals(features[1].getID(),fids[0]); //feature 1 failed?
    }

    /*
     * Test method for 'org.locationtech.udig.validation.ValidateLineNoSelfOverlap.op(Display,
     * Object, IProgressMonitor)'
     */
    @Ignore
    @Test
    public void testLineNoSelfOverlapOp() throws Exception {
        //create features suitable for the test
        GeometryFactory factory=new GeometryFactory();
        // test with 2 lines: a non-overlapping line, an overlapping line
        LineString[] line = new LineString[3];
        line[0] = factory.createLineString(new Coordinate[]{new Coordinate(10, 10),
                new Coordinate(10, 20),});
        line[1] = factory.createLineString(new Coordinate[]{new Coordinate(20, 10),
                new Coordinate(20, 20), new Coordinate(30, 15), new Coordinate(20, 15),
                new Coordinate(20, 30)});
        
        String[] attrValues = new String[2];
        attrValues[0] = "value0"; attrValues[1] = "value1"; //$NON-NLS-1$ //$NON-NLS-2$

        SimpleFeatureType ft=DataUtilities.createType("myLineType", "*geom:LineString,name:String"); //$NON-NLS-1$ //$NON-NLS-2$
        ft=DataUtilities.createSubType(ft, null, DefaultEngineeringCRS.CARTESIAN_2D);
        SimpleFeature[] features=new SimpleFeature[2];
        // add lines
        features[0]=SimpleFeatureBuilder.build(ft,new Object[]{line[0], attrValues[0]}, Integer.toString(0));    
        features[1]=SimpleFeatureBuilder.build(ft,new Object[]{line[1], attrValues[1]}, Integer.toString(1));    
        
        IGeoResource resource = MapTests.createGeoResource(features, true);
        Map map = MapTests.createNonDynamicMapAndRenderer(resource, new Dimension(500,512));
        ValidateLineNoSelfOverlapping isValidLine = new ValidateLineNoSelfOverlapping();
        isValidLine.op(Display.getDefault(), map.getLayersInternal().get(0), new NullProgressMonitor());
        //System.out.println(isValidLine.genericResults.failedFeatures.size()+" failed feature");
        assertEquals(1,isValidLine.results.failedFeatures.size());
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
        assertEquals(1,fids[0].length()); //only 1 feature failed?
        assertEquals(features[1].getID(),fids[0]); //feature 1 failed?
    }

    /*
     * Test method for 'org.locationtech.udig.validation.ValidateNullZero.op(Display,
     * Object, IProgressMonitor)'
     */
    @Test
    public void testNullZeroOp() throws Exception {
        //create features suitable for the test
        GeometryFactory factory=new GeometryFactory();
        // test with 2 arbitrary features, with the second having a null attribute
        LineString[] line = new LineString[3];
        line[0] = factory.createLineString(new Coordinate[]{new Coordinate(10, 10),
                new Coordinate(10, 20),});
        line[1] = factory.createLineString(new Coordinate[]{new Coordinate(20, 10),
                new Coordinate(20, 20), new Coordinate(30, 15), new Coordinate(20, 15),
                new Coordinate(20, 30)});
        
        String[] attrValues = new String[2];
        attrValues[0] = "value0"; attrValues[1] = null; //$NON-NLS-1$

        SimpleFeatureType ft=DataUtilities.createType("myLineType", "*geom:LineString,name:String"); //$NON-NLS-1$ //$NON-NLS-2$
        ft=DataUtilities.createSubType(ft, null, DefaultEngineeringCRS.CARTESIAN_2D);
        SimpleFeature[] features=new SimpleFeature[2];
        // add lines
        features[0]=SimpleFeatureBuilder.build(ft,new Object[]{line[0], attrValues[0]}, Integer.toString(0));    
        features[1]=SimpleFeatureBuilder.build(ft,new Object[]{line[1], attrValues[1]}, Integer.toString(1));    
        //FeatureFactory ff = new FeatureFactory();
        
        IGeoResource resource = MapTests.createGeoResource(features, true);
        Map map = MapTests.createNonDynamicMapAndRenderer(resource, new Dimension(500,512));
        ValidateNullZero isValidAttr = new ValidateNullZero();

        // test the dialog
        Dialog dialog = isValidAttr.getDialog(Display.getDefault().getActiveShell(), ((ILayer) map.getLayersInternal().get(0)).getSchema());
        dialog.setBlockOnOpen(false);
        dialog.open();
        // check the default xPath
        assertEquals(isValidAttr.xPath,"geom"); // first entry in attributes //$NON-NLS-1$
        //set a new xPath
        isValidAttr.combo.select(1);
        // check the new xPath
        assertEquals(isValidAttr.xPath,"name"); // second entry in attributes //$NON-NLS-1$
        
        // other checks...
        
        System.out.println("END OF DIALOG TESTS"); //$NON-NLS-1$
        ////////assertEquals(1,isValidAttr.genericResults.failedFeatures.size());
    }

    /*
     * Test method for 'org.locationtech.udig.validation.ValidatePolygonNoGaps.op(Display,
     * Object, IProgressMonitor)'
     */
    /*public void testPolygonNoGapsOp() throws Exception {
        //create features suitable for the test
        GeometryFactory factory=new GeometryFactory();
        // test with 1 closed polygon and 1 open polygon
        Polygon[] polys = new Polygon[2];

        LinearRing ring1 = factory.createLinearRing(new Coordinate[]{
                new Coordinate(10, 10),
                new Coordinate(10, 20),
                new Coordinate(20, 20),
                new Coordinate(20, 10),
                new Coordinate(10, 10),
        });

        //note: we will break this ring when it becomes a feature
        LinearRing ring2 = factory.createLinearRing(new Coordinate[]{
                new Coordinate(30, 10),
                new Coordinate(30, 20),
                new Coordinate(40, 20),
                new Coordinate(40, 10),
                new Coordinate(30, 10),
        });
        
        polys[0]=factory.createPolygon( ring1, new LinearRing[]{});
        polys[1]=factory.createPolygon( ring2, new LinearRing[]{});

        String[] attrValues = new String[2];
        attrValues[0] = "value0"; attrValues[1] = "value1";

        SimpleFeatureType ft=DataUtilities.createType("myPolyType", "*geom:Geometry,name:String"); //$NON-NLS-1$
        ft=DataUtilities.createSubType(ft, null, DefaultEngineeringCRS.CARTESIAN_2D);
        SimpleFeature[] features=new SimpleFeature[2];
        // add lines
        features[0]=SimpleFeatureBuilder.build(ft,new Object[]{polys[0], attrValues[0]}, Integer.toString(0));    
        features[1]=SimpleFeatureBuilder.build(ft,new Object[]{polys[1], attrValues[1]}, Integer.toString(1));    
        //FeatureFactory ff = new FeatureFactory();

        //break the polygon
        System.out.println("x was "+features[1].getDefaultGeometry().getCoordinates()[4].x);
        features[1].getDefaultGeometry().getCoordinates()[4].x = 35; // 30 --> 35
        System.out.println("x is now "+features[1].getDefaultGeometry().getCoordinates()[4].x);
        
        IGeoResource resource = MapTests.createGeoResource(features, true);
        Map map = MapTests.createNonDynamicMapAndRenderer(resource, new Dimension(500,512));
        ValidatePolygonNoGaps isValidPoly = new ValidatePolygonNoGaps();
        isValidPoly.op(Display.getDefault(), map.getLayersInternal().get(0), new NullProgressMonitor());
        System.out.println(isValidPoly.genericResults.failedFeatures.size()+" failed feature(s)");
        assertEquals(1,isValidPoly.genericResults.failedFeatures.size());
        map.sendCommandSync(new AbstractCommand(){

            public void run( IProgressMonitor monitor ) throws Exception {
            }

            public Command copy() {
                return null;
            }

            public String getName() {
                return null;
            }
            
        });//send a sync command so async doesn't give us a false junit failure
        
        Id filter = (Id) map.getLayersInternal().get(0).getFilter();
        String[] fids = filter.getIDs().toArray(new String[0]);
        //System.out.println(fids[0].length()+" features in FID");
        assertEquals(1,fids[0].length()); //only 1 feature failed?
        assertEquals(features[1].getID(),fids[0]); //feature 1 failed?
    }
    */

}
