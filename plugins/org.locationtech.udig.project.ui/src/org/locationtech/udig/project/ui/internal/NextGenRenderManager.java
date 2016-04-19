package org.locationtech.udig.project.ui.internal;

import static org.locationtech.udig.project.ui.internal.Trace.RENDER;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.ContextModelListenerAdapter;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.CompositeRenderContext;
import org.locationtech.udig.project.internal.render.ExecutorVisitor;
import org.locationtech.udig.project.internal.render.RenderContext;
import org.locationtech.udig.project.internal.render.RenderExecutor;
import org.locationtech.udig.project.internal.render.RenderPackage;
import org.locationtech.udig.project.internal.render.SelectionLayer;
import org.locationtech.udig.project.internal.render.impl.CompositeRenderContextImpl;
import org.locationtech.udig.project.internal.render.impl.RenderExecutorComposite;
import org.locationtech.udig.project.internal.render.impl.RenderExecutorMultiLayer;
import org.locationtech.udig.project.internal.render.impl.RenderManagerImpl;
import org.locationtech.udig.project.internal.render.impl.RenderTask;
import org.locationtech.udig.project.internal.render.impl.RenderTaskType;
import org.locationtech.udig.project.internal.render.impl.RendererConfigurator;
import org.locationtech.udig.project.internal.render.impl.RootCompositeRenderExecutor;
import org.locationtech.udig.project.internal.render.impl.RootCompositeRendererImpl;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderer;
import org.locationtech.udig.project.render.RenderException;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;

import com.vividsolutions.jts.geom.Envelope;

/**
 * {@link RenderManagerDynamic} replacement with better synchronized concurrent logic based on
 * rendering tasks queue and various optimizations.
 * 
 * @author vitalid
 *
 */
public class NextGenRenderManager extends RenderManagerImpl {
    
    
    /**
     * Optimizes real multithreading requests by compressing
     * {@link RenderTask}s into a single event that causes refreshing the map image.
     * 
     * @author vitalid
     *
     */
    class RenderTaskCompressor{

        static final int UNKNOWN = 0;
        static final int REFRESH_ALL = 0x01;
        static final int REFRESH_LAYERS = 0x01 << 1;
        static final int REFRESH_SELECTION = 0x01 << 2;
        static final int REFRESH_IMAGE_BUFFER = 0x01 << 3;

        Envelope bounds;

        List<ILayer> layers;
        
        SelectionLayer selectionLayer;

        int refreshType = UNKNOWN;

        RenderTaskCompressor(){
        }

        void compress(RenderTask task){

            switch(task.getTaskType()){
            case MAP_BOUNDS_CHANGED:
                refreshType |= REFRESH_ALL;

                break;
            case LAYER_ADDED:
            case LAYER_STATE_CHANGED:
                refreshType |= REFRESH_LAYERS;
                break;
            case LAYER_SELECTION_CHANGED:
                refreshType |= REFRESH_SELECTION;
                break;
            case LAYER_ZORDER_CHANGED:
                refreshType |= REFRESH_LAYERS;
                refreshType |= REFRESH_IMAGE_BUFFER;
                break;
            case LAYER_REMOVED:
                refreshType |= REFRESH_LAYERS;
                refreshType |= REFRESH_IMAGE_BUFFER;
                break;

            }

        }
        
        void addRefreshingLayer(ILayer layer){
            if(layers == null){
                layers = new LinkedList<ILayer>();
            }
            layers.add(layer);
        }
        
        void setSelectionLayer(SelectionLayer selectionLayer) {
            this.selectionLayer = selectionLayer;
        }
        
    }
    
    class RenderTaskQueueJob extends Job {
        

        private LinkedBlockingQueue<RenderTask> requests = new LinkedBlockingQueue<RenderTask>(); 

        public RenderTaskQueueJob(){
            super("RenderTaskQueueJob"); //$NON-NLS-1$
            setPriority(INTERACTIVE);
        }

        protected void addRenderTask(RenderTask renderTask){
            this.requests.add(renderTask);
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {

            int size = requests.size();
            if(size == 0)
                return Status.OK_STATUS;

            try{

                Collection<RenderContext> cachedRenderContexts = null;
                RenderTaskCompressor taskCompressor =  new RenderTaskCompressor();
                while(!requests.isEmpty()){
                    
                    if(monitor.isCanceled()){
                        return Status.CANCEL_STATUS;
                    }

                    if(cachedRenderContexts == null){
                        cachedRenderContexts = getRenderContexts();
                    }
                    //                List<RenderTask> liveRequests;
                    //                synchronized(requests){
                    //                    liveRequests = new ArrayList<RenderTask>(requests.size());
                    //                    requests.drainTo(liveRequests);
                    //                }

                    RenderTask next = null;
                    try {
                        next = requests.take();
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                        continue;//Bad request
                    }
                    taskCompressor.compress(next);

                    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    // Compression
                    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

                    RenderExecutor renderExecutor = getRenderExecutor();//Require lazy initialization
                    RendererConfigurator configurator = getConfigurator();

                    //                RenderTask task = null;
                    //                for (RenderTask rt : liveRequests) {
                    //                    if(task == null)
                    //                        task = new RenderTask(rt);
                    //                    else
                    //                        if(!task.compress(rt)){
                    //                            
                    //                        }
                    //                }


                    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    // Sync with RendererConfigurator state.
                    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    switch(next.getTaskType()){
                    case MAP_BOUNDS_CHANGED:

                        break;
                    case LAYER_ADDED:
                    case LAYER_REMOVED:
                        configurator.update(next);
                        break;

                    default:

                    }


                    switch(next.getTaskType()){
                    case MAP_BOUNDS_CHANGED:

                        break;
                    case LAYER_STATE_CHANGED:
                    {
                        for( ILayer layerToRender : next.getLayers() ) {
                            taskCompressor.addRefreshingLayer(layerToRender);
                        }
                        break;
                    }
                    case LAYER_SELECTION_CHANGED:

                        List<ILayer> layers = next.getLayers();
                        final ILayer layer = layers.get(0);
                        final Envelope bounds = next.getBounds();

                        final SelectionLayer selectionLayer = configurator.findSelectionLayer(layer);
                        taskCompressor.setSelectionLayer(selectionLayer);

                        break;

                    case LAYER_ADDED:
                    {
                        layers = next.getLayers();

                        List<Layer> layersToRender = synchronizeRenderers(cachedRenderContexts);
                        for( Layer layerToRender : layersToRender ) {
                            //                        layerToRender.refresh(null);
                            taskCompressor.addRefreshingLayer(layerToRender);
                        }
                    }
                    break;
                    case LAYER_ZORDER_CHANGED:
                    {
                        layers = next.getLayers();

                        List<Layer> layersToRender = synchronizeRenderers(cachedRenderContexts);
                        for( Layer layerToRender : layersToRender ) {
                            //                        layerToRender.refresh(null);
                            taskCompressor.addRefreshingLayer(layerToRender);
                        }
                        //                    manager.synchronizeAndRefresh(task.getEvent());
                        //                    try {
                        //                        ((RootCompositeRendererImpl) renderExecutor.getRenderer()).refreshImage();
                        //                    } catch (RenderException e) {
                        //                        // won't happen.
                        //                        e.printStackTrace();
                        //                    }
                        //                    ((ViewportPane) getMapDisplay()).renderDone();
                    }
                    break;
                    case LAYER_REMOVED:
                    {
                        layers = next.getLayers();
                        List<Layer> layersToRender = synchronizeRenderers(cachedRenderContexts);
                        for( Layer layerToRender : layersToRender ) {
                            //                      layerToRender.refresh(null);
                            taskCompressor.addRefreshingLayer(layerToRender);
                        }

                        //                    manager.synchronizeAndRefresh(task.getEvent());
                        //                    try {
                        //                        ((RootCompositeRendererImpl) renderExecutor.getRenderer()).refreshImage();
                        //                    } catch (RenderException e) {
                        //                        // won't happen.
                        //                        e.printStackTrace();
                        //                    }
                        //                    ((ViewportPane) getMapDisplay()).renderDone();
                    }
                    break;

                    default:
                    }
                }

                if(monitor.isCanceled()){
                    return Status.CANCEL_STATUS;
                }

                if( (taskCompressor.refreshType & RenderTaskCompressor.REFRESH_ALL) == RenderTaskCompressor.REFRESH_ALL){

                    configurator.reset();
                    Collection<RenderContext> configuration = configurator.getConfiguration();

                    //                  getRendererCreator().reset();
                    //                  validateRendererConfiguration();
                    validateRenderers();
                    
                    try {
                        getRenderExecutor().setRenderBounds(taskCompressor.bounds);
                        getRenderExecutor().render();
                    } catch (RenderException e) {
                        // Won't happen
                    }
                }else if( (taskCompressor.refreshType & RenderTaskCompressor.REFRESH_LAYERS) == RenderTaskCompressor.REFRESH_LAYERS
                        && taskCompressor.layers != null){

                    configurator.reset();
                    validateRenderers();
                    cachedRenderContexts=null;
                    
                    this.refreshLayers(taskCompressor.layers, null);

                }else if( (taskCompressor.refreshType & RenderTaskCompressor.REFRESH_SELECTION) == RenderTaskCompressor.REFRESH_SELECTION){


                    if (taskCompressor.selectionLayer != null){

                        configurator.reset();
                        validateRenderers();
                        cachedRenderContexts=null;

                        refreshSelection(taskCompressor.selectionLayer, null);
                    }

                }else if( (taskCompressor.refreshType & RenderTaskCompressor.REFRESH_IMAGE_BUFFER) == RenderTaskCompressor.REFRESH_IMAGE_BUFFER){

                    validateRenderers();
                    try {
                        ((RootCompositeRendererImpl) renderExecutor.getRenderer()).refreshImage();
                    } catch (RenderException e) {
                        // won't happen.
                        e.printStackTrace();
                    }
                    ((ViewportPane) getMapDisplay()).renderDone();
                }else{

                    System.out.println("WRONG LOGIC IN RENDER TASK QUEUE JOB: no compressing was done..");
                }


            }catch(Exception exc){
                ProjectUIPlugin.log("Failed to process rendering events in RenderTaskQueueJob", exc); //$NON-NLS-1$
            }

            return Status.OK_STATUS;
        }
        
        private void refreshLayers(final List<ILayer> layers, Envelope bounds){
            
            final List<SelectionLayer> selectionLayers = new ArrayList<SelectionLayer>(layers.size());
            for (ILayer layer : layers) {
                SelectionLayer selectionLayer = configurator.findSelectionLayer(layer);
                if(selectionLayer != null){
                    selectionLayers.add(selectionLayer);
                }
            }

            final ReferencedEnvelope bbox = bounds == null
                    || bounds instanceof ReferencedEnvelope ? (ReferencedEnvelope) bounds
                    : new ReferencedEnvelope(bounds, getRenderExecutor().getContext().getCRS());

            getRenderExecutor().visit(new ExecutorVisitor() {
                public void visit(RenderExecutor executor) {
                    
                    if(executor instanceof RootCompositeRenderExecutor){
                        
                        RootCompositeRenderExecutor compositeExecutor = (RootCompositeRenderExecutor)executor;
                        List<RenderExecutor> executors = new ArrayList<RenderExecutor>(
                                compositeExecutor.getRenderer().getRenderExecutors());
                        
                        for (RenderExecutor child : executors) {
                            child.visit(this);
                        }
                        
                    }else if (layers.contains(executor.getContext().getLayer())
                            || selectionLayers.contains(executor.getContext().getLayer())) {
                        // tell the renderer the area of the screen to refresh
                        executor.getRenderer().setRenderBounds(bbox);
                        // register our interest in seeing the screen redrawn
                        executor.getRenderer().setState(IRenderer.RENDER_REQUEST);
                    }
                }

                public void visit(RenderExecutorMultiLayer executor) {
                    
                    for (ILayer layer : layers) {
                        if (executor.getContext().getLayers().contains(layer)){
                            executor.getRenderer().setState(IRenderer.RENDER_REQUEST);
                            break;
                        }
                    }
                    if(!selectionLayers.isEmpty()){
                        for (SelectionLayer selectionLayer : selectionLayers) {
                            if(executor.getContext().getLayers().contains(
                                    selectionLayer)){
                                executor.getRenderer().setState(IRenderer.RENDER_REQUEST);
                                break;
                            }
                            
                        }
                    }
                    

                    

                }

                public void visit(RenderExecutorComposite executor) {
//                    List<RenderExecutor> executors = new ArrayList<RenderExecutor>(
//                            executor.getRenderer().getRenderExecutors());
//                    for (RenderExecutor child : executors) {
//                        child.visit(this);
//                    }
                }
            });
        }
        
        private void refreshSelection(final SelectionLayer selectionLayer, final Envelope bounds){


            getRenderExecutor().visit(new ExecutorVisitor() {
                public void visit(RenderExecutor executor) {
                    
                    IRenderContext context = executor.getContext();
                    if(executor instanceof RootCompositeRenderExecutor){
                        RootCompositeRenderExecutor compositeExecutor = (RootCompositeRenderExecutor)executor;
                        List<RenderExecutor> executors = new ArrayList<RenderExecutor>(
                                compositeExecutor.getRenderer().getRenderExecutors());
                        
                        for (RenderExecutor child : executors) {
                            child.visit(this);
                        }
                        
                        
                    }else if (selectionLayer == context.getLayer()) {
                        executor.getRenderer().setRenderBounds(bounds);
                        if (bounds != null) {
                            Rectangle bounds2 = context.toShape(
                                    new ReferencedEnvelope(bounds,
                                            getViewportModelInternal().getCRS()))
                                    .getBounds();
                            context.clearImage(bounds2);
                        } else {
                            context.clearImage();
                        }
                        executor.getRenderer().setState(IRenderer.RENDER_REQUEST);
                    }
                }

                public void visit(RenderExecutorMultiLayer executor) {
                    CompositeRenderContext contexts = executor.getContext();
                    for (IRenderContext context : contexts.getContexts()) {
                        if (context.getLayer() == selectionLayer) {
                            executor.getRenderer().setState(
                                    IRenderer.RENDER_REQUEST);
                            return;
                        }
                    }
                }

                public void visit(RenderExecutorComposite executor) {
                    for (RenderExecutor child : executor.getRenderer()
                            .getRenderExecutors())
                        child.visit(this);
                }
            });
            
        }

    }
    
    /**
     * Watches the layer add / delete / change of zorder and style changes.
     */
    ContextModelListenerAdapter contextModelAdapter = NextGenRenderManagerAdapters.createContextModelListener(this);
    /**
     * Watches the viewport model and triggers some kind of refresh. This is the
     * bounds and the crs changing; panning zooming swooshing and other assorted
     * user drive fun.
     */
    Adapter viewportListener = NextGenRenderManagerAdapters.createViewportListener(this);

    /**
     * listents to the viewport model (using the provided viewportListener)
     * and morphing the events into something for the map to be updated with?
     * 
     */
    private Adapter viewportModelChangeListener = NextGenRenderManagerAdapters.createViewportModelChangeListener(this, viewportListener, contextModelAdapter);

    private Adapter selectionListener = NextGenRenderManagerAdapters.createLayerListener(this);

    
    
    protected RendererConfigurator configurator;
    
    
    /**
     * Current configuration
     */
    protected volatile Collection<RenderContext> renderContexts = null;
    
    private RenderTaskQueueJob renderQueueJob;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @SuppressWarnings("unchecked")
    public NextGenRenderManager() {
        super();
        eAdapters().add(viewportModelChangeListener);
    }
    
    

    protected RenderTaskQueueJob getRenderQueueJob(){
        
        if(renderQueueJob == null){
            synchronized(this){
                if(renderQueueJob == null){
                renderQueueJob = new  RenderTaskQueueJob();
                }
            }
        }
        return renderQueueJob;
    }
    
    protected RendererConfigurator getConfigurator(){
        return this.configurator;
    }
    
    /**
     * Adds render task to the queue.
     * 
     * @param renderTask
     */
    protected void queueRenderTask(RenderTask renderTask){
        getRenderQueueJob().addRenderTask(renderTask);
    }
    
    protected int getRenderQueueSize(){
        return getRenderQueueJob().requests.size();
    }
    
    protected void scheduleQueueJob(){
        if(getRenderQueueSize() != 0){
            getRenderQueueJob().schedule();
        }
    }
    
    protected Collection<RenderContext> getRenderContexts() {
        return renderContexts;
    }

    /**
     * @see org.locationtech.udig.project.render.impl.RenderManagerImpl#refresh(org.locationtech.udig.project.Layer)
     */
    public void refresh(final ILayer layer, Envelope bounds) {
        checkState();
        if (!renderingEnabled) {
            return;
        }

        if (getMapDisplay() == null || getRenderExecutor() == null) {
            return; // we are not set up to renderer yet!
        }
        
        RenderTask renderTask = new RenderTask(RenderTaskType.LAYER_STATE_CHANGED, layer, bounds);
        queueRenderTask(renderTask);
        
        scheduleQueueJob();
        
        
    }
    

    
    @Override
    public void refreshImage() {
        try {
            ((RootCompositeRendererImpl)getRenderExecutor().getRenderer()).refreshImage();
            getRenderExecutor().setState(IRenderer.DONE);
        } catch (RenderException e) {
            ProjectPlugin.log("", e); //$NON-NLS-1$
        }
    }

    /**
     * @see org.locationtech.udig.project.render.impl.RenderManagerImpl#refreshSelection(com.vividsolutions.jts.geom.Envelope)
     */
    public void refreshSelection(final ILayer layer, final Envelope bounds) {
        checkState();
        if (!renderingEnabled) {
            return;
        }

        if (getMapDisplay() == null || getRenderExecutor() == null)
            return;

        
        RenderTask renderTask = new RenderTask(RenderTaskType.LAYER_SELECTION_CHANGED, layer, bounds);
        queueRenderTask(renderTask);
        
        scheduleQueueJob();
        
    }

    /**
     * @see org.locationtech.udig.project.render.IRenderManager#clearSelection(ILayer)
     */
    public void clearSelection(ILayer layer) {
        checkState();
        if (getMapDisplay() == null || getRenderExecutor() == null)
            return;
        
        final Layer selectionLayer = configurator.findSelectionLayer(layer);

        if (selectionLayer == null)
            return;

        try {
            RootCompositeRendererImpl renderer = (RootCompositeRendererImpl) getRenderExecutor().getRenderer();
            renderer.refreshImage();
            renderer.setState(IRenderer.DONE);

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
    public void refresh(final Envelope bounds) {
        
        checkState();
        if (!renderingEnabled) {
            return;
        }
        
        getViewportModelInternal().setInitialized(true);
        
        if (getMapDisplay() == null) {
            return;
        }
        if (getMapDisplay().getWidth() < 1 || getMapDisplay().getHeight() < 1)
            return;
        
        RenderTask renderTask = new RenderTask(RenderTaskType.MAP_BOUNDS_CHANGED);
        queueRenderTask(renderTask);
        
        if(getRenderQueueSize() != 0){
            getRenderQueueJob().schedule();
        }
    }
    

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @SuppressWarnings("unchecked")
    public void dispose() {
        if(renderQueueJob != null){
            renderQueueJob.cancel();
        }
        renderQueueJob=null;
        
        if(this.configurator != null){
            this.configurator.dispose();
        }
        
        this.renderingEnabled = false;
        this.renderContexts = null;
        
        //Destroy renderexecutor
//      try{
//          RenderExecutor re = getRenderExecutor();
//          re.dispose();
//      }catch(Exception exc){
//          exc.printStackTrace();
//      }
        Map mapObj = getMapInternal();
        removeAdapters(mapObj);
//      mapObj.eAdapters().remove(selectionListener);
        mapObj.removeDeepAdapter(selectionListener);
        removeAdapters(mapObj.getContextModel());
        
        Iterator<EObject> iter = getMapInternal().eAllContents();
        while (iter.hasNext()) {
            EObject obj = iter.next();
            removeAdapters(obj);
        }
        removeAdapters(getRenderExecutor());
        ((ViewportPane) mapDisplay).setRenderManager(null);
        eAdapters().remove(viewportModelChangeListener);
        
        this.contextModelAdapter = null;
        this.renderExecutorListener=null;
        this.viewportModelChangeListener = null;
        this.viewportListener = null;
        this.selectionListener = null;
        
        super.dispose();
    }

    /**
     * @param obj
     */
    private void removeAdapters(EObject obj) {
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
    public void setDisplayGen(IMapDisplay newDisplay) {
        IMapDisplay oldDisplay = mapDisplay;
        mapDisplay = newDisplay;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    RenderPackage.RENDER_MANAGER__MAP_DISPLAY, oldDisplay,
                    mapDisplay));
    }

    /**
     * TODO summary sentence for setViewport ...
     * 
     * @see org.locationtech.udig.project.render.RenderManager#setDisplay(org.locationtech.udig.project.render.displayAdapter.IMapDisplay)
     * @param value
     */
    public void setDisplay(IMapDisplay value) {
        checkState();
        ((ViewportPane) value).setRenderManager(this);
        setDisplayGen(value);
    }

    Adapter renderExecutorListener = RenderManagerAdapters.getRenderExecutorListener(this);

    /**
     * @see org.locationtech.udig.project.render.impl.RenderManagerImpl#setRenderExecutor(org.locationtech.udig.project.render.RenderExecutor)
     */
    @SuppressWarnings("unchecked")
    public void setRenderExecutor(RenderExecutor newRenderExecutor) {
        checkState();
        if (renderExecutor != null) {
            renderExecutor.eAdapters().remove(renderExecutorListener);
        }
        if (newRenderExecutor != null
                && !newRenderExecutor.eAdapters().contains(
                        renderExecutorListener)) {
            newRenderExecutor.eAdapters().add(renderExecutorListener);
        }
        super.setRenderExecutor(newRenderExecutor);
    }

    /**
     * @see org.locationtech.udig.project.render.impl.RenderManagerImpl#basicSetMap(org.locationtech.udig.project.Map,
     *      org.eclipse.emf.common.notify.NotificationChain)
     */
    @SuppressWarnings("unchecked")
    public NotificationChain basicSetMapInternal(Map newMap,
            NotificationChain msgs) {
        if (getMapInternal() != null) {
            getMapInternal().eAdapters().remove(selectionListener);
            getMapInternal().removeDeepAdapter(selectionListener);
        }
        if (newMap != null) {
            newMap.eAdapters().add(selectionListener);
            newMap.addDeepAdapter(selectionListener);
        }
        

        
        NotificationChain notificationChain = super.basicSetMapInternal(newMap, msgs);
//        if(newMap != null){
//            if (!getMapInternal().getContextModel().eAdapters().contains(contextModelAdapter))
//                getMapInternal().getContextModel().eAdapters().add(contextModelAdapter);
//        }
        
        return notificationChain;
    }

    
    
 

    /**
     * 
     */
    private void initRenderExecutor() {
        checkState();
//        MultiLayerRenderer rootRenderer = RenderFactory.eINSTANCE.createCompositeRenderer();
        RootCompositeRendererImpl rootRenderer = new RootCompositeRendererImpl();
        CompositeRenderContext context = new CompositeRenderContextImpl() {
            @Override
            public synchronized BufferedImage getImage(int width, int height) {
                if (image == null || image.getWidth() < width
                        || image.getHeight() < height) {
                    image = new BufferedImage(width, height,
                            BufferedImage.TYPE_3BYTE_BGR);
                    Graphics2D g = image.createGraphics();
                    g.setBackground(Color.WHITE);
                    g.clearRect(0, 0, width, height);
                    g.dispose();
                }

                return image;
            }

            @Override
            public synchronized void clearImage(Rectangle paintArea) {
                if (image == null)
                    return;
                Graphics2D g = image.createGraphics();
                g.setBackground(Color.WHITE);
                g.clearRect(paintArea.x, paintArea.y, paintArea.width,
                        paintArea.height);
                g.dispose();
                // // FIXME Arbonaut Oy , Vitali Diatchkov
                // System.out.println(
                // "synchronized CompositeRenderContext.clearImage()");
            }

        };
        context.setMapInternal(getMapInternal());
        context.setRenderManagerInternal(this);
        rootRenderer.setContext(context);
        rootRenderer.setName(Messages.RenderManagerDynamic_allLayers);
        
        initRendererConfigurator(context);
        
        rootRenderer.setRendererConfigurator(getRendererConfigurator());
        
//        setRenderExecutor(RenderFactory.eINSTANCE.createRenderExecutor(rootRenderer));
        RootCompositeRenderExecutor executor = new RootCompositeRenderExecutor(this);
        executor.setRenderer(rootRenderer);
        setRenderExecutor(executor);
        
    }

    private void logRendererTypes() {
        if (ProjectUIPlugin.isDebugging(RENDER)) {

            final StringBuffer log = new StringBuffer("Current Renderers:"); //$NON-NLS-1$
            getRenderExecutor().visit(new ExecutorVisitor() {
                public void visit(RenderExecutor executor) {
                    log
                            .append("\n\t" + executor.getClass().getSimpleName() + ":" + //$NON-NLS-1$ //$NON-NLS-2$
                                    executor.getRenderer().getClass()
                                            .getSimpleName() + "-" + //$NON-NLS-1$
                                    executor.getContext().getLayer().getName());
                }

                public void visit(RenderExecutorComposite executor) {
                    log
                            .append("\n\t" + executor.getRenderer().getClass().getSimpleName()); //$NON-NLS-1$
                    for (RenderExecutor child : executor.getRenderer()
                            .getRenderExecutors())
                        child.visit(this);
                }

                public void visit(RenderExecutorMultiLayer executor) {
                    log
                            .append("\n\t" + executor.getRenderer().getClass().getSimpleName()); //$NON-NLS-1$
                }
            });
            System.out.println(log);
        }
    }

    
    public RenderExecutor getRenderExecutor() {
        checkState();
        if (renderExecutor == null) {
            synchronized(this){
                if (renderExecutor == null) {
                    initRenderExecutor();
                }
            }
        }

        return renderExecutor;
    }
    
    

    protected void initRendererConfigurator(CompositeRenderContext context){
        
        configurator = new RendererConfigurator();
//        RenderContext context = new RenderContextImpl(){
//            @Override
//            public Map getMapInternal() {
//                return NextGenRenderManager.this.getMapInternal();
//            }
//            @Override
//            public Map getMap() {
//                return NextGenRenderManager.this.getMapInternal();
//            }
//            @Override
//            public RenderManager getRenderManagerInternal() {
//                return NextGenRenderManager.this;
//            }
//
//            @Override
//            public IRenderManager getRenderManager() {
//                return NextGenRenderManager.this;
//            }
//        };
        configurator.setContext(context);
        List<Layer> layers = getMapInternal().getLayersInternal();
        ENotificationImpl notification = new ENotificationImpl(this,
                Notification.ADD_MANY, ProjectPackage.CONTEXT_MODEL__LAYERS,
                null, layers);
        configurator.changed(notification);
    }
    
    public RendererConfigurator getRendererConfigurator(){
        return this.configurator;
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
    
    @Deprecated
    private List<Layer> synchronizeRenderers( final Notification msg) {
        
        /**
         * This is a back up of the render context (ie with buffered image and stuff) so
         * we can recycle them...
         * 
         */
        
        HashMap<RenderContext, RenderContext> oldToCopy = new HashMap<RenderContext, RenderContext>();
        Collection<RenderContext> configuration2= this.renderContexts;
        if (configuration2 != null)
            for( RenderContext context : configuration2 ) {
                //We skip outdated/removed layers
                if(context.getMap() == null)
                    continue;
                
                oldToCopy.put(context, context.copy());
            }
        /// smack the render creator and ask it to create us up some new renderers
        configurator.changed(msg);
        
        //// new configuration of render context ...
        configuration2= configurator.getConfiguration();
        
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
    
    
    
   private List<Layer> synchronizeRenderers( Collection<RenderContext> currentRenderContexts) {
        
        /**
         * This is a back up of the render context (ie with buffered image and stuff) so
         * we can recycle them...
         * 
         */
        
        HashMap<RenderContext, RenderContext> oldToCopy = new HashMap<RenderContext, RenderContext>();
//        Collection<RenderContext> configuration2= this.renderContexts;
        if (currentRenderContexts != null)
            for( RenderContext context : currentRenderContexts ) {
                //We skip outdated/removed layers
                if(context.getMap() == null)
                    continue;
                
                oldToCopy.put(context, context.copy());
            }
        /// smack the render creator and ask it to create us up some new renderers
//        configurator.changed(msg);
        
        //// new configuration of render context ...
        Collection<RenderContext> newRenderContexts= configurator.getConfiguration();
        
       // this is the list of layers we need to ask to be redrawn at the end of the day
        List<Layer> toRender = new ArrayList<Layer>();
        
        for( RenderContext newcontext : newRenderContexts ) {
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
     * Ensures that the current configuration of renderer is a valid choice. For
     * example the each of the layers in the map has a renderer that can render
     * it.
     */
    private void validateRenderers() {
        checkState();
        Collection<RenderContext> configuration;
        synchronized (this) {
            if (this.renderContexts == null) {
                configuration = null;
            } else {
                configuration = new ArrayList<RenderContext>(this.renderContexts);
            }
        }
        Collection<RenderContext> configuration2;
        if (configuration != null) {
            configuration2 = configurator.getConfiguration();
            List<RenderContext> removeList = new ArrayList<RenderContext>();
            List<RenderContext> addList = new ArrayList<RenderContext>();
            for (IRenderContext context : configuration) {
                if (!configuration2.contains(context))
                    removeList.add((RenderContext) context);
            }
            for (RenderContext context : configuration2) {
                if (configuration.contains(context))
                    continue;

                addList.add(context);
            }
            CompositeRenderContext compositecontext = (CompositeRenderContext) getRenderExecutor()
                    .getContext();
            compositecontext.removeContexts(removeList);
            if (!addList.isEmpty())
                compositecontext.removeContexts(addList);
            compositecontext.addContexts(addList);

        } else {
//            initRenderExecutor();
            configuration2 = configurator.getConfiguration();

            CompositeRenderContext compositecontext = (CompositeRenderContext) getRenderExecutor()
                    .getContext();
            // need this because this is taking place in a non-synchronized
            // block so it is possible for
            // this code to be executed twice. I want the second run to be
            // accurate.
            // might need to be thought about more.
            compositecontext.clear();
            compositecontext.addContexts(configuration2);
        }
        synchronized (this) {
            this.renderContexts = configuration2;
        }
        logRendererTypes();

    }
    
//    public void synchronizeAndRefresh(Notification msg) {
//        List<Layer> toRender = synchronizeRenderers(msg);
//        for( Layer layer : toRender ) {
//            layer.refresh(null);
//        }
//    }
}
