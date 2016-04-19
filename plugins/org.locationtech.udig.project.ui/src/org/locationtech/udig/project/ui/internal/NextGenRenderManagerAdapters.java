package org.locationtech.udig.project.ui.internal;


import java.io.IOException;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.geotools.data.FeatureEvent;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.ContextModel;
import org.locationtech.udig.project.internal.ContextModelListenerAdapter;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.MultiLayerRenderer;
import org.locationtech.udig.project.internal.render.RenderListenerAdapter;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.internal.render.RenderPackage;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.internal.render.impl.RenderManagerImpl;
import org.locationtech.udig.project.internal.render.impl.RenderTask;
import org.locationtech.udig.project.internal.render.impl.RenderTaskType;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.project.render.RenderException;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Provides factory methods for various Listeners used by RenderManagerDynamic.
 */

public class NextGenRenderManagerAdapters {

    static Adapter createViewportListener( final NextGenRenderManager manager ) {
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
            final NextGenRenderManager manager ) {
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
                RenderTask task = new RenderTask(RenderTaskType.LAYER_ZORDER_CHANGED, (ILayer)msg.getNewValue());
                task.setEvent(msg);
                manager.queueRenderTask(task);
                
                manager.scheduleQueueJob();
//                manager.synchronizeAndRefresh(msg);
//                updateImage();
            }

            /**
             * @see org.locationtech.udig.project.ContextModelListenerAdapter#layerAdded(org.eclipse.emf.common.notify.Notification)
             */
            protected void layerAdded( Notification msg ) {
                RenderTask task = new RenderTask(RenderTaskType.LAYER_ADDED, (ILayer)msg.getNewValue());
                task.setEvent(msg);
                manager.queueRenderTask(task);
                
                manager.scheduleQueueJob();
//                manager.synchronizeAndRefresh(msg);
//                updateImage();
            }

            /**
             * @see org.locationtech.udig.project.ContextModelListenerAdapter#layerRemoved(org.eclipse.emf.common.notify.Notification)
             */
            protected void layerRemoved( Notification msg ) {
                RenderTask task = new RenderTask(RenderTaskType.LAYER_REMOVED, (ILayer)msg.getOldValue());
                task.setEvent(msg);
                manager.queueRenderTask(task);
                
                manager.scheduleQueueJob();
//                manager.synchronizeAndRefresh(msg);
//                manager.validateRendererConfiguration();
//                updateImage();
            }

            /**
             * @see org.locationtech.udig.project.ContextModelListenerAdapter#manyLayersAdded(org.eclipse.emf.common.notify.Notification)
             */
            protected void manyLayersAdded( Notification msg ) {
                RenderTask task = new RenderTask(RenderTaskType.LAYER_ADDED, (List<ILayer>)msg.getNewValue());
                task.setEvent(msg);
                manager.queueRenderTask(task);
                
                manager.scheduleQueueJob();
                
//                manager.synchronizeAndRefresh(msg);
//                updateImage();
            }

            /**
             * @see org.locationtech.udig.project.ContextModelListenerAdapter#manyLayersRemoved(org.eclipse.emf.common.notify.Notification)
             */
            protected void manyLayersRemoved( Notification msg ) {
                RenderTask task = new RenderTask(RenderTaskType.LAYER_REMOVED, (List<ILayer>)msg.getOldValue());
                task.setEvent(msg);
                manager.queueRenderTask(task);
                
                manager.scheduleQueueJob();
                
//                manager.synchronizeAndRefresh(msg);
//                manager.validateRendererConfiguration();
//                updateImage();
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
    
    
//    /**
//     * This private method is called by listeners (such as the context model listener)
//     * in order to kick layers into life.
//     * <p>
//     * This implementation hunts down a layer can calls layer.refresh( null ) in order
//     * to ask it to redraw pretty much everything it has... the synchronizeRenderers
//     * will hunt down the list of layers effected by this notification....scared.
//     * <p>
//     * 
//     * @param msg
//     * @param manager
//     */
//    private static void synchronizeAndRefresh(Notification msg, NextGenRenderManager manager) {
//        List<Layer> toRender = synchronizeRenderers(msg, manager.configuration, manager.getRendererCreator());
//        for( Layer layer : toRender ) {
//            layer.refresh(null);
//        }
//    }
    

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

    static Adapter createLayerListener( final NextGenRenderManager manager ) {
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
                        styleChanged(manager, event);
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

            private void featureEvent( final NextGenRenderManager manager, Notification event, FeatureEvent featureEvent ) {
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
                
                //TODO
                if(refreshLayer != null && refreshLayer.isVisible() && /* !refreshLayer.isChangingInBulk()  && */ refreshBounds != null){
                    manager.refresh(refreshLayer, refreshBounds);
                }
            }

            private void filterChanged( final NextGenRenderManager manager, Notification event ) {
                if( !event.getOldValue().equals(event.getNewValue())){
                    Filter newFilter = (Filter)event.getNewValue();
                    
                    if(Filter.EXCLUDE.equals(newFilter))
                        manager.clearSelection((Layer) event.getNotifier());
                    else
                        manager.refreshSelection((Layer) event.getNotifier(), null);
                }
            }
            
            private void styleChanged(final NextGenRenderManager manager, Notification event){
                
                
                RenderTask task = new RenderTask(RenderTaskType.LAYER_STATE_CHANGED, (ILayer)event.getNotifier());
                task.setEvent(event);
                manager.queueRenderTask(task);
                
                manager.scheduleQueueJob();
                
//                //this is dealt with by a listener in the RenderExecutorImpl
//                manager.synchronizeRenderers(event);
//                
//                if ((ILayer)event.getNotifier() instanceof SelectionLayer)
//                  return;
//                manager.refresh((ILayer)event.getNotifier(), null);
                
                
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
            
    
}
