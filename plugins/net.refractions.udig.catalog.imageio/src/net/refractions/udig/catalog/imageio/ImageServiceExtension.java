/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.imageio;

import it.geosolutions.imageio.gdalframework.GDALUtilities;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension2;
import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.catalog.imageio.internal.Messages;

import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFactorySpi;
import org.geotools.coverageio.gdal.dted.DTEDFormatFactory;
import org.geotools.coverageio.gdal.ecw.ECWFormatFactory;
import org.geotools.coverageio.gdal.erdasimg.ErdasImgFormatFactory;
import org.geotools.coverageio.gdal.mrsid.MrSIDFormatFactory;
import org.geotools.coverageio.gdal.nitf.NITFFormatFactory;

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
 * 
 * @since 0.6.0
 */
public class ImageServiceExtension implements ServiceExtension2 {
	/** <code>URL_PARAM</code> field */
	public final static String URL_PARAM = "URL"; //$NON-NLS-1$

	/** The inner MrSID Format factory*/
	static final Map<String,GridFormatFactorySpi> factories;
	static final Map<String,List<String>>	fileExtensions;

    public static final String TYPE = "imageio"; //$NON-NLS-1$
	static{
		// 
		factories= new HashMap<String, GridFormatFactorySpi>();
		fileExtensions=new HashMap<String, List<String>>();
		
		// 
		if(GDALUtilities.isGDALAvailable())
		{
			//
			// add all drivers
			//
			
			// mrsid
			String driverCode="MrSID";
			if(GDALUtilities.isDriverAvailable(driverCode))
			{
				factories.put(driverCode, new MrSIDFormatFactory());
				fileExtensions.put(driverCode, Collections.singletonList("sid"));
			}
			
			
			// ecw
			driverCode="ECW";
			if(GDALUtilities.isDriverAvailable(driverCode))
			{
				factories.put(driverCode, new ECWFormatFactory());
				fileExtensions.put(driverCode, Collections.singletonList("ecw"));
			}		
			

			
			// DTED
			driverCode="DTED";
			if(GDALUtilities.isDriverAvailable(driverCode))
			{
				factories.put(driverCode, new DTEDFormatFactory());
				fileExtensions.put(driverCode,Arrays.asList("dt0","dt1", "dt2"));
			}		
			
			
			// HFA
			driverCode="HFA";
			if(GDALUtilities.isDriverAvailable(driverCode))
			{
				factories.put(driverCode, new ErdasImgFormatFactory());
				fileExtensions.put(driverCode,Arrays.asList("dt0","dt1", "dt2"));
			}	
			
			// NITF
			driverCode="NITF";
			if(GDALUtilities.isDriverAvailable(driverCode))
			{
				factories.put(driverCode, new NITFFormatFactory());
				fileExtensions.put(driverCode,Arrays.asList("jp2","j2k"));
			}			
		}
	}

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
		return ImageServiceExtension.factories.get(formatName);
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
		FOUND: for( List<String> extensionList : fileExtensions.values() ){
		    for( String extension : extensionList ){
		        if( fileExt.equalsIgnoreCase( extension )){
		            found = extension;
		            break FOUND;
		        }
		    }
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
		for(GridFormatFactorySpi spi: factories.values())
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
