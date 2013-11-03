/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

/**
 * Used to advertise the availability of an IService implementation
 * to the catalog.
 * 
 * Example:<pre><code>
 * public class MyService implements ServiceExtension {
 *    public Map<String, Serializable> createParams( URL url ) {
 *       if( url.toString().endsWith(".my") ){
 *          Map<String, Serializable> map = new HashMap<String, Serializable>();
 *          map.put("url",url);
 *          return map;
 *      }
 *      return null; // url must be for someone else
 *    }
 *    public IService createService( URL id, Map<String, Serializable> params ) {
 *       if( params.get("url") == null ||
 *           !params.get("url").toString().endsWith(".xml") ){
 *          return null;
 *       }
 *       return new MyService( params );
 *    }
 * }
 * <code></pre>
 * We also use this interface internally, so look in this plugin for additional examples.
 * 
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public interface ServiceExtension {

    public final static String EXTENSION_ID = "org.locationtech.udig.catalog.ServiceExtension"; //$NON-NLS-1$

    /**
     * Creates an IService based on the params provided. This may or may not return a singleton,
     * caching is optional. Error messages can be retrieved using the getStatus and getMessage
     * methods. It is important to note that this method must inspect the url to determine if it can
     * be used to create the service. If it cannot, null must be returned.
     * 
     * @param id The suggested service id, should be generated when null.
     * @param params The set of connection params. These param values may either be parsed, or
     *        unparsed (String).
     * @return the IService created, or null when a service cannot be created from these params.
     * @see IService#getStatus()
     * @see IService#getMessage()
     */
    IService createService( URL id, Map<String, Serializable> params );

    /**
     * The primary intention is for drag 'n' drop. This generates a set of params for the given URL
     * ... in most cases this will be passed to the createService method. It is important to note
     * that this method must inspect the url to determine if it can be used to create the service.
     * If it cannot, null must be returned.
     * 
     * @param url The potential source of params.
     * @return Map of params to be used for creation, <code>null</code> if the URL cannot be used.
     */
    Map<String, Serializable> createParams( URL url );
}
