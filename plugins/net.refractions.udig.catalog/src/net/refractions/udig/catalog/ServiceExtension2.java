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
package net.refractions.udig.catalog;

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
