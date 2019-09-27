/*
 * Created on Nov 24, 2004 TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package org.locationtech.udig.project.ui.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.geotools.data.FeatureEvent;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.ContextModel;
import org.locationtech.udig.project.internal.ContextModelListenerAdapter;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.CompositeRenderContext;
import org.locationtech.udig.project.internal.render.MultiLayerRenderer;
import org.locationtech.udig.project.internal.render.RenderContext;
import org.locationtech.udig.project.internal.render.RenderListenerAdapter;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.RenderPackage;
import org.locationtech.udig.project.internal.render.RendererCreator;
import org.locationtech.udig.project.internal.render.SelectionLayer;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.internal.render.impl.RenderManagerImpl;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.project.render.RenderException;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Provides factory methods for various Listeners used by RenderManagerDynamic.
 */
public class RenderManagerAdapters {

    static Adapter createViewportListener( final RenderManagerImpl manager ) {
        return new AdapterImpl(){
            /**
             * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
             */
        	public void notifyChanged( Notification msg ) {
                manager.checkState();
                switch( msg.getFeatureID(ViewportModel.class) ) {
                case RenderPackage.VIEWPORT_MODEL__BOUNDS: {
                    if (ApplicationGIS.getActiveMap() != null
                            && ApplicationGIS.getVisibleMaps().contains(
                                    ApplicationGIS.getActiveMap())) {
                        refreshDirtyArea(msg);
                    }
                    break;
                }
                case RenderPackage.VIEWPORT_MODEL__CRS: {
                    manager.refresh(null);
                    break;
                }
                case RenderPackage.VIEWPORT_MODEL__CURRENT_TIMESTEP: {
                    manager.refresh(null);
                    break;
                }
                case RenderPackage.VIEWPORT_MODEL__CURRENT_ELEVATION: {
                    manager.refresh(null);
                    break;
                }
                }// switch
            }

            private void refreshDirtyArea( Notification msg ) {
                manager.refresh(null);
            }
        };
    }
    
    
    /**
     * Creates a new viewport listener for a tiled render manager.
     * 
     * <p>This viewport listeners calls a soft refresh function with doesn't clear
     * existing contexts or rerender tiles before drawing so it should reuse tiles if they have already been
     * loaded.  New tiles will be rendered.</p>
     * <p>A CRS change causes a full refresh; flushing all the tiles as currently tiles do not
     * have a CRS associated with them so they have to be cleared so the wrong projection tiles
     * are not used.</p>
     * 
     * <p>This listeners listens to:
     * <ul>
     *   <li>RenderPackage.VIEWPORT_MODEL__BOUNDS
     *   <li>RenderPackage.VIEWPORT_MODEL__CRS
     * </ul>
     */
    static Adapter createViewportListener( final TiledRenderManagerDynamic manager ) {
        return new AdapterImpl(){
            /**
             * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
             */
            public void notifyChanged( Notification msg ) {
                manager.checkState();
                switch( msg.getFeatureID(ViewportModel.class) ) {
                case RenderPackage.VIEWPORT_MODEL__BOUNDS: {
                    manager.viewportChanged(msg);
                    break;
                }
                case RenderPackage.VIEWPORT_MODEL__CRS: {
                    manager.crsChanged(msg);
                    break;
                }
                }// switch
            }
        };
    }
    
    /**
     * Who are you and what do you do? It does not actually
     * get added anywhere so we do not know where and how.
     * 
     * @param manager 
     * @returnContext
     */
    static ContextModelListenerAdapter createContextModelListener(
            final RenderManagerDynamic manager ) {
        return new ContextModelListenerAdapter(){
            /**
             * @see org.locationtech.udig.project.ContextModelListenerAdapter#commandExecuted(org.eclipse.emf.common.notify.Notification)
             */
            public void notifyChanged( Notification msg ) {
                super.notifyChanged(msg);
            }

            /**
             * Will sychronizeAndRefresh( the manager based on the notification; and
             * updateImage().
             * 
             * @see org.locationtech.udig.project.ContextModelListenerAdapter#zorderChanged(org.eclipse.emf.common.notify.Notification)
             */
            protected void zorderChanged( Notification msg ) {
                synchronizeAndRefresh(msg, manager);

                updateImage();
            }

            /**
             * @see org.locationtech.udig.project.ContextModelListenerAdapter#layerAdded(org.eclipse.emf.common.notify.Notification)
             */
            protected void layerAdded( Notification msg ) {
                synchronizeAndRefresh(msg, manager);
                updateImage();
            }

            /**
             * @see org.locationtech.udig.project.ContextModelListenerAdapter#layerRemoved(org.eclipse.emf.common.notify.Notification)
             */
            protected void layerRemoved( Notification msg ) {
                synchronizeAndRefresh(msg, manager);
                manager.validateRendererConfiguration();
                updateImage();
            }

            /**
             * @see org.locationtech.udig.project.ContextModelListenerAdapter#manyLayersAdded(org.eclipse.emf.common.notify.Notification)
             */
            protected void manyLayersAdded( Notification msg ) {
                synchronizeAndRefresh(msg, manager);
                updateImage();
            }

            /**
             * @see org.locationtech.udig.project.ContextModelListenerAdapter#manyLayersRemoved(org.eclipse.emf.common.notify.Notification)
             */
            protected void manyLayersRemoved( Notification msg ) {
                synchronizeAndRefresh(msg, manager);
                manager.validateRendererConfiguration();
                updateImage();
            }

            public void updateImage() {
                try {
                    ((MultiLayerRenderer) manager.getRenderExecutor().getRenderer()).refreshImage();
                } catch (RenderException e) {
                    // won't happen.
                    e.printStackTrace();
                }
                ((ViewportPane) manager.getMapDisplay()).renderDone();
            }

        };

    }
    
//    /**
//     * This private method is called by the context model listener when a layer
//     * has been added/removed/moved in order to refresh the image.  
//     *  
//     * <p>
//     * This implementation hunts down a layer can calls layer.refresh( null ) in order
//     * to ask it to redraw pretty much everything it has... the synchronizeRenderers
//     * will hunt down the list of layers effected by this notification....scared.
//     * <p>
//     * 
//     * @param msg
//     * @param manager
//     */
//    private static void synchronizeAndRefresh(Notification msg, TiledRenderManagerDynamic manager) {
//        //something has happened and we need to re-create the contexts when we 
//        //go to render a tile
//        manager.invalidateAllTileContext();
//        List<Layer> toRender = synchronizeRenderers(msg, (TiledRendererCreatorImpl)manager.getRendererCreator());
//        for( Layer layer : toRender ) {
//            layer.refresh(null);
//        }
//    }
//    
//    /** 
//     * This method is responsible for spitting out a list of layers that need 
//     * to be refreshed in response to the provided notification.
//     * <p>
//     * 
//     * 
//     * @param msg notifcation message (such as a zorder change) causing this change
//     * @param configuration RemderContexts being drawn into...
//     * @param rendererCreator RemderCreator responsible for setting up renderers associated with these layers
//     * @return List of layers to refresh or other wise schedule for redrawing
//     */
//    public static List<Layer> synchronizeRenderers( final Notification msg, 
//            final TiledRendererCreatorImpl rendererCreator) {
//        
//        
//        //This call updates the layers list in the renderer creator
//        //this layer list is the list of layers drawn on the screen
//        //For the Tile Render Manager this deals with keeping and removing contexts as required
//        rendererCreator.changed(msg);
//        
//        //what are the new layers?
//        //these layers need to be kicked for redraw
//        ArrayList<Layer> addedLayers = new ArrayList<Layer>();
//        switch( msg.getEventType() ) {
//        case Notification.ADD: {
//            //layer has been added need to add to layers list
//            //if selectable layer also add a selection layer
//            Layer layer = (Layer) msg.getNewValue();
//            addedLayers.add(layer);
//            break;
//        }
//        case Notification.ADD_MANY: {
//            for( Layer layer : (Collection< ? extends Layer>) msg.getNewValue() ) {
//                addedLayers.add(layer);
//            }
//        }}
//        return addedLayers;
//    }
    
    
    /**
     * This private method is called by listeners (such as the context model listener)
     * in order to kick layers into life.
     * <p>
     * This implementation hunts down a layer can calls layer.refresh( null ) in order
     * to ask it to redraw pretty much everything it has... the synchronizeRenderers
     * will hunt down the list of layers effected by this notification....scared.
     * <p>
     * 
     * @param msg
     * @param manager
     */
    private static void synchronizeAndRefresh(Notification msg, RenderManagerDynamic manager) {
        List<Layer> toRender = synchronizeRenderers(msg, manager.configuration, manager.getRendererCreator());
        for( Layer layer : toRender ) {
            layer.refresh(null);
        }
    }
    /** 
     * This method is responsible for spitting out a list of layers that need 
     * to be refreshed in response to the provided notification.
     * <p>
     * 
     * 
     * @param msg notifcation message (such as a zorder change) causing this change
     * @param configuration RemderContexts being drawn into...
     * @param rendererCreator RemderCreator responsible for setting up renderers associated with these layers
     * @return List of layers to refresh or other wise schedule for redrawing
     */
    public static List<Layer> synchronizeRenderers( final Notification msg, final Collection<RenderContext> configuration, 
            final RendererCreator rendererCreator) {
        /**
         * This is a back up of the render context (ie with buffered image and stuff) so
         * we can recycle them...
         */
        HashMap<RenderContext, RenderContext> oldToCopy = new HashMap<RenderContext, RenderContext>();
        Collection<RenderContext> configuration2=configuration;
        if (configuration2 != null)
            for( RenderContext context : configuration2 ) {
                oldToCopy.put(context, context.copy());
            }
        /// smack the render creator and ask it to create us up some new renderers
        rendererCreator.changed(msg);
        
        //// new configuration of render context ...
        configuration2=rendererCreator.getConfiguration();
        
       // this is the list of layers we need to ask to be redrawn at the end of the day
        List<Layer> toRender = new ArrayList<Layer>();
        
        for( RenderContext newcontext : configuration2 ) {
            if (!oldToCopy.containsKey(newcontext)
                    && !(newcontext.getLayer() instanceof SelectionLayer)) {
            	// if it is something that was not there before we need to render it!
                toRender.add(newcontext.getLayerInternal());
            } else {
                if (newcontext instanceof CompositeRenderContext) {
                	// we got children ... that will slow us down a bit...
                    List<Layer> oldLayers = new ArrayList<Layer>(((CompositeRenderContext) oldToCopy.get(newcontext))
                            .getLayersInternal());
                    // these are our old layers; we want to check if they are in a different order or something ...
                    for( Layer layer : ((CompositeRenderContext) newcontext).getLayersInternal() ) {
                        if (!oldLayers.contains(layer)) {
                        	// our child is new we better ask it to get drawn...
                            toRender.add(newcontext.getLayerInternal());
                            break;
                        }
                        oldLayers.remove(layer); // we have this one already
                        // XXX
                    }
                    if (!oldLayers.isEmpty()){
                    	// this is stuff we no longer need
                        toRender.add(newcontext.getLayerInternal()); // perhaps the old stuff will be removed by someone?
                    }
                }
                oldToCopy.remove(newcontext);
            }
        }
        // we never check oldtoCopy for leftovers ... ie so we could dispose them?
        
        return toRender; // the end of the day
    }

    /**
     * <!-- begin-user-doc -->
     * Listens to the setMap or setViewportModel methods getting called; will wire up
     * our viewportListener so we can tell what is going on at runtime.
     *  <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    static Adapter createViewportModelChangeListener( final RenderManagerImpl manager,
            final Adapter viewportListener, final ContextModelListenerAdapter contextModelListener ) {
        return new AdapterImpl(){
            /**
             * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
             */
          
            public void notifyChanged( Notification msg ) {
                manager.checkState();

                switch( msg.getFeatureID(RenderManager.class) ) {
                case RenderPackage.RENDER_MANAGER__VIEWPORT_MODEL_INTERNAL: {
                    if (msg.getOldValue() != null)
                        ((ViewportModel) msg.getOldValue()).eAdapters().remove(viewportListener);
                    if (msg.getNewValue() != null)
                        ((ViewportModel) msg.getNewValue()).eAdapters().add(viewportListener);
                    break;
                }// case
                case RenderPackage.RENDER_MANAGER__MAP_INTERNAL: {
                    if (msg.getOldValue() != null)
                        ((Map) msg.getOldValue()).getContextModel().eAdapters().remove(
                                contextModelListener);
                    if (msg.getNewValue() != null)
                        ((Map) msg.getNewValue()).getContextModel().eAdapters().add(
                                contextModelListener);
                    break;
                }// case

                }
            }
        };
    }

    static Adapter createLayerListener( final RenderManagerDynamic manager ) {
        return new AdapterImpl(){
            /**
             * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
             */
            public void notifyChanged( Notification event ) {
                manager.checkState();

                if (event.getNotifier() instanceof Map) {
                    if (ProjectPackage.MAP__CONTEXT_MODEL == event.getFeatureID(Map.class)) {
                        if (event.getOldValue() != null) {
                            ((ContextModel) event.getOldValue()).removeDeepAdapter(this);
                        }
                        if (event.getNewValue() != null) {
                            ((ContextModel) event.getNewValue()).addDeepAdapter(this);
                        }
                    }
                } else if (event.getNotifier() instanceof Layer) {
                    switch( event.getFeatureID(Layer.class) ) {
                    case ProjectPackage.LAYER__FILTER:
                        filterChanged(manager, event);
                        break;
                    case ProjectPackage.LAYER__STYLE_BLACKBOARD:
                        //this is dealt with by a listener in the RenderExecutorImpl
                        synchronizeRenderers(event, manager.configuration, manager.getRendererCreator());
                        
                        if ((ILayer)event.getNotifier() instanceof SelectionLayer)
                          return;
                        manager.refresh((ILayer)event.getNotifier(), null);
                        break;
                    case ProjectPackage.LAYER__CRS:
                        if( event.getOldValue()==null || !event.getOldValue().equals(event.getNewValue())){
                            ((Layer)event.getNotifier()).refresh(null);
                        }
                        break;
                    case ProjectPackage.LAYER__FEATURE_CHANGES:
                        FeatureEvent featureEvent = (FeatureEvent) event.getNewValue();
                        if (featureEvent == null) {
                            break;
                        }
                        featureEvent(manager, event, featureEvent);
                        break;

                    default:
                        break;
                    }

                }
            }

            private void featureEvent( final RenderManagerDynamic manager, Notification event, FeatureEvent featureEvent ) {
                ReferencedEnvelope refreshBounds = null;
                ILayer refreshLayer = null;
                if(ProjectPlugin.getPlugin().getPluginPreferences().getBoolean(PreferenceConstants.P_FEATURE_EVENT_REFRESH_ALL)){
                    ILayer notifier = (ILayer) event.getNotifier();
                    ReferencedEnvelope viewportBounds = notifier.getMap().getViewportModel().getBounds();
                    
                    refreshLayer = notifier;
                    refreshBounds = viewportBounds;
                }else{
                	Envelope delta = featureEvent.getBounds();
                	if( delta != null ){
                        ILayer notifier = (ILayer) event.getNotifier();
                        if( delta.isNull() ){
                            // change to null because renderer treat null as the
							// entire viewport but don't make
                            // the same assumption for NULL envelope.
                            manager.refresh(notifier, null);
                        }
                        else{
                        	try {                                
	                            MathTransform layerToMapTransform = notifier.layerToMapTransform();
	                            Envelope mapDelta =new Envelope();
	                            if( layerToMapTransform.isIdentity() ){
	                            	mapDelta = delta;
	                            }
	                            else {
	                                JTS.transform(delta, mapDelta, layerToMapTransform, 10);
	                            }
	                            CoordinateReferenceSystem mapCRS = notifier.getMap().getViewportModel().getCRS();
	                            ReferencedEnvelope bounds = new ReferencedEnvelope( mapDelta, mapCRS );
	                            
	                            bounds.expandBy(bounds.getWidth()*.2, bounds.getHeight()*.2);
	                            refreshBounds = bounds;
	                            refreshLayer = notifier;	                            
                            } catch (IOException e) {
                                ProjectPlugin.log("", e); //$NON-NLS-1$
                            } catch (TransformException e) {
                                ProjectPlugin.log("", e); //$NON-NLS-1$
                            }
                            
							// if (notifier.isVisible()) {
							// manager.refresh(notifier, bounds);
							// }
                        }
                    }
                }
                
                if(refreshLayer != null && refreshLayer.isVisible() && refreshBounds != null){
                    manager.refresh(refreshLayer, refreshBounds);
                }                    
            }

            private void filterChanged( final RenderManagerDynamic manager, Notification event ) {
                if( !event.getOldValue().equals(event.getNewValue())){
                	Filter newFilter = (Filter)event.getNewValue();
                	
                	if(Filter.EXCLUDE.equals(newFilter))
                		manager.clearSelection((Layer) event.getNotifier());
                	else
                		manager.refreshSelection((Layer) event.getNotifier(), null);
                }
            }
        };
    }
    /**
     * This one actually kicks the viewport pane to redraw as rendering events
     * occur.
     */
    static class RenderExecutorListener extends RenderListenerAdapter {

        private RenderManager manager;

        /**
         * Construct <code>RenderManagerAdapters.RenderExecutorListener</code>.
         */
        public RenderExecutorListener( RenderManager manager ) {
            this.manager = manager;
        }

        /**
         * @see org.locationtech.udig.project.render.RenderListenerAdapter#renderStarting()
         */
        protected void renderStarting() {
            ((ViewportPane) manager.getMapDisplay()).renderStarting();
        }

        /**
         * @see org.locationtech.udig.project.render.RenderListenerAdapter#renderUpdate()
         */
        protected void renderUpdate() {
            ((ViewportPane) manager.getMapDisplay()).renderUpdate();
        }

        /**
         * @see org.locationtech.udig.project.render.RenderListenerAdapter#renderDone()
         */
        protected void renderDone() {
            ((ViewportPane) manager.getMapDisplay()).renderDone();
        }
    }

    static RenderExecutorListener getRenderExecutorListener( RenderManager manager ) {
        return new RenderExecutorListener(manager);
    }

    
    
    
    
    /**
     * Creates a layer listener for a tiled render manager.
     * 
     * <p> This listener listens to the following events:
     * <ul>
     *   <li>MAP__CONTEXT_MODEL
     *   <li>LAYER_FILTER
     *   <li>LAYER_STYLE_BLACKBOARD
     *   <li>LAYER_CRS
     *   <li>LAYER__FEATURE_CHANGES 
     * </ul>.
     * </p>
     * 
     */
    static Adapter createLayerListener( final TiledRenderManagerDynamic manager ) {
        return new AdapterImpl(){
            /**
             * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
             */
            public void notifyChanged( Notification event ) {
                manager.checkState();

                if (event.getNotifier() instanceof Map) {
                    if (ProjectPackage.MAP__CONTEXT_MODEL == event.getFeatureID(Map.class)) {
                        if (event.getOldValue() != null) {
                            ((ContextModel) event.getOldValue()).removeDeepAdapter(this);
                        }
                        if (event.getNewValue() != null) {
                            ((ContextModel) event.getNewValue()).addDeepAdapter(this);
                        }
                    }
                } else if (event.getNotifier() instanceof Layer) {
                    switch( event.getFeatureID(Layer.class) ) {
                    case ProjectPackage.LAYER__FILTER:
                        filterChanged(manager, event);
                        break;
                    case ProjectPackage.LAYER__STYLE_BLACKBOARD:
                        //This event is dealt with by the LayerListener in the RenderExecutorImpl
                        //We do not need to also process this event here.
                        Layer layer = (Layer)event.getNotifier();
                        manager.blackBoardChanged(layer);
                        break;
                    case ProjectPackage.LAYER__CRS:
                        if( event.getOldValue()==null || !event.getOldValue().equals(event.getNewValue())){
                            ((Layer)event.getNotifier()).refresh(null);
                        }
                        break;
                    case ProjectPackage.LAYER__FEATURE_CHANGES:
                        FeatureEvent featureEvent = (FeatureEvent) event.getNewValue();
                        if (featureEvent == null) {
                            break;
                        }
                        featureEvent(manager, event, featureEvent);
                        break;

                    default:
                        break;
                    }

                }
            }

            private void featureEvent( final TiledRenderManagerDynamic manager, Notification event, FeatureEvent featureEvent ) {
                ReferencedEnvelope refreshBounds = null;
                ILayer refreshLayer = null;
                if(ProjectPlugin.getPlugin().getPluginPreferences().getBoolean(PreferenceConstants.P_FEATURE_EVENT_REFRESH_ALL)){
                    ILayer notifier = (ILayer) event.getNotifier();
                    ReferencedEnvelope viewportBounds = notifier.getMap().getViewportModel().getBounds();
                    
                    refreshLayer = notifier;
                    refreshBounds = viewportBounds;
                }else{
                    Envelope delta = featureEvent.getBounds();
                    if( delta != null ){
                        ILayer notifier = (ILayer) event.getNotifier();
                        if( delta.isNull() ){
                            // change to null because renderer treat null as the
                            // entire viewport but don't make
                            // the same assumption for NULL envelope.
                            //TODO: can we do a soft refresh here?
                            manager.refresh(notifier, null);
                        }
                        else{
                            try {                                
                                MathTransform layerToMapTransform = notifier.layerToMapTransform();
                                Envelope mapDelta =new Envelope();
                                if( layerToMapTransform.isIdentity() ){
                                    mapDelta = delta;
                                }
                                else {
                                    JTS.transform(delta, mapDelta, layerToMapTransform, 10);
                                }
                                CoordinateReferenceSystem mapCRS = notifier.getMap().getViewportModel().getCRS();
                                ReferencedEnvelope bounds = new ReferencedEnvelope( mapDelta, mapCRS );
                                
                                bounds.expandBy(bounds.getWidth()*.2, bounds.getHeight()*.2);
                                refreshBounds = bounds;
                                refreshLayer = notifier;                                
                            } catch (IOException e) {
                                ProjectPlugin.log("", e); //$NON-NLS-1$
                            } catch (TransformException e) {
                                ProjectPlugin.log("", e); //$NON-NLS-1$
                            }
                            
                            // if (notifier.isVisible()) {
                            // manager.refresh(notifier, bounds);
                            // }
                        }
                    }
                }
                
                if(refreshLayer != null && refreshLayer.isVisible() && refreshBounds != null){
                    //refresh particular layer
                    manager.refresh(refreshLayer, refreshBounds);
                }                    
            }

            private void filterChanged( final TiledRenderManagerDynamic manager, Notification event ) {
                if( !event.getOldValue().equals(event.getNewValue())){
                    Filter newFilter = (Filter)event.getNewValue();
                    
                    if(Filter.EXCLUDE.equals(newFilter)){
                        manager.clearSelection((Layer) event.getNotifier());
                    }else{
                        //refresh selection layer
                        manager.refreshSelection((Layer) event.getNotifier(), null);
                    }
                }
            }
        };
    }

    /**
     * Creates an adapter that listens for
     * visibility changes in layers.  When a layer
     * visibility change occurs the tiles
     * in the tiles render manager need to be invalidated
     * appropriately.
     *
     * @param manager
     * @return
     */
    static Adapter createVisibilityChangedAdapater(final TiledRenderManagerDynamic manager){
        return new AdapterImpl(){
            public void notifyChanged( final Notification msg ) {
              if (msg.getNotifier() instanceof Layer && msg.getFeatureID(Layer.class) == ProjectPackage.LAYER__VISIBLE) {
                  if (msg.getNewBooleanValue() != msg.getOldBooleanValue()){
                      manager.layerMadeVisible((Layer)msg.getNotifier());
                  }
              }
          }
        };
    }
            
    
    /**
     * Creates a new adapter for dealing with zorder changes, layers added and removed.
     * 
     * @param manager 
     * @returnContext
     */
    static ContextModelListenerAdapter createContextModelListener(
            final TiledRenderManagerDynamic manager ) {
        
        return new ContextModelListenerAdapter(){
            /**
             * @see org.locationtech.udig.project.ContextModelListenerAdapter#commandExecuted(org.eclipse.emf.common.notify.Notification)
             */
            public void notifyChanged( Notification msg ) {
                super.notifyChanged(msg);
            }

            /**
             * Will sychronizeAndRefresh( the manager based on the notification; and
             * updateImage().
             * 
             * @see org.locationtech.udig.project.ContextModelListenerAdapter#zorderChanged(org.eclipse.emf.common.notify.Notification)
             */
            protected void zorderChanged( Notification msg ) {
                manager.zorderChanged(msg);
            }

            /**
             * @see org.locationtech.udig.project.ContextModelListenerAdapter#layerAdded(org.eclipse.emf.common.notify.Notification)
             */
            protected void layerAdded( Notification msg ) {
                manager.layersAdded(msg);
            }

            /**
             * @see org.locationtech.udig.project.ContextModelListenerAdapter#layerRemoved(org.eclipse.emf.common.notify.Notification)
             */
            protected void layerRemoved( Notification msg ) {
                manager.layersRemoved(msg);
            }

            /**
             * @see org.locationtech.udig.project.ContextModelListenerAdapter#manyLayersAdded(org.eclipse.emf.common.notify.Notification)
             */
            protected void manyLayersAdded( Notification msg ) {
                manager.layersAdded(msg);
            }

            /**
             * @see org.locationtech.udig.project.ContextModelListenerAdapter#manyLayersRemoved(org.eclipse.emf.common.notify.Notification)
             */
            protected void manyLayersRemoved( Notification msg ) {
                manager.layersRemoved(msg);
            }
        };
    }
}
