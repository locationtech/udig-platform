/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.geotools.renderer.label.LabelCacheImpl;
import org.geotools.util.ObjectCache;
import org.geotools.util.ObjectCaches;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.ContextModelListenerAdapter;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.ProjectPackage;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.CompositeRenderContext;
import org.locationtech.udig.project.internal.render.ExecutorVisitor;
import org.locationtech.udig.project.internal.render.RenderExecutor;
import org.locationtech.udig.project.internal.render.RenderFactory;
import org.locationtech.udig.project.internal.render.RenderPackage;
import org.locationtech.udig.project.internal.render.RendererCreator;
import org.locationtech.udig.project.internal.render.SelectionLayer;
import org.locationtech.udig.project.internal.render.impl.CompositeRenderContextImpl;
import org.locationtech.udig.project.internal.render.impl.CompositeRendererImpl;
import org.locationtech.udig.project.internal.render.impl.RenderContextImpl;
import org.locationtech.udig.project.internal.render.impl.RenderExecutorComposite;
import org.locationtech.udig.project.internal.render.impl.RenderExecutorMultiLayer;
import org.locationtech.udig.project.internal.render.impl.RenderManagerImpl;
import org.locationtech.udig.project.internal.render.impl.TiledCompositeRendererImpl;
import org.locationtech.udig.project.internal.render.impl.TiledRendererCreatorImpl;
import org.locationtech.udig.project.internal.render.impl.UDIGLabelCache;
import org.locationtech.udig.project.render.AbstractRenderMetrics;
import org.locationtech.udig.project.render.ILabelPainter;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderer;
import org.locationtech.udig.project.render.Tile;
import org.locationtech.udig.project.render.TileStateChangedListener;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.render.displayAdapter.impl.ViewportPaneTiledSWT;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;


/**
 * A IRenderManager that is reacts to events such as viewport model changes and renders 
 * the screen in tiles.
 * 
 * 
 * <p>
 * These are the events we have found this class pays attention to:
 * <ul>
 * <li>viewportListner will trigger some kind of refresh when you change view
 * size
 * <li>renderExecuttorListener does not actually do much; it passes along
 * something else that actually does the listening. The composite render
 * executor
 * <li>contextModelAdapter watches the IMap and notices new layers, deleted
 * layers, zorder changing and the occasional style change
 * </ul>
 * The Map owns one of these things; uses it to scribble on the screen to the
 * user.
 * 
 * <p>The tiling system used by this system is based on a tile size of 512x512 pixels and a "center" coordinate
 * of (0,0)</p>
 * 
 * @generated
 */
public class TiledRenderManagerDynamic extends RenderManagerImpl {
    
    /**
     * 
     * The size of the tiles to use.
     */
    private final static int TILE_SIZE = 512;
       
    /**
     * A "center" coordinate to use for the tile system.
     */
    private Coordinate tileCenter = new Coordinate(0,0);
    
    /**
     * Watches the layer add / delete / change of zorder and style changes.
     */
    ContextModelListenerAdapter contextModelAdapter = RenderManagerAdapters.createContextModelListener(this);
    
    /**
     * Watches the viewport model and class refresh(null) (triggers redrawing of the layers). 
     * Examples of viewport model events is the bounds and the crs changing; 
     * panning zooming swooshing and other assorted
     * user drive fun.
     */
    Adapter viewportListener = RenderManagerAdapters.createViewportListener(this);

    /**
     * Listens to the viewport model (using the provided viewportListener)
     * and morphing the events into something for the map to be updated with?
     * 
     */
    private Adapter viewportModelChangeListener = RenderManagerAdapters.createViewportModelChangeListener(this, viewportListener,contextModelAdapter);

    /**
     * Listens for layer made visible change events and upates
     * tile states accordingly.
     */
    private Adapter visibilityChangedListener = RenderManagerAdapters.createVisibilityChangedAdapater(this);
    
    /**
     * Creates a new RenderExecutorListener which listens to render events
     * and kicks the viewport to updated.
     */
    Adapter renderExecutorListener = RenderManagerAdapters.getRenderExecutorListener(this);


    Adapter selectionListener = RenderManagerAdapters.createLayerListener(this);
    

    /**
     * This is a weak reference to all the tiles.
     * 
     */
    ObjectCache tileCache = ObjectCaches.create("soft", 50); //Tiles that are on the screen //$NON-NLS-1$

    /**
     * This is a strong reference to the "important" tiles.  "Important" tiles and tiles
     * classified to be on screen or waiting to be on screen.
     */
    Set<Tile> importantTiles = Collections.synchronizedSet(new HashSet<Tile>());
    
    /**
     * This is our tile state changed listener what is responsible for maintaining
     * the important tiles collections.
     */
    TileStateChangedListener tileStateListener = new TileStateChangedListener(){
        
        public void screenStateChanged(Tile t){
            updateImportantTileList(t);
        }
        
        public void validationStateChanged(Tile t){
            updateImportantTileList(t);
        }
        
        public void renderStateChanged(Tile t){
            //this doesn't effect important tile list
        }
        
        public void contextStateChanged(Tile t){
            //this doesn't affect important tile list
        }
        
        /**
         * Updates the important tile list.  Either adds it or removes
         * it depending on the tile state.
         *
         * @param t
         */
        private void updateImportantTileList( Tile t ) {
            if (t.getScreenState() == Tile.ScreenState.ONSCREEN
                    || t.getTileState() == Tile.ValidatedState.VALIDATED) {
                importantTiles.add(t);
            } else {
                importantTiles.remove(t);
            }
        }
    };
    
    /**
     * Creates a new TiledRenderManagerDynamice and sets the render creator to null.  The render creator will
     * be set to a TiledRenderCreatorImpl when first used.
     * 
     * @generated NOT
     */
    public TiledRenderManagerDynamic() {
        super();
        this.rendererCreator = null;
        eAdapters().add(viewportModelChangeListener);
    }
    
    public RendererCreator getRendererCreator() {
        checkState();
        if (rendererCreator == null){
            initRenderCreator();
        }
        return rendererCreator;
    } 

    /**
     * Creates a new render creator and initializes it with the current layers on the map.
     * 
     */
    private void initRenderCreator() {
        this.rendererCreator = new TiledRendererCreatorImpl(getTileSize(), this);
        List<Layer> layers = getMapInternal().getLayersInternal();
        // make sure renderer creator is initialized.
        ENotificationImpl notification = new ENotificationImpl(this,
                Notification.ADD_MANY, ProjectPackage.CONTEXT_MODEL__LAYERS,
                null, layers);
        getRendererCreator().changed(notification);
    }

    
    /**
     * Gets a tile from the cached list.  If the tile
     * does not exist a new tile is created
     * and added to the cached list.
     * 

     * 
     *
     * @param env
     * @return
     */
    private Tile getOrCreateTile(ReferencedEnvelope env){
        
        Tile tile = (Tile) tileCache.get(env);
        if (tile == null) {
            try {
                tileCache.writeLock(env);
                tile = (Tile) tileCache.peek(env);
                if (tile == null) {
                    tile = createTile(env);
                    tileCache.put(env, tile);           
                }
            } finally {
                tileCache.writeUnLock(env);
            }
        }
        return tile;
    }
    
    /**
     * Creates a new tile adding the listeners and putting it in the important tiles list.
     *
     * @param key
     * @return
     */
    private Tile createTile(ReferencedEnvelope key){
        RenderExecutorComposite newRE = createRenderExecutor(key);
        Tile tile = new Tile(key, newRE, getTileSize());
        tile.setStateChangedListener(tileStateListener);
        importantTiles.add(tile);
        ((RenderContextImpl)newRE.getContext()).setLabelPainterLocal(newRE.getContext().getLabelPainter());
        return tile;
    }
 
    /** 
     * Refreshes all tiles within the given bounds for a particular layer.
     * 
     * If refreshing this layer causes other layers to need refreshing then the other layers will be refreshed
     * too.  An example is that if a wms layer has been moved a refresh may result in multiple wms contexts in the rendering
     * stack; each which need to be refreshed.
     * @see org.locationtech.udig.project.render.impl.RenderManagerImpl#refresh(org.locationtech.udig.project.Layer)
     */
    public void refresh(final ILayer layer, Envelope bounds) {
        checkState();
        if (!renderingEnabled) {
            return;
        }

        if (getMapDisplay() == null || getMapDisplay().getWidth() < 1 || getMapDisplay().getHeight() < 1) {
            return; // we are not set up to renderer yet!
        }
        
        final SelectionLayer selectionLayer = getRendererCreator().findSelectionLayer(layer);
        
        //get only the tile in the bounds in the area of interest 
        ReferencedEnvelope areaOfInterest = null;
        if (bounds == null){
            areaOfInterest = getMap().getViewportModel().getBounds();
        }else{
            areaOfInterest = new ReferencedEnvelope(bounds, getMap().getViewportModel().getCRS());
        }
        ReferencedEnvelope viewportbounds = getMap().getViewportModel().getBounds();
        double resolution = viewportbounds.getWidth() / getMapDisplay().getWidth();
        
        //get only the tile in the bounds in the area of interest 
        Collection<ReferencedEnvelope> tileBounds = computeTileBounds(areaOfInterest, resolution);
        setTileStateToOld(tileBounds);
        
        for( ReferencedEnvelope env : tileBounds ) {
            //need to render the entire tile otherwise new additions to tile doesn't work properly
            final Envelope bbox = env;
            if (bbox.isNull()){
                //nothing to do; the bounding box of the tile does not intersect the area of intersect
                //this shouldn't occur but it does???  (maybe a double precision thing)
                continue;
            }
            
            Tile tile = getOrCreateTile(env);
            
            tile.setTileState(Tile.ValidatedState.VALIDATED);
            tile.setRenderState(Tile.RenderState.RENDERED);
            
            RenderExecutorComposite re = tile.getRenderExecutor();
            
            //validate the context if necessary
            List<AbstractRenderMetrics> torefresh = new ArrayList<AbstractRenderMetrics>();
            if (tile.getContextState() == Tile.ContextState.INVALID){
                torefresh = validateRendererConfiguration(tile);
                tile.setContextState(Tile.ContextState.OKAY);
            }
            
            //determine which layers need to be updated
            Collection<ILayer> layersToUpdate = new ArrayList<ILayer>();
            layersToUpdate.add(layer);
            if (selectionLayer != null) layersToUpdate.add(selectionLayer);
            if (torefresh != null){
                for( AbstractRenderMetrics metrics : torefresh ) {
                    layersToUpdate.add(metrics.getRenderContext().getLayer());
                }
            }
          
            for( final ILayer newlayer : layersToUpdate) {
                re.visit(new ExecutorVisitor(){
                    public void visit( RenderExecutor executor ) {
                        if (executor.getContext().getLayer() == newlayer) {
                            // layer to refresh
                            executor.getRenderer().setRenderBounds(bbox);
                            // register request
                            executor.getRenderer().setState(IRenderer.RENDER_REQUEST);
                        }
                    }

                    public void visit( RenderExecutorMultiLayer executor ) {
                        if (executor.getContext().getLayers().contains(newlayer)) {
                            // why no need to set the bounds here???
                            // register request
                            executor.getRenderer().setState(IRenderer.RENDER_REQUEST);
                        }
                    }

                    public void visit( RenderExecutorComposite executor ) {
                        // visit all the children executors
                        List<RenderExecutor> executors = new ArrayList<RenderExecutor>(executor
                                .getRenderer().getRenderExecutors());
                        for( RenderExecutor child : executors ) {
                            child.visit(this);
                        }
                    }
                });
            }
        }
    }
    
    

    /**
     * 
     * Called when a selection layer is refreshed.
     * @see org.locationtech.udig.project.render.impl.RenderManagerImpl#refreshSelection(org.locationtech.jts.geom.Envelope)
     */
    public void refreshSelection(final ILayer layer, final Envelope bounds) {
        
        checkState();
        if (!renderingEnabled) {
            return;
        }

        if (getMapDisplay() == null || getMapDisplay().getWidth() < 1 || getMapDisplay().getHeight() < 1) {
            return; // we are not set up to renderer yet!
        }
        
        //find selection layer
        final SelectionLayer selectionLayer = getRendererCreator().findSelectionLayer(layer);
        if (selectionLayer == null) return;
                
        //redraw the ones we care about for now
        //determine area of interest
        ReferencedEnvelope areaOfInterest = null;
        if (bounds == null){
            areaOfInterest = getMap().getViewportModel().getBounds();
        }else{
            areaOfInterest= new ReferencedEnvelope(bounds, getMap().getViewportModel().getCRS());
        }
        ReferencedEnvelope viewportbounds = getMap().getViewportModel().getBounds();
        double resolution = viewportbounds.getWidth() / getMapDisplay().getWidth();
        
        //we need to re-render all tiles
        invalidateAllTilesRenderState();
        
        //get only the tile in the bounds in the area of interest 
        Collection<ReferencedEnvelope> tileBounds = computeTileBounds(areaOfInterest, resolution);
        setTileStateToOld(tileBounds);
        
        for( ReferencedEnvelope env : tileBounds ) {
            Tile tile = getOrCreateTile(env);
            
            tile.setRenderState(Tile.RenderState.RENDERED);
            tile.setTileState(Tile.ValidatedState.VALIDATED);
            RenderExecutorComposite re = tile.getRenderExecutor();
         
            // creates the contexts
            if (tile.getContextState() == Tile.ContextState.INVALID){
                validateRendererConfiguration(tile);
                tile.setContextState(Tile.ContextState.OKAY);
            }

            final Envelope bbox = env;

            //set render requests for the selection layer
            re.visit(new ExecutorVisitor(){

                public void visit( RenderExecutor executor ) {
                    IRenderContext context = executor.getContext();
                    if (selectionLayer == context.getLayer()) {
                        executor.getRenderer().setRenderBounds(bbox);
                        // only clear the part of the image in the bounds??
                        context.clearImage();
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
                    for( RenderExecutor child : executor.getRenderer().getRenderExecutors() ) {
                        child.visit(this);
                    }

                }
            });
        }   
    }

   
    /**
     * Clears the selection layer
     * @see org.locationtech.udig.project.render.IRenderManager#clearSelection(ILayer)
     */
    public void clearSelection(ILayer layer) {
        checkState();
        if (getMapDisplay() == null)
            return;
        
        //find selection layer
        final Layer selectionLayer = getRendererCreator().findSelectionLayer(layer);
        if (selectionLayer == null)
            return;      
        
        //tiles need to be re-rendered
        invalidateAllTilesRenderState();
        
        // refresh image
        refreshImage();
    }
    
    /**
     * Updates the validated state of all tiles whose key is not in the validTiles
     * list to ValidatedState.OLD.
     *
     * @param validTiles
     */
    private void setTileStateToOld( Collection<ReferencedEnvelope> validTiles ) {
        // mark all tiles as offscreen
        for( Iterator<Object> iterator = tileCache.getKeys().iterator(); iterator.hasNext(); ) {
            ReferencedEnvelope key = (ReferencedEnvelope) iterator.next();
            Tile t = (Tile) tileCache.get(key);
            if (t != null && !validTiles.contains(key)) {
                t.setTileState(Tile.ValidatedState.OLD);
            }
        }
    }

    /**
     * Renders the collection of tiles in a separate different thread.
     * 
     * <p>Currently this is used by the scroll panning tool
     * to render tiles as we pan around.</p>
     *
     * @param tiles
     */
    RenderTileJob tileRenderJob = null;
    public void renderTiles(Collection<Tile> tiles){
        if (tiles == null || tiles.isEmpty()) return;
        if (tileRenderJob == null){
            tileRenderJob = new RenderTileJob(this);
        }
        tileRenderJob.addTiles(tiles);
        tileRenderJob.schedule();
    }
    /**
     * Cancels all render jobs that are running in the background; that
     * were started by calling the renderTiles function.
     */
    public void cancelTileRenderJobs(){
        if (tileRenderJob != null){
            tileRenderJob.clear();
            tileRenderJob.cancel();
        }
    }
    
    /**
     * Renders a specific tile in the calling thread.
     * <p>The tile is only rendered if necessary (determined by the render
     * and context state).  The context
     * is also validated and updated if necessary.
     * </p>
     *
     * @param tile
     */
    public void renderTile( Tile tile ) {
        boolean rendertile = (tile.getRenderState() == Tile.RenderState.INVALID || tile.getRenderState() == Tile.RenderState.NEW);
        tile.setTileState(Tile.ValidatedState.VALIDATED);
        RenderExecutorComposite re = tile.getRenderExecutor();

        // creates the contexts - this function returns the "new" contexts; these are the ones
        // that need to be refreshed.
        List<AbstractRenderMetrics> forrefresh = null;
        if (tile.getContextState() == Tile.ContextState.INVALID) {
            forrefresh = validateRendererConfiguration(tile);
            tile.setContextState(Tile.ContextState.OKAY);
            if (forrefresh == null) {
                rendertile = true;
            }
        }
        if (!rendertile && forrefresh != null) {
            // only refresh the layers that have changed
            for( AbstractRenderMetrics context : forrefresh ) {

                // kick the child renderer that has been updated
                List<TiledCompositeRendererImpl.RenderInfo> kids = ((TiledCompositeRendererImpl) re.getRenderer()).getChildren();
                for( TiledCompositeRendererImpl.RenderInfo r : kids ) {
                    if (r.getMetrics().getRenderContext().equals(context.getRenderContext())) {
                        // we need to render this layer
                        // layer to refresh
                        r.getExecutor().getRenderer().setRenderBounds(tile.getReferencedEnvelope());
                        r.getExecutor().getRenderer().setState(IRenderer.RENDER_REQUEST);
                    }
                }
            }
        } else if (rendertile) {
            // refresh all layers  
            re.setRenderBounds(tile.getReferencedEnvelope());
            re.render();
        }
        tile.setRenderState(Tile.RenderState.RENDERED);
        refreshImage();
    }
    /**
     * Refreshes the map display without removing the existing tile contexts.
     *
     * @param bounds
     */
    public void softRefresh( final Envelope bounds) {
        checkState();
        if (!renderingEnabled) {
            return;
        }
        getViewportModelInternal().setInitialized(true);

        if (getMapDisplay() == null) {
            return;
        }

        if (getMapDisplay().getWidth() < 1 || getMapDisplay().getHeight() < 1) {
            return;
        }

        // only worry about tiles in the area of interest as specified by the bounds.
        // if the bounds is null then we are interested in the entire viewport
        ReferencedEnvelope areaOfInterest = null;
        if (bounds == null) {
            areaOfInterest = getMap().getViewportModel().getBounds();
        } else {
            areaOfInterest = new ReferencedEnvelope(bounds, getMap().getViewportModel().getCRS());
        }
        
        ReferencedEnvelope viewportbounds = getMap().getViewportModel().getBounds();
        double resolution = viewportbounds.getWidth() / getMapDisplay().getWidth();

        Collection<ReferencedEnvelope> tileBounds = computeTileBounds(areaOfInterest, resolution);
        //update tile states
        setTileStateToOld(tileBounds); 
        for( Iterator<ReferencedEnvelope> iterator = tileBounds.iterator(); iterator.hasNext(); ) {
            ReferencedEnvelope referencedEnvelope = (ReferencedEnvelope) iterator.next();
            
            Tile tile = getOrCreateTile(referencedEnvelope);
            boolean rendertile = (tile.getRenderState() == Tile.RenderState.INVALID || tile.getRenderState() == Tile.RenderState.NEW);
           
            tile.setTileState(Tile.ValidatedState.VALIDATED);
            RenderExecutorComposite re = tile.getRenderExecutor();

            // creates the contexts - this function returns the "new" contexts; these are the ones
            //that need to be refreshed.
            List<AbstractRenderMetrics> forrefresh = null;
            if (tile.getContextState() == Tile.ContextState.INVALID){
                forrefresh = validateRendererConfiguration(tile);
                tile.setContextState(Tile.ContextState.OKAY);
                if (forrefresh == null){
                    rendertile = true;
                }
            }
                        
            if (!rendertile && forrefresh != null ){
                //only refresh the layers that have changed
                for(AbstractRenderMetrics context : forrefresh){
                    
                    //kick the child renderer that has been updated
                    List<TiledCompositeRendererImpl.RenderInfo> kids = ((TiledCompositeRendererImpl)re.getRenderer()).getChildren();
                    for (TiledCompositeRendererImpl.RenderInfo r : kids){
                        if (r.getMetrics().getRenderContext().equals(context.getRenderContext())){
                            //we need to render this layer
                            // layer to refresh
                            r.getExecutor().getRenderer().setRenderBounds(referencedEnvelope);
                            r.getExecutor().getRenderer().setState(IRenderer.RENDER_REQUEST);
                        }
                    }
                }
            }else if (rendertile){
//                if (re.getState() != IRenderer.STARTING) { // && re.getState() != IRenderer.RENDERING){
//                    re.setState(IRenderer.STARTING);
//                    re.setRenderBounds(referencedEnvelope);
//                    re.render();
                    List<TiledCompositeRendererImpl.RenderInfo> kids = ((TiledCompositeRendererImpl)re.getRenderer()).getChildren();
                    for (TiledCompositeRendererImpl.RenderInfo r : kids){
                            // layer to refresh
                            r.getExecutor().getRenderer().setRenderBounds(referencedEnvelope);
                            r.getExecutor().getRenderer().setState(IRenderer.RENDER_REQUEST);
                    }
                    re.setState(IRenderer.RENDER_REQUEST);
//                }                
            }
        }
        
        if (!getMapInternal().getContextModel().eAdapters().contains(contextModelAdapter)){
            getMapInternal().getContextModel().eAdapters().add(contextModelAdapter);
        }

        refreshImage();
    }

    /**
     * Performs a "hard refresh" - this removes any existing tiles then recreates them.
     * 
     * @generated NOT
     */
    public void refresh(final Envelope bounds) {
        //clear and existing configurations
        if (getRendererCreator() != null){ 
            ((TiledRendererCreatorImpl)getRendererCreator()).reset();
        }

        if (getMapDisplay() instanceof ViewportPaneTiledSWT){
            //clear the tiles from the viewport pane as well otherwise the tiles will
            //be out of sync
            ViewportPaneTiledSWT tiledpane = (ViewportPaneTiledSWT) getMapDisplay();
            tiledpane.clearCachedTiles();
            tiledpane.clearReadyTiles();
        }
        
        //remove all existing images so everything will be recreated
        disposeAllTiles();
        tileCache.clear();
        importantTiles.clear();
        
        //now recreate everything
        softRefresh(bounds);
    }

    
    /**
     * Re composes all the images of all the tiles in the viewport.  
     * <p>
     * This function does not verify the rendering stack; however it
     * does verify that render state and re-render if necessary.
     * 
     */
    @Override
    public void refreshImage() {        
        //only worry about tiles in the area of interest as specified by the viewport
        ReferencedEnvelope areaOfInterest = getMap().getViewportModel().getBounds();
        
        ReferencedEnvelope viewportbounds = getMap().getViewportModel().getBounds();
        double resolution = viewportbounds.getWidth() / getMapDisplay().getWidth();
        
        Collection<ReferencedEnvelope> tileBounds = computeTileBounds(areaOfInterest, resolution);

        for( Iterator<ReferencedEnvelope> iterator = tileBounds.iterator(); iterator.hasNext(); ) {
            ReferencedEnvelope referencedEnvelope = (ReferencedEnvelope) iterator.next();

            Tile tile = getOrCreateTile(referencedEnvelope);

            RenderExecutorComposite re = tile.getRenderExecutor();
            if (tile.getRenderState() == Tile.RenderState.INVALID) {
                re.setRenderBounds(tile.getReferencedEnvelope());
                re.getRenderer().setState(IRenderer.RENDER_REQUEST);
                re.render();
            } else {
                //kick the renderer to refresh the image
               re.getRenderer().setState(IRenderer.DONE);
            }
            tile.setRenderState(Tile.RenderState.RENDERED);
        }
    }
    

    /**
     * Returns the size of the tiles
     *
     * @return
     */
    public int getTileSize(){
        return TILE_SIZE;
    }
    
    /**
     * Computes the tiles associated with a bounds and resolution
     *
     * @param viewBounds                bounds to find tiles for
     * @param worldunitsperpixel        resolution
     * 
     * @return  Collection of referenced envelopes that represent the tiles in the bounds
     */
    @Override
    public Collection<ReferencedEnvelope> computeTileBounds(ReferencedEnvelope viewBounds, double worldunitsperpixel){
        
        double unittilesize = worldunitsperpixel * getTileSize();
        double minx = viewBounds.getMinX();
        double minxtile = Math.floor((minx - tileCenter.x) / unittilesize) * unittilesize + tileCenter.x;
        
        double maxx = viewBounds.getMaxX();
        double maxxtile = (Math.floor((maxx - tileCenter.x) / unittilesize) + 1) * unittilesize + tileCenter.x;
        
        double miny = viewBounds.getMinY();
        double minytile = Math.floor((miny - tileCenter.y) / unittilesize) * unittilesize + tileCenter.y;
        
        double maxy = viewBounds.getMaxY();
        double maxytile = (Math.floor((maxy - tileCenter.y) / unittilesize) + 1) * unittilesize + tileCenter.y;

        ArrayList<ReferencedEnvelope> tileBounds = new ArrayList<ReferencedEnvelope>();
        int numberx = (int)Math.round( (maxxtile - minxtile)/unittilesize );
        int numbery = (int)Math.round( (maxytile - minytile)/unittilesize); 
        
        for (int x = 0; x < numberx; x ++){
            double xvalue = x * unittilesize + minxtile;
            double xvaluemax = (x + 1) * unittilesize + minxtile;
            xvalue = roundDouble(xvalue);
            xvaluemax = roundDouble(xvaluemax);
            for (int y = 0; y < numbery; y ++){
                double yvalue = y * unittilesize + minytile;
                double yvaluemax = (y + 1) * unittilesize + minytile;
                yvalue = roundDouble(yvalue);
                yvaluemax = roundDouble(yvaluemax);
                
                ReferencedEnvelope env = new ReferencedEnvelope(xvalue, xvaluemax, yvalue, yvaluemax, viewBounds.getCoordinateReferenceSystem());
                tileBounds.add(env);
            }
        }        
        return tileBounds;
    }
    
    /**
     * This function takes the last two digits (8 bits) of a double and 0's them. 
     * 
     *
     * @param number
     * @return
     */
    private static double roundDouble(double number){
        
        Long xBits = Double.doubleToLongBits(number);

        //zeroLowerBits
        int nBits = 8;
        long invMask = (1L << nBits) - 1L;
        
        long mask =~ invMask;
        
        xBits &= mask;   
        return Double.longBitsToDouble(xBits);
    }
    
    
    /**
     * Disposes of this render manager and all associated tiles.
     * 
     * @generated NOT
     */
    public void dispose() {
        Set<EObject> set = new HashSet<EObject>();
        set.add(getMapInternal());
        Iterator<EObject> iter = getMapInternal().eAllContents();
        while (iter.hasNext()) {
            EObject obj = iter.next();
            removeAdapters(obj);
        }
        try{
            getMapInternal().removeDeepAdapter(visibilityChangedListener);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        //remove Adapters from all render executors
        ((ViewportPane) mapDisplay).setRenderManager(null);
     
        //dispose of all tiles
        disposeAllTiles();
        tileCache.clear();
        importantTiles.clear();

        super.dispose();
    }
    
    /**
     * Disposes all tiles
     */
    private void disposeAllTiles(){
        for( Iterator<Object> iterator = tileCache.getKeys().iterator(); iterator.hasNext(); ) {
            ReferencedEnvelope key = (ReferencedEnvelope) iterator.next();
            Tile t = (Tile)tileCache.get(key);
            if (t != null){
                removeAdapters(t.getRenderExecutor());
                t.dispose();
            }
        }    
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
     * 
     * Sets the map display to the new value.
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
     * 
     * Sets the map display to the new value and updates
     * the render manager associated with the map display.
     * 
     * @see org.locationtech.udig.project.render.RenderManager#setDisplay(org.locationtech.udig.project.render.displayAdapter.IMapDisplay)
     * @param value
     */
    public void setDisplay(IMapDisplay value) {
        checkState();
        ((ViewportPane) value).setRenderManager(this);
        setDisplayGen(value);
    }


    /**
     * @see org.locationtech.udig.project.render.impl.RenderManagerImpl#basicSetMap(org.locationtech.udig.project.Map,
     *      org.eclipse.emf.common.notify.NotificationChain)
     */
    @Override
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
        NotificationChain change = super.basicSetMapInternal(newMap, msgs);
        if (getMapInternal() != null){
            getMapInternal().addDeepAdapter(this.visibilityChangedListener);
        }
        
        return change;
    }

    
    /**
     * Ensures that the current configuration of renderer is a valid choice. 
     * 
     * <p>This function takes the current "configuration" represented by the tile and compares it to a newly
     * created configuration that is created based on the current state of the system.  It then removes and contexts from the current
     * configuration that are not in the new configuration and adds any newly created configurations.
     * </p>
     * <p>
     * The function will return null if the entire context is new; otherwise it will return a set of
     * render metrics that represent the newly created contexts.  These new contexts are the context that need to be re-rendered.
     * </p>
     * 
     * @return a list of added contexts; null if all contexts are new; 
     * 
     */
    private List<AbstractRenderMetrics> validateRendererConfiguration(Tile t){
        
        checkState();
    
        if (rendererCreator == null){
            initRenderCreator();
        }
        RenderExecutorComposite re = t.getRenderExecutor();
        TiledCompositeRendererImpl renderer = null;
        try{
            renderer = (TiledCompositeRendererImpl)re.getRenderer();
        }catch (Exception ex){
            ProjectPlugin.log("Error creating context for tile - Tile does not have a renderer.", ex); //$NON-NLS-1$
            return null;
        }
        
        if (renderer == null){
            //the tile doesn't have a renderer there is some error;
            ProjectPlugin.log("Error creating context for tile - Tile does not have a renderer."); //$NON-NLS-1$
            return null;
        }
        Collection<AbstractRenderMetrics> currentconfiguration;
        synchronized (this) {
            if (renderer.getChildren().size() == 0){
                currentconfiguration = null;
            }else{
                currentconfiguration = renderer.getChildrenMetrics();
            }
        }
       
       ArrayList<AbstractRenderMetrics> newcontexts = new ArrayList<AbstractRenderMetrics>();
       ILabelPainter tilelabelpainter = ((RenderContextImpl)t.getRenderExecutor().getContext()).getLabelPainter();
       if (currentconfiguration == null){
            //get new configurations
            Collection<AbstractRenderMetrics> newconfig = ((TiledRendererCreatorImpl)getRendererCreator()).getConfiguration(t.getReferencedEnvelope());
            // need this because this is taking place in a non-synchronized
            // block so it is possible for
            // this code to be executed twice. I want the second run to be
            // accurate.
            // might need to be thought about more.
            renderer.removeAllChildren();
            renderer.addChildren(newconfig);
            newcontexts = null;
            for( Iterator<AbstractRenderMetrics> iterator = newconfig.iterator(); iterator.hasNext(); ) {
                AbstractRenderMetrics abstractRenderMetrics = (AbstractRenderMetrics) iterator.next();
                //setup label painter
                ((RenderContextImpl)abstractRenderMetrics.getRenderContext()).setLabelPainterLocal(tilelabelpainter);
            }
        }else{
            //we have an existing configuration; lets recycle as many of the contexts as possible
            //lets get the new configuration
            Collection <AbstractRenderMetrics> newconfig = ((TiledRendererCreatorImpl) getRendererCreator()).getConfiguration(t.getReferencedEnvelope());
            
            //items to add/remove from the existing configuration
            List<AbstractRenderMetrics> removeList = new ArrayList<AbstractRenderMetrics>();
            List<AbstractRenderMetrics> addList = new ArrayList<AbstractRenderMetrics>();
            
            for(AbstractRenderMetrics metric: currentconfiguration){
                if (!newconfig.contains(metric)){
                    //this is an existing context the doesn't exist in the new set
                    removeList.add(metric);
                }
            }
            for (AbstractRenderMetrics metric: newconfig){
                if (!currentconfiguration.contains(metric)){
                    //this is in the new configuration but not the only one therefore we need to add it
                    addList.add(metric);
                    //setup label painter
                    ((RenderContextImpl)metric.getRenderContext()).setLabelPainterLocal(tilelabelpainter);
                }
            }
            
            newcontexts.addAll(addList);
            
            renderer.removeChildren(removeList);
            
            if (!addList.isEmpty()){
                renderer.removeChildren(addList);
            }
            renderer.addChildren(addList);
            
            //let's properly dispose of anything in the remove List
            for( Iterator<AbstractRenderMetrics> iterator = removeList.iterator(); iterator.hasNext(); ) {
                AbstractRenderMetrics metric = (AbstractRenderMetrics) iterator.next();
                ((RenderContextImpl)metric.getRenderContext()).dispose();
            }
       }
       return newcontexts;
    }

    /**
     * Here we'll need a render executor and renderer for each tile.
     * 
     * This creates a render executor composite.
     */
    private RenderExecutorComposite createRenderExecutor(ReferencedEnvelope bounds) {
        checkState();
        
        CompositeRendererImpl renderer = (CompositeRendererImpl)RenderFactory.eINSTANCE.createTiledCompositeRenderer();

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
            }

        };
        
        context.setMapInternal(getMapInternal());
        context.setRenderManagerInternal(this);
        context.setImageBounds(bounds);
        context.setImageSize(new Dimension(getTileSize(), getTileSize()));
        
        ((CompositeRenderContextImpl)context).setLabelPainterLocal(new UDIGLabelCache(new LabelCacheImpl()));
        renderer.setContext(context);
        
        renderer.setName(Messages.RenderManagerDynamic_allLayers);
        
        RenderExecutorComposite renderExecutor = (RenderExecutorComposite)RenderFactory.eINSTANCE.createRenderExecutor(renderer);
        renderExecutor.setName("Tiled (" + bounds.hashCode() + "): " + bounds.toString()); //$NON-NLS-1$ //$NON-NLS-2$
        renderExecutor.eAdapters().add(renderExecutorListener);
        
        return renderExecutor;
    }

   
    
    /**
     * 
     * Returns true if these two layers are related in any way.  Two layers are related if:
     * <ul>
     *   <li>They are the same (layer = contained)
     *   <li>Layer is a part of a composite context and contained is also part of that context.</li>
     * </ul>
     *
     * @param layer
     * @param contained
     * 
     * @returns true if the two layers are part of the same context
     */
    public boolean areLayersRelatedByContext(ILayer layer, ILayer contained){
        
       if (tileCache.getKeys().size() == 0){
            //no contexts built to check against so return false;
            return false;
        }

        //they are the same; so they are related
        if (layer == contained) return true;
        
        
        Tile t = null;
        Iterator<Object> keys = tileCache.getKeys().iterator();
        while( t == null && keys.hasNext()){
            t = (Tile)tileCache.get((ReferencedEnvelope)keys.next());
        }
        if (t == null){
            //no tile found
            return false;
        }

        RenderExecutorComposite parent  = t.getRenderExecutor();
        Collection<TiledCompositeRendererImpl.RenderInfo>  kids = ((TiledCompositeRendererImpl)parent.getRenderer()).getChildren();
        
        for( Iterator<TiledCompositeRendererImpl.RenderInfo> iterator = kids.iterator(); iterator.hasNext(); ) {
            TiledCompositeRendererImpl.RenderInfo kid = iterator.next();
            IRenderContext childcontext = kid.getRenderer().getContext()
            ;
            if (childcontext instanceof CompositeRenderContext){
                if (((CompositeRenderContext) childcontext).getLayers().contains(layer)){
                    if( ((CompositeRenderContext) childcontext).getLayers().contains(contained)){
                        //composite context and contains layer + contained
                        //these layers are related through a composite context
                        return true;
                    }else{
                        //composite context that contains layer but not contained
                        //cannot be related
                        return false;
                    }
                }
            }else{
                if (childcontext.getLayer() == layer){
                    //layer is not part of a composite context and cannot be related in 
                    //any way to contained
                    return false;
                }
            }
            
        }
        return false;
    }
    
    /**
     * @return null as there is no render executor for this; there are multiple render executors
     */
    public RenderExecutor getRenderExecutor() {
        return null;
    }
    
    /**
     * @see org.locationtech.udig.project.render.impl.RenderManagerImpl#setRenderExecutor(org.locationtech.udig.project.render.RenderExecutor)
     */
    public void setRenderExecutor(RenderExecutor newRenderExecutor) {
        throw new UnsupportedOperationException("This operation is not supported for a Tiled Render Manager Dynamic."); //$NON-NLS-1$
    }

    /**
     * Throws an unsupported operation exception.
     * 
     * <p>The tiled rendering system does not have
     * a single set of renderers.</p>
     * 
     * @returns 
     */
    @Override
    public List<IRenderer> getRenderers(){
        throw new UnsupportedOperationException("Invalid operation for a Tiled RenderManager Dynamic."); //$NON-NLS-1$
    }

    /**
     * Returns a list of tiles that match the given tile bounds.  If the given tile 
     * bounds are null, they will be calculated from the viewport bounds.  The returned
     * list of tiles can then be cached for faster painting/panning.
     * 
     * This is assumed to be called by the viewport.  As a result any tile withing the bounds provided
     * is marked as on-screen; others will be marked as offscreen; which means it is possible for them to be
     * removed.
     * 
     */
    @Override
    public java.util.Map<ReferencedEnvelope, Tile> getTiles(
			Collection<ReferencedEnvelope> bounds) {
    	// if no tile bounds were passed, calculate them from the current viewport bounds
    	if (bounds == null) {
    		ReferencedEnvelope viewportbounds = getViewportModelInternal().getBounds();
    		bounds = computeTileBounds(viewportbounds, viewportbounds.getWidth() / getMapDisplay().getWidth());
    	}
    	
    	// loop through the tile bounds and return a tile for each
        java.util.Map<ReferencedEnvelope, Tile> newTiles = new HashMap<ReferencedEnvelope, Tile>();
        try {
            for( Iterator<ReferencedEnvelope> iterator = bounds.iterator(); iterator.hasNext(); ) {
                ReferencedEnvelope env = (ReferencedEnvelope) iterator.next();

                // get the tile
                Tile tile = getOrCreateTile(env);
                tile.setScreenState(Tile.ScreenState.ONSCREEN);
                if (tile.getRenderState() == Tile.RenderState.INVALID){
                    tile.getRenderExecutor().setRenderBounds(tile.getReferencedEnvelope());
                    tile.getRenderExecutor().setState(IRenderer.RENDER_REQUEST);
                }
                newTiles.put(env, tile);
            }
            
            //update the other tiles to an offscreen state
            for( Iterator<Object> iterator = tileCache.getKeys().iterator(); iterator.hasNext(); ) {
                ReferencedEnvelope referencedEnvelope = (ReferencedEnvelope) iterator.next();
                Tile t = (Tile)tileCache.get(referencedEnvelope);
                if (t != null && !newTiles.containsKey(referencedEnvelope)){
                    t.setScreenState(Tile.ScreenState.OFFSCREEN);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return newTiles;
    }    
    
    /**
     * Sets the rendered state on all tiles to invalid.  This means that next time
     * this tile is access it will be re-rendered.
     * 
     */
    private void invalidateAllTilesRenderState(){
        for( Iterator<Object> iterator = tileCache.getKeys().iterator(); iterator.hasNext(); ) {
            ReferencedEnvelope key = (ReferencedEnvelope)iterator.next();
            Tile t = (Tile)tileCache.get(key);
            if (t != null){
                t.setRenderState(Tile.RenderState.INVALID);
            }
        }
    }
    
    /**
     * Sets the context state on all tiles to be invalid.  This means next time this tile
     * is used for anything the context (rendering state) will be re-validated against the current stack
     * and modifications made as necessary.
     */
    private void invalidateAllTileContext(){
        for( Iterator<Object> iterator = tileCache.getKeys().iterator(); iterator.hasNext(); ) {
            ReferencedEnvelope key = (ReferencedEnvelope)iterator.next();
            Tile t = (Tile)tileCache.get(key);
            if (t != null){
                t.setContextState(Tile.ContextState.INVALID);
            }
        }
    }

    
    /* The section below only exists for testing against a regular viewport */

    private BufferedImage screen = null;
    
    /**
     * This is the image that is to be drawn on the screen.
     * 
     * So for now we get the images necessary to form the tiles and compile into a single
     * image for the viewport.
     */
    @Override
    public RenderedImage getImage() {
        ReferencedEnvelope bounds = getViewportModelInternal().getBounds();
        
        Collection<ReferencedEnvelope> tileBounds = computeTileBounds(bounds, bounds.getWidth() / getMapDisplay().getWidth());
    
        if (screen == null || screen.getWidth() != getMapDisplay().getWidth() || screen.getHeight() != getMapDisplay().getHeight()){
            screen = new BufferedImage(getMapDisplay().getWidth(), getMapDisplay().getHeight(), BufferedImage.TYPE_INT_ARGB);   
        }
        
        Graphics2D g = screen.createGraphics();

        //clear existing image
        g.setBackground(new Color(0, 0, 0, 0));
        g.clearRect(0,0, getMapDisplay().getWidth(), getMapDisplay().getHeight());
        
        //update screen tile states
        for( Iterator<Object> iterator = tileCache.getKeys().iterator(); iterator.hasNext(); ) {
            ReferencedEnvelope referencedEnvelope = (ReferencedEnvelope) iterator.next();
            Tile t = (Tile)tileCache.get(referencedEnvelope);
            if (t != null && !tileBounds.contains(referencedEnvelope)){
                t.setScreenState(Tile.ScreenState.OFFSCREEN);
            }
        }
        
        try {
            for( Iterator<ReferencedEnvelope> iterator = tileBounds.iterator(); iterator.hasNext(); ) {
                ReferencedEnvelope env = (ReferencedEnvelope) iterator.next();

                // get the tile image
                Tile tile = getOrCreateTile(env);
                tile.setScreenState(Tile.ScreenState.ONSCREEN);
                
                BufferedImage tileimage = tile.getBufferedImage(); 

                //write the image to the screen image
                Point a = getViewportModelInternal().worldToPixel(new Coordinate(env.getMinX(), env.getMinY()));
                Point b = getViewportModelInternal().worldToPixel(new Coordinate(env.getMaxX(), env.getMaxY()));
                
                g.drawImage(tileimage, a.x, b.y, b.x - a.x, a.y - b.y, null);
                
                //draw a border around the tile for debugging
                g.setColor(Color.BLACK);
                
                g.drawLine(a.x, a.y, a.x, b.y);
                g.drawLine(a.x, b.y, b.x, b.y);
                g.drawLine(b.x, b.y, b.x, a.y);
                g.drawLine(b.x, a.y, a.x, a.y);
                
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        g.dispose();

        return screen;
    }
    
    
    /* The functions below are designed to handle events that affect the rendering system.
     * This includes such things as layers added/removed and zorder changes; crs changes.
     * 
     * It does not include things such as viewport bounds changing as those such events
     * should not affect the rendering stack
     */
    
    /**
     * To be called when the order of layers has changed
     * <p>
     * This function invalidates all tiles and contexts.  The
     * contexts much be invalidated because the WMS layers may have been 
     * split which means instead of a single context we now need two (or merged).
     * </p>
     */
    public void zorderChanged( Notification msg ){
        getRendererCreator().changed(msg);
        
        invalidateAllTilesRenderState();
        invalidateAllTileContext();
        
        //refresh this layer
        Layer newV=(Layer) msg.getNewValue();
        refresh(newV, null);
    }
    
    /**
     * To be called when layers have been added.
     * <p>
     * This function invalidates all tiles and associated contexts then calls
     * refresh on the layers that have been added to re-render those layers
     * </p>
     */
    @SuppressWarnings("unchecked")
    public void layersAdded(Notification msg){
        getRendererCreator().changed(msg);
        
        invalidateAllTileContext();
        
        //lets only call refresh on the added tiles
        ArrayList<Layer> addedLayers = new ArrayList<Layer>();
        switch( msg.getEventType() ) {
        case Notification.ADD: {
            Layer layer = (Layer) msg.getNewValue();
            addedLayers.add(layer);
            break;
        }
        case Notification.ADD_MANY: {
            for( Layer layer : (Collection< ? extends Layer>) msg.getNewValue() ) {
                addedLayers.add(layer);
            }
        }}
        //refresh the layers that have been added
        for( Layer layer : addedLayers ) {
            refresh(layer, null);
        }
    }
    
    /**
     * To be called when layer(s) have been removed.
     * <p>
     * This function invalidates the context and render state of all tiles; then re-renders
     * the tiles in the current viewport.
     * 
     */
    public void layersRemoved(Notification msg){
        getRendererCreator().changed(msg);
        
        invalidateAllTileContext();
        invalidateAllTilesRenderState();
        
        softRefresh(null);
    }
    
    
    /**
     * Called when the CRS has changed.
     * <p>
     * This function removes all tiles and rebuilds the ones that are in the 
     * current viewport.
     * </p> 
     */
    public void crsChanged(Notification msg){
        //remove all existing images so everything will be recreated
        disposeAllTiles();
        tileCache.clear();
        importantTiles.clear();
        
        softRefresh(null);
    }
    
    /**
     * Called when the viewport changes.
     * 
     * <p>This function creates and renders the tiles for the new viewport</p>
     *
     * @param msg
     */
    public void viewportChanged(Notification msg){
        if (getViewportModelInternal().isBoundsChanging()){
            if (getMapDisplay() instanceof ViewportPaneTiledSWT){
                Collection<Tile> newtiles = ((ViewportPaneTiledSWT)getMapDisplay()).updateReadyTiles();
                renderTiles(newtiles);
            }
        }else{
            if (getMapDisplay() instanceof ViewportPaneTiledSWT){
                cancelTileRenderJobs();
            }
            if (ApplicationGIS.getActiveMap() != null
                    && ApplicationGIS.getVisibleMaps().contains(
                            ApplicationGIS.getActiveMap())) {

                // perform a soft refresh with re-rendering any of the data
                softRefresh(null);
            }
        }
        
    }
    
    /**
     * Called when a layer blackboard entry has been changed.
     * 
     * <p>
     * If the layer is not a selection layer this function invalidates all tile render states and
     * class refresh(layer, null) to re-render the layer.
     * </p>
     *
     * @param layer  the layer whose blackboard changed.
     */
    public void blackBoardChanged(Layer layer){
        if (layer instanceof SelectionLayer){
            return;
        }
        //all tiles need to be re-rendered
        //Maybe we should be invalidating the contexts here?  Does a blackboard change
        //mean a different renderer should be used?
        invalidateAllTilesRenderState();
        refresh(layer, null);
    }  
    
    
    /**
     * This function should be called when a layer is made visible.
     * <p>
     * The tiles that are off screen will have there render state set
     * to invalid because we don't know if the layer has been rendered for them or not.
     * </p>
     * <p>If layers are made invisible we don't need to call this as refreshImage() is called
     * and that will deal with re-composing the image and removing the hidden layer.</p>
     * 
     */
    public void layerMadeVisible(Layer layer){
        //tiles on the screen can have there contexts invalidated
        ReferencedEnvelope areaOfInterest = getMap().getViewportModel().getBounds();
        ReferencedEnvelope viewportbounds = getMap().getViewportModel().getBounds();
        double resolution = viewportbounds.getWidth() / getMapDisplay().getWidth();
        Collection<ReferencedEnvelope> tileBounds = computeTileBounds(areaOfInterest, resolution);
        
        for( Iterator<Object> iterator = tileCache.getKeys().iterator(); iterator.hasNext(); ) {
            ReferencedEnvelope key = (ReferencedEnvelope)iterator.next();
            Tile t = (Tile)tileCache.get(key);
            if (!tileBounds.contains(key)){
                //we can't assume that all layers have been rendered outside of the current tile bounds
                //really we only need to do this when tiles are made visible...
                t.setRenderState(Tile.RenderState.INVALID);
            }
        }
    }
    
    /**
     * This is job to render tiles in a separate thread.
     * 
     * <p>This is currently used to render tiles outside the display thread
     * as we zoom around.
     * </p>
     * @author Emily Gouge (Refractions Research, Inc.)
     * @since 1.2.0
     */
    class RenderTileJob extends Job{

        private List<Tile> tilestorender = new ArrayList<Tile>();
        private TiledRenderManagerDynamic manager;
        
        /**
         * Creates a new job from a given manager
         * @param manager
         */
        public RenderTileJob( TiledRenderManagerDynamic manager ) {
            super("Pan Tile Rendering Job"); //$NON-NLS-1$
            this.manager = manager;
        }
        
        /**
         * Adds tiles to the list of tiles to be processed.
         *
         * @param tiles
         */
        public void addTiles(Collection<Tile> tiles){
            tilestorender.addAll(tiles);
        }

        /**
         * Removes all tiles from the list to be rendered
         */
        public void clear(){
            tilestorender.clear();
        }
        /**
         * Renders each tile in the list; until the list is empty
         */
        @Override
        protected IStatus run( IProgressMonitor monitor ) {
            if (tilestorender.size() == 0) return Status.OK_STATUS;
            Tile t = tilestorender.remove(0);
            manager.renderTile(t);
            if (tilestorender.size() > 0){
                schedule();
            }
            return Status.OK_STATUS;
        }
        
    }    
}
