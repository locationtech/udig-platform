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
package net.refractions.udig.catalog.internal.geotiff;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.metadata.IIOMetadata;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension2;
import net.refractions.udig.catalog.geotiff.internal.Messages;

import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffFormatFactorySpi;
import org.geotools.gce.geotiff.GeoTiffIIOMetadataAdapter;

import com.sun.media.jai.operator.ImageReadDescriptor;

/**
 * Provides the interface to the catalog service extension point.
 * <p>
 * This class is responsible for ensuring that only those services that the GeoTiff plug-in is
 * capable of processing are created.
 * </p>
 *
 * @author mleslie
 * @since 0.6.0
 */
public class GeoTiffServiceExtension implements ServiceExtension2 {
    /** <code>URL_PARAM</code> field */
    public final static String URL_PARAM = "URL"; //$NON-NLS-1$

    private static GeoTiffFormatFactorySpi factory;
    private static GeoTiffFormat format;

    /**
     * Construct <code>GeoTiffServiceExtension</code>.
     */
    public GeoTiffServiceExtension() {
        super();
    }

    public IService createService( URL id, Map<String, Serializable> params ) {
        URL id2 = id;
        if (id2 == null) {
            id2 = extractID(params);
        }
        if (!canProcess(extractID(params))) {
            return null;
        }
        GeoTiffServiceImpl service = new GeoTiffServiceImpl(extractID(params), getFactory());
        return service;
    }

    private URL extractID( Map<String, Serializable> params ) {
        URL id;
        if (params.containsKey(URL_PARAM)) {
            Object param = params.get(URL_PARAM);
            if (param instanceof String) {
                try {
                    id = new URL((String) param);
                } catch (MalformedURLException ex) {
                    return null;
                }
            } else if (param instanceof URL) {
                id = (URL) param;
            } else {
                return null;
            }
        } else {
            return null;
        }
        return id;
    }

    private static GeoTiffFormat getFormat() {
        if (format == null) {
            format = (GeoTiffFormat) getFactory().createFormat();
        }
        return format;
    }

    /**
     * Finds or creates a GeoTiffFormatFactorySpi.
     *
     * @return Default GeoTiffFormatFactorySpi
     */
    public static GeoTiffFormatFactorySpi getFactory() {
        if (factory == null) {
            factory = new GeoTiffFormatFactorySpi();
        }
        return factory;
    }

    private boolean canProcess( URL id ) {
        if ( reasonForFailure(id)==null )
            return true;
        return false;
    }

    public Map<String, Serializable> createParams( URL url ) {
        if (!canProcess(url))
            return null;

        Map<String, Serializable> params = new HashMap<String, Serializable>();
        if (url != null) {
            params.put(URL_PARAM, url);
        }
        return params;
    }

    public String reasonForFailure( Map<String, Serializable> params ) {
        return reasonForFailure(extractID(params));
    }

    public String reasonForFailure( URL url ) {
        if (url == null) {
            return Messages.GeoTiffServiceExtension_nullURL;
        }

        if( !isSupportedExtension(url) )
            return Messages.GeoTiffServiceExtension_badExt;

        File file = null;
        try {
            file = new File(url.getFile());
        } catch (IllegalArgumentException ex) {
            return url.toExternalForm()+Messages.GeoTiffServiceExtension_notFile;
        }

        if (!file.exists() )
            return file+Messages.GeoTiffServiceExtension_notExist;

        String result = geotiffFile(file);
        if (result!=null)
            return result;
        try {
            if (!getFormat().accepts(file))
                return Messages.GeoTiffServiceExtension_unknown;
        } catch (RuntimeException ex) {
            return Messages.GeoTiffServiceExtension_unknown;
        }
        return null;
    }
    private boolean isSupportedExtension( URL url ) {
        String file=url.getFile();
        file=file.toLowerCase();

        return (file.endsWith(".tiff") || file.endsWith(".tif")); //$NON-NLS-1$ //$NON-NLS-2$
    }
    private String geotiffFile(File file) {
        RenderedOp img=null;
        try{
            img = JAI.create("ImageRead", file); //$NON-NLS-1$
        }catch (Throwable e) {
            return Messages.GeoTiffServiceExtension_notReadWError+e;
        }
        if (img == null) {
            return Messages.GeoTiffServiceExtension_NotRead;
        }

        // Get the metadata object.
        Object metadataImage = img.getProperty(ImageReadDescriptor.PROPERTY_NAME_METADATA_IMAGE);

        if (!(metadataImage instanceof IIOMetadata)) {
            return Messages.GeoTiffServiceExtension_NotTiff1;
        }

        IIOMetadata check = (IIOMetadata) metadataImage;

        GeoTiffIIOMetadataAdapter metadata = new GeoTiffIIOMetadataAdapter(check);

        // does the GeoKey Directory exist?
        boolean geoTiffFile = false;

        try {
            metadata.getGeoKeyDirectoryVersion();
            geoTiffFile = true;
        } catch (UnsupportedOperationException ue) {
            // this state is captured by the geoTiffFile flag == false
        }

        if ( !geoTiffFile )
            return Messages.GeoTiffServiceExtension_NotTiff;

        return null;
    }

}
