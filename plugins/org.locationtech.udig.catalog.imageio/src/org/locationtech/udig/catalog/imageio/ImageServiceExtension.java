/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.imageio;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.ServiceExtension2;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.catalog.imageio.internal.GDALFormatProvider;
import org.locationtech.udig.catalog.imageio.internal.Messages;

import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFactorySpi;

/**
 * Provides the interface to the catalog service extension point.
 * <p>
 * This class is responsible for ensuring that only those services that the
 * MrSID plugin is capable of processing are created.
 * </p>
 * 
 * @author mleslie
 * @author Daniele Romagnoli, GeoSolutions
 * @author Jody Garnett
 * @author Simone Giannecchini, GeoSolutions
 * @author Frank Gasdorf
 * 
 * @since 0.6.0
 */
public class ImageServiceExtension implements ServiceExtension2 {
	/** <code>URL_PARAM</code> field */
	public static final String URL_PARAM = "URL"; //$NON-NLS-1$

	public static final String TYPE = "imageio"; //$NON-NLS-1$

	/**
	 * Construct <code>ImageServiceExtension</code>.
	 */
	public ImageServiceExtension() {
	}

	/**
	 * Creates an {@link IService} based on the params provided
	 */
	public IService createService(URL id, Map<String, Serializable> params) {
		URL id2 = id;
		if (id2 == null)
			id2 = getID(params);

		if (!canProcess(id2))
			return null;

		ImageServiceImpl service = new ImageServiceImpl(id2, getFactoryForObject(id2, null));
		return service;
	}

	private URL getID(Map<String, Serializable> params) {
		if (params.containsKey(URL_PARAM)) {
			Object param = params.get(URL_PARAM);
			if (param instanceof String) {
				try {
					return new URL((String) param);
				} catch (MalformedURLException ex) {
					return null;
				}
			} else if (param instanceof URL) {
				return (URL) param;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Finds or creates the MrSIDFormatFactory.
	 * 
	 * @return Default instance of MrSIDFormatFactory
	 */
	public synchronized static GridFormatFactorySpi getFactory(final String formatName) {
		return GDALFormatProvider.factories.get(formatName);
	}

	/**
	 * Can we handle the provided URL?
	 * 
	 * @param id
	 *            the URL identifier
	 * @return <code>true</code> if the provided URL can be handled.
	 */
	private boolean canProcess(URL id) {
		return reasonForFailure(id) == null;
	}

	/**
	 * Create connection parameters for the provided url.
	 * <p>
	 * Provide sensible defaults; in the future a wizard may be around to allow
	 * a user to change these defaults.
	 * </p>
	 */
	public Map<String, Serializable> createParams(URL url) {
		if (!canProcess(url))
			return null;

		if (url != null) {
			Map<String, Serializable> params = new HashMap<String, Serializable>();
			params.put(URL_PARAM, url);
			return params;
		}

		return null;
	}

	/**
	 * Given the following connection parameters, returns a
	 * <code>String<code> specifying why can't we connect.
	 * return <code>null</code> in case of successfully connection
	 */
	public String reasonForFailure(Map<String, Serializable> params) {
		return reasonForFailure(getID(params));
	}

	/**
	 * Given the provided URL; why can't we connect?
	 * 
	 * @return reason we cannot connect, or null
	 */
	public String reasonForFailure(URL id) {
		if (id == null) {
			return Messages.ImageServiceExtension_noID;
		}
//		String fileExt = id.getFile().substring(id.getFile().indexOf('.') + 1);
//		
//		if (fileExt.compareToIgnoreCase("sid") != 0) { //$NON-NLS-1$
//			return Messages.ImageServiceExtension_badFileExtension + fileExt;
//		}
//		if (fileExt.compareToIgnoreCase("ecw") != 0) { //$NON-NLS-1$
//			return Messages.ImageServiceExtension_badFileExtension + fileExt;
//		}		
		if (!id.getProtocol().equals("file")) {
			return Messages.ImageServiceExtension_mustBeFIle;
		}
		File file = null;
		try {
			file = URLUtils.urlToFile(id);
		} catch (IllegalArgumentException ex) {
			return Messages.ImageServiceExtension_IllegalFilePart1
					+ id.getFile()
					+ Messages.ImageServiceExtension_IllegalFilePart2;
		}
		String filename = file.getName();
		int split = filename.lastIndexOf(".");
		String fileExt = split == -1 ? "" : filename.substring( split+1 );
		String found = null;
		
		if (GDALFormatProvider.supportedExtensions.contains(fileExt.toLowerCase())) {
			found = fileExt;
		}

		if( found == null ){
		    return Messages.ImageServiceExtension_geotoolsDisagrees;
		}
		final AbstractGridFormat format= getFormatForObject(id, file);

		/* Does this format accept file or URL? */
		if (format==null) {
			return Messages.ImageServiceExtension_geotoolsDisagrees;
		}

		return null;
	}

	private static GridFormatFactorySpi getFactoryForObject(URL id, File file) {
		for(GridFormatFactorySpi spi: GDALFormatProvider.factories.values())
		{	
			//we know that the factory is avaiable
			final AbstractGridFormat format = (AbstractGridFormat) spi.createFormat();
			if (file!=null&&format.accepts(file))
				return spi;
			if(id!=null&&format.accepts(id))
				return spi;
		}
		return null;
	}
	
	private static AbstractGridFormat getFormatForObject(URL id, File file) {
		final GridFormatFactorySpi spi=getFactoryForObject(id, file);
		return spi!=null?(AbstractGridFormat)spi.createFormat():null;
	}	
}
