/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;


/**
 * Extends the {@link ServiceExtension} interface to provide debug information about why it couldn't process
 * a URL or parameters.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface ServiceExtension2 extends ServiceExtension {
    /**
     * Returns a human consumable string explaining why the params cannot be used for creating a Service
     *
     * @param params params for trying to create a service
     * @return a human consumable string explaining why the params cannot be used for creating a Service or null if a Service should be 
     * able to be created using the params
     */
    String reasonForFailure(Map<String, Serializable> params);
    /**
     * Returns a human consumable string explaining why the url cannot be used for creating a Service
     *
     * @param url URL for trying to create a service
     * @return a human consumable string explaining why the url cannot be used for creating a Service or null if a Service should be 
     * able to be created using the URL
     */
    String reasonForFailure(URL url);
}
