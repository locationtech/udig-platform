package net.refractions.udig.catalog.wmsc;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import net.refractions.udig.catalog.wms.internal.Messages;
import net.refractions.udig.catalog.wmsc.server.Tile;
import net.refractions.udig.catalog.wmsc.server.TileListener;
import net.refractions.udig.catalog.wmsc.server.TileRangeOnDisk;
import net.refractions.udig.catalog.wmsc.server.TileSet;
import net.refractions.udig.catalog.wmsc.server.TileWorkerQueue;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.geotools.geometry.jts.JTS;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * A collection of utility methods for managing WMS-C Tiles
 * 
 * @author GDavis
 * @version 1.2.0
 */
public class WMSCTileUtils {

    /**
     * Given a TileSet, use it's bounds to request every tile in it and store it on disk. This is
     * run in a blocking dialog since the thousands of continuous requests otherwise bog down udig.
     * It can be canceled and does provide progress feedback.
     * 
     * @param tileset
     */
    public static void preloadAllTilesOnDisk( TileSet tileset ) {
        preloadAllTilesOnDisk(tileset, null);
    }

    /**
     * Given a List TileSet, use it's bounds to request every tile in it and store it on disk. This
     * is run in a blocking dialog since the thousands of continuous requests otherwise bog down
     * udig. It can be canceled and does provide progress feedback.
     * 
     * @param tileset The tileset to download
     * @param select The area to select (should be the the same crs as your tileset!
     */
    public static void preloadAllTilesOnDisk( TileSet tileset, Geometry select ) {
        final IRunnableWithProgress preloadTiles = new PreloadTilesClass(tileset, select);
        String taskname = Messages.WMSCTileUtils_preloadtask;
        PlatformGIS.runInProgressDialog(taskname, false, preloadTiles, true);
    }

    /**
     * Download the tileset; either the entire tileset or tiles that intersect a provided geometry.
     * 
     * @author gdavis
     * @since 1.2.0
     */
    private static class PreloadTilesClass implements IRunnableWithProgress {
        /**
         * The TileSet we want to download; you can use null to indicate the entire tileset; or
         * provided a Geometry that can be used to select out only specific tiles.
         */

        /** The tileset we are downloading */
        TileSet tileset;

        /** Area of the tileset to download, null to download the entire tileset */
        Geometry select;

        private int requestCount;
        private double percentPerTile;
        private IProgressMonitor monitor;
        private Envelope tileRangeBounds;
        private Map<String, Tile> tileRangeTiles;
        private TileWorkerQueue requestTileWorkQueue;
        private TileWorkerQueue writeTileWorkQueue;

        /**
         * The maximum number of tile requests to send to a server at once before waiting to send
         * the next group of requests off (used for preloading all tiles).
         */
        private static int default_maxTileRequestsPerGroup = 16;
        private int maxTileRequestsPerGroup = default_maxTileRequestsPerGroup;

        /**
         * Use a blocking queue to keep track of and notice when tiles done so we can wait for
         * chunks to complete without creating too many requests all at once
         */
        private BlockingQueue<Tile> tilesCompleted_queue = new PriorityBlockingQueue<Tile>();
        private TileListenerImpl listener = new TileListenerImpl();

        public PreloadTilesClass( final TileSet tileset, final Geometry select ) {
            this.tileset = tileset;
            this.select = select;
        }
        public void run( IProgressMonitor monitor ) throws InvocationTargetException,
                InterruptedException {
            this.monitor = monitor;

            String taskname = MessageFormat.format(Messages.WMSCTileUtils_preloadtask, tileset
                    .getLayers());
            this.monitor.beginTask(taskname, 100);

            // for each zoom level of tiles offered by the server, loop through
            // and request groups of tiles at a time so we don't overload the
            // server with too many requests at once.
            double[] resolutions = tileset.getResolutions();
            double percentPerResolution = (double) resolutions.length / 100.0;
            tileRangeTiles = new HashMap<String, Tile>();
            requestTileWorkQueue = new TileWorkerQueue();
            writeTileWorkQueue = new TileWorkerQueue();
            maxTileRequestsPerGroup = requestTileWorkQueue.getThreadPoolSize();
            int resCount = 0;

            try {
                for( double resolution : resolutions ) {
                    resCount++;
                    // cut up the bounds of the whole resolution into smaller pieces so that we
                    // don't get a hashmap of tiles that is huge (eg: 100K+) and run out of
                    // memory.
                    List<Envelope> boundsList;
                    long totalTilesForZoom;
                    // a performance boost, if we take the entire bounds of the tileset we get 
                    // a lot of redundant checking and a slow system so simple create a 
                    // approximate bounds around our selected Geometry so we are only ever 
                    // getting tiles within a close proximity to our region of interest
                    if (select != null) {
                        boundsList = tileset.getBoundsListForZoom(JTS.toEnvelope(select), resolution);
                        totalTilesForZoom = tileset.getTileCount(JTS.toEnvelope(select), resolution);
                    } else {
                        boundsList = tileset.getBoundsListForZoom(tileset.getBounds(),resolution);
                        totalTilesForZoom = tileset.getTileCount(tileset.getBounds(),resolution);
                    }

                    Iterator<Envelope> boundsIter = boundsList.iterator();

                    while( boundsIter.hasNext() ) {
                        if (monitor.isCanceled()) {
                            cleanup();
                            return;
                        }
                        Envelope env = boundsIter.next();

                        Map<String, Tile> allTilesInZoom = tileset
                                .getTilesFromZoom(env, resolution);
                        Map<String, Tile> tilesInZoom = new HashMap<String, Tile>();

                        if (select != null) {

                            for( String key : allTilesInZoom.keySet() ) {
                                final Tile tile = allTilesInZoom.get(key);

                                if (select.intersects(JTS.toGeometry(tile.getBounds()))) {
                                    tilesInZoom.put(key, tile);
                                }
                            }
                        } else {
                            tilesInZoom.putAll(allTilesInZoom);
                        }

                        if (!tilesInZoom.isEmpty()) {
                            // set the percent value for tiles in this resolution
                            percentPerTile = percentPerResolution / totalTilesForZoom;
                            String subname = MessageFormat.format(
                                    Messages.WMSCTileUtils_preloadtasksub, totalTilesForZoom,
                                    resCount, resolutions.length, tileset.getLayers());
                            this.monitor.setTaskName(subname);
                            Iterator<Entry<String, Tile>> iterator = tilesInZoom.entrySet()
                                    .iterator();

                            requestCount = 0;
                            tileRangeBounds = new Envelope();
                            tileRangeTiles.clear();
                            while( iterator.hasNext() ) {
                                if (monitor.isCanceled()) {
                                    cleanup();
                                    return;
                                }
                                requestCount++;
                                Entry<String, Tile> next = iterator.next();
                                tileRangeBounds.expandToInclude(next.getValue().getBounds());
                                tileRangeTiles.put(next.getKey(), next.getValue());

                                // if we have maxTileRequestsPerGroup ready to go tiles, send them
                                // off
                                if (requestCount >= maxTileRequestsPerGroup) {
                                    doRequestAndResetVars(tileset);
                                }
                            }
                        }

                        // if the requests is not reset to 0 then there are remaining tiles to
                        // fetch
                        if (requestCount != 0) {
                            doRequestAndResetVars(tileset);
                        }

                    } // end bounds iteration

                    // if the percent per tile is 0, then update the monitor now with the
                    // value for a complete resolution
                    if ((int) percentPerTile < 1) {
                        this.monitor.worked((int) percentPerResolution);
                    }
                    if ((int) percentPerResolution < 1) {
                        this.monitor.worked(1);
                    }

                } // end resolutions loop

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cleanup(); // cleanup the thread pool on exit
            }

            return;
        }

        /**
         * Load the tiles in the tilerange and then reset the vars
         */
        private void doRequestAndResetVars( final TileSet tileset ) {
            TileRangeOnDisk tileRangeOnDisk = new TileRangeOnDisk(tileset.getServer(), tileset,
                    tileRangeBounds, tileRangeTiles, requestTileWorkQueue, writeTileWorkQueue);
            // set the listener on the tile range so we can wait until all tiles are
            // done for the range before moving on.
            tileRangeOnDisk.addListener(listener);

            // remove any tiles that are already loaded from disk to avoid
            // deadlock waiting for all tiles
            Map<String, Tile> loadedTiles = new HashMap<String, Tile>();
            Iterator<Entry<String, Tile>> iterator = tileRangeTiles.entrySet().iterator();
            while( iterator.hasNext() ) {
                Tile tile = iterator.next().getValue();
                if (tile.getBufferedImage() != null) {
                    loadedTiles.put(tile.getId(), tile);
                }
            }
            Iterator<Entry<String, Tile>> iterator2 = loadedTiles.entrySet().iterator();
            while( iterator2.hasNext() ) {
                Tile tile = iterator2.next().getValue();
                tileRangeTiles.remove(tile.getId());
            }

            // now load any missing tiles and send off thread requests to fetch them
            tileRangeOnDisk.loadTiles(new NullProgressMonitor());

            // block and wait until all unloaded tiles are loaded before moving forward
            while( !tileRangeTiles.isEmpty() ) {
                Tile tile = null;
                try {
                    tile = (Tile) tilesCompleted_queue.take(); // blocks until a tile is done
                } catch (InterruptedException ex) {
                    // log error?
                    // ex.printStackTrace();
                } finally {
                    // remove the tile
                    if (tile != null) {
                        tileRangeTiles.remove(tile.getId());
                    }
                }
            }

            // all tiles in chunk are now complete, so update monitor
            this.monitor.worked((int) percentPerTile * tileRangeOnDisk.getTileCount());

            // reset vars
            requestCount = 0;
            tileRangeBounds = new Envelope();
            tileRangeTiles.clear();
        }

        /**
         * Task is complete or canceled, so cleanup the threads and other objects
         */
        private void cleanup() {
            requestTileWorkQueue.dispose();
            writeTileWorkQueue.dispose();
            requestTileWorkQueue = null;
            writeTileWorkQueue = null;
            this.monitor.done();
        }

        /**
         * TileListener implementation for listening when tiles are done
         * 
         * @author GDavis
         * @since 1.1.0
         */
        private class TileListenerImpl implements TileListener {

            public TileListenerImpl() {

            }
            public void notifyTileReady( Tile tile ) {
                // queue the tile as done
                try {
                    tilesCompleted_queue.put(tile);
                } catch (InterruptedException e) {
                    // log error?
                    // e.printStackTrace();
                }
            }

        };

    };

}
