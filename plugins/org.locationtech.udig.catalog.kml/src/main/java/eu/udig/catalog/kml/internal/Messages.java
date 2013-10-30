/*
 * (C) HydroloGIS - www.hydrologis.com 
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.catalog.kml.internal;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Access to string constants provided by properties
 * 
 * @author Frank Gasdorf
 */
public class Messages {
    private static final String BUNDLE_NAME = "org.locationtech.udig.catalog.kml.internal.messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages() {
    }
    public static String getString( String key ) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
