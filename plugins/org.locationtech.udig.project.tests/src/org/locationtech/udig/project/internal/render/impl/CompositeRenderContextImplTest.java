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
package org.locationtech.udig.project.internal.render.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;

import org.junit.Test;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.render.RenderContext;
import org.locationtech.udig.project.internal.render.RendererCreator;
import org.locationtech.udig.project.internal.render.SelectionLayer;
import org.locationtech.udig.project.testsupport.MapTests;

public class CompositeRenderContextImplTest {

    @Test
    public void testAddRemoveClear() throws Exception {
        Map map = MapTests.createDefaultMap("typename", 2, true, null); //$NON-NLS-1$
        map.getLayersInternal().add(map.getLayerFactory().createLayer(MapTests.createGeoResource("type2", 3, false))); //$NON-NLS-1$
        
        CompositeRendererImpl renderer = (CompositeRendererImpl) map.getRenderManagerInternal().getRenderExecutor().getRenderer();
        renderer.getContext().clear();
        RendererCreator creator=map.getRenderManagerInternal().getRendererCreator();
        
        SortedSet<Layer> layers = creator.getLayers();
        layers.clear();
        layers.addAll(map.getLayersInternal());
        layers.add(new SelectionLayer(map.getLayersInternal().get(0)));
        layers.add(new SelectionLayer(map.getLayersInternal().get(1)));
        
        creator.reset();
        
        CompositeRenderContextImpl comp=new CompositeRenderContextImpl();
        comp.addContexts(creator.getConfiguration());
        
        Iterator iter = comp.getContexts().iterator();  
        
        RenderContext executor = (RenderContext) iter.next();
        assertEquals( map.getLayersInternal().get(0),executor.getLayer() );
        executor = (RenderContext) iter.next();
        assertEquals( map.getLayersInternal().get(1),executor.getLayer() );
        executor = (RenderContext) iter.next();
        SelectionLayer sl = (SelectionLayer) executor.getLayer();
        assertEquals( map.getLayersInternal().get(0),sl.getWrappedLayer() );
        executor = (RenderContext) iter.next();
        sl = (SelectionLayer) executor.getLayer();
        assertEquals( map.getLayersInternal().get(1),sl.getWrappedLayer() );
    }

    @Test
    public void testAssertNoSelfReference() {
        CompositeRenderContextImpl comp=new CompositeRenderContextImpl();
        CompositeRenderContextImpl comp2=new CompositeRenderContextImpl();
        CompositeRenderContextImpl comp3=new CompositeRenderContextImpl();
        comp2.addContexts(Collections.singleton(comp3));
        comp3.addContexts(Collections.singleton(comp));
        assertFalse(CompositeRenderContextImpl.assertNoSelfReference(comp, comp, Collections.singleton(comp2)));
        
        comp2.removeContexts(Collections.singleton(comp3));
        assertTrue(CompositeRenderContextImpl.assertNoSelfReference(comp, comp, Collections.singleton(comp2)));
        
    }

    @Test
    public void testCopy() {
        // TODO
    }

}
