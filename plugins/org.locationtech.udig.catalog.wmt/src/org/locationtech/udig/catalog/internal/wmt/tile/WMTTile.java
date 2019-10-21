/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wmt.tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import javax.imageio.ImageIO;

import org.locationtech.udig.catalog.internal.wmt.WMTPlugin;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.WMTSource;
import org.locationtech.udig.catalog.wmsc.server.Tile;
import org.locationtech.udig.catalog.wmsc.server.TileSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.geometry.jts.ReferencedEnvelope;

import org.locationtech.jts.geom.Envelope;

public abstract class WMTTile implements Tile{
    private final static boolean testing = false;  // for testing output
    
    public static final int INERROR = 1;
    public static final int OK = 0;
    
    private WMTTileName tileName;
    private ReferencedEnvelope extent;
    private BufferedImage image; //imageObject of the downloaded/cached tile
    private Object imageLock = new Object();
    private int state;
    
    /**
     * The time this Tile is allowed to be cached before being forced to refresh from the server
     */
    private String maxCacheAge;
    
    public WMTTile(ReferencedEnvelope extent, WMTTileName tileName) {
        this.extent = extent;
        this.tileName = tileName;
    }
    
    public URL getUrl(){
        return tileName.getTileUrl();
    }
    
    public ReferencedEnvelope getExtent() {
        return extent;
    }
    
    public BufferedImage getBufferedImage() {
        return image;
    }
    
    public String getId() {
        return tileName.getId();
    }
    
    public String getReleatedSourceId() {
        return tileName.getSource().getId();
    }
    
    public abstract WMTTile getRightNeighbour();
    public abstract WMTTile getLowerNeighbour();
       
    public Envelope getBounds() {
        return extent;
    }

    public String getPosition() {
        return getId();
    }

    public double getScale() {
        return tileName.getZoomLevel();
    }

    public Object getTileLock() {
        return imageLock;
    }

    public TileSet getTileSet() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.locationtech.udig.catalog.wmsc.server.Tile#getTileState()
     */
    public int getTileState(){
        return this.state;
    }
    
    /* (non-Javadoc)
     * @see org.locationtech.udig.catalog.wmsc.server.Tile#setTileState(int)
     */
    public void setTileState(int state){
        this.state = state;
    }

    public boolean loadTile(IProgressMonitor monitor) {
        if (tileName == null) {
            WMTPlugin.log("error, no tilename", null); //$NON-NLS-1$
            return false;
        }
        
        // get a lock for this tile and only fetch a new image if
        // it does not have one set already when the lock is obtained.
        // TODO:  add support for re-fetching expired images too
        Object lock = getTileLock();
        BufferedImage bufImage = null;
        if (testing) {
            System.out.println("getting lock: "+getId()); //$NON-NLS-1$
        }
        synchronized (lock) {
            if (testing) {
                System.out.println("got lock: "+getId()); //$NON-NLS-1$
            }
            if ((getBufferedImage() != null && getTileState() != WMTTile.INERROR) || monitor.isCanceled()) {
                // tile image already set
                monitor.setCanceled(true);
                if (testing) {
                    System.out.println("REQUEST CANCELLED - REMOVING lock: "+getId()); //$NON-NLS-1$
                }                    
                return true;
            }
           try {
                // simulate latency if testing
                if (testing) {
                    Random rand = new Random(); 
                    long delay = rand.nextInt(5000); // delay 1-5 secs
                    System.out.println("request delaying for: "+delay); //$NON-NLS-1$
                    Thread.sleep(delay);  // simulate latency
                }
                URL url = getUrl();
                WMTPlugin.log("WMT GetTile: "+ url, null);  //$NON-NLS-1$
                
                URLConnection openConnection = url.openConnection();
                if (openConnection!=null) {
                    HttpURLConnection connection = null;
                    connection = (HttpURLConnection) openConnection;  
                    setConnectionParams(connection);
                    
                    bufImage = ImageIO.read(connection.getInputStream());
                    
                    //bufImage = ImageIO.read(url);
                }else{
                    File file = new File(url.toExternalForm()); 
                    if (file.exists()) {
                        bufImage = ImageIO.read(file);
                    }
                }
                if (bufImage != null) {
                    setBufferedImageInternal(bufImage);
                    setTileState(WMTTile.OK);
                }else{
                    // create an error buffered image
                    setBufferedImageInternal(createErrorImage());
                    setTileState(WMTTile.INERROR);
                }
                
            } catch (Exception e1) {
                // create an error buffered image
                setBufferedImageInternal(createErrorImage());
                setTileState(WMTTile.INERROR);
                WMTPlugin.log("error loading tile, placeholder created:", e1); //$NON-NLS-1$
            } catch( Throwable t){
                // create an error buffered image
                setBufferedImageInternal(createErrorImage());
                setTileState(WMTTile.INERROR);
                WMTPlugin.log("error loading tile, placeholder created:", t); //$NON-NLS-1$
            } finally {
                // nothing?
            }
        } // end synchronized block
        
        if (testing) {
            System.out.println("REMOVING lock: "+getId()); //$NON-NLS-1$
        }
        
        // if we successfully set the buffered image to something, return true
        if (getBufferedImage() != null) {
            return true;
        }
        System.out.println("// if we get here, something prevented us from setting an image"); //$NON-NLS-1$
        // if we get here, something prevented us from setting an image
        return false;
    }

    protected void setConnectionParams(HttpURLConnection connection) {}    

    private BufferedImage createErrorImage() {
        BufferedImage bf = new BufferedImage(tileName.getSource().getTileWidth(), tileName.getSource().getTileHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bf.createGraphics();
        g.setColor(Color.RED);
        g.drawLine(0, 0, tileName.getSource().getTileWidth(), tileName.getSource().getTileHeight());
        g.drawLine(0, tileName.getSource().getTileHeight(), tileName.getSource().getTileWidth(), 0);
        return bf;
    }
    
    public void setBufferedImage(BufferedImage im) {
        Object lock = getTileLock();
        synchronized (lock) {       
            setBufferedImageInternal(im);
            if (getBufferedImage() != null) {
                setTileState(WMTTile.OK);
            }
            else {
                setTileState(WMTTile.INERROR);
            }
        }
    }

    /**
     * Set the buffered image without locking.
     */
    private void setBufferedImageInternal(BufferedImage im) {
        this.image = im;
    }
    
    public void setPosition(String pos)  {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxCacheAge( String maxCacheAge ) {
        this.maxCacheAge = maxCacheAge;
    }

    @Override
    public String getMaxCacheAge() {
        return this.maxCacheAge;
    }

    public int compareTo(Tile other) {
        // id contains scale and bounds so compare with that
        return getId().compareTo( other.getId() );
    }
    
    public boolean equals(Object arg0) {
        if (arg0 instanceof Tile) {
            Tile tile = (Tile) arg0;
            // id contains scale and bounds so compare with that
            if (getId().equals(tile.getId())) {
                return true;
            }
            else {
                return false;
            }
        }
        return super.equals(arg0);
    }
    
    /**
     * TileFactory is used inside WMTSource.cutExtentIntoTiles(..) to get
     * the tile which contains a coordinate.
     * 
     * @author to.srwn
     * @since 1.1.0
     */
    public static abstract class WMTTileFactory {
        public abstract WMTTile getTileFromCoordinate(double lat, double lon, 
                WMTZoomLevel zoomLevel, WMTSource wmtSource);
        
        public abstract WMTZoomLevel getZoomLevel(int zoomLevel, WMTSource wmtSource);
        
        /**
         * uDig may produce numbers like -210° for the longitude, but we need
         * a number in the range -180 to 180, so instead of -210 we want 150.
         * 
         * @param value the number to normalize (e.g. -210)
         * @param maxValue the maximum value (e.g. 180 -> the range is: -180..180)
         * @return a number between (-maxvalue) and maxvalue
         */
        public static double normalizeDegreeValue(double value, int maxValue) {
            int range = 2 * maxValue;
            
            if (value > 0) {
                value = (value + maxValue - 1) % range;
                
                if (value < 0) {
                    value += range;
                }
                
                return (value - maxValue + 1);
            } else {
                value = (value + maxValue) % range;
                
                if (value < 0) {
                    value += range;
                }
                
                return (value - maxValue);                
            }
        }
        
        /**
         * This method ensures that value is between min and max. 
         * If value < min, min is returned.
         * If value > max, max is returned.
         * Otherwise value.
         *
         * @param value
         * @param min
         * @param max
         * @return
         */
        public static double moveInRange(double value, double min, double max) {
            if (value < min) {
                value = min;
            } else if(value > max) {
                value = max;
            }
            
            return value;
        }
    }
    
    public static abstract class WMTZoomLevel {
        private int zoomLevel;
        
        private int maxTilePerRowNumber;
        private int maxTilePerColNumber;
        private long maxTileNumber;
        
        public WMTZoomLevel(int zoomLevel) {
            setZoomLevel(zoomLevel);  
        }
        
        public void setZoomLevel(int zoomLevel) {
            this.zoomLevel = zoomLevel;
            
            this.maxTilePerRowNumber = calculateMaxTilePerRowNumber(zoomLevel);
            this.maxTilePerColNumber = calculateMaxTilePerColNumber(zoomLevel);
            
            this.maxTileNumber = calculateMaxTileNumber();
        }
        
        public abstract int calculateMaxTilePerRowNumber(int zoomLevel);
        public abstract int calculateMaxTilePerColNumber(int zoomLevel);
        
        public long calculateMaxTileNumber() {
            return ((long) (maxTilePerColNumber)) * ((long) (maxTilePerRowNumber));
        }
        
        public int getZoomLevel() {
            return zoomLevel;
        }
        
        public int getMaxTilePerRowNumber() {
            return maxTilePerRowNumber;
        }
        
        public int getMaxTilePerColNumber() {
            return maxTilePerColNumber;
        }
        
        public long getMaxTileNumber() {
            return maxTileNumber;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof WMTZoomLevel)) return false;
            
            WMTZoomLevel other = (WMTZoomLevel) obj;
            
            return zoomLevel == other.zoomLevel;
        }
    }
}
