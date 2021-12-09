/**
 * <copyright></copyright> $Id$
 */
package org.locationtech.udig.project.internal.render.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.emf.common.notify.Notification;
import org.geotools.data.FeatureSource;
import org.locationtech.udig.core.internal.ExtensionPointUtil;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.ContextModel;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.impl.IEListVisitor;
import org.locationtech.udig.project.internal.impl.SynchronizedEObjectWithInverseResolvingEList;
import org.locationtech.udig.project.internal.render.CompositeRenderContext;
import org.locationtech.udig.project.internal.render.RenderContext;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.internal.render.RendererCreator;
import org.locationtech.udig.project.internal.render.SelectionLayer;
import org.locationtech.udig.project.internal.render.impl.InternalRenderMetricsFactory.InternalRenderMetrics;
import org.locationtech.udig.project.render.AbstractRenderMetrics;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderMetricsFactory;
import org.locationtech.udig.project.render.IRenderer;

/**
 * Default implementation
 *
 * @author Jesse
 * @since 1.0.0
 * @generated
 */
public class RendererCreatorImpl implements RendererCreator {

    /**
     * The cached value of the '{@link #getContext() <em>Context</em>}' reference.
     *
     * @see #getContext()
     * @generated NOT
     */
    protected volatile RenderContext context = null;

    /**
     * The cached value of the '{@link #getLayers() <em>Layers</em>}' reference list.
     *
     * @see #getLayers()
     */
    protected final SortedSet<Layer> layers = Collections.synchronizedSortedSet(new TreeSet<Layer>());

    /**
     * @uml.property name="configuration"
     * @uml.associationEnd qualifier="key:java.lang.Object
     *                     org.locationtech.udig.project.internal.render.RenderContext"
     */
    private volatile Map<Layer, RenderContext> configuration;

    public RendererCreatorImpl() {
        super();
    }

    @Override
    public RenderContext getContext() {
        return context;
    }

    @Override
    public void setContext(RenderContext newContext) {
        context = newContext;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @uml.property name="layers"
     * @generated NOT
     */
    @Override
    public SortedSet<Layer> getLayers() {
        return layers;
    }

    /**
     * <code>MetricsMap</code> maintains a list of metrics by Layer.
     *
     * @uml.property name="metrics"
     * @uml.associationEnd qualifier="key:java.lang.Object java.util.SortedSet<IRenderMetrics>"
     */
    Map<Layer, List<InternalRenderMetrics>> layerToMetricsFactoryMap = new HashMap<>();

    @Override
    public Map<String, String> getAvailableRenderersInfo(Layer layer) {
        Map<String, String> renderers = new HashMap<>();

        List<InternalRenderMetrics> availableRenderers = layerToMetricsFactoryMap.get(layer);

        for (InternalRenderMetrics irm : availableRenderers) {
            renderers.put(irm.getName(), irm.getDescription());
        }

        return renderers;
    }

    /**
     * returns a list of available metrics Ids by Layer.
     *
     * @param layer
     * @return
     */
    @Override
    public Collection<String> getAvailableRendererIds(Layer layer) {
        List<String> idsList = new ArrayList<>();

        List<InternalRenderMetrics> availableRenderers = layerToMetricsFactoryMap.get(layer);

        for (InternalRenderMetrics irm : availableRenderers) {
            idsList.add(irm.getId());
        }

        return idsList;
    }

    @Override
    public Collection<AbstractRenderMetrics> getAvailableRendererMetrics(Layer layer) {
        List<AbstractRenderMetrics> metrics = new ArrayList<>();

        List<InternalRenderMetrics> availableRenderers = layerToMetricsFactoryMap.get(layer);

        if (availableRenderers == null) {
            createConfiguration();
            availableRenderers = layerToMetricsFactoryMap.get(layer);
            if (availableRenderers == null) {
                return Collections.emptyList();
            }
        }
        for (InternalRenderMetrics internalRenderMetrics : availableRenderers) {
            IRenderContext renderContext = internalRenderMetrics.getRenderContext();
            IRenderMetricsFactory renderMetricsFactory = internalRenderMetrics.getRenderMetricsFactory();
            AbstractRenderMetrics createMetrics = renderMetricsFactory.createMetrics(renderContext);
            // set the id since initially it is set only in InternalRenderMetrics
            createMetrics.setId(internalRenderMetrics.getId());
            metrics.add(createMetrics);
        }
        return metrics;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    public Renderer getRenderer(RenderContext context) {

        // Part 1 of decision goes here
        Object o = layerToMetricsFactoryMap.get(context.getLayer());
        if (o == null) {
            createConfiguration(); // carefully creates the configuration entry for the layer
        }
        List<InternalRenderMetrics> list = layerToMetricsFactoryMap.get(context.getLayerInternal());
        if (list.isEmpty()) {
            return getPlaceHolder(context); // layer won't be rendered
        }
        InternalRenderMetrics internalRenderMetrics = null;
        for (Iterator<InternalRenderMetrics> iter = list.iterator(); iter.hasNext()
                && internalRenderMetrics == null;) {
            internalRenderMetrics = iter.next();
            boolean canRender;
            try {
                canRender = internalRenderMetrics.getRenderMetricsFactory().canRender(context);
                if (canRender) {
                    Renderer createRenderer = internalRenderMetrics.createRenderer();
                    createRenderer.setContext(context);
                    return createRenderer;
                }
            } catch (Throwable e) {
                internalRenderMetrics = null;
            }
        }
        return getPlaceHolder(context);
    }

    private PlaceHolder getPlaceHolder(RenderContext context) {
        PlaceHolder placeHolder = new PlaceHolder();
        placeHolder.setContext(context);
        return placeHolder;
    }

    /**
     * Carefully creates the configuration in a threadsafe manner.
     * <p>
     * This method has the side effect of setting a Layer/RenderContext entry in the configuration
     * map.
     * <p>
     * Because the rendermetrics may call any code in order to obtain their metrics it is possible
     * that the rendermetrics could end up triggering createConfiguration to be called. Because of
     * this the render metrics methods cannot be called within a synchronization block because a
     * deadlock could occur
     * <p>
     * Consider: rendermetrics somehow resets a IGeoResource which triggers a re-render (and
     * therefore a re-evaluation of the renderers). If anywhere there is synchronous waiting between
     * 2 threads a dead lock can occur.
     * <p>
     * To overcome this issue this method has limitted synchronization. The configuration is made
     * but before it assigns the configuration it determines whether or not the layers have changed
     * since it started (this check is in a synchronization block so that it is thread safe) if the
     * layers have changed then it starts over again. This way there is no chance for deadlock but
     * the correctness semantics are maintained.
     */
    void createConfiguration() {
        boolean configurationPassed = false;

        while (!configurationPassed) {

            initRenderMetrics();

            Set<Layer> configured = new HashSet<>();
            List<Layer> currentLayers = new ArrayList<>();

            synchronized (this.layers) {
                currentLayers.addAll(this.layers);
            }

            Map<Layer, RenderContext> configuration = new HashMap<>();

            LAYERS: for (int i = 0; i < currentLayers.size(); i++) {
                Layer layer = currentLayers.get(i);

                if (configured.contains(layer)) {
                    continue LAYERS;
                }

                List<InternalRenderMetrics> layerfactories = layerToMetricsFactoryMap.get(layer);
                /*
                 * This causes concurrent modification exception. In the end most probably render
                 * metrics sorted once will serve forever in the same order. Move this sorting to
                 * creation.
                 *
                 */
                // Collections.sort(layerfactories, new RenderMetricsSorter(currentLayers));

                if (layerfactories.isEmpty()) {
                    // nobody loves this layer
                    // layer.setStatus(Layer.UNCONFIGURED);
                    continue LAYERS;
                } else {
                    AbstractRenderMetrics metrics = layerfactories.get(0); // sorted in order of preference

                    if (metrics != null) {
                        RenderContext renderContext = (RenderContext) metrics.getRenderContext();
                        if (renderContext instanceof CompositeRenderContext) {
                            constructCompositeContext(configured, currentLayers, configuration, i,
                                    metrics, (CompositeRenderContext) renderContext);
                        }
                        configuration.put(layer, renderContext);
                    }
                }
            }

            synchronized (this.layers) {
                Iterator<Layer> iter1 = currentLayers.iterator();
                Iterator<Layer> iter2 = this.layers.iterator();
                boolean failed = false;
                while (iter1.hasNext()) {
                    if (!iter2.hasNext() || !iter1.next().equals(iter2.next())) {
                        failed = true;
                        break;
                    }
                }
                if (!failed) {
                    this.configuration = Collections.synchronizedMap(configuration);
                    configurationPassed = true;
                }
            }
        }
    }

    private void constructCompositeContext(Set<Layer> configured, List<Layer> layers,
            Map<Layer, RenderContext> configuration, int i, AbstractRenderMetrics metrics,
            CompositeRenderContext renderContext) {

        renderContext.addContexts(Collections.singleton(renderContext));
        configured.add(renderContext.getLayerInternal());
        configuration.put(renderContext.getLayerInternal(), renderContext);

        CONTEXT: for (int j = i + 1; j < layers.size(); j++) {
            try {
                Layer layer = layers.get(j);
                if (!configured.contains(layer) && metrics.canAddLayer(layer)) {
                    addChildContextToComposite(configured, configuration, renderContext, layer);
                } else {
                    break CONTEXT;
                }
            } catch (Exception e) {
                break CONTEXT;
            }
        }
    }

    private void addChildContextToComposite(Set<Layer> configured,
            Map<Layer, RenderContext> configuration, CompositeRenderContext renderContext,
            Layer layer) {
        List<InternalRenderMetrics> layerfactories2 = layerToMetricsFactoryMap.get(layer);
        AbstractRenderMetrics metrics2 = layerfactories2.get(0);
        Set<RenderContext> child = Collections.singleton((RenderContext) metrics2.getRenderContext());
        renderContext.addContexts(child);
        // add to configurated to indicate that it has been configured.
        configured.add(layer);

        configuration.put(layer, renderContext);
    }

    /**
     * Initialize all known render metrics for a given layer
     */
    private void initRenderMetrics() {
        synchronized (this.layers) {
            for (Layer layer : getLayers()) {
                if (!layerToMetricsFactoryMap.containsKey(layer))
                    initFactories(layer);
            }
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    public RenderContext getRenderContext(Layer layer) {
        if (configuration == null)
            createConfiguration();
        return configuration.get(layer);
    }

    @Override
    public void changed(Notification event) {
        if (((event.getNotifier() instanceof ContextModel
                || event.getNotifier() instanceof RenderManager)
                && event.getFeatureID(
                        ContextModel.class) == ProjectPackage.CONTEXT_MODEL__LAYERS)) {
            handleMapCompositionEvent(event);
        }
    }

    @SuppressWarnings("unchecked")
    private void handleMapCompositionEvent(Notification event) {
        switch (event.getEventType()) {
        case Notification.ADD: {
            Layer layer = (Layer) event.getNewValue();
            List<Layer> currentLayers = new ArrayList<>();
            currentLayers.add(layer);
            if (layer.hasResource(FeatureSource.class))
                currentLayers.add(new SelectionLayer(layer));
            synchronized (this.layers) {
                this.layers.addAll(currentLayers);
            }
            break;
        }
        case Notification.ADD_MANY: {
            List<Layer> currentLayers = new ArrayList<>();
            Collection<? extends Layer> layerList = (Collection<? extends Layer>) event
                    .getNewValue();
            if (layerList instanceof SynchronizedEObjectWithInverseResolvingEList) {
                ((SynchronizedEObjectWithInverseResolvingEList<Layer>) layerList)
                        .syncedIteration(new IEListVisitor<Layer>() {
                            @Override
                            public void visit(Layer layer) {
                                currentLayers.add(layer);
                                if (layer.hasResource(FeatureSource.class)
                                        && findSelectionLayer(layer) == null) {
                                    currentLayers.add(new SelectionLayer(layer));
                                }
                            }
                        });
            } else {
                for (Layer layer : layerList) {
                    currentLayers.add(layer);
                    if (layer.hasResource(FeatureSource.class) && findSelectionLayer(layer) == null)
                        currentLayers.add(new SelectionLayer(layer));
                }
            }
            synchronized (this.layers) {
                this.layers.addAll(currentLayers);
            }
            break;
        }

        /*
         * The collection <code>layers</code> is a sorted TreeMap of <? extends Layer> objects:
         * Layer.compareTo() is used to sort and identify items for equality. Comparing is performed
         * by z-order. But this collection (<code>layers</code>) contains also additional
         * SelectionLayer objects and their z-order is artificial. This leads to errors during
         * removing by TreeMap.remove(..) methods. The <code>layers</code> collection is re-created
         * safely to fix deleting layers from map with synchronization of this cache list of layers
         * and selection layers with map's list.
         */
        case Notification.REMOVE: {

            synchronized (layers) {

                Layer removedLayer = (Layer) event.getOldValue();

                for (Iterator<Layer> iter = layers.iterator(); iter.hasNext();) {
                    Layer l = iter.next();
                    if (removedLayer == l)
                        iter.remove();
                    else if (l instanceof SelectionLayer) {
                        SelectionLayer sl = (SelectionLayer) l;
                        if (removedLayer == sl.getWrappedLayer())
                            iter.remove();
                    }
                }

            }
            break;
        }
        case Notification.REMOVE_MANY: {

            synchronized (layers) {
                Collection<Layer> removedLayers = (Collection<Layer>) event.getOldValue();

                for (Iterator<Layer> iter = layers.iterator(); iter.hasNext();) {
                    Layer l = iter.next();
                    if (removedLayers.contains(l))
                        iter.remove();
                    else if (l instanceof SelectionLayer) {
                        SelectionLayer sl = (SelectionLayer) l;
                        if (removedLayers.contains(sl.getWrappedLayer()))
                            iter.remove();
                    }
                }
            }
            break;
        }
        case Notification.MOVE: {
            // this should be a layer accordint to the reverse engineered rules...
            // I like type safety better. or at least documentation :(
            Layer newV = (Layer) event.getNewValue();

            // remove then add the layers to fix ordering of layers.
            synchronized (layers) {
                SelectionLayer selectionLayer = null;
                for (Iterator<Layer> iter = layers.iterator(); iter.hasNext();) {
                    Layer l = iter.next();
                    if (newV == l)
                        iter.remove();
                    else if (l instanceof SelectionLayer) {
                        SelectionLayer sl = (SelectionLayer) l;
                        if (newV == sl.getWrappedLayer()) {
                            iter.remove();
                            selectionLayer = sl;
                        }
                    }
                }
                layers.add(newV);
                if (selectionLayer != null) {
                    layers.add(selectionLayer);
                }
            }

            break;
        }
        case Notification.SET: {
            Layer oldV = (Layer) event.getOldValue();

            Layer newV = (Layer) event.getNewValue();
            SelectionLayer selectionLayer = null;
            if (newV.hasResource(FeatureSource.class))
                selectionLayer = new SelectionLayer(newV);

            // remove then add the layers to fix ordering of layers.
            synchronized (layers) {
                for (Iterator<Layer> iter = layers.iterator(); iter.hasNext();) {
                    Layer l = iter.next();
                    if (oldV == l)
                        iter.remove();
                    else if (l instanceof SelectionLayer) {
                        SelectionLayer sl = (SelectionLayer) l;
                        if (oldV == sl.getWrappedLayer()) {
                            iter.remove();
                        }
                    }
                }
                layers.add(newV);
                if (selectionLayer != null) {
                    layers.add(selectionLayer);
                }
            }

            break;
        }
        default:
            break;
        }
        configuration = null;
    }

    /**
     * Locates the selection layer for layer or returns null;
     *
     * @return the selection layer for layer or returns null;
     */
    @Override
    public SelectionLayer findSelectionLayer(ILayer targetLayer) {
        try {
            if (targetLayer.getResource(FeatureSource.class, null) == null)
                return null;
        } catch (IOException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
        for (Layer layer : getLayers())
            if (layer instanceof SelectionLayer
                    && ((SelectionLayer) layer).getWrappedLayer() == targetLayer) {
                return (SelectionLayer) layer;
            }

        return null;
    }

    private void initFactories(Layer layer) {
        if (!layerToMetricsFactoryMap.containsKey(layer)) {
            RendererExtensionProcessor p = new RendererExtensionProcessor(layer,
                    getContext().getMapInternal(), getContext().getRenderManagerInternal());

            ExtensionPointUtil.process(ProjectPlugin.getPlugin(), IRenderer.RENDER_EXT, p);

            List<Layer> layers = new ArrayList<Layer>();
            synchronized (this.layers) {
                layers.addAll(this.layers);
            }
            Collections.sort(p.rFactories, new RenderMetricsSorter(layers));
            layerToMetricsFactoryMap.put(layer, new CopyOnWriteArrayList<>(p.rFactories));

        }
    }

    Collection<RenderContext> contexts = Collections.synchronizedSet(new TreeSet<RenderContext>());

    @Override
    public Collection<RenderContext> getConfiguration() {

        synchronized (this) {
            if (configuration == null)
                createConfiguration();
        }
        Collection<RenderContext> uniqueValues = new LinkedList<>();

        synchronized (configuration) {
            Collection<RenderContext> values = configuration.values();
            for (RenderContext renderCtx : values) {
                boolean found = false;
                for (RenderContext ctx : uniqueValues) {
                    if (renderCtx == ctx) { // reference check
                        found = true;
                        break;
                    }
                }
                if (!found)
                    uniqueValues.add(renderCtx);
            }
        }
        synchronized (contexts) {
            contexts.clear();
            for (RenderContext context : uniqueValues) {
                contexts.add(context);
            }
            return new ArrayList<>(contexts);
        }
    }

    @Override
    public void reset() {
        configuration = null;
        contexts.clear();

        getConfiguration();
    }

} // RendererCreatorImpl
