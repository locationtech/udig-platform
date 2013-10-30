/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.render.internal.feature.basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.render.impl.RenderContextImpl;
import org.locationtech.udig.project.tests.support.AbstractProjectTestCase;
import org.locationtech.udig.project.tests.support.MapTests;
import org.locationtech.udig.project.tests.support.TestMapDisplay;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

/**
 * Test BasicFeatureRenderer
 * @author Jesse
 * @since 1.1.0
 */
public class BasicFeatureRendererTest extends AbstractProjectTestCase {
    
    public static final String BC_ALBERS_WKT=
        "PROJCS[\"BC_Albers\",GEOGCS[\"GCS_North_American_1983\"," + //$NON-NLS-1$
            "DATUM[\"North_American_Datum_1983\"," + //$NON-NLS-1$
            "SPHEROID[\"GRS_1980\",6378137,298.257222101]]," + //$NON-NLS-1$
            "PRIMEM[\"Greenwich\",0]," + //$NON-NLS-1$
            "UNIT[\"Degree\",0.017453292519943295]]," + //$NON-NLS-1$
            "PROJECTION[\"Albers\"]," + //$NON-NLS-1$
            "PARAMETER[\"False_Easting\",1000000]," + //$NON-NLS-1$
            "PARAMETER[\"False_Northing\",0]," + //$NON-NLS-1$
            "PARAMETER[\"Central_Meridian\",-126]," + //$NON-NLS-1$
            "PARAMETER[\"Standard_Parallel_1\",50]," + //$NON-NLS-1$
            "PARAMETER[\"Standard_Parallel_2\",58.5]," + //$NON-NLS-1$
            "PARAMETER[\"Latitude_Of_Origin\",45]," + //$NON-NLS-1$
            "UNIT[\"Meter\",1]]"; //$NON-NLS-1$

    //  Accuracy for when the envelopes are being transformed.  The tests are
    //  made so that the accuracy isn't expected to be perfect.
    private static final double ACCURACY = 0.1;
    
    private RenderContextImpl context;

    private void createContext( Map map ) {
        context=new RenderContextImpl();
        context.setGeoResourceInternal(map.getLayersInternal().get(0).getGeoResources().get(0));
        context.setMapInternal(map);
        context.setRenderManagerInternal(map.getRenderManagerInternal());
        context.setLayerInternal(map.getLayersInternal().get(0));
    }

    /**
     * Test method for {@link org.locationtech.udig.render.internal.feature.basic.BasicFeatureRenderer#validateBounds(com.vividsolutions.jts.geom.Envelope, org.eclipse.core.runtime.IProgressMonitor, org.locationtech.udig.project.render.IRenderContext)}.
     */
    @Ignore
    @Test
    public void testValidateBounds() throws Exception {
        Map map = MapTests.createDefaultMap("BasicFeatureRenderer", 4, true, new Dimension(1024,1024)); //$NON-NLS-1$
        map.getViewportModelInternal().setCRS(DefaultGeographicCRS.WGS84);
        
        createContext(map);
        context.getRenderManagerInternal().setMapDisplay(new TestMapDisplay(new Dimension( 1000, 200)));

        map.getViewportModelInternal().setBounds(0,100,0,90);
        
        CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
        Envelope result = BasicFeatureRenderer.validateBounds(new ReferencedEnvelope(0,50,0,10, crs), new NullProgressMonitor(), context);
        
        assertEquals( new Envelope( 0,50,0,10 ), result);

        result = BasicFeatureRenderer.validateBounds(new ReferencedEnvelope( 0,170,0,10, crs), new NullProgressMonitor(), context);
        assertEquals( new ReferencedEnvelope( 0,100,0,10, crs ), result);

        result = BasicFeatureRenderer.validateBounds(new ReferencedEnvelope( 0,300,0,200,crs ), new NullProgressMonitor(), context);
        assertEquals( new Envelope( 0,100,0,90 ), result);

        result = BasicFeatureRenderer.validateBounds(new ReferencedEnvelope( -100,-80,-70,-50, crs ), new NullProgressMonitor(), context);
        assertTrue( result.isNull() );
    }

    @SuppressWarnings("deprecation")
    @Ignore
    @Test
    public void testBC_ALBERS_Viewport() throws Exception {
        CRSFactory fac = (CRSFactory) ReferencingFactoryFinder.getCRSFactories(null).iterator().next();
        CoordinateReferenceSystem crs = fac.createFromWKT(BC_ALBERS_WKT);
        
        GeometryFactory gfac = new GeometryFactory();
        
        double maxx = -120;
        double maxy = 59.9;
        double minx = -125;
        double miny = 50;
        LineString linestring = gfac.createLineString(new Coordinate[]{
                new Coordinate(minx,miny),
                new Coordinate(maxx,maxy)
        });
        
        linestring=(LineString) JTS.transform(linestring, CRS.findMathTransform(DefaultGeographicCRS.WGS84, crs, true));
        
        SimpleFeature[] features = UDIGTestUtil.createTestFeatures("testBC_ALBERS_Viewport", new Geometry[]{ //$NON-NLS-1$
                linestring
        }
        , null, crs);
        
        IGeoResource resource = MapTests.createGeoResource(features, false );
        Map map = MapTests.createNonDynamicMapAndRenderer(resource, new Dimension(1024,1024));
        map.getViewportModelInternal().setCRS(DefaultGeographicCRS.WGS84);
        
        createContext(map);
        
        map.getViewportModelInternal().setBounds(-150,-120,45,65);
        
        Envelope result = BasicFeatureRenderer.validateBounds(new ReferencedEnvelope( minx,maxx,miny,maxy,crs ), new NullProgressMonitor(), context);
        compareEnvelopes( new Envelope( minx,maxx,miny,maxy ), result);
        
        result = BasicFeatureRenderer.validateBounds(new ReferencedEnvelope( minx,maxx,miny,maxy-5, crs), new NullProgressMonitor(), context);
        compareEnvelopes( new Envelope( minx,maxx,miny,maxy-5 ), result);

        result = BasicFeatureRenderer.validateBounds(new ReferencedEnvelope( minx,maxx,miny-20,maxy+20, crs ), new NullProgressMonitor(), context);
        compareEnvelopes( new Envelope( minx,maxx,50,60 ), result);
        
        map.getViewportModelInternal().setBounds(minx-5,minx+5,miny-5,miny+5);

        result = BasicFeatureRenderer.validateBounds(new ReferencedEnvelope( minx,maxx,miny,maxy,crs ), new NullProgressMonitor(), context);
        compareEnvelopes( new Envelope( minx,minx+5,miny,miny+5 ), result);
        
        map.getViewportModelInternal().setBounds(minx+2,minx+4,miny+2,miny+4);

        result = BasicFeatureRenderer.validateBounds(new ReferencedEnvelope( minx,maxx,miny,maxy,crs ), new NullProgressMonitor(), context);
        compareEnvelopes( new Envelope( minx+2,minx+4,miny+2,miny+4 ), result);
        
        result = BasicFeatureRenderer.validateBounds(new ReferencedEnvelope( 0,20,0,20,crs ), new NullProgressMonitor(), context);
        assertTrue( result.isNull() );
    }
    
    @SuppressWarnings("deprecation")
    @Ignore
    @Test
    public void testLayerWithNoBounds() throws Exception {
        SimpleFeature[] features = UDIGTestUtil.createTestFeatures("testNoBounds_Viewport", new Geometry[]{ //$NON-NLS-1$
        }
        , null, DefaultGeographicCRS.WGS84);
        
        IGeoResource resource = MapTests.createGeoResource(features, false );
        Map map = MapTests.createNonDynamicMapAndRenderer(resource, new Dimension(1024,1024));
        map.getViewportModelInternal().setCRS(DefaultGeographicCRS.WGS84);
        
        createContext(map);
        
        map.getViewportModelInternal().setBounds(-150,-120,45,65);
        CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
        
        Envelope result = BasicFeatureRenderer.validateBounds(new ReferencedEnvelope( 0,20,0,20,crs ), new NullProgressMonitor(), context);
        assertTrue( result.isNull() );
    }
    
    
    /**
     * Compares envelopes to verify that they are "close".  THis is because reprojection is involved so
     * they can't be perfect.  Or probably aren't perfect at any rate...
     */
    private void compareEnvelopes( Envelope expected, Envelope result ) {
        assertEquals(expected.getMinX(), result.getMinX(), ACCURACY);
        assertEquals(expected.getMinY(), result.getMinY(), ACCURACY);
        assertEquals(expected.getMaxX(), result.getMaxX(), ACCURACY);
        assertEquals(expected.getMaxY(), result.getMaxY(), ACCURACY);
    }

    @Test
    public void testPoint() throws Exception {
        
    }
    
}
