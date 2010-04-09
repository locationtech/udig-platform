package net.refractions.udig.catalog.service;

import java.util.Set;

/**
 * Used to dynamically generate a list of supported formats.
 * <p>
 * This class is registered with the fileFormat extension point using the provider tag. It
 * is used to register formats in a dynamic fashion based on "external" factors such
 * as the formats supported by GDAL, or ImageIO-EXT or GeoTools.
 * </p>
 * @since 1.2.0
 */
public interface FormatProvider {

    /**
     * Generated supported format extensions.
     * <p>
     * Extensions used directly and should be provided in the format "*.xxx".
     * 
     * @return Set of format extensions
     */
    Set<String> getExtensions();

}
