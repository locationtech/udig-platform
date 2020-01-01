/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wms;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.ows.ServiceException;
import org.geotools.ows.wms.CRSEnvelope;
import org.geotools.ows.wms.Layer;
import org.geotools.ows.wms.WMSCapabilities;
import org.geotools.ows.wms.WebMapServer;
import org.geotools.ows.wms.request.GetLegendGraphicRequest;
import org.geotools.ows.wms.response.GetLegendGraphicResponse;
import org.geotools.ows.wms.xml.WMSSchema;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.ui.CatalogUIPlugin;
import org.locationtech.udig.catalog.ui.ISharedImages;
import org.locationtech.udig.catalog.wms.internal.Messages;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * SimpleFeatureType provided by WFS. </p>
 * 
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class WMSGeoResourceImpl extends IGeoResource {

    org.geotools.ows.wms.Layer layer;
    private ImageDescriptor icon;
    private URL identifier;
    private ArrayList<IResolve> members;
    private IResolve parent;
    private Lock iconLock = new ReentrantLock();

    /**
     * Construct <code>WMSGeoResourceImpl</code>.
     * 
     * @param service
     * @param parent the parent Georesource may be null if parent is the service.
     * @param layer
     */
    public WMSGeoResourceImpl( WMSServiceImpl service, IResolve parent,
            org.geotools.ows.wms.Layer layer ) {
        this.service = service;
        if (parent == null) {
            this.parent = service;
        } else {
            this.parent = parent;
        }
        this.layer = layer;
        members = new ArrayList<IResolve>();
        for( Layer child : layer.getChildren() ) {
            if (child != layer) {
                if (child.getName() == null) {
                    members.add(new WMSFolder(service, this, child));
                } else {
                    members.add(new WMSGeoResourceImpl(service, this, child));
                }
            }
        }

        try {
            String name = layer.getName();
            if (name == null) {
                WmsPlugin.log("Can't get a unique name for the identifier of WMSGeoResource: "
                        + layer, null);
                throw new RuntimeException("This should be a WMSFolder not an IGeoResource");
            }
            identifier = new URL(service.getIdentifier().toString() + "#" + name); //$NON-NLS-1$

        } catch (Throwable e) {
            WmsPlugin.log(null, e);
            identifier = service.getIdentifier();
        }
    }

    @Override
    public IResolve parent( IProgressMonitor monitor ) throws IOException {
        return parent;
    }
    /*
     * @see org.locationtech.udig.catalog.IGeoResource#getStatus()
     */
    public Status getStatus() {
        return service.getStatus();
    }

    @Override
    public WMSResourceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        return (WMSResourceInfo) super.getInfo(monitor);
    }
    protected WMSResourceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        WMSServiceImpl wmsServer = service(new SubProgressMonitor(monitor, 50));

        wmsServer.rLock.lock();
        try {
            return new WMSResourceInfo(new SubProgressMonitor(monitor, 50));
        } finally {
            wmsServer.rLock.unlock();
        }
    }

    public URL getIdentifier() {
        return identifier;
    }

    @Override
    public List<IResolve> members( IProgressMonitor monitor ) {
        return members;
    }

    /*
     * @see org.locationtech.udig.catalog.IGeoResource#resolve(java.lang.Class,
     * org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee == null) {
            throw new NullPointerException();
        }

        // if (adaptee.isAssignableFrom(IService.class)) {
        // return adaptee.cast( parent);
        // }

        if (adaptee.isAssignableFrom(IGeoResource.class)) {
            return adaptee.cast(this);
        }

        if (adaptee.isAssignableFrom(IGeoResourceInfo.class)) {
            return adaptee.cast(createInfo(monitor));
        }

        if (adaptee.isAssignableFrom(WebMapServer.class)) {
            return adaptee.cast(service(monitor).getWMS(monitor));
        }

        if (adaptee.isAssignableFrom(org.geotools.ows.wms.Layer.class)) {
            return adaptee.cast(layer);
        }
        if (adaptee.isAssignableFrom(ImageDescriptor.class)) {
            return adaptee.cast(getIcon(monitor));
        }
        return super.resolve(adaptee, monitor);
    }
    /** Must be the same as resolve( ImageDescriptor.class ) */
    public ImageDescriptor getIcon( IProgressMonitor monitor ) throws IOException {
        iconLock.lock();
        try {
            if (icon == null) {
                icon = fetchIcon(monitor, layer, service(monitor));
                if (icon == null) {
                    icon = CatalogUIPlugin.getDefault().getImageDescriptor(
                            ISharedImages.GRID_OBJ);
                }
            }
            return icon;
        } finally {
            iconLock.unlock();
        }
    }
    /**
     * This method will fetch the Icon associated with this url (if such is available).
     * 
     * @see WMSFolder
     * @param monitor
     * @return Requested Icon or ISharedImages.GRID_OBJ
     */
    protected static ImageDescriptor fetchIcon( IProgressMonitor monitor, Layer layer,
            WMSServiceImpl service ) {
        try {
            if (monitor != null)
                monitor.beginTask(Messages.WMSGeoResourceImpl_acquiring_task, 3);
            if (monitor != null)
                monitor.worked(1);

            if (layer.getChildren() != null && layer.getChildren().length != 0) {
                // Do not request "parent" layer graphics - this kills Mapserver
                return CatalogUIPlugin.getDefault().getImageDescriptor(
                        ISharedImages.GRID_OBJ);
            }

            ImageDescriptor imageDescriptor = requestImage(monitor, layer, service);

            Image image = null;
            Image swatch = null;
            try {
                if (monitor != null)
                    monitor.worked(2);
                if (monitor != null)
                    monitor.subTask(Messages.WMSGeoResourceImpl_downloading_icon);
                image = imageDescriptor.createImage();
                Rectangle bound = image.getBounds();
                if (bound.width == 16 && bound.height == 16) {
                    // perfect! it did what was expected!
                    //
                    final ImageData data = (ImageData) image.getImageData().clone();
                    return new ImageDescriptor(){
                        public ImageData getImageData() {
                            return (ImageData) data.clone();
                        }
                    };
                }
                if (bound.height < 16 || bound.width < 16) {
                    // the image is smaller than what we asked for
                    // perhaps we should display nothing?
                    // in stead we will try scaling it up
                    if (WmsPlugin.getDefault().isDebugging()) {
                        System.out.println("Icon scaled up to requested size"); //$NON-NLS-1$                                        
                    }
                    final ImageData data = image.getImageData().scaledTo(16, 16);
                    return new ImageDescriptor(){
                        public ImageData getImageData() {
                            return (ImageData) data.clone();
                        }
                    };
                }
                // the image is larger than the size we asked for
                // (so this WMS is not being nice!)
                // we will try and decide what to do here ...
                // let us select the image we want ...

                swatch = new Image(null, 16, 16);
                GC gc = new GC(swatch);
                int sy = 0; // (bound.height / 2 ) - 8;
                int sx = 0;
                int sw = 0;
                int sh = 0;
                ImageData contents = image.getImageData();
                if (contents == null) {
                    return CatalogUIPlugin.getDefault().getImageDescriptor(
                            ISharedImages.GRID_MISSING);
                }
                if (contents.maskData != null) {
                    // ((width + 7) / 8 + (maskPad - 1)) / maskPad * maskPad
                    int maskPad = contents.maskPad;
                    int scanLine = ((contents.width + 7) / 8 + (maskPad - 1)) / maskPad * maskPad;
                    // skip leading mask ...
                    SKIPY: for( int y = 0; y < contents.height / 2; y++ ) {
                        sy = y;
                        for( int x = 0; x < contents.width / 2; x++ ) {
                            int mask = contents.maskData[y * scanLine + x];
                            if (mask != 0)
                                break SKIPY;
                        }
                    }
                    SKIPX: for( int x = 0; x < contents.width / 2; x++ ) {
                        sx = x;
                        for( int y = sy; y < contents.height / 2; y++ ) {
                            int mask = contents.maskData[y * scanLine + x];
                            if (mask != 0)
                                break SKIPX;
                        }
                    }
                    sh = Math.min(contents.height - sy, 16);
                    sw = Math.min(contents.width - sx, 16);
                    if (WmsPlugin.getDefault().isDebugging())
                        System.out.println("Mask offset to " + sx + "x" + sy); //$NON-NLS-1$ //$NON-NLS-2$                        
                } else if (contents.alphaData != null) {
                    SKIPY: for( int y = 0; y < contents.height / 2; y++ ) {
                        sy = y;
                        for( int x = 0; x < contents.width / 2; x++ ) {
                            int alpha = contents.alphaData[y * contents.width + x];
                            if (alpha != 0)
                                break SKIPY;
                        }
                    }
                    SKIPX: for( int x = 0; x < contents.width / 2; x++ ) {
                        sx = x;
                        for( int y = sy; y < contents.height / 2; y++ ) {
                            int alpha = contents.alphaData[y * contents.width + x];
                            if (alpha != 0)
                                break SKIPX;
                        }
                    }
                    sh = Math.min(contents.height - sy, 16);
                    sw = Math.min(contents.width - sx, 16);
                    if (WmsPlugin.getDefault().isDebugging())
                        System.out.println("Alpha offset to " + sx + "x" + sy); //$NON-NLS-1$ //$NON-NLS-2$                        
                } else {
                    // try ignoring "white"
                    int depth = contents.depth;
                    int scanLine = contents.bytesPerLine;
                    SKIPY: for( int y = 0; y < contents.height / 2; y++ ) {
                        sy = y;
                        for( int x = 0; x < contents.width / 2; x++ ) {
                            int datum = contents.data[y * scanLine + x * depth];
                            if (datum != 0)
                                break SKIPY;
                        }
                    }
                    SKIPX: for( int x = 0; x < contents.width / 2; x++ ) {
                        sx = x;
                        for( int y = sy; y < contents.height / 2; y++ ) {
                            int datum = contents.data[y * scanLine + x * depth];
                            if (datum != 0)
                                break SKIPX;
                        }
                    }
                    sh = Math.min(contents.height - sy, 16);
                    sw = Math.min(contents.width - sx, 16);
                    if (WmsPlugin.getDefault().isDebugging())
                        System.out.println("Alpha offset to " + sx + "x" + sy); //$NON-NLS-1$ //$NON-NLS-2$                        
                }
                // else {
                // sh = Math.min( bound.height, bound.width );
                // sw = Math.min( bound.height, bound.width );
                // }
                if (WmsPlugin.getDefault().isDebugging())
                    System.out.println("Image resized to " + sh + "x" + sw); //$NON-NLS-1$ //$NON-NLS-2$

                // gc.drawImage(image, sx, sy, sw, sh, 0, 0, 16, 16);

                // chances are this has a label or category view or something
                // grab the gply from the bottom left corner and we are good to
                // (based on mapserver example)
                //
                gc.drawImage(image, 0, bound.height - 16, 16, 16, 0, 0, 16, 16);

                final ImageData data = (ImageData) swatch.getImageData().clone();
                return new ImageDescriptor(){
                    public ImageData getImageData() {
                        return (ImageData) data.clone();
                    }
                };
            } finally {
                if (image != null) {
                    image.dispose();
                }
                if (swatch != null) {
                    swatch.dispose();
                }
                if (monitor != null)
                    monitor.worked(3);
            }
        } catch (IOException t) {
            WmsPlugin.trace("Could not get icon", t); //$NON-NLS-1$
            return CatalogUIPlugin.getDefault().getImageDescriptor(
                    ISharedImages.GRID_MISSING);
        }
    }

    @SuppressWarnings("unchecked")
    private static ImageDescriptor requestImage( IProgressMonitor monitor, Layer layer,
            WMSServiceImpl service ) throws IOException {
        WebMapServer wms = service.getWMS(monitor);

        if (wms.getCapabilities().getRequest().getGetLegendGraphic() == null) {
            return CatalogUIPlugin.getDefault().getImageDescriptor(
                    ISharedImages.GRID_OBJ);
        }

        ImageDescriptor imageDescriptor = null;
        try {
            GetLegendGraphicRequest request = wms.createGetLegendGraphicRequest();
            request.setLayer(layer.getName());
            request.setWidth("16"); //$NON-NLS-1$
            request.setHeight("16"); //$NON-NLS-1$

            List<String> formats = wms.getCapabilities().getRequest().getGetLegendGraphic()
                    .getFormats();

            Collections.sort(formats, new Comparator<String>(){

                public int compare( String format1, String format2 ) {
                    if (format1.trim().equalsIgnoreCase("image/png")) { //$NON-NLS-1$
                        return -1;
                    }
                    if (format2.trim().equalsIgnoreCase("image/png")) { //$NON-NLS-1$
                        return 1;
                    }
                    if (format1.trim().equalsIgnoreCase("image/gif")) { //$NON-NLS-1$
                        return -1;
                    }
                    if (format2.trim().equalsIgnoreCase("image/gif")) { //$NON-NLS-1$
                        return 1;
                    }
                    return 0;
                }

            });

            for( Iterator<String> iterator = formats.iterator(); iterator.hasNext()
                    && imageDescriptor == null; ) {
                String format = iterator.next();

                imageDescriptor = loadImageDescriptor(wms, request, format);
            }

            if (imageDescriptor == null) {
                // cannot understand any of the provided formats
                return CatalogUIPlugin.getDefault().getImageDescriptor(
                        ISharedImages.GRID_OBJ);
            }
        } catch (UnsupportedOperationException notAvailable) {
            WmsPlugin.trace("Icon is not available", notAvailable); //$NON-NLS-1$                
            return CatalogUIPlugin.getDefault().getImageDescriptor(
                    ISharedImages.GRID_OBJ);
        } catch (ServiceException e) {
            WmsPlugin.trace("Icon is not available", e); //$NON-NLS-1$                
            return CatalogUIPlugin.getDefault().getImageDescriptor(
                    ISharedImages.GRID_OBJ);
        }

        return imageDescriptor;
    }
    private static ImageDescriptor loadImageDescriptor( WebMapServer wms,
            GetLegendGraphicRequest request, String desiredFormat ) throws IOException,
            ServiceException {
        if (desiredFormat == null) {
            return null;
        }
        try {
            ImageDescriptor imageDescriptor;
            request.setFormat(desiredFormat);
            if( wms.getCapabilities().getVersion().startsWith("1.3") ){
                // NO STYLE as it is optional
            }
            else {
                request.setStyle(""); //$NON-NLS-1$
            }

            System.out.println(request.getFinalURL().toExternalForm());

            GetLegendGraphicResponse response = wms.issueRequest(request);

            imageDescriptor = ImageDescriptor.createFromImageData(getImageData(response
                    .getInputStream()));
            return imageDescriptor;
        } catch (SWTException exc) {
            WmsPlugin.trace("Icon is not available or has unsupported format", exc); //$NON-NLS-1$                
            return null;
        }
    }

    private static ImageData getImageData( InputStream in ) {
        ImageData result = null;
        if (in != null) {
            try {
                result = new ImageData(in);
            } catch (SWTException e) {
                if (e.code != SWT.ERROR_INVALID_IMAGE)
                    throw e;
                // fall through otherwise
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    // System.err.println(getClass().getName()+".getImageData(): "+
                    // "Exception while closing InputStream : "+e);
                }
            }
        }
        return result;
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null) {
            return false;
        }

        if (adaptee.isAssignableFrom(IGeoResource.class)
                || adaptee.isAssignableFrom(WebMapServer.class)
                || adaptee.isAssignableFrom(org.geotools.ows.wms.Layer.class)
                || adaptee.isAssignableFrom(ImageDescriptor.class)
                || adaptee.isAssignableFrom(IService.class) || super.canResolve(adaptee)) {
            return true;
        }

        return false;
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#getMessage()
     */
    public Throwable getMessage() {
        return service.getMessage();
    }

    private class WMSResourceInfo extends IGeoResourceInfo {
        @SuppressWarnings("unchecked")
        WMSResourceInfo( IProgressMonitor monitor ) throws IOException {
            WebMapServer wms = service(monitor).getWMS(monitor);
            WMSCapabilities caps = wms.getCapabilities();

            if (layer.getTitle() != null && layer.getTitle().length() != 0) {
                title = layer.getTitle();
            }

            calculateBounds();

            String parentid = service != null && service.getIdentifier() != null ? getIdentifier()
                    .toString() : ""; //$NON-NLS-1$
            name = layer.getName();
            getKeywords(caps, parentid);

            if (layer.get_abstract() != null && layer.get_abstract().length() != 0) {
                description = layer.get_abstract();
            } else {
                description = caps.getService().get_abstract();
            }
            description = caps.getService().get_abstract();

            super.icon = CatalogUIPlugin.getDefault().getImageDescriptor(
                    ISharedImages.GRID_OBJ);

            // icon = fetchIcon( monitor );
        }

        private void getKeywords( WMSCapabilities caps, String parentid ) {
            List<String> keywordsFromWMS = new ArrayList<String>();
            if (caps.getService().getKeywordList() != null) {
                keywordsFromWMS.addAll(Arrays.asList(caps.getService().getKeywordList()));
            }

            if (layer.getKeywords() != null) {
                keywordsFromWMS.addAll(Arrays.asList(layer.getKeywords()));
            }
            keywordsFromWMS.add("WMS"); //$NON-NLS-1$
            keywordsFromWMS.add(layer.getName());
            keywordsFromWMS.add(caps.getService().getName());
            keywordsFromWMS.add(parentid);
            keywords = keywordsFromWMS.toArray(new String[keywordsFromWMS.size()]);
        }

        @SuppressWarnings("unchecked")
        private void calculateBounds() {
            org.opengis.geometry.Envelope env = null;
            CoordinateReferenceSystem crs = null;

            Map<String, CRSEnvelope> boundingBoxes = layer.getBoundingBoxes();

            if (boundingBoxes.isEmpty()) {
                crs = DefaultGeographicCRS.WGS84;
                // env = layer.getLatLonBoundingBox();
                env = layer.getEnvelope(crs);
            } else {
                GeneralEnvelope layerDefinedEnv = layer.getEnvelope(DefaultGeographicCRS.WGS84);

                CRSEnvelope bbox;
                String epsg4326 = "EPSG:4326"; //$NON-NLS-1$
                String epsg4269 = "EPSG:4269"; //$NON-NLS-1$

                if (boundingBoxes.size() < 4) {
                    // This is a silly heuristic but the idea is that if there are only a few bboxes
                    // then one of them is likely the *natural* crs and that crs should be used.

                    bbox = boundingBoxes.values().iterator().next();
                } else if (boundingBoxes.containsKey(epsg4326)) {
                    bbox = boundingBoxes.get(epsg4326);
                } else if (boundingBoxes.containsKey(epsg4269)) {
                    bbox = boundingBoxes.get(epsg4269);
                } else {
                    bbox = boundingBoxes.values().iterator().next();
                }
                try {
                    if (bbox.getEPSGCode().equals(epsg4269) || bbox.getEPSGCode().equals(epsg4326)) {
                        // It is lat long so lets use the layer definition
                        env = layerDefinedEnv;
                        crs = DefaultGeographicCRS.WGS84;
                    } else {
                        crs = CRS.decode(bbox.getEPSGCode());
                        env = new ReferencedEnvelope(bbox.getMinX(), bbox.getMaxX(),
                                bbox.getMinY(), bbox.getMaxY(), crs);
                    }
                } catch (NoSuchAuthorityCodeException e) {
                    crs = DefaultGeographicCRS.WGS84;
                    env = layer.getEnvelope(crs);
                } catch (FactoryException e) {
                    crs = DefaultGeographicCRS.WGS84;
                    env = layer.getEnvelope(crs);
                }
            }
            bounds = new ReferencedEnvelope(new Envelope(env.getMinimum(0), env.getMaximum(0), env
                    .getMinimum(1), env.getMaximum(1)), crs);
        }

        public String getName() {
            return name;
        }
        public URI getSchema() {
            return WMSSchema.NAMESPACE;
        }
        public String getTitle() {
            return title;
        }
    }
    @Override
    public WMSServiceImpl service( IProgressMonitor monitor ) throws IOException {
        return (WMSServiceImpl) super.service(monitor);
    }
}
