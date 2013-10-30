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
package org.locationtech.udig.project.ui.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.locationtech.udig.AbstractProjectUITestCase;
import org.locationtech.udig.catalog.tests.CatalogTests;
import org.locationtech.udig.mapgraphic.internal.MapGraphicService;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.render.CompositeRenderContext;
import org.locationtech.udig.project.internal.render.RenderContext;
import org.locationtech.udig.project.internal.render.SelectionLayer;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderer;
import org.locationtech.udig.project.tests.support.MapTests;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.ui.WaitCondition;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SynchronizeRendererConfigurationTest extends AbstractProjectUITestCase {

    private Map map;
    private RenderManagerDynamic manager;

    @Before
    public void setUp() throws Exception {
        map=MapTests.createDefaultMap("SynchronizeRendererConfigurationTest", 5, true, null); //$NON-NLS-1$
        map.setRenderManagerInternal(null);
        ApplicationGIS.openMap(map, true);
        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

            public boolean isTrue() {
                return ApplicationGIS.getActiveMap()!=null;
            }
            
        }, true);
        manager=(RenderManagerDynamic) map.getRenderManagerInternal();
    }
    
    @Ignore
    @Test
    public void testRemoveLayerThenAddAnother() throws Exception {
        removeAndAddLayer(1);
        removeAndAddLayer(2);
        Layer layer = MapTests.createLayer(map, MapGraphicService.class,
                "file:/localhost/mapgraphic",//$NON-NLS-1$
                "file:/localhost/mapgraphic#Scalebar", //$NON-NLS-1$
        "layer1");//$NON-NLS-1$
        Layer layer2 = MapTests.createLayer(map, MapGraphicService.class,
                "file:/localhost/mapgraphic",//$NON-NLS-1$
                "file:/localhost/mapgraphic#Scalebar", //$NON-NLS-1$
        "layer2");//$NON-NLS-1$
        map.getLayersInternal().remove(layer);
        map.getLayersInternal().remove(layer2);
        
        removeAndAddLayer(3);
        
    }

    private void removeAndAddLayer(int i) throws Exception {
        Layer removed = map.getLayersInternal().remove(0);
        manager.refresh(null);
        UDIGTestUtil.inDisplayThreadWait(1000, new WaitCondition(){

            public boolean isTrue() {
                return 0==manager.configuration.size();
            }
            
        }, false);
        assertEquals(0,manager.configuration.size());
        assertEquals(0, manager.getRenderers().size());
        
        ILayer newLayer = map.getLayerFactory().createLayer( CatalogTests.createGeoResource("SyncRenderesTestType"+i, i, false) ); //$NON-NLS-1$
        map.getLayersInternal().add((Layer) newLayer);
        manager.refresh(null);
        UDIGTestUtil.inDisplayThreadWait(1000, new WaitCondition(){

            public boolean isTrue() {
                return 2==manager.configuration.size();
            }
            
        }, false);

        assertEquals(2,manager.configuration.size());
        RenderContext addedContext=null;
        for( RenderContext context : manager.configuration ) {
            assertFalse( "Layer has been removed but has managed to get re-instated... what's up?",  //$NON-NLS-1$
                    context.getLayer()==removed );
            if( !(context.getLayer() instanceof SelectionLayer) ){
                assertEquals(newLayer, context.getLayer());
                addedContext=context;
            }
            
        }
        
        List<IRenderer> renderers = manager.getRenderers();
        assertEquals(1, renderers.size());
        for( RenderContext context : manager.configuration ) {
            assertFalse( "Layer has been removed but has managed to get re-instated... what's up?",  //$NON-NLS-1$
                    context.getLayer()==removed );
            if( !(context.getLayer() instanceof SelectionLayer) ){
                assertEquals(addedContext, renderers.get(0).getContext());
            }
        }
    }
    
    @Ignore
    @Test
    public void testReorder() throws Exception {
        map.getLayersInternal().clear();
        
        final Layer layer = MapTests.createLayer(map, MapGraphicService.class,
                "file:/localhost/mapgraphic",//$NON-NLS-1$
                "file:/localhost/mapgraphic#Scalebar", //$NON-NLS-1$
        "layer1");//$NON-NLS-1$
        Layer layer2 = MapTests.createLayer(map, MapGraphicService.class,
                "file:/localhost/mapgraphic",//$NON-NLS-1$
                "file:/localhost/mapgraphic#Scalebar", //$NON-NLS-1$
        "layer2");//$NON-NLS-1$
        final Layer layer3 = MapTests.createLayer(map, MapGraphicService.class,
                "file:/localhost/mapgraphic",//$NON-NLS-1$
                "file:/localhost/mapgraphic#Scalebar", //$NON-NLS-1$
        "layer3");//$NON-NLS-1$
        final Layer layer4 = MapTests.createLayer(map, MapGraphicService.class,
                "file:/localhost/mapgraphic",//$NON-NLS-1$
                "file:/localhost/mapgraphic#Scalebar", //$NON-NLS-1$
        "layer4");//$NON-NLS-1$
        
        map.getLayersInternal().addAll(Arrays.asList(new Layer[]{layer, layer2, layer3, layer4}));
        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

            public boolean isTrue() {
                CompositeRenderContext context = (CompositeRenderContext) map.getRenderManagerInternal().getRenderExecutor().getContext();
                return layer4==context.getContexts().get(0).getLayer();
            }
            
        }, false);
        manager.refresh(null);
        CompositeRenderContext context = (CompositeRenderContext) map.getRenderManagerInternal().getRenderExecutor().getContext();
        
        context = (CompositeRenderContext) context.getContexts().get(0);
        assertEquals(layer, context.getLayer());
        
        Iterator<IRenderContext> children = context.getContexts().iterator();
        
        IRenderContext next = children.next();
        assertEquals(layer, next.getLayer());
        
        next = children.next();
        assertEquals(layer2, next.getLayer());
        
        next = children.next();
        assertEquals(layer3, next.getLayer());
        
        next = children.next();
        assertEquals(layer4, next.getLayer());
        
        map.lowerLayer(layer3);
        
        UDIGTestUtil.inDisplayThreadWait(2000, new WaitCondition(){

            public boolean isTrue() {
                CompositeRenderContext context = (CompositeRenderContext) map.getRenderManagerInternal().getRenderExecutor().getContext();
                context = (CompositeRenderContext) context.getContexts().get(0);
                return layer3==context.getContexts().get(1).getLayer();
            }
            
        }, false);
        manager.refresh(null);
        context = (CompositeRenderContext) map.getRenderManagerInternal().getRenderExecutor().getContext();
        
        context = (CompositeRenderContext) context.getContexts().get(0);
        assertEquals(layer, context.getLayer());
        
        children = context.getContexts().iterator();
        
        next = children.next();
        assertEquals(layer, next.getLayer());
        
        next = children.next();
        assertEquals(layer3, next.getLayer());
        
        next = children.next();
        assertEquals(layer2, next.getLayer());
        
        next = children.next();
        assertEquals(layer4, next.getLayer());
    }
}
