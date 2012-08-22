/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010-2012, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.geotools.data;

import java.util.Set;
import java.util.TreeSet;

import net.refractions.udig.catalog.service.FormatProvider;

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
