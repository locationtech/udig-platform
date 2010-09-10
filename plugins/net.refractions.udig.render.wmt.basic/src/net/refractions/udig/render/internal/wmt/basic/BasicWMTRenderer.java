/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.render.internal.wmt.basic;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.internal.PreferenceConstants;
import net.refractions.udig.catalog.internal.wmt.WMTRenderJob;
import net.refractions.udig.catalog.internal.wmt.tile.WMTTile;
import net.refractions.udig.catalog.internal.wmt.tile.WMTTileImageReadWriter;
import net.refractions.udig.catalog.internal.wmt.tile.WMTTileSetWrapper;
import net.refractions.udig.catalog.internal.wmt.ui.properties.WMTLayerProperties;
import net.refractions.udig.catalog.internal.wmt.wmtsource.WMTSource;
import net.refractions.udig.catalog.wmsc.server.Tile;
import net.refractions.udig.catalog.wmsc.server.TileListener;
import net.refractions.udig.catalog.wmsc.server.TileRange;
import net.refractions.udig.catalog.wmsc.server.TileRangeInMemory;
import net.refractions.udig.catalog.wmsc.server.TileRangeOnDisk;
import net.refractions.udig.catalog.wmsc.server.TileSet;
import net.refractions.udig.catalog.wmsc.server.TileWorkerQueue;
import net.refractions.udig.catalog.wmsc.server.WMSTile;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.StyleBlackboard;
import net.refractions.udig.project.internal.render.impl.RendererImpl;
import net.refractions.udig.project.render.RenderException;
import net.refractions.udig.render.internal.wmsc.basic.WMSCTileCaching;
import net.refractions.udig.render.wmt.basic.WMTPlugin;
import net.refractions.udig.render.wmt.basic.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.renderer.lite.gridcoverage2d.GridCoverageRenderer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.StyleBuilder;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * The basic renderer for a WMT Layer
 * <p>
 * </p>
 */
public class BasicWMTRenderer extends RendererImpl {
    
    private static StyleBuilder styleBuilder = new StyleBuilder();

    private TileListenerImpl listener = new TileListenerImpl();
    
    private final static boolean testing = false;  // for debugging
    private static final boolean TESTING = WMTPlugin.getDefault().isDebugging();
    
    private static int staticid = 0; // for debugging
    
    /**
     * Static thread pools that will be reused for each renderer that gets created
     */
    private static TileWorkerQueue requestTileWorkQueue = new TileWorkerQueue();
    private static TileWorkerQueue writeTileWorkQueue = new TileWorkerQueue();
    
    /**
     * Use a blocking queue to keep track of and notice when tiles are ready to draw
     */
    private BlockingQueue<Tile> tilesToDraw_queue = new PriorityBlockingQueue<Tile>();
    
    public BasicWMTRenderer() {
    }

    @Override
    public void render(Graphics2D destination, IProgressMonitor monitor) throws RenderException {
        render(destination, getRenderBounds(), monitor);
    }

    @Override
    public void render(IProgressMonitor monitor) throws RenderException {
        Graphics2D graphics = (Graphics2D) getContext().getImage().getGraphics();
        render(graphics, getRenderBounds(), monitor);
    }

//    private int count = 1;
//    private IProgressMonitor previousMonitor = null;
//    public void render(Graphics2D destination, Envelope bounds2,
//            IProgressMonitor monitor) throws RenderException {
//        WMTPlugin.trace("[BasicWMTRender.render] is called"); //$NON-NLS-1$
//        try {
//            int count = this.count++;
//            
//            setState(STARTING);
//            // check if the same Monitor is shared for every render call (no, it is not)
//            System.out.println("Rendercall: " + count + " " + (previousMonitor == monitor));
//            previousMonitor = monitor;
//            
//            if (monitor.isCanceled()) {
//                System.out.println("cancel 1 " + count);
////                setState(IRenderer.CANCELLED);
////                monitor.done();
//                return;
//            }
//            
//            System.out.println("Before sleep " + count);
//            monitor.beginTask("Render WMT", 100); //$NON-NLS-1$
//            Thread.sleep(10000);
//            System.out.println("Behind sleep " + count);
//            
//            if (monitor.isCanceled()) {
//                System.out.println("cancel 2 " + count);
////                setState(IRenderer.CANCELLED);
////                monitor.done();
//                return;
//            }
//            //monitor.done();
//        } catch (Exception e) {
//            System.out.println("Wait interrupted");
//        }
//    }
    
    public void render(Graphics2D destination, Envelope bounds, IProgressMonitor monitor)
            throws RenderException {
        WMTPlugin.trace("[BasicWMTRender.render] is called"); //$NON-NLS-1$

        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        monitor.beginTask("Render WMT", 100); //$NON-NLS-1$
        setState(STARTING);
        
        ILayer layer = null;
        try {
            layer = getContext().getLayer();
            // assume everything will work fine
            layer.setStatus(ILayer.DONE);
            layer.setStatusMessage(""); //$NON-NLS-1$

            WMTSource wmtSource = getWmtSourceFromLayer(layer);

            if (wmtSource == null)
                throw new UnsupportedOperationException(Messages.Render_Error_NoSource);

            // Layer properties
            WMTLayerProperties layerProperties = new WMTLayerProperties((StyleBlackboard) layer
                    .getStyleBlackboard());

            // Get map extent, which should be drawn
            ReferencedEnvelope mapExtent = getRenderBounds();
            if (mapExtent == null) {
                mapExtent = context.getViewportModel().getBounds();
            }

            // Scale
            double scale = getContext().getViewportModel().getScaleDenominator();
            WMTPlugin.trace("[BasicWMTRender.render] Scale: " + scale); //$NON-NLS-1$
            
            WMTRenderJob renderJob = null;
            try {
                renderJob = WMTRenderJob.createRenderJob(mapExtent, scale, wmtSource);
            } catch (Exception exc) {
                throw new UnsupportedOperationException(Messages.Render_Error_Projection);
            }
            
            // Find tiles
            Map<String, Tile> tileList = wmtSource.cutExtentIntoTiles(renderJob,
                    WMTRenderJob.getScaleFactor(), false, layerProperties);

            // if we have nothing to display, return
            if (tileList.isEmpty()) {
                throw new UnsupportedOperationException(Messages.Render_Error_NoData);
            }

            // check if this are too many tiles
            if ((tileList = checkTooManyTiles(layer, wmtSource, layerProperties, renderJob,
                    tileList)).isEmpty()) {
                throw new UnsupportedOperationException(Messages.Render_Error_TooManyTiles);
            }

            // Download and display tiles

            // look up the preference for caching tiles on-disk or in
            // memory and use the proper tilerange for that.
            TileRange range = createTileRange(wmtSource, renderJob, tileList);

            // create an empty raster symbolizer for rendering
            RasterSymbolizer style = styleBuilder.createRasterSymbolizer();

            // setup how much each tile is worth for the monitor work %
            int tileCount = range.getTileCount();
            int tileWorth = (tileCount / 100) * tileCount;

            int thisid = 0;
            if (testing) {
                staticid++;
                thisid = staticid;
            }

            // first render any tiles that are ready and render non-ready tiles with blank images
            Map<String, Tile> tiles = range.getTiles();
            Set<String> notRenderedTiles = new HashSet<String>();
            Set<String> renderedTiles = new HashSet<String>();

            renderReadyTiles(destination, monitor, renderJob, style, tileWorth, thisid, tiles,
                    notRenderedTiles, renderedTiles);
            
            setState(RENDERING);

            // if the tilerange is not already completed, then load
            // the missing tiles
            if (!notRenderedTiles.isEmpty()) {
                renderNotRenderedTiles(destination, monitor, renderJob, range, style, tileWorth,
                        thisid, notRenderedTiles, renderedTiles);
            }

            if (testing) {
                System.out.println("DONE!!!: " + thisid); //$NON-NLS-1$
            }
        } catch (UnsupportedOperationException doneExc) {
            setDone(monitor);
            
            layer.setStatus(ILayer.ERROR);
            layer.setStatusMessage(doneExc.getMessage());
            WMTPlugin.log("[BasicWMTRenderer.render] Error: ", doneExc); //$NON-NLS-1$

            return;
        } catch (CancellationException cancelExc) {
            return;
        } catch (Exception ex) {
            WMTPlugin.log("[BasicWMTRenderer.render] Unexpected Error: ", ex); //$NON-NLS-1$
        }

        setDone(monitor);
    }

    private void renderNotRenderedTiles(Graphics2D destination, IProgressMonitor monitor,
            WMTRenderJob renderJob, TileRange range, RasterSymbolizer style, int tileWorth,
            int thisid, Set<String> notRenderedTiles, Set<String> renderedTiles) throws Exception {
        checkCancelState(monitor, thisid, false);            
        
        // set the listener on the tile range
        range.addListener(listener);
        
        // load the missing tiles by sending requests for them
        range.loadTiles(monitor);
        
        // block until all the missing tiles have come through (and draw them
        // as they are added to the blocking queue
        while (!notRenderedTiles.isEmpty()) {
            // check that the rendering is not canceled
            checkCancelState(monitor, thisid, true); 
            
            if (testing) {
                System.out.println("BLOCKED: "+thisid); //$NON-NLS-1$
                System.out.println("waiting on: " + notRenderedTiles.size()+" tiles"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            
            Tile tile = null;
            try {
                Object element = null;
                
                /* get the next tile that is ready to render, 
                 * check after 1 sec if the rendering was canceled
                 */
                while ((element = tilesToDraw_queue.poll(1000, TimeUnit.MILLISECONDS)) == null) {
                    checkCancelState(monitor, thisid, true); 
                }
                
                tile = (Tile) element;

                if (testing) {
                    System.out.println("removed from queue: "+tile.getId()); //$NON-NLS-1$
                }
            } catch (InterruptedException ex) {
                if (testing) {
                    System.out.println("InterruptedException trying to take: "+ex); //$NON-NLS-1$
                }
            }
            
            if (testing) {
                System.out.println("UNBLOCKED!!!: "+thisid); //$NON-NLS-1$
            }
            
            // check that the rendering is not canceled again after block
            checkCancelState(monitor, thisid, true); 
            
            // check that the tile's bounds are within the current
            // context's bounds (if it's not, don't bother drawing it) and also
            // only draw tiles that haven't already been drawn (panning fast
            // can result in listeners being notified the same tile is ready multiple
            // times but we don't want to draw it more than once per render cycle)
            //ReferencedEnvelope viewbounds = getContext().getViewportModel().getBounds();            
            
            ReferencedEnvelope viewbounds = renderJob.projectMapToTileCrs(
                    context.getViewportModel().getBounds()); 
            
            if (tile != null && tile.getBufferedImage() != null &&
                    viewbounds != null && 
                    viewbounds.intersects(tile.getBounds()) && 
                    !renderedTiles.contains(tile.getId()) &&
                    notRenderedTiles.contains(tile.getId())) {
                try {
                    renderedTiles.add(tile.getId());
                    renderTile(destination, (WMTTile) tile, style, renderJob);
                    
                } catch(Exception exc) {
                    WMTPlugin.log("[BasicWMTRender.render] renderTile failed (2): " + tile.getId(), exc); //$NON-NLS-1$
                }
                monitor.worked(tileWorth);  // inc the monitor work by 1 tile
                setState(RENDERING); // tell renderer new data is ready                
            }
            
            // remove the tile from the not rendered list regardless
            // of whether it was actually drawn (this is to prevent
            // this render cycle from blocking endlessly waiting for tiles
            // that either didn't return or had some error)
            notRenderedTiles.remove(tile.getId());
        }
    }

    private void checkCancelState(IProgressMonitor monitor, int thisid, boolean clearList ) {
        if (monitor.isCanceled()) {
//                        setState(CANCELLED);       
            if (testing) {
                System.out.println("monitor CANCELED!!! (tilesToDraw_queue.clear()-1): "+thisid); //$NON-NLS-1$
            }
            
            if (clearList) {
                tilesToDraw_queue.clear(); 
            }
            
            throw new CancellationException();
        }
    }

    private void renderReadyTiles( Graphics2D destination, IProgressMonitor monitor,
            WMTRenderJob renderJob, RasterSymbolizer style, int tileWorth, int thisid,
            Map<String, Tile> tiles, Set<String> notRenderedTiles, Set<String> renderedTiles )
            throws Exception {
        for (String key : tiles.keySet()) {
            checkCancelState(monitor, thisid, false);
            
            Tile tile = tiles.get(key);
            if (tile != null && tile.getBufferedImage() != null && tile.getTileState() != WMSTile.INERROR) {
                try {
                    renderedTiles.add(key);
                    renderTile(destination, (WMTTile) tile, style, renderJob);
                } catch(Exception exc) {
                    WMTPlugin.log("[BasicWMTRender.render] renderTile failed (1): " + tile.getId(), exc); //$NON-NLS-1$
                }
                monitor.worked(tileWorth);  // inc the monitor work by 1 tile
            }
            else {
                // set the tile blank (removing any previous content) and add it
                // to be drawn later
                notRenderedTiles.add(key);
                renderBlankTile(destination, (WMTTile) tile, renderJob);
            }
        }
    }

    private TileRange createTileRange( WMTSource wmtSource, WMTRenderJob renderJob,
            Map<String, Tile> tileList ) {
        TileRange range;
        TileSet tileset = new WMTTileSetWrapper(wmtSource);
    
        String value = CatalogPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.P_WMSCTILE_CACHING);
        if (value.equals(WMSCTileCaching.ONDISK.toString())) {
            String dir = CatalogPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.P_WMSCTILE_DISKDIR);
            WMTTileImageReadWriter tileReadWriter = new WMTTileImageReadWriter(dir);
            
            range = new TileRangeOnDisk(null, tileset, renderJob.getMapExtentTileCrs(), 
                    tileList, requestTileWorkQueue, writeTileWorkQueue, tileReadWriter);
        } else {
            range = new TileRangeInMemory(null, tileset, renderJob.getMapExtentTileCrs(),
                    tileList, requestTileWorkQueue);
        }
        
        return range;
    }

    private Map<String, Tile> checkTooManyTiles(ILayer layer, WMTSource wmtSource,
            WMTLayerProperties layerProperties, WMTRenderJob renderJob, 
            Map<String, Tile> tileList) {
        int tilesCount = tileList.size();
        
        if (tilesCount > WMTRenderJob.getTileLimitWarning()) {
            // too many tiles, let's use the recommended zoom-level (if it wasn't already used)
            Boolean selectionAutomatic = layerProperties.getSelectionAutomatic();
            
            if ((selectionAutomatic != null) && (selectionAutomatic == false)) {
                tileList.clear();
                tileList = wmtSource.cutExtentIntoTiles(renderJob, WMTRenderJob.getScaleFactor(), 
                        true, layerProperties);                
                tilesCount = tileList.size();
            }
            
            // show a warning about this
            layer.setStatus(ILayer.WARNING);
            layer.setStatusMessage(Messages.Render_Warning_TooManyTiles);
            
            WMTPlugin.trace("[BasicWMTRender.render] Set WARNING_TOO_MANY_TILES"); //$NON-NLS-1$
        }
                    
        if (tilesCount > WMTRenderJob.getTileLimitError()) {
            // this is just too much, cancel            
            WMTPlugin.trace("[BasicWMTRender.render] Set ERROR_TOO_MANY_TILES"); //$NON-NLS-1$
            
            return Collections.emptyMap();                
        }
        
        return tileList;
    }

    private void setDone( IProgressMonitor monitor ) {
        setState(DONE);
        monitor.done();
    }
    
    private WMTSource getWmtSourceFromLayer(ILayer layer) {
        IGeoResource resource = layer.findGeoResource(WMTSource.class);
        
        if (resource != null) {
            WMTSource wmtSource = null;
            try {            
                wmtSource = resource.resolve(WMTSource.class, null);
                
                return wmtSource;
            } catch (Exception e) {}
        }
        
        return null;
    }

    /**
     * 
     * @see net.refractions.udig.render.internal.wmsc.basic#renderTile(Graphics2D graphics, WMTTile tile, CoordinateReferenceSystem crs, RasterSymbolizer style)
     * @param graphics
     * @param tile
     * @param style
     * @throws FactoryException
     * @throws TransformException
     * @throws RenderException 
     */
    private void renderTile(Graphics2D graphics, WMTTile tile, RasterSymbolizer style,
            WMTRenderJob renderJob) throws Exception {
        
        if (tile == null || tile.getBufferedImage() == null) {
            return;
        }
        
        // create a gridcoverage from the tile image        
        GridCoverageFactory factory = new GridCoverageFactory();
        
        // get the tile bounds in the CRS the tiles were drawn in
        ReferencedEnvelope tileBndsMercatorRef = renderJob.projectTileToTileProjectedCrs(tile.getExtent());
        
        GridCoverage2D coverage = (GridCoverage2D) factory.create("GridCoverage", tile.getBufferedImage(), tileBndsMercatorRef); //$NON-NLS-1$        
         
        Envelope2D coveragebounds = coverage.getEnvelope2D();

        // bounds of tile
        ReferencedEnvelope bnds = new ReferencedEnvelope(coveragebounds.getMinX(), coveragebounds.getMaxX(), 
                coveragebounds.getMinY(), coveragebounds.getMaxY(), renderJob.getCrsTilesProjected());
        
        // reproject tile bounds to map CRS
        bnds = renderJob.projectTileProjectedToMapCrs(bnds);

        //determine screen coordinates of tiles
        Point upperLeft = getContext().worldToPixel(new Coordinate(bnds.getMinX(), bnds.getMinY()));
        Point bottomRight = getContext().worldToPixel(new Coordinate(bnds.getMaxX(), bnds.getMaxY()));
        Rectangle tileSize = new Rectangle(upperLeft);
        tileSize.add(bottomRight);
        
        
        //render
        try{
            GridCoverageRenderer paint = new GridCoverageRenderer(getContext().getCRS(), bnds, tileSize);
            
            paint.paint(graphics, coverage, style);
           
            if(TESTING){
//            if(true){
                /* for testing draw border around tiles */
                graphics.setColor(Color.BLACK);
                graphics.drawLine((int)tileSize.getMinX(), (int)tileSize.getMinY(), (int)tileSize.getMinX(), (int)tileSize.getMaxY());
                graphics.drawLine((int)tileSize.getMinX(), (int)tileSize.getMinY(), (int)tileSize.getMaxX(), (int)tileSize.getMinY());
                graphics.drawLine((int)tileSize.getMaxX(), (int)tileSize.getMinY(), (int)tileSize.getMaxX(), (int)tileSize.getMaxY());
                graphics.drawLine((int)tileSize.getMinX(), (int)tileSize.getMaxY(), (int)tileSize.getMaxX(), (int)tileSize.getMaxY());
                graphics.drawString(tile.getId(), ((int)tileSize.getMaxX()-113), 
                        ((int)tileSize.getMaxY()-113));
            }
        } catch (Throwable t) {
            WMTPlugin.log("Error Rendering tile. Painting Tile " +  tile.getId(), t); //$NON-NLS-1$
        }
    }
    
    /**
     * Clears the area of the tile on the graphics
     * 
     * @param graphics graphics to draw onto
     * @param style raster symbolizer 
     * @throws FactoryException 
     * @throws TransformException 
     * @throws RenderException 
     */
    private void renderBlankTile(Graphics2D graphics, WMTTile tile,
            WMTRenderJob renderJob) 
    throws Exception {
        
        if (tile == null) {
            return;
        }
        
        // get the bounds of the tile and convert to necessary viewport projection
        Envelope bnds = renderJob.projectTileToMapCrs(tile.getExtent()); 
        
        // determine screen coordinates of tiles
        Point upperLeft = getContext().worldToPixel(new Coordinate(bnds.getMinX(), bnds.getMinY()));
        Point bottomRight = getContext().worldToPixel(new Coordinate(bnds.getMaxX(), bnds.getMaxY()));
        Rectangle tileSize = new Rectangle(upperLeft);
        tileSize.add(bottomRight);

        // render
        try {
            graphics.setBackground(new Color(255, 255, 255, 0));  // set the tile transparent for now
            graphics.clearRect(tileSize.x, tileSize.y, tileSize.width, tileSize.height);

            if( TESTING ){
                /* for testing draw border around tiles */
                graphics.setColor(Color.BLACK);
                graphics.drawLine((int)tileSize.getMinX(), (int)tileSize.getMinY(), (int)tileSize.getMinX(), (int)tileSize.getMaxY());
                graphics.drawLine((int)tileSize.getMinX(), (int)tileSize.getMinY(), (int)tileSize.getMaxX(), (int)tileSize.getMinY());
                graphics.drawLine((int)tileSize.getMaxX(), (int)tileSize.getMinY(), (int)tileSize.getMaxX(), (int)tileSize.getMaxY());
                graphics.drawLine((int)tileSize.getMinX(), (int)tileSize.getMaxY(), (int)tileSize.getMaxX(), (int)tileSize.getMaxY());
            }
        } catch (Throwable t) {            
            WMTPlugin.log("Error Rendering Blank tile. Painting Tile: " + tile.getId(), t); //$NON-NLS-1$
        }
    } 
    
    /**
     * TileListener implementation for rendering new tiles that are ready
     * 
     * This is a plain copy of the BasiWMSCRenderer implementation!
     * 
     * 
     * @author GDavis
     * @since 1.1.0
     */
    protected class TileListenerImpl implements TileListener {

        public TileListenerImpl() {
            
        }
        public void notifyTileReady( net.refractions.udig.catalog.wmsc.server.Tile tile ) {
            // set the area that needs updating
            //setRenderBounds(tile.getBounds());
            
            // if the rendering is already in a rendering state, queue this tile
            // to draw and tell the renderer more data is ready, 
            // otherwise create a new rendering thread (which will check the tiles afresh)
            int currentState = getState();
            if ( (currentState == RENDERING || currentState == STARTING) ) {
                // queue the tile to draw
                try {
                    tilesToDraw_queue.put(tile);
                    if (testing) {
                        System.out.println("added to queue: "+tile.getId()); //$NON-NLS-1$
                    }
                } catch (InterruptedException e) {
                    WMTPlugin.log("Error while added tile to queue.", e); //$NON-NLS-1$
                }
            }
            else {  
                if (testing) {
                    System.out.println("RENDER_REQUEST: "+tile.getId()); //$NON-NLS-1$
                }
                setState(RENDER_REQUEST);   // start a new rendering thread
               
            }
        }        
    }
}
