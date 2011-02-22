package net.refractions.udig.project.internal.render.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.render.RenderExecutor;
import net.refractions.udig.project.internal.render.RendererCreator;
import net.refractions.udig.project.internal.render.SelectionLayer;
import net.refractions.udig.project.tests.support.MapTests;
import junit.framework.TestCase;

public class CompositeRendererImplTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testZorder() throws Exception {
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

        renderer.getContext().addContexts(creator.getConfiguration());

        Collection<RenderExecutor> executors = renderer.getRenderExecutors();

        Iterator<RenderExecutor> iter = executors.iterator();

        RenderExecutor executor = iter.next();
        assertEquals( map.getLayersInternal().get(0),executor.getContext().getLayer() );
        executor = iter.next();
        assertEquals( map.getLayersInternal().get(1),executor.getContext().getLayer() );
        executor = iter.next();
        SelectionLayer sl = (SelectionLayer) executor.getContext().getLayer();
        assertEquals( map.getLayersInternal().get(0),sl.getWrappedLayer() );
        executor = iter.next();
        sl = (SelectionLayer) executor.getContext().getLayer();
        assertEquals( map.getLayersInternal().get(1),sl.getWrappedLayer() );

    }
}
