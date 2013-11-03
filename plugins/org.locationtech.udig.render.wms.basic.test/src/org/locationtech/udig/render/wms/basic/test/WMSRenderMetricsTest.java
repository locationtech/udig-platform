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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.tests.DummyService;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.render.impl.CompositeRenderContextImpl;
import org.locationtech.udig.project.render.AbstractRenderMetrics;
import org.locationtech.udig.project.tests.support.MapTests;
import org.locationtech.udig.render.internal.wms.basic.BasicWMSMetricsFactory2;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.ows.CRSEnvelope;
import org.geotools.data.ows.Layer;
import org.junit.Before;
import org.junit.Test;

public class WMSRenderMetricsTest {

    private static final HashMap<String, CRSEnvelope> BBOXES1;
    static{
        BBOXES1 = new HashMap<String, CRSEnvelope>();
        CRSEnvelope generalEnvelope = new CRSEnvelope("EPSG:4326",0,0, 100,20); //$NON-NLS-1$
        BBOXES1.put("EPSG:4326", generalEnvelope); //$NON-NLS-1$
        CRSEnvelope generalEnvelope2 = new CRSEnvelope("EPSG:3005",1000000,1111000, 1222222,1111222); //$NON-NLS-1$
        BBOXES1.put("EPSG:3005", generalEnvelope2); //$NON-NLS-1$
    }
    private static final HashMap<String, CRSEnvelope> BBOXES2;
    static{
        BBOXES2 = new HashMap<String, CRSEnvelope>();
        CRSEnvelope generalEnvelope = new CRSEnvelope("EPSG:4326",0,0, 20,80); //$NON-NLS-1$
        BBOXES2.put("EPSG:4326", generalEnvelope); //$NON-NLS-1$
        CRSEnvelope generalEnvelope2 = new CRSEnvelope("EPSG:3005",1555000,1555000, 1555222,1555333); //$NON-NLS-1$
        BBOXES2.put("EPSG:3005", generalEnvelope2); //$NON-NLS-1$
    }

    public static final HashMap<String, CRSEnvelope> BBOXES3;
    static{
        BBOXES3 = new HashMap<String, CRSEnvelope>();
        CRSEnvelope generalEnvelope = new CRSEnvelope("EPSG:4326",-180,-90,180,90); //$NON-NLS-1$
        BBOXES3.put("EPSG:4326", generalEnvelope); //$NON-NLS-1$
    }
    
    private Map map;
    private List< ? extends IGeoResource> members;
    private List< ? extends IGeoResource> members2;

    @Before
    public void setUp() throws Exception {
        IService service = createService(new URL("http://serviceWMSRenderMetricsTest"), true); //$NON-NLS-1$
        members = service.resources(new NullProgressMonitor());
        service = createService(new URL("http://serviceWMSRenderMetricsTest2"), true); //$NON-NLS-1$
        members2 = service.resources(new NullProgressMonitor());
        map = MapTests.createNonDynamicMapAndRenderer(members.get(0), null);
        map.getLayersInternal().add(map.getLayerFactory().createLayer(members.get(1)));
    }

    static IService createService(URL string, boolean make2Resources) throws MalformedURLException {
        List<List<Object>> resourceResolveTos=new ArrayList<List<Object>>();
        List<Object> resolves=new ArrayList<Object>();
        Layer layer1 = new Layer("layer1"); //$NON-NLS-1$
        layer1.setBoundingBoxes(BBOXES1);
        layer1.setSrs(BBOXES1.keySet());
        resolves.add(layer1);
        resourceResolveTos.add(resolves);
        List<Object> resolves2=new ArrayList<Object>();
        Layer layer2 = new Layer("layer2"); //$NON-NLS-1$
        layer2.setBoundingBoxes(BBOXES2);
        layer2.setSrs(BBOXES2.keySet());
        resolves.add(layer2);
        resourceResolveTos.add(resolves);
        if( make2Resources )
            resourceResolveTos.add(resolves2);
        IService service = DummyService.createService(string, null, resourceResolveTos);
        return service;
    }

    @Test
    public void testCanAddLayer() throws Throwable {
        BasicWMSMetricsFactory2 fac=new BasicWMSMetricsFactory2();
        CompositeRenderContextImpl comp = createCompositeRenderer();
        AbstractRenderMetrics metrics = fac.createMetrics(comp);
        assertTrue(metrics.canAddLayer(map.getLayersInternal().get(1)));
        org.locationtech.udig.project.internal.Layer layer = map.getLayerFactory().createLayer(members2.get(0));
        assertFalse(metrics.canAddLayer(layer));
    }

    private CompositeRenderContextImpl createCompositeRenderer() {
        CompositeRenderContextImpl comp=new CompositeRenderContextImpl();
        comp.setGeoResourceInternal(members.get(0));
        comp.setLayerInternal(map.getLayersInternal().get(0));
        comp.setMapInternal(map);
        comp.setRenderManagerInternal(map.getRenderManagerInternal());
        return comp;
    }

    @Test
    public void testCanStyle() {
         // TODO
    }

}

