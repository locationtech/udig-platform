/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.location;

import java.io.IOException;
import java.util.List;

import org.apache.xmlrpc.XmlRpcException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.feature.Feature;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Find a location, using the USG service.
 *
 * @author Jody Garnett
 * @since 1.0.0
 */
public class USGLocation {

    public List<Feature> search( String pattern, Envelope bbox, IProgressMonitor monitor )
            throws IOException {
        AddressSeeker seek = new AddressSeeker();
        List<Feature> stuff;
        try {
            stuff = seek.geocode( pattern );
        } catch (IOException e) {
            return null;
        } catch (XmlRpcException e) {
            return null;
        }
        return stuff;
    }

}
