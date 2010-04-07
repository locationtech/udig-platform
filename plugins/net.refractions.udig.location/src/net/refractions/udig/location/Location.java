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

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Find a location (for the given text and bounds) - often used to wrap
 * up a "GeoCoder".
 * <p>
 * This location search results in a List of Features; these features may be
 * entirely artificial (representing an address location for example) and are
 * not required to represent any formal data model.
 * </p>
 *  
 * @author Jody Garnett
 * @since 1.0.0
 */
public interface Location {

    public List<SimpleFeature> search( String text, Envelope bbox, IProgressMonitor monitor )
            throws IOException;

}
