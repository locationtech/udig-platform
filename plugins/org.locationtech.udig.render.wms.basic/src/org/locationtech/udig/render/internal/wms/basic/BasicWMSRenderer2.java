/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.render.internal.wms.basic;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;
import javax.naming.OperationNotSupportedException;

import org.locationtech.udig.catalog.util.CRSUtil;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ProjectBlackboardConstants;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.internal.render.impl.RendererImpl;
import org.locationtech.udig.project.render.ICompositeRenderContext;
import org.locationtech.udig.project.render.IMultiLayerRenderer;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.RenderException;
import org.locationtech.udig.render.wms.basic.WMSPlugin;
import org.locationtech.udig.render.wms.basic.internal.Messages;
import org.locationtech.udig.render.wms.basic.preferences.PreferenceConstants;
import org.locationtech.udig.style.wms.WMSStyleContent;
import org.locationtech.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.data.Query;
import org.geotools.ows.wms.Layer;
import org.geotools.data.ows.Service;
import org.geotools.ows.wms.StyleImpl;
import org.geotools.ows.wms.WMSCapabilities;
import org.geotools.ows.wms.WebMapServer;
import org.geotools.ows.wms.request.GetMapRequest;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.gml2.SrsSyntax;
import org.geotools.ows.ServiceException;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.lite.RendererUtilities;
import org.geotools.renderer.lite.gridcoverage2d.GridCoverageRenderer;
import org.geotools.image.util.ImageUtilities;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.xml.DocumentWriter;
import org.geotools.xml.filter.FilterSchema;
import org.opengis.filter.Filter;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

/**
 * The basic renderer for a WMS Layer
 * <p>
 * </p>
 */
public class BasicWMSRenderer2 extends RendererImpl implements IMultiLayerRenderer {

    private static final String REFRESH_JOB = Messages.BasicWMSRenderer2_refreshJob_title;
    private static final String EPSG_4326 = "EPSG:4326"; //$NON-NLS-1$
    private static final String CRS_84 = "CRS:84"; //$NON-NLS-1$
    private static final String EPSG_4269 = "EPSG:4269"; //$NON-NLS-1$
    private static final ReferencedEnvelope NILL_BOX = new ReferencedEnvelope(0, 0, 0, 0,
            DefaultGeographicCRS.WGS84);
    private static final String EPSG_CODE = "CRS_EPSG_CODE";

    /**
     * Construct a new BasicWMSRenderer
     */
    public BasicWMSRenderer2() {
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(WebMapServer.class.getClassLoader());
            Logger logger = Logger.getLogger("org.geotools.data.ows");//$NON-NLS-1$
            if (WMSPlugin.isDebugging(Trace.RENDER)) {
                logger.setLevel(Level.FINE);
                logger.addHandler(new Handler(){

                    @Override
                    public void publish( LogRecord record ) {
                        System.err.println(record.getMessage());
                    }

                    @Override
                    public void flush() {
                        System.err.flush();
                    }

                    @Override
                    public void close() throws SecurityException {

                    }

                });
            } else {
                logger.setLevel(Level.SEVERE);
            }
        } finally {
            Thread.currentThread().setContextClassLoader(current);
        }        
    }

    @Override
    public void render( Graphics2D destination, IProgressMonitor monitor ) throws RenderException {
        render(destination, getContext().getImageBounds(), monitor);
    }

    @Override
    public void render( IProgressMonitor monitor ) throws RenderException {
        Graphics2D graphics = (Graphics2D) getContext().getImage().getGraphics();
        render(graphics, getRenderBounds(), monitor);
    }

    public synchronized void render( Graphics2D destination, ReferencedEnvelope bounds,
            IProgressMonitor monitor ) throws RenderException {

        
        int endLayerStatus = ILayer.DONE;
        try {
            if (bounds == null || bounds.isNull()) {
                bounds = getContext().getImageBounds();
            }
            
            if (monitor.isCanceled())
                return;

            getContext().setStatus(ILayer.WAIT);

            WebMapServer wms = getWMS();
            
            GetMapRequest request = wms.createGetMapRequest();
            
            // put in default exception format we understand as a client
            // (if suppoted by the server)
            WMSCapabilities capabilities = wms.getCapabilities();
            if( capabilities.getRequest().getGetMap().getFormats().contains(GetMapRequest.EXCEPTION_XML) ){
                request.setExceptions(GetMapRequest.EXCEPTION_XML);
            }
            setImageFormat(wms, request);

            if (monitor.isCanceled())
                return;

            double currScale = getContext().getViewportModel().getScaleDenominator();
            List<ILayer> layers = getLayers();
            int countAddedLayers = 0;
            for( int i = layers.size() - 1; i >= 0; i-- ) {
                ILayer ilayer = layers.get(i);
                Layer layer;
                double minScale = 0;
                double maxScale = Double.MAX_VALUE;
                layer = ilayer.getResource(org.geotools.ows.wms.Layer.class, null);
                // check if there are min/max scale rules
                StyleBlackboard sb = (StyleBlackboard) ilayer.getStyleBlackboard();
                Style style = (Style) sb.lookup(Style.class);
                if (style != null) {
                    Rule rule = style.featureTypeStyles().get(0).rules().get(0);
                    minScale = rule.getMinScaleDenominator();
                    maxScale = rule.getMaxScaleDenominator();
                }

                if (currScale >= minScale && currScale <= maxScale) {
                    // check for a wms style
                    StyleImpl wmsStyle = (StyleImpl) ilayer.getStyleBlackboard().get(
                            WMSStyleContent.WMSSTYLE);
                    if (wmsStyle != null) {
                        request.addLayer(layer, wmsStyle);
                    } else {
                        request.addLayer(layer);
                    }
                    countAddedLayers++;
                } else {
                    //skip layer addition
                    continue;
                }
            }

            if (monitor.isCanceled())
                return;

            if (countAddedLayers == 0) {
                return;
            }

            List<Layer> wmsLayers = getWMSLayers();
            if (wmsLayers == null || wmsLayers.isEmpty()){
                endLayerStatus = ILayer.WARNING;
                return;
            }
            
            // figure out request CRS
            CoordinateReferenceSystem viewportCRS = getViewportCRS();
            IMap map = getContext().getMap();
            
            String requestCRScode = findRequestCRS(wmsLayers, viewportCRS, map);
            
            // TODO: make findRequestCRS more efficient (we are running CRS.decode at *least* twice)
            CoordinateReferenceSystem requestCRS = CRS.decode(requestCRScode);

            // figure out viewport
//            ReferencedEnvelope viewport;
//            Envelope viewportBBox = getViewportBBox();
//            CoordinateReferenceSystem viewportCRS = getViewportCRS();
//            if (viewportBBox == null) {
//                // change viewport to world
//                viewportBBox = new Envelope(-180, 180, -90, 90);
//                if (!DefaultGeographicCRS.WGS84.equals(viewportCRS)) { // reproject
//                    viewport = new ReferencedEnvelope(viewportBBox, DefaultGeographicCRS.WGS84);
//                    viewportBBox = viewport.transform(viewportCRS, true);
//                }
//            }


            ReferencedEnvelope requestBBox = null;
            Envelope backprojectedBBox = null; // request bbox projected to the viewport crs
//            viewport = new ReferencedEnvelope(viewportBBox, viewportCRS);
//            requestBBox = calculateRequestBBox(wmsLayers, viewport, requestCRS);
            
            requestBBox = calculateRequestBBox(wmsLayers, bounds, requestCRS, capabilities.getVersion() );

            // check that a request is needed (not out of a bounds, invalid, etc)
            if (requestBBox == NILL_BOX) {
                endLayerStatus = ILayer.WARNING;
                return;
            }
            assert requestBBox.getCoordinateReferenceSystem().equals(requestCRS);

            if (requestBBox.getCoordinateReferenceSystem().equals(viewportCRS)) {
                backprojectedBBox = (Envelope) requestBBox;
            } else {
                backprojectedBBox = (Envelope) requestBBox.transform(viewportCRS, true);
            }

            if (WMSPlugin.isDebugging(Trace.RENDER)) {
                WMSPlugin.trace("Viewport CRS: " + viewportCRS.getName()); //$NON-NLS-1$
                WMSPlugin.trace("Request CRS: " + requestCRS.getName()); //$NON-NLS-1$
                WMSPlugin.trace("Context Image bounds: " + getContext().getImageBounds()); //$NON-NLS-1$
                WMSPlugin.trace("Request BBox  bounds: " + requestBBox); //$NON-NLS-1$
                WMSPlugin.trace("Backprojected request bounds: " + backprojectedBBox); //$NON-NLS-1$
            }

            Service wmsService = capabilities.getService();
            Dimension maxDimensions = new Dimension(wmsService.getMaxWidth(), wmsService
                    .getMaxHeight());
//            Dimension imageDimensions = calculateImageDimensions(getContext().getMapDisplay()
//                    .getDisplaySize(), maxDimensions, getViewportBBox(), backprojectedBBox);
            Dimension imageDimensions = calculateImageDimensions(getContext().getImageSize(), maxDimensions, bounds, backprojectedBBox);
            if (imageDimensions.height < 1 || imageDimensions.width < 1) {
                endLayerStatus = ILayer.WARNING;
                return;
            }
            request.setDimensions(imageDimensions.width + "", imageDimensions.height + ""); //$NON-NLS-1$ //$NON-NLS-2$
            // epsg could be under identifiers or authority.
            Set<ReferenceIdentifier> identifiers = requestCRS.getIdentifiers();
            String srs = identifiers.isEmpty() ? EPSG_4326 : identifiers.iterator().next().toString();            
            request.setSRS(srs); // EPSG_4326
            request.setBBox( requestBBox );
            // request.setBBox(requestBBox.getMinX() + "," + requestBBox.getMinY()+ "," + requestBBox.getMaxX()+ "," + requestBBox.getMaxY());
            
            if (monitor.isCanceled())
                return;

            setFilter(wms, request);

            // request.setProperty("DACS_ACS", null);
            BufferedImage image = readImage(wms, request, monitor);

            if (monitor.isCanceled())
                return;

            if (image == null) {
                Exception e = new RuntimeException(
                        Messages.BasicWMSRenderer2_unable_to_decode_image);
                throw wrapException(e);
            }

            // backprojectedBBox or viewportBBox
            renderGridCoverage(destination, backprojectedBBox, imageDimensions, requestBBox, image);

        } catch (Exception e) {
        	if( e instanceof RenderException )
        		throw (RenderException) e;
            throw new RenderException(e);
        } finally {
            getContext().setStatus(endLayerStatus);
            if (endLayerStatus == ILayer.DONE) {
                // clear the status message (rendering was successful)
                getContext().setStatusMessage(null);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void setFilter( WebMapServer wms, GetMapRequest request ) {
        Filter mapFilter = null;

        Map<ILayer, Filter> filters = new HashMap<ILayer, Filter>();
        List<ILayer> layers = getContext().getLayers();
        for( ILayer layer : layers ) {
            Object layerFilter = layer.getStyleBlackboard().get(
                    ProjectBlackboardConstants.LAYER__DATA_QUERY);
            Filter filter;
            if (layerFilter instanceof Query) {
                filter = (Filter) ((Query) layerFilter).getFilter();
            } else if (layerFilter instanceof Filter) {
                filter = (Filter) layerFilter;
            } else {
                filter = mapFilter;
            }
            if (filter != null && filter != Filter.INCLUDE){
                filters.put(layer, filter);
            }
        }

        if (filters.isEmpty())
            return;

        StringBuilder builder = new StringBuilder();
        HashMap hashMap = new HashMap();
        for( Map.Entry<ILayer, Filter> entry : filters.entrySet() ) {
            if (entry.getValue() == null)
                builder.append('(');
            try {
                StringWriter writer = new StringWriter();
                DocumentWriter.writeDocument(entry.getValue(), FilterSchema.getInstance(), writer,
                        hashMap);
                builder.append(writer.toString());
            } catch (OperationNotSupportedException e) {
                WMSPlugin.log("Error writing filter for layer: " + entry.getKey().getID(), e); //$NON-NLS-1$
                builder.append("<Filter/>"); //$NON-NLS-1$
            } catch (IOException e) {
                // SHOULDN'T Happen I don't think...
                assert false;
                WMSPlugin.log("Error writing filter for layer: " + entry.getKey().getID(), e); //$NON-NLS-1$
                builder.append("<Filter/>"); //$NON-NLS-1$
            }
            builder.append(')');
        }

        try {
            String encode = URLEncoder.encode(builder.toString(), "UTF-8"); //$NON-NLS-1$
            request.setVendorSpecificParameter("filter", encode); //$NON-NLS-1$
        } catch (UnsupportedEncodingException e) {
            // better not happen!
            throw (RuntimeException) new RuntimeException().initCause(e);
        }
    }

    private void renderGridCoverage( Graphics2D graphics, Envelope bounds, Dimension dimension,
            ReferencedEnvelope requestBBox, BufferedImage image ) throws Exception {
        CoordinateReferenceSystem destinationCRS = getContext().getCRS();

        Envelope envelope = bounds;
        if (envelope == null || envelope.isNull()) {
            //get the bounds from the context
            envelope = getContext().getImageBounds();
        }
        Point upperLeft = getContext().worldToPixel(
                new Coordinate(envelope.getMinX(), envelope.getMinY()));
        Point bottomRight = getContext().worldToPixel(
                new Coordinate(envelope.getMaxX(), envelope.getMaxY()));
        Rectangle screenSize = new Rectangle(upperLeft);
        screenSize.add(bottomRight);

        GridCoverage2D coverage = convertImageToGridCoverage(requestBBox, image);

        AffineTransform worldToScreen = RendererUtilities.worldToScreenTransform(envelope, screenSize, destinationCRS);
		GridCoverageRenderer paint = new GridCoverageRenderer( destinationCRS, envelope, screenSize, worldToScreen  );
                     
        RasterSymbolizer symbolizer = CommonFactoryFinder.getStyleFactory(null).createRasterSymbolizer();
                        
        paint.paint( graphics, coverage, symbolizer );  

    }
    private GridCoverage2D convertImageToGridCoverage( ReferencedEnvelope requestBBox,
            BufferedImage image ) throws RenderException {
        Envelope env = requestBBox;
        GeneralEnvelope gtEnv = new GeneralEnvelope(new double[]{env.getMinX(), env.getMinY()},
                new double[]{env.getMaxX(), env.getMaxY()});

        try {
            gtEnv.setCoordinateReferenceSystem(requestBBox.getCoordinateReferenceSystem());
        } catch (Exception e) {
            throw wrapException(e);
        }

        GridCoverageFactory factory = new GridCoverageFactory();

        GridCoverage2D gc = (GridCoverage2D) factory.create("GridCoverage", image, gtEnv); //$NON-NLS-1$
        return gc;
    }

    @SuppressWarnings("unchecked")
    private void setImageFormat( WebMapServer wms, GetMapRequest request ) {
        List formats = wms.getCapabilities().getRequest().getGetMap().getFormats();
        String str;
        if (getPreferencesStore().getBoolean(PreferenceConstants.P_USE_DEFAULT_ORDER)) {
            str = getPreferencesStore().getDefaultString(PreferenceConstants.P_IMAGE_TYPE_ORDER);
        } else {
            str = getPreferencesStore().getString(PreferenceConstants.P_IMAGE_TYPE_ORDER);
        }
        String[] preferredFormats = str.split(","); //$NON-NLS-1$
        // Select one of the available formats from the WMS server
        // the order of preferred formats is set in the preferences
        for( String format : preferredFormats ) {
            if (formats.contains(format)) {
                request.setProperty(GetMapRequest.FORMAT, format);
                request.setTransparent(formatSupportsTransparency(format));
                break;
            }
        }
    }

    private BufferedImage readImage( final WebMapServer wms, final GetMapRequest request,
            IProgressMonitor monitor ) throws RenderException {
        final BufferedImage[] image = new BufferedImage[1];
        final RenderException[] exception = new RenderException[1];
        final Object condition = new Object();

        Thread thread = new Thread(){
            @Override
            public void run() {
                InputStream inputStream = null;
                try {
                    inputStream = wms.issueRequest(request).getInputStream();
                    image[0] = ImageIO.read(inputStream);
                } catch (IOException e1) {
                    exception[0] = wrapException(e1);
                } catch (ServiceException e) {
                    exception[0]=wrapException(e);
                } catch( Throwable t){
                	String message = Messages.BasicWMSRenderer2_errorObtainingImage;
					image[0] = getContext().getImage();
					exception[0]=new RenderException(message, t);
                } finally {
                    synchronized (condition) {
                        condition.notify();
                    }
                    if (inputStream != null)
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            WMSPlugin.log("failed to close input stream!!!", e); //$NON-NLS-1$
                        }
                }
            }
        };
        thread.start();

        int time = 0;

        while( image[0] == null && !monitor.isCanceled() && exception[0] == null ) {
            synchronized (condition) {
                try {
                    time += 200;
                    condition.wait(200);
                    if (time == 1000) {
                        setState(RENDERING);
                        time = 0;
                    }
                } catch (InterruptedException e) {
                    thread.interrupt();
                    throw wrapException(e);
                }
            }
        }
        
        if( exception[0]!=null )
            throw exception[0];
        return image[0];
    }

    private boolean formatSupportsTransparency( String format ) {
        if (format.equalsIgnoreCase("image/png")) //$NON-NLS-1$
            return true;
        if (format.equalsIgnoreCase("image/png8")) //$NON-NLS-1$
            return true;
        if (format.equalsIgnoreCase("image/gif")) //$NON-NLS-1$
            return true;
        if (format.equalsIgnoreCase("image/tiff")) //$NON-NLS-1$
            return true;
        if (format.equalsIgnoreCase("image/bmp")) //$NON-NLS-1$
            return true;
        return false;
    }

    /**
     * Determines the dimensions of the image to request, usually 1:1 to display, but sometimes more
     * (too fuzzy) or less (image too large) when reprojecting.
     * 
     * @param maxDimensions TODO
     * @param viewport
     * @param request
     * @param context
     * @return
     * @throws RenderException
     */
    public static Dimension calculateImageDimensions( Dimension displaySize,
            Dimension maxDimensions, Envelope viewport, Envelope request ) throws RenderException {
        double xScale = request.getWidth() / viewport.getWidth();
        double yScale = request.getHeight() / viewport.getHeight();
        // TODO: adjust height and width when we are reprojecting to make things less fuzzy

        int width = (int) (xScale * displaySize.getWidth());
        int height = (int) (yScale * displaySize.getHeight());

        // ensure we don't exceed the dimensions the server will allow
        int maxWidth = (int) maxDimensions.getWidth();
        if ((maxWidth > 0) && (width > maxWidth)) {
            width = maxWidth;
        }
        int maxHeight = (int) maxDimensions.getHeight();
        if ((maxHeight > 0) && (height > maxHeight)) {
            height = maxHeight;
        }

        WMSPlugin.trace("WMS request image dimensions: " + width + ", " + height); //$NON-NLS-1$ //$NON-NLS-2$
        return new Dimension(width, height);
    }

    public Point calculateImageOffset( Point min, Point max ) throws RenderException {
        return new Point(Math.min(min.x, max.x), max.y);
    }

    public static ReferencedEnvelope calculateRequestBBox(List<Layer> wmsLayers,
            ReferencedEnvelope viewport, CoordinateReferenceSystem requestCRS)
            throws MismatchedDimensionException, TransformException, FactoryException {
        return calculateRequestBBox(wmsLayers, viewport, requestCRS, "1.1.1");
    }
    /**
     * Using the viewport bounds and combined wms layer extents, determines an appropriate bounding
     * box by projecting the viewport into the request CRS, intersecting the bounds, and returning
     * the result.
     * 
     * @param wmsLayers all adjacent wms layers we are requesting
     * @param viewport map editor bounds and crs
     * @param requestCRS coordinate reference system supported by the server
     * @return the bbox to ask the server for
     * @throws MismatchedDimensionException
     * @throws TransformException
     * @throws FactoryException
     */
    public static ReferencedEnvelope calculateRequestBBox( List<Layer> wmsLayers,
            ReferencedEnvelope viewport, CoordinateReferenceSystem requestCRS, String version )
            throws MismatchedDimensionException, TransformException, FactoryException {
        /* The bounds of all wms layers on this server combined */
        ReferencedEnvelope layersBBox = getLayersBoundingBox(requestCRS, wmsLayers, version);
        if (isEnvelopeNull(layersBBox)) {
            // the wms server has no bounds
            WMSPlugin.log("Zero width/height envelope: wmsLayers = " + layersBBox); //$NON-NLS-1$
            layersBBox = null;
            // alternatively, we could impose a reprojected -180,180,-90,90
        }

        /* The viewport bounds projected to the request crs */
        ReferencedEnvelope reprojectedViewportBBox = viewport.transform(requestCRS, true);
        if (isEnvelopeNull(reprojectedViewportBBox)) {
            // viewport couldn't be reprojected
            WMSPlugin.log("Zero width/height envelope: reprojected viewport from " + viewport //$NON-NLS-1$
                    + " to " + requestCRS + " returned " + reprojectedViewportBBox); //$NON-NLS-1$ //$NON-NLS-2$
        }
        // alternative for better accuracy: new ReferencedEnvelope(JTS.transform(viewport, null,
        // CRS.findMathTransform(viewportCRS, crs, true), 4), crs);

        /* The intersection of the viewport and the combined wms layers */
        Envelope interestBBox;
        if (layersBBox == null) {
            interestBBox = reprojectedViewportBBox;
        } else {
            interestBBox = reprojectedViewportBBox.intersection(layersBBox);
        }
        if (isEnvelopeNull(interestBBox)) {
            // outside of bounds, do not draw
            WMSPlugin.trace("Bounds of the data are outside the bounds of the viewscreen."); //$NON-NLS-1$
            return NILL_BOX;
        }

        /* The bounds of the request we are going to make */
        ReferencedEnvelope requestBBox = new ReferencedEnvelope(interestBBox, requestCRS);
        return requestBBox;
    }

    private static boolean isEnvelopeNull( Envelope bbox ) {
        if (bbox.getWidth() <= 0 || bbox.getHeight() <= 0) {
            return true;
        }
        return false;
    }

    // private static ReferencedEnvelope calculateOldRequestBoundingBox(
    // final boolean clientSideReprojection, final Envelope viewportBBox,
    // final CoordinateReferenceSystem viewportCRS, final List<Layer> wmsLayers,
    // final boolean[] isFullSizeOut ) throws RenderException {
    // isFullSizeOut[0] = false;
    //
    // Envelope viewportBBox2 = viewportBBox;
    // if (viewportBBox2 == null) {
    // viewportBBox2 = new Envelope(-180, 180, -90, 90);
    // }
    //
    // Envelope layersBBox = getLayersBoundingBox(viewportCRS, wmsLayers);
    // if (layersBBox == null)
    // try {
    // return new ReferencedEnvelope(viewportBBox, viewportCRS).transform(
    // DefaultGeographicCRS.WGS84, true);
    // } catch (TransformException e1) {
    // throw (RuntimeException) new RuntimeException(Messages.BasicWMSRenderer2_error)
    // .initCause(e1);
    // } catch (FactoryException e1) {
    // throw (RuntimeException) new RuntimeException(Messages.BasicWMSRenderer2_error)
    // .initCause(e1);
    // }
    //
    // if (!layersBBox.intersects(viewportBBox2))
    // return new ReferencedEnvelope(new Envelope(0, 0, 0, 0), viewportCRS);
    //
    // double minx, miny, maxx, maxy;
    // minx = layersBBox.getMinX();
    // maxx = layersBBox.getMaxX();
    // miny = layersBBox.getMinY();
    // maxy = layersBBox.getMaxY();
    // boolean noClipping = false;
    //
    // int i = 0;
    // if (viewportBBox2.getMinX() > minx || noClipping) {
    // minx = viewportBBox2.getMinX();
    // i++;
    // }
    // if (viewportBBox2.getMinY() > miny || noClipping) {
    // miny = viewportBBox2.getMinY();
    // i++;
    // }
    // if (viewportBBox2.getMaxX() < maxx || noClipping) {
    // maxx = viewportBBox2.getMaxX();
    // i++;
    // }
    // if (viewportBBox2.getMaxY() < maxy || noClipping) {
    // maxy = viewportBBox2.getMaxY();
    // i++;
    // }
    //
    // if (i == 4)
    // isFullSizeOut[0] = true;
    //
    // ReferencedEnvelope clippedBBox = new ReferencedEnvelope(
    // new Envelope(minx, maxx, miny, maxy), viewportCRS);
    //
    // if (clientSideReprojection) {
    // // Convert the clipped bounding box to the request CRS. This is the
    // // BBox to be used in the request.
    // try {
    // String code = findRequestCRS(wmsLayers);
    // if (code == null)
    // throw new RenderException(
    // "Error has occurred in the framework! There is no common CRS in layers in renderer");
    // //$NON-NLS-1$
    // CoordinateReferenceSystem crs = CRS.decode(code);
    //
    // clippedBBox = new ReferencedEnvelope(JTS.transform(clippedBBox, null, CRS
    // .findMathTransform(viewportCRS, crs, true), 4), crs);
    // } catch (NoSuchAuthorityCodeException e) {
    // WMSPlugin.log(e.getLocalizedMessage(), e);
    // return null;
    // } catch (FactoryException e) {
    // WMSPlugin.log(e.getLocalizedMessage(), e);
    // return null;
    // } catch (MismatchedDimensionException e) {
    // WMSPlugin.log(e.getLocalizedMessage(), e);
    // return null;
    // } catch (TransformException e) {
    // e.printStackTrace();
    // WMSPlugin.log(e.getLocalizedMessage(), e);
    // return null;
    // }
    // }
    //
    // return clippedBBox;
    // }
    private static ReferencedEnvelope swapAxis( ReferencedEnvelope envelope ){
        double min0 = envelope.getLowerCorner().getOrdinate(0);
        double min1 = envelope.getLowerCorner().getOrdinate(1);
        double max0 = envelope.getUpperCorner().getOrdinate(0);
        double max1 = envelope.getUpperCorner().getOrdinate(1);
        ReferencedEnvelope swap = new ReferencedEnvelope( min1,max1,min0,max0,envelope.getCoordinateReferenceSystem());
        
        return swap;
    }
    
    public static ReferencedEnvelope getLayersBoundingBox( CoordinateReferenceSystem crs, List<Layer> layers,String version ) {
        ReferencedEnvelope envelope = null;
        for( Layer layer : layers ) {
            GeneralEnvelope temp = layer.getEnvelope(crs);            
            if (temp != null) {
                ReferencedEnvelope jtsTemp = ReferencedEnvelope.reference( temp );
//              if( version != null && version.startsWith("1.3")){
//                  jtsTemp = swapAxis(jtsTemp);
//              }
                if (envelope == null) {
                    envelope = jtsTemp;
                } else {
                    envelope.expandToInclude(jtsTemp);
                }
            }
        }

        return envelope;
    }

    /**
     * We have made this visible so that WMSDescribeLayer (used by InfoView2)
     * can figure out how to make the *exact* same request in order
     * to a getInfo operation. We should really store the last request
     * on the layer blackboard for this intra module communication.
     * 
     * @return SRS code
     */
    public static String findRequestCRS( List<Layer> layers, CoordinateReferenceSystem viewportCRS, IMap map ) {
        String requestCRS = null;

        if (layers == null || layers.isEmpty()) {
            return null;
        }

        Collection<String> viewportEPSG=extractEPSG(map, viewportCRS);
        if( viewportEPSG!=null ){
            String match=matchEPSG(layers, viewportEPSG);
            if( match!=null ){
                return match;
            }
        }
        
        if( matchEPSG(layers, CRS_84) ){
            return CRS_84;    // preferred default
        }
        
        if( matchEPSG(layers, EPSG_4326) ){
            return EPSG_4326; // recommended from WMS specification
        }
        
        // Why prefer NAD84?
        if ( matchEPSG(layers, EPSG_4269)) {
            return EPSG_4269; // similar to CRS_84
        }

        Layer firstLayer = layers.get(0);
        for (Object object : firstLayer.getSrs()) {
            String epsgCode = (String) object;

            try {
                // Check to see if *we* can actually use this code first.
                CoordinateReferenceSystem check = CRS.decode(epsgCode);
                if( check == null ) {
                    continue; // skip this one!
                }
            } catch (NoSuchAuthorityCodeException e) {
                continue; // skip this one we do not have an authority for it
            } catch (FactoryException e) {
                e.printStackTrace(); // internal trouble :(
            }
            
            if (matchEPSG(layers, epsgCode)) {
                requestCRS = epsgCode;
                return requestCRS;
            }
        }

        if (requestCRS == null) {
            // Hmm. Our layers have no SRS in common - we are in an illegal state
            WMSPlugin
                    .log("ERROR: Illegal State: Basic WMS Renderer contains layers with no common CRS. Unable to perform request."); //$NON-NLS-1$
            return null;
        }
        return requestCRS;
    }

    private static String matchEPSG(List<Layer> layers, Collection<String> epsgCodes) {
        for (String epsg : epsgCodes) {
            if (matchEPSG(layers, epsg))
                return epsg;
        }
        return null;
    }


    private static Collection<String> extractEPSG(final IMap map,
            final CoordinateReferenceSystem crs) {

        final Collection<String> codes = new ArrayList<String>();
        if (CRS.equalsIgnoreMetadata(crs, DefaultGeographicCRS.WGS84)) {
            codes.add(CRS_84);
            codes.add(EPSG_4326);
            return codes;
        }        
        codes.addAll(CRSUtil.extractAuthorityCodes(crs));

        final String DONT_FIND = "DONT_FIND";
        boolean search = map.getBlackboard().get(EPSG_CODE) != DONT_FIND;
        if (codes.isEmpty() && search) {
            PlatformGIS.syncInDisplayThread(new Runnable() {
                public void run() {
                    Shell shell = Display.getCurrent().getActiveShell();

                    ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
                    try {
                        dialog.run(false, true, new IRunnableWithProgress() {

                            public void run(IProgressMonitor monitor)
                                    throws InvocationTargetException, InterruptedException {
                                CoordinateReferenceSystem found = CRSUtil
                                        .findEPSGCode(crs, monitor);
                                if (found == null) {
                                    return;
                                }

                                ViewportModel model = (ViewportModel) map.getViewportModel();
                                model.eSetDeliver(false);
                                try {
                                    model.setCRS(found);
                                    codes.addAll(CRSUtil.extractAuthorityCodes(found));
                                } finally {
                                    model.eSetDeliver(true);
                                }
                            }

                        });
                    } catch (InvocationTargetException e) {
                        WMSPlugin.log("Error tracking down EPSG Code", e);
                        dontFind(map, DONT_FIND);
                    } catch (InterruptedException e) {
                        WMSPlugin.log("Error tracking down EPSG Code", e);
                        dontFind(map, DONT_FIND);
                    }
                    dontFind(map, DONT_FIND);
                }
            });
        }
        return codes;
    }
	

    private static void dontFind( final IMap map, final String DONT_FIND ) {
        map.getBlackboard().put(EPSG_CODE,
                DONT_FIND);
    }
    /**
     * Quickly check provided layers to ensure they have the provided epsgCode in common.
     * 
     * @param layers
     * @param epsgCode
     * @return
     */
    private static boolean matchEPSG(List<Layer> layers, String epsgCode) {
        boolean match = true;
        for (Layer layer : layers) {
            Set<String> srs = layer.getSrs();
            if (!srs.contains(epsgCode)) {
                match = false;
                break;
            }
        }
        return match;
    }

    private List<ILayer> getLayers() {
        List<ILayer> layers = new ArrayList<ILayer>();

        ICompositeRenderContext context1 = getContext();
        IRenderContext[] contexts = context1.getContexts().toArray(
                new IRenderContext[context1.getContexts().size()]);

        if (contexts.length == 0)
            throw new RuntimeException(Messages.BasicWMSRenderer2_no_layers_to_render);

        for( IRenderContext renderContext : contexts ) {
            if (renderContext.getLayer().isVisible()) {
                layers.add(renderContext.getLayer());
            }
        }

        if (layers.isEmpty()) {
            WMSPlugin.log("WARNING: WMS Renderer contains no layers."); //$NON-NLS-1$
        }

        return layers;
    }

    private List<Layer> getWMSLayers() throws IOException {
        List<Layer> layers = new ArrayList<Layer>();

        for( ILayer iLayer : getLayers() ) {
            Layer layer = iLayer.getResource(Layer.class, null);
            layers.add(layer);
        }

        return layers;
    }

    private WebMapServer getWMS() throws IOException {
        return getContext().getLayer().getResource(WebMapServer.class, null);
    }

    @Override
    public void dispose() {
        // TODO: make sure there is nothing needing disposal
    }

    Job refreshJob = new Job(REFRESH_JOB){
        @Override
        protected IStatus run( IProgressMonitor monitor ) {
            getContext().clearImage();
            try {
                render(getContext().getImage().createGraphics(), monitor);
            } catch (Throwable e) {
                WMSPlugin.log(e.getLocalizedMessage(), e);
            }
            return Status.OK_STATUS;
        }

    };
    public void refreshImage() throws RenderException {
        if (needRefresh()) {
            refreshJob.schedule();
            try {
                refreshJob.join();
            } catch (InterruptedException e) {
                // TODO Catch e
            }
        }
    }

    /**
     * Determine whether the image needs to be refreshed
     * 
     * @return Whether the image needs to be refreshed
     */
    protected boolean needRefresh() {
        return false; // TODO
    }

    public RenderException wrapException( Throwable t ) {
        getContext().setStatus(ILayer.ERROR);
        RenderException renderException = new RenderException(t.getLocalizedMessage());
        renderException.initCause(t);
        return renderException;
    }

    @Override
    public ICompositeRenderContext getContext() {
        return (ICompositeRenderContext) super.getContext();
    }

    private CoordinateReferenceSystem getViewportCRS() {
        return getContext().getViewportModel().getCRS();
    }

    private IPreferenceStore getPreferencesStore() {
        return WMSPlugin.getDefault().getPreferenceStore();
    }

}
