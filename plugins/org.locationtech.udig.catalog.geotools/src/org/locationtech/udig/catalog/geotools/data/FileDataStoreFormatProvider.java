/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.geotools.data;

import java.util.Set;
import java.util.TreeSet;

import org.locationtech.udig.catalog.service.FormatProvider;

import org.geotools.data.FileDataStoreFinder;

/**
 * Converts from GeoTools FileDataStoreFinder registery of file extensions; to FormatProvider
 * so uDig drag and drop can work.
 * 
 * @author Jody Garnett
 * @since 1.3.0
 */
public class FileDataStoreFormatProvider implements FormatProvider {

    public Set<String> getExtensions() {
        Set<String> extensions = new TreeSet<String>();
        for( String ext : FileDataStoreFinder.getAvailableFileExtentions() ){
            if( ext.length()==4 && ext.startsWith(".")){
                ext = "*"+ext; // convert ".shp" --> "*.shp"
            }
            extensions.add( ext );
        }
        return extensions;
    }

}
