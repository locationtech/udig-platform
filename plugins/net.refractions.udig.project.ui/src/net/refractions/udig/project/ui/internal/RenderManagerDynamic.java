/**
 * <copyright></copyright> $Id: RenderManagerDynamic.java 28204 2007-12-01 01:00:14Z jeichar $
 */
package net.refractions.udig.project.ui.internal;

import static net.refractions.udig.project.ui.internal.Trace.RENDER;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.ContextModelListenerAdapter;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectPackage;
import net.refractions.udig.project.internal.render.CompositeRenderContext;
import net.refractions.udig.project.internal.render.ExecutorVisitor;
import net.refractions.udig.project.internal.render.MultiLayerRenderer;
import net.refractions.udig.project.internal.render.RenderContext;
import net.refractions.udig.project.internal.render.RenderExecutor;
import net.refractions.udig.project.internal.render.RenderFactory;
import net.refractions.udig.project.internal.render.RenderPackage;
import net.refractions.udig.project.internal.render.SelectionLayer;
import net.refractions.udig.project.internal.render.impl.CompositeRenderContextImpl;
import net.refractions.udig.project.internal.render.impl.CompositeRendererImpl;
import net.refractions.udig.project.internal.render.impl.RenderExecutorComposite;
import net.refractions.udig.project.internal.render.impl.RenderExecutorMultiLayer;
import net.refractions.udig.project.internal.render.impl.RenderManagerImpl;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderer;
import net.refractions.udig.project.render.RenderException;
import net.refractions.udig.project.render.displayAdapter.IMapDisplay;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.geotools.geometry.jts.ReferencedEnvelope;

import com.vividsolutions.jts.geom.Envelope;

/**
 * An implementation of IRenderManager that is reacts to events such as viewport model changes.
 *
 * @generated
 */
public class RenderManagerDynamic extends RenderManagerImpl {

    ContextModelListenerAdapter contextModelAdapter = RenderManagerAdapters
            .createContextModelListener(this);

    Adapter viewportListener = RenderManagerAdapters.createViewportListener(this);

    private Adapter viewportModelChangeListener = RenderManagerAdapters
            .createViewportModelChangeListener(this, viewportListener, contextModelAdapter);

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @SuppressWarnings("unchecked")
    public RenderManagerDynamic() {
        super();
        eAdapters().add(viewportModelChangeListener);
    }

    /**
     * @see net.refractions.udig.project.render.impl.RenderManagerImpl#refresh(net.refractions.udig.project.Layer)
     */
    public void refresh( final ILayer layer, final Envelope bounds ) {
        checkState();
        if( !renderingEnabled ){
        	return;
        }
        if (getMapDisplay() == null || getRenderExecutor() == null)
            return;

        getRendererCreator().reset();
        validateRendererConfiguration();

        final SelectionLayer selectionLayer = getRendererCreator().findSelectionLayer(layer);

        getRenderExecutor().visit(new ExecutorVisitor(){
            public void visit( RenderExecutor executor ) {
                if (executor.getContext().getLayer() == layer
                        || selectionLayer == executor.getContext().getLayer()){
                    executor.getRenderer().setRenderBounds(bounds);
                    executor.getRenderer().setState(IRenderer.RENDER_REQUEST);
                }
            }

            public void visit( RenderExecutorMultiLayer executor ) {
                if (executor.getContext().getLayers().contains(layer)
                        || executor.getContext().getLayers().contains(selectionLayer))
                    executor.getRenderer().setState(IRenderer.RENDER_REQUEST);
            }

            public void visit( RenderExecutorComposite executor ) {
                List<RenderExecutor> executors = new ArrayList<RenderExecutor>(executor
                        .getRenderer().getRenderExecutors());
                for( RenderExecutor child : executors )
                    child.visit(this);
            }
        });
    }


    /**
     * @see net.refractions.udig.project.render.impl.RenderManagerImpl#refreshSelection(com.vividsolutions.jts.geom.Envelope)
     */
    public void refreshSelection( final ILayer layer, final Envelope bounds ) {
        checkState();
        if( !renderingEnabled ){
        	return;
        }

        if (getMapDisplay() == null || getRenderExecutor() == null)
            return;

        final Layer selectionLayer = getRendererCreator().findSelectionLayer(layer);

        if (selectionLayer == null)
            return;

        getRendererCreator().reset();
        validateRendererConfiguration();

        getRenderExecutor().visit(new ExecutorVisitor(){
            public void visit( RenderExecutor executor ) {
                IRenderContext context = executor.getContext();
                if (selectionLayer == context.getLayer()){
                    executor.getRenderer().setRenderBounds(bounds);
                    if (bounds!=null ){
                        Rectangle bounds2 = context.toShape(new ReferencedEnvelope(bounds, getViewportModelInternal().getCRS())).getBounds();
                        context.clearImage(bounds2);
                    }else{
                        context.clearImage();
                    }
                    executor.getRenderer().setState(IRenderer.RENDER_REQUEST);
                }
            }

            public void visit( RenderExecutorMultiLayer executor ) {
                CompositeRenderContext contexts = executor.getContext();
                for( IRenderContext context : contexts.getContexts() ) {
                    if (context.getLayer() == selectionLayer) {
                        executor.getRenderer().setState(IRenderer.RENDER_REQUEST);
                        return;
                    }
                }
            }

            public void visit( RenderExecutorComposite executor ) {
                for( RenderExecutor child : executor.getRenderer().getRenderExecutors() )
                    child.visit(this);
            }
        });
    }

    /**
     * @see net.refractions.udig.project.render.IRenderManager#clearSelection(ILayer)
     */
    public void clearSelection( ILayer layer) {
        checkState();
        if (getMapDisplay() == null || getRenderExecutor() == null)
            return;
        final Layer selectionLayer = getRendererCreator().findSelectionLayer(layer);

        if (selectionLayer == null)
            return;

        try {
            CompositeRendererImpl compositeRendererImpl = (CompositeRendererImpl) getRenderExecutor().getRenderer();
            compositeRendererImpl.refreshImage();
            compositeRendererImpl.setState(IRenderer.DONE);

        } catch (RenderException e) {
            ProjectUIPlugin.log("", e); //$NON-NLS-1$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @SuppressWarnings("unchecked")
    public void refresh( final Envelope bounds ) {
        checkState();
        if( !renderingEnabled ){
        	return;
        }
        getViewportModelInternal().setInitialized(true);
        if (getMapDisplay() == null) {
            return;
        }
        if (getMapDisplay().getWidth() < 1 || getMapDisplay().getHeight() < 1)
            return;

        getRendererCreator().reset();
        validateRendererConfiguration();

        if (!getMapInternal().getContextModel().eAdapters().contains(contextModelAdapter))
            getMapInternal().getContextModel().eAdapters().add(contextModelAdapter);

        try {
            getRenderExecutor().setRenderBounds(bounds);
            getRenderExecutor().render();
        } catch (RenderException e) {
            // Won't happen
        }
    }

    void initRenderCreator( RenderContext context ) {
        checkState();
        List<Layer> layers = getMapInternal().getLayersInternal();
            // make sure renderer creator is initialized.
        getRendererCreator().setContext(context);
        ENotificationImpl notification = new ENotificationImpl(this, Notification.ADD_MANY,
                ProjectPackage.CONTEXT_MODEL__LAYERS, null, layers);
        getRendererCreator().changed(notification);


    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @SuppressWarnings("unchecked")
    public void dispose() {
        configuration = null;
        Set<EObject> set = new HashSet<EObject>();
        set.add(getMapInternal());
        Iterator<EObject> iter = getMapInternal().eAllContents();
        while( iter.hasNext() ) {
            EObject obj = iter.next();
            removeAdapters(obj);
        }
        removeAdapters(getRenderExecutor());
        ((ViewportPane)mapDisplay).setRenderManager(null);
        super.dispose();
    }

    /**
     * @param obj
     */
    private void removeAdapters( EObject obj ) {
        obj.eAdapters().remove(this.viewportListener);
        obj.eAdapters().remove(this.viewportModelChangeListener);
        obj.eAdapters().remove(this.contextModelAdapter);
        obj.eAdapters().remove(this.renderExecutorListener);
        obj.eAdapters().remove(this.selectionListener);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public void setDisplayGen( IMapDisplay newDisplay ) {
        IMapDisplay oldDisplay = mapDisplay;
        mapDisplay = newDisplay;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    RenderPackage.RENDER_MANAGER__MAP_DISPLAY, oldDisplay, mapDisplay));
    }

    /**
     * TODO summary sentence for setViewport ...
     *
     * @see net.refractions.udig.project.render.RenderManager#setDisplay(net.refractions.udig.project.render.displayAdapter.IMapDisplay)
     * @param value
     */
    public void setDisplay( IMapDisplay value ) {
        checkState();
        ((ViewportPane) value).setRenderManager(this);
        setDisplayGen(value);
    }

    Adapter renderExecutorListener = RenderManagerAdapters.getRenderExecutorListener(this);

    /**
     * @see net.refractions.udig.project.render.impl.RenderManagerImpl#setRenderExecutor(net.refractions.udig.project.render.RenderExecutor)
     */
    @SuppressWarnings("unchecked")
    public void setRenderExecutor( RenderExecutor newRenderExecutor ) {
        checkState();
        if (renderExecutor != null) {
            renderExecutor.eAdapters().remove(renderExecutorListener);
        }
        if (newRenderExecutor != null
                && !newRenderExecutor.eAdapters().contains(renderExecutorListener)) {
            newRenderExecutor.eAdapters().add(renderExecutorListener);
        }
        super.setRenderExecutor(newRenderExecutor);
    }

    Adapter selectionListener = RenderManagerAdapters.createLayerListener(this);

    /**
     * @see net.refractions.udig.project.render.impl.RenderManagerImpl#basicSetMap(net.refractions.udig.project.Map,
     *      org.eclipse.emf.common.notify.NotificationChain)
     */
    @SuppressWarnings("unchecked")
    public NotificationChain basicSetMapInternal( Map newMap, NotificationChain msgs ) {
        if (getMapInternal() != null) {
            getMapInternal().eAdapters().remove(selectionListener);
            getMapInternal().removeDeepAdapter(selectionListener);
        }
        if (newMap != null) {
            newMap.eAdapters().add(selectionListener);
            newMap.addDeepAdapter(selectionListener);
        }
        return super.basicSetMapInternal(newMap, msgs);
    }

    public volatile Collection<RenderContext> configuration = null;

    /**
     * Ensures that the current configuration of renderer is a valid choice. For example the each of
     * the layers in the map has a renderer that can render it.
     */
    public void validateRendererConfiguration() {
        checkState();
        Collection<RenderContext> configuration;
        synchronized (this) {
            if( this.configuration==null ){
                configuration=null;
            }else{
                configuration = new ArrayList<RenderContext>(this.configuration);
            }
        }
        Collection<RenderContext> configuration2;
        if (configuration != null) {
            configuration2 = getRendererCreator().getConfiguration();
            List<RenderContext> removeList = new ArrayList<RenderContext>();
            List<RenderContext> addList = new ArrayList<RenderContext>();
            for( IRenderContext context : configuration ) {
                if (!configuration2.contains(context))
                    removeList.add((RenderContext) context);
            }
            for( RenderContext context : configuration2 ) {
                if (configuration.contains(context))
                    continue;

                addList.add(context);
            }
            CompositeRenderContext compositecontext = (CompositeRenderContext) getRenderExecutor()
            .getContext();
            compositecontext.removeContexts(removeList);
            if( !addList.isEmpty() )
            compositecontext.removeContexts(addList);
            compositecontext.addContexts(addList);

        } else {
            initRenderExecutor();
            configuration2=getRendererCreator().getConfiguration();

            CompositeRenderContext compositecontext = (CompositeRenderContext) getRenderExecutor()
            .getContext();
            // need this because this is taking place in a non-synchronized block so it is possible for
            // this code to be executed twice.  I want the second run to be accurate.
            // might need to be thought about more.
            compositecontext.clear();
            compositecontext.addContexts(configuration2);
        }
        synchronized (this) {
            this.configuration = configuration2;
        }
        logRendererTypes();

    }

    /**
     *
     */
    private void initRenderExecutor() {
        checkState();
        MultiLayerRenderer renderExecutor = RenderFactory.eINSTANCE.createCompositeRenderer();
        CompositeRenderContext context = new CompositeRenderContextImpl(){
            @Override
            public synchronized BufferedImage getImage( int width, int height ) {
                if (image == null || image.getWidth() < width || image.getHeight() < height) {
                    image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
                    Graphics2D g = image.createGraphics();
                    g.setBackground(Color.WHITE);
                    g.clearRect(0, 0, width, height);
                    g.dispose();
                }

                return image;
            }
            @Override
            public synchronized void clearImage( Rectangle paintArea ) {
                if (image == null)
                    return;
                Graphics2D g = image.createGraphics();
                g.setBackground(Color.WHITE);
                g.clearRect(paintArea.x, paintArea.y, paintArea.width, paintArea.height);
                g.dispose();
////              FIXME Arbonaut Oy , Vitali Diatchkov
//                System.out.println("synchronized CompositeRenderContext.clearImage()");
            }

        };
        context.setMapInternal(getMapInternal());
        context.setRenderManagerInternal(this);
        renderExecutor.setContext(context);

        initRenderCreator(context);

        renderExecutor.setName(Messages.RenderManagerDynamic_allLayers);
        setRenderExecutor(RenderFactory.eINSTANCE.createRenderExecutor(renderExecutor));
    }

    private void logRendererTypes() {
        if (ProjectUIPlugin.isDebugging(RENDER)) {

            final StringBuffer log = new StringBuffer("Current Renderers:"); //$NON-NLS-1$
            getRenderExecutor().visit(new ExecutorVisitor(){
                public void visit( RenderExecutor executor ) {
                        log.append("\n\t" + executor.getClass().getSimpleName() + ":" +  //$NON-NLS-1$ //$NON-NLS-2$
                                executor.getRenderer().getClass().getSimpleName() + "-" +  //$NON-NLS-1$
                                executor.getContext().getLayer().getName());
                }

                public void visit( RenderExecutorComposite executor ) {
                    log.append("\n\t" + executor.getRenderer().getClass().getSimpleName()); //$NON-NLS-1$
                    for( RenderExecutor child : executor.getRenderer().getRenderExecutors() )
                        child.visit(this);
                }

                public void visit( RenderExecutorMultiLayer executor ) {
                    log.append("\n\t" + executor.getRenderer().getClass().getSimpleName()); //$NON-NLS-1$
                }
            });
            System.out.println(log);
        }
    }

    public RenderExecutor getRenderExecutor() {
        checkState();
        if (renderExecutor == null) {
            initRenderExecutor();
        }

        return renderExecutor;
    }
} // RenderManagerImpl
