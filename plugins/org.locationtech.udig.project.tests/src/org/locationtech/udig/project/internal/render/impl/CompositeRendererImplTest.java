/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render.impl;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;

import org.junit.Test;
import org.locationtech.udig.catalog.tests.CatalogTests;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.render.RenderExecutor;
import org.locationtech.udig.project.internal.render.RendererCreator;
import org.locationtech.udig.project.internal.render.SelectionLayer;
import org.locationtech.udig.project.tests.support.MapTests;

public class CompositeRendererImplTest {

    @Test
    public void testZorder() throws Exception {
        Map map = MapTests.createDefaultMap("typename", 2, true, null); //$NON-NLS-1$
        map.getLayersInternal().add(map.getLayerFactory()
                .createLayer(CatalogTests.createGeoResource("type2", 3, false))); //$NON-NLS-1$

        CompositeRendererImpl renderer = (CompositeRendererImpl) map.getRenderManagerInternal()
                .getRenderExecutor().getRenderer();
        renderer.getContext().clear();
        RendererCreator creator = map.getRenderManagerInternal().getRendererCreator();

        SortedSet<Layer> layers = creator.getLayers();
        layers.clear();
        layers.addAll(map.getLayersInternal());
        layers.add(new SelectionLayer(map.getLayersInternal().get(0)));
        layers.add(new SelectionLayer(map.getLayersInternal().get(1)));

        creator.reset();

        renderer.getContext().addContexts(creator.getConfiguration());

        Collection<RenderExecutor> executors = renderer.getRenderExecutors();

        Iterator<RenderExecutor> iter = executors.iterator();

        RenderExecutor executor = iter.next();
        assertEquals(map.getLayersInternal().get(0), executor.getContext().getLayer());
        executor = iter.next();
        assertEquals(map.getLayersInternal().get(1), executor.getContext().getLayer());
        executor = iter.next();
        SelectionLayer sl = (SelectionLayer) executor.getContext().getLayer();
        assertEquals(map.getLayersInternal().get(0), sl.getWrappedLayer());
        executor = iter.next();
        sl = (SelectionLayer) executor.getContext().getLayer();
        assertEquals(map.getLayersInternal().get(1), sl.getWrappedLayer());

    }
}
