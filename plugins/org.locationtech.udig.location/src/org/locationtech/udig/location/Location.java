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

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.feature.simple.SimpleFeature;

import org.locationtech.jts.geom.Envelope;

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
