/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2008-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.render.internal.wmsc.basic;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.data.ows.AbstractOpenWebService;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.ows.wms.WebMapServer;
import org.geotools.referencing.CRS;
import org.geotools.renderer.lite.RendererUtilities;
import org.geotools.renderer.lite.gridcoverage2d.GridCoverageRenderer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.StyleBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.internal.PreferenceConstants;
import org.locationtech.udig.catalog.internal.wms.WmsPlugin;
import org.locationtech.udig.catalog.wmsc.server.Tile;
import org.locationtech.udig.catalog.wmsc.server.TileListener;
import org.locationtech.udig.catalog.wmsc.server.TileRange;
import org.locationtech.udig.catalog.wmsc.server.TileRangeInMemory;
import org.locationtech.udig.catalog.wmsc.server.TileRangeOnDisk;
import org.locationtech.udig.catalog.wmsc.server.TileSet;
import org.locationtech.udig.catalog.wmsc.server.TileWorkerQueue;
import org.locationtech.udig.catalog.wmsc.server.TiledWebMapServer;
import org.locationtech.udig.catalog.wmsc.server.WMSTile;
import org.locationtech.udig.project.internal.render.impl.RendererImpl;
import org.locationtech.udig.project.render.IRenderer;
import org.locationtech.udig.project.render.RenderException;
import org.locationtech.udig.render.wms.basic.WMSPlugin;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Basic WMS-C Renderer. Determines what tiles from which zoom level need to be retrieved from the
 * service; retrieves these tiles; and draws them
 * 
 * @author Graham Davis, Emily Gouge, Refractions Research
 * @since 1.1.0
 */
public class BasicWMSCRenderer extends RendererImpl implements IRenderer {

    private static StyleBuilder styleBuilder = new StyleBuilder();
    private TileListenerImpl listener = new TileListenerImpl();

    private final static boolean testing = false; // for debugging
    private static final boolean TESTING = WMSPlugin.getDefault().isDebugging();

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

    @Override
    public void render( Graphics2D graphics, IProgressMonitor monitor ) throws RenderException {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        monitor.beginTask("Render WMSC", 100); //$NON-NLS-1$
        setState(STARTING);

        IGeoResource handle = getContext().getLayer().findGeoResource(TileSet.class);

        try {
            TileSet tileset = handle.resolve(TileSet.class, new SubProgressMonitor(monitor, 30));
            AbstractOpenWebService server = null;

            if (handle.canResolve(WebMapServer.class)) {
                server = handle.resolve(WebMapServer.class, new SubProgressMonitor(monitor, 30));
            }
            if (handle.canResolve(TiledWebMapServer.class)) {
                server = handle.resolve(TiledWebMapServer.class,
                        new SubProgressMonitor(monitor, 30));
            }

            // determine the bounds that need to be rendered
            ReferencedEnvelope bounds = getRenderBounds();
            if (bounds == null) {
                ReferencedEnvelope viewbounds = getContext().getImageBounds();
                if (getContext().getCRS().equals(viewbounds.getCoordinateReferenceSystem())) {
                    bounds = viewbounds;
                }
            }

            // ensure the bounds are in the right CRS
            if (!bounds.getCoordinateReferenceSystem().equals(
                    tileset.getCoordinateReferenceSystem())) {
                // need to reproject the bounds to the tile coordinate reference system.
                MathTransform transform = CRS.findMathTransform(
                        bounds.getCoordinateReferenceSystem(),
                        tileset.getCoordinateReferenceSystem());
                bounds = new ReferencedEnvelope(JTS.transform(bounds, transform),
                        tileset.getCoordinateReferenceSystem());
            }

            // determine scale factor used to determine zoom level
            // compute the scale factor based on the viewport size; we cannot use the bounds and
            // tile size becuase the
            // bounds may not be the full size of the tile and we might get the wrong scale
            double scaleFactor = getContext().getViewportModel().getBounds().getWidth()
                    / getContext().getMapDisplay().getWidth();

            // create a TileRange to handle loading the tiles
            org.locationtech.jts.geom.Envelope bnds = new org.locationtech.jts.geom.Envelope(
                    bounds.getMinX(), bounds.getMaxX(), bounds.getMinY(), bounds.getMaxY());

            Map<String, Tile> tilesInRange = tileset.getTilesFromViewportScale(bnds, scaleFactor);

            // look up the preference for caching tiles on-disk or in
            // memory and use the proper tilerange for that.
            TileRange range = null;
            String value = CatalogPlugin.getDefault().getPreferenceStore()
                    .getString(PreferenceConstants.P_WMSCTILE_CACHING);
            if (value.equals(WMSCTileCaching.ONDISK.toString())) {
                range = new TileRangeOnDisk(server, tileset, bnds, tilesInRange,
                        requestTileWorkQueue, writeTileWorkQueue);
            } else {
                range = new TileRangeInMemory(server, tileset, bnds, tilesInRange,
                        requestTileWorkQueue);
            }

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

            for( String key : tiles.keySet() ) {
                if (monitor.isCanceled()) {
                    setState(CANCELLED);
                    if (testing) {
                        System.out.println("monitor CANCELED!!!: " + thisid); //$NON-NLS-1$
                    }
                    return;
                }
                Tile tile = tiles.get(key);
                if (tile != null && tile.getBufferedImage() != null
                        && tile.getTileState() != WMSTile.INERROR) {
                    renderTile(graphics, tile, tileset.getCoordinateReferenceSystem(), style);
                    renderedTiles.add(key);
                    monitor.worked(tileWorth); // inc the monitor work by 1 tile
                } else {
                    // set the tile blank (removing any previous content) and add it
                    // to be drawn later
                    renderBlankTile(graphics, tile, tileset.getCoordinateReferenceSystem());
                    notRenderedTiles.add(key);
                }
            }
            setState(RENDERING);

            // if the tilerange is not already completed, then load
            // the missing tiles
            if (!notRenderedTiles.isEmpty()) {
                if (monitor.isCanceled()) {
                    setState(CANCELLED);
                    if (testing) {
                        System.out.println("monitor CANCELED!!!: " + thisid); //$NON-NLS-1$
                    }
                    return;
                }

                // set the listener on the tile range
                range.addListener(listener);

                // load the missing tiles by sending requests for them
                range.loadTiles(monitor);

                // block until all the missing tiles have come through (and draw them
                // as they are added to the blocking queue
                while( !notRenderedTiles.isEmpty() ) {
                    // check that the rendering is not canceled
                    if (monitor.isCanceled()) {
                        setState(CANCELLED);
                        if (testing) {
                            System.out.println("monitor CANCELED!!!: " + thisid); //$NON-NLS-1$
                        }
                        tilesToDraw_queue.clear();
                        return;
                    }

                    if (testing) {
                        System.out.println("BLOCKED: " + thisid); //$NON-NLS-1$
                        System.out.println("waiting on: " + notRenderedTiles.size() + " tiles"); //$NON-NLS-1$ //$NON-NLS-2$
                    }

                    Tile tile = null;
                    try {
                        tile = (Tile) tilesToDraw_queue.take(); // blocks until a tile is ready to
                                                                // take
                        if (testing) {
                            System.out.println("removed from queue: " + tile.getId()); //$NON-NLS-1$
                        }
                    } catch (InterruptedException ex) {
                        if (testing) {
                            System.out.println("InterruptedException trying to take: " + ex); //$NON-NLS-1$
                        }
                    }

                    if (testing) {
                        System.out.println("UNBLOCKED!!!: " + thisid); //$NON-NLS-1$
                    }

                    // check that the rendering is not canceled again after block
                    if (monitor.isCanceled()) {
                        setState(CANCELLED);
                        if (testing) {
                            System.out.println("monitor CANCELED!!!: " + thisid); //$NON-NLS-1$
                        }

                        tilesToDraw_queue.clear();
                        return;
                    }

                    // check that the tile's bounds are within the current
                    // context's bounds (if it's not, don't bother drawing it) and also
                    // only draw tiles that haven't already been drawn (panning fast
                    // can result in listeners being notified the same tile is ready multiple
                    // times but we don't want to draw it more than once per render cycle)
                    // ReferencedEnvelope viewbounds = getContext().getViewportModel().getBounds();
                    ReferencedEnvelope viewbounds = getContext().getImageBounds();
                    if (tile != null && tile.getBufferedImage() != null && viewbounds != null
                            && viewbounds.intersects(tile.getBounds())
                            && !renderedTiles.contains(tile.getId())) {

                        renderTile(graphics, tile, tileset.getCoordinateReferenceSystem(), style);
                        renderedTiles.add(tile.getId());
                        monitor.worked(tileWorth); // inc the monitor work by 1 tile
                        setState(RENDERING); // tell renderer new data is ready
                    }

                    // remove the tile from the not rendered list regardless
                    // of whether it was actually drawn (this is to prevent
                    // this render cycle from blocking endlessly waiting for tiles
                    // that either didn't return or had some error)
                    notRenderedTiles.remove(tile.getId());
                }
            }

            if (testing) {
                System.out.println("DONE!!!: " + thisid); //$NON-NLS-1$
            }

        } catch (Exception ex) {
            WmsPlugin.log("Error rendering wmsc.", ex); //$NON-NLS-1$
        }

        monitor.done();
        setState(DONE);
    }
    /**
     * @param graphics graphics to draw onto
     * @param tile to draw
     * @param style raster symbolizer
     * @throws FactoryException
     * @throws TransformException
     */
    private void renderTile( Graphics2D graphics, Tile tile, CoordinateReferenceSystem crs,
            RasterSymbolizer style ) throws FactoryException, TransformException {

        if (tile == null || tile.getBufferedImage() == null) {
            return;
        }

        // create a gridcoverage from the tile image
        Envelope bounds = tile.getBounds();
        GridCoverageFactory factory = new GridCoverageFactory();
        ReferencedEnvelope ref = new ReferencedEnvelope(bounds.getMinX(), bounds.getMaxX(),
                bounds.getMinY(), bounds.getMaxY(), crs);
        GridCoverage2D coverage = (GridCoverage2D) factory.create(
                "GridCoverage", tile.getBufferedImage(), ref); //$NON-NLS-1$        
        Envelope2D coveragebounds = coverage.getEnvelope2D();

        // bounds of tile
        Envelope tilebBounds = new Envelope(coveragebounds.getMinX(), coveragebounds.getMaxX(),
                coveragebounds.getMinY(), coveragebounds.getMaxY());

        // convert bounds to necessary viewport projection
        CoordinateReferenceSystem tileCrs = getContext().getCRS();
        if (!coverage.getCoordinateReferenceSystem().equals(tileCrs)) {
            MathTransform transform = CRS.findMathTransform(
                    coverage.getCoordinateReferenceSystem(), tileCrs);
            tilebBounds = JTS.transform(tilebBounds, transform);
        }

        // determine screen coordinates of tiles
        Point upperLeft = getContext().worldToPixel(
                new Coordinate(tilebBounds.getMinX(), tilebBounds.getMinY()));
        Point bottomRight = getContext().worldToPixel(
                new Coordinate(tilebBounds.getMaxX(), tilebBounds.getMaxY()));
        Rectangle tileSize = new Rectangle(upperLeft);
        tileSize.add(bottomRight);

        // render
        try {
            AffineTransform worldToScreenTransform = RendererUtilities.worldToScreenTransform(
                    tilebBounds, tileSize, tileCrs);
            GridCoverageRenderer paint = new GridCoverageRenderer(tileCrs, tilebBounds, tileSize,
                    worldToScreenTransform);

            paint.paint(graphics, coverage, style);

            if (TESTING) {
                /* for testing draw border around tiles */
                graphics.setColor(Color.BLACK);
                graphics.drawLine((int) tileSize.getMinX(), (int) tileSize.getMinY(),
                        (int) tileSize.getMinX(), (int) tileSize.getMaxY());
                graphics.drawLine((int) tileSize.getMinX(), (int) tileSize.getMinY(),
                        (int) tileSize.getMaxX(), (int) tileSize.getMinY());
                graphics.drawLine((int) tileSize.getMaxX(), (int) tileSize.getMinY(),
                        (int) tileSize.getMaxX(), (int) tileSize.getMaxY());
                graphics.drawLine((int) tileSize.getMinX(), (int) tileSize.getMaxY(),
                        (int) tileSize.getMaxX(), (int) tileSize.getMaxY());
                graphics.drawString("pos: " + tile.getPosition(), ((int) tileSize.getMaxX() - 113),
                        ((int) tileSize.getMaxY() - 113));
            }
        } catch (Throwable t) {
            t.printStackTrace();
            WmsPlugin
                    .log("Error Rendering tile. Painting Tile:" + (coverage != null ? coverage.getName() : ""), t); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * Clears the area of the tile on the graphics
     * 
     * @param graphics graphics to draw onto
     * @param style raster symbolizer
     * @throws FactoryException
     * @throws TransformException
     */
    private void renderBlankTile( Graphics2D graphics, Tile tile, CoordinateReferenceSystem crs )
            throws FactoryException, TransformException {

        if (tile == null) {
            return;
        }

        // get the bounds of the tile
        Envelope bnds = tile.getBounds();

        // convert bounds to necessary viewport projection
        if (!crs.equals(getContext().getCRS())) {
            MathTransform transform = CRS.findMathTransform(crs, getContext().getCRS());
            bnds = JTS.transform(bnds, transform);
        }

        // determine screen coordinates of tiles
        Point upperLeft = getContext().worldToPixel(new Coordinate(bnds.getMinX(), bnds.getMinY()));
        Point bottomRight = getContext().worldToPixel(
                new Coordinate(bnds.getMaxX(), bnds.getMaxY()));
        Rectangle tileSize = new Rectangle(upperLeft);
        tileSize.add(bottomRight);

        // render
        try {
            graphics.setBackground(new Color(255, 255, 255, 0)); // set the tile transparent for now
            graphics.clearRect(tileSize.x, tileSize.y, tileSize.width, tileSize.height);

            if (TESTING) {
                /* for testing draw border around tiles */
                graphics.setColor(Color.BLACK);
                graphics.drawLine((int) tileSize.getMinX(), (int) tileSize.getMinY(),
                        (int) tileSize.getMinX(), (int) tileSize.getMaxY());
                graphics.drawLine((int) tileSize.getMinX(), (int) tileSize.getMinY(),
                        (int) tileSize.getMaxX(), (int) tileSize.getMinY());
                graphics.drawLine((int) tileSize.getMaxX(), (int) tileSize.getMinY(),
                        (int) tileSize.getMaxX(), (int) tileSize.getMaxY());
                graphics.drawLine((int) tileSize.getMinX(), (int) tileSize.getMaxY(),
                        (int) tileSize.getMaxX(), (int) tileSize.getMaxY());
            }
        } catch (Throwable t) {
            t.printStackTrace();
            WmsPlugin.log("Error Rendering Blank tile. Painting Tile", t); //$NON-NLS-1$
        }
    }

    @Override
    public void render( IProgressMonitor monitor ) throws RenderException {
        Graphics2D g2 = (Graphics2D) context.getImage().getGraphics();
        render(g2, monitor);
    }

    /**
     * TileListener implementation for rendering new tiles that are ready
     * 
     * @author GDavis
     * @since 1.1.0
     */
    protected class TileListenerImpl implements TileListener {

        public TileListenerImpl() {

        }
        public void notifyTileReady( Tile tile ) {
            // set the area that needs updating
            // setRenderBounds(tile.getBounds());

            // if the rendering is already in a rendering state, queue this tile
            // to draw and tell the renderer more data is ready,
            // otherwise create a new rendering thread (which will check the tiles afresh)
            int currentState = getState();
            if ((currentState == RENDERING || currentState == STARTING)) {
                // queue the tile to draw
                try {
                    tilesToDraw_queue.put(tile);
                    if (testing) {
                        System.out.println("added to queue: " + tile.getId()); //$NON-NLS-1$
                    }
                } catch (InterruptedException e) {
                    WMSPlugin.log("Error while added tile to queue.", e); //$NON-NLS-1$
                }
            } else {
                if (testing) {
                    System.out.println("RENDER_REQUEST: " + tile.getId()); //$NON-NLS-1$
                }
                setState(RENDER_REQUEST); // start a new rendering thread

            }
        }

    }

    @Override
    public void dispose() {
        super.dispose();
    }

}
