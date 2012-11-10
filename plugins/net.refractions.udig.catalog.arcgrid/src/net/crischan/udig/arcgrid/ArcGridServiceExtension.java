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
package net.crischan.udig.arcgrid;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension;

import org.geotools.gce.arcgrid.ArcGridFormat;
import org.geotools.gce.arcgrid.ArcGridFormatFactory;

public class ArcGridServiceExtension implements ServiceExtension {
	private static ArcGridFormat format;
	private static ArcGridFormatFactory factory;
	
	public final static String URL_PARAM = "URL"; //$NON-NLS-1$
    public static final String TYPE = "arcgrid"; //$NON-NLS-1$
	
	public ArcGridServiceExtension() {
		super();
	}

	public Map<String, Serializable> createParams(URL url) {		
		if (!canProcess(url))
			return null;
		
		Map<String, Serializable> params = new HashMap<String, Serializable>();
        if (url != null)
            params.put(URL_PARAM, url);
        
        return params;
	}

	public IService createService(URL url, Map<String, Serializable> params) {
		URL url2 = url;
		if (url2 == null)
			url2 = extractURL(params);
		
		if (!canProcess(url2))
			return null;
		
		ArcGridServiceImplementation service = new ArcGridServiceImplementation(url2, getFactory());
		
		return service;
	}
	
	private boolean canProcess(URL url) {
		if (reasonForFailure(url) == null)
			return true;
		return false;
	}
	
	public String reasonForFailure(URL url) {
		String msg;
		
		if (url == null) {
			msg = "URL = null";
			return msg;
		}
		
		if (!isSupportedExtension(url)) {
			msg = "Unsupported extension";
			return msg;
		}
		
		File file = null;
		try {
			file = new File(url.getFile());
		} catch (IllegalArgumentException e) {
			msg = "Not a file";
			return msg;		
		}
		
		msg = arcGridFile(file);
		if (msg != null) {
			return msg;
		}
		
		try {
			if (!getFormat().accepts(file)) {
				msg = "Unknown format";
				return msg;
			}				
		} catch (RuntimeException e) {
			msg = "Unknown format";
			return msg;
		}
		
		return null;
	}

	private boolean isSupportedExtension(URL url) {
		String file = url.getFile();
		file = file.toLowerCase();
		
		return(file.endsWith(".asc") || file.endsWith(".grd") || file.endsWith(".asc.gz") );
	}

	private String arcGridFile(File file) {
		return null;
	}

	private static ArcGridFormat getFormat() {
		if (format == null)
			format = (ArcGridFormat) getFactory().createFormat();
		
		return format;
	}

	private static ArcGridFormatFactory getFactory() {
		if (factory == null)
			factory = new ArcGridFormatFactory();
		
		return factory;
	}

	private URL extractURL(Map<String, Serializable> params) {
		URL url;
		
		if (params.containsKey(URL_PARAM)) {
			Object param = params.get(URL_PARAM);
			if (param instanceof String) {
				try {
					url = new URL((String) param);
				} catch (MalformedURLException e) {
					return null;
				}
			} else if (param instanceof URL) {
				url = (URL) param;
			} else {
				return null;
			}
		} else {
			return null;
		}
		
		return url;
	}

}
