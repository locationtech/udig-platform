/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.location;

import java.io.IOException;
import java.util.List;

import org.apache.xmlrpc.XmlRpcException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.feature.simple.SimpleFeature;

import org.locationtech.jts.geom.Envelope;

/**
 * Find a location, using the USG service.
 *  
 * @author Jody Garnett
 * @since 1.0.0
 */
public class USGLocation implements Location {

    public List<SimpleFeature> search( String pattern, Envelope bbox, IProgressMonitor monitor )
            throws IOException {
        AddressSeeker seek = new AddressSeeker();
        List<SimpleFeature> stuff;
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
