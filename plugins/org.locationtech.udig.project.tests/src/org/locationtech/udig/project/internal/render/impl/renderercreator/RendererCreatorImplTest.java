/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render.impl.renderercreator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.tests.CatalogTests;
import org.locationtech.udig.catalog.tests.DummyService;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.project.internal.impl.LayerImpl;
import org.locationtech.udig.project.internal.render.CompositeRenderContext;
import org.locationtech.udig.project.internal.render.RenderContext;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.internal.render.RendererCreator;
import org.locationtech.udig.project.internal.render.SelectionLayer;
import org.locationtech.udig.project.internal.render.impl.PlaceHolder;
import org.locationtech.udig.project.internal.render.impl.RendererCreatorImpl;
import org.locationtech.udig.project.render.IRenderer;
import org.locationtech.udig.project.tests.support.AbstractProjectTestCase;
import org.locationtech.udig.project.tests.support.MapTests;
import org.locationtech.udig.project.tests.support.TestFeatureStore;

/**
 * Tests the renderer creator implementation.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class RendererCreatorImplTest extends AbstractProjectTestCase {

    /**
     * Test method for
     * {@link org.locationtech.udig.project.internal.render.impl.RendererCreatorImpl#getAvailableRenderersInfo(org.locationtech.udig.project.internal.Layer)}.
     */
    @Test
    public void testGetAvailableRenderersInfo() {
        // TODO implement test
    }

    @Test
    public void testGetLayers() throws Exception {
        Map map = MapTests.createDefaultMap("typename", 2, true, null); //$NON-NLS-1$
        map.getLayersInternal().add(map.getLayerFactory()
                .createLayer(CatalogTests.createGeoResource("type2", 3, false))); //$NON-NLS-1$

        RendererCreatorImpl creator = MapTests.createRendererCreator(map);

        SortedSet<Layer> layers = creator.getLayers();
        layers.clear();
        layers.addAll(map.getLayersInternal());
        layers.add(new SelectionLayer(map.getLayersInternal().get(0)));
        layers.add(new SelectionLayer(map.getLayersInternal().get(1)));

        Iterator<Layer> iter = layers.iterator();

        Layer layer = iter.next();
        assertEquals(map.getLayersInternal().get(0), layer);
        layer = iter.next();
        assertEquals(map.getLayersInternal().get(1), layer);
        SelectionLayer sl = (SelectionLayer) iter.next();
        assertEquals(map.getLayersInternal().get(0), sl.getWrappedLayer());
        sl = (SelectionLayer) iter.next();
        assertEquals(map.getLayersInternal().get(1), sl.getWrappedLayer());

    }

    /**
     * Test method for
     * {@link org.locationtech.udig.project.internal.render.impl.RendererCreatorImpl#getRenderer(org.locationtech.udig.project.internal.render.RenderContext)}.
     *
     * @throws Exception
     */
    @Ignore
    @Test
    public void testGetRenderer() throws Exception {
        LayerImpl layer = MapTests.createLayer(null, new RendererCreatorTestObjForSingleRenderer(),
                null);

        RendererCreatorImpl creator = MapTests.createRendererCreator(layer.getMapInternal());
        creator.getLayers().add(layer);

        creator.reset();
        RenderContext context = creator.getConfiguration().iterator().next();
        Renderer renderer = creator.getRenderer(context);
        assertTrue(renderer instanceof SingleRenderer);

        // now test case where there is no good renderer
        layer = MapTests.createLayer(null, "HELLO", null); //$NON-NLS-1$

        creator = MapTests.createRendererCreator(layer.getMapInternal());
        creator.getLayers().add(layer);

        creator.reset();

        context = creator.getConfiguration().iterator().next();
        renderer = creator.getRenderer(context);
        assertTrue(renderer instanceof PlaceHolder);
        assertEquals(context, renderer.getContext());

    }

    /**
     * Test method for
     * {@link org.locationtech.udig.project.internal.render.impl.RendererCreatorImpl#getRenderContext(org.locationtech.udig.project.internal.Layer)}.
     */
    @Test
    public void testGetRenderContext() {
        // TODO implement test
    }

    /**
     * Test method for
     * {@link org.locationtech.udig.project.internal.render.impl.RendererCreatorImpl#changed(org.eclipse.emf.common.notify.Notification)}.
     *
     * @throws Exception
     */
    @Test
    public void testChanged() throws Exception {

        LayerImpl layer = MapTests.createLayer(null, new TestFeatureStore(), null);

        RendererCreatorImpl creator = MapTests.createRendererCreator(layer.getMapInternal());

        NotificationImpl notificationImpl = new ENotificationImpl(
                (InternalEObject) layer.getContextModel(), Notification.ADD,
                ProjectPackage.CONTEXT_MODEL__LAYERS, null, layer);
        creator.changed(notificationImpl);

        SortedSet<Layer> layers = creator.getLayers();

        assertEquals(2, layers.size());

        Iterator<Layer> iter = layers.iterator();
        boolean selectionLayerFound = false;
        while (iter.hasNext()) {
            Layer next = iter.next();
            if (next instanceof SelectionLayer) {
                SelectionLayer sl = (SelectionLayer) next;
                selectionLayerFound = true;
                assertEquals(layer, sl.getWrappedLayer());
            } else {
                assertEquals(layer, next);
            }
        }
        assertTrue(selectionLayerFound);

        ContextModelListener listener = new ContextModelListener();
        layer.getContextModel().eAdapters().add(listener);

        layer.getMapInternal().getLayersInternal().remove(layer);
        creator.changed(listener.lastNotification);

        layers = creator.getLayers();

        assertEquals(0, layers.size());
    }

    /**
     * Test method for
     * {@link org.locationtech.udig.project.internal.render.impl.RendererCreatorImpl#findSelectionLayer(org.locationtech.udig.project.ILayer)}.
     *
     * @throws Exception
     */
    @Test
    public void testFindSelectionLayer() throws Exception {
        LayerImpl layer = MapTests.createLayer(null, new TestFeatureStore(), null);
        Map map = layer.getMapInternal();
        LayerImpl layer2 = MapTests.createLayer(null, new TestFeatureStore(), map);

        RendererCreatorImpl creator = MapTests.createRendererCreator(map);

        NotificationImpl event = new ENotificationImpl((InternalEObject) layer.getContextModel(),
                Notification.ADD, ProjectPackage.CONTEXT_MODEL__LAYERS, null, layer);

        creator.changed(event);
        event = new ENotificationImpl((InternalEObject) layer.getContextModel(), Notification.ADD,
                ProjectPackage.CONTEXT_MODEL__LAYERS, null, layer2);

        creator.changed(event);
        assertEquals(layer, creator.findSelectionLayer(layer).getWrappedLayer());
        assertEquals(layer2, creator.findSelectionLayer(layer2).getWrappedLayer());

    }

    /**
     * Test method for
     * {@link org.locationtech.udig.project.internal.render.impl.RendererCreatorImpl#getConfiguration()}.
     */
    @Ignore
    @Test
    public void testGetConfigurationSingleLayer() throws Throwable {
        LayerImpl layer = MapTests.createLayer(null, new RendererCreatorTestObjForSingleRenderer(),
                null);

        RendererCreatorImpl creator = MapTests.createRendererCreator(layer.getMapInternal());

        NotificationImpl notificationImpl = new ENotificationImpl(
                (InternalEObject) layer.getContextModel(), Notification.ADD,
                ProjectPackage.CONTEXT_MODEL__LAYERS, null, layer);
        creator.changed(notificationImpl);
        RenderContext context = creator.getConfiguration().iterator().next();

        assertEquals(1, creator.getLayers().size());
        Renderer renderer = creator.getRenderer(context);
        assertTrue("Expected SingleRenderer but was " + renderer, //$NON-NLS-1$
                renderer instanceof SingleRenderer);
        assertEquals(1, creator.getConfiguration().size());

        layer = MapTests.createLayer(null, new RendererCreatorTestObjForSingleRenderer(),
                layer.getMapInternal());
        notificationImpl = new ENotificationImpl((InternalEObject) layer.getContextModel(),
                Notification.ADD, ProjectPackage.CONTEXT_MODEL__LAYERS, null, layer);
        creator.changed(notificationImpl);

        assertEquals(2, creator.getLayers().size());
        assertEquals(2, creator.getConfiguration().size());
        for (RenderContext context2 : creator.getConfiguration()) {
            renderer = creator.getRenderer(context2);
            assertTrue("Expected SingleRenderer but was " + renderer, //$NON-NLS-1$
                    renderer instanceof SingleRenderer);
        }

        notificationImpl = new ENotificationImpl((InternalEObject) layer.getContextModel(),
                Notification.REMOVE, ProjectPackage.CONTEXT_MODEL__LAYERS, layer, null);
        creator.changed(notificationImpl);
        context = creator.getConfiguration().iterator().next();

        assertEquals(1, creator.getLayers().size());
        renderer = creator.getRenderer(context);
        assertTrue("Expected SingleRenderer but was " + renderer, //$NON-NLS-1$
                renderer instanceof SingleRenderer);
        assertEquals(1, creator.getConfiguration().size());
    }

    @Test
    public void testGetConfigurationMultiLayerRenderer() throws Throwable {
        LayerImpl layer = MapTests.createLayer(null, new RendererCreatorTestObjForMulitRenderer(),
                null);
        Map map = layer.getMapInternal();
        ContextModelListener listener = new ContextModelListener();
        layer.getContextModel().eAdapters().add(listener);

        RendererCreatorImpl creator = MapTests.createRendererCreator(layer.getMapInternal());

        // Listener added after first layer was added so make notification by hand
        NotificationImpl notificationImpl = new ENotificationImpl(
                (InternalEObject) layer.getContextModel(), Notification.ADD,
                ProjectPackage.CONTEXT_MODEL__LAYERS, null, layer);
        creator.changed(notificationImpl);
        RenderContext context = creator.getConfiguration().iterator().next();

        assertEquals(1, creator.getLayers().size());
        Renderer renderer = creator.getRenderer(context);
        assertSame(context, renderer.getContext());
        assertTrue("Expected MultiLayerRenderer but was " + renderer, //$NON-NLS-1$
                renderer instanceof MultiLayerRenderer);
        assertEquals(1, creator.getConfiguration().size());

        layer = MapTests.createLayer(new URL("http://multi_dummy"), //$NON-NLS-1$
                new RendererCreatorTestObjForMulitRenderer(), layer.getMapInternal());
        creator.changed(listener.lastNotification);

        assertEquals(2, creator.getLayers().size());
        assertEquals(1, creator.getConfiguration().size());
        context = creator.getConfiguration().iterator().next();
        renderer = creator.getRenderer(context);
        assertSame(context, renderer.getContext());
        CompositeRenderContext compositeRenderContext = ((CompositeRenderContext) creator
                .getConfiguration().iterator().next());
        assertEquals(2, compositeRenderContext.getContexts().size());

        assertTrue("Expected MultiRenderer but was " + renderer, //$NON-NLS-1$
                renderer instanceof MultiLayerRenderer);

        layer = MapTests.createLayer(new URL("http://othername"), //$NON-NLS-1$
                new RendererCreatorTestObjForMulitRenderer(), layer.getMapInternal());
        creator.changed(listener.lastNotification);

        assertEquals(3, creator.getLayers().size());
        assertEquals(2, creator.getConfiguration().size());
        for (RenderContext context2 : creator.getConfiguration()) {
            renderer = creator.getRenderer(context2);
            assertTrue("Expected SingleRenderer but was " + renderer, //$NON-NLS-1$
                    renderer instanceof MultiLayerRenderer);
        }

        map.lowerLayer(layer);

        int indexOfOtherName = map.getMapLayers().indexOf(layer);

        creator.changed(listener.lastNotification);

        assertEquals(3, creator.getLayers().size());
        assertEquals(3, creator.getConfiguration().size());
        for (RenderContext context2 : creator.getConfiguration()) {
            renderer = creator.getRenderer(context2);
            assertTrue("Expected SingleRenderer but was " + renderer, //$NON-NLS-1$
                    renderer instanceof MultiLayerRenderer);
        }

        layer = MapTests.createLayer(new URL("http://dummy"), //$NON-NLS-1$
                new RendererCreatorTestObjForMulitRenderer(), null);
        map.getLayersInternal().set(indexOfOtherName, layer);
        creator.changed(listener.lastNotification);

        assertEquals(3, creator.getLayers().size());
        assertEquals(1, creator.getConfiguration().size());
        renderer = creator.getRenderer(creator.getConfiguration().iterator().next());
        compositeRenderContext = ((CompositeRenderContext) creator.getConfiguration().iterator()
                .next());
        assertEquals(3, compositeRenderContext.getContexts().size());

        map.getLayersInternal().remove(0);
        creator.changed(listener.lastNotification);
        assertEquals(2, creator.getLayers().size());
        assertEquals(1, creator.getConfiguration().size());
        renderer = creator.getRenderer(creator.getConfiguration().iterator().next());
        compositeRenderContext = ((CompositeRenderContext) creator.getConfiguration().iterator()
                .next());
        assertEquals(2, compositeRenderContext.getContexts().size());

    }

    @Ignore
    @Test
    public void testGetConfigurationMixedLayers() throws Exception {
        LayerImpl layer = MapTests.createLayer(null, new RendererCreatorTestObjForSingleRenderer(),
                null);

        RendererCreatorImpl creator = MapTests.createRendererCreator(layer.getMapInternal());

        LayerImpl layer2 = MapTests.createLayer(null, new RendererCreatorTestObjForMulitRenderer(),
                layer.getMapInternal());
        LayerImpl layer3 = MapTests.createLayer(null, new RendererCreatorTestObjForMulitRenderer(),
                layer.getMapInternal());
        LayerImpl layer4 = MapTests.createLayer(null, new RendererCreatorTestObjForSingleRenderer(),
                layer.getMapInternal());

        NotificationImpl notificationImpl = new ENotificationImpl(
                (InternalEObject) layer.getContextModel(), Notification.ADD,
                ProjectPackage.CONTEXT_MODEL__LAYERS, null, layer);
        creator.changed(notificationImpl);

        creator.reset();
        Collection<RenderContext> config = creator.getConfiguration();

        assertEquals(1, config.size());

        Iterator<RenderContext> iter = config.iterator();

        RenderContext context = iter.next();
        assertEquals(layer, context.getLayer());

        notificationImpl = new ENotificationImpl((InternalEObject) layer.getContextModel(),
                Notification.ADD_MANY, ProjectPackage.CONTEXT_MODEL__LAYERS, null,
                Arrays.asList(new Layer[] { layer2, layer3, layer4 }));
        creator.changed(notificationImpl);
        creator.reset();
        config = creator.getConfiguration();

        iter = config.iterator();

        context = iter.next();
        assertEquals(layer, context.getLayer());

        assertEquals(3, config.size());

        CompositeRenderContext compContext = (CompositeRenderContext) iter.next();
        assertEquals(layer2, compContext.getContexts().get(0).getLayer());
        assertEquals(layer3, compContext.getContexts().get(1).getLayer());
        context = iter.next();
        assertEquals(layer4, context.getLayer());
    }

    /**
     * 2 wms layer not always added together add 1 wms layer to map add a shapefile below wms layer
     * add a 2nd wms layer below shp move top layer below 2nd wms layer. still 2 wms renderer but
     * only 1 renders move bottom above.... now they are together
     *
     * @throws Exception
     */
    @Ignore
    @Test
    public void testBugWithMultiLayerRenderers() throws Exception {
        LayerImpl layer = MapTests.createLayer(null, new RendererCreatorTestObjForMulitRenderer(),
                null);
        layer.setName("layer1"); //$NON-NLS-1$
        RendererCreatorImpl creator = MapTests.createRendererCreator(layer.getMapInternal());

        LayerImpl layer2 = MapTests.createLayer(null, new RendererCreatorTestObjForSingleRenderer(),
                layer.getMapInternal());
        LayerImpl layer3 = MapTests.createLayer(null, new RendererCreatorTestObjForMulitRenderer(),
                layer.getMapInternal());
        layer2.setName("layer2"); //$NON-NLS-1$
        layer3.setName("layer3"); //$NON-NLS-1$

        creator.getLayers().addAll(Arrays.asList(new Layer[] { layer, layer2, layer3 }));

        creator.reset();

        layer3.setZorder(0);
        NotificationImpl notificationImpl = new ENotificationImpl(
                (InternalEObject) layer.getContextModel(), Notification.MOVE,
                ProjectPackage.CONTEXT_MODEL__LAYERS, null, layer3, 0);
        creator.changed(notificationImpl);

        Collection<RenderContext> config = creator.getConfiguration();

        assertEquals(3, creator.getLayers().size());
        assertEquals(2, config.size());
    }

    @Ignore
    @Test
    public void testBlackboardPreferred() throws Exception {
        LayerImpl layer = MapTests.createLayer(null, new RendererCreatorTestObjForSingleRenderer(),
                null);
        layer.setName("Layer1"); //$NON-NLS-1$
        Map map = layer.getMapInternal();
        LayerImpl layer2 = MapTests.createLayer(null, new RendererCreatorTestObjForSingleRenderer(),
                map);
        layer2.setName("Layer2"); //$NON-NLS-1$
        RendererCreatorImpl creator = MapTests.createRendererCreator(layer.getMapInternal());

        layer.getBlackboard().clear();
        layer2.getBlackboard().clear();
        map.getBlackboard().clear();

        NotificationImpl notificationImpl = new ENotificationImpl(
                (InternalEObject) layer.getContextModel(), Notification.ADD_MANY,
                ProjectPackage.CONTEXT_MODEL__LAYERS, null,
                Arrays.asList(new Layer[] { layer, layer2 }));
        creator.changed(notificationImpl);

        sameRenderer(creator, SingleRenderer.class, 2);

        layer.getBlackboard().put(RendererCreator.PREFERRED_RENDERER_ID,
                "org.locationtech.udig.project.tests.single2"); //$NON-NLS-1$

        firstLayerR2SecondR1(creator);

        // now both layers have it so both should use renderer2
        layer2.getBlackboard().put(RendererCreator.PREFERRED_RENDERER_ID,
                "org.locationtech.udig.project.tests.single2"); //$NON-NLS-1$

        sameRenderer(creator, SingleRenderer2.class, 2);

        map.getBlackboard().put(RendererCreator.PREFERRED_RENDERER_ID,
                "org.locationtech.udig.project.tests.single2"); //$NON-NLS-1$

        sameRenderer(creator, SingleRenderer2.class, 2);

        layer2.getBlackboard().clear();

        sameRenderer(creator, SingleRenderer2.class, 2);

        layer2.getBlackboard().put(RendererCreator.PREFERRED_RENDERER_ID,
                "org.locationtech.udig.project.tests.single"); //$NON-NLS-1$

        // map has renderer2 but layer2 has renderer1 so it should be renderer1 on layer2.
        // layer1 still has renderer2 on it.
        firstLayerR2SecondR1(creator);

        layer.getBlackboard().clear();

        // Now layer1 is clear but map dictates that Renderer2 is the important one.
        firstLayerR2SecondR1(creator);
    }

    @Ignore
    @Test
    public void testBlackBoardLastResort() throws Exception {
        LayerImpl layer = MapTests.createLayer(null, new RendererCreatorTestObjForSingleRenderer(),
                null);
        layer.setName("Layer1"); //$NON-NLS-1$
        Map map = layer.getMapInternal();
        LayerImpl layer2 = MapTests.createLayer(null, new RendererCreatorTestObjForSingleRenderer(),
                map);
        layer2.setName("Layer2"); //$NON-NLS-1$
        RendererCreatorImpl creator = MapTests.createRendererCreator(layer.getMapInternal());

        layer.getBlackboard().clear();
        layer2.getBlackboard().clear();
        map.getBlackboard().clear();

        NotificationImpl notificationImpl = new ENotificationImpl(
                (InternalEObject) layer.getContextModel(), Notification.ADD_MANY,
                ProjectPackage.CONTEXT_MODEL__LAYERS, null,
                Arrays.asList(new Layer[] { layer, layer2 }));
        creator.changed(notificationImpl);

        sameRenderer(creator, SingleRenderer.class, 2);

        layer.getBlackboard().put(RendererCreator.LAST_RESORT_RENDERER_ID,
                "org.locationtech.udig.project.tests.single"); //$NON-NLS-1$

        firstLayerR2SecondR1(creator);

        // now both layers have it so both should use renderer2
        layer2.getBlackboard().put(RendererCreator.LAST_RESORT_RENDERER_ID,
                "org.locationtech.udig.project.tests.single"); //$NON-NLS-1$

        sameRenderer(creator, SingleRenderer2.class, 2);

        map.getBlackboard().put(RendererCreator.LAST_RESORT_RENDERER_ID,
                "org.locationtech.udig.project.tests.single"); //$NON-NLS-1$

        sameRenderer(creator, SingleRenderer2.class, 2);

        layer2.getBlackboard().clear();

        sameRenderer(creator, SingleRenderer2.class, 2);

        layer2.getBlackboard().put(RendererCreator.LAST_RESORT_RENDERER_ID,
                "org.locationtech.udig.project.tests.single2"); //$NON-NLS-1$

        // map has renderer2 but layer2 has renderer1 so it should be renderer1 on layer2.
        // layer1 still has renderer2 on it.
        firstLayerR2SecondR1(creator);

        layer.getBlackboard().clear();

        // Now layer1 is clear but map dictates that Renderer2 is the important one.
        firstLayerR2SecondR1(creator);
    }

    private void sameRenderer(RendererCreatorImpl creator, Class<? extends IRenderer> rendererClass,
            int expectedContexts) {
        Collection<RenderContext> configuration;
        Renderer renderer;
        creator.reset();
        configuration = creator.getConfiguration();

        assertEquals(expectedContexts, configuration.size());

        // map has it but layer2 doesn't yet since map has the renderer2 both should still use
        // renderer2.
        for (RenderContext context : configuration) {
            renderer = creator.getRenderer(context);
            assertTrue("Expected SingleRenderer but was " + renderer, //$NON-NLS-1$
                    rendererClass.isAssignableFrom(renderer.getClass()));
        }
    }

    private void firstLayerR2SecondR1(RendererCreatorImpl creator) {
        Collection<RenderContext> configuration;
        Iterator<RenderContext> iter;
        Renderer renderer;
        creator.reset();
        configuration = creator.getConfiguration();
        assertEquals(2, configuration.size());
        iter = configuration.iterator();

        renderer = creator.getRenderer(iter.next());
        assertTrue("Expected SingleRenderer but was " + renderer, //$NON-NLS-1$
                renderer instanceof SingleRenderer2);
        renderer = creator.getRenderer(iter.next());
        assertTrue("Expected SingleRenderer but was " + renderer, //$NON-NLS-1$
                renderer instanceof SingleRenderer);
        assertFalse(iter.hasNext());
    }

    @Test
    public void testPreference() throws Exception {
        // TODO
    }

    @Ignore
    @Test
    public void testSwitchStyle() throws Exception {
        LayerImpl layer = MapTests.createLayer(null, new RendererCreatorTestObjForSingleRenderer(),
                null);
        layer.setName("Layer1"); //$NON-NLS-1$
        IService createService = DummyService.createService(new URL("http://dummy77"), //$NON-NLS-1$
                Collections.emptyList(), Collections.singletonList(Arrays
                        .asList(new Object[] { new RendererCreatorTestObjForMulitRenderer() })));
        layer.getGeoResources().add(createService.resources(null).get(0));

        StyleBlackboard bb = layer.getStyleBlackboard();

        bb.put(SingleRendererStyleContent.ID, new SingleRendererStyleContent());

        RendererCreatorImpl creator = MapTests.createRendererCreator(layer.getMapInternal());

        NotificationImpl notificationImpl = new ENotificationImpl(
                (InternalEObject) layer.getContextModel(), Notification.ADD,
                ProjectPackage.CONTEXT_MODEL__LAYERS, null, layer);
        creator.changed(notificationImpl);

        sameRenderer(creator, SingleRenderer.class, 1);

        bb.clear();
        bb.put(MultiRendererStyleContent.ID, new MultiRendererStyleContent());

        sameRenderer(creator, MultiLayerRenderer.class, 1);

        bb.put(SingleRendererStyleContent.ID, new SingleRendererStyleContent());
        bb.setSelected(new String[] { SingleRendererStyleContent.ID });

        sameRenderer(creator, SingleRenderer.class, 1);

        bb.setSelected(new String[] { MultiRendererStyleContent.ID });

        sameRenderer(creator, MultiLayerRenderer.class, 1);
    }

    @Test
    public void testBadMetrics() throws Exception {
        LayerImpl layer = MapTests.createLayer(null, BadRenderMetricsFactory.ALWAYS_EXCEPTION,
                null);

        RendererCreatorImpl creator = MapTests.createRendererCreator(layer.getMapInternal());
        creator.getLayers().add(layer);

        Collection<RenderContext> configuration = creator.getConfiguration();

        assertEquals(0, configuration.size());

        layer = MapTests.createLayer(null, BadRenderMetricsFactory.CAN_RENDER_NO_EXCEPTION, null);

        creator.getLayers().clear();
        creator.getLayers().add(layer);

        creator.reset();
        configuration = creator.getConfiguration();

        assertEquals(1, configuration.size());
        assertTrue(creator.getRenderer(configuration.iterator().next()) instanceof PlaceHolder);

        layer = MapTests.createLayer(null, BadRenderMetricsFactory.CAN_CREATE_METRICS, null);

        creator.getLayers().clear();
        creator.getLayers().add(layer);

        creator.reset();
        configuration = creator.getConfiguration();

        assertEquals(1, configuration.size());
        assertTrue(creator.getRenderer(configuration.iterator().next()) instanceof PlaceHolder);

        layer = MapTests.createLayer(null, BadRenderMetricsFactory.CAN_ADD_LAYER_EXCEPTION, null);

        creator.getLayers().clear();
        layer.setName("layer1"); //$NON-NLS-1$
        creator.getLayers().add(layer);
        layer = MapTests.createLayer(null, BadRenderMetricsFactory.CAN_ADD_LAYER_EXCEPTION,
                layer.getMapInternal());
        layer.setName("layer2"); //$NON-NLS-1$
        creator.getLayers().add(layer);

        creator.reset();
        configuration = creator.getConfiguration();

        assertEquals(2, configuration.size());
        Iterator<RenderContext> iter = configuration.iterator();
        assertFalse(creator.getRenderer(iter.next()) instanceof PlaceHolder);
        assertFalse(creator.getRenderer(iter.next()) instanceof PlaceHolder);

    }

    @Test
    public void testResourceChanged() throws Exception {
        // TODO
    }

    private class ContextModelListener extends AdapterImpl {
        Notification lastNotification;

        @Override
        public void notifyChanged(Notification msg) {
            lastNotification = msg;
        }
    }

}
