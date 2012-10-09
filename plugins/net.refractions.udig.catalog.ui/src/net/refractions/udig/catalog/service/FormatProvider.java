/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
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
