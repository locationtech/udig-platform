/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render.impl;

import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.core.internal.ExtensionPointUtil;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.ContextModel;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.CompositeRenderContext;
import org.locationtech.udig.project.internal.render.RenderContext;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.internal.render.RendererCreator;
import org.locationtech.udig.project.internal.render.SelectionLayer;
import org.locationtech.udig.project.render.AbstractRenderMetrics;
import org.locationtech.udig.project.render.IMultiLayerRenderer;
import org.locationtech.udig.project.render.IRenderMetricsFactory;
import org.locationtech.udig.project.render.IRenderer;

import org.eclipse.emf.common.notify.Notification;
import org.geotools.data.FeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;

public class TiledRendererCreatorImpl implements RendererCreator {
    
    /**
     * The cached value of the '{@link #getLayers() <em>Layers</em>}' reference list. 
     * 
     * <p>This list is used to build contexts therefore it must be maintained when the layers
     * change or change order.</p>
     * 
     * @see #getLayers()
     */
    protected final SortedSet<Layer> layers = Collections.synchronizedSortedSet(new TreeSet<Layer>());

    /**
     * This is a map from a layer to a collection of render metrics.  This collection of render metrics is the collection
     * of metrics that can render the layer.
     * 
     * This render metrics has a context with the bounds of the viewport.  It is
     * used to satisfy the functions that require a single render metrics for a layer (zoom to extent).   
     * 
     * <p>Maybe this should be changed as it seems odd to have to do this?</p> 
     */
    Map<Layer, List<AbstractRenderMetrics>> layerToRenderMetrics = new HashMap<Layer, List<AbstractRenderMetrics>>();
    
    /**
     * A collection of render metrics factories that are registered.
     * 
     * <p>
     * An extension point processor is used to populate these (See initRenderMetricFactories).
     * </p>
     */
    private Collection<IRenderMetricsFactory> renderMetricsFactories = null;
    
    /**
     * The size of the tiles in pixels.
     *   
     * <p>
     * The tiles must be square.
     * </p>
     */
    private int tilesize;

    /**
     * The render manager that is using this Render Creator.
     */
    private RenderManager manager;
    
    /**
     * Creates a new Tiled Renderer Creator.  This creates configurations that 
     * are tile based.
     * 
     * @param tilesize  size of the tiles to create
     * @param manager   render manager
     */
    public TiledRendererCreatorImpl(int tilesize, RenderManager manager ){
        super();
        this.manager = manager;
        this.tilesize = tilesize;
    }
    
    /**
     * Listens for events from the ContextModel or RenderManager 
     * that are context model events (ProjectPackage.CONTEXT_MODEL__LAYERS)
     * 
     * <p>This is the event that maintains the layers list.</p>
     */
    public void changed( Notification msg ) {
        if (((msg.getNotifier() instanceof ContextModel || msg.getNotifier() instanceof RenderManager) 
                && msg.getFeatureID(ContextModel.class) == ProjectPackage.CONTEXT_MODEL__LAYERS)) {
            handleMapCompositionEvent(msg);
        }
    }
    
    /**
     * Handles context events.
     * 
     * <p>This function updates the layers list adding or removing layers as associated
     * with the event.
     * </p> 
     *
     * @param event
     */
    @SuppressWarnings("unchecked")
    private void handleMapCompositionEvent( Notification event ) {
                
        switch( event.getEventType() ) {
        case Notification.ADD: {
            //layer has been added need to add to layers list
            //if selectable layer also add a selection layer
            Layer layer = (Layer) event.getNewValue();
            List<Layer> layers=new ArrayList<Layer>();
            layers.add(layer);
            if (layer.hasResource(FeatureSource.class))
                 layers.add(new SelectionLayer(layer));
            synchronized (this.layers){
                this.layers.addAll(layers);
            }
            break;
        }
        case Notification.ADD_MANY: {
            //multiple layer has been added need to add to layers list
            //if selectable layer also add a selection layer
            List<Layer> layers = new ArrayList<Layer>();
            for( Layer layer : (Collection< ? extends Layer>) event.getNewValue() ) {
                layers.add(layer);
                if (layer.hasResource(FeatureSource.class)
                        && findSelectionLayer(layer) == null)
                    layers.add(new SelectionLayer(layer));
            }
            synchronized (this.layers){
                this.layers.addAll(layers);
            }
            break;
        }
        
        /*
         * The collection <code>layers</code> is a sorted TreeMap of <? extends Layer> objects:
         * Layer.compareTo() is used to sort and identify items for equality. Comparing is performed 
         * by z-order. But this collection (<code>layers</code>) contains also
         * additional SelectionLayer objects and their z-order is artificial. This leads to
         * errors during removing by TreeMap.remove(..) methods.
         * The <code>layers</code> collection is re-created safely to fix deleting
         * layers from map with synchronization of this cache list of layers and selection layers with
         * map's list.
         */
        
        case Notification.REMOVE: {
            //remove layer from layers list (both layer and selection layer)
            synchronized (layers) {
                Layer removedLayer = (Layer) event.getOldValue();
                for ( Iterator iter = layers.iterator(); iter.hasNext(); ) {
                    Layer l = (Layer) iter.next();
                    if(removedLayer==l){
                        //remove layer
                        iter.remove();
                    }
                    else if( l instanceof SelectionLayer ){
                        SelectionLayer sl=(SelectionLayer) l;
                        if( removedLayer==sl.getWrappedLayer() ){
                            iter.remove();
                        }
                    }
                }
            }
            break;
        }
        case Notification.REMOVE_MANY: {
          //remove layers from layers list (both layer and selection layer)
            synchronized (layers) {
                Collection<Layer> removedLayers = (Collection<Layer>) event.getOldValue();

                for ( Iterator iter = layers.iterator(); iter.hasNext(); ) {
                    Layer l = (Layer) iter.next();
                    if( removedLayers.contains(l)){
                        iter.remove();
                    }
                    else if( l instanceof SelectionLayer ){
                        SelectionLayer sl=(SelectionLayer) l;
                        if( removedLayers.contains(sl.getWrappedLayer()) ){
                            iter.remove();
                        }
                    }
                }
            }
            break;
        }
        case Notification.MOVE: {
            // moving a layer (getNewValue is expected to return a layer)
            // I like type safety better. or at least documentation :( 
            Layer newV=(Layer) event.getNewValue();
            
            // remove then add the layers to fix ordering of layers (layers are stored in a sorted set that
            //is sorted by zorder
            synchronized (layers) {
                //remove layer and selection layer
                SelectionLayer selectionLayer=null;
                for( Iterator iter = layers.iterator(); iter.hasNext(); ) {
                    Layer l = (Layer) iter.next();
                    if(newV==l)
                        iter.remove();
                    else if( l instanceof SelectionLayer ){
                        SelectionLayer sl=(SelectionLayer) l;
                        if( newV==sl.getWrappedLayer() ){
                            iter.remove();
                            selectionLayer=sl;
                        }
                    }
                }
                //add layers and selection layer
                layers.add(newV);
                if( selectionLayer!=null ){
                    layers.add(selectionLayer);
                }
            }
            
            break;
        }case Notification.SET:{
            //what is a SET event?
            Layer oldV=(Layer) event.getOldValue();
            Layer newV=(Layer) event.getNewValue();
            SelectionLayer selectionLayer=null;
            if( newV.hasResource(FeatureSource.class) )
                selectionLayer=new SelectionLayer(newV);

            // remove then add the layers to fix ordering of layers.
            synchronized (layers) {
                for( Iterator iter = layers.iterator(); iter.hasNext(); ) {
                    Layer l = (Layer) iter.next();
                    if(oldV==l)
                        iter.remove();
                    else if( l instanceof SelectionLayer ){
                        SelectionLayer sl=(SelectionLayer) l;
                        if( oldV==sl.getWrappedLayer() ){
                            iter.remove();
                        }
                    }
                }
                layers.add(newV);
                if( selectionLayer!=null ){
                    layers.add(selectionLayer);
                }
            }

            break;
        }
        default:
            break;
        }
    }
    

    /**
     * Locates the selection layer for layer or returns null;
     * 
     * @return the selection layer for layer or returns null;
     */
    public SelectionLayer findSelectionLayer( ILayer targetLayer ) {
        try{
            if (targetLayer.getResource(FeatureSource.class, null) == null){
                //no feature source so no selection layer so return null
                return null;
            }
        }catch (IOException e){
            return null;
        }
        for(Layer layer: getLayers()){
            if (layer instanceof SelectionLayer){
                if (  ((SelectionLayer)layer).getWrappedLayer() == targetLayer){
                    return ((SelectionLayer)layer);
                }
            }
        }
        
        return null;
    }

    /**
     * For a given layer; this function uses the render metrics factories to create a list of possible render metrics
     * for a given layer.  The metrics are not stored anywhere; they have contexts but are not reused. 
     */
    public Collection<AbstractRenderMetrics> getAvailableRendererMetrics( Layer layer ) {     
        Collection<AbstractRenderMetrics> metrics = layerToRenderMetrics.get(layer);
        if (metrics == null){
            initRenderMetricsPerLayer(layer);
            metrics = layerToRenderMetrics.get(layer);
        }
        return metrics;
    }
    

    /**
     * @throws UnsupportedOperationException
     */
    public Map<String, String> getAvailableRenderersInfo( Layer layer ) {
        throw new UnsupportedOperationException("Renderers don't have information in this system."); //$NON-NLS-1$
    }


    /**
     * 
     */
    public Collection<String> getAvailableRendererIds(Layer layer) {
    	List<String> result = new ArrayList<String>(); 
    	Collection<AbstractRenderMetrics> metrics = getAvailableRendererMetrics(layer);
    	for (AbstractRenderMetrics metric : metrics) {
    		result.add(metric.getId());
    	}
    	return result;
    }
    
    /**
     * 
     * @throws UnsupportedOperationException
     */
    public Collection<RenderContext> getConfiguration() {
        throw new UnsupportedOperationException("A single configuration doesn't exist in a tiled system.  Use getConfiguration(RenderenceEnvelope) instead."); //$NON-NLS-1$
    }


    /**
     * Gets a configuration for a particular tile.  A configuration consists of a collection
     * of render metrics necessary to draw all the layers in a given tile.
     *
     * @param bounds
     * @return
     */
    public Collection<AbstractRenderMetrics> getConfiguration( ReferencedEnvelope bounds ) {
        ArrayList<AbstractRenderMetrics> metrics = null;
        // create the configuration
        Map<Layer, AbstractRenderMetrics> metricsmap = createConfiguration(bounds);

        // convert to a list and return it
        metrics = new ArrayList<AbstractRenderMetrics>();
        synchronized (metricsmap) {            
            for( Iterator<Entry<Layer, AbstractRenderMetrics>> iterator = metricsmap.entrySet().iterator(); iterator.hasNext(); ) {
                Entry<Layer, AbstractRenderMetrics> entry = (Entry<Layer, AbstractRenderMetrics>) iterator.next();
                if (entry.getValue() != null && !metrics.contains(entry.getValue())){
                    metrics.add(entry.getValue());
                }   
            }
        }
        
        return metrics;
    }
   
    public RenderContext getContext() {
        throw new UnsupportedOperationException("Tiled system doesn't have a context."); //$NON-NLS-1$
    }

    /**
     * This list is maintained by listening to context events.  When a layer is added,
     * removed, moved or otherwise modified this list is updated.
     * 
     * @return The list of layers that the RendererCreator is responsible for creating renderers
     */
    public SortedSet<Layer> getLayers() {
        return layers;
    }

    /**
     * A layer no longer has a single render context.
     * @returns null
     */
    public RenderContext getRenderContext( Layer layer ) {
        throw new UnsupportedOperationException("A layer doesn't have a single render context in the Tiled system."); //$NON-NLS-1$
    }

    /**
     * Gets a renderer for a particular render context.  This function uses the layer and
     * bound information in the render context to determine what render metrics to use to
     * create a renderer.
     * 
     * <p>
     * getRenderer called by 
     * <ul>
     *   <li>CompositeRendererImpl - when created a new CompositeContextListener
     *   <li>CompositeRendererImpl - when context is set
     *   <li>ApplicationGIS
     * </ul>
     * 
     */
    public Renderer getRenderer( RenderContext context ) {
        throw new UnsupportedOperationException("Cannot get renderer for tiled system."); //$NON-NLS-1$
    }

 
    /**
     * Remove all render metrics and re-creates them
     */
    public void reset() {
        if (renderMetricsFactories != null){
            renderMetricsFactories.clear();         //clear the render metric factories
            layerToRenderMetrics.clear();
        }
        initRenderMetricFactories();            //reload the factories
    }

    /**
     * @throws UnsupportedOperationException
     */
    public void setContext( RenderContext value ) {
        throw new UnsupportedOperationException("Tiled system doesn't have a context."); //$NON-NLS-1$
    }

    
    /**
     * Loads all the possible render metric factories using an Extension Point Processor.
     */
    private void initRenderMetricFactories(){
        RenderMetricsFactoryExtensionPointProcessor p = new RenderMetricsFactoryExtensionPointProcessor();
        
        ExtensionPointUtil.process(ProjectPlugin.getPlugin(), IRenderer.RENDER_EXT, p);
        renderMetricsFactories = p.getRFactories();
    }
    
    /**
     * Creates render metrics for a given layer.  If layer is null then it creates render metrics for all layers.
     * <p>
     * These render metrics are not tile specific and the corresponding contexts have the bounding box of the
     * viewport.
     * 
     * <p>These metrics are stored for use by functions that need a non-tile specific metric.  See getAvaliableRendererMetrics(Layer).
     * </p>
     */
    private void initRenderMetricsPerLayer(Layer layer){
        Collection<Layer> layers = this.layers;
        if (layer == null){
            layerToRenderMetrics = new HashMap<Layer, List<AbstractRenderMetrics>>();
        }else{
            layers = new ArrayList<Layer>();
            layers.add(layer);
        }
        if (renderMetricsFactories == null){
            initRenderMetricFactories();
        }
        for( Layer l : layers ) {    
            List<AbstractRenderMetrics> m = createRenderMetrics(renderMetricsFactories, l, manager.getViewportModelInternal().getBounds());
            layerToRenderMetrics.put(l, m);
        }
    }
    
    
    /**
     * This function goes through all the layers and determines what Render to use and creates an associated
     * context.
     * <p>
     * For each layer in the layers list:
     * <ul>
     *   <li>Creates a AbstractRenderMetric for each of the possible RenderMetricFactories associated with the layer.
     *   <li>Sorts these metrics and determines which of the metrics is "best"
     *   <li>Creates a context for the winning metric and sets the layer, map, render manager, and image bounds
     *   associated with that context.
     *   <li>Adds the metric to the layertileToRenderMetrics map
     *   <li>Adds the context to the Map returned.
     * </ul>
     * </p>
     *
     * @param bounds
     * @return
     */
    private Map<Layer, AbstractRenderMetrics> createConfiguration(ReferencedEnvelope bounds){
        //ensure the list of metric factories has been processed
        if (renderMetricsFactories == null){
            initRenderMetricFactories();
        }
        
        Collection<IRenderMetricsFactory> factories = renderMetricsFactories;
        
        //local copy of the configuration
        //the configuration consists of a map of layer to render context
        Map<Layer, AbstractRenderMetrics> configuration = new HashMap<Layer, AbstractRenderMetrics>();
        
        //this while loop allows us to make a copy of the layers, go through them 
        //then compare the results with the original layer list
        //so if a change was made to the original layer list while we were processing
        //we can go through the list again.
        boolean configurationPassed = false;
        
        while( !configurationPassed ) {
            // make a copy of the layers to deal with so we don't have to worry
            // about changes
            List<Layer> layers = new ArrayList<Layer>();
            synchronized (this.layers) {
                layers.addAll(this.layers);
            }

            LAYERS: for( int i = 0; i < layers.size(); i++ ) {
                Layer layer = layers.get(i);

                if (configuration.get(layer) != null) {
                    // we've dealt with this layer so we don't need to worry about it
                    continue LAYERS;
                }

                //get possible metrics & sort them
                List<AbstractRenderMetrics> availableMetrics = createRenderMetrics(factories, layer, bounds);
                Collections.sort(availableMetrics, new AbstractRenderMetricsSorter(layers));
                
                if (availableMetrics.isEmpty()) {
                    // no render metrics for this layer
                    //clear our render metrics cache
                    configuration.put(layer, null);
                    continue LAYERS;
                } else {
                    AbstractRenderMetrics metrics = availableMetrics.get(0); //the best metric once sorted
                    if (metrics != null) {
                        RenderContext renderContext = (RenderContext) metrics.getRenderContext();
                                                
                        if (renderContext instanceof CompositeRenderContextImpl) {
                            //need to construct the composite context by going through the next layers and seeing
                            //if they can be added to the current context.
                            constructCompositeContext(layers, configuration, i, metrics,
                                    (CompositeRenderContext) renderContext, bounds, factories);
                        }
                        configuration.put(layer, metrics);
                    }
                }
            }

            // ensure all layers have been dealt with and no changes were made while we were processing the layer
            synchronized (this.layers) {
                Iterator<Layer> iter1 = layers.iterator();
                Iterator<Layer> iter2 = this.layers.iterator();
                boolean failed = false;
                while( iter1.hasNext() ) {
                    if (!iter2.hasNext()) {
                        failed = true;
                        break;
                    }
                    if (!iter1.next().equals(iter2.next())) {
                        failed = true;
                        break;
                    }
                }
                if (!failed) {
                    configuration = Collections.synchronizedMap(configuration);
                    configurationPassed = true;
                }
            }
        }
        return configuration;
    }
    
    
    /**
     * Creates RenderMetrics for each of the possible factories.
     * <p>A metrics is created for each metric factory for each georesouce the layer can resolve to.</p> 
     * <p>In addition to creating the render metrics, the RenderContext is created here.</p>
     *
     * @param factories list of factories to use to create metrics
     * @param layer layer being processed (a metric is created for each georesource the layer can resolve to. 
     * @param map map being processed (for context)
     * @param rm render manager being processed (for context)
     * @param bounds tile bounds (for context)
     * 
     * @return A list of AbstractRenderMetrics created from each of the factories.  These Metrics will have there context created and set.
     */
    private List<AbstractRenderMetrics> createRenderMetrics(Collection<IRenderMetricsFactory> factories, Layer layer, ReferencedEnvelope bounds){
        ArrayList<AbstractRenderMetrics> metrics = new ArrayList<AbstractRenderMetrics>();
        
        for( Iterator<IRenderMetricsFactory> iterator = factories.iterator(); iterator.hasNext(); ) {
            IRenderMetricsFactory renderMetricsFactory = (IRenderMetricsFactory) iterator.next();
            
            List<IGeoResource> data = layer.getGeoResources();
            for( IGeoResource resource : data ) {
                RenderContext context;
                try{
                    if (IMultiLayerRenderer.class.isAssignableFrom(renderMetricsFactory.getRendererType())){
                        context = new CompositeRenderContextImpl();
                    }else{
                        context = new RenderContextImpl(layer instanceof SelectionLayer);
                    }
                }catch (Throwable e){
                    context = new RenderContextImpl(layer instanceof SelectionLayer);
                }
                
                context.setMapInternal(manager.getMapInternal());
                context.setRenderManagerInternal(manager);
                context.setLayerInternal(layer);
                context.setGeoResourceInternal(resource);
                context.setImageSize(new Dimension(tilesize, tilesize));
                context.setImageBounds(bounds);
                
                try {
                    if (renderMetricsFactory.canRender(context)){
                        AbstractRenderMetrics metric = ((InternalRenderMetricsFactory.InternalRenderMetrics) renderMetricsFactory.createMetrics(context)).delegate;
                        // we need to assign an id here for the metrics sorting
                        metric.setId(  ((RenderMetricsFactoryExtensionPointProcessor.IdRenderMetricsFactory)renderMetricsFactory).getId() );
                        metrics.add(metric);
                        
                    }
                } catch (IOException e) {
                    ProjectPlugin.log("Cannot determine if context is renderable.", e); //$NON-NLS-1$
                }
            }
        }
        return metrics;
    }
        
    private void constructCompositeContext(List<Layer> layers,
            Map<Layer, AbstractRenderMetrics> configuration, 
            int startindex, 
            AbstractRenderMetrics parentMetrics, 
            CompositeRenderContext parentRenderContext, ReferencedEnvelope bounds
            ,Collection<IRenderMetricsFactory> factories){
                
        //go through the remaining layers starting a start index until
        //we cannot add the layer to the existing metrics
        for( int j = startindex; j < layers.size(); j++ ) {
            try{
                Layer layer = layers.get(j);
                if (parentMetrics.canAddLayer(layer) && configuration.get(layer) == null){
                    //we can add this layer
                    addChildContextToComposite(configuration, parentRenderContext, layer, parentMetrics);
                }else{
                    //nothing else to add
                    break;
                }
            }catch (Exception ex){
                //nothing else to add
                break;
            }
        }
        
    }
    
    private void addChildContextToComposite( 
            Map<Layer, AbstractRenderMetrics> configuration, 
            CompositeRenderContext parentRenderContext, 
            Layer layer, AbstractRenderMetrics parentMetrics) {

        //create a context for this layer that matches the parent layer
        RenderContext childcontext = new RenderContextImpl(layer instanceof SelectionLayer);
        childcontext.setMapInternal(parentRenderContext.getMapInternal());
        childcontext.setRenderManagerInternal(parentRenderContext.getRenderManagerInternal());
        childcontext.setLayerInternal(layer);
        childcontext.setGeoResourceInternal(layer.getGeoResource());
        childcontext.setImageSize(parentRenderContext.getImageSize());
        childcontext.setImageBounds(parentRenderContext.getImageBounds());
        
        //use the same render metrics factory to create a new render metrics
        AbstractRenderMetrics childmetric = parentMetrics.getRenderMetricsFactory().createMetrics(childcontext);
        
        //add to the parent context
      Set<RenderContext> child = Collections.singleton((RenderContext) childmetric.getRenderContext());
        parentRenderContext.addContexts(child);
       
        //this layer has the parent render context associated with it
        configuration.put(layer, parentMetrics);
    }
}
