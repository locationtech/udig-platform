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
package org.locationtech.udig.render.wms.basic.test;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.tests.support.MapTests;
import org.locationtech.udig.render.internal.wms.basic.BasicWMSRenderer2;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.ows.Layer;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Before;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

public class BasicWMSRenderer2Test extends AbstractProjectUITestCase {

    private static final double ACCURACY = 0.0000001;
    private List< ? extends IGeoResource> members;
    private List<Layer> wmsLayers;
    private CoordinateReferenceSystem viewportCRS;
    private Envelope viewportBBox;
    private ReferencedEnvelope viewport;
    private Map map;

    @Before
    public void setUp() throws Exception {
        IService service = WMSRenderMetricsTest.createService(new URL(
                "http://BasicWMSRenderer2Test"), false); //$NON-NLS-1$
        members = service.resources(new NullProgressMonitor());
        map = MapTests.createNonDynamicMapAndRenderer(members.get(0), new Dimension(1024, 1024));
        viewportBBox = new Envelope(-180, 180, -90, 90);
        viewportCRS = DefaultGeographicCRS.WGS84;
        viewport = new ReferencedEnvelope(viewportBBox, viewportCRS);
        wmsLayers = Arrays.asList(members.get(0).resolve(Layer.class, new NullProgressMonitor()));
    }

    @Test
    public void testGetLayersBoundingBox() {
        // TODO
    }

    @Test
    public void testFindRequestCRS() {
        // TODO
    }

    @Test
    public void testCalculateRequestBBox_reprojecting() throws Exception {
        Layer world = new Layer("world"); //$NON-NLS-1$
        world.setBoundingBoxes(WMSRenderMetricsTest.BBOXES3);
        world.setSrs(WMSRenderMetricsTest.BBOXES3.keySet());
        List<Layer> wmsLayers = new ArrayList<Layer>();
        wmsLayers.add(world);

        viewportCRS = CRS.decode("EPSG:3005");
        // arbitrary viewport with bc in top left corner, US in center, s. america on bottom right
        viewportBBox = new Envelope(-737102.342, 7752070.309, -2520851.926, 1758412.392);
        // the request originates in 4326, so a good chunk of the world is requested and reprojected
        viewport = new ReferencedEnvelope(viewportBBox, viewportCRS);
        CoordinateReferenceSystem requestCRS = DefaultGeographicCRS.WGS84;

        // request the bc albers viewport in wgs 84
        ReferencedEnvelope result = BasicWMSRenderer2.calculateRequestBBox(wmsLayers, viewport,
                requestCRS);
        assertEquals("Reprojected MinX wrong", -155.8, result.getMinX(), 0.1);
        assertEquals("Reprojected MaxX wrong", -51.6, result.getMaxX(), 0.1);
        assertEquals("Reprojected MinY wrong", -5.4, result.getMinY(), 0.1);
        assertEquals("Reprojected MaxY wrong", 60.6, result.getMaxY(), 0.1);
    }

    @Test
    public void testCalculateRequestBBox_LayerContained() throws Exception {
        // Test viewport is larger than layer
        ReferencedEnvelope bbox = BasicWMSRenderer2.calculateRequestBBox(wmsLayers, viewport,
                viewportCRS);
        assertEquals(DefaultGeographicCRS.WGS84, bbox.getCoordinateReferenceSystem());
        
        assertEquals(0.0, bbox.getMinX(), ACCURACY);
        assertEquals(0.0, bbox.getMinY(), ACCURACY);
        assertEquals(100.0, bbox.getMaxX(), ACCURACY);
        assertEquals(20.0, bbox.getMaxY(), ACCURACY);
    }

    @Test
    public void testCalculateRequestBBox_ViewportContained() throws Exception {
        Envelope bboxInEnv = new Envelope(10, 40, 5, 15);
        ReferencedEnvelope bboxIn = new ReferencedEnvelope(bboxInEnv, viewportCRS);
        ReferencedEnvelope bbox = BasicWMSRenderer2.calculateRequestBBox(wmsLayers, bboxIn,
                viewportCRS);
        assertEquals(DefaultGeographicCRS.WGS84, bbox.getCoordinateReferenceSystem());
        assertEquals(10.0, bbox.getMinX(), ACCURACY);
        assertEquals(5.0, bbox.getMinY(), ACCURACY);
        assertEquals(40.0, bbox.getMaxX(), ACCURACY);
        assertEquals(15.0, bbox.getMaxY(), ACCURACY);
    }

    @Test
    public void testCalculateImageDimensions() throws Exception {
        // everything in WGS84
        Dimension displaySize = new Dimension(400, 300);
        Dimension maxDimensions = new Dimension(0, 0); // usually not specified
        viewport = new ReferencedEnvelope(viewportBBox, viewportCRS);

        // viewport = world, request = middle 50% W, 50% H
        Envelope request = new Envelope(-90, 90, -45, 45);
        Dimension result = BasicWMSRenderer2.calculateImageDimensions(displaySize, maxDimensions,
                viewport, request);
        // width and height of request is 50% each, so we expect half the pixels in each direction
        // to be requested
        assertEquals("request width is different", 200, result.getWidth(), ACCURACY);
        assertEquals("request height is different", 150, result.getHeight(), ACCURACY);

        // apply a max dim and try again
        maxDimensions = new Dimension(100, 110);
        result = BasicWMSRenderer2.calculateImageDimensions(displaySize, maxDimensions, viewport,
                request);
        assertEquals("request width is different", 100, result.getWidth(), ACCURACY);
        assertEquals("request height is different", 110, result.getHeight(), ACCURACY);

        // viewport = chunk, request = same chunk
        viewportBBox = new Envelope(-135, -115, 49, 55); // 20, 6
        viewport = new ReferencedEnvelope(viewportBBox, viewportCRS);
        maxDimensions = new Dimension(1024, 768); // typical bounds which we won't hit
        result = BasicWMSRenderer2.calculateImageDimensions(displaySize, maxDimensions, viewport,
                viewportBBox);
        assertEquals("request width is different", 400, result.getWidth(), ACCURACY);
        assertEquals("request height is different", 300, result.getHeight(), ACCURACY);
    }

    @Test
    public void testCalculateImageDimensions_reprojecting() throws Exception {
        // everything in BC albers 3005
        viewportCRS = CRS.decode("EPSG:3005");
        // arbitrary viewport with bc in top left corner, US in center, s. america on bottom right
        viewportBBox = new Envelope(-737102.342, 7752070.309, -2520851.926, 1758412.392);
        // the request originates in 4326, so a good chunk of the world is requested and reprojected
        Envelope request = new Envelope(-3333263.569, 10181883.642, -4929356.875, 3697429.13);
        viewport = new ReferencedEnvelope(viewportBBox, viewportCRS);

        Dimension displaySize = new Dimension(1224, 612);
        Dimension maxDimensions = new Dimension(0, 0); // usually not specified
        Dimension result = BasicWMSRenderer2.calculateImageDimensions(displaySize, maxDimensions,
                viewport, request);
        assertEquals("request width is different", 1948, result.getWidth(), ACCURACY);
        assertEquals("request height is different", 1233, result.getHeight(), ACCURACY);
        // we request 1948x1233 pixels and reproject to 1224x612 on the screen
    }

}
