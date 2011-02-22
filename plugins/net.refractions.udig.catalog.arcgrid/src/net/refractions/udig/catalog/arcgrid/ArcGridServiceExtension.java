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
package net.refractions.udig.catalog.arcgrid;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension2;

import org.geotools.gce.arcgrid.ArcGridFormat;
import org.geotools.gce.arcgrid.ArcGridFormatFactory;

/**
 * Provides the interface to the catalog service extension point.
 * <p>
 * This class is responsible for ensuring that only those services that the ArcGrid plug-in is
 * capable of processing are created.
 * </p>
 *
 * @author mleslie
 * @since 0.6.0
 */
public class ArcGridServiceExtension implements ServiceExtension2 {
    /** <code>URL_PARAM</code> field */
    public final static String URL_PARAM = "URL"; //$NON-NLS-1$

    private static ArcGridFormatFactory factory;
    private static ArcGridFormat format;

    /**
     * Construct <code>ArcGridServiceExtension</code>.
     */
    public ArcGridServiceExtension() {
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
        ArcGridServiceImpl service = new ArcGridServiceImpl(extractID(params), getFactory());
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

    private static ArcGridFormat getFormat() {
        if (format == null) {
            format = (ArcGridFormat) getFactory().createFormat();
        }
        return format;
    }

    /**
     * Finds or creates a ArcGridFormatFactorySpi.
     *
     * @return Default ArcGridFormatFactorySpi
     */
    public static ArcGridFormatFactory getFactory() {
        if (factory == null) {
            factory = new ArcGridFormatFactory();
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
            return Messages.ArcGridServiceExtension_nullURL;
        }

        if( !isSupportedExtension(url) )
            return Messages.ArcGridServiceExtension_badExt;

        File file = null;
        try {
            file = new File(url.getFile());
        } catch (IllegalArgumentException ex) {
            return url.toExternalForm()+Messages.ArcGridServiceExtension_notFile;
        }

        if (!file.exists() )
            return file+Messages.ArcGridServiceExtension_notExist;

        try {
            if (!getFormat().accepts(file))
                return Messages.ArcGridServiceExtension_unknown;
        } catch (RuntimeException ex) {
            return Messages.ArcGridServiceExtension_unknown;
        }
        return null;
    }
    private boolean isSupportedExtension( URL url ) {
        String file=url.getFile();
        file=file.toLowerCase();

        return (file.endsWith(".asc")); //$NON-NLS-1$
    }

}
