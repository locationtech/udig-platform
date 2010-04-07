/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.catalog;

import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Builds *new* IService handle by using ServiceExtention to process provided connection parameters.
 * <p>
 * Please note that the *ID* associated with a set of connection parameters is the sole responsible
 * of the individual ServiceExtention implementations (after all only ShpServiceExtention knows how
 * to take connection parameters and determine a good identifier).
 * <p>
 * Connection parameters, and dragNdrop URLs, are only used as a guide:
 * <ul>
 * <li>Information that was not used may be removed
 * <li>Authentication information (such as username and password) when we build a security plugin
 * <li>Where possible we make use of sensible defaults for missing information
 * </ul>
 * We hope that for most uses client code will be able to store an ID directly and use it to look up
 * a live resources in the Catalog. The use of IServiceFactory should be rare and unusual.
 * <h2>Using Connection Parameters to connect to an IService</h2> If you are working with client
 * code (such as a project file) that stores connection information directly please use
 * IServiceFactory to create a IService handle and use the service.getID() to look up the live
 * resource in the local catalog.
 * <ol>
 * <li>User supplied connection parameters
 * <li>IServiceFactory.createService( parameters )
 * <li>Process list of candidate services; allowing the user to choose if needed
 * <li>Catalog findById( service.getIdentifier() )
 * <ul>
 * <li>if non null the service is already known (the returned service may not be in the location you
 * expect; for example if the file has been moved and the catalog knows about it)
 * <li>if null the catalog has never met this service before; use add( service ) to register it
 * </ul>
 * <li>service.getInfo( monitor ) to ensure the service can connect
 * <ul>
 * <li>If service.getStatus() == Status.BROKEN you can check service.getMessage() to find out why.
 * <li>If service.getStatus() == Status.CONNECTED everything is good
 * </ul>
 * </ol>
 * <h2>Use Drag and Drop URLs to connect to an IService</h2> Handling a drag and drop URL works in a
 * similar manner; this time we have even less information to work with and it is much more likely
 * that the user will need to choose between the available IService alternatives.
 * <p>
 * Also not that the IService handles returned may not have enough information to connect; a classic
 * case is a security enabled WFS capabilities document. User involvement will be required.
 * 
 * @since 1.0 Initial version caused some confusion over use of acquire
 * @version 1.1 Explicitly broke generation of default parameters and creating a IService into
 *          separate steps
 * @author Jody Garnett (Refractions Research)
 */
public interface IServiceFactory {

    /**
     * Generate a list of candidate services each with their own connection parameters.
     * <p>
     * The provided connectionParameters are used as a starting point and may be supplemented with
     * defaults, or user credentials as required.
     * <p>
     * Please be advised that the services handles returned may already be noted in the local
     * catalog; please use findById to check before blinding throwing in a new service handle.
     * 
     * @param connectionParameters
     * @return List of candidate IService handles, list may be empty but is never null
     */
    List<IService> createService( Map<String, Serializable> connectionParameters );

    /**
     * Will generate a list of candidate services; each with their own default parameters based on
     * the provided dragNdrop url.
     * <p>
     * This code is used to guess what a URL means when it is provided to the application as part of
     * a drag and drop action. A list of IService options is returned allowing the user to choose
     * when more than one option is available.
     * 
     * @param dragNdrop Target url provided by a drag and drop operation
     * @return List of candidate IService handles, list may be empty but is never null
     */
    List<IService> createService( URL dragNdrop );

    /**
     * Helper method used to clean up "unused" services in a list returned by createService.
     * <p>
     * <pre><code>
     * List<IService> possible = serviceFactory.createService( url );
     * try {
     *     // usually you process the possible services seeing which one will connect
     *     return possible.remove(0); // remove the first service
     * }
     * finally {
     *    serviceFactory.dispose( possible ); // dispose any remaining services in the possible list
     * }
     * </code></pre>
     * This is just a simple method that closes each service in the list; nothing fancy.
     * 
     * @param List of services to be disposed, each in turn
     * @param monitor Used to track what is going on
     */
    void dispose( List<IService> list, IProgressMonitor monitor );
    
    /**
     * Generate a list of candidate services each with their own connection parameters.
     * <p>
     * The provided connectionParameters are used as a starting point and may be supplemented with
     * defaults, or user credentials as required.
     * <p>
     * Please be advised that the services handles returned may already be noted in the local
     * catalog; please use findById to check before blinding throwing in a new service handle.
     * 
     * @param params
     * @return List of candidate IService handles, list may be empty but is never null
     * @deprecated Use createService( Map )
     */
    List<IService> acquire( Map<String, Serializable> connectionParameter );

    /**
     * Will generate a list of candidate services; each with their own default parameters based on
     * the provided dragNdrop url.
     * 
     * @param dragNdrop Url provided by a drag and drop operation
     * @return List of candidate services
     * @deprecated Use createService( URL )
     */
    List<IService> acquire( URL dragNdrop ); // creates a map, may look up authentication

    /**
     * Will generate a list of candidate services; each with their own default parameters.
     * <p>
     * Please be advised that although this method accepts an <code>id</code> parameter it you may
     * not assume that all (or even any) of the returned services will match match the provided id.
     * The resource may of moved on disk; or the service may not be available in this environment
     * (if you are opening up a project on a different machine).
     * 
     * @param id This is the original id, please replace with service.getIdentifier
     * @param params Connection Parameters
     * @deprecated Use createService( Map )
     * @return List of candidate services
     */
    List<IService> acquire( URL id, Map<String, Serializable> params );
    /**
     * @deprecated use {@link #acquire(Map)}
     */
    List<IService> aquire( Map<String, Serializable> params ); // may look up authentication
    /**
     * @deprecated use {@link #acquire(URL)}
     */
    List<IService> aquire( URL target ); // creates a map, may look up authentication
    /**
     * @deprecated use {@link #acquire(URL, Map)}
     */
    List<IService> aquire( URL id, Map<String, Serializable> params ); // may not look up
}