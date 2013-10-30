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
package org.locationtech.udig.catalog.service;

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
