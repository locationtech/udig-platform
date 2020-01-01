/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.wmsc.server;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Random;

import javax.imageio.ImageIO;

import org.locationtech.udig.catalog.internal.wms.WmsPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.ows.AbstractOpenWebService;
import org.geotools.data.ows.AbstractRequest;
import org.geotools.data.ows.HTTPClient;
import org.geotools.data.ows.HTTPResponse;
import org.geotools.data.ows.Response;
import org.geotools.ows.ServiceException;

import org.locationtech.jts.geom.Envelope;

/**
 * Represents one tile in a tileset from a WMS Server. Has a unique id, bounds, scale and a
 * BufferedImage. All other data such as the format, crs, etc are stored as part of the tileset.
 * 
 * @author GDavis
 * @since 1.2.0
 */
public class WMSTile implements Tile {

    private final static boolean testing = false; // for testing output

    public static final int INERROR = 1;
    public static final int OK = 0;

    private String id; // id = "<scale>_<minx>_<maxy>_<maxy>_<miny>"
    private String position; // pos = x_y tile position within tilerange grid for this scale
    private static String ID_DIVIDER = "_"; //$NON-NLS-1$
    private WMSTileSet tileset;
    private AbstractOpenWebService< ? , ? > server;
    private BufferedImage image;
    private Object imageLock = new Object();
    private Envelope bounds;
    private double scale;

    /**
     * The state of the tile; if something happens while fetching the image and the image isn't
     * returned properly we set the state to error. If another request is made for this tile we'll
     * try re-requesting the tile.
     */
    private int state = OK;

    /**
     * The time this Tile is allowed to be cached before being forced to refresh from the server
     */
    private String maxCacheAge;

    public WMSTile( AbstractOpenWebService< ? , ? > server, WMSTileSet tileset, Envelope bounds,
            double scale ) {
        this.server = server;
        this.tileset = tileset;
        this.bounds = bounds;
        this.scale = scale;
        this.id = buildId(bounds, scale);
    }

    /**
     * Builds a tile id in the form of: id = "<scale>_<minx>_<maxy>_<maxy>_<miny>"
     * 
     * @param bbox
     * @param scale
     * @return tile id string
     */
    public static String buildId( Envelope bbox, double scale ) {
        if (bbox == null) {
            return null;
        }
        return scale + ID_DIVIDER + bbox.getMinX() + ID_DIVIDER + bbox.getMaxY() + ID_DIVIDER
                + bbox.getMaxY() + ID_DIVIDER + bbox.getMinY();
    }

    /*
     * (non-Javadoc)
     * @see org.locationtech.udig.catalog.wmsc.server.Tile#getId()
     */
    public String getId() {
        if (id == null) {
            id = buildId(bounds, scale);
        }
        return id;
    }

    public TileSet getTileSet() {
        return tileset;
    }

    /*
     * (non-Javadoc)
     * @see org.locationtech.udig.catalog.wmsc.server.Tile#getBounds()
     */
    public Envelope getBounds() {
        return bounds;
    }

    /*
     * (non-Javadoc)
     * @see org.locationtech.udig.catalog.wmsc.server.Tile#getScale()
     */
    public double getScale() {
        return scale;
    }

    /**
     * Set the buffered image while locking and set the state of the tile.
     * 
     * @see org.locationtech.udig.catalog.wmsc.server.Tile#setBufferedImage(java.awt.image.BufferedImage)
     */
    public void setBufferedImage( BufferedImage im ) {
        Object lock = getTileLock();
        synchronized (lock) {
            setBufferedImageInternal(im);
            if (getBufferedImage() != null) {
                setTileState(WMSTile.OK);
            } else {
                setTileState(WMSTile.INERROR);
            }
        }
    }

    /**
     * See the buffered image without locking.
     */
    private void setBufferedImageInternal( BufferedImage im ) {
        this.image = im;
    }

    /*
     * (non-Javadoc)
     * @see org.locationtech.udig.catalog.wmsc.server.Tile#getBufferedImage()
     */
    public BufferedImage getBufferedImage() {
        return image;
    }

    public Object getTileLock() {
        return imageLock;
    }

    public int compareTo( Tile other ) {
        // id contains scale and bounds so compare with that
        return getId().compareTo(other.getId());
    }

    public boolean equals( Object arg0 ) {
        if (arg0 instanceof Tile) {
            Tile tile = (Tile) arg0;
            // id contains scale and bounds so compare with that
            if (getId().equals(tile.getId())) {
                return true;
            } else {
                return false;
            }
        }
        return super.equals(arg0);
    }

    /*
     * (non-Javadoc)
     * @see org.locationtech.udig.catalog.wmsc.server.Tile#getTileState()
     */
    public int getTileState() {
        return this.state;
    }

    /*
     * (non-Javadoc)
     * @see org.locationtech.udig.catalog.wmsc.server.Tile#setTileState(int)
     */
    public void setTileState( int state ) {
        this.state = state;
    }

    /*
     * (non-Javadoc)
     * @see org.locationtech.udig.catalog.wmsc.server.Tile#setPosition(java.lang.String)
     */
    public void setPosition( String pos ) {
        this.position = pos;
    }

    /*
     * (non-Javadoc)
     * @see org.locationtech.udig.catalog.wmsc.server.Tile#getPosition()
     */
    public String getPosition() {
        return this.position;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.locationtech.udig.catalog.wmsc.server.Tile#loadTile(org.eclipse.core.runtime.IProgressMonitor
     * )
     */
    public boolean loadTile( IProgressMonitor monitor ) {
        // build request
        if (server == null && tileset != null) {
            // try getting server from the tileset
            setServer(tileset.getServer());
        }
        if (server == null || tileset == null) {
            WmsPlugin.log("error, tile not setup with a server and/or tileset", null); //$NON-NLS-1$
            createErrorImage();
            return false;
        }
        // String baseUrl = server.buildBaseTileRequestURL();
        Envelope env = getBounds();
        URL req = null;
        try {
            URI rawURI = server.getInfo().getSource();
            String baseUrl = rawURI.getScheme()
                    + "://" + rawURI.getHost() + ":" + rawURI.getPort() + "" + rawURI.getPath() + "?"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            req = new URL(
                    baseUrl
                            + "&version=" + server.getCapabilities().getVersion() + "&" + tileset.createQueryString(env)); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (MalformedURLException e2) {
            WmsPlugin.log("error building request URL:", e2); //$NON-NLS-1$
            return false;
        }
        GetMapRequest request = new GetMapRequest(req);

        // get a lock for this tile and only fetch a new image if
        // it does not have one set already when the lock is obtained.
        // TODO: add support for re-fetching expired images too
        Object lock = getTileLock();
        BufferedImage bufImage = null;
        if (testing) {
            System.out.println("getting lock: " + getId()); //$NON-NLS-1$
        }
        synchronized (lock) {
            if (testing) {
                System.out.println("got lock: " + getId()); //$NON-NLS-1$
            }
            if ((getBufferedImage() != null && getTileState() != WMSTile.INERROR)
                    || monitor.isCanceled()) {
                // tile image already set
                monitor.setCanceled(true);
                if (testing) {
                    System.out.println("REQUEST CANCELLED - REMOVING lock: " + getId()); //$NON-NLS-1$
                }
                return true;
            }
            InputStream inputStream = null;
            try {
                HTTPResponse response = issueRequest(request);
                String cacheControl = extractHeaderCacheControl(response);
                setMaxCacheAge(cacheControl);
                inputStream = response.getResponseStream();
                // simulate latency if testing
                if (testing) {
                    Random rand = new Random();
                    long delay = rand.nextInt(5000); // delay 1-5 secs
                    System.out.println("request delaying for: " + delay); //$NON-NLS-1$
                    Thread.sleep(delay); // simulate latency
                }
                WmsPlugin.log("WMSC GetMap: " + req.toString(), null); //$NON-NLS-1$
                bufImage = ImageIO.read(inputStream);
                if (bufImage != null) {
                    setBufferedImageInternal(bufImage);
                    setTileState(WMSTile.OK);
                } else {
                    // create an error buffered image
                    setBufferedImageInternal(createErrorImage());
                    setTileState(WMSTile.INERROR);
                }
            } catch (Exception e1) {
                // create an error buffered image
                setBufferedImageInternal(createErrorImage());
                setTileState(WMSTile.INERROR);
                WmsPlugin.log("error loading tile, placeholder created:", e1); //$NON-NLS-1$
            } catch (Throwable t) {
                // create an error buffered image
                setBufferedImageInternal(createErrorImage());
                setTileState(WMSTile.INERROR);
                WmsPlugin.log("error loading tile, placeholder created:", t); //$NON-NLS-1$
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        WmsPlugin.log("failed to close input stream!!!", e); //$NON-NLS-1$
                    }
                }
            }
        } // end synchronized block

        if (testing) {
            System.out.println("REMOVING lock: " + getId()); //$NON-NLS-1$
        }

        // if we successfully set the buffered image to something, return true
        if (getBufferedImage() != null) {
            return true;
        }

        // if we get here, something prevented us from setting an image
        return false;
    }

    /**
     * Extract the cache-control header from the servers response object in the form: 'max-age=3600,
     * must-revalidate'
     * 
     * @param response
     * @return
     */
    @SuppressWarnings("nls")
    private String extractHeaderCacheControl( HTTPResponse response ) {
        String cacheControl = response.getResponseHeader("Cache-Control");
        if (cacheControl != null && !"".equals(cacheControl)) {
            String[] split = cacheControl.split(",");
            String maxAge = split[0];
            if (maxAge != null && !"".equals(maxAge)) {
                return maxAge.split("=")[1];
            }
        }
        return null;
    }

    public HTTPResponse issueRequest( GetMapRequest request ) throws IOException, ServiceException {
        URL finalURL = request.getFinalURL();
        if (finalURL.getHost() == null) {
            // System.out.prinln("Poor WMS-C configuration - no host provided by "+ finalURL );
            throw new NullPointerException("No host provided by " + finalURL); //$NON-NLS-1$
        }

        final HTTPClient httpClient = server.getHTTPClient();
        final HTTPResponse httpResponse;

        if (request.requiresPost()) {

            final String postContentType = request.getPostContentType();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            request.performPostOutput(out);
            InputStream in = new ByteArrayInputStream(out.toByteArray());

            try {
                httpResponse = httpClient.post(finalURL, in, postContentType);
            } finally {
                in.close();
            }
        } else {
            httpResponse = httpClient.get(finalURL);
        }

        return httpResponse;
    }

    private BufferedImage createErrorImage() {
        BufferedImage bf = new BufferedImage(tileset.getWidth(), tileset.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bf.createGraphics();
        g.setColor(Color.RED);
        g.drawLine(0, 0, tileset.getWidth(), tileset.getHeight());
        g.drawLine(0, tileset.getHeight(), tileset.getWidth(), 0);
        return bf;
    }

    public AbstractOpenWebService< ? , ? > getServer() {
        return server;
    }

    public void setServer( AbstractOpenWebService< ? , ? > server ) {
        this.server = server;
    }

    @Override
    public void setMaxCacheAge( String maxCacheAge ) {
        this.maxCacheAge = maxCacheAge;
    }

    @Override
    public String getMaxCacheAge() {
        return this.maxCacheAge;
    }

    /**
     * Class for sending map requests
     * 
     * @author GDavis
     * @since 1.1.0
     */
    static class GetMapRequest extends AbstractRequest {
        private URL url;

        public GetMapRequest( URL url ) {
            super(url, null);
            this.url = url;
        }
        public Response createResponse( HTTPResponse response ) throws ServiceException,
                IOException {
            return new Response(response){
            };
        }

        public URL getFinalURL() {
            return this.url;
        }

        @Override
        protected void initRequest() {
        }
        @Override
        protected void initService() {
        }
        @Override
        protected void initVersion() {
        }
    }
}
