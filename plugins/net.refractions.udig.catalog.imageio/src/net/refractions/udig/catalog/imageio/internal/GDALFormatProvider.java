package net.refractions.udig.catalog.imageio.internal;

import it.geosolutions.imageio.gdalframework.GDALImageReaderSpi;
import it.geosolutions.imageio.gdalframework.GDALUtilities;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.spi.ImageReaderSpi;

import net.refractions.udig.catalog.imageio.Activator;
import net.refractions.udig.catalog.service.FormatProvider;

import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFactorySpi;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.coverageio.BaseGridFormatFactorySPI;
import org.geotools.coverageio.gdal.BaseGDALGridFormat;

/**
 * Format Provider that returns all supported GDAL Formats provided by the
 * imageio-ext project. It depends on native installation of GDAL and the 
 * gdal_data folder. Without a native GDAL installation, the provider will
 * return an empty set, therefore unsupported formats are not shown up 
 * in the FileConnectionPage of the Wizard. 
 * 
 * @author Frank Gasdorf
 * 
 */
public class GDALFormatProvider implements FormatProvider {

	/**
	 * supported GDAL formats and its FormatFactory 
	 */
	public static final Map<String, GridFormatFactorySpi> factories = new HashMap<String, GridFormatFactorySpi>();

	/**
	 * Set of all available and supported file extensions, representes as strings like '<i>*.img</i>'
	 */
	public static final Set<String> supportedExtensions = new HashSet<String>();
	
	static {
		loadProvidedFormats();
	}

	private static void loadProvidedFormats() {
		if (GDALUtilities.isGDALAvailable()) {
			GridFormatFinder.scanForPlugins();
			Set<GridFormatFactorySpi> availableFormats = GridFormatFinder.getAvailableFormats();
	
			for (GridFormatFactorySpi gridFormatFactorySpi : availableFormats) {
				if (gridFormatFactorySpi instanceof BaseGridFormatFactorySPI) {
//					System.out.println(gridFormatFactorySpi.getClass().getName());
					if (gridFormatFactorySpi.isAvailable()) {
						AbstractGridFormat format = gridFormatFactorySpi.createFormat();
						
						if (format != null && format instanceof BaseGDALGridFormat) {
							getExtensionsForFormat(gridFormatFactorySpi, (BaseGDALGridFormat)format);
						}
					}
				}
			}
		}
	}

	@Override
	public Set<String> getExtensions() {
		Set<String> fileExtentsions = new HashSet<String>();
		for (String fileExtension : supportedExtensions) {
			fileExtentsions.add("*." + fileExtension);
		}

		return Collections.unmodifiableSet(fileExtentsions);
	}

	@SuppressWarnings("unchecked")
	private static void getExtensionsForFormat(GridFormatFactorySpi gridFormatFactorySpi, BaseGDALGridFormat format) {
		
		GDALImageReaderSpi gdalSPI = null;
		try {
			if (format.getClass().getSuperclass().equals(BaseGDALGridFormat.class)) {
				Class<BaseGDALGridFormat> baseGDALGridFormatClass = (Class<BaseGDALGridFormat>) format.getClass().getSuperclass();

				Field field = baseGDALGridFormatClass.getDeclaredField("spi");
				field.setAccessible(true);
				Class<?> type = field.getType();
				if (type != null && type.equals(ImageReaderSpi.class)) {
					ImageReaderSpi spi = (ImageReaderSpi) field.get(format);
					if (spi instanceof GDALImageReaderSpi) {
						gdalSPI = (GDALImageReaderSpi)spi;
					}
				}
			}
		} catch (Exception e) {
			Activator.log("unable to load Extensions for imageio-ext Format " + format.getName(), e);
			return;
		}
		
		if (gdalSPI != null) {
			HashSet<String> extensions = new HashSet<String>();
			List<String> supportedFormats = gdalSPI.getSupportedFormats();
			
			for (String supportedFormat : supportedFormats) {
				
				if (!GDALUtilities.isDriverAvailable(supportedFormat)) {
					continue;
				}
				
				// check GDAL Driver
				Driver getDriverByName = gdal.GetDriverByName(supportedFormat);
				if (getDriverByName != null) {
					// TODO where to find the const DMD_EXTENSION in gdal? 
					String dmdExtension = getDriverByName.GetMetadataItem("DMD_EXTENSION");
					if (dmdExtension != null && dmdExtension.length() > 0) {
						extensions.add(dmdExtension.trim().toLowerCase());
					}
				}

				// TODO Review : File extension from SPI's (it.geosolutions.imageio.plugins) seems to be different to the GDAL Driver extensions
				String[] fileSuffixes = gdalSPI.getFileSuffixes();
				if (fileSuffixes != null) {
					for (String fileSuffix : fileSuffixes) {
						if (fileSuffix != null && fileSuffix.trim().length() > 0) {
							extensions.add(fileSuffix.trim().toLowerCase());
						}
					}
				}

				if (extensions != null && !extensions.isEmpty()) {
					supportedExtensions.addAll(extensions);
					factories.put(supportedFormat, gridFormatFactorySpi);
				}
			}
		}
	}
}
