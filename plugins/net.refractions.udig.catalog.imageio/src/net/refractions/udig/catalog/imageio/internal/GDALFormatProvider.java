package net.refractions.udig.catalog.imageio.internal;

import it.geosolutions.imageio.gdalframework.GDALImageReaderSpi;
import it.geosolutions.imageio.gdalframework.GDALUtilities;

import java.lang.reflect.Field;
import java.util.Arrays;
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
 * return an empty set, therefore unsupported formats are not shown up in the
 * FileConnectionPage of the Wizard.
 * 
 * @author Frank Gasdorf
 * 
 */
public class GDALFormatProvider implements FormatProvider {

	/**
	 * the private filed in {@link BaseGDALGridFormat} class for the
	 * {@link BaseGridFormatFactorySPI}
	 */

	private static final String SPI_ATTRIBUTE_GRID_FORMAT = "spi"; //$NON-NLS-1$

	/**
	 * Metadata access key to get extensions from GDAL driver TODO Where to find
	 * the const DMD_EXTENSION in GDAL?
	 */
	private static final String GDAL_METADATA_DMD_EXTENSION = "DMD_EXTENSION"; //$NON-NLS-1$
	/**
	 * supported GDAL formats and its FormatFactory
	 */

	public static final Map<String, GridFormatFactorySpi> factories = new HashMap<String, GridFormatFactorySpi>();

	/**
	 * Set of all available and supported file extensions, represents as strings
	 * like '<i>*.img</i>'
	 */
	public static final Set<String> supportedExtensions = new HashSet<String>();

	static {
		// Note, this is because the neither gdal.Driver nor GDALImageReaderSpi
		// provide all supported file extensions.
		// this can be removed, if the imageio-ext would provide these in the
		// future (GDALImageReaderSpi)
		if (GDALUtilities.isGDALAvailable()) {
			if (GDALUtilities.isDriverAvailable("ECW")) { //$NON-NLS-1$
				supportedExtensions.add("ecw"); //$NON-NLS-1$
			}
			if (GDALUtilities.isDriverAvailable("MrSID")) { //$NON-NLS-1$
				supportedExtensions.add("sid"); //$NON-NLS-1$
			}
			if (GDALUtilities.isDriverAvailable("DTED")) { //$NON-NLS-1$
				supportedExtensions.addAll(Arrays.asList("dt0", "dt1", "dt2")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			if (GDALUtilities.isDriverAvailable("HFA")) { //$NON-NLS-1$
				supportedExtensions.add("img"); //$NON-NLS-1$
			}

			if (GDALUtilities.isDriverAvailable("NITF")) { //$NON-NLS-1$
				supportedExtensions.add("ntf") //$NON-NLS-1$
				;
				supportedExtensions
						.addAll(Arrays
								.asList("gn1", "gn2", "gn3", "gn4", "gn5", "gn6", "gn7", "gn8", "gn9")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
				supportedExtensions
						.addAll(Arrays
								.asList("on1", "on2", "on3", "on4", "on5", "on6", "on7", "on8", "on9")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
				supportedExtensions
						.addAll(Arrays
								.asList("ja1", "ja2", "ja3", "ja4", "ja5", "ja6", "ja7", "ja8", "ja9")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
				supportedExtensions
						.addAll(Arrays
								.asList("jg1", "jg2", "jg3", "jg4", "jg5", "jg6", "jg7", "jg8", "jg9")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
				supportedExtensions
						.addAll(Arrays
								.asList("jn1", "jn2", "jn3", "jn4", "jn5", "jn6", "jn7", "jn8", "jn9")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
				supportedExtensions
						.addAll(Arrays
								.asList("tl1", "tl2", "tl3", "tl4", "tl5", "tl6", "tl7", "tl8", "tl9")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
				supportedExtensions
						.addAll(Arrays
								.asList("tp1", "tp2", "tp3", "tp4", "tp5", "tp6", "tp7", "tp8", "tp9")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
			}
		}
		// load all others from drivers and GDALImageReaderSpi's
		loadProvidedFormats();
	}

	private static void loadProvidedFormats() {
		if (GDALUtilities.isGDALAvailable()) {
			GridFormatFinder.scanForPlugins();
			Set<GridFormatFactorySpi> availableFormats = GridFormatFinder
					.getAvailableFormats();

			for (GridFormatFactorySpi gridFormatFactorySpi : availableFormats) {
				if (gridFormatFactorySpi instanceof BaseGridFormatFactorySPI) {
					// System.out.println(gridFormatFactorySpi.getClass().getName());
					if (gridFormatFactorySpi.isAvailable()) {
						AbstractGridFormat format = gridFormatFactorySpi
								.createFormat();

						if (format != null
								&& format instanceof BaseGDALGridFormat) {
							getExtensionsForFormat(gridFormatFactorySpi,
									(BaseGDALGridFormat) format);
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
			fileExtentsions.add("*." + fileExtension); //$NON-NLS-1$
		}

		return Collections.unmodifiableSet(fileExtentsions);
	}

	@SuppressWarnings("unchecked")
	private static void getExtensionsForFormat(
			GridFormatFactorySpi gridFormatFactorySpi, BaseGDALGridFormat format) {

		GDALImageReaderSpi gdalSPI = null;
		try {
			if (format.getClass().getSuperclass()
					.equals(BaseGDALGridFormat.class)) {
				Class<BaseGDALGridFormat> baseGDALGridFormatClass = (Class<BaseGDALGridFormat>) format
						.getClass().getSuperclass();

				// TODO REVIEW Is there an other API to get the Extensions for
				// Grid Format?
				Field field = baseGDALGridFormatClass
						.getDeclaredField(SPI_ATTRIBUTE_GRID_FORMAT);
				field.setAccessible(true);
				Class<?> type = field.getType();
				if (type != null && type.equals(ImageReaderSpi.class)) {
					ImageReaderSpi spi = (ImageReaderSpi) field.get(format);
					if (spi instanceof GDALImageReaderSpi) {
						gdalSPI = (GDALImageReaderSpi) spi;
					}
				}
			}
		} catch (Exception e) {
			Activator
					.log("unable to load Extensions for imageio-ext Format " + format.getName(), e); //$NON-NLS-1$
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
					String dmdExtension = getDriverByName
							.GetMetadataItem(GDAL_METADATA_DMD_EXTENSION);
					if (dmdExtension != null && dmdExtension.length() > 0) {
						extensions.add(dmdExtension.trim().toLowerCase());
					}
				}

				// TODO Review : File extension from SPI's
				// (it.geosolutions.imageio.plugins) seems to be different to
				// the GDAL Driver extensions
				String[] fileSuffixes = gdalSPI.getFileSuffixes();
				if (fileSuffixes != null) {
					for (String fileSuffix : fileSuffixes) {
						if (fileSuffix != null
								&& fileSuffix.trim().length() > 0) {
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
