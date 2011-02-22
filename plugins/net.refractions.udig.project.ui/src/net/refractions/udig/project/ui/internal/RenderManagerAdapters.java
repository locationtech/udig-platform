/*
 * Created on Nov 24, 2004 TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package net.refractions.udig.project.ui.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.ContextModel;
import net.refractions.udig.project.internal.ContextModelListenerAdapter;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.render.CompositeRenderContext;
import net.refractions.udig.project.internal.render.MultiLayerRenderer;
import net.refractions.udig.project.internal.render.RenderContext;
import net.refractions.udig.project.internal.render.RenderListenerAdapter;
import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.internal.render.RenderPackage;
import net.refractions.udig.project.internal.render.RendererCreator;
import net.refractions.udig.project.internal.render.SelectionLayer;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.internal.render.impl.RenderManagerImpl;
import net.refractions.udig.project.preferences.PreferenceConstants;
import net.refractions.udig.project.render.RenderException;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.geotools.data.FeatureEvent;
import org.geotools.filter.Filter;
import org.geotools.geometry.jts.JTS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;

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
                    if( ApplicationGIS.getActiveMap()!=null && ApplicationGIS.getVisibleMaps().contains(ApplicationGIS.getActiveMap()) )
                        refreshDirtyArea(msg);
                    break;
                }
                case RenderPackage.VIEWPORT_MODEL__CRS: {
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

    static ContextModelListenerAdapter createContextModelListener(
            final RenderManagerDynamic manager ) {
        return new ContextModelListenerAdapter(){
            /**
             * @see net.refractions.udig.project.ContextModelListenerAdapter#commandExecuted(org.eclipse.emf.common.notify.Notification)
             */
            public void notifyChanged( Notification msg ) {
                super.notifyChanged(msg);
            }

            /**
             * @see net.refractions.udig.project.ContextModelListenerAdapter#zorderChanged(org.eclipse.emf.common.notify.Notification)
             */
            protected void zorderChanged( Notification msg ) {
                synchronizeAndRefresh(msg, manager);

                updateImage();
            }

            /**
             * @see net.refractions.udig.project.ContextModelListenerAdapter#layerAdded(org.eclipse.emf.common.notify.Notification)
             */
            protected void layerAdded( Notification msg ) {
                synchronizeAndRefresh(msg, manager);
                updateImage();
            }

            /**
             * @see net.refractions.udig.project.ContextModelListenerAdapter#layerRemoved(org.eclipse.emf.common.notify.Notification)
             */
            protected void layerRemoved( Notification msg ) {
                synchronizeAndRefresh(msg, manager);
                manager.validateRendererConfiguration();
                updateImage();
            }

            /**
             * @see net.refractions.udig.project.ContextModelListenerAdapter#manyLayersAdded(org.eclipse.emf.common.notify.Notification)
             */
            protected void manyLayersAdded( Notification msg ) {
                synchronizeAndRefresh(msg, manager);
                updateImage();
            }

            /**
             * @see net.refractions.udig.project.ContextModelListenerAdapter#manyLayersRemoved(org.eclipse.emf.common.notify.Notification)
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

    private static void synchronizeAndRefresh(Notification msg, RenderManagerDynamic manager) {
        List<Layer> toRender = synchronizeRenderers(msg, manager.configuration, manager.getRendererCreator());
        for( Layer layer : toRender ) {
            layer.refresh(null);
        }
    }

    public static List<Layer> synchronizeRenderers( final Notification msg, final Collection<RenderContext> configuration,
            final RendererCreator rendererCreator) {

        HashMap<RenderContext, RenderContext> oldToCopy = new HashMap<RenderContext, RenderContext>();
        Collection<RenderContext> configuration2=configuration;
        if (configuration2 != null)
            for( RenderContext context : configuration2 ) {
                oldToCopy.put(context, context.copy());
            }

        rendererCreator.changed(msg);

        configuration2=rendererCreator.getConfiguration();
        List<Layer> toRender = new ArrayList<Layer>();
        for( RenderContext newcontext : configuration2 ) {
            if (!oldToCopy.containsKey(newcontext)
                    && !(newcontext.getLayer() instanceof SelectionLayer)) {
                toRender.add(newcontext.getLayerInternal());
            } else {
                if (newcontext instanceof CompositeRenderContext) {
                    List<Layer> oldLayers = new ArrayList<Layer>(((CompositeRenderContext) oldToCopy.get(newcontext))
                            .getLayersInternal());
                    for( Layer layer : ((CompositeRenderContext) newcontext).getLayersInternal() ) {
                        if (!oldLayers.contains(layer)) {
                            toRender.add(newcontext.getLayerInternal());
                            break;
                        }
                        oldLayers.remove(layer);
                        // XXX
                    }
                    if (!oldLayers.isEmpty())
                        toRender.add(newcontext.getLayerInternal());
                }
                oldToCopy.remove(newcontext);
            }
        }
        return toRender;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    static Adapter createViewportModelChangeListener( final RenderManagerImpl manager,
            final Adapter viewportListener, final ContextModelListenerAdapter contextModelListener ) {
        return new AdapterImpl(){
            /**
             * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
             */
            @SuppressWarnings("unchecked")
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
                        synchronizeRenderers(event, manager.configuration, manager.getRendererCreator());
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
                Envelope refreshBounds = null;
                ILayer refreshLayer = null;
                if(ProjectPlugin.getPlugin().getPluginPreferences().getBoolean(PreferenceConstants.P_FEATURE_EVENT_REFRESH_ALL)){
                    ILayer notifier = (ILayer) event.getNotifier();
                    Envelope viewportBounds = notifier.getMap().getViewportModel().getBounds();

                    refreshLayer = notifier;
                    refreshBounds = viewportBounds;
                }else{
                    Envelope bounds = featureEvent.getBounds();
                    if (bounds != null ){
                        ILayer notifier = (ILayer) event.getNotifier();
                        if( bounds.isNull() ){
                            // change to null because renderer treat null as the entire viewport but don't make
                            // the same assumption for NULL envelope.
                            manager.refresh(notifier, null);
                        }else{
                            try {
                                MathTransform layerToMapTransform = notifier.layerToMapTransform();
                                if( !layerToMapTransform.isIdentity() ){
                                    Envelope newBounds=new Envelope();
                                    JTS.transform(bounds, newBounds, layerToMapTransform, 10);
                                    bounds=newBounds;
                                }
                            } catch (IOException e) {
                                ProjectPlugin.log("", e); //$NON-NLS-1$
                            } catch (TransformException e) {
                                ProjectPlugin.log("", e); //$NON-NLS-1$
                            }
                            bounds.expandBy(bounds.getWidth()*.2, bounds.getHeight()*.2);
                            refreshBounds = bounds;
                            refreshLayer = notifier;
//                          if (notifier.isVisible()) {
//                          manager.refresh(notifier, bounds);
//                          }
                        }
                    }
                }

                if(refreshLayer.isVisible() && refreshLayer != null && refreshBounds != null)
                    manager.refresh(refreshLayer, refreshBounds);

            }

            private void filterChanged( final RenderManagerDynamic manager, Notification event ) {
                if( !event.getOldValue().equals(event.getNewValue())){
                	Filter newFilter = (Filter)event.getNewValue();

                	if(Filter.ALL.equals(newFilter))
                		manager.clearSelection((Layer) event.getNotifier());
                	else
                		manager.refreshSelection((Layer) event.getNotifier(), null);
                }
            }
        };
    }

    static class RenderExecutorListener extends RenderListenerAdapter {

        private RenderManager manager;

        /**
         * Construct <code>RenderManagerAdapters.RenderExecutorListener</code>.
         */
        public RenderExecutorListener( RenderManager manager ) {
            this.manager = manager;
        }

        /**
         * @see net.refractions.udig.project.render.RenderListenerAdapter#renderStarting()
         */
        protected void renderStarting() {
            ((ViewportPane) manager.getMapDisplay()).renderStarting();
        }

        /**
         * @see net.refractions.udig.project.render.RenderListenerAdapter#renderUpdate()
         */
        protected void renderUpdate() {
            ((ViewportPane) manager.getMapDisplay()).renderUpdate();
        }

        /**
         * @see net.refractions.udig.project.render.RenderListenerAdapter#renderDone()
         */
        protected void renderDone() {
            ((ViewportPane) manager.getMapDisplay()).renderDone();
        }
    }

    static RenderExecutorListener getRenderExecutorListener( RenderManager manager ) {
        return new RenderExecutorListener(manager);
    }

}
