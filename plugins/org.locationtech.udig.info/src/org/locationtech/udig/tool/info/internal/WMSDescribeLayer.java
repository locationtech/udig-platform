/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tool.info.internal;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.ows.Layer;
import org.geotools.data.ows.StyleImpl;
import org.geotools.data.wms.WebMapServer;
import org.geotools.data.wms.request.GetFeatureInfoRequest;
import org.geotools.data.wms.request.GetMapRequest;
import org.geotools.data.wms.response.GetFeatureInfoResponse;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.render.ICompositeRenderContext;
import org.locationtech.udig.style.wms.WMSStyleContent;
import org.locationtech.udig.tool.info.InfoPlugin;
import org.locationtech.udig.tool.info.LayerPointInfo;
import org.xml.sax.SAXException;

import com.vividsolutions.jts.geom.Envelope;

public class WMSDescribeLayer {
    
    public WebMapServer getWMS( ICompositeRenderContext context, IProgressMonitor monitor ) throws IOException {
        return context.getLayer().getResource(WebMapServer.class, monitor );
    }

    @SuppressWarnings("unchecked")
    public static List<LayerPointInfo> info( ILayer layer, ReferencedEnvelope bbox ) throws IOException {
        LayerPointInfo info = info2( layer, bbox );
        if( info == null ) return Collections.EMPTY_LIST;
        
        return Collections.singletonList( info );        
    }
    
    /**
     * Acquire info for the provided bbox
     * 
     * @param layer Must be associated with a wms layer 
     * @param bbox
     * @return LayerPointInfo object or null if information is unavailable.
     * @throws IOException 
     * @throws IOException if Response could not be understood
     * 
     * TODO: possible problem with client side reprojection - may need to 
     * properly reproject the center point that was clicked on
     * 
     * TODO: this requires some testing
     */
    public static LayerPointInfo info2( ILayer layer, ReferencedEnvelope query ) throws IOException {        

		Envelope reprojected = null;
		IMap map = layer.getMap();
        try {
            reprojected = query.transform(map.getViewportModel().getCRS(), true);
        } catch (Exception e) {
            InfoPlugin.log("", e); //$NON-NLS-1$
            return null;
        }
        Point centre = map.getViewportModel().worldToPixel(reprojected.centre());

        Envelope sanebbox = map.getViewportModel().getBounds();
        ReferencedEnvelope bbox = new ReferencedEnvelope(sanebbox, query.getCoordinateReferenceSystem());
		
    	Layer wmslayer;
        wmslayer = layer.getResource( Layer.class, null );
        
        if( wmslayer == null ) {
            throw new IllegalArgumentException("Provided layer is not a WMS layer" ); //$NON-NLS-1$
        }
        if( !wmslayer.isQueryable() ){
            return null;
        }
        // TODO: Fix wmslayer so we can ask who its "source" is.
        final WebMapServer wms = layer.getResource( WebMapServer.class, null );        
        if( wms == null ) {
            throw new IllegalArgumentException("Provided layer cannot resolve to a wms" ); //$NON-NLS-1$
        }
        String desiredFormat = desiredInfoFormat( wms );                
        if( desiredFormat == null ){
            return null;
        }
        GetMapRequest getmap = wms.createGetMapRequest();        
        getmap.setBBox( bbox );
        getmap.setProperty( GetMapRequest.LAYERS, wmslayer.getName() );
        int width = map.getRenderManager().getMapDisplay().getWidth();
        int height = map.getRenderManager().getMapDisplay().getHeight();
        getmap.setDimensions(width, height);
        //getmap.setSRS(code);
        
        List<String> formats = wms.getCapabilities().getRequest().getGetMap().getFormats();
        if (formats.contains("image/png")) { //$NON-NLS-1$
            getmap.setProperty(GetMapRequest.FORMAT, "image/png"); //$NON-NLS-1$
        } else if (formats.contains("image/gif")) { //$NON-NLS-1$
            getmap.setProperty(GetMapRequest.FORMAT, "image/gif"); //$NON-NLS-1$
        } else if (formats.contains("image/jpeg")) { //$NON-NLS-1$
            getmap.setProperty(GetMapRequest.FORMAT, "image/jpeg"); //$NON-NLS-1$
        } else if (formats.contains("image/bmp")) { //$NON-NLS-1$
            getmap.setProperty(GetMapRequest.FORMAT, "image/bmp"); //$NON-NLS-1$
        }
        
        StyleImpl wmsStyle = (StyleImpl) layer.getStyleBlackboard().get(WMSStyleContent.WMSSTYLE);
        if (wmsStyle != null) {
            getmap.setProperty(GetMapRequest.STYLES, wmsStyle.getName());
        }
        else {
            // supply an empty String as per UDIG-1507
            getmap.setProperty(GetMapRequest.STYLES, "");
        }
        
        final GetFeatureInfoRequest request = wms.createGetFeatureInfoRequest( getmap );                       
        request.setInfoFormat( desiredFormat );
        request.setQueryPoint( centre.x, centre.y );                
        request.setQueryLayers( Collections.singleton( wmslayer ) );
                
        LayerPointInfo info = new LayerPointInfo( layer ){
                    
            private GetFeatureInfoResponse response;
            /** Lazy request */
                    
            protected GetFeatureInfoResponse getResponse() throws IOException {
                if (this.response == null) {
                    try {
                        System.out.println(request.getFinalURL());
                        this.response = wms.issueRequest( request );
                    } catch (SAXException e) {
                        throw new IOException(
                                "Unable to parse the returned response from the server. Reason unknown."); //$NON-NLS-1$
                    }
                }
                return this.response;
            }
            public URL getRequestURL() {
                return request.getFinalURL();
            }
            /** Only acquire the value if needed */
            public String getMimeType() {
                try {
                    return getResponse().getContentType();
                } catch (IOException e) {
                    return null; // unavailable
                }
            }
            /** Only acquire the value if needed */
            public Object acquireValue() throws IOException {
                // final String CONTENT_TYPE = response.getContentType();
                // if ("text/plain".equals( CONTENT_TYPE ) ||
                // "text/html".equals( CONTENT_TYPE )) {
                String result = ""; //$NON-NLS-1$
                InputStream input = getResponse().getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String line = null;
                while( (line = reader.readLine()) != null ) {
                    result = result + line;
                }
                reader.close();
                input.close();
                return result;
                // } else if ( LayerPointInfo.GML.equals( CONTENT_TYPE ) ) {
                // // parse w/ GML parser from WFS !
                // return Collections.singletonList(new LayerPointInfo( getContext().getLayer() ) {
                // protected Object acquireValue() {
                // return null; // should perform lazy parsing here
                // }
                // public String getMimeType() {
                // return LayerPointInfo.GML;
                // }
                // });
                // }
                // else {
                // return Collections.EMPTY_LIST;
                // }
            }
        };
        return info;
    }

    /**
     * This assumes the same formats are available for all layers? Hope that is a good assumption.
     *
     * @param wms
     * @return MIME type of prefered content, or null if we can't get our info on
     */
    private static String desiredInfoFormat( WebMapServer wms ) {
        List formats  =
            wms.getCapabilities().getRequest().getGetFeatureInfo().getFormats();
        
        String desiredFormat;
      
        if (formats.contains("text/html")) { //$NON-NLS-1$
            desiredFormat = "text/html"; //$NON-NLS-1$
        } else if (formats.contains("text/plain")) { //$NON-NLS-1$
            desiredFormat = "text/plain"; //$NON-NLS-1$
        } else if (formats.contains(LayerPointInfo.GML)) {
            desiredFormat = LayerPointInfo.GML;
        } else {
            desiredFormat = null;
        }
        return desiredFormat;
    }
}
